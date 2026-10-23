package tech.qiantong.qontology.module.app.service.neo4j.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.GraphUpsertWrapper;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.enums.GraphLabelEnum;
import tech.qiantong.qontology.graphis.repository.GraphisRepository;
import tech.qiantong.qontology.module.app.enums.ReleaseStatus;
import tech.qiantong.qontology.module.app.service.neo4j.AppNeo4jService;

import java.util.HashMap;
import java.util.Map;

/**
 * 知识图谱 Neo4j 服务实现。
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppNeo4jServiceImpl implements AppNeo4jService {

    /** 非结构化抽取类型。 */
    private static final Integer UNSTRUCTURED_TYPE = GraphLabelEnum.UNSTRUCTURED.getCode();

    /** 结构化抽取类型。 */
    private static final Integer STRUCTURED_TYPE = GraphLabelEnum.STRUCTURED.getCode();

    /** 未发布状态。 */
    private static final Integer UNPUBLISHED_STATUS = ReleaseStatus.UNPUBLISHED.getValue();

    @Resource
    private GraphisRepository graphisRepository;

    /**
     * 删除指定结构化任务以前生成的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void deleteStructExtraction(Long taskId) {
        graphisRepository.deleteExtStruck(taskId);
    }

    /**
     * 保存结构化抽取实体。
     *
     * @param graphEntity 图谱实体
     */
    @Override
    public void saveStructEntity(GraphEntity graphEntity) {
        GraphUpsertWrapper<GraphEntity> wrapper = buildStructEntityMergeWrapper(
                ((Number) graphEntity.getProperty("task_id")).longValue(),
                String.valueOf(graphEntity.getProperty("table_name")),
                graphEntity.getProperty("primary_key_data"));
        graphisRepository.merge(graphEntity, wrapper);
    }

    /**
     * 保存结构化抽取关系。
     *
     * @param taskId 任务编号
     * @param relationName 关系名称
     * @param sourceTable 源表
     * @param sourcePrimaryKey 源表主键值
     * @param targetTable 目标表
     * @param targetPrimaryKey 目标表主键值
     */
    @Override
    public void saveStructRelationship(Long taskId, String relationName,
                                       String sourceTable, Object sourcePrimaryKey,
                                       String targetTable, Object targetPrimaryKey) {
        Map<String, Object> relationshipProperties = new HashMap<>();
        relationshipProperties.put("task_id", taskId);
        relationshipProperties.put("entity_type", STRUCTURED_TYPE);
        graphisRepository.mergeRelationShip(relationName, relationshipProperties,
                buildStructEntityMergeWrapper(taskId, sourceTable, sourcePrimaryKey),
                buildStructEntityMergeWrapper(taskId, targetTable, targetPrimaryKey));
    }

    /**
     * 发布指定结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void releaseStructExtraction(Long taskId) {
        updateStructReleaseStatus(taskId, ReleaseStatus.PUBLISHED.getValue());
    }

    /**
     * 取消发布指定结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void cancelReleaseStructExtraction(Long taskId) {
        updateStructReleaseStatus(taskId, ReleaseStatus.UNPUBLISHED.getValue());
    }

    /**
     * 构建结构化实体合并条件。
     *
     * @param taskId 任务编号
     * @param tableName 表名
     * @param primaryKeyData 主键值
     * @return 实体合并条件
     */
    private GraphUpsertWrapper<GraphEntity> buildStructEntityMergeWrapper(Long taskId,
                                                                           String tableName,
                                                                           Object primaryKeyData) {
        GraphUpsertWrapper<GraphEntity> wrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        wrapper.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        wrapper.eq(GraphEntity::getDynamicProperties, "task_id", taskId);
        wrapper.eq(GraphEntity::getDynamicProperties, "table_name", tableName);
        wrapper.eq(GraphEntity::getDynamicProperties, "primary_key_data", primaryKeyData);
        wrapper.eq(GraphEntity::getDynamicProperties, "entity_type", STRUCTURED_TYPE);
        return wrapper;
    }

    /**
     * 更新结构化任务节点的发布状态。
     *
     * @param taskId 任务编号
     * @param releaseStatus 发布状态
     */
    private void updateStructReleaseStatus(Long taskId, Integer releaseStatus) {
        GraphUpsertWrapper<GraphEntity> wrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        wrapper.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        wrapper.eq(GraphEntity::getDynamicProperties, "task_id", taskId);
        wrapper.eq(GraphEntity::getDynamicProperties, "entity_type", STRUCTURED_TYPE);
        GraphEntity graphEntity = new GraphEntity();
        graphEntity.setReleaseStatus(releaseStatus);
        graphEntity.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        graphisRepository.update(graphEntity, wrapper);
    }

    /**
     * 删除指定非结构化任务以前生成的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void deleteUnstructExtraction(Long taskId) {
        graphisRepository.deleteExtUnStruck(taskId);
    }

    /**
     * 保存单个分段的大模型抽取结果。
     *
     * @param taskId 任务编号
     * @param docId 文件编号
     * @param paragraphIndex 分段序号
     * @param textId 抽取片段编号
     * @param result 抽取结果
     */
    @Override
    public void saveUnstructExtraction(Long taskId, Long docId, Integer paragraphIndex, Long textId,
                                       JSONObject result) {
        JSONArray entities = result.getJSONArray("entities");
        Map<String, JSONObject> entityMap = new HashMap<>();
        if (entities != null) {
            for (int index = 0; index < entities.size(); index++) {
                JSONObject entity = entities.getJSONObject(index);
                String entityName = entity.getString("实体文本");
                if (StringUtils.isBlank(entityName)) {
                    continue;
                }
                entityName = entityName.trim();
                mergeEntity(taskId, docId, paragraphIndex, textId, entityName, entity);
                entityMap.put(entityName, entity);
            }
        }

        JSONArray relations = result.getJSONArray("relations");
        if (relations == null) {
            return;
        }
        for (int index = 0; index < relations.size(); index++) {
            JSONObject relation = relations.getJSONObject(index);
            String head = relation.getString("头实体");
            String tail = relation.getString("尾实体");
            String relationName = relation.getString("关系");
            if (StringUtils.isBlank(head) || StringUtils.isBlank(tail)
                    || StringUtils.isBlank(relationName)) {
                continue;
            }
            head = head.trim();
            tail = tail.trim();
            relationName = relationName.trim();

            // 与 qKnow 保持一致：关系中出现但实体列表遗漏的节点也需要补建。
            mergeEntity(taskId, docId, paragraphIndex, textId, head, entityMap.get(head));
            mergeEntity(taskId, docId, paragraphIndex, textId, tail, entityMap.get(tail));
            mergeRelationship(taskId, docId, paragraphIndex, head, tail, relationName);
        }
    }

    /**
     * 发布指定非结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void releaseUnstructExtraction(Long taskId) {
        updateReleaseStatus(taskId, ReleaseStatus.PUBLISHED.getValue());
    }

    /**
     * 取消发布指定非结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    @Override
    public void cancelReleaseUnstructExtraction(Long taskId) {
        updateReleaseStatus(taskId, ReleaseStatus.UNPUBLISHED.getValue());
    }

    /**
     * 合并非结构化抽取实体。
     *
     * @param taskId 任务编号
     * @param docId 文件编号
     * @param paragraphIndex 分段序号
     * @param textId 抽取片段编号
     * @param entityName 实体名称
     * @param entityResult 实体抽取结果
     */
    private void mergeEntity(Long taskId, Long docId, Integer paragraphIndex,
                             Long textId, String entityName, JSONObject entityResult) {
        GraphEntity graphEntity = new GraphEntity();
        graphEntity.setName(entityName);
        graphEntity.setReleaseStatus(UNPUBLISHED_STATUS);
        graphEntity.addLabels(GraphLabelEnum.UNSTRUCTURED.getLabel());
        graphEntity.putDynamicProperties("task_id", taskId);
        graphEntity.putDynamicProperties("entity_type", UNSTRUCTURED_TYPE);
        graphEntity.putDynamicProperties("doc_id", docId);
        graphEntity.putDynamicProperties("paragraph_index", paragraphIndex);
        graphEntity.putDynamicProperties("text_ids", String.valueOf(textId));

        if (entityResult != null) {
            putDynamicProperty(graphEntity, "schema_name", entityResult.get("实体类型"));
            putDynamicProperty(graphEntity, "fragment", entityResult.get("片段"));
            JSONObject properties = entityResult.getJSONObject("属性");
            if (properties != null) {
                properties.forEach((key, value) -> putDynamicProperty(graphEntity, key, value));
            }
        }

        GraphUpsertWrapper<GraphEntity> mergeWrapper = buildEntityMergeWrapper(
                taskId, docId, paragraphIndex, entityName);
        graphisRepository.merge(graphEntity, mergeWrapper);
    }

    /**
     * 合并非结构化抽取关系。
     *
     * @param taskId 任务编号
     * @param docId 文件编号
     * @param paragraphIndex 分段序号
     * @param head 头实体名称
     * @param tail 尾实体名称
     * @param relationName 关系名称
     */
    private void mergeRelationship(Long taskId, Long docId, Integer paragraphIndex,
                                   String head, String tail, String relationName) {
        Map<String, Object> relationshipProperties = new HashMap<>();
        relationshipProperties.put("task_id", taskId);
        relationshipProperties.put("entity_type", UNSTRUCTURED_TYPE);
        graphisRepository.mergeRelationShip(
                relationName,
                relationshipProperties,
                buildEntityMergeWrapper(taskId, docId, paragraphIndex, head),
                buildEntityMergeWrapper(taskId, docId, paragraphIndex, tail));
    }

    /**
     * 构建与 qKnow 非结构化节点一致的合并条件。
     *
     * @param taskId 任务编号
     * @param docId 文件编号
     * @param paragraphIndex 分段序号
     * @param entityName 实体名称
     * @return 节点合并条件
     */
    private GraphUpsertWrapper<GraphEntity> buildEntityMergeWrapper(Long taskId, Long docId,
                                                                     Integer paragraphIndex,
                                                                     String entityName) {
        GraphUpsertWrapper<GraphEntity> mergeWrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        mergeWrapper.addLabels(GraphLabelEnum.UNSTRUCTURED.getLabel());
        mergeWrapper.eq(GraphEntity::getName, entityName);
        mergeWrapper.eq(GraphEntity::getDynamicProperties, "task_id", taskId);
        mergeWrapper.eq(GraphEntity::getDynamicProperties, "doc_id", docId);
        mergeWrapper.eq(GraphEntity::getDynamicProperties, "paragraph_index", paragraphIndex);
        mergeWrapper.eq(GraphEntity::getDynamicProperties, "entity_type", UNSTRUCTURED_TYPE);
        return mergeWrapper;
    }

    /**
     * 更新指定非结构化任务节点的发布状态。
     *
     * @param taskId 任务编号
     * @param releaseStatus 发布状态
     */
    private void updateReleaseStatus(Long taskId, Integer releaseStatus) {
        GraphUpsertWrapper<GraphEntity> updateWrapper = new GraphUpsertWrapper<>(GraphEntity.class);
        updateWrapper.addLabels(GraphLabelEnum.UNSTRUCTURED.getLabel());
        updateWrapper.eq(GraphEntity::getDynamicProperties, "task_id", taskId);
        updateWrapper.eq(GraphEntity::getDynamicProperties, "entity_type", UNSTRUCTURED_TYPE);

        GraphEntity graphEntity = new GraphEntity();
        graphEntity.setReleaseStatus(releaseStatus);
        graphEntity.addLabels(GraphLabelEnum.UNSTRUCTURED.getLabel());
        graphisRepository.update(graphEntity, updateWrapper);
    }

    /**
     * 写入 Neo4j 支持的动态属性值。
     *
     * @param graphEntity 图谱实体
     * @param key 属性名称
     * @param value 属性值
     */
    private void putDynamicProperty(GraphEntity graphEntity, String key, Object value) {
        if (StringUtils.isBlank(key) || value == null) {
            return;
        }
        if (value instanceof Map || value instanceof Iterable) {
            graphEntity.putDynamicProperties(key, JSONObject.toJSONString(value));
            return;
        }
        graphEntity.putDynamicProperties(key, value);
    }
}
