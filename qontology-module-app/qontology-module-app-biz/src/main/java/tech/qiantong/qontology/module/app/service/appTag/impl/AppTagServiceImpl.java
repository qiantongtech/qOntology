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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appTag.vo.AppTagSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTag.AppTagDO;
import tech.qiantong.qontology.module.app.dal.mapper.appTag.AppTagMapper;
import tech.qiantong.qontology.module.app.service.appTag.IAppTagService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识标签Service业务层处理
 *
 * @author qknow
 * @date 2025-11-13
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppTagServiceImpl extends ServiceImpl<AppTagMapper, AppTagDO> implements IAppTagService {
    @Resource
    private AppTagMapper AppTagMapper;

    @Override
    public PageResult<AppTagDO> getAppTagPage(AppTagPageReqVO pageReqVO) {
        return AppTagMapper.selectPage(pageReqVO);
    }

    @Override
    public Long createAppTag(AppTagSaveReqVO createReqVO) {
        AppTagDO dictType = BeanUtils.toBean(createReqVO, AppTagDO.class);
        AppTagMapper.insert(dictType);
        return dictType.getId();
    }

    @Override
    public int updateAppTag(AppTagSaveReqVO updateReqVO) {
        // 相关校验

        // 更新知识标签
        AppTagDO updateObj = BeanUtils.toBean(updateReqVO, AppTagDO.class);
        return AppTagMapper.updateById(updateObj);
    }

    @Override
    public int removeAppTag(Collection<Long> idList) {
        // 批量删除知识标签
        return AppTagMapper.deleteBatchIds(idList);
    }

    @Override
    public AppTagDO getAppTagById(Long id) {
        return AppTagMapper.selectById(id);
    }

    @Override
    public List<AppTagDO> getAppTagList() {
        return AppTagMapper.selectList();
    }

    @Override
    public Map<Long, AppTagDO> getAppTagMap() {
        List<AppTagDO> AppTagList = AppTagMapper.selectList();
        return AppTagList.stream()
                .collect(Collectors.toMap(
                        AppTagDO::getId,
                        AppTagDO -> AppTagDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /**
     * 导入知识标签数据
     *
     * @param importExcelList 知识标签数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importAppTag(List<AppTagRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (AppTagRespVO respVO : importExcelList) {
            try {
                AppTagDO AppTagDO = BeanUtils.toBean(respVO, AppTagDO.class);
                Long AppTagId = respVO.getId();
                if (isUpdateSupport) {
                    if (AppTagId != null) {
                        AppTagDO existingAppTag = AppTagMapper.selectById(AppTagId);
                        if (existingAppTag != null) {
                            AppTagMapper.updateById(AppTagDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + AppTagId + " 的知识标签记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + AppTagId + " 的知识标签记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<AppTagDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", AppTagId);
                    AppTagDO existingAppTag = AppTagMapper.selectOne(queryWrapper);
                    if (existingAppTag == null) {
                        AppTagMapper.insert(AppTagDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + AppTagId + " 的知识标签记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + AppTagId + " 的知识标签记录已存在。");
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
}
