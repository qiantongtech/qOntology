package tech.qiantong.qontology.server.ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.qiantong.qontology.ai.api.chat.AiChatChunk;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatResponse;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.ai.api.model.AiModelRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 通义千问问答集成测试。
 *
 * <p>启动完整 Spring 容器，通过 {@link AiChatService} 公共 API 调用问答——
 * 这正是业务模块使用 AI 能力的方式：模型与密钥已在系统（数据库 ai_api_key 表）中配置好，
 * 这里只负责注入服务、构造请求、发起调用。
 *
 * <p>运行前提：dev 环境的 MySQL、Redis 可连接，且 ai_api_key 表中存在通义千问的可用凭证。
 *
 * <p>本地运行：
 * <pre>
 *   mvn -pl qontology-server test -Dtest=AiChatServiceTest
 * </pre>
 *
 * @author qontology
 * @date 2026-10-15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class AiChatServiceTest {
    /**
     * ai_api_key 表中通义千问凭证记录的主键 ID。
     * 改成你自己数据库里对应记录的 ID。
     */
    private static final String CONFIG_ID = "1";
    private static final String MODEL_NAME = "qwen-max";

    @Autowired
    private AiChatService aiChatService;

    /**
     * 同步问答：问"中国的首都是哪里？"，验证通义千问能正常返回。
     */
    @Test
    void chat() {
        AiChatRequest request = new AiChatRequest(
                new AiModelRef(CONFIG_ID, MODEL_NAME),
                List.of(
                        AiChatMessage.system("你是一个简洁的助手，回答尽量短"),
                        AiChatMessage.user("中国的首都是哪里？")),
                0.7, null, Map.of());

        AiChatResponse response = aiChatService.chat(request);

        System.out.println("===== 通义千问回复 =====");
        System.out.println(response.content());
        System.out.println("供应商: " + response.providerCode() + ", 模型: " + response.modelName()
                + ", 耗时: " + response.durationMillis() + "ms");

        assertThat(response.content()).isNotBlank();
        assertThat(response.content()).contains("北京");
        assertThat(response.providerCode()).isEqualTo("tongyi");
    }

    /**
     * 流式问答："用一句话介绍杭州"，验证分片推送与结束标记。
     */
    @Test
    void stream() {
        AiChatRequest request = AiChatRequest.of(
                new AiModelRef(CONFIG_ID, MODEL_NAME),
                "请详细介绍杭州");

        List<AiChatChunk> chunks = new ArrayList<>();
        aiChatService.stream(request, chunks::add);

        System.out.println("===== 流式分片 =====");
        chunks.forEach(chunk -> System.out.print(chunk.content()));
        System.out.println();

        assertThat(chunks).isNotEmpty();
        assertThat(chunks.get(chunks.size() - 1).finished()).isTrue();
    }
}
