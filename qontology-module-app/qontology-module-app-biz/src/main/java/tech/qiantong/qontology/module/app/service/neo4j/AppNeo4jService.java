package tech.qiantong.qontology.module.app.service.neo4j;

import com.alibaba.fastjson2.JSONObject;

/**
 * 知识图谱 Neo4j 服务接口。
 *
 * @author qOntology
 * @date 2026-10-20
 */
public interface AppNeo4jService {

    /**
     * 删除指定结构化任务以前生成的图谱数据。
     *
     * @param taskId 任务编号
     */
    void deleteStructExtraction(Long taskId);

    /**
     * 保存结构化抽取实体。
     *
     * @param graphEntity 图谱实体
     */
    void saveStructEntity(tech.qiantong.qontology.graphis.domain.GraphEntity graphEntity);

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
    void saveStructRelationship(Long taskId, String relationName,
                                String sourceTable, Object sourcePrimaryKey,
                                String targetTable, Object targetPrimaryKey);

    /**
     * 发布指定结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    void releaseStructExtraction(Long taskId);

    /**
     * 取消发布指定结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    void cancelReleaseStructExtraction(Long taskId);
    /**
     * 删除指定非结构化任务以前生成的图谱数据。
     *
     * @param taskId 任务编号
     */
    void deleteUnstructExtraction(Long taskId);

    /**
     * 保存单个分段的大模型抽取结果。
     *
     * @param taskId 任务编号
     * @param docId 文件编号
     * @param paragraphIndex 分段序号
     * @param textId 抽取片段编号
     * @param result 抽取结果
     */
    void saveUnstructExtraction(Long taskId, Long docId, Integer paragraphIndex, Long textId,
                                JSONObject result);

    /**
     * 发布指定非结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    void releaseUnstructExtraction(Long taskId);

    /**
     * 取消发布指定非结构化任务的图谱数据。
     *
     * @param taskId 任务编号
     */
    void cancelReleaseUnstructExtraction(Long taskId);
}
