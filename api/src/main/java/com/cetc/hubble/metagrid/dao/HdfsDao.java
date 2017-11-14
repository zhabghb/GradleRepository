package com.cetc.hubble.metagrid.dao;

import com.cetc.hubble.metagrid.vo.HdfsFileAttribute;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * HDFS Dao
 */
@Repository
public class HdfsDao extends AbstractMySQLOpenSourceDAO {

    private final static String INSERT_HDFS_FILE_ATTR =
            "insert into hdfs_file_attr(sourceId,keyword,path,filename) VALUES(?,?,?,?)";

    private final static String UPDATE_HDFS_FILE_ATTR =
            "UPDATE hdfs_file_attr SET `keyword` = ? WHERE id=?";

    private final static String DELETE_HDFS_FILE_ATTR =
            "DELETE FROM hdfs_file_attr WHERE id = ?";

    private final static String DELETE_HDFS_FILE_ATTR_BY_SOURCEID = "DELETE FROM hdfs_file_attr WHERE sourceId = ?";

    private final static String SEARCH_HDFS_FILE_ATTR =
            "SELECT * FROM hdfs_file_attr";

    private final static String GET_HDFS_FILE_ATTR =
            "SELECT * FROM hdfs_file_attr where sourceId=:sourceId and path=:path and filename=:filename";

    private final  static String DELETE_HDfS_FILE_BY_PATH="DELETE FROM hdfs_file_attr WHERE sourceId= ? and path=? AND filename=?";

    /** 插入hdfs文件属性并返回属性主键ID */
    public Integer addFileAttrReturnKey(HdfsFileAttribute hdfsFileAttribute) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int autoIncId = 0;
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con)
                    throws SQLException {
                PreparedStatement ps = con.prepareStatement(INSERT_HDFS_FILE_ATTR,PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1, hdfsFileAttribute.getSourceId());
                ps.setString(2, hdfsFileAttribute.getKeyword());
                ps.setString(3, hdfsFileAttribute.getPath());
                ps.setString(4,hdfsFileAttribute.getFileName());

                return ps;
            }
        }, keyHolder);
        autoIncId = keyHolder.getKey().intValue();
        return autoIncId;
    }

    public Integer updateFileAttr(Integer id, String keyword) {
        return getJdbcTemplate().update(UPDATE_HDFS_FILE_ATTR, keyword,id);
    }

    public Integer deleteFileAttr(Integer id) {
        return getJdbcTemplate().update(DELETE_HDFS_FILE_ATTR, id);
    }

    public Integer deleteFileAttrBySourceId(Integer sourceId) {
        return getJdbcTemplate().update(DELETE_HDFS_FILE_ATTR_BY_SOURCEID, sourceId);
    }

    public List<HdfsFileAttribute> searchFileAttr(String keyword) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(keyword)) {
            builder.append(" where keyword like \"%").append(keyword).append("%\" ");
        }
        List<HdfsFileAttribute> list = getNamedParameterJdbcTemplate().query(SEARCH_HDFS_FILE_ATTR + builder.toString(), new BeanPropertyRowMapper<HdfsFileAttribute>(HdfsFileAttribute.class));
        return list;
    }


    public List<HdfsFileAttribute> getFileAttr(Map<String, Object> params) {
        List<HdfsFileAttribute> hdfsFileAttributes = getNamedParameterJdbcTemplate().query(GET_HDFS_FILE_ATTR,new MapSqlParameterSource(params), new BeanPropertyRowMapper<HdfsFileAttribute>(HdfsFileAttribute.class));
        return hdfsFileAttributes;
    }

    public Integer deleteFileByPath(Integer sourceId,String path,String filename){
        Integer result = getJdbcTemplate().update(DELETE_HDfS_FILE_BY_PATH,sourceId,path,filename);
        return result;
    }

    public List<HdfsFileAttribute> getAllFileAttr(){
        return getNamedParameterJdbcTemplate().query(SEARCH_HDFS_FILE_ATTR,new BeanPropertyRowMapper<HdfsFileAttribute>(HdfsFileAttribute.class));
    }


}
