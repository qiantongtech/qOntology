package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * AI 辅助建模提示词文件加载器。
 */
@Component
public class OmAiPromptLoader {

    private static final String PROMPT_ROOT = "prompts/ai-modeling/";

    /**
     * 加载指定提示词文件。
     *
     * @param resourceName 提示词文件名
     * @return 提示词内容
     */
    public String load(String resourceName) {
        ClassPathResource resource = new ClassPathResource(PROMPT_ROOT + resourceName);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("提示词文件读取失败：" + resourceName, exception);
        }
    }
}
