/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.olingo.commons.api.edm.provider.annotation.AnnotationExpression;
import org.apache.olingo.commons.api.edm.provider.annotation.Collection;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ClientCsdlCollection.CollectionDeserializer.class)
class ClientCsdlCollection extends AbstractClientCsdlDynamicAnnotationExpression implements Collection {

  private static final long serialVersionUID = -724749123749715643L;

  private final List<AnnotationExpression> items = new ArrayList<AnnotationExpression>();

  @Override
  public List<AnnotationExpression> getItems() {
    return items;
  }

  static class CollectionDeserializer extends AbstractClientCsdlEdmDeserializer<ClientCsdlCollection> {
    @Override
    protected ClientCsdlCollection doDeserialize(final JsonParser jp, final DeserializationContext ctxt)
            throws IOException {
      final ClientCsdlCollection collection = new ClientCsdlCollection();
      for (; jp.getCurrentToken() != JsonToken.END_OBJECT; jp.nextToken()) {
        final JsonToken token = jp.getCurrentToken();
        if (token == JsonToken.FIELD_NAME) {
          if (isAnnotationConstExprConstruct(jp)) {
            collection.getItems().add(parseAnnotationConstExprConstruct(jp));
          } else {
            collection.getItems().add(jp.readValueAs(AbstractClientCsdlDynamicAnnotationExpression.class));
          }
        }
      }

      return collection;
    }
  }
}
