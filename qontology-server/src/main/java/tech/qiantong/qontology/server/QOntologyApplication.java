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

package tech.qiantong.qontology.server;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 启动程序
 *
 * @author qontology
 */
@EnableFileStorage
@ComponentScan(basePackages = {"tech.qiantong"})
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class QOntologyApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(QOntologyApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  qOntology本体工程平台启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                "         ___        _        _                   \n" +
                "   __ _ / _ \\ _ __ | |_ ___ | | ___   __ _ _   _ \n" +
                "  / _` | | | | '_ \\| __/ _ \\| |/ _ \\ / _` | | | |\n" +
                " | (_| | |_| | | | | || (_) | | (_) | (_| | |_| |\n" +
                "  \\__, |\\___/|_| |_|\\__\\___/|_|\\___/ \\__, |\\__, |\n" +
                "     |_|                             |___/ |___/");
    }
}
