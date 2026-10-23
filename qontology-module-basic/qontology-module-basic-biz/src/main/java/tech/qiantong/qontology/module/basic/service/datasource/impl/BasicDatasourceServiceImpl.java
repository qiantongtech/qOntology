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

package tech.qiantong.qontology.module.basic.service.datasource.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.database.DataSourceFactory;
import tech.qiantong.qontology.common.database.DbQuery;
import tech.qiantong.qontology.common.database.constants.DbQueryProperty;
import tech.qiantong.qontology.common.database.core.DbColumn;
import tech.qiantong.qontology.common.database.core.DbTable;
import tech.qiantong.qontology.common.database.exception.DataQueryException;
import tech.qiantong.qontology.common.utils.JSONUtils;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.dto.datasource.DatasourceCreaTeTableReqDTO;
import tech.qiantong.qontology.module.basic.api.dto.model.BasicModelColumnReqDTO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourcePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourceRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourceSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicSearchTablePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.datasource.BasicDatasourceDO;
import tech.qiantong.qontology.module.basic.dal.mapper.datasource.BasicDatasourceMapper;
import tech.qiantong.qontology.module.basic.service.datasource.IBasicDatasourceService;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import tech.qiantong.qontology.redis.service.IRedisService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据源Service业务层处理
 *
 * @author lhs
 * @date 2025-01-21
 */
@Slf4j
@Service("dataSourceService")
@Transactional(rollbackFor = Exception.class)
public class BasicDatasourceServiceImpl extends ServiceImpl<BasicDatasourceMapper, BasicDatasourceDO>
        implements IBasicDatasourceService {

    @Autowired
    private DataSourceFactory dataSourceFactory;

    private static String tempDataSource;


    /**
     * 查询数据资产的数据源连接信息
     *
     * @param daAsset
     * @return
     */
    @Override
    public List<BasicDatasourceDO> getDataSourceByAsset(BasicDatasourceRespVO daAsset) {
        return baseMapper.getDataSourceByAsset();
    }

    @Override
    public PageResult<BasicDatasourceDO> getDmDatasourcePage(BasicDatasourcePageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    @Override
    public List<BasicDatasourceDO> getDaDatasourceList(BasicDatasourcePageReqVO reqVO) {
        LambdaQueryWrapperX<BasicDatasourceDO> daDatasourceDOLambdaQueryWrapperX = new LambdaQueryWrapperX<>();
        daDatasourceDOLambdaQueryWrapperX.likeIfPresent(BasicDatasourceDO::getDatasourceName, reqVO.getDatasourceName())
                .eqIfPresent(BasicDatasourceDO::getDatasourceType, reqVO.getDatasourceType())
                .eqIfPresent(BasicDatasourceDO::getDatasourceConfig, reqVO.getDatasourceConfig())
                .eqIfPresent(BasicDatasourceDO::getIp, reqVO.getIp())
                .eqIfPresent(BasicDatasourceDO::getPort, reqVO.getPort())
                .eqIfPresent(BasicDatasourceDO::getListCount, reqVO.getListCount())
                .eqIfPresent(BasicDatasourceDO::getSyncCount, reqVO.getSyncCount())
                .eqIfPresent(BasicDatasourceDO::getDataSize, reqVO.getDataSize())
                .eqIfPresent(BasicDatasourceDO::getDescription, reqVO.getDescription())
                .eqIfPresent(BasicDatasourceDO::getCreateTime, reqVO.getCreateTime());

        return baseMapper.selectList(daDatasourceDOLambdaQueryWrapperX);
    }

    @Override
    public Long createDaDatasource(BasicDatasourceSaveReqVO createReqVO) {
        BasicDatasourceDO dictType = BeanUtils.toBean(createReqVO, BasicDatasourceDO.class);
        baseMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateDaDatasource(BasicDatasourceSaveReqVO updateReqVO) {
        // 相关校验

        // 更新数据源
        BasicDatasourceDO updateObj = BeanUtils.toBean(updateReqVO, BasicDatasourceDO.class);
        return baseMapper.updateById(updateObj);
    }

    @Override
    public int removeDaDatasource(Collection<Long> idList) {
        // 批量删除数据源
        return baseMapper.deleteBatchIds(idList);
    }

    @Override
    public BasicDatasourceDO getDaDatasourceById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<BasicDatasourceDO> getDaDatasourceList() {
        return baseMapper.selectList();
    }

    @Override
    public Map<Long, BasicDatasourceDO> getDaDatasourceMap() {
        List<BasicDatasourceDO> daDatasourceList = baseMapper.selectList();
        return daDatasourceList.stream()
                .collect(Collectors.toMap(
                        BasicDatasourceDO::getId,
                        daDatasourceDO -> daDatasourceDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    @Override
    public AjaxResult clientsTest(Long id) {
        DbQuery dbQuery = this.buildDbQuery(id);
        if (dbQuery.valid()) {
            dbQuery.close();
            return AjaxResult.success("数据库连接成功");
        }
        dbQuery.close();
        return AjaxResult.error("数据库连接失败");

    }

    public DbQuery buildDbQuery(Long id) {
        BasicDatasourceDO daDatasourceBy = this.getDaDatasourceById(id);
        if (daDatasourceBy == null) {
            throw new DataQueryException("数据源详情信息查询失败");
        }
        DbQueryProperty dbQueryProperty = new DbQueryProperty(
                daDatasourceBy.getDatasourceType(),
                daDatasourceBy.getIp(),
                daDatasourceBy.getPort(),
                daDatasourceBy.getDatasourceConfig()
        );
        return dataSourceFactory.createDbQuery(dbQueryProperty);
    }

    /**
     * @param dmSearchTablePageReqVo 数据源id
     * @return
     */
    @Override
    public List<DbTable> getDbTables(BasicSearchTablePageReqVO dmSearchTablePageReqVo) {
        DbQuery dbQuery = null;
        try {
            BasicDatasourceDO daDatasourceBy = this.getDaDatasourceById(dmSearchTablePageReqVo.getDataSourceId());
            if (daDatasourceBy == null) {
                throw new DataQueryException("数据源详情信息查询失败");
            }

            DbQueryProperty dbQueryProperty = new DbQueryProperty(daDatasourceBy.getDatasourceType()
                    , daDatasourceBy.getIp(), daDatasourceBy.getPort(), daDatasourceBy.getDatasourceConfig());
            dbQuery = dataSourceFactory.createDbQuery(dbQueryProperty);
            if (!dbQuery.valid()) {
                throw new DataQueryException("数据库连接失败");
            }
            List<DbTable> tables = dbQuery.getTables(dbQueryProperty);

            // 如果有关键词，则进行模糊匹配；否则返回全部
            if (StringUtils.isNotEmpty(dmSearchTablePageReqVo.getTableName())) {
                tables = tables.stream()
                        .filter(table -> table.getTableName().contains(dmSearchTablePageReqVo.getTableName()))
                        .collect(Collectors.toList());
            }

            return tables;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("数据库连接失败");
        } finally {
            dbQuery.close();
        }
    }

    /**
     * @param id        数据源id
     * @param tableName 表名称
     * @return
     */
    @Override
    public List<DbColumn> getDbTableColumns(Long id, String tableName) {
        DbQuery dbQuery = null;
        try {
//            if (StringUtils.isEmpty(tableName)) {
//                throw new DataQueryException("表名不能为空");
//            }

            BasicDatasourceDO daDatasourceBy = this.getDaDatasourceById(id);
            if (daDatasourceBy == null) {
                throw new DataQueryException("数据源详情信息查询失败");
            }

            DbQueryProperty dbQueryProperty = new DbQueryProperty(daDatasourceBy.getDatasourceType()
                    , daDatasourceBy.getIp(), daDatasourceBy.getPort(), daDatasourceBy.getDatasourceConfig());
            dbQuery = dataSourceFactory.createDbQuery(dbQueryProperty);
            if (!dbQuery.valid()) {
                throw new DataQueryException("数据库连接失败");
            }

            List<DbColumn> tableColumns = dbQuery.getTableColumns(dbQueryProperty, tableName);
            return tableColumns;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new RuntimeException("数据库连接失败");
        } finally {
            dbQuery.close();
        }
    }

    /**
     * 获取数据表里面的数据字段
     *
     * @param jsonObject 数据源id和数据表
     * @return
     */
    @Override
    public List<BasicModelColumnReqDTO> getColumnsList(JSONObject jsonObject) {
        List<BasicModelColumnReqDTO> modelIdColumnList = new ArrayList<>();
        Boolean isOld = jsonObject.getString("isOld") == null ? null : Boolean.valueOf(jsonObject.getString("isOld"));
        if (isOld != null && !isOld && jsonObject.getString("modelId") != null) {
//            modelIdColumnList = dpModelApiService.getModelIdColumnList(Long.valueOf(jsonObject.getStr("modelId")));
        }
        if (modelIdColumnList.size() > 0) {
            List<BasicModelColumnReqDTO> columnReqDTOList = BeanUtils.toBean(modelIdColumnList, BasicModelColumnReqDTO.class);
            return columnReqDTOList;
        }
        List<DbColumn> columnList = this.getDbTableColumns(Long.valueOf(jsonObject.getString("id")), jsonObject.getString("tableName"));
        List<BasicModelColumnReqDTO> columnReqDTOList = new ArrayList<>();
        for (DbColumn column : columnList) {
            BasicModelColumnReqDTO basicModelColumnReqDTO = new BasicModelColumnReqDTO(column);
            columnReqDTOList.add(basicModelColumnReqDTO);
        }
        return columnReqDTOList;
    }

    @Override
    public boolean creaDatasourceTeTable(DatasourceCreaTeTableReqDTO datasourceCreaTeTableReqDTO) {
        DbQueryProperty dbQueryProperty = new DbQueryProperty(datasourceCreaTeTableReqDTO.getDatasourceType()
                , datasourceCreaTeTableReqDTO.getIp(), datasourceCreaTeTableReqDTO.getPort(), datasourceCreaTeTableReqDTO.getDatasourceConfig());
        DbQuery dbQuery = dataSourceFactory.createDbQuery(dbQueryProperty);
        if (!dbQuery.valid()) {
            throw new DataQueryException("数据库连接失败");
        }

        int tableStatus = dbQuery.generateCheckTableExistsSQL(dbQueryProperty, datasourceCreaTeTableReqDTO.getTableName());
        if (tableStatus > 0) {
            return false;
        }

        List<String> tableSQLList = dbQuery.generateCreateTableSQL(dbQueryProperty, datasourceCreaTeTableReqDTO.getTableName(), datasourceCreaTeTableReqDTO.getTableComment(), datasourceCreaTeTableReqDTO.getColumnsList());

        for (String sql : tableSQLList) {
            dbQuery.execute(sql);
        }
        return true;
    }


    @Override
    public boolean creaDatasourceTeTable(DbQuery dbQuery, DbQueryProperty dbQueryProperty, DatasourceCreaTeTableReqDTO datasourceCreaTeTableReqDTO) {

        int tableStatus = dbQuery.generateCheckTableExistsSQL(dbQueryProperty, datasourceCreaTeTableReqDTO.getTableName());
        if (tableStatus > 0) {
            return false;
        }

        List<String> tableSQLList = dbQuery.generateCreateTableSQL(dbQueryProperty, datasourceCreaTeTableReqDTO.getTableName(), datasourceCreaTeTableReqDTO.getTableComment(), datasourceCreaTeTableReqDTO.getColumnsList());

        for (String sql : tableSQLList) {
            dbQuery.execute(sql);
        }
        return true;
    }


    @Override
    public DatasourceCreaTeTableReqDTO createTaskTempTable(Map<String, Object> taskParams) {

        DatasourceCreaTeTableReqDTO datasourceCreaTeTableReqDTO = buildJobDatasource(taskParams);

        creaDatasourceTeTable(datasourceCreaTeTableReqDTO);
        datasourceCreaTeTableReqDTO.setColumnsList(null);
        return datasourceCreaTeTableReqDTO;
    }

    /**
     * 创建对象
     *
     * @param taskParams
     * @return
     */
    private static DatasourceCreaTeTableReqDTO buildJobDatasource(Map<String, Object> taskParams) {
        Map<String, Object> datasource = JSONUtils.convertTaskDefinitionJsonMap(tempDataSource);

//        Map<String, Object> datasource = (Map<String, Object>) MapUtils.getObject(taskParams, "readerDatasource");
        DatasourceCreaTeTableReqDTO datasourceCreaTeTableReqDTO = new DatasourceCreaTeTableReqDTO();
        String ip = MapUtils.getString(datasource, "ip");
        long port = MapUtils.getLong(datasource, "port");
        String datasourceConfig = MapUtils.getString(datasource, "datasourceConfig");
        String datasourceType = MapUtils.getString(datasource, "datasourceType");

        datasourceCreaTeTableReqDTO.setDatasourceType(datasourceType);
        datasourceCreaTeTableReqDTO.setDatasourceConfig(datasourceConfig);
        datasourceCreaTeTableReqDTO.setIp(ip);
        datasourceCreaTeTableReqDTO.setPort(port);

//        String table_name = "task_"+StringUtils.generateRandomString();
        String table_name = "task_";


        datasourceCreaTeTableReqDTO.setTableName(table_name);

        List<Map<String, Object>> tableColumns = (List<Map<String, Object>>) MapUtils.getObject(taskParams, "tableFields");
        List<DbColumn> columnsList = convertToDbColumnList(tableColumns);
        datasourceCreaTeTableReqDTO.setColumnsList(columnsList);


        return datasourceCreaTeTableReqDTO;
    }


    public static List<DbColumn> convertToDbColumnList(List<Map<String, Object>> tableColumns) {
        return tableColumns.stream().map(columnMap -> {
            DbColumn dbColumn = new DbColumn();


            // 遍历 columnMap，将其映射到 DbColumn
            dbColumn.setColName(MapUtils.getString(columnMap, "columnName"));
            dbColumn.setDataType(MapUtils.getString(columnMap, "columnType"));
            dbColumn.setDataLength(MapUtils.getString(columnMap, "columnLength"));
            dbColumn.setDataPrecision(MapUtils.getString(columnMap, "columnPrecision"));
            dbColumn.setDataScale(MapUtils.getString(columnMap, "columnScale"));

            // 转换 pkFlag 和 nullableFlag 字段
            dbColumn.setColKey("1".equals(columnMap.get("pkFlag")));  // "1" 是主键，"0" 不是
            dbColumn.setNullable("1".equals(columnMap.get("nullableFlag")));  // "1" 是可空，"0" 不是

            dbColumn.setColPosition((Integer) columnMap.get("colPosition"));
            dbColumn.setDataDefault(MapUtils.getString(columnMap, "defaultValue"));
            dbColumn.setColComment(MapUtils.getString(columnMap, "columnComment"));

            return dbColumn;
        }).collect(Collectors.toList());
    }

}
