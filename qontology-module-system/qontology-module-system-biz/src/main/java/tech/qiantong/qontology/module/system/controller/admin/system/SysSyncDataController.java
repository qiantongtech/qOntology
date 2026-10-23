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

package tech.qiantong.qontology.module.system.controller.admin.system;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.module.system.service.auth.SysSyncDataService;

import jakarta.annotation.Resource;

/**
 * 接收认证平台推送的数据
 */
@RestController
@RequestMapping("/syncData")
public class SysSyncDataController {
    private static final Logger log = LoggerFactory.getLogger(SysSyncDataController.class);
    @Resource
    private SysSyncDataService sysSyncDataService;


    /**
     * 接收认证平台推送过来的数据
     *
     * @return
     */
    @PostMapping
    @Transactional
    public AjaxResult syncData(@RequestBody JSONObject jsonObject) {
        log.info("接收认证平台推送的数据:{}", jsonObject);
        return sysSyncDataService.syncData(jsonObject);
    }
}
