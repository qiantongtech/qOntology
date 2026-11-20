-- AI 辅助建模任务记录文件来源，便于编辑回显和问题追踪。
-- 已有任务均按历史行为视为用户上传文件。
ALTER TABLE `om_ai_task` ADD COLUMN `file_source` varchar(16) NOT NULL DEFAULT 'UPLOAD' COMMENT '文件来源：UPLOAD用户上传，SYSTEM系统文件'  AFTER `version_id`;

-- 系统文件按一对多关系记录在任务文件明细中，保留原始知识文件编号用于追溯。
ALTER TABLE `om_ai_file`  ADD COLUMN `source_file_id` bigint NULL   COMMENT '系统文件编号；用户上传文件为空' AFTER `task_id`,   ADD INDEX `idx_om_ai_file_source` (`source_file_id`);
ALTER TABLE `om_object_type`
    ADD COLUMN `color` varchar(7) NOT NULL DEFAULT '#3B68C2' COMMENT '对象类型颜色' AFTER `name`;

-- 为历史对象类型分配固定颜色。
UPDATE `om_object_type`
SET `color` = ELT(MOD(`id`, 15) + 1,
    '#3B68C2', '#289470', '#B58200', '#B93C32', '#2A8CB8',
    '#6B4296', '#C46518', '#0E706E', '#A83F70', '#4068A8',
    '#358252', '#B16C27', '#7257A0', '#2D78B5', '#378A46');

UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '1';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '2';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '3';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '4';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '5';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '6';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '7';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '8';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '9';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '10';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '11';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '12';
UPDATE `app_unstruct_task` SET `ttl_file_path` = '/2026/11/19/6afe9c1884562a8ced3fae43.ttl' WHERE id = '13';
