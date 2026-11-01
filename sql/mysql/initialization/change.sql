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



INSERT INTO system_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES(1197, '项目版本查询', 1196, 1, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:query', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO system_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES(1198, '版本管理新增', 1196, 2, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:add', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO system_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES(1199, '版本管理修改', 1196, 3, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:edit', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO system_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES(1200, '版本管理删除', 1196, 4, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:remove', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
INSERT INTO system_menu (menu_id, menu_name, parent_id, order_num, `path`, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES(1201, '版本管理对比', 1196, 5, '#', '', NULL, '', 1, 0, 'F', '0', '0', 'basic:project:projectversion:diff', '#', 'admin', '2026-10-27 15:07:19', '', NULL, '');
UPDATE `system_menu` SET `menu_name` = '约束规则', `parent_id` = 1078, `order_num` = 4, `path` = ':versionId/constraint', `component` = NULL, `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'M', `visible` = '0', `status` = '0', `perms` = '', `icon` = '约束规则', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-27 15:28:39', `remark` = '' WHERE `menu_id` = 1163;
UPDATE `system_menu` SET `menu_name` = 'AI辅助建模', `parent_id` = 1078, `order_num` = 5, `path` = ':versionId/aiModeling', `component` = 'om/aiModeling/index', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'C', `visible` = '0', `status` = '0', `perms` = '', `icon` = 'organization-chart', `create_by` = '吴同', `create_time` = '2026-10-19 14:50:52', `update_by` = '超级管理员', `update_time` = '2026-10-27 14:18:15', `remark` = '' WHERE `menu_id` = 1079;
UPDATE `system_menu` SET `menu_name` = '模型公理', `parent_id` = 1163, `order_num` = 1, `path` = 'modelAxiom', `component` = 'om/om/constraint/modelAxiom/index', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'C', `visible` = '0', `status` = '0', `perms` = 'om:constraint:modelaxi:list', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 1164;
UPDATE `system_menu` SET `menu_name` = '数据规则', `parent_id` = 1163, `order_num` = 2, `path` = 'dataRule', `component` = 'om/om/constraint/dataRule/index', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'C', `visible` = '0', `status` = '0', `perms` = 'om:constraint:datarule:list', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '', `update_time` = NULL, `remark` = '' WHERE `menu_id` = 1169;
UPDATE `system_menu` SET `menu_name` = '模型公理查询', `parent_id` = 1164, `order_num` = 1, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:modelaxi:query', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:42:22', `remark` = '' WHERE `menu_id` = 1165;
UPDATE `system_menu` SET `menu_name` = '模型公理新增', `parent_id` = 1164, `order_num` = 2, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:modelaxi:add', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:42:28', `remark` = '' WHERE `menu_id` = 1166;
UPDATE `system_menu` SET `menu_name` = '模型公理修改', `parent_id` = 1164, `order_num` = 3, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:modelaxi:edit', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:42:36', `remark` = '' WHERE `menu_id` = 1167;
UPDATE `system_menu` SET `menu_name` = '模型公理删除', `parent_id` = 1164, `order_num` = 4, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:modelaxi:remove', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:42:41', `remark` = '' WHERE `menu_id` = 1168;
UPDATE `system_menu` SET `menu_name` = '数据规则查询', `parent_id` = 1169, `order_num` = 1, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:datarule:query', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:25', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:43:02', `remark` = '' WHERE `menu_id` = 1170;
UPDATE `system_menu` SET `menu_name` = '数据规则新增', `parent_id` = 1169, `order_num` = 2, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:datarule:add', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:26', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:43:17', `remark` = '' WHERE `menu_id` = 1171;
UPDATE `system_menu` SET `menu_name` = '数据规则修改', `parent_id` = 1169, `order_num` = 3, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:datarule:edit', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:26', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:43:21', `remark` = '' WHERE `menu_id` = 1172;
UPDATE `system_menu` SET `menu_name` = '数据规则删除', `parent_id` = 1169, `order_num` = 4, `path` = '#', `component` = '', `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'F', `visible` = '0', `status` = '0', `perms` = 'om:constraint:datarule:remove', `icon` = '#', `create_by` = 'admin', `create_time` = '2026-10-22 19:56:26', `update_by` = '超级管理员', `update_time` = '2026-10-28 11:43:26', `remark` = '' WHERE `menu_id` = 1173;
UPDATE `system_dict_data` SET `dict_sort` = 1, `dict_label` = '子类型', `dict_value` = 'SUBCLASS', `dict_type` = 'om_axiom_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'Y', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:30', `update_by` = '吴同', `update_time` = '2026-10-28 13:57:57', `remark` = '' WHERE `dict_code` = 72;
UPDATE `system_dict_data` SET `dict_sort` = 2, `dict_label` = '等价', `dict_value` = 'EQUIVALENT', `dict_type` = 'om_axiom_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:30', `update_by` = '吴同', `update_time` = '2026-10-28 13:58:31', `remark` = '' WHERE `dict_code` = 73;
UPDATE `system_dict_data` SET `dict_sort` = 3, `dict_label` = '互斥', `dict_value` = 'DISJOINT', `dict_type` = 'om_axiom_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:30', `update_by` = '吴同', `update_time` = '2026-10-28 13:58:35', `remark` = '' WHERE `dict_code` = 74;
UPDATE `system_dict_data` SET `dict_sort` = 4, `dict_label` = 'Domain / Range', `dict_value` = 'DOMAIN_RANGE', `dict_type` = 'om_axiom_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:30', `update_by` = '吴同', `update_time` = '2026-10-28 13:58:39', `remark` = '' WHERE `dict_code` = 75;
UPDATE `system_dict_data` SET `dict_sort` = 5, `dict_label` = '属性范围', `dict_value` = 'PROPERTY_RANGE', `dict_type` = 'om_axiom_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:31', `update_by` = '吴同', `update_time` = '2026-10-28 13:58:42', `remark` = '' WHERE `dict_code` = 76;
UPDATE `system_dict_data` SET `dict_sort` = 1, `dict_label` = '必填', `dict_value` = 'REQUIRED', `dict_type` = 'om_data_rule_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'Y', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:31', `update_by` = '吴同', `update_time` = '2026-10-28 13:54:11', `remark` = '' WHERE `dict_code` = 77;
UPDATE `system_dict_data` SET `dict_sort` = 2, `dict_label` = '枚举', `dict_value` = 'ENUM', `dict_type` = 'om_data_rule_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:31', `update_by` = '吴同', `update_time` = '2026-10-28 13:54:16', `remark` = '' WHERE `dict_code` = 78;
UPDATE `system_dict_data` SET `dict_sort` = 3, `dict_label` = '取值范围', `dict_value` = 'RANGE', `dict_type` = 'om_data_rule_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:31', `update_by` = '吴同', `update_time` = '2026-10-28 13:54:20', `remark` = '' WHERE `dict_code` = 79;
UPDATE `system_dict_data` SET `dict_sort` = 4, `dict_label` = '格式校验', `dict_value` = 'FORMAT', `dict_type` = 'om_data_rule_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 09:52:31', `update_by` = '吴同', `update_time` = '2026-10-28 13:54:23', `remark` = '' WHERE `dict_code` = 80;


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
-- Records of om_model_axiom
-- ----------------------------

INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (1, 1, '成品属于物料', '成品是物料的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'INFO', 4, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (2, 1, '零部件属于物料', '零部件是物料的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'WARNING', 5, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (3, 1, '原材料属于物料', '原材料是物料的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'ERROR', 6, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (4, 1, '物流服务商属于组织', '物流服务商是组织的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'INFO', 8, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (5, 1, '采购方属于组织', '采购方是组织的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'WARNING', 9, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (6, 1, '供应商属于组织', '供应商是组织的子类型，沿用现有对象分类层级。', 'SUBCLASS', 'ERROR', 10, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (7, 1, '组成关系定义域和值域', '零部件通过装配构成成品，用于产品结构追溯。', 'DOMAIN_RANGE', 'INFO', NULL, NULL, 1, '组成', 5, 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (8, 1, '指定收货方关系定义域和值域', '出库单指定接收本次发货物料的采购方。', 'DOMAIN_RANGE', 'WARNING', NULL, NULL, 2, '指定收货方', 1, 9, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (9, 1, '承运关系定义域和值域', '物流服务商承担出库单对应发货物料的运输。', 'DOMAIN_RANGE', 'ERROR', NULL, NULL, 3, '承运', 8, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (10, 1, '记录出库物料关系定义域和值域', '出库单记录本次办理出库和发货的物料。', 'DOMAIN_RANGE', 'ERROR', NULL, NULL, 4, '记录出库物料', 1, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (11, 1, '记录入库物料关系定义域和值域', '入库单记录实际验收并办理入库的物料。', 'DOMAIN_RANGE', 'INFO', NULL, NULL, 5, '记录入库物料', 2, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (12, 1, '对应采购订单关系定义域和值域', '入库单关联本次到货对应的采购订单。', 'DOMAIN_RANGE', 'WARNING', NULL, NULL, 6, '对应采购订单', 2, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (13, 1, '采购物料关系定义域和值域', '采购订单记录本次需要采购的物料。', 'DOMAIN_RANGE', 'ERROR', NULL, NULL, 7, '采购物料', 3, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (14, 1, '指定供应商关系定义域和值域', '采购订单指定承担本次供货任务的供应商。', 'DOMAIN_RANGE', 'INFO', NULL, NULL, 8, '指定供应商', 3, 10, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (15, 1, '下达关系定义域和值域', '采购方根据采购需求下达采购订单。', 'DOMAIN_RANGE', 'WARNING', NULL, NULL, 9, '下达', 9, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (16, 1, '提供关系定义域和值域', '供应商向企业提供生产经营所需的物料。', 'DOMAIN_RANGE', 'ERROR', NULL, NULL, 10, '提供', 10, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (17, 1, '管理关系定义域和值域', '组织负责采购订单的维护、审批协调和业务管理。', 'DOMAIN_RANGE', 'INFO', NULL, NULL, 11, '管理', 11, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (18, 1, '出库单出库状态属性范围', '出库单当前业务处理状态', 'PROPERTY_RANGE', 'WARNING', 1, NULL, NULL, NULL, NULL, NULL, 1, 2, NULL, 10, 'PROJECT', NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (19, 1, '出库单经办人员属性范围', '负责办理本次出库的人员姓名', 'PROPERTY_RANGE', 'ERROR', 1, NULL, NULL, NULL, NULL, NULL, 2, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (20, 1, '出库单出库数量属性范围', '本次出库确认的物料数量', 'PROPERTY_RANGE', 'ERROR', 1, NULL, NULL, NULL, NULL, NULL, 5, 1, 2, NULL, NULL, '件', 3, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (21, 1, '出库单出库用途属性范围', '本次出库的业务用途', 'PROPERTY_RANGE', 'INFO', 1, NULL, NULL, NULL, NULL, NULL, 6, 2, NULL, 1, 'PROJECT', NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_model_axiom` (`id`, `version_id`, `name`, `description`, `type`, `level`, `object_type_id`, `other_type_id`, `relation_id`, `relation_name`, `domain_id`, `range_id`, `attribute_id`, `type_category`, `basic_type_code`, `business_type_id`, `business_type_source`, `unit`, `decimal_places`, `format`, `enum_type`, `enum_source`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (22, 1, '出库单出库日期属性范围', '物料实际办理出库的日期', 'PROPERTY_RANGE', 'WARNING', 1, NULL, NULL, NULL, NULL, NULL, 10, 2, NULL, 22, 'PROJECT', NULL, NULL, NULL, NULL, NULL, 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');


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
-- Records of om_data_rule
-- ----------------------------
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (1, 1, '出库单号不能为空', '出库单号不能为空，请补充业务编号。', 'REQUIRED', 'INFO', 'OR', '[{\"value\": null, \"operator\": null, \"attributeId\": \"11\", \"objectTypeId\": \"1\"}]', '出库单号不能为空，请补充业务编号。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (2, 1, '入库单号不能为空', '入库单号不能为空，请补充业务编号。', 'REQUIRED', 'WARNING', 'OR', '[{\"value\": null, \"operator\": null, \"attributeId\": \"22\", \"objectTypeId\": \"2\"}]', '入库单号不能为空，请补充业务编号。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (3, 1, '出库仓库信息完整性', '仓库名称或仓库编码为空，请补全仓库信息。', 'REQUIRED', 'ERROR', 'OR', '[{\"value\": null, \"operator\": null, \"attributeId\": \"8\", \"objectTypeId\": \"1\"}, {\"value\": null, \"operator\": null, \"attributeId\": \"9\", \"objectTypeId\": \"1\"}]', '仓库名称或仓库编码为空，请补全仓库信息。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (4, 1, '入库仓库信息完整性', '仓库名称或仓库编码为空，请补全仓库信息。', 'REQUIRED', 'INFO', 'OR', '[{\"value\": null, \"operator\": null, \"attributeId\": \"19\", \"objectTypeId\": \"2\"}, {\"value\": null, \"operator\": null, \"attributeId\": \"20\", \"objectTypeId\": \"2\"}]', '仓库名称或仓库编码为空，请补全仓库信息。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (5, 1, '出库数量必须大于零', '出库数量必须大于零。', 'RANGE', 'WARNING', 'OR', '[{\"value\": \"0\", \"operator\": \"LE\", \"attributeId\": \"5\", \"objectTypeId\": \"1\"}]', '出库数量必须大于零。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (6, 1, '入库数量必须大于零', '入库数量必须大于零。', 'RANGE', 'ERROR', 'OR', '[{\"value\": \"0\", \"operator\": \"LE\", \"attributeId\": \"16\", \"objectTypeId\": \"2\"}]', '入库数量必须大于零。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (7, 1, '出库日期格式校验', '出库日期必须符合yyyy-MM-dd格式。', 'FORMAT', 'INFO', 'OR', '[{\"value\": \"yyyy-MM-dd\", \"operator\": null, \"attributeId\": \"10\", \"objectTypeId\": \"1\"}]', '出库日期必须符合yyyy-MM-dd格式。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (8, 1, '入库日期格式校验', '入库日期必须符合yyyy-MM-dd格式。', 'FORMAT', 'WARNING', 'OR', '[{\"value\": \"yyyy-MM-dd\", \"operator\": null, \"attributeId\": \"21\", \"objectTypeId\": \"2\"}]', '入库日期必须符合yyyy-MM-dd格式。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (9, 1, '出库状态取值校验', '属性取值必须使用当前项目枚举中定义的编码。', 'ENUM', 'ERROR', 'OR', '[{\"value\": \"CLOSED,CANCELLED,COMPLETED,PARTIAL_COMPLETED,EXECUTING,TO_EXECUTE,REJECTED,APPROVED,PENDING_APPROVAL,TO_SUBMIT,DRAFT\", \"operator\": null, \"attributeId\": \"1\", \"objectTypeId\": \"1\"}]', '属性取值必须使用当前项目枚举中定义的编码。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (10, 1, '入库状态取值校验', '属性取值必须使用当前项目枚举中定义的编码。', 'ENUM', 'ERROR', 'OR', '[{\"value\": \"CLOSED,CANCELLED,COMPLETED,PARTIAL_COMPLETED,EXECUTING,TO_EXECUTE,REJECTED,APPROVED,PENDING_APPROVAL,TO_SUBMIT,DRAFT\", \"operator\": null, \"attributeId\": \"12\", \"objectTypeId\": \"2\"}]', '属性取值必须使用当前项目枚举中定义的编码。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (11, 1, '出库用途取值校验', '属性取值必须使用当前项目枚举中定义的编码。', 'ENUM', 'INFO', 'OR', '[{\"value\": \"INVENTORY_LOSS,SCRAP_DISPOSAL,QUALITY_TEST,RD_TRIAL,MAINTENANCE,SAMPLE_DELIVERY,WAREHOUSE_TRANSFER,PURCHASE_RETURN,OUTSOURCING,PRODUCTION_PICKING,SALES_DELIVERY\", \"operator\": null, \"attributeId\": \"6\", \"objectTypeId\": \"1\"}]', '属性取值必须使用当前项目枚举中定义的编码。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');
INSERT INTO `om_data_rule` (`id`, `version_id`, `name`, `description`, `type`, `level`, `condition_relation`, `conditions`, `failure_message`, `valid_flag`, `del_flag`, `create_by`, `creator_id`, `create_time`, `update_by`, `updater_id`, `update_time`, `remark`) VALUES (12, 1, '出库经办与库位同时缺失提示', '经办人员和库位同时为空，请完善出库追溯信息。', 'REQUIRED', 'WARNING', 'AND', '[{\"value\": null, \"operator\": null, \"attributeId\": \"2\", \"objectTypeId\": \"1\"}, {\"value\": null, \"operator\": null, \"attributeId\": \"7\", \"objectTypeId\": \"1\"}]', '经办人员和库位同时为空，请完善出库追溯信息。', 1, 0, '吴同', 1, '2026-10-28 13:40:27', '吴同', 1, '2026-10-28 13:40:27', '供应链约束规则示例数据');


-- 项目版本演示数据
delete FROM basic_project_version where id in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(1, 1, 'v1.0.0', '初始正式版本，完整覆盖采购与仓储协同核心实体、属性及关联关系，支撑基础本体建模与业务场景映射', NULL, 0, '吴同', 1, '2026-10-20 09:47:23', '吴同', NULL, '2026-10-20 09:47:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(2, 2, 'v1.0.0', '首个正式版本，定义安全隐患、整改流程、危险源管控等核心模型，因业务范围调整暂停后续迭代', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(3, 3, 'v1.2.0', '迭代优化版本，完善组织层级与岗位职级映射，补充考勤记录与薪酬等级的关联属性定义', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(4, 4, 'v2.0.0', '重大版本升级，重构客户全生命周期模型，新增销售线索转商机链路与客户分层管理规则', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(5, 5, 'v1.0.0', '初始正式版本，搭建财务核算科目树与核算维度基础模型，已由新版财务本体替代并停用', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(6, 6, 'v0.9.0-beta', 'Beta测试版本，完成BOM多级结构与工艺路线核心建模，待专家评审通过后发布正式版本', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(7, 7, 'v0.1.0', '初始草稿版本，完成运输订单、车辆调度、运单状态等核心对象定义，业务场景待补充完善', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(8, 8, 'v1.1.0', '迭代优化版本，优化任务依赖关系与里程碑节点模型，补充进度状态流转规则定义', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(9, 9, 'v1.0.0', '初始正式版本，覆盖质量检验标准、不合格品处理、质量追溯全流程核心业务模型', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(10, 10, 'v1.3.0', '最终维护版本，完善设备全生命周期与维保计划关联模型，已升级至统一资产管理本体', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(11, 11, 'v2.1.0', '迭代优化版本，补充交付计划拆分逻辑，优化销售订单全生命周期状态流转定义', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(12, 12, 'v1.0.0-rc1', '发布候选版本，完成多仓库库存层级、批次管理与出入库单据建模，待最终验证后正式发布', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(13, 13, 'v1.0.0', '初始正式版本，覆盖供应商全生命周期、分级评价体系与品类供应关系核心模型', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(14, 14, 'v0.2.0', '迭代草稿版本，补充需求跟踪矩阵与缺陷关联定义，行业业务场景持续梳理完善中', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(15, 15, 'v1.0.0', '初始正式版本，覆盖办公资产全生命周期与领用归还流程，支撑行政资产台账管理场景', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(16, 16, 'v2.0.0', '重大版本升级，重构指标体系层级模型，新增指标血缘追溯与业务主题分类定义', NULL, 0, '吴同', 1, '2026-10-22 09:53:58', '吴同', NULL, '2026-10-22 09:24:23', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(17, 1, 'v1.1.0', '迭代优化版本，基于v1.0.0补充出入库单据联动逻辑、物料批次追溯属性与供应商资质关联关系，优化零部件与成品的BOM组成层级定义', 'v1.0.0', 0, '吴同', 1, '2026-10-25 10:12:36', '吴同', NULL, '2026-10-25 10:12:36', NULL);
INSERT INTO basic_project_version (id, project_id, version, description, base_version, del_flag, create_by, creator_id, create_time, update_by, updater_id, update_time, remark) VALUES(18, 1, 'v2.0.0', '重大架构升级版本，重构库存多层级管控模型，新增跨仓调拨、在途库存、安全库存阈值定义，扩展多组织采购协同与多仓库联动能力', 'v1.1.0', 0, '吴同', 1, '2026-10-28 14:30:18', '吴同', NULL, '2026-10-28 14:30:18', '核心版本升级，向下兼容v1.x历史数据');



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

INSERT INTO `system_dict_type` VALUES (32, '发布状态', 'publish_status', '0', '吴同', '2026-04-24 10:01:38', '', NULL, '发布状态列表');
INSERT INTO `system_dict_type` VALUES (33, 'AI建模任务状态', 'om_ai_task_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI辅助建模任务状态');
INSERT INTO `system_dict_type` VALUES (34, 'AI候选采纳状态', 'om_ai_adopt_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI辅助建模候选采纳状态');
INSERT INTO `system_dict_type` VALUES (35, 'AI解析阶段状态', 'om_ai_stage_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI解析阶段状态');
INSERT INTO `system_dict_type` VALUES (36, 'AI文件解析状态', 'om_ai_file_parse_status', '0', '吴同', '2026-10-30 11:48:21', '', NULL, 'AI上传文件解析状态');

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

INSERT INTO `system_menu` VALUES (1202, '质量中心', 1078, 6, 'quality', NULL, NULL, '', 1, 0, 'M', '0', '0', NULL, '质量中心', '吴同', '2026-10-28 09:44:37', '', NULL, '');
INSERT INTO `system_menu` VALUES (1203, '质量验证', 1202, 6, ':versionId/qualityValidation', 'om/qualityValidation/index', NULL, '', 1, 0, 'C', '0', '0', '', '#', '吴同', '2026-10-26 15:27:19', '吴同', '2026-10-28 09:44:50', '');
INSERT INTO `system_menu` VALUES (1204, 'AI建模查询', 1079, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:list', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1205, 'AI建模新增', 1079, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:add', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1206, 'AI建模修改', 1079, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:edit', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1207, 'AI建模删除', 1079, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:remove', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1208, 'AI建模采纳', 1079, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:adopt', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1209, 'AI建模重解析', 1079, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:reparse', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');
INSERT INTO `system_menu` VALUES (1210, 'AI建模证据', 1079, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'om:aiModeling:evidence', '#', '吴同', '2026-10-30 11:53:43', '', NULL, '');


UPDATE `system_dict_type` SET `create_by` = '吴同' WHERE `dict_id` = 27;
UPDATE `system_dict_type` SET `create_by` = '吴同' WHERE `dict_id` = 28;
UPDATE `system_dict_type` SET `create_by` = '吴同' WHERE `dict_id` = 29;
UPDATE `system_dict_type` SET `create_by` = '吴同' WHERE `dict_id` = 30;
UPDATE `system_dict_type` SET `create_by` = '吴同' WHERE `dict_id` = 31;

UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 90;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 91;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 92;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 93;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 94;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 95;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 96;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 97;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 98;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 99;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 100;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 101;
UPDATE `system_dict_data` SET `create_by` = '吴同' WHERE `dict_code` = 102;


INSERT INTO `om_ai_attribute` VALUES (1, 1, 1, '订单状态', 'order_status', '记录订单生命周期流转节点', 1, 5, NULL, NULL, '', NULL, '', 'order_status', 'PROJECT', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (2, 1, 1, '订单金额', 'order_amount', '订单总交易金额，必须大于零', 2, NULL, 25, 'PROJECT', '元', 2, '', '', '', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (3, 1, 2, '供应商编码', 'supplier_code', '唯一标识供应商，S开头后接6位数字', 1, 0, NULL, NULL, '', NULL, 'S[0-9]{6}', '', '', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (4, 1, 2, '供应商分类', 'supplier_category', '区分物料供应商与服务供应商', 1, 5, NULL, NULL, '', NULL, '', 'supplier_category', 'PROJECT', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (5, 1, 2, '成立日期', 'establishment_date', '供应商注册成立的时间，须早于当前日期', 1, 4, NULL, NULL, '', NULL, 'yyyy-MM-dd', '', '', 0, NULL, NULL, 0.90000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (6, 1, 4, '订购数量', 'order_quantity', '单次采购的物料件数，必须为正整数', 1, 1, NULL, NULL, '件', NULL, '', '', '', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_attribute` VALUES (7, 1, 4, '采购单价', 'unit_price', '单件物料的采购价格', 2, NULL, 19, 'PROJECT', '元/件', 4, '', '', '', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);

INSERT INTO `om_ai_axiom` VALUES (1, 1, '采购订单指定供应商关系约束', '采购订单必须关联至供应商对象，明确业务归属', 'DOMAIN_RANGE', 'INFO', NULL, NULL, '指定供应商', 1, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);
INSERT INTO `om_ai_axiom` VALUES (2, 1, '采购订单包含明细关系约束', '采购订单与采购明细之间为包含关系，支撑明细溯源', 'DOMAIN_RANGE', 'INFO', NULL, NULL, '包含明细', 1, 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);

INSERT INTO `om_ai_data_evidence` VALUES (1, 1, 'target', 1, 3, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (2, 1, 'target', 1, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (3, 1, 'target', 1, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (4, 1, 'target', 2, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (5, 1, 'target', 2, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (6, 1, 'target', 3, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (7, 1, 'target', 3, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (8, 1, 'term', 1, 4, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (9, 1, 'term', 1, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (10, 1, 'term', 1, 9, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (11, 1, 'term', 2, 4, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (12, 1, 'term', 2, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (13, 1, 'term', 2, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (14, 1, 'term', 3, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (15, 1, 'term', 3, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (16, 1, 'term', 4, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (17, 1, 'term', 4, 21, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (18, 1, 'term', 5, 9, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (19, 1, 'enum_type', 1, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (20, 1, 'enum_type', 1, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (21, 1, 'enum_data', 1, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (22, 1, 'enum_data', 1, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (23, 1, 'enum_data', 2, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (24, 1, 'enum_data', 2, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (25, 1, 'enum_data', 3, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (26, 1, 'enum_data', 3, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (27, 1, 'enum_data', 4, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (28, 1, 'enum_data', 4, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (29, 1, 'enum_type', 2, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (30, 1, 'enum_data', 5, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (31, 1, 'enum_data', 6, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:25:43', NULL, NULL, '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (32, 1, 'object', 1, 3, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (33, 1, 'object', 1, 4, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (34, 1, 'object', 1, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (35, 1, 'object', 1, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (36, 1, 'object', 1, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (37, 1, 'object', 1, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (38, 1, 'object', 1, 17, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (39, 1, 'object', 1, 18, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (40, 1, 'object', 1, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (41, 1, 'object', 1, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (42, 1, 'object', 2, 3, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (43, 1, 'object', 2, 4, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (44, 1, 'object', 2, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (45, 1, 'object', 2, 9, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (46, 1, 'object', 2, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (47, 1, 'object', 2, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (48, 1, 'object', 2, 17, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (49, 1, 'object', 2, 20, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (50, 1, 'object', 2, 22, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (51, 1, 'object', 2, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (52, 1, 'object', 3, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (53, 1, 'object', 3, 11, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (54, 1, 'object', 3, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (55, 1, 'object', 3, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (56, 1, 'object', 3, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (57, 1, 'object', 4, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (58, 1, 'object', 4, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (59, 1, 'object', 4, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (60, 1, 'object', 4, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (61, 1, 'object', 4, 21, 'POSITIVE', 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (62, 1, 'attribute', 1, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:48', NULL, NULL, '2026-10-31 20:29:48', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (63, 1, 'attribute', 1, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:48', NULL, NULL, '2026-10-31 20:29:48', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (64, 1, 'attribute', 2, 18, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (65, 1, 'attribute', 3, 9, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (66, 1, 'attribute', 3, 20, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (67, 1, 'attribute', 4, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (68, 1, 'attribute', 5, 22, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (69, 1, 'attribute', 6, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (70, 1, 'attribute', 6, 21, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (71, 1, 'attribute', 7, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (72, 1, 'relation', 1, 17, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (73, 1, 'relation', 1, 24, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (74, 1, 'relation', 2, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (75, 1, 'relation', 2, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (76, 1, 'relation', 3, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (77, 1, 'relation', 3, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (78, 1, 'relation', 4, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (79, 1, 'relation', 4, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (80, 1, 'axiom', 1, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (81, 1, 'axiom', 1, 14, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (82, 1, 'axiom', 1, 17, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (83, 1, 'axiom', 2, 10, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (84, 1, 'axiom', 2, 13, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (85, 1, 'data_rule', 1, 18, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (86, 1, 'data_rule', 2, 6, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (87, 1, 'data_rule', 2, 19, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (88, 1, 'data_rule', 3, 21, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);
INSERT INTO `om_ai_data_evidence` VALUES (89, 1, 'data_rule', 4, 7, 'POSITIVE', 1, '吴同', '2026-10-31 20:32:32', NULL, NULL, '2026-10-31 20:32:32', NULL);

INSERT INTO `om_ai_data_rule` VALUES (1, 1, '订单金额正数校验', '订单金额必须大于零，禁止零或负数录入', 'RANGE', 'ERROR', 'AND', '[{\"objectCandidateId\":1,\"attributeCandidateId\":2,\"operator\":\"GT\",\"value\":\"0\"}]', '订单金额必须大于0元', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);
INSERT INTO `om_ai_data_rule` VALUES (2, 1, '订单状态枚举限制', '订单状态仅限系统预设的四种状态流转', 'ENUM', 'ERROR', 'AND', '[{\"objectCandidateId\":1,\"attributeCandidateId\":1,\"value\":\"草稿,已提交,已审批,已取消\"}]', '订单状态只能选择草稿、已提交、已审批或已取消', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);
INSERT INTO `om_ai_data_rule` VALUES (3, 1, '订购数量正整数校验', '采购明细的订购数量必须为正整数，禁止小数', 'RANGE', 'ERROR', 'AND', '[{\"objectCandidateId\":4,\"attributeCandidateId\":6,\"operator\":\"GT\",\"value\":\"0\"}]', '订购数量必须为大于0的整数', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);
INSERT INTO `om_ai_data_rule` VALUES (4, 1, '供应商分类枚举限制', '供应商分类仅限物料供应商与服务供应商两类', 'ENUM', 'WARNING', 'AND', '[{\"objectCandidateId\":2,\"attributeCandidateId\":4,\"value\":\"物料供应商,服务供应商\"}]', '供应商分类只能选择物料供应商或服务供应商', 0, NULL, NULL, 0.90000, 0, 1, '吴同', '2026-10-31 20:32:33', NULL, NULL, '2026-10-31 20:32:33', NULL);

INSERT INTO `om_ai_enum_data` VALUES (1, 1, 1, 0, '草稿', '草稿', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_data` VALUES (2, 1, 1, 1, '已提交', '已提交', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_data` VALUES (3, 1, 1, 2, '已审批', '已审批', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_data` VALUES (4, 1, 1, 3, '已取消', '已取消', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_data` VALUES (5, 1, 2, 0, '物料供应商', '物料供应商', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_data` VALUES (6, 1, 2, 1, '服务供应商', '服务供应商', '0', 0, NULL, NULL, NULL, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);

INSERT INTO `om_ai_enum_type` VALUES (1, 1, '订单状态', 'order_status', '0', 0, NULL, NULL, 0.90000, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_enum_type` VALUES (2, 1, '供应商分类', 'supplier_category', '0', 0, NULL, NULL, 0.90000, 0, 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);

INSERT INTO `om_ai_evidence` VALUES (1, 1, 1, 1, 'PARAGRAPH', NULL, NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购业务资料汇编（客户交付原始材料）', '9a283b5f201738f78458b342eabe52af9a6397439042c7f201588da237dd81e5', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (2, 1, 1, 2, 'PARAGRAPH', NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '一、业务概况', '6fa564367cce9ce0422295f9b2ba95454e66030066d7a47a9d00d2ff5d059dda', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (3, 1, 1, 3, 'PARAGRAPH', NULL, NULL, 3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '我们采购部负责全公司的生产物资与办公物资采购。目前最头疼的问题是，每张采购单最后到底由哪个供应商承接的，经常要翻纸质单据和多个系统才能查出来，出了质量问题找不到责任源头。领导要求把这块理顺。', '62e243f99312eb73e07e72931a8dd002ea3c7b640526b4c7661aea8ae4ef8d65', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (4, 1, 1, 4, 'PARAGRAPH', NULL, NULL, 4, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '现在公司没有统一的采购口径，不同部门对\"供应商\"的理解不一样：有的把给物料的地方叫供应商，有的把提供服务的也叫供应商。订单在系统里的叫法也不统一，有人叫采购单，有人叫订购单，其实都是一个东西。', '4ff79a95706074125d81bc3aaa3a9b14b04402f58f20acbe773d191c2b7782a5', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (5, 1, 1, 5, 'PARAGRAPH', NULL, NULL, 5, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '二、采购流程', '0f53ae98dd296129b998f8e5e2394bf1b30763a60a4f165a0e4d5d7ef365f308', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (6, 1, 1, 6, 'PARAGRAPH', NULL, NULL, 6, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '一个典型流程是这样的：业务部门提出采购需求 → 采购员登记成一张订单 → 订单里明确要买哪些东西、各买多少、单价多少 → 报给部门负责人审批 → 审批通过后生成正式订单 → 安排供应商供货。订单整个过程中有几种状态，一开始是草稿，登记完提交上去是已提交，领导批了就是已审批，如果中途不买了或者错了，就作废，叫已取消。', 'c9ccf95745597f6f1773b0fc214103aec0aa991e5ef57c8e12113961f867ec94', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (7, 1, 1, 7, 'PARAGRAPH', NULL, NULL, 7, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商有分类。一种是给我们供货的物料供应商，一种是提供安装、维护这类服务的服务供应商，两类要分开管理，考核指标也不一样。', 'cd51274917dec346b165a2ff309048a44f60eed90b6747c450dfa711a405a4f3', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (8, 1, 1, 8, 'PARAGRAPH', NULL, NULL, 8, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '三、对象与基础数据', 'eda4948ffc391e94428930f906599de2d18568c7a85dc391b60fd11b321ed226', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (9, 1, 1, 9, 'PARAGRAPH', NULL, NULL, 9, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '公司层面的组织是一个大的概念，集团、各分厂、各事业部都算组织，供应商从本质上也属于组织这一类，只是它是外部组织，给我们供货。每个供应商我们给编一个固定的编码，规则是大写S开头后面跟6位数字，比如S123456，这个编码在系统里要唯一，不能重复。', '9dff4393beb517bae2a8286bab62be6a887d7f1d311d556b1fc4964f01a8549e', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (10, 1, 1, 10, 'PARAGRAPH', NULL, NULL, 10, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料是我们采购的物资，每个物料也有个编码。一张采购订单会包含多个采购明细，明细就是\"这张单子里某一种物料买了多少、单价多少\"这样的记录。', '7200541cc528b9fe37b7f3ea61c0d87df6f36f34362ef904c8c92b7e83819486', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (11, 1, 1, 11, 'PARAGRAPH', NULL, NULL, 11, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '物料平时要归类，我们有个物料分类的概念，方便查找，不过这个分类目前还没完全建起来，系统里只是有个字段挂着，暂时不急着细管。', '6f166fad7226480ba02b332fd7425fa13201d73302a21efbe02bab681fd3807e', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (12, 1, 1, 12, 'PARAGRAPH', NULL, NULL, 12, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '四、系统间的关系', 'c17ea613e413c2feeeec63ab3cc96bd851ca4ef1a0bb07a36eb46b74c81f84ef', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (13, 1, 1, 13, 'PARAGRAPH', NULL, NULL, 13, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商和物料之间是\"提供\"的关系，反过来就是说物料\"由供应商提供\"。采购订单和采购明细之间是\"包含\"关系，反过来明细\"归属\"于那张订单。物料归属到物料分类。', '2b989de8b9a52c7c47f9f3795e209e5973e38104e989885b220121939cfe17cb', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (14, 1, 1, 14, 'PARAGRAPH', NULL, NULL, 14, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '这些关系主要用来支撑几件事：第一，查某个订单的供货源头；第二，汇总一个供应商到底供了哪些料，好给他做绩效打分；第三，统计某个物料被多少张订单用到，做需求分析。', 'e2144b985a441f8520294da628dc524541a0ee9e9a9604c30273a83ae50d3ae2', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (15, 1, 1, 15, 'PARAGRAPH', NULL, NULL, 15, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '五、录入规范与要求（制度原文）', 'dd302f206b7dbed52f4c2b819cc326a152bdf88812f7840866b4a4619b8a3122', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (16, 1, 1, 16, 'PARAGRAPH', NULL, NULL, 16, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '根据采购管理制度，录入时有几条硬性要求，业务人员必须遵守：', '272040e08d31302340862ded77b95f1dc7b96342b6ee897a0ba4bd416171cb57', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (17, 1, 1, 17, 'PARAGRAPH', NULL, NULL, 17, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '每张采购订单必须指定供应商，不能空着。', '137f5bf04814545791a784b9a7a55ff656171c4918bb4c37c2a036a96f22bfe0', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (18, 1, 1, 18, 'PARAGRAPH', NULL, NULL, 18, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '订单金额必须大于零，负数或者零都不允许。', 'c2acda8e8afe957491d93b5c7198b53eaf7a511ca97c5f8376f4fc15d54f9d5d', NULL, NULL, '2026-10-31 20:23:35', NULL, NULL, '2026-10-31 20:23:35', NULL);
INSERT INTO `om_ai_evidence` VALUES (19, 1, 1, 19, 'PARAGRAPH', NULL, NULL, 19, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '订单状态只能从上面说的那几种状态里选，不能随便填。', 'a5e68933a018dcfc680efe268080712ba2ce76b8c40701bcb637fcf24cc0fd65', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (20, 1, 1, 20, 'PARAGRAPH', NULL, NULL, 20, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商编码必须符合格式，S开头6位数字，不规范的不能保存。', '690927ee9f4db840ff578975e945f7f1cf6e1f5191be261d1f03076df70bc9b9', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (21, 1, 1, 21, 'PARAGRAPH', NULL, NULL, 21, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购明细里的订购数量必须是正整数，不能是小数。', '2a4b6b60a5dbb399c4e2abd28893bcaf7b2aaa21b038e7b214be2a94b833f715', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (22, 1, 1, 22, 'PARAGRAPH', NULL, NULL, 22, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '供应商成立日期必须在当前日期之前，新注册的不能是未来日期。', '7ec7c944c75573ae56fdeb263d9be9d4c2efb5ed42d1861409d5adb1b6890cc0', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (23, 1, 1, 23, 'PARAGRAPH', NULL, NULL, 23, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '六、历史访谈记录摘录', '4285d9353ae9510dceb23788b2f29237a975e6e3a127b5d457570188534627d3', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (24, 1, 1, 24, 'PARAGRAPH', NULL, NULL, 24, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '采购主管张工：我们现在想做一个系统，输入订单号就能直接看到是哪家供应商接的单，这是最基本的需求。另外领导还要看每家供应商给我们供了哪些货，方便年底考核。物料那边，市场部的人老问某个料这半年被多少订单用，我们现在都统计不出来，只能人工翻。', '973ad0b98b052fcd0e143dce192fed89ec4e98776e4247d4ff795111a40b2d77', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (25, 1, 1, 25, 'PARAGRAPH', NULL, NULL, 25, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '信息部王工：数据这块之前做过一些规则校验，主要是采购单不能没供应商、金额不能为负这些，都是写在旧系统里的。新系统我们希望规则能配出来，不用写死代码。', '925a920ccc641b02506268f4a75e88b7d5c619e66796ce699a40be0de8649ec9', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (26, 1, 1, 26, 'PARAGRAPH', NULL, NULL, 26, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '七、其他说明', 'a4985be13f99994c48edc939b27836cd4259e0d2c55c2d99af635e9ffe59322f', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_evidence` VALUES (27, 1, 1, 27, 'PARAGRAPH', NULL, NULL, 27, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '文档里提到的\"采购单\"\"订购单\"\"采购订单\"指的是同一个业务单据，历史文档里叫法乱。供应商这个词在流程、对象、制度各个地方都反复提到，都是同一个主体。物料分类这块先放一放，不属于近期要做的重点，暂时不用纳入建模范围，也不用跟上面那几个目标挂钩。', '2a0e46fd5dd903c9ee3db4c8e19ce0fc75057f28e2dba517f0d9da1f949dd992', NULL, NULL, '2026-10-31 20:23:36', NULL, NULL, '2026-10-31 20:23:36', NULL);

INSERT INTO `om_ai_file` VALUES (1, 1, '采购业务文档.docx', 'http://localhost:8099/profile/ai-modeling/2026/10/31/6ae5ddc5eefcfb2beff78a88.docx', 'docx', 15813, '3966d7c2d7abc98d8ff9efaae0baa777ffe0cb3da99f0d50758f802aa23e5299', 2, NULL, 27, 0, 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:23:36', NULL);

INSERT INTO `om_ai_object` VALUES (1, 1, '采购订单', '记录采购业务需求的单据，包含状态、金额及指定供应商等信息', NULL, 0, 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object` VALUES (2, 1, '供应商', '提供物料或服务的外部组织实体，具有唯一编码与分类属性', NULL, 0, 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object` VALUES (3, 1, '物料', '被采购的物资实体，具有唯一编码，用于需求统计与分类管理', NULL, 0, 0, NULL, NULL, 0.90000, 0, 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object` VALUES (4, 1, '采购明细', '订单内具体物料的采购记录，包含数量与单价，归属于特定订单', 1, 0, 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);

INSERT INTO `om_ai_object_target` VALUES (1, 1, 1, 1, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (2, 1, 1, 2, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (3, 1, 2, 1, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (4, 1, 2, 2, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (5, 1, 3, 3, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (6, 1, 4, 1, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (7, 1, 4, 2, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_object_target` VALUES (8, 1, 4, 3, NULL, NULL, '2026-10-31 20:27:13', NULL, NULL, '2026-10-31 20:27:13', NULL);

INSERT INTO `om_ai_relation` VALUES (1, 1, '指定供应商', '采购订单必须关联承接的供应商', 1, 2, 0, '承接订单', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation` VALUES (2, 1, '包含明细', '一张采购订单包含多条采购明细记录', 1, 4, 0, '归属订单', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation` VALUES (3, 1, '提供物料', '供应商向企业供应具体物料', 2, 3, 0, '由供应商提供', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation` VALUES (4, 1, '对应物料', '采购明细记录具体采购的物料种类', 4, 3, 0, '被明细记录', 0, NULL, NULL, 0.95000, 0, 1, '吴同', '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);

INSERT INTO `om_ai_relation_target` VALUES (1, 1, 1, 1, NULL, NULL, '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation_target` VALUES (2, 1, 2, 1, NULL, NULL, '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation_target` VALUES (3, 1, 3, 2, NULL, NULL, '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_relation_target` VALUES (4, 1, 4, 3, NULL, NULL, '2026-10-31 20:29:49', NULL, NULL, '2026-10-31 20:29:49', NULL);

INSERT INTO `om_ai_stage` VALUES (1, 1, 'FILE_PARSE', '文档结构化解析', 2, 1, 100, NULL, NULL, NULL, 1, 27, 0, 1793, '已解析 1 个文件，生成 27 个带原文位置的证据块', NULL, NULL, '2026-10-31 20:23:34', '2026-10-31 20:23:36', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_stage` VALUES (2, 1, 'EVIDENCE_INDEX', '证据索引构建', 2, 2, 100, NULL, NULL, NULL, 0, 0, 0, 40, '已按文件、页码、章节、段落和表格位置建立证据索引；抽取轮次只读取证据窗口和前序候选索引', NULL, NULL, '2026-10-31 20:23:36', '2026-10-31 20:23:36', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:23:36', NULL);
INSERT INTO `om_ai_stage` VALUES (3, 1, 'ROUND_1_FOUNDATION', '第1轮：目标、术语和枚举', 2, 3, 100, NULL, 'qwen3.6-plus', NULL, 27, 1, 0, 128331, '文本模型已完成第 1 个证据窗口分析。模型分析摘要：本批证据围绕企业采购业务的核心痛点与规范化需求展开。明确依据包括：证据块6与19定义了订单全生命周期状态流转及强制校验规则；证据块7与20明确了供应商分类标准及编码格式；证据块24与27确认了业务目标聚焦于订单溯源、供应商考核与物料统计，并明确排除物料分类。证据不足项为：部分术语（如采购单/订购单）存在历史叫法混用，需统一为“采购订单”；供应商成立日期等校验规则缺乏具体系统实现方案，暂不纳入本轮枚举或目标。', NULL, NULL, '2026-10-31 20:23:36', '2026-10-31 20:25:43', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:25:43', NULL);
INSERT INTO `om_ai_stage` VALUES (4, 1, 'ROUND_2_OBJECT_ATTR', '第2轮：对象与属性抽取', 2, 4, 100, NULL, 'qwen3.6-plus', NULL, 27, 1, 0, 142122, '完成对象、属性候选抽取，识别采购订单、供应商、物料、采购明细4个对象以及对应7个属性', NULL, NULL, '2026-10-31 20:25:43', '2026-10-31 20:27:13', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:27:13', NULL);
INSERT INTO `om_ai_stage` VALUES (5, 1, 'ROUND_3_REL_AXIOM', '第3轮：关系与公理抽取', 2, 5, 100, NULL, 'qwen3.6-plus', NULL, 27, 1, 0, 96445, '完成关系、公理候选抽取，识别4条对象关系，2条业务约束公理', NULL, NULL, '2026-10-31 20:27:13', '2026-10-31 20:29:49', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_stage` VALUES (6, 1, 'ROUND_4_RULE', '第4轮：业务规则抽取', 2, 6, 100, NULL, 'qwen3.6-plus', NULL, 27, 1, 0, 84217, '完成业务校验规则候选抽取，生成4条数据校验规则', NULL, NULL, '2026-10-31 20:29:49', '2026-10-31 20:32:33', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:32:33', NULL);
INSERT INTO `om_ai_stage` VALUES (7, 1, 'FINISH', '任务完成', 2, 7, 100, NULL, NULL, NULL, 0, 0, 0, 0, '全部候选抽取流程执行完毕，等待人工采纳审核', NULL, NULL, '2026-10-31 20:32:33', '2026-10-31 20:32:33', 1, '吴同', '2026-10-31 20:23:34', NULL, '吴同', '2026-10-31 20:32:33', NULL);


INSERT INTO `om_ai_target` VALUES (1, 1, 'target:订单溯源追踪', '订单溯源追踪', '我们采购部负责全公司的生产物资与办公物资采购。目前最头疼的问题是，每张采购单最后到底由哪个供应商承接的，经常要翻纸质单据和多个系统才能查出来，出了质量问题找不到责任源头。领导要求把这块理顺。；这些关系主要用来支撑几件事：第一，查某个订单的供货源头；第二，汇总一个供应商到底供了哪些料，好给他做绩效打分；第三，统计某个物料被多少张订单用到，做需求分析。；采购主管张工：我们现在想做一个系统，输入订单号就能直接看到是哪家供应商接的单，这是最基本的需求。另外领导还要看每家供应商给我们供了哪些货，方便年底考核。物料那', '采购订单执行与质量追溯', 2, '', '0', 5, 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_target` VALUES (2, 1, 'target:供应商绩效汇总', '供应商绩效汇总', '这些关系主要用来支撑几件事：第一，查某个订单的供货源头；第二，汇总一个供应商到底供了哪些料，好给他做绩效打分；第三，统计某个物料被多少张订单用到，做需求分析。；采购主管张工：我们现在想做一个系统，输入订单号就能直接看到是哪家供应商接的单，这是最基本的需求。另外领导还要看每家供应商给我们供了哪些货，方便年底考核。物料那边，市场部的人老问某个料这半年被多少订单用，我们现在都统计不出来，只能人工翻。', '供应商年度考核与评估', 2, '', '0', 4, 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:29:49', NULL);
INSERT INTO `om_ai_target` VALUES (3, 1, 'target:物料需求统计', '物料需求统计', '这些关系主要用来支撑几件事：第一，查某个订单的供货源头；第二，汇总一个供应商到底供了哪些料，好给他做绩效打分；第三，统计某个物料被多少张订单用到，做需求分析。；采购主管张工：我们现在想做一个系统，输入订单号就能直接看到是哪家供应商接的单，这是最基本的需求。另外领导还要看每家供应商给我们供了哪些货，方便年底考核。物料那边，市场部的人老问某个料这半年被多少订单用，我们现在都统计不出来，只能人工翻。', '物料使用频次与需求预测', 2, '', '0', 3, 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:29:49', NULL);

INSERT INTO `om_ai_task` VALUES (1, '采购业务抽取', '解析采购申请单、采购订单、入库单、采购发票、供应商对账单据，抽取供应商、物料、采购数量、单价、金额、交付周期、结算方式等实体，构建采购领域知识图谱本体关系', 1001, 1, 2, 'FINISH', 37, '2026-10-31 20:23:34', '2026-10-31 20:32:33', NULL, b'0', 1, '吴同', '2026-10-31 20:23:33', NULL, '吴同', '2026-10-31 22:50:36', NULL);
INSERT INTO `om_ai_task` VALUES (2, '销售订单业务抽取', '针对销售订单、发货单、回款单据做本体抽取，识别客户、商品、订单、回款关系，构建销售业务知识图谱', 1001, 1, 2, 'FINISH', 42, '2026-10-31 19:23:34', '2026-10-31 19:32:33', NULL, b'0', 1, '吴同', '2026-10-31 19:23:33', NULL, '吴同', '2026-10-31 19:32:33', NULL);
INSERT INTO `om_ai_task` VALUES (3, '库存出入库业务抽取', '解析入库单、出库单、盘点单数据，提取物料、仓库、批次、数量变动信息，建立库存流转本体模型', 1001, 1, 2, 'FINISH', 31, '2026-10-31 18:23:34', '2026-10-31 18:32:33', NULL, b'0', 1, '吴同', '2026-10-31 18:23:33', NULL, '吴同', '2026-10-31 18:32:33', NULL);
INSERT INTO `om_ai_task` VALUES (4, '生产工单业务抽取', '读取生产工单、工序流转卡、报工记录，抽取产品、工序、班组、设备、产量等生产核心实体与关联关系', 1001, 1, 2, 'PARSE', 55, '2026-10-31 17:23:34', NULL, NULL, b'0', 1, '吴同', '2026-10-31 17:23:33', NULL, '吴同', '2026-10-31 22:49:27', NULL);
INSERT INTO `om_ai_task` VALUES (5, '财务应收单据抽取', '对接应收台账、发票、收款核销记录，识别客户应收、账期、核销流水，构建财务应收领域本体', 1001, 1, 0, 'WAIT', 0, NULL, NULL, NULL, b'0', 1, '吴同', '2026-10-31 16:23:33', NULL, '吴同', '2026-10-31 22:51:08', NULL);
INSERT INTO `om_ai_task` VALUES (6, '财务应付单据抽取', '解析采购发票、付款申请、付款回单，抽取供应商、应付账款、付款计划、核销关系数据', 1001, 1, 3, 'FAIL', 0, '2026-10-31 15:23:34', '2026-10-31 15:28:11', '源文件格式异常，部分Excel单元格存在合并单元格，字段无法正常拆分识别', b'0', 1, '吴同', '2026-10-31 15:23:33', NULL, '吴同', '2026-10-31 22:51:10', NULL);
INSERT INTO `om_ai_task` VALUES (7, '人力资源人事抽取', '解析员工档案、劳动合同、考勤、绩效数据，抽取人员、岗位、部门、考勤绩效相关实体关系', 1001, 1, 2, 'FINISH', 47, '2026-10-31 14:23:34', '2026-10-31 14:32:33', NULL, b'0', 1, '吴同', '2026-10-31 14:23:33', NULL, '吴同', '2026-10-31 14:32:33', NULL);
INSERT INTO `om_ai_task` VALUES (8, '设备运维工单抽取', '解析设备台账、维修工单、保养计划、故障记录，提取设备、故障类型、维保人员、备件更换信息', 1001, 1, 3, 'FAIL', 0, '2026-10-31 13:23:34', '2026-10-31 13:29:05', '模型调用超时，大模型接口响应长时间无返回，任务执行中断', b'0', 1, '吴同', '2026-10-31 13:23:33', NULL, '吴同', '2026-10-31 22:51:12', NULL);
INSERT INTO `om_ai_task` VALUES (9, '质量检验单据抽取', '解析来料检验、过程检验、成品检验报告，抽取检验项、检验结果、不良原因、检验批次信息', 1001, 1, 2, 'PARSE', 39, '2026-10-31 12:23:34', NULL, NULL, b'0', 1, '吴同', '2026-10-31 12:23:33', NULL, '吴同', '2026-10-31 22:49:24', NULL);
INSERT INTO `om_ai_task` VALUES (10, '仓储调拨单抽取', '解析仓库调拨、移库单、出库调拨单据，识别调出仓库、调入仓库、物料、调拨数量、调拨原因', 1001, 1, 0, 'WAIT', 0, NULL, NULL, NULL, b'0', 1, '吴同', '2026-10-31 11:23:33', NULL, '吴同', '2026-10-31 22:51:13', NULL);
INSERT INTO `om_ai_task` VALUES (11, '项目合同信息抽取', '解析项目合同、变更单、结算单，抽取项目、甲方乙方、合同金额、交付节点、结算进度信息', 1001, 1, 3, 'FAIL', 0, '2026-10-31 10:23:34', '2026-10-31 10:27:42', '文档内缺少预设本体中必须的合同编号字段，校验不通过直接终止解析', b'0', 1, '吴同', '2026-10-31 10:23:33', NULL, '吴同', '2026-10-31 22:51:16', NULL);

INSERT INTO `om_ai_term` VALUES (1, 1, '供应商', '', '物料供应商、服务供应商', '', '现在公司没有统一的采购口径，不同部门对\"供应商\"的理解不一样：有的把给物料的地方叫供应商，有的把提供服务的也叫供应商。订单在系统里的叫法也不统一，有人叫采购单，有人叫订购单，其实都是一个东西。；供应商有分类。一种是给我们供货的物料供应商，一种是提供安装、维护这类服务的服务供应商，两类要分开管理，考核指标也不一样。；公司层面的组织是一个大的概念，集团、各分厂、各事业部都算组织，供应商从本质上也属于组织这一类，只是它是外部组织，给我们供货。每个供应商我们给编一个固定的编码，规则是大写S开头后面跟6位数字，比如', '为采购方提供物资或安装维护等服务的商业实体，需分配唯一编码', 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_term` VALUES (2, 1, '采购订单', '', '采购单、订购单', '', '现在公司没有统一的采购口径，不同部门对\"供应商\"的理解不一样：有的把给物料的地方叫供应商，有的把提供服务的也叫供应商。订单在系统里的叫法也不统一，有人叫采购单，有人叫订购单，其实都是一个东西。；一个典型流程是这样的：业务部门提出采购需求 → 采购员登记成一张订单 → 订单里明确要买哪些东西、各买多少、单价多少 → 报给部门负责人审批 → 审批通过后生成正式订单 → 安排供应商供货。订单整个过程中有几种状态，一开始是草稿，登记完提交上去是已提交，领导批了就是已审批，如果中途不买了或者错了，就作废，叫已取消。', '记录采购物资、数量、单价及审批状态的正式业务凭证', 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_term` VALUES (3, 1, '物料', '', '', '', '物料是我们采购的物资，每个物料也有个编码。一张采购订单会包含多个采购明细，明细就是\"这张单子里某一种物料买了多少、单价多少\"这样的记录。；供应商和物料之间是\"提供\"的关系，反过来就是说物料\"由供应商提供\"。采购订单和采购明细之间是\"包含\"关系，反过来明细\"归属\"于那张订单。物料归属到物料分类。', '具有唯一编码的采购对象，可被归类并记录于采购明细中', 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_term` VALUES (4, 1, '采购明细', '', '', '', '物料是我们采购的物资，每个物料也有个编码。一张采购订单会包含多个采购明细，明细就是\"这张单子里某一种物料买了多少、单价多少\"这样的记录。；采购明细里的订购数量必须是正整数，不能是小数。', '采购订单的组成部分，用于记录单一物料的具体采购数量与单价', 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);
INSERT INTO `om_ai_term` VALUES (5, 1, '组织', '', '', '', '公司层面的组织是一个大的概念，集团、各分厂、各事业部都算组织，供应商从本质上也属于组织这一类，只是它是外部组织，给我们供货。每个供应商我们给编一个固定的编码，规则是大写S开头后面跟6位数字，比如S123456，这个编码在系统里要唯一，不能重复。', '企业架构与外部合作方的广义分类，包含内部机构与外部供应商', 0, NULL, NULL, 0.90000, b'0', 1, '吴同', '2026-10-31 20:25:44', NULL, NULL, '2026-10-31 20:25:44', NULL);


