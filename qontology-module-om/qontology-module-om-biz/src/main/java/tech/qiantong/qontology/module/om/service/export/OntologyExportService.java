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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.module.om.api.export.OntologyExportApi;
import tech.qiantong.qontology.module.basic.api.dto.dataType.BasicDataTypeOptionDTO;
import tech.qiantong.qontology.module.basic.api.service.dataType.IBasicDataTypeApiService;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.dataType.OmDataTypeMapper;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;
import tech.qiantong.qontology.module.om.dal.mapper.term.OmTermMapper;

/**
 * 按版本导出对象类型、属性、关系和术语为 Turtle 或 RDF/XML，不写回业务表。
 * 枚举按字符串导出，保留类型编码；暂不生成枚举值约束。
 */
@Service
public class OntologyExportService implements OntologyExportApi {

    private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";

    private static final String XSD = "http://www.w3.org/2001/XMLSchema#";
    private static final Map<Integer, String> BASIC_TYPES = Map.of(
            0, "string", 1, "integer", 2, "decimal", 3, "boolean", 4, "date", 5, "string");

    @Resource
    private IBasicDataTypeApiService dataTypeApiService;
    @Resource
    private OmDataTypeMapper projectDataTypeMapper;
    @Resource
    private OmObjectTypeMapper typeMapper;
    @Resource
    private OmObjectTypeAttributeMapper attributeMapper;
    @Resource
    private OmObjectTypeRelationMapper relationMapper;
    @Resource
    private OmTermMapper termMapper;

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public byte[] exportOwl(Long versionId, String namespace, String projectName, String versionName) {
        return exportOntology(versionId, namespace, projectName, versionName, false);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public byte[] exportTurtle(Long versionId, String namespace, String projectName, String versionName) {
        return exportOntology(versionId, namespace, projectName, versionName, true);
    }

    private byte[] exportOntology(Long versionId, String namespace, String projectName, String versionName, boolean turtle) {
        if (versionId == null || versionId <= 0) throw new ServiceException("请选择导出版本");
        String ns = namespace(namespace);
        List<OmObjectTypeDO> types = typeMapper.selectList(versionQuery(versionId, "id"));
        List<OmObjectTypeAttributeDO> attributes = attributeMapper.selectList(versionQuery(versionId, "id"));
        List<OmObjectTypeRelationDO> relations = relationMapper.selectList(versionQuery(versionId, "id"));
        List<OmTermDO> terms = termMapper.selectList(versionQuery(versionId, "id"));
        Set<Long> typeIds = types.stream().map(OmObjectTypeDO::getId).collect(Collectors.toSet());
        for (OmObjectTypeDO type : types) {
            if (type.getParentId() != null && type.getParentId() != 0) requireType(typeIds, type.getParentId());
        }
        for (OmObjectTypeAttributeDO attribute : attributes) requireType(typeIds, attribute.getObjectTypeId());
        for (OmObjectTypeRelationDO relation : relations) {
            requireType(typeIds, relation.getStartTypeId());
            requireType(typeIds, relation.getEndTypeId());
        }
        Map<BusinessTypeKey, BasicDataTypeOptionDTO> businessTypes = resolveBusinessTypes(versionId, attributes);
        try {
            OntologyDocumentWriter x = OntologyDocumentWriter.create(turtle, ns);
            entity(x, "owl", "Ontology", ns);
            text(x, "rdfs", "label", projectName);
            text(x, "owl", "versionInfo", versionName);
            text(x, "dct", "identifier", String.valueOf(versionId));
            x.endEntity();
            // 保留原有分类注释名称，同时声明新增类型元数据。
            for (String annotation : List.of("dataTypeCategory", "basicTypeCode", "businessTypeId",
                    "businessTypeSource", "unit", "decimalPlaces", "format")) {
                entity(x, "owl", "AnnotationProperty", ns + annotation);
                x.endEntity();
            }
            for (Map.Entry<BusinessTypeKey, BasicDataTypeOptionDTO> entry : businessTypes.entrySet()) {
                BusinessTypeKey key = entry.getKey();
                BasicDataTypeOptionDTO type = entry.getValue();
                entity(x, "rdfs", "Datatype", ns + key.localName());
                labels(x, type.getName(), type.getDescription());
                text(x, "ont", "businessTypeId", String.valueOf(type.getId()));
                text(x, "ont", "businessTypeSource", key.source());
                text(x, "ont", "basicTypeCode", type.getDataType());
                resource(x, "owl", "equivalentClass", businessBasicType(type));
                text(x, "ont", "unit", type.getUnit());
                if (type.getDecimalPlaces() != null)
                    text(x, "ont", "decimalPlaces", String.valueOf(type.getDecimalPlaces()));
                text(x, "ont", "format", type.getFormat());
                x.endEntity();
            }
            for (OmObjectTypeDO type : types) {
                entity(x, "owl", "Class", ns + "type_" + type.getId());
                labels(x, type.getName(), type.getDescription());
                if (type.getParentId() != null && type.getParentId() != 0)
                    resource(x, "rdfs", "subClassOf", ns + "type_" + type.getParentId());
                x.endEntity();
            }
            for (OmObjectTypeAttributeDO attribute : attributes) {
                entity(x, "owl", "DatatypeProperty", ns + "attribute_" + attribute.getId());
                labels(x, attribute.getName(), attribute.getDescription());
                resource(x, "rdfs", "domain", ns + "type_" + attribute.getObjectTypeId());
                if (attribute.getTypeCategory() != null)
                    text(x, "ont", "dataTypeCategory", String.valueOf(attribute.getTypeCategory()));
                if (Integer.valueOf(1).equals(attribute.getTypeCategory()) && attribute.getBasicTypeCode() != null) {
                    text(x, "ont", "basicTypeCode", String.valueOf(attribute.getBasicTypeCode()));
                    resource(x, "rdfs", "range", basicType(attribute.getBasicTypeCode()));
                } else if (Integer.valueOf(2).equals(attribute.getTypeCategory())) {
                    BusinessTypeKey key = businessTypeKey(attribute);
                    text(x, "ont", "businessTypeId", String.valueOf(attribute.getBusinessTypeId()));
                    text(x, "ont", "businessTypeSource", key.source());
                    resource(x, "rdfs", "range", ns + key.localName());
                }
                x.endEntity();
            }
            for (OmObjectTypeRelationDO relation : relations) {
                String iri = ns + "relation_" + relation.getId();
                entity(x, "owl", "ObjectProperty", iri);
                labels(x, relation.getName(), relation.getDescription());
                resource(x, "rdfs", "domain", ns + "type_" + relation.getStartTypeId());
                resource(x, "rdfs", "range", ns + "type_" + relation.getEndTypeId());
                x.endEntity();
                if (Integer.valueOf(1).equals(relation.getReversibleFlag())) {
                    entity(x, "owl", "ObjectProperty", iri + "_inverse");
                    text(x, "rdfs", "label", relation.getReverseName());
                    resource(x, "owl", "inverseOf", iri);
                    resource(x, "rdfs", "domain", ns + "type_" + relation.getEndTypeId());
                    resource(x, "rdfs", "range", ns + "type_" + relation.getStartTypeId());
                    x.endEntity();
                }
            }
            for (OmTermDO term : terms) {
                entity(x, "owl", "NamedIndividual", ns + "term_" + term.getId());
                resource(x, "rdf", "type", SKOS + "Concept");
                labels(x, term.getStdName(), term.getDescription());
                languageText(x, "prefLabel", term.getStdName(), "zh");
                languageText(x, "prefLabel", term.getEnName(), "en");
                if (term.getAlias() != null)
                    for (String alias : term.getAlias().split("[,，;；\n]")) text(x, "skos", "altLabel", alias.trim());
                text(x, "skos", "notation", term.getAbbr());
                text(x, "dct", "source", term.getSource());
                x.endEntity();
            }
            return x.finish();
        } catch (XMLStreamException ex) {
            throw new ServiceException("本体文件生成失败，请检查模型中的文本内容");
        }
    }

    private Map<BusinessTypeKey, BasicDataTypeOptionDTO> resolveBusinessTypes(
            Long versionId, List<OmObjectTypeAttributeDO> attributes) {
        Map<BusinessTypeKey, BasicDataTypeOptionDTO> result = new LinkedHashMap<>();
        for (OmObjectTypeAttributeDO attribute : attributes) {
            if (Integer.valueOf(2).equals(attribute.getTypeCategory())) {
                if (attribute.getBusinessTypeId() == null)
                    throw new ServiceException("属性“" + attribute.getName() + "”未选择业务数据类型，请修正后导出");
                BusinessTypeKey key = businessTypeKey(attribute);
                if (result.containsKey(key)) continue;
                BasicDataTypeOptionDTO type;
                if ("GLOBAL".equals(key.source())) {
                    type = dataTypeApiService.getAvailableDataType(key.id());
                } else {
                    OmDataTypeDO projectType = projectDataTypeMapper.selectById(key.id());
                    if (projectType == null || !Boolean.TRUE.equals(projectType.getValidFlag())
                            || !Objects.equals(projectType.getVersionId(), versionId)) {
                        throw new ServiceException("属性“" + attribute.getName() + "”引用的当前项目业务数据类型“"
                                + key.id() + "”不存在、已停用或不属于导出版本，请修正后导出");
                    }
                    type = new BasicDataTypeOptionDTO();
                    type.setId(projectType.getId());
                    type.setName(projectType.getName());
                    type.setDataType(projectType.getDataType() == null ? null : projectType.getDataType().getCode());
                    type.setDescription(projectType.getDescription());
                    type.setUnit(projectType.getUnit());
                    type.setDecimalPlaces(projectType.getDecimalPlaces());
                    type.setFormat(projectType.getFormat());
                }
                if (type == null)
                    throw new ServiceException("属性“" + attribute.getName() + "”引用的全局业务数据类型“"
                            + key.id() + "”不存在或已停用，请修正后导出");
                businessBasicType(type);
                result.put(key, type);
            }
        }
        return result;
    }

    private static BusinessTypeKey businessTypeKey(OmObjectTypeAttributeDO attribute) {
        // 历史属性未保存来源时，与属性保存逻辑保持一致，按全局类型处理。
        String source = attribute.getBusinessTypeSource() == null ? "GLOBAL" : attribute.getBusinessTypeSource();
        if (!Set.of("PROJECT", "GLOBAL").contains(source))
            throw new ServiceException("属性“" + attribute.getName() + "”的业务数据类型来源无效，请修正后导出");
        return new BusinessTypeKey(source, attribute.getBusinessTypeId());
    }

    private record BusinessTypeKey(String source, Long id) {
        String localName() {
            // 保留原有全局类型 IRI，项目类型使用独立名称，避免两张表主键相同导致串用。
            return "PROJECT".equals(source) ? "business_type_project_" + id : "business_type_" + id;
        }
    }

    private static String basicType(Integer code) {
        String type = BASIC_TYPES.get(code);
        if (type == null) throw new ServiceException("基础类型编码无效，请修正后导出");
        return XSD + type;
    }

    private static String businessBasicType(BasicDataTypeOptionDTO type) {
        try {
            return basicType(Integer.valueOf(type.getDataType()));
        } catch (NumberFormatException ex) {
            throw new ServiceException("业务数据类型“" + type.getName() + "”的基础类型编码无效，请修正后导出");
        }
    }

    private static <T> QueryWrapper<T> versionQuery(Long versionId, String key) {
        // 仅导出当前版本的有效数据；逻辑删除条件由 MyBatis-Plus 自动追加。
        return new QueryWrapper<T>().eq("version_id", versionId)
                .eq("valid_flag", true).orderByAsc(key);
    }

    private static void requireType(Set<Long> ids, Long id) {
        if (!ids.contains(id)) throw new ServiceException("对象类型、属性或关系存在跨版本或失效引用，请检查后再导出");
    }

    private static String namespace(String value) {
        try {
            URI uri = new URI(value == null ? "" : value.trim());
            if (!uri.isAbsolute() || uri.getSchemeSpecificPart().isEmpty()) throw new IllegalArgumentException();
            String result = uri.toASCIIString();
            return result.endsWith("#") || result.endsWith("/") ? result : result + (uri.getFragment() == null ? "#" : "_");
        } catch (Exception ex) {
            throw new ServiceException("项目命名空间必须是有效的绝对 URI，例如 https://example.com/ontology#");
        }
    }

    private static void entity(OntologyDocumentWriter writer, String prefix, String local, String iri) throws XMLStreamException {
        writer.entity(prefix, local, iri);
    }

    private static void resource(OntologyDocumentWriter writer, String prefix, String local, String iri) throws XMLStreamException {
        writer.resource(prefix, local, iri);
    }

    private static void text(OntologyDocumentWriter writer, String prefix, String local, String value) throws XMLStreamException {
        if (value == null || value.isBlank()) return;
        validateText(value);
        writer.literal(prefix, local, value, null);
    }

    private static void languageText(OntologyDocumentWriter writer, String local, String value, String language) throws XMLStreamException {
        if (value == null || value.isBlank()) return;
        validateText(value);
        writer.literal("skos", local, value, language);
    }

    private static void labels(OntologyDocumentWriter x, String label, String comment) throws XMLStreamException {
        text(x, "rdfs", "label", label);
        text(x, "rdfs", "comment", comment);
    }

    private static void validateText(String value) {
        if (value.codePoints().anyMatch(c -> !(c == 9 || c == 10 || c == 13 || c >= 0x20 && c <= 0xD7FF || c >= 0xE000 && c <= 0xFFFD || c >= 0x10000 && c <= 0x10FFFF))) {
            throw new ServiceException("模型文本包含 XML 不支持的控制字符，请修正后导出");
        }
    }

}
