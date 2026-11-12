package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Iterator;
import javax.imageio.ImageIO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
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

    /** 仅在解析期间存在的文档内媒体，不直接作为独立上传文件保存。 */
    public record MediaBlock(Integer pageNo, String chapterPath, Integer paragraphNo, Integer anchorBlockNo,
                             Integer imageNo,
                             String mimeType, String name, byte[] data, String description) {
        public MediaBlock {
            data = data == null ? new byte[0] : data.clone();
        }
        @Override public byte[] data() { return data.clone(); }
    }

    public record ParseResult(List<Block> blocks, List<MediaBlock> media) {
        public ParseResult {
            blocks = List.copyOf(blocks);
            media = List.copyOf(media);
        }
    }

    /** 根据文件扩展名选择对应解析策略。 */
    public List<Block> parse(MultipartFile file) throws IOException {
        return parseWithMedia(file).blocks();
    }

    /** 同时返回文字证据和文档内部图片/扫描页媒体。 */
    public ParseResult parseWithMedia(MultipartFile file) throws IOException {
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("unknown");
        String extension = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                : "";
        return switch (OmAiFileTypeEnum.fromExtension(extension)) {
            case HTML, HTM -> new ParseResult(parseHtml(file), List.of());
            case CSV -> new ParseResult(parseCsv(file), List.of());
            case TXT, MD, MDX, MARKDOWN, TTL -> new ParseResult(parseText(file), List.of());
            case DOCX -> parseDocx(file);
            case PDF -> parsePdf(file);
            case XLS, XLSX -> new ParseResult(parseExcel(file), List.of());
        };
    }

    /** 解析已持久化文件的字节内容。 */
    public List<Block> parse(String fileName, byte[] content) throws IOException {
        return parse(new StoredMultipartFile(fileName, content));
    }

    public ParseResult parseWithMedia(String fileName, byte[] content) throws IOException {
        return parseWithMedia(new StoredMultipartFile(fileName, content));
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

    /** 按段落和字符预算切分纯文本，避免单行或四十行内容形成超大证据块。 */
    private List<Block> parseText(MultipartFile file) throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            lines = reader.lines().toList();
        }

        List<Block> result = new ArrayList<>();
        int blockNo = 1;
        int paragraphStart = -1;
        StringBuilder paragraph = new StringBuilder();
        for (int index = 0; index <= lines.size(); index++) {
            String line = index < lines.size() ? lines.get(index) : "";
            if (!line.isBlank()) {
                if (paragraphStart < 0) paragraphStart = index + 1;
                if (!paragraph.isEmpty()) paragraph.append('\n');
                paragraph.append(line);
                continue;
            }
            if (!paragraph.isEmpty()) {
                blockNo = appendTextChunks(result, blockNo, "TEXT", paragraph.toString(),
                        null, null, null, null, null, paragraphStart, index);
                paragraph.setLength(0);
                paragraphStart = -1;
            }
        }
        return result;
    }

    /** 使用 HTML 结构提取标题、正文、列表和表格行，不将标签噪声传给模型。 */
    private List<Block> parseHtml(MultipartFile file) throws IOException {
        String html = new String(file.getBytes(), StandardCharsets.UTF_8);
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        List<Block> result = new ArrayList<>();
        int blockNo = 1;
        String chapter = null;
        for (Element element : document.select("h1,h2,h3,h4,h5,h6,p,li,tr")) {
            String text;
            String type;
            if (element.tagName().matches("h[1-6]")) {
                text = element.text().trim();
                type = "HEADING";
                chapter = text;
            } else if ("tr".equals(element.tagName())) {
                text = element.select("th,td").eachText().stream()
                        .map(String::trim).filter(value -> !value.isBlank())
                        .collect(java.util.stream.Collectors.joining(" | "));
                type = "TABLE_ROW";
            } else {
                text = element.ownText().trim();
                if (text.isBlank()) text = element.text().trim();
                type = "HTML_TEXT";
            }
            if (!text.isBlank()) {
                blockNo = appendTextChunks(result, blockNo, type, text, null, chapter,
                        null, null, null);
            }
        }
        if (result.isEmpty() && document.body() != null) {
            appendTextChunks(result, blockNo, "HTML_TEXT", document.body().text(),
                    null, null, null, null, null);
        }
        return result;
    }

    /** 使用标准 CSV 解析器处理引号、逗号和单元格内换行，并按表头映射每一行。 */
    private List<Block> parseCsv(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            Iterator<CSVRecord> iterator = parser.iterator();
            if (!iterator.hasNext()) return result;
            List<String> headers = new ArrayList<>();
            iterator.next().forEach(value -> headers.add(value == null ? "" : value.trim()));
            int blockNo = 1;
            int recordIndex = 1;
            while (iterator.hasNext()) {
                CSVRecord record = iterator.next();
                List<String> values = new ArrayList<>();
                for (int column = 0; column < record.size(); column++) {
                    String value = record.get(column).trim();
                    if (value.isBlank()) continue;
                    String header = column < headers.size() && !headers.get(column).isBlank()
                            ? headers.get(column) : "第" + (column + 1) + "列";
                    values.add(header + "=" + value);
                }
                if (!values.isEmpty()) {
                    blockNo = appendTextChunks(result, blockNo, "CSV_ROW",
                            String.join("；", values), null, null, null, 1, recordIndex + 1);
                }
                recordIndex++;
            }
        }
        return result;
    }

    /** 解析 Word 2007+ 段落、表格单元格和内嵌图片占位。 */
    private ParseResult parseDocx(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        List<MediaBlock> media = new ArrayList<>();
        int blockNo = 1;
        int paragraphNo = 0;
        int tableNo = 0;
        int imageNo = 0;
        String chapter = null;

        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            for (IBodyElement element : document.getBodyElements()) {
                if (element instanceof XWPFParagraph paragraph) {
                    paragraphNo++;
                    String text = paragraph.getText().trim();
                    if (!text.isBlank()) {
                        String style = paragraph.getStyle();
                        if (style != null && style.toLowerCase().contains("heading")) {
                            chapter = text;
                            result.add(new Block(blockNo++, "HEADING", null, chapter, paragraphNo,
                                    null, null, null, null, null, null, text));
                        } else {
                            // 部分业务 Word 全文只有一个物理段落，通过手动换行分隔章节和正文。
                            String[] logicalParagraphs = text.split("\\R+");
                            for (String logicalParagraph : logicalParagraphs) {
                                String content = logicalParagraph.trim();
                                if (content.isBlank()) continue;
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
                    }
                    // 图片按所在段落提取并记录锚点，后续识别文本插入该段落之后。
                    for (var run : paragraph.getRuns()) {
                        for (var picture : run.getEmbeddedPictures()) {
                            imageNo++;
                            var pictureData = picture.getPictureData();
                            String mimeType = Optional.ofNullable(pictureData.getPackagePart().getContentType())
                                    .orElse("application/octet-stream");
                            media.add(new MediaBlock(null, chapter, paragraphNo, Math.max(0, blockNo - 1),
                                    imageNo, mimeType, pictureData.getFileName(), pictureData.getData(),
                                    "Word内嵌图片"));
                        }
                    }
                } else if (element instanceof XWPFTable table) {
                    tableNo++;
                    blockNo = appendDocxTable(result, table, blockNo, tableNo, chapter);
                }
            }
        }
        return new ParseResult(result, media);
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
        return appendTextChunks(result, blockNo, type, text, pageNo, chapter, paragraphNo, tableNo, rowNo,
                null, null);
    }

    private int appendTextChunks(List<Block> result, int blockNo, String type, String text,
                                 Integer pageNo, String chapter, Integer paragraphNo,
                                 Integer tableNo, Integer rowNo, Integer lineStart, Integer lineEnd) {
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
                        null, null, lineStart, lineEnd, content));
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
    private ParseResult parsePdf(MultipartFile file) throws IOException {
        List<Block> result = new ArrayList<>();
        List<MediaBlock> media = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            PDFRenderer renderer = new PDFRenderer(document);
            int blockNo = 1;
            for (int pageNo = 1; pageNo <= document.getNumberOfPages(); pageNo++) {
                stripper.setStartPage(pageNo);
                stripper.setEndPage(pageNo);
                String text = stripper.getText(document).trim();
                boolean needsVision = text.length() < 120;
                Integer anchorBlockNo = Math.max(0, blockNo - 1);
                if (!text.isBlank()) {
                    blockNo = appendTextChunks(result, blockNo, "PAGE", text,
                            pageNo, null, null, null, null);
                    anchorBlockNo = blockNo - 1;
                }
                if (needsVision) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ImageIO.write(renderer.renderImageWithDPI(pageNo - 1, 144, ImageType.RGB), "png", output);
                    media.add(new MediaBlock(pageNo, null, null, anchorBlockNo, pageNo, "image/png",
                            "pdf-page-" + pageNo + ".png", output.toByteArray(),
                            text.isBlank() ? "PDF扫描页" : "PDF图文混合页"));
                }
            }
        }
        return new ParseResult(result, media);
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
                        blockNo = appendTextChunks(result, blockNo, "SHEET_ROW",
                                String.join("；", values), null, sheet.getSheetName(),
                                null, tableNo, row.getRowNum() + 1);
                    }
                }
            }
        }
        return result;
    }

}
