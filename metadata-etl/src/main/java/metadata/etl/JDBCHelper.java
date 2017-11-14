/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package metadata.etl;

import com.google.common.collect.Lists;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import oracle.sql.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public abstract class JDBCHelper extends EtlJob {

    public JDBCHelper(Integer dbId, Long whExecId, Properties prop) {
        super(null, dbId, whExecId, prop);
    }

    public void ping() throws Exception {
        try (Connection conn = getConnection()) {
        }
    }

    public abstract Connection getConnection() throws Exception;

    public QueryResult query(QueryParam param) throws Exception {
        long s = System.currentTimeMillis();
        try (Connection conn = getConnection()) {
            QueryResult res = new QueryResult();
            try (Statement statement = conn.createStatement()) {
                statement.setMaxRows(param.getLimit());
                statement.setFetchSize(param.getLimit());
//                long total = 0;
                String sql = param.getSql();
//                String countSql = "select count(*) from (" + sql + ")";
//                try (ResultSet resultSet = statement.executeQuery(countSql)) {
//                    if (resultSet.next())
//                        total = resultSet.getLong(1);
//                }
//                res.setTotal(total);
                //boolean pagination = true;//total > param.getLimit();
                if (!"oracle".equalsIgnoreCase(param.getSourceType()) && !sql.toLowerCase().contains(" limit ")) {
                    //int start = (param.getPage() - 1) * param.getLimit();
                    sql = "select t.* from (" + sql + ") t limit " + param.getLimit();
                }

                logger.info("exec sql:{}", sql);
                try (ResultSet resultSet = statement.executeQuery(sql)) {
                    ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                    int colCount = resultSetMetaData.getColumnCount();
                    List<String> colNames = new ArrayList<>();
                    for (int i = 0; i < colCount; i++) {
                        colNames.add(resultSetMetaData.getColumnName(i + 1));
//                        System.out.println(resultSetMetaData.getColumnTypeName(i + 1));
                    }
                    res.setColNames(colNames);
                    List<List<Object>> data = new ArrayList<>();
                    while (resultSet.next()) {
                        List<Object> item = new ArrayList<>();
                        for (int i = 0; i < colCount; i++) {
//                            System.out.println(resultSet.getDate(i+1));
//                            System.out.println(resultSet.getTimestamp(i+1));
//                            ArrayList<String> specialTypes = Lists.newArrayList("NCLOB","CLOB","BLOB","BFILE","RAW","ROWID");
                            ArrayList<String> specialTypes = Lists.newArrayList("NCLOB","CLOB","BLOB");
                            Object v = resultSet.getObject(i + 1);
                            String columnType = resultSetMetaData.getColumnTypeName(i + 1);
                            if ("DATE".equals(columnType) && v != null && v.toString().endsWith(".0")) {
                                v = v.toString().substring(0, v.toString().indexOf(".0"));
                            } else if (v != null && "TIMESTAMP WITH TIME ZONE".equalsIgnoreCase(columnType)) {
                                TIMESTAMPTZ tsTZ = (TIMESTAMPTZ) v;
                                v = tsTZ.stringValue(conn).replace(".0 ", " +");
                            } else if (v != null && "TIMESTAMP WITH LOCAL TIME ZONE".equalsIgnoreCase(columnType)) {
                                TIMESTAMPLTZ ltz = (TIMESTAMPLTZ) v;
                                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                v = sdf.format(ltz.dateValue(conn));
                            } else if (v != null && "RAW".equals(columnType)){
                                v = new String((byte[]) v,"utf-8");
                            } else if (v != null && "ROWID".equals(columnType)){
                                ROWID rowid = (ROWID)v;
                                v = rowid.stringValue();
                            } else if (v != null && "binary".equals(columnType)){
                                v = "[binary]";
                            }else if (v != null && specialTypes.contains(columnType)) {
                                Reader reader = null;
                                if(columnType.contains("CLOB")){
                                    CLOB clob = (CLOB) v;
                                    reader = clob.getCharacterStream();
                                } else  {
                                    Datum datum = (Datum)v;
                                    reader = datum.characterStreamValue();
                                }

                                BufferedReader br = new BufferedReader(reader);
                                String str = br.readLine();
                                StringBuffer buffer = new StringBuffer();
                                while (str != null) {
                                    buffer.append(str);
                                    str = br.readLine();
                                }
                                v = buffer.toString();
                            }
                            item.add(v == null ? null : v.toString());
                        }
                        data.add(item);
                    }
                    res.setResults(data);
                    res.setDuration(System.currentTimeMillis() - s);
                    return res;
                }
            }
        }
    }


}
