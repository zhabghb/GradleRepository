#coding=utf-8
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

from com.ziclix.python.sql import zxJDBC
from metagrid.common import Constant
from org.slf4j import LoggerFactory
import sys, os, datetime
import csv,json


class HbaseLoad:
  def __init__(self, args):
    print sys.getdefaultencoding()
    reload(sys)
    sys.setdefaultencoding('utf-8')
    print sys.getdefaultencoding()
    # self.logger = LoggerFactory.getLogger('jython script : ' + self.__class__.__name__)
    print "jython script : "+self.__class__.__name__

    self.sync_username=args[Constant.WH_SYNC_USERNAME_KEY]
    print self.sync_username

    username = args[Constant.WH_DB_USERNAME_KEY]
    print "username:"+username
    password = args[Constant.WH_DB_PASSWORD_KEY]
    print "password:"+password
    JDBC_DRIVER = args[Constant.WH_DB_DRIVER_KEY]
    print JDBC_DRIVER
    JDBC_URL = args[Constant.WH_DB_URL_KEY]
    print JDBC_URL
    self.input_table_file = args[Constant.HBASE_SCHEMA_OUTPUT_KEY]
    print self.input_table_file
    self.input_field_file = args[Constant.HBASE_FIELD_OUTPUT_KEY]
    print self.input_field_file
    self.csv_urn_list = []
    self.csv_tables_list = []
    self.wh_etl_job_id = args[Constant.WH_ETL_JOB_ID_KEY]
    print self.wh_etl_job_id
    self.wh_etl_exec_id = args[Constant.WH_EXEC_ID_KEY]
    print self.wh_etl_exec_id
    charset_url = JDBC_URL+"?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull"
    print charset_url
    CHARSET='utf8'
    self.conn_mysql = zxJDBC.connect(charset_url, username, password, JDBC_DRIVER,CHARSET)
    print  self.conn_mysql
    self.conn_cursor = self.conn_mysql.cursor()

    #if Constant.INNODB_LOCK_WAIT_TIMEOUT in args:
      #lock_wait_time = args[Constant.INNODB_LOCK_WAIT_TIMEOUT]
      #self.conn_cursor.execute("SET innodb_lock_wait_timeout = %s;" % lock_wait_time)

    # self.logger.info("Load Hbase Metadata into {}, wh_etl_job_id {}, wh_exec_id {}"
    #                  .format(JDBC_URL, self.wh_etl_job_id, self.wh_etl_exec_id))
    # print "Load Hbase Metadata into %s wh_etl_job_id %s, wh_exec_id %d" %(JDBC_URL, self.wh_etl_job_id, self.wh_etl_exec_id)

    self.dict_dataset_table = 'dict_dataset'
    self.dict_field_table = 'dict_field_detail'
    
  def load_csv(self):
    csvfile = file(self.input_table_file, 'rb')
    print csvfile
    reader = csv.reader(csvfile)

    for index,line in enumerate(reader):
      if index > 0:
        rows = ",".join(line).split("\x1a")
        self.csv_urn_list.append(rows[4])
        self.csv_tables_list.append(rows)
      
    csvfile.close() 

  def insert_dataset_tag(self):
    self.conn_cursor.execute("SELECT id from dict_dataset where modified_time is null and (parent_name is not null and char_length(trim(parent_name)) <> 0) and wh_etl_job_id = "+str(self.wh_etl_job_id))
    rows = self.conn_cursor.fetchall()
    for row in rows:
      dataset_tag_sql = "INSERT INTO `dataset_tag`(dataset_id,tag_id) VALUES ('{dataset_id}', '{tag_id}')".format(dataset_id=row[0],tag_id=self.wh_etl_job_id)
      print dataset_tag_sql
      self.conn_cursor.execute(dataset_tag_sql)
      self.conn_mysql.commit()


  def delete_types(self):
    self.conn_cursor.execute("SELECT id,name,alias,parent_name,urn from dict_dataset where wh_etl_job_id = "+str(self.wh_etl_job_id))
    rows = self.conn_cursor.fetchall()
    for row in rows:
      if row[4] not in self.csv_urn_list:

        print "%s not in self.csv_urn_list:%s" %(row[4],self.csv_urn_list)

        print "======there is a delete======="
        sql0 = "DELETE from domain_entity where entity_id  = {dataset_id}".format(dataset_id=row[0])
        self.conn_cursor.execute(sql0)
        self.conn_mysql.commit()
        print "sql0:{%s} executed" %(sql0)
        sql1 = "DELETE from dataset_tag where dataset_id = {dataset_id}".format(dataset_id=row[0])
        self.conn_cursor.execute(sql1)
        self.conn_mysql.commit()
        print "sql1:{%s} executed" %(sql1)
        sql2 = "DELETE from dict_field_detail where dataset_id = {dataset_id}".format(dataset_id=row[0])
        self.conn_cursor.execute(sql2)
        self.conn_mysql.commit()
        print "sql2:{%s} executed" %(sql2)
        sql3 = "DELETE from dict_dataset where id = {id}".format(id=row[0])
        self.conn_cursor.execute(sql3)
        self.conn_mysql.commit()
        print "sql3:{%s} executed" %(sql3)
        if row[3] and row[3] !="":
          sql4 = "INSERT INTO `stage_log`(update_time,type,status,dataset_name,parent_name,dataset_alias,wh_etl_job_id,wh_etl_exec_id,username) VALUES ('{update_time}', '{dataset_type}','{status}', '{type_name}', '{index_name}', '{dataset_alias}', '{wh_etl_job_id}', '{wh_etl_exec_id}','{username}')".format(update_time=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"),dataset_type='HBASE',status='DELETED',type_name=row[1],index_name=row[3],dataset_alias=row[2].replace("'","''") if row[2] else "",wh_etl_job_id=self.wh_etl_job_id,wh_etl_exec_id=self.wh_etl_exec_id,username=self.sync_username)
          self.conn_cursor.execute(sql4.decode('utf-8'))
          self.conn_mysql.commit()
          # self.logger.info("sql:{} executed".format(sql3))
          print "sql4:{%s} executed" %(sql4)


  def delete_out_of_date(self):
    self.load_csv()
    self.delete_types()  
  
  def check_dataset_updated(self):
    for row in self.csv_tables_list:
      sql = "SELECT d.id,d.schema,d.name,d.alias,d.parent_name from dict_dataset d where urn='{urn}'".format(urn=row[4])
      self.conn_cursor.execute(sql.decode('utf-8'))
      sqlrows = self.conn_cursor.fetchall()
      if len(sqlrows) == 1 and row[1].replace("\"","") != sqlrows[0][1].replace("\"","").replace("\\","\\\\"):
        sqlrow = sqlrows[0]
        print "======there is an update======="
        print row[1].replace("\"","")
        print sqlrow[1].replace("\"","")

        format_json = eval(row[1], type('Dummy', (dict,), dict(__getitem__=lambda s, n: n))())
        update_log_sql = "INSERT INTO `stage_log`(update_time,type,status,dataset_id,dataset_name,parent_name,dataset_alias,`schema`,`previous_schema`,wh_etl_job_id,wh_etl_exec_id,username) VALUES ('{update_time}', '{dataset_type}','{status}','{dataset_id}', '{type_name}', '{index_name}', '{dataset_alias}','{schema}','{previous_schema}', '{wh_etl_job_id}', '{wh_etl_exec_id}','{username}')".format(update_time=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"),dataset_type='HBASE',status='MODIFIED',dataset_id=sqlrow[0],type_name=row[0],index_name=sqlrow[4],dataset_alias=sqlrow[3].replace("'","''") if sqlrow[3] else "",schema=json.dumps(format_json),previous_schema=sqlrow[1].replace("\\","\\\\"),wh_etl_job_id=self.wh_etl_job_id,wh_etl_exec_id=self.wh_etl_exec_id,username=self.sync_username)
        print update_log_sql
        self.conn_cursor.execute(update_log_sql.decode('utf-8'))
        self.conn_mysql.commit()

        update_dataset_sql="UPDATE dict_dataset AS a SET previous_schema = a.`schema` WHERE id ={id} ".format(id=sqlrow[0])
        print update_dataset_sql
        self.conn_cursor.execute(update_dataset_sql)
        self.conn_mysql.commit()

  def check_dataset_inserted(self):
    self.conn_cursor.execute("SELECT id,name,alias,parent_name from dict_dataset where modified_time is null and (parent_name is not null and char_length(trim(parent_name)) <> 0) and wh_etl_job_id = "+str(self.wh_etl_job_id))
    rows = self.conn_cursor.fetchall()
    for row in rows:
      print "======there is an insert======="
      update_log_sql = "INSERT INTO `stage_log`(update_time,type,status,dataset_id,dataset_name,parent_name,dataset_alias,wh_etl_job_id,wh_etl_exec_id,username) VALUES ('{update_time}', '{dataset_type}','{status}','{dataset_id}', '{type_name}', '{index_name}','{dataset_alias}', '{wh_etl_job_id}', '{wh_etl_exec_id}','{username}')".format(update_time=datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S"),dataset_type='HBASE',status='CREATED',dataset_id=row[0],type_name=row[1],index_name=row[3],dataset_alias='',wh_etl_job_id=self.wh_etl_job_id,wh_etl_exec_id=self.wh_etl_exec_id,username=self.sync_username)
      print update_log_sql
      self.conn_cursor.execute(update_log_sql)
      self.conn_mysql.commit()


  def load_tables(self):
    load_tables_cmd = '''
   DELETE FROM stg_dict_dataset WHERE wh_etl_job_id = {wh_etl_job_id};

    -- load into stg table
    LOAD DATA LOCAL INFILE '{source_file}'
    INTO TABLE stg_dict_dataset
    FIELDS TERMINATED BY '\Z'
    IGNORE 1 LINES
    (`name`, `schema`, `schema_type`, `properties`, `urn`, `source`, `location_prefix`, `parent_name`,
    `storage_type`, `dataset_type`, `is_partitioned`)
    SET wh_etl_job_id = {wh_etl_job_id},
    wh_etl_exec_id = {wh_etl_exec_id};

    -- insert into final table
    INSERT INTO {dict_dataset}
    ( `name`,
      `schema`,
      schema_type,
      `fields`,
      properties,
      urn,
      `source`,
      location_prefix,
      parent_name,
      storage_type,
      ref_dataset_id,
      status_id,
      dataset_type,
      hive_serdes_class,
      is_partitioned,
      partition_layout_pattern_id,
      sample_partition_full_path,
      source_created_time,
      source_modified_time,
      created_time,
      wh_etl_job_id,
      wh_etl_exec_id
    )
    select s.name, s.schema, s.schema_type, s.fields, s.properties, s.urn,
        s.source, s.location_prefix, s.parent_name,
        s.storage_type, s.ref_dataset_id, s.status_id,
        s.dataset_type, s.hive_serdes_class, s.is_partitioned,
        s.partition_layout_pattern_id, s.sample_partition_full_path,
        s.source_created_time, s.source_modified_time, UNIX_TIMESTAMP(now()),
        s.wh_etl_job_id,s.wh_etl_exec_id
    from stg_dict_dataset s
    where s.wh_etl_job_id = {wh_etl_job_id}
    on duplicate key update
        `name`=s.name, `schema`=s.schema, schema_type=s.schema_type, `fields`=s.fields,
        properties=s.properties, `source`=s.source, location_prefix=s.location_prefix, parent_name=s.parent_name,
        storage_type=s.storage_type, ref_dataset_id=s.ref_dataset_id, status_id=s.status_id,
        dataset_type=s.dataset_type, hive_serdes_class=s.hive_serdes_class, is_partitioned=s.is_partitioned,
        partition_layout_pattern_id=s.partition_layout_pattern_id, sample_partition_full_path=s.sample_partition_full_path,
        source_created_time=s.source_created_time, source_modified_time=s.source_modified_time,
        modified_time=UNIX_TIMESTAMP(now()),wh_etl_job_id=s.wh_etl_job_id, wh_etl_exec_id=s.wh_etl_exec_id
    ;
    
    analyze table {dict_dataset}
    '''.format(wh_etl_job_id=self.wh_etl_job_id, source_file=self.input_table_file, wh_etl_exec_id=self.wh_etl_exec_id,
               dict_dataset=self.dict_dataset_table)

    print "----------about to excute sql------------"
    self.executeCommands(load_tables_cmd)
    # self.logger.info("finish loading Hbase table metadata from {} to {}"
    #                  .format(self.input_table_file, self.dict_dataset_table))
    print "finish loading Hbase table metadata from %s to %s" %(self.input_table_file, self.dict_dataset_table)


  def load_fields(self):
    load_fields_cmd = '''
          DELETE FROM stg_dict_field_detail where wh_etl_job_id = {wh_etl_job_id};

          LOAD DATA LOCAL INFILE '{source_file}'
          INTO TABLE stg_dict_field_detail
          FIELDS TERMINATED BY '\Z'
          IGNORE 1 LINES
          (urn, sort_id, field_name, data_type, is_nullable, @vdata_size, @vdata_precision,
          @vdata_scale, @vdefault_value, @remark)
          SET wh_etl_job_id = {wh_etl_job_id},
          data_size = nullif(@vdata_size,''),
          data_precision = nullif(@vdata_precision,''),
          data_scale = nullif(@vdata_scale,''),
          default_value = nullif(@vdefault_value,''),
          field_label = nullif(@remark,'')
          ;
          -- show warnings limit 20;
          analyze table stg_dict_field_detail;

          update stg_dict_field_detail
          set default_value = trim(default_value) where wh_etl_job_id = {wh_etl_job_id};

          -- delete old record if it does not exist in this load batch anymore (but have the dataset id)
          create temporary table if not exists t_deleted_fields (primary key (field_id))
            select x.field_id
              from stg_dict_field_detail s
                join {dict_dataset} i
                  on s.urn = i.urn
                  and s.wh_etl_job_id = {wh_etl_job_id}
                right join {dict_field_detail} x
                  on i.id = x.dataset_id
                  and s.field_name = x.field_name
                  and s.parent_path = x.parent_path
            where s.field_name is null
              and x.dataset_id in (
                         select d.id dataset_id
                         from stg_dict_field_detail k join {dict_dataset} d
                           on k.urn = d.urn
                          and k.wh_etl_job_id = {wh_etl_job_id}
              )
          ; -- run time : ~2min

          delete from {dict_field_detail} where field_id in (select field_id from t_deleted_fields);

          -- update the old record if some thing changed
          update {dict_field_detail} t join
          (
            select x.field_id, s.*
            from stg_dict_field_detail s
            join {dict_dataset} d
              on s.urn = d.urn
            join {dict_field_detail} x
              on s.field_name = x.field_name
              and coalesce(s.parent_path, '*') = coalesce(x.parent_path, '*')
              and d.id = x.dataset_id
            where s.wh_etl_job_id = {wh_etl_job_id}
              and (x.sort_id <> s.sort_id
                  or x.parent_sort_id <> s.parent_sort_id
                  or x.data_type <> s.data_type
                  or x.data_size <> s.data_size or (x.data_size is null XOR s.data_size is null)
                  or x.data_precision <> s.data_precision or (x.data_precision is null XOR s.data_precision is null)
                  or x.is_nullable <> s.is_nullable or (x.is_nullable is null XOR s.is_nullable is null)
                  or x.is_partitioned <> s.is_partitioned or (x.is_partitioned is null XOR s.is_partitioned is null)
                  or x.default_value <> s.default_value or (x.default_value is null XOR s.default_value is null)
              )
          ) p
            on t.field_id = p.field_id
          set t.sort_id = p.sort_id,
              t.parent_sort_id = p.parent_sort_id,
              t.data_type = p.data_type,
              t.data_size = p.data_size,
              t.data_precision = p.data_precision,
              t.is_nullable = p.is_nullable,
              t.is_partitioned = p.is_partitioned,
              t.default_value = p.default_value,
              t.modified = now()
          ;

          insert into {dict_field_detail} (
            dataset_id, sort_id, parent_sort_id, parent_path,
            field_name, field_label,data_type, data_size, is_nullable, default_value, modified,wh_etl_job_id
          )
          select
            d.id, sf.sort_id, sf.parent_sort_id, sf.parent_path,
            sf.field_name, sf.field_label,sf.data_type, sf.data_size, sf.is_nullable, sf.default_value, now(),sf.wh_etl_job_id
          from stg_dict_field_detail sf join {dict_dataset} d
            on sf.urn = d.urn
               left join {dict_field_detail} t
            on d.id = t.dataset_id
            and sf.field_name = t.field_name
            and sf.parent_path = t.parent_path
          where sf.wh_etl_job_id = {wh_etl_job_id} and t.field_id is null
          ;

          analyze table {dict_field_detail}
      '''.format(source_file=self.input_field_file, wh_etl_job_id=self.wh_etl_job_id,
                 dict_dataset=self.dict_dataset_table, dict_field_detail=self.dict_field_table)

    print "about to excute sql..."
    self.executeCommands(load_fields_cmd)
    # self.logger.info("finish loading Oracle type fields from {} to {}"
    #                  .format(self.input_field_file, self.dict_field_table))
    print "finish loading Hbase field metadata from %s to %s" %(self.input_table_file, self.dict_dataset_table)


  def executeCommands(self, commands):
    for cmd in commands.split(";"):
      # self.logger.debug(cmd)
      print "cmd:"+cmd
      self.conn_cursor.execute(cmd)
      self.conn_mysql.commit()

  def run(self):
    try:
      begin = datetime.datetime.now().strftime("%H:%M:%S")
      self.delete_out_of_date()
      self.check_dataset_updated()
      print "load tables..."
      self.load_tables()

      # print "insert dataset tag..."
      # self.insert_dataset_tag()

      #check whether it is a new job
      sql = "SELECT wh_etl_exec_id FROM wh_etl_job_execution WHERE wh_etl_job_id = {wh_etl_job_id} and `status` = 'SUCCEEDED'".format(wh_etl_job_id=self.wh_etl_job_id)
      self.conn_cursor.execute(sql)
      rows = self.conn_cursor.fetchall()
      if len(rows) > 0 :
        self.check_dataset_inserted()
      else :
        print "======this is a new job======="

      print "load fields..."
      self.load_fields()

      end = datetime.datetime.now().strftime("%H:%M:%S")
      # self.logger.info("Load Hbase metadata [%s -> %s]" % (str(begin), str(end)))
      print "Load Hbase metadata [%s -> %s]" % (str(begin), str(end))
    finally:
      self.conn_cursor.close()
      self.conn_mysql.close()


if __name__ == "__main__":
  args = sys.argv[1]

  hbase = HbaseLoad(args)

  hbase.run()
