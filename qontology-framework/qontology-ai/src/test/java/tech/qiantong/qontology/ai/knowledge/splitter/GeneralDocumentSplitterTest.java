package tech.qiantong.qontology.ai.knowledge.splitter;

import org.junit.jupiter.api.Test;
import tech.qiantong.qontology.ai.api.knowledge.AiVectorDocument;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 通用文档切分器测试。
 *
 * @author qontology
 * @date 2026-10-14
 */
class GeneralDocumentSplitterTest {
    @Test
    void keepsSingleChunkWithoutReadingANonexistentSecondChunk() {
        GeneralDocumentSplitter splitter = new GeneralDocumentSplitter("\\n+", 20, 5);

        assertEquals(List.of("short text"), splitter.split("short text"));
    }

    @Test
    void createsStableOverlappingChunks() {
        GeneralDocumentSplitter splitter = new GeneralDocumentSplitter(null, 5, 2);

        assertEquals(List.of("abcde", "defgh", "ghij"), splitter.split("abcdefghij"));
    }

    @Test
    void carriesSourceMetadataIntoChunks() {
        GeneralDocumentSplitter splitter = new GeneralDocumentSplitter(null, 4, 0);
        AiVectorDocument source = new AiVectorDocument("doc", "abcdefgh", Map.of("tenant", 7));

        List<AiVectorDocument> chunks = splitter.split(source);

        assertEquals("doc-0", chunks.get(0).id());
        assertEquals("doc", chunks.get(0).metadata().get("sourceDocumentId"));
        assertEquals(7, chunks.get(1).metadata().get("tenant"));
    }
}
