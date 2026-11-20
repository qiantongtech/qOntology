package tech.qiantong.qontology.module.app.service.extraction;

import com.alibaba.fastjson2.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 从本平台导出的 TTL 读取可选类型颜色，不依赖模型生成视觉元数据。 */
public final class ExtractionTypeColors {
    private static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    private static final String OWL_CLASS = "http://www.w3.org/2002/07/owl#Class";
    private static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String UI_COLOR = "https://qontology.ai/ontology/ui#color";

    private ExtractionTypeColors() {
    }

    /** 类型 IRI 和颜色快照；颜色为空表示未配置或无效。 */
    public record TypeStyle(String iri, String color) {
    }

    /**
     * 每个任务只解析一次。名称跨类重复时不匹配；外部不支持的 TTL 返回空映射，不阻断原抽取。
     * @param ttl 本体文件全文
     * @return 唯一类型名称到视觉元数据的映射
     */
    public static Map<String, TypeStyle> parse(String ttl) {
        try {
            Map<String, TypeStyle> styles = new HashMap<>();
            Set<String> ambiguous = new HashSet<>();
            ExtractionSchemaCompactor.readSubjects(ttl).forEach((iri, fields) -> {
                if (!fields.getOrDefault(RDF_TYPE, List.of()).contains(OWL_CLASS)) return;
                List<String> labels = fields.getOrDefault(RDFS_LABEL, List.of());
                List<String> colors = fields.getOrDefault(UI_COLOR, List.of());
                String color = colors.size() == 1 && colors.get(0).matches("#[0-9a-fA-F]{6}")
                        ? colors.get(0) : null;
                for (String label : labels) {
                    String name = label.trim();
                    if (name.isEmpty()) continue;
                    if (styles.putIfAbsent(name, new TypeStyle(iri, color)) != null) ambiguous.add(name);
                }
            });
            ambiguous.forEach(styles::remove);
            return Map.copyOf(styles);
        } catch (RuntimeException exception) {
            return Map.of();
        }
    }

    /**
     * 以 TTL 配置覆盖模型输出的同名元数据；未匹配类型保持原实体内容。
     * @param result 模型标准化结果
     * @param styles 任务开始时解析的类型索引
     */
    public static void apply(JSONObject result, Map<String, TypeStyle> styles) {
        if (result.getJSONArray("entities") == null) return;
        for (Object item : result.getJSONArray("entities")) {
            if (!(item instanceof JSONObject entity)) continue;
            entity.remove("schemaIri");
            entity.remove("schemaColor");
            String name = entity.getString("实体类型");
            TypeStyle style = name == null ? null : styles.get(name.trim());
            if (style == null) continue;
            entity.put("schemaIri", style.iri());
            if (style.color() != null) entity.put("schemaColor", style.color());
        }
    }
}
