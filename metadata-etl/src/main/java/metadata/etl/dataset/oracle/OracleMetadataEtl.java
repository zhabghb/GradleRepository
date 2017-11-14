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
package metadata.etl.dataset.oracle;

import metadata.etl.JDBCHelper;
import metagrid.common.Constant;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


public class OracleMetadataEtl extends JDBCHelper {

    public OracleMetadataEtl(Integer whEtljobId, Long whExecId, Properties prop) {
        super(whEtljobId, whExecId, prop);
    }


    @Override
    public void extract()
            throws Exception {
        logger.info("In Oracle metadata ETL, launch extract jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/OracleExtract.py");
        // logger.info("call scripts with args: " + interpreter.getSystemState().argv);
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void executeUpdate(List<String> sqls)
            throws Exception {
        try (Connection conn = getConnection()) {
            try (Statement statement = conn.createStatement()) {
                for(String sql:sqls){
                    statement.addBatch(sql);
                }
                statement.executeBatch();
            }
        }
    }

    @Override
    public void transform()
            throws Exception {
//      sql = "SELECT * FROM (SELECT a.*, ROWNUM rn FROM ("
//              + sql + ") a WHERE ROWNUM <= " + (start + param.getLimit())
//              + ") WHERE rn >= " + (start + 1);
    }

    public Connection getConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        //loads the driver
        String url = prop.getProperty(Constant.ORACLE_DB_JDBC_URL);
        logger.info("connecting to {} with {}", url, prop.getProperty(Constant.ORACLE_DB_USERNAME, "user"));
        Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.ORACLE_DB_USERNAME), prop.getProperty(Constant.ORACLE_DB_PASSWORD));
        return con;
    }

    @Override
    public void load()
            throws Exception {
        logger.info("In oracle metadata ETL, launch load jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/OracleLoad.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void index () throws Exception {
        logger.info("In Oracle metadata ETL, launch index jython scripts");
        System.out.println("index!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In Oracle metadata ETL index scripts finished");
    }

}
