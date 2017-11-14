/**
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.cetc.hubble.metagrid.mapper;

import com.cetc.hubble.metagrid.vo.Dataset;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

public class DatasetRowMapper implements RowMapper<Dataset>
{
    public static String DATASET_ID_COLUMN = "id";
    public static String DATASET_NAME_COLUMN = "name";
    public static String DATASET_URN_COLUMN = "urn";
    public static String DATASET_SOURCE_COLUMN = "source";
    public static String DATASET_CREATED_TIME_COLUMN = "created";
    public static String DATASET_MODIFIED_TIME_COLUMN = "modified";
    public static String DATASET_SOURCE_MODIFIED_TIME_COLUMN = "source_modified_time";
    public static String DATASET_PROPERTIES_COLUMN = "properties";
    public static String DATASET_SCHEMA_COLUMN = "schema";
    public static String DATASET_OWNER_ID_COLUMN = "owner_id";
    public static String DATASET_OWNER_NAME_COLUMN = "owner_name";
    public static String SCHEMA_HISTORY_ID_COLUMN = "schema_history_id";
    public static String HDFS_PREFIX = "hdfs";
    public static int HDFS_URN_PREFIX_LEN = 7;  //for hdfs prefix is hdfs:///, but we need the last slash


    public Dataset mapRow(ResultSet rs, int rowNum) throws SQLException
    {
        int id = rs.getInt(DATASET_ID_COLUMN);
        String name = rs.getString(DATASET_NAME_COLUMN);
        String urn = rs.getString(DATASET_URN_COLUMN);
        String source = rs.getString(DATASET_SOURCE_COLUMN);
        String strOwner = rs.getString(DATASET_OWNER_ID_COLUMN);
        String strOwnerName = rs.getString(DATASET_OWNER_NAME_COLUMN);
        String schema = rs.getString(DATASET_SCHEMA_COLUMN);
        Time created = rs.getTime(DATASET_CREATED_TIME_COLUMN);
        Time modified = rs.getTime(DATASET_MODIFIED_TIME_COLUMN);
        Integer schemaHistoryId = rs.getInt(SCHEMA_HISTORY_ID_COLUMN);
        Long sourceModifiedTime = rs.getLong(DATASET_SOURCE_MODIFIED_TIME_COLUMN);
        Dataset dataset = new Dataset();
        dataset.id = id;
        dataset.name = name;
        dataset.urn = urn;
        dataset.schema = schema;
        String[] owners = null;
        if (StringUtils.isNotBlank(strOwner))
        {
            owners = strOwner.split(",");
        }
        String[] ownerNames = null;
        if (StringUtils.isNotBlank(strOwnerName))
        {
            ownerNames = strOwnerName.split(",");
        }

        dataset.source = source;
        if (modified != null && sourceModifiedTime != null && sourceModifiedTime > 0)
        {
            dataset.modified = new java.util.Date(modified.getTime());
            dataset.formatedModified = dataset.modified.toString();
        }
        if (created != null)
        {
            dataset.created = new java.util.Date(created.getTime());
        } else if (modified != null) {
            dataset.created = new java.util.Date(modified.getTime());
        }

        if (schemaHistoryId != null && schemaHistoryId > 0)
        {
            dataset.hasSchemaHistory = true;
        }
        else
        {
            dataset.hasSchemaHistory = false;
        }

        return dataset;
    }
}
