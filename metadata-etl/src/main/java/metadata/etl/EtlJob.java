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
package metadata.etl;

import metagrid.common.Constant;
import metagrid.common.vo.PreviewParam;
import metagrid.common.vo.QueryParam;
import metagrid.common.vo.QueryResult;
import org.python.core.PyDictionary;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public abstract class EtlJob {

    public PythonInterpreter interpreter;
    public Properties prop;
    public ClassLoader classLoader = getClass().getClassLoader();
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Used by backend service
     *
     * @param appId      nullable
     * @param whEtljobId nullable
     * @param whExecId
     * @param properties
     */
    public EtlJob(Integer appId, Integer whEtljobId, Long whExecId, Properties properties) {
        properties.putAll(loadFileProperties());
        this.prop = properties;
        if (whExecId != null) {
            PySystemState sys = configFromProperties();
            addJythonToPath(sys);
            interpreter = new PythonInterpreter(null, sys);
        }
    }

    private Properties loadFileProperties() {
        Properties prop = new Properties();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("job.properties")) {
            if (inputStream != null)
                prop.load(inputStream);
        } catch (Throwable e) {
            logger.warn("couldn't find job.properties");
        }

        return prop;
    }

    protected void addJythonToPath(PySystemState pySystemState) {
        URL url = classLoader.getResource("jython");
        if (url != null) {
            File file = new File(url.getFile());
            String path = file.getPath();
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }
            pySystemState.path.append(new PyString(path.replace("!", "")));
        }
    }


    /**
     * Copy all properties into jython envirenment
     *
     * @return PySystemState A PySystemState that contain all the arguments.
     */
    protected PySystemState configFromProperties() {

//    if (appId != null)
//      prop.setProperty(Constant.APP_ID_KEY, String.valueOf(appId));
//        if (whEtljobId != null)
//            prop.setProperty(Constant.WH_ETL_JOB_ID_KEY, String.valueOf(whEtljobId));
//        prop.setProperty(Constant.WH_EXEC_ID_KEY, String.valueOf(whExecId));
        PyDictionary config = new PyDictionary();
        for (String key : prop.stringPropertyNames()) {
            String value = prop.getProperty(key);
            config.put(new PyString(key), new PyString(value));
        }
        PySystemState sys = new PySystemState();
        sys.argv.append(config);
        return sys;
    }

    public abstract void ping() throws Exception;

    public abstract QueryResult query(QueryParam param) throws Exception;

    public abstract void extract()
            throws Exception;

    public abstract void transform()
            throws Exception;

    public abstract void load()
            throws Exception;

    public abstract void index()
            throws Exception;

    public void setup()
            throws Exception {
        // redirect error to out
//        System.setErr(System.out);
    }

    public void close()
            throws Exception {
        interpreter.cleanup();
        interpreter.close();
    }

    public void run()
            throws Exception {
        setup();
        extract();
        transform();
        load();
        if (this.prop.containsKey(Constant.WH_ELASTICSEARCH_URL_KEY)){
            index();
        }
        close();
    }

    public void executeUpdate(List<String> sql)
            throws Exception {
    }

    public void executeUpdate(String sql)
            throws Exception {
    }



    /**
     * Only used for HBase data preview.
     *
     * @param param
     * @return
     * @throws Exception
     */
    public QueryResult preview(PreviewParam param) throws Exception {
        return null;
    }
}
