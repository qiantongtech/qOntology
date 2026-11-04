package tech.qiantong.qontology.module.om.enums.ai;

import java.util.Arrays;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.qiantong.qontology.common.exception.ServiceException;

/** AI 辅助建模支持的上传文件类型。 */
@Getter
@AllArgsConstructor
public enum OmAiFileTypeEnum {

    TXT("txt", "TXT 文本"),
    PDF("pdf", "PDF 文档"),
    HTML("html", "HTML 文档"),
    XLSX("xlsx", "Excel 工作簿"),
    XLS("xls", "Excel 工作簿"),
    DOCX("docx", "Word 文档"),
    CSV("csv", "CSV 文本"),
    MD("md", "Markdown 文档"),
    MDX("mdx", "MDX 文档"),
    HTM("htm", "HTML 文档"),
    MARKDOWN("markdown", "Markdown 文档"),
    TTL("ttl", "Turtle 文档");

    private final String extension;
    private final String label;

    /** 根据文件后缀获得文件类型。 */
    public static OmAiFileTypeEnum fromExtension(String extension) {
        String normalized = extension == null ? "" : extension.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(item -> item.extension.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new ServiceException("不支持的文件格式：" + normalized));
    }
}
