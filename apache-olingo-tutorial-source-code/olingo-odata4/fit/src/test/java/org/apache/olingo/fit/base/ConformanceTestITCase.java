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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.client.api.communication.request.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataDeltaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

/**
 * 13.2 Interoperable OData Clients
 * <br />
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793762
 */
public class ConformanceTestITCase extends AbstractTestITCase {

  /**
   * 1. MUST specify the OData-MaxVersion header in requests (section 8.2.6).
   */
  @Test
  public void item1() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    assertEquals("4.0", req.getHeader(HttpHeader.ODATA_MAX_VERSION));

    assertNotNull(req.execute().getBody());
  }

  /**
   * 2. MUST specify OData-Version (section 8.1.5) and Content-Type (section 8.1.1) in any request with a payload.
   */
  @Test
  public void item2() {
    final ClientEntity order = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    final ClientProperty orderId = getClient().getObjectFactory().newPrimitiveProperty("OrderID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(2000));
    order.getProperties().add(orderId);

    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(2011, 2, 4, 16, 3, 57);
    final ClientProperty orderDate = getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build());
    order.getProperties().add(orderDate);

    final ClientProperty shelfLife = getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000001")).build());
    order.getProperties().add(shelfLife);

    final ClientCollectionValue<ClientValue> orderShelfLifesValue = getClient().getObjectFactory().
        newCollectionValue("Collection(Duration)");
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000001")).build());
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000002")).build());
    final ClientProperty orderShelfLifes = getClient().getObjectFactory().
        newCollectionProperty("OrderShelfLifes", orderShelfLifesValue);
    order.getProperties().add(orderShelfLifes);

    final ODataEntityCreateRequest<ClientEntity> req = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Orders").build(), order);
    req.setFormat(ContentType.JSON_FULL_METADATA);

    // check for OData-Version
    assertEquals("4.0", req.getHeader(HttpHeader.ODATA_VERSION));

    // check for Content-Type
    assertEquals(
        ContentType.JSON_FULL_METADATA.toContentTypeString(),
        req.getHeader("Content-Type"));
    assertEquals(
        ContentType.JSON_FULL_METADATA.toContentTypeString(),
        req.getContentType());

    final ClientEntity created = req.execute().getBody();
    assertNotNull(created);

    final URI deleteURI = created.getEditLink() == null
        ? getClient().newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Orders").appendKeySegment(2000).build()
            : created.getEditLink();
            final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(deleteURI);
            final ODataDeleteResponse deleteRes = deleteReq.execute();
            assertEquals(204, deleteRes.getStatusCode());
  }

  /**
   * 4. MUST follow redirects (section 9.1.5).
   */
  @Test
  public void item4() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("redirect").
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    assertEquals("4.0", req.getHeader(HttpHeader.ODATA_MAX_VERSION));

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertTrue(entity.getProperty("Home").hasPrimitiveValue());
    assertEquals("Edm.GeographyPoint", entity.getProperty("Home").getPrimitiveValue().getTypeName());
  }

  /**
   * 5. MUST correctly handle next links (section 11.2.5.7).
   */
  @Test
  public void item5() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON_FULL_METADATA);
    req.setPrefer(client.newPreferences().maxPageSize(5));

    ODataRetrieveResponse<ClientEntitySet> res = req.execute();
    ClientEntitySet feed = res.getBody();

    assertNotNull(feed);

    assertEquals(5, feed.getEntities().size());
    assertNotNull(feed.getNext());

    final URI expected = URI.create(testStaticServiceRootURL + "/People?$skiptoken=5");
    final URI found = URIUtils.getURI(testStaticServiceRootURL, feed.getNext().toASCIIString());

    assertEquals(expected, found);

    req = client.getRetrieveRequestFactory().getEntitySetRequest(found);
    req.setFormat(ContentType.JSON_FULL_METADATA);

    res = req.execute();
    feed = res.getBody();

    assertNotNull(feed);
  }

  /**
   * 6. MUST support instances returning properties and navigation properties not specified in metadata (section 11.2).
   */
  @Test
  public void item6() {
    final Integer id = 2000;

    ClientEntity rowIndex = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.OpenTypesServiceV4.RowIndex"));
    getClient().getBinder().add(rowIndex,
        getClient().getObjectFactory().newPrimitiveProperty("Id",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));

    // add property not in metadata
    getClient().getBinder().add(rowIndex,
        getClient().getObjectFactory().newPrimitiveProperty("aString",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("string")));

    // add navigation property not in metadata
    rowIndex.addLink(client.getObjectFactory().newEntityNavigationLink(
        "Row", URI.create(testOpenTypeServiceRootURL + "/Row(71f7d0dc-ede4-45eb-b421-555a2aa1e58f)")));

    final ODataEntityCreateRequest<ClientEntity> createReq = getClient().getCUDRequestFactory().
        getEntityCreateRequest(getClient().newURIBuilder(testOpenTypeServiceRootURL).
            appendEntitySetSegment("RowIndex").build(), rowIndex);

    final ODataEntityCreateResponse<ClientEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    final URIBuilder builder = getClient().newURIBuilder(testOpenTypeServiceRootURL).
        appendEntitySetSegment("RowIndex").appendKeySegment(id);

    rowIndex = read(ContentType.JSON_FULL_METADATA, builder.build());
    assertNotNull(rowIndex);
    assertEquals(EdmPrimitiveTypeKind.Int32, rowIndex.getProperty("Id").getPrimitiveValue().getTypeKind());
    assertEquals(EdmPrimitiveTypeKind.String, rowIndex.getProperty("aString").getPrimitiveValue().getTypeKind());
    assertNotNull(rowIndex.getNavigationLink("Row"));

    final ODataDeleteResponse deleteRes = getClient().getCUDRequestFactory().
        getDeleteRequest(rowIndex.getEditLink()).execute();
    assertEquals(204, deleteRes.getStatusCode());
  }

  /**
   * 7. MUST generate PATCH requests for updates, if the client supports updates (section 11.4.3).
   */
  @Test
  public void item7() {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).build();

    final ClientEntity patch = client.getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));
    patch.setEditLink(uri);

    final String newname = "New Name (" + System.currentTimeMillis() + ")";
    patch.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("FirstName",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString(newname)));

    final ODataEntityUpdateRequest<ClientEntity> req =
        getClient().getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);

    final ODataEntityUpdateResponse<ClientEntity> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ClientEntity actual = read(ContentType.JSON, uri);

    assertEquals(newname, actual.getProperty("FirstName").getPrimitiveValue().toString());
  }

  /**
   * 8. SHOULD support basic authentication as specified in [RFC2617] over HTTPS.
   * <br />
   * Unfortunately no service over HTTPs is available yet.
   */
  @Test
  public void item8() {
    client.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("odatajclient", "odatajclient"));

    final URIBuilder uriBuilder = client.newURIBuilder(testAuthServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON_FULL_METADATA);

    assertNotNull(req.execute().getBody());

    client.getConfiguration().setHttpClientFactory(new DefaultHttpClientFactory());
  }

  /**
   * 9. MAY request entity references in place of entities previously returned in the response (section 11.2.7).
   */
  @Test
  public void item9() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Orders").appendKeySegment(8).appendNavigationSegment("CustomerForOrder").
        appendRefSegment();

    ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON_FULL_METADATA);

    ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertNotNull(res);

    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertTrue(entity.getId().toASCIIString().endsWith("/StaticService/V40/Static.svc/Customers(PersonID=1)"));

    final URI referenceURI = client.newURIBuilder(testStaticServiceRootURL).
        appendEntityIdSegment(entity.getId().toASCIIString()).build();

    req = client.getRetrieveRequestFactory().getEntityRequest(referenceURI);
    req.setFormat(ContentType.JSON_FULL_METADATA);

    res = req.execute();
    assertNotNull(res);
    assertNotNull(res.getBody());
  }

  /**
   * 10. MAY support deleted entities, link entities, deleted link entities in a delta response (section 11.3).
   */
  @Test
  public void item10() {
    final ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(
        client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customers").build());
    req.setPrefer(client.newPreferences().trackChanges());

    final ClientEntitySet customers = req.execute().getBody();
    assertNotNull(customers);
    assertNotNull(customers.getDeltaLink());

    final ODataDeltaRequest deltaReq = client.getRetrieveRequestFactory().getDeltaRequest(customers.getDeltaLink());
    deltaReq.setFormat(ContentType.JSON_FULL_METADATA);

    final ClientDelta delta = deltaReq.execute().getBody();
    assertNotNull(delta);

    assertNotNull(delta.getDeltaLink());
    assertTrue(delta.getDeltaLink().isAbsolute());
    assertEquals(5, delta.getCount(), 0);

    assertEquals(1, delta.getDeletedEntities().size());
    assertTrue(delta.getDeletedEntities().get(0).getId().isAbsolute());
    assertTrue(delta.getDeletedEntities().get(0).getId().toASCIIString().endsWith("Customers('ANTON')"));

    assertEquals(1, delta.getAddedLinks().size());
    assertTrue(delta.getAddedLinks().get(0).getSource().isAbsolute());
    assertTrue(delta.getAddedLinks().get(0).getSource().toASCIIString().endsWith("Customers('BOTTM')"));
    assertEquals("Orders", delta.getAddedLinks().get(0).getRelationship());

    assertEquals(1, delta.getDeletedLinks().size());
    assertTrue(delta.getDeletedLinks().get(0).getSource().isAbsolute());
    assertTrue(delta.getDeletedLinks().get(0).getSource().toASCIIString().endsWith("Customers('ALFKI')"));
    assertEquals("Orders", delta.getDeletedLinks().get(0).getRelationship());

    assertEquals(2, delta.getEntities().size());
    ClientProperty property = delta.getEntities().get(0).getProperty("ContactName");
    assertNotNull(property);
    assertTrue(property.hasPrimitiveValue());
    property = delta.getEntities().get(1).getProperty("ShippingAddress");
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
  }

  /**
   * 11. MAY support asynchronous responses (section 9.1.3).
   */
  @Test
  public void item11() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("async").appendEntitySetSegment("Orders");

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON_FULL_METADATA);

    final AsyncRequestWrapper<ODataRetrieveResponse<ClientEntitySet>> async =
        client.getAsyncRequestFactory().<ODataRetrieveResponse<ClientEntitySet>> getAsyncRequestWrapper(req);
    async.callback(URI.create("http://client.service.it/callback/endpoint"));

    final AsyncResponseWrapper<ODataRetrieveResponse<ClientEntitySet>> responseWrapper = async.execute();

    assertTrue(responseWrapper.isPreferenceApplied());
    assertTrue(responseWrapper.isDone());

    final ODataRetrieveResponse<ClientEntitySet> res = responseWrapper.getODataResponse();
    final ClientEntitySet entitySet = res.getBody();

    assertFalse(entitySet.getEntities().isEmpty());
  }

  /**
   * 12. MAY support odata.metadata=minimal in a JSON response (see [OData-JSON]).
   */
  @Test
  public void item12() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON);

    assertEquals("application/json;odata.metadata=minimal", req.getHeader("Accept"));
    assertEquals("application/json;odata.metadata=minimal", req.getAccept());

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertTrue(res.getContentType().startsWith("application/json; odata.metadata=minimal"));

    assertNotNull(res.getBody());
  }
}
