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
