UPDATE `qontology_dev`.`system_menu` SET `menu_name` = '文件管理', `parent_id` = 1080, `order_num` = 4, `path` = 'file', `component` = NULL, `query` = NULL, `route_name` = '', `is_frame` = 1, `is_cache` = 0, `menu_type` = 'M', `visible` = '0', `status` = '0', `perms` = '', `icon` = '文件管理', `create_by` = '吴同', `create_time` = '2026-11-02 14:18:12', `update_by` = '吴同', `update_time` = '2026-11-03 15:58:22', `remark` = '文件分类与文件管理统一入口' WHERE `menu_id` = 1195;

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
