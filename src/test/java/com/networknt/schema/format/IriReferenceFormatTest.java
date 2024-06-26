/*
 * Copyright (c) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.schema.format;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidatorsConfig;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;

class IriReferenceFormatTest {
    @Test
    void uriShouldPass() {
        String schemaData = "{\r\n"
                + "  \"format\": \"iri-reference\"\r\n"
                + "}";
        
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFormatAssertionsEnabled(true);
        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData, config);
        Set<ValidationMessage> messages = schema.validate("\"https://test.com/assets/product.pdf\"",
                InputFormat.JSON);
        assertTrue(messages.isEmpty());
    }

    @Test
    void queryWithBracketsShouldFail() {
        String schemaData = "{\r\n"
                + "  \"format\": \"iri-reference\"\r\n"
                + "}";
        
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFormatAssertionsEnabled(true);
        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData, config);
        Set<ValidationMessage> messages = schema.validate("\"https://test.com/assets/product.pdf?filter[test]=1\"",
                InputFormat.JSON);
        assertFalse(messages.isEmpty());
    }

    @Test
    void queryWithEncodedBracketsShouldPass() {
        String schemaData = "{\r\n"
                + "  \"format\": \"iri-reference\"\r\n"
                + "}";

        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFormatAssertionsEnabled(true);
        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData, config);
        Set<ValidationMessage> messages = schema.validate("\"https://test.com/assets/product.pdf?filter%5Btest%5D=1\"",
                InputFormat.JSON);
        assertTrue(messages.isEmpty());
    }

    @Test
    void iriShouldPass() {
        String schemaData = "{\r\n"
                + "  \"format\": \"iri-reference\"\r\n"
                + "}";
        
        SchemaValidatorsConfig config = new SchemaValidatorsConfig();
        config.setFormatAssertionsEnabled(true);
        JsonSchema schema = JsonSchemaFactory.getInstance(VersionFlag.V202012).getSchema(schemaData, config);
        Set<ValidationMessage> messages = schema.validate("\"https://test.com/assets/produktdatenblätter.pdf\"",
                InputFormat.JSON);
        assertTrue(messages.isEmpty());
    }

}
