ALTER TABLE `basic_file_category`
    ADD COLUMN `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0正常，1停用' AFTER `ancestors`;
ALTER TABLE basic_datasource ADD status tinyint DEFAULT 0 NOT NULL COMMENT '状态：0正常，1停用' after description;
