package com.cetc.hubble.metagrid.service;

import com.cetc.hubble.metagrid.controller.RelationshipController;
import com.cetc.hubble.metagrid.dao.DataSourceManagerDAO;
import com.cetc.hubble.metagrid.dao.SqlDAO;
import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import com.cetc.hubble.metagrid.vo.DataSource;
import com.cetc.hubble.metagrid.vo.DataSourceProperty;
import com.cetc.hubble.metagrid.vo.QueryHistory;
import com.google.common.collect.Lists;
import metadata.etl.EtlJob;
import metadata.etl.models.EtlJobFactory;
import metadata.etl.models.EtlJobName;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

/**
 * Created by dahey on 16-10-31.
 */
@Service
public class SqlService {

    private static Logger logger = LoggerFactory.getLogger(RelationshipController.class);

    @Autowired
    private SqlDAO sqlDAO;
    @Autowired
    private DataSourceManagerDAO sourceManagerDAO;

    public QueryResult query(QueryParam param) throws Exception{
        DataSource dataSource = sourceManagerDAO.getEtlJobById(param.getSourceId());
        if (dataSource == null) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }

        saveSql(param);

        if ("HBASE".equalsIgnoreCase(param.getSourceType())) {
            Statement statement = CCJSqlParserUtil.parse(param.getSql());
            Select selectStatement = (Select) statement;
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> tableList = tablesNamesFinder.getTableList(selectStatement);
            if (tableList == null || tableList.size() == 0) {
                throw new AppException("SQL语句有误!", ErrorCode.BAD_REQUEST);
            }
            for (String table : tableList) {
                String[] tables = table.split("\\.");
                if (tables.length != 2) {
                    throw new AppException("SQL语句有误!", ErrorCode.BAD_REQUEST);
                }
                if (!sqlDAO.isHbaseTable(tables[1], tables[0])) {
                    throw new AppException("表" + tables[0] + "." + tables[1] + "不存在!", ErrorCode.BAD_REQUEST);
                }
            }
        }

        EtlJobName etlJobName = EtlJobName.valueOf(dataSource.getEtlJobName());
        Properties prop = new Properties();
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            prop.setProperty(p.getPropertyName(), p.getPropertyValue());
        }
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, param.getSourceId(), null, prop);

        String sql = param.getSql().trim();
        while (sql.endsWith(";")) {
            sql = sql.substring(0, sql.trim().length() - 1).trim();
        }
        param.setSql(sql);
        QueryResult queryResult = etlJob.query(param);

        //使用hive jdbc查询出来的字段会有表名前缀，去之。
        if ("HIVE".equals(param.getSourceType())||"HBASE".equals(param.getSourceType())){
            List<String> colNames = queryResult.getColNames();
            List<String> newColNames = Lists.newArrayList();
            for (String s:colNames) {
                String[] strings = s.split("\\.");
                if (strings.length==1){
                    newColNames = colNames;
                    break;
                }else {
                    newColNames.add(strings[1]);
                }
            }
            queryResult.setColNames(newColNames);
        }

        return queryResult;
    }

    public void executeUpdate(int sourceId, List<String> sql) throws Exception {
        DataSource dataSource = sourceManagerDAO.getEtlJobById(sourceId);
        if (dataSource == null) {
            throw new AppException("该数据源已被删除!", ErrorCode.BAD_REQUEST);
        }

        EtlJobName etlJobName = EtlJobName.valueOf(dataSource.getEtlJobName());
        Properties prop = new Properties();
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            prop.setProperty(p.getPropertyName(), p.getPropertyValue());
        }
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, sourceId, null, prop);

        etlJob.executeUpdate(sql);
    }
    public void executeUpdateHive(int sourceId, String sql) {
        DataSource dataSource = sourceManagerDAO.getEtlJobById(sourceId);
        if (dataSource == null) {
            throw new AppException("该数据源不存在!", ErrorCode.BAD_REQUEST);
        }
        EtlJobName etlJobName = EtlJobName.valueOf(dataSource.getEtlJobName());
        Properties prop = new Properties();
        for (DataSourceProperty p : dataSource.getEtlJobProperties()) {
            prop.setProperty(p.getPropertyName(), p.getPropertyValue());
        }
        EtlJob etlJob = EtlJobFactory.getEtlJob(etlJobName, sourceId, null, prop);

        logger.info("etl job name:{},id:{}",new Object[]{etlJobName, sourceId});
        try {
            etlJob.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("执行sql出错:{}",e.getMessage());
            throw new AppException("建立模型存储表失败!", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }



    public void saveSql(QueryParam param) {
        QueryHistory queryHistory = new QueryHistory();
        queryHistory.setSql(param.getSql());
        queryHistory.setSourceId(param.getSourceId());
        saveSql(queryHistory);
    }

    public void saveSql(QueryHistory queryHistory) {
        sqlDAO.insert(queryHistory);
    }

    public List<QueryHistory> listSqlHistories(int sourceId,String db,int limit) {
        return sqlDAO.getSqlHistories(sourceId,db,limit);
    }
}
