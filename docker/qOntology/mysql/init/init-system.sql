/*
 Navicat Premium Dump SQL

 Source Server         : 本地数据库
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 10/11/2026 17:41:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
                              `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                              `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表名称',
                              `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '表描述',
                              `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
                              `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
                              `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '实体类名称',
                              `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
                              `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
                              `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成包路径',
                              `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成模块名',
                              `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成业务名',
                              `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能名',
                              `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '生成功能作者',
                              `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
                              `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
                              `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '其它生成选项',
                              `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                              `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                              `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                              `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                              `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
                                     `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
                                     `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
                                     `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列名称',
                                     `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列描述',
                                     `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '列类型',
                                     `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
                                     `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
                                     `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
                                     `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
                                     `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
                                     `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
                                     `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
                                     `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
                                     `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
                                     `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
                                     `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
                                     `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
                                     `sort` int NULL DEFAULT NULL COMMENT '排序',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                     PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
                            `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `sender_id` bigint NULL DEFAULT NULL COMMENT '发送人',
                            `receiver_id` bigint NULL DEFAULT NULL COMMENT '接收人',
                            `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
                            `content` varchar(3072) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息模板内容',
                            `category` tinyint UNSIGNED NOT NULL COMMENT '消息类别',
                            `msg_level` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息等级',
                            `module` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息模块',
                            `entity_type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '实体类型',
                            `entity_id` bigint NULL DEFAULT NULL COMMENT '实体id',
                            `entity_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息链接',
                            `has_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
                            `has_retraction` tinyint(1) NULL DEFAULT 0 COMMENT '是否撤回',
                            `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                            `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                            `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                            `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
                            `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------


-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                       `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
                                       `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                       `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
                                   `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                   `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '日历名称',
                                   `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
                                   PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '日历信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
                                       `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                       `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
                                       `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                       `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'cron表达式',
                                       `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时区',
                                       PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                       CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
                                        `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                        `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例id',
                                        `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
                                        `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                        `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度器实例名',
                                        `fired_time` bigint NOT NULL COMMENT '触发的时间',
                                        `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
                                        `priority` int NOT NULL COMMENT '优先级',
                                        `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态',
                                        `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务名称',
                                        `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务组名',
                                        `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否并发',
                                        `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
                                        PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '已触发的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
                                     `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                     `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                                     `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
                                     `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
                                     `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行任务类名称',
                                     `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否持久化',
                                     `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否并发',
                                     `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否更新数据',
                                     `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '是否接受恢复执行',
                                     `job_data` blob NULL COMMENT '存放持久化job对象',
                                     PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务详细信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
                               `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                               `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '悲观锁名称',
                               PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
                                             `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                             `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                             PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '暂停的触发器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                         `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例名称',
                                         `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
                                         `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
                                         PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '调度器状态表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
                                         `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                         `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
                                         `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                         `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
                                         `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
                                         `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
                                         PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                         CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
                                          `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                          `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
                                          `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
                                          `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
                                          `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
                                          `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
                                          `int_prop_1` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
                                          `int_prop_2` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
                                          `long_prop_1` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
                                          `long_prop_2` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
                                          `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
                                          `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
                                          `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
                                          `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
                                          PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                          CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
                                  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调度名称',
                                  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的名字',
                                  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器所属组的名字',
                                  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
                                  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
                                  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '相关介绍',
                                  `next_fire_time` bigint NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
                                  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
                                  `priority` int NULL DEFAULT NULL COMMENT '优先级',
                                  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
                                  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
                                  `start_time` bigint NOT NULL COMMENT '开始时间',
                                  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
                                  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
                                  `misfire_instr` smallint NULL DEFAULT NULL COMMENT '补偿执行的策略',
                                  `job_data` blob NULL COMMENT '存放持久化job对象',
                                  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE,
                                  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '触发器详细信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
                                  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
                                  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数名称',
                                  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键名',
                                  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '参数键值',
                                  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', '吴同', '2026-04-20 09:32:54', NULL, NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `system_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', '吴同', '2026-04-20 09:32:54', NULL, NULL, '初始化密码 123456');
INSERT INTO `system_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', '吴同', '2026-04-20 09:32:54', NULL, NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `system_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', '吴同', '2026-04-20 09:32:54', '吴同', '2026-09-29 17:46:40', '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `system_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', '吴同', '2026-04-20 09:32:54', NULL, NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `system_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', '吴同', '2026-04-20 09:32:54', NULL, NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');

-- ----------------------------
-- Table structure for system_content
-- ----------------------------
DROP TABLE IF EXISTS `system_content`;
CREATE TABLE `system_content`  (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `sys_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统名称',
                                   `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统logo',
                                   `login_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录页面logo',
                                   `carousel_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '轮播图',
                                   `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                                   `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
                                   `copyright` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版权方',
                                   `record_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备案号',
                                   `del_flag` int NULL DEFAULT NULL COMMENT '删除标记',
                                   `status` int NULL DEFAULT NULL COMMENT '状态',
                                   `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `creator_id` int NULL DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `updater_id` int NULL DEFAULT NULL COMMENT '修改人id',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_content
-- ----------------------------
INSERT INTO `system_content` VALUES (1, NULL, '', '', '', '400-660-8208', 'support@qiantong.tech', 'Copyright© 2026 江苏千桐科技有限公司 版权所有', '苏ICP备2022008519号-3', 0, NULL, NULL, NULL, NULL, '吴同', 2, '2025-01-13 13:18:06', NULL);

-- ----------------------------
-- Table structure for system_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_dept`;
CREATE TABLE `system_dept`  (
                                `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
                                `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
                                `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '祖级列表',
                                `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
                                `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
                                `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '负责人',
                                `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                                `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 117 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_dept
-- ----------------------------
INSERT INTO `system_dept` VALUES (100, 0, '0', '江苏千桐科技有限公司', 0, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (101, 100, '0,100', '南京总公司', 1, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (102, 100, '0,100', '郑州分公司', 2, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (103, 100, '0,100', '合作伙伴', 3, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (104, 101, '0,100,101', '研发部门', 1, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (105, 101, '0,100,101', '市场部门', 2, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (106, 101, '0,100,101', '测试部门', 3, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (107, 101, '0,100,101', '财务部门', 4, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (108, 101, '0,100,101', '运维部门', 5, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (109, 102, '0,100,102', '研发部门', 1, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (110, 102, '0,100,102', '市场部门', 2, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (111, 102, '0,100,102', '测试部门', 3, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (112, 102, '0,100,102', '财务部门', 4, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (113, 102, '0,100,102', '运维部门', 5, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (114, 103, '0,100,103', '研发部门', 1, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (115, 103, '0,100,103', '市场部门', 2, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);
INSERT INTO `system_dept` VALUES (116, 103, '0,100,103', '测试部门', 3, NULL, NULL, NULL, '0', '0', '吴同', '2026-10-22 20:19:08', '', NULL);

-- ----------------------------
-- Table structure for system_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_data`;
CREATE TABLE `system_dict_data`  (
                                     `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
                                     `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
                                     `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典标签',
                                     `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典键值',
                                     `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
                                     `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
                                     `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '表格回显样式',
                                     `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
                                     `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 132 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_dict_data
-- ----------------------------
INSERT INTO `system_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '性别男');
INSERT INTO `system_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '性别女');
INSERT INTO `system_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '性别未知');
INSERT INTO `system_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '显示菜单');
INSERT INTO `system_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '隐藏菜单');
INSERT INTO `system_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '正常状态');
INSERT INTO `system_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '停用状态');
INSERT INTO `system_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '正常状态');
INSERT INTO `system_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '停用状态');
INSERT INTO `system_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '默认分组');
INSERT INTO `system_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '系统分组');
INSERT INTO `system_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '系统默认是');
INSERT INTO `system_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '系统默认否');
INSERT INTO `system_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '通知');
INSERT INTO `system_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '公告');
INSERT INTO `system_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '正常状态');
INSERT INTO `system_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '关闭状态');
INSERT INTO `system_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '其他操作');
INSERT INTO `system_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '新增操作');
INSERT INTO `system_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '修改操作');
INSERT INTO `system_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '删除操作');
INSERT INTO `system_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '授权操作');
INSERT INTO `system_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '导出操作');
INSERT INTO `system_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '导入操作');
INSERT INTO `system_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '强退操作');
INSERT INTO `system_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '生成操作');
INSERT INTO `system_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '清空操作');
INSERT INTO `system_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '正常状态');
INSERT INTO `system_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '停用状态');
INSERT INTO `system_dict_data` VALUES (30, 1, '停用', '0', 'ai_config_status', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 19:56:05', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (31, 2, '启用', '1', 'ai_config_status', NULL, 'success', 'Y', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (32, 1, '对话', 'CHAT', 'ai_model_capability', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (33, 2, '流式对话', 'CHAT_STREAM', 'ai_model_capability', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (34, 3, '向量化', 'EMBEDDING', 'ai_model_capability', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (35, 4, '重排', 'RERANK', 'ai_model_capability', NULL, 'success', 'N', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (36, 1, '未配置', '0', 'ai_apikey_status', NULL, 'info', 'Y', '0', '吴同', '2026-10-22 19:56:06', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (37, 2, '已配置', '1', 'ai_apikey_status', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (38, 3, '已同步', '2', 'ai_apikey_status', NULL, 'success', 'N', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (39, 1, '通义千问', 'TongYi', 'ai_model_platform', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (40, 2, 'DeepSeek', 'DeepSeek', 'ai_model_platform', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (41, 3, 'OpenAI', 'OpenAI', 'ai_model_platform', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (42, 4, 'Ollama', 'Ollama', 'ai_model_platform', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (43, 1, '对话', '1', 'ai_model_tag', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 19:56:07', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (44, 2, '图片', '2', 'ai_model_tag', NULL, 'success', 'N', '0', '吴同', '2026-10-22 19:56:08', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (45, 3, '语音', '3', 'ai_model_tag', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 19:56:08', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (46, 4, '视频', '4', 'ai_model_tag', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 19:56:08', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (47, 5, '向量', '5', 'ai_model_tag', NULL, 'info', 'N', '0', '吴同', '2026-10-22 19:56:08', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (48, 6, '重排序', '6', 'ai_model_tag', NULL, 'success', 'N', '0', '吴同', '2026-10-22 19:56:08', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (49, 1, '本地部署', '1', 'ai_deploy_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 19:56:09', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (50, 2, '开放平台', '2', 'ai_deploy_type', NULL, 'success', 'Y', '0', '吴同', '2026-10-22 19:56:09', NULL, NULL, '');
INSERT INTO `system_dict_data` VALUES (51, 1, '基础数据类型', '1', 'om_attribute_data_type', '', 'success', 'Y', '0', 'admin', '2026-10-16 09:48:41', '吴同', '2026-11-10 10:31:47', '');
INSERT INTO `system_dict_data` VALUES (52, 2, '业务数据类型', '2', 'om_attribute_data_type', '', '', 'N', '0', 'admin', '2026-10-16 09:48:41', '超级管理员', '2026-10-16 09:49:08', '');
INSERT INTO `system_dict_data` VALUES (53, 0, '文本', '0', 'basic_data_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-16 15:03:01', '超级管理员', '2026-10-16 15:04:24', NULL);
INSERT INTO `system_dict_data` VALUES (54, 1, '整数', '1', 'basic_data_type', NULL, 'success', 'N', '0', '吴同', '2026-10-16 15:03:18', '超级管理员', '2026-10-16 15:04:28', NULL);
INSERT INTO `system_dict_data` VALUES (55, 2, '小数', '2', 'basic_data_type', NULL, 'info', 'N', '0', '吴同', '2026-10-16 15:03:44', '超级管理员', '2026-10-16 15:04:33', NULL);
INSERT INTO `system_dict_data` VALUES (56, 3, '布尔', '3', 'basic_data_type', NULL, 'warning', 'N', '0', '吴同', '2026-10-16 15:03:58', '超级管理员', '2026-10-16 15:04:40', NULL);
INSERT INTO `system_dict_data` VALUES (57, 4, '日期', '4', 'basic_data_type', NULL, 'danger', 'N', '0', '吴同', '2026-10-16 15:04:12', '超级管理员', '2026-10-16 15:04:44', NULL);
INSERT INTO `system_dict_data` VALUES (58, 0, '停用', '0', 'project_status', NULL, 'danger', 'N', '0', '吴同', '2026-10-12 15:11:21', '吴同', '2026-11-10 14:54:15', NULL);
INSERT INTO `system_dict_data` VALUES (59, 1, '启用', '1', 'project_status', NULL, 'success', 'N', '0', '超级管理员', '2026-10-12 15:11:34', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (60, 0, '待验证', '0', 'om_accept_status', NULL, 'primary', 'N', '0', '超级管理员', '2026-10-13 17:59:57', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (61, 0, '已覆盖', '1', 'om_accept_status', NULL, 'success', 'N', '0', '超级管理员', '2026-10-13 18:00:11', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (62, 0, '未覆盖', '2', 'om_accept_status', NULL, 'danger', 'N', '0', '超级管理员', '2026-10-13 18:00:26', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (63, 3, '低', '1', 'om_target_priority', NULL, 'warning', 'N', '0', '超级管理员', '2026-10-13 18:08:04', '超级管理员', '2026-10-22 16:43:48', NULL);
INSERT INTO `system_dict_data` VALUES (64, 2, '中', '2', 'om_target_priority', NULL, 'primary', 'N', '0', '超级管理员', '2026-10-13 18:08:24', '超级管理员', '2026-10-13 18:08:33', NULL);
INSERT INTO `system_dict_data` VALUES (65, 1, '高', '3', 'om_target_priority', NULL, 'success', 'N', '0', '超级管理员', '2026-10-13 18:08:46', '超级管理员', '2026-10-22 16:44:15', NULL);
INSERT INTO `system_dict_data` VALUES (66, 1, 'MySQL', 'MySql', 'datasource_type', NULL, 'primary', 'N', '0', 'admin', '2025-03-12 14:04:59', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (67, 2, 'DM8', 'DM8', 'datasource_type', NULL, 'primary', 'N', '0', 'admin', '2025-03-12 14:05:11', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (68, 3, 'Oracle', 'Oracle', 'datasource_type', NULL, 'primary', 'N', '0', 'admin', '2025-03-12 14:05:25', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (69, 4, 'Oracle11', 'Oracle11', 'datasource_type', NULL, 'primary', 'N', '0', 'admin', '2025-03-12 14:05:39', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (70, 5, 'Kingbase8', 'Kingbase8', 'datasource_type', NULL, 'primary', 'N', '0', 'admin', '2025-03-12 14:05:52', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (71, 5, '枚举', '5', 'basic_data_type', NULL, 'info', 'N', '0', '超级管理员', '2026-10-21 10:16:15', '超级管理员', '2026-10-21 10:16:27', NULL);
INSERT INTO `system_dict_data` VALUES (72, 1, '子类型', 'SUBCLASS', 'om_axiom_type', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 09:52:30', '吴同', '2026-10-28 13:57:57', '');
INSERT INTO `system_dict_data` VALUES (73, 2, '等价', 'EQUIVALENT', 'om_axiom_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:30', '吴同', '2026-10-28 13:58:31', '');
INSERT INTO `system_dict_data` VALUES (74, 3, '互斥', 'DISJOINT', 'om_axiom_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:30', '吴同', '2026-10-28 13:58:35', '');
INSERT INTO `system_dict_data` VALUES (75, 4, 'Domain / Range', 'DOMAIN_RANGE', 'om_axiom_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:30', '吴同', '2026-10-28 13:58:39', '');
INSERT INTO `system_dict_data` VALUES (76, 5, '属性范围', 'PROPERTY_RANGE', 'om_axiom_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:31', '吴同', '2026-10-28 13:58:42', '');
INSERT INTO `system_dict_data` VALUES (77, 1, '必填', 'REQUIRED', 'om_data_rule_type', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 09:52:31', '吴同', '2026-10-28 13:54:11', '');
INSERT INTO `system_dict_data` VALUES (78, 2, '枚举', 'ENUM', 'om_data_rule_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:31', '吴同', '2026-10-28 13:54:16', '');
INSERT INTO `system_dict_data` VALUES (79, 3, '取值范围', 'RANGE', 'om_data_rule_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:31', '吴同', '2026-10-28 13:54:20', '');
INSERT INTO `system_dict_data` VALUES (80, 4, '格式校验', 'FORMAT', 'om_data_rule_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 09:52:31', '吴同', '2026-10-28 13:54:23', '');
INSERT INTO `system_dict_data` VALUES (81, 1, '提示', 'INFO', 'om_rule_level', NULL, 'info', 'Y', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (82, 2, '警告', 'WARNING', 'om_rule_level', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (83, 3, '错误', 'ERROR', 'om_rule_level', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (84, 1, '大于', 'GT', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (85, 2, '大于等于', 'GE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (86, 3, '小于', 'LT', 'om_rule_operator', NULL, 'default', 'Y', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (87, 4, '小于等于', 'LE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (88, 5, '等于', 'EQ', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (89, 6, '不等于', 'NE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (90, 0, '未执行', '0', 'app_task_status', NULL, 'primary', 'Y', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (91, 1, '执行中', '1', 'app_task_status', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (92, 2, '已执行', '2', 'app_task_status', NULL, 'success', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (93, 3, '执行失败', '3', 'app_task_status', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (94, 4, '队列中', '4', 'app_task_status', NULL, 'info', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (95, 0, '失败', '0', 'app_log_status', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (96, 1, '成功', '1', 'app_log_status', NULL, 'success', 'N', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (97, 0, '结构化', '0', 'app_task_log_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (98, 1, '非结构化', '1', 'app_task_log_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (99, 0, '未映射', '0', 'app_mapping_status', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (100, 1, '已映射', '1', 'app_mapping_status', NULL, 'success', 'N', '0', '吴同', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (101, 0, '全量更新', '0', 'app_update_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-22 20:06:17', '吴同', '2026-11-06 12:47:58', '');
INSERT INTO `system_dict_data` VALUES (102, 1, '增量更新', '1', 'app_update_type', NULL, 'info', 'N', '0', '吴同', '2026-10-22 20:06:17', '吴同', '2026-11-06 12:48:02', '');
INSERT INTO `system_dict_data` VALUES (103, 0, '未发布', '0', 'publish_status', '', 'warning', 'N', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '未发布状态');
INSERT INTO `system_dict_data` VALUES (104, 1, '已发布', '1', 'publish_status', '', 'primary', 'N', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '已发布状态');
INSERT INTO `system_dict_data` VALUES (105, 1, '待执行', '0', 'om_ai_task_status', '', 'info', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (106, 2, '解析中', '1', 'om_ai_task_status', '', 'warning', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (107, 3, '解析成功', '2', 'om_ai_task_status', '', 'success', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (108, 4, '解析失败', '3', 'om_ai_task_status', '', 'danger', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (109, 1, '待采纳', '0', 'om_ai_adopt_status', '', 'warning', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (110, 2, '已采纳', '1', 'om_ai_adopt_status', '', 'success', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (111, 3, '采纳失败', '2', 'om_ai_adopt_status', '', 'danger', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (112, 1, '等待', '0', 'om_ai_stage_status', '', 'info', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (113, 2, '执行中', '1', 'om_ai_stage_status', '', 'warning', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (114, 3, '成功', '2', 'om_ai_stage_status', '', 'success', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (115, 4, '失败', '3', 'om_ai_stage_status', '', 'danger', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (116, 1, '待解析', '0', 'om_ai_file_parse_status', '', 'info', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (117, 2, '解析中', '1', 'om_ai_file_parse_status', '', 'warning', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (118, 3, '解析成功', '2', 'om_ai_file_parse_status', '', 'success', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (119, 4, '解析失败', '3', 'om_ai_file_parse_status', '', 'danger', 'N', '0', '吴同', '2026-10-30 11:48:21', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (120, 1, '待改进', '0', 'qa_health_status', '', 'warning', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (121, 2, '良好', '1', 'qa_health_status', '', 'success', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (122, 3, '优秀', '2', 'qa_health_status', '', 'primary', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (123, 1, '离线', '0', 'qa_sync_status', '', 'info', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (124, 2, '实时', '1', 'qa_sync_status', '', 'success', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (125, 3, '延迟', '2', 'qa_sync_status', '', 'warning', 'N', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (126, 0, '关系事件', '0', 'app_event_type', NULL, NULL, 'N', '0', '吴同', '2026-05-12 09:38:36', '', NULL, '事件类型');
INSERT INTO `system_dict_data` VALUES (127, 1, '实体事件', '1', 'app_event_type', NULL, NULL, 'N', '0', '吴同', '2026-05-12 09:38:37', '', NULL, '事件类型');
INSERT INTO `system_dict_data` VALUES (128, 0, '结构化抽取', '1', 'app_enetity_type', NULL, 'primary', 'N', '0', '吴同', '2026-05-12 09:38:41', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (129, 1, '非结构化抽取', '2', 'app_enetity_type', NULL, 'primary', 'N', '0', '吴同', '2026-05-12 09:38:41', '', NULL, NULL);
INSERT INTO `system_dict_data` VALUES (130, 0, '功能标签', '0', 'app_tag_category', NULL, '', 'N', '0', '吴同', '2026-05-12 09:38:36', '', NULL, '标签类型');
INSERT INTO `system_dict_data` VALUES (131, 1, '业务标签', '1', 'app_tag_category', NULL, '', 'N', '0', '吴同', '2026-05-12 09:38:36', '', NULL, '标签类型');

-- ----------------------------
-- Table structure for system_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_type`;
CREATE TABLE `system_dict_type`  (
                                     `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
                                     `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
                                     `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
                                     `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`dict_id`) USING BTREE,
                                     UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_dict_type
-- ----------------------------
INSERT INTO `system_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '用户性别列表');
INSERT INTO `system_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '菜单状态列表');
INSERT INTO `system_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '系统开关列表');
INSERT INTO `system_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '任务状态列表');
INSERT INTO `system_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '任务分组列表');
INSERT INTO `system_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '系统是否列表');
INSERT INTO `system_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '通知类型列表');
INSERT INTO `system_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '通知状态列表');
INSERT INTO `system_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '操作类型列表');
INSERT INTO `system_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', '吴同', '2026-04-20 09:32:54', '', NULL, '登录状态列表');
INSERT INTO `system_dict_type` VALUES (11, 'AI 配置状态', 'ai_config_status', '0', '吴同', '2026-10-22 19:56:03', NULL, NULL, 'AI 密钥和模型启停状态');
INSERT INTO `system_dict_type` VALUES (12, 'AI 模型能力', 'ai_model_capability', '0', '吴同', '2026-10-22 19:56:04', NULL, NULL, 'AI 模型支持的能力');
INSERT INTO `system_dict_type` VALUES (13, 'API 密钥状态', 'ai_apikey_status', '0', '吴同', '2026-10-22 19:56:04', NULL, NULL, '模型市场 API 密钥状态');
INSERT INTO `system_dict_type` VALUES (14, 'AI 模型平台', 'ai_model_platform', '0', '吴同', '2026-10-22 19:56:05', NULL, NULL, '模型市场平台');
INSERT INTO `system_dict_type` VALUES (15, 'AI 模型标签', 'ai_model_tag', '0', '吴同', '2026-10-22 19:56:05', NULL, NULL, '模型类型标签');
INSERT INTO `system_dict_type` VALUES (16, 'AI 部署方式', 'ai_deploy_type', '0', '吴同', '2026-10-22 19:56:05', NULL, NULL, '模型部署方式');
INSERT INTO `system_dict_type` VALUES (17, '属性数据类型分类', 'om_attribute_data_type', '0', 'admin', '2026-10-16 09:48:41', '超级管理员', '2026-10-16 09:49:00', '');
INSERT INTO `system_dict_type` VALUES (18, '数据类型', 'basic_data_type', '0', '吴同', '2026-10-16 15:02:14', '', NULL, NULL);
INSERT INTO `system_dict_type` VALUES (19, '数据源类型', 'datasource_type', '0', '吴同', '2025-03-12 14:04:38', '', NULL, NULL);
INSERT INTO `system_dict_type` VALUES (20, '目标验证状态', 'om_accept_status', '0', '吴同', '2026-10-13 17:58:54', '', NULL, NULL);
INSERT INTO `system_dict_type` VALUES (21, '目标优先级', 'om_target_priority', '0', '吴同', '2026-10-13 18:07:31', '', NULL, NULL);
INSERT INTO `system_dict_type` VALUES (22, '项目状态', 'project_status', '0', '吴同', '2026-10-12 15:10:48', '', NULL, NULL);
INSERT INTO `system_dict_type` VALUES (23, '公理类型', 'om_axiom_type', '0', '吴同', '2026-10-22 09:51:26', '', NULL, '公理类型');
INSERT INTO `system_dict_type` VALUES (24, '数据规则类型', 'om_data_rule_type', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '数据规则类型');
INSERT INTO `system_dict_type` VALUES (25, '规则级别', 'om_rule_level', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '规则级别');
INSERT INTO `system_dict_type` VALUES (26, '规则比较符', 'om_rule_operator', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '规则比较符');
INSERT INTO `system_dict_type` VALUES (27, '任务执行状态', 'app_task_status', '0', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (28, '知识抽取日志状态', 'app_log_status', '0', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (29, '抽取日志类型', 'app_task_log_type', '0', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (30, '表映射状态', 'app_mapping_status', '0', '吴同', '2026-10-22 20:06:15', '', NULL, '结构化抽取表映射状态');
INSERT INTO `system_dict_type` VALUES (31, '结构化抽取更新类型', 'app_update_type', '0', '吴同', '2026-10-22 20:06:16', '', NULL, '结构化抽取全量或增量更新');
INSERT INTO `system_dict_type` VALUES (32, '发布状态', 'publish_status', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '发布状态列表');
INSERT INTO `system_dict_type` VALUES (33, 'AI建模任务状态', 'om_ai_task_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI辅助建模任务状态');
INSERT INTO `system_dict_type` VALUES (34, 'AI候选采纳状态', 'om_ai_adopt_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI辅助建模候选采纳状态');
INSERT INTO `system_dict_type` VALUES (35, 'AI解析阶段状态', 'om_ai_stage_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI解析阶段状态');
INSERT INTO `system_dict_type` VALUES (36, 'AI文件解析状态', 'om_ai_file_parse_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI上传文件解析状态');
INSERT INTO `system_dict_type` VALUES (37, '知识健康度', 'qa_health_status', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '智能问答知识健康度状态');
INSERT INTO `system_dict_type` VALUES (38, '同步状态', 'qa_sync_status', '0', '吴同', '2026-11-01 10:00:00', '', NULL, '智能问答本体同步状态');
INSERT INTO `system_dict_type` VALUES (39, '事件类型', 'app_event_type', '0', '吴同', '2026-05-12 09:37:25', '', NULL, '知识管理事件分类');
INSERT INTO `system_dict_type` VALUES (40, '实体类型', 'app_enetity_type', '0', '吴同', '2026-05-12 09:37:26', '', NULL, '实体类型');
INSERT INTO `system_dict_type` VALUES (41, '标签类型', 'app_tag_category', '0', '吴同', '2026-05-12 09:37:26', '', NULL, '知识管理标签分类');

-- ----------------------------
-- Table structure for system_job
-- ----------------------------
DROP TABLE IF EXISTS `system_job`;
CREATE TABLE `system_job`  (
                               `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
                               `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务名称',
                               `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
                               `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
                               `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
                               `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
                               `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
                               `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
                               `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                               `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                               `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                               `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                               `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注信息',
                               PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_job
-- ----------------------------
INSERT INTO `system_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', '吴同', '2026-04-20 09:32:54', '', NULL, '');

-- ----------------------------
-- Table structure for system_job_log
-- ----------------------------
DROP TABLE IF EXISTS `system_job_log`;
CREATE TABLE `system_job_log`  (
                                   `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
                                   `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                                   `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务组名',
                                   `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '调用目标字符串',
                                   `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志信息',
                                   `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
                                   `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '异常信息',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for system_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `system_logininfor`;
CREATE TABLE `system_logininfor`  (
                                      `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
                                      `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户账号',
                                      `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP地址',
                                      `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录地点',
                                      `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '浏览器类型',
                                      `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作系统',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
                                      `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提示消息',
                                      `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
                                      PRIMARY KEY (`info_id`) USING BTREE,
                                      INDEX `idx_system_logininfor_s`(`status` ASC) USING BTREE,
                                      INDEX `idx_system_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_logininfor
-- ----------------------------
INSERT INTO `system_logininfor` VALUES (1, 'admin', '117.89.24.233', 'XX XX', 'Chrome 15', 'Mac OS X', '0', '登录成功', '2026-10-22 20:22:20');
INSERT INTO `system_logininfor` VALUES (2, 'qOntology', '192.168.0.97', '内网IP', 'Mobile Safari', 'Mac OS X (iPhone)', '0', '登录成功', '2026-11-02 17:01:42');
INSERT INTO `system_logininfor` VALUES (3, 'qOntology', '192.168.0.187', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-11-02 17:28:01');
INSERT INTO `system_logininfor` VALUES (4, 'admin', '117.89.24.233', 'XX XX', 'Chrome 15', 'Windows 10', '0', '退出成功', '2026-11-02 18:37:16');
INSERT INTO `system_logininfor` VALUES (5, 'qOntology', '49.74.228.64', 'XX XX', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-11-02 18:37:19');
INSERT INTO `system_logininfor` VALUES (6, 'qOntology', '192.168.0.72', '内网IP', 'Chrome 15', 'Windows 10', '1', '验证码错误', '2026-11-06 18:30:25');
INSERT INTO `system_logininfor` VALUES (7, 'qOntology', '192.168.0.72', '内网IP', 'Chrome 15', 'Windows 10', '0', '登录成功', '2026-11-06 18:30:29');

-- ----------------------------
-- Table structure for system_menu
-- ----------------------------
DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`  (
                                `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                                `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                                `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
                                `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
                                `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
                                `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
                                `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
                                `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
                                `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
                                `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
                                `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
                                `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
                                `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
                                `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '#' COMMENT '菜单图标',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '备注',
                                PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1223 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_menu
-- ----------------------------
INSERT INTO `system_menu` VALUES (1, '系统管理', 0, 6, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', '系统中心', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-14 10:50:01', '系统管理目录');
INSERT INTO `system_menu` VALUES (2, '系统监控', 0, 7, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'airplay-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 11:05:08', '系统监控目录');
INSERT INTO `system_menu` VALUES (3, '系统工具', 0, 8, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'centos-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:57:19', '系统工具目录');
INSERT INTO `system_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-14 11:08:39', '用户管理菜单');
INSERT INTO `system_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'parent-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-14 11:11:54', '角色管理菜单');
INSERT INTO `system_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'menu-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-14 11:13:00', '菜单管理菜单');
INSERT INTO `system_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'organization-chart', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:18:06', '部门管理菜单');
INSERT INTO `system_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'account-box-2-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:15:52', '岗位管理菜单');
INSERT INTO `system_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'book-ai-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:16:58', '字典管理菜单');
INSERT INTO `system_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'sound-module-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:08:37', '参数设置菜单');
INSERT INTO `system_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message-2-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-14 11:15:01', '通知公告菜单');
INSERT INTO `system_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'booklet-line', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:20:49', '日志管理菜单');
INSERT INTO `system_menu` VALUES (109, '在线用户', 2, 1, 'online', 'system/monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', '吴同', '2026-04-20 09:32:53', '', NULL, '在线用户菜单');
INSERT INTO `system_menu` VALUES (110, '定时任务', 2, 2, 'job', 'system/monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', '吴同', '2026-04-20 09:32:53', '', NULL, '定时任务菜单');
INSERT INTO `system_menu` VALUES (111, '服务监控', 2, 4, 'server', 'system/monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', '吴同', '2026-04-20 09:32:53', '', NULL, '服务监控菜单');
INSERT INTO `system_menu` VALUES (112, '缓存监控', 2, 5, 'cache', 'system/monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', '吴同', '2026-04-20 09:32:53', '', NULL, '缓存监控菜单');
INSERT INTO `system_menu` VALUES (113, '缓存列表', 2, 6, 'cacheList', 'system/monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'list-view', '吴同', '2026-04-20 09:32:53', '吴同', '2026-05-15 10:35:38', '缓存列表菜单');
INSERT INTO `system_menu` VALUES (114, '代码生成', 3, 2, 'gen', 'system/tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', '吴同', '2026-04-20 09:32:53', '', NULL, '代码生成菜单');
INSERT INTO `system_menu` VALUES (115, '系统接口', 3, 3, 'swagger', 'system/tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', '吴同', '2026-04-20 09:32:53', '', NULL, '系统接口菜单');
INSERT INTO `system_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'system/monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', '吴同', '2026-04-20 09:32:53', '', NULL, '操作日志菜单');
INSERT INTO `system_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'system/monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', '吴同', '2026-04-20 09:32:53', '', NULL, '登录日志菜单');
INSERT INTO `system_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', '吴同', '2026-04-20 09:32:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1055, '生成查询', 114, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1056, '生成修改', 114, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1057, '生成删除', 114, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1058, '导入代码', 114, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1059, '预览代码', 114, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1060, '生成代码', 114, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', '吴同', '2026-04-20 09:32:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1061, '模型市场', 1080, 10, 'llm', NULL, '', '', 1, 0, 'M', '0', '0', '', '模型市场', '吴同', '2026-10-22 19:56:02', '', NULL, 'AI 模型与密钥管理目录');
INSERT INTO `system_menu` VALUES (1062, '模型市场', 1061, 1, 'modelMarket', 'basic/llm/modelMarket/index', '', '', 1, 0, 'C', '0', '0', 'ai:modelMarket:key:list', 'apps-line', '吴同', '2026-10-22 19:56:02', '', NULL, '供应商与 API 密钥配置');
INSERT INTO `system_menu` VALUES (1063, '我的模型', 1061, 2, 'myModel', 'basic/llm/myModel/index', '', '', 1, 0, 'C', '0', '0', 'ai:modelMarket:key:list', 'book-read-line', '吴同', '2026-10-22 19:56:02', '', NULL, '工作区模型管理');
INSERT INTO `system_menu` VALUES (1064, '密钥查询', 1062, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:query', '#', '吴同', '2026-10-22 19:56:02', '', NULL, '');
INSERT INTO `system_menu` VALUES (1065, '密钥配置', 1062, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:edit', '#', '吴同', '2026-10-22 19:56:02', '', NULL, '');
INSERT INTO `system_menu` VALUES (1066, '模型查询', 1063, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:query', '#', '吴同', '2026-10-22 19:56:03', '', NULL, '');
INSERT INTO `system_menu` VALUES (1077, '模型配置', 1063, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:edit', '#', '吴同', '2026-10-22 19:56:03', '', NULL, '');
INSERT INTO `system_menu` VALUES (1078, '本体建模', 0, 2, 'om', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '本体建模', '超级管理员', '2026-10-10 17:36:46', '吴同', '2026-10-23 10:35:43', '');
INSERT INTO `system_menu` VALUES (1079, 'AI辅助建模', 1078, 5, ':versionId/aiModeling', 'om/aiModeling/index', NULL, '', 1, 0, 'C', '0', '0', '', 'organization-chart', '吴同', '2026-10-19 14:50:52', '超级管理员', '2026-10-27 14:18:15', '');
INSERT INTO `system_menu` VALUES (1080, '基础管理', 0, 1, 'basic', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '基础管理', '超级管理员', '2026-10-12 13:56:32', '吴同', '2026-10-23 10:35:24', '');
INSERT INTO `system_menu` VALUES (1081, '项目管理', 1080, 1, 'project', 'basic/project/index', NULL, '', 1, 0, 'C', '0', '0', 'basic:project:project:list', '项目管理', 'admin', '2026-10-12 14:19:40', '吴同', '2026-10-23 10:36:02', '项目管理菜单');
INSERT INTO `system_menu` VALUES (1082, '语义资产', 1078, 2, ':versionId/semantic', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '语义资产', '超级管理员', '2026-10-10 17:41:08', '吴同', '2026-10-23 10:37:11', '');
INSERT INTO `system_menu` VALUES (1083, '枚举管理', 1082, 2, 'omEnum', 'om/semantic/enum/type/index', NULL, '', 1, 0, 'C', '0', '0', 'om:enumeration:type:list', '#', '超级管理员', '2026-10-10 17:42:07', '超级管理员', '2026-10-10 18:05:36', '');
INSERT INTO `system_menu` VALUES (1084, '枚举类型查询', 1083, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:query', '#', 'admin', '2026-10-10 17:48:04', '超级管理员', '2026-10-10 17:48:54', '');
INSERT INTO `system_menu` VALUES (1085, '枚举类型新增', 1083, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:add', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1086, '枚举类型修改', 1083, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:edit', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1087, '枚举类型删除', 1083, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:remove', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1088, '枚举类型导出', 1083, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:export', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1089, '枚举类型导入', 1083, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:type:import', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1090, '术语管理', 1082, 0, 'term', 'om/semantic/term/index', NULL, '', 1, 0, 'C', '0', '0', 'om:term:term:list', '#', '超级管理员', '2026-10-10 18:11:39', '超级管理员', '2026-10-10 18:12:18', '');
INSERT INTO `system_menu` VALUES (1091, '术语查询', 1090, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:query', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1092, '术语新增', 1090, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:add', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1093, '术语修改', 1090, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:edit', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1094, '术语删除', 1090, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:remove', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1095, '术语导出', 1090, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:export', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1096, '术语导入', 1090, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:term:term:import', '#', 'admin', '2026-10-10 18:12:24', '', NULL, '');
INSERT INTO `system_menu` VALUES (1097, '本体建模', 1078, 3, ':versionId/ontology', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '二级本体建模', '超级管理员', '2026-10-13 09:30:35', '吴同', '2026-10-23 10:37:19', '');
INSERT INTO `system_menu` VALUES (1098, '对象类型', 1097, 0, 'objectType', 'om/om/object/type/index', NULL, '', 1, 0, 'C', '0', '0', 'om:object:type:list', '#', '超级管理员', '2026-10-13 09:31:26', '超级管理员', '2026-10-13 09:33:42', '');
INSERT INTO `system_menu` VALUES (1099, '对象类型查询', 1098, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:query', '#', 'admin', '2026-10-13 09:32:12', '超级管理员', '2026-10-13 09:32:27', '');
INSERT INTO `system_menu` VALUES (1100, '对象类型新增', 1098, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:add', '#', 'admin', '2026-10-13 09:32:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1101, '对象类型修改', 1098, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:edit', '#', 'admin', '2026-10-13 09:32:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1102, '对象类型删除', 1098, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:remove', '#', 'admin', '2026-10-13 09:32:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1103, '对象类型导出', 1098, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:export', '#', 'admin', '2026-10-13 09:32:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1104, '对象类型导入', 1098, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:type:import', '#', 'admin', '2026-10-13 09:32:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1105, '对象类型关系', 1097, 2, 'objectRelation', 'om/om/object/relation/index', NULL, '', 1, 0, 'C', '0', '0', 'om:object:relation:list', '#', '超级管理员', '2026-10-13 18:19:34', '超级管理员', '2026-10-13 18:25:02', '');
INSERT INTO `system_menu` VALUES (1106, '对象类型关系查询', 1105, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:query', '#', 'admin', '2026-10-14 14:47:34', '超级管理员', '2026-10-14 14:47:46', '');
INSERT INTO `system_menu` VALUES (1107, '对象类型关系新增', 1105, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:add', '#', 'admin', '2026-10-14 14:47:34', '', NULL, '');
INSERT INTO `system_menu` VALUES (1108, '对象类型关系修改', 1105, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:edit', '#', 'admin', '2026-10-14 14:47:34', '', NULL, '');
INSERT INTO `system_menu` VALUES (1109, '对象类型关系删除', 1105, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:remove', '#', 'admin', '2026-10-14 14:47:34', '', NULL, '');
INSERT INTO `system_menu` VALUES (1110, '对象类型关系导出', 1105, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:export', '#', 'admin', '2026-10-14 14:47:34', '', NULL, '');
INSERT INTO `system_menu` VALUES (1111, '对象类型关系导入', 1105, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:object:relation:import', '#', 'admin', '2026-10-14 14:47:34', '', NULL, '');
INSERT INTO `system_menu` VALUES (1112, '项目管理查询', 1081, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:query', '#', 'admin', '2026-10-12 14:19:40', '', NULL, '');
INSERT INTO `system_menu` VALUES (1113, '项目管理新增', 1081, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:add', '#', 'admin', '2026-10-12 14:19:40', '', NULL, '');
INSERT INTO `system_menu` VALUES (1114, '项目管理修改', 1081, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:edit', '#', 'admin', '2026-10-12 14:19:40', '', NULL, '');
INSERT INTO `system_menu` VALUES (1115, '项目管理删除', 1081, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:remove', '#', 'admin', '2026-10-12 14:19:40', '', NULL, '');
INSERT INTO `system_menu` VALUES (1116, '建模目标', 1078, 1, ':versionId/target', 'om/omtarget/index', NULL, '', 1, 0, 'C', '0', '0', 'om:target:target:list', '建模目标', 'admin', '2026-10-13 14:45:21', '吴同', '2026-10-23 10:37:04', '建模目标菜单');
INSERT INTO `system_menu` VALUES (1117, '建模目标查询', 1116, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:target:target:query', '#', 'admin', '2026-10-13 14:45:21', '', NULL, '');
INSERT INTO `system_menu` VALUES (1118, '建模目标新增', 1116, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:target:target:add', '#', 'admin', '2026-10-13 14:45:22', '', NULL, '');
INSERT INTO `system_menu` VALUES (1119, '建模目标修改', 1116, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:target:target:edit', '#', 'admin', '2026-10-13 14:45:22', '', NULL, '');
INSERT INTO `system_menu` VALUES (1120, '建模目标删除', 1116, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:target:target:remove', '#', 'admin', '2026-10-13 14:45:22', '', NULL, '');
INSERT INTO `system_menu` VALUES (1121, '建模任务查询', 1116, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'om:omtarget:targettask:query', '#', '超级管理员', '2026-10-14 09:45:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1122, '建模任务验证', 1116, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'om:omtarget:targettask:add', '#', '超级管理员', '2026-10-14 09:45:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1123, '业务数据类型', 1080, 3, 'basicDataType', 'basic/dataType/index', NULL, '', 1, 0, 'C', '0', '0', 'basic:dataType:datatype:list', '数据类型', 'admin', '2026-10-16 14:02:11', '吴同', '2026-10-23 10:36:18', '数据类型菜单');
INSERT INTO `system_menu` VALUES (1124, '数据类型查询', 1123, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:dataType:datatype:query', '#', 'admin', '2026-10-16 14:02:11', '', NULL, '');
INSERT INTO `system_menu` VALUES (1125, '数据类型新增', 1123, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:dataType:datatype:add', '#', 'admin', '2026-10-16 14:02:11', '', NULL, '');
INSERT INTO `system_menu` VALUES (1126, '数据类型修改', 1123, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:dataType:datatype:edit', '#', 'admin', '2026-10-16 14:02:11', '', NULL, '');
INSERT INTO `system_menu` VALUES (1127, '数据类型删除', 1123, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:dataType:datatype:remove', '#', 'admin', '2026-10-16 14:02:11', '', NULL, '');
INSERT INTO `system_menu` VALUES (1128, '枚举管理', 1080, 2, 'basicEnum', 'basic/enumeration/index', NULL, '', 1, 0, 'C', '0', '0', 'basic:enumeration:type:list', '枚举管理', '超级管理员', '2026-10-10 17:42:07', '吴同', '2026-10-23 10:36:10', '');
INSERT INTO `system_menu` VALUES (1129, '枚举类型查询', 1128, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:type:query', '#', 'admin', '2026-10-10 17:48:04', '超级管理员', '2026-10-10 17:48:54', '');
INSERT INTO `system_menu` VALUES (1130, '枚举类型新增', 1128, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:type:add', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1131, '枚举类型修改', 1128, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:type:edit', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1132, '枚举类型删除', 1128, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:type:remove', '#', 'admin', '2026-10-10 17:48:04', '', NULL, '');
INSERT INTO `system_menu` VALUES (1133, '数据源', 1080, 4, 'datasource', 'basic/datasource/index', NULL, NULL, 1, 0, 'C', '0', '0', 'basic:datasource:datasource:list', '数据源', '吴同', '2025-06-17 10:23:51', '吴同', '2026-10-23 10:36:27', '');
INSERT INTO `system_menu` VALUES (1134, '数据源查询', 1133, 1, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'basic:datasource:datasource:query', '#', '吴同', '2025-06-17 10:23:51', '', NULL, '');
INSERT INTO `system_menu` VALUES (1135, '数据源新增', 1133, 2, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'basic:datasource:datasource:add', '#', '吴同', '2025-06-17 10:23:51', '', NULL, '');
INSERT INTO `system_menu` VALUES (1136, '数据源修改', 1133, 3, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'basic:datasource:datasource:edit', '#', '吴同', '2025-06-17 10:23:51', '', NULL, '');
INSERT INTO `system_menu` VALUES (1137, '数据源删除', 1133, 4, '', NULL, NULL, NULL, 1, 0, 'F', '0', '0', 'basic:datasource:datasource:remove', '#', '吴同', '2025-06-17 10:23:52', '', NULL, '');
INSERT INTO `system_menu` VALUES (1138, '文件分类', 1195, 1, 'fileCategory', 'basic/file/category/index', NULL, NULL, 1, 0, 'C', '0', '0', 'basic:file:category:list', '文件分类', 'admin', '2026-08-18 15:59:26', '吴同', '2026-10-23 10:36:35', '模型数据文件分类菜单');
INSERT INTO `system_menu` VALUES (1139, '文件分类查询', 1138, 1, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:query', '#', 'admin', '2026-08-18 15:59:27', '超级管理员', '2026-08-27 19:11:54', '');
INSERT INTO `system_menu` VALUES (1140, '文件分类新增', 1138, 2, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:add', '#', 'admin', '2026-08-18 15:59:27', '超级管理员', '2026-08-27 19:12:02', '');
INSERT INTO `system_menu` VALUES (1141, '文件分类修改', 1138, 3, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:edit', '#', 'admin', '2026-08-18 15:59:28', '超级管理员', '2026-08-27 19:12:10', '');
INSERT INTO `system_menu` VALUES (1142, '文件分类删除', 1138, 4, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:remove', '#', 'admin', '2026-08-18 15:59:28', '超级管理员', '2026-08-27 19:12:22', '');
INSERT INTO `system_menu` VALUES (1143, '文件管理', 1195, 2, 'fileManager', 'basic/file/manager/index', NULL, NULL, 1, 0, 'C', '0', '0', 'basic:file:manager:list', '文件管理', 'admin', '2026-08-18 15:59:42', '吴同', '2026-10-23 10:36:42', '模型数据文件菜单');
INSERT INTO `system_menu` VALUES (1144, '文件查询', 1143, 1, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:manager:query', '#', 'admin', '2026-08-18 15:59:42', '超级管理员', '2026-08-27 19:10:53', '');
INSERT INTO `system_menu` VALUES (1145, '文件新增', 1143, 2, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:manager:add', '#', 'admin', '2026-08-18 15:59:44', '超级管理员', '2026-08-27 19:11:01', '');
INSERT INTO `system_menu` VALUES (1146, '文件修改', 1143, 3, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:manager:edit', '#', 'admin', '2026-08-18 15:59:44', '超级管理员', '2026-08-27 19:11:09', '');
INSERT INTO `system_menu` VALUES (1147, '文件删除', 1143, 4, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:manager:remove', '#', 'admin', '2026-08-18 15:59:44', '超级管理员', '2026-08-27 19:11:16', '');
INSERT INTO `system_menu` VALUES (1148, '枚举数据列表', 1083, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:list', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1149, '枚举数据查询', 1083, 8, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:query', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1150, '枚举数据新增', 1083, 9, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:add', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1151, '枚举数据修改', 1083, 10, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:edit', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1152, '枚举数据删除', 1083, 11, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:remove', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1153, '枚举数据导出', 1083, 12, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:export', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1154, '枚举数据导入', 1083, 13, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:enumeration:data:import', '#', 'admin', '2026-10-20 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1155, '本体应用', 0, 3, 'app', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '本体应用', '吴同', '2026-10-20 14:56:32', '吴同', '2026-10-23 14:00:13', '');
INSERT INTO `system_menu` VALUES (1156, '知识图谱', 1155, 2, 'graph', '', NULL, '', 1, 0, 'M', '0', '0', '', '知识图谱', '吴同', '2026-10-20 15:21:42', '千桐科技', '2026-11-02 09:45:57', '');
INSERT INTO `system_menu` VALUES (1157, '业务数据类型', 1082, 1, 'omDataType', 'om/semantic/dataType/index', NULL, '', 1, 0, 'C', '0', '0', 'om:dataType:datatype:list', 'list-view', 'admin', '2026-10-21 00:00:00', '', NULL, '版本数据类型菜单');
INSERT INTO `system_menu` VALUES (1158, '数据类型查询', 1157, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:query', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1159, '数据类型新增', 1157, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:add', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1160, '数据类型修改', 1157, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:edit', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1161, '数据类型删除', 1157, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:remove', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1162, '项目概览', 1078, 0, ':versionId/overview', 'om/overview/index', NULL, '', 1, 0, 'C', '0', '0', '', '项目概览', '超级管理员', '2026-10-21 15:01:04', '吴同', '2026-10-23 10:36:50', '');
INSERT INTO `system_menu` VALUES (1163, '约束规则', 1078, 4, ':versionId/constraint', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '约束规则', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-27 15:28:39', '');
INSERT INTO `system_menu` VALUES (1164, '模型公理', 1163, 1, 'modelAxiom', 'om/om/constraint/modelAxiom/index', NULL, '', 1, 0, 'C', '0', '0', 'om:constraint:modelaxi:list', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1165, '模型公理查询', 1164, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:modelaxi:query', '#', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-28 11:42:22', '');
INSERT INTO `system_menu` VALUES (1166, '模型公理新增', 1164, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:modelaxi:add', '#', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-28 11:42:28', '');
INSERT INTO `system_menu` VALUES (1167, '模型公理修改', 1164, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:modelaxi:edit', '#', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-28 11:42:36', '');
INSERT INTO `system_menu` VALUES (1168, '模型公理删除', 1164, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:modelaxi:remove', '#', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-28 11:42:41', '');
INSERT INTO `system_menu` VALUES (1169, '数据规则', 1163, 2, 'dataRule', 'om/om/constraint/dataRule/index', NULL, '', 1, 0, 'C', '0', '0', 'om:constraint:datarule:list', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1170, '数据规则查询', 1169, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:datarule:query', '#', 'admin', '2026-10-22 19:56:25', '超级管理员', '2026-10-28 11:43:02', '');
INSERT INTO `system_menu` VALUES (1171, '数据规则新增', 1169, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:datarule:add', '#', 'admin', '2026-10-22 19:56:26', '超级管理员', '2026-10-28 11:43:17', '');
INSERT INTO `system_menu` VALUES (1172, '数据规则修改', 1169, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:datarule:edit', '#', 'admin', '2026-10-22 19:56:26', '超级管理员', '2026-10-28 11:43:21', '');
INSERT INTO `system_menu` VALUES (1173, '数据规则删除', 1169, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:datarule:remove', '#', 'admin', '2026-10-22 19:56:26', '超级管理员', '2026-10-28 11:43:26', '');
INSERT INTO `system_menu` VALUES (1174, '非结构化抽取', 1156, 2, 'unstructTask', 'app/unstructTask/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appUnstructTask:unstructtask:list', '非结构化抽取', '吴同', '2026-10-20 18:04:48', '吴同', '2026-10-23 14:01:40', '非结构化抽取任务菜单');
INSERT INTO `system_menu` VALUES (1175, '任务查询', 1174, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:query', '#', '吴同', '2026-10-22 20:06:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1176, '任务新增', 1174, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:add', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1177, '任务修改', 1174, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:edit', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1178, '任务删除', 1174, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:remove', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1179, '任务执行', 1174, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:edit', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1180, '执行日志', 1174, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:taskLog', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1181, '任务导出', 1174, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:export', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1182, '任务导入', 1174, 8, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:import', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1183, '抽取日志', 1156, 4, 'taskLog', 'app/taskLog/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appTaskLog:tasklog:list', '抽取日志', '吴同', '2026-10-20 18:04:48', '吴同', '2026-10-23 14:01:55', '结构化与非结构化通用抽取日志');
INSERT INTO `system_menu` VALUES (1184, '日志导出', 1183, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appTaskLog:tasklog:export', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1185, '结构化抽取', 1156, 3, 'structTask', 'app/structTask/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appStructTask:struct:list', '结构化抽取', '吴同', '2026-10-22 20:06:14', '吴同', '2026-10-23 14:01:46', '结构化抽取任务菜单');
INSERT INTO `system_menu` VALUES (1186, '任务查询', 1185, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:list', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1187, '任务新增', 1185, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:add', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1188, '任务修改', 1185, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:edit', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1189, '任务删除', 1185, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:remove', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1190, '任务执行', 1185, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:edit', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1191, '任务导出', 1185, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:export', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1192, '任务导入', 1185, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:import', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1193, '项目管理导出', 1081, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:export', '#', '超级管理员', '2026-10-22 18:49:17', '超级管理员', '2026-10-22 18:49:30', '');
INSERT INTO `system_menu` VALUES (1194, '枚举类型数据查看', 1128, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:data:list', '#', '超级管理员', '2026-10-22 19:07:21', '超级管理员', '2026-10-22 19:07:29', '');
INSERT INTO `system_menu` VALUES (1195, '文件管理', 1080, 4, 'file', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '文件管理', '吴同', '2026-11-02 14:18:12', '吴同', '2026-11-03 15:58:22', '文件分类与文件管理统一入口');
INSERT INTO `system_menu` VALUES (1196, '版本管理', 1078, 6, ':versionId/projectVersion', 'basic/projectVersion/index', NULL, '', 1, 0, 'C', '0', '0', 'basic:project:projectversion:list', 'account-box-2-line', 'admin', '2026-10-27 15:07:18', '超级管理员', '2026-10-27 15:13:07', '项目版本菜单');
INSERT INTO `system_menu` VALUES (1197, '项目版本查询', 1196, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:query', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO `system_menu` VALUES (1198, '版本管理新增', 1196, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:add', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO `system_menu` VALUES (1199, '版本管理修改', 1196, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:edit', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO `system_menu` VALUES (1200, '版本管理删除', 1196, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:remove', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO `system_menu` VALUES (1201, '版本管理对比', 1196, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:diff', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO `system_menu` VALUES (1202, '质量中心', 1078, 6, 'quality', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, '质量中心', '吴同', '2026-10-28 09:44:37', '', NULL, '');
INSERT INTO `system_menu` VALUES (1203, '质量验证', 1202, 6, ':versionId/qualityValidation', 'om/qualityValidation/index', NULL, '', 1, 0, 'C', '0', '0', '', '#', '吴同', '2026-10-26 15:27:19', '吴同', '2026-10-28 09:44:50', '');
INSERT INTO `system_menu` VALUES (1204, 'AI建模查询', 1079, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:list', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1205, 'AI建模新增', 1079, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:add', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1206, 'AI建模修改', 1079, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:edit', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1207, 'AI建模删除', 1079, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:remove', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1208, 'AI建模采纳', 1079, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:adopt', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1209, 'AI建模重解析', 1079, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:reparse', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1210, 'AI建模证据', 1079, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:evidence', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1211, '智能问答', 1155, 5, 'ontologyQa', 'app/ontologyQa/index', NULL, '', 1, 0, 'C', '0', '0', '', 'menu-qa', '吴同', '2026-11-01 10:14:15', '吴同', '2026-11-01 10:14:21', '');
INSERT INTO `system_menu` VALUES (1212, '分析看板', 1155, 6, 'analysisDashboard', 'app/analysisDashboard/index', NULL, '', 1, 0, 'C', '0', '0', '', 'menu-board', '吴同', '2026-11-01 11:33:53', '吴同', '2026-11-01 11:34:40', '');
INSERT INTO `system_menu` VALUES (1213, '业务智能体', 1155, 7, 'businessAgent', 'app/businessAgent/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'menu-agent', '吴同', '2026-11-01 14:50:55', '', NULL, '');
INSERT INTO `system_menu` VALUES (1214, '应用中心', 1155, 1, 'applicationCenter', 'app/applicationCenter/index', NULL, '', 1, 0, 'C', '0', '0', NULL, 'menu-app-center', '吴同', '2026-11-01 16:36:54', '', NULL, '');
INSERT INTO `system_menu` VALUES (1215, '图谱探索', 1156, 1, 'general', 'app/graph/index', NULL, 'general', 1, 0, 'C', '0', '0', NULL, '#', '千桐科技', '2026-11-02 09:46:32', '', NULL, '');
INSERT INTO `system_menu` VALUES (1216, '事件管理', 1156, 5, 'event', 'app/event/index', NULL, 'event', 1, 0, 'C', '0', '0', 'app:graph:event:list', '#', '千桐科技', '2026-11-02 09:48:55', '', NULL, '');
INSERT INTO `system_menu` VALUES (1217, '事件管理查询', 1216, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:query', '#', '吴同', '2026-11-01 18:46:28', '', NULL, '');
INSERT INTO `system_menu` VALUES (1218, '事件管理新增', 1216, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:add', '#', '吴同', '2026-11-01 18:46:53', '', NULL, '');
INSERT INTO `system_menu` VALUES (1219, '事件管理修改', 1216, 3, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:edit', '#', '吴同', '2026-11-01 18:47:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1220, '事件管理删除', 1216, 4, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:remove', '#', '吴同', '2026-11-01 18:47:39', '', NULL, '');
INSERT INTO `system_menu` VALUES (1221, '事件管理导入', 1216, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:import', '#', '吴同', '2026-11-01 18:47:59', '吴同', '2026-11-01 18:48:40', '');
INSERT INTO `system_menu` VALUES (1222, '事件管理导出', 1216, 6, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'app:graph:event:export', '#', '吴同', '2026-11-01 18:48:29', '', NULL, '');

-- ----------------------------
-- Table structure for system_notice
-- ----------------------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice`  (
                                  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
                                  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告标题',
                                  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
                                  `notice_content` longblob NULL COMMENT '公告内容',
                                  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
                                  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_notice
-- ----------------------------
INSERT INTO `system_notice` VALUES (1, 'qOntology本体工程平台正式开源！', '2', 0x3C703E714F6E746F6C6F6779E69CACE4BD93E5B7A5E7A88BE5B9B3E58FB0E6ADA3E5BC8FE5BC80E6BA90EFBC813C2F703E, '0', '吴同', '2026-10-15 13:47:04', '超级管理员', '2026-10-22 15:34:46', NULL);
INSERT INTO `system_notice` VALUES (2, '本体建模操作指南：对象类型与关系管理', '1', 0x3C703EE9809AE8BF87E69CACE4BD93E5BBBAE6A8A1E7BBB4E68AA4E5AFB9E8B1A1E7B1BBE59E8BE38081E5B19EE680A7E5928CE5AFB9E8B1A1E585B3E7B3BBEFBC8CE7BB9FE4B880E4B89AE58AA1E6A682E5BFB5E4B88EE585B3E7B3BBE8A1A8E8BEBEE380823C2F703E3C703EE5BBBAE8AEAEE58588E7A1AEE5AE9AE9A1B9E79BAEE5928CE78988E69CACEFBC8CE5868DE5AE8CE59684E5AFB9E8B1A1E5B19EE680A7EFBC8CE5B9B6E5B086E5AFB9E8B1A1E7B1BBE59E8BE58F8AE585B3E7B3BBE585B3E88194E588B0E5BBBAE6A8A1E79BAEE6A087E380823C2F703E, '0', '吴同', '2026-10-19 09:30:00', '吴同', '2026-10-19 09:30:00', NULL);
INSERT INTO `system_notice` VALUES (3, '语义资产维护说明：术语、数据类型与枚举', '2', 0x3C703EE59CA8E8AFADE4B989E8B584E4BAA7E4B8ADE7BB9FE4B880E7BBB4E68AA4E69CAFE8AFADE38081E4B89AE58AA1E695B0E68DAEE7B1BBE59E8BE5928CE69E9AE4B8BEEFBC8CE5878FE5B091E4B88DE5908CE4B89AE58AA1E59CBAE699AFE4B8ADE79A84E6ADA7E4B989E380823C2F703E3C703EE7BBB4E68AA4E69CAFE8AFADE697B6E8AFB7E5A1ABE58699E5908DE7A7B0E38081E88BB1E69687E5908DE7A7B0E38081E588ABE5908DE38081E7BCA9E58699E5928CE69DA5E6BA90EFBC9BE5BC95E794A8E69E9AE4B8BEE697B6E8AFB7E6B3A8E6848FE58CBAE58886E5BD93E5898DE9A1B9E79BAEE4B88EE585A8E5B180E69DA5E6BA90E380823C2F703E, '0', '吴同', '2026-10-20 10:00:00', '吴同', '2026-10-20 10:00:00', NULL);
INSERT INTO `system_notice` VALUES (4, '项目版本与本体导出使用说明', '1', 0x3C703EE9A1B9E79BAEE794A8E4BA8EE7BB84E7BB87E69CACE4BD93E6A8A1E59E8BEFBC8CE78988E69CACE794A8E4BA8EE58CBAE58886E4B88DE5908CE998B6E6AEB5E79A84E6A8A1E59E8BE58685E5AEB9E380823C2F703E3C703EE5AFBCE587BAE5898DE8AFB7E7A1AEE8AEA4E9A1B9E79BAEE38081E78988E69CACE5928CE69687E4BBB6E5908DE7A7B0E38082E5BD93E5898DE9A1B5E99DA2E68F90E4BE9B20547572746C65EFBC882E74746CEFBC89E5AFBCE587BAEFBC8CE585B6E4BB96E5B195E7A4BAE4B8ADE79A84E6A0BCE5BC8FE69A82E69CAAE5BC80E694BEE98089E68BA9E380823C2F703E, '0', '吴同', '2026-10-21 14:00:00', '吴同', '2026-10-21 14:00:00', NULL);
INSERT INTO `system_notice` VALUES (5, '建模目标维护与验证状态说明', '2', 0x3C703EE5BBBAE6A8A1E79BAEE6A087E794A8E4BA8EE8AEB0E5BD95E99C80E8A681E9809AE8BF87E69CACE4BD93E6A8A1E59E8BE59B9EE7AD94E79A84E4B89AE58AA1E997AEE9A298E380823C2F703E3C703EE8AFB7E7BB93E59088E997AEE9A298E69DA5E6BA90E38081E4B89AE58AA1E59CBAE699AFE5928CE4BC98E58588E7BAA7E7BBB4E68AA4E79BAEE6A087EFBC8CE5B9B6E585B3E88194E79BB8E585B3E5AFB9E8B1A1E7B1BBE59E8BE68896E585B3E7B3BBEFBC8CE4BEBFE4BA8EE68C81E7BBADE8B79FE8B8AAE6A8A1E59E8BE5AFB9E79BAEE6A087E997AEE9A298E79A84E8A686E79B96E68385E586B5E380823C2F703E, '0', '吴同', '2026-10-22 09:30:00', '吴同', '2026-10-22 09:30:00', NULL);
INSERT INTO `system_notice` VALUES (6, 'qOntology 1.1.0 正式发布', '2', 0x3C68313E3C7374726F6E673EF09F9A8020714F6E746F6C6F67792076312E312E3020E69BB4E696B0E697A5E5BF973C2F7374726F6E673E3C2F68313E3C68323E3C7374726F6E673EF09FA7AD20E78988E69CACE6A682E8BFB03C2F7374726F6E673E3C2F68323E3C703EE69CACE6ACA1E78988E69CACE59BB4E7BB95E69CACE4BD93E5BBBAE6A8A1E4B88EE79FA5E8AF86E59BBEE8B0B1E5BA94E794A8E585A8E993BEE8B7AFEFBC8CE585A8E99DA2E58D87E7BAA7E59BBEE8B0B1E68EA2E7B4A2E883BDE58A9BEFBC8CE696B0E5A29EE59B9BE7B1BBE8A786E59BBEE4B88EE4BA8BE4BBB6E7AEA1E79086EFBC9BE69CACE4BD93E5BA94E794A8E696B0E5A29EE5BA94E794A8E4B8ADE5BF83E38081E699BAE883BDE997AEE7AD94E38081E58886E69E90E79C8BE69DBFE38081E4B89AE58AA1E699BAE883BDE4BD93E7AD89E9A1B5E99DA2EFBC9BE6ADA3E5BC8FE4B88AE7BABF20414920E8BE85E58AA9E5BBBAE6A8A1EFBC8CE694AFE68C81E69687E6A1A3E4B88AE4BCA0E38081E5A4A7E6A8A1E59E8BE8A7A3E69E90E38081E69DA5E6BA90E8BFBDE6BAAFE4B88EE8BF87E7A88BE59B9EE79C8BEFBC9BE696B0E5A29EE9A1B9E79BAEE78988E69CACE7AEA1E79086E4B88EE7BAA6E69D9FE8A784E58899E9858DE7BDAEEFBC8CE5B9B6E5908CE6ADA5E5AE8CE68890E4B880E689B9E7B3BBE7BB9FE7958CE99DA2E4B88EE4BAA4E4BA92E7BB86E88A82E4BC98E58C96EFBC8CE585A8E99DA2E68F90E58D87E4BB8EE5BBBAE6A8A1E38081E6B2BBE79086E588B0E5BA94E794A8E890BDE59CB0E79A84E69588E78E87E4B88EE4BD93E9AA8CE380823C2F703E3C68323E3C7374726F6E673EF09F9BA020E4B8BBE8A681E69BB4E696B0E58685E5AEB93C2F7374726F6E673E3C2F68323E3C68333E3C7374726F6E673E31EFB88FE283A320E79FA5E8AF86E59BBEE8B0B1E883BDE58A9BE58D87E7BAA7EFBC8CE59BBEE8B0B1E68EA2E7B4A2E69BB4E585A8E99DA23C2F7374726F6E673E3C2F68333E3C703EE2978F20E59BBEE8B0B1E68EA2E7B4A2E696B0E5A29EE59B9BE7B1BBE8A786E59BBEEFBC9AE5B8B8E8A784E8A786E59BBEE38081E585B3E7B3BBE8A786E59BBEE38081E697B6E5BA8FE8A786E59BBEE38081E8A1A8E6A0BCE8A786E59BBEEFBC8CE6BBA1E8B6B3E5A49AE59CBAE699AFE59BBEE8B0B1E6B58FE8A788E58886E69E90E99C80E6B1823C2F703E3C703EE2978F20E696B0E5A29EE4BA8BE4BBB6E7AEA1E79086E6A8A1E59D97EFBC8CE694AFE68C81E4BA8BE4BBB6E4B88EE5AE9EE4BD93E7BB91E5AE9AEFBC8CE5AE8CE59684E59BBEE8B0B1E4BA8BE4BBB6E993BEE8B7AFE7AEA1E790863C2F703E3C68333E3C7374726F6E673E32EFB88FE283A320E69CACE4BD93E5BA94E794A8E9A1B5E99DA2E689A9E5AEB9EFBC8CE4B89AE58AA1E890BDE59CB0E69BB4E5AE8CE695B43C2F7374726F6E673E3C2F68333E3C703EE2978F20E69CACE4BD93E5BA94E794A8E696B0E5A29EE5BA94E794A8E4B8ADE5BF83E38081E699BAE883BDE997AEE7AD94E38081E58886E69E90E79C8BE69DBFE38081E4B89AE58AA1E699BAE883BDE4BD93E9A1B5E99DA2EFBC8CE8A1A5E9BD90E4BB8EE5BBBAE6A8A1E588B0E5BA94E794A8E79A84E585A8E993BEE8B7AFE883BDE58A9B3C2F703E3C68333E3C7374726F6E673E33EFB88FE283A320E4B88AE7BABF20414920E8BE85E58AA9E5BBBAE6A8A1EFBC8CE5BBBAE6A8A1E69588E78E87E5A4A7E5B985E68F90E58D873C2F7374726F6E673E3C2F68333E3C703EE2978F20E694AFE68C81E4B88AE4BCA0E69687E6A1A3E7BB93E59088E5A4A7E6A8A1E59E8BE887AAE58AA8E8A7A3E69E90E695B0E68DAEEFBC8CE68ABDE58F96E58685E5AEB9E8A686E79B96E5BBBAE6A8A1E79BAEE6A087E38081E69CAFE8AFADE38081E69E9AE4B8BEE38081E5AFB9E8B1A1E38081E5AFB9E8B1A1E7B1BBE59E8BE5B19EE680A7E38081E5AFB9E8B1A1E7B1BBE59E8BE585B3E7B3BBE38081E6A8A1E59E8BE585ACE79086E38081E695B0E68DAEE8A784E588993C2F703E3C703EE2978F20E68ABDE58F96E7BB93E69E9CE694AFE68C81E69FA5E79C8BE69DA5E6BA90E38081E4B880E994AEE98787E7BAB3E4BDBFE794A8EFBC8CE695B0E68DAEE58FAFE6BAAFE6BA90E38081E58FAFE5A48DE794A83C2F703E3C703EE2978F20E696B0E5A29E20414920E8A7A3E69E90E8BF87E7A88BE69FA5E79C8BEFBC8CE6AF8FE4B8AAE6ADA5E9AAA4E38081E6AF8FE6ACA1E6809DE88083E8BF87E7A88BE59D87E58FAFE59B9EE6BAAF3C2F703E3C68333E3C7374726F6E673E34EFB88FE283A320E696B0E5A29EE78988E69CACE7AEA1E79086EFBC8CE8BFADE4BBA3E5AFB9E6AF94E69BB4E6B885E699B03C2F7374726F6E673E3C2F68333E3C703EE2978F20E694AFE68C81E9A1B9E79BAEE5A49AE78988E69CACE58886E7B1BBE7AEA1E79086EFBC8CE7BB9FE4B880E7AEA1E68EA7E6A8A1E59E8BE8BFADE4BBA3E58E86E58FB23C2F703E3C703EE2978F20E694AFE68C81E5908CE4B880E9A1B9E79BAEE4B88DE5908CE78988E69CACE4B880E994AEE5AFB9E6AF94EFBC8CE79BB4E8A782E69FA5E79C8BE6A8A1E59E8BE5B7AEE5BC823C2F703E3C703EE2978F20E694AFE68C81E59FBAE4BA8EE58E86E58FB2E78988E69CACE5889BE5BBBAE696B0E78988E69CACE5B9B6E69BB4E696B0E695B0E68DAEEFBC8CE9998DE4BD8EE9878DE5A48DE5BBBAE6A8A1E68890E69CAC3C2F703E3C68333E3C7374726F6E673E35EFB88FE283A320E696B0E5A29EE7BAA6E69D9FE8A784E58899E9858DE7BDAEEFBC8CE6A8A1E59E8BE6B2BBE79086E69BB4E8A784E88C833C2F7374726F6E673E3C2F68333E3C703EE2978F20E696B0E5A29EE6A8A1E59E8BE585ACE79086E7AEA1E79086EFBC8CE99288E5AFB9E69CACE4BD93E695B0E68DAEE588B6E5AE9AE7BB9FE4B880E8A784E588993C2F703E3C703EE2978F20E696B0E5A29EE695B0E68DAEE8A784E58899E9858DE7BDAEEFBC8CE99288E5AFB9E5AFB9E8B1A1E7B1BBE59E8BE5B19EE680A7E695B0E68DAEE8BF9BE8A18CE8A784E58899E9858DE7BDAE3C2F703E3C68323E3C7374726F6E673EF09F94A720E7B3BBE7BB9FE8B083E695B4E4B88EE4BC98E58C963C2F7374726F6E673E3C2F68323E3C703EE2978F20E4BFAEE5A48DE9A696E9A1B5E696B0E997BBE585ACE5918AE6BB9AE58AA8E5BC82E5B8B8E997AEE9A298EFBC8CE4BF9DE99A9CE585ACE5918AE6ADA3E5B8B8E8BDAEE692ADE5B195E7A4BA3C2F703E3C703EE2978F20E5BC80E6BA90E58D8FE8AEAEE9A1B5E5A4B4E983A8E69687E6A188E5B7A6E5AFB9E9BD90EFBC8CE5B7A6E58FB3E997B4E8B79DE8B083E695B43C2F703E3C703EE2978F20E5B7A6E4BEA7E88F9CE58D95E6A08FE3808CE7A4BEE58CBAE4B88EE694AFE68C81E3808DE6A087E9A298E4B88EE68C89E992AEE997B4E8B79DE7BB9FE4B880E4B8BA20313570783C2F703E3C703EE2978F20E696B0E5A29EE5BCB9E7AA97E68C89E992AEE58CBAE4B88AE4B88BE997B4E8B79DE8B083E695B43C2F703E3C703EE2978F20E5BCB9E7AA97E997B4E8B79DE7BB9FE4B880E4B8BAE5B7A6E58FB32034307078E38081E4B88AE4B88B2031357078EFBC8CE8A1A8E58D95E997B4E8B79DE7BB9FE4B880E4B8BA20313570783C2F703E3C703EE2978F20E7BCA9E5B08FE68F90E7A4BAE59BBEE6A087E5B0BAE5AFB8EFBC8CE6A087E9A298E4B88EE69687E6A188E5B7A6E5AFB9E9BD90EFBC8CE6A087E9A298E694B9E4B8BAE3808CE6A087E9A2982BE589AFE6A087E9A298E3808DE7BB93E69E843C2F703E3C703EE2978F20E696B0E5A29EE5BCB9E7AA97E6A0B7E5BC8FE8B083E695B4EFBC8CE695B0E68DAEE8BF9EE68EA5E7B1BBE59E8BE5A29EE58AA0E7B1BBE59E8BE59BBEE6A0873C2F703E3C703EE2978F20E696B0E5A29EE3808CE6B58BE8AF95E8BF9EE68EA5E3808DE68C89E992AEEFBC8CE6938DE4BD9CE58CBAE9A1BAE5BA8FE8B083E695B4E4B8BAE3808CE6B58BE8AF95E8BF9EE68EA52DE8AFA6E683852DE4BFAEE694B92DE588A0E999A4E3808D3C2F703E3C703EE2978F20E69687E4BBB6E58886E7B1BBE4B88EE69687E4BBB6E7AEA1E79086E59088E5B9B6E4B8BAE4B880E7BAA7E88F9CE58D95E3808CE69687E4BBB6E7AEA1E79086E3808D3C2F703E3C703EE2978F20E69687E4BBB6E58886E7B1BBE5A29EE58AA0E788B6E7BAA7E58886E7B1BBE7AD9BE98089EFBC8CE5AD97E6AEB5E9A1BAE5BA8FE8B083E695B4E4B8BAE3808CE58886E7B1BBE5908DE7A7B0E38081E68F8FE8BFB0E38081E78AB6E68081E38081E68E92E5BA8FE38081E5889BE5BBBAE4BABAE38081E5889BE5BBBAE697B6E997B4E3808DEFBC8CE696B0E5A29EE590AFE794A82FE7A681E794A8E78AB6E68081E5BC80E585B3EFBC8CE7A7BBE999A4E5908DE7A7B0E5AD97E7ACA6E695B0E99990E588B63C2F703E3C703EE2978F20E69687E4BBB6E7AEA1E79086E5B7A6E4BEA7E58886E7B1BBE6A091E9BB98E8AEA4E5B195E5BC80E4B8A4E7BAA7EFBC8CE696B0E5A29EE69687E4BBB6E7B1BBE59E8BE7AD9BE98089E4B88EE8AFA6E68385E68C89E992AEEFBC88E8A2ABE5BC95E794A8E697B6E5B195E7A4BAE9A1B5E99DA2E7BAA7E8AFA6E68385EFBC89EFBC8CE3808CE695B0E68DAEE58886E7B1BBE3808DE69BB4E5908DE4B8BAE3808CE69687E4BBB6E58886E7B1BBE3808D3C2F703E3C703EE2978F20E7AD9BE98089E58CBAE696B0E5A29EE7AD9BE98089E69DA1E4BBB6EFBC8CE58DA1E78987E68F8FE8BFB0E69687E6A188E689A9E58585EFBC8CE696B0E5A29EE696B0E794A8E688B7E5BC95E5AFBCE68F90E7A4BA3C2F703E3C703EE2978F20E58897E8A1A8E694AFE68C81E5B195E7A4BAE585B3E88194E4B89AE58AA1E695B0E68DAEE7B1BBE59E8BE695B0E9878FEFBC8CE8AFA6E68385E694B9E4B8BAE9A1B5E99DA2E7BAA7E8AFA6E68385E5B9B6E696B0E5A29EE3808CE585B3E88194E4B89AE58AA1E695B0E68DAEE7B1BBE59E8BE3808D5461623C2F703E3C703EE2978F20E58897E8A1A8E696B0E5A29EE7BB84E59088E5AD97E6AEB5E5B195E7A4BAE4B89AE58AA1E4BFA1E681AFEFBC8CE694AFE68C81E696B0E5A29EE5AD97E6AEB5E7BB84E59088E5B195E7A4BAEFBC8CE8AFA6E68385E4B8ADE5B195E7A4BAE585B3E88194E58685E5AEB93C2F703E, '0', '吴同', '2026-11-02 19:10:37', '', NULL, NULL);
INSERT INTO `system_notice` VALUES (7, 'qOntology 1.1.1 正式发布', '2', 0x3C68313E3C7374726F6E673EF09F9A80714F6E746F6C6F67792076312E312E3120E69BB4E696B0E697A5E5BF973C2F7374726F6E673E3C2F68313E3C68323E3C7374726F6E673EF09FA7AD20E78988E69CACE6A682E8BFB03C2F7374726F6E673E3C2F68323E3C703EE69CACE6ACA1E78988E69CACE697A0E696B0E5A29EE6A0B8E5BF83E4B89AE58AA1E883BDE58A9BEFBC8CE8819AE784A6E9A1B5E99DA2E5BDA2E68081E58D87E7BAA7E38081E7BB84E4BBB6E6A087E58786E58C96E694B9E980A0E38081E4BAA4E4BA92E6A0B7E5BC8FE4BC98E58C96EFBC8CE5908CE6ADA5E4BFAEE5A48D4149E8BE85E58AA9E5BBBAE6A8A1E38081E69CACE4BD93E5BBBAE6A8A1E9A1B9E79BAEE585B3E88194E7AD89E997AEE9A298EFBC9BE696B0E5A29EE5A49AE5A484E9A1B5E99DA2E7BAA7E8AFA6E68385E38081546162E9A1B5E58886E7BB84E38081E5BCB9E6A186E58886E58CBAE5B883E5B180EFBC8CE8A1A5E9BD90E68ABDE58F96E4BBBBE58AA1E68C87E6A087E4BFA1E681AFEFBC8CE7BB9FE4B880E695B0E68DAEE6BA90E38081E5BBBAE6A8A1E58583E7B4A0E5B195E7A4BAE8A784E88C83EFBC8CE68F90E58D87E69CACE4BD93E5BBBAE6A8A1E38081E68ABDE58F96E4BBBBE58AA1E7AEA1E79086E79A84E4BFA1E681AFE58FAFE8AFBBE680A7E4B88EE6938DE4BD9CE6B581E79585E5BAA6E380823C2F703E3C68323E3C7374726F6E673EE29CA820E7B3BBE7BB9FE4BC98E58C963C2F7374726F6E673E3C2F68323E3C68333E3C7374726F6E673E31EFB88FE283A320E9809AE794A8E6A087E58786E58C96E694B9E980A03C2F7374726F6E673E3C2F68333E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE7B3BBE7BB9FE8A1A8E6A0BCE38081E6909CE7B4A2E38081E8A1A8E58D95E38081E6A091E5BDA2E7BB84E4BBB6E7BB9FE4B880E69BBFE68DA2E4B8BAE9809AE794A8E7BB84E4BBB6EFBC8CE8A784E88C83E7BB84E4BBB6E4BDBFE794A8E6A087E58786E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE9A1B6E983A8E68F90E98692E6A0B7E5BC8FE58D87E7BAA7EFBC9AE696B0E5A29EE4BA8CE7BAA7E6A087E9A298EFBC8CE4BC98E58C96E5AD97E4BD93E38081E5AD97E58FB7E38081E9A29CE889B2E4B88EE8BEB9E8B79DE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE58897E8A1A8E5AD97E6AEB5E8B685E995BFE59CBAE699AFE694AFE68C81E5AD97E6AEB5E58685E5AEB9E59088E5B9B6E5B195E7A4BAE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE4BFAEE694B9E5BCB9E6A186E5A29EE5BCBAEFBC9AE8A1A5E58585E58A9FE883BDE8AFB4E6988EE38081E5AD97E6AEB5E6B3A8E8A7A3EFBC8CE5A29EE58AA0E5AD97E6AEB5E8BE93E585A5E995BFE5BAA6E99990E588B6E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE8AFA6E68385E9A1B5E7BB9FE4B880E4BC98E58C96EFBC9AE5BCB9E6A186E59E8BE8AFA6E68385E8B083E695B4E5B195E7A4BAE58685E5AEB9EFBC9BE9A1B5E99DA2E59E8BE8AFA6E68385E4BC98E58C96E59FBAE7A180E4BFA1E681AFE5B195E7A4BAE4B88EE7BD91E6A0BCE5B883E5B180EFBC8CE5908CE6ADA5E69BB4E696B074616273E99DA2E69DBFE58685E5AEB9E6A0B7E5BC8FE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE9A1B5E99DA2E7BAA7E8AFA6E68385E694AFE68C81E58897E8A1A8E782B9E587BBE5908DE7A7B0E79BB4E68EA5E8B7B3E8BDACE8AFA6E68385E9A1B5E99DA2E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE789B9E6AE8AE9A1B5E99DA2E5908DE7A7B0E5898DE7BDAEE59BBEE6A087E6A087E8AF86EFBC8CE68F90E58D87E8BEA8E8AF86E5BAA6E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E4149E5BA94E794A8E9A1B5E99DA2E585A8E9878FE6A087E58786E58C96EFBC9AE7BB9FE4B880E5AD97E4BD93E5A4A7E5B08FE38081E69687E5AD97E9A29CE889B2E38081E59BBEE6A087E58F8AE7BB84E4BBB6E6A0B7E5BC8FE380823C2F703E3C68333E3C7374726F6E673E32EFB88FE283A320E59084E4B89AE58AA1E9A1B5E99DA2E4B893E9A1B9E4BC98E58C963C2F7374726F6E673E3C2F68333E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE9A1B9E79BAEE7AEA1E79086EFBC9AE696B0E5A29EE9A1B5E99DA2E7BAA7E8AFA6E68385E9A1B5EFBC8C546162E9A1B5E58685E7BDAEE78988E69CACE7AEA1E79086E6A8A1E59D97E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE695B0E68DAEE6BA90E9A1B5E99DA2E695B4E4BD93E4BC98E58C96EFBC8CE695B0E68DAEE6BA90E7B1BBE59E8BE9858DE5A597E5A29EE58AA0E7B1BBE59E8BE59BBEE6A087E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE5BBBAE6A8A1E79BAEE6A087E585B3E88194E58583E7B4A0E5B195E7A4BAE6A0B7E5BC8FE9878DE69E84E8B083E695B4E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE5AFB9E8B1A1E7B1BBE59E8BE696B0E5A29EE5BCB9E6A186E9878DE69E84EFBC8CE58886E4B8BAE59FBAE7A180E4BFA1E681AFE38081E585B3E88194E5BBBAE6A8A1E79BAEE6A087E4B8A4E4B8AAE58886E7BB84E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE5AFB9E8B1A1E7B1BBE59E8BE585B3E7B3BBE696B0E5A29EE5BCB9E6A186E9878DE69E84EFBC8CE58886E4B8BAE59FBAE7A180E4BFA1E681AFE38081E585B3E88194E5BBBAE6A8A1E79BAEE6A087E4B8A4E4B8AAE58886E7BB84E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE6A8A1E59E8BE585ACE79086E696B0E5A29EE5BCB9E6A186EFBC9AE4B89AE58AA1E695B0E68DAEE7B1BBE59E8BE694AFE68C81E98089E68BA9E5BD93E5898DE9A1B9E79BAEE68896E585A8E5B180E9A1B9E79BAEE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE99D9EE7BB93E69E84E58C96E68ABDE58F96E58897E8A1A8E696B0E5A29EE69C80E696B0E689A7E8A18CE697B6E997B4E38081E689A7E8A18CE88097E697B6E38081E8B49FE8B4A3E4BABAE5AD97E6AEB5EFBC9BE8AFA6E68385E58D87E7BAA7E4B8BAE9A1B5E99DA2E7BAA7E8AFA6E68385EFBC8C546162E9A1B5E58C85E590ABE689A7E8A18CE697A5E5BF97E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE7BB93E69E84E58C96E68ABDE58F96E58897E8A1A8E696B0E5A29EE69C80E696B0E689A7E8A18CE697B6E997B4E38081E689A7E8A18CE88097E697B6E38081E8B49FE8B4A3E4BABAE5AD97E6AEB5EFBC9BE8AFA6E68385E58D87E7BAA7E4B8BAE9A1B5E99DA2E7BAA7E8AFA6E68385EFBC8C546162E9A1B5E58C85E590ABE689A7E8A18CE697A5E5BF97E380823C2F703E3C68323E3C7374726F6E673EF09F9BA0EFB88F20E997AEE9A298E4BFAEE5A48D3C2F7374726F6E673E3C2F68323E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223E312E266E6273703B3C2F7370616E3E20E8B083E695B44149E8BE85E58AA9E5BBBAE6A8A1E79A84E69687E6A1A3E4B88AE4BCA0E7B1BBE59E8BE4B88EE5AD98E582A8E4BF9DE5AD98E980BBE8BE91E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223E322E266E6273703B3C2F7370616E3E20E4BFAEE5A48DE69CACE4BD93E5BBBAE6A8A1E58FB3E4B88AE8A792E58887E68DA2E9A1B9E79BAEE5908EEFBC8CE696B0E5BBBAE5BBBAE6A8A1E79BAEE6A087E5BD92E5B19EE9A1B9E79BAEE4B88DE6ADA3E7A1AEE79A84E997AEE9A298E380823C2F703E3C68323E3C7374726F6E673EF09F938C20E78988E69CACE680BBE7BB933C2F7374726F6E673E3C2F68323E3C703E714F6E746F6C6F67792076312E312E3120E4BBA55549E6A087E58786E58C96E694B6E6959BE38081E9A1B5E99DA2E5BDA2E68081E58D87E7BAA7E4B88EE997AEE9A298E4BFAEE5A48DE4B8BAE4B8BBEFBC8CE5AE8CE68890E585A8E5B9B3E58FB0E9809AE794A8E7BB84E4BBB6E7BB9FE4B880EFBC8CE890BDE59CB0E9A1B9E79BAEE38081E68ABDE58F96E4BBBBE58AA1E9A1B5E99DA2E7BAA7E8AFA6E68385E694B9E980A0EFBC8CE9878DE69E84E5A49AE7B1BBE696B0E5A29EE5BCB9E6A186E5B883E5B180EFBC8CE8A1A5E58585E68ABDE58F96E4BBBBE58AA1E8BF90E8A18CE68C87E6A087EFBC8CE4BC98E58C96E695B0E68DAEE6BA90E38081E5BBBAE6A8A1E585B3E88194E58583E7B4A0E5B195E7A4BAE69588E69E9CEFBC8CE8A7A3E586B34149E8BE85E58AA9E5BBBAE6A8A1E69687E6A1A3E4B88AE4BCA0E38081E5BBBAE6A8A1E79BAEE6A087E9A1B9E79BAEE5BD92E5B19EE5BC82E5B8B8E997AEE9A298EFBC8CE7958CE99DA2E8A784E88C83E5BAA6E38081E4BFA1E681AFE5AE8CE5A487E5BAA6E4B88EE6938DE4BD9CE7A8B3E5AE9AE680A7E5908CE6ADA5E68F90E58D87EFBC8CE9998DE4BD8EE69CACE4BD93E5BBBAE6A8A1E38081E68ABDE58F96E4BBBBE58AA1E8BF90E7BBB4E69FA5E79C8BE68890E69CACE380823C2F703E, '0', '吴同', '2026-11-06 18:33:49', '', NULL, NULL);
INSERT INTO `system_notice` VALUES (8, 'qOntology 1.1.2 正式发布', '2', 0x3C68313E3C7374726F6E673EF09F9A8020714F6E746F6C6F67792076312E312E3220E69BB4E696B0E697A5E5BF973C2F7374726F6E673E3C2F68313E3C68323E3C7374726F6E673EF09FA7AD20E78988E69CACE6A682E8BFB03C2F7374726F6E673E3C2F68323E3C703EE69CACE6ACA1E78988E69CACE8819AE784A6E7B3BBE7BB9FE4BAA4E4BA92E8A784E88C83E7BB9FE4B880E38081E8AFA6E68385E9A1B5E99DA2E5AE8CE59684E4B88EE58897E8A1A8E5B195E7A4BAE4BC98E58C96EFBC8CE7BB9FE4B880E8B083E695B4E689B9E9878FE6938DE4BD9CE38081E590AFE794A8E78AB6E68081E4B88BE79A84E6938DE4BD9CE99990E588B6E58F8AE697A0E695B0E68DAEE5B195E7A4BAE69588E69E9CEFBC9BE696B0E5A29EE9A1B9E79BAEE7AEA1E79086E9A1B5E99DA2E7BAA7E8AFA6E68385E5B9B6E585B3E88194E78988E69CACE7AEA1E79086EFBC8CE4BC98E58C96E695B0E68DAEE6BA90E38081E6A8A1E59E8BE5B882E59CBAE38081E5BBBAE6A8A1E79BAEE6A087E7AD89E4B89AE58AA1E9A1B5E99DA2EFBC8CE5908CE6ADA5E4BFAEE5A48DE5BBBAE6A8A1E79BAEE6A087E8AFA6E68385E8BF94E59B9EE5908EE9A1B5E99DA2E7A9BAE799BDE79A84E997AEE9A298EFBC8CE4BBA5E58F8A4149E8BE85E58AA9E5BBBAE6A8A1E98787E7BAB3E5BC82E5B8B8E997AEE9A298EFBC8CE68F90E58D87E4BFA1E681AFE58FAFE8AFBBE680A7E4B88EE6938DE4BD9CE6B581E79585E5BAA6E380823C2F703E3C68323E3C7374726F6E673EE29CA820E7B3BBE7BB9FE4BC98E58C963C2F7374726F6E673E3C2F68323E3C68333E3C7374726F6E673E31EFB88FE283A320E9809AE794A8E4BAA4E4BA92E4BC98E58C963C2F7374726F6E673E3C2F68333E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE8B083E695B4E7B3BBE7BB9FE689B9E9878FE588A0E999A4E68C89E992AEE5908DE7A7B0EFBC8CE4BC98E58C96E689B9E9878FE588A0E999A4E5BCB9E6A186E79A84E5B883E5B180E4B88EE5B195E7A4BAE6A0B7E5BC8FE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE7BB9FE4B880E58897E8A1A8E6938DE4BD9CE99990E588B6EFBC9AE695B0E68DAEE5A484E4BA8EE590AFE794A8E78AB6E68081E697B6EFBC8CE4BFAEE694B9E38081E588A0E999A4E6938DE4BD9CE4B88DE58FAFE794A8E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3EE7B3BBE7BB9FE58685E9A1B5E99DA2E697A0E695B0E68DAEE697B6E7BB9FE4B880E4BDBFE794A8E7A9BAE78AB6E68081E59BBEE78987EFBC8CE4BC98E58C96E7A9BAE799BDE58CBAE59F9FE79A84E5B195E7A4BAE69588E69E9CE380823C2F703E3C68333E3C7374726F6E673E32EFB88FE283A320E59084E4B89AE58AA1E9A1B5E99DA2E4B893E9A1B9E4BC98E58C963C2F7374726F6E673E3C2F68333E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE9A1B9E79BAEE7AEA1E790863C2F7374726F6E673EEFBC9AE696B0E5A29EE9A1B5E99DA2E7BAA7E8AFA6E68385E9A1B5EFBC8CE585B3E88194E5B195E7A4BAE78988E69CACE7AEA1E79086E4BFA1E681AFEFBC8CE696B9E4BEBFE69FA5E79C8BE9A1B9E79BAEE58F8AE585B6E78988E69CACE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE695B0E68DAEE6BA903C2F7374726F6E673EEFBC9AE58897E8A1A8E5AD97E6AEB5E2809CE5908DE7A7B0E2809DE2809CE7B1BBE59E8BE2809DE58886E588ABE8B083E695B4E4B8BAE2809CE695B0E68DAEE6BA90E5908DE7A7B0E2809DE2809CE695B0E68DAEE6BA90E7B1BBE59E8BE2809DEFBC8CE6988EE7A1AEE5AD97E6AEB5E590ABE4B989E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE6A8A1E59E8BE5B882E59CBAE58F8AE68891E79A84E6A8A1E59E8B3C2F7374726F6E673EEFBC9AE8AFA6E68385E9A1B5E58897E8A1A8E7BB9FE4B880E4BDBFE794A82071742D7461626C6520E7BB84E4BBB6EFBC8CE5B9B6E69BBFE68DA2E697A0E695B0E68DAEE697B6E79A84E8838CE699AFE59BBEE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE5BBBAE6A8A1E79BAEE6A0873C2F7374726F6E673EEFBC9AE9BB98E8AEA4E68C89E5889BE5BBBAE697B6E997B4E58092E5BA8FE68E92E58897EFBC8CE4BC98E58588E5B195E7A4BAE69C80E696B0E5889BE5BBBAE79A84E695B0E68DAEEFBC9BE5908CE6ADA5E4BC98E58C96E8AFA6E68385E9A1B5E58897E8A1A8E6A0B7E5BC8FE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673E4149E8BE85E58AA9E5BBBAE6A8A13C2F7374726F6E673EEFBC9AE8A1A5E58585E68F8FE8BFB0E4BFA1E681AFE79A84E995BFE5BAA6E68F90E7A4BAEFBC8CE6988EE7A1AEE8BE93E585A5E8A681E6B182E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE695B0E68DAEE8A784E588993C2F7374726F6E673EEFBC9AE8B083E695B4E8AFA6E68385E9A1B5E5A4B1E8B4A5E69DA1E4BBB6E79A84E5B195E7A4BAE6A0B7E5BC8FEFBC8CE68F90E58D87E58685E5AEB9E58FAFE8AFBBE680A7E380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE5AFB9E8B1A1E7B1BBE59E8BE585B3E7B3BB3C2F7374726F6E673EEFBC9AE8B083E695B4E58897E8A1A8E5AD97E6AEB5E9A1BAE5BA8FEFBC8CE58F8DE59091E5908DE7A7B0E4B8BAE7A9BAE697B6E7BB9FE4B880E5B195E7A4BAE2809C2DE2809DE380823C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE2978F203C2F7370616E3E3C7374726F6E673EE7BB93E69E84E58C96E68ABDE58F963C2F7374726F6E673EEFBC9AE8B083E695B4E8B083E5BAA6E591A8E69C9FE5BCB9E7AA97E6A0B7E5BC8FEFBC8CE4BC98E58C96E9858DE7BDAEE7958CE99DA2E79A84E5B883E5B180E4B88EE5B195E7A4BAE69588E69E9CE380823C2F703E3C68323E3C7374726F6E673EF09F9BA020E997AEE9A298E4BFAEE5A48D3C2F7374726F6E673E3C2F68323E3C703E312E266E6273703B20E4BFAEE5A48DE4BB8EE5BBBAE6A8A1E79BAEE6A087E8AFA6E68385E9A1B5E8BF94E59B9EE5908EE9A1B5E99DA2E7A9BAE799BDE79A84E997AEE9A298E380823C2F703E3C703E322E266E6273703B20E4BFAEE5A48D4149E8BE85E58AA9E5BBBAE6A8A1E7BB93E69E9CE98787E7BAB3E697B6E79A84E5BC82E5B8B8E997AEE9A298E380823C2F703E3C68323E3C7374726F6E673EF09F938C20E78988E69CACE680BBE7BB933C2F7374726F6E673E3C2F68323E3C703EE69CACE6ACA1E69BB4E696B0E4BBA5E4BAA4E4BA92E4BC98E58C96E38081E5B195E7A4BAE8A784E88C83E7BB9FE4B880E4B88EE997AEE9A298E4BFAEE5A48DE4B8BAE4B8BBEFBC8CE5AE8CE59684E9A1B9E79BAEE58F8AE78988E69CACE4BFA1E681AFE5B195E7A4BAEFBC8CE7BB9FE4B880E7A9BAE78AB6E68081E5928CE58897E8A1A8E6938DE4BD9CE8A784E58899EFBC8CE4BC98E58C96E5A49AE4B8AAE4B89AE58AA1E9A1B5E99DA2E79A84E5AD97E6AEB5E38081E58897E8A1A8E58F8AE5BCB9E7AA97E6A0B7E5BC8FEFBC8CE5B9B6E4BFAEE5A48DE5BBBAE6A8A1E79BAEE6A087E8AFA6E68385E8BF94E59B9EE5BC82E5B8B8E58F8A4149E8BE85E58AA9E5BBBAE6A8A1E98787E7BAB3E5BC82E5B8B8E997AEE9A298EFBC8CE68F90E58D87E697A5E5B8B8E5BBBAE6A8A1E4B88EE7AEA1E79086E6938DE4BD9CE79A84E4BDBFE794A8E4BD93E9AA8CE380823C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223E0B3C2F7370616E3E3C2F703E, '0', '吴同', '2026-11-10 19:49:24', '', NULL, NULL);

-- ----------------------------
-- Table structure for system_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `system_oper_log`;
CREATE TABLE `system_oper_log`  (
                                    `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                    `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
                                    `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
                                    `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
                                    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
                                    `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
                                    `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
                                    `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
                                    `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
                                    `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
                                    `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
                                    `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
                                    `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
                                    `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
                                    `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
                                    `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
                                    `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
                                    PRIMARY KEY (`oper_id`) USING BTREE,
                                    INDEX `idx_system_oper_log_bt`(`business_type` ASC) USING BTREE,
                                    INDEX `idx_system_oper_log_s`(`status` ASC) USING BTREE,
                                    INDEX `idx_system_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_oper_log
-- ----------------------------
INSERT INTO `system_oper_log` VALUES (1, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:27:05', 83);
INSERT INTO `system_oper_log` VALUES (2, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":3,\"roleKey\":\"sales\",\"roleName\":\"客户经理\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:27:51', 88);
INSERT INTO `system_oper_log` VALUES (3, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,101,102,103,104,105,106,1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1000,1007,1012,1016,1020,1025,1030,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077],\"params\":{},\"remark\":\"\",\"roleId\":4,\"roleKey\":\"guest\",\"roleName\":\"体验用户\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:29:17', 81);
INSERT INTO `system_oper_log` VALUES (4, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:31:22', 65);
INSERT INTO `system_oper_log` VALUES (5, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '192.168.0.72', '内网IP', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1195,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1061,1062,1064,1065,1063,1066,1077,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1204,1205,1206,1207,1208,1209,1210,1196,1197,1198,1199,1200,1201,1202,1203,1155,1214,1156,1215,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1216,1217,1218,1219,1220,1221,1222,1211,1212,1213,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"千桐科技\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-11-02 16:58:18', 304);
INSERT INTO `system_oper_log` VALUES (6, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '192.168.0.72', '内网IP', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1195,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1061,1062,1064,1065,1063,1066,1077,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1204,1205,1206,1207,1208,1209,1210,1196,1197,1198,1199,1200,1201,1202,1203,1155,1214,1156,1215,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1216,1217,1218,1219,1220,1221,1222,1211,1212,1213,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":3,\"roleKey\":\"sales\",\"roleName\":\"客户经理\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"千桐科技\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-11-02 16:58:23', 158);
INSERT INTO `system_oper_log` VALUES (7, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '192.168.0.72', '内网IP', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,101,102,103,104,105,106,1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1195,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1061,1062,1064,1065,1063,1066,1077,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1196,1197,1198,1199,1200,1201,1202,1203,1155,1214,1156,1215,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1216,1217,1218,1219,1220,1221,1222,1211,1212,1213,1000,1007,1012,1016,1020,1025,1030,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045],\"params\":{},\"remark\":\"\",\"roleId\":4,\"roleKey\":\"guest\",\"roleName\":\"体验用户\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"千桐科技\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-11-02 16:58:42', 149);
INSERT INTO `system_oper_log` VALUES (8, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '117.89.24.233', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1061,1062,1063,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1195,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1064,1066,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1204,1205,1206,1207,1208,1209,1210,1196,1197,1198,1199,1200,1201,1202,1203,1155,1214,1156,1215,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1216,1217,1218,1219,1220,1221,1222,1211,1212,1213,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-11-02 18:36:58', 91);
INSERT INTO `system_oper_log` VALUES (9, '业务数据类型状态', 2, 'tech.qiantong.qontology.module.basic.controller.admin.dataType.BasicDataTypeController.updateStatus()', 'PUT', 1, 'qOntology', '江苏千桐科技有限公司', '/basic/dataType/11/status', '127.0.0.1', '内网IP', '{\"status\":\"0\"}', '{\"code\":200,\"msg\":\"操作成功\"}', 0, NULL, '2026-11-10 17:31:38', 63);
INSERT INTO `system_oper_log` VALUES (10, '业务数据类型状态', 2, 'tech.qiantong.qontology.module.basic.controller.admin.dataType.BasicDataTypeController.updateStatus()', 'PUT', 1, 'qOntology', '江苏千桐科技有限公司', '/basic/dataType/11/status', '127.0.0.1', '内网IP', '{\"status\":\"1\"}', '{\"code\":200,\"msg\":\"操作成功\"}', 0, NULL, '2026-11-10 17:31:44', 18);

-- ----------------------------
-- Table structure for system_post
-- ----------------------------
DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post`  (
                                `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
                                `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
                                `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
                                `post_sort` int NOT NULL COMMENT '显示顺序',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态（0正常 1停用）',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_post
-- ----------------------------
INSERT INTO `system_post` VALUES (1, 'CEO', '董事长', 1, '0', '吴同', '2026-05-12 09:33:22', '', NULL, NULL);
INSERT INTO `system_post` VALUES (2, 'DM', '部门经理', 2, '0', '吴同', '2026-05-12 09:33:23', '', NULL, NULL);
INSERT INTO `system_post` VALUES (3, 'AR', '架构师', 3, '0', '吴同', '2026-05-12 09:33:23', '', NULL, NULL);
INSERT INTO `system_post` VALUES (4, 'SE', '软件工程师', 4, '0', '吴同', '2026-05-12 09:33:24', '', NULL, NULL);
INSERT INTO `system_post` VALUES (5, 'PM', '产品经理', 5, '0', '吴同', '2026-05-12 09:33:24', '', NULL, NULL);
INSERT INTO `system_post` VALUES (6, 'QA', '测试工程师', 6, '0', '吴同', '2026-05-12 09:33:24', '', NULL, NULL);
INSERT INTO `system_post` VALUES (7, 'OP', '运维工程师', 7, '0', '吴同', '2026-05-12 09:33:25', '', NULL, NULL);
INSERT INTO `system_post` VALUES (8, 'CM', '客户经理', 8, '0', '吴同', '2026-05-12 09:33:25', '', NULL, NULL);

-- ----------------------------
-- Table structure for system_role
-- ----------------------------
DROP TABLE IF EXISTS `system_role`;
CREATE TABLE `system_role`  (
                                `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                                `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                                `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
                                `role_sort` int NOT NULL COMMENT '显示顺序',
                                `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
                                `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
                                `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_role
-- ----------------------------
INSERT INTO `system_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '', NULL, '');
INSERT INTO `system_role` VALUES (2, '系统管理员', 'system', 2, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '超级管理员', '2026-11-02 18:36:58', '');
INSERT INTO `system_role` VALUES (3, '客户经理', 'sales', 3, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '千桐科技', '2026-11-02 16:58:23', '');
INSERT INTO `system_role` VALUES (4, '体验用户', 'guest', 4, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '千桐科技', '2026-11-02 16:58:41', '');

-- ----------------------------
-- Table structure for system_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_role_dept`;
CREATE TABLE `system_role_dept`  (
                                     `role_id` bigint NOT NULL COMMENT '角色ID',
                                     `dept_id` bigint NOT NULL COMMENT '部门ID',
                                     PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和部门关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for system_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `system_role_menu`;
CREATE TABLE `system_role_menu`  (
                                     `role_id` bigint NOT NULL COMMENT '角色ID',
                                     `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                     PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_role_menu
-- ----------------------------
INSERT INTO `system_role_menu` VALUES (2, 1);
INSERT INTO `system_role_menu` VALUES (2, 2);
INSERT INTO `system_role_menu` VALUES (2, 3);
INSERT INTO `system_role_menu` VALUES (2, 100);
INSERT INTO `system_role_menu` VALUES (2, 101);
INSERT INTO `system_role_menu` VALUES (2, 102);
INSERT INTO `system_role_menu` VALUES (2, 103);
INSERT INTO `system_role_menu` VALUES (2, 104);
INSERT INTO `system_role_menu` VALUES (2, 105);
INSERT INTO `system_role_menu` VALUES (2, 106);
INSERT INTO `system_role_menu` VALUES (2, 107);
INSERT INTO `system_role_menu` VALUES (2, 108);
INSERT INTO `system_role_menu` VALUES (2, 109);
INSERT INTO `system_role_menu` VALUES (2, 110);
INSERT INTO `system_role_menu` VALUES (2, 111);
INSERT INTO `system_role_menu` VALUES (2, 112);
INSERT INTO `system_role_menu` VALUES (2, 113);
INSERT INTO `system_role_menu` VALUES (2, 114);
INSERT INTO `system_role_menu` VALUES (2, 115);
INSERT INTO `system_role_menu` VALUES (2, 500);
INSERT INTO `system_role_menu` VALUES (2, 501);
INSERT INTO `system_role_menu` VALUES (2, 1000);
INSERT INTO `system_role_menu` VALUES (2, 1001);
INSERT INTO `system_role_menu` VALUES (2, 1002);
INSERT INTO `system_role_menu` VALUES (2, 1003);
INSERT INTO `system_role_menu` VALUES (2, 1004);
INSERT INTO `system_role_menu` VALUES (2, 1005);
INSERT INTO `system_role_menu` VALUES (2, 1006);
INSERT INTO `system_role_menu` VALUES (2, 1007);
INSERT INTO `system_role_menu` VALUES (2, 1008);
INSERT INTO `system_role_menu` VALUES (2, 1009);
INSERT INTO `system_role_menu` VALUES (2, 1010);
INSERT INTO `system_role_menu` VALUES (2, 1011);
INSERT INTO `system_role_menu` VALUES (2, 1012);
INSERT INTO `system_role_menu` VALUES (2, 1013);
INSERT INTO `system_role_menu` VALUES (2, 1014);
INSERT INTO `system_role_menu` VALUES (2, 1015);
INSERT INTO `system_role_menu` VALUES (2, 1016);
INSERT INTO `system_role_menu` VALUES (2, 1017);
INSERT INTO `system_role_menu` VALUES (2, 1018);
INSERT INTO `system_role_menu` VALUES (2, 1019);
INSERT INTO `system_role_menu` VALUES (2, 1020);
INSERT INTO `system_role_menu` VALUES (2, 1021);
INSERT INTO `system_role_menu` VALUES (2, 1022);
INSERT INTO `system_role_menu` VALUES (2, 1023);
INSERT INTO `system_role_menu` VALUES (2, 1024);
INSERT INTO `system_role_menu` VALUES (2, 1025);
INSERT INTO `system_role_menu` VALUES (2, 1026);
INSERT INTO `system_role_menu` VALUES (2, 1027);
INSERT INTO `system_role_menu` VALUES (2, 1028);
INSERT INTO `system_role_menu` VALUES (2, 1029);
INSERT INTO `system_role_menu` VALUES (2, 1030);
INSERT INTO `system_role_menu` VALUES (2, 1031);
INSERT INTO `system_role_menu` VALUES (2, 1032);
INSERT INTO `system_role_menu` VALUES (2, 1033);
INSERT INTO `system_role_menu` VALUES (2, 1034);
INSERT INTO `system_role_menu` VALUES (2, 1035);
INSERT INTO `system_role_menu` VALUES (2, 1036);
INSERT INTO `system_role_menu` VALUES (2, 1037);
INSERT INTO `system_role_menu` VALUES (2, 1038);
INSERT INTO `system_role_menu` VALUES (2, 1039);
INSERT INTO `system_role_menu` VALUES (2, 1040);
INSERT INTO `system_role_menu` VALUES (2, 1041);
INSERT INTO `system_role_menu` VALUES (2, 1042);
INSERT INTO `system_role_menu` VALUES (2, 1043);
INSERT INTO `system_role_menu` VALUES (2, 1044);
INSERT INTO `system_role_menu` VALUES (2, 1045);
INSERT INTO `system_role_menu` VALUES (2, 1046);
INSERT INTO `system_role_menu` VALUES (2, 1047);
INSERT INTO `system_role_menu` VALUES (2, 1048);
INSERT INTO `system_role_menu` VALUES (2, 1049);
INSERT INTO `system_role_menu` VALUES (2, 1050);
INSERT INTO `system_role_menu` VALUES (2, 1051);
INSERT INTO `system_role_menu` VALUES (2, 1052);
INSERT INTO `system_role_menu` VALUES (2, 1053);
INSERT INTO `system_role_menu` VALUES (2, 1054);
INSERT INTO `system_role_menu` VALUES (2, 1055);
INSERT INTO `system_role_menu` VALUES (2, 1056);
INSERT INTO `system_role_menu` VALUES (2, 1057);
INSERT INTO `system_role_menu` VALUES (2, 1058);
INSERT INTO `system_role_menu` VALUES (2, 1059);
INSERT INTO `system_role_menu` VALUES (2, 1060);
INSERT INTO `system_role_menu` VALUES (2, 1061);
INSERT INTO `system_role_menu` VALUES (2, 1062);
INSERT INTO `system_role_menu` VALUES (2, 1063);
INSERT INTO `system_role_menu` VALUES (2, 1064);
INSERT INTO `system_role_menu` VALUES (2, 1066);
INSERT INTO `system_role_menu` VALUES (2, 1078);
INSERT INTO `system_role_menu` VALUES (2, 1079);
INSERT INTO `system_role_menu` VALUES (2, 1080);
INSERT INTO `system_role_menu` VALUES (2, 1081);
INSERT INTO `system_role_menu` VALUES (2, 1082);
INSERT INTO `system_role_menu` VALUES (2, 1083);
INSERT INTO `system_role_menu` VALUES (2, 1084);
INSERT INTO `system_role_menu` VALUES (2, 1085);
INSERT INTO `system_role_menu` VALUES (2, 1086);
INSERT INTO `system_role_menu` VALUES (2, 1087);
INSERT INTO `system_role_menu` VALUES (2, 1088);
INSERT INTO `system_role_menu` VALUES (2, 1089);
INSERT INTO `system_role_menu` VALUES (2, 1090);
INSERT INTO `system_role_menu` VALUES (2, 1091);
INSERT INTO `system_role_menu` VALUES (2, 1092);
INSERT INTO `system_role_menu` VALUES (2, 1093);
INSERT INTO `system_role_menu` VALUES (2, 1094);
INSERT INTO `system_role_menu` VALUES (2, 1095);
INSERT INTO `system_role_menu` VALUES (2, 1096);
INSERT INTO `system_role_menu` VALUES (2, 1097);
INSERT INTO `system_role_menu` VALUES (2, 1098);
INSERT INTO `system_role_menu` VALUES (2, 1099);
INSERT INTO `system_role_menu` VALUES (2, 1100);
INSERT INTO `system_role_menu` VALUES (2, 1101);
INSERT INTO `system_role_menu` VALUES (2, 1102);
INSERT INTO `system_role_menu` VALUES (2, 1103);
INSERT INTO `system_role_menu` VALUES (2, 1104);
INSERT INTO `system_role_menu` VALUES (2, 1105);
INSERT INTO `system_role_menu` VALUES (2, 1106);
INSERT INTO `system_role_menu` VALUES (2, 1107);
INSERT INTO `system_role_menu` VALUES (2, 1108);
INSERT INTO `system_role_menu` VALUES (2, 1109);
INSERT INTO `system_role_menu` VALUES (2, 1110);
INSERT INTO `system_role_menu` VALUES (2, 1111);
INSERT INTO `system_role_menu` VALUES (2, 1112);
INSERT INTO `system_role_menu` VALUES (2, 1113);
INSERT INTO `system_role_menu` VALUES (2, 1114);
INSERT INTO `system_role_menu` VALUES (2, 1115);
INSERT INTO `system_role_menu` VALUES (2, 1116);
INSERT INTO `system_role_menu` VALUES (2, 1117);
INSERT INTO `system_role_menu` VALUES (2, 1118);
INSERT INTO `system_role_menu` VALUES (2, 1119);
INSERT INTO `system_role_menu` VALUES (2, 1120);
INSERT INTO `system_role_menu` VALUES (2, 1121);
INSERT INTO `system_role_menu` VALUES (2, 1122);
INSERT INTO `system_role_menu` VALUES (2, 1123);
INSERT INTO `system_role_menu` VALUES (2, 1124);
INSERT INTO `system_role_menu` VALUES (2, 1125);
INSERT INTO `system_role_menu` VALUES (2, 1126);
INSERT INTO `system_role_menu` VALUES (2, 1127);
INSERT INTO `system_role_menu` VALUES (2, 1128);
INSERT INTO `system_role_menu` VALUES (2, 1129);
INSERT INTO `system_role_menu` VALUES (2, 1130);
INSERT INTO `system_role_menu` VALUES (2, 1131);
INSERT INTO `system_role_menu` VALUES (2, 1132);
INSERT INTO `system_role_menu` VALUES (2, 1133);
INSERT INTO `system_role_menu` VALUES (2, 1134);
INSERT INTO `system_role_menu` VALUES (2, 1135);
INSERT INTO `system_role_menu` VALUES (2, 1136);
INSERT INTO `system_role_menu` VALUES (2, 1137);
INSERT INTO `system_role_menu` VALUES (2, 1138);
INSERT INTO `system_role_menu` VALUES (2, 1139);
INSERT INTO `system_role_menu` VALUES (2, 1140);
INSERT INTO `system_role_menu` VALUES (2, 1141);
INSERT INTO `system_role_menu` VALUES (2, 1142);
INSERT INTO `system_role_menu` VALUES (2, 1143);
INSERT INTO `system_role_menu` VALUES (2, 1144);
INSERT INTO `system_role_menu` VALUES (2, 1145);
INSERT INTO `system_role_menu` VALUES (2, 1146);
INSERT INTO `system_role_menu` VALUES (2, 1147);
INSERT INTO `system_role_menu` VALUES (2, 1148);
INSERT INTO `system_role_menu` VALUES (2, 1149);
INSERT INTO `system_role_menu` VALUES (2, 1150);
INSERT INTO `system_role_menu` VALUES (2, 1151);
INSERT INTO `system_role_menu` VALUES (2, 1152);
INSERT INTO `system_role_menu` VALUES (2, 1153);
INSERT INTO `system_role_menu` VALUES (2, 1154);
INSERT INTO `system_role_menu` VALUES (2, 1155);
INSERT INTO `system_role_menu` VALUES (2, 1156);
INSERT INTO `system_role_menu` VALUES (2, 1157);
INSERT INTO `system_role_menu` VALUES (2, 1158);
INSERT INTO `system_role_menu` VALUES (2, 1159);
INSERT INTO `system_role_menu` VALUES (2, 1160);
INSERT INTO `system_role_menu` VALUES (2, 1161);
INSERT INTO `system_role_menu` VALUES (2, 1162);
INSERT INTO `system_role_menu` VALUES (2, 1163);
INSERT INTO `system_role_menu` VALUES (2, 1164);
INSERT INTO `system_role_menu` VALUES (2, 1165);
INSERT INTO `system_role_menu` VALUES (2, 1166);
INSERT INTO `system_role_menu` VALUES (2, 1167);
INSERT INTO `system_role_menu` VALUES (2, 1168);
INSERT INTO `system_role_menu` VALUES (2, 1169);
INSERT INTO `system_role_menu` VALUES (2, 1170);
INSERT INTO `system_role_menu` VALUES (2, 1171);
INSERT INTO `system_role_menu` VALUES (2, 1172);
INSERT INTO `system_role_menu` VALUES (2, 1173);
INSERT INTO `system_role_menu` VALUES (2, 1174);
INSERT INTO `system_role_menu` VALUES (2, 1175);
INSERT INTO `system_role_menu` VALUES (2, 1176);
INSERT INTO `system_role_menu` VALUES (2, 1177);
INSERT INTO `system_role_menu` VALUES (2, 1178);
INSERT INTO `system_role_menu` VALUES (2, 1179);
INSERT INTO `system_role_menu` VALUES (2, 1180);
INSERT INTO `system_role_menu` VALUES (2, 1181);
INSERT INTO `system_role_menu` VALUES (2, 1182);
INSERT INTO `system_role_menu` VALUES (2, 1183);
INSERT INTO `system_role_menu` VALUES (2, 1184);
INSERT INTO `system_role_menu` VALUES (2, 1185);
INSERT INTO `system_role_menu` VALUES (2, 1186);
INSERT INTO `system_role_menu` VALUES (2, 1187);
INSERT INTO `system_role_menu` VALUES (2, 1188);
INSERT INTO `system_role_menu` VALUES (2, 1189);
INSERT INTO `system_role_menu` VALUES (2, 1190);
INSERT INTO `system_role_menu` VALUES (2, 1191);
INSERT INTO `system_role_menu` VALUES (2, 1192);
INSERT INTO `system_role_menu` VALUES (2, 1193);
INSERT INTO `system_role_menu` VALUES (2, 1194);
INSERT INTO `system_role_menu` VALUES (2, 1195);
INSERT INTO `system_role_menu` VALUES (2, 1196);
INSERT INTO `system_role_menu` VALUES (2, 1197);
INSERT INTO `system_role_menu` VALUES (2, 1198);
INSERT INTO `system_role_menu` VALUES (2, 1199);
INSERT INTO `system_role_menu` VALUES (2, 1200);
INSERT INTO `system_role_menu` VALUES (2, 1201);
INSERT INTO `system_role_menu` VALUES (2, 1202);
INSERT INTO `system_role_menu` VALUES (2, 1203);
INSERT INTO `system_role_menu` VALUES (2, 1204);
INSERT INTO `system_role_menu` VALUES (2, 1205);
INSERT INTO `system_role_menu` VALUES (2, 1206);
INSERT INTO `system_role_menu` VALUES (2, 1207);
INSERT INTO `system_role_menu` VALUES (2, 1208);
INSERT INTO `system_role_menu` VALUES (2, 1209);
INSERT INTO `system_role_menu` VALUES (2, 1210);
INSERT INTO `system_role_menu` VALUES (2, 1211);
INSERT INTO `system_role_menu` VALUES (2, 1212);
INSERT INTO `system_role_menu` VALUES (2, 1213);
INSERT INTO `system_role_menu` VALUES (2, 1214);
INSERT INTO `system_role_menu` VALUES (2, 1215);
INSERT INTO `system_role_menu` VALUES (2, 1216);
INSERT INTO `system_role_menu` VALUES (2, 1217);
INSERT INTO `system_role_menu` VALUES (2, 1218);
INSERT INTO `system_role_menu` VALUES (2, 1219);
INSERT INTO `system_role_menu` VALUES (2, 1220);
INSERT INTO `system_role_menu` VALUES (2, 1221);
INSERT INTO `system_role_menu` VALUES (2, 1222);
INSERT INTO `system_role_menu` VALUES (3, 1);
INSERT INTO `system_role_menu` VALUES (3, 2);
INSERT INTO `system_role_menu` VALUES (3, 3);
INSERT INTO `system_role_menu` VALUES (3, 100);
INSERT INTO `system_role_menu` VALUES (3, 101);
INSERT INTO `system_role_menu` VALUES (3, 102);
INSERT INTO `system_role_menu` VALUES (3, 103);
INSERT INTO `system_role_menu` VALUES (3, 104);
INSERT INTO `system_role_menu` VALUES (3, 105);
INSERT INTO `system_role_menu` VALUES (3, 106);
INSERT INTO `system_role_menu` VALUES (3, 107);
INSERT INTO `system_role_menu` VALUES (3, 108);
INSERT INTO `system_role_menu` VALUES (3, 109);
INSERT INTO `system_role_menu` VALUES (3, 110);
INSERT INTO `system_role_menu` VALUES (3, 111);
INSERT INTO `system_role_menu` VALUES (3, 112);
INSERT INTO `system_role_menu` VALUES (3, 113);
INSERT INTO `system_role_menu` VALUES (3, 114);
INSERT INTO `system_role_menu` VALUES (3, 115);
INSERT INTO `system_role_menu` VALUES (3, 500);
INSERT INTO `system_role_menu` VALUES (3, 501);
INSERT INTO `system_role_menu` VALUES (3, 1000);
INSERT INTO `system_role_menu` VALUES (3, 1001);
INSERT INTO `system_role_menu` VALUES (3, 1002);
INSERT INTO `system_role_menu` VALUES (3, 1003);
INSERT INTO `system_role_menu` VALUES (3, 1004);
INSERT INTO `system_role_menu` VALUES (3, 1005);
INSERT INTO `system_role_menu` VALUES (3, 1006);
INSERT INTO `system_role_menu` VALUES (3, 1007);
INSERT INTO `system_role_menu` VALUES (3, 1008);
INSERT INTO `system_role_menu` VALUES (3, 1009);
INSERT INTO `system_role_menu` VALUES (3, 1010);
INSERT INTO `system_role_menu` VALUES (3, 1011);
INSERT INTO `system_role_menu` VALUES (3, 1012);
INSERT INTO `system_role_menu` VALUES (3, 1013);
INSERT INTO `system_role_menu` VALUES (3, 1014);
INSERT INTO `system_role_menu` VALUES (3, 1015);
INSERT INTO `system_role_menu` VALUES (3, 1016);
INSERT INTO `system_role_menu` VALUES (3, 1017);
INSERT INTO `system_role_menu` VALUES (3, 1018);
INSERT INTO `system_role_menu` VALUES (3, 1019);
INSERT INTO `system_role_menu` VALUES (3, 1020);
INSERT INTO `system_role_menu` VALUES (3, 1021);
INSERT INTO `system_role_menu` VALUES (3, 1022);
INSERT INTO `system_role_menu` VALUES (3, 1023);
INSERT INTO `system_role_menu` VALUES (3, 1024);
INSERT INTO `system_role_menu` VALUES (3, 1025);
INSERT INTO `system_role_menu` VALUES (3, 1026);
INSERT INTO `system_role_menu` VALUES (3, 1027);
INSERT INTO `system_role_menu` VALUES (3, 1028);
INSERT INTO `system_role_menu` VALUES (3, 1029);
INSERT INTO `system_role_menu` VALUES (3, 1030);
INSERT INTO `system_role_menu` VALUES (3, 1031);
INSERT INTO `system_role_menu` VALUES (3, 1032);
INSERT INTO `system_role_menu` VALUES (3, 1033);
INSERT INTO `system_role_menu` VALUES (3, 1034);
INSERT INTO `system_role_menu` VALUES (3, 1035);
INSERT INTO `system_role_menu` VALUES (3, 1036);
INSERT INTO `system_role_menu` VALUES (3, 1037);
INSERT INTO `system_role_menu` VALUES (3, 1038);
INSERT INTO `system_role_menu` VALUES (3, 1039);
INSERT INTO `system_role_menu` VALUES (3, 1040);
INSERT INTO `system_role_menu` VALUES (3, 1041);
INSERT INTO `system_role_menu` VALUES (3, 1042);
INSERT INTO `system_role_menu` VALUES (3, 1043);
INSERT INTO `system_role_menu` VALUES (3, 1044);
INSERT INTO `system_role_menu` VALUES (3, 1045);
INSERT INTO `system_role_menu` VALUES (3, 1046);
INSERT INTO `system_role_menu` VALUES (3, 1047);
INSERT INTO `system_role_menu` VALUES (3, 1048);
INSERT INTO `system_role_menu` VALUES (3, 1049);
INSERT INTO `system_role_menu` VALUES (3, 1050);
INSERT INTO `system_role_menu` VALUES (3, 1051);
INSERT INTO `system_role_menu` VALUES (3, 1052);
INSERT INTO `system_role_menu` VALUES (3, 1053);
INSERT INTO `system_role_menu` VALUES (3, 1054);
INSERT INTO `system_role_menu` VALUES (3, 1055);
INSERT INTO `system_role_menu` VALUES (3, 1056);
INSERT INTO `system_role_menu` VALUES (3, 1057);
INSERT INTO `system_role_menu` VALUES (3, 1058);
INSERT INTO `system_role_menu` VALUES (3, 1059);
INSERT INTO `system_role_menu` VALUES (3, 1060);
INSERT INTO `system_role_menu` VALUES (3, 1061);
INSERT INTO `system_role_menu` VALUES (3, 1062);
INSERT INTO `system_role_menu` VALUES (3, 1063);
INSERT INTO `system_role_menu` VALUES (3, 1064);
INSERT INTO `system_role_menu` VALUES (3, 1065);
INSERT INTO `system_role_menu` VALUES (3, 1066);
INSERT INTO `system_role_menu` VALUES (3, 1077);
INSERT INTO `system_role_menu` VALUES (3, 1078);
INSERT INTO `system_role_menu` VALUES (3, 1079);
INSERT INTO `system_role_menu` VALUES (3, 1080);
INSERT INTO `system_role_menu` VALUES (3, 1081);
INSERT INTO `system_role_menu` VALUES (3, 1082);
INSERT INTO `system_role_menu` VALUES (3, 1083);
INSERT INTO `system_role_menu` VALUES (3, 1084);
INSERT INTO `system_role_menu` VALUES (3, 1085);
INSERT INTO `system_role_menu` VALUES (3, 1086);
INSERT INTO `system_role_menu` VALUES (3, 1087);
INSERT INTO `system_role_menu` VALUES (3, 1088);
INSERT INTO `system_role_menu` VALUES (3, 1089);
INSERT INTO `system_role_menu` VALUES (3, 1090);
INSERT INTO `system_role_menu` VALUES (3, 1091);
INSERT INTO `system_role_menu` VALUES (3, 1092);
INSERT INTO `system_role_menu` VALUES (3, 1093);
INSERT INTO `system_role_menu` VALUES (3, 1094);
INSERT INTO `system_role_menu` VALUES (3, 1095);
INSERT INTO `system_role_menu` VALUES (3, 1096);
INSERT INTO `system_role_menu` VALUES (3, 1097);
INSERT INTO `system_role_menu` VALUES (3, 1098);
INSERT INTO `system_role_menu` VALUES (3, 1099);
INSERT INTO `system_role_menu` VALUES (3, 1100);
INSERT INTO `system_role_menu` VALUES (3, 1101);
INSERT INTO `system_role_menu` VALUES (3, 1102);
INSERT INTO `system_role_menu` VALUES (3, 1103);
INSERT INTO `system_role_menu` VALUES (3, 1104);
INSERT INTO `system_role_menu` VALUES (3, 1105);
INSERT INTO `system_role_menu` VALUES (3, 1106);
INSERT INTO `system_role_menu` VALUES (3, 1107);
INSERT INTO `system_role_menu` VALUES (3, 1108);
INSERT INTO `system_role_menu` VALUES (3, 1109);
INSERT INTO `system_role_menu` VALUES (3, 1110);
INSERT INTO `system_role_menu` VALUES (3, 1111);
INSERT INTO `system_role_menu` VALUES (3, 1112);
INSERT INTO `system_role_menu` VALUES (3, 1113);
INSERT INTO `system_role_menu` VALUES (3, 1114);
INSERT INTO `system_role_menu` VALUES (3, 1115);
INSERT INTO `system_role_menu` VALUES (3, 1116);
INSERT INTO `system_role_menu` VALUES (3, 1117);
INSERT INTO `system_role_menu` VALUES (3, 1118);
INSERT INTO `system_role_menu` VALUES (3, 1119);
INSERT INTO `system_role_menu` VALUES (3, 1120);
INSERT INTO `system_role_menu` VALUES (3, 1121);
INSERT INTO `system_role_menu` VALUES (3, 1122);
INSERT INTO `system_role_menu` VALUES (3, 1123);
INSERT INTO `system_role_menu` VALUES (3, 1124);
INSERT INTO `system_role_menu` VALUES (3, 1125);
INSERT INTO `system_role_menu` VALUES (3, 1126);
INSERT INTO `system_role_menu` VALUES (3, 1127);
INSERT INTO `system_role_menu` VALUES (3, 1128);
INSERT INTO `system_role_menu` VALUES (3, 1129);
INSERT INTO `system_role_menu` VALUES (3, 1130);
INSERT INTO `system_role_menu` VALUES (3, 1131);
INSERT INTO `system_role_menu` VALUES (3, 1132);
INSERT INTO `system_role_menu` VALUES (3, 1133);
INSERT INTO `system_role_menu` VALUES (3, 1134);
INSERT INTO `system_role_menu` VALUES (3, 1135);
INSERT INTO `system_role_menu` VALUES (3, 1136);
INSERT INTO `system_role_menu` VALUES (3, 1137);
INSERT INTO `system_role_menu` VALUES (3, 1138);
INSERT INTO `system_role_menu` VALUES (3, 1139);
INSERT INTO `system_role_menu` VALUES (3, 1140);
INSERT INTO `system_role_menu` VALUES (3, 1141);
INSERT INTO `system_role_menu` VALUES (3, 1142);
INSERT INTO `system_role_menu` VALUES (3, 1143);
INSERT INTO `system_role_menu` VALUES (3, 1144);
INSERT INTO `system_role_menu` VALUES (3, 1145);
INSERT INTO `system_role_menu` VALUES (3, 1146);
INSERT INTO `system_role_menu` VALUES (3, 1147);
INSERT INTO `system_role_menu` VALUES (3, 1148);
INSERT INTO `system_role_menu` VALUES (3, 1149);
INSERT INTO `system_role_menu` VALUES (3, 1150);
INSERT INTO `system_role_menu` VALUES (3, 1151);
INSERT INTO `system_role_menu` VALUES (3, 1152);
INSERT INTO `system_role_menu` VALUES (3, 1153);
INSERT INTO `system_role_menu` VALUES (3, 1154);
INSERT INTO `system_role_menu` VALUES (3, 1155);
INSERT INTO `system_role_menu` VALUES (3, 1156);
INSERT INTO `system_role_menu` VALUES (3, 1157);
INSERT INTO `system_role_menu` VALUES (3, 1158);
INSERT INTO `system_role_menu` VALUES (3, 1159);
INSERT INTO `system_role_menu` VALUES (3, 1160);
INSERT INTO `system_role_menu` VALUES (3, 1161);
INSERT INTO `system_role_menu` VALUES (3, 1162);
INSERT INTO `system_role_menu` VALUES (3, 1163);
INSERT INTO `system_role_menu` VALUES (3, 1164);
INSERT INTO `system_role_menu` VALUES (3, 1165);
INSERT INTO `system_role_menu` VALUES (3, 1166);
INSERT INTO `system_role_menu` VALUES (3, 1167);
INSERT INTO `system_role_menu` VALUES (3, 1168);
INSERT INTO `system_role_menu` VALUES (3, 1169);
INSERT INTO `system_role_menu` VALUES (3, 1170);
INSERT INTO `system_role_menu` VALUES (3, 1171);
INSERT INTO `system_role_menu` VALUES (3, 1172);
INSERT INTO `system_role_menu` VALUES (3, 1173);
INSERT INTO `system_role_menu` VALUES (3, 1174);
INSERT INTO `system_role_menu` VALUES (3, 1175);
INSERT INTO `system_role_menu` VALUES (3, 1176);
INSERT INTO `system_role_menu` VALUES (3, 1177);
INSERT INTO `system_role_menu` VALUES (3, 1178);
INSERT INTO `system_role_menu` VALUES (3, 1179);
INSERT INTO `system_role_menu` VALUES (3, 1180);
INSERT INTO `system_role_menu` VALUES (3, 1181);
INSERT INTO `system_role_menu` VALUES (3, 1182);
INSERT INTO `system_role_menu` VALUES (3, 1183);
INSERT INTO `system_role_menu` VALUES (3, 1184);
INSERT INTO `system_role_menu` VALUES (3, 1185);
INSERT INTO `system_role_menu` VALUES (3, 1186);
INSERT INTO `system_role_menu` VALUES (3, 1187);
INSERT INTO `system_role_menu` VALUES (3, 1188);
INSERT INTO `system_role_menu` VALUES (3, 1189);
INSERT INTO `system_role_menu` VALUES (3, 1190);
INSERT INTO `system_role_menu` VALUES (3, 1191);
INSERT INTO `system_role_menu` VALUES (3, 1192);
INSERT INTO `system_role_menu` VALUES (3, 1193);
INSERT INTO `system_role_menu` VALUES (3, 1194);
INSERT INTO `system_role_menu` VALUES (3, 1195);
INSERT INTO `system_role_menu` VALUES (3, 1196);
INSERT INTO `system_role_menu` VALUES (3, 1197);
INSERT INTO `system_role_menu` VALUES (3, 1198);
INSERT INTO `system_role_menu` VALUES (3, 1199);
INSERT INTO `system_role_menu` VALUES (3, 1200);
INSERT INTO `system_role_menu` VALUES (3, 1201);
INSERT INTO `system_role_menu` VALUES (3, 1202);
INSERT INTO `system_role_menu` VALUES (3, 1203);
INSERT INTO `system_role_menu` VALUES (3, 1204);
INSERT INTO `system_role_menu` VALUES (3, 1205);
INSERT INTO `system_role_menu` VALUES (3, 1206);
INSERT INTO `system_role_menu` VALUES (3, 1207);
INSERT INTO `system_role_menu` VALUES (3, 1208);
INSERT INTO `system_role_menu` VALUES (3, 1209);
INSERT INTO `system_role_menu` VALUES (3, 1210);
INSERT INTO `system_role_menu` VALUES (3, 1211);
INSERT INTO `system_role_menu` VALUES (3, 1212);
INSERT INTO `system_role_menu` VALUES (3, 1213);
INSERT INTO `system_role_menu` VALUES (3, 1214);
INSERT INTO `system_role_menu` VALUES (3, 1215);
INSERT INTO `system_role_menu` VALUES (3, 1216);
INSERT INTO `system_role_menu` VALUES (3, 1217);
INSERT INTO `system_role_menu` VALUES (3, 1218);
INSERT INTO `system_role_menu` VALUES (3, 1219);
INSERT INTO `system_role_menu` VALUES (3, 1220);
INSERT INTO `system_role_menu` VALUES (3, 1221);
INSERT INTO `system_role_menu` VALUES (3, 1222);
INSERT INTO `system_role_menu` VALUES (4, 1);
INSERT INTO `system_role_menu` VALUES (4, 100);
INSERT INTO `system_role_menu` VALUES (4, 101);
INSERT INTO `system_role_menu` VALUES (4, 102);
INSERT INTO `system_role_menu` VALUES (4, 103);
INSERT INTO `system_role_menu` VALUES (4, 104);
INSERT INTO `system_role_menu` VALUES (4, 105);
INSERT INTO `system_role_menu` VALUES (4, 106);
INSERT INTO `system_role_menu` VALUES (4, 107);
INSERT INTO `system_role_menu` VALUES (4, 108);
INSERT INTO `system_role_menu` VALUES (4, 500);
INSERT INTO `system_role_menu` VALUES (4, 501);
INSERT INTO `system_role_menu` VALUES (4, 1000);
INSERT INTO `system_role_menu` VALUES (4, 1007);
INSERT INTO `system_role_menu` VALUES (4, 1012);
INSERT INTO `system_role_menu` VALUES (4, 1016);
INSERT INTO `system_role_menu` VALUES (4, 1020);
INSERT INTO `system_role_menu` VALUES (4, 1025);
INSERT INTO `system_role_menu` VALUES (4, 1030);
INSERT INTO `system_role_menu` VALUES (4, 1035);
INSERT INTO `system_role_menu` VALUES (4, 1036);
INSERT INTO `system_role_menu` VALUES (4, 1037);
INSERT INTO `system_role_menu` VALUES (4, 1038);
INSERT INTO `system_role_menu` VALUES (4, 1039);
INSERT INTO `system_role_menu` VALUES (4, 1040);
INSERT INTO `system_role_menu` VALUES (4, 1041);
INSERT INTO `system_role_menu` VALUES (4, 1042);
INSERT INTO `system_role_menu` VALUES (4, 1043);
INSERT INTO `system_role_menu` VALUES (4, 1044);
INSERT INTO `system_role_menu` VALUES (4, 1045);
INSERT INTO `system_role_menu` VALUES (4, 1061);
INSERT INTO `system_role_menu` VALUES (4, 1062);
INSERT INTO `system_role_menu` VALUES (4, 1063);
INSERT INTO `system_role_menu` VALUES (4, 1064);
INSERT INTO `system_role_menu` VALUES (4, 1065);
INSERT INTO `system_role_menu` VALUES (4, 1066);
INSERT INTO `system_role_menu` VALUES (4, 1077);
INSERT INTO `system_role_menu` VALUES (4, 1078);
INSERT INTO `system_role_menu` VALUES (4, 1079);
INSERT INTO `system_role_menu` VALUES (4, 1080);
INSERT INTO `system_role_menu` VALUES (4, 1081);
INSERT INTO `system_role_menu` VALUES (4, 1082);
INSERT INTO `system_role_menu` VALUES (4, 1083);
INSERT INTO `system_role_menu` VALUES (4, 1084);
INSERT INTO `system_role_menu` VALUES (4, 1085);
INSERT INTO `system_role_menu` VALUES (4, 1086);
INSERT INTO `system_role_menu` VALUES (4, 1087);
INSERT INTO `system_role_menu` VALUES (4, 1088);
INSERT INTO `system_role_menu` VALUES (4, 1089);
INSERT INTO `system_role_menu` VALUES (4, 1090);
INSERT INTO `system_role_menu` VALUES (4, 1091);
INSERT INTO `system_role_menu` VALUES (4, 1092);
INSERT INTO `system_role_menu` VALUES (4, 1093);
INSERT INTO `system_role_menu` VALUES (4, 1094);
INSERT INTO `system_role_menu` VALUES (4, 1095);
INSERT INTO `system_role_menu` VALUES (4, 1096);
INSERT INTO `system_role_menu` VALUES (4, 1097);
INSERT INTO `system_role_menu` VALUES (4, 1098);
INSERT INTO `system_role_menu` VALUES (4, 1099);
INSERT INTO `system_role_menu` VALUES (4, 1100);
INSERT INTO `system_role_menu` VALUES (4, 1101);
INSERT INTO `system_role_menu` VALUES (4, 1102);
INSERT INTO `system_role_menu` VALUES (4, 1103);
INSERT INTO `system_role_menu` VALUES (4, 1104);
INSERT INTO `system_role_menu` VALUES (4, 1105);
INSERT INTO `system_role_menu` VALUES (4, 1106);
INSERT INTO `system_role_menu` VALUES (4, 1107);
INSERT INTO `system_role_menu` VALUES (4, 1108);
INSERT INTO `system_role_menu` VALUES (4, 1109);
INSERT INTO `system_role_menu` VALUES (4, 1110);
INSERT INTO `system_role_menu` VALUES (4, 1111);
INSERT INTO `system_role_menu` VALUES (4, 1112);
INSERT INTO `system_role_menu` VALUES (4, 1113);
INSERT INTO `system_role_menu` VALUES (4, 1114);
INSERT INTO `system_role_menu` VALUES (4, 1115);
INSERT INTO `system_role_menu` VALUES (4, 1116);
INSERT INTO `system_role_menu` VALUES (4, 1117);
INSERT INTO `system_role_menu` VALUES (4, 1118);
INSERT INTO `system_role_menu` VALUES (4, 1119);
INSERT INTO `system_role_menu` VALUES (4, 1120);
INSERT INTO `system_role_menu` VALUES (4, 1121);
INSERT INTO `system_role_menu` VALUES (4, 1122);
INSERT INTO `system_role_menu` VALUES (4, 1123);
INSERT INTO `system_role_menu` VALUES (4, 1124);
INSERT INTO `system_role_menu` VALUES (4, 1125);
INSERT INTO `system_role_menu` VALUES (4, 1126);
INSERT INTO `system_role_menu` VALUES (4, 1127);
INSERT INTO `system_role_menu` VALUES (4, 1128);
INSERT INTO `system_role_menu` VALUES (4, 1129);
INSERT INTO `system_role_menu` VALUES (4, 1130);
INSERT INTO `system_role_menu` VALUES (4, 1131);
INSERT INTO `system_role_menu` VALUES (4, 1132);
INSERT INTO `system_role_menu` VALUES (4, 1133);
INSERT INTO `system_role_menu` VALUES (4, 1134);
INSERT INTO `system_role_menu` VALUES (4, 1135);
INSERT INTO `system_role_menu` VALUES (4, 1136);
INSERT INTO `system_role_menu` VALUES (4, 1137);
INSERT INTO `system_role_menu` VALUES (4, 1138);
INSERT INTO `system_role_menu` VALUES (4, 1139);
INSERT INTO `system_role_menu` VALUES (4, 1140);
INSERT INTO `system_role_menu` VALUES (4, 1141);
INSERT INTO `system_role_menu` VALUES (4, 1142);
INSERT INTO `system_role_menu` VALUES (4, 1143);
INSERT INTO `system_role_menu` VALUES (4, 1144);
INSERT INTO `system_role_menu` VALUES (4, 1145);
INSERT INTO `system_role_menu` VALUES (4, 1146);
INSERT INTO `system_role_menu` VALUES (4, 1147);
INSERT INTO `system_role_menu` VALUES (4, 1148);
INSERT INTO `system_role_menu` VALUES (4, 1149);
INSERT INTO `system_role_menu` VALUES (4, 1150);
INSERT INTO `system_role_menu` VALUES (4, 1151);
INSERT INTO `system_role_menu` VALUES (4, 1152);
INSERT INTO `system_role_menu` VALUES (4, 1153);
INSERT INTO `system_role_menu` VALUES (4, 1154);
INSERT INTO `system_role_menu` VALUES (4, 1155);
INSERT INTO `system_role_menu` VALUES (4, 1156);
INSERT INTO `system_role_menu` VALUES (4, 1157);
INSERT INTO `system_role_menu` VALUES (4, 1158);
INSERT INTO `system_role_menu` VALUES (4, 1159);
INSERT INTO `system_role_menu` VALUES (4, 1160);
INSERT INTO `system_role_menu` VALUES (4, 1161);
INSERT INTO `system_role_menu` VALUES (4, 1162);
INSERT INTO `system_role_menu` VALUES (4, 1163);
INSERT INTO `system_role_menu` VALUES (4, 1164);
INSERT INTO `system_role_menu` VALUES (4, 1165);
INSERT INTO `system_role_menu` VALUES (4, 1166);
INSERT INTO `system_role_menu` VALUES (4, 1167);
INSERT INTO `system_role_menu` VALUES (4, 1168);
INSERT INTO `system_role_menu` VALUES (4, 1169);
INSERT INTO `system_role_menu` VALUES (4, 1170);
INSERT INTO `system_role_menu` VALUES (4, 1171);
INSERT INTO `system_role_menu` VALUES (4, 1172);
INSERT INTO `system_role_menu` VALUES (4, 1173);
INSERT INTO `system_role_menu` VALUES (4, 1174);
INSERT INTO `system_role_menu` VALUES (4, 1175);
INSERT INTO `system_role_menu` VALUES (4, 1176);
INSERT INTO `system_role_menu` VALUES (4, 1177);
INSERT INTO `system_role_menu` VALUES (4, 1178);
INSERT INTO `system_role_menu` VALUES (4, 1179);
INSERT INTO `system_role_menu` VALUES (4, 1180);
INSERT INTO `system_role_menu` VALUES (4, 1181);
INSERT INTO `system_role_menu` VALUES (4, 1182);
INSERT INTO `system_role_menu` VALUES (4, 1183);
INSERT INTO `system_role_menu` VALUES (4, 1184);
INSERT INTO `system_role_menu` VALUES (4, 1185);
INSERT INTO `system_role_menu` VALUES (4, 1186);
INSERT INTO `system_role_menu` VALUES (4, 1187);
INSERT INTO `system_role_menu` VALUES (4, 1188);
INSERT INTO `system_role_menu` VALUES (4, 1189);
INSERT INTO `system_role_menu` VALUES (4, 1190);
INSERT INTO `system_role_menu` VALUES (4, 1191);
INSERT INTO `system_role_menu` VALUES (4, 1192);
INSERT INTO `system_role_menu` VALUES (4, 1193);
INSERT INTO `system_role_menu` VALUES (4, 1194);
INSERT INTO `system_role_menu` VALUES (4, 1195);
INSERT INTO `system_role_menu` VALUES (4, 1196);
INSERT INTO `system_role_menu` VALUES (4, 1197);
INSERT INTO `system_role_menu` VALUES (4, 1198);
INSERT INTO `system_role_menu` VALUES (4, 1199);
INSERT INTO `system_role_menu` VALUES (4, 1200);
INSERT INTO `system_role_menu` VALUES (4, 1201);
INSERT INTO `system_role_menu` VALUES (4, 1202);
INSERT INTO `system_role_menu` VALUES (4, 1203);
INSERT INTO `system_role_menu` VALUES (4, 1211);
INSERT INTO `system_role_menu` VALUES (4, 1212);
INSERT INTO `system_role_menu` VALUES (4, 1213);
INSERT INTO `system_role_menu` VALUES (4, 1214);
INSERT INTO `system_role_menu` VALUES (4, 1215);
INSERT INTO `system_role_menu` VALUES (4, 1216);
INSERT INTO `system_role_menu` VALUES (4, 1217);
INSERT INTO `system_role_menu` VALUES (4, 1218);
INSERT INTO `system_role_menu` VALUES (4, 1219);
INSERT INTO `system_role_menu` VALUES (4, 1220);
INSERT INTO `system_role_menu` VALUES (4, 1221);
INSERT INTO `system_role_menu` VALUES (4, 1222);

-- ----------------------------
-- Table structure for system_user
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user`  (
                                `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
                                `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户账号',
                                `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户昵称',
                                `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
                                `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户邮箱',
                                `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '手机号码',
                                `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
                                `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '头像地址',
                                `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '密码',
                                `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
                                `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
                                `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '最后登录IP',
                                `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
                                `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_user
-- ----------------------------
INSERT INTO `system_user` VALUES (1, 100, 'admin', '超级管理员', '00', 'support@qiantong.tech', '15888888888', '0', NULL, '$2a$10$DY8QLFujLI.kuAk9R75mn.0TR8glHXCRUss5/B758pEzf1uG7.wD.', '0', '0', '117.89.24.233', '2026-10-22 20:22:21', '吴同', '2026-04-20 09:32:53', '超级管理员', '2026-10-22 20:22:20', '管理员');
INSERT INTO `system_user` VALUES (2, 100, 'qOntology', '吴同', '00', 'support@qiantong.tech', '18888888888', '0', NULL, '$2a$10$6snwwaFipC2kqvFC763IIuV4BNOt8jJNH83x8CI4/2ShbiX96J3WO', '0', '0', '192.168.0.72', '2026-11-06 18:30:29', '吴同', '2026-06-03 14:48:53', '超级管理员', '2026-11-06 18:30:29', NULL);

-- ----------------------------
-- Table structure for system_user_post
-- ----------------------------
DROP TABLE IF EXISTS `system_user_post`;
CREATE TABLE `system_user_post`  (
                                     `user_id` bigint NOT NULL COMMENT '用户ID',
                                     `post_id` bigint NOT NULL COMMENT '岗位ID',
                                     PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_user_post
-- ----------------------------
INSERT INTO `system_user_post` VALUES (1, 1);
INSERT INTO `system_user_post` VALUES (2, 2);

-- ----------------------------
-- Table structure for system_user_role
-- ----------------------------
DROP TABLE IF EXISTS `system_user_role`;
CREATE TABLE `system_user_role`  (
                                     `user_id` bigint NOT NULL COMMENT '用户ID',
                                     `role_id` bigint NOT NULL COMMENT '角色ID',
                                     PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_user_role
-- ----------------------------
INSERT INTO `system_user_role` VALUES (1, 1);
INSERT INTO `system_user_role` VALUES (2, 2);

SET FOREIGN_KEY_CHECKS = 1;
