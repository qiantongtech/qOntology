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

package tech.qiantong.qontology.module.om.service.omtarget.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetRespVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmObjectTypeTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmObjectTypeTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmRelationTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetMapper;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 建模目标Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmTargetServiceImpl extends ServiceImpl<OmTargetMapper, OmTargetDO> implements IOmTargetService {

    @Resource
    private OmObjectTypeTargetMapper objectTypeTargetMapper;
    @Resource
    private OmRelationTargetMapper relationTargetMapper;

    /**
     * 获得建模目标分页列表
     *
     * @param pageReqVO 分页请求
     * @return 建模目标分页列表
     */
    @Override
    public PageResult<OmTargetRespVO> getOmTargetPage(OmTargetPageReqVO pageReqVO) {
        // 统计
        PageResult<OmTargetDO> resultPage = baseMapper.selectPage(pageReqVO);
        List<OmTargetRespVO> respVOS = convertTargets(resultPage.getList());
        return new PageResult<>(respVOS, resultPage.getTotal());
    }

    private List<OmTargetRespVO> convertTargets(List<OmTargetDO> targets) {
        if (targets.isEmpty()) return Collections.emptyList();
        List<Long> ids = targets.stream().map(OmTargetDO::getId).collect(Collectors.toList());
        Map<Long, Long> counts = objectTypeTargetMapper
                .selectList(new QueryWrapper<OmObjectTypeTargetDO>()
                        .in("target_id", ids).eq("valid_flag", true))
                .stream()
                .collect(
                        Collectors.groupingBy(OmObjectTypeTargetDO::getTargetId,
                        Collectors.mapping(OmObjectTypeTargetDO::getObjectTypeId,
                                Collectors.collectingAndThen(Collectors.toSet(), set -> (long) set.size()))));
        Map<Long, Long> relationCounts = relationTargetMapper.selectList(new QueryWrapper<OmRelationTargetDO>()
                        .in("target_id", ids).eq("valid_flag", true).isNotNull("relation_id")).stream()
                .collect(Collectors.groupingBy(OmRelationTargetDO::getTargetId,
                        Collectors.mapping(OmRelationTargetDO::getRelationId,
                                Collectors.collectingAndThen(Collectors.toSet(), set -> (long) set.size()))));
        List<OmTargetRespVO> result = new ArrayList<>();
        for (OmTargetDO target : targets) {
            OmTargetRespVO item = BeanUtils.toBean(target, OmTargetRespVO.class);
            item.setRelatedElementCount(counts.getOrDefault(target.getId(), 0L)
                    + relationCounts.getOrDefault(target.getId(), 0L));
            result.add(item);
        }
        return result;
    }


    /**
     * 创建建模目标
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    @Override
    public Long createOmTarget(OmTargetSaveReqVO createReqVO) {
        OmTargetDO dictType = BeanUtils.toBean(createReqVO, OmTargetDO.class);
        dictType.setAcceptStatus(AcceptStatusEnum.WAITING);
        baseMapper.insert(dictType);
        return dictType.getId();
    }

    /**
     * 更新建模目标
     *
     * @param updateReqVO 更新信息
     * @return 影响行数
     */
    @Override
    public int updateOmTarget(OmTargetSaveReqVO updateReqVO) {
        // 相关校验

        // 更新建模目标
        OmTargetDO updateObj = BeanUtils.toBean(updateReqVO, OmTargetDO.class);
        return baseMapper.updateById(updateObj);
    }

    /**
     * 删除建模目标
     *
     * @param idList 编号集合
     * @return 影响行数
     */
    @Override
    public int removeOmTarget(Collection<Long> idList) {
        // 批量删除建模目标
        return baseMapper.deleteByIds(idList);
    }

    /**
     * 获得建模目标
     *
     * @param id 编号
     * @return 建模目标
     */
    @Override
    public OmTargetRespVO getOmTargetById(Long id) {
        OmTargetDO omTargetDO = baseMapper.selectById(id);
        List<OmTargetRespVO> vos = this.convertTargets(Collections.singletonList(omTargetDO));
        return vos.get(0);
    }

    /**
     * 获得建模目标列表
     *
     * @return 建模目标列表
     */
    @Override
    public List<OmTargetDO> getOmTargetList() {
        return baseMapper.selectList();
    }

    /**
     * 获得建模目标 Map
     *
     * @return 建模目标 Map
     */
    @Override
    public Map<Long, OmTargetDO> getOmTargetMap() {
        List<OmTargetDO> omTargetList = baseMapper.selectList();
        return omTargetList.stream()
                .collect(Collectors.toMap(
                        OmTargetDO::getId,
                        omTargetDO -> omTargetDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }

}
