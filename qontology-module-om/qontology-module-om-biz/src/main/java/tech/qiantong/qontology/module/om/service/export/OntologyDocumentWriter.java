/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.service.export;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * 同一份本体映射的两种文档语法；仅负责序列化，不读取业务数据。
 */
abstract class OntologyDocumentWriter {

    private static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String XML = "http://www.w3.org/XML/1998/namespace";

    /**
     * 根据目标语法创建本体文档写入器
     *
     * @param turtle 是否使用 Turtle 语法，false 使用 RDF/XML 语法
     * @param namespace 本体命名空间，作为导出资源标识的前缀
     * @return 对应目标语法的文档写入器
     */
    static OntologyDocumentWriter create(boolean turtle, String namespace) throws XMLStreamException {
        Map<String, String> prefixes = new LinkedHashMap<>();
        prefixes.put("rdf", RDF);
        prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
        prefixes.put("skos", "http://www.w3.org/2004/02/skos/core#");
        prefixes.put("dct", "http://purl.org/dc/terms/");
        prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        prefixes.put("ont", namespace);
        return turtle ? new TurtleWriter(prefixes) : new RdfXmlWriter(prefixes);
    }

    /**
     * 开始写入指定类型的本体实体
     *
     * @param prefix 谓词或实体类型的命名空间前缀
     * @param local 谓词或实体类型的本地名称
     * @param iri 资源的完整 IRI
     */
    abstract void entity(String prefix, String local, String iri) throws XMLStreamException;

    /**
     * 写入指向资源 IRI 的属性
     *
     * @param prefix 谓词或实体类型的命名空间前缀
     * @param local 谓词或实体类型的本地名称
     * @param iri 资源的完整 IRI
     */
    abstract void resource(String prefix, String local, String iri) throws XMLStreamException;

    /**
     * 写入文本字面量及可选语言标签
     *
     * @param prefix 谓词或实体类型的命名空间前缀
     * @param local 谓词或实体类型的本地名称
     * @param value 待写出的字面量文本
     * @param language 字面量语言标签，为空时不附加语言标签
     */
    abstract void literal(String prefix, String local, String value, String language) throws XMLStreamException;

    /**
     * 结束当前本体实体的写入
     *
     */
    abstract void endEntity() throws XMLStreamException;

    /**
     * 完成序列化并获取 UTF-8 编码的文档内容
     *
     * @return UTF-8 编码的本体文档字节数组
     */
    abstract byte[] finish() throws XMLStreamException;

    private static final class RdfXmlWriter extends OntologyDocumentWriter {

        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        private final XMLStreamWriter xml;
        private final Map<String, String> prefixes;

        private RdfXmlWriter(Map<String, String> prefixes) throws XMLStreamException {
            this.prefixes = prefixes;
            xml = XMLOutputFactory.newFactory().createXMLStreamWriter(output, "UTF-8");
            xml.writeStartDocument("UTF-8", "1.0");
            xml.writeStartElement("rdf", "RDF", RDF);
            for (Map.Entry<String, String> entry : prefixes.entrySet()) {
                xml.writeNamespace(entry.getKey(), entry.getValue());
            }
        }

        /**
         * 开始写入指定类型的本体实体
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param iri 资源的完整 IRI
         */
        @Override
        void entity(String prefix, String local, String iri) throws XMLStreamException {
            xml.writeStartElement(prefix, local, prefixes.get(prefix));
            xml.writeAttribute("rdf", RDF, "about", iri);
        }

        /**
         * 写入指向资源 IRI 的属性
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param iri 资源的完整 IRI
         */
        @Override
        void resource(String prefix, String local, String iri) throws XMLStreamException {
            xml.writeStartElement(prefix, local, prefixes.get(prefix));
            xml.writeAttribute("rdf", RDF, "resource", iri);
            xml.writeEndElement();
        }

        /**
         * 写入文本字面量及可选语言标签
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param value 待写出的字面量文本
         * @param language 字面量语言标签，为空时不附加语言标签
         */
        @Override
        void literal(String prefix, String local, String value, String language) throws XMLStreamException {
            xml.writeStartElement(prefix, local, prefixes.get(prefix));
            if (language != null) xml.writeAttribute("xml", XML, "lang", language);
            // XML 解析会把原始回车归一化成换行，用字符引用保留原始字面量。
            int offset = 0;
            for (int position; (position = value.indexOf('\r', offset)) >= 0; offset = position + 1) {
                xml.writeCharacters(value.substring(offset, position));
                xml.writeEntityRef("#xD");
            }
            xml.writeCharacters(value.substring(offset));
            xml.writeEndElement();
        }

        /**
         * 结束当前本体实体的写入
         *
         */
        @Override
        void endEntity() throws XMLStreamException {
            xml.writeEndElement();
        }

        /**
         * 完成序列化并获取 UTF-8 编码的文档内容
         *
         * @return UTF-8 编码的本体文档字节数组
         */
        @Override
        byte[] finish() throws XMLStreamException {
            xml.writeEndElement();
            xml.writeEndDocument();
            xml.close();
            return output.toByteArray();
        }
    }

    private static final class TurtleWriter extends OntologyDocumentWriter {

        private final StringBuilder output = new StringBuilder();
        private final Map<String, String> prefixes;
        private String subject;

        private TurtleWriter(Map<String, String> prefixes) {
            this.prefixes = prefixes;
            prefixes.forEach((prefix, iri) ->
                output.append("@prefix ").append(prefix).append(": ").append(iri(iri)).append(" .\n")
            );
            output.append('\n');
        }

        /**
         * 开始写入指定类型的本体实体
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param iri 资源的完整 IRI
         */
        @Override
        void entity(String prefix, String local, String iri) {
            subject = resourceName(iri);
            predicate("rdf", "type");
            output.append(prefix).append(':').append(local).append(" .\n");
        }

        /**
         * 写入指向资源 IRI 的属性
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param iri 资源的完整 IRI
         */
        @Override
        void resource(String prefix, String local, String iri) {
            predicate(prefix, local);
            output.append(resourceName(iri)).append(" .\n");
        }

        /**
         * 写入文本字面量及可选语言标签
         *
         * @param prefix 谓词或实体类型的命名空间前缀
         * @param local 谓词或实体类型的本地名称
         * @param value 待写出的字面量文本
         * @param language 字面量语言标签，为空时不附加语言标签
         */
        @Override
        void literal(String prefix, String local, String value, String language) {
            predicate(prefix, local);
            output.append('"').append(escape(value)).append('"');
            if (language != null) output.append('@').append(language);
            output.append(" .\n");
        }

        private void predicate(String prefix, String local) {
            output.append(subject).append(' ').append(prefix).append(':').append(local).append(' ');
        }

        /**
         * 结束当前本体实体的写入
         *
         */
        @Override
        void endEntity() {
            output.append('\n');
            subject = null;
        }

        /**
         * 完成序列化并获取 UTF-8 编码的文档内容
         *
         * @return UTF-8 编码的本体文档字节数组
         */
        @Override
        byte[] finish() {
            return output.toString().getBytes(StandardCharsets.UTF_8);
        }

        private String resourceName(String value) {
            String result = null;
            int longestNamespace = -1;
            for (Map.Entry<String, String> entry : prefixes.entrySet()) {
                String namespace = entry.getValue();
                if (!value.startsWith(namespace) || namespace.length() <= longestNamespace) continue;
                String local = value.substring(namespace.length());
                // 仅缩写可以直接安全输出的局部名，其余保留完整 IRI，避免改变资源地址。
                if (local.isEmpty() || local.matches("[A-Za-z_0-9][A-Za-z_0-9-]*")) {
                    result = entry.getKey() + ':' + local;
                    longestNamespace = namespace.length();
                }
            }
            return result == null ? iri(value) : result;
        }

        private static String escape(String value) {
            return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        }

        private static String iri(String value) {
            StringBuilder result = new StringBuilder("<");
            value
                .codePoints()
                .forEach(c -> {
                    if (c <= 0x20 || "<>\"{}|^".indexOf(c) >= 0 || c == 0x60 || c == 0x5c) {
                        result.append(String.format("\\u%04X", c));
                    } else {
                        result.appendCodePoint(c);
                    }
                });
            return result.append('>').toString();
        }
    }
}
