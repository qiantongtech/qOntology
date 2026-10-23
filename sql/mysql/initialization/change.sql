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
