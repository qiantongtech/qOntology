package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** AI 数据规则支持的日期格式。 */
@Getter
@AllArgsConstructor
public enum OmAiDateFormatEnum {
    YEAR("yyyy", "年份"),
    YEAR_MONTH("yyyy-MM", "年月"),
    DATE("yyyy-MM-dd", "日期"),
    SLASH_DATE("yyyy/MM/dd", "斜线日期"),
    COMPACT_DATE("yyyyMMdd", "紧凑日期"),
    MINUTE("yyyy-MM-dd HH:mm", "日期时间（分钟）"),
    SECOND("yyyy-MM-dd HH:mm:ss", "日期时间（秒）"),
    SLASH_SECOND("yyyy/MM/dd HH:mm:ss", "斜线日期时间"),
    ISO_SECOND("yyyy-MM-dd'T'HH:mm:ss", "ISO 日期时间");

    private final String pattern;
    private final String label;

    /** 判断格式是否在系统允许范围内。 */
    public static boolean contains(String pattern) {
        return Arrays.stream(values()).anyMatch(item -> item.pattern.equals(pattern));
    }
}
