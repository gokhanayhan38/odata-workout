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
package org.apache.olingo.server.api.serializer;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmProperty;

/** Options for the OData serializer. */
public final class PrimitiveSerializerOptions {

  private ContextURL contextURL;
  private Boolean isNullable;
  private Integer maxLength;
  private Integer precision;
  private Integer scale;
  private Boolean isUnicode;
  
  /** Gets the {@link ContextURL}. */
  public ContextURL getContextURL() {
    return contextURL;
  }

  /** Gets the nullable facet. */
  public Boolean isNullable() {
    return isNullable;
  }

  /** Gets the maxLength facet. */
  public Integer getMaxLength() {
    return maxLength;
  }

  /** Gets the precision facet. */
  public Integer getPrecision() {
    return precision;
  }

  /** Gets the scale facet. */
  public Integer getScale() {
    return scale;
  }

  /** Gets the unicode facet. */
  public Boolean isUnicode() {
    return isUnicode;
  }
  
  private PrimitiveSerializerOptions() {}

  /** Initializes the options builder. */
  public static Builder with() {
    return new Builder();
  }

  /** Builder of OData serializer options. */
  public static final class Builder {

    private PrimitiveSerializerOptions options;

    private Builder() {
      options = new PrimitiveSerializerOptions();
    }

    /** Sets the {@link ContextURL}. */
    public Builder contextURL(final ContextURL contextURL) {
      options.contextURL = contextURL;
      return this;
    }

    /** Sets the nullable facet. */
    public Builder nullable(final Boolean isNullable) {
      options.isNullable = isNullable;
      return this;
    }

    /** Sets the maxLength facet. */
    public Builder maxLength(final Integer maxLength) {
      options.maxLength = maxLength;
      return this;
    }

    /** Sets the precision facet. */
    public Builder precision(final Integer precision) {
      options.precision = precision;
      return this;
    }

    /** Sets the scale facet. */
    public Builder scale(final Integer scale) {
      options.scale = scale;
      return this;
    }

    /** Sets the unicode facet. */
    public Builder unicode(final Boolean isUnicode) {
      options.isUnicode = isUnicode;
      return this;
    }
    
    /** Sets all facets from an EDM property. */
    public Builder facetsFrom(final EdmProperty property) {
      options.isNullable = property.isNullable();
      options.maxLength = property.getMaxLength();
      options.precision = property.getPrecision();
      options.scale = property.getScale();
      options.isUnicode = property.isUnicode();
      return this;
    }

    /** Builds the OData serializer options. */
    public PrimitiveSerializerOptions build() {
      return options;
    }
  }
}
