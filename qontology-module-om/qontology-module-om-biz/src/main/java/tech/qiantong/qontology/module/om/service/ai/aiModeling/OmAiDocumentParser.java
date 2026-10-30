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
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
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

/** 将上传文件解析为带原文位置的原子证据块。 */
@Component
public class OmAiDocumentParser {

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
        return switch (extension) {
            case "txt" -> parseText(file);
            case "docx" -> parseDocx(file);
            case "doc" -> parseDoc(file);
            case "pdf" -> parsePdf(file);
            case "xls", "xlsx" -> parseExcel(file);
            case "png", "jpg", "jpeg" -> List.of(imagePlaceholder(1, 1, 1, "图片文件"));
            default -> throw new IllegalArgumentException("不支持的文件格式：" + extension);
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
                    }
                    result.add(new Block(blockNo++, "PARAGRAPH", null, chapter, paragraphNo,
                            null, null, null, null, null, null, text));
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

    /** 将 Word 表格按单元格转换为证据块。 */
    private int appendDocxTable(List<Block> result, XWPFTable table, int blockNo, int tableNo, String chapter) {
        int rowNo = 0;
        for (XWPFTableRow row : table.getRows()) {
            rowNo++;
            int columnNo = 0;
            for (XWPFTableCell cell : row.getTableCells()) {
                columnNo++;
                String text = cell.getText().trim();
                if (!text.isBlank()) {
                    result.add(new Block(blockNo++, "TABLE_CELL", null, chapter, null, tableNo,
                            rowNo, columnNo, null, null, null, text));
                }
            }
        }
        return blockNo;
    }

    /** 解析 Word 97-2003 段落。 */
    private List<Block> parseDoc(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        try (HWPFDocument document = new HWPFDocument(file.getInputStream());
             WordExtractor extractor = new WordExtractor(document)) {
            int blockNo = 1;
            int paragraphNo = 0;
            for (String value : extractor.getParagraphText()) {
                paragraphNo++;
                String text = value.trim();
                if (!text.isBlank()) {
                    result.add(new Block(blockNo++, "PARAGRAPH", null, null, paragraphNo,
                            null, null, null, null, null, null, text));
                }
            }
        }
        return result;
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
                    result.add(new Block(blockNo++, "PAGE", pageNo, null, null, null, null, null,
                            null, null, null, text));
                }
            }
        }
        return result;
    }

    /** 按工作表、行、列解析 Excel 单元格。 */
    private List<Block> parseExcel(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            int blockNo = 1;
            int tableNo = 0;
            for (Sheet sheet : workbook) {
                tableNo++;
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        String text = formatter.formatCellValue(cell).trim();
                        if (!text.isBlank()) {
                            result.add(new Block(blockNo++, "SHEET_CELL", null, sheet.getSheetName(),
                                    null, tableNo, row.getRowNum() + 1, cell.getColumnIndex() + 1,
                                    null, null, null, text));
                        }
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
