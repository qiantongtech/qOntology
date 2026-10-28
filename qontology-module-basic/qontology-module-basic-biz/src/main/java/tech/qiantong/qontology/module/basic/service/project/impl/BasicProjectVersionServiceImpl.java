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

package tech.qiantong.qontology.module.basic.service.project.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.domain.entity.SysDictData;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionPageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.BasicProjectVersionSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.project.vo.VersionDiffStatsVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.project.BasicProjectVersionDO;
import tech.qiantong.qontology.module.basic.dal.mapper.project.BasicProjectVersionMapper;
import tech.qiantong.qontology.module.basic.dto.OmTargetCountDTO;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectVersionService;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 项目版本Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-12
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicProjectVersionServiceImpl extends ServiceImpl<BasicProjectVersionMapper, BasicProjectVersionDO> implements IBasicProjectVersionService {

    @Resource
    private List<IProjectRepeatable> projectRepeatableList;
    // 复用项目全局线程池，用于并行执行各实现类的 copy 方法
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    /**
     * 获得项目版本分页列表
     *
     * @param pageReqVO 分页请求
     * @return 项目版本分页列表
     */
    @Override
    public PageResult<BasicProjectVersionDO> getBasicProjectVersionPage(BasicProjectVersionPageReqVO pageReqVO) {
        return baseMapper.selectPage(pageReqVO);
    }

    /**
     * 创建项目版本
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public Long createBasicProjectVersion(BasicProjectVersionSaveReqVO createReqVO, LoginUser currentUser) {
        BasicProjectVersionDO dictType = BeanUtils.toBean(createReqVO, BasicProjectVersionDO.class);
        baseMapper.insert(dictType);

        // 从容器中获取所有 Repeatable 接口的实现类
        if (CollUtil.isEmpty(projectRepeatableList)) {
            return dictType.getId();
        }

        // 基准版本作为源版本，新建版本作为目标版本；无基准版本时无需复制
        Long sourceVersionId = createReqVO.getBaseVersionId();
        Long targetVersionId = dictType.getId();

        // 并行执行所有实现类的 copy：每个任务在独立线程与其独立事务中提交
        List<CompletableFuture<String>> futures = projectRepeatableList.stream()
                .map(repeatable -> CompletableFuture.supplyAsync(() -> {
                    String module = repeatable.getClass().getSimpleName();
                    try {
                        Boolean copyResult = repeatable.copy(sourceVersionId, targetVersionId);
                        return Boolean.TRUE.equals(copyResult) ? null : module;
                    } catch (Exception e) {
                        log.error("项目版本数据复制失败, module={}, sourceVersionId={}, targetVersionId={}",
                                module, sourceVersionId, targetVersionId, e);
                        return module;
                    }
                }, threadPoolTaskExecutor)).toList();

        // 等待全部完成并汇总失败的模块名（无论成败都等所有任务跑完，便于一次性反馈）
        List<String> failedModules = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(failedModules)) {
            throw new ServiceException("项目版本数据复制失败：" + String.join(", ", failedModules));
        }

        return dictType.getId();
    }

    /**
     * 更新项目版本
     *
     * @param updateReqVO 更新信息
     * @return 影响行数
     */
    @Override
    public int updateBasicProjectVersion(BasicProjectVersionSaveReqVO updateReqVO) {
        // 相关校验

        // 更新项目版本
        BasicProjectVersionDO updateObj = BeanUtils.toBean(updateReqVO, BasicProjectVersionDO.class);
        return baseMapper.updateById(updateObj);
    }

    /**
     * 删除项目版本
     *
     * @param idList 编号集合
     * @return 影响行数
     */
    @Override
    public int removeBasicProjectVersion(Collection<Long> idList) {
        // 批量删除项目版本
        return baseMapper.deleteByIds(idList);
    }

    /**
     * 获得项目版本
     *
     * @param id 编号
     * @return 项目版本
     */
    @Override
    public BasicProjectVersionDO getBasicProjectVersionById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 获得项目版本列表
     *
     * @param projectId 项目编号
     * @return 项目版本列表
     */
    @Override
    public List<SysDictData> queryProjectVersionDict(Long projectId) {
        LambdaQueryWrapper<BasicProjectVersionDO> queryWrapper = Wrappers.lambdaQuery(BasicProjectVersionDO.class)
                .eq(BasicProjectVersionDO::getProjectId, projectId);

        List<BasicProjectVersionDO> list = super.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        return list.stream().map(item -> {
            SysDictData dictData = new SysDictData();
            dictData.setDictLabel(item.getVersion());
            dictData.setDictValue(item.getId().toString());
            return dictData;
        }).collect(Collectors.toList());
    }

    /**
     * 获得项目版本差异统计
     *
     * @param versionId 项目版本编号
     * @return 项目版本差异统计
     */
    @Override
    public VersionDiffStatsVO queryVersionDiffStats(Long versionId) {
        BasicProjectVersionDO version = super.getById(versionId);
        VersionDiffStatsVO resultVO = new VersionDiffStatsVO();
        resultVO.setProjectVersion(version);

        // 1. 异步任务1：查询对象基础信息
        CompletableFuture<OmTargetCountDTO> omTargetCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.getOmTargetCount(versionId),
                threadPoolTaskExecutor
        );
        CompletableFuture<Integer> termCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.selectTermCount(versionId),
                threadPoolTaskExecutor
        );
        CompletableFuture<Integer> enumerationCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.selectEnumerationCount(versionId),
                threadPoolTaskExecutor
        );
        CompletableFuture<Integer> objectTypeCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.selectObjectTypeCount(versionId),
                threadPoolTaskExecutor
        );
        CompletableFuture<Integer> objectTypeAttributeCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.selectObjectTypeAttributeCount(versionId),
                threadPoolTaskExecutor
        );
        CompletableFuture<Integer> relationCountFuture = CompletableFuture.supplyAsync(
                () -> baseMapper.selectRelationCount(versionId),
                threadPoolTaskExecutor
        );
        // 等待所有查询任务完成
        CompletableFuture.allOf(omTargetCountFuture, termCountFuture, enumerationCountFuture, objectTypeCountFuture,
                objectTypeAttributeCountFuture, relationCountFuture).join();

        // 获取结果并组装
        try {
            OmTargetCountDTO omTargetCount = omTargetCountFuture.get();
            Integer termCount = termCountFuture.get();
            Integer enumerationCount = enumerationCountFuture.get();
            Integer objectTypeCount = objectTypeCountFuture.get();
            Integer objectTypeAttributeCount = objectTypeAttributeCountFuture.get();
            Integer relationCount = relationCountFuture.get();

            // 组装VO

            BigDecimal totalCount = omTargetCount.getTotalCount();
            BigDecimal coveredCount = omTargetCount.getCoveredCount();
            if (totalCount.compareTo(BigDecimal.ZERO) == 0){
                resultVO.setOmTargetCount(0);
                resultVO.setOmTargetCoveredRate(0.0f);
            }else {
                BigDecimal divide = coveredCount.divide(totalCount, 2, RoundingMode.HALF_UP);
                resultVO.setOmTargetCount(totalCount.intValue());
                resultVO.setOmTargetCoveredRate(divide.floatValue());
            }

            resultVO.setTermCount(termCount);
            resultVO.setEnumerationCount(enumerationCount);
            resultVO.setObjectTypeCount(objectTypeCount);
            resultVO.setObjectTypeAttributeCount(objectTypeAttributeCount);
            resultVO.setRelationCount(relationCount);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("并行查询对象详情失败");
        }
        return resultVO;
    }
}
