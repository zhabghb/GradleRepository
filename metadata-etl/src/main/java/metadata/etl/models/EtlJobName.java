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
package metadata.etl.models;

/**
 * Created by zechen on 9/4/15.
 */
public enum EtlJobName {
    ELASTICSEARCH_EXECUTION_INDEX_ETL(EtlType.OPERATION),
    TREEBUILDER_EXECUTION_DATASET_ETL(EtlType.OPERATION),
    ORACLE_DATASET_METADATA_ETL(EtlType.ORACLE),
    ELASTICSEARCH_DATASET_METADATA_ETL(EtlType.ELASTICSEARCH),
    PGXZ_DATASET_METADATA_ETL(EtlType.PGXZ),
    TRAFODION_DATASET_METADATA_ETL(EtlType.TRAFODION),
    HBASE_DATASET_METADATA_ETL(EtlType.HBASE),
    HIVE_DATASET_METADATA_ETL(EtlType.HIVE),
    HDFS_DATASET_METADATA_ETL(EtlType.HDFS);


    EtlType etlType;

    EtlJobName(EtlType etlType) {
        this.etlType = etlType;
    }

    public EtlType getEtlType() {
        return etlType;
    }

    public boolean affectDataset() {
        return this.getEtlType().equals(EtlType.DATASET);
    }

    public boolean affectFlow() {
        return this.getEtlType().equals(EtlType.OPERATION);
    }
}
