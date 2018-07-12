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
package org.apache.olingo.fit.tecsvc.client;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class AbstractParamTecSvcITCase extends AbstractTecSvcITCase {

  @Parameterized.Parameter
  public ContentType contentType;

  /**
   * Returns a list of parameter arrays, in this case a list of one-element arrays
   * containing the content types to be used.
   */
  @Parameterized.Parameters(name = "{0}")
  public static List<ContentType[]> parameters() {
    return Arrays.asList(new ContentType[] { ContentType.APPLICATION_JSON },
        new ContentType[] { ContentType.APPLICATION_XML });
  }

  @Override
  protected ContentType getContentType() {
    return contentType;
  }
}
