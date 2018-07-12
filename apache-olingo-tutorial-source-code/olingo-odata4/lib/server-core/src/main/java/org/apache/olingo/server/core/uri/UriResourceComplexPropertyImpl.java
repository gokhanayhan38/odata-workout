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
package org.apache.olingo.server.core.uri;

import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceComplexProperty;
import org.apache.olingo.server.api.uri.UriResourceKind;

public class UriResourceComplexPropertyImpl extends UriResourceTypedImpl implements UriResourceComplexProperty {

  protected EdmProperty property;

  public UriResourceComplexPropertyImpl() {
    super(UriResourceKind.complexProperty);
  }

  @Override
  public EdmProperty getProperty() {
    return property;
  }

  public UriResourceComplexPropertyImpl setProperty(final EdmProperty property) {
    this.property = property;
    return this;
  }

  @Override
  public EdmComplexType getComplexType() {
    return (EdmComplexType) getType();
  }

  @Override
  public EdmComplexType getComplexTypeFilter() {
    return (EdmComplexType) typeFilter;
  }

  @Override
  public EdmType getType() {
    return property.getType();
  }

  @Override
  public boolean isCollection() {
    return property.isCollection();
  }

  @Override
  public String getSegmentValue(){
    return property.getName();
  }
  
  @Override
  public String toString() {
    return getSegmentValue();
  }

}
