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

package tech.qiantong.qontology.arangodb.core.config;

import com.arangodb.ArangoDB;
import com.arangodb.springframework.annotation.EnableArangoRepositories;
import com.arangodb.springframework.config.ArangoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author wang
 */
@Configuration
@EnableArangoRepositories(basePackages = "tech.qiantong.**.arangodb.repository")
@ConditionalOnProperty(prefix = "graphis", name = "type", havingValue="arangodb")
public class ArangoDBConfig implements ArangoConfiguration {

    @Value("${arangodb.host}")
    private String host;
    @Value("${arangodb.port}")
    private int port;
    @Value("${arangodb.user}")
    private String username;
    @Value("${arangodb.password}")
    private String password;
    @Value("${arangodb.database}")
    private String database;

    @Override
    public ArangoDB.Builder arango() {
        return new ArangoDB.Builder().host(host, port).user(username).password(password);
    }

    @Override
    public String database() {
        return database;
    }
}
