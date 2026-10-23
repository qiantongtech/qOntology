/*
 * Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
 *  *
 * Software Name: qKnow Knowledge Platform (Business Edition)
 * Software Copyright Registration No. 15980140
 *  *
 * [RIGHTS AND LICENSE STATEMENT]
 * This file contains non-public commercial source code of which Jiangsu Qiantong
 * Technology Co., Ltd. lawfully possesses complete intellectual property rights.
 *  *
 * Access and use are limited to entities or individuals who have signed a valid
 * commercial license agreement, within the scope stipulated in the agreement.
 * The "accessibility" of this source code is premised on lawful authorization
 * and does not constitute any form of transfer of intellectual property rights
 * or implied licensing.
 *  *
 * [PROHIBITIONS]
 * Unless explicitly agreed in the license agreement, the following acts in any
 * form are strictly prohibited:
 * 1. Copying, disseminating, disclosing, selling, renting, or redistributing
 * this source code;
 * 2. Providing the software's functionality to third parties via SaaS, PaaS,
 * cloud hosting, or other means;
 * 3. Using this software or its derivative versions to develop products that
 * compete with the Right Holder;
 * 4. Providing or displaying this source code or related technical information
 * to unauthorized third parties;
 * 5. Tampering with, circumventing, or destroying copyright notices, license
 * verifications, or other technical protection measures.
 *  *
 * [LEGAL LIABILITY]
 * Any unauthorized use constitutes an infringement of trade secrets and
 * intellectual property rights.
 *  *
 * The Right Holder will strictly pursue liability for breach of contract and
 * infringement in accordance with the commercial agreement and laws such as
 * the "Copyright Law of the People's Republic of China" and the "Anti-Unfair
 * Competition Law".
 *  *
 * ============================================================================
 *  *
 * Copyright (c) 2026 江苏千桐科技有限公司
 *  *
 * 软件名称：qKnow 知识平台（商业版） | 软著登字第15980140号
 *  *
 * 【权利与授权声明】
 * 本文件属于江苏千桐科技有限公司依法享有完全知识产权的非公开商业源代码。
 * 仅限已签署有效商业授权合同的单位或个人在约定范围内查阅和使用。
 * 源代码的“可访问性”均以合法授权为前提，不构成任何形式的知识产权转让或默示授权。
 *  *
 * 【禁止事项】
 * 除授权合同明确约定外，严禁任何形式的：
 * 1. 复制、传播、披露、出售、出租或再分发本源代码；
 * 2. 通过 SaaS、PaaS、云托管等方式向第三方提供本软件功能；
 * 3. 将本软件或其衍生版本用于开发与权利人构成竞争的产品；
 * 4. 向未授权第三方提供或展示本源代码或相关技术信息；
 * 5. 篡改、规避或破坏版权标识、授权校验及其他技术保护措施。
 *  *
 * 【法律责任】
 * 任何未经授权的利用行为，均构成对商业秘密及知识产权的侵害。
 * 权利人将依据商业合同及《中华人民共和国著作权法》《反不正当竞争法》
 * 等法律法规，严厉追究违约与侵权责任。
 */

package tech.qiantong.qontology.neo4j.core.wrapper.query;

import tech.qiantong.qontology.neo4j.core.wrapper.AbstractWrapper;
import tech.qiantong.qontology.neo4j.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 语法生成
 * @author wang
 * @date 2025/11/18 10:29
 **/
public abstract class AbstractCypherQueryWrapper<T, R, Children extends AbstractCypherQueryWrapper<T, R, Children>>
        extends AbstractWrapper<T, R, Children> {

    public AbstractCypherQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 获取标签
     * @return
     */
    private String getLabel() {
        StringBuilder labelBuilder = new StringBuilder();
        String simpleName = entityClass.getSimpleName();
        labelBuilder.append(simpleName);
        labels.forEach(label -> labelBuilder.append(":").append(label));
        return labelBuilder.toString();
    }

    public String getCypherQuerySql() {
        StringBuilder query = new StringBuilder("MATCH (n:" + getLabel() + ")");
        query.append(Utils.getWhereClause(super.conditions));
        // 添加 OPTIONAL MATCH 捕获所有关系
        query.append(" OPTIONAL MATCH (n)-[r]->(related)");
        query.append(" RETURN n, collect(r), collect(related)");
        if (orderBy != null) {
            query.append(" ORDER BY ").append(orderBy);
        }
        if (limit > 0) {
            query.append(" SKIP ").append(offset).append(" LIMIT ").append(limit);
        }
        return query.toString();
    }

    public String getRelationChainCypherQuerySql(Integer depth) {
        StringBuilder query = new StringBuilder();

        // 1. 获取所有符合条件的起点节点
        query.append("MATCH (n:").append(getLabel()).append(")");
        query.append(Utils.getWhereClause(super.conditions));
        query.append(" WITH COLLECT(id(n)) AS startIds");

        // 2. 使用子查询获取扩展节点（避免路径爆炸）
        query.append(" CALL {");
        query.append("   WITH startIds");
        query.append("   UNWIND startIds AS startId");
        query.append("   MATCH (startNode) WHERE id(startNode) = startId");
        query.append("   CALL {");
        query.append("     WITH startNode");
        query.append("     MATCH (startNode)-[*0..").append(depth).append("]-(connected)");
        query.append("     RETURN COLLECT(DISTINCT id(connected)) AS connectedIds");
        query.append("   }");
        query.append("   RETURN connectedIds");
        query.append(" }");

        // 3. 合并所有节点ID并去重
        query.append(" WITH startIds + connectedIds AS allIds");
        query.append(" UNWIND allIds AS idList");
        query.append(" UNWIND idList AS nodeId");
        query.append(" WITH COLLECT(DISTINCT nodeId) AS uniqueIds");

        // 4. 批量获取节点
        query.append(" UNWIND uniqueIds AS id");
        query.append(" MATCH (currentNode) WHERE id(currentNode) = id");

        // 5. 批量获取关系（关键优化：只获取子图内部关系）
        query.append(" OPTIONAL MATCH (currentNode)-[r]->(related)");
        query.append(" WHERE id(related) IN uniqueIds");

        // 6. 保持原有返回格式
        query.append(" WITH currentNode, r, related");
        query.append(" RETURN currentNode, collect(r), collect(related)");

        return query.toString();
    }

    public String getCypherCountQuerySql() {
        return "MATCH (n:" + getLabel() + ")" + Utils.getWhereClause(super.conditions) +
                " RETURN count(n)";
    }

    public String getCypherCountRelationQuerySql() {
        return "MATCH (n:" + getLabel() + ")-[r]->()" + Utils.getWhereClause(super.conditions) +
                " RETURN count(r)";
    }

    public String getNodeQuerySql(Set<String> labels) {
        StringBuilder query = new StringBuilder("MATCH (n)");
        query.append(Utils.getWhereClause(super.conditions));
        if (!labels.isEmpty()){
            List<String> labelList = new ArrayList<>(labels);
            // 构建条件表达式的字符串部分
            StringBuilder labelConditions = new StringBuilder();
            for (int i = 0; i < labelList.size(); i++) {
                labelConditions.append("'" + labelList.get(i) + "' IN labels(n)");
                if (i < labelList.size() - 1) {
                    labelConditions.append(" OR ");
                }
            }
            query.append(" AND (").append(labelConditions).append(")");
        }
        query.append(" RETURN n");
        return query.toString();
    }

    public String getRelQuerySql(Set<String> labels) {
        StringBuilder query = new StringBuilder("OPTIONAL MATCH (n)-[r]->(m)");
        query.append(Utils.getWhereClause(super.conditions));
        if (!labels.isEmpty()){
            List<String> labelList = new ArrayList<>(labels);
            // 构建条件表达式的字符串部分
            StringBuilder labelConditions = new StringBuilder();
            for (int i = 0; i < labelList.size(); i++) {
                labelConditions.append("'" + labelList.get(i) + "' IN labels(n)");
                if (i < labelList.size() - 1) {
                    labelConditions.append(" OR ");
                }
            }
            query.append(" AND (").append(labelConditions).append(")");
        }
        query.append(" RETURN n, r, m");
        return query.toString();
    }
}
