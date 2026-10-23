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

package tech.qiantong.qontology.module.app.service.graph.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.common.database.DataSourceFactory;
import tech.qiantong.qontology.common.database.DbQuery;
import tech.qiantong.qontology.common.database.constants.DbQueryProperty;
import tech.qiantong.qontology.common.database.core.DbColumn;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.graphis.core.wrapper.query.GraphQueryWrapper;
import tech.qiantong.qontology.graphis.core.wrapper.upsert.GraphUpsertWrapper;
import tech.qiantong.qontology.graphis.domain.GraphEntity;
import tech.qiantong.qontology.graphis.domain.GraphRelationship;
import tech.qiantong.qontology.graphis.enums.GraphLabelEnum;
import tech.qiantong.qontology.graphis.repository.GraphisRepository;
import tech.qiantong.qontology.graphis.utils.Convert;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphRelationshipSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.AppGraphVO;
import tech.qiantong.qontology.module.app.controller.admin.graph.vo.DeleteNodeAttributeVO;
import tech.qiantong.qontology.module.app.enums.ReleaseStatus;
import tech.qiantong.qontology.module.app.service.graph.AppGraphService;
import tech.qiantong.qontology.module.basic.api.dto.datasource.BasicDatasourceRespDTO;
import tech.qiantong.qontology.module.basic.api.service.asset.IBasicDatasourceApiService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 图谱
 */
@Slf4j
@Service
public class AppGraphServiceImpl implements AppGraphService {

    @Resource
    private GraphisRepository graphisRepository;

    @Resource
    private IBasicDatasourceApiService basicDatasourceApiService;

    @Resource
    private DataSourceFactory dataSourceFactory;

    /**
     * 获取图谱数据
     *
     * @param appGraphVO
     * @return
     */
    @Override
    public Map<String, Object> getGraph(AppGraphVO appGraphVO) {
        GraphQueryWrapper<GraphEntity> build = new GraphQueryWrapper<>(GraphEntity.class);
        GraphLabelEnum graphLabelEnum = GraphLabelEnum.get(appGraphVO.getEntityType());
        if (appGraphVO.getReleaseStatus() != null) {
            build.eq(GraphEntity::getReleaseStatus, appGraphVO.getReleaseStatus());
        }
        if (graphLabelEnum != null) {
            if (GraphLabelEnum.DYNAMICENTITY.eq(graphLabelEnum.getCode())) {
                // 发布状态
                build.eq(GraphEntity::getReleaseStatus, 1);
            } else {
                build.addLabels(graphLabelEnum.getLabel());
                if (appGraphVO.getEntityId() != null) {
                    build.eq(GraphEntity::getDynamicProperties, graphLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
                }
            }
        }
        if (appGraphVO.getGraphId() != null) {
            build.eq(GraphEntity::getGraphId, appGraphVO.getGraphId());
        }
        List<GraphEntity> graphEntitiyList = graphisRepository.find(build);
        JSONObject entityJSONObject = Convert.toEntityJSONObject(graphEntitiyList);
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("entities", entityJSONObject.get("entities"));
        hashMap.put("relationships", entityJSONObject.get("relationships"));
        return hashMap;
    }

    @Override
    public PageResult<JSONObject> getGraphPage(AppGraphPageReqVO appGraphVO) {
        GraphQueryWrapper<GraphEntity> build = new GraphQueryWrapper<>(GraphEntity.class);
        GraphLabelEnum graphLabelEnum = GraphLabelEnum.get(appGraphVO.getEntityType());
        if (appGraphVO.getReleaseStatus() != null) {
            build.eq(GraphEntity::getReleaseStatus, appGraphVO.getReleaseStatus());
        }
        if (graphLabelEnum != null) {
            if (GraphLabelEnum.DYNAMICENTITY.eq(graphLabelEnum.getCode())) {
                build.eq(GraphEntity::getReleaseStatus, ReleaseStatus.PUBLISHED.getValue()); //发布状态
            } else {
                build.addLabels(graphLabelEnum.getLabel());
                if (appGraphVO.getEntityId() != null) {
                    build.eq(GraphEntity::getDynamicProperties, graphLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
                }
            }
        }
        if (StringUtils.isNotEmpty(appGraphVO.getName())) {
            build.like(GraphEntity::getName, appGraphVO.getName());
        }
        if (StringUtils.isNotEmpty(appGraphVO.getSchema())) {
            build.like(GraphEntity::getDynamicProperties, "schema_name", appGraphVO.getSchema());
        }
        if (appGraphVO.getGraphId() != null) {
            build.eq(GraphEntity::getGraphId, appGraphVO.getGraphId());
        }
        build.page(appGraphVO.getPageNum(), appGraphVO.getPageSize());
        PageResult<GraphEntity> page = graphisRepository.findPage(build);
        JSONObject entityJSONObject = Convert.toEntityJSONObject(page.getList());
        JSONArray jsonArray = entityJSONObject.getJSONArray("entities");
        // 根据时间倒序排序
        JSONArray sortedArray = jsonArray.stream()
                .map(obj -> (JSONObject) obj)
                .sorted(Comparator.comparing(
                        (JSONObject obj) -> obj.getString("createTime"),
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toCollection(JSONArray::new));
        return new PageResult<>(sortedArray.toList(JSONObject.class), page.getTotal());
    }

    /**
     * 根据实体，获取对应的关系链数据
     *
     * @param appGraphVO
     * @return
     */
    @Override
    public AjaxResult getDynamicEntityList(AppGraphVO appGraphVO) {
        // 获取关系链
        GraphQueryWrapper<GraphEntity> queryWrapper = new GraphQueryWrapper<>(GraphEntity.class);
        queryWrapper.in(GraphEntity::getId, appGraphVO.getEntityIds());
        List<GraphEntity> dynamicEntityList = graphisRepository.relationChain(queryWrapper, 5);
        JSONObject jsonObject = Convert.toEntityJSONObject(dynamicEntityList);
        return AjaxResult.success(jsonObject);
    }

    @Override
    public Map<String, Object> getNodeDatasource(String nodeId) {
        GraphEntity entity = graphisRepository.findEntityByIds(Collections.singleton(nodeId)).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("图谱实体不存在"));
        Map<String, Object> properties = entity.getDynamicProperties();
        Long datasourceId = toLong(properties.get("database_id"));
        String tableName = Objects.toString(properties.get("table_name"), null);
        String primaryKey = Objects.toString(properties.get("primary_key"), null);
        Object primaryKeyValue = properties.get("primary_key_data");
        if (datasourceId == null || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(primaryKey)
                || primaryKeyValue == null) {
            return emptyDatasource();
        }

        BasicDatasourceRespDTO datasource = basicDatasourceApiService.getDatasourceById(datasourceId);
        if (datasource == null) {
            return emptyDatasource();
        }
        DbQueryProperty property = new DbQueryProperty(datasource.getDatasourceType(), datasource.getIp(),
                datasource.getPort(), datasource.getDatasourceConfig());
        DbQuery dbQuery = dataSourceFactory.createDbQuery(property);
        try {
            List<DbColumn> columns = dbQuery.getTableColumns(property, tableName);
            DbColumn primaryKeyColumn = columns.stream()
                    .filter(column -> primaryKey.equalsIgnoreCase(column.getColName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("源表中不存在实体主键字段"));
            Map<String, Object> params = Collections.singletonMap("primaryKeyValue", primaryKeyValue);
            Map<String, Object> record = dbQuery.queryOne(
                    "SELECT * FROM " + dbQuery.buildTableNameByDbType(tableName)
                            + " WHERE " + primaryKeyColumn.getColName() + " = :primaryKeyValue",
                    params, 0);

            Map<String, Object> result = emptyDatasource();
            Map<String, Object> database = (Map<String, Object>) result.get("database");
            database.put("type", datasource.getDatasourceType());
            database.put("host", datasource.getIp() + ":" + datasource.getPort());
            database.put("databaseName", property.getDbName());
            database.put("tableName", tableName);
            if (record != null) {
                List<Map<String, Object>> tableData = new ArrayList<>();
                record.forEach((field, value) -> tableData.add(Map.of("field", field, "value", value == null ? "" : value)));
                result.put("tableData", tableData);
            }
            return result;
        } finally {
            dbQuery.close();
        }
    }

    private Map<String, Object> emptyDatasource() {
        Map<String, Object> result = new HashMap<>();
        result.put("database", new HashMap<String, Object>());
        result.put("tableData", new ArrayList<>());
        return result;
    }

    private Long toLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String string && StringUtils.isNotEmpty(string)) {
            return Long.valueOf(string);
        }
        return null;
    }

    /**
     * 统计 (实体数量,关系类型数量,三元组数量)
     *
     * @param appGraphVO
     * @return
     */
    @Override
    public Map<String, Object> getGraphDataStatistics(AppGraphVO appGraphVO) {
        List<GraphEntity> entitieList = graphisRepository.findAll();
        JSONObject entityJSONObject = Convert.toEntityJSONObject(entitieList);
        JSONArray entities = entityJSONObject.getJSONArray("entities");
        JSONArray relationships = entityJSONObject.getJSONArray("relationships");
        long uniqueRelationTypeCount = relationships.stream()
                .map(relationship -> ((JSONObject) relationship).getString("relationType"))
                .distinct()
                .count();

        Map<String, Object> result = Maps.newHashMap();
        result.put("entityCount", entities.size());   // 实体数量
        result.put("relationshipCount", uniqueRelationTypeCount);  // 去重后的关系类型数量
        result.put("tripletCount", relationships.size());  // 三元组数量
        return result;
    }

    /**
     * 新增实体
     *
     * @return
     */
    @Override
    public Boolean addNode(JSONArray jsonArray) {
        Set<String> idSet = jsonArray.stream()
                .map(obj -> (JSONObject) obj)
                .filter(jsonObject -> jsonObject.containsKey("id") && jsonObject.get("id") != null)
                .map(jsonObject -> jsonObject.getString("id"))
                .collect(Collectors.toSet());
        List<GraphEntity> graphEntityList = graphisRepository.findEntityByIds(idSet);
        Map<String, GraphEntity> entityMap = graphEntityList.stream()
                .collect(Collectors.toMap(GraphEntity::getId, entity -> entity));
        List<GraphEntity> entityList = Lists.newArrayList();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String id = jsonObject.getString("id");
            GraphEntity graphEntity;
            if (id != null) {
                graphEntity = entityMap.get(id);
            } else {
                graphEntity = new GraphEntity();
                graphEntity.setReleaseStatus(ReleaseStatus.UNPUBLISHED.getValue());
                graphEntity.setCreatorId(jsonObject.getLong("creatorId"));
                graphEntity.setCreateTime(jsonObject.getDate("createTime"));
                graphEntity.setCreateBy(jsonObject.getString("createBy"));
                jsonObject.remove("creatorId");
                jsonObject.remove("createTime");
                jsonObject.remove("createBy");
            }
            // 标签
            graphEntity.addLabels(GraphLabelEnum.get(jsonObject.getInteger("entityType")).getLabel());
            // 固定字段
            graphEntity.setName(jsonObject.getString("name"));
            graphEntity.setGraphId(jsonObject.getLong("graphId"));
            jsonObject.remove("name");
            jsonObject.remove("graphId");

            // 动态属性
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                if (!"id".equals(entry.getKey())) {
                    graphEntity.putDynamicProperties(Convert.camelToSnake(entry.getKey()), entry.getValue());
                }
            }
            entityList.add(graphEntity);
        }
        graphisRepository.saveAll(entityList);
        return true;
    }

    /**
     * 新增三元组 TODO
     * 根据起点id和结点id创建关系
     *
     * @param graphRelationshipSaveReqVOList
     * @return
     */
    @Override
    public Boolean addTripletRel(List<AppGraphRelationshipSaveReqVO> graphRelationshipSaveReqVOList) {
        // 取出所有的实体id
        Set<String> idSet = graphRelationshipSaveReqVOList.stream()
                .flatMap(user -> Stream.of(user.getEntityId1(), user.getEntityId2()))
                .collect(Collectors.toSet());
        // 查询出所有的实体
        List<GraphEntity> graphEntityList = graphisRepository.findEntityByIds(idSet);
        Map<String, GraphEntity> graphEntityMap = graphEntityList.stream()
                .collect(Collectors.toMap(GraphEntity::getId, graphEntity -> graphEntity));
        List<String> relationshipIds = graphRelationshipSaveReqVOList.stream()
                .map(AppGraphRelationshipSaveReqVO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!relationshipIds.isEmpty()) {
            // 删除关系
            graphisRepository.deleteRelationshipByIds(relationshipIds);
        }
        // 新增/更新关系
        graphRelationshipSaveReqVOList.forEach(appGraphRelationshipSaveReqVO -> {
            GraphEntity graphEntity1 = graphEntityMap.get(appGraphRelationshipSaveReqVO.getEntityId1());
            GraphEntity graphEntity2 = graphEntityMap.get(appGraphRelationshipSaveReqVO.getEntityId2());
            Collection<GraphRelationship> graphRelationships = graphEntity1 != null ? graphEntity1.getGraphRelationships() : null;
            // 如果graphEntity1为null，跳过当前循环
            if (graphEntity1 == null) {
                return; // 跳过当前元素，继续处理下一个
            }
            if (graphRelationships == null) {
                graphRelationships = Lists.newArrayList();
            } else {
                graphRelationships.removeIf(e -> relationshipIds.contains(e.getId()));
            }
            GraphRelationship graphEntityRelationship = new GraphRelationship();
            graphEntityRelationship.setName(appGraphRelationshipSaveReqVO.getRelationshipType());
            graphEntityRelationship.setStartNode(graphEntity1);
            graphEntityRelationship.setEndNode(graphEntity2);
            graphRelationships.add(graphEntityRelationship);
            graphEntity1.setGraphRelationships(graphRelationships);
        });
        graphisRepository.saveAll(graphEntityList);
        return true;
    }

    /**
     * 根据节点id和属性的id删除属性
     *
     * @param deleteNodeAttributeVO
     * @return
     */
    @Override
    public AjaxResult deleteNodeAttributeById(DeleteNodeAttributeVO deleteNodeAttributeVO) {
        graphisRepository.deleteEntityAttributeById(deleteNodeAttributeVO.getNodeId().toString(), deleteNodeAttributeVO.getAttributeKey());
        return AjaxResult.success("操作成功");
    }

    /**
     * 发布 / 取消发布
     *
     * @return
     */
    @Override
    public AjaxResult updateReleaseStatus(AppGraphVO appGraphVO) {
        GraphUpsertWrapper<GraphEntity> build = new GraphUpsertWrapper<>(GraphEntity.class);
        GraphLabelEnum graphLabelEnum = GraphLabelEnum.get(appGraphVO.getEntityType());
        if (graphLabelEnum != null) {
            build.addLabels(graphLabelEnum.getLabel());
            build.eq(GraphEntity::getDynamicProperties, graphLabelEnum.getEntityIdName(), appGraphVO.getEntityId());
            //修改属性内容
            GraphEntity graphEntity = new GraphEntity();
            graphEntity.setReleaseStatus(appGraphVO.getReleaseStatus());
            graphEntity.addLabels(graphLabelEnum.getLabel());
            graphisRepository.update(graphEntity, build);
        }
        return AjaxResult.success("操作成功");
    }

    /**
     * 根据节点id删除对应的节点
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult deleteNode(String id) {
        graphisRepository.deleteEntityById(id);
        return AjaxResult.success("删除成功");
    }

    @Override
    public Boolean deleteNodeByIds(String[] ids) {
        List<String> idList = Arrays.asList(ids);
        graphisRepository.deleteEntityByIds(idList);
        return true;
    }


    /**
     * 根据关系id删除关系
     *
     * @param id
     * @return
     */
    @Override
    public AjaxResult deleteRelationshipById(String id) {
        graphisRepository.deleteRelationshipById(id);
        return AjaxResult.success("删除成功");
    }

    /**
     * 根据关系ids删除关系
     *
     * @param ids
     * @return
     */
    @Override
    public AjaxResult deleteRelationshipsByIds(String[] ids) {
        List<String> idList = Arrays.asList(ids);
        graphisRepository.deleteRelationshipByIds(idList);
        return AjaxResult.success("删除成功");
    }

    @Override
    public Map<String, Object> getTableGraph(AppGraphVO appGraphVO) {
        Integer pageSize = appGraphVO.getPageSize();
        Integer pageNum = appGraphVO.getPageNum();
        appGraphVO.setReleaseStatus(ReleaseStatus.PUBLISHED.getValue());
        Map<String, Object> graph = getGraph(appGraphVO);
        JSONArray relationships = JSONArray.from(graph.get("relationships"));
        // 先根据条件过滤
        List relation = tableGraphFilter(appGraphVO, relationships);
        JSONArray result = JSONArray.parseArray(JSON.toJSONString(relation));

        // 手动分页
        int total = result.size();
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        // 截取分页后的子列表（处理超出范围的情况）
        if (startIndex >= total) {
            result = new JSONArray(); // 页码超出时返回空列表
        } else {
            result = new JSONArray(result.subList(startIndex, endIndex));
        }

        graph.put("relationships", result);
        graph.put("entities", null);
        graph.put("total", total);
        return graph;
    }

    // 映射概念name
//    public String getSchemaName(Long schemaId, List<ExtSchemaRespDTO> schemas) {
//        // 查询当前图谱所有概念数据
//        for (ExtSchemaRespDTO schema : schemas) {
//            if (schema.getId().equals(schemaId)) {
//                return schema.getName();
//            }
//        }
//        return null;
//    }

    // 条件过滤方法
    public List tableGraphFilter(AppGraphVO appGraphVO, JSONArray array) {
        Predicate p = (item) -> item != null && !"".equals(item);
        String startEntityName = appGraphVO.getStartEntityName();
        String endEntityName = appGraphVO.getEndEntityName();
        String startSchemaName = appGraphVO.getStartSchemaName();
        String endSchemaName = appGraphVO.getEndSchemaName();
        String relationName = appGraphVO.getRelationName();
        String beginTime = appGraphVO.getBeginTime();
        String endTime = appGraphVO.getEndTime();

        LocalDateTime begin;
        LocalDateTime end;
        if (beginTime != null && !beginTime.isEmpty()) {
            begin = LocalDateTime.parse(beginTime + "T00:00:00");  // 设置为开始时刻
        } else {
            begin = null;
        }
        if (endTime != null && !endTime.isEmpty()) {
            end = LocalDateTime.parse(endTime + "T23:59:59");  // 设置为结束时刻
        } else {
            end = null;
        }

        List resultList = array.stream().filter(item -> {
            JSONObject relation = JSONObject.from(item);
            boolean allMatch = true;

            if (allMatch && p.test(startEntityName)) {
                Object startName = relation.get("startName");
                if (!p.test(startName) || !startName.toString().contains(startEntityName.trim())) {
                    allMatch = false;
                }
            }

            if (allMatch && p.test(endEntityName)) {
                Object endName = relation.get("endName");
                if (!p.test(endName) || !endName.toString().contains(endEntityName.trim())) {
                    allMatch = false;
                }
            }

            if (allMatch && p.test(startSchemaName)) {
                Object schemaName = relation.get("startSchemaName");
                if (!p.test(schemaName) || !schemaName.toString().contains(startSchemaName.trim())) {
                    allMatch = false;
                }
            }

            if (allMatch && p.test(endSchemaName)) {
                Object schemaName = relation.get("endSchemaName");
                if (!p.test(schemaName) || !schemaName.toString().contains(endSchemaName.trim())) {
                    allMatch = false;
                }
            }

            if (allMatch && p.test(relationName)) {
                Object relationType = relation.get("relationType");
                if (!p.test(relationType) || !relationType.toString().contains(relationName.trim())) {
                    allMatch = false;
                }
            }

            // 时间校验
            if (allMatch && p.test(beginTime) && p.test(endTime)) {
                Date createTime = (Date) relation.get("createTime");
                if (p.test(createTime)) {
                    LocalDateTime localDateTime = createTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                    LocalDateTime parse = LocalDateTime.parse(createTime.toString(), dateFormatter);
                    if (localDateTime.isAfter(end) || localDateTime.isBefore(begin)) {
                        allMatch = false;
                    }
                } else {
                    allMatch = false;
                }
            }


            return allMatch;
        }).collect(Collectors.toList());
        return resultList;
    }
}
