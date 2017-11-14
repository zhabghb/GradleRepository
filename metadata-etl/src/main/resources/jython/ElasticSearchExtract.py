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

import csv
import datetime
import json
import os
import sys
import urllib2
from metagrid.common import Constant
from org.slf4j import LoggerFactory


class ElasticSearchExtract:

    def __init__(self,args):
        self.logger = LoggerFactory.getLogger('jython script : ' + self.__class__.__name__) 
        self.table_output_list = []
        self.field_output_list = []
        # self.table_output_file = "/var/tmp/wherehows/type_metadata.dat"
        # self.field_output_file = "/var/tmp/wherehows/field_metadata.dat"
        self.table_output_file = args[Constant.ES_SCHEMA_OUTPUT_KEY]
        self.field_output_file = args[Constant.ES_FIELD_OUTPUT_KEY]
        self.wh_etl_job_id = args[Constant.WH_ETL_JOB_ID_KEY]
        print self.wh_etl_job_id
        #table_output_file = "D:\\var\\tmp\\wherehows\\type_metadata.dat"
        #field_output_file = "D:\\var\\tmp\\wherehows\\field_metadata.dat"
        self.data = {}        


    def get_es_metadata(self):
        
        elasticsearch_index_url = 'http://'+args[Constant.ELASTICSEARCH_APP_HOST_KEY]
        elasticsearch_port = args[Constant.ELASTICSEARCH_APP_PORT_KEY]
        # elasticsearch_index_url = "http://172.16.50.82"
        # elasticsearch_port = 7200
        elasticsearch_state_url = elasticsearch_index_url + ':' + str(elasticsearch_port) +  '/_cluster/state'
        req = urllib2.Request(url=elasticsearch_state_url)
        req.get_method = lambda: "GET"
        response = urllib2.urlopen(req)
        self.data = json.load(response)
        self.logger.info("get data:%s from url:{%s}  " % (self.data))


   

    def format_metadata(self):
        '''
        add table info from rows into schema
        :param rows: input. each row is a database with all it's tables
        :param schema: {schema : _, type : _, tables : ['name' : _, ... 'original_name' : _] }
        :return:
        '''
        indices_dict = self.data["metadata"]["indices"]
        indices_list = [x for x in indices_dict.iterkeys()]
        print indices_list

        for index in indices_list:
    
            types_dict= indices_dict[index]["mappings"]
            types_list = [x for x in types_dict.iterkeys()]
    
            schema_dict = {"fields": []}

            print types_list
    
            if len(types_list) == 0:
                properties = {
                    "routing_table": []
                }

                index_record = {
                    "name": index.encode('utf-8'),
                    "schema": None,
                    "schema_type": "NONE",
                    "properties": json.dumps(properties),
                    "urn":  "%s:///%s" % (self.wh_etl_job_id,index.encode('utf-8', 'ignore')),
                    "source": "ELASTICSEARCH",
                    "location_prefix":None,
                    "parent_name": None,
                    "storage_type": "Index",
                    "dataset_type": "ELASTICSEARCH"
                }            
    
                self.table_output_list.append(index_record) 
    
    
            else :
                for estype in types_list:
                    # estype_name_key = "%s.%s" % (index,estype)
                    estype_urn = "%s:///%s/%s" % (self.wh_etl_job_id,index, estype)
    
                    fields_dict = indices_dict[index]["mappings"][estype]["properties"]
                    es_fields_list = [x for x in fields_dict.iterkeys()]

                    shim_schema_dict = {"shim_fields": []}

                    # "routing_table": self.data["routing_table"]["indices"][index]
                    properties = {
                        "routing_table": []
                    }                
    
                    type_record = {
                        "name": estype.encode('utf-8', 'ignore'),
                        "schema": None,
                        "schema_type": "JSON",
                        "properties": json.dumps(properties),
                        "urn": estype_urn.encode('utf-8', 'ignore'),
                        "source": "ELASTICSEARCH",
                        "location_prefix":"/"+index.encode('utf-8', 'ignore'),
                        "parent_name": index.encode('utf-8', 'ignore'),
                        "storage_type": "Type",
                        "dataset_type": "ELASTICSEARCH"
                    }

                    for field in es_fields_list:
    
    
                        field_record = {
                            "sort_id":es_fields_list.index(field),
                            "name": field.encode('utf-8', 'ignore'),
                            "data_type": fields_dict[field]["type"] if fields_dict[field].has_key("type") else "json",
                            "nullable": "N",
                            "size": 1000,
                            "precision": None,
                            "scale": None,
                            "default_value": None,
                            "doc": None
                        }
                        
                        shim_field_record = {
                            "name": field.encode('utf-8', 'ignore'),
                            "dataType": fields_dict[field]["type"] if fields_dict[field].has_key("type") else "json"
                        }

                        field_record['dataset_urn'] = estype_urn.encode('utf-8', 'ignore')
                        schema_dict['fields'].append(field_record)
                        shim_schema_dict["shim_fields"].append(shim_field_record)

                        self.field_output_list.append(field_record)                    
    
                    schema_dict["num_fields"] = len(es_fields_list)
                    type_record["schema"] = json.dumps(shim_schema_dict["shim_fields"]).replace("\\","\\\\")

                    self.table_output_list.append(type_record)
    
            self.logger.info("table_output_list:{}".format(self.table_output_list))
            self.logger.info("field_output_list:{}".format(self.field_output_list))
        
            self.logger.info("%d dataset records generated" % len(self.table_output_list))
            self.logger.info("%d field records generated" % len(self.field_output_list))
            self.write_metadata_csv()
            
    def write_metadata_csv(self):
        csv_columns = ['name', 'schema', 'schema_type', 'properties', 'urn', 'source', 'location_prefix',
                       'parent_name', 'storage_type', 'dataset_type', 'is_partitioned']
        self.write_csv(self.table_output_file, csv_columns, self.table_output_list)
    
        csv_columns = ['dataset_urn', 'sort_id', 'name', 'data_type', 'nullable',
                       'size', 'precision', 'scale', 'default_value', 'doc']
        self.write_csv(self.field_output_file, csv_columns, self.field_output_list)
        



    def write_csv(self, csv_filename, csv_columns, data_list):
        csvfile = open(csv_filename, 'wb')
        os.chmod(csv_filename, 0644)
        writer = csv.DictWriter(csvfile, fieldnames=csv_columns, delimiter='\x1A', lineterminator='\n',
                                quoting=csv.QUOTE_NONE, quotechar='\1', escapechar='\0')
        writer.writeheader()
        for data in data_list:
            writer.writerow(data)
        csvfile.close()

    def trim_newline(self, line):
        return None if line is None else line.replace('\n', ' ').replace('\r', ' ').encode('utf-8', 'ignore')


    def run(self):
        """
        The entrance of the class, extract schema and sample data
        Notice the database need to have a order that the databases have more info (DWH_STG) should be scaned first.
        """
        begin = datetime.datetime.now().strftime("%H:%M:%S")

        self.get_es_metadata()
        self.format_metadata()
        self.write_metadata_csv()
        
        end = datetime.datetime.now().strftime("%H:%M:%S")
        self.logger.info("Extract ElasticSearch metadata [%s -> %s]" % (str(begin), str(end)))        

if __name__ == "__main__":
    args = sys.argv[1]

    es = ElasticSearchExtract(args)

    es.run()
