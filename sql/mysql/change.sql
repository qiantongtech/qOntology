UPDATE `qontology_dev`.`system_menu` SET `menu_name` = '文件管理', `parent_id` = 1080, `order_num` = 4, `path` = 'file', `component` = NULL, `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'M', `visible` = '0', `status` = '0', `perms` = '', `icon` = '文件管理', `create_by` = '吴同', `create_time` = '2026-11-02 14:18:12', `update_by` = '吴同', `update_time` = '2026-11-03 15:58:22', `remark` = '文件分类与文件管理统一入口' WHERE `menu_id` = 1195;

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
ALTER TABLE qontology_dev.basic_data_type ADD validation_type varchar(128) NULL COMMENT '格式验证';
ALTER TABLE qontology_dev.basic_data_type CHANGE validation_type validation_type varchar(128) NULL COMMENT '格式验证' AFTER all_places;
ALTER TABLE qontology_dev.basic_data_type ADD validation_pattern varchar(256) NULL COMMENT '正则表达式';
ALTER TABLE qontology_dev.basic_data_type CHANGE validation_pattern validation_pattern varchar(256) NULL COMMENT '正则表达式' AFTER format_valid;


ALTER TABLE `qontology_dev`.`app_unstruct_task` ADD COLUMN `last_execute_time` datetime NULL COMMENT '最后执行时间' AFTER `status`;
ALTER TABLE `qontology_dev`.`app_unstruct_task` ADD COLUMN `execute_duration` int NULL COMMENT '执行时长' AFTER `last_execute_time`;
ALTER TABLE qontology_dev.app_unstruct_task ADD responsible_user_id BIGINT NULL COMMENT '负责人id';
ALTER TABLE qontology_dev.app_unstruct_task CHANGE responsible_user_id responsible_user_id BIGINT NULL COMMENT '负责人id' AFTER publisher_phone;
ALTER TABLE qontology_dev.app_unstruct_task ADD responsible_person_name varchar(128) NULL COMMENT '负责人姓名';
ALTER TABLE qontology_dev.app_unstruct_task CHANGE responsible_person_name responsible_person_name varchar(128) NULL COMMENT '负责人姓名' AFTER responsible_user_id;


ALTER TABLE qontology_dev.app_struct_task ADD last_execute_time DATETIME NULL COMMENT '最后执行时间';
ALTER TABLE qontology_dev.app_struct_task CHANGE last_execute_time last_execute_time DATETIME NULL COMMENT '最后执行时间' AFTER datasource_id;
ALTER TABLE qontology_dev.app_struct_task ADD execute_duration int NULL COMMENT '执行时长';
ALTER TABLE qontology_dev.app_struct_task CHANGE execute_duration execute_duration int NULL COMMENT '执行时长' AFTER last_execute_time;
ALTER TABLE qontology_dev.app_struct_task ADD scheduling_status tinyint NULL COMMENT '调度状态';
ALTER TABLE qontology_dev.app_struct_task CHANGE scheduling_status scheduling_status tinyint NULL COMMENT '调度状态' AFTER execute_duration;
ALTER TABLE qontology_dev.app_struct_task ADD responsible_user_id bigint NULL COMMENT '负责人id';
ALTER TABLE qontology_dev.app_struct_task CHANGE responsible_user_id responsible_user_id bigint NULL COMMENT '负责人id' AFTER description;
ALTER TABLE qontology_dev.app_struct_task ADD responsible_person_name varchar(128) NULL COMMENT '负责人姓名';
ALTER TABLE qontology_dev.app_struct_task CHANGE responsible_person_name responsible_person_name varchar(128) NULL COMMENT '负责人姓名' AFTER responsible_user_id;
