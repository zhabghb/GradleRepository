package metadata.etl.dataset.hbase;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import metadata.etl.JDBCHelper;
import metadata.etl.Launcher;
import metagrid.common.Constant;
import metagrid.common.utils.Json;
import metagrid.common.vo.PreviewParam;
import metagrid.common.vo.QueryResult;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.python.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * HBase meta data ETL.
 * <p>
 * Created by tao on 16-10-18.
 */
public class HBaseMetadataETL extends JDBCHelper {

    private static Logger logger = LoggerFactory.getLogger(HBaseMetadataETL.class);
    // fields
    private static final String[] TABLE_COLUMNS = {"name", "schema", "schema_type", "properties", "urn", "source", "location_prefix",
            "parent_name", "storage_type", "dataset_type", "is_partitioned"};
    private static final String[] FIELD_COLUMNS =
            "dataset_urn,sort_id,name,data_type,nullable,size,precision,scale,default_value,remark".split(",");
    private static final String HBASE_STR = "HBase";
    private static final String SCHEMA_TYPE = "JSON";
    private static final String STORAGE_TYPE_STR = "Table";
//    private static String zkQuorum = "172.16.50.22,172.16.50.23,172.16.50.24";
//    private static String zkClientPort = "2181";

    public HBaseMetadataETL(Integer dbId, Long whExecId, Properties prop) {
        super(dbId, whExecId, prop);
    }

    @Override
    public void extract() throws Exception {

        logger.info("Start HBase meta data extraction {}:{}.",
                prop.getProperty(Constant.HBASE_ZK_QUORUM),
                prop.getProperty(Constant.HBASE_ZK_CLIENT_PORT));
        File tableFile = new File(prop.getProperty(Constant.HBASE_SCHEMA_OUTPUT_KEY));
        File fieldFile = new File(prop.getProperty(Constant.HBASE_FIELD_OUTPUT_KEY));
        Writer tableWriter = new FileWriter(tableFile);
        Writer fieldWriter = new FileWriter(fieldFile);
        CSVWriter tableCSVWriter = new CSVWriter(tableWriter, '\032', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        CSVWriter fieldCSVWriter = new CSVWriter(fieldWriter, '\032', CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
        List metaDataLs = getHBaseMetaDataLs(prop.getProperty(Constant.HBASE_ZK_QUORUM),
                prop.getProperty(Constant.HBASE_ZK_CLIENT_PORT));
        try {
            tableCSVWriter.writeNext(TABLE_COLUMNS);
            tableCSVWriter.writeAll((List) metaDataLs.get(0));
        } finally {
            tableCSVWriter.close();
        }
        try {
            fieldCSVWriter.writeNext(FIELD_COLUMNS);
            fieldCSVWriter.writeAll((List) metaDataLs.get(1));
        } finally {
            fieldCSVWriter.close();
        }
    }

    @Override
    public void transform() throws Exception {
    }

    @Override
    public void load() throws Exception {

        logger.info("In HBase metadata ETL, launch load jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/HbaseLoad.py");
        interpreter.execfile(inputStream);
        inputStream.close();
    }

    /*    @Override
        public java.sql.Connection getConnection() throws Exception {
            Class.forName("org.trafodion.jdbc.t4.T4Driver");
            //loads the driver
            String url = prop.getProperty(Constant.TRAFODION_DB_JDBC_URL);
            logger.info("connecting to {} with {}", url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"));
            java.sql.Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"), prop.getProperty(Constant.TRAFODION_DB_PASSWORD, "pwd"));
            return con;
        }*/
    @Override
    public java.sql.Connection getConnection() throws Exception {
        throw new RuntimeException("HBase不再使用JDBC连接。不能获取JDBC Connection！");
//        Class.forName("org.apache.hive.jdbc.HiveDriver");
//        //loads the driver
//        String url = prop.getProperty(Constant.HIVE_METASTORE_HIVESERVER2_URL);
//
//        logger.info("connecting to {} ", url);
//        java.sql.Connection con = DriverManager.getConnection(url, prop.getProperty(Constant.TRAFODION_DB_USERNAME, "user"), prop.getProperty(Constant.TRAFODION_DB_PASSWORD, "pwd"));
//        return con;
    }

    @Override
    public void ping() throws Exception {

        try (Connection conn = getConn(prop.getProperty(Constant.HBASE_ZK_QUORUM),
                prop.getProperty(Constant.HBASE_ZK_CLIENT_PORT), 1, 1)) {
            // next line is to make sure the conn is a correct one by get a table's connection
            TableName[] tableNameLs = conn.getAdmin().listTableNames();
            TableName tableName = tableNameLs[0];
            try (Table table = conn.getTable(tableName)) {
                logger.info("Successful connection to HBase cluster {}:{}",
                        prop.getProperty(Constant.HBASE_ZK_QUORUM),
                        prop.getProperty(Constant.HBASE_ZK_CLIENT_PORT));
            } catch (IOException e) {
                throw e;
            }
//            try (java.sql.Connection hiveServer2conn = getConnection()) {
//
//            }
//        } catch (SQLException e1) {
//            logger.error("Failure in connection to {} {}", prop.getProperty(Constant.HIVE_METASTORE_HIVESERVER2_URL), e1);
//            e1.printStackTrace();
//            throw e1;
        } catch (Exception e) {
            logger.error("Failure in connection to {} {}", prop.getProperty(Constant.HBASE_ZK_QUORUM), e);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public QueryResult preview(PreviewParam param) throws Exception {

//        Connection conn = getConn(zkQuorum, zkClientPort, 3, 3)
        QueryResult res = new QueryResult();
        try (Connection conn = getConn(prop.getProperty(Constant.HBASE_ZK_QUORUM),
                prop.getProperty(Constant.HBASE_ZK_CLIENT_PORT), 3, 3)) {
            TableName tableName = TableName.valueOf(param.getNamespace(), param.getTableName());
            try (Table table = conn.getTable(tableName)) {
                List<List<Object>> lists = Lists.newArrayList();
                Scan scan = new Scan();
                scan.setCaching(3000);
                scan.setFilter(new PageFilter(1000));
                ResultScanner resultScanner = table.getScanner(scan);
                for (Result result : resultScanner) {
                    for (Cell rowKV : result.rawCells()) {
                        // row_id, column family, column name, timestamp, value
                        List<Object> row = Lists.newArrayList();
                        row.add(new String(CellUtil.cloneRow(rowKV)));
                        row.add(new String(CellUtil.cloneFamily(rowKV)));
                        row.add(new String(CellUtil.cloneQualifier(rowKV)));
                        row.add(rowKV.getTimestamp());
                        row.add(new String(CellUtil.cloneValue(rowKV)));
                        lists.add(row);
                    }
                }
                res.setResults(lists);
            } catch (IOException e) {
                throw e;
            }
        } catch (Exception e) {
            logger.error("Failed to scan {}/{}:{} cause of {}", prop.getProperty(Constant.HBASE_ZOOKEEPER_QUORUM),
                    param.getNamespace(), param.getTableName(), e);
            e.printStackTrace();
            throw e;
        }
        return res;
    }

    private List getHBaseMetaDataLs(String zkQuorum, String zkCliPort) throws Exception {

        List tableMetaData = Lists.newArrayList();
        List fieldMetaDataLs = Lists.newArrayList();
        try (Connection conn = getConn(zkQuorum, zkCliPort, 3, 3)) {
            Admin admin = conn.getAdmin();
            NamespaceDescriptor[] nameSpaces = admin.listNamespaceDescriptors();
            // iterating over namespaces & tables to fetch all tables metadata
            for (NamespaceDescriptor namespaceDescriptor : nameSpaces) {
                // skip HBase managed tables
                // injection into property?
                if (namespaceDescriptor.getName().equals("hbase")) {
                    continue;
                }
                TableName[] tableNames = admin.listTableNamesByNamespace(namespaceDescriptor.getName());

                System.out.println("==============namespace=================="+namespaceDescriptor.getName());
                System.out.println("==============length=================="+tableNames.length);

                if (tableNames.length == 0){
                    String nameSpace = namespaceDescriptor.getName();
                    String jobID = prop.getProperty(Launcher.WH_ETL_JOB_ID);
                    String urn = String.format("%s:///%s", jobID, nameSpace);
                    String[] hnamespaceData = new String[]{nameSpace, "", "NONE", "", urn, HBASE_STR,
                            "", "", "NAMESPACE", HBASE_STR, "N"};
                    tableMetaData.add(hnamespaceData);
                }else {
                    for (TableName tableName : tableNames) {
                        String[] hTableMetaData = getHTableMetadata(admin, tableName, fieldMetaDataLs);
                        if (null != hTableMetaData) tableMetaData.add(hTableMetaData);
                    }
                }

            }
        } catch (IOException e) {
            logger.error("Connected to {} {}", zkQuorum, e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return Lists.newArrayList(tableMetaData, fieldMetaDataLs);
    }

    /**
     * Get HBase table's meta data.
     * And field (column family) meta data.
     *
     * @param admin
     * @param tableName
     * @param fieldMetaDataLs field meta data collection
     * @return The HBase table's meta data.
     */
    private String[] getHTableMetadata(Admin admin, TableName tableName, List fieldMetaDataLs) throws IOException {

        String nameSpace = tableName.getNamespaceAsString();
        String tableNameStr = tableName.getQualifierAsString(); // qualifier is the actual HTable name
        String[] blackedHeads = prop.getProperty(Constant.HBASE_METADATA_BLACKLIST).split(",");
        for (String blackHead : blackedHeads) {
            if (tableNameStr.startsWith(blackHead)) return null; // injection into property
        }
//        if (tableNameStr.startsWith("TRAFODION")) return null;
        String jobID = prop.getProperty(Launcher.WH_ETL_JOB_ID);
        String urn = String.format("%s:///%s/%s", jobID, nameSpace, tableNameStr);
        HTableDescriptor hTableDescriptor = admin.getTableDescriptor(tableName);
        HColumnDescriptor[] hColumnDescriptors = hTableDescriptor.getColumnFamilies();
        // get columns and properties
        List<Map> columns = Lists.newArrayList();
        List propertyList = Lists.newArrayList();
        // iterate over column descriptors to fetch column families and other property info
        int sortID = 0;
        for (HColumnDescriptor columnDescriptor : hColumnDescriptors) {
            sortID++;
            // families
            String columnFamilyName = columnDescriptor.getNameAsString();
            Map<String, Object> columnFamily = Maps.newHashMap();
            // for front end Json format compatibility
            columnFamily.put("name", string2Unicode(columnFamilyName));
            columnFamily.put("dataType", "ColumnFamily");
            columns.add(columnFamily);
            // dataset_urn sort_id name data_type nullable size precision scale default_value doc
            String[] fieldMetaData = new String[]{urn, sortID + "", columnFamilyName, null,
                    "Y", null, null, null, null, null};
            fieldMetaDataLs.add(fieldMetaData);
            // CF property info
            Map values = columnDescriptor.getValues();
            // turn byte values into String, cause Storage type is ImmutableBytesWritable
            Map strValues = Maps.newHashMap();
            Iterator it = values.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                ImmutableBytesWritable key = (ImmutableBytesWritable) entry.getKey();
                ImmutableBytesWritable value = (ImmutableBytesWritable) entry.getValue();
                strValues.put(Bytes.toString(key.get()), Bytes.toString(value.get()));
            }
            propertyList.add(strValues);
        }
        String schema = Json.toJson(columns).toString();
        String properties = Json.toJson(propertyList).toString();
        // name schema schema_type properties urn source
        // location_prefix parent_name storage_type dataset_type is_partitioned
        return new String[]{tableNameStr, schema, SCHEMA_TYPE, properties, urn, HBASE_STR,
                "/" + nameSpace, nameSpace, STORAGE_TYPE_STR, HBASE_STR, "N"};
    }

    /**
     * Get the HBase connection of the specified cluster.
     *
     * @param zkQuorum      Zookeeper quorum
     * @param zkCliPort     Zookeeper client port
     * @param retryTimes    retry times
     * @param recoveryTimes recovery times
     * @return
     */
    private Connection getConn(String zkQuorum, String zkCliPort, int retryTimes, int recoveryTimes) throws IOException, InterruptedException {
        Configuration conf = genericConfig(zkQuorum, zkCliPort);
        conf.setBoolean(Constant.HBASE_CLUSTER_DISTRIBUTED, true);
        conf.setInt(Constant.HBASE_CLIENT_SCANNER_CACHING, 3000);
        conf.setInt(Constant.HBASE_CLIENT_RETRIES_NUMBER, retryTimes);
        conf.setInt(Constant.HBASE_ZOOKEEPER_RECOVERY_RETRY, recoveryTimes);
        conf.setInt(Constant.HBASE_ZOOKEEPER_RECOVERY_RETRY_INTERVAL, 200);

        Connection conn = (Connection)UserGroupInformation.createRemoteUser("root").doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run () throws Exception {
                return ConnectionFactory.createConnection(conf);
            }
        });


        return conn;
    }


    /**
     * Convert String to String in unicode form
     */
    private String string2Unicode(String from) {

        char[] myBuffer = from.toCharArray();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < myBuffer.length; i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if (ub == ub.BASIC_LATIN) {
                // Alphanumeric
                builder.append(myBuffer[i]);
            } else if (ub == ub.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                // Half and Full width Character
                int j = (int) myBuffer[i] - 65248;
                builder.append((char) j);
            } else {
                // Chinese
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                builder.append("\\u").append(hexS.toLowerCase());
            }
        }

        return builder.toString();
    }


    /**
     * Retrun a general configuration instance.
     *
     * @param zkQuorum  Zookeeper quorum
     * @param zkCliPort Zookeeper client port
     * @return
     */
    private Configuration genericConfig(String zkQuorum, String zkCliPort) {

        Configuration conf = new Configuration();
        conf.set(Constant.HBASE_ZK_QUORUM, zkQuorum);
        conf.set(Constant.HBASE_ZK_CLIENT_PORT, zkCliPort);
        return conf;
    }

    @Override
    public void index () throws Exception {
        logger.info("In Hbase metadata ETL, launch index jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In Hbase metadata ETL load index scripts finished");
    }

//    public static void main(String[] args) throws Exception {
//        Properties properties = new Properties();
//        properties.put(Constant.HBASE_ZOOKEEPER_QUORUM, "172.16.50.83");
//        properties.put(Constant.HBASE_ZK_CLIENT_PORT, "2181");
//        properties.put(Constant.HBASE_SCHEMA_OUTPUT_KEY, "/tmp/metagrid");
//        properties.put(Constant.HBASE_FIELD_OUTPUT_KEY, "/tmp/metagrid");
//        HBaseMetadataETL hBaseMetadataETL = new HBaseMetadataETL(1, 1L, properties);
//        hBaseMetadataETL.extract();
//    }
}
