
-- AI 辅助建模候选枚举描述及关系反向名称数据修复
ALTER TABLE `om_ai_enum_type` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_type`;
ALTER TABLE `om_ai_enum_data` ADD COLUMN `description` VARCHAR(512) NULL COMMENT '描述' AFTER `enum_value`;
UPDATE `om_ai_relation` SET `reverse_name` = NULL WHERE `reversible_flag` IS NULL OR `reversible_flag` <> 1;
