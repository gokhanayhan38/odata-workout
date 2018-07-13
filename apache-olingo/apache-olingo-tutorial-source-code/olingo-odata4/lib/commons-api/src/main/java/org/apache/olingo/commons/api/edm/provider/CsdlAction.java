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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

/**
 * Represents an action CSDL item
 */
public class CsdlAction extends CsdlOperation {

  private static final long serialVersionUID = 211295197043448841L;

  @Override
  public CsdlAction setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public CsdlAction setBound(final boolean isBound) {
    this.isBound = isBound;
    return this;
  }

  @Override
  public CsdlAction setEntitySetPath(final String entitySetPath) {
    this.entitySetPath = entitySetPath;
    return this;
  }

  @Override
  public CsdlAction setParameters(final List<CsdlParameter> parameters) {
    this.parameters = parameters;
    return this;
  }


  @Override
  public CsdlAction setReturnType(final CsdlReturnType returnType) {
    this.returnType = returnType;
    return this;
  }
}
