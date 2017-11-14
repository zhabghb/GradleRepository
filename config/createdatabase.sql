CREATE DATABASE metagrid
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_bin;

CREATE USER 'metagrid';
SET PASSWORD FOR 'metagrid' = PASSWORD('metagrid');
GRANT ALL ON metagrid.* TO 'metagrid';
grant all privileges on metagrid.* to metagrid@localhost identified by 'metagrid';
grant all privileges on metagrid.* to metagrid@'%' identified by 'metagrid';
flush privileges;