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
package org.apache.olingo.fit.proxy;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.proxy.staticservice.Service;
import org.apache.olingo.fit.proxy.staticservice.microsoft.test.odata.services.odatawcfservice.InMemoryEntities;
import org.junit.Test;

public class UnauthorizedEntityCreateTestITCase extends AbstractTestITCase {

  private Service<EdmEnabledODataClient> ecf;

  private InMemoryEntities ime;

  public Service<EdmEnabledODataClient> getService() {
    if (ecf == null) {
      ecf = Service.getV4(testAuthServiceRootURL);
      ecf.getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
      ecf.getClient().getConfiguration().
          setHttpClientFactory(new BasicAuthHttpClientFactory("not_auth", "not_auth"));
    }
    return ecf;
  }

  @Test(expected = HttpClientException.class)
  public void unauthorizedCreate() {
    createPatchAndDeleteOrder(getContainer(), getService());
  }

  protected InMemoryEntities getContainer() {
    if (ime == null) {
      ime = getService().getEntityContainer(InMemoryEntities.class);
    }
    return ime;
  }
}
