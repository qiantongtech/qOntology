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

    TONG_YI("TongYi", "tongyi", "通义千问", "1,2,3,4,5,6",
            "通义千问提供的模型。", "https://dashscope.aliyuncs.com/compatible-mode"),
    DEEP_SEEK("DeepSeek", "deepseek", "DeepSeek", "1,2,3",
            "深度求索提供的模型，例如 deepseek-chat、deepseek-coder。", "https://api.deepseek.com/v1"),
    OPENAI("OpenAI", "openai", "OpenAI", "1,2,3,4,5,6",
            "符合 OpenAI 规范的模型。", "https://api.openai.com"),
    OLLAMA("Ollama", "ollama", "Ollama", "1,2,3",
            "Ollama 提供的本地模型。", "http://localhost:11434");

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
