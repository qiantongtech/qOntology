/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 */
package tech.qiantong.qontology.module.ai.dal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * qOntology 当前支持的 AI 模型平台。
 */
@Getter
@AllArgsConstructor
public enum AiPlatformEnum {

    // 平台描述说明接入方式及业务用途，供模型市场卡片和详情统一展示。
    TONG_YI("TongYi", "tongyi", "通义千问", "1,2,3,4,5,6",
            "接入阿里云通义千问系列模型，统一配置平台密钥并查看可用模型，为本体智能建模、业务文档理解和信息抽取等场景提供 AI 能力。", "https://dashscope.aliyuncs.com/compatible-mode"),
    DEEP_SEEK("DeepSeek", "deepseek", "DeepSeek", "1,2,3",
            "接入 DeepSeek 平台的对话与推理模型，配置密钥后可查看可用模型，供本体建模、业务语义分析和信息抽取等功能选择调用。", "https://api.deepseek.com/v1"),
    OPENAI("OpenAI", "openai", "OpenAI", "1,2,3,4,5,6",
            "接入 OpenAI 或兼容其接口规范的模型服务，统一维护 API 地址和密钥，为本体智能建模、文本理解及信息抽取等业务场景提供模型调用入口。", "https://api.openai.com"),
    OLLAMA("Ollama", "ollama", "Ollama", "1,2,3",
            "接入通过 Ollama 部署的本地模型服务，配置服务地址后查看可用模型，供本体智能建模、文档理解和信息抽取等功能使用本地推理能力。", "http://localhost:11434");

    private final String platform;
    private final String providerCode;
    private final String name;
    private final String platformTag;
    private final String description;
    private final String openAiUrl;

    public static final String[] ARRAYS = Arrays.stream(values())
            .map(AiPlatformEnum::getPlatform)
            .toArray(String[]::new);

    public static AiPlatformEnum validatePlatform(String platform) {
        return Arrays.stream(values())
                .filter(item -> item.platform.equalsIgnoreCase(platform))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("暂不支持 AI 平台: " + platform));
    }

    public String[] array() {
        return ARRAYS;
    }
}
