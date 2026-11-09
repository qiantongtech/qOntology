ALTER TABLE `basic_file_category`
    ADD COLUMN `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用' AFTER `ancestors`;
ALTER TABLE basic_datasource ADD status tinyint DEFAULT 1 NOT NULL COMMENT '状态：0禁用，1启用' after description;