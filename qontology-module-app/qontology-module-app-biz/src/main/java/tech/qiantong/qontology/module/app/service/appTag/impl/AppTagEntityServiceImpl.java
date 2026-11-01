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

package tech.qiantong.qontology.module.app.service.appTag.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.App.api.tag.IAppTagEntityApiService;
import tech.qiantong.qontology.module.App.api.tag.dto.AppTagEntityReqDTO;
import tech.qiantong.qontology.module.App.api.tag.dto.AppTagEntityRespDTO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagEntitySaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagEntityDO;
import tech.qiantong.qontology.module.app.dal.mapper.appTag.AppTagEntityMapper;
import tech.qiantong.qontology.module.app.service.appTag.IAppTagEntityService;
import tech.qiantong.qontology.module.app.service.appTag.IAppTagService;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 实体标签Service业务层处理
 *
 * @author qknow
 * @date 2025-11-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppTagEntityServiceImpl extends ServiceImpl<AppTagEntityMapper, AppTagEntityDO> implements IAppTagEntityService, IAppTagEntityApiService {
    @Resource
    private tech.qiantong.qontology.module.app.dal.mapper.appTag.AppTagEntityMapper AppTagEntityMapper;

    @Resource
    private IAppTagService tagService;

    @Override
    public PageResult<AppTagEntityDO> getAppTagEntityPage(AppTagEntityPageReqVO pageReqVO) {
        return AppTagEntityMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createAppTagEntity(AppTagEntitySaveReqVO createReqVO) {
        AppTagEntityDO dictType = BeanUtils.toBean(createReqVO, AppTagEntityDO.class);
        AppTagEntityMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateAppTagEntity(AppTagEntitySaveReqVO updateReqVO) {
        // 相关校验

        // 更新实体标签
        AppTagEntityDO updateObj = BeanUtils.toBean(updateReqVO, AppTagEntityDO.class);
        return AppTagEntityMapper.updateById(updateObj);
    }

    @Override
    public int removeAppTagEntity(Collection<Long> idList) {
        // 批量删除实体标签
        return AppTagEntityMapper.deleteBatchIds(idList);
    }

    @Override
    public AppTagEntityDO getAppTagEntityById(Long id) {
        return AppTagEntityMapper.selectById(id);
    }

    @Override
    public List<AppTagEntityDO> getAppTagEntityList() {
        return AppTagEntityMapper.selectList();
    }

    @Override
    public Map<Long, AppTagEntityDO> getAppTagEntityMap() {
        List<AppTagEntityDO> AppTagEntityList = AppTagEntityMapper.selectList();
        return AppTagEntityList.stream()
                .collect(Collectors.toMap(
                        AppTagEntityDO::getId,
                        AppTagEntityDO -> AppTagEntityDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /**
     * 导入实体标签数据
     *
     * @param importExcelList 实体标签数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importAppTagEntity(List<AppTagEntityRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (AppTagEntityRespVO respVO : importExcelList) {
            try {
                AppTagEntityDO AppTagEntityDO = BeanUtils.toBean(respVO, AppTagEntityDO.class);
                Long AppTagEntityId = respVO.getId();
                if (isUpdateSupport) {
                    if (AppTagEntityId != null) {
                        AppTagEntityDO existingAppTagEntity = AppTagEntityMapper.selectById(AppTagEntityId);
                        if (existingAppTagEntity != null) {
                            AppTagEntityMapper.updateById(AppTagEntityDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + AppTagEntityId + " 的实体标签记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + AppTagEntityId + " 的实体标签记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<AppTagEntityDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", AppTagEntityId);
                    AppTagEntityDO existingAppTagEntity = AppTagEntityMapper.selectOne(queryWrapper);
                    if (existingAppTagEntity == null) {
                        AppTagEntityMapper.insert(AppTagEntityDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + AppTagEntityId + " 的实体标签记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + AppTagEntityId + " 的实体标签记录已存在。");
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

    @Override
    public List<AppTagDO> getAppEntityTagPage(AppTagEntityPageReqVO pageReqVO) {
        PageResult<AppTagEntityDO> pageResult = AppTagEntityMapper.selectPage(pageReqVO);
        List<AppTagEntityDO> list = pageResult.getList();
        // 获取当前图谱和实体的标签id集合
        Set<Long> tagIds = list.stream().map(AppTagEntityDO::getTagId).collect(Collectors.toSet());
        LambdaQueryWrapperX<AppTagDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.inIfPresent(AppTagDO::getId, tagIds);
        List<AppTagDO> tags = (CollectionUtils.isEmpty(tagIds)) ? Collections.emptyList() : tagService.list(wrapper);
        return tags;
    }


    @Override
    @Transactional
    public Boolean updateAppEntityTag(AppTagEntitySaveReqVO updateReqVO) {
        // 获取收参ids
        List<Long> newIds = updateReqVO.getTagIds().stream().map(AppTagDO::getId).collect(Collectors.toList());
        // 获取当前实体原本标签集合
        LambdaQueryWrapperX<AppTagEntityDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(AppTagEntityDO::getEntityId, updateReqVO.getEntityId());
        List<AppTagEntityDO> AppEntityTagDOS = AppTagEntityMapper.selectList(wrapper);
        List<Long> oldIds = AppEntityTagDOS.stream()
                .map(AppTagEntityDO::getTagId)
                .collect(Collectors.toList());

        // 增加的标签
        List<Long> added = newIds.stream()
                .filter(id -> !oldIds.contains(id))
                .collect(Collectors.toList());
        // 减少的标签
        List<Long> removed = oldIds.stream()
                .filter(id -> !newIds.contains(id))
                .collect(Collectors.toList());

        // 删除
        if (CollectionUtils.isNotEmpty(removed)) {
            List<AppTagEntityDO> delList = new ArrayList<>();
            for (Long l : removed) {
                AppTagEntityDO delDo = new AppTagEntityDO();
                delDo.setEntityId(updateReqVO.getTagId());
                delDo.setTagId(l);
                delList.add(delDo);
            }

            LambdaQueryWrapperX<AppTagEntityDO> delWrapper = new LambdaQueryWrapperX<>();
            delWrapper.in(AppTagEntityDO::getTagId, removed);
            AppTagEntityMapper.delete(delWrapper);
        }

        // 新增
        if (CollectionUtils.isNotEmpty(added)) {
            List<AppTagEntityDO> addList = new ArrayList<>();
            for (Long l : added) {
                AppTagEntityDO addDo = new AppTagEntityDO();
                addDo.setEntityId(updateReqVO.getEntityId());
                addDo.setTagId(l);
                addDo.setGraphId(updateReqVO.getGraphId());
                addList.add(addDo);
            }
            return AppTagEntityMapper.insertBatch(addList);
        }
        return true;
    }

    @Override
    public Integer deleteTagEntity(AppTagEntitySaveReqVO reqVO) {
        LambdaQueryWrapperX<AppTagEntityDO> delWrapper = new LambdaQueryWrapperX<>();
        delWrapper.eqIfPresent(AppTagEntityDO::getGraphId, reqVO.getGraphId())
                .eqIfPresent(AppTagEntityDO::getEntityId, reqVO.getEntityId())
                .eqIfPresent(AppTagEntityDO::getTagId, reqVO.getTagId());
        return AppTagEntityMapper.delete(delWrapper);
    }


    @Override
    public List<AppTagEntityRespDTO> getAppTagEntityList(AppTagEntityReqDTO pageReqVO) {
        return BeanUtils.toBean(this.getAppTagEntityPage(BeanUtils.toBean(pageReqVO, AppTagEntityPageReqVO.class)), AppTagEntityRespDTO.class).getList();
    }
}
