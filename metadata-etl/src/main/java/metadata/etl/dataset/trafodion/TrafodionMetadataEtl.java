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
package metadata.etl.dataset.trafodion;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import metadata.etl.JDBCHelper;
import metagrid.common.Constant;
import metagrid.common.utils.Json;
import org.python.google.common.collect.Maps;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class TrafodionMetadataEtl extends JDBCHelper {
    private static String[] columns = {"name", "schema", "schema_type", "properties", "urn", "source", "location_prefix",
            "parent_name", "storage_type", "dataset_type", "is_partitioned"};
    private final String[] fieldColumns = "dataset_urn,sort_id,name,data_type,nullable,size,precision,scale,default_value,remark".split(",");

    public TrafodionMetadataEtl(Integer dbId, Long whExecId, Properties prop) {
        super(dbId, whExecId, prop);
    }


    @Override
    public void extract()
            throws Exception {

        logger.info("Start trafodion{}({}/{}) meta data extraction.",
                prop.getProperty(Constant.TRAFODION_DB_JDBC_URL),
                prop.getProperty(Constant.TRAFODION_DB_USERNAME),
                prop.getProperty(Constant.TRAFODION_DB_PASSWORD));

        Writer tableWriter = new FileWriter(new File(prop.getProperty(Constant.TRAFODION_SCHEMA_OUTPUT_KEY,
                "/tmp/metagrid/trafodion/trafodionTableMetadata.txt")));
        Writer fieldWriter = new FileWriter(new File(prop.getProperty(Constant.TRAFODION_FIELD_OUTPUT_KEY,
                "/tmp/metagrid/trafodion/trafodionFieldMetadata.txt")));

        List data = getTrafodionMetaData();
        try (CSVWriter csvWriter = new CSVWriter(tableWriter, '\032', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            csvWriter.writeNext(columns);
            csvWriter.writeAll((List) data.get(0));
        }
        try (CSVWriter csvWriter = new CSVWriter(fieldWriter, '\032', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {
            csvWriter.writeNext(fieldColumns);
            csvWriter.writeAll((List) data.get(1));
        }
    }

    private List getTrafodionMetaData() throws Exception {
        logger.info("Get trafodion metadata information");
        String jobId = prop.getProperty(Constant.WH_ETL_JOB_ID_KEY);
        List<String[]> tables = Lists.newArrayList();
        List<String[]> fields = Lists.newArrayList();
        try (Connection conn = getConnection()) {
            DatabaseMetaData dbMeta = conn.getMetaData();
            ResultSet schemaRs = dbMeta.getSchemas();
            while (schemaRs.next()) {
                String schema = schemaRs.getString(1);
                if ("_MD_".equalsIgnoreCase(schema) || "_REPOS_".equalsIgnoreCase(schema) || "_LIBMGR_".equalsIgnoreCase(schema))
                    continue;//system
                ResultSet rs = dbMeta.getTables(conn.getCatalog(), schema, null, null);
                boolean hasTables = false;
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if ("SB_HISTOGRAMS".equalsIgnoreCase(tableName) || "SB_HISTOGRAM_INTERVALS".equalsIgnoreCase(tableName) || "SB_PERSISTENT_SAMPLES".equalsIgnoreCase(tableName))
                        continue;
                    List<String> cols = new ArrayList<>();
                    String schem = rs.getString("TABLE_SCHEM");
                    System.out.println(schem + "--------");
                    String urn = String.format("%s:///%s/%s", jobId, schem, tableName);
                    List tableSchema = Lists.newArrayList();
                    ResultSet colRs = dbMeta.getColumns(conn.getCatalog(), schem, tableName, null);
                    while (colRs.next()) {
                        hasTables = true;
                        List<String> fieldItem = Lists.newArrayList();
                        Map schemaMap = Maps.newHashMap();
                        fieldItem.add(urn);
                        String columnName = colRs.getString("COLUMN_NAME");
                        Object dataType = colRs.getString("TYPE_NAME");
                        String columnSize = colRs.getString("COLUMN_SIZE");
                        String precisionStr = "(" + columnSize + (colRs.getObject("NUM_PREC_RADIX") == null || (dataType.equals("INTEGER")) ? "" : ("," + colRs.getObject("NUM_PREC_RADIX"))) + ")";
                        String precision = (colRs.getObject("NUM_PREC_RADIX") == null || (dataType.equals("INTEGER"))) ? "0" : String.valueOf(colRs.getObject("NUM_PREC_RADIX"));
                        fieldItem.add(colRs.getString("ORDINAL_POSITION"));
                        fieldItem.add(columnName);
                        fieldItem.add(dataType + precisionStr);
                        fieldItem.add(colRs.getString("IS_NULLABLE").charAt(0) + "");
                        fieldItem.add(columnSize);
                        fieldItem.add(precision);
                        fieldItem.add("0");//scal
                        fieldItem.add("");//default_value
                        fieldItem.add(colRs.getString("REMARKS"));//REMARKS
                        schemaMap.put("name", columnName);
                        schemaMap.put("dataType", dataType + precisionStr);
                        tableSchema.add(schemaMap);
                        fields.add(fieldItem.toArray(new String[0]));
                    }
                    cols.add(rs.getString("TABLE_NAME"));
//                Map schemaMap = Maps.newHashMap();
//                schemaMap.put("shim_fields", tableSchema);
                    cols.add(Json.toJson(tableSchema).toString());
                    cols.add("JSON");
                    cols.add("");//properties
                    cols.add(urn);
                    cols.add("TRAFODION");
                    cols.add("/" + schem);
                    cols.add(schem);
                    cols.add("");//storage_type
                    cols.add("TRAFODION");//dataset_type
                    cols.add("N");//is_partitioned
                    tables.add(cols.toArray(new String[0]));
                }


                if (!hasTables) {
                    List<String> cols = new ArrayList<>();
                    cols.add(schema);
//                Map schemaMap = Maps.newHashMap();
//                schemaMap.put("shim_fields", tableSchema);
                    cols.add("");
                    cols.add("NONE");
                    cols.add("");//properties
                    String urn = String.format("%s:///%s", jobId, schema);
                    cols.add(urn);
                    cols.add("TRAFODION");
                    cols.add("");
                    cols.add("");
                    cols.add("");//storage_type
                    cols.add("TRAFODION");//dataset_type
                    cols.add("N");//is_partitioned
                    tables.add(cols.toArray(new String[0]));
                }
            }
            return Lists.newArrayList(tables, fields);
        }
    }

    @Override
    public void transform()
            throws Exception {
    }

    @Override
    public Connection getConnection() throws Exception {
        String url = prop.getProperty(Constant.TRAFODION_DB_JDBC_URL);
        try {
            Class.forName("org.trafodion.jdbc.t4.T4Driver");
            //loads the driver
            logger.info("connecting to {} with {}", url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"));
            Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"), prop.getProperty(Constant.TRAFODION_DB_PASSWORD, "pwd"));
            return con;
        } catch (Exception e) {
            if (e.getMessage().contains("server handle not available"))
                return null;
            throw e;
        }

    }

    @Override
    public void load()
            throws Exception {
        logger.info("In trafodion metadata ETL, launch load jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/TrafodionLoad.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    public void load2()
            throws Exception {
        Connection connection;
        Statement stmt;
        PreparedStatement pStmt;
        ResultSet rs;
        DatabaseMetaData dbMeta;
        int rowNo;
        String table = "DBMETASAMPLE";

        try {
            connection = getConnection();
            for (int i = 0; i < 6; i++) {
                switch (i) {
                    case 0:
                        System.out.println("");
                        System.out.println("getTypeInfo() ");
                        dbMeta = connection.getMetaData();
                        rs = dbMeta.getTypeInfo();
                        break;
                    case 1:
                        System.out.println("");
                        System.out.println("getCatalogs()");
                        dbMeta = connection.getMetaData();
                        rs = dbMeta.getCatalogs();
                        break;
                    case 2:
                        System.out.println("");
                        System.out.println("getSchemas()");
                        dbMeta = connection.getMetaData();
                        rs = dbMeta.getSchemas();
                        break;
                    case 3:
                        System.out.println("");
                        System.out.println("getTables() ");
                        dbMeta = connection.getMetaData();
                        //	getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
                        rs = dbMeta.getTables(connection.getCatalog(), null, null, null);
                        break;
                    case 4:
                        System.out.println("");
                        System.out.println("getColumns()");
                        dbMeta = connection.getMetaData();
                        //getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
                        rs = dbMeta.getColumns(connection.getCatalog(), null, null, null);
                        break;
                    case 5:
                        System.out.println("");
                        System.out.println("getProcedures()");
                        dbMeta = connection.getMetaData();
                        rs = dbMeta.getProcedures(connection.getCatalog(), null, null);
                        break;
                    default:
                        rs = null;
                        continue;
                }

                ResultSetMetaData rsMD = rs.getMetaData();
                System.out.println("");
                System.out.println("Printing ResultSetMetaData ...");
                System.out.println("No. of Columns " + rsMD.getColumnCount());
                for (int j = 1; j <= rsMD.getColumnCount(); j++) {
                    System.out.print(rsMD.getColumnTypeName(j) + ":" + rsMD.getColumnName(j) + "\t");
                }
//                System.out.println("");
//                System.out.println("Fetching rows...");
                rowNo = 0;
                while (rs.next()) {
                    rowNo++;
                    System.out.println("");
                    //System.out.println("Printing Row " + rowNo + " using getString(), getObject()");
                    for (int j = 1; j <= rsMD.getColumnCount(); j++) {
                        System.out.print(rs.getObject(j) + "\t");
                    }
                }
                System.out.println("");
                System.out.println("End of Data");
                rs.close();
            }

            connection.close();
        } catch (SQLException e) {
            SQLException nextException;

            nextException = e;
            do {
                System.out.println(nextException.getMessage());
                System.out.println("SQLState   " + nextException.getSQLState());
                System.out.println("Error Code " + nextException.getErrorCode());
            } while ((nextException = nextException.getNextException()) != null);
        }


        System.out.println("done");
    }

    @Override
    public void index () throws Exception {
        logger.info("In Trafodion metadata ETL, launch index jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In Trafodion metadata ETL load index scripts finished");
    }
}
