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

import java.util.*;

import static com.networknt.schema.VersionCode.MaxV201909;

public class ItemsValidator implements JsonValidator {

    private final JsonValidator delegate;

    public ItemsValidator(String schemaPath, JsonNode schemaNode, JsonSchema parentSchema,
                          ValidationContext validationContext) {
        SpecVersion.VersionFlag versionFlag = SpecVersionDetector.detect(validationContext.getMetaSchema());
        boolean isMaxV201909 = SpecVersion.getVersionFlags(MaxV201909.getValue()).contains(versionFlag);
        this.delegate = isMaxV201909
                ? new ItemsValidatorMax2019(schemaPath, schemaNode, parentSchema, validationContext)
                : new ItemsValidatorMin2020(schemaPath, schemaNode, parentSchema, validationContext);
    }

    @Override
    public Set<ValidationMessage> validate(JsonNode rootNode) {
        return delegate.validate(rootNode);
    }

    @Override
    public Set<ValidationMessage> validate(JsonNode node, JsonNode rootNode, String at) {
        return delegate.validate(node, rootNode, at);
    }

    @Override
    public Set<ValidationMessage> walk(JsonNode node, JsonNode rootNode, String at, boolean shouldValidateSchema) {
        return delegate.walk(node, rootNode, at, shouldValidateSchema);
    }

    @Override
    public void preloadJsonSchema() {
        delegate.preloadJsonSchema();
    }
}
