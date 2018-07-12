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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;

public class EdmReturnTypeImpl implements EdmReturnType {

  private final CsdlReturnType returnType;
  private final EdmTypeInfo typeInfo;
  private EdmType typeImpl;

  public EdmReturnTypeImpl(final Edm edm, final CsdlReturnType returnType) {
    this.returnType = returnType;
    typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(returnType.getType()).build();
  }

  @Override
  public boolean isCollection() {
    return returnType.isCollection();
  }

  @Override
  public boolean isNullable() {
    return returnType.isNullable();
  }

  @Override
  public Integer getMaxLength() {
    return returnType.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return returnType.getPrecision();
  }

  @Override
  public Integer getScale() {
    return returnType.getScale();
  }

  @Override
  public SRID getSrid() {
    return returnType.getSrid();
  }

  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      typeImpl = typeInfo.getType();
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + typeInfo.getFullQualifiedName());
      }
    }

    return typeImpl;
  }
}
