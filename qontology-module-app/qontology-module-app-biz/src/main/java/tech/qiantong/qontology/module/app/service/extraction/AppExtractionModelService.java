package tech.qiantong.qontology.module.app.service.extraction;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.ai.api.model.AiModelRef;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 知识抽取大模型服务，统一维护结构化与非结构化抽取使用的模型和提示词。
 */
@Slf4j
@Service
public class AppExtractionModelService {
    /** 模型平台配置编号，对应 ai_api_key 表主键。 */
    private static final String MODEL_PLATFORM_ID = "1";
    /** 知识抽取使用的大模型名称。 */
    private static final String MODEL_NAME = "qwen-max";
    /** 按完整 TTL 内容缓存，修改本体后自动失效，限制缓存数量防止无限增长。 */
    private final Map<String, String> schemaCache = new LinkedHashMap<>(16, 0.75f, true);
    private volatile String promptTemplate;

    @Resource
    private AiChatService aiChatService;

    @Value("classpath:prompts/extraction-prompt.txt")
    private org.springframework.core.io.Resource extractionPrompt;

    /**
     * 根据 TTL 本体和文本分段执行知识抽取。
     *
     * @param ttl TTL 本体内容
     * @param segment 待抽取的文本分段
     * @return 标准实体和关系结果
     */
    public JSONObject extract(String ttl, String segment) {
        String schema = compactSchema(ttl);
        String prompt = buildPrompt(schema);
        log.info("开始知识抽取，模型={}，原本体字符数={}，压缩后字符数={}，文本字符数={}，请求字符数={}",
                MODEL_NAME, ttl.length(), schema.length(), segment.length(), prompt.length() + segment.length());
        AiChatRequest request = new AiChatRequest(
                new AiModelRef(MODEL_PLATFORM_ID, MODEL_NAME),
                List.of(AiChatMessage.system(prompt), AiChatMessage.user(segment)),
                0.7, null, Map.of("enable_thinking", false));
        AiChatResponse response = aiChatService.chat(request);
        if (response == null || StringUtils.isBlank(response.content())) {
            throw new ServiceException("大模型未返回抽取结果");
        }
        log.info("大模型抽取完成，平台={}，模型={}，耗时={}ms",
                response.providerCode(), response.modelName(), response.durationMillis());
        return normalizeResult(response.content());
    }

    private synchronized String compactSchema(String ttl) {
        String cached = schemaCache.get(ttl);
        if (cached != null) {
            return cached;
        }
        String compact = ExtractionSchemaCompactor.compact(ttl);
        if (schemaCache.size() >= 16) {
            schemaCache.remove(schemaCache.keySet().iterator().next());
        }
        schemaCache.put(ttl, compact);
        return compact;
    }

    /** 稳定的规则和本体作为系统消息，文本单独作为用户消息，便于供应商复用前缀。 */
    private String buildPrompt(String schema) {
        if (promptTemplate == null) {
            synchronized (this) {
                if (promptTemplate == null) {
                    try (java.io.InputStream input = extractionPrompt.getInputStream()) {
                        promptTemplate = new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                    } catch (java.io.IOException e) {
                        throw new ServiceException("读取知识抽取提示词失败");
                    }
                }
            }
        }
        return promptTemplate.replace("{{TTL_SCHEMA}}", schema);
    }

    /** 将大模型回答统一为实体和关系结果格式。 */
    private JSONObject normalizeResult(String output) {
        JSONObject result = JSONObject.parseObject(output);
        if (result == null) {
            result = new JSONObject();
        }
        if (result.getJSONArray("entities") == null) {
            result.put("entities", new JSONArray());
        }
        if (result.getJSONArray("relations") == null) {
            result.put("relations", new JSONArray());
        }
        return result;
    }
}
