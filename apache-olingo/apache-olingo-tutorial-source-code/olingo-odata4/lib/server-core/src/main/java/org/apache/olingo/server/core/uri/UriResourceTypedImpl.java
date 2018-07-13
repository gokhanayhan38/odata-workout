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

import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.UriResourcePartTyped;

public abstract class UriResourceTypedImpl extends UriResourceImpl implements UriResourcePartTyped {

  protected EdmType typeFilter = null;

  public UriResourceTypedImpl(final UriResourceKind kind) {
    super(kind);
  }

  public EdmType getTypeFilter() {
    return typeFilter;
  }

  public UriResourceTypedImpl setTypeFilter(final EdmStructuredType typeFilter) {
    this.typeFilter = typeFilter;
    return this;
  }

  public String getSegmentValue(final boolean includeFilters) {
    if (includeFilters) {
      if (typeFilter != null) {
        return getSegmentValue() + "/" + typeFilter.getFullQualifiedName().toString();
      } else {
        return getSegmentValue();
      }
    }
    return getSegmentValue();
  }

  @Override
  public String toString(final boolean includeFilters) {
    return getSegmentValue(includeFilters);
  }
}
