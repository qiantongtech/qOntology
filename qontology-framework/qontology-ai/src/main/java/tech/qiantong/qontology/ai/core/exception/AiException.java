package tech.qiantong.qontology.ai.core.exception;

import tech.qiantong.qontology.ai.enums.AiErrorCode;

/**
 * AI 统一运行时异常。
 *
 * @author qontology
 * @date 2026-10-14
 */
public class AiException extends RuntimeException {
    private final AiErrorCode code;

    public AiException(AiErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public AiException(AiErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public AiErrorCode getCode() {
        return code;
    }
}
