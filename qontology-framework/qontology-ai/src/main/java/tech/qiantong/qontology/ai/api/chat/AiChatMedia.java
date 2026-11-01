package tech.qiantong.qontology.ai.api.chat;

import java.util.Objects;

/** 多模态消息媒体内容，data 为原始字节。 */
public record AiChatMedia(String mimeType, byte[] data, String name) {
    public AiChatMedia {
        if (mimeType == null || mimeType.isBlank()) throw new IllegalArgumentException("mimeType 不能为空");
        Objects.requireNonNull(data, "data 不能为空");
        data = data.clone();
    }

    @Override public byte[] data() { return data.clone(); }
}
