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

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Ignore;
import org.junit.Test;

/**
 * see the class comment on {@link DeepInsertITCase}
 */
public class DeepInsertXmlITCase extends AbstractParamTecSvcITCase {

  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ET_KEY_NAV_NAME = "ETKeyNav";
  private static final String ET_TWO_KEY_NAV_NAME = "ETTwoKeyNav";
  private static final FullQualifiedName ET_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, ET_KEY_NAV_NAME);
  private static final FullQualifiedName ET_TWO_KEY_NAV = 
      new FullQualifiedName(SERVICE_NAMESPACE, ET_TWO_KEY_NAV_NAME);
  private static final String CT_PRIM_COMP = "CTPrimComp";
  private static final String CT_TWO_PRIM = "CTTwoPrim";
  private static final String CT_ALL_PRIM = "CTAllPrim";
  private static final String CT_NAV_FIVE_PROP = "CTNavFiveProp";
  private static final String CT_BASE_PRIM_COMP_NAV = "CTBasePrimCompNav";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";
  private static final String PROPERTY_COMP = "PropertyComp";
  private static final String PROPERTY_COMP_NAV = "PropertyCompNav";
  private static final String PROPERTY_COMP_COMP_NAV = "PropertyCompCompNav";
  private static final String PROPERTY_COMP_TWO_PRIM = "PropertyCompTwoPrim";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_ONE = "NavPropertyETTwoKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";
  private static final String COL_PROPERTY_STRING = "CollPropertyString";
  private static final String COL_PROPERTY_COMP_NAV = "CollPropertyCompNav";
  private static final String EDM_STRING = "Edm.String";

  @Test
  public void deepInsertExpandedResponse() {
    final ODataClient client = getClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ClientObjectFactory of = client.getObjectFactory();
    final ClientEntity entity = of.newEntity(ET_KEY_NAV);

    // Root entity
    entity.getProperties().add(
        of.newPrimitiveProperty(PROPERTY_STRING, 
            of.newPrimitiveValueBuilder().buildString("String Property level 0")));
    entity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 41)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 0, complex level 1")))));

    // First level NavPropertyETTwoKeyNavOne => Type ETTwoKeyNav
    final ClientEntity firstLevelTwoKeyNav = of.newEntity(ET_TWO_KEY_NAV);
    firstLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 1, complex level 1")))));
    firstLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    firstLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));
    final ClientInlineEntity firstLevelTwoKeyOneInline =
        of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, firstLevelTwoKeyNav);
    entity.addLink(firstLevelTwoKeyOneInline);

    // Second level NavPropertyETTwoKeyNavOne => Type ETTwoKeyNav
    final ClientEntity secondLevelTwoKeyNav = of.newEntity(ET_TWO_KEY_NAV);
    secondLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 421)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 2, complex level 1")))));
    secondLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    secondLevelTwoKeyNav.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));

    // Binding links
    secondLevelTwoKeyNav.addLink(of.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, client.newURIBuilder(
        SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 3109256773218160485L;
          {
            put(PROPERTY_INT16, 3);
            put(PROPERTY_STRING, "1");
          }
        }).build()));

    final ClientInlineEntity secondLevelTwoKeyOneInline =
        of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, secondLevelTwoKeyNav);
    firstLevelTwoKeyNav.addLink(secondLevelTwoKeyOneInline);

    // Third level NavPropertyETTwoKeyNavMany => Type ETTwoKeyNav
    final ClientEntity thirdLevelTwoKeyNavMany1 = of.newEntity(ET_TWO_KEY_NAV);
    thirdLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 3, complex level 1")))));
    thirdLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    thirdLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));

    final ClientEntity thirdLevelTwoKeyNavMany2 = of.newEntity(ET_TWO_KEY_NAV);
    thirdLevelTwoKeyNavMany2.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 432)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 3, complex level 1")))));
    thirdLevelTwoKeyNavMany2.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    thirdLevelTwoKeyNavMany2.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));

    final ClientEntitySet entitySetThirdLevelTwoKeyNavMany = of.newEntitySet();
    entitySetThirdLevelTwoKeyNavMany.getEntities().add(thirdLevelTwoKeyNavMany1);
    entitySetThirdLevelTwoKeyNavMany.getEntities().add(thirdLevelTwoKeyNavMany2);
    secondLevelTwoKeyNav.addLink(of.newDeepInsertEntitySet(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
        entitySetThirdLevelTwoKeyNavMany));

    // First level NavPropertyETTwoKeyNavMany => Type ETTwoKeyNav
    final ClientEntity firstLevelTwoKeyNavMany1 = of.newEntity(ET_TWO_KEY_NAV);
    firstLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 422)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString(
                "String Property level 1, complex level 1")))));
    firstLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    firstLevelTwoKeyNavMany1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));

    final ClientEntitySet entitySetfirstLevelTwoKeyNavMany = of.newEntitySet();
    entitySetfirstLevelTwoKeyNavMany.getEntities().add(firstLevelTwoKeyNavMany1);
    entity.addLink(of.newDeepInsertEntitySet(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
        entitySetfirstLevelTwoKeyNavMany));

    final ODataEntityCreateResponse<ClientEntity> createResponse =
        client.getCUDRequestFactory().getEntityCreateRequest(createURI, entity).execute();

    // Check response
    final ClientEntity resultEntityFirstLevel =
        createResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE)
        .asInlineEntity().getEntity();
    assertEquals(42, resultEntityFirstLevel.getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    assertEquals("String Property level 1, complex level 1", 
        resultEntityFirstLevel.getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    final ClientEntity resultEntitySecondLevel =
        resultEntityFirstLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE).asInlineEntity().getEntity();
    assertEquals(421, resultEntitySecondLevel.getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    assertEquals("String Property level 2, complex level 1", resultEntitySecondLevel
        .getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    final ClientEntitySet thirdLevelEntitySetNavMany =
        resultEntitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet().getEntitySet();
    assertEquals(2, thirdLevelEntitySetNavMany.getEntities().size());

    assertEquals(431, thirdLevelEntitySetNavMany.getEntities().get(0).getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("String Property level 3, complex level 1", thirdLevelEntitySetNavMany.getEntities().get(0)
        .getProperty(PROPERTY_COMP_TWO_PRIM).getComplexValue().get(PROPERTY_STRING).getPrimitiveValue().toValue());

    assertEquals(432, thirdLevelEntitySetNavMany.getEntities().get(1).getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("String Property level 3, complex level 1", thirdLevelEntitySetNavMany.getEntities().get(1)
        .getProperty(PROPERTY_COMP_TWO_PRIM).getComplexValue().get(PROPERTY_STRING).getPrimitiveValue().toValue());

    final ClientEntitySet firstLevelEntitySetNavMany =
        createResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet().getEntitySet();
    assertEquals(1, firstLevelEntitySetNavMany.getEntities().size());
    assertEquals(422, firstLevelEntitySetNavMany.getEntities().get(0).getProperty(PROPERTY_COMP_TWO_PRIM)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("String Property level 1, complex level 1", firstLevelEntitySetNavMany.getEntities().get(0)
        .getProperty(PROPERTY_COMP_TWO_PRIM).getComplexValue()
        .get(PROPERTY_STRING).getPrimitiveValue().toValue());
  }

  @Test
  public void simpleDeepInsert() throws EdmPrimitiveTypeException {
    final ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ClientObjectFactory of = client.getObjectFactory();
    final ClientEntity entity = client.getObjectFactory().newEntity(ET_KEY_NAV);

    // Prepare entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, 
                of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Non collection navigation property
    // Create related entity(EntitySet: ESTwoKeyNav, Type: ETTwoKeyNav, Nav. Property: NavPropertyETTwoKeyNavOne)
    final ClientEntity inlineEntitySingle = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntitySingle.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)));
    inlineEntitySingle.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("43")));
    inlineEntitySingle.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 430)))));
    inlineEntitySingle.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))));
    inlineEntitySingle.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 432)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("432")))));

    // Collection navigation property
    // The navigation property has a partner navigation property named "NavPropertyETKeyNavOne"
    // Create related entity(EntitySet: ESTwoKeyNav, Type: NavPropertyETTwoKeyNavMany
    final ClientEntity inlineEntityCol1 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol1.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 44)));
    inlineEntityCol1.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("44")));
    inlineEntityCol1.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 441)))));
    inlineEntityCol1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
          .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 440)))));
    inlineEntityCol1.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 442)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("442")))));

    final ClientEntity inlineEntityCol2 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol2.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 45)));
    inlineEntityCol2.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("45")));
    inlineEntityCol2.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 451)))));
    inlineEntityCol2.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
          .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 450)))));
    inlineEntityCol2.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 452)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("452")))));

    final ClientInlineEntity newDeepInsertEntityLink =
        of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, inlineEntitySingle);
    final ClientEntitySet newDeepInsertEntitySet = of.newEntitySet();
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol1);
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol2);
    final ClientInlineEntitySet newDeepInsertEntitySetLink =
        of.newDeepInsertEntitySet(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, newDeepInsertEntitySet);

    entity.addLink(newDeepInsertEntityLink);
    entity.addLink(newDeepInsertEntitySetLink);

    // Perform create request
    final ODataEntityCreateResponse<ClientEntity> responseCreate = client.getCUDRequestFactory()
        .getEntityCreateRequest(createURI, entity)
        .execute();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), responseCreate.getStatusCode());

    final String cookie = responseCreate.getHeader(HttpHeader.SET_COOKIE).toString();

    // Fetch ESKeyNav entity with expand of NavPropertyETTwoKeyNavOne nav. property
    ClientProperty propertyInt16 = responseCreate.getBody().getProperty(PROPERTY_INT16);
    final URI esKeyNavURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(
            propertyInt16.getPrimitiveValue().toValue()).expand(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
                NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).build();

    final ODataEntityRequest<ClientEntity> esKeyNavRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esKeyNavURI);
    esKeyNavRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esKeyNavResponse = esKeyNavRequest.execute();

    ClientEntity clientEntity = esKeyNavResponse.getBody();
    // Check nav. property NavPropertyETTwoKeyNavOne
    assertNotNull(clientEntity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE));
    ClientInlineEntity navOne = (ClientInlineEntity)clientEntity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE);
    assertShortOrInt(431, navOne.getEntity().getProperty(
        PROPERTY_COMP_NAV).getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check nav. property NavPropertyETTwoKeyNavMany
    assertNotNull(esKeyNavResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    ClientInlineEntitySet navMany = (ClientInlineEntitySet)clientEntity
        .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY);
    assertEquals(2, navMany.getEntitySet().getEntities().size());
    
    assertShortOrInt(441,  navMany.getEntitySet().getEntities().get(0).getProperty(PROPERTY_COMP_NAV)
        .getValue().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertShortOrInt(451, navMany.getEntitySet().getEntities().get(1).getProperty(PROPERTY_COMP_NAV)
        .getValue().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Fetch ESTwoKeyNav entities and check if available and the partner relation have been set up
    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavOne)
    Map<String, Object> composedKey = new HashMap<String, Object>();
    composedKey.put(PROPERTY_INT16, navOne.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, navOne.getEntity().getProperty(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    final URI esTwoKeyNavEntitySingleURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(composedKey)
        .build();
    final ODataEntityRequest<ClientEntity> esTwoKeyNavSingleRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esTwoKeyNavEntitySingleURI);
    esTwoKeyNavSingleRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavSingleResponse = esTwoKeyNavSingleRequest.execute();
    assertShortOrInt(431, esTwoKeyNavSingleResponse.getBody().getProperty(PROPERTY_COMP_NAV).getComplexValue()
        .get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(0))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, navMany.getEntitySet().getEntities().get(0).getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, navMany.getEntitySet().getEntities().get(0).getProperty(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    URI esTwoKeyNavEntityManyOneURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ClientEntity> esTwoKeyNavManyOneRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyOneURI);
    esTwoKeyNavManyOneRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavManyOneResponse = esTwoKeyNavManyOneRequest.execute();

    assertShortOrInt(441, esTwoKeyNavManyOneResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    
    assertNotNull(esTwoKeyNavManyOneResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    ClientInlineEntity nvLink = (ClientInlineEntity)esTwoKeyNavManyOneResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE);
    assertEquals(propertyInt16.getPrimitiveValue().toValue(), nvLink.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(1))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, navMany.getEntitySet().getEntities().get(1).getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, navMany.getEntitySet().getEntities().get(1).getProperty(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    URI esTwoKeyNavEntityManyTwoURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ClientEntity> esTwoKeyNavManyTwoRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyTwoURI);
    esTwoKeyNavManyTwoRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavManyTwoResponse = esTwoKeyNavManyTwoRequest.execute();

    assertShortOrInt(451, esTwoKeyNavManyTwoResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(esTwoKeyNavManyTwoResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    
    nvLink = (ClientInlineEntity)esTwoKeyNavManyTwoResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE);
    assertEquals(propertyInt16.getPrimitiveValue().toValue(), nvLink.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
  }

  @Test
  public void deepInsertSameEntitySet() throws EdmPrimitiveTypeException {
    final ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ClientObjectFactory of = client.getObjectFactory();
    final ClientEntity entity = client.getObjectFactory().newEntity(ET_KEY_NAV);

    // Prepare entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, 
                of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));
    entity.addLink(of.newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put(PROPERTY_INT16, 1);
            put(PROPERTY_STRING, "1");
          }
        })
        .build()));

    // Prepare inline entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    final ClientEntity innerEntity = of.newEntity(ET_KEY_NAV);
    innerEntity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)));
    innerEntity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("43")));
    innerEntity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))));
    innerEntity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))));
    innerEntity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))));
    innerEntity
    .getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder()
                .buildInt16((short) 431)))))));
    innerEntity.addLink(of.newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put(PROPERTY_INT16, 1);
            put(PROPERTY_STRING, "1");
          }
        })
        .build()));

    ClientInlineEntity inlineEntity = of.newDeepInsertEntity(NAV_PROPERTY_ET_KEY_NAV_ONE, innerEntity);
    entity.addLink(inlineEntity);

    final ODataEntityCreateResponse<ClientEntity> responseCreate =
        client.getCUDRequestFactory().getEntityCreateRequest(createURI, entity).execute();
    final String cookie = responseCreate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short esKeyNavEntityKey =
        responseCreate.getBody().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class);

    // Fetch Entity
    URI fetchEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(esKeyNavEntityKey)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    ODataEntityRequest<ClientEntity> entityRequest =
        client.getRetrieveRequestFactory().getEntityRequest(fetchEntityURI);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    ClientEntity clientEntity = entityResponse.getBody();
    ClientInlineEntity navOne =
        (ClientInlineEntity) clientEntity.getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE);

    // Check values
    assertShortOrInt(431, navOne.getEntity().getProperty(PROPERTY_COMP_NAV).getComplexValue()
        .get(PROPERTY_INT16).getPrimitiveValue().toValue());

    Short innerEntityInt16Key = navOne.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toCastValue(Short.class);

    final URI innerEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(innerEntityInt16Key)
        .build();
    final ODataEntityRequest<ClientEntity> innerRequest =
        client.getRetrieveRequestFactory().getEntityRequest(innerEntityURI);
    innerRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    ODataRetrieveResponse<ClientEntity> innerResponse = innerRequest.execute();

    assertShortOrInt(431, innerResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
  }

  @Test
  public void consistency() throws EdmPrimitiveTypeException {
    final EdmEnabledODataClient client = getClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();
    final String cookie = getCookie();

    // Do not set PropertyString(Nullable=false)
    final ClientEntity entity = of.newEntity(ET_KEY_NAV);
    entity.getProperties().add(
        of.newCollectionProperty(COL_PROPERTY_STRING,
            of.newCollectionValue(EDM_STRING).add(
                of.newPrimitiveValueBuilder().buildString("Test"))));

    final URI targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();

    try {
      ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
          .getEntityCreateRequest(targetURI, entity);
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
      request.execute();
      fail("Expecting bad request");
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }

    // Entity must not be created
    validateSet(targetURI, cookie, (short) 1, (short) 2, (short) 3);
  }

  @Test
  public void invalidType() throws EdmPrimitiveTypeException {
    final EdmEnabledODataClient client = getClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();
    final String cookie = getCookie();

    final ClientEntity entity = of.newEntity(ET_KEY_NAV);
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_STRING, 
        of.newPrimitiveValueBuilder().buildInt32(1)));
    final URI targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();

    try {
      ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
          .getEntityCreateRequest(targetURI, entity);
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
      request.execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }

    validateSet(targetURI, cookie, (short) 1, (short) 2, (short) 3);

    entity.getProperties().add(
        of.newCollectionProperty(PROPERTY_STRING,
            of.newCollectionValue(EDM_STRING).add(
                of.newPrimitiveValueBuilder().buildString("Test"))));

    try {
      ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
          .getEntityCreateRequest(targetURI, entity);
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
      request.execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }

    validateSet(targetURI, cookie, (short) 1, (short) 2, (short) 3);
  }

  @Test
  @Ignore
  public void deepInsertOnNavigationPropertyInComplexProperty() {
    final EdmEnabledODataClient client = getClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final ClientEntity inlineEntity = of.newEntity(ET_TWO_KEY_NAV);
    inlineEntity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    inlineEntity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));
    inlineEntity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 1)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("1")))));

    final ClientEntity entity = of.newEntity(ET_TWO_KEY_NAV);
    entity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)));
    entity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_BASE_PRIM_COMP_NAV)));
    entity.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 2)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("2")))));

    final ClientLink link = of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, inlineEntity);
    final ClientComplexValue complexValueCreate = of.newComplexValue(CT_NAV_FIVE_PROP);
    complexValueCreate.getNavigationLinks().add(link);

    entity.getProperties().add(
        of.newCollectionProperty(COL_PROPERTY_COMP_NAV, of.newCollectionValue(CT_NAV_FIVE_PROP)
            .add(complexValueCreate)));

    final URI targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).build();
    final ODataEntityCreateResponse<ClientEntity> response = client.getCUDRequestFactory()
        .getEntityCreateRequest(targetURI, entity)
        .execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    final Iterator<ClientValue> iter = response.getBody()
        .getProperty(COL_PROPERTY_COMP_NAV)
        .getCollectionValue()
        .iterator();

    assertTrue(iter.hasNext());
    final ClientComplexValue complexValue = iter.next().asComplex();
    final ClientLink linkedEntity = complexValue.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE);
    assertNotNull(linkedEntity);
    assertEquals(1, linkedEntity.asInlineEntity()
        .getEntity()
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  public void deepUpsert() {
    final ODataClient client = getClient();
    final URI updateURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(815)
        .build();
    final ClientObjectFactory of = client.getObjectFactory();
    final ClientEntity entity = client.getObjectFactory().newEntity(ET_KEY_NAV);

    // Prepare entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, 
                of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Non collection navigation property
    // Create related entity(EntitySet: ESTwoKeyNav, Type: ETTwoKeyNav, Nav. Property: NavPropertyETTwoKeyNavOne)
    final ClientEntity inlineEntitySingle = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntitySingle.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)));
    inlineEntitySingle.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("43")));
    inlineEntitySingle.getProperties().add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)))));
    inlineEntitySingle.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))));
    inlineEntitySingle.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 432)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("432")))));

    // Collection navigation property
    // The navigation property has a partner navigation property named "NavPropertyETKeyNavOne"
    // Create related entity(EntitySet: ESTwoKeyNav, Type: NavPropertyETTwoKeyNavMany
    final ClientEntity inlineEntityCol1 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol1.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 44)));
    inlineEntityCol1.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("44")));
    inlineEntityCol1.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 441)))));
    inlineEntityCol1.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 441)))));
    inlineEntityCol1.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 442)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("442")))));

    final ClientEntity inlineEntityCol2 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol2.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 45)));
    inlineEntityCol2.getProperties()
    .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("45")));
    inlineEntityCol2.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 451)))));
    inlineEntityCol2.getProperties().add(
        of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 451)))));
    inlineEntityCol2.getProperties()
    .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 452)))
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("452")))));

    final ClientInlineEntity newDeepInsertEntityLink =
        of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, inlineEntitySingle);
    final ClientEntitySet newDeepInsertEntitySet = of.newEntitySet();
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol1);
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol2);
    final ClientInlineEntitySet newDeepInsertEntitySetLink =
        of.newDeepInsertEntitySet(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, newDeepInsertEntitySet);

    entity.addLink(newDeepInsertEntityLink);
    entity.addLink(newDeepInsertEntitySetLink);

    // Perform update request (upsert)
    final ODataEntityUpdateResponse<ClientEntity> responseCreate = client.getCUDRequestFactory()
        .getEntityUpdateRequest(updateURI, UpdateType.PATCH, entity)
        .execute();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), responseCreate.getStatusCode());

    final String cookie = responseCreate.getHeader(HttpHeader.SET_COOKIE).toString();

    // Fetch ESKeyNav entity with expand of NavPropertyETTwoKeyNavOne nav. property
    ClientProperty propertyInt16 = responseCreate.getBody().getProperty(PROPERTY_INT16);
    final URI esKeyNavURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(
            propertyInt16.getPrimitiveValue().toValue()).expand(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
                NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).build();

    final ODataEntityRequest<ClientEntity> esKeyNavRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esKeyNavURI);
    esKeyNavRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esKeyNavResponse = esKeyNavRequest.execute();

    // Check nav. property NavPropertyETTwoKeyNavOne
    assertNotNull(esKeyNavResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE));
    ClientInlineEntity navOne = (ClientInlineEntity)esKeyNavResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE);
    assertShortOrInt(431, navOne.getEntity().getProperty(PROPERTY_COMP_NAV).getComplexValue()
        .get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check nav. property NavPropertyETTwoKeyNavMany
    assertNotNull(esKeyNavResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    ClientInlineEntitySet navMany = (ClientInlineEntitySet)esKeyNavResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY);
    assertEquals(2, navMany.getEntitySet().getEntities().size());

    assertShortOrInt(441, navMany.getEntitySet().getEntities().get(0).getProperty(PROPERTY_COMP_NAV).getValue()
        .asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    
    assertShortOrInt(451, navMany.getEntitySet().getEntities().get(1).getProperty(PROPERTY_COMP_NAV).getValue()
        .asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Fetch ESTwoKeyNav entities and check if available and the partner relation have been set up
    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavOne)
    Map<String, Object> composedKey = new HashMap<String, Object>();
    composedKey.put(PROPERTY_INT16, navOne.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING,  navOne.getEntity().getProperty(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    final URI esTwoKeyNavEntitySingleURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(composedKey)
        .build();

    final ODataEntityRequest<ClientEntity> esTwoKeyNavSingleRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esTwoKeyNavEntitySingleURI);
    esTwoKeyNavSingleRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavSingleResponse = esTwoKeyNavSingleRequest.execute();
    assertShortOrInt(431, esTwoKeyNavSingleResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(0))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, navMany.getEntitySet().getEntities().get(0)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING,navMany.getEntitySet().getEntities().get(0)
        .getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    URI esTwoKeyNavEntityManyOneURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ClientEntity> esTwoKeyNavManyOneRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyOneURI);
    esTwoKeyNavManyOneRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavManyOneResponse = esTwoKeyNavManyOneRequest.execute();

    assertShortOrInt(441, esTwoKeyNavManyOneResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(esTwoKeyNavManyOneResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    ClientInlineEntity nvLink = (ClientInlineEntity)esTwoKeyNavManyOneResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE); 
    assertEquals(propertyInt16.getPrimitiveValue().toValue(), nvLink.getEntity().getProperty(PROPERTY_INT16)
        .getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(1))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, navMany.getEntitySet().getEntities().get(1)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING,navMany.getEntitySet().getEntities().get(1)
        .getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    URI esTwoKeyNavEntityManyTwoURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ClientEntity> esTwoKeyNavManyTwoRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyTwoURI);
    esTwoKeyNavManyTwoRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> esTwoKeyNavManyTwoResponse = esTwoKeyNavManyTwoRequest.execute();

    assertShortOrInt(451, esTwoKeyNavManyTwoResponse.getBody().getProperty(PROPERTY_COMP_NAV)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(esTwoKeyNavManyTwoResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    nvLink = (ClientInlineEntity)esTwoKeyNavManyTwoResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE);
    assertEquals(propertyInt16.getPrimitiveValue().toValue(),nvLink.getEntity()
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }

  private String getCookie() {
    final EdmEnabledODataClient client = getClient(SERVICE_URI);
    final ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build())
        .execute();

    return response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
  }

  private void validateSet(final URI uri, final String cookie, final short... keys) 
      throws EdmPrimitiveTypeException {
    final EdmEnabledODataClient client = getClient(SERVICE_URI);
    final ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri);
    request.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(3, response.getBody().getEntities().size());

    for (final ClientEntity responseEntity : response.getBody().getEntities()) {
      short propertyInt16 = responseEntity.getProperty(PROPERTY_INT16)
          .getPrimitiveValue().toCastValue(Short.class);

      boolean found = false;
      for (int i = 0; i < keys.length && !found; i++) {
        if (propertyInt16 == keys[i]) {
          found = true;
        }
      }

      if (!found) {
        fail("Invalid key " + propertyInt16);
      }
    }
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ContentType.APPLICATION_ATOM_XML);
    return odata;
  }  

  @Override
  protected void assertContentType(final String content) {
    assertThat(content, containsString(ContentType.APPLICATION_ATOM_XML.toContentTypeString()));
  }

  @Override
  protected EdmEnabledODataClient getClient(final String serviceRoot) {
    return ODataClientFactory.getEdmEnabledClient(serviceRoot, ContentType.APPLICATION_ATOM_XML);
  }  
}