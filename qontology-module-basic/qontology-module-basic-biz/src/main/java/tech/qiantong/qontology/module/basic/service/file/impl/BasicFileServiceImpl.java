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

package tech.qiantong.qontology.module.basic.service.file.impl;

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
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFilePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileTrackReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileCategoryDO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileDO;
import tech.qiantong.qontology.module.basic.dal.mapper.file.BasicFileCategoryMapper;
import tech.qiantong.qontology.module.basic.dal.mapper.file.BasicFileMapper;
import tech.qiantong.qontology.module.basic.service.file.IBasicFileService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模型数据文件Service业务层处理
 *
 * @author qModel
 * @date 2026-08-18
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicFileServiceImpl extends ServiceImpl<BasicFileMapper, BasicFileDO> implements IBasicFileService {
    @Resource
    private BasicFileMapper modelDataFileMapper;

    @Resource
    private BasicFileCategoryMapper modelDataCategoryMapper;

    @Override
    public PageResult<BasicFileDO> getModelDataFilePage(BasicFilePageReqVO pageReqVO) {
        PageResult<BasicFileDO> pageResult = modelDataFileMapper.selectPage(pageReqVO);
        List<BasicFileDO> list = pageResult.getList();
        if (list.isEmpty()) {
            return pageResult;
        }

        // 提取分类 ID
        List<Long> categoryIds = list.stream()
                .map(BasicFileDO::getCategoryId)
                .filter(id -> id != null && id >= 0)
                .distinct()
                .collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return pageResult;
        }

        // 查询分类信息
        List<BasicFileCategoryDO> categories = new ArrayList<>();
        if (categoryIds.contains(0L)) {
            BasicFileCategoryDO root = new BasicFileCategoryDO();
            root.setId(0L);
            root.setName("顶级分类");
            categories.add(root);

            List<Long> realIds = categoryIds.stream().filter(id -> id > 0).collect(Collectors.toList());
            if (!realIds.isEmpty()) {
                categories.addAll(modelDataCategoryMapper.selectBatchIds(realIds));
            }
        } else {
            categories = modelDataCategoryMapper.selectBatchIds(categoryIds);
        }

        Map<Long, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(BasicFileCategoryDO::getId, BasicFileCategoryDO::getName));

        // 填充分类名称
        list.forEach(item -> item.setCategoryName(categoryNameMap.get(item.getCategoryId())));

        return pageResult;
    }

    @Override
    public Long createModelDataFile(BasicFileSaveReqVO createReqVO) {
        String[] names = createReqVO.getFileName().split(",");
        String[] paths = createReqVO.getFilePath().split(",");
        List<BasicFileDO> documentList = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            BasicFileDO fileDO = BeanUtils.toBean(createReqVO, BasicFileDO.class);
            fileDO.setFileName(names[i]);
            fileDO.setFilePath(paths[i]);
            documentList.add(fileDO);
        }
        // 批量添加
        boolean success = modelDataFileMapper.insertBatch(documentList);
        return success ? (long) documentList.size() : 0L;
    }

    @Override
    public int updateModelDataFile(BasicFileSaveReqVO updateReqVO) {
        String[] names = updateReqVO.getFileName().split(",");
        String[] paths = updateReqVO.getFilePath().split(",");
        List<BasicFileDO> documentList = new ArrayList<>();
        for (int i = 0; i < paths.length; i++) {
            BasicFileDO fileDO = BeanUtils.toBean(updateReqVO, BasicFileDO.class);
            fileDO.setFileName(names[i]);
            fileDO.setFilePath(paths[i]);
            if (i != 0) {
                fileDO.setId(null);
            }
            documentList.add(fileDO);
        }

        // 第一条记录更新
        BasicFileDO mainFile = documentList.get(0);
        documentList.remove(0);

        // 其他记录作为新增
        if (!documentList.isEmpty()) {
            modelDataFileMapper.insertBatch(documentList);
        }

        return modelDataFileMapper.updateById(mainFile);
    }

    @Override
    public int removeModelDataFile(Collection<Long> idList) {
        // 批量删除模型数据文件
        return modelDataFileMapper.deleteBatchIds(idList);
    }

    @Override
    public BasicFileDO getModelDataFileById(Long id) {
        BasicFileDO fileDO = modelDataFileMapper.selectById(id);
        if (fileDO != null && fileDO.getCategoryId() != null && fileDO.getCategoryId() >= 0) {
            if (fileDO.getCategoryId() == 0) {
                fileDO.setCategoryName("顶级分类");
            } else {
                BasicFileCategoryDO category = modelDataCategoryMapper.selectById(fileDO.getCategoryId());
                if (category != null) {
                    fileDO.setCategoryName(category.getName());
                }
            }
        }
        return fileDO;
    }

    @Override
    public List<BasicFileDO> getModelDataFileList() {
        return modelDataFileMapper.selectList();
    }

    @Override
    public Map<Long, BasicFileDO> getModelDataFileMap() {
        List<BasicFileDO> modelDataFileList = modelDataFileMapper.selectList();
        return modelDataFileList.stream()
                .collect(Collectors.toMap(
                        BasicFileDO::getId,
                        modelDataFileDO -> modelDataFileDO,
                        // 保留已存在的值
                        (existing, replacement) -> existing
                ));
    }


    /**
     * 导入模型数据文件数据
     *
     * @param importExcelList 模型数据文件数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    @Override
    public String importModelDataFile(List<BasicFileRespVO> importExcelList, boolean isUpdateSupport, String operName) {
        if (StringUtils.isNull(importExcelList) || importExcelList.size() == 0) {
            throw new ServiceException("导入数据不能为空！");
        }

        int successNum = 0;
        int failureNum = 0;
        List<String> successMessages = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        for (BasicFileRespVO respVO : importExcelList) {
            try {
                BasicFileDO modelDataFileDO = BeanUtils.toBean(respVO, BasicFileDO.class);
                Long modelDataFileId = respVO.getId();
                if (isUpdateSupport) {
                    if (modelDataFileId != null) {
                        BasicFileDO existingModelDataFile = modelDataFileMapper.selectById(modelDataFileId);
                        if (existingModelDataFile != null) {
                            modelDataFileMapper.updateById(modelDataFileDO);
                            successNum++;
                            successMessages.add("数据更新成功，ID为 " + modelDataFileId + " 的模型数据文件记录。");
                        } else {
                            failureNum++;
                            failureMessages.add("数据更新失败，ID为 " + modelDataFileId + " 的模型数据文件记录不存在。");
                        }
                    } else {
                        failureNum++;
                        failureMessages.add("数据更新失败，某条记录的ID不存在。");
                    }
                } else {
                    QueryWrapper<BasicFileDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", modelDataFileId);
                    BasicFileDO existingModelDataFile = modelDataFileMapper.selectOne(queryWrapper);
                    if (existingModelDataFile == null) {
                        modelDataFileMapper.insert(modelDataFileDO);
                        successNum++;
                        successMessages.add("数据插入成功，ID为 " + modelDataFileId + " 的模型数据文件记录。");
                    } else {
                        failureNum++;
                        failureMessages.add("数据插入失败，ID为 " + modelDataFileId + " 的模型数据文件记录已存在。");
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
    public List<Map<String, Object>> getFileTypes() {
        // 1. 获取所有分类
        List<BasicFileCategoryDO> categories = modelDataCategoryMapper.selectList();

        // 2. 获取每个分类的文件统计数量
        List<Map<String, Object>> fileCounts = modelDataFileMapper.getFileTypes();
        Map<Long, Long> countMap = fileCounts.stream()
                .filter(m -> m.get("category_id") != null)
                .collect(Collectors.toMap(
                        m -> Long.valueOf(m.get("category_id").toString()),
                        m -> Long.valueOf(m.get("totalCount").toString())
                ));

        // 3. 将统计数量合并到分类数据中
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (BasicFileCategoryDO category : categories) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", category.getId());
            map.put("parentId", category.getParentId());
            map.put("name", category.getName());
            map.put("totalCount", countMap.getOrDefault(category.getId(), 0L));
            resultList.add(map);
        }

        return resultList;
    }

    @Override
    public Boolean trackCount(List<BasicFileTrackReqVO> documentTrackList, Long userId, String userName) {
        // 由于当前模块暂无文件操作日志记录表，此处仅做接口兼容
        return true;
    }
}
