package tech.qiantong.qontology.module.om.enums.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 上传文件解析状态。 */
@Getter
@AllArgsConstructor
public enum OmAiFileParseStatusEnum {

    WAITING(0, "待解析"),
    RUNNING(1, "解析中"),
    SUCCESS(2, "解析成功"),
    FAILED(3, "解析失败");

    private final Integer code;
    private final String label;
}
