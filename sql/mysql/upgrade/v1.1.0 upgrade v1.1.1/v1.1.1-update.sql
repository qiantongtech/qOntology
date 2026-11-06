
/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */


ALTER TABLE basic_data_type ADD max_length int NULL COMMENT '最大长度';
ALTER TABLE basic_data_type CHANGE max_length max_length int NULL COMMENT '最大长度' AFTER enum_type;
ALTER TABLE basic_data_type ADD min_length int NULL COMMENT '最小长度';
ALTER TABLE basic_data_type CHANGE min_length min_length int NULL COMMENT '最小长度' AFTER max_length;
ALTER TABLE basic_data_type ADD earliest_time datetime NULL COMMENT '最早日期';
ALTER TABLE basic_data_type CHANGE earliest_time earliest_time datetime NULL COMMENT '最早日期' AFTER min_length;
ALTER TABLE basic_data_type ADD latest_time datetime NULL COMMENT '最晚日期';
ALTER TABLE basic_data_type CHANGE latest_time latest_time datetime NULL COMMENT '最晚日期' AFTER earliest_time;
ALTER TABLE basic_data_type ADD true_label varchar(64) NULL COMMENT 'true 对应文案';
ALTER TABLE basic_data_type CHANGE true_label true_label varchar(64) NULL COMMENT 'true 对应文案' AFTER latest_time;
ALTER TABLE basic_data_type ADD false_label varchar(64) NULL COMMENT 'false 对应文案';
ALTER TABLE basic_data_type CHANGE false_label false_label varchar(64) NULL COMMENT 'false 对应文案' AFTER true_label;
ALTER TABLE basic_data_type ADD all_places int NULL COMMENT '总位数';
ALTER TABLE basic_data_type CHANGE all_places all_places int NULL COMMENT '总位数' AFTER false_label;
ALTER TABLE basic_data_type ADD validation_type varchar(128) NULL COMMENT '格式验证';
ALTER TABLE basic_data_type CHANGE validation_type validation_type varchar(128) NULL COMMENT '格式验证' AFTER all_places;
ALTER TABLE basic_data_type ADD validation_pattern varchar(256) NULL COMMENT '正则表达式';
ALTER TABLE basic_data_type CHANGE validation_pattern validation_pattern varchar(256) NULL COMMENT '正则表达式' AFTER validation_type;


ALTER TABLE `app_unstruct_task` ADD COLUMN `last_execute_time` datetime NULL COMMENT '最后执行时间' AFTER `status`;
ALTER TABLE `app_unstruct_task` ADD COLUMN `execute_duration` int NULL COMMENT '执行时长' AFTER `last_execute_time`;
ALTER TABLE app_unstruct_task ADD responsible_user_id BIGINT NULL COMMENT '负责人id';
ALTER TABLE app_unstruct_task CHANGE responsible_user_id responsible_user_id BIGINT NULL COMMENT '负责人id' AFTER publisher_id;
ALTER TABLE app_unstruct_task ADD responsible_person_name varchar(128) NULL COMMENT '负责人姓名';
ALTER TABLE app_unstruct_task CHANGE responsible_person_name responsible_person_name varchar(128) NULL COMMENT '负责人姓名' AFTER responsible_user_id;


ALTER TABLE app_struct_task ADD last_execute_time DATETIME NULL COMMENT '最后执行时间';
ALTER TABLE app_struct_task CHANGE last_execute_time last_execute_time DATETIME NULL COMMENT '最后执行时间' AFTER datasource_id;
ALTER TABLE app_struct_task ADD execute_duration int NULL COMMENT '执行时长';
ALTER TABLE app_struct_task CHANGE execute_duration execute_duration int NULL COMMENT '执行时长' AFTER last_execute_time;
ALTER TABLE app_struct_task ADD scheduling_status tinyint NULL COMMENT '调度状态';
ALTER TABLE app_struct_task CHANGE scheduling_status scheduling_status tinyint NULL COMMENT '调度状态' AFTER execute_duration;
ALTER TABLE app_struct_task ADD responsible_user_id bigint NULL COMMENT '负责人id';
ALTER TABLE app_struct_task CHANGE responsible_user_id responsible_user_id bigint NULL COMMENT '负责人id' AFTER description;
ALTER TABLE app_struct_task ADD responsible_person_name varchar(128) NULL COMMENT '负责人姓名';
ALTER TABLE app_struct_task CHANGE responsible_person_name responsible_person_name varchar(128) NULL COMMENT '负责人姓名' AFTER responsible_user_id;

ALTER TABLE `om_term` ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `description`;

ALTER TABLE `om_data_type` ADD COLUMN `max_length` INT NULL COMMENT '最大长度' AFTER `enum_type`;
ALTER TABLE `om_data_type` ADD COLUMN `min_length` INT NULL COMMENT '最小长度' AFTER `max_length`;
ALTER TABLE `om_data_type` ADD COLUMN `earliest_time` DATETIME NULL COMMENT '最早日期' AFTER `min_length`;
ALTER TABLE `om_data_type` ADD COLUMN `latest_time` DATETIME NULL COMMENT '最晚日期' AFTER `earliest_time`;
ALTER TABLE `om_data_type` ADD COLUMN `true_value` VARCHAR(64) NULL COMMENT 'true 对应文案' AFTER `latest_time`;
ALTER TABLE `om_data_type` ADD COLUMN `false_value` VARCHAR(64) NULL COMMENT 'false 对应文案' AFTER `true_value`;
ALTER TABLE `om_data_type` ADD COLUMN `all_places` INT NULL COMMENT '总位数' AFTER `false_value`;

ALTER TABLE `om_data_type` ADD COLUMN `validation_type` VARCHAR(16) NULL COMMENT '文本格式校验编码' AFTER `all_places`;
ALTER TABLE `om_data_type` ADD COLUMN `validation_pattern` VARCHAR(512) NULL COMMENT '自定义正则表达式' AFTER `validation_type`;
ALTER TABLE `om_data_type` ADD COLUMN `example_value` VARCHAR(256) NULL COMMENT '示例值' AFTER `validation_pattern`;
ALTER TABLE `om_data_type` ADD COLUMN `min_value` DECIMAL(65, 20) NULL COMMENT '数值最小值' AFTER `example_value`;
ALTER TABLE `om_data_type` ADD COLUMN `max_value` DECIMAL(65, 20) NULL COMMENT '数值最大值' AFTER `min_value`;



ALTER TABLE `om_enum_type` ADD COLUMN `description` varchar(512) DEFAULT NULL COMMENT '描述' AFTER `enum_type`;
ALTER TABLE `om_enum_data` ADD COLUMN `description` varchar(512) DEFAULT NULL COMMENT '描述' AFTER `enum_type`;

ALTER TABLE `om_target_task` ADD COLUMN `task_name` VARCHAR(256) NULL COMMENT '任务名称' AFTER `target_id`;
ALTER TABLE `basic_project_version` ADD COLUMN `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态;0：禁用，1：启用' AFTER `base_version`;
ALTER TABLE `om_data_type` ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `enum_source`;
ALTER TABLE `basic_data_type` ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `enum_type`;
ALTER TABLE `basic_data_type` ADD COLUMN `example_value` VARCHAR(256) NULL COMMENT '示例值' AFTER `validation_pattern`;
ALTER TABLE `basic_data_type` ADD COLUMN `min_value` DECIMAL(65, 20) NULL COMMENT '数值最小值' AFTER `example_value`;
ALTER TABLE `basic_data_type` ADD COLUMN `max_value` DECIMAL(65, 20) NULL COMMENT '数值最大值' AFTER `min_value`;
ALTER TABLE `basic_enum_type` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_type`;
ALTER TABLE `basic_enum_data` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_type`;

