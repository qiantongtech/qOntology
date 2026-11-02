
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

DROP TABLE IF EXISTS `om_constraint_rule`;

-- ----------------------------
-- Table structure for om_model_axiom
-- ----------------------------
DROP TABLE IF EXISTS `om_model_axiom`;
CREATE TABLE `om_model_axiom`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                   `version_id` bigint NOT NULL COMMENT '项目版本id',
                                   `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型公理名称',
                                   `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                   `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '公理类型;SUBCLASS：子类型，EQUIVALENT：等价，DISJOINT：互斥，DOMAIN_RANGE：Domain/Range，PROPERTY_RANGE：属性范围',
                                   `level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INFO' COMMENT '级别;INFO：提示，WARNING：警告，ERROR：错误',
                                   `object_type_id` bigint NULL DEFAULT NULL COMMENT '对象类型id;子类型、对象类型A或属性所属对象类型',
                                   `other_type_id` bigint NULL DEFAULT NULL COMMENT '另一对象类型id;父类型或对象类型B',
                                   `relation_id` bigint NULL DEFAULT NULL COMMENT '对象类型关系id;关联om_object_type_relation',
                                   `relation_name` varchar(128) DEFAULT NULL COMMENT '关系名称',
                                   `domain_id` bigint NULL DEFAULT NULL COMMENT 'Domain对象类型id',
                                   `range_id` bigint NULL DEFAULT NULL COMMENT 'Range对象类型id',
                                   `attribute_id` bigint NULL DEFAULT NULL COMMENT '数据属性id;关联om_object_type_attribute',
                                   `type_category` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '属性值类型分类;1：基础数据类型，2：业务数据类型',
                                   `basic_type_code` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '基础类型编码;0：文本，1：整数，2：小数，3：布尔，4：日期，5：枚举',
                                   `business_type_id` bigint NULL DEFAULT NULL COMMENT '业务类型id;按来源关联om_data_type或basic_data_type',
                                   `business_type_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型来源;PROJECT当前项目版本,GLOBAL全局',
                                   `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位;基础整数、小数展示',
                                   `decimal_places` int NULL DEFAULT NULL COMMENT '小数位数;基础小数展示',
                                   `format` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日期格式;基础日期展示',
                                   `enum_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举类型编码;基础枚举展示',
                                   `enum_source` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '枚举来源;PROJECT当前项目版本,GLOBAL全局',
                                   `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                   `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                   `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                   `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
                                   `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                   `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
                                   `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_version_del_name`(`version_id` ASC, `del_flag` ASC, `name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '模型公理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Table structure for om_data_rule
-- ----------------------------
DROP TABLE IF EXISTS `om_data_rule`;
CREATE TABLE `om_data_rule`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                 `version_id` bigint NOT NULL COMMENT '项目版本id',
                                 `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据规则名称',
                                 `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
                                 `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '规则类型;REQUIRED：必填，ENUM：枚举，RANGE：取值范围，FORMAT：格式校验',
                                 `level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INFO' COMMENT '级别;INFO：提示，WARNING：警告，ERROR：错误',
                                 `condition_relation` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OR' COMMENT '失败条件关系;AND全部满足,OR任一满足',
                                 `conditions` json NOT NULL COMMENT '有序失败条件数组;objectTypeId：对象类型ID；attributeId：属性ID；operator：比较符；value：条件值',
                                 `failure_message` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '失败提示',
                                 `valid_flag` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
                                 `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志;1：已删除，0：未删除',
                                 `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                                 `creator_id` bigint NULL DEFAULT NULL COMMENT '创建人id',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '更新人',
                                 `updater_id` bigint NULL DEFAULT NULL COMMENT '更新人id',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_version_del_name`(`version_id` ASC, `del_flag` ASC, `name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据规则表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
DROP TABLE IF EXISTS `om_ai_task`;
-- AI 辅助建模（任务、解析过程、证据和候选数据）
CREATE TABLE  `om_ai_task` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
                               `task_name` varchar(128) NOT NULL COMMENT '任务名称',
                               `description` varchar(512) DEFAULT NULL COMMENT '任务描述',
                               `workspace_id` bigint DEFAULT NULL COMMENT '工作区ID',
                               `version_id` bigint NOT NULL COMMENT '项目版本ID',
                               `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待执行 1解析中 2解析成功 3解析失败',
                               `current_stage` varchar(64) DEFAULT NULL COMMENT '当前阶段',
                               `candidate_count` int NOT NULL DEFAULT 0 COMMENT '候选数据数量',
                               `started_time` datetime DEFAULT NULL COMMENT '开始时间',
                               `finished_time` datetime DEFAULT NULL COMMENT '完成时间',
                               `failure_reason` varchar(1000) DEFAULT NULL COMMENT '失败原因',
                               `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志',
                               `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                               `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                               `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`),
                               KEY `idx_om_ai_task_version` (`version_id`, `status`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI辅助建模任务';

DROP TABLE IF EXISTS `om_ai_file`;
CREATE TABLE `om_ai_file` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
                              `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                              `file_name` varchar(255) NOT NULL COMMENT '原始文件名称',
                              `file_url` varchar(1000) NOT NULL COMMENT '文件存储地址',
                              `file_ext` varchar(16) DEFAULT NULL COMMENT '文件扩展名',
                              `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
                              `content_hash` varchar(64) DEFAULT NULL COMMENT '文件内容SHA-256摘要',
                              `parse_status` tinyint NOT NULL DEFAULT 0 COMMENT '解析状态：0待解析 1解析中 2解析成功 3解析失败',
                              `page_count` int DEFAULT NULL COMMENT '文件页数',
                              `block_count` int DEFAULT NULL COMMENT '解析证据块数量',
                              `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                              `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                              `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`id`),
                              KEY `idx_om_ai_file_task` (`task_id`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI辅助建模文件';

DROP TABLE IF EXISTS `om_ai_stage`;
CREATE TABLE `om_ai_stage` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阶段记录ID',
                               `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                               `stage_code` varchar(64) NOT NULL COMMENT '阶段编码',
                               `stage_name` varchar(128) NOT NULL COMMENT '阶段名称',
                               `status` tinyint NOT NULL DEFAULT 0 COMMENT '阶段状态：0等待 1执行中 2成功 3失败',
                               `sequence_no` int NOT NULL COMMENT '阶段执行顺序',
                               `progress` int NOT NULL DEFAULT 0 COMMENT '阶段进度（0-100）',
                               `model_key_id` bigint DEFAULT NULL COMMENT '本阶段使用的模型密钥ID',
                               `model_name` varchar(128) DEFAULT NULL COMMENT '本阶段使用的模型名称',
                               `prompt_resource` varchar(255) DEFAULT NULL COMMENT '本阶段使用的提示词资源文件',
                               `input_count` int DEFAULT 0 COMMENT '输入证据块数量',
                               `output_count` int DEFAULT 0 COMMENT '输出候选数据数量',
                               `token_count` int DEFAULT 0 COMMENT '模型消耗Token数量',
                               `duration_ms` bigint DEFAULT 0 COMMENT '阶段耗时（毫秒）',
                               `summary` varchar(2000) DEFAULT NULL COMMENT '阶段执行摘要',
                               `reasoning_content` longtext DEFAULT NULL COMMENT '模型流式中文分析过程',
                               `error_message` varchar(2000) DEFAULT NULL COMMENT '阶段错误信息',
                               `started_time` datetime DEFAULT NULL COMMENT '阶段开始时间',
                               `finished_time` datetime DEFAULT NULL COMMENT '阶段完成时间',
                               `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                               `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                               `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`),
                               KEY `idx_om_ai_stage_task` (`task_id`, `sequence_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI解析阶段记录';

DROP TABLE IF EXISTS `om_ai_evidence`;
CREATE TABLE `om_ai_evidence` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '证据块ID',
                                  `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                  `file_id` bigint NOT NULL COMMENT '来源文件ID',
                                  `block_no` int NOT NULL COMMENT '文件内证据块序号',
                                  `block_type` varchar(32) NOT NULL COMMENT '证据块类型：文本、表格、图片等',
                                  `page_no` int DEFAULT NULL COMMENT '来源页码',
                                  `chapter_path` varchar(1000) DEFAULT NULL COMMENT '章节路径',
                                  `paragraph_no` int DEFAULT NULL COMMENT '段落序号',
                                  `table_no` int DEFAULT NULL COMMENT '表格序号',
                                  `row_no` int DEFAULT NULL COMMENT '表格行号',
                                  `column_no` int DEFAULT NULL COMMENT '表格列号',
                                  `image_no` int DEFAULT NULL COMMENT '图片序号',
                                  `line_start` int DEFAULT NULL COMMENT '起始行号',
                                  `line_end` int DEFAULT NULL COMMENT '结束行号',
                                  `bbox_json` varchar(1000) DEFAULT NULL COMMENT '页面坐标JSON',
                                  `content` longtext NOT NULL COMMENT '证据块原始内容',
                                  `content_hash` varchar(64) DEFAULT NULL COMMENT '证据块内容SHA-256摘要',
                                  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_om_ai_evidence_task_file` (`task_id`, `file_id`, `block_no`),
                                  KEY `idx_om_ai_evidence_hash` (`content_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI解析原子证据';

DROP TABLE IF EXISTS `om_ai_target`;
CREATE TABLE `om_ai_target` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                `candidate_key` varchar(128) DEFAULT NULL COMMENT '模型生成的稳定候选键',
                                `question` varchar(500) NOT NULL COMMENT '目标问题',
                                `question_source` varchar(255) DEFAULT NULL COMMENT '问题来源',
                                `business_scenarios` varchar(1000) DEFAULT NULL COMMENT '业务场景',
                                `priority` int DEFAULT NULL COMMENT '优先级',
                                `accept_criterion` varchar(1000) DEFAULT NULL COMMENT '验收标准',
                                `accept_status` varchar(2) NOT NULL DEFAULT '0' COMMENT '验证状态：0待验证 1已覆盖 2未覆盖',
                                `related_element_count` int NOT NULL DEFAULT 0 COMMENT '关联元素数量',
                                `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`id`),
                                KEY `idx_om_ai_target_task` (`task_id`, `adopt_status`, `del_flag`),
                                KEY `idx_om_ai_target_candidate_key` (`task_id`, `candidate_key`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选建模目标';

DROP TABLE IF EXISTS `om_ai_term`;
CREATE TABLE `om_ai_term` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                              `std_name` varchar(128) NOT NULL COMMENT '标准名称',
                              `en_name` varchar(128) DEFAULT NULL COMMENT '英文名称',
                              `alias` varchar(255) DEFAULT NULL COMMENT '别名',
                              `abbr` varchar(64) DEFAULT NULL COMMENT '缩写',
                              `source` varchar(255) DEFAULT NULL COMMENT '来源',
                              `description` varchar(1000) DEFAULT NULL COMMENT '描述',
                              `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                              `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                              `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                              `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                              `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                              `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                              `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                              `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                              PRIMARY KEY (`id`),
                              KEY `idx_om_ai_term_task` (`task_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选术语';


DROP TABLE IF EXISTS `om_ai_enum_type`;
CREATE TABLE `om_ai_enum_type` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                   `enum_name` varchar(128) NOT NULL COMMENT '枚举名称',
                                   `enum_type` varchar(100) NOT NULL COMMENT '枚举类型编码',
                                   `status` char(1) DEFAULT '0' COMMENT '状态',
                                   `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                   `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                   `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                   `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                   `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                   `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_om_ai_enum_type_task` (`task_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选枚举类型';

DROP TABLE IF EXISTS `om_ai_enum_data`;
CREATE TABLE `om_ai_enum_data` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                   `enum_type_id` bigint NOT NULL COMMENT '枚举类型候选ID',
                                   `enum_sort` bigint DEFAULT 0 COMMENT '枚举排序',
                                   `enum_label` varchar(128) NOT NULL COMMENT '枚举标签',
                                   `enum_value` varchar(128) NOT NULL COMMENT '枚举值',
                                   `status` char(1) DEFAULT '0' COMMENT '状态',
                                   `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                   `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                   `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                   `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                   `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                   `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_om_ai_enum_data_type` (`enum_type_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选枚举值';

DROP TABLE IF EXISTS `om_ai_object`;
CREATE TABLE `om_ai_object` (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                `name` varchar(128) NOT NULL COMMENT '名称',
                                `description` varchar(512) DEFAULT NULL COMMENT '描述',
                                `parent_candidate_id` bigint DEFAULT NULL COMMENT '父对象类型候选ID',
                                `order_num` bigint DEFAULT 0 COMMENT '显示顺序',
                                `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                PRIMARY KEY (`id`),
                                KEY `idx_om_ai_object_task` (`task_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选对象类型';

DROP TABLE IF EXISTS `om_ai_attribute`;
CREATE TABLE `om_ai_attribute` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                   `object_candidate_id` bigint NOT NULL COMMENT '对象类型候选ID',
                                   `name` varchar(128) NOT NULL COMMENT '名称',
                                   `code` varchar(128) NOT NULL COMMENT '属性编码',
                                   `description` varchar(512) DEFAULT NULL COMMENT '描述',
                                   `type_category` int DEFAULT NULL COMMENT '数据类型分类：1基础数据类型 2业务数据类型',
                                   `basic_type_code` int DEFAULT NULL COMMENT '基础数据类型编码',
                                   `business_type_id` bigint DEFAULT NULL COMMENT '业务数据类型ID',
                                   `business_type_source` varchar(16) DEFAULT NULL COMMENT '业务数据类型来源',
                                   `unit` varchar(32) DEFAULT NULL COMMENT '计量单位',
                                   `decimal_places` int DEFAULT NULL COMMENT '小数位数',
                                   `format` varchar(128) DEFAULT NULL COMMENT '数据格式',
                                   `enum_type` varchar(100) DEFAULT NULL COMMENT '枚举类型编码',
                                   `enum_source` varchar(16) DEFAULT NULL COMMENT '枚举来源',
                                   `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                   `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                   `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                   `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                   `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                   `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_om_ai_attribute_object` (`object_candidate_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选对象属性';

DROP TABLE IF EXISTS `om_ai_relation`;
CREATE TABLE `om_ai_relation` (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                  `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                  `name` varchar(128) NOT NULL COMMENT '名称',
                                  `description` varchar(256) DEFAULT NULL COMMENT '描述',
                                  `start_object_candidate_id` bigint NOT NULL COMMENT '起点对象类型候选ID',
                                  `end_object_candidate_id` bigint NOT NULL COMMENT '终点对象类型候选ID',
                                  `reversible_flag` tinyint DEFAULT 0 COMMENT '是否可逆：0否 1是',
                                  `reverse_name` varchar(128) DEFAULT NULL COMMENT '反向关系名称',
                                  `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                  `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                  `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                  `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                  `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                  `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_om_ai_relation_task` (`task_id`, `adopt_status`, `del_flag`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选对象关系';

DROP TABLE IF EXISTS `om_ai_axiom`;
CREATE TABLE `om_ai_axiom` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                               `name` varchar(128) NOT NULL COMMENT '模型公理名称',
                               `description` varchar(512) DEFAULT NULL COMMENT '描述',
                               `type` varchar(32) NOT NULL COMMENT '公理类型',
                               `level` varchar(16) NOT NULL DEFAULT 'INFO' COMMENT '级别',
                               `object_candidate_id` bigint DEFAULT NULL COMMENT '对象类型候选ID',
                               `other_object_candidate_id` bigint DEFAULT NULL COMMENT '另一对象类型候选ID',
                               `relation_name` varchar(128) DEFAULT NULL COMMENT '关系名称',
                               `domain_candidate_id` bigint DEFAULT NULL COMMENT 'Domain对象类型候选ID',
                               `range_candidate_id` bigint DEFAULT NULL COMMENT 'Range对象类型候选ID',
                               `attribute_candidate_id` bigint DEFAULT NULL COMMENT '属性候选ID',
                               `type_category` varchar(8) DEFAULT NULL COMMENT '属性值类型分类',
                               `basic_type_code` varchar(8) DEFAULT NULL COMMENT '基础类型编码',
                               `business_type_id` bigint DEFAULT NULL COMMENT '业务类型ID',
                               `business_type_source` varchar(16) DEFAULT NULL COMMENT '业务类型来源',
                               `unit` varchar(32) DEFAULT NULL COMMENT '单位',
                               `decimal_places` bigint DEFAULT NULL COMMENT '小数位数',
                               `format` varchar(128) DEFAULT NULL COMMENT '日期格式',
                               `enum_type` varchar(100) DEFAULT NULL COMMENT '枚举类型编码',
                               `enum_source` varchar(16) DEFAULT NULL COMMENT '枚举来源',
                               `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                               `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                               `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                               `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                               `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                               `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                               `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                               `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                               PRIMARY KEY (`id`),
                               KEY `idx_om_ai_axiom_task` (`task_id`, `adopt_status`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选模型公理';

DROP TABLE IF EXISTS `om_ai_data_rule`;
CREATE TABLE `om_ai_data_rule` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                   `name` varchar(128) NOT NULL COMMENT '数据规则名称',
                                   `description` varchar(512) DEFAULT NULL COMMENT '描述',
                                   `type` varchar(32) NOT NULL COMMENT '规则类型',
                                   `level` varchar(16) NOT NULL DEFAULT 'INFO' COMMENT '级别',
                                   `condition_relation` varchar(8) NOT NULL DEFAULT 'OR' COMMENT '条件关系',
                                   `conditions` longtext NOT NULL COMMENT '候选对象及属性条件JSON',
                                   `failure_message` varchar(512) NOT NULL COMMENT '失败提示',
                                   `adopt_status` tinyint NOT NULL DEFAULT 0 COMMENT '采纳状态：0待采纳 1已采纳 2采纳失败',
                                   `adopted_id` bigint DEFAULT NULL COMMENT '已采纳正式数据ID',
                                   `adopt_message` varchar(1000) DEFAULT NULL COMMENT '采纳结果信息',
                                   `confidence` decimal(6,5) DEFAULT NULL COMMENT '抽取置信度',
                                   `del_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '删除标志：0未删除 1已删除',
                                   `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_om_ai_data_rule_task` (`task_id`, `adopt_status`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选数据规则';

DROP TABLE IF EXISTS `om_ai_object_target`;
CREATE TABLE `om_ai_object_target` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                       `object_candidate_id` bigint NOT NULL COMMENT '对象类型候选ID',
                                       `target_candidate_id` bigint NOT NULL COMMENT '建模目标候选ID',
                                       `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                       `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                       `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_om_ai_object_target` (`object_candidate_id`, `target_candidate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选对象目标关系';

DROP TABLE IF EXISTS `om_ai_relation_target`;
CREATE TABLE `om_ai_relation_target` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                         `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                         `relation_candidate_id` bigint NOT NULL COMMENT '对象关系候选ID',
                                         `target_candidate_id` bigint NOT NULL COMMENT '建模目标候选ID',
                                         `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                         `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                         `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                         `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                         `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_om_ai_relation_target` (`relation_candidate_id`, `target_candidate_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选关系目标关系';

DROP TABLE IF EXISTS `om_ai_data_evidence`;
CREATE TABLE `om_ai_data_evidence` (
                                       `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                       `data_type` varchar(32) NOT NULL COMMENT '候选数据类型',
                                       `data_id` bigint NOT NULL COMMENT '候选数据ID',
                                       `evidence_id` bigint NOT NULL COMMENT '证据块ID',
                                       `evidence_role` varchar(16) NOT NULL DEFAULT 'POSITIVE' COMMENT '证据角色：POSITIVE正向 NEGATIVE反向',
                                       `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                       `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                       `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                       `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                       `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_om_ai_data_evidence` (`data_type`, `data_id`, `evidence_id`),
                                       KEY `idx_om_ai_data_evidence_data` (`task_id`, `data_type`, `data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选数据证据关系';

DROP TABLE IF EXISTS `om_ai_adopt_log`;
CREATE TABLE `om_ai_adopt_log` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                   `task_id` bigint NOT NULL COMMENT 'AI建模任务ID',
                                   `data_type` varchar(32) NOT NULL COMMENT '候选数据类型',
                                   `data_id` bigint NOT NULL COMMENT '候选数据ID',
                                   `formal_id` bigint DEFAULT NULL COMMENT '正式数据ID',
                                   `status` tinyint NOT NULL COMMENT '状态',
                                   `message` varchar(1000) DEFAULT NULL COMMENT '采纳日志信息',
                                   `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
                                   `operator_name` varchar(64) DEFAULT NULL COMMENT '操作人名称',
                                   `creator_id` bigint DEFAULT NULL COMMENT '创建人ID',
                                   `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `updater_id` bigint DEFAULT NULL COMMENT '更新人ID',
                                   `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `remark` varchar(512) DEFAULT NULL COMMENT '备注',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_om_ai_adopt_log_task` (`task_id`, `data_type`, `data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI候选采纳日志';


-- Table structure for app_event
-- ----------------------------
DROP TABLE IF EXISTS `app_event`;
CREATE TABLE `app_event` (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                             `workspace_id` bigint NOT NULL COMMENT '工作区id',
                             `model_id` bigint DEFAULT NULL COMMENT '模型id',
                             `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件名称',
                             `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '事件描述',
                             `type` tinyint unsigned NOT NULL COMMENT '事件类型',
                             `occur_time` datetime DEFAULT NULL COMMENT '发生时间',
                             `occur_place` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发生地点',
                             `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
                             `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
                             `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                             `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                             `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                             `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='事件管理表';
-- ----------------------------
-- Table structure for app_event_entity
-- ----------------------------
DROP TABLE IF EXISTS `app_event_entity`;
CREATE TABLE `app_event_entity` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                    `workspace_id` bigint NOT NULL COMMENT '工作区id',
                                    `event_id` bigint NOT NULL COMMENT '事件id',
                                    `entity_id` bigint NOT NULL COMMENT '实体id',
                                    `entity_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实体名称',
                                    `valid_flag` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
                                    `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志;1：已删除，0：未删除',
                                    `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '创建人',
                                    `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                    `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
                                    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                    PRIMARY KEY (`id`,`create_by`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='事件实体关联表';
-- ----------------------------

ALTER TABLE `basic_project` ADD COLUMN `icon` VARCHAR(512) NULL DEFAULT NULL COMMENT '项目图标路径' AFTER `description`;
ALTER TABLE `basic_project` ADD COLUMN `responsible_user_id` BIGINT NULL DEFAULT NULL COMMENT '责任人用户编号' AFTER `icon`;
ALTER TABLE `basic_project` ADD COLUMN `responsible_person_name` VARCHAR(128) NULL DEFAULT NULL COMMENT '责任人姓名（保存时用户资料快照）' AFTER `responsible_user_id`;
ALTER TABLE `basic_project` ADD COLUMN `responsible_person_phone` VARCHAR(64) NULL DEFAULT NULL COMMENT '联系电话（保存时用户资料快照）' AFTER `responsible_person_name`;
