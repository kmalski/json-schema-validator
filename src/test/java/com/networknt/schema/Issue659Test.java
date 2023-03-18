package com.networknt.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Set;

public class Issue659Test {

    private static JsonSchema schema;

    @BeforeAll
    static void init() {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        String schemaPath = "/schema/issue659-2020-12.json";
        InputStream schemaInputStream = Issue659Test.class.getResourceAsStream(schemaPath);
        schema = factory.getSchema(schemaInputStream);
    }

    private JsonNode getJsonNodeFromJsonData(String jsonFilePath) throws Exception {
        InputStream content = getClass().getResourceAsStream(jsonFilePath);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(content);
    }

    @Test
    public void testValidJson() throws Exception {
        JsonNode node = getJsonNodeFromJsonData("/data/issue659-valid.json");
        Set<ValidationMessage> errors = schema.validate(node);
        Assertions.assertTrue(errors.isEmpty());
    }

    @Test
    public void testInvalidJson() throws Exception {
        JsonNode node = getJsonNodeFromJsonData("/data/issue659-invalid.json");
        Set<ValidationMessage> errors = schema.validate(node);
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals("$.states[0].status: does not have a value in the enumeration [WAITING, RUNNING]", errors.iterator().next().getMessage());
    }
}
