#
# Copyright 2015 LinkedIn Corp. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#

import json
import datetime
import sys, os
import time
from com.ziclix.python.sql import zxJDBC
from org.slf4j import LoggerFactory
from metagrid.common.writers import FileWriter
from metagrid.common.schemas import DatasetSchemaRecord, DatasetFieldRecord, HiveDependencyInstanceRecord, DatasetInstanceRecord
from metagrid.common import Constant
from HiveExtract import TableInfo

from HiveColumnParser import HiveColumnParser


class HiveTransform:
  dataset_dict = {}
  def __init__(self):
    self.logger = LoggerFactory.getLogger('jython script : ' + self.__class__.__name__)
    username = args[Constant.HIVE_METASTORE_USERNAME]
    password = args[Constant.HIVE_METASTORE_PASSWORD]
    jdbc_driver = args[Constant.HIVE_METASTORE_JDBC_DRIVER]
    jdbc_url = args[Constant.HIVE_METASTORE_JDBC_URL]
    self.wh_etl_job_id = args[Constant.WH_ETL_JOB_ID_KEY]
    self.conn_hms = zxJDBC.connect(jdbc_url, username, password, jdbc_driver)
    self.curs = self.conn_hms.cursor()

  def transform(self, input, hive_metadata, hive_field_metadata):
    """
    convert from json to csv
    :param input: input json file
    :param hive_metadata: output data file for hive table metadata
    :param hive_field_metadata: output data file for hive field metadata
    :return:
    """
    f_json = open(input)
    all_data = json.load(f_json)
    f_json.close()

    dataset_idx = -1
    schema_file_writer = FileWriter(hive_metadata)
    field_file_writer = FileWriter(hive_field_metadata)

    # one db info : 'type', 'database', 'tables'
    # one table info : required : 'name' , 'type', 'serializationFormat' ,'createTime', 'DB_ID', 'TBL_ID', 'SD_ID'
    #                  optional : 'schemaLiteral', 'schemaUrl', 'fieldDelimiter', 'fieldList'
    for one_db_info in all_data:
      i = 0
      hive_tables = one_db_info['tables']
      if len(hive_tables) == 0:
        print "----------empty database----------"
        database_urn = self.wh_etl_job_id+":///"+one_db_info['database']
        print database_urn

        dataset_schema_record = DatasetSchemaRecord(one_db_info['database'], '', 'JSON', '',database_urn,'HIVE', '','','Database', "HIVE", 'N', None, None)
        schema_file_writer.append(dataset_schema_record)

      for table in one_db_info['tables']:
        i += 1
        schema_json = {}
        prop_json = {}  # set the prop json

        for prop_name in TableInfo.optional_prop:
          if prop_name in table and table[prop_name] is not None:
            prop_json[prop_name] = table[prop_name]

        # process either schema
        field_detail_list = []


        if TableInfo.field_list in table:
          # Convert to avro
          print one_db_info['type']
          uri = "%s:///%s/%s" % (self.wh_etl_job_id,one_db_info['database'], table['dataset_name'])
          self.logger.info("Getting column definition for: %s" % (uri))
          try:
            hcp = HiveColumnParser(table, urn = uri)

            schema_json = hcp.column_type_dict['shim_fields']
            field_detail_list += hcp.column_type_list
          except:
            self.logger.error("HiveColumnParser(%s) failed!" % (uri))
            schema_json = {'fields' : {}, 'type' : 'record', 'name' : table['name'], 'uri' : uri}

        dataset_urn = "%s:///%s/%s" % (self.wh_etl_job_id,one_db_info['database'], table['dataset_name'])

        if dataset_urn not in self.dataset_dict:
          dataset_schema_record = DatasetSchemaRecord(table['dataset_name'], json.dumps(schema_json),'JSON', json.dumps(prop_json),
                                                    dataset_urn,'HIVE', '/'+one_db_info['database'],one_db_info['database'],table['type'],"HIVE","N", (table[TableInfo.create_time] if table.has_key(
            TableInfo.create_time) else None), (table["lastAlterTime"]) if table.has_key("lastAlterTime") else None)
          schema_file_writer.append(dataset_schema_record)

          dataset_idx += 1
          self.dataset_dict[dataset_urn] = dataset_idx

          for fields in field_detail_list:
            field_record = DatasetFieldRecord(fields)
            field_file_writer.append(field_record)



      schema_file_writer.flush()
      field_file_writer.flush()
      self.logger.info("%20s contains %6d tables" % (one_db_info['database'], i))

    schema_file_writer.close()
    field_file_writer.close()

  def convert_timestamp(self, time_string):
    return int(time.mktime(time.strptime(time_string, "%Y-%m-%d %H:%M:%S")))


if __name__ == "__main__":
  args = sys.argv[1]
  t = HiveTransform()
  try:
    t.transform(args[Constant.HIVE_SCHEMA_JSON_FILE_KEY],
                args[Constant.HIVE_SCHEMA_CSV_FILE_KEY],
                args[Constant.HIVE_FIELD_METADATA_KEY])
  finally:
    t.curs.close()
    t.conn_hms.close()



