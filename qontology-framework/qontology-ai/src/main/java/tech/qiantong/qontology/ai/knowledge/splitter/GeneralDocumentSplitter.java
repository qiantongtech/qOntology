package tech.qiantong.qontology.ai.knowledge.splitter;

import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 通用文档切分器，支持正则分节和重叠切分。
 *
 * @author qontology
 * @date 2026-10-14
 */
public final class GeneralDocumentSplitter {
    private final Pattern separator;
    private final int maxChunkSize;
    private final int chunkOverlapSize;

    public GeneralDocumentSplitter(String regex, int maxChunkSize, int chunkOverlapSize) {
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize 必须为正数");
        }
        if (chunkOverlapSize < 0 || chunkOverlapSize >= maxChunkSize) {
            throw new IllegalArgumentException("chunkOverlapSize 必须在 0 到 maxChunkSize-1 之间");
        }
        this.separator = regex == null || regex.isBlank() ? null : Pattern.compile(regex);
        this.maxChunkSize = maxChunkSize;
        this.chunkOverlapSize = chunkOverlapSize;
    }

    public List<String> split(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String[] sections = separator == null ? new String[]{text} : separator.split(text);
        List<String> chunks = new ArrayList<>();
        for (String section : sections) {
            String trimmed = section.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            splitSection(trimmed, chunks);
        }
        return List.copyOf(chunks);
    }

    public List<AiVectorDocument> split(AiVectorDocument document) {
        List<String> chunks = split(document.content());
        List<AiVectorDocument> result = new ArrayList<>(chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new java.util.LinkedHashMap<>(document.metadata());
            metadata.put("sourceDocumentId", document.id());
            metadata.put("chunkIndex", i);
            result.add(new AiVectorDocument(document.id() + "-" + i, chunks.get(i), metadata));
        }
        return List.copyOf(result);
    }

    private void splitSection(String section, List<String> target) {
        if (section.length() <= maxChunkSize) {
            target.add(section);
            return;
        }
        int step = maxChunkSize - chunkOverlapSize;
        for (int start = 0; start < section.length(); start += step) {
            int end = Math.min(section.length(), start + maxChunkSize);
            target.add(section.substring(start, end));
            if (end == section.length()) {
                break;
            }
        }
    }
}
