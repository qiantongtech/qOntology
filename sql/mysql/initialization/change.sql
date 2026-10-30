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

UPDATE system_menu SET menu_name='模型市场', parent_id=1080, order_num=10, `path`='llm', component=NULL, query='', route_name='', is_frame=1, is_cache=0, menu_type='M', visible='0', status='0', perms='', icon='book-ai-line', create_by='吴同', create_time='2026-10-22 19:56:02', update_by='', update_time=NULL, remark='AI 模型与密钥管理目录' WHERE menu_id=1061;

UPDATE om_target SET version_id=1, question='指定成品由哪些零部件组成？某零部件用于哪些成品？', question_source='产品研发部门提出', business_scenarios='产品组成与影响范围分析', accept_criterion='能够查询成品包含的零部件，并支持按零部件反向查询其应用的成品。', accept_status=1, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:05:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:05:00', remark=NULL WHERE id=1 AND priority=3;
UPDATE om_target SET version_id=1, question='指定出库单的收货方是谁，实际发往哪个地址？', question_source='销售及物流部门提出', business_scenarios='发货与收货核对', accept_criterion='能够查询出库单关联的采购方，并展示收货地址及采购方联系信息。', accept_status=0, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:05:30', update_by='吴同', updater_id=1, update_time='2026-10-20 10:05:30', remark=NULL WHERE id=2 AND priority=2;
UPDATE om_target SET version_id=1, question='指定出库单由哪家物流服务商承运？', question_source='物流管理部门提出', business_scenarios='运输责任追溯', accept_criterion='能够按出库单查询承运商，并支持查询承运商承担的出库发货记录。', accept_status=2, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:06:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:06:00', remark=NULL WHERE id=3 AND priority=3;
UPDATE om_target SET version_id=1, question='指定物料涉及哪些出库单，分别用于什么业务？', question_source='仓储部门提出', business_scenarios='物料出库流向追溯', accept_criterion='能够按物料查询相关出库单，并展示出库日期、用途、数量及经办人员。', accept_status=1, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:06:30', update_by='吴同', updater_id=1, update_time='2026-10-20 10:06:30', remark=NULL WHERE id=4 AND priority=2;
UPDATE om_target SET version_id=1, question='指定入库单记录了哪些到货物料，验收结果如何？', question_source='质量管理部门提出', business_scenarios='到货验收追溯', accept_criterion='能够查询入库单对应的物料，并展示入库数量、计量单位、验收结果和验收人员。', accept_status=0, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:07:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:07:00', remark=NULL WHERE id=5 AND priority=3;
UPDATE om_target SET version_id=1, question='哪些采购订单尚未关联入库单？已到货订单对应哪些入库记录？', question_source='仓储部门提出', business_scenarios='采购到货核对', accept_criterion='能够识别未关联入库单的采购订单，并查询已关联订单的入库记录。', accept_status=1, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:07:30', update_by='吴同', updater_id=1, update_time='2026-10-20 10:07:30', remark=NULL WHERE id=6 AND priority=3;
UPDATE om_target SET version_id=1, question='指定采购订单采购了哪些物料？某物料涉及哪些采购订单？', question_source='生产计划部门提出', business_scenarios='采购需求与物料追踪', accept_criterion='能够在采购订单与物料之间进行双向查询，并展示物料编码、名称和计量单位。', accept_status=0, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:08:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:08:00', remark=NULL WHERE id=7 AND priority=3;
UPDATE om_target SET version_id=1, question='采购订单分别由哪些供应商承接？', question_source='采购部门提出', business_scenarios='订单供货责任追溯', accept_criterion='能够按采购订单查询供应商，并支持按供应商查询其承接的采购订单。', accept_status=0, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:08:30', update_by='吴同', updater_id=1, update_time='2026-10-20 10:08:30', remark=NULL WHERE id=8 AND priority=3;
UPDATE om_target SET version_id=1, question='指定采购方下达了哪些采购订单？', question_source='采购部门提出', business_scenarios='采购业务执行追踪', accept_criterion='能够按采购方查询其下达的采购订单，并展示订单编号、日期及执行状态。', accept_status=2, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:09:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:09:00', remark=NULL WHERE id=9 AND priority=2;
UPDATE om_target SET version_id=1, question='指定供应商能够提供哪些物料？同一物料有哪些供应来源？', question_source='供应链管理部门提出', business_scenarios='供应来源分析', accept_criterion='能够查询供应商提供的物料清单，并支持按物料查询对应的供应商。', accept_status=0, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:09:30', update_by='吴同', updater_id=1, update_time='2026-10-20 10:09:30', remark=NULL WHERE id=10 AND priority=3;
UPDATE om_target SET version_id=1, question='各组织负责哪些采购订单的维护和管理？', question_source='采购管理部门提出', business_scenarios='采购职责与管理范围梳理', accept_criterion='能够按组织查询其管理的采购订单，并支持从采购订单反向查询管理组织。', accept_status=1, object_type_num=11, object_attribute_num=0, object_relationship_num=11, valid_flag=1, del_flag=0, create_by='吴同', creator_id=1, create_time='2026-10-20 10:10:00', update_by='吴同', updater_id=1, update_time='2026-10-20 10:10:00', remark=NULL WHERE id=11 AND priority=2;

INSERT INTO om_target_task VALUES(1, 11, 1, '仓库实体及核心属性已完整覆盖，包含库区、库位层级关联定义', 1, 0, '吴同', 2, '2026-10-23 17:43:17', NULL, NULL, '2026-10-23 16:43:17', '基础实体验证通过');
INSERT INTO om_target_task VALUES(2, 11, 1, '物料实体与库存批次的一对多关联关系已定义完整', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '');
INSERT INTO om_target_task VALUES(3, 11, 2, '库存状态枚举值未完全覆盖，缺少冻结、待检等细分状态项', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '需补充枚举项，待迭代完善');
INSERT INTO om_target_task VALUES(4, 11, 1, '入库单实体及明细结构已覆盖，包含单据全状态流转定义', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '');
INSERT INTO om_target_task VALUES(5, 11, 1, '出库单实体与收货方的关联关系已明确', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '');
INSERT INTO om_target_task VALUES(6, 11, 2, '盘点单实体属性不完整，缺少盘点类型、盘点范围等核心字段', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '下一版本补充完善');
INSERT INTO om_target_task VALUES(7, 11, 1, '库存台账数据类型定义正确，数值字段与计量单位属性匹配', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '数据类型验证通过');
INSERT INTO om_target_task VALUES(8, 11, 1, '物料与库存台账的主从关联关系已明确定义', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '');
INSERT INTO om_target_task VALUES(9, 11, 2, '库位编码规则未在本体中定义，缺少编码规范说明属性', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '需补充编码规则元数据');
INSERT INTO om_target_task VALUES(10, 11, 1, '包含范围中定义的出入库核心业务场景已全部覆盖', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '业务场景验证通过');
INSERT INTO om_target_task VALUES(11, 11, 1, '排除范围中财务核算、成本计算相关内容未纳入本体边界', 1, 0, '吴同', 2, '2026-10-23 16:43:17', NULL, NULL, '2026-10-23 16:43:17', '边界合规验证通过');



INSERT INTO `system_menu` VALUES (1196, '质量验证', 1195, 6, ':versionId/qualityValidation', 'om/qualityValidation/index', NULL, '', 1, 0, 'C', '0', '0', '', '#', '吴同', '2026-10-26 15:27:19', '吴同', '2026-10-28 09:44:50', '');
INSERT INTO `system_menu` VALUES (1195, '质量中心', 1078, 6, 'quality', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, '质量中心', '吴同', '2026-10-28 09:44:37', '', NULL, '');

-- AI 辅助建模（任务、解析过程、证据和候选数据）
CREATE TABLE IF NOT EXISTS `om_ai_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_code` varchar(64) NOT NULL COMMENT '任务编号',
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
  UNIQUE KEY `uk_om_ai_task_code` (`task_code`),
  KEY `idx_om_ai_task_version` (`version_id`, `status`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI辅助建模任务';

CREATE TABLE IF NOT EXISTS `om_ai_file` (
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

CREATE TABLE IF NOT EXISTS `om_ai_stage` (
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

CREATE TABLE IF NOT EXISTS `om_ai_evidence` (
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

CREATE TABLE IF NOT EXISTS `om_ai_target` (
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

CREATE TABLE IF NOT EXISTS `om_ai_term` (
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



CREATE TABLE IF NOT EXISTS `om_ai_enum_type` (
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
CREATE TABLE IF NOT EXISTS `om_ai_enum_data` (
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

CREATE TABLE IF NOT EXISTS `om_ai_object` (
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
CREATE TABLE IF NOT EXISTS `om_ai_attribute` (
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
CREATE TABLE IF NOT EXISTS `om_ai_relation` (
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

CREATE TABLE IF NOT EXISTS `om_ai_object_target` (
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
CREATE TABLE IF NOT EXISTS `om_ai_relation_target` (
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
CREATE TABLE IF NOT EXISTS `om_ai_data_evidence` (
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
CREATE TABLE IF NOT EXISTS `om_ai_adopt_log` (
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

INSERT IGNORE INTO `system_dict_type` (`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`remark`) VALUES
('AI建模任务状态','om_ai_task_status','0','system',NOW(),'AI辅助建模任务状态'),
('AI候选采纳状态','om_ai_adopt_status','0','system',NOW(),'AI辅助建模候选采纳状态'),
('AI解析阶段状态','om_ai_stage_status','0','system',NOW(),'AI解析阶段状态'),
('AI文件解析状态','om_ai_file_parse_status','0','system',NOW(),'AI上传文件解析状态');
INSERT IGNORE INTO `system_dict_data` (`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`is_default`,`status`,`create_by`,`create_time`,`remark`) VALUES
(1,'待执行','0','om_ai_task_status','','info','N','0','system',NOW(),''),(2,'解析中','1','om_ai_task_status','','warning','N','0','system',NOW(),''),(3,'解析成功','2','om_ai_task_status','','success','N','0','system',NOW(),''),(4,'解析失败','3','om_ai_task_status','','danger','N','0','system',NOW(),''),
(1,'待采纳','0','om_ai_adopt_status','','warning','N','0','system',NOW(),''),(2,'已采纳','1','om_ai_adopt_status','','success','N','0','system',NOW(),''),(3,'采纳失败','2','om_ai_adopt_status','','danger','N','0','system',NOW(),''),
(1,'等待','0','om_ai_stage_status','','info','N','0','system',NOW(),''),(2,'执行中','1','om_ai_stage_status','','warning','N','0','system',NOW(),''),(3,'成功','2','om_ai_stage_status','','success','N','0','system',NOW(),''),(4,'失败','3','om_ai_stage_status','','danger','N','0','system',NOW(),''),
(1,'待解析','0','om_ai_file_parse_status','','info','N','0','system',NOW(),''),(2,'解析中','1','om_ai_file_parse_status','','warning','N','0','system',NOW(),''),(3,'解析成功','2','om_ai_file_parse_status','','success','N','0','system',NOW(),''),(4,'解析失败','3','om_ai_file_parse_status','','danger','N','0','system',NOW(),'');

UPDATE `system_menu` SET `perms`='om:aiModeling:list' WHERE `menu_id`=1079;
INSERT IGNORE INTO `system_menu` (`menu_id`,`menu_name`,`parent_id`,`order_num`,`path`,`component`,`query`,`route_name`,`is_frame`,`is_cache`,`menu_type`,`visible`,`status`,`perms`,`icon`,`create_by`,`create_time`,`remark`) VALUES
(1200,'AI建模查询',1079,1,'','','','',1,0,'F','0','0','om:aiModeling:list','#','system',NOW(),''),
(1201,'AI建模新增',1079,2,'','','','',1,0,'F','0','0','om:aiModeling:add','#','system',NOW(),''),
(1202,'AI建模修改',1079,3,'','','','',1,0,'F','0','0','om:aiModeling:edit','#','system',NOW(),''),
(1203,'AI建模删除',1079,4,'','','','',1,0,'F','0','0','om:aiModeling:remove','#','system',NOW(),''),
(1204,'AI建模采纳',1079,5,'','','','',1,0,'F','0','0','om:aiModeling:adopt','#','system',NOW(),''),
(1205,'AI建模重解析',1079,6,'','','','',1,0,'F','0','0','om:aiModeling:reparse','#','system',NOW(),''),
(1206,'AI建模证据',1079,7,'','','','',1,0,'F','0','0','om:aiModeling:evidence','#','system',NOW(),'');

-- 兼容历史候选数据：未抽取到枚举类型时生成合法且稳定的默认编码。
UPDATE `om_ai_enum_type`
SET `enum_type` = CONCAT('enum_type_', `id`)
WHERE `enum_type` IS NULL OR TRIM(`enum_type`) = '';
