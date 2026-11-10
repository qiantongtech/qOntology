package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.module.om.enums.ai.OmAiFileTypeEnum;

/** 将上传文件解析为带原文位置的原子证据块。 */
@Component
public class OmAiDocumentParser {
    /** 单个证据块保持在较小语义范围内，避免一份短文档整体成为一个证据块。 */
    private static final int MAX_BLOCK_CHARACTERS = 1800;

    /**
     * 文档原子证据块。
     *
     * @param no 文件内块序号
     * @param type 块类型
     * @param pageNo 页码
     * @param chapterPath 章节路径或工作表名称
     * @param paragraphNo 段落序号
     * @param tableNo 表格序号
     * @param rowNo 行号
     * @param columnNo 列号
     * @param imageNo 图片序号
     * @param lineStart 起始行号
     * @param lineEnd 结束行号
     * @param content 原始内容
     */
    public record Block(
            int no,
            String type,
            Integer pageNo,
            String chapterPath,
            Integer paragraphNo,
            Integer tableNo,
            Integer rowNo,
            Integer columnNo,
            Integer imageNo,
            Integer lineStart,
            Integer lineEnd,
            String content) {
    }

    /** 根据文件扩展名选择对应解析策略。 */
    public List<Block> parse(MultipartFile file) throws IOException {
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown");
        String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        return switch (OmAiFileTypeEnum.fromExtension(extension)) {
            case TXT, HTML, HTM, CSV, MD, MDX, MARKDOWN, TTL -> parseText(file);
            case DOCX -> parseDocx(file);
            case PDF -> parsePdf(file);
            case XLS, XLSX -> parseExcel(file);
        };
    }

    /** 解析已持久化文件的字节内容。 */
    public List<Block> parse(String fileName, byte[] content) throws IOException {
        return parse(new StoredMultipartFile(fileName, content));
    }

    /** 提供可重复读取的持久化文件适配器。 */
    private record StoredMultipartFile(String originalFilename, byte[] content) implements MultipartFile {
        @Override public String getName() { return "file"; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return null; }
        @Override public boolean isEmpty() { return content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() { return content.clone(); }
        @Override public java.io.InputStream getInputStream() { return new ByteArrayInputStream(content); }
        @Override public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }

    /** 按每四十行切分纯文本，保留起止行号。 */
    private List<Block> parseText(MultipartFile file) throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().toList();
        }

        List<Block> result = new ArrayList<>();
        int blockNo = 1;
        for (int start = 0; start < lines.size(); start += 40) {
            int end = Math.min(lines.size(), start + 40);
            String content = String.join("\n", lines.subList(start, end)).trim();
            if (!content.isBlank()) {
                result.add(new Block(
                        blockNo++, "TEXT", null, null, null, null, null, null, null,
                        start + 1, end, content));
            }
        }
        return result;
    }

    /** 解析 Word 2007+ 段落、表格单元格和内嵌图片占位。 */
    private List<Block> parseDocx(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        int blockNo = 1;
        int paragraphNo = 0;
        int tableNo = 0;
        String chapter = null;

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph paragraph) {
                    paragraphNo++;
                    String text = paragraph.getText().trim();
                    if (text.isBlank()) continue;
                    String style = paragraph.getStyle();
                    if (style != null && style.toLowerCase().contains("heading")) {
                        chapter = text;
                        result.add(new Block(blockNo++, "HEADING", null, chapter, paragraphNo,
                                null, null, null, null, null, null, text));
                    } else {
                        // 部分业务 Word 全文只有一个物理段落，通过手动换行分隔章节和正文。
                        // 必须继续按内部换行拆分，否则页面会显示成一个超大“第1段”。
                        String[] logicalParagraphs = text.split("\\R+");
                        for (String logicalParagraph : logicalParagraphs) {
                            String content = logicalParagraph.trim();
                            if (content.isBlank()) {
                                continue;
                            }
                            if (isSemanticHeading(content)) {
                                chapter = content;
                                result.add(new Block(blockNo++, "HEADING", null, chapter, paragraphNo,
                                        null, null, null, null, null, null, content));
                            } else {
                                blockNo = appendTextChunks(result, blockNo, "PARAGRAPH", content,
                                        null, chapter, paragraphNo, null, null);
                            }
                        }
                    }
                } else if (element instanceof XWPFTable table) {
                    tableNo++;
                    blockNo = appendDocxTable(result, table, blockNo, tableNo, chapter);
                }
            }
            int imageNo = 0;
            for (var picture : document.getAllPictures()) {
                result.add(new Block(blockNo++, "IMAGE", null, chapter, null, null, null, null,
                        ++imageNo, null, null,
                        "[Word内嵌图片：" + picture.getFileName() + "，等待多模态模型识别]"));
            }
        }
        return result;
    }

    /** 识别没有套用 Word 标题样式的中文章节标题。 */
    private boolean isSemanticHeading(String text) {
        if (text == null || text.length() > 50) {
            return false;
        }
        return text.matches("^(第[一二三四五六七八九十百]+[章节部分]|[一二三四五六七八九十]+[、.]|\\d+[、.．])\\s*.*")
                || text.matches("^[（(][一二三四五六七八九十\\d]+[）)]\\s*.*");
    }

    /**
     * 超长段落优先在换行、句号、分号等语义边界切分，只有找不到边界时才硬切。
     */
    private int appendTextChunks(List<Block> result, int blockNo, String type, String text,
                                 Integer pageNo, String chapter, Integer paragraphNo,
                                 Integer tableNo, Integer rowNo) {
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + MAX_BLOCK_CHARACTERS);
            if (end < text.length()) {
                int boundary = findSplitBoundary(text, start, end);
                if (boundary > start) {
                    end = boundary;
                }
            }
            String content = text.substring(start, end).trim();
            if (!content.isBlank()) {
                result.add(new Block(blockNo++, type, pageNo, chapter, paragraphNo, tableNo, rowNo,
                        null, null, null, null, content));
            }
            start = end;
        }
        return blockNo;
    }

    private int findSplitBoundary(String text, int start, int suggestedEnd) {
        int minimum = start + MAX_BLOCK_CHARACTERS / 2;
        for (int index = suggestedEnd - 1; index >= minimum; index--) {
            char character = text.charAt(index);
            if (character == '\n' || character == '。' || character == '；'
                    || character == '！' || character == '？' || character == ';') {
                return index + 1;
            }
        }
        return suggestedEnd;
    }

    /** 将 Word 表格按行转换为证据块，保留同一行字段之间的业务关系。 */
    private int appendDocxTable(List<Block> result, XWPFTable table, int blockNo, int tableNo, String chapter) {
        int rowNo = 0;
        for (XWPFTableRow row : table.getRows()) {
            rowNo++;
            List<String> cells = new ArrayList<>();
            for (XWPFTableCell cell : row.getTableCells()) {
                String text = cell.getText().trim();
                cells.add(text);
            }
            String content = String.join(" | " , cells).trim();
            if (!content.replace("|", "").isBlank()) {
                result.add(new Block(blockNo++, "TABLE_ROW", null, chapter, null, tableNo,
                        rowNo, null, null, null, null, content));
            }
        }
        return blockNo;
    }

    /** 按页解析 PDF；无文本页作为多模态识别占位。 */
    private List<Block> parsePdf(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            int blockNo = 1;
            for (int pageNo = 1; pageNo <= document.getNumberOfPages(); pageNo++) {
                stripper.setStartPage(pageNo);
                stripper.setEndPage(pageNo);
                String text = stripper.getText(document).trim();
                if (text.isBlank()) {
                    result.add(imagePlaceholder(blockNo++, pageNo, null, "扫描页或图片页"));
                } else {
                    blockNo = appendTextChunks(result, blockNo, "PAGE", text,
                            pageNo, null, null, null, null);
                }
            }
        }
        return result;
    }

    /** 按工作表和行解析 Excel，重复携带表头以保留列名与字段值的对应关系。 */
    private List<Block> parseExcel(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            int blockNo = 1;
            int tableNo = 0;
            for (Sheet sheet : workbook) {
                tableNo++;
                List<String> headers = new ArrayList<>();
                Row headerRow = sheet.getRow(sheet.getFirstRowNum());
                if (headerRow != null) {
                    for (Cell cell : headerRow) headers.add(formatter.formatCellValue(cell).trim());
                }
                for (Row row : sheet) {
                    List<String> values = new ArrayList<>();
                    for (Cell cell : row) {
                        String text = formatter.formatCellValue(cell).trim();
                        if (!text.isBlank()) {
                            String header = cell.getColumnIndex() < headers.size()
                                    ? headers.get(cell.getColumnIndex()) : "第" + (cell.getColumnIndex() + 1) + "列";
                            values.add((header == null || header.isBlank() ? "第" + (cell.getColumnIndex() + 1) + "列" : header)
                                    + "=" + text);
                        }
                    }
                    if (!values.isEmpty()) {
                        result.add(new Block(blockNo++, "SHEET_ROW", null, sheet.getSheetName(),
                                null, tableNo, row.getRowNum() + 1, null, null, null, null,
                                String.join("；", values)));
                    }
                }
            }
        }
        return result;
    }

    /** 创建等待多模态模型识别的图片证据占位。 */
    private Block imagePlaceholder(int blockNo, Integer pageNo, Integer imageNo, String description) {
        return new Block(blockNo, "IMAGE", pageNo, null, null, null, null, null, imageNo,
                null, null, "[" + description + "，等待多模态模型识别]");
    }
}
