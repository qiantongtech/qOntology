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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.ai.service.modelMarket.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelPageReqVO;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiModelSaveReqVO;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiApiKeyDO;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiModelDO;
import tech.qiantong.qontology.module.ai.dal.enums.AiModelTypeEnum;
import tech.qiantong.qontology.module.ai.dal.enums.AiPlatformEnum;
import tech.qiantong.qontology.module.ai.dal.enums.ApiKeyStatus;
import tech.qiantong.qontology.module.ai.dal.mapper.modelMarket.AiApiKeyMapper;
import tech.qiantong.qontology.module.ai.dal.mapper.modelMarket.AiModelMapper;
import tech.qiantong.qontology.module.ai.service.modelMarket.IAiModelService;
import tech.qiantong.qontology.redis.service.IRedisService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 模型Service业务层处理
 *
 * @author qontology
 * @date 2025-12-23
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModelDO> implements IAiModelService {

    @Resource
    private AiModelMapper modelMapper;
    @Resource
    private AiApiKeyMapper apiKeyMapper;
    @Resource
    private IRedisService redisService;

    private static final Integer STATUS_ENABLE = 1;// 状态-启用
    private static final Integer STATUS_DISABLE = 0;// 状态-未启用
    // 模型目录使用 DashScope 原生分页接口，不能复用对话的 OpenAI 兼容地址。
    private static final String TONG_YI_MODELS_URL = "https://dashscope.aliyuncs.com/api/v1/models";

    /**
     * 添加数据
     *
     * @param createReqVO AI 模型信息
     * @return modelId
     */
    @Override
    public Long createAiModel(AiModelSaveReqVO createReqVO) {
        AiModelDO entity = BeanUtils.toBean(createReqVO, AiModelDO.class);
        AiApiKeyDO apiKeyDO = apiKeyMapper.selectById(entity.getKeyId());
        entity.setPlatform(apiKeyDO.getPlatform());
        entity.setWorkspaceId(apiKeyDO.getWorkspaceId());
        modelMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 删除AI 模型
     *
     * @param keyId     密钥id
     * @param modelName 模型名称
     */
    @Override
    public int removeAiModel(Long keyId, String modelName) {
        LambdaQueryWrapper<AiModelDO> queryWrapper = Wrappers.<AiModelDO>lambdaQuery()
                .eq(AiModelDO::getKeyId, keyId)
                .eq(AiModelDO::getName, modelName);
        // 批量删除AI 模型
        return modelMapper.delete(queryWrapper);
    }

    /**
     * 修改模型的状态，是否启用
     *
     * @param aiModel 根据keyId 和 模型名进行修改改
     * @return 操作是否成功
     */
    @Override
    public Boolean changeModelEnable(AiModelSaveReqVO aiModel) {
        LambdaUpdateWrapper<AiModelDO> updateWrapper = Wrappers.<AiModelDO>lambdaUpdate()
                .eq(AiModelDO::getName, aiModel.getName())
                .eq(AiModelDO::getKeyId, aiModel.getKeyId())
                .set(AiModelDO::getStatus, aiModel.getStatus());
        return modelMapper.update(updateWrapper) > 0;
    }

    @Override
    public AiModelRef getModelRef(Long keyId, String modelName) {
        AiApiKeyDO aiApiKeyDO = apiKeyMapper.selectById(keyId);
        if (aiApiKeyDO == null) {
            throw new ServiceException("获取模型失败，请检查模型配置");
        }
        AiPlatformEnum.validatePlatform(aiApiKeyDO.getPlatform());
        return new AiModelRef(String.valueOf(keyId), modelName);
    }

    /**
     * 获取模型字典
     *
     * @param type 模型类型
     */
    @Override
    public JSONArray getModelDict(Integer type) {
        LambdaQueryWrapper<AiModelDO> queryWrapper = Wrappers.<AiModelDO>lambdaQuery()
                .eq(AiModelDO::getType, type);
        if (Objects.equals(type, AiModelTypeEnum.RERANK.getType())) {
            queryWrapper.eq(AiModelDO::getPlatform, AiPlatformEnum.TONG_YI.getPlatform());
        }
        Map<Long, String> apiKeyMap = apiKeyMapper.selectList().stream()
                .collect(Collectors.toMap(AiApiKeyDO::getId, AiApiKeyDO::getName));
        List<AiModelDO> modelDOList = baseMapper.selectList(queryWrapper);
        JSONArray result = new JSONArray();
        Map<JSONObject, List<JSONObject>> resultMap = new HashMap<>();
        for (AiModelDO aiModelDO : modelDOList) {
            JSONObject key = new JSONObject();
            String label = apiKeyMap.get(aiModelDO.getKeyId());
            if (StrUtil.isBlank(label)) {
                continue;
            }
            key.put("label", Map.of("zh_Hans", label));
            key.put("provider", aiModelDO.getKeyId());
            List<JSONObject> valueList = resultMap.get(key);
            if (CollUtil.isEmpty(valueList)) {
                valueList = new ArrayList<>();
            }
            JSONObject valObj = new JSONObject();
            valObj.put("model", aiModelDO.getName());
            valueList.add(valObj);
            resultMap.put(key, valueList);
        }

        for (Map.Entry<JSONObject, List<JSONObject>> entry : resultMap.entrySet()) {
            JSONObject key = entry.getKey();
            key.put("models", entry.getValue());
            result.add(key);
        }
        return result;
    }

    /**
     * 获取模型分页列表
     * 首先先进行数据的同步，将平台中的所有模型都同步到表中，默认是启用的状态
     *
     * @param modelPage 模型分页参数
     * @return 模型分页结果
     */
    @Override
    public PageResult<AiModelDO> getModelPage(AiModelPageReqVO modelPage) {
        if (Objects.isNull(modelPage.getKeyId())) {
            return new PageResult<>(0L);
        }
        AiApiKeyDO aiApiKeyDO = apiKeyMapper.selectById(modelPage.getKeyId());
        // 历史版本可能把失败请求当作空列表并标记为已同步，空目录需重新拉取。
        boolean retryEmptyTongYi = AiPlatformEnum.TONG_YI.getPlatform().equalsIgnoreCase(aiApiKeyDO.getPlatform())
                && ApiKeyStatus.SYNC.getCode().equals(aiApiKeyDO.getStatus())
                && modelMapper.selectCount(Wrappers.<AiModelDO>lambdaQuery()
                        .eq(AiModelDO::getKeyId, aiApiKeyDO.getId())) == 0;
        // 需要同步数据
        if (ApiKeyStatus.CONFIG.getCode().equals(aiApiKeyDO.getStatus()) || retryEmptyTongYi) {
            // 首先进行数据同步
            Boolean syncResult = syncModel(aiApiKeyDO);
            if (!syncResult) {
                throw new ServiceException("同步数据失败");
            }
        }
        return baseMapper.selectPage(modelPage);
    }

    /**
     * 同步平台下的模型数据
     *
     * @param aiApiKeyDO apiKey 对象
     * @return 操作是否成功
     */
    private Boolean syncModel(AiApiKeyDO aiApiKeyDO) {
        String redisLock = redisService.get("redisLock");
        if (StrUtil.isNotBlank(redisLock)) {
            return true;
        }
        redisService.set("redisLock", "1");
        try {
            List<AiModelDO> aiModelDOList = this.queryListByKeyId(aiApiKeyDO.getId());
            Set<String> sqlSet = aiModelDOList.stream().map(AiModelDO::getName).collect(Collectors.toSet());

            AiPlatformEnum aiPlatformEnum = AiPlatformEnum.validatePlatform(aiApiKeyDO.getPlatform());
            JSONArray modelJsonArray = this.getModelsUrl(aiPlatformEnum, aiApiKeyDO);
            Set<String> modelSet = new HashSet<>();
            Set<String> tempModelSet = new HashSet<>();
            for (int i = 0; i < modelJsonArray.size(); i++) {
                JSONObject jsonObject1 = modelJsonArray.getJSONObject(i);
                String modeName = jsonObject1.getString("id");
                modelSet.add(modeName);
                tempModelSet.add(modeName);
            }

            modelSet.removeAll(sqlSet);// 需要添加的
            sqlSet.removeAll(tempModelSet);// 需要删除的

            this.saveBatch(aiApiKeyDO, modelSet);
            this.removeBatch(aiApiKeyDO, sqlSet);
            LambdaUpdateWrapper<AiApiKeyDO> updateWrapper = Wrappers.<AiApiKeyDO>lambdaUpdate()
                    .eq(AiApiKeyDO::getId, aiApiKeyDO.getId())
                    .set(AiApiKeyDO::getStatus, ApiKeyStatus.SYNC.getCode());
            apiKeyMapper.update(updateWrapper);
        } catch (Exception e) {
            log.error("同步模型数据失败，平台：{}，密钥ID：{}", aiApiKeyDO.getPlatform(), aiApiKeyDO.getId(), e);
            throw new ServiceException("同步模型数据失败");
        } finally {
            redisService.delete("redisLock");
        }

        return true;
    }

    /**
     * 根据 keyId 查询模型列表
     *
     * @param keyId 密钥 id
     * @return 模型列表
     */
    private List<AiModelDO> queryListByKeyId(Long keyId) {
        LambdaQueryWrapper<AiModelDO> queryWrapper = Wrappers.<AiModelDO>lambdaQuery()
                .eq(AiModelDO::getKeyId, keyId);
        return modelMapper.selectList(queryWrapper);
    }

    /**
     * 获取平台中的所有可用模型列表
     *
     * @param aiPlatform ai大模型平台
     * @param apiKeyDO   apiKey 对象
     * @return 可用模型列表
     * [
     * {
     * "id": "nomic-embed-text:latest",
     * "object": "model",
     * "created": 1765938001,
     * "owned_by": "library"
     * }
     * ]
     */
    private JSONArray getModelsUrl(AiPlatformEnum aiPlatform, AiApiKeyDO apiKeyDO) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        header.put("Authorization", "Bearer " + apiKeyDO.getApiKey());

        String baseUrl;
        if (Objects.equals(aiPlatform, AiPlatformEnum.TONG_YI)) {
            return this.getTongYiModel(header);
        }
        switch (aiPlatform) {
            case OLLAMA, OPENAI -> baseUrl = apiKeyDO.getUrl() + "/v1";
            default -> baseUrl = aiPlatform.getOpenAiUrl();
        }
        HttpRequest get = HttpUtil.createGet(baseUrl + "/models");
        get.addHeaders(header);
        HttpResponse response;
        try {
            response = get.execute();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("获取模型列表失败");
        }
        if (Objects.isNull(response) || !response.isOk()) {
            throw new ServiceException("获取模型列表失败");
        }

        JSONObject jsonObject = JSONObject.parseObject(response.body());
        return jsonObject.getJSONArray("data");
    }

    /**
     * 查询模型类型
     *
     * @return 模型字典key
     */
    private Integer getModelType(String modelName) {
        modelName = modelName.toLowerCase();
        if (modelName.contains("tts")) {
            return AiModelTypeEnum.VOICE.getType();
        } else if (modelName.contains("embed")) {
            return AiModelTypeEnum.EMBEDDING.getType();
        } else if (modelName.contains("image")) {
            return AiModelTypeEnum.IMAGE.getType();
        } else if (modelName.contains("rerank")) {
            return AiModelTypeEnum.RERANK.getType();
        }
        return AiModelTypeEnum.CHAT.getType();
    }

    /**
     * 批量新增数据
     *
     * @param aiApiKeyDO    apiKey 对象
     * @param modelNameColl 需要新增的模型名称集合
     */
    private void saveBatch(AiApiKeyDO aiApiKeyDO, Collection<String> modelNameColl) {
        if (CollUtil.isEmpty(modelNameColl)) {
            return;
        }
        List<AiModelDO> saveList = new ArrayList<>(modelNameColl.size());
        for (String modelName : modelNameColl) {
            AiModelDO entity = new AiModelDO();
            entity.setWorkspaceId(aiApiKeyDO.getWorkspaceId());
            entity.setKeyId(aiApiKeyDO.getId());
            entity.setName(modelName);
//            entity.setModel(modelName);
            entity.setPlatform(aiApiKeyDO.getPlatform());
            entity.setType(this.getModelType(modelName));
            entity.setStatus(STATUS_ENABLE);// 默认是启用的

            saveList.add(entity);
        }
        super.saveBatch(saveList);

    }

    /**
     * 批量删除数据
     *
     * @param aiApiKeyDO    apiKey 对象
     * @param modelNameColl 需要删除的模型名称集合
     */
    private void removeBatch(AiApiKeyDO aiApiKeyDO, Collection<String> modelNameColl) {
        if (CollUtil.isEmpty(modelNameColl)) {
            return;
        }
        LambdaQueryWrapper<AiModelDO> queryWrapper = Wrappers.<AiModelDO>lambdaQuery()
                .eq(AiModelDO::getKeyId, aiApiKeyDO.getId())
                .in(AiModelDO::getName, modelNameColl);
        modelMapper.delete(queryWrapper);
    }

    /**
     * 获取通义千问的模型列表，需要分页获取
     *
     * @param header 请求头
     * @return 模型列表
     */
    private JSONArray getTongYiModel(Map<String, String> header) {
        String url = TONG_YI_MODELS_URL + "?page_size={}&page_no={}";
        int pageNo = 1;
        int pageSize = 200;
        JSONArray result = new JSONArray();
        while (true) {
            HttpRequest get = HttpUtil.createGet(StrUtil.format(url, pageSize, pageNo));
            get.addHeaders(header);
            get.timeout(30000);
            try (HttpResponse response = get.execute()) {
                if (!response.isOk()) {
                    throw new ServiceException("获取通义千问模型列表失败，HTTP状态码：" + response.getStatus());
                }
                JSONObject jsonObject = JSONObject.parseObject(response.body());
                JSONObject output = jsonObject == null ? null : jsonObject.getJSONObject("output");
                Integer total = output == null ? null : output.getInteger("total");
                JSONArray modelArray = output == null ? null : output.getJSONArray("models");
                if (total == null || total < 0 || modelArray == null
                        || (modelArray.isEmpty() && result.size() < total)) {
                    throw new ServiceException("获取通义千问模型列表失败，响应数据不完整");
                }

                for (int i = 0; i < modelArray.size(); i++) {
                    JSONObject model = modelArray.getJSONObject(i);
                    String modelName = model == null ? null : model.getString("model");
                    if (StrUtil.isBlank(modelName)) {
                        throw new ServiceException("获取通义千问模型列表失败，模型名称为空");
                    }
                    result.add(Map.of("id", modelName));
                }
                if ((long) pageNo * pageSize >= total) {
                    break;
                }
            }
            pageNo++;
        }
        return result;
    }

}
