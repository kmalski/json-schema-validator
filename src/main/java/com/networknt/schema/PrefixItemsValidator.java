/*
 * Copyright (c) 2022 Network New Technologies Inc.
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

package com.networknt.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.networknt.schema.walk.DefaultItemWalkListenerRunner;
import com.networknt.schema.walk.WalkListenerRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PrefixItemsValidator extends BaseJsonValidator implements JsonValidator {

    private static final Logger logger = LoggerFactory.getLogger(PrefixItemsValidator.class);

    private final List<JsonSchema> tupleSchema = new ArrayList<>();

    public PrefixItemsValidator(String schemaPath, JsonNode schemaNode, JsonSchema parentSchema,
                                ValidationContext validationContext) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.PREFIX_ITEMS, validationContext);
        this.validationContext = validationContext;

        if (schemaNode.isArray()) {
            for (JsonNode prefixItem : schemaNode) {
                tupleSchema.add(new JsonSchema(validationContext, schemaPath, parentSchema.getCurrentUri(), prefixItem, parentSchema));
            }
        } else if (schemaNode.isObject()) {
            tupleSchema.add(new JsonSchema(validationContext, schemaPath, parentSchema.getCurrentUri(), schemaNode, parentSchema));
        } else {
            throw new JsonSchemaException("prefixItems is neither an array nor an object");
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonNode node, JsonNode rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (!node.isArray() && !this.validationContext.getConfig().isTypeLoose()) {
            return Collections.emptySet();
        }

        Set<ValidationMessage> errors = new LinkedHashSet<>();
        if (node.isArray()) {
            int size = Math.min(node.size(), tupleSchema.size());
            for (int i = 0; i < size; i++) {
                doValidate(errors, i, node.get(i), rootNode, at);
            }
        } else {
            doValidate(errors, 0, node, rootNode, at);
        }
        return Collections.unmodifiableSet(errors);
    }

    private void doValidate(Set<ValidationMessage> errors, int i, JsonNode node, JsonNode rootNode, String at) {
        errors.addAll(tupleSchema.get(i).validate(node, rootNode, at + "[" + i + "]"));
    }

    @Override
    public void preloadJsonSchema() {
        preloadJsonSchemas(tupleSchema);
    }
}
