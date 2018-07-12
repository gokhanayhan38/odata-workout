/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.edm.annotation;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.annotation.EdmCast;
import org.apache.olingo.commons.api.edm.annotation.EdmDynamicAnnotationExpression;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.annotation.Cast;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class EdmCastImpl extends AbstractEdmAnnotatableDynamicAnnotationExpression implements EdmCast {

  private final Edm edm;

  private final Cast cast;

  private final EdmDynamicAnnotationExpression value;

  private EdmType type;

  public EdmCastImpl(final Edm edm, final Cast cast, final EdmDynamicAnnotationExpression value) {
    this.edm = edm;
    this.cast = cast;
    this.value = value;
  }

  @Override
  public Integer getMaxLength() {
    return cast.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return cast.getPrecision();
  }

  @Override
  public Integer getScale() {
    return cast.getScale();
  }

  @Override
  public SRID getSrid() {
    return cast.getSrid();
  }

  @Override
  public EdmType getType() {
    if (type == null) {
      final EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(cast.getType()).build();
      type = typeInfo.getType();
    }
    return type;
  }

  @Override
  public EdmDynamicAnnotationExpression getValue() {
    return value;
  }

}
