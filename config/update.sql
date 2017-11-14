/*
此update语句在2017-10-13前数据库的基础上增加。此前稳定的代码版本是Hubble1.5。
从2017年4月份安徽高检部署后数据库没有变化，也可以认为是在2017年4月份安徽高检的基础上的更新
*/

-- 数据资源集合（原来的数据主题）添加类型2017-10-13
ALTER TABLE `data_domain`
  ADD COLUMN `type` VARCHAR(255) NULL DEFAULT NULL COMMENT '原始数据origin、主题数据domain、服务数据service' AFTER `name`;

-- 数据资源集合（原来的数据主题）name可以重复，name、type的组合不能重复 2017-10-17
ALTER TABLE `data_domain`
  DROP INDEX `uq_name`,
  ADD UNIQUE INDEX `unique_name_type` (`name`, `type`);