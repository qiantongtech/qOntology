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
import tech.qiantong.qontology.module.basic.dto.VersionDiffStatsCountDTO;
import tech.qiantong.qontology.module.basic.service.project.IBasicProjectVersionService;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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

        copyBusinessTable(createReqVO.getBaseVersionId(), dictType.getId(), currentUser);

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

        VersionDiffStatsCountDTO statsCount = baseMapper.selectVersionDiffStatsCount(versionId);
        if (Objects.isNull(statsCount)) {
            return resultVO;
        }

        // 建模目标数量与覆盖率
        int omTargetCount = nullToZero(statsCount.getOmTargetCount());
        int omTargetCoveredCount = nullToZero(statsCount.getOmTargetCoveredCount());
        resultVO.setOmTargetCount(omTargetCount);
        if (omTargetCount == 0) {
            resultVO.setOmTargetCoveredRate(0.0f);
        } else {
            BigDecimal coveredRate = BigDecimal.valueOf(omTargetCoveredCount)
                    .divide(BigDecimal.valueOf(omTargetCount), 2, RoundingMode.HALF_UP);
            resultVO.setOmTargetCoveredRate(coveredRate.floatValue());
        }

        resultVO.setTermCount(nullToZero(statsCount.getTermCount()));
        resultVO.setEnumerationCount(nullToZero(statsCount.getEnumerationCount()));
        resultVO.setObjectTypeCount(nullToZero(statsCount.getObjectTypeCount()));
        resultVO.setObjectTypeAttributeCount(nullToZero(statsCount.getObjectTypeAttributeCount()));
        resultVO.setRelationCount(nullToZero(statsCount.getRelationCount()));
        resultVO.setModelAxiomCount(nullToZero(statsCount.getModelAxiomCount()));
        resultVO.setDataRuleCount(nullToZero(statsCount.getDataRuleCount()));
        return resultVO;
    }

    /**
     * 统计数值空值兜底，统一按 0 处理
     */
    private static int nullToZero(Integer count) {
        return Objects.isNull(count) ? 0 : count;
    }

    /**
     * 复制业务表
     *
     * @param sourceVersionId 源版本 id
     * @param targetVersionId 目标版本 id
     * @param currentUser     当前用户
     */
    private void copyBusinessTable(Long sourceVersionId, Long targetVersionId, LoginUser currentUser) {

        if (Objects.isNull(sourceVersionId) || Objects.isNull(targetVersionId)) {
            return;
        }

        // 构建复制上下文，按 copyOrder 升序【同线程串行】执行各实现类的 copy：
        // 1) 保证被依赖父表（如 om_target、om_object_type）先完成并登记旧ID->新ID映射；
        // 2) 全部在外层同一事务内进行，新行对后续关联表可见，且任一失败整体回滚。
        ProjectCopyContext context = new ProjectCopyContext(sourceVersionId, targetVersionId, currentUser);
        List<String> failedModules = new ArrayList<>();
        projectRepeatableList.stream()
                .sorted(Comparator.comparingInt(IProjectRepeatable::copyOrder))
                .forEach(repeatable -> {
                    String module = repeatable.getClass().getSimpleName();
                    try {
                        Boolean copyResult = repeatable.copy(context);
                        if (!Boolean.TRUE.equals(copyResult)) {
                            failedModules.add(module);
                        }
                    } catch (Exception e) {
                        log.error("项目版本数据复制失败, module={}, sourceVersionId={}, targetVersionId={}",
                                module, sourceVersionId, targetVersionId, e);
                        failedModules.add(module);
                    }
                });
        if (CollUtil.isNotEmpty(failedModules)) {
            throw new ServiceException("项目版本数据复制失败：" + String.join(", ", failedModules));
        }

    }
}
