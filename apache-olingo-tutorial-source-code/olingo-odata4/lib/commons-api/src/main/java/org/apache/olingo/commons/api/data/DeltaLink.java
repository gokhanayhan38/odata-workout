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
package org.apache.olingo.commons.api.data;

import java.net.URI;

/**
 * A delta link.
 */
public class DeltaLink extends Annotatable {

  private URI source;
  private String relationship;
  private URI target;

  /**
   * Get source of this link.
   * @return source of this link
   */
  public URI getSource() {
    return source;
  }

  /**
   * Set source of this link.
   * @param source source of this link
   */
  public void setSource(final URI source) {
    this.source = source;
  }

  /**
   * Get relationship of this link.
   * @return relationship of this link
   */
  public String getRelationship() {
    return relationship;
  }

  /**
   * Set relationship of this link.
   * @param relationship relationship of this link
   */
  public void setRelationship(final String relationship) {
    this.relationship = relationship;
  }

  /**
   * Get target of this link.
   * @return target of this link
   */
  public URI getTarget() {
    return target;
  }

  /**
   * Set target of this link.
   * @param target target of this link
   */
  public void setTarget(final URI target) {
    this.target = target;
  }
}
