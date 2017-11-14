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
package metadata.etl.elasticsearch;

import metadata.etl.EtlJob;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;

import java.io.InputStream;
import java.util.Properties;


public class ElasticSearchBuildIndexETL extends EtlJob {

    public ElasticSearchBuildIndexETL(int whEtljobId, long whExecId, Properties properties) {
        super(whEtljobId, null, whExecId, properties);
    }

    public void ping() throws Exception {

    }

    public QueryResult query(QueryParam param) throws Exception {
        return null;
    }

    @Override
    public void extract()
            throws Exception {
        logger.info("In ElasticSearchBuildIndexETL java launch extract jython scripts");
    }

    @Override
    public void transform()
            throws Exception {
        logger.info("In ElasticSearchBuildIndexETL java launch transform jython scripts");
    }

    @Override
    public void load()
            throws Exception {
        logger.info("In ElasticSearchBuildIndexETL java launch load jython scripts");
        InputStream inputStream = classLoader.getResourceAsStream("jython/ElasticSearchIndex.py");
        interpreter.execfile(inputStream);
        inputStream.close();
        logger.info("In ElasticSearchBuildIndexETL java load jython scripts finished");
    }

    @Override
    public void index () throws Exception {

    }


}
