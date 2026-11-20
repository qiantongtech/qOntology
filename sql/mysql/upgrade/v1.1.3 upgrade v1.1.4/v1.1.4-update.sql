
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

ALTER TABLE `om_ai_task` ADD COLUMN `file_source` varchar(16) NOT NULL DEFAULT 'UPLOAD' COMMENT '文件来源：UPLOAD用户上传，SYSTEM系统文件'  AFTER `version_id`;
ALTER TABLE `om_ai_file`  ADD COLUMN `source_file_id` bigint NULL   COMMENT '系统文件编号；用户上传文件为空' AFTER `task_id`,   ADD INDEX `idx_om_ai_file_source` (`source_file_id`);
ALTER TABLE `om_ai_object` ADD COLUMN `color` varchar(7) NOT NULL DEFAULT '#3B68C2' COMMENT '对象类型颜色' AFTER `name`;
ALTER TABLE `om_object_type` ADD COLUMN `color` varchar(7) NOT NULL DEFAULT '#3B68C2' COMMENT '对象类型颜色' AFTER `name`;
