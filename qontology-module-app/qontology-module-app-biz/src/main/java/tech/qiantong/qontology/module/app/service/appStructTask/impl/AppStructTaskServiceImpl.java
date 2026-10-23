/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.service.appStructTask.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.constant.ScheduleConstants;
import tech.qiantong.qontology.core.domain.AjaxResult;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.common.database.DataSourceFactory;
import tech.qiantong.qontology.common.database.DbQuery;
import tech.qiantong.qontology.common.database.constants.DbQueryProperty;
import tech.qiantong.qontology.common.database.core.DbColumn;
import tech.qiantong.qontology.common.database.exception.DataQueryException;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.exception.job.TaskException;
import tech.qiantong.qontology.common.utils.PageUtil;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.graphis.core.wrapper.query.GraphQueryWrapper;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.enums.GraphLabelEnum;
import tech.qiantong.qontology.graphis.repository.GraphisRepository;
import tech.qiantong.qontology.module.app.enums.ReleaseStatus;
import tech.qiantong.qontology.module.basic.api.dto.datasource.BasicDatasourceRespDTO;
import tech.qiantong.qontology.module.basic.api.service.asset.IBasicDatasourceApiService;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appAttributeMapping.vo.AppAttributeMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appRelationMapping.vo.AppRelationMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appSchemaMapping.vo.AppSchemaMappingRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appAttributeMapping.AppAttributeMappingDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMapping.AppRelationMappingDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appRelationMappingMiddle.AppRelationMappingMiddleDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appSchemaMapping.AppSchemaMappingDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTask;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTaskDO;
import tech.qiantong.qontology.module.app.dal.mapper.appAttributeMapping.AppAttributeMappingMapper;
import tech.qiantong.qontology.module.app.dal.mapper.appRelationMapping.AppRelationMappingMapper;
import tech.qiantong.qontology.module.app.dal.mapper.appRelationMappingMiddle.AppRelationMappingMiddleMapper;
import tech.qiantong.qontology.module.app.dal.mapper.appSchemaMapping.AppSchemaMappingMapper;
import tech.qiantong.qontology.module.app.dal.mapper.appStructTask.AppStructTaskMapper;
import tech.qiantong.qontology.module.app.extEnum.*;
import tech.qiantong.qontology.module.app.service.appStructTask.IAppStructTaskService;
import tech.qiantong.qontology.module.app.service.appTaskLog.IAppTaskLogService;
import tech.qiantong.qontology.module.app.service.neo4j.AppNeo4jService;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphVO;
import tech.qiantong.qontology.module.app.service.graph.AppGraphService;
import tech.qiantong.qontology.quartz.domain.SysJob;
import tech.qiantong.qontology.quartz.util.CronUtils;
import tech.qiantong.qontology.quartz.util.ScheduleUtils;
import tech.qiantong.qontology.redis.service.IRedisService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 结构化抽取任务Service业务层处理
 *
 * @author qknow
 * @date 2025-02-25
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppStructTaskServiceImpl extends ServiceImpl<AppStructTaskMapper, AppStructTaskDO> implements IAppStructTaskService {
    @Resource
    private AppStructTaskMapper appStructTaskMapper;
    @Resource
    private IBasicDatasourceApiService basicDatasourceApiService;
    @Resource
    private AppSchemaMappingMapper appSchemaMappingMapper;
    @Resource
    private AppAttributeMappingMapper appAttributeMappingMapper;
    @Resource
    private AppRelationMappingMapper appRelationMappingMapper;

    @Resource
    private AppNeo4jService appNeo4jService;
    @Resource
    private AppGraphService appGraphService;
    @Resource
    private IRedisService redisService;

    @Resource
    private Scheduler scheduler;
    @Resource
    private DataSourceFactory dataSourceFactory;
    @Resource
    private AppRelationMappingMiddleMapper appRelationMappingMiddleMapper;

    @Resource
    private GraphisRepository graphisRepository;

    @Resource
    private IAppTaskLogService appTaskLogService;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "app-struct-task-thread");
        thread.setDaemon(true);
        return thread;
    });


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Integer PAGE_SIZE = 50;




    /**
     * 执行抽取
     *
     * @param appStructTask
     * @return tech.qiantong.qontology.core.domain.AjaxResult
     * @author shaun
     * @date 2025/06/18 18:04
     */
    @Override
    public AjaxResult executeExtraction(AppStructTaskPageReqVO appStructTask) {
        AppStructTaskDO appStructTaskDO = appStructTaskMapper.selectById(appStructTask.getId());
        if (appStructTaskDO == null) {
            throw new ServiceException("结构化抽取任务不存在");
        }
        if (ReleaseStatus.PUBLISHED.getValue().equals(appStructTaskDO.getPublishStatus())) {
            throw new RuntimeException("已发布状态不能重新执行抽取");
        }
        if (AppTaskStatus.INPROGRESS.getValue().equals(appStructTaskDO.getStatus())
                || AppTaskStatus.QUEUE.getValue().equals(appStructTaskDO.getStatus())) {
            throw new ServiceException("任务已在执行或排队中");
        }
        appStructTaskDO.setStatus(AppTaskStatus.QUEUE.getValue());
        appStructTaskMapper.updateById(appStructTaskDO);

        //放入redis队列中, 按顺序 一个一个执行
        redisService.leftPush("app_struck", String.valueOf(appStructTask.getId()));
        CompletableFuture.runAsync(this::consumeQueue, executor);
        return AjaxResult.success("操作成功,执行中");
    }

    /**
     * 定时任务，执行抽取
     *
     * @return void
     * @author shaun
     * @date 2025/06/18 18:07
     */
    public void consumeQueue() {
        while (true) {
        // 取出队列中的任务id
        String taskId = redisService.leftPop("app_struck");
        // 判断队列是否为空
        if (StringUtils.isEmpty(taskId)) {
            log.info("队列中没有等待抽取的任务");
            return;
        }

        // 开始抽取
        log.info("----------------结构化抽取任务开始-----------------------taskId:{}", taskId);

        AppStructTaskDO appStructTaskDO = getAppStructTaskById(Long.valueOf(taskId));
        Long logId = appTaskLogService.startInvoke(appStructTaskDO.getWorkspaceId(),
                appStructTaskDO.getId(), appStructTaskDO.getName(), AppTaskTypeEnum.STRUCTURED,
                appStructTaskDO.getCreateBy(), appStructTaskDO.getCreatorId());
        try {
            appStructTaskDO.setStatus(AppTaskStatus.INPROGRESS.getValue());
            appStructTaskMapper.updateById(appStructTaskDO);
            appTaskLogService.recordStep(logId, "结构化抽取开始，读取表映射、属性映射和对象类型关系映射");
            this.execStructTaskInternal(taskId, AppTaskUpdateType.INCREMENTUDATE.getValue().equals(appStructTaskDO.getUpdateType()));
            appTaskLogService.recordStep(logId, "结构化实体及关系已保存到 Neo4j");
            appTaskLogService.endInvoke(logId, AppLogStatusEnum.SUCCESS, "");
        } catch (Exception e) {
            e.printStackTrace();
            // 捕获异常并打印日志
            log.error("结构化任务抽取处理失败: {}", e.getMessage());

            appStructTaskDO.setStatus(AppTaskStatus.ERROR.getValue());
            appStructTaskMapper.updateById(appStructTaskDO);
            appTaskLogService.recordStep(logId, "结构化抽取失败：" + e.getMessage());
            appTaskLogService.endInvoke(logId, AppLogStatusEnum.FAIL, e.getMessage());
        }
        }
    }
    /** 全量更新结构化抽取数据。 */
    private void execStructTask(String taskId) {
        execStructTaskInternal(taskId, false);
    }

    /** 增量更新结构化抽取数据。 */
    private void execStructTaskIncrementUpdate(String taskId) {
        execStructTaskInternal(taskId, true);
    }

    /**
     * 统一的抽取任务执行方法
     */
    private void execStructTaskInternal(String taskId, boolean isIncrement) {
        // 1. 初始化
        AppStructTaskDO task = appStructTaskMapper.selectById(Long.valueOf(taskId));
        if (task == null) throw new DataQueryException("抽取任务不存在");

        // 全量清理
        if (!isIncrement) {
            appNeo4jService.deleteStructExtraction(task.getId());
        }

        // 加载现有实体（用于增量更新）
        Map<String, GraphEntity> existingMap = isIncrement ? loadExistingEntities(task) : new HashMap<>();

        // 2. 数据库连接
        BasicDatasourceRespDTO datasource = basicDatasourceApiService.getDatasourceById(task.getDatasourceId());
        if (datasource == null) throw new DataQueryException("未查到数据源信息");

        DbQueryProperty dbQueryProperty = new DbQueryProperty(
                datasource.getDatasourceType(), datasource.getIp(),
                datasource.getPort(), datasource.getDatasourceConfig());
        DbQuery dbQuery = dataSourceFactory.createDbQuery(dbQueryProperty);
        if (!dbQuery.valid()) {
            dbQuery.close();
            throw new DataQueryException("数据库连接失败");
        }

        try {
            // 3. 加载配置
            List<AppSchemaMappingDO> schemaMappings = loadSchemaMappings(task.getId());
            Map<String, List<AppAttributeMappingDO>> attrMap = loadAttributeMappings(task.getId());
            Map<String, List<AppRelationMappingDO>> relMap = loadRelationMappings(task.getId());
            Map<Long, List<AppRelationMappingMiddleDO>> middleMap = loadMiddleMappings();

            // 4. 处理数据
            List<GraphEntity> entities = new ArrayList<>();
            List<Map<String,Object>> relations = new ArrayList<>();

            for (AppSchemaMappingDO schema : schemaMappings) {
                List<AppRelationMappingDO> relMappings = relMap.get(schema.getTableName());

                if (CollectionUtils.isEmpty(relMappings)) {
                    // 单表处理 - 直接查询
                    processSingleTable(task, schema, dbQuery, dbQueryProperty, isIncrement,
                            existingMap, datasource, attrMap, entities);
                } else {
                    // 多表处理 - 关联查询
                    processMultiTablesWithComplexRelations(task, schema, dbQuery, dbQueryProperty, isIncrement,
                            existingMap, datasource, attrMap, schemaMappings, relMappings, middleMap, entities, relations);
                }
            }

            // 5. 先保存实体，再按表名与主键合并关系。
            entities.forEach(appNeo4jService::saveStructEntity);
            for (Map<String, Object> relation : relations) {
                GraphEntity source = (GraphEntity) relation.get("source");
                GraphEntity target = (GraphEntity) relation.get("target");
                appNeo4jService.saveStructRelationship(task.getId(), (String) relation.get("rel"),
                        String.valueOf(source.getProperty("table_name")), source.getProperty("primary_key_data"),
                        String.valueOf(target.getProperty("table_name")), target.getProperty("primary_key_data"));
            }


            // 6. 更新任务状态
            task.setStatus(AppTaskStatus.EXECUTED.getValue());
            appStructTaskMapper.updateById(task);

        } finally {
            dbQuery.close();
        }
    }

    /**
     * 处理多表
     */
    private void processMultiTablesWithComplexRelations(AppStructTaskDO task, AppSchemaMappingDO schema,
                                                        DbQuery dbQuery, DbQueryProperty dbQueryProperty,
                                                        boolean isIncrement, Map<String, GraphEntity> existingMap,
                                                        BasicDatasourceRespDTO datasource,
                                                        Map<String, List<AppAttributeMappingDO>> attrMap,
                                                        List<AppSchemaMappingDO> schemaMappings,
                                                        List<AppRelationMappingDO> relMappings,
                                                        Map<Long, List<AppRelationMappingMiddleDO>> middleMap,
                                                        List<GraphEntity> entities,
                                                        List<Map<String, Object>> relations) {

        // 用于缓存已处理的实体，避免重复创建
        Map<String, GraphEntity> processedEntities = new HashMap<>();
        // 用于缓存已处理的关系，避免重复创建
        Set<String> processedRelationKeys = new HashSet<>();

        // 首先，为每个关系映射创建独立的查询
        for (AppRelationMappingDO rel : relMappings) {
            // 分页查询参数
            Integer pageNum = 1;
            boolean hasMoreData = true;

            // 循环分页查询直到没有数据
            while (hasMoreData) {
                PageUtil pageUtil = new PageUtil(pageNum, PAGE_SIZE);
                Integer offset = pageUtil.getOffset();
                // 构建关联查询SQL
                String sql = buildRelationSQL(schema, rel, dbQuery, dbQueryProperty, middleMap);
                // 使用dbQuery的分页方法
                tech.qiantong.qontology.common.database.core.PageResult<Map<String, Object>> rows = dbQuery.queryByPage(sql, offset, PAGE_SIZE);

                if (rows == null || rows.getData() == null || rows.getData().isEmpty()) {
                    hasMoreData = false;
                    continue;
                }

                if (CollectionUtils.isEmpty(rows.getData())) continue;

                // 处理查询结果中的每一行
                for (Map<String, Object> row : rows.getData()) {
                    // 1. 提取源表数据（a_开头的字段）
                    Map<String, Object> sourceData = extractTableData(row, "a_", schema.getTableName());

                    // 获取源表实体信息
                    String sourceEntityNameField = schema.getEntityNameField();
                    String sourcePrimaryKeyField = schema.getPrimaryKey();

                    Object sourceEntityName = sourceData.get(sourceEntityNameField);
                    Object sourcePrimaryKeyValue = sourceData.get(sourcePrimaryKeyField);

                    if (sourceEntityName == null || sourcePrimaryKeyValue == null) continue;

                    // 2. 提取目标表数据（b_开头的字段）
                    Map<String, Object> targetData = extractTableData(row, "b_", rel.getRelationTable());

                    // 获取目标表实体信息
                    String targetEntityNameField = rel.getRelationNameField();
                    String targetPrimaryKeyField = rel.getRelationField();

                    Object targetEntityName = targetData.get(targetEntityNameField);
                    Object targetPrimaryKeyValue = targetData.get(targetPrimaryKeyField);

                    if (targetEntityName == null || targetPrimaryKeyValue == null) continue;

                    // 3. 获取或创建源实体
                    GraphEntity sourceEntity = getOrCreateEntity(task, datasource, schema, sourceData, attrMap,
                            existingMap, entities, processedEntities, isIncrement,
                            sourceEntityName, sourcePrimaryKeyValue);

                    if (sourceEntity == null) continue;

                    // 4. 获取或创建目标实体
                    GraphEntity targetEntity = getOrCreateTargetEntity(task, datasource, rel, targetData,
                            existingMap, entities, processedEntities, attrMap, schemaMappings, isIncrement,
                            targetEntityName, targetPrimaryKeyValue);

                    if (targetEntity == null) continue;

                    // 5. 构建关系键（用于去重）
                    String relationKey = buildRelationKey(sourceEntity, targetEntity, rel.getRelation());

                    // 6. 如果关系不存在，则创建
                    if (!processedRelationKeys.contains(relationKey)) {
                        Map<String, Object> relation = buildRelation(task, sourceEntity, targetEntity, rel);
                        relations.add(relation);
                        processedRelationKeys.add(relationKey);
                    }
                }

                // 准备下一页
                pageNum++;

                // 如果当前页数据少于pageSize，说明是最后一页
                if (rows.getData().size() < PAGE_SIZE) {
                    hasMoreData = false;
                }
            }

        }
    }

    /**
     * 获取或创建实体
     */
    private GraphEntity getOrCreateEntity(AppStructTaskDO task, BasicDatasourceRespDTO datasource,
                                          AppSchemaMappingDO schema, Map<String, Object> data,
                                          Map<String, List<AppAttributeMappingDO>> attrMap,
                                          Map<String, GraphEntity> existingMap,
                                          List<GraphEntity> entities,
                                          Map<String, GraphEntity> processedEntities,
                                          boolean isIncrement,
                                          Object entityName, Object primaryKeyValue) {

        // 构建实体唯一键（表名 + 主键值）
        String entityKey = schema.getTableName() + "_" + primaryKeyValue;

        // 1. 检查是否已处理（当前批次缓存）
        if (processedEntities.containsKey(entityKey)) {
            return processedEntities.get(entityKey);
        }

        // 2. 检查是否已在当前实体列表中
        GraphEntity existingEntity = findEntityInList(entities, schema.getTableName(), primaryKeyValue);
        if (existingEntity != null) {
            processedEntities.put(entityKey, existingEntity);
            return existingEntity;
        }

        // 3. 检查是否已存在（增量模式下的历史数据）
        if (isIncrement) {
            // 从existingMap中查找（现有实现使用表名+实体名，需要改进）
            for (GraphEntity entity : existingMap.values()) {
                if (schema.getTableName().equals(entity.getDynamicProperties().get("table_name")) &&
                        primaryKeyValue.toString().equals(entity.getDynamicProperties().get("primary_key_data").toString())) {
                    processedEntities.put(entityKey, entity);
                    return entity;
                }
            }
        }

        // 4. 创建新实体
        GraphEntity entity = createGraphEntity(task, datasource, schema, data, attrMap,
                entityName, primaryKeyValue);
        entities.add(entity);
        processedEntities.put(entityKey, entity);

        return entity;
    }

    /**
     * 获取或创建目标实体
     */
    private GraphEntity getOrCreateTargetEntity(AppStructTaskDO task, BasicDatasourceRespDTO datasource,
                                                AppRelationMappingDO rel, Map<String, Object> data,
                                                Map<String, GraphEntity> existingMap,
                                                List<GraphEntity> entities,
                                                Map<String, GraphEntity> processedEntities,
                                                Map<String, List<AppAttributeMappingDO>> attrMap,
                                                List<AppSchemaMappingDO> schemaMappings,
                                                boolean isIncrement,
                                                Object entityName, Object primaryKeyValue) {

        // 构建实体唯一键（表名 + 主键值）
        String entityKey = rel.getRelationTable() + "_" + primaryKeyValue;

        // 1. 检查是否已处理（当前批次缓存）
        if (processedEntities.containsKey(entityKey)) {
            return processedEntities.get(entityKey);
        }

        // 2. 检查是否已在当前实体列表中
        GraphEntity existingEntity = findEntityInList(entities, rel.getRelationTable(), primaryKeyValue);
        if (existingEntity != null) {
            processedEntities.put(entityKey, existingEntity);
            return existingEntity;
        }

        // 3. 检查是否已存在（增量模式下的历史数据）
        if (isIncrement) {
            // 从existingMap中查找
            for (GraphEntity entity : existingMap.values()) {
                if (rel.getRelationTable().equals(entity.getDynamicProperties().get("table_name")) &&
                        primaryKeyValue.toString().equals(entity.getDynamicProperties().get("primary_key_data").toString())) {
                    processedEntities.put(entityKey, entity);
                    return entity;
                }
            }
        }

        // 4. 创建新实体
        GraphEntity entity = createTargetEntity(task, datasource, rel, data,
                attrMap, schemaMappings, entityName, primaryKeyValue);
        entities.add(entity);
        processedEntities.put(entityKey, entity);

        return entity;
    }

    /**
     * 在实体列表中查找实体（根据表名和主键值）
     */
    private GraphEntity findEntityInList(List<GraphEntity> entities, String tableName, Object primaryKeyValue) {
        for (GraphEntity entity : entities) {
            if (tableName.equals(entity.getDynamicProperties().get("table_name")) &&
                    primaryKeyValue.toString().equals(entity.getDynamicProperties().get("primary_key_data").toString())) {
                return entity;
            }
        }
        return null;
    }

    /**
     * 构建关系键（用于去重）
     * 格式：源表:源主键->目标表:目标主键:关系类型
     */
    private String buildRelationKey(GraphEntity sourceEntity, GraphEntity targetEntity, String relationType) {
        String sourceKey = sourceEntity.getDynamicProperties().get("table_name") + ":" +
                sourceEntity.getDynamicProperties().get("primary_key_data");
        String targetKey = targetEntity.getDynamicProperties().get("table_name") + ":" +
                targetEntity.getDynamicProperties().get("primary_key_data");

        return sourceKey + "->" + targetKey + ":" + relationType;
    }

    /**
     * 从查询结果中提取指定表的字段数据
     */
    private Map<String, Object> extractTableData(Map<String, Object> row, String prefix, String tableName) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith(prefix)) {
                // 去掉前缀，得到原始字段名
                String originalField = key.substring(prefix.length());
                result.put(originalField, value);
            } else if (prefix.equals("a_") && !key.startsWith("b_")) {
                // 对于源表，还包括那些没有前缀的字段（如name、primary_key_data等）
                // 这些字段可能是之前处理时添加的
                result.put(key, value);
            }
        }

        return result;
    }

    /**
     * 构建关联SQL
     */
    private String buildRelationSQL(AppSchemaMappingDO schema, AppRelationMappingDO rel,
                                    DbQuery dbQuery, DbQueryProperty dbQueryProperty,
                                    Map<Long, List<AppRelationMappingMiddleDO>> middleMap) {

        // 获取列信息
        List<DbColumn> sourceCols = dbQuery.getTableColumns(dbQueryProperty, schema.getTableName());
        List<DbColumn> targetCols = dbQuery.getTableColumns(dbQueryProperty, rel.getRelationTable());

        StringBuilder sql = new StringBuilder("SELECT ");

        // 添加源表字段
        for (DbColumn col : sourceCols) {
            sql.append("t1.").append(col.getColName()).append(" AS a_").append(col.getColName()).append(", ");
        }

        // 添加目标表字段
        List<AppRelationMappingMiddleDO> middleList = middleMap.get(rel.getId());
        int targetIndex = CollectionUtils.isEmpty(middleList) ? 2 : middleList.size() + 2;
        for (DbColumn col : targetCols) {
            sql.append("t").append(targetIndex).append(".").append(col.getColName())
                    .append(" AS b_").append(col.getColName()).append(", ");
        }

        // 移除末尾逗号
        sql.delete(sql.length() - 2, sql.length());

        // FROM和JOIN
        sql.append("\nFROM ").append(schema.getTableName()).append(" t1");

        if (CollectionUtils.isEmpty(middleList)) {
            sql.append("\nINNER JOIN ").append(rel.getRelationTable()).append(" t2")
                    .append("\n    ON t1.").append(rel.getFieldName())
                    .append(" = t2.").append(rel.getRelationField());
        } else {
            String prevTable = "t1";
            String prevField = rel.getFieldName();
            for (int i = 0; i < middleList.size(); i++) {
                AppRelationMappingMiddleDO middle = middleList.get(i);
                String tableName = dbQuery.buildTableNameByDbType(middle.getTableName());
                String currTable = "t" + (i + 2);

                sql.append("\nINNER JOIN ").append(tableName).append(" ").append(currTable)
                        .append("\n    ON ").append(prevTable).append(".").append(prevField)
                        .append(" = ").append(currTable).append(".").append(middle.getTableField());

                prevTable = currTable;
                prevField = middle.getRelationField();
            }

            sql.append("\nINNER JOIN ").append(rel.getRelationTable()).append(" t").append(targetIndex)
                    .append("\n    ON ").append(prevTable).append(".").append(prevField)
                    .append(" = t").append(targetIndex).append(".").append(rel.getRelationField());
        }

        return sql.toString();
    }

    /**
     * 创建图实体
     */
    private GraphEntity createGraphEntity(AppStructTaskDO task, BasicDatasourceRespDTO datasource,
                                          AppSchemaMappingDO schema, Map<String, Object> data,
                                          Map<String, List<AppAttributeMappingDO>> attrMap,
                                          Object entityNameValue, Object primaryKeyValue) {

        ConcurrentHashMap<String, Object> props = new ConcurrentHashMap<>();

        // 基础属性
        props.put("task_id", task.getId());
        props.put("database_id", datasource.getId());
        props.put("table_name", schema.getTableName());
        props.put("schema_id", schema.getSchemaId());
        props.put("schema_name", schema.getSchemaName());
        props.put("entity_type", GraphLabelEnum.STRUCTURED.getCode());
        props.put("workspace_id", task.getWorkspaceId());
        props.put("release_status", AppReleaseStatus.UNPUBLISHED.getStatus());
        if (task.getProjectId() != null) props.put("project_id", task.getProjectId());
        if (task.getProjectVersionId() != null) props.put("project_version_id", task.getProjectVersionId());
        props.put("primary_key", schema.getPrimaryKey());
        props.put("primary_key_data", primaryKeyValue);
        props.put("name", entityNameValue);
//        props.put("create_time", LocalDateTime.now().format(formatter));

        // 添加属性映射
        List<AppAttributeMappingDO> attrs = attrMap.get(schema.getTableName());
        if (CollectionUtils.isNotEmpty(attrs)) {
            // 构建字段名到属性ID的映射
            Map<String, Long> fieldToAttrId = new HashMap<>();
            for (AppAttributeMappingDO attr : attrs) {
                if (attr.getAttributeId() != null) {
                    fieldToAttrId.put(attr.getFieldName(), attr.getAttributeId());
                }
            }

            // 添加属性字段
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldName = entry.getKey();
                Long attrId = fieldToAttrId.get(fieldName);
                if (attrId != null && entry.getValue() != null) {
                    props.put("attribute_id_" + attrId, entry.getValue());
                }
            }
        }

        // 创建实体对象
        GraphEntity entity = new GraphEntity();
        entity.setName(entityNameValue.toString());
        entity.setReleaseStatus(task.getPublishStatus());
        entity.setDynamicProperties(props);
        entity.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        entity.setCreateTime(new Date());

        return entity;
    }

    /**
     * 创建目标实体（用于多表关联）
     */
    private GraphEntity createTargetEntity(AppStructTaskDO task, BasicDatasourceRespDTO datasource,
                                           AppRelationMappingDO rel, Map<String, Object> data,
                                           Map<String, List<AppAttributeMappingDO>> attrMap,
                                           List<AppSchemaMappingDO> schemaMappings,
                                           Object entityNameValue, Object primaryKeyValue) {

        // 目标表已有对象类型映射时，复用同一实体构建方法，保证属性及项目版本信息完整。
        for (AppSchemaMappingDO schema : schemaMappings) {
            if (rel.getRelationTable().equals(schema.getTableName())) {
                return createGraphEntity(task, datasource, schema, data, attrMap,
                        entityNameValue, primaryKeyValue);
            }
        }

        ConcurrentHashMap<String, Object> props = new ConcurrentHashMap<>();

        // 基础属性
        props.put("task_id", task.getId());
        props.put("database_id", datasource.getId());
        props.put("table_name", rel.getRelationTable());
        props.put("entity_type", GraphLabelEnum.STRUCTURED.getCode());
        props.put("workspace_id", task.getWorkspaceId());
        props.put("release_status", AppReleaseStatus.UNPUBLISHED.getStatus());
        if (task.getProjectId() != null) props.put("project_id", task.getProjectId());
        if (task.getProjectVersionId() != null) props.put("project_version_id", task.getProjectVersionId());
        props.put("primary_key", rel.getRelationField());
        props.put("primary_key_data", primaryKeyValue);
        props.put("name", entityNameValue);
//        props.put("create_time", LocalDateTime.now().format(formatter));

        // 注意：目标实体的schema_id和schema_name可能为空，因为没有对应的schema配置
        // 如果需要，可以从其他途径获取

        // 创建实体对象
        GraphEntity entity = new GraphEntity();
        entity.setName(entityNameValue.toString());
        entity.setReleaseStatus(task.getPublishStatus());
        entity.setDynamicProperties(props);
        entity.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        entity.setCreateTime(new Date());

        return entity;
    }

    /**
     * 构建关系
     */
    private Map<String, Object> buildRelation(AppStructTaskDO task,
                                              GraphEntity sourceEntity,
                                              GraphEntity targetEntity,
                                              AppRelationMappingDO rel) {

        Map<String, Object> relation = new HashMap<>();

        // 关系类型
        relation.put("rel", rel.getRelation());

        // 关系属性
        ConcurrentHashMap<String, Object> relProps = new ConcurrentHashMap<>();
        relProps.put("task_id", task.getId());
        relProps.put("entity_type", GraphLabelEnum.STRUCTURED.getCode());
        relation.put("relMa", relProps);

        relation.put("source", sourceEntity);
        relation.put("target", targetEntity);

        return relation;
    }

    /**
     * 加载现有实体
     */
    private Map<String, GraphEntity> loadExistingEntities(AppStructTaskDO task) {
        GraphQueryWrapper<GraphEntity> wrapper = new GraphQueryWrapper<>(GraphEntity.class);
        wrapper.addLabels(GraphLabelEnum.STRUCTURED.getLabel());
        wrapper.eq(GraphEntity::getDynamicProperties, "task_id", task.getId());
        List<GraphEntity> graphEntities = graphisRepository.find(wrapper);

        return graphEntities.stream()
                .filter(e -> task.getId().equals(e.getDynamicProperties().get("task_id")))
                .collect(Collectors.toMap(
                        e -> e.getDynamicProperties().get("table_name") + "_" + e.getDynamicProperties().get("name"),
                        Function.identity(), (e1, e2) -> e1));
    }

    /**
     * 处理单表
     */
    private void processSingleTable(AppStructTaskDO task, AppSchemaMappingDO schema,
                                    DbQuery dbQuery, DbQueryProperty dbQueryProperty,
                                    boolean isIncrement, Map<String, GraphEntity> existingMap,
                                    BasicDatasourceRespDTO datasource,
                                    Map<String, List<AppAttributeMappingDO>> attrMap,
                                    List<GraphEntity> entities) {

        // 构建SQL
        String tableName = dbQuery.buildTableNameByDbType(schema.getTableName());
        if (dbQuery.generateCheckTableExistsSQL(dbQueryProperty, schema.getTableName()) == 0) {
            throw new DataQueryException("表不存在: " + schema.getTableName());
        }

        String sql = "SELECT * FROM " + tableName;
        if (isIncrement && schema.getLastDateTime() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sql += " WHERE " + schema.getEntityTimeField() + " > '" + sdf.format(schema.getLastDateTime()) + "'";
        }

        // 分页查询参数
        Integer pageNum = 1;
        boolean hasMoreData = true;

        // 循环分页查询直到没有数据
        while (hasMoreData) {
            PageUtil pageUtil = new PageUtil(pageNum, PAGE_SIZE);
            Integer offset = pageUtil.getOffset();
            // 使用dbQuery的分页方法
            tech.qiantong.qontology.common.database.core.PageResult<Map<String, Object>> rows = dbQuery.queryByPage(sql, offset, PAGE_SIZE);

            if (rows == null || rows.getData() == null || rows.getData().isEmpty()) {
                hasMoreData = false;
                continue;
            }

            for (Map<String, Object> row : rows.getData()) {
                // 获取实体名称值
                String entityNameField = schema.getEntityNameField();
                String primaryKeyField = schema.getPrimaryKey();

                Object entityNameValue = row.get(entityNameField);
                Object primaryKeyValue = row.get(primaryKeyField);

                if (entityNameValue == null || primaryKeyValue == null) {
                    continue;
                }

                // 构建实体唯一键
                String key = schema.getTableName() + "_" + entityNameValue;

                // 检查是否已存在
                if (isIncrement && existingMap.containsKey(key)) continue;
                if (isEntityExists(entities, schema.getTableName(), entityNameValue)) continue;

                // 创建实体
                GraphEntity entity = createGraphEntity(task, datasource, schema, row, attrMap,
                        entityNameValue, primaryKeyValue);
                entities.add(entity);
            }

            // 准备下一页
            pageNum++;

            // 如果当前页数据少于pageSize，说明是最后一页
            if (rows.getData().size() < PAGE_SIZE) {
                hasMoreData = false;
            }
        }
    }

    /**
     * 检查实体是否已存在
     */
    private boolean isEntityExists(List<GraphEntity> entities, String tableName, Object entityName) {
        return entities.stream().anyMatch(e ->
                tableName.equals(e.getDynamicProperties().get("table_name")) &&
                        entityName.equals(e.getDynamicProperties().get("name"))
        );
    }

    /**
     * 加载配置的简单实现
     */
    private List<AppSchemaMappingDO> loadSchemaMappings(Long taskId) {
        AppSchemaMappingPageReqVO req = new AppSchemaMappingPageReqVO();
        req.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        req.setTaskId(taskId);
        PageResult<AppSchemaMappingDO> result = appSchemaMappingMapper.selectPage(req);
        return BeanUtils.toBean(result.getRows(), AppSchemaMappingDO.class);
    }

    /** 加载任务的全部对象类型属性映射。 */
    private Map<String, List<AppAttributeMappingDO>> loadAttributeMappings(Long taskId) {
        AppAttributeMappingPageReqVO req = new AppAttributeMappingPageReqVO();
        req.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        req.setTaskId(taskId);
        PageResult<AppAttributeMappingDO> result = appAttributeMappingMapper.selectPage(req);
        List<AppAttributeMappingDO> list = BeanUtils.toBean(result.getRows(), AppAttributeMappingDO.class);
        return list.stream().collect(Collectors.groupingBy(AppAttributeMappingDO::getTableName));
    }

    /** 加载任务的全部对象类型关系映射。 */
    private Map<String, List<AppRelationMappingDO>> loadRelationMappings(Long taskId) {
        AppRelationMappingPageReqVO req = new AppRelationMappingPageReqVO();
        req.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        req.setTaskId(taskId);
        PageResult<AppRelationMappingDO> result = appRelationMappingMapper.selectPage(req);
        List<AppRelationMappingDO> list = BeanUtils.toBean(result.getRows(), AppRelationMappingDO.class);
        return list.stream().collect(Collectors.groupingBy(AppRelationMappingDO::getTableName));
    }

    /** 加载关系映射的中间表配置。 */
    private Map<Long, List<AppRelationMappingMiddleDO>> loadMiddleMappings() {
        List<AppRelationMappingMiddleDO> list = appRelationMappingMiddleMapper.selectList();
        return list.stream().collect(Collectors.groupingBy(AppRelationMappingMiddleDO::getRelationId));
    }


    /** 查询抽取结果的对象类型属性信息。 */
    @Override
    public AjaxResult getAttributeInformation(List<String> list) {
        List<AppAttributeMappingDO> attributeList = appAttributeMappingMapper.selectList(
                new QueryWrapper<AppAttributeMappingDO>().in("attribute_id", list));
        log.info("------{}", attributeList);
        return AjaxResult.success("获取成功", attributeList);
    }

    /**
     * 获取抽取结果
     *
     * @param taskId
     * @param extractType
     * @return
     */
    @Override
    public AjaxResult selectByTaskId(Long taskId, Integer extractType) {
        AppStructTaskDO taskDO = appStructTaskMapper.selectById(taskId);
        AppGraphVO graphVO = new AppGraphVO();
        graphVO.setEntityId(taskId);
        graphVO.setEntityType(GraphLabelEnum.STRUCTURED.getCode());
        AjaxResult ajaxResult = AjaxResult.success(appGraphService.getGraph(graphVO));
        if (ajaxResult.isSuccess()) {
            Map<String, Object> hashMap = (Map<String, Object>) ajaxResult.get("data");
            hashMap.put("releaseStatus", taskDO.getPublishStatus());
            return AjaxResult.success("", hashMap);
        }
        return ajaxResult;
    }

    /**
     * 发布
     *
     * @param structTaskDO
     * @return
     */
    @Override
    public AjaxResult releaseByTaskId(AppStructTaskDO structTaskDO) {
        appStructTaskMapper.updateById(structTaskDO);
        appNeo4jService.releaseStructExtraction(structTaskDO.getId());
        return AjaxResult.success("发布成功");
    }

    /**
     * 取消发布
     *
     * @param structTaskDO
     * @return
     */
    @Override
    public AjaxResult cancelReleaseByTaskId(AppStructTaskDO structTaskDO) {
        appStructTaskMapper.updateById(structTaskDO);
        appNeo4jService.cancelReleaseStructExtraction(structTaskDO.getId());
        return AjaxResult.success("取消发布成功");
    }

    /**
     * 修改结构化抽取任务
     *
     * @param appStructTask
     * @return
     */
    @Transactional
    @Override
    public AjaxResult updateDataMapping(AppStructTask appStructTask) {
        Long taskId = appStructTask.getTaskId();
        //修改任务数据
        BasicDatasourceRespDTO datasource = basicDatasourceApiService.getDatasourceById(appStructTask.getDataSourceId());
        AppStructTaskDO appStructTaskDO = appStructTaskMapper.selectById(taskId);
//        appStructTaskDO.setStatus(AppTaskStatus.UNEXECUTED.getValue());
        appStructTaskDO.setDescription(appStructTask.getDescription());
        appStructTaskDO.setRemark(appStructTask.getRemark());
        appStructTaskDO.setProjectId(appStructTask.getProjectId());
        appStructTaskDO.setProjectName(appStructTask.getProjectName());
        appStructTaskDO.setProjectVersionId(appStructTask.getProjectVersionId());
        appStructTaskDO.setProjectVersionName(appStructTask.getProjectVersionName());
        appStructTaskDO.setName(appStructTask.getTaskName());
        appStructTaskDO.setUpdateType(appStructTask.getUpdateType());
        appStructTaskDO.setUpdateFrequency(appStructTask.getUpdateFrequency());
        appStructTaskDO.setDatasourceId(appStructTask.getDataSourceId());
        appStructTaskDO.setDatasourceName(datasource.getDatasourceName());
        appStructTaskDO.setUpdateBy(appStructTask.getUpdateBy());
        appStructTaskDO.setUpdaterId(appStructTask.getUpdaterId());
        appStructTaskDO.setUpdateTime(new Date());
        appStructTaskMapper.updateById(appStructTaskDO);
        //删除之前添加的映射关系
        appSchemaMappingMapper.delete("task_id", taskId.toString());//概念
        appAttributeMappingMapper.delete("task_id", taskId.toString());//属性
        //删除关系中间表数据
        List<AppRelationMappingDO> relationMappingDOList =  appRelationMappingMapper.selectList("task_id",taskId.toString());
        if (StringUtils.isNotEmpty(relationMappingDOList)){
            for (AppRelationMappingDO appRelationMappingDO : relationMappingDOList) {
                appRelationMappingMiddleMapper.delete("relation_id", appRelationMappingDO.getId().toString());
            }
        }
        appRelationMappingMapper.delete("task_id", taskId.toString());//关系

        return addMapping(appStructTask, appStructTaskDO);
    }

    /**
     * 添加结构化抽取任务
     *
     * @param appStructTask
     * @return
     */
    @Transactional
    @Override
    public AjaxResult addDataMapping(AppStructTask appStructTask) {
        BasicDatasourceRespDTO datasource = basicDatasourceApiService.getDatasourceById(appStructTask.getDataSourceId());
        // 存储结构化抽取任务
        AppStructTaskDO appStructTaskDO = new AppStructTaskDO();
        appStructTaskDO.setWorkspaceId(appStructTask.getWorkspaceId());
        appStructTaskDO.setProjectId(appStructTask.getProjectId());
        appStructTaskDO.setProjectName(appStructTask.getProjectName());
        appStructTaskDO.setProjectVersionId(appStructTask.getProjectVersionId());
        appStructTaskDO.setProjectVersionName(appStructTask.getProjectVersionName());
        appStructTaskDO.setUpdateStatus(1);
        appStructTaskDO.setName(appStructTask.getTaskName());
        appStructTaskDO.setRemark(appStructTask.getRemark());
        appStructTaskDO.setUpdateType(appStructTask.getUpdateType());
        appStructTaskDO.setUpdateFrequency(appStructTask.getUpdateFrequency());
        appStructTaskDO.setDatasourceId(appStructTask.getDataSourceId());
        appStructTaskDO.setDatasourceName(datasource.getDatasourceName());
        appStructTaskDO.setStatus(AppTaskStatus.UNEXECUTED.getValue());
        appStructTaskDO.setPublishStatus(AppReleaseStatus.UNPUBLISHED.getStatus());
        appStructTaskDO.setValidFlag(false);
        appStructTaskDO.setDelFlag(false);
        appStructTaskDO.setCreateBy(appStructTask.getUpdateBy());
        appStructTaskDO.setUpdateBy(appStructTask.getUpdateBy());
        appStructTaskDO.setCreatorId(appStructTask.getUpdaterId());
        appStructTaskDO.setUpdaterId(appStructTask.getUpdaterId());
//        appStructTaskDO.setPublisherId(appStructTask.getUpdaterId());
//        appStructTaskDO.setPublishTime(new Date());
        appStructTaskDO.setCreateTime(new Date());
        appStructTaskDO.setUpdateTime(new Date());
        appStructTaskMapper.insert(appStructTaskDO);

        return addMapping(appStructTask, appStructTaskDO);
    }

    /**
     * 添加映射关系
     *
     * @param appStructTask
     * @param appStructTaskDO
     * @return
     */
    private AjaxResult addMapping(AppStructTask appStructTask, AppStructTaskDO appStructTaskDO) {
        try {
            List<AppStructTask.TableData> tableData = appStructTask.getTableData();
            tableData.forEach(ext -> {
                //如果未映射, 不处理
                if (!"1".equals(ext.getStatus())) {
                    return;
                }
                AppStructTask.MappingData mappingData = ext.getMappingData();

                //概念映射
                AppSchemaMappingDO schemaMappingDO = new AppSchemaMappingDO();
                schemaMappingDO.setWorkspaceId(appStructTask.getWorkspaceId());
                schemaMappingDO.setTaskId(appStructTaskDO.getId());
                schemaMappingDO.setTableName(ext.getTableName());
                schemaMappingDO.setTableComment(ext.getTableComment());
                schemaMappingDO.setEntityNameField(mappingData.getEntityNameField());
                schemaMappingDO.setPrimaryKey(mappingData.getPrimaryKey());
                schemaMappingDO.setEntityTimeField(mappingData.getEntityTimeField());
                schemaMappingDO.setSchemaId(mappingData.getConcept());
                schemaMappingDO.setSchemaName(mappingData.getConceptName());
                schemaMappingDO.setValidFlag(false);
                schemaMappingDO.setDelFlag(false);
                schemaMappingDO.setCreateBy(appStructTask.getUpdateBy());
                schemaMappingDO.setUpdateBy(appStructTask.getUpdateBy());
                schemaMappingDO.setCreatorId(appStructTask.getUpdaterId());
                schemaMappingDO.setUpdaterId(appStructTask.getUpdaterId());
                schemaMappingDO.setCreateTime(new Date());
                schemaMappingDO.setUpdateTime(new Date());
                appSchemaMappingMapper.insert(schemaMappingDO);

                //属性映射
                List<AppStructTask.Attribute> attributeList = mappingData.getAttributeList();
                attributeList = attributeList.stream()
                        .filter(e -> e.getConceptId() != null)
                        .collect(Collectors.toList());
                ArrayList<AppAttributeMappingDO> attributeMappingDOS = new ArrayList<>();
                attributeList.forEach(e -> {
                    AppAttributeMappingDO appAttributeMappingDO = new AppAttributeMappingDO();
                    appAttributeMappingDO.setWorkspaceId(appStructTask.getWorkspaceId());
                    appAttributeMappingDO.setTaskId(appStructTaskDO.getId());
                    appAttributeMappingDO.setTableName(ext.getTableName());
                    appAttributeMappingDO.setTableComment(ext.getTableComment());//TODO
                    appAttributeMappingDO.setFieldName(e.getField());
                    appAttributeMappingDO.setFieldComment(e.getFieldDescription());
                    appAttributeMappingDO.setAttributeId(e.getConceptId());
                    appAttributeMappingDO.setAttributeName(e.getConceptName());
                    appAttributeMappingDO.setAttributeName(e.getConceptName());
                    appAttributeMappingDO.setValidFlag(false);
                    appAttributeMappingDO.setDelFlag(false);
                    appAttributeMappingDO.setCreateBy(appStructTask.getUpdateBy());
                    appAttributeMappingDO.setUpdateBy(appStructTask.getUpdateBy());
                    appAttributeMappingDO.setCreatorId(appStructTask.getUpdaterId());
                    appAttributeMappingDO.setUpdaterId(appStructTask.getUpdaterId());
                    appAttributeMappingDO.setCreateTime(new Date());
                    appAttributeMappingDO.setUpdateTime(new Date());
                    attributeMappingDOS.add(appAttributeMappingDO);
                });
                if (attributeMappingDOS.size() > 0) {
                    appAttributeMappingMapper.insertBatch(attributeMappingDOS);
                }

                //关系映射
                List<AppStructTask.Relationship> relationshipList = mappingData.getRelationshipList();
                //关系映射添加集合数据
                ArrayList<AppRelationMappingDO> relationMappingDOS = new ArrayList<>();
                ArrayList<AppRelationMappingMiddleDO> relationMappingMiddleDOS = new ArrayList<>();
                if (StringUtils.isNotEmpty(relationshipList)) {
                    relationshipList = relationshipList.stream()
                            .filter(e -> StringUtils.isNotBlank(e.getTableName())
                                    && StringUtils.isNotBlank(e.getFieldName())
                                    && StringUtils.isNotBlank(e.getRelation())
                                    && StringUtils.isNotBlank(e.getRelationTable())
                                    && StringUtils.isNotBlank(e.getRelationField())
                                    && StringUtils.isNotBlank(e.getRelationNameField())
                            ).collect(Collectors.toList());
                    relationshipList.forEach(e -> {
                        if (StringUtils.isBlank(e.getTableName()) || StringUtils.isBlank(e.getFieldName()) || StringUtils.isBlank(e.getRelation())
                                || StringUtils.isBlank(e.getRelationTable()) || StringUtils.isBlank(e.getRelationField())) {
                            return;
                        }
                        AppRelationMappingDO relationMappingDO = new AppRelationMappingDO();
                        relationMappingDO.setWorkspaceId(appStructTask.getWorkspaceId());
                        relationMappingDO.setTaskId(appStructTaskDO.getId());
                        relationMappingDO.setTableName(ext.getTableName());
                        relationMappingDO.setTableComment(ext.getTableComment());
                        //TODO fieldComment 这个值暂时没有
                        relationMappingDO.setFieldComment("");
                        relationMappingDO.setFieldName(e.getFieldName());
                        relationMappingDO.setRelation(e.getRelation());
                        relationMappingDO.setRelationTable(e.getRelationTable());
                        for (AppStructTask.TableData tableDatum : tableData) {
                            if (tableDatum.getTableName().equals(e.getRelationTable())) {
                                relationMappingDO.setRelationTableName(tableDatum.getTableComment());
                            }
                        }
                        relationMappingDO.setRelationField(e.getRelationField());
                        relationMappingDO.setRelationNameField(e.getRelationNameField());
                        relationMappingDO.setValidFlag(false);
                        relationMappingDO.setDelFlag(false);
                        relationMappingDO.setCreateBy(appStructTask.getUpdateBy());
                        relationMappingDO.setUpdateBy(appStructTask.getUpdateBy());
                        relationMappingDO.setCreatorId(appStructTask.getUpdaterId());
                        relationMappingDO.setUpdaterId(appStructTask.getUpdaterId());
                        relationMappingDO.setCreateTime(new Date());
                        relationMappingDO.setUpdateTime(new Date());
                        relationMappingDOS.add(relationMappingDO);

                    });
                }

                // 批量插入关系映射数据，并获取生成的ID
                if (relationMappingDOS.size() > 0) {
                    // 批量插入关系映射数据
                    appRelationMappingMapper.insertBatch(relationMappingDOS);

                    // 处理中间表数据
                    for (int i = 0; i < relationshipList.size(); i++) {
                        AppStructTask.Relationship relationship = relationshipList.get(i);
                        AppRelationMappingDO savedRelationMapping = relationMappingDOS.get(i);
                        Long relationId = savedRelationMapping.getId(); // 获取生成的ID

                        List<AppStructTask.RelationMappingMiddle> relationMappingMiddles = relationship.getRelationMappingMiddle();
                        if (CollectionUtils.isNotEmpty(relationMappingMiddles)) {
                            relationMappingMiddles.forEach(r -> {
                                if (StringUtils.isNotBlank(r.getTableName())
                                        && StringUtils.isNotBlank(r.getTableField())
                                        && StringUtils.isNotBlank(r.getRelationField())) {

                                    AppRelationMappingMiddleDO relationMappingMiddleDO = new AppRelationMappingMiddleDO();
                                    relationMappingMiddleDO.setRelationId(relationId); // 设置关系ID
                                    relationMappingMiddleDO.setTableName(r.getTableName());
                                    relationMappingMiddleDO.setTableField(r.getTableField());
                                    relationMappingMiddleDO.setRelationField(r.getRelationField());
                                    relationMappingMiddleDO.setCreateTime(new Date());
                                    relationMappingMiddleDO.setUpdateTime(new Date());
                                    relationMappingMiddleDO.setCreateBy(appStructTask.getUpdateBy());
                                    relationMappingMiddleDO.setUpdateBy(appStructTask.getUpdateBy());
                                    relationMappingMiddleDOS.add(relationMappingMiddleDO);
                                }
                            });
                        }
                    }

                    // 批量插入中间表数据
                    if (relationMappingMiddleDOS.size() > 0) {
                        appRelationMappingMiddleMapper.insertBatch(relationMappingMiddleDOS);
                    }
                }
            });

            return AjaxResult.success();
        } catch (Exception e) {
            log.info("添加结构化抽取任务异常:{}", e);
            return AjaxResult.error("添加失败: " + e.getMessage());
        }
    }

    /** 分页查询结构化抽取任务。 */
    @Override
    public PageResult<AppStructTaskDO> getAppStructTaskPage(AppStructTaskPageReqVO pageReqVO) {
        return appStructTaskMapper.selectPage(pageReqVO);
    }

    /** 新增结构化抽取任务。 */
    @Override
    public Long createAppStructTask(AppStructTaskSaveReqVO createReqVO) {
        AppStructTaskDO dictType = BeanUtils.toBean(createReqVO, AppStructTaskDO.class);
        appStructTaskMapper.insert(dictType);
        return dictType.getId();
    }

    /** 修改结构化抽取任务。 */
    @Override
    public int updateAppStructTask(AppStructTaskSaveReqVO updateReqVO) throws SchedulerException {
        // 更新结构化抽取任务
        AppStructTaskDO updateObj = BeanUtils.toBean(updateReqVO, AppStructTaskDO.class);
        int result = appStructTaskMapper.updateById(updateObj);
        // 如果更新成功，重新查询数据
        if (result > 0) {
            // 查询更新后的数据
            AppStructTaskDO updatedTask = appStructTaskMapper.selectById(updateObj.getId());

            // 判断如果定时更新状态为启用,则执行定时任务
            if(updatedTask.getUpdateStatus().equals(AppTaskConcurrent.ALLOW.getValue())) {
                // 只处理已执行完成的任务，验证 Cron 表达式是否有效
                if (updatedTask.getStatus().equals(AppTaskStatus.EXECUTED.getValue()) && CronUtils.isValid(updatedTask.getUpdateFrequency())) {
                    try {
                        scheduleTask(updatedTask); // 创建定时任务
                    } catch (SchedulerException | TaskException e) {
                        log.error("定时任务创建失败，任务ID: {}", updatedTask.getId(), e);
                        // 可考虑重试或标记任务为失败
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else {
                // 检查定时任务是否已存在，。删除任务
                if (isScheduleJobExists(updatedTask.getId(),updatedTask.getUpdateFrequency())) {
                    this.deleteJob(updatedTask.getId());
                }
            }
        }
        return result;
    }

    /** 批量删除结构化抽取任务。 */
    @Override
    public int removeAppStructTask(Collection<Long> idList) throws SchedulerException {
        // 批量删除结构化抽取任务
        int result = appStructTaskMapper.deleteBatchIds(idList);

        //删除对应的定时任务
        for (Long id : idList) {
            this.deleteJob(id);
        }
        return result;
    }

    /** 根据编号查询结构化抽取任务。 */
    @Override
    public AppStructTaskDO getAppStructTaskById(Long id) {
        return appStructTaskMapper.selectById(id);
    }

    /** 查询结构化任务及表、属性和关系映射详情。 */
    @Override
    public AjaxResult getInfo(Long id) {
        AppStructTaskDO structTaskDO = appStructTaskMapper.selectById(id);
        AppStructTaskRespVO structTaskRespVO = BeanUtils.toBean(structTaskDO, AppStructTaskRespVO.class);

        //获取任务中关联的概念
        AppSchemaMappingPageReqVO mappingPageReqVO = new AppSchemaMappingPageReqVO();
        mappingPageReqVO.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        mappingPageReqVO.setTaskId(id);
        PageResult<AppSchemaMappingDO> schemaMappingDOPageResult = appSchemaMappingMapper.selectPage(mappingPageReqVO);
        List<AppSchemaMappingRespVO> schemaMappingList = BeanUtils.toBean(schemaMappingDOPageResult.getRows(), AppSchemaMappingRespVO.class);

        //获取任务中关联的属性映射
        AppAttributeMappingPageReqVO attributeMappingPageReqVO = new AppAttributeMappingPageReqVO();
        attributeMappingPageReqVO.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        attributeMappingPageReqVO.setTaskId(id);
        PageResult<AppAttributeMappingDO> attributeMappingDOPageResult = appAttributeMappingMapper.selectPage(attributeMappingPageReqVO);
        List<AppAttributeMappingRespVO> attributeMappingList = BeanUtils.toBean(attributeMappingDOPageResult.getRows(), AppAttributeMappingRespVO.class);

        //获取任务中关联的关系映射
        AppRelationMappingPageReqVO relationMappingPageReqVO = new AppRelationMappingPageReqVO();
        relationMappingPageReqVO.setPageSize(tech.qiantong.qontology.core.page.PageParam.PAGE_SIZE_NONE);
        relationMappingPageReqVO.setTaskId(id);
        PageResult<AppRelationMappingDO> relationMappingDOPageResult = appRelationMappingMapper.selectPage(relationMappingPageReqVO);
        List<AppRelationMappingRespVO> relationMappingList = BeanUtils.toBean(relationMappingDOPageResult.getRows(), AppRelationMappingRespVO.class);


        //获取所有的关系中间表数据
        List<AppRelationMappingMiddleDO> relationMappingMiddleDOList =  appRelationMappingMiddleMapper.selectList();
        if(StringUtils.isNotEmpty(relationMappingMiddleDOList)){
            Map<Long, List<AppRelationMappingMiddleDO>> relationMappingMiddleMap = relationMappingMiddleDOList.stream().collect(Collectors.groupingBy(AppRelationMappingMiddleDO::getRelationId));
            for (AppRelationMappingRespVO appRelationMappingRespVO : relationMappingList) {
                if(relationMappingMiddleMap.get(appRelationMappingRespVO.getId()) != null) {
                    appRelationMappingRespVO.setRelationMappingMiddle(relationMappingMiddleMap.get(appRelationMappingRespVO.getId()));
                }
            }
        }


        //获取导入的表
        List<String> stringList = Stream.concat(
                        schemaMappingList.stream().map(AppSchemaMappingRespVO::getTableName).filter(Objects::nonNull),
                        relationMappingList.stream().map(AppRelationMappingRespVO::getRelationTable).filter(Objects::nonNull)
                )
                .distinct()  // 去重
                .collect(Collectors.toList());  // 收集成 List


        //判断这些表是否有对应概念
        ArrayList<Object> tableMappingList = new ArrayList<>();
        for (String tableName : stringList) {
            boolean status = false;
            Long schemaId = null;
            String schemaName = null;
            String tableComment = null;
            String entityTimeField = null;
            String primaryKey = null;
            String entityNameField = null;
            for (AppSchemaMappingRespVO respVO : schemaMappingList) {
                if (tableName.equals(respVO.getTableName())) {
                    status = true;
                    schemaId = respVO.getSchemaId();
                    schemaName = respVO.getSchemaName();
                    entityTimeField = respVO.getEntityTimeField();
                    primaryKey = respVO.getPrimaryKey();
                    entityNameField = respVO.getEntityNameField();
                    break;
                }
            }
            for (AppRelationMappingRespVO relationMappingRespVO : relationMappingList) {
                if (tableName.equals(relationMappingRespVO.getTableName())) {
                    tableComment = relationMappingRespVO.getTableComment();
                    break;
                } else if (tableName.equals(relationMappingRespVO.getRelationTable())) {
                    tableComment = relationMappingRespVO.getRelationTableName();
                    break;
                }
            }
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("tableName", tableName);
            hashMap.put("tableComment", tableComment);
            hashMap.put("status", status);
            hashMap.put("schemaId", schemaId);
            hashMap.put("schemaName", schemaName);
            hashMap.put("entityTimeField",entityTimeField);
            hashMap.put("primaryKey",primaryKey);
            hashMap.put("entityNameField",entityNameField);

            tableMappingList.add(hashMap);
        }


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("structTask", structTaskRespVO);
        hashMap.put("tableMappingList", tableMappingList);//表
        hashMap.put("schemaMappingList", schemaMappingList);//概念
        hashMap.put("attributeMappingList", attributeMappingList);//属性
        hashMap.put("relationMappingList", relationMappingList);//关系
        return AjaxResult.success("", hashMap);
    }

    /** 查询全部结构化抽取任务。 */
    @Override
    public List<AppStructTaskDO> getAppStructTaskList() {
        return appStructTaskMapper.selectList();
    }

    /** 查询编号对应的结构化抽取任务映射。 */
    @Override
    public ConcurrentHashMap<Long, AppStructTaskDO> getAppStructTaskMap() {
        List<AppStructTaskDO> appStructTaskList = appStructTaskMapper.selectList();
        return appStructTaskList.stream()
                .collect(Collectors.toMap(
                        AppStructTaskDO::getId,
                        appStructTaskDO -> appStructTaskDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing,
                        ConcurrentHashMap::new  // 指定返回的 Map 实现是 ConcurrentHashMap
                ));
    }


    /**
     * 导入结构化抽取任务数据
     *
     * @param importExcelList 结构化抽取任务数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importAppStructTask(List<AppStructTaskRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (AppStructTaskRespVO respVO : importExcelList) {
            try {
                AppStructTaskDO appStructTaskDO = BeanUtils.toBean(respVO, AppStructTaskDO.class);
                Long appStructTaskId = respVO.getId();
                if (isUpdateSupport) {
                    if (appStructTaskId != null) {
                        AppStructTaskDO existingAppStructTask = appStructTaskMapper.selectById(appStructTaskId);
                        if (existingAppStructTask != null) {
                            appStructTaskMapper.updateById(appStructTaskDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + appStructTaskId + " 的结构化抽取任务记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + appStructTaskId + " 的结构化抽取任务记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<AppStructTaskDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", appStructTaskId);
                    AppStructTaskDO existingAppStructTask = appStructTaskMapper.selectOne(queryWrapper);
                    if (existingAppStructTask == null) {
                        appStructTaskMapper.insert(appStructTaskDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + appStructTaskId + " 的结构化抽取任务记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + appStructTaskId + " 的结构化抽取任务记录已存在。");
                    }
                }
            } catch (Exception e) {
                failureNum++;
                String errorMsg = "数据导入失败，错误信息：" + e.getMessage();
                failureMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }
        StringBuilder resultMsg = new StringBuilder();
        if (failureNum > 0) {
            resultMsg.append("很抱歉，导入失败！共 ").append(failureNum).append(" 条数据格式不正确，错误如下：");
            resultMsg.append("<br/>").append(String.join("<br/>", failureMessages));
            throw new ServiceException(resultMsg.toString());
        } else {
            resultMsg.append("恭喜您，数据已全部导入成功！共 ").append(successNum).append(" 条。");
        }
        return resultMsg.toString();
    }



    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() throws Exception {
        AppStructTaskPageReqVO pageReqVO = new AppStructTaskPageReqVO();
        PageResult<AppStructTaskDO> result = appStructTaskMapper.selectPage(pageReqVO);
        //遍历数据，查询定时任务
        for (AppStructTaskDO structTaskDO : result.getList()) {
            // 如果 Cron 表达式为空，跳过当前任务
            if (structTaskDO.getUpdateFrequency() == null || !CronUtils.isValid(structTaskDO.getUpdateFrequency())) {
                continue; // 跳过这个任务，继续处理下一个
            }
            //任务状态是已完成并且定时更新状态的已开启的
            if (structTaskDO.getStatus().equals(AppTaskStatus.EXECUTED.getValue()) && structTaskDO.getUpdateStatus().equals(AppTaskConcurrent.ALLOW.getValue())) {
                this.scheduleTask(structTaskDO);
            }
        }
    }


    /**
     * 获取任务，执行定时数据更新
     *
     * @param appStructTaskDO
     * @return
     */
    public void scheduleTask(AppStructTaskDO appStructTaskDO) throws Exception {
        // 检查定时任务是否已存在，并且cron表达式没有变
        if (isScheduleJobExists(appStructTaskDO.getId(),appStructTaskDO.getUpdateFrequency())) {
            log.info("定时任务已存在，任务ID: {}", appStructTaskDO.getId());
            return;
        }
        SysJob job = new SysJob();
        job.setJobId(appStructTaskDO.getId());
        job.setJobName(appStructTaskDO.getName() + "数据更新");
        //判断是全量更新还是增量更新
        if (appStructTaskDO.getUpdateType().equals(AppTaskUpdateType.FULLUPDATE.getValue())) {
            job.setInvokeTarget("appStructTaskServiceImpl.getAppStructTaskFullUpdate(" + appStructTaskDO.getId() + ")");
        }
        if (appStructTaskDO.getUpdateType().equals(AppTaskUpdateType.INCREMENTUDATE.getValue())) {
            job.setInvokeTarget("appStructTaskServiceImpl.getAppStructTaskIncrementUpdate(" + appStructTaskDO.getId() + ")");
        }
        job.setCronExpression(appStructTaskDO.getUpdateFrequency());
        job.setMisfirePolicy(ScheduleConstants.MISFIRE_IGNORE_MISFIRES);
        job.setConcurrent(AppTaskConcurrent.FORBID.getValue().toString());
        job.setJobGroup("DEFAULT");
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        //创建定时任务
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 检查定时任务是否已存在
     */
    private boolean isScheduleJobExists(Long taskId,String cronExpression) {
        try {

            // 检查任务是否已存在
            JobKey jobKey = JobKey.jobKey("TASK_CLASS_NAME" + taskId.toString(), "DEFAULT");
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            // 创建 TriggerKey 来查找任务触发器
            TriggerKey triggerKey = TriggerKey.triggerKey("TASK_CLASS_NAME" + taskId.toString(), "DEFAULT");
            Trigger trigger = scheduler.getTrigger(triggerKey);

//            // 检查是否有对应的触发器
//            if (trigger != null) {
//                log.info("获取到任务的触发器，Cron表达式为: {}", ((CronTrigger) trigger).getCronExpression());
//            } else {
//                log.warn("未找到对应的触发器，任务ID: {}", taskId);
//            }

            //用于匹配cron表达式是否更新
            String cron = trigger != null ?  ((CronTrigger) trigger).getCronExpression() : null;

            if (jobDetail != null && trigger != null && cron.equals(cronExpression)) {
                log.info("获取到任务的 JobDetail: {}", jobDetail.getKey());
                return true; // 任务存在或这cron不一致
            } else {
                log.warn("未找到对应的 JobDetail，任务ID: {}", taskId);
                return false; // 任务不存在
            }
        } catch (SchedulerException e) {
            log.error("检查定时任务是否存在时发生错误", e);
            return false;
        }
    }


    //执行全量更新的方法
    /** 执行结构化任务全量更新。 */
    public void getAppStructTaskFullUpdate(Integer taskId) {
        this.execStructTask(String.valueOf(taskId));
    }

    //执行增量更新的方法
    /** 执行结构化任务增量更新。 */
    public void getAppStructTaskIncrementUpdate(Integer taskId){
        this.execStructTaskIncrementUpdate(String.valueOf(taskId));
    }

    //删除指定的触发器和任务
    /** 删除结构化任务的定时调度。 */
    public void deleteJob(Long taskId) throws SchedulerException {
        // 删除触发器
        TriggerKey triggerKey = TriggerKey.triggerKey("TASK_CLASS_NAME" + taskId.toString(), "DEFAULT");
        scheduler.unscheduleJob(triggerKey);

        // 删除任务
        JobKey jobKey = JobKey.jobKey("TASK_CLASS_NAME" + taskId.toString(), "DEFAULT");
        scheduler.deleteJob(jobKey);

    }

    /**
     * 定时任务立即执行一次
     *
     * @param appStructTask
     * @return
     */
    @Override
    public void  runStructTask(AppStructTask appStructTask) {
        if (StringUtils.isNull(appStructTask.getTaskId())) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        AppStructTaskDO appStructTaskDO = appStructTaskMapper.selectById(appStructTask.getTaskId());
        if (ReleaseStatus.PUBLISHED.getValue().equals(appStructTaskDO.getPublishStatus())) {
            throw new RuntimeException("已发布状态不能重新执行抽取");
        }
        appStructTaskDO.setStatus(AppTaskStatus.INPROGRESS.getValue());
        appStructTaskMapper.updateById(appStructTaskDO);
        // ✅ 提交异步任务（不等待）
        CompletableFuture.runAsync(() -> {
            // 判断是全量更新还是增量更新
            if (appStructTask.getUpdateType().equals(AppTaskUpdateType.FULLUPDATE.getValue())) {
                this.execStructTask(String.valueOf(appStructTask.getTaskId()));
            } else if (appStructTask.getUpdateType().equals(AppTaskUpdateType.INCREMENTUDATE.getValue())) {
                this.execStructTaskIncrementUpdate(String.valueOf(appStructTask.getTaskId()));
            } else {
                throw new IllegalArgumentException("无效的更新类型");
            }
        });

    }

}

