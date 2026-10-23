/*
 Navicat Premium Dump SQL

 Source Server         : qOntology线上
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : 110.42.51.148:33067
 Source Schema         : qontology

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 22/10/2026 20:32:26
*/


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_api_key
-- ----------------------------
DROP TABLE IF EXISTS `ai_api_key`;
CREATE TABLE `ai_api_key`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                               `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                               `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
                               `api_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '秘钥',
                               `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台',
                               `url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API地址',
                               `platform_tag` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台标签',
                               `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                               `deploy_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部署方式',
                               `status` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '状态',
                               `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                               `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                               `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                               `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                               `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'API秘钥' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_api_key
-- ----------------------------
INSERT INTO `ai_api_key` VALUES (1, 1001, '通义千问', NULL, 'TongYi', NULL, '1,2,3,4,5,6', '通义千问提供的模型。', '2', 0, 1, 0, '吴同', 2, '2026-04-29 09:27:03', '吴同', NULL, '2026-05-07 15:36:00', NULL);
INSERT INTO `ai_api_key` VALUES (2, 1001, 'DeepSeek', NULL, 'DeepSeek', NULL, '1,2,3', '深度求索提供的模型，例如 deepseek-chat、deepseek-coder 。', '2', 0, 1, 1, '吴同', 2, '2026-04-29 09:27:03', '吴同', NULL, '2026-04-29 10:28:58', NULL);
INSERT INTO `ai_api_key` VALUES (3, 1001, 'Ollama', NULL, 'Ollama', NULL, '1,2,3', 'ollama', '1', 0, 1, 1, '吴同', 2, '2026-04-29 09:27:03', '吴同', NULL, '2026-04-29 10:29:04', NULL);
INSERT INTO `ai_api_key` VALUES (4, 1001, 'OpenAI', 'ldfef#01', 'OpenAI', 'http://192.144.173.87:7001', '1,2,3,4,5,6', '符合 openai 规范的模型', '1', 2, 1, 1, '吴同', 2, '2026-04-21 13:57:13', '吴同', NULL, '2026-04-21 16:33:47', NULL);

-- ----------------------------
-- Table structure for ai_model
-- ----------------------------
DROP TABLE IF EXISTS `ai_model`;
CREATE TABLE `ai_model`  (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                             `key_id` bigint(20) NULL DEFAULT NULL COMMENT '秘钥id',
                             `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型名称',
                             `model` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模型标志',
                             `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '平台',
                             `type` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '类型',
                             `sort` int(11) NULL DEFAULT NULL COMMENT '排序值',
                             `status` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '状态',
                             `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                             `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                             `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                             `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                             `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                             `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI 模型' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_model
-- ----------------------------

-- ----------------------------
-- Table structure for app_attribute_mapping
-- ----------------------------
DROP TABLE IF EXISTS `app_attribute_mapping`;
CREATE TABLE `app_attribute_mapping`  (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                          `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                          `task_id` bigint(20) NOT NULL COMMENT '任务id',
                                          `table_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名',
                                          `table_comment` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表显示名称',
                                          `field_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名',
                                          `field_comment` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段显示名称',
                                          `attribute_id` bigint(20) NULL DEFAULT NULL COMMENT '属性id',
                                          `attribute_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '属性名称',
                                          `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                          `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                          `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                          `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                          `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '属性映射' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_attribute_mapping
-- ----------------------------
INSERT INTO `app_attribute_mapping` VALUES (1, 1001, 1, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (2, 1001, 1, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (3, 1001, 2, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (4, 1001, 2, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (5, 1001, 3, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (6, 1001, 3, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (7, 1001, 4, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (8, 1001, 4, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (9, 1001, 5, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (10, 1001, 5, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (11, 1001, 6, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (12, 1001, 6, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (13, 1001, 7, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (14, 1001, 7, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (15, 1001, 8, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (16, 1001, 8, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (17, 1001, 9, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (18, 1001, 9, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (19, 1001, 10, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (20, 1001, 10, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (21, 1001, 11, 'basic_project', '项目管理', 'id', 'ID', 110, '供应商编码', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_attribute_mapping` VALUES (22, 1001, 11, 'basic_project', '项目管理', 'name', '项目名称', 109, '供应商名称', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);

-- ----------------------------
-- Table structure for app_relation_mapping
-- ----------------------------
DROP TABLE IF EXISTS `app_relation_mapping`;
CREATE TABLE `app_relation_mapping`  (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                         `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                         `task_id` bigint(20) NOT NULL COMMENT '任务id',
                                         `table_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名',
                                         `table_comment` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表显示名称',
                                         `field_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字段名',
                                         `field_comment` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段显示名称',
                                         `relation` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '关系',
                                         `relation_table` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联表',
                                         `relation_table_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联表名称',
                                         `relation_field` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联表字段',
                                         `relation_name_field` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '关联表实体字段',
                                         `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                         `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                         `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                         `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                         `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                         PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关系映射' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_relation_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for app_relation_mapping_middle
-- ----------------------------
DROP TABLE IF EXISTS `app_relation_mapping_middle`;
CREATE TABLE `app_relation_mapping_middle`  (
                                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                                `relation_id` bigint(20) NOT NULL COMMENT '关系表id',
                                                `table_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '中间表名称',
                                                `table_field` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联源表字段',
                                                `relation_field` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联目标表字段',
                                                `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                                `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                                `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                                `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                                `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                                `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                                PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '关系映射中间表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_relation_mapping_middle
-- ----------------------------

-- ----------------------------
-- Table structure for app_schema_mapping
-- ----------------------------
DROP TABLE IF EXISTS `app_schema_mapping`;
CREATE TABLE `app_schema_mapping`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                       `task_id` bigint(20) NOT NULL COMMENT '任务id',
                                       `table_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名',
                                       `table_comment` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表显示名称',
                                       `entity_name_field` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '实体名称列',
                                       `primary_key` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键',
                                       `entity_time_field` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '增量依据字段',
                                       `last_date_time` datetime NULL DEFAULT NULL COMMENT '最新数据时间',
                                       `schema_id` bigint(20) NULL DEFAULT NULL COMMENT '对象类型id',
                                       `schema_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '对象类型名称',
                                       `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                       `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                       `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                       `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                       `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象类型映射' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_schema_mapping
-- ----------------------------
INSERT INTO `app_schema_mapping` VALUES (1, 1001, 1, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (2, 1001, 2, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (3, 1001, 3, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (4, 1001, 4, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (5, 1001, 5, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (6, 1001, 6, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (7, 1001, 7, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (8, 1001, 8, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (9, 1001, 9, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (10, 1001, 10, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);
INSERT INTO `app_schema_mapping` VALUES (11, 1001, 11, 'basic_project', '项目管理', 'name', 'id', NULL, NULL, 10, '供应商', 0, 0, '吴同', 2, '2026-10-23 11:14:59', '吴同', 2, '2026-10-23 11:14:59', NULL);

-- ----------------------------
-- Table structure for app_struct_task
-- ----------------------------
DROP TABLE IF EXISTS `app_struct_task`;
CREATE TABLE `app_struct_task`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                    `project_id` bigint(20) NULL DEFAULT NULL COMMENT '项目编号',
                                    `project_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目名称',
                                    `project_version_id` bigint(20) NULL DEFAULT NULL COMMENT '项目版本编号',
                                    `project_version_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目版本名称',
                                    `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务名称',
                                    `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '任务状态;0：未执行，1：进行中：2：已完成',
                                    `publish_status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '发布状态;0：未发布，1：已发布',
                                    `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
                                    `publisher_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人id',
                                    `publish_by` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发布人',
                                    `update_type` tinyint(4) NULL DEFAULT 0 COMMENT '更新类型;0：全量更新，1：增量更新',
                                    `update_frequency` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新频率',
                                    `update_status` tinyint(4) NULL DEFAULT 1 COMMENT '定时更新状态（0正常 1暂停）',
                                    `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
                                    `datasource_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源名称',
                                    `description` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                                    `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                    `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                    `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                    `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                    `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '结构化抽取任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of app_struct_task
-- ----------------------------
INSERT INTO `app_struct_task` VALUES (1, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '采购清单数据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从采购、仓储相关单据和文档里，抽取采购 - 仓储协同相关的实体、属性和关系，产出符合本体模型的数据，支撑采购仓储知识图谱搭建。', 0, 0, '吴同', 2, '2026-10-23 11:02:31', '吴同', 2, '2026-10-23 11:14:59', '从采购、仓储相关单据和文档里，抽取采购 - 仓储协同相关的实体、属性和关系，产出符合本体模型的数据，支撑采购仓储知识图谱搭建。');
INSERT INTO `app_struct_task` VALUES (2, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '供应商基础信息抽取任务', 3, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从供应商档案、资质文档中抽取供应商基础信息、资质信息、联系人信息，标准化供应商实体数据，完善供应链供应商知识图谱。', 0, 0, '吴同', 2, '2026-10-23 11:02:32', '吴同', 2, '2026-10-23 11:14:59', '从供应商档案、资质文档中抽取供应商基础信息、资质信息、联系人信息，标准化供应商实体数据，完善供应链供应商知识图谱。');
INSERT INTO `app_struct_task` VALUES (3, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '采购订单数据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从采购订单单据中抽取订单、物料、供应商、数量、金额、交期信息，构建采购订单实体及关联关系，支撑采购业务链路图谱构建。', 0, 0, '吴同', 2, '2026-10-23 11:02:33', '吴同', 2, '2026-10-23 11:14:59', '从采购订单单据中抽取订单、物料、供应商、数量、金额、交期信息，构建采购订单实体及关联关系，支撑采购业务链路图谱构建。');
INSERT INTO `app_struct_task` VALUES (4, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '入库单数据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从入库单据中抽取入库单号、物料、批次、入库数量、库位、入库时间信息，建立入库记录与采购订单、库存之间关联关系。', 0, 0, '吴同', 2, '2026-10-23 11:02:34', '吴同', 2, '2026-10-23 11:14:59', '从入库单据中抽取入库单号、物料、批次、入库数量、库位、入库时间信息，建立入库记录与采购订单、库存之间关联关系。');
INSERT INTO `app_struct_task` VALUES (5, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '出库单数据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从出库单据提取出库单号、物料、出库数量、领用部门、出库时间，构建出库实体，关联库存台账，还原物料出库流转链路。', 0, 0, '吴同', 2, '2026-10-23 11:02:35', '吴同', 2, '2026-10-23 11:14:59', '从出库单据提取出库单号、物料、出库数量、领用部门、出库时间，构建出库实体，关联库存台账，还原物料出库流转链路。');
INSERT INTO `app_struct_task` VALUES (6, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '库存台账数据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从库存台账抽取物料库存、库位、可用数量、冻结数量、盘点信息，建立物料库存实体，支撑仓储库存态势知识查询。', 0, 0, '吴同', 2, '2026-10-23 11:02:36', '吴同', 2, '2026-10-23 11:14:59', '从库存台账抽取物料库存、库位、可用数量、冻结数量、盘点信息，建立物料库存实体，支撑仓储库存态势知识查询。');
INSERT INTO `app_struct_task` VALUES (7, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '物料基础信息抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从物料档案抽取物料编码、物料名称、规格型号、物料分类、单位、安全库存等基础属性，构建物料标准本体数据。', 0, 0, '吴同', 2, '2026-10-23 11:02:37', '吴同', 2, '2026-10-23 11:14:59', '从物料档案抽取物料编码、物料名称、规格型号、物料分类、单位、安全库存等基础属性，构建物料标准本体数据。');
INSERT INTO `app_struct_task` VALUES (8, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '采购合同信息抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从采购合同文档提取合同编号、甲乙双方、合同金额、履约周期、付款条款，构建采购合同实体与关联关系。', 0, 0, '吴同', 2, '2026-10-23 11:02:38', '吴同', 2, '2026-10-23 11:14:59', '从采购合同文档提取合同编号、甲乙双方、合同金额、履约周期、付款条款，构建采购合同实体与关联关系。');
INSERT INTO `app_struct_task` VALUES (9, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '质检单据抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从来料质检单抽取质检单号、物料、检验结果、不合格原因、检验人，建立质检实体，关联入库、采购订单数据。', 0, 0, '吴同', 2, '2026-10-23 11:02:39', '吴同', 2, '2026-10-23 11:14:59', '从来料质检单抽取质检单号、物料、检验结果、不合格原因、检验人，建立质检实体，关联入库、采购订单数据。');
INSERT INTO `app_struct_task` VALUES (10, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '库位信息抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从仓库库位档案抽取仓库、库区、库位编码、库位类型、存储限制信息，构建仓储空间实体，支撑仓储位置关系图谱。', 0, 0, '吴同', 2, '2026-10-23 11:02:40', '吴同', 2, '2026-10-23 11:14:59', '从仓库库位档案抽取仓库、库区、库位编码、库位类型、存储限制信息，构建仓储空间实体，支撑仓储位置关系图谱。');
INSERT INTO `app_struct_task` VALUES (11, 1001, 1, '供应链采购与仓储协同本体', 1, 'v1.0.0', '供应商履约评价抽取任务', 2, 0, NULL, NULL, NULL, 0, '0 0 0 * * ?', 1, 1, '测试数据', '从供应商履约评价记录中抽取交付准时率、质量合格率、异常扣分、评价等级等信息，构建供应商评价实体，支撑供应商信用知识图谱。', 0, 0, '吴同', 2, '2026-10-23 11:02:41', '吴同', 2, '2026-10-23 11:14:59', '从供应商履约评价记录中抽取交付准时率、质量合格率、异常扣分、评价等级等信息，构建供应商评价实体，支撑供应商信用知识图谱。');

-- ----------------------------
-- Table structure for app_task_log
-- ----------------------------
DROP TABLE IF EXISTS `app_task_log`;
CREATE TABLE `app_task_log`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区ID',
                                 `task_id` bigint(20) NOT NULL COMMENT '任务ID',
                                 `task_type` tinyint(4) NOT NULL COMMENT '任务类型;0结构化,1非结构化',
                                 `task_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                                 `status` tinyint(4) NOT NULL COMMENT '执行状态;0失败,1成功',
                                 `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误消息',
                                 `start_time` datetime NULL DEFAULT NULL COMMENT '执行开始时间',
                                 `end_time` datetime NULL DEFAULT NULL COMMENT '执行结束时间',
                                 `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                 `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                 `creator_id` bigint(20) NULL DEFAULT NULL,
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                 `updater_id` bigint(20) NULL DEFAULT NULL,
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_app_task_log_task`(`task_id`, `task_type`) USING BTREE,
                                 INDEX `idx_app_task_log_start_time`(`start_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '抽取任务执行日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_task_log
-- ----------------------------

-- ----------------------------
-- Table structure for app_task_log_detail
-- ----------------------------
DROP TABLE IF EXISTS `app_task_log_detail`;
CREATE TABLE `app_task_log_detail`  (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                        `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区ID',
                                        `log_id` bigint(20) NOT NULL COMMENT '执行日志ID',
                                        `step` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务执行步骤',
                                        `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                        `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                        `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                        `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                        `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `idx_app_task_log_detail_log`(`log_id`, `create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '抽取任务执行日志详情' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_task_log_detail
-- ----------------------------

-- ----------------------------
-- Table structure for app_unstruct_task
-- ----------------------------
DROP TABLE IF EXISTS `app_unstruct_task`;
CREATE TABLE `app_unstruct_task`  (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                      `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区ID',
                                      `name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
                                      `ttl_file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'TTL文件名称',
                                      `ttl_file_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'TTL文件存储地址',
                                      `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '任务状态;0未执行,1执行中,2已执行,3失败,4队列中',
                                      `publish_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发布状态;0未发布,1已发布',
                                      `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
                                      `publisher_id` bigint(20) NULL DEFAULT NULL COMMENT '发布人ID',
                                      `publish_by` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布人',
                                      `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                      `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                      `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                      `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                      `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
                                      `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_app_unstruct_task_status`(`status`) USING BTREE,
                                      INDEX `idx_app_unstruct_task_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '非结构化抽取任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_unstruct_task
-- ----------------------------
INSERT INTO `app_unstruct_task` VALUES (1, 1001, '原材料采购合同数据抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 12:00:01', '从原材料采购合同中抽取合同、物料、供应商、金额、交付条款等信息，构建合同实体与关联关系，支撑供应链知识图谱构建。');
INSERT INTO `app_unstruct_task` VALUES (2, 1001, '供应商资质文档抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:59', '从供应商资质文档抽取资质证书、有效期、经营范围、认证信息，构建供应商资质实体，完善供应商知识图谱。');
INSERT INTO `app_unstruct_task` VALUES (3, 1001, '来料检验报告抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:58', '从来料检验报告抽取物料信息、检验项、检验结果、不合格说明，建立质检实体，关联采购单据。');
INSERT INTO `app_unstruct_task` VALUES (4, 1001, '仓储作业规程抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 3, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:58', '从仓储作业规程文档提取入库、出库、盘点作业规则、操作要求，构建仓储流程知识实体。');
INSERT INTO `app_unstruct_task` VALUES (5, 1001, '供应商履约评价文档抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:56', '从供应商履约评价文档抽取交付、质量、服务评分及评价说明，生成供应商评价知识。');
INSERT INTO `app_unstruct_task` VALUES (6, 1001, '物料技术规格书抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:55', '从物料技术规格书提取物料参数、性能指标、技术标准，构建物料技术属性知识。');
INSERT INTO `app_unstruct_task` VALUES (7, 1001, '采购变更单文档抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:55', '从采购变更单提取变更原因、变更内容、调整后的数量与交期，记录采购变更关系。');
INSERT INTO `app_unstruct_task` VALUES (8, 1001, '仓库应急预案抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:54', '从仓库应急预案文档提取风险类型、处置流程、责任人，构建仓储应急知识图谱。');
INSERT INTO `app_unstruct_task` VALUES (9, 1001, '采购谈判纪要抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:54', '从采购谈判纪要提取谈判对象、价格、交付约定、附加条款，沉淀采购谈判知识。');
INSERT INTO `app_unstruct_task` VALUES (10, 1001, '库存盘点报告抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:53', '从库存盘点报告抽取盘点物料、账实差异、差异原因及处理意见，构建盘点相关知识。');
INSERT INTO `app_unstruct_task` VALUES (11, 1001, '运输交付单文档抽取', 'ces.ttl', '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', 2, 0, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-23 11:38:51', '吴同', 2, '2026-10-23 11:59:52', '从运输交付单提取承运方、物料、运输时间、签收信息，搭建物流交付链路知识。');
INSERT INTO `app_unstruct_task` VALUES (12, 1001, '供应链采购与仓储协同抽取', 'ces.ttl', '/2026/10/23/6adaee20d9e0784f1cbeb76a.ttl', 2, 0, '2026-10-23 13:22:42', 2, '吴同', 1, 0, '吴同', 2, '2026-10-23 13:19:25', '吴同', NULL, '2026-10-23 13:22:53', '从采购、仓储相关文本中抽取协同节点、物料、单据、动作、异常等关键信息并结构化输出，支撑流程校验与风险识别。');
INSERT INTO `app_unstruct_task` VALUES (13, 1001, '供应链采购与仓储协同业务实例描述文档抽取', 'ces.ttl', '/2026/10/23/6adb039be4b07a35261640c9.ttl', 2, 1, '2026-10-23 14:56:52', 2, '吴同', 1, 0, '吴同', 2, '2026-10-23 14:50:07', '吴同', NULL, '2026-10-23 14:56:52', '该文档为供应链采购仓储业务自然语言样例，依托 ces.txt 本体抽取，包含多家企业、人员、多笔订单、物料、出入库、物流、质检场景，可从中抽取实体、属性、关系三元组，附带本体抽取映射说明。');

-- ----------------------------
-- Table structure for app_unstruct_task_doc_rel
-- ----------------------------
DROP TABLE IF EXISTS `app_unstruct_task_doc_rel`;
CREATE TABLE `app_unstruct_task_doc_rel`  (
                                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                              `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区ID',
                                              `task_id` bigint(20) NOT NULL COMMENT '任务ID',
                                              `doc_id` bigint(20) NOT NULL COMMENT '文件ID',
                                              `doc_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
                                              `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                              `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                              `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                              `creator_id` bigint(20) NULL DEFAULT NULL,
                                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                              `updater_id` bigint(20) NULL DEFAULT NULL,
                                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                              PRIMARY KEY (`id`) USING BTREE,
                                              UNIQUE INDEX `uk_app_unstruct_task_doc_rel`(`task_id`, `doc_id`) USING BTREE,
                                              INDEX `idx_app_unstruct_task_doc_rel_task`(`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务文件关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_unstruct_task_doc_rel
-- ----------------------------
INSERT INTO `app_unstruct_task_doc_rel` VALUES (1, 1001, 1, 7, '原材料采购合同模板.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:04', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (2, 1001, 2, 8, '供应商资质文件.pdf', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:05', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (3, 1001, 3, 9, '来料检验报告.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:06', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (4, 1001, 4, 10, '仓储作业规程.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:07', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (5, 1001, 5, 11, '供应商履约评价表.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:07', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (6, 1001, 6, 12, '物料技术规格书.pdf', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:08', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (7, 1001, 7, 13, '采购变更审批单.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:09', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (8, 1001, 8, 14, '仓库应急预案.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:10', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (9, 1001, 9, 15, '采购谈判纪要.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:11', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (10, 1001, 10, 16, '库存盘点报告.docx', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:12', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (11, 1001, 11, 17, '运输交付签收单.pdf', 1, 0, '吴同', 2, '2026-10-23 11:55:26', '吴同', 2, '2026-10-23 13:27:13', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (12, 1001, 12, 13, '供应链采购与仓储协同文档.docx', 1, 0, '吴同', 2, '2026-10-23 13:19:25', '吴同', NULL, '2026-10-23 13:19:25', NULL);
INSERT INTO `app_unstruct_task_doc_rel` VALUES (13, 1001, 13, 14, '供应链采购与仓储协同业务实例描述文档.docx', 1, 0, '吴同', 2, '2026-10-23 14:50:07', '吴同', NULL, '2026-10-23 14:50:07', NULL);

-- ----------------------------
-- Table structure for app_unstruct_task_text
-- ----------------------------
DROP TABLE IF EXISTS `app_unstruct_task_text`;
CREATE TABLE `app_unstruct_task_text`  (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                           `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区ID',
                                           `task_id` bigint(20) NOT NULL COMMENT '任务ID',
                                           `doc_id` bigint(20) NOT NULL COMMENT '文件ID',
                                           `paragraph_index` int(11) NOT NULL COMMENT '段落标识',
                                           `text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文字内容',
                                           `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                           `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                           `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                           `creator_id` bigint(20) NULL DEFAULT NULL,
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                           `updater_id` bigint(20) NULL DEFAULT NULL,
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `idx_app_unstruct_task_text_task`(`task_id`, `doc_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '任务文件段落关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_unstruct_task_text
-- ----------------------------
INSERT INTO `app_unstruct_task_text` VALUES (1, 1001, 12, 13, 1, '供应链采购与仓储协同本体\n本体定义参考文档（可抽取复用版）\n版本：v1.0.0　　标识符：1　　来源：ces.txt（RDF/OWL 本体定义）\n业务实例样例\n本节为供应链采购仓储业务真实业务描述样例，用于知识图谱实例抽取与复用测试。以下各条均给出采购、到货、入库、出库、物流与收货环节的实例描述，可供抽取测试时直接作为输入文本使用。\n东南装备有限公司是一家装备制造企业，属于采购方，统一社会信用代码为91320100MA1NXH7Y2R，采购负责人为王明，收货地址为南京市江宁区科创路88号。\n恒顺金属材料有限公司是供应商，统一社会信用代码为91320115MA04F2KD3T，供应范围为各类金属板材，联系人是刘经理，联系电话为13800138101。\n东南装备有限公司向恒顺金属材料有限公司下达编号PO-20261001的采购订单；该采购订单订单日期为2026-10-01，约定交货日期为2026-10-15，结算方式为月结60天。\n这份采购订单指定恒顺金属材料有限公司作为供应商，采购物料为304不锈钢板材。\n恒顺金属材料有限公司向东南装备有限公司提供304不锈钢板材这种物料。\n物料到货后生成入库单，入库单号GRN-20261005，入库日期为2026-10-05；该入库单对应采购订单PO-20261001，记录入库物料304不锈钢板材，仓库名称为江宁原料仓，入库数量为500千克，计量单位为千克，验收结果为合格。\n后续物料出库生成出库单，出库单号GIN-20261010，出库日期为2026-10-10；迅捷物流有限公司是物流服务商，由迅捷物流有限公司承运出库单GIN-20261010。\n出库单GIN-20261010记录出库物料304不锈钢板材，该出库单指定收货方为东南装备有限公司。\n传动轴零部件可以组成减速器成品。¹©Ó¦Á´²É¹ºÓë²Ö´¢Ð­Í¬±¾Ìå±¾Ìå¶¨Òå²Î¿¼ÎÄµµ£¨¿É³éÈ¡¸´ÓÃ°æ£©°æ±¾£ºv1.0.0±êÊ¶·û£º1À´Ô´£ºces.txt£¨RDF/OWL±¾Ìå¶¨Òå£©', 1, 0, '吴同', NULL, '2026-10-23 13:22:21', NULL, NULL, '2026-10-23 13:22:21', NULL);
INSERT INTO `app_unstruct_task_text` VALUES (2, 1001, 13, 14, 1, '供应链采购与仓储协同业务实例描述文档\n（三元组可抽取版）\n1 文档说明\n1.1 文档用途\n本文档提供供应链采购与仓储业务的自然语言业务描述，供知识图谱三元组抽取与复用测试使用。文档中的实体、属性与关系均可对照《供应链采购与仓储协同本体》（ces.txt）中定义的类、数据属性与对象关系进行抽取与映射，形成结构化三元组。\n1.2 数据说明\n以下均为示例业务数据，覆盖本体定义的全部实体类型（组织、供应商、采购方、物流服务商、物料、原材料、零部件、成品、采购订单、入库单、出库单、仓库、人员、员工、库存记录、运输任务、质量检验记录）及主要对象关系。正文中出现的公司名称、统一社会信用代码、单号、日期与标准编号均为演示样例，仅用于抽取验证。\n2 组织主数据\n2.1 采购方\n东南装备有限公司是一家装备制造企业，属于采购方，组织编码为ORG-0001，统一社会信用代码为91320100MA1NXH7Y2R，采购区域为江苏省南京市，采购部门为采购部，采购负责人为王明，联系电话为025-88990001，电子邮箱为purchase@dnequip.com，收货地址为南京市江宁区科创路88号，结算方式为月结60天，合作状态为合作中。\n2.2 供应商\n恒顺金属材料有限公司是供应商，供应商编码为SUP-0001，统一社会信用代码为91320115MA04F2KD3T，供应商类别为原材料供应商，供应范围为各类金属板材，联系人为刘经理，联系电话为13800138101，结算方式为月结60天，合作开始日期为2024年3月1日，风险评分为85，合作状态为合作中。\n华东紧固件有限公司是供应商，供应商编码为SUP-0002，统一社会信用代码为91320106MA1XC1P2Q9，供应商类别为零部件供应商，供应范围为紧固件类零部件，联系人为赵工，联系电话为13900000101，结算方式为月结30天，合作状态为合作中。\n2.3 物流服务商\n迅捷物流有限公司是物流服务商，服务商编码为LSP-0001，统一社会信用代码为91320113MA1Y8LQ2KX，运输方式为公路运输，服务区域为江苏省及周边，运输资质为道路运输经营许可证，联系人为陈师傅，联系电话为13900000002，可投入车辆数量为25辆，货损率为0.2%，合作状态为合作中。\n2.4 仓库\n江宁原料仓是仓库，仓库编码为WH-001，地址为南京市江宁区科创路88号，仓库类型为原料仓，库容面积为5000平方米，仓库负责人为王明。\n江宁成品仓是仓库，仓库编码为WH-002，地址为南京市江宁区科创路90号，仓库类型为成品仓，仓库负责人为李工。\n2.5 员工\n王明是东南装备有限公司的员工，员工编码为EMP-001，岗位为采购经理，所属部门为采购部，联系电话为13800000001。\n刘经理是恒顺金属材料有限公司的员工，员工编码为EMP-002，岗位为销售经理，所属部门为销售部，联系电话为13800138101。\n李工是东南装备有限公司的员工，员工编码为EMP-003，岗位为仓储专员，所属部门为仓储部，联系电话为13900000003。\n张检是东南装备有限公司的员工，员工编码为EMP-004，岗位为质检员，所属部门为质量部，联系电话为13900000004。\n3 物料主数据\n3.1 原材料\n304不锈钢板材是原材料，物料编码为MAT-001，原材料编码为RAW-001，材料类别为金属板材，材料牌号为304，规格为1.5mm×1m×2m，纯度99.8%，密度为7.93g/cm³，计量单位为千克，品牌为宝钢，原产地为上海，采购单价为18元，最低库存为200千克，质量标准为GB/T 3280-2015，存储条件为干燥通风，物料状态为启用。\n3.2 零部件\n传动轴是零部件，物料编码为MAT-002，零部件编码为COMP-001，零部件类别为动力传动件，规格型号为DS-300，图号为DWG-2026-088，材质为40Cr，计量单位为件，单件重量为12千克，尺寸精度为IT7，质保期限为12个月，质量标准为ISO 9001:2026。\n紧固螺栓是零部件，物料编码为MAT-004，零部件编码为COMP-002，零部件类别为标准紧固件，规格型号为M12×40，材质为8.8级碳钢，计量单位为件。\n铝合金外壳是零部件，物料编码为MAT-005，零部件编码为COMP-003，零部件类别为结构件，规格型号为DS-300外壳，材质为6061铝合金，计量单位为件。\n3.3 成品\n减速器是成品，物料编码为MAT-003，成品编码为FG-001，成品型号为R97，品牌为东南装备，规格为R97-40-5.5kW，计量单位为台，生产批次为B20261008，生产日期为2026年10月8日，保质期限为24个月，质量等级为一级，成品状态为可销售。\n4 采购业务\n东南装备有限公司向恒顺金属材料有限公司下达编号为PO-20261001的采购订单；该采购订单的订单日期为2026年10月1日，采购类别为原材料采购，采购部门为采购部，采购负责人为王明，约定交货日期为2026年10月15日，采购总金额为45000元，币种为人民币，交货地址为南京市江宁区科创路88号，结算方式为月结60天，订单状态为已收货。\n采购订单PO-20261001指定恒顺金属材料有限公司作为供应商，采购物料为304不锈钢板材。\n恒顺金属材料有限公司向东南装备有限公司提供304不锈钢板材这种物料。\n东南装备有限公司向华东紧固件有限公司下达编号为PO-20261012的采购订单；该采购订单的订单日期为2026年10月12日，约定交货日期为2026年10月20日，采购总金额为12800元，采购物料为紧固螺栓和铝合金外壳，订单状态为已下达。\n5 到货入库与质检\n物料到货后生成入库单，入库单号为GRN-20261005，入库日期为2026年10月5日；该入库单对应采购订单PO-20261001，记录入库物料304不锈钢板材，仓库名称为江宁原料仓，库位为A-03-12，入库数量为500千克，计量单位为千克，到货批次为B20261001，验收结果为合格，验收人员为王明，入库状态为已入库。\n入库单GRN-20261005入库至江宁原料仓，由王明负责验收；304不锈钢板材入库后存放于江宁原料仓。\n质量检验记录QC-20261005-01于2026年10月5日对304不锈钢板材进行检验，检验依据标准为GB/T 3280-2015，检验结论为合格，不合格数量为0。\n华东紧固件到货后生成入库单，入库单号为GRN-20261018，入库日期为2026年10月18日；该入库单对应采购订单PO-20261012，记录入库物料紧固螺栓和铝合金外壳，仓库名称为江宁原料仓，入库数量分别为200件和100件，验收结果为合格，入库状态为已入库。\n6 库存与出库\n入库单GRN-20261005入库后产生库存记录INV-001，该库存记录关联物料304不锈钢板材，库存数量为600千克，可用数量为550千克，锁定数量为50千克，最近盘点日期为2026年10月20日。\n物料出库生成出库单，出库单号为GIN-20261010，出库日期为2026年10月10日；该出库单从江宁原料仓出库，库位为A-03-12，出', 1, 0, '吴同', NULL, '2026-10-23 14:56:06', NULL, NULL, '2026-10-23 14:56:06', NULL);
INSERT INTO `app_unstruct_task_text` VALUES (3, 1001, 13, 14, 2, '库用途为生产领用，出库数量为300千克，计量单位为千克，经办人员为李工，收货地址为南京市江宁区科创路88号，出库状态为已出库。\n出库单GIN-20261010记录出库物料304不锈钢板材，由李工经办。\n7 物流与收货\n迅捷物流有限公司承运出库单GIN-20261010对应的发货物料。\n出库单GIN-20261010指定收货方为东南装备有限公司。\n运输任务SHIP-20261010-01由迅捷物流有限公司承运，发货日期为2026年10月10日，预计到达日期为2026年10月11日，实际到达日期为2026年10月11日，运输状态为已签收，车牌号为苏A·1B234，驾驶员为陈师傅。\n8 产品结构\n传动轴零部件可以组成减速器成品。\n紧固螺栓零部件和铝合金外壳零部件共同组成减速器成品。\n减速器成品包含传动轴、紧固螺栓和铝合金外壳等零部件，生产批次为B20261008，可通过组成关系追溯其零部件构成。\n9 抽取映射说明\n抽取时按ces.txt本体定义映射：组织、供应商、采购方、物流服务商、仓库、员工等主体描述映射为实例及其类型（rdf:type）；“编码”“名称”“统一社会信用代码”“数量”“日期”“结算方式”等字段映射为对应数据属性；“下达”“指定供应商”“采购物料”“提供”“对应采购订单”“记录入库物料”“记录出库物料”“入库至”“从仓库出库”“存放于”“承运”“指定收货方”“任职于”“负责”“验收由”“检验”“产生库存”“关联物料”“组成”等动作映射为对象关系。', 1, 0, '吴同', NULL, '2026-10-23 14:56:30', NULL, NULL, '2026-10-23 14:56:30', NULL);

-- ----------------------------
-- Table structure for basic_data_type
-- ----------------------------
DROP TABLE IF EXISTS `basic_data_type`;
CREATE TABLE `basic_data_type`  (
                                    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
                                    `type` tinyint(4) UNSIGNED NULL DEFAULT NULL COMMENT '类型;业务数据类型；基础数据类型',
                                    `data_type` tinyint(4) UNSIGNED NULL DEFAULT NULL COMMENT '数据类型;文本；布尔；小数；整数；日期',
                                    `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                    `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位;小数、整数展示',
                                    `decimal_places` int(11) NULL DEFAULT NULL COMMENT '小数位数;小数展示',
                                    `format` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '格式;日期展示',
                                    `enum_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型;枚举展示',
                                    `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                    `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                    `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                    `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of basic_data_type
-- ----------------------------
INSERT INTO `basic_data_type` VALUES (1, '电子邮箱', NULL, 0, '符合电子邮箱的格式', NULL, NULL, NULL, NULL, 1, 0, '超级管理员', 1, '2026-10-22 10:24:57', '超级管理员', NULL, '2026-10-22 10:25:35', NULL);
INSERT INTO `basic_data_type` VALUES (2, '是否有效', 0, 3, '布尔状态标识，true=有效、false=无效', NULL, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (3, '姓名', 0, 0, '人员标准姓名字段，用于记录自然人正式姓名', NULL, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (4, '手机号码', 0, 0, '11位中国大陆手机号码格式，支持运营商号段校验', NULL, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (5, '身份证号', 0, 0, '18位居民身份证号码，支持校验位合法性验证', NULL, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (6, '通讯地址', 0, 0, '通用通讯地址文本，支持省市区详细地址录入', NULL, NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (7, '年龄', 0, 1, '人员年龄，正整数取值范围0-150', '岁', NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (8, '通用数量', 0, 1, '通用计数单位，非负整数取值', '个', NULL, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);
INSERT INTO `basic_data_type` VALUES (9, '基础单价', 0, 2, '通用物品单价，人民币货币计量', '元', 2, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', '默认保留两位小数');
INSERT INTO `basic_data_type` VALUES (10, '重量', 0, 2, '物品重量计量值，千克单位', 'kg', 3, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', '默认保留三位小数');
INSERT INTO `basic_data_type` VALUES (11, '折扣比例', 0, 2, '折扣率百分比数值，取值范围0-100', '%', 2, NULL, NULL, 1, 0, '吴同', 2, '2026-10-22 10:32:43', NULL, NULL, '2026-10-22 10:32:43', NULL);

-- ----------------------------
-- Table structure for basic_datasource
-- ----------------------------
DROP TABLE IF EXISTS `basic_datasource`;
CREATE TABLE `basic_datasource`  (
                                     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                     `graph_id` bigint(20) NULL DEFAULT NULL COMMENT '图谱id',
                                     `datasource_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源名称',
                                     `datasource_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '数据源类型',
                                     `datasource_config` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据源配置(json字符串)',
                                     `ip` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'IP地址',
                                     `port` int(11) NOT NULL COMMENT '端口号',
                                     `list_count` int(11) NULL DEFAULT NULL COMMENT '数据库表数（预留）',
                                     `sync_count` int(11) NULL DEFAULT NULL COMMENT '同步记录数（预留）',
                                     `data_size` int(11) NULL DEFAULT NULL COMMENT '同步数据量大小（预留）',
                                     `description` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
                                     `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                     `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                     `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
                                     `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
                                     `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                     `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                     `remark` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据源' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_datasource
-- ----------------------------
INSERT INTO `basic_datasource` VALUES (1, NULL, '测试数据', 'MySql', '{\"username\":\"root\",\"password\":\"lJ7gP0cA5dD2dA1hA3bJ\",\"dbname\":\"qontology\"}', 'mysql57', 3306, NULL, NULL, NULL, '开发环境测试数据库', 1, 0, '吴同', 1, '2026-10-22 15:49:06', '吴同', 2, '2026-10-23 15:44:10', NULL);
INSERT INTO `basic_datasource` VALUES (2, 1, '供应链业务库-生产', 'MySql', '{\"username\":\"supply_prod\",\"password\":\"Kp9xQ2vB7nM3wS8z\",\"dbname\":\"supply_chain_prod\"}', 'prod-mysql.qiantong.com', 3306, 156, 286500, 4096, '供应链核心业务生产主库，承载采购、库存、订单全量业务数据', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '生产环境主库，双主高可用集群');
INSERT INTO `basic_datasource` VALUES (3, 2, '人力资源业务库-测试', 'MySql', '{\"username\":\"hr_test\",\"password\":\"Hr@test2026\",\"dbname\":\"hr_management_test\"}', 'test.qiantong.tech', 3307, 89, 12500, 256, '人力资源模块测试环境数据库，用于功能验证与性能测试', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '测试环境，每日重置测试数据');
INSERT INTO `basic_datasource` VALUES (4, NULL, '信创适配数据源-生产', 'DM8', '{\"username\":\"SYSDBA\",\"password\":\"Dm@prod2026\",\"dbname\":\"qontology_adapt\",\"schema\":\"public\"}', 'prod-dm8.qiantong.com', 5236, 86, 98500, 1024, '国产化信创适配生产数据源，达梦8数据库', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '信创合规，替代MySQL方案');
INSERT INTO `basic_datasource` VALUES (5, 14, '行政资产库-预发', 'DM8', '{\"username\":\"admin_pre\",\"password\":\"Admin#pre2026\",\"dbname\":\"admin_asset\",\"schema\":\"biz\"}', 'pre-dm8.qiantong.com', 5236, 62, 36800, 768, '行政办公资产管理预发库，国产化适配验证', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '预发环境，功能验证中');
INSERT INTO `basic_datasource` VALUES (6, 5, '财务核心库-生产', 'Oracle', '{\"username\":\"finance_prod\",\"password\":\"Fin$prod2026\",\"service_name\":\"FINPROD\"}', 'prod-oracle.qiantong.com', 1521, 215, 325600, 8192, '财务核心核算生产库，承载科目、凭证、账簿全量数据', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '核心财务库，RAC双机热备');
INSERT INTO `basic_datasource` VALUES (7, 4, '生产制造库-生产', 'Oracle', '{\"username\":\"mfg_prod\",\"password\":\"Mfg!prod2026\",\"service_name\":\"MFGPROD\"}', 'prod-oracle-mfg.qiantong.com', 1521, 168, 256800, 6144, '生产制造BOM与工艺生产库，承载产品结构、工艺路线数据', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '制造业务核心库，分区表架构');
INSERT INTO `basic_datasource` VALUES (8, NULL, '遗留业务系统库-生产', 'Oracle11', '{\"username\":\"legacy_user\",\"password\":\"Legacy@2026\",\"sid\":\"LEGACYDB\"}', 'legacy-oracle.qiantong.com', 1521, 132, 186500, 4096, '老旧业务系统生产库，Oracle 11g版本，逐步下线中', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '遗留系统，仅维持基本运维');
INSERT INTO `basic_datasource` VALUES (9, NULL, '历史数据查询库-只读', 'Oracle11', '{\"username\":\"query_r\",\"password\":\"queryread123\",\"sid\":\"HISTORY\"}', '192.168.1.103', 1521, 108, 986000, 12288, '历史数据只读查询库，Oracle 11g版本，归档查询用', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '只读权限，禁止写入操作');
INSERT INTO `basic_datasource` VALUES (10, NULL, '国企项目库-生产', 'Kingbase8', '{\"username\":\"kingbase_prod\",\"password\":\"Kb@prod2026\",\"dbname\":\"state_owned_project\",\"currentSchema\":\"biz\"}', 'prod-kingbase.qiantong.com', 54321, 92, 125600, 2048, '国企项目生产数据库，人大金仓V8版本，信创合规', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '信创项目专属，等保三级');
INSERT INTO `basic_datasource` VALUES (11, NULL, '审计日志库-生产', 'Kingbase8', '{\"username\":\"audit_prod\",\"password\":\"Audit$prod2026\",\"dbname\":\"audit_log\",\"currentSchema\":\"audit\"}', 'prod-kingbase-audit.qiantong.com', 54321, 58, 896500, 10240, '系统操作审计日志生产库，人大金仓存储全量审计数据', 1, 0, '吴同', 2, '2026-10-22 15:00:57', NULL, NULL, '2026-10-22 15:00:57', '审计专用库，仅追加写入');

-- ----------------------------
-- Table structure for basic_enum_data
-- ----------------------------
DROP TABLE IF EXISTS `basic_enum_data`;
CREATE TABLE `basic_enum_data`  (
                                    `enum_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举编码',
                                    `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                    `enum_sort` int(11) NULL DEFAULT 0 COMMENT '枚举排序',
                                    `enum_label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举标签',
                                    `enum_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举键值',
                                    `enum_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型',
                                    `status` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '状态;0：正常，1：停用',
                                    `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                    `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                    `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                    `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`enum_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 119 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '枚举数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_enum_data
-- ----------------------------
INSERT INTO `basic_enum_data` VALUES (1, 1001, 0, '女', '2', 'sys_user_sex', 0, 1, 0, '超级管理员', 1, '2026-10-22 10:11:58', '超级管理员', NULL, '2026-10-22 10:11:58', '');
INSERT INTO `basic_enum_data` VALUES (2, 1001, 0, '男', '1', 'sys_user_sex', 0, 1, 0, '超级管理员', 1, '2026-10-22 10:11:43', '超级管理员', NULL, '2026-10-22 10:11:43', '');
INSERT INTO `basic_enum_data` VALUES (3, 1001, 0, '正常', '0', 'sys_user_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (4, 1001, 1, '禁用', '1', 'sys_user_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (5, 1001, 2, '锁定', '2', 'sys_user_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (6, 1001, 3, '待激活', '3', 'sys_user_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (7, 1001, 0, '草稿', '0', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (8, 1001, 1, '审核中', '1', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (9, 1001, 2, '已发布', '2', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (10, 1001, 3, '已归档', '3', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (11, 1001, 4, '已停用', '4', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (12, 1001, 0, '开发中', 'dev', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (13, 1001, 1, '测试版', 'beta', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (14, 1001, 2, '候选版', 'rc', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (15, 1001, 3, '正式版', 'release', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (16, 1001, 4, '已废弃', 'deprecated', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (17, 1001, 0, '战略供应商', 'A', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (18, 1001, 1, '核心供应商', 'B', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (19, 1001, 2, '合格供应商', 'C', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (20, 1001, 3, '备选供应商', 'D', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (21, 1001, 4, '不合格供应商', 'E', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (22, 1001, 0, '原材料', 'raw', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (23, 1001, 1, '零部件', 'component', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (24, 1001, 2, '半成品', 'semi', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (25, 1001, 3, '成品', 'finished', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (26, 1001, 4, '辅料', 'auxiliary', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (27, 1001, 5, '备品备件', 'spare', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (28, 1001, 0, '待提交', '0', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (29, 1001, 1, '待审核', '1', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (30, 1001, 2, '已审核', '2', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (31, 1001, 3, '待发货', '3', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (32, 1001, 4, '部分到货', '4', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (33, 1001, 5, '全部到货', '5', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (34, 1001, 6, '已关闭', '6', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (35, 1001, 0, '在库正常', 'normal', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (36, 1001, 1, '在库冻结', 'frozen', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (37, 1001, 2, '在途', 'in_transit', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (38, 1001, 3, '待检', 'pending_check', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (39, 1001, 4, '不合格', 'unqualified', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (40, 1001, 5, '已报废', 'scrapped', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (41, 1001, 0, '运行中', 'running', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (42, 1001, 1, '待机', 'standby', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (43, 1001, 2, '维修中', 'repairing', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (44, 1001, 3, '故障', 'fault', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (45, 1001, 4, '停用', 'stop', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (46, 1001, 0, '低', 'low', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (47, 1001, 1, '中', 'medium', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (48, 1001, 2, '高', 'high', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (49, 1001, 3, '紧急', 'urgent', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (50, 1001, 4, '特急', 'critical', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (51, 1001, 0, '待开始', 'todo', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (52, 1001, 1, '进行中', 'doing', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (53, 1001, 2, '已暂停', 'paused', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (54, 1001, 3, '已完成', 'done', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (55, 1001, 4, '已取消', 'canceled', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (56, 1001, 5, '已逾期', 'overdue', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (57, 1001, 0, '优等品', 'grade_a', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (58, 1001, 1, '一等品', 'grade_b', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (59, 1001, 2, '合格品', 'grade_c', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (60, 1001, 3, '不合格品', 'unqualified', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (61, 1001, 4, '废品', 'waste', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (62, 1001, 0, '身份证', 'id_card', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (63, 1001, 1, '护照', 'passport', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (64, 1001, 2, '军官证', 'officer_card', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (65, 1001, 3, '营业执照', 'business_license', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (66, 1001, 4, '统一社会信用代码', 'uscc', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (67, 1001, 0, '高中及以下', 'high_school', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (68, 1001, 1, '大专', 'college', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (69, 1001, 2, '本科', 'bachelor', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (70, 1001, 3, '硕士', 'master', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (71, 1001, 4, '博士', 'doctor', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (72, 1001, 0, '管理岗', 'management', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (73, 1001, 1, '技术岗', 'technology', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (74, 1001, 2, '业务岗', 'business', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (75, 1001, 3, '操作岗', 'operation', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (76, 1001, 4, '职能岗', 'function', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (77, 1001, 0, '业务部门', 'business_dept', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (78, 1001, 1, '职能部门', 'function_dept', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (79, 1001, 2, '研发部门', 'rnd_dept', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (80, 1001, 3, '生产部门', 'produce_dept', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (81, 1001, 4, '支持部门', 'support_dept', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (82, 1001, 0, '采购合同', 'purchase', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (83, 1001, 1, '销售合同', 'sales', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (84, 1001, 2, '服务合同', 'service', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (85, 1001, 3, '租赁合同', 'lease', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (86, 1001, 4, '技术合同', 'technology', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (87, 1001, 5, '劳务合同', 'labor', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (88, 1001, 0, '草稿', 'draft', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (89, 1001, 1, '审批中', 'approving', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (90, 1001, 2, '已签署', 'signed', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (91, 1001, 3, '履行中', 'performing', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (92, 1001, 4, '已完成', 'finished', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (93, 1001, 5, '已违约', 'breach', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (94, 1001, 6, '已解除', 'terminated', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (95, 1001, 0, '人民币', 'CNY', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (96, 1001, 1, '美元', 'USD', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (97, 1001, 2, '欧元', 'EUR', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (98, 1001, 3, '日元', 'JPY', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (99, 1001, 4, '英镑', 'GBP', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (100, 1001, 0, '个', 'piece', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (101, 1001, 1, '件', 'unit', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (102, 1001, 2, '千克', 'kg', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (103, 1001, 3, '吨', 'ton', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (104, 1001, 4, '米', 'meter', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (105, 1001, 5, '箱', 'box', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (106, 1001, 0, '全部数据', 'all', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (107, 1001, 1, '本部门数据', 'dept', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (108, 1001, 2, '本部门及下级数据', 'dept_and_child', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (109, 1001, 3, '本人数据', 'self', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (110, 1001, 4, '自定义数据', 'custom', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (111, 1001, 0, '新增', 'add', 'old_log_operate_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (112, 1001, 1, '修改', 'edit', 'old_log_operate_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (113, 1001, 2, '删除', 'delete', 'old_log_operate_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (114, 1001, 0, '系统通知', 'system', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (115, 1001, 1, '业务通知', 'business', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (116, 1001, 2, '告警通知', 'alarm', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (117, 1001, 3, '待办通知', 'todo', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');
INSERT INTO `basic_enum_data` VALUES (118, 1001, 4, '审批通知', 'approve', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:23', NULL, NULL, '2026-10-22 10:20:23', '');

-- ----------------------------
-- Table structure for basic_enum_type
-- ----------------------------
DROP TABLE IF EXISTS `basic_enum_type`;
CREATE TABLE `basic_enum_type`  (
                                    `enum_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举主键',
                                    `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                    `enum_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举名称',
                                    `enum_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型',
                                    `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态;0：正常，1：停用',
                                    `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                    `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                    `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                    `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`enum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '枚举类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_enum_type
-- ----------------------------
INSERT INTO `basic_enum_type` VALUES (1, 1001, '用户性别', 'sys_user_sex', 0, 1, 0, '超级管理员', 1, '2026-10-21 14:39:03', '超级管理员', NULL, '2026-10-22 10:11:18', '');
INSERT INTO `basic_enum_type` VALUES (2, 1001, '用户状态', 'sys_user_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '系统用户通用状态枚举');
INSERT INTO `basic_enum_type` VALUES (3, 1001, '项目状态', 'proj_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '本体项目全生命周期状态');
INSERT INTO `basic_enum_type` VALUES (4, 1001, '本体版本状态', 'ontology_version_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '本体版本发布阶段状态');
INSERT INTO `basic_enum_type` VALUES (5, 1001, '供应商等级', 'supplier_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '供应链供应商分级枚举');
INSERT INTO `basic_enum_type` VALUES (6, 1001, '物料类型', 'material_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '供应链物料分类枚举');
INSERT INTO `basic_enum_type` VALUES (7, 1001, '采购订单状态', 'purchase_order_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '采购订单全流程状态');
INSERT INTO `basic_enum_type` VALUES (8, 1001, '库存状态', 'inventory_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '库存物料状态枚举');
INSERT INTO `basic_enum_type` VALUES (9, 1001, '设备运行状态', 'equipment_run_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '设备运行状态枚举');
INSERT INTO `basic_enum_type` VALUES (10, 1001, '任务优先级', 'task_priority', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '通用任务优先级枚举');
INSERT INTO `basic_enum_type` VALUES (11, 1001, '任务状态', 'task_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '通用任务执行状态枚举');
INSERT INTO `basic_enum_type` VALUES (12, 1001, '质量等级', 'quality_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '产品质量等级划分');
INSERT INTO `basic_enum_type` VALUES (13, 1001, '证件类型', 'sys_cert_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '人员身份证件类型');
INSERT INTO `basic_enum_type` VALUES (14, 1001, '学历等级', 'sys_education_level', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '人员学历划分枚举');
INSERT INTO `basic_enum_type` VALUES (15, 1001, '岗位类型', 'hr_position_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '人力资源岗位分类');
INSERT INTO `basic_enum_type` VALUES (16, 1001, '部门类型', 'hr_dept_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '组织部门类型划分');
INSERT INTO `basic_enum_type` VALUES (17, 1001, '合同类型', 'contract_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '业务合同分类枚举');
INSERT INTO `basic_enum_type` VALUES (18, 1001, '合同状态', 'contract_status', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '合同全生命周期状态');
INSERT INTO `basic_enum_type` VALUES (19, 1001, '币种类型', 'currency_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '通用币种编码枚举');
INSERT INTO `basic_enum_type` VALUES (20, 1001, '计量单位', 'measure_unit', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '物料通用计量单位');
INSERT INTO `basic_enum_type` VALUES (21, 1001, '数据权限类型', 'data_permission_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '系统数据权限范围枚举');
INSERT INTO `basic_enum_type` VALUES (22, 1001, '旧版日志操作类型', 'old_log_operate_type', 1, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '已停用，由新版日志操作类型替代');
INSERT INTO `basic_enum_type` VALUES (23, 1001, '消息通知类型', 'message_notify_type', 0, 1, 0, '吴同', 2, '2026-10-22 10:20:22', NULL, NULL, '2026-10-22 10:20:22', '系统消息通知分类');

-- ----------------------------
-- Table structure for basic_file
-- ----------------------------
DROP TABLE IF EXISTS `basic_file`;
CREATE TABLE `basic_file`  (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
                               `category_id` bigint(20) NULL DEFAULT NULL COMMENT '数据分类ID',
                               `file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称',
                               `file_suffix` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件后缀',
                               `file_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OTHER' COMMENT '文件类型：JSON、JSONL、MODEL、OFFICE、OTHER',
                               `file_size` decimal(18, 2) NOT NULL DEFAULT 0.00 COMMENT '文件大小MB',
                               `file_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储地址',
                               `storage_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCAL' COMMENT '存储类型：LOCAL、MINIO、OSS',
                               `file_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件哈希值',
                               `mime_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件MIME类型',
                               `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '文件状态：0上传中，1正常，2失败',
                               `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件描述',
                               `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                               `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                               `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                               `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                               `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_category_id`(`category_id`) USING BTREE,
                               INDEX `idx_file_type`(`file_type`) USING BTREE,
                               INDEX `idx_file_hash`(`file_hash`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '基础信息文件' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_file
-- ----------------------------
INSERT INTO `basic_file` VALUES (1, 1, '模型融合.md', 'md', 'OFFICE', 14018.00, '/2026/10/22/6ad978d2e4b00c36a2d37552.md', 'MINIO', 'd41d8cd98f00b204e9800998ecf8427e', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '系统全局参数配置说明文档', 1, 0, '吴同', 2, '2026-10-22 10:45:11', '超级管理员', NULL, '2026-10-22 10:45:41', NULL);
INSERT INTO `basic_file` VALUES (2, 3, '2026年度采购框架合同.pdf', 'pdf', 'OTHER', 3.12, '/local/contract/2026年度采购框架合同.pdf', 'LOCAL', 'b2c3d4e5f6a78901234567890abcdef1', 'application/pdf', 1, '年度原材料采购框架合同扫描件', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:56', NULL);
INSERT INTO `basic_file` VALUES (3, 5, '2026年10月库存台账.xlsx', 'xlsx', 'OFFICE', 1.24, '/local/report/202610库存台账.xlsx', 'LOCAL', 'd4e5f6a7b8c901234567890abcdef123', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 1, '10月份全仓库出入库库存台账', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:56', NULL);
INSERT INTO `basic_file` VALUES (4, 7, '本体评审会会议纪要.docx', 'docx', 'OFFICE', 0.56, '/minio/meeting/20261020本体评审纪要.docx', 'MINIO', 'f6a7b8c9d0e1234567890abcdef12345', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '2026-10-20本体模型专家评审会纪要', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:57', NULL);
INSERT INTO `basic_file` VALUES (5, 8, '前端用户操作手册.pdf', 'pdf', 'OTHER', 4.78, '/oss/doc/前端用户操作手册.pdf', 'OSS', 'a7b8c9d0e1f234567890abcdef123456', 'application/pdf', 1, '本体平台前端用户操作指南', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:58', NULL);
INSERT INTO `basic_file` VALUES (6, 10, '本体项目需求说明书.docx', 'docx', 'OFFICE', 3.45, '/minio/project/本体项目需求说明书.docx', 'MINIO', 'c9d0e1f2a3b4567890abcdef12345678', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '本体建设项目详细需求规格说明书', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:59', NULL);
INSERT INTO `basic_file` VALUES (7, 12, '原材料采购合同模板.docx', 'docx', 'OFFICE', 0.98, '/local/contract/原材料采购合同模板.docx', 'LOCAL', 'e1f2a3b4c5d67890abcdef1234567890', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '标准原材料采购合同通用模板', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 15:28:59', NULL);
INSERT INTO `basic_file` VALUES (8, 18, 'ISO9001质量体系认证.pdf', 'pdf', 'OTHER', 1.23, '/oss/cert/ISO9001质量体系认证.pdf', 'OSS', 'e7f8a9b0c1dabcdef1234567890abcdef', 'application/pdf', 1, 'ISO9001质量管理体系认证证书', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 17:38:58', NULL);
INSERT INTO `basic_file` VALUES (9, 16, '前端组件开发手册.pdf', 'pdf', 'OTHER', 3.21, '/oss/doc/前端组件开发手册.pdf', 'OSS', 'b0c1d2e3f4abcdef1234567890abcdef', 'application/pdf', 0, '前端组件库开发规范手册', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 17:38:59', '上传中');
INSERT INTO `basic_file` VALUES (10, 17, '业务功能需求清单.xlsx', 'xlsx', 'OFFICE', 1.12, '/oss/requirement/业务功能需求清单.xlsx', 'OSS', 'e3f4a5b6c7def1234567890abcdef', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', 2, '业务应用功能需求清单', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 17:38:59', '上传失败，需重新上传');
INSERT INTO `basic_file` VALUES (11, 13, '2026Q4销售合同汇总.pdf', 'pdf', 'OTHER', 2.34, '/minio/sales/2026Q4销售合同汇总.pdf', 'MINIO', 'f2a3b4c5d6e7890abcdef1234567890a', 'application/pdf', 1, '第四季度销售合同汇总归档文件', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 17:39:00', NULL);
INSERT INTO `basic_file` VALUES (12, 20, '项目周例会纪要.docx', 'docx', 'OFFICE', 0.32, '/minio/meeting/20261021项目周例会纪要.docx', 'MINIO', 'a9b0c1d2e3fabcdef1234567890abcdef', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 1, '2026-10-21项目周度例会纪要', 1, 0, '吴同', 2, '2026-10-22 10:45:11', NULL, NULL, '2026-10-22 17:39:01', NULL);
INSERT INTO `basic_file` VALUES (13, 3, '供应链采购与仓储协同文档.docx', 'docx', 'OTHER', 35167.00, '/2026/10/23/6adaed2ad9e0784f1cbeb769.docx', 'LOCAL', NULL, NULL, 1, '本文档为**供应链采购与仓储协同规范文档**，明确采购部门与仓储部门在需求提报、计划下达、到货预约、入库验收、库存核对、退换货、异常处理、对账复盘等全链路协同流程、职责边界、信息传递标准及配套管控要求。旨在打通采购下单、供应商送货、仓储收货上架的数据与流程衔接，减少到货延误、错收漏收、账实不符、积压呆滞等问题，提升到货履约效率、库存准确性，实现采购需求和仓储资源的高效联动。', 1, 0, '吴同', 2, '2026-10-23 13:14:59', '吴同', NULL, '2026-10-23 13:14:59', '');

-- ----------------------------
-- Table structure for basic_file_category
-- ----------------------------
DROP TABLE IF EXISTS `basic_file_category`;
CREATE TABLE `basic_file_category`  (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                                        `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
                                        `parent_id` bigint(20) NOT NULL COMMENT '父级id',
                                        `order_num` int(11) NOT NULL DEFAULT 0 COMMENT '显示顺序',
                                        `ancestors` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '祖级列表',
                                        `valid_flag` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否有效',
                                        `del_flag` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标志',
                                        `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                        `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人ID',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                        `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人ID',
                                        `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `uk_model_category_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件分类' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_file_category
-- ----------------------------
INSERT INTO `basic_file_category` VALUES (1, '系统文档', 0, 1, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '系统配置、操作手册类文档');
INSERT INTO `basic_file_category` VALUES (2, '项目资料', 0, 2, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '本体项目相关全套资料');
INSERT INTO `basic_file_category` VALUES (3, '合同文件', 0, 3, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '采购、销售、服务合同附件');
INSERT INTO `basic_file_category` VALUES (4, '图纸资料', 0, 4, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '设计图纸、结构图、原理图');
INSERT INTO `basic_file_category` VALUES (5, '报表台账', 0, 5, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '业务导出报表、统计台账');
INSERT INTO `basic_file_category` VALUES (6, '资质证件', 0, 6, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '供应商、单位资质证书扫描件');
INSERT INTO `basic_file_category` VALUES (7, '会议材料', 0, 7, '', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '评审会、例会、启动会材料');
INSERT INTO `basic_file_category` VALUES (8, '操作手册', 1, 1, '1', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '各模块用户操作指南');
INSERT INTO `basic_file_category` VALUES (9, '部署文档', 1, 2, '1', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '环境部署、安装运维文档');
INSERT INTO `basic_file_category` VALUES (10, '需求文档', 2, 1, '2', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '项目需求说明书、PRD文档');
INSERT INTO `basic_file_category` VALUES (11, '评审资料', 2, 2, '2', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '方案评审、本体模型评审材料');
INSERT INTO `basic_file_category` VALUES (12, '采购合同', 3, 1, '3', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '原材料、服务采购合同');
INSERT INTO `basic_file_category` VALUES (13, '销售合同', 3, 2, '3', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '对外销售及合作协议');
INSERT INTO `basic_file_category` VALUES (14, '二维图纸', 4, 1, '4', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', 'CAD二维工程图纸');
INSERT INTO `basic_file_category` VALUES (15, '三维模型', 4, 2, '4', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', 'STEP、IGS、3D模型文件');
INSERT INTO `basic_file_category` VALUES (16, '库存台账', 5, 1, '5', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '出入库、库存盘点报表');
INSERT INTO `basic_file_category` VALUES (17, '质量报表', 5, 2, '5', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '质检报告、不合格品台账');
INSERT INTO `basic_file_category` VALUES (18, '企业资质', 6, 1, '6', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '营业执照、信用证明');
INSERT INTO `basic_file_category` VALUES (19, '人员证书', 6, 2, '6', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '执业证、资格证扫描件');
INSERT INTO `basic_file_category` VALUES (20, '会议纪要', 7, 1, '7', 1, 0, '吴同', 2, '2026-10-22 10:41:07', NULL, NULL, '2026-10-22 10:41:07', '每次会议整理后的纪要文档');

-- ----------------------------
-- Table structure for basic_project
-- ----------------------------
DROP TABLE IF EXISTS `basic_project`;
CREATE TABLE `basic_project`  (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目名称',
                                  `domain` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '项目领域',
                                  `namespace` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '命名空间',
                                  `status` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '状态',
                                  `current_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前版本',
                                  `include_range` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '包含范围',
                                  `exclude_range` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '排除范围',
                                  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                  `valid_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否有效;0：无效，1：有效',
                                  `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志;1：已删除，0：未删除',
                                  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                  `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                  `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_project
-- ----------------------------
INSERT INTO `basic_project` VALUES (1, '供应链采购与仓储协同本体', '供应链管理', 'https://qontology.com/ontology/supply-chain', 1, 'v1.0.0', '涵盖组织、供应商、采购方、物流服务商、物料、原材料、零部件、成品、采购订单、入库单和出库单等对象，以及相关术语、枚举、属性和关系。重点描述采购订单管理、供应商供货、物料采购、到货入库、出库发运、收货方关联及零部件与成品的组成关系。', '暂不包含财务核算、发票与付款结算、合同审批、人力资源、生产工序与排程、设备运维及运输路线优化等业务。不涉及业务系统中的实际单据处理、库存计算和流程执行。', '面向企业采购与仓储物流协同场景。', b'1', b'0', '吴同', 1, '2026-10-20 09:47:23', '超级管理员', NULL, '2026-10-20 18:49:07', NULL);
INSERT INTO `basic_project` VALUES (2, '安全生产管理本体', '安全管理', 'https://qontology.com/ontology/safety-management', 0, 'v1.0.0', '涵盖安全隐患、隐患等级、整改记录、安全检查、安全培训、事故记录、危险源、防护措施等对象，以及相关属性、等级枚举和整改流程关系。重点描述隐患排查整改、事故追溯、危险源管控。', '不包含安全资质认证、消防管理、职业健康、劳保用品管理、安全费用预算、应急演练执行。不涉及安全管理系统的隐患自动预警和考核统计。', '面向企业生产安全隐患排查与事故管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '业务范围调整，暂停维护');
INSERT INTO `basic_project` VALUES (3, '人力资源组织与员工管理本体', '人力资源管理', 'https://qontology.com/ontology/hr-management', 1, 'v1.2.0', '涵盖组织架构、部门、岗位、职级、员工、人员编制、考勤记录、薪酬等级、劳动合同、入职离职、调动任免等对象，以及相关属性、枚举值和关联关系。重点描述组织层级、员工任职关系、岗位与职级对应、考勤与薪酬关联。', '不包含社保公积金核算、个税计算、招聘流程、培训管理、绩效考评、员工福利发放等业务模块。不涉及人事系统的流程审批和实际薪资发放计算。', '面向企业组织架构管理与员工基础人事场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '核心业务本体');
INSERT INTO `basic_project` VALUES (4, '客户关系管理本体', '客户关系管理', 'https://qontology.com/ontology/crm', 1, 'v2.0.0', '涵盖客户、联系人、客户分级、销售线索、商机、跟进记录、客户标签、行业分类、区域划分等对象，以及相关属性、状态枚举和业务关系。重点描述客户全生命周期、线索转商机、跟进记录关联、客户分层管理。', '不包含订单履约、收款结算、售后服务工单、营销活动执行、客户投诉处理流程。不涉及CRM系统的自动化营销和销售漏斗计算。', '面向企业客户全生命周期管理与销售跟进场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', NULL);
INSERT INTO `basic_project` VALUES (5, '财务核算科目本体', '财务管理', 'https://qontology.com/ontology/finance-accounting', 0, 'v1.0.0', '涵盖会计科目、科目分类、核算维度、凭证类型、账套、会计期间、币种、汇率等对象，以及相关属性、借贷规则和层级关系。重点描述科目树结构、核算维度关联、凭证与科目对应关系。', '不包含账务处理流程、报表生成、预算管理、成本核算、资金管理、税务申报等业务。不涉及财务系统的记账、结账和报表计算功能。', '面向企业财务核算基础科目与维度标准化场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '已停用，由新财务本体替代');
INSERT INTO `basic_project` VALUES (6, '生产制造BOM与工艺本体', '生产制造', 'https://qontology.com/ontology/manufacturing-bom', 0, 'v0.9.0-beta', '涵盖物料BOM、工艺路线、工序、工位、工装、半成品、成品、物料替代、工序参数等对象，以及相关属性、层级结构和先后关系。重点描述BOM多级组成、工艺工序顺序、物料与工序对应关系。', '不包含生产计划排程、生产工单执行、质量检验、设备稼动、产能计算、领料退料流程。不涉及MES系统的生产调度和现场数据采集。', '面向制造企业产品结构与工艺路线标准化场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '待评审发布');
INSERT INTO `basic_project` VALUES (7, '物流运输管理本体', '物流运输', 'https://qontology.com/ontology/logistics-transport', 0, 'v0.1.0', '涵盖运输订单、车辆、司机、线路、站点、货物、运输方式、运单状态、签收记录等对象，以及相关属性、状态枚举和关联关系。重点描述运输任务分配、车辆调度、运单轨迹、签收交付流程。', '不包含运费结算、运输路线优化、车辆维保、油耗管理、报关清关、危险品运输资质审核。不涉及TMS系统的路径规划和运费自动核算。', '面向企业干线运输与城配运输任务管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '初稿待完善');
INSERT INTO `basic_project` VALUES (8, '项目管理任务本体', '项目管理', 'https://qontology.com/ontology/project-management', 1, 'v1.1.0', '涵盖项目、项目阶段、任务、子任务、里程碑、负责人、优先级、任务状态、进度计划等对象，以及相关属性、层级关系和依赖关系。重点描述任务分解结构、任务依赖、里程碑节点、进度状态流转。', '不包含项目成本核算、工时填报、资源分配、风险管理、变更管理、项目验收流程。不涉及项目管理系统的工时统计和挣值计算。', '面向企业项目任务分解与进度管控场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', NULL);
INSERT INTO `basic_project` VALUES (9, '质量管理体系本体', '质量管理', 'https://qontology.com/ontology/quality-management', 1, 'v1.0.0', '涵盖质量标准、检验项、检验批次、不合格品、纠正措施、质量文档、审核记录、质量等级等对象，以及相关属性、判定规则和关联关系。重点描述检验标准、不合格品处理流程、质量问题追溯。', '不包含质量成本统计、计量器具校准、供应商质量考核、生产过程实时质检、质量体系认证流程。不涉及QMS系统的检验数据自动采集和SPC分析。', '面向企业质量体系管理与产品检验场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', NULL);
INSERT INTO `basic_project` VALUES (10, '设备资产管理本体', '设备管理', 'https://qontology.com/ontology/equipment-management', 0, 'v1.3.0', '涵盖设备台账、设备分类、设备状态、维保计划、维保记录、备件、故障记录、设备位置等对象，以及相关属性、状态流转和关联关系。重点描述设备全生命周期、维保计划执行、故障与备件关联。', '不包含设备采购、设备折旧、能耗统计、备品备件库存管理、设备点检执行、设备稼动率计算。不涉及EAM系统的维保工单派工和设备运行监控。', '面向企业设备台账与维保基础管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '已升级至资产统一本体');
INSERT INTO `basic_project` VALUES (11, '销售订单管理本体', '销售管理', 'https://qontology.com/ontology/sales-management', 1, 'v2.1.0', '涵盖销售订单、订单明细、产品、价格、客户、收货地址、订单状态、支付方式、交付计划等对象，以及相关属性、状态枚举和关联关系。重点描述订单全生命周期、订单与产品关联、交付计划拆分。', '不包含促销规则、折扣计算、发票开具、收款核销、退货退款流程、销售业绩统计。不涉及销售系统的订单自动拆单和价格计算。', '面向企业销售订单全链路管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', NULL);
INSERT INTO `basic_project` VALUES (12, '库存管理本体', '库存管理', 'https://qontology.com/ontology/inventory-management', 0, 'v1.0.0-rc1', '涵盖仓库、库区、库位、物料、库存批次、库存状态、出入库单据、盘点单、库存台账等对象，以及相关属性、状态枚举和数量关系。重点描述库存层级、批次管理、出入库单据关联、库存状态流转。', '不包含库存成本核算、安全库存计算、库存预警、拣货策略、库存周转率分析、ABC分类计算。不涉及WMS系统的库存实时扣减和波次拣货。', '面向企业多仓库库存基础管理与盘点场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '发布候选版，待最终验证');
INSERT INTO `basic_project` VALUES (13, '供应商管理本体', '采购管理', 'https://qontology.com/ontology/supplier-management', 1, 'v1.0.0', '涵盖供应商、供应商分级、资质文件、联系人、供应品类、报价、供应商评价、准入状态等对象，以及相关属性、等级枚举和关联关系。重点描述供应商全生命周期、资质管理、品类供应关系、评价体系。', '不包含采购订单执行、招投标流程、合同管理、付款结算、供应商现场审核、采购寻源。不涉及SRM系统的采购竞价和供应商自动考核。', '面向企业供应商基础信息与分级管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '与供应链本体协同使用');
INSERT INTO `basic_project` VALUES (14, '研发需求管理本体', '研发管理', 'https://qontology.com/ontology/rnd-requirement', 0, 'v0.2.0', '涵盖产品需求、功能需求、需求优先级、需求状态、需求来源、版本、需求文档、关联缺陷等对象，以及相关属性、状态流转和追溯关系。重点描述需求分解、需求跟踪矩阵、版本关联、需求与缺陷对应。', '不包含研发工时管理、任务分配、代码管理、测试用例、发布管理、项目成本核算。不涉及需求管理系统的流程审批和工作量估算。', '面向研发团队产品需求全生命周期管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '需求梳理中，补充行业场景');
INSERT INTO `basic_project` VALUES (15, '行政办公资产本体', '行政管理', 'https://qontology.com/ontology/admin-office', 1, 'v1.0.0', '涵盖办公资产、资产分类、领用记录、归还记录、资产状态、使用人、存放地点、采购批次等对象，以及相关属性、状态流转和归属关系。重点描述办公资产全生命周期、领用归还流程、资产归属管理。', '不包含资产折旧、费用分摊、采购审批、维修保养、报废处置流程、资产盘点执行。不涉及办公系统的资产费用核算和采购流程。', '面向企业办公资产台账与领用管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', NULL);
INSERT INTO `basic_project` VALUES (16, '数据指标管理本体', '数据分析', 'https://qontology.com/ontology/data-metrics', 1, 'v2.0.0', '涵盖指标、指标维度、度量值、统计周期、数据来源、业务主题、指标分级、计算公式等对象，以及相关属性、层级关系和逻辑关联。重点描述指标体系、维度与度量关系、指标血缘、业务主题分类。', '不包含数据采集、数据清洗、数据存储、报表生成、数据权限、数据质量校验。不涉及BI系统的指标计算和数据可视化。', '面向企业数据指标体系标准化与指标血缘管理场景。', b'1', b'0', '吴同', 2, '2026-10-22 09:53:58', NULL, NULL, '2026-10-22 09:53:58', '数据治理核心本体');

-- ----------------------------
-- Table structure for basic_project_version
-- ----------------------------
DROP TABLE IF EXISTS `basic_project_version`;
CREATE TABLE `basic_project_version`  (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                          `project_id` bigint(20) NOT NULL COMMENT '项目id',
                                          `version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本号',
                                          `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                          `base_version` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '基准版本',
                                          `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志;1：已删除，0：未删除',
                                          `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                          `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                          `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目版本' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of basic_project_version
-- ----------------------------
INSERT INTO `basic_project_version` VALUES (1, 1, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-20 09:47:23', '吴同', NULL, '2026-10-20 09:47:23', NULL);
INSERT INTO `basic_project_version` VALUES (2, 2, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (3, 3, 'v1.2.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (4, 4, 'v2.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (5, 5, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (6, 6, 'v0.9.0-beta', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (7, 7, 'v0.1.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (8, 8, 'v1.1.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (9, 9, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (10, 10, 'v1.3.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (11, 11, 'v2.1.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (12, 12, 'v1.0.0-rc1', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (13, 13, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (14, 14, 'v0.2.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (15, 15, 'v1.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO `basic_project_version` VALUES (16, 16, 'v2.0.0', NULL, NULL, b'0', '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
                              `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
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
                                     `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
                                     `table_id` bigint(20) NULL DEFAULT NULL COMMENT '归属表编号',
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
                                     `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
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
                            `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `sender_id` bigint(20) NULL DEFAULT NULL COMMENT '发送人',
                            `receiver_id` bigint(20) NULL DEFAULT NULL COMMENT '接收人',
                            `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
                            `content` varchar(3072) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息模板内容',
                            `category` tinyint(3) UNSIGNED NOT NULL COMMENT '消息类别',
                            `msg_level` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息等级',
                            `module` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息模块',
                            `entity_type` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '实体类型',
                            `entity_id` bigint(20) NULL DEFAULT NULL COMMENT '实体id',
                            `entity_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息链接',
                            `has_read` tinyint(1) NULL DEFAULT 0 COMMENT '是否已读',
                            `has_retraction` tinyint(1) NULL DEFAULT 0 COMMENT '是否撤回',
                            `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                            `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                            `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                            `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                            `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------

-- ----------------------------
-- Table structure for om_constraint_rule
-- ----------------------------
DROP TABLE IF EXISTS `om_constraint_rule`;
CREATE TABLE `om_constraint_rule`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区id',
                                       `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                       `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规则名称',
                                       `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规则描述',
                                       `applicable_type` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '适用类型;0：对象类型，1：属性，2：对象类型关系',
                                       `data_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据类型',
                                       `required_flag` tinyint(1) NULL DEFAULT 0 COMMENT '是否必填;0: 否，1: 是',
                                       `min_value` decimal(38, 10) NULL DEFAULT NULL COMMENT '数值下限',
                                       `max_value` decimal(38, 10) NULL DEFAULT NULL COMMENT '数值上限',
                                       `text_length` int(11) NULL DEFAULT NULL COMMENT '文本最大允许字符数',
                                       `allowed_values` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '允许值',
                                       `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                       `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                       `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                       `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                       `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '约束规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_constraint_rule
-- ----------------------------

-- ----------------------------
-- Table structure for om_data_type
-- ----------------------------
DROP TABLE IF EXISTS `om_data_type`;
CREATE TABLE `om_data_type`  (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `version_id` bigint(20) NOT NULL COMMENT '项目版本id',
                                 `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
                                 `data_type` tinyint(4) UNSIGNED NULL DEFAULT NULL COMMENT '数据类型;文本；布尔；小数；整数；日期',
                                 `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                 `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位;小数、整数展示',
                                 `decimal_places` int(11) NULL DEFAULT NULL COMMENT '小数位数;小数展示',
                                 `format` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '格式;日期展示',
                                 `enum_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型;枚举展示',
                                 `enum_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PROJECT' COMMENT '枚举来源;PROJECT当前项目版本，GLOBAL全局',
                                 `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                 `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_version_id`(`version_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '本体建模数据类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of om_data_type
-- ----------------------------
INSERT INTO `om_data_type` VALUES (1, 1, '出库用途', 5, '使用当前版本的出库用途枚举值统一业务取值。', NULL, NULL, NULL, 'outbound_purpose', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:00:00', '吴同', 1, '2026-10-21 09:00:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (2, 1, '零部件类别', 5, '使用当前版本的零部件类别枚举值统一业务取值。', NULL, NULL, NULL, 'component_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:01:00', '吴同', 1, '2026-10-21 09:01:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (3, 1, '联系地址', 0, '组织注册地址、收货地址及仓库地址。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:02:00', '吴同', 1, '2026-10-21 09:02:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (4, 1, '原材料类别', 5, '使用当前版本的原材料类别枚举值统一业务取值。', NULL, NULL, NULL, 'raw_material_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:03:00', '吴同', 1, '2026-10-21 09:03:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (5, 1, '联系电话', 0, '保存电话号码及区号，保留前导零和连接符。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:04:00', '吴同', 1, '2026-10-21 09:04:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (6, 1, '结算方式', 5, '使用当前版本的结算方式枚举值统一业务取值。', NULL, NULL, NULL, 'settlement_method', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:05:00', '吴同', 1, '2026-10-21 09:05:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (7, 1, '税率', 2, '采购业务适用的税率，以百分数记录，例如13.00表示13%。', '%', 2, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:06:00', '吴同', 1, '2026-10-21 09:06:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (8, 1, '采购类别', 5, '使用当前版本的采购类别枚举值统一业务取值。', NULL, NULL, NULL, 'purchase_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:07:00', '吴同', 1, '2026-10-21 09:07:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (9, 1, '是否验收合格', 3, '标识入库物料是否通过验收。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:08:00', '吴同', 1, '2026-10-21 09:08:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (10, 1, '单据状态', 5, '使用当前版本的单据状态枚举值统一业务取值。', NULL, NULL, NULL, 'document_status', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:09:00', '吴同', 1, '2026-10-21 09:09:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (11, 1, '业务时间', 4, '实际到货、入库及出库等业务发生时间。', NULL, NULL, 'yyyy-MM-dd HH:mm:ss', NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:10:00', '吴同', 1, '2026-10-21 09:10:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (12, 1, '运输方式', 5, '使用当前版本的运输方式枚举值统一业务取值。', NULL, NULL, NULL, 'transport_mode', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:11:00', '吴同', 1, '2026-10-21 09:11:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (13, 1, '交货周期', 1, '采购下单至约定交货之间的天数。', '天', NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:12:00', '吴同', 1, '2026-10-21 09:12:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (14, 1, '计量单位', 5, '使用当前版本的计量单位枚举值统一业务取值。', NULL, NULL, NULL, 'material_unit', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:13:00', '吴同', 1, '2026-10-21 09:13:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (15, 1, '物料重量', 2, '采购、入库及出库环节记录的物料重量。', 'kg', 3, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:14:00', '吴同', 1, '2026-10-21 09:14:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (16, 1, '物料类别', 5, '使用当前版本的物料类别枚举值统一业务取值。', NULL, NULL, NULL, 'material_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:15:00', '吴同', 1, '2026-10-21 09:15:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (17, 1, '业务名称', 0, '组织、供应商、物料等业务对象的名称。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:16:00', '吴同', 1, '2026-10-21 09:16:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (18, 1, '供应商类别', 5, '使用当前版本的供应商类别枚举值统一业务取值。', NULL, NULL, NULL, 'supplier_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:17:00', '吴同', 1, '2026-10-21 09:17:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (19, 1, '采购单价', 2, '采购物料每件对应的人民币含税单价。', '元/件', 4, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:18:00', '吴同', 1, '2026-10-21 09:18:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (20, 1, '是否启用', 3, '标识组织、供应商或物料是否处于启用状态。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:19:00', '吴同', 1, '2026-10-21 09:19:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (21, 1, '件数', 1, '按件统计的物料、包装或单据数量。', '件', NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:20:00', '吴同', 1, '2026-10-21 09:20:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (22, 1, '业务日期', 4, '采购日期、计划交货日期等仅记录年月日的业务日期。', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:21:00', '吴同', 1, '2026-10-21 09:21:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (23, 1, '组织类别', 5, '使用当前版本的组织类别枚举值统一业务取值。', NULL, NULL, NULL, 'organization_category', 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:22:00', '吴同', 1, '2026-10-21 09:22:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (24, 1, '业务编码', 0, '组织、供应商、物料及业务单据的唯一业务编码。', NULL, NULL, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:23:00', '吴同', 1, '2026-10-21 09:23:00', '供应链示例数据');
INSERT INTO `om_data_type` VALUES (25, 1, '订单金额', 2, '采购订单或收发货单据对应的人民币金额。', '元', 2, NULL, NULL, 'PROJECT', 1, 0, '吴同', 1, '2026-10-21 09:24:00', '吴同', 1, '2026-10-21 09:24:00', '供应链示例数据');

-- ----------------------------
-- Table structure for om_enum_data
-- ----------------------------
DROP TABLE IF EXISTS `om_enum_data`;
CREATE TABLE `om_enum_data`  (
                                 `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区id',
                                 `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                 `enum_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举编码',
                                 `enum_sort` int(11) NULL DEFAULT 0 COMMENT '枚举排序',
                                 `enum_label` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举标签',
                                 `enum_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举键值',
                                 `enum_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型',
                                 `status` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '状态;0：正常，1：停用',
                                 `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                 `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`enum_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '枚举数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_enum_data
-- ----------------------------
INSERT INTO `om_enum_data` VALUES (1001, 1, 1, 11, '盘亏处理', 'INVENTORY_LOSS', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:30:00', '吴同', 1, '2026-10-20 11:30:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 2, 10, '报废处置', 'SCRAP_DISPOSAL', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:30:30', '吴同', 1, '2026-10-20 11:30:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 3, 9, '质量检测', 'QUALITY_TEST', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:31:00', '吴同', 1, '2026-10-20 11:31:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 4, 8, '研发试制', 'RD_TRIAL', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:31:30', '吴同', 1, '2026-10-20 11:31:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 5, 7, '维修领用', 'MAINTENANCE', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:32:00', '吴同', 1, '2026-10-20 11:32:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 6, 6, '样品寄送', 'SAMPLE_DELIVERY', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:32:30', '吴同', 1, '2026-10-20 11:32:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 7, 5, '仓库调拨', 'WAREHOUSE_TRANSFER', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:33:00', '吴同', 1, '2026-10-20 11:33:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 8, 4, '采购退货', 'PURCHASE_RETURN', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:33:30', '吴同', 1, '2026-10-20 11:33:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 9, 3, '委外加工', 'OUTSOURCING', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:34:00', '吴同', 1, '2026-10-20 11:34:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 10, 2, '生产领料', 'PRODUCTION_PICKING', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:34:30', '吴同', 1, '2026-10-20 11:34:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 11, 1, '销售发货', 'SALES_DELIVERY', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 11:35:00', '吴同', 1, '2026-10-20 11:35:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 12, 11, '线缆', 'CABLE', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:35:30', '吴同', 1, '2026-10-20 11:35:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 13, 10, '连接器', 'CONNECTOR', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:36:00', '吴同', 1, '2026-10-20 11:36:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 14, 9, '控制器', 'CONTROLLER', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:36:30', '吴同', 1, '2026-10-20 11:36:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 15, 8, '传感器', 'SENSOR', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:37:00', '吴同', 1, '2026-10-20 11:37:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 16, 7, '电机', 'MOTOR', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:37:30', '吴同', 1, '2026-10-20 11:37:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 17, 6, '弹簧', 'SPRING', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:38:00', '吴同', 1, '2026-10-20 11:38:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 18, 5, '密封件', 'SEAL', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:38:30', '吴同', 1, '2026-10-20 11:38:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 19, 4, '轴类零件', 'SHAFT', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:39:00', '吴同', 1, '2026-10-20 11:39:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 20, 3, '齿轮', 'GEAR', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:39:30', '吴同', 1, '2026-10-20 11:39:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 21, 2, '轴承', 'BEARING', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:40:00', '吴同', 1, '2026-10-20 11:40:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 22, 1, '紧固件', 'FASTENER', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:40:30', '吴同', 1, '2026-10-20 11:40:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 23, 11, '复合材料', 'COMPOSITE', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:41:00', '吴同', 1, '2026-10-20 11:41:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 24, 10, '化工原料', 'CHEMICAL', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:41:30', '吴同', 1, '2026-10-20 11:41:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 25, 9, '木材', 'WOOD', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:42:00', '吴同', 1, '2026-10-20 11:42:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 26, 8, '陶瓷', 'CERAMIC', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:42:30', '吴同', 1, '2026-10-20 11:42:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 27, 7, '玻璃', 'GLASS', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:43:00', '吴同', 1, '2026-10-20 11:43:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 28, 6, '橡胶', 'RUBBER', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:43:30', '吴同', 1, '2026-10-20 11:43:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 29, 5, '工程塑料', 'ENGINEERING_PLASTIC', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:44:00', '吴同', 1, '2026-10-20 11:44:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 30, 4, '不锈钢', 'STAINLESS_STEEL', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:44:30', '吴同', 1, '2026-10-20 11:44:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 31, 3, '铜材', 'COPPER', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:45:00', '吴同', 1, '2026-10-20 11:45:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 32, 2, '铝材', 'ALUMINUM', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:45:30', '吴同', 1, '2026-10-20 11:45:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 33, 1, '钢材', 'STEEL', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:46:00', '吴同', 1, '2026-10-20 11:46:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 34, 11, '托收', 'COLLECTION', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:46:30', '吴同', 1, '2026-10-20 11:46:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 35, 10, '信用证', 'LETTER_OF_CREDIT', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:47:00', '吴同', 1, '2026-10-20 11:47:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 36, 9, '商业承兑', 'COMMERCIAL_ACCEPTANCE', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:47:30', '吴同', 1, '2026-10-20 11:47:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 37, 8, '银行承兑', 'BANK_ACCEPTANCE', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:48:00', '吴同', 1, '2026-10-20 11:48:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 38, 7, '分期付款', 'INSTALLMENT', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:48:30', '吴同', 1, '2026-10-20 11:48:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 39, 6, '月结90天', 'NET_90', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:49:00', '吴同', 1, '2026-10-20 11:49:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 40, 5, '月结60天', 'NET_60', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:49:30', '吴同', 1, '2026-10-20 11:49:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 41, 4, '月结30天', 'NET_30', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:50:00', '吴同', 1, '2026-10-20 11:50:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 42, 3, '预付款', 'ADVANCE', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:50:30', '吴同', 1, '2026-10-20 11:50:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 43, 2, '货到付款', 'COD', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:51:00', '吴同', 1, '2026-10-20 11:51:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 44, 1, '现款结算', 'CASH', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 11:51:30', '吴同', 1, '2026-10-20 11:51:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 45, 11, '物流服务采购', 'LOGISTICS_SERVICE', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:52:00', '吴同', 1, '2026-10-20 11:52:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 46, 10, '技术服务采购', 'TECHNICAL_SERVICE', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:52:30', '吴同', 1, '2026-10-20 11:52:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 47, 9, '软件采购', 'SOFTWARE', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:53:00', '吴同', 1, '2026-10-20 11:53:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 48, 8, '办公用品采购', 'OFFICE_SUPPLY', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:53:30', '吴同', 1, '2026-10-20 11:53:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 49, 7, '生产耗材采购', 'CONSUMABLE', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:54:00', '吴同', 1, '2026-10-20 11:54:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 50, 6, '包装材料采购', 'PACKAGING', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:54:30', '吴同', 1, '2026-10-20 11:54:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 51, 5, '维修备件采购', 'SPARE_PART', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:55:00', '吴同', 1, '2026-10-20 11:55:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 52, 4, '设备采购', 'EQUIPMENT', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:55:30', '吴同', 1, '2026-10-20 11:55:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 53, 3, '成品采购', 'FINISHED_GOODS', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:56:00', '吴同', 1, '2026-10-20 11:56:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 54, 2, '生产零部件采购', 'COMPONENT', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:56:30', '吴同', 1, '2026-10-20 11:56:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 55, 1, '生产原料采购', 'RAW_MATERIAL', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:57:00', '吴同', 1, '2026-10-20 11:57:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 56, 11, '已关闭', 'CLOSED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 11:57:30', '吴同', 1, '2026-10-20 11:57:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 57, 10, '已取消', 'CANCELLED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 11:58:00', '吴同', 1, '2026-10-20 11:58:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 58, 9, '已完成', 'COMPLETED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 11:58:30', '吴同', 1, '2026-10-20 11:58:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 59, 8, '部分完成', 'PARTIAL_COMPLETED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 11:59:00', '吴同', 1, '2026-10-20 11:59:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 60, 7, '执行中', 'EXECUTING', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 11:59:30', '吴同', 1, '2026-10-20 11:59:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 61, 6, '待执行', 'TO_EXECUTE', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:00:00', '吴同', 1, '2026-10-20 12:00:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 62, 5, '审批驳回', 'REJECTED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:00:30', '吴同', 1, '2026-10-20 12:00:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 63, 4, '审批通过', 'APPROVED', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:01:00', '吴同', 1, '2026-10-20 12:01:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 64, 3, '待审批', 'PENDING_APPROVAL', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:01:30', '吴同', 1, '2026-10-20 12:01:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 65, 2, '待提交', 'TO_SUBMIT', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:02:00', '吴同', 1, '2026-10-20 12:02:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 66, 1, '草稿', 'DRAFT', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 12:02:30', '吴同', 1, '2026-10-20 12:02:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 67, 11, '专线配送', 'DEDICATED_LINE', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:03:00', '吴同', 1, '2026-10-20 12:03:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 68, 10, '多式联运', 'MULTIMODAL', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:03:30', '吴同', 1, '2026-10-20 12:03:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 69, 9, '快递配送', 'EXPRESS', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:04:00', '吴同', 1, '2026-10-20 12:04:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 70, 8, '国际航空', 'INTERNATIONAL_AIR', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:04:30', '吴同', 1, '2026-10-20 12:04:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 71, 7, '国内航空', 'DOMESTIC_AIR', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:05:00', '吴同', 1, '2026-10-20 12:05:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 72, 6, '国际海运', 'INTERNATIONAL_SEA', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:05:30', '吴同', 1, '2026-10-20 12:05:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 73, 5, '国内海运', 'DOMESTIC_SEA', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:06:00', '吴同', 1, '2026-10-20 12:06:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 74, 4, '铁路集装箱', 'RAIL_CONTAINER', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:06:30', '吴同', 1, '2026-10-20 12:06:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 75, 3, '铁路整车', 'RAIL_WAGON', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:07:00', '吴同', 1, '2026-10-20 12:07:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 76, 2, '公路零担', 'ROAD_LTL', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:07:30', '吴同', 1, '2026-10-20 12:07:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 77, 1, '公路整车', 'ROAD_FTL', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 12:08:00', '吴同', 1, '2026-10-20 12:08:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 78, 11, '卷', 'ROLL', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:08:30', '吴同', 1, '2026-10-20 12:08:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 79, 10, '箱', 'BOX', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:09:00', '吴同', 1, '2026-10-20 12:09:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 80, 9, '升', 'L', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:09:30', '吴同', 1, '2026-10-20 12:09:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 81, 8, '立方米', 'M3', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:10:00', '吴同', 1, '2026-10-20 12:10:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 82, 7, '平方米', 'M2', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:10:30', '吴同', 1, '2026-10-20 12:10:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 83, 6, '米', 'M', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:11:00', '吴同', 1, '2026-10-20 12:11:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 84, 5, '克', 'G', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:11:30', '吴同', 1, '2026-10-20 12:11:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 85, 4, '吨', 'TON', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:12:00', '吴同', 1, '2026-10-20 12:12:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 86, 3, '千克', 'KG', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:12:30', '吴同', 1, '2026-10-20 12:12:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 87, 2, '套', 'SET', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:13:00', '吴同', 1, '2026-10-20 12:13:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 88, 1, '件', 'PCS', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 12:13:30', '吴同', 1, '2026-10-20 12:13:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 89, 11, '办公用品', 'OFFICE_SUPPLY', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:14:00', '吴同', 1, '2026-10-20 12:14:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 90, 10, '生产设备', 'EQUIPMENT', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:14:30', '吴同', 1, '2026-10-20 12:14:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 91, 9, '工装夹具', 'TOOLING', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:15:00', '吴同', 1, '2026-10-20 12:15:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 92, 8, '维修备件', 'SPARE_PART', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:15:30', '吴同', 1, '2026-10-20 12:15:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 93, 7, '生产耗材', 'CONSUMABLE', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:16:00', '吴同', 1, '2026-10-20 12:16:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 94, 6, '辅助材料', 'AUXILIARY', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:16:30', '吴同', 1, '2026-10-20 12:16:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 95, 5, '包装材料', 'PACKAGING', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:17:00', '吴同', 1, '2026-10-20 12:17:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 96, 4, '成品', 'FINISHED_GOODS', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:17:30', '吴同', 1, '2026-10-20 12:17:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 97, 3, '半成品', 'SEMI_FINISHED', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:18:00', '吴同', 1, '2026-10-20 12:18:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 98, 2, '零部件', 'COMPONENT', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:18:30', '吴同', 1, '2026-10-20 12:18:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 99, 1, '原材料', 'RAW_MATERIAL', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:19:00', '吴同', 1, '2026-10-20 12:19:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 100, 11, '综合服务商', 'GENERAL_SERVICE', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:19:30', '吴同', 1, '2026-10-20 12:19:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 101, 10, '检测服务商', 'TESTING', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:20:00', '吴同', 1, '2026-10-20 12:20:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 102, 9, '维修服务商', 'MAINTENANCE', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:20:30', '吴同', 1, '2026-10-20 12:20:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 103, 8, '软件供应商', 'SOFTWARE', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:21:00', '吴同', 1, '2026-10-20 12:21:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 104, 7, '耗材供应商', 'CONSUMABLE', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:21:30', '吴同', 1, '2026-10-20 12:21:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 105, 6, '包装供应商', 'PACKAGING', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:22:00', '吴同', 1, '2026-10-20 12:22:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 106, 5, '设备供应商', 'EQUIPMENT', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:22:30', '吴同', 1, '2026-10-20 12:22:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 107, 4, '零部件供应商', 'COMPONENT', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:23:00', '吴同', 1, '2026-10-20 12:23:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 108, 3, '原材料供应商', 'RAW_MATERIAL', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:23:30', '吴同', 1, '2026-10-20 12:23:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 109, 2, '贸易经销商', 'DISTRIBUTOR', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:24:00', '吴同', 1, '2026-10-20 12:24:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 110, 1, '生产制造商', 'MANUFACTURER', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:24:30', '吴同', 1, '2026-10-20 12:24:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 111, 11, '外部机构', 'EXTERNAL_ORGANIZATION', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:25:00', '吴同', 1, '2026-10-20 12:25:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 112, 10, '项目团队', 'PROJECT_TEAM', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:25:30', '吴同', 1, '2026-10-20 12:25:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 113, 9, '职能部门', 'FUNCTION_DEPARTMENT', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:26:00', '吴同', 1, '2026-10-20 12:26:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 114, 8, '仓储中心', 'WAREHOUSE_CENTER', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:26:30', '吴同', 1, '2026-10-20 12:26:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 115, 7, '物流中心', 'LOGISTICS_CENTER', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:27:00', '吴同', 1, '2026-10-20 12:27:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 116, 6, '销售公司', 'SALES_COMPANY', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:27:30', '吴同', 1, '2026-10-20 12:27:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 117, 5, '采购中心', 'PURCHASE_CENTER', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:28:00', '吴同', 1, '2026-10-20 12:28:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 118, 4, '研发中心', 'RD_CENTER', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:28:30', '吴同', 1, '2026-10-20 12:28:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 119, 3, '生产工厂', 'FACTORY', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:29:00', '吴同', 1, '2026-10-20 12:29:00', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 120, 2, '区域公司', 'REGIONAL_COMPANY', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:29:30', '吴同', 1, '2026-10-20 12:29:30', NULL);
INSERT INTO `om_enum_data` VALUES (1001, 1, 121, 1, '集团总部', 'GROUP', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 12:30:00', '吴同', 1, '2026-10-20 12:30:00', NULL);

-- ----------------------------
-- Table structure for om_enum_type
-- ----------------------------
DROP TABLE IF EXISTS `om_enum_type`;
CREATE TABLE `om_enum_type`  (
                                 `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区id',
                                 `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                 `enum_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '枚举主键',
                                 `enum_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举名称',
                                 `enum_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型',
                                 `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态;0：正常，1：停用',
                                 `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                 `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`enum_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '枚举类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_enum_type
-- ----------------------------
INSERT INTO `om_enum_type` VALUES (1001, 1, 1, '出库用途', 'outbound_purpose', 0, 1, 0, '吴同', 1, '2026-10-20 10:55:00', '吴同', 1, '2026-10-20 10:55:00', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 2, '零部件类别', 'component_category', 0, 1, 0, '吴同', 1, '2026-10-20 10:55:30', '吴同', 1, '2026-10-20 10:55:30', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 3, '原材料类别', 'raw_material_category', 0, 1, 0, '吴同', 1, '2026-10-20 10:56:00', '吴同', 1, '2026-10-20 10:56:00', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 4, '结算方式', 'settlement_method', 0, 1, 0, '吴同', 1, '2026-10-20 10:56:30', '吴同', 1, '2026-10-20 10:56:30', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 5, '采购类别', 'purchase_category', 0, 1, 0, '吴同', 1, '2026-10-20 10:57:00', '吴同', 1, '2026-10-20 10:57:00', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 6, '单据状态', 'document_status', 0, 1, 0, '吴同', 1, '2026-10-20 10:57:30', '吴同', 1, '2026-10-20 10:57:30', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 7, '运输方式', 'transport_mode', 0, 1, 0, '吴同', 1, '2026-10-20 10:58:00', '吴同', 1, '2026-10-20 10:58:00', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 8, '计量单位', 'material_unit', 0, 1, 0, '吴同', 1, '2026-10-20 10:58:30', '吴同', 1, '2026-10-20 10:58:30', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 9, '物料类别', 'material_category', 0, 1, 0, '吴同', 1, '2026-10-20 10:59:00', '吴同', 1, '2026-10-20 10:59:00', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 10, '供应商类别', 'supplier_category', 0, 1, 0, '吴同', 1, '2026-10-20 10:59:30', '吴同', 1, '2026-10-20 10:59:30', NULL);
INSERT INTO `om_enum_type` VALUES (1001, 1, 11, '组织类别', 'organization_category', 0, 1, 0, '吴同', 1, '2026-10-20 11:00:00', '吴同', 1, '2026-10-20 11:00:00', NULL);

-- ----------------------------
-- Table structure for om_object_type
-- ----------------------------
DROP TABLE IF EXISTS `om_object_type`;
CREATE TABLE `om_object_type`  (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                   `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                   `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '对象类型名称',
                                   `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                   `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父级id',
                                   `ancestors` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '祖级列表',
                                   `order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
                                   `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                   `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                   `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象类型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_object_type
-- ----------------------------
INSERT INTO `om_object_type` VALUES (1, 1001, 1, '出库单', '记录物料出库和发货信息的业务单据，用于追溯物料流向、承运主体及收货方。', 0, '0', 5, 1, 0, '吴同', 1, '2026-10-20 13:25:00', '吴同', 1, '2026-10-20 13:25:00', NULL);
INSERT INTO `om_object_type` VALUES (2, 1001, 1, '入库单', '记录物料接收、验收及入库情况的业务单据，用于到货核对和仓储追溯。', 0, '0', 4, 1, 0, '吴同', 1, '2026-10-20 13:25:30', '吴同', 1, '2026-10-20 13:25:30', NULL);
INSERT INTO `om_object_type` VALUES (3, 1001, 1, '采购订单', '记录采购物料、供应商、采购金额和交货约定的业务单据。', 0, '0', 3, 1, 0, '吴同', 1, '2026-10-20 13:26:00', '吴同', 1, '2026-10-20 13:26:00', NULL);
INSERT INTO `om_object_type` VALUES (4, 1001, 1, '成品', '完成生产加工并可交付使用的产品，可以通过组成关系追溯其零部件。', 7, '0,7', 3, 1, 0, '吴同', 1, '2026-10-20 13:26:30', '吴同', 1, '2026-10-20 13:26:30', NULL);
INSERT INTO `om_object_type` VALUES (5, 1001, 1, '零部件', '用于组成设备或产品的部件，需要维护规格型号、图号和质量要求等信息。', 7, '0,7', 2, 1, 0, '吴同', 1, '2026-10-20 13:27:00', '吴同', 1, '2026-10-20 13:27:00', NULL);
INSERT INTO `om_object_type` VALUES (6, 1001, 1, '原材料', '用于生产加工的基础材料，需要维护材料类别、牌号、纯度和质量标准等信息。', 7, '0,7', 1, 1, 0, '吴同', 1, '2026-10-20 13:27:30', '吴同', 1, '2026-10-20 13:27:30', NULL);
INSERT INTO `om_object_type` VALUES (7, 1001, 1, '物料', '生产经营活动中采购、储存、使用或销售的实物资源。', 0, '0', 2, 1, 0, '吴同', 1, '2026-10-20 13:28:00', '吴同', 1, '2026-10-20 13:28:00', NULL);
INSERT INTO `om_object_type` VALUES (8, 1001, 1, '物流服务商', '提供运输、配送及相关物流服务的组织，承担出库发货的运输任务。', 11, '0,11', 3, 1, 0, '吴同', 1, '2026-10-20 13:28:30', '吴同', 1, '2026-10-20 13:28:30', NULL);
INSERT INTO `om_object_type` VALUES (9, 1001, 1, '采购方', '负责采购物料、下达采购订单并接收货物的组织。', 11, '0,11', 2, 1, 0, '吴同', 1, '2026-10-20 13:29:00', '吴同', 1, '2026-10-20 13:29:00', NULL);
INSERT INTO `om_object_type` VALUES (10, 1001, 1, '供应商', '向企业提供物料或服务的组织，负责采购订单中约定物料的供应。', 11, '0,11', 1, 1, 0, '吴同', 1, '2026-10-20 13:29:30', '吴同', 1, '2026-10-20 13:29:30', NULL);
INSERT INTO `om_object_type` VALUES (11, 1001, 1, '组织', '具有明确职责和管理结构的业务主体，可承担采购、供应、物流和业务单据管理等活动。', 0, '0', 1, 1, 0, '吴同', 1, '2026-10-20 13:30:00', '吴同', 1, '2026-10-20 13:30:00', NULL);

-- ----------------------------
-- Table structure for om_object_type_attribute
-- ----------------------------
DROP TABLE IF EXISTS `om_object_type_attribute`;
CREATE TABLE `om_object_type_attribute`  (
                                             `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                             `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                             `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                             `object_type_id` bigint(20) NOT NULL COMMENT '对象类型id',
                                             `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性名称',
                                             `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性代码，在对象类型内唯一',
                                             `type_category` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '类型分类;1：基础数据类型，2：业务数据类型',
                                             `basic_type_code` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '基础类型编码;0文本,1整数,2小数,3布尔,4日期,5枚举',
                                             `business_type_id` bigint(20) NULL DEFAULT NULL COMMENT '业务类型id，按来源关联om_data_type或basic_data_type',
                                             `business_type_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型来源;PROJECT当前项目版本,GLOBAL全局',
                                             `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
                                             `decimal_places` int(11) NULL DEFAULT NULL COMMENT '小数位数',
                                             `format` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日期格式',
                                             `enum_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型编码',
                                             `enum_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举来源;PROJECT当前项目版本,GLOBAL全局',
                                             `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性描述',
                                             `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                             `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                             `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                             `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                             `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象类型属性表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_object_type_attribute
-- ----------------------------
INSERT INTO `om_object_type_attribute` VALUES (1, 1001, 1, 1, '出库状态', 'issue_status', 2, 5, 10, 'PROJECT', NULL, NULL, NULL, 'document_status', 'PROJECT', '出库单当前业务处理状态', 1, 0, '吴同', 1, '2026-10-20 14:00:00', '吴同', 1, '2026-10-20 14:00:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (2, 1001, 1, 1, '经办人员', 'operator', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '负责办理本次出库的人员姓名', 1, 0, '吴同', 1, '2026-10-20 14:00:30', '吴同', 1, '2026-10-20 14:00:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (3, 1001, 1, 1, '收货地址', 'receiving_address', 2, 0, 3, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '本次发货的最终收货地址', 1, 0, '吴同', 1, '2026-10-20 14:01:00', '吴同', 1, '2026-10-20 14:01:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (4, 1001, 1, 1, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '本次出库数量使用的计量单位', 1, 0, '吴同', 1, '2026-10-20 14:01:30', '吴同', 1, '2026-10-20 14:01:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (5, 1001, 1, 1, '出库数量', 'issue_quantity', 1, 2, NULL, NULL, '件', 3, NULL, NULL, NULL, '本次出库确认的物料数量', 1, 0, '吴同', 1, '2026-10-20 14:02:00', '吴同', 1, '2026-10-20 14:02:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (6, 1001, 1, 1, '出库用途', 'outbound_purpose', 2, 5, 1, 'PROJECT', NULL, NULL, NULL, 'outbound_purpose', 'PROJECT', '本次出库的业务用途', 1, 0, '吴同', 1, '2026-10-20 14:02:30', '吴同', 1, '2026-10-20 14:02:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (7, 1001, 1, 1, '库位', 'storage_location', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '本次出库物料所在库位', 1, 0, '吴同', 1, '2026-10-20 14:03:00', '吴同', 1, '2026-10-20 14:03:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (8, 1001, 1, 1, '仓库名称', 'warehouse_name', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '发出物料的仓库名称', 1, 0, '吴同', 1, '2026-10-20 14:03:30', '吴同', 1, '2026-10-20 14:03:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (9, 1001, 1, 1, '仓库编码', 'warehouse_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '发出物料的仓库编码', 1, 0, '吴同', 1, '2026-10-20 14:04:00', '吴同', 1, '2026-10-20 14:04:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (10, 1001, 1, 1, '出库日期', 'issue_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '物料实际办理出库的日期', 1, 0, '吴同', 1, '2026-10-20 14:04:30', '吴同', 1, '2026-10-20 14:04:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (11, 1001, 1, 1, '出库单号', 'issue_number', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '出库单的唯一业务编号', 1, 0, '吴同', 1, '2026-10-20 14:05:00', '吴同', 1, '2026-10-20 14:05:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (12, 1001, 1, 2, '入库状态', 'receipt_status', 2, 5, 10, 'PROJECT', NULL, NULL, NULL, 'document_status', 'PROJECT', '入库单当前业务处理状态', 1, 0, '吴同', 1, '2026-10-20 14:05:30', '吴同', 1, '2026-10-20 14:05:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (13, 1001, 1, 2, '验收人员', 'inspector', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '负责本次验收的人员姓名', 1, 0, '吴同', 1, '2026-10-20 14:06:00', '吴同', 1, '2026-10-20 14:06:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (14, 1001, 1, 2, '验收结果', 'acceptance_passed', 2, 3, 9, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '入库物料的验收结论', 1, 0, '吴同', 1, '2026-10-20 14:06:30', '吴同', 1, '2026-10-20 14:06:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (15, 1001, 1, 2, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '本次入库数量使用的计量单位', 1, 0, '吴同', 1, '2026-10-20 14:07:00', '吴同', 1, '2026-10-20 14:07:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (16, 1001, 1, 2, '入库数量', 'receipt_quantity', 1, 2, NULL, NULL, '件', 3, NULL, NULL, NULL, '本次入库确认的物料数量', 1, 0, '吴同', 1, '2026-10-20 14:07:30', '吴同', 1, '2026-10-20 14:07:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (17, 1001, 1, 2, '到货批次', 'arrival_batch', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '本次入库对应的到货批次', 1, 0, '吴同', 1, '2026-10-20 14:08:00', '吴同', 1, '2026-10-20 14:08:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (18, 1001, 1, 2, '库位', 'storage_location', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '入库物料的存放库位', 1, 0, '吴同', 1, '2026-10-20 14:08:30', '吴同', 1, '2026-10-20 14:08:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (19, 1001, 1, 2, '仓库名称', 'warehouse_name', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '接收入库物料的仓库名称', 1, 0, '吴同', 1, '2026-10-20 14:09:00', '吴同', 1, '2026-10-20 14:09:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (20, 1001, 1, 2, '仓库编码', 'warehouse_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '接收入库物料的仓库编码', 1, 0, '吴同', 1, '2026-10-20 14:09:30', '吴同', 1, '2026-10-20 14:09:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (21, 1001, 1, 2, '入库日期', 'receipt_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '物料实际办理入库的日期', 1, 0, '吴同', 1, '2026-10-20 14:10:00', '吴同', 1, '2026-10-20 14:10:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (22, 1001, 1, 2, '入库单号', 'receipt_number', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '入库单的唯一业务编号', 1, 0, '吴同', 1, '2026-10-20 14:10:30', '吴同', 1, '2026-10-20 14:10:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (23, 1001, 1, 3, '订单状态', 'order_status', 2, 5, 10, 'PROJECT', NULL, NULL, NULL, 'document_status', 'PROJECT', '采购订单当前执行状态', 1, 0, '吴同', 1, '2026-10-20 14:11:00', '吴同', 1, '2026-10-20 14:11:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (24, 1001, 1, 3, '结算方式', 'settlement_method', 2, 5, 6, 'PROJECT', NULL, NULL, NULL, 'settlement_method', 'PROJECT', '采购订单约定的付款结算方式', 1, 0, '吴同', 1, '2026-10-20 14:11:30', '吴同', 1, '2026-10-20 14:11:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (25, 1001, 1, 3, '交货地址', 'delivery_address', 2, 0, 3, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '采购订单约定的交货地址', 1, 0, '吴同', 1, '2026-10-20 14:12:00', '吴同', 1, '2026-10-20 14:12:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (26, 1001, 1, 3, '约定交货日期', 'expected_delivery_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '采购订单约定的到货日期', 1, 0, '吴同', 1, '2026-10-20 14:12:30', '吴同', 1, '2026-10-20 14:12:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (27, 1001, 1, 3, '币种', 'currency', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购订单金额使用的币种', 1, 0, '吴同', 1, '2026-10-20 14:13:00', '吴同', 1, '2026-10-20 14:13:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (28, 1001, 1, 3, '采购总金额', 'purchase_amount', 2, 2, 25, 'PROJECT', '元', 2, NULL, NULL, 'PROJECT', '采购订单约定的含税总金额', 1, 0, '吴同', 1, '2026-10-20 14:13:30', '超级管理员', NULL, '2026-10-22 10:15:05', NULL);
INSERT INTO `om_object_type_attribute` VALUES (29, 1001, 1, 3, '采购负责人', 'purchase_manager', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '负责订单执行的采购人员', 1, 0, '吴同', 1, '2026-10-20 14:14:00', '吴同', 1, '2026-10-20 14:14:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (30, 1001, 1, 3, '采购部门', 'purchase_department', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '发起采购订单的部门名称', 1, 0, '吴同', 1, '2026-10-20 14:14:30', '吴同', 1, '2026-10-20 14:14:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (31, 1001, 1, 3, '采购类别', 'purchase_category', 2, 5, 8, 'PROJECT', NULL, NULL, NULL, 'purchase_category', 'PROJECT', '采购订单所属采购类别', 1, 0, '吴同', 1, '2026-10-20 14:15:00', '吴同', 1, '2026-10-20 14:15:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (32, 1001, 1, 3, '订单日期', 'order_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '采购订单正式签发日期', 1, 0, '吴同', 1, '2026-10-20 14:15:30', '吴同', 1, '2026-10-20 14:15:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (33, 1001, 1, 3, '订单编号', 'order_number', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购订单的唯一业务编号', 1, 0, '吴同', 1, '2026-10-20 14:16:00', '吴同', 1, '2026-10-20 14:16:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (34, 1001, 1, 4, '成品状态', 'product_status', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品当前可销售或停用状态', 1, 0, '吴同', 1, '2026-10-20 14:16:30', '吴同', 1, '2026-10-20 14:16:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (35, 1001, 1, 4, '质量等级', 'quality_grade', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品经过检验确认的质量等级', 1, 0, '吴同', 1, '2026-10-20 14:17:00', '吴同', 1, '2026-10-20 14:17:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (36, 1001, 1, 4, '保质期限', 'shelf_life', 1, 1, NULL, NULL, '天', NULL, NULL, NULL, NULL, '成品约定的有效保存期限', 1, 0, '吴同', 1, '2026-10-20 14:17:30', '吴同', 1, '2026-10-20 14:17:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (37, 1001, 1, 4, '生产日期', 'production_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '成品完成生产的日期', 1, 0, '吴同', 1, '2026-10-20 14:18:00', '吴同', 1, '2026-10-20 14:18:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (38, 1001, 1, 4, '生产批次', 'production_batch', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品对应的生产批次编号', 1, 0, '吴同', 1, '2026-10-20 14:18:30', '吴同', 1, '2026-10-20 14:18:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (39, 1001, 1, 4, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '成品数量计量单位', 1, 0, '吴同', 1, '2026-10-20 14:19:00', '吴同', 1, '2026-10-20 14:19:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (40, 1001, 1, 4, '规格', 'specification', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品外形或包装规格', 1, 0, '吴同', 1, '2026-10-20 14:19:30', '吴同', 1, '2026-10-20 14:19:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (41, 1001, 1, 4, '品牌', 'brand', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品所属品牌', 1, 0, '吴同', 1, '2026-10-20 14:20:00', '吴同', 1, '2026-10-20 14:20:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (42, 1001, 1, 4, '成品型号', 'product_model', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '成品产品型号', 1, 0, '吴同', 1, '2026-10-20 14:20:30', '吴同', 1, '2026-10-20 14:20:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (43, 1001, 1, 4, '成品名称', 'product_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '成品业务名称', 1, 0, '吴同', 1, '2026-10-20 14:21:00', '吴同', 1, '2026-10-20 14:21:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (44, 1001, 1, 4, '成品编码', 'product_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '成品的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:21:30', '吴同', 1, '2026-10-20 14:21:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (45, 1001, 1, 5, '质量标准', 'quality_standard', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '零部件适用的质量标准', 1, 0, '吴同', 1, '2026-10-20 14:22:00', '吴同', 1, '2026-10-20 14:22:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (46, 1001, 1, 5, '质保期限', 'warranty_period', 1, 1, NULL, NULL, '月', NULL, NULL, NULL, NULL, '零部件约定的质量保证期限', 1, 0, '吴同', 1, '2026-10-20 14:22:30', '吴同', 1, '2026-10-20 14:22:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (47, 1001, 1, 5, '尺寸精度', 'dimensional_accuracy', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '零部件加工的尺寸精度要求', 1, 0, '吴同', 1, '2026-10-20 14:23:00', '吴同', 1, '2026-10-20 14:23:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (48, 1001, 1, 5, '重量', 'weight', 2, 2, 3, 'GLOBAL', 'kg', 3, NULL, NULL, NULL, '零部件单件重量', 1, 0, '吴同', 1, '2026-10-20 14:23:30', '吴同', 1, '2026-10-20 14:23:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (49, 1001, 1, 5, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '零部件数量计量单位', 1, 0, '吴同', 1, '2026-10-20 14:24:00', '吴同', 1, '2026-10-20 14:24:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (50, 1001, 1, 5, '材质', 'material', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '零部件采用的主要材质', 1, 0, '吴同', 1, '2026-10-20 14:24:30', '吴同', 1, '2026-10-20 14:24:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (51, 1001, 1, 5, '图号', 'drawing_number', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '零部件对应的设计图纸编号', 1, 0, '吴同', 1, '2026-10-20 14:25:00', '吴同', 1, '2026-10-20 14:25:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (52, 1001, 1, 5, '规格型号', 'specification_model', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '零部件规格及型号', 1, 0, '吴同', 1, '2026-10-20 14:25:30', '吴同', 1, '2026-10-20 14:25:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (53, 1001, 1, 5, '零部件类别', 'component_category', 2, 5, 2, 'PROJECT', NULL, NULL, NULL, 'component_category', 'PROJECT', '零部件所属功能类别', 1, 0, '吴同', 1, '2026-10-20 14:26:00', '吴同', 1, '2026-10-20 14:26:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (54, 1001, 1, 5, '零部件名称', 'component_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '零部件业务名称', 1, 0, '吴同', 1, '2026-10-20 14:26:30', '吴同', 1, '2026-10-20 14:26:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (55, 1001, 1, 5, '零部件编码', 'component_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '零部件的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:27:00', '吴同', 1, '2026-10-20 14:27:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (56, 1001, 1, 6, '质量标准', 'quality_standard', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '原材料适用的质量标准', 1, 0, '吴同', 1, '2026-10-20 14:27:30', '吴同', 1, '2026-10-20 14:27:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (57, 1001, 1, 6, '存储条件', 'storage_condition', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '原材料存放所需的环境条件', 1, 0, '吴同', 1, '2026-10-20 14:28:00', '吴同', 1, '2026-10-20 14:28:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (58, 1001, 1, 6, '原产地', 'country_of_origin', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '原材料的生产来源地', 1, 0, '吴同', 1, '2026-10-20 14:28:30', '吴同', 1, '2026-10-20 14:28:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (59, 1001, 1, 6, '密度', 'density', 1, 2, NULL, NULL, 'g/cm³', 3, NULL, NULL, NULL, '原材料单位体积的质量', 1, 0, '吴同', 1, '2026-10-20 14:29:00', '吴同', 1, '2026-10-20 14:29:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (60, 1001, 1, 6, '纯度', 'purity', 1, 2, NULL, NULL, '%', 2, NULL, NULL, NULL, '原材料主要成分的纯度', 1, 0, '吴同', 1, '2026-10-20 14:29:30', '吴同', 1, '2026-10-20 14:29:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (61, 1001, 1, 6, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '原材料数量计量单位', 1, 0, '吴同', 1, '2026-10-20 14:30:00', '吴同', 1, '2026-10-20 14:30:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (62, 1001, 1, 6, '规格', 'specification', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '原材料尺寸或包装规格', 1, 0, '吴同', 1, '2026-10-20 14:30:30', '吴同', 1, '2026-10-20 14:30:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (63, 1001, 1, 6, '材料牌号', 'material_grade', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '原材料牌号及等级', 1, 0, '吴同', 1, '2026-10-20 14:31:00', '吴同', 1, '2026-10-20 14:31:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (64, 1001, 1, 6, '材料类别', 'raw_material_category', 2, 5, 4, 'PROJECT', NULL, NULL, NULL, 'raw_material_category', 'PROJECT', '原材料所属材料类别', 1, 0, '吴同', 1, '2026-10-20 14:31:30', '吴同', 1, '2026-10-20 14:31:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (65, 1001, 1, 6, '原材料名称', 'raw_material_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '原材料业务名称', 1, 0, '吴同', 1, '2026-10-20 14:32:00', '吴同', 1, '2026-10-20 14:32:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (66, 1001, 1, 6, '原材料编码', 'raw_material_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '原材料的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:32:30', '吴同', 1, '2026-10-20 14:32:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (67, 1001, 1, 7, '物料状态', 'material_status', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料当前启用或停用状态', 1, 0, '吴同', 1, '2026-10-20 14:33:00', '吴同', 1, '2026-10-20 14:33:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (68, 1001, 1, 7, '存储条件', 'storage_condition', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料存放所需的环境条件', 1, 0, '吴同', 1, '2026-10-20 14:33:30', '吴同', 1, '2026-10-20 14:33:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (69, 1001, 1, 7, '最低库存', 'minimum_stock', 1, 1, NULL, NULL, '件', NULL, NULL, NULL, NULL, '物料安全库存下限', 1, 0, '吴同', 1, '2026-10-20 14:34:00', '吴同', 1, '2026-10-20 14:34:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (70, 1001, 1, 7, '采购单价', 'purchase_price', 2, 2, 19, 'PROJECT', '元/件', 4, NULL, NULL, 'PROJECT', '物料参考采购单价', 1, 0, '吴同', 1, '2026-10-20 14:34:30', '吴同', 1, '2026-10-20 14:34:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (71, 1001, 1, 7, '产地', 'origin', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料生产来源地', 1, 0, '吴同', 1, '2026-10-20 14:35:00', '吴同', 1, '2026-10-20 14:35:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (72, 1001, 1, 7, '品牌', 'brand', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料所属品牌', 1, 0, '吴同', 1, '2026-10-20 14:35:30', '吴同', 1, '2026-10-20 14:35:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (73, 1001, 1, 7, '计量单位', 'material_unit', 2, 5, 14, 'PROJECT', NULL, NULL, NULL, 'material_unit', 'PROJECT', '物料数量计量单位', 1, 0, '吴同', 1, '2026-10-20 14:36:00', '吴同', 1, '2026-10-20 14:36:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (74, 1001, 1, 7, '规格型号', 'specification_model', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料规格及型号', 1, 0, '吴同', 1, '2026-10-20 14:36:30', '吴同', 1, '2026-10-20 14:36:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (75, 1001, 1, 7, '物料类别', 'material_category', 2, 5, 16, 'PROJECT', NULL, NULL, NULL, 'material_category', 'PROJECT', '物料所属业务分类', 1, 0, '吴同', 1, '2026-10-20 14:37:00', '吴同', 1, '2026-10-20 14:37:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (76, 1001, 1, 7, '物料名称', 'material_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '物料业务名称', 1, 0, '吴同', 1, '2026-10-20 14:37:30', '吴同', 1, '2026-10-20 14:37:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (77, 1001, 1, 7, '物料编码', 'material_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '物料的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:38:00', '吴同', 1, '2026-10-20 14:38:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (78, 1001, 1, 8, '合作状态', 'cooperation_status', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物流服务商当前合作状态', 1, 0, '吴同', 1, '2026-10-20 14:38:30', '吴同', 1, '2026-10-20 14:38:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (79, 1001, 1, 8, '货损率', 'damage_rate', 2, 2, 2, 'GLOBAL', '%', 2, NULL, NULL, NULL, '统计周期内的货损比例', 1, 0, '吴同', 1, '2026-10-20 14:39:00', '吴同', 1, '2026-10-20 14:39:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (80, 1001, 1, 8, '车辆数量', 'vehicle_count', 1, 1, NULL, NULL, '辆', NULL, NULL, NULL, NULL, '物流服务商可投入的车辆数量', 1, 0, '吴同', 1, '2026-10-20 14:39:30', '吴同', 1, '2026-10-20 14:39:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (81, 1001, 1, 8, '联系电话', 'phone', 2, 0, 5, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '物流业务联系电话', 1, 0, '吴同', 1, '2026-10-20 14:40:00', '吴同', 1, '2026-10-20 14:40:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (82, 1001, 1, 8, '联系人', 'contact_name', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物流业务联系人姓名', 1, 0, '吴同', 1, '2026-10-20 14:40:30', '吴同', 1, '2026-10-20 14:40:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (83, 1001, 1, 8, '运输资质', 'transport_qualification', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物流服务商持有的运输资质', 1, 0, '吴同', 1, '2026-10-20 14:41:00', '吴同', 1, '2026-10-20 14:41:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (84, 1001, 1, 8, '服务区域', 'service_region', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物流服务商能够覆盖的运输区域', 1, 0, '吴同', 1, '2026-10-20 14:41:30', '吴同', 1, '2026-10-20 14:41:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (85, 1001, 1, 8, '运输方式', 'transport_mode', 2, 5, 12, 'PROJECT', NULL, NULL, NULL, 'transport_mode', 'PROJECT', '物流服务商支持的运输方式', 1, 0, '吴同', 1, '2026-10-20 14:42:00', '吴同', 1, '2026-10-20 14:42:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (86, 1001, 1, 8, '统一社会信用代码', 'credit_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物流服务商依法登记的统一社会信用代码', 1, 0, '吴同', 1, '2026-10-20 14:42:30', '吴同', 1, '2026-10-20 14:42:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (87, 1001, 1, 8, '服务商名称', 'provider_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '物流服务商登记名称', 1, 0, '吴同', 1, '2026-10-20 14:43:00', '吴同', 1, '2026-10-20 14:43:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (88, 1001, 1, 8, '服务商编码', 'provider_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '物流服务商的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:43:30', '吴同', 1, '2026-10-20 14:43:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (89, 1001, 1, 9, '合作状态', 'cooperation_status', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购方当前业务合作状态', 1, 0, '吴同', 1, '2026-10-20 14:44:00', '吴同', 1, '2026-10-20 14:44:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (90, 1001, 1, 9, '结算方式', 'settlement_method', 2, 5, 6, 'PROJECT', NULL, NULL, NULL, 'settlement_method', 'PROJECT', '采购方约定的货款结算方式', 1, 0, '吴同', 1, '2026-10-20 14:44:30', '吴同', 1, '2026-10-20 14:44:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (91, 1001, 1, 9, '收货地址', 'receiving_address', 2, 0, 3, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '采购方默认收货地址', 1, 0, '吴同', 1, '2026-10-20 14:45:00', '吴同', 1, '2026-10-20 14:45:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (92, 1001, 1, 9, '电子邮箱', 'email', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购业务联系邮箱', 1, 0, '吴同', 1, '2026-10-20 14:45:30', '吴同', 1, '2026-10-20 14:45:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (93, 1001, 1, 9, '联系电话', 'phone', 2, 0, 5, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '采购业务联系电话', 1, 0, '吴同', 1, '2026-10-20 14:46:00', '吴同', 1, '2026-10-20 14:46:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (94, 1001, 1, 9, '采购负责人', 'purchase_manager', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购业务负责人姓名', 1, 0, '吴同', 1, '2026-10-20 14:46:30', '吴同', 1, '2026-10-20 14:46:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (95, 1001, 1, 9, '采购部门', 'purchase_department', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '负责采购业务的部门名称', 1, 0, '吴同', 1, '2026-10-20 14:47:00', '吴同', 1, '2026-10-20 14:47:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (96, 1001, 1, 9, '采购区域', 'purchase_region', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购方负责的采购区域', 1, 0, '吴同', 1, '2026-10-20 14:47:30', '吴同', 1, '2026-10-20 14:47:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (97, 1001, 1, 9, '统一社会信用代码', 'credit_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购方依法登记的统一社会信用代码', 1, 0, '吴同', 1, '2026-10-20 14:48:00', '吴同', 1, '2026-10-20 14:48:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (98, 1001, 1, 9, '采购方名称', 'purchaser_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '采购方登记名称', 1, 0, '吴同', 1, '2026-10-20 14:48:30', '吴同', 1, '2026-10-20 14:48:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (99, 1001, 1, 9, '采购方编码', 'purchaser_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '采购方的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:49:00', '吴同', 1, '2026-10-20 14:49:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (100, 1001, 1, 10, '合作开始日期', 'cooperation_start_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '首次建立合作关系的日期', 1, 0, '吴同', 1, '2026-10-20 14:49:30', '吴同', 1, '2026-10-20 14:49:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (101, 1001, 1, 10, '结算方式', 'settlement_method', 2, 5, 6, 'PROJECT', NULL, NULL, NULL, 'settlement_method', 'PROJECT', '供应商约定的货款结算方式', 1, 0, '吴同', 1, '2026-10-20 14:50:00', '吴同', 1, '2026-10-20 14:50:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (102, 1001, 1, 10, '联系电话', 'phone', 2, 0, 5, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '供应商业务联系电话', 1, 0, '吴同', 1, '2026-10-20 14:50:30', '吴同', 1, '2026-10-20 14:50:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (103, 1001, 1, 10, '联系人', 'contact_name', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商业务联系人姓名', 1, 0, '吴同', 1, '2026-10-20 14:51:00', '吴同', 1, '2026-10-20 14:51:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (104, 1001, 1, 10, '供应范围', 'supply_scope', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商能够提供的物料或服务范围', 1, 0, '吴同', 1, '2026-10-20 14:51:30', '吴同', 1, '2026-10-20 14:51:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (105, 1001, 1, 10, '风险评分', 'risk_score', 1, 2, NULL, NULL, '分', 1, NULL, NULL, NULL, '供应商综合风险评估分值', 1, 0, '吴同', 1, '2026-10-20 14:52:00', '吴同', 1, '2026-10-20 14:52:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (106, 1001, 1, 10, '合作状态', 'cooperation_status', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商当前合作状态', 1, 0, '吴同', 1, '2026-10-20 14:52:30', '吴同', 1, '2026-10-20 14:52:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (107, 1001, 1, 10, '统一社会信用代码', 'credit_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商依法登记的统一社会信用代码', 1, 0, '吴同', 1, '2026-10-20 14:53:00', '吴同', 1, '2026-10-20 14:53:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (108, 1001, 1, 10, '供应商类别', 'supplier_category', 2, 5, 18, 'PROJECT', NULL, NULL, NULL, 'supplier_category', 'PROJECT', '供应商提供产品或服务的类别', 1, 0, '吴同', 1, '2026-10-20 14:53:30', '吴同', 1, '2026-10-20 14:53:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (109, 1001, 1, 10, '供应商名称', 'supplier_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '供应商登记名称', 1, 0, '吴同', 1, '2026-10-20 14:54:00', '吴同', 1, '2026-10-20 14:54:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (110, 1001, 1, 10, '供应商编码', 'supplier_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '供应商的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 14:54:30', '吴同', 1, '2026-10-20 14:54:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (111, 1001, 1, 11, '成立日期', 'established_date', 2, 4, 22, 'PROJECT', NULL, NULL, 'yyyy-MM-dd', NULL, 'PROJECT', '组织依法成立的日期', 1, 0, '吴同', 1, '2026-10-20 14:55:00', '吴同', 1, '2026-10-20 14:55:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (112, 1001, 1, 11, '电子邮箱', 'email', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '组织公共联系邮箱', 1, 0, '吴同', 1, '2026-10-20 14:55:30', '吴同', 1, '2026-10-20 14:55:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (113, 1001, 1, 11, '联系电话', 'phone', 2, 0, 5, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '组织对外联系电话', 1, 0, '吴同', 1, '2026-10-20 14:56:00', '吴同', 1, '2026-10-20 14:56:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (114, 1001, 1, 11, '负责人', 'manager', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '组织业务负责人姓名', 1, 0, '吴同', 1, '2026-10-20 14:56:30', '吴同', 1, '2026-10-20 14:56:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (115, 1001, 1, 11, '办公地址', 'office_address', 2, 0, 3, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '组织实际办公所在地', 1, 0, '吴同', 1, '2026-10-20 14:57:00', '吴同', 1, '2026-10-20 14:57:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (116, 1001, 1, 11, '注册地址', 'registered_address', 2, 0, 3, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '组织依法登记的注册地址', 1, 0, '吴同', 1, '2026-10-20 14:57:30', '吴同', 1, '2026-10-20 14:57:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (117, 1001, 1, 11, '组织简称', 'short_name', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '组织日常使用的简称', 1, 0, '吴同', 1, '2026-10-20 14:58:00', '吴同', 1, '2026-10-20 14:58:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (118, 1001, 1, 11, '统一社会信用代码', 'credit_code', 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '组织依法登记的统一社会信用代码', 1, 0, '吴同', 1, '2026-10-20 14:58:30', '吴同', 1, '2026-10-20 14:58:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (119, 1001, 1, 11, '组织类别', 'organization_category', 2, 5, 23, 'PROJECT', NULL, NULL, NULL, 'organization_category', 'PROJECT', '组织所属业务类别', 1, 0, '吴同', 1, '2026-10-20 14:59:00', '吴同', 1, '2026-10-20 14:59:00', NULL);
INSERT INTO `om_object_type_attribute` VALUES (120, 1001, 1, 11, '组织名称', 'organization_name', 2, 0, 17, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '组织对外使用的正式名称', 1, 0, '吴同', 1, '2026-10-20 14:59:30', '吴同', 1, '2026-10-20 14:59:30', NULL);
INSERT INTO `om_object_type_attribute` VALUES (121, 1001, 1, 11, '组织编码', 'organization_code', 2, 0, 24, 'PROJECT', NULL, NULL, NULL, NULL, 'PROJECT', '组织的唯一业务编码', 1, 0, '吴同', 1, '2026-10-20 15:00:00', '吴同', 1, '2026-10-20 15:00:00', NULL);

-- ----------------------------
-- Table structure for om_object_type_relation
-- ----------------------------
DROP TABLE IF EXISTS `om_object_type_relation`;
CREATE TABLE `om_object_type_relation`  (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                            `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                            `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                            `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关系名称',
                                            `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关系描述',
                                            `start_type_id` bigint(20) NULL DEFAULT NULL COMMENT '起点对象类型id',
                                            `end_type_id` bigint(20) NULL DEFAULT NULL COMMENT '终点对象类型id',
                                            `reversible_flag` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '是否可逆;0：否，1：是',
                                            `reverse_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '反向名称',
                                            `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                            `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                            `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                            `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                            `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                            `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                            PRIMARY KEY (`id`, `workspace_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象类型关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_object_type_relation
-- ----------------------------
INSERT INTO `om_object_type_relation` VALUES (1, 1001, 1, '组成', '零部件通过装配构成成品，用于产品结构追溯。', 5, 4, 1, '包含零部件', 1, 0, '吴同', 1, '2026-10-20 15:25:00', '吴同', 1, '2026-10-20 15:25:00', NULL);
INSERT INTO `om_object_type_relation` VALUES (2, 1001, 1, '指定收货方', '出库单指定接收本次发货物料的采购方。', 1, 9, 1, '关联收货出库单', 1, 0, '吴同', 1, '2026-10-20 15:25:30', '吴同', 1, '2026-10-20 15:25:30', NULL);
INSERT INTO `om_object_type_relation` VALUES (3, 1001, 1, '承运', '物流服务商承担出库单对应发货物料的运输。', 8, 1, 1, '由物流服务商承运', 1, 0, '吴同', 1, '2026-10-20 15:26:00', '吴同', 1, '2026-10-20 15:26:00', NULL);
INSERT INTO `om_object_type_relation` VALUES (4, 1001, 1, '记录出库物料', '出库单记录本次办理出库和发货的物料。', 1, 7, 1, '出库记录于', 1, 0, '吴同', 1, '2026-10-20 15:26:30', '吴同', 1, '2026-10-20 15:26:30', NULL);
INSERT INTO `om_object_type_relation` VALUES (5, 1001, 1, '记录入库物料', '入库单记录实际验收并办理入库的物料。', 2, 7, 1, '入库记录于', 1, 0, '吴同', 1, '2026-10-20 15:27:00', '吴同', 1, '2026-10-20 15:27:00', NULL);
INSERT INTO `om_object_type_relation` VALUES (6, 1001, 1, '对应采购订单', '入库单关联本次到货对应的采购订单。', 2, 3, 1, '关联入库单', 1, 0, '吴同', 1, '2026-10-20 15:27:30', '吴同', 1, '2026-10-20 15:27:30', NULL);
INSERT INTO `om_object_type_relation` VALUES (7, 1001, 1, '采购物料', '采购订单记录本次需要采购的物料。', 3, 7, 1, '关联采购订单', 1, 0, '吴同', 1, '2026-10-20 15:28:00', '吴同', 1, '2026-10-20 15:28:00', NULL);
INSERT INTO `om_object_type_relation` VALUES (8, 1001, 1, '指定供应商', '采购订单指定承担本次供货任务的供应商。', 3, 10, 1, '承接采购订单', 1, 0, '吴同', 1, '2026-10-20 15:28:30', '吴同', 1, '2026-10-20 15:28:30', NULL);
INSERT INTO `om_object_type_relation` VALUES (9, 1001, 1, '下达', '采购方根据采购需求下达采购订单。', 9, 3, 1, '由采购方下达', 1, 0, '吴同', 1, '2026-10-20 15:29:00', '吴同', 1, '2026-10-20 15:29:00', NULL);
INSERT INTO `om_object_type_relation` VALUES (10, 1001, 1, '提供', '供应商向企业提供生产经营所需的物料。', 10, 7, 1, '由供应商提供', 1, 0, '吴同', 1, '2026-10-20 15:29:30', '吴同', 1, '2026-10-20 15:29:30', NULL);
INSERT INTO `om_object_type_relation` VALUES (11, 1001, 1, '管理', '组织负责采购订单的维护、审批协调和业务管理。', 11, 3, 1, '由组织管理', 1, 0, '吴同', 1, '2026-10-20 15:30:00', '吴同', 1, '2026-10-20 15:30:00', NULL);

-- ----------------------------
-- Table structure for om_object_type_target
-- ----------------------------
DROP TABLE IF EXISTS `om_object_type_target`;
CREATE TABLE `om_object_type_target`  (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                          `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                                          `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                          `target_id` bigint(20) NOT NULL COMMENT '建模目标id',
                                          `object_type_id` bigint(20) NOT NULL COMMENT '对象类型id',
                                          `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                          `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                          `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                          `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                          `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                          `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                          `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '建模目标与对象类型关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_object_type_target
-- ----------------------------
INSERT INTO `om_object_type_target` VALUES (1, 1001, 1, 1, 1, 1, 0, '吴同', 1, '2026-10-20 16:00:00', '吴同', 1, '2026-10-20 16:00:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (2, 1001, 1, 2, 1, 1, 0, '吴同', 1, '2026-10-20 16:00:30', '吴同', 1, '2026-10-20 16:00:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (3, 1001, 1, 3, 1, 1, 0, '吴同', 1, '2026-10-20 16:01:00', '吴同', 1, '2026-10-20 16:01:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (4, 1001, 1, 4, 1, 1, 0, '吴同', 1, '2026-10-20 16:01:30', '吴同', 1, '2026-10-20 16:01:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (5, 1001, 1, 5, 1, 1, 0, '吴同', 1, '2026-10-20 16:02:00', '吴同', 1, '2026-10-20 16:02:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (6, 1001, 1, 6, 1, 1, 0, '吴同', 1, '2026-10-20 16:02:30', '吴同', 1, '2026-10-20 16:02:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (7, 1001, 1, 7, 1, 1, 0, '吴同', 1, '2026-10-20 16:03:00', '吴同', 1, '2026-10-20 16:03:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (8, 1001, 1, 8, 1, 1, 0, '吴同', 1, '2026-10-20 16:03:30', '吴同', 1, '2026-10-20 16:03:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (9, 1001, 1, 9, 1, 1, 0, '吴同', 1, '2026-10-20 16:04:00', '吴同', 1, '2026-10-20 16:04:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (10, 1001, 1, 10, 1, 1, 0, '吴同', 1, '2026-10-20 16:04:30', '吴同', 1, '2026-10-20 16:04:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (11, 1001, 1, 11, 1, 1, 0, '吴同', 1, '2026-10-20 16:05:00', '吴同', 1, '2026-10-20 16:05:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (12, 1001, 1, 1, 2, 1, 0, '吴同', 1, '2026-10-20 16:05:30', '吴同', 1, '2026-10-20 16:05:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (13, 1001, 1, 2, 2, 1, 0, '吴同', 1, '2026-10-20 16:06:00', '吴同', 1, '2026-10-20 16:06:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (14, 1001, 1, 3, 2, 1, 0, '吴同', 1, '2026-10-20 16:06:30', '吴同', 1, '2026-10-20 16:06:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (15, 1001, 1, 4, 2, 1, 0, '吴同', 1, '2026-10-20 16:07:00', '吴同', 1, '2026-10-20 16:07:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (16, 1001, 1, 5, 2, 1, 0, '吴同', 1, '2026-10-20 16:07:30', '吴同', 1, '2026-10-20 16:07:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (17, 1001, 1, 6, 2, 1, 0, '吴同', 1, '2026-10-20 16:08:00', '吴同', 1, '2026-10-20 16:08:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (18, 1001, 1, 7, 2, 1, 0, '吴同', 1, '2026-10-20 16:08:30', '吴同', 1, '2026-10-20 16:08:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (19, 1001, 1, 8, 2, 1, 0, '吴同', 1, '2026-10-20 16:09:00', '吴同', 1, '2026-10-20 16:09:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (20, 1001, 1, 9, 2, 1, 0, '吴同', 1, '2026-10-20 16:09:30', '吴同', 1, '2026-10-20 16:09:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (21, 1001, 1, 10, 2, 1, 0, '吴同', 1, '2026-10-20 16:10:00', '吴同', 1, '2026-10-20 16:10:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (22, 1001, 1, 11, 2, 1, 0, '吴同', 1, '2026-10-20 16:10:30', '吴同', 1, '2026-10-20 16:10:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (23, 1001, 1, 1, 3, 1, 0, '吴同', 1, '2026-10-20 16:11:00', '吴同', 1, '2026-10-20 16:11:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (24, 1001, 1, 2, 3, 1, 0, '吴同', 1, '2026-10-20 16:11:30', '吴同', 1, '2026-10-20 16:11:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (25, 1001, 1, 3, 3, 1, 0, '吴同', 1, '2026-10-20 16:12:00', '吴同', 1, '2026-10-20 16:12:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (26, 1001, 1, 4, 3, 1, 0, '吴同', 1, '2026-10-20 16:12:30', '吴同', 1, '2026-10-20 16:12:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (27, 1001, 1, 5, 3, 1, 0, '吴同', 1, '2026-10-20 16:13:00', '吴同', 1, '2026-10-20 16:13:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (28, 1001, 1, 6, 3, 1, 0, '吴同', 1, '2026-10-20 16:13:30', '吴同', 1, '2026-10-20 16:13:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (29, 1001, 1, 7, 3, 1, 0, '吴同', 1, '2026-10-20 16:14:00', '吴同', 1, '2026-10-20 16:14:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (30, 1001, 1, 8, 3, 1, 0, '吴同', 1, '2026-10-20 16:14:30', '吴同', 1, '2026-10-20 16:14:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (31, 1001, 1, 9, 3, 1, 0, '吴同', 1, '2026-10-20 16:15:00', '吴同', 1, '2026-10-20 16:15:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (32, 1001, 1, 10, 3, 1, 0, '吴同', 1, '2026-10-20 16:15:30', '吴同', 1, '2026-10-20 16:15:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (33, 1001, 1, 11, 3, 1, 0, '吴同', 1, '2026-10-20 16:16:00', '吴同', 1, '2026-10-20 16:16:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (34, 1001, 1, 1, 4, 1, 0, '吴同', 1, '2026-10-20 16:16:30', '吴同', 1, '2026-10-20 16:16:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (35, 1001, 1, 2, 4, 1, 0, '吴同', 1, '2026-10-20 16:17:00', '吴同', 1, '2026-10-20 16:17:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (36, 1001, 1, 3, 4, 1, 0, '吴同', 1, '2026-10-20 16:17:30', '吴同', 1, '2026-10-20 16:17:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (37, 1001, 1, 4, 4, 1, 0, '吴同', 1, '2026-10-20 16:18:00', '吴同', 1, '2026-10-20 16:18:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (38, 1001, 1, 5, 4, 1, 0, '吴同', 1, '2026-10-20 16:18:30', '吴同', 1, '2026-10-20 16:18:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (39, 1001, 1, 6, 4, 1, 0, '吴同', 1, '2026-10-20 16:19:00', '吴同', 1, '2026-10-20 16:19:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (40, 1001, 1, 7, 4, 1, 0, '吴同', 1, '2026-10-20 16:19:30', '吴同', 1, '2026-10-20 16:19:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (41, 1001, 1, 8, 4, 1, 0, '吴同', 1, '2026-10-20 16:20:00', '吴同', 1, '2026-10-20 16:20:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (42, 1001, 1, 9, 4, 1, 0, '吴同', 1, '2026-10-20 16:20:30', '吴同', 1, '2026-10-20 16:20:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (43, 1001, 1, 10, 4, 1, 0, '吴同', 1, '2026-10-20 16:21:00', '吴同', 1, '2026-10-20 16:21:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (44, 1001, 1, 11, 4, 1, 0, '吴同', 1, '2026-10-20 16:21:30', '吴同', 1, '2026-10-20 16:21:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (45, 1001, 1, 1, 5, 1, 0, '吴同', 1, '2026-10-20 16:22:00', '吴同', 1, '2026-10-20 16:22:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (46, 1001, 1, 2, 5, 1, 0, '吴同', 1, '2026-10-20 16:22:30', '吴同', 1, '2026-10-20 16:22:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (47, 1001, 1, 3, 5, 1, 0, '吴同', 1, '2026-10-20 16:23:00', '吴同', 1, '2026-10-20 16:23:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (48, 1001, 1, 4, 5, 1, 0, '吴同', 1, '2026-10-20 16:23:30', '吴同', 1, '2026-10-20 16:23:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (49, 1001, 1, 5, 5, 1, 0, '吴同', 1, '2026-10-20 16:24:00', '吴同', 1, '2026-10-20 16:24:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (50, 1001, 1, 6, 5, 1, 0, '吴同', 1, '2026-10-20 16:24:30', '吴同', 1, '2026-10-20 16:24:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (51, 1001, 1, 7, 5, 1, 0, '吴同', 1, '2026-10-20 16:25:00', '吴同', 1, '2026-10-20 16:25:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (52, 1001, 1, 8, 5, 1, 0, '吴同', 1, '2026-10-20 16:25:30', '吴同', 1, '2026-10-20 16:25:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (53, 1001, 1, 9, 5, 1, 0, '吴同', 1, '2026-10-20 16:26:00', '吴同', 1, '2026-10-20 16:26:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (54, 1001, 1, 10, 5, 1, 0, '吴同', 1, '2026-10-20 16:26:30', '吴同', 1, '2026-10-20 16:26:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (55, 1001, 1, 11, 5, 1, 0, '吴同', 1, '2026-10-20 16:27:00', '吴同', 1, '2026-10-20 16:27:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (56, 1001, 1, 1, 6, 1, 0, '吴同', 1, '2026-10-20 16:27:30', '吴同', 1, '2026-10-20 16:27:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (57, 1001, 1, 2, 6, 1, 0, '吴同', 1, '2026-10-20 16:28:00', '吴同', 1, '2026-10-20 16:28:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (58, 1001, 1, 3, 6, 1, 0, '吴同', 1, '2026-10-20 16:28:30', '吴同', 1, '2026-10-20 16:28:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (59, 1001, 1, 4, 6, 1, 0, '吴同', 1, '2026-10-20 16:29:00', '吴同', 1, '2026-10-20 16:29:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (60, 1001, 1, 5, 6, 1, 0, '吴同', 1, '2026-10-20 16:29:30', '吴同', 1, '2026-10-20 16:29:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (61, 1001, 1, 6, 6, 1, 0, '吴同', 1, '2026-10-20 16:30:00', '吴同', 1, '2026-10-20 16:30:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (62, 1001, 1, 7, 6, 1, 0, '吴同', 1, '2026-10-20 16:30:30', '吴同', 1, '2026-10-20 16:30:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (63, 1001, 1, 8, 6, 1, 0, '吴同', 1, '2026-10-20 16:31:00', '吴同', 1, '2026-10-20 16:31:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (64, 1001, 1, 9, 6, 1, 0, '吴同', 1, '2026-10-20 16:31:30', '吴同', 1, '2026-10-20 16:31:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (65, 1001, 1, 10, 6, 1, 0, '吴同', 1, '2026-10-20 16:32:00', '吴同', 1, '2026-10-20 16:32:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (66, 1001, 1, 11, 6, 1, 0, '吴同', 1, '2026-10-20 16:32:30', '吴同', 1, '2026-10-20 16:32:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (67, 1001, 1, 1, 7, 1, 0, '吴同', 1, '2026-10-20 16:33:00', '吴同', 1, '2026-10-20 16:33:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (68, 1001, 1, 2, 7, 1, 0, '吴同', 1, '2026-10-20 16:33:30', '吴同', 1, '2026-10-20 16:33:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (69, 1001, 1, 3, 7, 1, 0, '吴同', 1, '2026-10-20 16:34:00', '吴同', 1, '2026-10-20 16:34:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (70, 1001, 1, 4, 7, 1, 0, '吴同', 1, '2026-10-20 16:34:30', '吴同', 1, '2026-10-20 16:34:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (71, 1001, 1, 5, 7, 1, 0, '吴同', 1, '2026-10-20 16:35:00', '吴同', 1, '2026-10-20 16:35:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (72, 1001, 1, 6, 7, 1, 0, '吴同', 1, '2026-10-20 16:35:30', '吴同', 1, '2026-10-20 16:35:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (73, 1001, 1, 7, 7, 1, 0, '吴同', 1, '2026-10-20 16:36:00', '吴同', 1, '2026-10-20 16:36:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (74, 1001, 1, 8, 7, 1, 0, '吴同', 1, '2026-10-20 16:36:30', '吴同', 1, '2026-10-20 16:36:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (75, 1001, 1, 9, 7, 1, 0, '吴同', 1, '2026-10-20 16:37:00', '吴同', 1, '2026-10-20 16:37:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (76, 1001, 1, 10, 7, 1, 0, '吴同', 1, '2026-10-20 16:37:30', '吴同', 1, '2026-10-20 16:37:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (77, 1001, 1, 11, 7, 1, 0, '吴同', 1, '2026-10-20 16:38:00', '吴同', 1, '2026-10-20 16:38:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (78, 1001, 1, 1, 8, 1, 0, '吴同', 1, '2026-10-20 16:38:30', '吴同', 1, '2026-10-20 16:38:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (79, 1001, 1, 2, 8, 1, 0, '吴同', 1, '2026-10-20 16:39:00', '吴同', 1, '2026-10-20 16:39:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (80, 1001, 1, 3, 8, 1, 0, '吴同', 1, '2026-10-20 16:39:30', '吴同', 1, '2026-10-20 16:39:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (81, 1001, 1, 4, 8, 1, 0, '吴同', 1, '2026-10-20 16:40:00', '吴同', 1, '2026-10-20 16:40:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (82, 1001, 1, 5, 8, 1, 0, '吴同', 1, '2026-10-20 16:40:30', '吴同', 1, '2026-10-20 16:40:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (83, 1001, 1, 6, 8, 1, 0, '吴同', 1, '2026-10-20 16:41:00', '吴同', 1, '2026-10-20 16:41:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (84, 1001, 1, 7, 8, 1, 0, '吴同', 1, '2026-10-20 16:41:30', '吴同', 1, '2026-10-20 16:41:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (85, 1001, 1, 8, 8, 1, 0, '吴同', 1, '2026-10-20 16:42:00', '吴同', 1, '2026-10-20 16:42:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (86, 1001, 1, 9, 8, 1, 0, '吴同', 1, '2026-10-20 16:42:30', '吴同', 1, '2026-10-20 16:42:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (87, 1001, 1, 10, 8, 1, 0, '吴同', 1, '2026-10-20 16:43:00', '吴同', 1, '2026-10-20 16:43:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (88, 1001, 1, 11, 8, 1, 0, '吴同', 1, '2026-10-20 16:43:30', '吴同', 1, '2026-10-20 16:43:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (89, 1001, 1, 1, 9, 1, 0, '吴同', 1, '2026-10-20 16:44:00', '吴同', 1, '2026-10-20 16:44:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (90, 1001, 1, 2, 9, 1, 0, '吴同', 1, '2026-10-20 16:44:30', '吴同', 1, '2026-10-20 16:44:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (91, 1001, 1, 3, 9, 1, 0, '吴同', 1, '2026-10-20 16:45:00', '吴同', 1, '2026-10-20 16:45:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (92, 1001, 1, 4, 9, 1, 0, '吴同', 1, '2026-10-20 16:45:30', '吴同', 1, '2026-10-20 16:45:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (93, 1001, 1, 5, 9, 1, 0, '吴同', 1, '2026-10-20 16:46:00', '吴同', 1, '2026-10-20 16:46:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (94, 1001, 1, 6, 9, 1, 0, '吴同', 1, '2026-10-20 16:46:30', '吴同', 1, '2026-10-20 16:46:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (95, 1001, 1, 7, 9, 1, 0, '吴同', 1, '2026-10-20 16:47:00', '吴同', 1, '2026-10-20 16:47:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (96, 1001, 1, 8, 9, 1, 0, '吴同', 1, '2026-10-20 16:47:30', '吴同', 1, '2026-10-20 16:47:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (97, 1001, 1, 9, 9, 1, 0, '吴同', 1, '2026-10-20 16:48:00', '吴同', 1, '2026-10-20 16:48:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (98, 1001, 1, 10, 9, 1, 0, '吴同', 1, '2026-10-20 16:48:30', '吴同', 1, '2026-10-20 16:48:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (99, 1001, 1, 11, 9, 1, 0, '吴同', 1, '2026-10-20 16:49:00', '吴同', 1, '2026-10-20 16:49:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (100, 1001, 1, 1, 10, 1, 0, '吴同', 1, '2026-10-20 16:49:30', '吴同', 1, '2026-10-20 16:49:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (101, 1001, 1, 2, 10, 1, 0, '吴同', 1, '2026-10-20 16:50:00', '吴同', 1, '2026-10-20 16:50:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (102, 1001, 1, 3, 10, 1, 0, '吴同', 1, '2026-10-20 16:50:30', '吴同', 1, '2026-10-20 16:50:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (103, 1001, 1, 4, 10, 1, 0, '吴同', 1, '2026-10-20 16:51:00', '吴同', 1, '2026-10-20 16:51:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (104, 1001, 1, 5, 10, 1, 0, '吴同', 1, '2026-10-20 16:51:30', '吴同', 1, '2026-10-20 16:51:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (105, 1001, 1, 6, 10, 1, 0, '吴同', 1, '2026-10-20 16:52:00', '吴同', 1, '2026-10-20 16:52:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (106, 1001, 1, 7, 10, 1, 0, '吴同', 1, '2026-10-20 16:52:30', '吴同', 1, '2026-10-20 16:52:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (107, 1001, 1, 8, 10, 1, 0, '吴同', 1, '2026-10-20 16:53:00', '吴同', 1, '2026-10-20 16:53:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (108, 1001, 1, 9, 10, 1, 0, '吴同', 1, '2026-10-20 16:53:30', '吴同', 1, '2026-10-20 16:53:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (109, 1001, 1, 10, 10, 1, 0, '吴同', 1, '2026-10-20 16:54:00', '吴同', 1, '2026-10-20 16:54:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (110, 1001, 1, 11, 10, 1, 0, '吴同', 1, '2026-10-20 16:54:30', '吴同', 1, '2026-10-20 16:54:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (111, 1001, 1, 1, 11, 1, 0, '吴同', 1, '2026-10-20 16:55:00', '吴同', 1, '2026-10-20 16:55:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (112, 1001, 1, 2, 11, 1, 0, '吴同', 1, '2026-10-20 16:55:30', '吴同', 1, '2026-10-20 16:55:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (113, 1001, 1, 3, 11, 1, 0, '吴同', 1, '2026-10-20 16:56:00', '吴同', 1, '2026-10-20 16:56:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (114, 1001, 1, 4, 11, 1, 0, '吴同', 1, '2026-10-20 16:56:30', '吴同', 1, '2026-10-20 16:56:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (115, 1001, 1, 5, 11, 1, 0, '吴同', 1, '2026-10-20 16:57:00', '吴同', 1, '2026-10-20 16:57:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (116, 1001, 1, 6, 11, 1, 0, '吴同', 1, '2026-10-20 16:57:30', '吴同', 1, '2026-10-20 16:57:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (117, 1001, 1, 7, 11, 1, 0, '吴同', 1, '2026-10-20 16:58:00', '吴同', 1, '2026-10-20 16:58:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (118, 1001, 1, 8, 11, 1, 0, '吴同', 1, '2026-10-20 16:58:30', '吴同', 1, '2026-10-20 16:58:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (119, 1001, 1, 9, 11, 1, 0, '吴同', 1, '2026-10-20 16:59:00', '吴同', 1, '2026-10-20 16:59:00', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (120, 1001, 1, 10, 11, 1, 0, '吴同', 1, '2026-10-20 16:59:30', '吴同', 1, '2026-10-20 16:59:30', '列表及分页测试关联');
INSERT INTO `om_object_type_target` VALUES (121, 1001, 1, 11, 11, 1, 0, '吴同', 1, '2026-10-20 17:00:00', '吴同', 1, '2026-10-20 17:00:00', '列表及分页测试关联');

-- ----------------------------
-- Table structure for om_relation_target
-- ----------------------------
DROP TABLE IF EXISTS `om_relation_target`;
CREATE TABLE `om_relation_target`  (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                       `workspace_id` bigint(20) NULL DEFAULT NULL COMMENT '工作区id',
                                       `version_id` bigint(20) NULL DEFAULT NULL COMMENT '版本id',
                                       `target_id` bigint(20) NULL DEFAULT NULL COMMENT '建模目标id',
                                       `relation_id` bigint(20) NULL DEFAULT NULL COMMENT '对象类型关系id',
                                       `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                       `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                       `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                       `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                       `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                       `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                       `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                       PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '对象类型关系与建模目标关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_relation_target
-- ----------------------------
INSERT INTO `om_relation_target` VALUES (1, 1001, 1, 1, 1, 1, 0, '吴同', 1, '2026-10-20 17:30:00', '吴同', 1, '2026-10-20 17:30:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (2, 1001, 1, 2, 1, 1, 0, '吴同', 1, '2026-10-20 17:30:30', '吴同', 1, '2026-10-20 17:30:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (3, 1001, 1, 3, 1, 1, 0, '吴同', 1, '2026-10-20 17:31:00', '吴同', 1, '2026-10-20 17:31:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (4, 1001, 1, 4, 1, 1, 0, '吴同', 1, '2026-10-20 17:31:30', '吴同', 1, '2026-10-20 17:31:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (5, 1001, 1, 5, 1, 1, 0, '吴同', 1, '2026-10-20 17:32:00', '吴同', 1, '2026-10-20 17:32:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (6, 1001, 1, 6, 1, 1, 0, '吴同', 1, '2026-10-20 17:32:30', '吴同', 1, '2026-10-20 17:32:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (7, 1001, 1, 7, 1, 1, 0, '吴同', 1, '2026-10-20 17:33:00', '吴同', 1, '2026-10-20 17:33:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (8, 1001, 1, 8, 1, 1, 0, '吴同', 1, '2026-10-20 17:33:30', '吴同', 1, '2026-10-20 17:33:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (9, 1001, 1, 9, 1, 1, 0, '吴同', 1, '2026-10-20 17:34:00', '吴同', 1, '2026-10-20 17:34:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (10, 1001, 1, 10, 1, 1, 0, '吴同', 1, '2026-10-20 17:34:30', '吴同', 1, '2026-10-20 17:34:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (11, 1001, 1, 11, 1, 1, 0, '吴同', 1, '2026-10-20 17:35:00', '吴同', 1, '2026-10-20 17:35:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (12, 1001, 1, 1, 2, 1, 0, '吴同', 1, '2026-10-20 17:35:30', '吴同', 1, '2026-10-20 17:35:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (13, 1001, 1, 2, 2, 1, 0, '吴同', 1, '2026-10-20 17:36:00', '吴同', 1, '2026-10-20 17:36:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (14, 1001, 1, 3, 2, 1, 0, '吴同', 1, '2026-10-20 17:36:30', '吴同', 1, '2026-10-20 17:36:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (15, 1001, 1, 4, 2, 1, 0, '吴同', 1, '2026-10-20 17:37:00', '吴同', 1, '2026-10-20 17:37:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (16, 1001, 1, 5, 2, 1, 0, '吴同', 1, '2026-10-20 17:37:30', '吴同', 1, '2026-10-20 17:37:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (17, 1001, 1, 6, 2, 1, 0, '吴同', 1, '2026-10-20 17:38:00', '吴同', 1, '2026-10-20 17:38:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (18, 1001, 1, 7, 2, 1, 0, '吴同', 1, '2026-10-20 17:38:30', '吴同', 1, '2026-10-20 17:38:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (19, 1001, 1, 8, 2, 1, 0, '吴同', 1, '2026-10-20 17:39:00', '吴同', 1, '2026-10-20 17:39:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (20, 1001, 1, 9, 2, 1, 0, '吴同', 1, '2026-10-20 17:39:30', '吴同', 1, '2026-10-20 17:39:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (21, 1001, 1, 10, 2, 1, 0, '吴同', 1, '2026-10-20 17:40:00', '吴同', 1, '2026-10-20 17:40:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (22, 1001, 1, 11, 2, 1, 0, '吴同', 1, '2026-10-20 17:40:30', '吴同', 1, '2026-10-20 17:40:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (23, 1001, 1, 1, 3, 1, 0, '吴同', 1, '2026-10-20 17:41:00', '吴同', 1, '2026-10-20 17:41:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (24, 1001, 1, 2, 3, 1, 0, '吴同', 1, '2026-10-20 17:41:30', '吴同', 1, '2026-10-20 17:41:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (25, 1001, 1, 3, 3, 1, 0, '吴同', 1, '2026-10-20 17:42:00', '吴同', 1, '2026-10-20 17:42:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (26, 1001, 1, 4, 3, 1, 0, '吴同', 1, '2026-10-20 17:42:30', '吴同', 1, '2026-10-20 17:42:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (27, 1001, 1, 5, 3, 1, 0, '吴同', 1, '2026-10-20 17:43:00', '吴同', 1, '2026-10-20 17:43:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (28, 1001, 1, 6, 3, 1, 0, '吴同', 1, '2026-10-20 17:43:30', '吴同', 1, '2026-10-20 17:43:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (29, 1001, 1, 7, 3, 1, 0, '吴同', 1, '2026-10-20 17:44:00', '吴同', 1, '2026-10-20 17:44:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (30, 1001, 1, 8, 3, 1, 0, '吴同', 1, '2026-10-20 17:44:30', '吴同', 1, '2026-10-20 17:44:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (31, 1001, 1, 9, 3, 1, 0, '吴同', 1, '2026-10-20 17:45:00', '吴同', 1, '2026-10-20 17:45:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (32, 1001, 1, 10, 3, 1, 0, '吴同', 1, '2026-10-20 17:45:30', '吴同', 1, '2026-10-20 17:45:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (33, 1001, 1, 11, 3, 1, 0, '吴同', 1, '2026-10-20 17:46:00', '吴同', 1, '2026-10-20 17:46:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (34, 1001, 1, 1, 4, 1, 0, '吴同', 1, '2026-10-20 17:46:30', '吴同', 1, '2026-10-20 17:46:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (35, 1001, 1, 2, 4, 1, 0, '吴同', 1, '2026-10-20 17:47:00', '吴同', 1, '2026-10-20 17:47:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (36, 1001, 1, 3, 4, 1, 0, '吴同', 1, '2026-10-20 17:47:30', '吴同', 1, '2026-10-20 17:47:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (37, 1001, 1, 4, 4, 1, 0, '吴同', 1, '2026-10-20 17:48:00', '吴同', 1, '2026-10-20 17:48:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (38, 1001, 1, 5, 4, 1, 0, '吴同', 1, '2026-10-20 17:48:30', '吴同', 1, '2026-10-20 17:48:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (39, 1001, 1, 6, 4, 1, 0, '吴同', 1, '2026-10-20 17:49:00', '吴同', 1, '2026-10-20 17:49:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (40, 1001, 1, 7, 4, 1, 0, '吴同', 1, '2026-10-20 17:49:30', '吴同', 1, '2026-10-20 17:49:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (41, 1001, 1, 8, 4, 1, 0, '吴同', 1, '2026-10-20 17:50:00', '吴同', 1, '2026-10-20 17:50:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (42, 1001, 1, 9, 4, 1, 0, '吴同', 1, '2026-10-20 17:50:30', '吴同', 1, '2026-10-20 17:50:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (43, 1001, 1, 10, 4, 1, 0, '吴同', 1, '2026-10-20 17:51:00', '吴同', 1, '2026-10-20 17:51:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (44, 1001, 1, 11, 4, 1, 0, '吴同', 1, '2026-10-20 17:51:30', '吴同', 1, '2026-10-20 17:51:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (45, 1001, 1, 1, 5, 1, 0, '吴同', 1, '2026-10-20 17:52:00', '吴同', 1, '2026-10-20 17:52:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (46, 1001, 1, 2, 5, 1, 0, '吴同', 1, '2026-10-20 17:52:30', '吴同', 1, '2026-10-20 17:52:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (47, 1001, 1, 3, 5, 1, 0, '吴同', 1, '2026-10-20 17:53:00', '吴同', 1, '2026-10-20 17:53:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (48, 1001, 1, 4, 5, 1, 0, '吴同', 1, '2026-10-20 17:53:30', '吴同', 1, '2026-10-20 17:53:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (49, 1001, 1, 5, 5, 1, 0, '吴同', 1, '2026-10-20 17:54:00', '吴同', 1, '2026-10-20 17:54:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (50, 1001, 1, 6, 5, 1, 0, '吴同', 1, '2026-10-20 17:54:30', '吴同', 1, '2026-10-20 17:54:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (51, 1001, 1, 7, 5, 1, 0, '吴同', 1, '2026-10-20 17:55:00', '吴同', 1, '2026-10-20 17:55:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (52, 1001, 1, 8, 5, 1, 0, '吴同', 1, '2026-10-20 17:55:30', '吴同', 1, '2026-10-20 17:55:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (53, 1001, 1, 9, 5, 1, 0, '吴同', 1, '2026-10-20 17:56:00', '吴同', 1, '2026-10-20 17:56:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (54, 1001, 1, 10, 5, 1, 0, '吴同', 1, '2026-10-20 17:56:30', '吴同', 1, '2026-10-20 17:56:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (55, 1001, 1, 11, 5, 1, 0, '吴同', 1, '2026-10-20 17:57:00', '吴同', 1, '2026-10-20 17:57:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (56, 1001, 1, 1, 6, 1, 0, '吴同', 1, '2026-10-20 17:57:30', '吴同', 1, '2026-10-20 17:57:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (57, 1001, 1, 2, 6, 1, 0, '吴同', 1, '2026-10-20 17:58:00', '吴同', 1, '2026-10-20 17:58:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (58, 1001, 1, 3, 6, 1, 0, '吴同', 1, '2026-10-20 17:58:30', '吴同', 1, '2026-10-20 17:58:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (59, 1001, 1, 4, 6, 1, 0, '吴同', 1, '2026-10-20 17:59:00', '吴同', 1, '2026-10-20 17:59:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (60, 1001, 1, 5, 6, 1, 0, '吴同', 1, '2026-10-20 17:59:30', '吴同', 1, '2026-10-20 17:59:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (61, 1001, 1, 6, 6, 1, 0, '吴同', 1, '2026-10-20 18:00:00', '吴同', 1, '2026-10-20 18:00:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (62, 1001, 1, 7, 6, 1, 0, '吴同', 1, '2026-10-20 18:00:30', '吴同', 1, '2026-10-20 18:00:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (63, 1001, 1, 8, 6, 1, 0, '吴同', 1, '2026-10-20 18:01:00', '吴同', 1, '2026-10-20 18:01:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (64, 1001, 1, 9, 6, 1, 0, '吴同', 1, '2026-10-20 18:01:30', '吴同', 1, '2026-10-20 18:01:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (65, 1001, 1, 10, 6, 1, 0, '吴同', 1, '2026-10-20 18:02:00', '吴同', 1, '2026-10-20 18:02:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (66, 1001, 1, 11, 6, 1, 0, '吴同', 1, '2026-10-20 18:02:30', '吴同', 1, '2026-10-20 18:02:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (67, 1001, 1, 1, 7, 1, 0, '吴同', 1, '2026-10-20 18:03:00', '吴同', 1, '2026-10-20 18:03:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (68, 1001, 1, 2, 7, 1, 0, '吴同', 1, '2026-10-20 18:03:30', '吴同', 1, '2026-10-20 18:03:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (69, 1001, 1, 3, 7, 1, 0, '吴同', 1, '2026-10-20 18:04:00', '吴同', 1, '2026-10-20 18:04:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (70, 1001, 1, 4, 7, 1, 0, '吴同', 1, '2026-10-20 18:04:30', '吴同', 1, '2026-10-20 18:04:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (71, 1001, 1, 5, 7, 1, 0, '吴同', 1, '2026-10-20 18:05:00', '吴同', 1, '2026-10-20 18:05:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (72, 1001, 1, 6, 7, 1, 0, '吴同', 1, '2026-10-20 18:05:30', '吴同', 1, '2026-10-20 18:05:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (73, 1001, 1, 7, 7, 1, 0, '吴同', 1, '2026-10-20 18:06:00', '吴同', 1, '2026-10-20 18:06:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (74, 1001, 1, 8, 7, 1, 0, '吴同', 1, '2026-10-20 18:06:30', '吴同', 1, '2026-10-20 18:06:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (75, 1001, 1, 9, 7, 1, 0, '吴同', 1, '2026-10-20 18:07:00', '吴同', 1, '2026-10-20 18:07:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (76, 1001, 1, 10, 7, 1, 0, '吴同', 1, '2026-10-20 18:07:30', '吴同', 1, '2026-10-20 18:07:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (77, 1001, 1, 11, 7, 1, 0, '吴同', 1, '2026-10-20 18:08:00', '吴同', 1, '2026-10-20 18:08:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (78, 1001, 1, 1, 8, 1, 0, '吴同', 1, '2026-10-20 18:08:30', '吴同', 1, '2026-10-20 18:08:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (79, 1001, 1, 2, 8, 1, 0, '吴同', 1, '2026-10-20 18:09:00', '吴同', 1, '2026-10-20 18:09:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (80, 1001, 1, 3, 8, 1, 0, '吴同', 1, '2026-10-20 18:09:30', '吴同', 1, '2026-10-20 18:09:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (81, 1001, 1, 4, 8, 1, 0, '吴同', 1, '2026-10-20 18:10:00', '吴同', 1, '2026-10-20 18:10:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (82, 1001, 1, 5, 8, 1, 0, '吴同', 1, '2026-10-20 18:10:30', '吴同', 1, '2026-10-20 18:10:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (83, 1001, 1, 6, 8, 1, 0, '吴同', 1, '2026-10-20 18:11:00', '吴同', 1, '2026-10-20 18:11:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (84, 1001, 1, 7, 8, 1, 0, '吴同', 1, '2026-10-20 18:11:30', '吴同', 1, '2026-10-20 18:11:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (85, 1001, 1, 8, 8, 1, 0, '吴同', 1, '2026-10-20 18:12:00', '吴同', 1, '2026-10-20 18:12:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (86, 1001, 1, 9, 8, 1, 0, '吴同', 1, '2026-10-20 18:12:30', '吴同', 1, '2026-10-20 18:12:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (87, 1001, 1, 10, 8, 1, 0, '吴同', 1, '2026-10-20 18:13:00', '吴同', 1, '2026-10-20 18:13:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (88, 1001, 1, 11, 8, 1, 0, '吴同', 1, '2026-10-20 18:13:30', '吴同', 1, '2026-10-20 18:13:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (89, 1001, 1, 1, 9, 1, 0, '吴同', 1, '2026-10-20 18:14:00', '吴同', 1, '2026-10-20 18:14:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (90, 1001, 1, 2, 9, 1, 0, '吴同', 1, '2026-10-20 18:14:30', '吴同', 1, '2026-10-20 18:14:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (91, 1001, 1, 3, 9, 1, 0, '吴同', 1, '2026-10-20 18:15:00', '吴同', 1, '2026-10-20 18:15:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (92, 1001, 1, 4, 9, 1, 0, '吴同', 1, '2026-10-20 18:15:30', '吴同', 1, '2026-10-20 18:15:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (93, 1001, 1, 5, 9, 1, 0, '吴同', 1, '2026-10-20 18:16:00', '吴同', 1, '2026-10-20 18:16:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (94, 1001, 1, 6, 9, 1, 0, '吴同', 1, '2026-10-20 18:16:30', '吴同', 1, '2026-10-20 18:16:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (95, 1001, 1, 7, 9, 1, 0, '吴同', 1, '2026-10-20 18:17:00', '吴同', 1, '2026-10-20 18:17:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (96, 1001, 1, 8, 9, 1, 0, '吴同', 1, '2026-10-20 18:17:30', '吴同', 1, '2026-10-20 18:17:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (97, 1001, 1, 9, 9, 1, 0, '吴同', 1, '2026-10-20 18:18:00', '吴同', 1, '2026-10-20 18:18:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (98, 1001, 1, 10, 9, 1, 0, '吴同', 1, '2026-10-20 18:18:30', '吴同', 1, '2026-10-20 18:18:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (99, 1001, 1, 11, 9, 1, 0, '吴同', 1, '2026-10-20 18:19:00', '吴同', 1, '2026-10-20 18:19:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (100, 1001, 1, 1, 10, 1, 0, '吴同', 1, '2026-10-20 18:19:30', '吴同', 1, '2026-10-20 18:19:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (101, 1001, 1, 2, 10, 1, 0, '吴同', 1, '2026-10-20 18:20:00', '吴同', 1, '2026-10-20 18:20:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (102, 1001, 1, 3, 10, 1, 0, '吴同', 1, '2026-10-20 18:20:30', '吴同', 1, '2026-10-20 18:20:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (103, 1001, 1, 4, 10, 1, 0, '吴同', 1, '2026-10-20 18:21:00', '吴同', 1, '2026-10-20 18:21:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (104, 1001, 1, 5, 10, 1, 0, '吴同', 1, '2026-10-20 18:21:30', '吴同', 1, '2026-10-20 18:21:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (105, 1001, 1, 6, 10, 1, 0, '吴同', 1, '2026-10-20 18:22:00', '吴同', 1, '2026-10-20 18:22:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (106, 1001, 1, 7, 10, 1, 0, '吴同', 1, '2026-10-20 18:22:30', '吴同', 1, '2026-10-20 18:22:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (107, 1001, 1, 8, 10, 1, 0, '吴同', 1, '2026-10-20 18:23:00', '吴同', 1, '2026-10-20 18:23:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (108, 1001, 1, 9, 10, 1, 0, '吴同', 1, '2026-10-20 18:23:30', '吴同', 1, '2026-10-20 18:23:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (109, 1001, 1, 10, 10, 1, 0, '吴同', 1, '2026-10-20 18:24:00', '吴同', 1, '2026-10-20 18:24:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (110, 1001, 1, 11, 10, 1, 0, '吴同', 1, '2026-10-20 18:24:30', '吴同', 1, '2026-10-20 18:24:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (111, 1001, 1, 1, 11, 1, 0, '吴同', 1, '2026-10-20 18:25:00', '吴同', 1, '2026-10-20 18:25:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (112, 1001, 1, 2, 11, 1, 0, '吴同', 1, '2026-10-20 18:25:30', '吴同', 1, '2026-10-20 18:25:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (113, 1001, 1, 3, 11, 1, 0, '吴同', 1, '2026-10-20 18:26:00', '吴同', 1, '2026-10-20 18:26:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (114, 1001, 1, 4, 11, 1, 0, '吴同', 1, '2026-10-20 18:26:30', '吴同', 1, '2026-10-20 18:26:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (115, 1001, 1, 5, 11, 1, 0, '吴同', 1, '2026-10-20 18:27:00', '吴同', 1, '2026-10-20 18:27:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (116, 1001, 1, 6, 11, 1, 0, '吴同', 1, '2026-10-20 18:27:30', '吴同', 1, '2026-10-20 18:27:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (117, 1001, 1, 7, 11, 1, 0, '吴同', 1, '2026-10-20 18:28:00', '吴同', 1, '2026-10-20 18:28:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (118, 1001, 1, 8, 11, 1, 0, '吴同', 1, '2026-10-20 18:28:30', '吴同', 1, '2026-10-20 18:28:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (119, 1001, 1, 9, 11, 1, 0, '吴同', 1, '2026-10-20 18:29:00', '吴同', 1, '2026-10-20 18:29:00', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (120, 1001, 1, 10, 11, 1, 0, '吴同', 1, '2026-10-20 18:29:30', '吴同', 1, '2026-10-20 18:29:30', '列表及分页测试关联');
INSERT INTO `om_relation_target` VALUES (121, 1001, 1, 11, 11, 1, 0, '吴同', 1, '2026-10-20 18:30:00', '吴同', 1, '2026-10-20 18:30:00', '列表及分页测试关联');

-- ----------------------------
-- Table structure for om_target
-- ----------------------------
DROP TABLE IF EXISTS `om_target`;
CREATE TABLE `om_target`  (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `version_id` bigint(20) NOT NULL COMMENT '项目版本id',
                              `question` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标问题',
                              `question_source` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '问题来源',
                              `business_scenarios` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务场景',
                              `priority` tinyint(3) UNSIGNED NOT NULL COMMENT '优先级;1低；2中；3高',
                              `accept_criterion` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '验收标准',
                              `accept_status` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '验证状态;0待验证；1已覆盖；2未覆盖',
                              `object_type_num` int(11) NULL DEFAULT NULL COMMENT '对象类型数量',
                              `object_attribute_num` int(11) NULL DEFAULT NULL COMMENT '对象类型属性数量',
                              `object_relationship_num` int(11) NULL DEFAULT NULL COMMENT '对象类型关系数量',
                              `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                              `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                              `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                              `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                              `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                              `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`id`, `priority`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '建模目标' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_target
-- ----------------------------
INSERT INTO `om_target` VALUES (1, 1, '指定成品由哪些零部件组成？某零部件用于哪些成品？', '产品研发部门提出', '产品组成与影响范围分析', 3, '能够查询成品包含的零部件，并支持按零部件反向查询其应用的成品。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:05:00', '吴同', 1, '2026-10-20 10:05:00', NULL);
INSERT INTO `om_target` VALUES (2, 1, '指定出库单的收货方是谁，实际发往哪个地址？', '销售及物流部门提出', '发货与收货核对', 2, '能够查询出库单关联的采购方，并展示收货地址及采购方联系信息。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:05:30', '吴同', 1, '2026-10-20 10:05:30', NULL);
INSERT INTO `om_target` VALUES (3, 1, '指定出库单由哪家物流服务商承运？', '物流管理部门提出', '运输责任追溯', 3, '能够按出库单查询承运商，并支持查询承运商承担的出库发货记录。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:06:00', '吴同', 1, '2026-10-20 10:06:00', NULL);
INSERT INTO `om_target` VALUES (4, 1, '指定物料涉及哪些出库单，分别用于什么业务？', '仓储部门提出', '物料出库流向追溯', 2, '能够按物料查询相关出库单，并展示出库日期、用途、数量及经办人员。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:06:30', '吴同', 1, '2026-10-20 10:06:30', NULL);
INSERT INTO `om_target` VALUES (5, 1, '指定入库单记录了哪些到货物料，验收结果如何？', '质量管理部门提出', '到货验收追溯', 3, '能够查询入库单对应的物料，并展示入库数量、计量单位、验收结果和验收人员。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:07:00', '吴同', 1, '2026-10-20 10:07:00', NULL);
INSERT INTO `om_target` VALUES (6, 1, '哪些采购订单尚未关联入库单？已到货订单对应哪些入库记录？', '仓储部门提出', '采购到货核对', 3, '能够识别未关联入库单的采购订单，并查询已关联订单的入库记录。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:07:30', '吴同', 1, '2026-10-20 10:07:30', NULL);
INSERT INTO `om_target` VALUES (7, 1, '指定采购订单采购了哪些物料？某物料涉及哪些采购订单？', '生产计划部门提出', '采购需求与物料追踪', 3, '能够在采购订单与物料之间进行双向查询，并展示物料编码、名称和计量单位。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:08:00', '吴同', 1, '2026-10-20 10:08:00', NULL);
INSERT INTO `om_target` VALUES (8, 1, '采购订单分别由哪些供应商承接？', '采购部门提出', '订单供货责任追溯', 3, '能够按采购订单查询供应商，并支持按供应商查询其承接的采购订单。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:08:30', '吴同', 1, '2026-10-20 10:08:30', NULL);
INSERT INTO `om_target` VALUES (9, 1, '指定采购方下达了哪些采购订单？', '采购部门提出', '采购业务执行追踪', 2, '能够按采购方查询其下达的采购订单，并展示订单编号、日期及执行状态。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:09:00', '吴同', 1, '2026-10-20 10:09:00', NULL);
INSERT INTO `om_target` VALUES (10, 1, '指定供应商能够提供哪些物料？同一物料有哪些供应来源？', '供应链管理部门提出', '供应来源分析', 3, '能够查询供应商提供的物料清单，并支持按物料查询对应的供应商。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:09:30', '吴同', 1, '2026-10-20 10:09:30', NULL);
INSERT INTO `om_target` VALUES (11, 1, '各组织负责哪些采购订单的维护和管理？', '采购管理部门提出', '采购职责与管理范围梳理', 2, '能够按组织查询其管理的采购订单，并支持从采购订单反向查询管理组织。', 0, 11, 0, 11, 1, 0, '吴同', 1, '2026-10-20 10:10:00', '吴同', 1, '2026-10-20 10:10:00', NULL);

-- ----------------------------
-- Table structure for om_target_task
-- ----------------------------
DROP TABLE IF EXISTS `om_target_task`;
CREATE TABLE `om_target_task`  (
                                   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `target_id` bigint(20) NULL DEFAULT NULL COMMENT '目标id',
                                   `accept_result` tinyint(3) UNSIGNED NULL DEFAULT NULL COMMENT '验证结果;1已覆盖；2未覆盖',
                                   `task_explain` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '判断说明',
                                   `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                   `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                   `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '建模目标验证任务' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_target_task
-- ----------------------------

-- ----------------------------
-- Table structure for om_term
-- ----------------------------
DROP TABLE IF EXISTS `om_term`;
CREATE TABLE `om_term`  (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                            `workspace_id` bigint(20) NOT NULL COMMENT '工作区id',
                            `version_id` bigint(20) NOT NULL COMMENT '版本id',
                            `std_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标准名称',
                            `en_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '英文名称',
                            `alias` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '别名',
                            `abbr` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '缩写',
                            `source` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源',
                            `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                            `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                            `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                            `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                            `creator_id` bigint(20) NULL DEFAULT NULL COMMENT '创建人id',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                            `updater_id` bigint(20) NULL DEFAULT NULL COMMENT '更新人id',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                            `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '术语表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of om_term
-- ----------------------------
INSERT INTO `om_term` VALUES (1, 1001, 1, '出库单', 'Goods Issue Note', '发货单,出库凭证', 'GIN', '仓储及物流管理', '记录物料出库、发货用途及数量的业务单据，可关联收货方和物流服务商。', 1, 0, '吴同', 1, '2026-10-20 10:25:00', '吴同', 1, '2026-10-20 10:25:00', NULL);
INSERT INTO `om_term` VALUES (2, 1001, 1, '入库单', 'Goods Receipt Note', '收货单,入库凭证', 'GRN', '仓储业务管理', '记录物料接收、验收和入库信息的业务单据，可关联采购订单及本次到货物料。', 1, 0, '吴同', 1, '2026-10-20 10:25:30', '吴同', 1, '2026-10-20 10:25:30', NULL);
INSERT INTO `om_term` VALUES (3, 1001, 1, '采购订单', 'Purchase Order', '采购单,订购单', 'PO', '采购业务管理', '记录采购方、供应商、采购物料及交货约定的业务单据，是采购执行和到货核对的依据。', 1, 0, '吴同', 1, '2026-10-20 10:26:00', '吴同', 1, '2026-10-20 10:26:00', NULL);
INSERT INTO `om_term` VALUES (4, 1001, 1, '成品', 'Finished Product', '产成品,最终产品', 'FG', '生产及销售管理', '完成规定生产工序并满足交付要求的产品，可由多个零部件装配组成。', 1, 0, '吴同', 1, '2026-10-20 10:26:30', '吴同', 1, '2026-10-20 10:26:30', NULL);
INSERT INTO `om_term` VALUES (5, 1001, 1, '零部件', 'Component', '部件,零件', 'COMP', '产品结构管理', '用于组成设备或产品的独立部件，具有明确的规格型号、设计图号和质量要求。', 1, 0, '吴同', 1, '2026-10-20 10:27:00', '吴同', 1, '2026-10-20 10:27:00', NULL);
INSERT INTO `om_term` VALUES (6, 1001, 1, '原材料', 'Raw Material', '原料,生产材料', 'RAW', '生产物料管理', '用于生产加工的基础材料，通过加工或装配形成零部件、半成品或成品。', 1, 0, '吴同', 1, '2026-10-20 10:27:30', '吴同', 1, '2026-10-20 10:27:30', NULL);
INSERT INTO `om_term` VALUES (7, 1001, 1, '物料', 'Material', '材料,物资', 'MAT', '物料管理', '企业生产经营过程中采购、存储、使用或销售的实物资源，包括原材料、零部件和成品。', 1, 0, '吴同', 1, '2026-10-20 10:28:00', '吴同', 1, '2026-10-20 10:28:00', NULL);
INSERT INTO `om_term` VALUES (8, 1001, 1, '物流服务商', 'Logistics Service Provider', '承运商,运输服务商', 'LSP', '物流业务管理', '承担物料运输、配送及相关物流服务的组织，可作为出库单对应运输任务的承运主体。', 1, 0, '吴同', 1, '2026-10-20 10:28:30', '吴同', 1, '2026-10-20 10:28:30', NULL);
INSERT INTO `om_term` VALUES (9, 1001, 1, '采购方', 'Purchaser', '采购单位,买方', 'PUR', '采购业务管理', '发起采购需求、下达采购订单并接收采购物料的组织。', 1, 0, '吴同', 1, '2026-10-20 10:29:00', '吴同', 1, '2026-10-20 10:29:00', NULL);
INSERT INTO `om_term` VALUES (10, 1001, 1, '供应商', 'Supplier', '供货商,供应单位', 'SUP', '供应商管理', '向企业提供原材料、零部件、设备或服务的组织，是采购订单中的供货责任主体。', 1, 0, '吴同', 1, '2026-10-20 10:29:30', '吴同', 1, '2026-10-20 10:29:30', NULL);
INSERT INTO `om_term` VALUES (11, 1001, 1, '组织', 'Organization', '机构,单位', 'ORG', '企业基础信息管理', '具有明确职责、管理结构和业务边界的业务主体，可承担采购、供应、物流及内部管理等活动。', 1, 0, '吴同', 1, '2026-10-20 10:30:00', '吴同', 1, '2026-10-20 10:30:00', NULL);

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
                                        `fired_time` bigint(20) NOT NULL COMMENT '触发的时间',
                                        `sched_time` bigint(20) NOT NULL COMMENT '定时器制定的时间',
                                        `priority` int(11) NOT NULL COMMENT '优先级',
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
                                         `last_checkin_time` bigint(20) NOT NULL COMMENT '上次检查时间',
                                         `checkin_interval` bigint(20) NOT NULL COMMENT '检查间隔时间',
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
                                         `repeat_count` bigint(20) NOT NULL COMMENT '重复的次数统计',
                                         `repeat_interval` bigint(20) NOT NULL COMMENT '重复的间隔时间',
                                         `times_triggered` bigint(20) NOT NULL COMMENT '已经触发的次数',
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
                                          `int_prop_1` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
                                          `int_prop_2` int(11) NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
                                          `long_prop_1` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
                                          `long_prop_2` bigint(20) NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
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
                                  `next_fire_time` bigint(20) NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
                                  `prev_fire_time` bigint(20) NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
                                  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
                                  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器状态',
                                  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '触发器的类型',
                                  `start_time` bigint(20) NOT NULL COMMENT '开始时间',
                                  `end_time` bigint(20) NULL DEFAULT NULL COMMENT '结束时间',
                                  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日程表名称',
                                  `misfire_instr` smallint(6) NULL DEFAULT NULL COMMENT '补偿执行的策略',
                                  `job_data` blob NULL COMMENT '存放持久化job对象',
                                  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
                                  INDEX `sched_name`(`sched_name`, `job_name`, `job_group`) USING BTREE,
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
                                  `config_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
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
                                   `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `sys_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统名称',
                                   `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '系统logo',
                                   `login_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录页面logo',
                                   `carousel_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '轮播图',
                                   `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
                                   `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电子邮箱',
                                   `copyright` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '版权方',
                                   `record_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备案号',
                                   `del_flag` int(11) NULL DEFAULT NULL COMMENT '删除标记',
                                   `status` int(11) NULL DEFAULT NULL COMMENT '状态',
                                   `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `creator_id` int(11) NULL DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '修改人',
                                   `updater_id` int(11) NULL DEFAULT NULL COMMENT '修改人id',
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
                                `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
                                `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
                                `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '祖级列表',
                                `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
                                `order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
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
                                     `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
                                     `dict_sort` int(11) NULL DEFAULT 0 COMMENT '字典排序',
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
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典数据表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `system_dict_data` VALUES (51, 1, '基础数据类型', '1', 'om_attribute_data_type', '', '', 'Y', '0', 'admin', '2026-10-16 09:48:41', '超级管理员', '2026-10-16 09:49:06', '');
INSERT INTO `system_dict_data` VALUES (52, 2, '业务数据类型', '2', 'om_attribute_data_type', '', '', 'N', '0', 'admin', '2026-10-16 09:48:41', '超级管理员', '2026-10-16 09:49:08', '');
INSERT INTO `system_dict_data` VALUES (53, 0, '文本', '0', 'basic_data_type', NULL, 'primary', 'N', '0', '吴同', '2026-10-16 15:03:01', '超级管理员', '2026-10-16 15:04:24', NULL);
INSERT INTO `system_dict_data` VALUES (54, 1, '整数', '1', 'basic_data_type', NULL, 'success', 'N', '0', '吴同', '2026-10-16 15:03:18', '超级管理员', '2026-10-16 15:04:28', NULL);
INSERT INTO `system_dict_data` VALUES (55, 2, '小数', '2', 'basic_data_type', NULL, 'info', 'N', '0', '吴同', '2026-10-16 15:03:44', '超级管理员', '2026-10-16 15:04:33', NULL);
INSERT INTO `system_dict_data` VALUES (56, 3, '布尔', '3', 'basic_data_type', NULL, 'warning', 'N', '0', '吴同', '2026-10-16 15:03:58', '超级管理员', '2026-10-16 15:04:40', NULL);
INSERT INTO `system_dict_data` VALUES (57, 4, '日期', '4', 'basic_data_type', NULL, 'danger', 'N', '0', '吴同', '2026-10-16 15:04:12', '超级管理员', '2026-10-16 15:04:44', NULL);
INSERT INTO `system_dict_data` VALUES (58, 0, '禁用', '0', 'project_status', NULL, 'danger', 'N', '0', '超级管理员', '2026-10-12 15:11:21', '', NULL, NULL);
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
INSERT INTO `system_dict_data` VALUES (72, 1, '子类型', 'SUBCLASS', 'om_axiom_type', NULL, 'default', 'Y', '0', '吴同', '2026-10-22 09:52:30', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (73, 2, '等价', 'EQUIVALENT', 'om_axiom_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:30', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (74, 3, '互斥', 'DISJOINT', 'om_axiom_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:30', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (75, 4, 'Domain / Range', 'DOMAIN_RANGE', 'om_axiom_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:30', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (76, 5, '属性范围', 'PROPERTY_RANGE', 'om_axiom_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (77, 1, '必填', 'REQUIRED', 'om_data_rule_type', NULL, 'default', 'Y', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (78, 2, '枚举', 'ENUM', 'om_data_rule_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (79, 3, '取值范围', 'RANGE', 'om_data_rule_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (80, 4, '格式校验', 'FORMAT', 'om_data_rule_type', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (81, 1, '提示', 'INFO', 'om_rule_level', NULL, 'info', 'Y', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (82, 2, '警告', 'WARNING', 'om_rule_level', NULL, 'warning', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (83, 3, '错误', 'ERROR', 'om_rule_level', NULL, 'danger', 'N', '0', '吴同', '2026-10-22 09:52:31', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (84, 1, '大于', 'GT', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (85, 2, '大于等于', 'GE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (86, 3, '小于', 'LT', 'om_rule_operator', NULL, 'default', 'Y', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (87, 4, '小于等于', 'LE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (88, 5, '等于', 'EQ', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (89, 6, '不等于', 'NE', 'om_rule_operator', NULL, 'default', 'N', '0', '吴同', '2026-10-22 09:52:32', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (90, 0, '未执行', '0', 'app_task_status', NULL, 'primary', 'Y', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (91, 1, '执行中', '1', 'app_task_status', NULL, 'warning', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (92, 2, '已执行', '2', 'app_task_status', NULL, 'success', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (93, 3, '执行失败', '3', 'app_task_status', NULL, 'danger', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (94, 4, '队列中', '4', 'app_task_status', NULL, 'info', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (95, 0, '失败', '0', 'app_log_status', NULL, 'danger', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (96, 1, '成功', '1', 'app_log_status', NULL, 'success', 'N', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (97, 0, '结构化', '0', 'app_task_log_type', NULL, 'primary', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (98, 1, '非结构化', '1', 'app_task_log_type', NULL, 'primary', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (99, 0, '未映射', '0', 'app_mapping_status', NULL, 'warning', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (100, 1, '已映射', '1', 'app_mapping_status', NULL, 'success', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (101, 0, '全量更新', '0', 'app_update_type', NULL, 'default', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (102, 1, '增量更新', '1', 'app_update_type', NULL, 'default', 'N', '0', 'admin', '2026-10-22 20:06:17', '', NULL, '');
INSERT INTO `system_dict_data` VALUES (103, 0, '未发布', '0', 'publish_status', '', 'warning', 'N', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '未发布状态');
INSERT INTO `system_dict_data` VALUES (104, 1, '已发布', '1', 'publish_status', '', 'primary', 'N', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '已发布状态');

-- ----------------------------
-- Table structure for system_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `system_dict_type`;
CREATE TABLE `system_dict_type`  (
                                     `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
                                     `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典名称',
                                     `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '字典类型',
                                     `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
                                     `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
                                     `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                     `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
                                     `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
                                     `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                     PRIMARY KEY (`dict_id`) USING BTREE,
                                     UNIQUE INDEX `dict_type`(`dict_type`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典类型表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `system_dict_type` VALUES (27, '任务执行状态', 'app_task_status', '0', 'admin', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (28, '知识抽取日志状态', 'app_log_status', '0', 'admin', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (29, '抽取日志类型', 'app_task_log_type', '0', 'admin', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_dict_type` VALUES (30, '表映射状态', 'app_mapping_status', '0', 'admin', '2026-10-22 20:06:15', '', NULL, '结构化抽取表映射状态');
INSERT INTO `system_dict_type` VALUES (31, '结构化抽取更新类型', 'app_update_type', '0', 'admin', '2026-10-22 20:06:16', '', NULL, '结构化抽取全量或增量更新');
INSERT INTO `system_dict_type` VALUES (32, '发布状态', 'publish_status', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '发布状态列表');

-- ----------------------------
-- Table structure for system_job
-- ----------------------------
DROP TABLE IF EXISTS `system_job`;
CREATE TABLE `system_job`  (
                               `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
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
                                   `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
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
                                      `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
                                      `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '用户账号',
                                      `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录IP地址',
                                      `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '登录地点',
                                      `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '浏览器类型',
                                      `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作系统',
                                      `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
                                      `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '提示消息',
                                      `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
                                      PRIMARY KEY (`info_id`) USING BTREE,
                                      INDEX `idx_system_logininfor_s`(`status`) USING BTREE,
                                      INDEX `idx_system_logininfor_lt`(`login_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统访问记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_logininfor
-- ----------------------------
INSERT INTO `system_logininfor` VALUES (1, 'admin', '117.89.24.233', 'XX XX', 'Chrome 15', 'Mac OS X', '0', '登录成功', '2026-10-22 20:22:20');

-- ----------------------------
-- Table structure for system_menu
-- ----------------------------
DROP TABLE IF EXISTS `system_menu`;
CREATE TABLE `system_menu`  (
                                `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                                `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
                                `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
                                `order_num` int(11) NULL DEFAULT 0 COMMENT '显示顺序',
                                `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由地址',
                                `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '组件路径',
                                `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '路由参数',
                                `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '路由名称',
                                `is_frame` int(11) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
                                `is_cache` int(11) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
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
) ENGINE = InnoDB AUTO_INCREMENT = 1195 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单权限表' ROW_FORMAT = DYNAMIC;

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
INSERT INTO `system_menu` VALUES (1061, '模型市场', 1, 10, 'ai', NULL, '', '', 1, 0, 'M', '0', '0', '', 'book-ai-line', '吴同', '2026-10-22 19:56:02', '', NULL, 'AI 模型与密钥管理目录');
INSERT INTO `system_menu` VALUES (1062, '模型市场', 1061, 1, 'modelMarket', 'ai/modelMarket/index', '', '', 1, 0, 'C', '0', '0', 'ai:modelMarket:key:list', 'apps-line', '吴同', '2026-10-22 19:56:02', '', NULL, '供应商与 API 密钥配置');
INSERT INTO `system_menu` VALUES (1063, '我的模型', 1061, 2, 'myModel', 'ai/myModel/index', '', '', 1, 0, 'C', '0', '0', 'ai:modelMarket:key:list', 'book-read-line', '吴同', '2026-10-22 19:56:02', '', NULL, '工作区模型管理');
INSERT INTO `system_menu` VALUES (1064, '密钥查询', 1062, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:query', '#', '吴同', '2026-10-22 19:56:02', '', NULL, '');
INSERT INTO `system_menu` VALUES (1065, '密钥配置', 1062, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:edit', '#', '吴同', '2026-10-22 19:56:02', '', NULL, '');
INSERT INTO `system_menu` VALUES (1066, '模型查询', 1063, 1, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:query', '#', '吴同', '2026-10-22 19:56:03', '', NULL, '');
INSERT INTO `system_menu` VALUES (1077, '模型配置', 1063, 2, '', NULL, '', '', 1, 0, 'F', '0', '0', 'ai:modelMarket:key:edit', '#', '吴同', '2026-10-22 19:56:03', '', NULL, '');
INSERT INTO `system_menu` VALUES (1078, '本体建模', 0, 2, 'om', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '本体建模', '超级管理员', '2026-10-10 17:36:46', '吴同', '2026-10-23 10:35:43', '');
INSERT INTO `system_menu` VALUES (1079, 'AI辅助建模', 1078, 4, ':versionId/aiModeling', 'om/aiModeling/index', NULL, '', 1, 0, 'C', '0', '0', '', 'AI辅助建模', '吴同', '2026-10-19 14:50:52', '吴同', '2026-10-23 13:59:58', '');
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
INSERT INTO `system_menu` VALUES (1138, '文件分类', 1080, 5, 'fileCategory', 'basic/file/category/index', NULL, NULL, 1, 0, 'C', '0', '0', 'basic:file:category:list', '文件分类', 'admin', '2026-08-18 15:59:26', '吴同', '2026-10-23 10:36:35', '模型数据文件分类菜单');
INSERT INTO `system_menu` VALUES (1139, '文件分类查询', 1138, 1, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:query', '#', 'admin', '2026-08-18 15:59:27', '超级管理员', '2026-08-27 19:11:54', '');
INSERT INTO `system_menu` VALUES (1140, '文件分类新增', 1138, 2, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:add', '#', 'admin', '2026-08-18 15:59:27', '超级管理员', '2026-08-27 19:12:02', '');
INSERT INTO `system_menu` VALUES (1141, '文件分类修改', 1138, 3, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:edit', '#', 'admin', '2026-08-18 15:59:28', '超级管理员', '2026-08-27 19:12:10', '');
INSERT INTO `system_menu` VALUES (1142, '文件分类删除', 1138, 4, '#', '', NULL, NULL, 1, 0, 'F', '0', '0', 'basic:file:category:remove', '#', 'admin', '2026-08-18 15:59:28', '超级管理员', '2026-08-27 19:12:22', '');
INSERT INTO `system_menu` VALUES (1143, '文件管理', 1080, 6, 'fileManager', 'basic/file/manager/index', NULL, NULL, 1, 0, 'C', '0', '0', 'basic:file:manager:list', '文件管理', 'admin', '2026-08-18 15:59:42', '吴同', '2026-10-23 10:36:42', '模型数据文件菜单');
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
INSERT INTO `system_menu` VALUES (1156, '知识图谱', 1155, 1, 'graph', 'app/graph/index', NULL, '', 1, 0, 'C', '0', '0', '', '知识图谱', '超级管理员', '2026-10-20 15:21:42', '吴同', '2026-10-23 14:04:34', '');
INSERT INTO `system_menu` VALUES (1157, '业务数据类型', 1082, 1, 'omDataType', 'om/semantic/dataType/index', NULL, '', 1, 0, 'C', '0', '0', 'om:dataType:datatype:list', 'list-view', 'admin', '2026-10-21 00:00:00', '', NULL, '版本数据类型菜单');
INSERT INTO `system_menu` VALUES (1158, '数据类型查询', 1157, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:query', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1159, '数据类型新增', 1157, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:add', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1160, '数据类型修改', 1157, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:edit', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1161, '数据类型删除', 1157, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:dataType:datatype:remove', '#', 'admin', '2026-10-21 00:00:00', '', NULL, '');
INSERT INTO `system_menu` VALUES (1162, '项目概览', 1078, 0, ':versionId/overview', 'om/overview/index', NULL, '', 1, 0, 'C', '0', '0', '', '项目概览', '超级管理员', '2026-10-21 15:01:04', '吴同', '2026-10-23 10:36:50', '');
INSERT INTO `system_menu` VALUES (1163, '约束规则', 1097, 3, 'constraint', NULL, NULL, '', 1, 0, 'M', '0', '0', '', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1164, '模型公理', 1163, 1, 'model', 'om/om/constraint/model/index', NULL, '', 1, 0, 'C', '0', '0', 'om:constraint:model:list', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1165, '模型公理查询', 1164, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:model:query', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1166, '模型公理新增', 1164, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:model:add', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1167, '模型公理修改', 1164, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:model:edit', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1168, '模型公理删除', 1164, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:model:remove', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1169, '数据规则', 1163, 2, 'data', 'om/om/constraint/data/index', NULL, '', 1, 0, 'C', '0', '0', 'om:constraint:data:list', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1170, '数据规则查询', 1169, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:data:query', '#', 'admin', '2026-10-22 19:56:25', '', NULL, '');
INSERT INTO `system_menu` VALUES (1171, '数据规则新增', 1169, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:data:add', '#', 'admin', '2026-10-22 19:56:26', '', NULL, '');
INSERT INTO `system_menu` VALUES (1172, '数据规则修改', 1169, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:data:edit', '#', 'admin', '2026-10-22 19:56:26', '', NULL, '');
INSERT INTO `system_menu` VALUES (1173, '数据规则删除', 1169, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'om:constraint:data:remove', '#', 'admin', '2026-10-22 19:56:26', '', NULL, '');
INSERT INTO `system_menu` VALUES (1174, '非结构化抽取', 1155, 2, 'unstructTask', 'app/unstructTask/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appUnstructTask:unstructtask:list', '非结构化抽取', '吴同', '2026-10-20 18:04:48', '吴同', '2026-10-23 14:01:40', '非结构化抽取任务菜单');
INSERT INTO `system_menu` VALUES (1175, '任务查询', 1174, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:query', '#', '吴同', '2026-10-22 20:06:12', '', NULL, '');
INSERT INTO `system_menu` VALUES (1176, '任务新增', 1174, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:add', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1177, '任务修改', 1174, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:edit', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1178, '任务删除', 1174, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:remove', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1179, '任务执行', 1174, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:edit', '#', '吴同', '2026-10-22 20:06:13', '', NULL, '');
INSERT INTO `system_menu` VALUES (1180, '执行日志', 1174, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:taskLog', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1181, '任务导出', 1174, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:export', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1182, '任务导入', 1174, 8, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appUnstructTask:unstructtask:import', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1183, '抽取日志', 1155, 4, 'taskLog', 'app/taskLog/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appTaskLog:tasklog:list', '抽取日志', '吴同', '2026-10-20 18:04:48', '吴同', '2026-10-23 14:01:55', '结构化与非结构化通用抽取日志');
INSERT INTO `system_menu` VALUES (1184, '日志导出', 1183, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appTaskLog:tasklog:export', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1185, '结构化抽取', 1155, 3, 'structTask', 'app/structTask/index', NULL, '', 1, 0, 'C', '0', '0', 'app:appStructTask:struct:list', '结构化抽取', '吴同', '2026-10-22 20:06:14', '吴同', '2026-10-23 14:01:46', '结构化抽取任务菜单');
INSERT INTO `system_menu` VALUES (1186, '任务查询', 1185, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:list', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1187, '任务新增', 1185, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:add', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1188, '任务修改', 1185, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:edit', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1189, '任务删除', 1185, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:remove', '#', '吴同', '2026-10-22 20:06:14', '', NULL, '');
INSERT INTO `system_menu` VALUES (1190, '任务执行', 1185, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:edit', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1191, '任务导出', 1185, 6, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:export', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1192, '任务导入', 1185, 7, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'app:appStructTask:struct:import', '#', '吴同', '2026-10-22 20:06:15', '', NULL, '');
INSERT INTO `system_menu` VALUES (1193, '项目管理导出', 1081, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'basic:project:project:export', '#', '超级管理员', '2026-10-22 18:49:17', '超级管理员', '2026-10-22 18:49:30', '');
INSERT INTO `system_menu` VALUES (1194, '枚举类型数据查看', 1128, 5, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'basic:enumeration:data:list', '#', '超级管理员', '2026-10-22 19:07:21', '超级管理员', '2026-10-22 19:07:29', '');

-- ----------------------------
-- Table structure for system_notice
-- ----------------------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice`  (
                                  `notice_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_notice
-- ----------------------------
INSERT INTO `system_notice` VALUES (1, 'qOntology本体工程平台正式开源！', '2', 0x3C703E714F6E746F6C6F6779E69CACE4BD93E5B7A5E7A88BE5B9B3E58FB0E6ADA3E5BC8FE5BC80E6BA90EFBC813C2F703E, '0', '吴同', '2026-10-15 13:47:04', '超级管理员', '2026-10-22 15:34:46', NULL);
INSERT INTO `system_notice` VALUES (2, '本体建模操作指南：对象类型与关系管理', '1', 0x3C703EE9809AE8BF87E69CACE4BD93E5BBBAE6A8A1E7BBB4E68AA4E5AFB9E8B1A1E7B1BBE59E8BE38081E5B19EE680A7E5928CE5AFB9E8B1A1E585B3E7B3BBEFBC8CE7BB9FE4B880E4B89AE58AA1E6A682E5BFB5E4B88EE585B3E7B3BBE8A1A8E8BEBEE380823C2F703E3C703EE5BBBAE8AEAEE58588E7A1AEE5AE9AE9A1B9E79BAEE5928CE78988E69CACEFBC8CE5868DE5AE8CE59684E5AFB9E8B1A1E5B19EE680A7EFBC8CE5B9B6E5B086E5AFB9E8B1A1E7B1BBE59E8BE58F8AE585B3E7B3BBE585B3E88194E588B0E5BBBAE6A8A1E79BAEE6A087E380823C2F703E, '0', '吴同', '2026-10-19 09:30:00', '吴同', '2026-10-19 09:30:00', NULL);
INSERT INTO `system_notice` VALUES (3, '语义资产维护说明：术语、数据类型与枚举', '2', 0x3C703EE59CA8E8AFADE4B989E8B584E4BAA7E4B8ADE7BB9FE4B880E7BBB4E68AA4E69CAFE8AFADE38081E4B89AE58AA1E695B0E68DAEE7B1BBE59E8BE5928CE69E9AE4B8BEEFBC8CE5878FE5B091E4B88DE5908CE4B89AE58AA1E59CBAE699AFE4B8ADE79A84E6ADA7E4B989E380823C2F703E3C703EE7BBB4E68AA4E69CAFE8AFADE697B6E8AFB7E5A1ABE58699E5908DE7A7B0E38081E88BB1E69687E5908DE7A7B0E38081E588ABE5908DE38081E7BCA9E58699E5928CE69DA5E6BA90EFBC9BE5BC95E794A8E69E9AE4B8BEE697B6E8AFB7E6B3A8E6848FE58CBAE58886E5BD93E5898DE9A1B9E79BAEE4B88EE585A8E5B180E69DA5E6BA90E380823C2F703E, '0', '吴同', '2026-10-20 10:00:00', '吴同', '2026-10-20 10:00:00', NULL);
INSERT INTO `system_notice` VALUES (4, '项目版本与本体导出使用说明', '1', 0x3C703EE9A1B9E79BAEE794A8E4BA8EE7BB84E7BB87E69CACE4BD93E6A8A1E59E8BEFBC8CE78988E69CACE794A8E4BA8EE58CBAE58886E4B88DE5908CE998B6E6AEB5E79A84E6A8A1E59E8BE58685E5AEB9E380823C2F703E3C703EE5AFBCE587BAE5898DE8AFB7E7A1AEE8AEA4E9A1B9E79BAEE38081E78988E69CACE5928CE69687E4BBB6E5908DE7A7B0E38082E5BD93E5898DE9A1B5E99DA2E68F90E4BE9B20547572746C65EFBC882E74746CEFBC89E5AFBCE587BAEFBC8CE585B6E4BB96E5B195E7A4BAE4B8ADE79A84E6A0BCE5BC8FE69A82E69CAAE5BC80E694BEE98089E68BA9E380823C2F703E, '0', '吴同', '2026-10-21 14:00:00', '吴同', '2026-10-21 14:00:00', NULL);
INSERT INTO `system_notice` VALUES (5, '建模目标维护与验证状态说明', '2', 0x3C703EE5BBBAE6A8A1E79BAEE6A087E794A8E4BA8EE8AEB0E5BD95E99C80E8A681E9809AE8BF87E69CACE4BD93E6A8A1E59E8BE59B9EE7AD94E79A84E4B89AE58AA1E997AEE9A298E380823C2F703E3C703EE8AFB7E7BB93E59088E997AEE9A298E69DA5E6BA90E38081E4B89AE58AA1E59CBAE699AFE5928CE4BC98E58588E7BAA7E7BBB4E68AA4E79BAEE6A087EFBC8CE5B9B6E585B3E88194E79BB8E585B3E5AFB9E8B1A1E7B1BBE59E8BE68896E585B3E7B3BBEFBC8CE4BEBFE4BA8EE68C81E7BBADE8B79FE8B8AAE6A8A1E59E8BE5AFB9E79BAEE6A087E997AEE9A298E79A84E8A686E79B96E68385E586B5E380823C2F703E, '0', '吴同', '2026-10-22 09:30:00', '吴同', '2026-10-22 09:30:00', NULL);

-- ----------------------------
-- Table structure for system_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `system_oper_log`;
CREATE TABLE `system_oper_log`  (
                                    `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
                                    `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '模块标题',
                                    `business_type` int(11) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
                                    `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '方法名称',
                                    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求方式',
                                    `operator_type` int(11) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
                                    `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作人员',
                                    `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '部门名称',
                                    `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求URL',
                                    `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '主机地址',
                                    `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '操作地点',
                                    `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '请求参数',
                                    `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '返回参数',
                                    `status` int(11) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
                                    `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '错误消息',
                                    `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
                                    `cost_time` bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
                                    PRIMARY KEY (`oper_id`) USING BTREE,
                                    INDEX `idx_system_oper_log_bt`(`business_type`) USING BTREE,
                                    INDEX `idx_system_oper_log_s`(`status`) USING BTREE,
                                    INDEX `idx_system_oper_log_ot`(`oper_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '操作日志记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_oper_log
-- ----------------------------
INSERT INTO `system_oper_log` VALUES (1, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:27:05', 83);
INSERT INTO `system_oper_log` VALUES (2, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":3,\"roleKey\":\"sales\",\"roleName\":\"客户经理\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:27:51', 88);
INSERT INTO `system_oper_log` VALUES (3, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,101,102,103,104,105,106,1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1000,1007,1012,1016,1020,1025,1030,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077],\"params\":{},\"remark\":\"\",\"roleId\":4,\"roleKey\":\"guest\",\"roleName\":\"体验用户\",\"roleSort\":4,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:29:17', 81);
INSERT INTO `system_oper_log` VALUES (4, '角色管理', 2, 'tech.qiantong.qontology.module.system.controller.admin.system.SysRoleController.edit()', 'PUT', 1, 'admin', '江苏千桐科技有限公司', '/system/role', '49.74.228.64', 'XX XX', '{\"admin\":false,\"createBy\":\"吴同\",\"createTime\":\"2026-10-22 20:19:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1080,1081,1112,1113,1114,1115,1193,1128,1129,1130,1131,1132,1194,1123,1124,1125,1126,1127,1133,1134,1135,1136,1137,1138,1139,1140,1141,1142,1143,1144,1145,1146,1147,1078,1162,1116,1117,1118,1119,1120,1121,1122,1082,1090,1091,1092,1093,1094,1095,1096,1157,1158,1159,1160,1161,1083,1084,1085,1086,1087,1088,1089,1148,1149,1150,1151,1152,1153,1154,1097,1098,1099,1100,1101,1102,1103,1104,1105,1106,1107,1108,1109,1110,1111,1163,1164,1165,1166,1167,1168,1169,1170,1171,1172,1173,1079,1155,1156,1174,1175,1176,1177,1178,1179,1180,1181,1182,1185,1186,1187,1188,1189,1190,1191,1192,1183,1184,1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,1061,1062,1064,1065,1063,1066,1077,2,109,1046,1047,1048,110,1049,1050,1051,1052,1053,1054,111,112,113,3,114,1055,1056,1057,1058,1059,1060,115],\"params\":{},\"remark\":\"\",\"roleId\":2,\"roleKey\":\"system\",\"roleName\":\"系统管理员\",\"roleSort\":2,\"status\":\"0\",\"updateBy\":\"超级管理员\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2026-10-22 20:31:22', 65);

-- ----------------------------
-- Table structure for system_post
-- ----------------------------
DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post`  (
                                `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
                                `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
                                `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
                                `post_sort` int(11) NOT NULL COMMENT '显示顺序',
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
                                `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                                `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
                                `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色权限字符串',
                                `role_sort` int(11) NOT NULL COMMENT '显示顺序',
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
INSERT INTO `system_role` VALUES (2, '系统管理员', 'system', 2, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '超级管理员', '2026-10-22 20:31:22', '');
INSERT INTO `system_role` VALUES (3, '客户经理', 'sales', 3, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '超级管理员', '2026-10-22 20:27:51', '');
INSERT INTO `system_role` VALUES (4, '体验用户', 'guest', 4, '1', 1, 1, '0', '0', '吴同', '2026-10-22 20:19:09', '超级管理员', '2026-10-22 20:29:17', '');

-- ----------------------------
-- Table structure for system_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `system_role_dept`;
CREATE TABLE `system_role_dept`  (
                                     `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                     `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
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
                                     `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                     `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
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
INSERT INTO `system_role_menu` VALUES (2, 1065);
INSERT INTO `system_role_menu` VALUES (2, 1066);
INSERT INTO `system_role_menu` VALUES (2, 1077);
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

-- ----------------------------
-- Table structure for system_user
-- ----------------------------
DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user`  (
                                `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                                `dept_id` bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
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
INSERT INTO `system_user` VALUES (2, 100, 'qOntology', '吴同', '00', 'support@qiantong.tech', '18888888888', '0', NULL, '$2a$10$6snwwaFipC2kqvFC763IIuV4BNOt8jJNH83x8CI4/2ShbiX96J3WO', '0', '0', '127.0.0.1', '2026-09-29 17:46:51', '吴同', '2026-06-03 14:48:53', '超级管理员', '2026-09-29 17:46:51', NULL);

-- ----------------------------
-- Table structure for system_user_post
-- ----------------------------
DROP TABLE IF EXISTS `system_user_post`;
CREATE TABLE `system_user_post`  (
                                     `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                     `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
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
                                     `user_id` bigint(20) NOT NULL COMMENT '用户ID',
                                     `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                     PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户和角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of system_user_role
-- ----------------------------
INSERT INTO `system_user_role` VALUES (1, 1);
INSERT INTO `system_user_role` VALUES (2, 2);

SET FOREIGN_KEY_CHECKS = 1;
