package tech.qiantong.qontology.module.ai.service.modelMarket.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONArray;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;
import tech.qiantong.qontology.common.exception.ServiceException;

import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AiModelServiceImplTest {
    private static final String MODELS_URL = "https://dashscope.aliyuncs.com/api/v1/models?page_size=200&page_no=";
    private final AiModelServiceImpl service = new AiModelServiceImpl();
    private final Map<String, String> headers = Map.of("Authorization", "Bearer test-key");

    @Test
    void usesNativeEndpointAndStopsAtExactPageBoundary() {
        JSONArray models = new JSONArray();
        IntStream.range(0, 200).forEach(i -> models.add(Map.of("model", "qwen-" + i)));
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            HttpRequest request = request(http, 1, 200,
                    "{\"output\":{\"total\":200,\"models\":" + models + "}}");

            JSONArray result = fetch();

            assertEquals(200, result.size());
            assertEquals("qwen-0", result.getJSONObject(0).getString("id"));
            verify(request).addHeaders(headers);
            http.verify(() -> HttpUtil.createGet(MODELS_URL + 1));
            http.verifyNoMoreInteractions();
        }
    }

    @Test
    void collectsAllPages() {
        JSONArray models = new JSONArray();
        IntStream.range(0, 200).forEach(i -> models.add(Map.of("model", "qwen-" + i)));
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            request(http, 1, 200, "{\"output\":{\"total\":201,\"models\":" + models + "}}");
            request(http, 2, 200, "{\"output\":{\"total\":201,\"models\":[{\"model\":\"qwen-last\"}]}}");

            JSONArray result = fetch();

            assertEquals(201, result.size());
            assertEquals("qwen-last", result.getJSONObject(200).getString("id"));
        }
    }

    @Test
    void rejectsFailedPageInsteadOfReturningPartialModels() {
        JSONArray models = new JSONArray();
        IntStream.range(0, 200).forEach(i -> models.add(Map.of("model", "qwen-" + i)));
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            request(http, 1, 200, "{\"output\":{\"total\":201,\"models\":" + models + "}}");
            request(http, 2, 401, "{\"code\":\"InvalidApiKey\"}");

            assertThrows(ServiceException.class, this::fetch);
        }
    }

    @Test
    void rejectsInvalidResponseInsteadOfReturningEmptyModels() {
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            request(http, 1, 200, "{\"data\":[]}");
            assertThrows(ServiceException.class, this::fetch);
        }
    }

    @Test
    void rejectsPrematureEmptyPage() {
        try (MockedStatic<HttpUtil> http = mockStatic(HttpUtil.class)) {
            request(http, 1, 200, "{\"output\":{\"total\":1,\"models\":[]}}");
            assertThrows(ServiceException.class, this::fetch);
        }
    }

    private JSONArray fetch() {
        return ReflectionTestUtils.invokeMethod(service, "getTongYiModel", headers);
    }

    private HttpRequest request(MockedStatic<HttpUtil> http, int page, int status, String body) {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);
        http.when(() -> HttpUtil.createGet(MODELS_URL + page)).thenReturn(request);
        when(request.execute()).thenReturn(response);
        when(response.isOk()).thenReturn(status == 200);
        when(response.getStatus()).thenReturn(status);
        when(response.body()).thenReturn(body);
        return request;
    }
}
