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

from metagrid.common import Constant
from com.ziclix.python.sql import zxJDBC
import DbUtil
import sys, os, datetime
import json
import urllib
import urllib2
from org.slf4j import LoggerFactory


class ElasticSearchIndex():
  def __init__(self, args):
    self.logger = LoggerFactory.getLogger('jython script : ' + self.__class__.__name__)
    self.elasticsearch_index_url = args[Constant.WH_ELASTICSEARCH_URL_KEY]
    print "elasticsearch_index_url:"+self.elasticsearch_index_url
    self.wh_etl_job_id = args[Constant.WH_ETL_JOB_ID_KEY]
    print "wh_etl_job_id:"+self.wh_etl_job_id
    # self.elasticsearch_port = args[Constant.WH_ELASTICSEARCH_PORT_KEY]
    self.wh_con = zxJDBC.connect(args[Constant.WH_DB_URL_KEY],
                                 args[Constant.WH_DB_USERNAME_KEY],
                                 args[Constant.WH_DB_PASSWORD_KEY],
                                 args[Constant.WH_DB_DRIVER_KEY])
    self.wh_cursor = self.wh_con.cursor()

  def bulk_insert(self, params, url):
    try:

      data='\n'.join(params) + '\n'
      print data.encode('utf-8', 'ignore')
      request = urllib2.Request(url, data=data.encode('utf-8', 'ignore'))
      request.add_header('Content-type', 'application/json')
      # request.add_header('User-Agent','Mozilla/5.0 (Windows NT 5.1; rv:10.0.1) Gecko/20100101 Firefox/10.0.1')
      request.get_method = lambda: "PUT"
      response = urllib2.urlopen(request)
      # response = urllib2.urlopen(req)
      data = json.load(response)
      print "=============insert successfully=================="
      if str(data['errors']) != 'False':
        self.logger.info(str(data))
        print "===========error============="
        print str(data)
    except urllib2.HTTPError as e:
      print "===========bulk_insert HTTPError============="
      print str(e.code)
      print e.read()
      self.logger.error(str(e.code))
      self.logger.error(e.read())


  def mapping(self, params, url):
    try:
      req = urllib2.Request(url=url)
      req.add_header('Content-type', 'application/json')
      req.get_method = lambda: "PUT"
      req.add_data(params)
      response = urllib2.urlopen(req)
      data = json.load(response)
      print "=============mapping successfully=================="
    except urllib2.HTTPError as e:
      print "===========mapping HTTPError============="
      print str(e.code)
      print e.read()
      self.logger.error(str(e.code))
      self.logger.error(e.read())


  def update_dataset_field(self, last_time=None):
      if last_time:
          sql = """
            SELECT * FROM dict_field_detail WHERE wh_etl_job_id = %s AND modified >= DATE_SUB(%s, INTERVAL 1 HOUR)
            """ % (self.wh_etl_job_id,last_time)
      else:
          sql = """
            SELECT * FROM dict_field_detail WHERE wh_etl_job_id = %s
          """ % self.wh_etl_job_id

      url = self.elasticsearch_index_url + '/metagrid/field/_bulk'
      params = []
      self.wh_cursor.execute(sql)
      description = [x[0] for x in self.wh_cursor.description]
      row_count = 1
      for result in self.wh_cursor:
          row = dict(zip(description, result))
          params.append('{ "index": { "_id": ' +
                        str(row['field_id']) + ', "parent": ' + str(row['dataset_id']) + '  }}')
          params.append("""{"dataset_id": %s, "sort_id": %s, "field_name": "%s", "field_label": "%s", "parent_path": "%s"}"""
          % (row['dataset_id'] if row['dataset_id'] else 0, row['sort_id'] if row['sort_id'] else 0,
          row['field_name'] if row['field_name'] else '',row['field_label'] if row['field_label'] else '', row['parent_path'] if row['parent_path'] else ''))

          if row_count % 1000 == 0:
              print "=========bulk_insert============="
              self.bulk_insert(params, url)
              params = []
          row_count += 1
      if len(params) > 0:
          print "=========bulk_insert============="
          self.bulk_insert(params, url)
          print 'field size:' + str(len(params))


  def update_comment(self, last_time=None):
    if last_time:
        sql = """
          SELECT * FROM comments WHERE modified >= DATE_SUB(%s, INTERVAL 1 HOUR)
          """ % last_time
    else:
        sql = """
          SELECT * FROM comments
          """

    url = self.elasticsearch_index_url+  '/metagrid/comment/_bulk'
    params = []
    self.wh_cursor.execute(sql)
    row_count = 1
    description = [x[0] for x in self.wh_cursor.description]
    for result in self.wh_cursor:
      row = dict(zip(description, result))
      params.append('{ "index": { "_id": ' + str(row['id']) + ', "parent": ' + str(row['dataset_id']) + '  }}')
      params.append(
          """{ "text": %s, "user_id": %s, "dataset_id": %s, "comment_type": "%s"}"""
          % (json.dumps(row['text']) if row['text'] else '', row['user_id'] if row['user_id'] else 0,
             row['dataset_id'] if row['dataset_id'] else 0, row['comment_type'] if row['comment_type'] else ''))
      if row_count % 1000 == 0:
        self.bulk_insert(params, url)
        params = []
      row_count += 1
    if len(params) > 0:
      self.bulk_insert(params, url)

  def update_dataset(self, last_unixtime=None):
    if last_unixtime:
        sql = """
          SELECT dd.*,wej.data_source_name FROM dict_dataset dd LEFT JOIN wh_etl_job wej ON dd.wh_etl_job_id = wej.wh_etl_job_id   WHERE dd.wh_etl_job_id = %s  AND from_unixtime(dd.modified_time) >= DATE_SUB(from_unixtime(%f), INTERVAL 1 HOUR)
          """ % (self.wh_etl_job_id,last_unixtime)
    else:
        sql = """
        SELECT dd.*,wej.data_source_name FROM dict_dataset dd LEFT JOIN wh_etl_job wej ON dd.wh_etl_job_id = wej.wh_etl_job_id WHERE dd.wh_etl_job_id = %s
        """ % self.wh_etl_job_id

    url = self.elasticsearch_index_url+  '/metagrid/dataset/_bulk'
    params = []
    self.wh_cursor.execute(sql)
    description = [x[0] for x in self.wh_cursor.description]
    print description
    row_count = 1
    for result in self.wh_cursor:
      row = dict(zip(description, result))
      params.append('{ "index": { "_id": ' + str(row['id']) + ' }}')
      params.append(
          """{ "name": "%s",  "alias": "%s","source_name": "%s", "source": "%s", "urn": "%s", "location_prefix": "%s", "parent_name": "%s","schema_type": "%s", "properties": %s, "schema": %s }"""
          % (row['name'] if row['name'] else '', row['alias'] if row['alias'] else '',row['data_source_name'] if row['data_source_name'] else '',row['source'] if row['source'] else '',
             row['urn'] if row['urn'] else '', row['location_prefix'] if row['location_prefix'] else '',
             row['parent_name'] if row['parent_name'] else '', row['schema_type'] if row['schema_type'] else '',
             json.dumps(row['properties'])  if row['properties'] else '',
             json.dumps(row['schema'])  if row['schema'] else ''))

      if row_count % 1000 == 0:
        self.bulk_insert(params, url)
        self.logger.info('dataset' + str(row_count))
        params = []
      row_count += 1
    if len(params) > 0:
      self.bulk_insert(params, url)
      print 'dataset size:' + str(len(params))
      self.logger.info('dataset' + str(len(params)))


  def get_es_version(self):
      elasticsearch_index_url = self.elasticsearch_index_url
      try:
          req = urllib2.Request(url=elasticsearch_index_url)
          req.get_method = lambda: "GET"
          response = urllib2.urlopen(req)
          print response
          data = json.load(response)
          return data["version"]["number"][0]
      except urllib2.HTTPError as e:
          print "===========get_es_version HTTPError============="+str(e.code)
          print "===========get_es_version HTTPError============="+e.read()
          self.logger.error(str(e.code))
          self.logger.error(e.read())

  def create_mapping(self):
      elasticsearch_index_url = self.elasticsearch_index_url+  '/metagrid/'
      try:
          req = urllib2.Request(url=elasticsearch_index_url)
          req.get_method = lambda: "GET"
          response = urllib2.urlopen(req)
          print response
      except urllib2.HTTPError as e:
          print "===========create_mapping HTTPError============="+str(e.code)
          if e.code == 404:
              print "creating mapping...."
              version = self.get_es_version()
              if version == "5":
                mapping = '''{"mappings":{"dataset":{"properties":{"alias":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"location_prefix":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"name":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"parent_name":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"properties":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"schema":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"schema_type":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"source":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"source_name":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"urn":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}}}},"field":{"_parent":{"type":"dataset"},"_routing":{"required":true},"properties":{"dataset_id":{"type":"long"},"field_label":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"field_name":{"type":"text","analyzer":"simple","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"parent_path":{"type":"text","fields":{"keyword":{"type":"keyword","ignore_above":256}}},"sort_id":{"type":"long"}}}}}'''
              else :
                mapping = '''{"mappings":{"dataset":{"properties":{"alias":{"type": "string","analyzer": "simple"},"location_prefix":{"type": "string"},"name":{"type": "string","analyzer": "simple"},"parent_name":{"type": "string","analyzer": "simple"},"properties":{"type": "string"},"schema":{"type": "string"},"schema_type":{"type": "string"},"source":{"type": "string"},"source_name":{"type": "string","analyzer": "simple"},"urn":{"type": "string"}}},"field":{"_parent":{"type": "dataset"},"_routing":{"required": true},"properties":{"dataset_id":{"type": "long"},"field_label":{"type": "string","analyzer": "simple"},"field_name":{"type": "string","analyzer": "simple"},"parent_path":{"type": "string"},"sort_id":{"type": "long"}}}}}'''

              self.mapping(mapping,elasticsearch_index_url)
          self.logger.error(str(e.code))
          self.logger.error(e.read())


  def run(self):

    try:
      self.create_mapping()
      # {"mappings": {"dataset": {},"field": {"_parent": {"type": "dataset"}}}}
      begin = datetime.datetime.now().strftime("%H:%M:%S")
      print "building index for dataset..."
      self.update_dataset()
      print "building index for field..."
      self.update_dataset_field()
      end = datetime.datetime.now().strftime("%H:%M:%S")
      print "building index for metadata finished [%s -> %s]" % (str(begin), str(end))
    finally:
      self.wh_cursor.close()
      self.wh_con.close()

if __name__ == "__main__":
  props = sys.argv[1]
  esi = ElasticSearchIndex(props)
  esi.run()
