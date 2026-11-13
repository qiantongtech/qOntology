
-- AI 辅助建模候选枚举描述及关系反向名称数据修复
ALTER TABLE `om_ai_enum_type` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_type`;
ALTER TABLE `om_ai_enum_data` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_value`;
UPDATE `om_ai_relation` SET `reverse_name` = NULL WHERE `reversible_flag` IS NULL OR `reversible_flag` <> 1;

-- 菜单图标中文文件名重命名为 kebab-case 英文名（对应 qontology-ui/src/assets/icons/svg/ 下 SVG 文件改名）
UPDATE `system_menu` SET `icon` = 'system-center' WHERE `icon` = '系统中心';
UPDATE `system_menu` SET `icon` = 'model-market' WHERE `icon` = '模型市场';
UPDATE `system_menu` SET `icon` = 'ontology-modeling' WHERE `icon` = '本体建模';
UPDATE `system_menu` SET `icon` = 'basic-management' WHERE `icon` = '基础管理';
UPDATE `system_menu` SET `icon` = 'project-management' WHERE `icon` = '项目管理';
UPDATE `system_menu` SET `icon` = 'semantic-asset' WHERE `icon` = '语义资产';
UPDATE `system_menu` SET `icon` = 'sub-ontology-modeling' WHERE `icon` = '二级本体建模';
UPDATE `system_menu` SET `icon` = 'modeling-goal' WHERE `icon` = '建模目标';
UPDATE `system_menu` SET `icon` = 'data-type' WHERE `icon` = '数据类型';
UPDATE `system_menu` SET `icon` = 'enumeration-management' WHERE `icon` = '枚举管理';
UPDATE `system_menu` SET `icon` = 'data-source' WHERE `icon` = '数据源';
UPDATE `system_menu` SET `icon` = 'file-category' WHERE `icon` = '文件分类';
UPDATE `system_menu` SET `icon` = 'file-management' WHERE `icon` = '文件管理';
UPDATE `system_menu` SET `icon` = 'ontology-application' WHERE `icon` = '本体应用';
UPDATE `system_menu` SET `icon` = 'knowledge-graph' WHERE `icon` = '知识图谱';
UPDATE `system_menu` SET `icon` = 'project-overview' WHERE `icon` = '项目概览';
UPDATE `system_menu` SET `icon` = 'constraint-rule' WHERE `icon` = '约束规则';
UPDATE `system_menu` SET `icon` = 'unstructured-extraction' WHERE `icon` = '非结构化抽取';
UPDATE `system_menu` SET `icon` = 'extraction-log' WHERE `icon` = '抽取日志';
UPDATE `system_menu` SET `icon` = 'structured-extraction' WHERE `icon` = '结构化抽取';
UPDATE `system_menu` SET `icon` = 'quality-center' WHERE `icon` = '质量中心';
UPDATE `system_menu` SET `icon` = 'ai-assisted-modeling' WHERE `icon` = 'AI辅助建模';

UPDATE `system_dict_data` SET `dict_sort` = 0, `dict_label` = '禁用', `dict_value` = '0', `dict_type` = 'project_status', `css_class` = NULL, `list_class` = 'danger', `is_default` = 'N', `status` = '0', `create_by` = '吴同', `create_time` = '2026-10-12 15:11:21', `update_by` = '吴同', `update_time` = '2026-11-13 15:37:52', `remark` = NULL WHERE `dict_code` = 58;
