-- AI 辅助建模任务记录文件来源，便于编辑回显和问题追踪。
-- 已有任务均按历史行为视为用户上传文件。
ALTER TABLE `om_ai_task` ADD COLUMN `file_source` varchar(16) NOT NULL DEFAULT 'UPLOAD' COMMENT '文件来源：UPLOAD用户上传，SYSTEM系统文件'  AFTER `version_id`;

-- 系统文件按一对多关系记录在任务文件明细中，保留原始知识文件编号用于追溯。
ALTER TABLE `om_ai_file`  ADD COLUMN `source_file_id` bigint NULL   COMMENT '系统文件编号；用户上传文件为空' AFTER `task_id`,   ADD INDEX `idx_om_ai_file_source` (`source_file_id`);
