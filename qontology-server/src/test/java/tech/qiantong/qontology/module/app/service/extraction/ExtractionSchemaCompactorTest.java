package tech.qiantong.qontology.module.app.service.extraction;

import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExtractionSchemaCompactorTest {
    private static final String SCHEMA = """
            @prefix ont: <https://example.org/ontology#> .
            @prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
            @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
            @prefix owl: <http://www.w3.org/2002/07/owl#> .
            ont:company rdf:type owl:Class .
            ont:company rdfs:label "企业" .
            ont:supplier rdf:type owl:Class .
            ont:supplier rdfs:label "供应商" .
            ont:supplier rdfs:subClassOf ont:company .
            ont:code rdf:type owl:DatatypeProperty .
            ont:code rdfs:label "编码" .
            ont:code rdfs:domain ont:company .
            ont:code ont:dataTypeCategory "1" .
            ont:supplies rdf:type owl:ObjectProperty .
            ont:supplies rdfs:label "供应" .
            ont:supplies rdfs:comment "向企业供应物料" .
            ont:supplies rdfs:domain ont:supplier .
            ont:supplies rdfs:range ont:company .
            ont:purchases rdf:type owl:ObjectProperty .
            ont:purchases rdfs:label "采购自" .
            ont:purchases owl:inverseOf ont:supplies .
            """;

    @Test
    public void preservesExtractionConstraints() {
        String compact = ExtractionSchemaCompactor.compact(SCHEMA);
        assertTrue(compact.length() < SCHEMA.length());
        JSONObject result = JSONObject.parseObject(compact);
        assertEquals(2, result.getJSONArray("classes").size());
        assertEquals("企业", result.getJSONArray("classes").getJSONObject(1)
                .getJSONArray("parents").getString(0));
        JSONObject attribute = result.getJSONArray("attributes").getJSONObject(0);
        assertEquals("编码", attribute.getString("name"));
        assertEquals("企业", attribute.getJSONArray("domain").getString(0));
        assertEquals("1", attribute.getString("dataTypeCategory"));
        JSONObject relation = result.getJSONArray("relations").getJSONObject(0);
        assertEquals("供应商", relation.getJSONArray("domain").getString(0));
        assertEquals("企业", relation.getJSONArray("range").getString(0));
        assertEquals("向企业供应物料", relation.getString("description"));
        assertEquals("供应", result.getJSONArray("relations").getJSONObject(1)
                .getJSONArray("inverseOf").getString(0));
    }

    @Test
    public void unsupportedConstraintsAndSyntaxKeepOriginal() {
        for (String suffix : new String[]{
                "ont:supplier owl:equivalentClass ont:company .",
                "ont:supplier rdfs:subClassOf [ a owl:Restriction ] .",
                "ont:supplier rdfs:label \"备用名\" .",
                "ont:supplier rdfs:comment \"bad\\xescape\" .",
                "ont:supplier rdfs:comment \"跨行\" ; rdfs:label \"供应商\" ."}) {
            String original = SCHEMA + suffix;
            assertEquals(original, ExtractionSchemaCompactor.compact(original));
        }
    }

    @Test
    public void preservesEscapedLiteralsAndMultipleDomains() {
        String ttl = SCHEMA + "ont:code rdfs:domain ont:supplier .\n"
                + "ont:code rdfs:comment \"第\\n二行及\\\"引号\\\"\" .\n";
        JSONObject result = JSONObject.parseObject(ExtractionSchemaCompactor.compact(ttl));
        JSONObject attribute = result.getJSONArray("attributes").getJSONObject(0);
        assertEquals(2, attribute.getJSONArray("domain").size());
        assertEquals("第\n二行及\"引号\"", attribute.getString("description"));
    }
}
