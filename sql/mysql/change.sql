UPDATE `system_menu` SET `menu_name` = '文件管理', `parent_id` = 1080, `order_num` = 4, `path` = 'file', `component` = NULL, `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'M', `visible` = '0', `status` = '0', `perms` = '', `icon` = '文件管理', `create_by` = '吴同', `create_time` = '2026-11-02 14:18:12', `update_by` = '吴同', `update_time` = '2026-11-03 15:58:22', `remark` = '文件分类与文件管理统一入口' WHERE `menu_id` = 1195;

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

-- ============================================================================
-- 2026-11-04 字段变更
-- ============================================================================

-- 1. 术语状态：0 禁用，1 启用；历史数据默认启用。
ALTER TABLE `om_term`
    ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `description`;

-- 2. 业务数据类型：与基础管理使用相同的字段名、类型和顺序。
ALTER TABLE `om_data_type`
    ADD COLUMN `max_length`    INT         NULL COMMENT '最大长度'       AFTER `enum_type`,
    ADD COLUMN `min_length`    INT         NULL COMMENT '最小长度'       AFTER `max_length`,
    ADD COLUMN `earliest_time` DATETIME    NULL COMMENT '最早日期'       AFTER `min_length`,
    ADD COLUMN `latest_time`   DATETIME    NULL COMMENT '最晚日期'       AFTER `earliest_time`,
    ADD COLUMN `true_value`    VARCHAR(64) NULL COMMENT 'true 对应文案'  AFTER `latest_time`,
    ADD COLUMN `false_value`   VARCHAR(64) NULL COMMENT 'false 对应文案' AFTER `true_value`,
    ADD COLUMN `all_places`    INT         NULL COMMENT '总位数'        AFTER `false_value`;

-- 3. 新交互所需的其他配置字段。
ALTER TABLE `om_data_type`
    ADD COLUMN `validation_type`    VARCHAR(16)     NULL COMMENT '文本格式校验编码'   AFTER `all_places`,
    ADD COLUMN `validation_pattern` VARCHAR(512)    NULL COMMENT '自定义正则表达式'   AFTER `validation_type`,
    ADD COLUMN `example_value`      VARCHAR(256)    NULL COMMENT '示例值'        AFTER `validation_pattern`,
    ADD COLUMN `min_value`          DECIMAL(65, 20) NULL COMMENT '数值最小值'      AFTER `example_value`,
    ADD COLUMN `max_value`          DECIMAL(65, 20) NULL COMMENT '数值最大值'      AFTER `min_value`;

-- ============================================================================
-- 2026-11-04 数据更新（在上述字段变更完成后执行）
-- ============================================================================

-- 2026-11-04 业务数据类型存量配置补齐（先执行上方 om_data_type 加字段语句）。
-- 具体取值约束仅用于版本1的供应链示例；按编号、名称、类型和示例标记共同匹配。
-- 已填写的配置、枚举来源及枚举编码均保留。
START TRANSACTION;

-- 文本示例：补齐长度和示例，不对普通历史文本强加业务约束。
UPDATE `om_data_type`
SET `validation_type` = COALESCE(`validation_type`, 'NONE'),
    `min_length` = 1,
    `max_length` = 512,
    `example_value` = COALESCE(`example_value`, '江苏省南京市示例路1号')
WHERE `id` = 3
  AND `version_id` = 1
  AND `name` = '联系地址'
  AND `data_type` = 0
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND `min_length` IS NULL
  AND `max_length` IS NULL;

-- 联系电话含区号、前导零、连接符，不直接套用仅适用于手机的 PHONE 规则。
UPDATE `om_data_type`
SET `validation_type` = COALESCE(`validation_type`, 'CUSTOM'),
    `validation_pattern` = COALESCE(`validation_pattern`, '^[0-9+() -]{3,32}$'),
    `min_length` = 3,
    `max_length` = 32,
    `example_value` = COALESCE(`example_value`, '025-12345678')
WHERE `id` = 5
  AND `version_id` = 1
  AND `name` = '联系电话'
  AND `data_type` = 0
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND `min_length` IS NULL
  AND `max_length` IS NULL;

UPDATE `om_data_type`
SET `validation_type` = COALESCE(`validation_type`, 'NONE'),
    `min_length` = 1,
    `max_length` = 128,
    `example_value` = COALESCE(`example_value`, CASE `id`
        WHEN 17 THEN '示例供应商'
        ELSE 'PO_20260001'
    END)
WHERE `version_id` = 1
  AND `data_type` = 0
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND (
        (`id` = 17 AND `name` = '业务名称')
        OR (`id` = 24 AND `name` = '业务编码')
  )
  AND `min_length` IS NULL
  AND `max_length` IS NULL;

-- 税率按百分数记录，金额、重量、数量和天数均采用非负示例边界。
UPDATE `om_data_type`
SET `all_places` = COALESCE(`all_places`, GREATEST(5, COALESCE(`decimal_places`, 2))),
    `min_value` = COALESCE(`min_value`, 0),
    `max_value` = COALESCE(`max_value`, 100)
WHERE `id` = 7
  AND `version_id` = 1
  AND `name` = '税率'
  AND `data_type` = 2
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND `min_value` IS NULL
  AND `max_value` IS NULL;

UPDATE `om_data_type`
SET `all_places` = COALESCE(`all_places`, GREATEST(18, COALESCE(`decimal_places`, 2))),
    `min_value` = 0
WHERE `version_id` = 1
  AND `data_type` = 2
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND (
        (`id` = 15 AND `name` = '物料重量')
        OR (`id` = 19 AND `name` = '采购单价')
        OR (`id` = 25 AND `name` = '订单金额')
  )
  AND `min_value` IS NULL
  AND (`max_value` IS NULL OR `max_value` >= 0);

UPDATE `om_data_type`
SET `min_value` = 0
WHERE `version_id` = 1
  AND `data_type` = 1
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0
  AND (
        (`id` = 13 AND `name` = '交货周期')
        OR (`id` = 21 AND `name` = '件数')
  )
  AND `min_value` IS NULL
  AND (`max_value` IS NULL OR `max_value` >= 0);

UPDATE `om_data_type`
SET `true_value` = COALESCE(`true_value`, '合格'),
    `false_value` = COALESCE(`false_value`, '不合格')
WHERE `id` = 9
  AND `version_id` = 1
  AND `name` = '是否验收合格'
  AND `data_type` = 3
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0;

UPDATE `om_data_type`
SET `true_value` = COALESCE(`true_value`, '启用'),
    `false_value` = COALESCE(`false_value`, '禁用')
WHERE `id` = 20
  AND `version_id` = 1
  AND `name` = '是否启用'
  AND `data_type` = 3
  AND `remark` = '供应链示例数据'
  AND `del_flag` = 0;

-- 其他历史记录仅填补新表单必需的缺省配置，不新增数值上下限或覆盖用户配置。
UPDATE `om_data_type`
SET `validation_type` = 'NONE'
WHERE `data_type` = 0
  AND `validation_type` IS NULL
  AND `del_flag` = 0;

UPDATE `om_data_type`
SET `all_places` = COALESCE(`all_places`, GREATEST(10, COALESCE(`decimal_places`, 2))),
    `decimal_places` = COALESCE(`decimal_places`, 2)
WHERE `data_type` = 2
  AND `del_flag` = 0;

UPDATE `om_data_type`
SET `true_value` = COALESCE(`true_value`, '是'),
    `false_value` = COALESCE(`false_value`, '否')
WHERE `data_type` = 3
  AND `del_flag` = 0;

-- 日期格式切换为新选择框编码；业务时间保留时分秒，不降为只有日期。
UPDATE `om_data_type`
SET `format` = CASE `format`
    WHEN 'yyyy-MM-dd' THEN 'YYYY-MM-DD'
    WHEN 'yyyy/MM/dd' THEN 'YYYY/MM/DD'
    WHEN 'yyyy年MM月dd日' THEN 'YYYY年MM月DD日'
    WHEN 'yyyy-MM-dd HH:mm:ss' THEN 'YYYY-MM-DD HH:mm:ss'
    ELSE COALESCE(NULLIF(`format`, ''), 'YYYY-MM-DD')
END
WHERE `data_type` = 4
  AND `del_flag` = 0;

COMMIT;

-- ============================================================================
-- 2026-11-04 项目枚举仿真数据备注补齐
-- ============================================================================

-- 仅补齐工作区 1001、版本 1 的原有示例枚举，不覆盖已填写的备注。
UPDATE `om_enum_type`
SET `remark` = CASE `enum_type`
    WHEN 'outbound_purpose' THEN '区分生产领用、销售发货等出库用途，用于追踪物料出库去向。'
    WHEN 'component_category' THEN '统一零部件分类口径，便于采购、库存及产品结构管理。'
    WHEN 'raw_material_category' THEN '统一原材料分类口径，便于采购计划、库存统计及生产领料。'
    WHEN 'settlement_method' THEN '定义采购业务的结算方式，用于记录订单付款及对账约定。'
    WHEN 'purchase_category' THEN '区分采购业务类别，便于采购需求归类及订单统计。'
    WHEN 'document_status' THEN '统一采购、入库及出库等业务单据的状态取值。'
    WHEN 'transport_mode' THEN '定义物流运输方式，用于记录运输安排及交付信息。'
    WHEN 'material_unit' THEN '统一物料计量单位，用于采购、收货及库存数量核算。'
    WHEN 'material_category' THEN '统一物料分类口径，便于物料档案、采购及库存管理。'
    WHEN 'supplier_category' THEN '定义供应商业务类别，便于供应商分类维护及采购协同。'
    WHEN 'organization_category' THEN '区分集团、公司、工厂及部门等组织类别，明确业务主体层级。'
    ELSE `remark`
END
WHERE `workspace_id` = 1001
  AND `version_id` = 1
  AND `del_flag` = 0
  AND (`remark` IS NULL OR TRIM(`remark`) = '')
  AND (
        (`enum_id` = 1 AND `enum_type` = 'outbound_purpose')
        OR (`enum_id` = 2 AND `enum_type` = 'component_category')
        OR (`enum_id` = 3 AND `enum_type` = 'raw_material_category')
        OR (`enum_id` = 4 AND `enum_type` = 'settlement_method')
        OR (`enum_id` = 5 AND `enum_type` = 'purchase_category')
        OR (`enum_id` = 6 AND `enum_type` = 'document_status')
        OR (`enum_id` = 7 AND `enum_type` = 'transport_mode')
        OR (`enum_id` = 8 AND `enum_type` = 'material_unit')
        OR (`enum_id` = 9 AND `enum_type` = 'material_category')
        OR (`enum_id` = 10 AND `enum_type` = 'supplier_category')
        OR (`enum_id` = 11 AND `enum_type` = 'organization_category')
  );

-- ============================================================================
-- 建模目标验证任务：新增任务名称
-- ============================================================================

-- 添加时间：2026-11-04 19:34:52（北京时间）
ALTER TABLE `om_target_task`
    ADD COLUMN `task_name` VARCHAR(256) NULL COMMENT '任务名称' AFTER `target_id`;

-- 添加时间：2026-11-04 19:34:52（北京时间）
-- 补齐历史任务名称，保留已有名称和验证结论。
UPDATE `om_target_task`
SET `task_name` = CONCAT('建模目标验证任务-', `id`)
WHERE `task_name` IS NULL
   OR TRIM(`task_name`) = '';

--版本管理添加停用启用状态。
ALTER TABLE `basic_project_version`
    ADD COLUMN `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态;0：禁用，1：启用' AFTER `base_version`;

ALTER TABLE `om_data_type`
    ADD COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `enum_source`;


UPDATE `system_dict_data` SET `dict_sort` = 0, `dict_label` = '全量更新', `dict_value` = '0', `dict_type` = 'app_update_type', `css_class` = NULL, `list_class` = 'primary', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 20:06:17', `update_by` = '吴同', `update_time` = '2026-11-06 12:47:58', `remark` = '' WHERE `dict_code` = 101;
UPDATE `system_dict_data` SET `dict_sort` = 1, `dict_label` = '增量更新', `dict_value` = '1', `dict_type` = 'app_update_type', `css_class` = NULL, `list_class` = 'info', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-22 20:06:17', `update_by` = '吴同', `update_time` = '2026-11-06 12:48:02', `remark` = '' WHERE `dict_code` = 102;

UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '采购清单数据抽取任务', `status` = 2, `publish_status` = 1, `publish_time` = '2026-10-23 11:14:59', `publisher_id` = 2, `publish_by` = '吴同', `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:15:36', `execute_duration` = 37, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从采购、仓储相关单据和文档里，抽取采购 - 仓储协同相关的实体、属性和关系，产出符合本体模型的数据，支撑采购仓储知识图谱搭建。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:31', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从采购、仓储相关单据和文档里，抽取采购 - 仓储协同相关的实体、属性和关系，产出符合本体模型的数据，支撑采购仓储知识图谱搭建。' WHERE `id` = 1;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '供应商基础信息抽取任务', `status` = 3, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:15:43', `execute_duration` = 44, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从供应商档案、资质文档中抽取供应商基础信息、资质信息、联系人信息，标准化供应商实体数据，完善供应链供应商知识图谱。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:32', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从供应商档案、资质文档中抽取供应商基础信息、资质信息、联系人信息，标准化供应商实体数据，完善供应链供应商知识图谱。' WHERE `id` = 2;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '采购订单数据抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:15:50', `execute_duration` = 51, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从采购订单单据中抽取订单、物料、供应商、数量、金额、交期信息，构建采购订单实体及关联关系，支撑采购业务链路图谱构建。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:33', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从采购订单单据中抽取订单、物料、供应商、数量、金额、交期信息，构建采购订单实体及关联关系，支撑采购业务链路图谱构建。' WHERE `id` = 3;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '入库单数据抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:15:57', `execute_duration` = 58, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从入库单据中抽取入库单号、物料、批次、入库数量、库位、入库时间信息，建立入库记录与采购订单、库存之间关联关系。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:34', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从入库单据中抽取入库单号、物料、批次、入库数量、库位、入库时间信息，建立入库记录与采购订单、库存之间关联关系。' WHERE `id` = 4;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '出库单数据抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:04', `execute_duration` = 65, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从出库单据提取出库单号、物料、出库数量、领用部门、出库时间，构建出库实体，关联库存台账，还原物料出库流转链路。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:35', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从出库单据提取出库单号、物料、出库数量、领用部门、出库时间，构建出库实体，关联库存台账，还原物料出库流转链路。' WHERE `id` = 5;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '库存台账数据抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:11', `execute_duration` = 72, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从库存台账抽取物料库存、库位、可用数量、冻结数量、盘点信息，建立物料库存实体，支撑仓储库存态势知识查询。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:36', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从库存台账抽取物料库存、库位、可用数量、冻结数量、盘点信息，建立物料库存实体，支撑仓储库存态势知识查询。' WHERE `id` = 6;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '物料基础信息抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:18', `execute_duration` = 79, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从物料档案抽取物料编码、物料名称、规格型号、物料分类、单位、安全库存等基础属性，构建物料标准本体数据。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:37', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从物料档案抽取物料编码、物料名称、规格型号、物料分类、单位、安全库存等基础属性，构建物料标准本体数据。' WHERE `id` = 7;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '采购合同信息抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:25', `execute_duration` = 86, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从采购合同文档提取合同编号、甲乙双方、合同金额、履约周期、付款条款，构建采购合同实体与关联关系。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:38', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从采购合同文档提取合同编号、甲乙双方、合同金额、履约周期、付款条款，构建采购合同实体与关联关系。' WHERE `id` = 8;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '质检单据抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:32', `execute_duration` = 93, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从来料质检单抽取质检单号、物料、检验结果、不合格原因、检验人，建立质检实体，关联入库、采购订单数据。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:39', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从来料质检单抽取质检单号、物料、检验结果、不合格原因、检验人，建立质检实体，关联入库、采购订单数据。' WHERE `id` = 9;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '库位信息抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:39', `execute_duration` = 100, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从仓库库位档案抽取仓库、库区、库位编码、库位类型、存储限制信息，构建仓储空间实体，支撑仓储位置关系图谱。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:40', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从仓库库位档案抽取仓库、库区、库位编码、库位类型、存储限制信息，构建仓储空间实体，支撑仓储位置关系图谱。' WHERE `id` = 10;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '供应商履约评价抽取任务', `status` = 2, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `publish_by` = NULL, `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 11:16:46', `execute_duration` = 107, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从供应商履约评价记录中抽取交付准时率、质量合格率、异常扣分、评价等级等信息，构建供应商评价实体，支撑供应商信用知识图谱。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:02:41', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 11:14:59', `remark` = '从供应商履约评价记录中抽取交付准时率、质量合格率、异常扣分、评价等级等信息，构建供应商评价实体，支撑供应商信用知识图谱。' WHERE `id` = 11;
UPDATE `app_struct_task` SET `workspace_id` = 1001, `project_id` = 1, `project_name` = '供应链采购与仓储协同本体', `project_version_id` = 1, `project_version_name` = 'v1.0.0', `name` = '供应链采购人员数据抽取', `status` = 2, `publish_status` = 1, `publish_time` = '2026-11-06 13:36:48', `publisher_id` = 2, `publish_by` = '吴同', `update_type` = 0, `update_frequency` = '0 0 0 * * ?', `update_status` = 1, `datasource_id` = 1, `last_execute_time` = '2026-10-23 16:47:45', `execute_duration` = 100, `scheduling_status` = NULL, `datasource_name` = '测试数据', `description` = '从表抽取采购人员内容并结构化输出，支撑采购人员信息核查与管理分析。', `responsible_user_id` = 2, `responsible_person_name` = '吴同', `valid_flag` = 0, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 16:26:46', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-10-23 16:27:10', `remark` = NULL WHERE `id` = 12;

UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '原材料采购合同数据抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:09', `execute_duration` = 25, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从原材料采购合同中抽取合同、物料、供应商、金额、交付条款等信息，构建合同实体与关联关系，支撑供应链知识图谱构建。' WHERE `id` = 1;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '供应商资质文档抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:14', `execute_duration` = 30, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从供应商资质文档抽取资质证书、有效期、经营范围、认证信息，构建供应商资质实体，完善供应商知识图谱。' WHERE `id` = 2;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '来料检验报告抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:19', `execute_duration` = 35, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从来料检验报告抽取物料信息、检验项、检验结果、不合格说明，建立质检实体，关联采购单据。' WHERE `id` = 3;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '仓储作业规程抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 3, `last_execute_time` = NULL, `execute_duration` = NULL, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从仓储作业规程文档提取入库、出库、盘点作业规则、操作要求，构建仓储流程知识实体。' WHERE `id` = 4;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '供应商履约评价文档抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:29', `execute_duration` = 45, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从供应商履约评价文档抽取交付、质量、服务评分及评价说明，生成供应商评价知识。' WHERE `id` = 5;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '物料技术规格书抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:34', `execute_duration` = 50, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从物料技术规格书提取物料参数、性能指标、技术标准，构建物料技术属性知识。' WHERE `id` = 6;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '采购变更单文档抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:39', `execute_duration` = 55, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从采购变更单提取变更原因、变更内容、调整后的数量与交期，记录采购变更关系。' WHERE `id` = 7;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '仓库应急预案抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:44', `execute_duration` = 60, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从仓库应急预案文档提取风险类型、处置流程、责任人，构建仓储应急知识图谱。' WHERE `id` = 8;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '采购谈判纪要抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:49', `execute_duration` = 65, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从采购谈判纪要提取谈判对象、价格、交付约定、附加条款，沉淀采购谈判知识。' WHERE `id` = 9;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '库存盘点报告抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:54', `execute_duration` = 70, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从库存盘点报告抽取盘点物料、账实差异、差异原因及处理意见，构建盘点相关知识。' WHERE `id` = 10;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '运输交付单文档抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adad6b7d9e0dd27052e7269.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:59', `execute_duration` = 75, `publish_status` = 0, `publish_time` = NULL, `publisher_id` = NULL, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = NULL, `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 11:38:51', `update_by` = '吴同', `updater_id` = 2, `update_time` = '2026-11-06 13:33:44', `remark` = '从运输交付单提取承运方、物料、运输时间、签收信息，搭建物流交付链路知识。' WHERE `id` = 11;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '供应链采购与仓储协同抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adaee20d9e0784f1cbeb76a.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:51', `execute_duration` = 70, `publish_status` = 1, `publish_time` = '2026-10-23 13:22:42', `publisher_id` = 2, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = '吴同', `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 13:19:25', `update_by` = '吴同', `updater_id` = NULL, `update_time` = '2026-11-06 16:27:39', `remark` = '从采购、仓储相关文本中抽取协同节点、物料、单据、动作、异常等关键信息并结构化输出，支撑流程校验与风险识别。' WHERE `id` = 12;
UPDATE `app_unstruct_task` SET `workspace_id` = 1001, `name` = '供应链采购与仓储协同业务实例描述文档抽取', `ttl_file_name` = 'ces.ttl', `ttl_file_path` = '/2026/10/23/6adb039be4b07a35261640c9.ttl', `status` = 2, `last_execute_time` = '2026-11-06 13:34:54', `execute_duration` = 75, `publish_status` = 1, `publish_time` = '2026-10-23 14:56:52', `publisher_id` = 2, `responsible_user_id` = 2, `responsible_person_name` = '吴同', `publish_by` = '吴同', `valid_flag` = 1, `del_flag` = 0, `create_by` = '吴同', `creator_id` = 2, `create_time` = '2026-10-23 14:50:07', `update_by` = '吴同', `updater_id` = NULL, `update_time` = '2026-11-06 13:35:06', `remark` = '该文档为供应链采购仓储业务自然语言样例，依托 ces.txt 本体抽取，包含多家企业、人员、多笔订单、物料、出入库、物流、质检场景，可从中抽取实体、属性、关系三元组，附带本体抽取映射说明。' WHERE `id` = 13;

INSERT INTO `app_task_log` VALUES (2, 1001, 1, 0, '采购清单数据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:15:36', 1, 0, '吴同', 2, '2026-11-06 15:50:12', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (3, 1001, 2, 0, '供应商基础信息抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:15:43', 1, 0, '吴同', 2, '2026-11-06 15:50:10', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (4, 1001, 3, 0, '采购订单数据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:15:50', 1, 0, '吴同', 2, '2026-11-06 15:50:08', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (5, 1001, 4, 0, '入库单数据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:15:57', 1, 0, '吴同', 2, '2026-11-06 15:50:06', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (6, 1001, 5, 0, '出库单数据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:04', 1, 0, '吴同', 2, '2026-11-06 15:50:03', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (7, 1001, 6, 0, '库存台账数据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:11', 1, 0, '吴同', 2, '2026-11-06 15:50:01', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (8, 1001, 7, 0, '物料基础信息抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:18', 1, 0, '吴同', 2, '2026-11-06 15:49:58', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (9, 1001, 8, 0, '采购合同信息抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:25', 1, 0, '吴同', 2, '2026-11-06 15:49:56', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (10, 1001, 9, 0, '质检单据抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:32', 1, 0, '吴同', 2, '2026-11-06 15:49:53', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (11, 1001, 10, 0, '库位信息抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:39', 1, 0, '吴同', 2, '2026-11-06 15:49:50', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (12, 1001, 11, 0, '供应商履约评价抽取任务', 1, NULL, '2026-10-23 11:14:59', '2026-10-23 11:16:46', 1, 0, '吴同', 2, '2026-11-06 15:49:47', NULL, NULL, '2026-11-06 15:50:18', NULL);
INSERT INTO `app_task_log` VALUES (17, 1001, 1, 1, '原材料采购合同数据抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:09', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (18, 1001, 2, 1, '供应商资质文档抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:14', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (19, 1001, 3, 1, '来料检验报告抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:19', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (20, 1001, 5, 1, '供应商履约评价文档抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:29', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (21, 1001, 6, 1, '物料技术规格书抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:34', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (22, 1001, 7, 1, '采购变更单文档抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:39', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (23, 1001, 8, 1, '仓库应急预案抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:44', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (24, 1001, 9, 1, '采购谈判纪要抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:49', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (25, 1001, 10, 1, '库存盘点报告抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:54', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (26, 1001, 11, 1, '运输交付单文档抽取', 1, NULL, '2026-11-06 13:33:44', '2026-11-06 13:34:59', 1, 0, '吴同', 2, '2026-11-06 13:33:44', NULL, NULL, '2026-11-06 13:33:44', NULL);
INSERT INTO `app_task_log` VALUES (32, 1001, 13, 1, '供应链采购与仓储协同业务实例描述文档抽取', 1, NULL, '2026-11-06 13:14:25', '2026-11-06 13:14:28', 1, 0, '吴同', 2, '2026-11-06 16:14:22', NULL, NULL, '2026-11-06 16:15:16', NULL);
