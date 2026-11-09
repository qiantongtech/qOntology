UPDATE `om_target_task` SET `task_name` = CASE `id` WHEN 1 THEN '仓库实体及层级属性定义' WHEN 2 THEN '物料与库存批次关联关系定义' WHEN 3 THEN '库存状态枚举完整性定义' WHEN 4 THEN '入库单实体及状态流转定义' WHEN 5 THEN '出库单与收货方关联关系定义' WHEN 6 THEN '盘点单核心属性定义' WHEN 7 THEN '库存台账数据类型及计量单位定义' WHEN 8 THEN '物料与库存台账关联关系定义' WHEN 9 THEN '库位编码规则定义' WHEN 10 THEN '出入库业务场景覆盖验证' WHEN 11 THEN '本体建模范围边界验证' ELSE `task_name` END WHERE `target_id` = 11 AND `del_flag` = 0 AND `id` IN (1,2,3,4,5,6,7,8,9,10,11) AND (`task_name` IS NULL OR TRIM(`task_name`) = '');

ALTER TABLE `basic_file_category` ADD COLUMN `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常，1停用' AFTER `ancestors`;
ALTER TABLE basic_datasource ADD status tinyint DEFAULT 0 NOT NULL COMMENT '状态：0正常，1停用' after description;
