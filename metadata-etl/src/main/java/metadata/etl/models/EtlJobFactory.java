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
package metadata.etl.models;

import metadata.etl.EtlJob;
import metadata.etl.dataset.elasticsearch.ElasticSearchMetadataEtl;
import metadata.etl.dataset.hbase.HBaseMetadataETL;
import metadata.etl.dataset.hdfs.HdfsMetadataEtl;
import metadata.etl.dataset.hive.HiveMetadataEtl;
import metadata.etl.dataset.oracle.OracleMetadataEtl;
import metadata.etl.dataset.pgxz.PGXZMetadataEtl;
import metadata.etl.dataset.trafodion.TrafodionMetadataEtl;
import metadata.etl.elasticsearch.ElasticSearchBuildIndexETL;

import java.util.Properties;


/**
 * Created by zechen on 10/21/15.
 */
public class EtlJobFactory {

  public static EtlJob getEtlJob(EtlJobName etlJobName, Integer whEtljobId, Long whExecId, Properties properties) {
    switch (etlJobName) {
      case HIVE_DATASET_METADATA_ETL:
        return new HiveMetadataEtl(whEtljobId, whExecId, properties);
      case ELASTICSEARCH_EXECUTION_INDEX_ETL:
        return new ElasticSearchBuildIndexETL(whEtljobId, whExecId, properties);
      case TREEBUILDER_EXECUTION_DATASET_ETL:
        return new ElasticSearchBuildIndexETL(whEtljobId, whExecId, properties);
      case ORACLE_DATASET_METADATA_ETL:
        return new OracleMetadataEtl(whEtljobId, whExecId, properties);
      case ELASTICSEARCH_DATASET_METADATA_ETL:
        return new ElasticSearchMetadataEtl(whEtljobId, whExecId, properties);
      case TRAFODION_DATASET_METADATA_ETL:
        return new TrafodionMetadataEtl(whEtljobId, whExecId, properties);
      case PGXZ_DATASET_METADATA_ETL:
        return new PGXZMetadataEtl(whEtljobId, whExecId, properties);
      case HBASE_DATASET_METADATA_ETL:
        return new HBaseMetadataETL(whEtljobId, whExecId, properties);
//      case HDFS_DATASET_METADATA_ETL:
//        return new HdfsMetadataEtl();
      default:
        throw new UnsupportedOperationException("Unsupported job type: " + etlJobName);
    }
  }
}
