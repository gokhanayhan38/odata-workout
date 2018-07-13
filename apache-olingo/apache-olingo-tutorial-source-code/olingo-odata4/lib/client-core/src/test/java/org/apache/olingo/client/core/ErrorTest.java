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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private ODataError error(final String name, final ContentType contentType) throws ODataDeserializerException {
    final ODataError error = getClient().getDeserializer(contentType).toError(
            getClass().getResourceAsStream(name + "." + getSuffix(contentType)));
    assertNotNull(error);
    return error;
  }

  private void simple(final ContentType contentType) throws ODataDeserializerException {
    final ODataError error = error("error", contentType);
    assertEquals("501", error.getCode());
    assertEquals("Unsupported functionality", error.getMessage());
    assertEquals("query", error.getTarget());
  }

  @Test
  public void jsonSimple() throws Exception {
    simple(ContentType.JSON);
  }

  @Test
  public void atomSimple() throws Exception {
    simple(ContentType.APPLICATION_ATOM_XML);
  }

}
