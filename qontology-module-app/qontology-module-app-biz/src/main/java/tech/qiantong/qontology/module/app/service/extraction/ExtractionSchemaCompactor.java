package tech.qiantong.qontology.module.app.service.extraction;

import com.alibaba.fastjson2.JSON;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 压缩系统导出的逐行 Turtle 本体。只处理可完整识别的语法；其他 Turtle 原样传递。
 * 不用关键词筛选类型或关系，避免丢失当前文本可能需要的约束。
 */
final class ExtractionSchemaCompactor {
    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
    private static final String OWL = "http://www.w3.org/2002/07/owl#";
    private static final String TERM = "(?:<[^<>\\s]+>|[\\w-]*:[\\w-]*)";
    private static final Pattern PREFIX = Pattern.compile("@prefix\\s+([\\w-]*):\\s*<([^<>]+)>\\s*\\.");
    private static final Pattern TRIPLE = Pattern.compile("(" + TERM + ")\\s+(a|" + TERM
            + ")\\s+(" + TERM + "|\"(?:[^\"\\\\]|\\\\.)*\"(?:@[\\w-]+|\\^\\^" + TERM + ")?)\\s*\\.");

    private ExtractionSchemaCompactor() {
    }

    static String compact(String ttl) {
        try {
            return project(ttl);
        } catch (RuntimeException exception) {
            // 不猜测分号、空白节点、unionOf、restriction 等复杂结构的含义。
            return ttl;
        }
    }

    private static String project(String ttl) {
        Map<String, String> prefixes = new LinkedHashMap<>();
        Map<String, Map<String, List<String>>> subjects = new LinkedHashMap<>();
        for (String raw : ttl.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            Matcher prefix = PREFIX.matcher(line);
            if (prefix.matches()) {
                prefixes.put(prefix.group(1), prefix.group(2));
                continue;
            }
            Matcher triple = TRIPLE.matcher(line);
            if (!triple.matches()) {
                throw new IllegalArgumentException("非逐行 Turtle");
            }
            String subject = iri(triple.group(1), prefixes);
            String predicate = "a".equals(triple.group(2)) ? RDF + "type" : iri(triple.group(2), prefixes);
            String object = triple.group(3);
            String value = object.startsWith("\"") ? literal(object) : iri(object, prefixes);
            subjects.computeIfAbsent(subject, ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(predicate, ignored -> new ArrayList<>()).add(value);
        }

        Map<String, String> names = new LinkedHashMap<>();
        subjects.forEach((id, fields) -> {
            if (isSchema(fields)) {
                List<String> labels = fields.getOrDefault(RDFS + "label", List.of());
                if (labels.size() != 1 || labels.get(0).isBlank()) {
                    throw new IllegalArgumentException("本体名称不唯一");
                }
                names.put(id, labels.get(0));
            }
        });
        if (names.isEmpty()) {
            return ttl;
        }
        Map<String, List<Map<String, Object>>> schema = new LinkedHashMap<>();
        schema.put("classes", new ArrayList<>());
        schema.put("attributes", new ArrayList<>());
        schema.put("relations", new ArrayList<>());
        subjects.forEach((id, fields) -> {
            if (!isSchema(fields)) {
                return;
            }
            List<String> types = fields.get(RDF + "type");
            String category = types.contains(OWL + "Class") ? "classes"
                    : types.contains(OWL + "DatatypeProperty") ? "attributes" : "relations";
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", names.get(id));
            fields.forEach((predicate, values) -> {
                String key;
                boolean reference = false;
                switch (predicate) {
                    case RDF + "type" -> {
                        if (values.size() != 1) {
                            throw new IllegalArgumentException("附加 OWL 类型约束");
                        }
                        return;
                    }
                    case RDFS + "label" -> { return; }
                    case RDFS + "comment" -> key = "description";
                    case RDFS + "domain" -> { key = "domain"; reference = true; }
                    case RDFS + "range" -> { key = "range"; reference = true; }
                    case RDFS + "subClassOf" -> { key = "parents"; reference = true; }
                    case OWL + "inverseOf" -> { key = "inverseOf"; reference = true; }
                    default -> {
                        if (!predicate.endsWith("#dataTypeCategory")
                                && !predicate.endsWith("/dataTypeCategory")) {
                            throw new IllegalArgumentException("未处理的本体约束");
                        }
                        key = "dataTypeCategory";
                    }
                }
                if (reference) {
                    entry.put(key, values.stream().map(value -> {
                        if (names.containsKey(value)) {
                            return names.get(value);
                        }
                        if (value.startsWith("http://www.w3.org/2001/XMLSchema#")) {
                            return value;
                        }
                        throw new IllegalArgumentException("无法解析本体引用");
                    }).toList());
                } else {
                    entry.put(key, values.size() == 1 ? values.get(0) : values);
                }
            });
            schema.get(category).add(entry);
        });
        String compact = JSON.toJSONString(schema);
        return compact.length() < ttl.length() ? compact : ttl;
    }

    private static boolean isSchema(Map<String, List<String>> fields) {
        List<String> types = fields.getOrDefault(RDF + "type", List.of());
        return types.contains(OWL + "Class") || types.contains(OWL + "DatatypeProperty")
                || types.contains(OWL + "ObjectProperty");
    }

    private static String iri(String term, Map<String, String> prefixes) {
        if (term.startsWith("<")) {
            return term.substring(1, term.length() - 1);
        }
        int colon = term.indexOf(':');
        String namespace = prefixes.get(term.substring(0, colon));
        if (namespace == null) {
            throw new IllegalArgumentException("未知前缀");
        }
        return namespace + term.substring(colon + 1);
    }

    private static String literal(String term) {
        int end = term.length() - 1;
        while (end > 0 && term.charAt(end) != '"') {
            end--;
        }
        // Turtle 的 \U、\' 等转义不等同于 JSON，保守回退而非错误解码。
        String quoted = term.substring(0, end + 1);
        if (quoted.contains("\\U") || quoted.contains("\\'")) {
            throw new IllegalArgumentException("非 JSON 字符串转义");
        }
        return JSON.parseObject(quoted, String.class);
    }
}
