/*
 * Copyright (c) 2016 Network New Technologies Inc.
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

import java.util.*;

public class ItemsValidatorMin2020 extends BaseJsonValidator implements JsonValidator {

    private static final Logger logger = LoggerFactory.getLogger(ItemsValidatorMin2020.class);

    private final JsonSchema schema;
    private final Boolean itemsAllowed;

    private final boolean isPartOfTupleValidation;
    private final int prefixItemsSize;

    public ItemsValidatorMin2020(String schemaPath, JsonNode schemaNode, JsonSchema parentSchema,
                                 ValidationContext validationContext) {
        super(schemaPath, schemaNode, parentSchema, ValidatorTypeCode.ITEMS, validationContext);
        this.validationContext = validationContext;

        this.isPartOfTupleValidation = parentSchema.getSchemaNode().has("prefixItems");
        this.prefixItemsSize = isPartOfTupleValidation
                ? parentSchema.getSchemaNode().get("prefixItems").size()
                : 0;

        if (schemaNode.isObject()) {
            this.schema = new JsonSchema(validationContext, schemaPath, parentSchema.getCurrentUri(), schemaNode, parentSchema);
            this.itemsAllowed = null;
        } else if (schemaNode.isBoolean()) {
            this.schema = null;
            this.itemsAllowed = schemaNode.asBoolean();
        } else {
            throw new JsonSchemaException("items is neither an object nor a boolean");
        }

        parseErrorCode(getValidatorType().getErrorCodeKey());
    }

    public Set<ValidationMessage> validate(JsonNode node, JsonNode rootNode, String at) {
        debug(logger, node, rootNode, at);

        if (!node.isArray() && !this.validationContext.getConfig().isTypeLoose()) {
            return Collections.emptySet();
        }

        if (itemsAllowed != null) {
            if (itemsAllowed) {
                return Collections.emptySet();
            } else if (node.size() > prefixItemsSize) {
                return Collections.singleton(buildValidationMessage(at, "" + prefixItemsSize));
            }
        }

        Set<ValidationMessage> errors = new LinkedHashSet<>();
        if (node.isArray()) {
            for (int i = prefixItemsSize; i < node.size(); i++) {
                doValidate(errors, i, node.get(i), rootNode, at);
            }
        } else if (!isPartOfTupleValidation) {
            doValidate(errors, 0, node, rootNode, at);
        }
        return Collections.unmodifiableSet(errors);
    }

    private void doValidate(Set<ValidationMessage> errors, int i, JsonNode node, JsonNode rootNode, String at) {
        errors.addAll(schema.validate(node, rootNode, at + "[" + i + "]"));
    }

    @Override
    public void preloadJsonSchema() {
        if (schema != null) {
            schema.initializeValidators();
        }
    }
}
