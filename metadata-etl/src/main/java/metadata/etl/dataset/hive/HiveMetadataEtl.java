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
package metadata.etl.dataset.hive;

import metadata.etl.JDBCHelper;
import metagrid.common.Constant;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;


/**
 * Created by zsun on 11/16/15.
 */
public class HiveMetadataEtl extends JDBCHelper {

    public HiveMetadataEtl(Integer whEtljobId, Long whExecId, Properties prop) {
        super(whEtljobId, whExecId, prop);
    }

/*    @Override
    public Connection getConnection() throws Exception {
        Class.forName("org.trafodion.jdbc.t4.T4Driver");
        //loads the driver
        String url = prop.getProperty(Constant.TRAFODION_DB_JDBC_URL);
        logger.info("connecting to {} with {}", url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"));
        Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"), prop.getProperty(Constant.TRAFODION_DB_PASSWORD, "pwd"));
        return con;
    }*/
    @Override
    public Connection getConnection() throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        //loads the driver
        String url = prop.getProperty(Constant.HIVE_METASTORE_HIVESERVER2_URL);

        logger.info("connecting to {}", url);
        Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "hdfs"), prop.getProperty(Constant.TRAFODION_DB_PASSWORD, "pwd"));
        return con;
    }

    @Override
    public void ping() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = prop.getProperty(Constant.HIVE_METASTORE_JDBC_URL);
            try (Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.HIVE_METASTORE_USERNAME), prop.getProperty(Constant.HIVE_METASTORE_PASSWORD))) {

            }
            try (Connection conn = getConnection()) {

            }
        } catch (Exception e) {
            logger.error("Failure in connection to ", prop.getProperty(Constant.HIVE_METASTORE_JDBC_URL),
                    prop.getProperty(Constant.HIVE_METASTORE_USERNAME, e.getMessage()));
            throw e;
        }
    }

    @Override
    public void extract()
            throws Exception {
        logger.info("In Hive metadata ETL, launch extract jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/HiveExtract.py");
        logger.info("before call extract scripts " + interpreter.getSystemState().argv);
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void transform()
            throws Exception {
        logger.info("In Hive metadata ETL, launch transform jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/HiveTransform.py");
        logger.info("before call transform scripts " + interpreter.getSystemState().argv);
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void load()
            throws Exception {
        logger.info("In Hive metadata ETL, launch load jython scripts");
        int i = interpreter.getSystemState().argv.size();
        if (i < 2){
            logger.warn("before call load jython scripts, argv.length not equal 2, reset argv "+i);
            System.out.println("before call load jython scripts, argv.length not equal 2, reset argv "+i);
            PySystemState sys = configFromProperties();
            addJythonToPath(sys);
            interpreter = new PythonInterpreter(null, sys);
        }
        InputStream inputStream = classLoader.getResourceAsStream("jython/HiveLoad.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    @Override
    public void index () throws Exception {
        logger.info("In Hive metadata ETL, launch index jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In Hive metadata ETL load index scripts finished");
    }

    @Override
    public void executeUpdate(String sql)
            throws Exception {
        logger.info("===============executeUpdate===============");
        System.out.println("executeUpdate!!!");
        try (Connection conn = getConnection()) {
            try (Statement statement = conn.createStatement()) {
                logger.info("===============use chinacloud===============");
                System.out.println("use chinacloud!!!");
                statement.execute("use chinacloud");
                statement.execute(sql);
            }catch (Throwable t){
                System.out.println("执行sql出错!!!"+t.getMessage());
                System.out.println("执行sql出错!!!"+t);
                logger.error("执行sql出错:{}",t);
                logger.info("执行sql出错:{}",t);
            }
        }
    }

}
