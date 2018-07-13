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
package org.apache.olingo.server.core.deserializer.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlMapping;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class ODataJsonDeserializerEntityTest extends AbstractODataDeserializerTest {

  @Test
  public void emptyEntity() throws Exception {
    String entityString = "{}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(0, properties.size());
  }

  @Test
  public void simpleEntityETAllPrim() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());
    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertEquals(new Boolean(true), entity.getProperty("PropertyBoolean").getValue());
    assertEquals(new Short((short) 255), entity.getProperty("PropertyByte").getValue());
    assertEquals(new Byte((byte) 127), entity.getProperty("PropertySByte").getValue());
    assertEquals(new Integer(2147483647), entity.getProperty("PropertyInt32").getValue());
    assertEquals(new Long(9223372036854775807l), entity.getProperty("PropertyInt64").getValue());
    assertEquals(new Float(1.79E20), entity.getProperty("PropertySingle").getValue());
    assertEquals(new Double(-1.79E19), entity.getProperty("PropertyDouble").getValue());
    assertEquals(new BigDecimal(34), entity.getProperty("PropertyDecimal").getValue());
    assertNotNull(entity.getProperty("PropertyBinary").getValue());
    assertNotNull(entity.getProperty("PropertyDate").getValue());
    assertNotNull(entity.getProperty("PropertyDateTimeOffset").getValue());
    assertNotNull(entity.getProperty("PropertyDuration").getValue());
    assertNotNull(entity.getProperty("PropertyGuid").getValue());
    assertNotNull(entity.getProperty("PropertyTimeOfDay").getValue());
  }

  @Test
  public void simpleEntityETAllPrimWithDefaultNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertNull(entity.getProperty("PropertyBoolean").getValue());
    assertNull(entity.getProperty("PropertyDate").getValue());
  }

  @Test
  public void simpleEntityETAllPrimNoTAllPropertiesPresent() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"" +
            "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
  }

  @Test
  public void simpleEntityETNoneNullable() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertNull(entity.getProperty("PropertyBoolean").getValue());
    assertNull(entity.getProperty("PropertyDate").getValue());
  }

  @Test
  public void simpleEntityETCompAllPrim() throws Exception {
    String entityString = "{\"PropertyInt16\":32767," +
        "\"PropertyComp\":{" +
        "\"PropertyString\":\"First Resource - first\"," +
        "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
        "\"PropertyBoolean\":true," +
        "\"PropertyByte\":255," +
        "\"PropertyDate\":\"2012-10-03\"," +
        "\"PropertyDateTimeOffset\":\"2012-10-03T07:16:23.1234567Z\"," +
        "\"PropertyDecimal\":34.27," +
        "\"PropertySingle\":1.79E20," +
        "\"PropertyDouble\":-1.79E19," +
        "\"PropertyDuration\":\"PT6S\"," +
        "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
        "\"PropertyInt16\":32767," +
        "\"PropertyInt32\":2147483647," +
        "\"PropertyInt64\":9223372036854775807," +
        "\"PropertySByte\":127," +
        "\"PropertyTimeOfDay\":\"01:00:01\"}}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCompAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());

    assertNotNull(entity.getProperty("PropertyComp"));
    assertNotNull(entity.getProperty("PropertyComp") instanceof List);
    List<Property> complexProperties = entity.getProperty("PropertyComp").asComplex().getValue();
    assertEquals(16, complexProperties.size());
  }

  @Test
  public void simpleEntityETCollAllPrim() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyBoolean\":[true,false,true],"
        + "\"CollPropertyByte\":[50,200,249],"
        + "\"CollPropertySByte\":[-120,120,126],"
        + "\"CollPropertyInt16\":[1000,2000,30112],"
        + "\"CollPropertyInt32\":[23232323,11223355,10000001],"
        + "\"CollPropertyInt64\":[929292929292,333333333333,444444444444],"
        + "\"CollPropertySingle\":[1790.0,26600.0,3210.0],"
        + "\"CollPropertyDouble\":[-17900.0,-2.78E7,3210.0],"
        + "\"CollPropertyDecimal\":[12,-2,1234],"
        + "\"CollPropertyBinary\":[\"q83v\",\"ASNF\",\"VGeJ\"],"
        + "\"CollPropertyDate\":[\"1958-12-03\",\"1999-08-05\",\"2013-06-25\"],"
        + "\"CollPropertyDateTimeOffset\":[\"2015-08-12T03:08:34Z\",\"1970-03-28T12:11:10Z\","
        + "\"1948-02-17T09:09:09Z\"],"
        + "\"CollPropertyDuration\":[\"PT13S\",\"PT5H28M0S\",\"PT1H0S\"],"
        + "\"CollPropertyGuid\":[\"ffffff67-89ab-cdef-0123-456789aaaaaa\",\"eeeeee67-89ab-cdef-0123-456789bbbbbb\","
        + "\"cccccc67-89ab-cdef-0123-456789cccccc\"],"
        + "\"CollPropertyTimeOfDay\":[\"04:14:13\",\"23:59:59\",\"01:12:33\"]"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(17, properties.size());

    // All properties need 3 entires
    for (Property prop : properties) {
      if (!prop.getName().equals("PropertyInt16")) {
        assertEquals(ValueType.COLLECTION_PRIMITIVE, prop.getValueType());
        assertTrue(prop.getValue() instanceof List);
        List<? extends Object> asCollection = prop.asCollection();
        assertEquals(3, asCollection.size());
      }
    }
    Property property = entity.getProperty("CollPropertyBoolean");
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(true, asCollection.get(0));
    assertEquals(false, asCollection.get(1));
    assertEquals(true, asCollection.get(2));
  }

  @Test
  public void simpleEntityETMixPrimCollComp() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(4, properties.size());

    Property property = entity.getProperty("CollPropertyComp");
    assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

    assertTrue(property.getValue() instanceof List);
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(3, asCollection.size());

    for (Object arrayElement : asCollection) {
      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      assertEquals(2, castedArrayElement.size());
    }
  }

  @Test
  public void eTMixPrimCollCompMIssingPropertyInComplexType() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":{\"PropertyInt16\":111},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123},"
        + "{\"PropertyInt16\":456},"
        + "{\"PropertyInt16\":789}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    Property complexProperty = entity.getProperty("PropertyComp");
    assertEquals(ValueType.COMPLEX, complexProperty.getValueType());
    List<Property> complexPropertyValues = complexProperty.asComplex().getValue();
    assertEquals(1, complexPropertyValues.size());

    Property property = entity.getProperty("CollPropertyComp");
    assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

    assertTrue(property.getValue() instanceof List);
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(3, asCollection.size());

    for (Object arrayElement : asCollection) {
      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      assertEquals(1, castedArrayElement.size());
    }
  }

  @Test
  public void simpleEntityWithContextURL() throws Exception {
    String entityString =
        "{\"@odata.context\": \"$metadata#ESAllPrim/$entity\"," +
            "\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
  }

  @Test
  public void ignoreSomeAnnotationsInEntityTypes() throws Exception {
    // We have to ignore @odata.navigation, @odata.association and @odata.type annotations on server side
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString@odata.type\":\"test\","
            + "\"Navigation@odata.navigationLink\":\"test\","
            + "\"Association@odata.associationLink\":\"test\","
            + "\"PropertyString\":\"First Resource - positive values\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
  }

  @Test
  public void ignoreSomeAnnotationsInComplexTypes() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"Navigation@odata.navigationLink\": 12," +
        "\"Association@odata.associationLink\": 12,\"PropertyString@odata.type\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"Navigation@odata.navigationLink\": 12," +
        "\"Association@odata.associationLink\": 12,\"PropertyString@odata.type\": 12,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
  }

  @Test
  public void odataControlInformationIsIgnoredForRequests() throws Exception {
    String entityString =
        "{"
            + "\"@odata.context\":\"http://localhost:8080\","
            + "\"@odata.metadataEtag\":\"metadataEtag\","
            + "\"@odata.id\":\"value\","
            + "\"@odata.editLink\":\"value\","
            + "\"@odata.readLink\":\"value\","
            + "\"@odata.etag\":\"value\","
            + "\"@odata.mediaEtag\":\"value\","
            + "\"@odata.mediaReadLink\":\"value\","
            + "\"@odata.mediaEditLink\":\"value\","
            + "\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
  }

  @Test
  public void etAllPrimBindingOperation() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":\"ESTwoPrim(2)\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[\"ESTwoPrim(2)\",\"ESTwoPrim(3)\"]"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    assertNotNull(entity);

    Link bindingToOne = entity.getNavigationBinding("NavPropertyETTwoPrimOne");
    assertNotNull(bindingToOne);
    assertEquals("NavPropertyETTwoPrimOne", bindingToOne.getTitle());
    assertEquals("ESTwoPrim(2)", bindingToOne.getBindingLink());
    assertEquals(Constants.ENTITY_BINDING_LINK_TYPE, bindingToOne.getType());
    assertTrue(bindingToOne.getBindingLinks().isEmpty());
    assertNull(bindingToOne.getHref());
    assertNull(bindingToOne.getRel());

    Link bindingToMany = entity.getNavigationBinding("NavPropertyETTwoPrimMany");
    assertNotNull(bindingToMany);
    assertEquals("NavPropertyETTwoPrimMany", bindingToMany.getTitle());
    assertNotNull(bindingToMany.getBindingLinks());
    assertEquals(2, bindingToMany.getBindingLinks().size());
    assertEquals(Constants.ENTITY_COLLECTION_BINDING_LINK_TYPE, bindingToMany.getType());
    assertNull(bindingToMany.getBindingLink());
    assertNull(bindingToMany.getHref());
    assertNull(bindingToMany.getRel());
  }

  @Test
  public void etAllPrimBindingOperationEmptyArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[]"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")))
            .getEntity();
    Link bindingToMany = entity.getNavigationBinding("NavPropertyETTwoPrimMany");
    assertNotNull(bindingToMany);
    assertTrue(bindingToMany.getBindingLinks().isEmpty());
  }

  @Test
  public void eTMixEnumDefCollCompTest() throws Exception {
    InputStream stream = getFileAsStream("EntityETMixEnumDefCollComp.json");
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, edm
            .getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")))
            .getEntity();

    assertEquals(6, entity.getProperties().size());

    Property enumProperty = entity.getProperty("PropertyEnumString");
    assertNotNull(enumProperty);
    assertEquals((short) 2, enumProperty.getValue());

    Property defProperty = entity.getProperty("PropertyDefString");
    assertNotNull(defProperty);
    assertEquals("string", defProperty.getValue());

    Property complexProperty = entity.getProperty("PropertyCompMixedEnumDef");
    List<Property> value = complexProperty.asComplex().getValue();
    assertEquals((short) 2, value.get(0).getValue());
    stream.close();
  }

  @Test
  public void eTCollAllPrimWithNullValue() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyBoolean\":[true,null,false],"
        + "\"CollPropertyByte\":[50,200,249],"
        + "\"CollPropertySByte\":[-120,120,126],"
        + "\"CollPropertyInt16\":[1000,2000,30112],"
        + "\"CollPropertyInt32\":[23232323,11223355,10000001],"
        + "\"CollPropertyInt64\":[929292929292,333333333333,444444444444],"
        + "\"CollPropertySingle\":[1790.0,26600.0,3210.0],"
        + "\"CollPropertyDouble\":[-17900.0,-2.78E7,3210.0],"
        + "\"CollPropertyDecimal\":[12,-2,1234],"
        + "\"CollPropertyBinary\":[\"q83v\",\"ASNF\",\"VGeJ\"],"
        + "\"CollPropertyDate\":[\"1958-12-03\",\"1999-08-05\",\"2013-06-25\"],"
        + "\"CollPropertyDateTimeOffset\":[\"2015-08-12T03:08:34Z\",\"1970-03-28T12:11:10Z\","
        + "\"1948-02-17T09:09:09Z\"],"
        + "\"CollPropertyDuration\":[\"PT13S\",\"PT5H28M0S\",\"PT1H0S\"],"
        + "\"CollPropertyGuid\":[\"ffffff67-89ab-cdef-0123-456789aaaaaa\",\"eeeeee67-89ab-cdef-0123-456789bbbbbb\","
        + "\"cccccc67-89ab-cdef-0123-456789cccccc\"],"
        + "\"CollPropertyTimeOfDay\":[\"04:14:13\",\"23:59:59\",\"01:12:33\"]"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity e = deserializer.entity(stream, edm.getEntityType(
        new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim"))).getEntity();

    assertTrue((Boolean) e.getProperty("CollPropertyBoolean").asCollection().get(0));
    assertNull(e.getProperty("CollPropertyBoolean").asCollection().get(1));
    assertFalse((Boolean) e.getProperty("CollPropertyBoolean").asCollection().get(2));
  }

  @Test
  public void validJsonValueForComplexTypeNull() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":null"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity = deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias",
        "ETMixPrimCollComp"))).getEntity();

    assertNull(entity.getProperty("PropertyComp").getValue());
  }

  @Test
  public void validJsonValueForComplexCollectionNullValue() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyComp\":["
        + "null,"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);

    Entity entity = deserializer.entity(stream, edm.getEntityType(
        new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp"))).getEntity();
    List<?> collPropertyComp = entity.getProperty("CollPropertyComp").asCollection();
    assertNull(collPropertyComp.get(0));
    List<Property> complexPropertyProperties = ((ComplexValue) collPropertyComp.get(1)).getValue();
    assertEquals(Short.valueOf((short) 789), complexPropertyProperties.get(0).getValue());
    assertEquals("TEST 3", complexPropertyProperties.get(1).getValue());
  }

  @Test
  public void validJsonValueForPrimPropertyInComplexTypeNull() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":{\"PropertyString\":\"TEST A\",\"PropertyInt16\":null}"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity = deserializer.entity(stream, edm.getEntityType(
        new FullQualifiedName("Namespace1_Alias", "ETCompAllPrim"))).getEntity();

    assertEquals("TEST A", entity.getProperty("PropertyComp").asComplex().getValue().get(0).getValue());
    assertNull(entity.getProperty("PropertyComp").asComplex().getValue().get(1).getValue());
  }

  @Test
  public void eTMixEnumDefCollCompNavValidComplexEnumValueNull() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"String2\","
        + "\"PropertyCompMixedEnumDef\" : {"
        + "\"PropertyEnumString\" : null"
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity e = deserializer.entity(stream, edm.getEntityType(
        new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp"))).getEntity();

    assertEquals((short) 2, e.getProperty("PropertyEnumString").getValue());
    Property propertyCompMixedEnumDef = e.getProperty("PropertyCompMixedEnumDef");
    assertNull(propertyCompMixedEnumDef.asComplex().getValue().get(0).getValue());
  }

  @Test
  public void eTMixEnumDefCollCompMultipleValuesForEnum() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"String1,String2\""
        + "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity e = deserializer.entity(stream, edm.getEntityType(
        new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp"))).getEntity();

    assertEquals((short) 3, e.getProperty("PropertyEnumString").getValue());
  }

  @Test
  public void mappingTest() throws Exception {
    OData.newInstance().createServiceMetadata(new EdmTechProvider(), Collections.<EdmxReference> emptyList())
        .getEdm();

    EdmEntityType entityType = mock(EdmEntityType.class);
    when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("napespace", "name"));
    List<String> propertyNames = new ArrayList<String>();
    propertyNames.add("PropertyDate");
    propertyNames.add("PropertyDateTimeOffset");
    when(entityType.getPropertyNames()).thenReturn(propertyNames);
    CsdlMapping mapping = new CsdlMapping().setMappedJavaClass(Date.class);

    EdmProperty propertyDate = mock(EdmProperty.class);
    when(propertyDate.getName()).thenReturn("PropertyDate");
    when(propertyDate.getMapping()).thenReturn(mapping);
    when(propertyDate.getType()).thenReturn(new EdmDate());
    when(entityType.getProperty("PropertyDate")).thenReturn(propertyDate);

    EdmProperty propertyDateTimeOffset = mock(EdmProperty.class);
    when(propertyDateTimeOffset.getName()).thenReturn("PropertyDateTimeOffset");
    when(propertyDateTimeOffset.getMapping()).thenReturn(mapping);
    when(propertyDateTimeOffset.getType()).thenReturn(new EdmDateTimeOffset());
    when(entityType.getProperty("PropertyDateTimeOffset")).thenReturn(propertyDateTimeOffset);

    String entityString =
        "{\"PropertyDate\":\"2012-12-03\","
            + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    Entity entity =
        deserializer.entity(stream, entityType).getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    assertNotNull(entity.getProperty("PropertyDate").getValue());
    assertEquals(Date.class, entity.getProperty("PropertyDate").getValue().getClass());
    assertNotNull(entity.getProperty("PropertyDateTimeOffset").getValue());
    assertEquals(Date.class, entity.getProperty("PropertyDateTimeOffset").getValue().getClass());
  }

  // ---------------------------------- Negative Tests -----------------------------------------------------------

  @Test(expected = DeserializerException.class)
  public void emptyInput() throws Exception {
    OData.newInstance().createDeserializer(CONTENT_TYPE_JSON).entity(
        new ByteArrayInputStream(new byte[] {}),
        edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
  }

  @Test(expected = DeserializerException.class)
  public void etAllPrimWithInvalidNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":null," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doublePrimitiveProperty() throws Exception {
    final String entityString = "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\"," +
        "\"PropertyInt16\":32766,\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETTwoPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doubleComplexProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST B\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doubleComplexPropertyCollection() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}],"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]"
        + "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doublePrimitivePropertyCollection() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void customAnnotationInEntityLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString@custom.annotation\": 12,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void customAnnotationInComplexValueLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111," +
        "\"CollPropertyString@custom.annotation\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void customAnnotationInComplexCollectionValueLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"CollPropertyString@custom.annotation\": 12,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInEntity() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"unknown\": 12,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKNOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInComplexProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"unknown\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKNOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInComplexCollectionProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"unknown\": 12,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKNOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test
  public void propertyInt16JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt16\":\"32767\"}");
    checkPropertyJsonType("{\"PropertyInt16\":true}");
  }

  @Test
  public void propertyInt32JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt32\":\"2147483647\"}");
    checkPropertyJsonType("{\"PropertyInt32\":true}");
  }

  @Test
  public void propertyInt64JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt64\":\"9223372036854775807\"}");
    checkPropertyJsonType("{\"PropertyInt64\":true}");
  }

  @Test
  public void propertyStringJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyString\":32767}");
    checkPropertyJsonType("{\"PropertyString\":true}");
  }

  @Test
  public void propertyBooleanJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyBoolean\":\"true\"}");
    checkPropertyJsonType("{\"PropertyBoolean\":123}");
  }

  @Test
  public void propertyByteJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyByte\":\"255\"}");
    checkPropertyJsonType("{\"PropertyByte\":true}");
  }

  @Test
  public void propertySByteJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertySByte\":\"127\"}");
    checkPropertyJsonType("{\"PropertySByte\":true}");
  }

  @Test
  public void propertySingleJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertySingle\":\"1.79E20\"}");
    checkPropertyJsonType("{\"PropertySingle\":true}");
  }

  @Test
  public void propertyDoubleJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDouble\":\"-1.79E19\"}");
    checkPropertyJsonType("{\"PropertyDouble\":true}");
  }

  @Test
  public void propertyDecimalJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDecimal\":\"34\"}");
    checkPropertyJsonType("{\"PropertyDecimal\":true}");
  }

  @Test
  public void propertyBinaryJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyBinary\":32767}");
    checkPropertyJsonType("{\"PropertyBinary\":true}");
  }

  @Test
  public void propertyDateJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDate\":32767}");
    checkPropertyJsonType("{\"PropertyDate\":true}");
  }

  @Test
  public void propertyDateTimeOffsetJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDateTimeOffset\":32767}");
    checkPropertyJsonType("{\"PropertyDateTimeOffset\":true}");
  }

  @Test
  public void propertyDurationJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDuration\":32767}");
    checkPropertyJsonType("{\"PropertyDuration\":true}");
  }

  @Test
  public void propertyGuidTimeOffsetJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyGuid\":32767}");
    checkPropertyJsonType("{\"PropertyGuid\":true}");
  }

  @Test
  public void propertyTimeOfDayJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyTimeOfDay\":32767}");
    checkPropertyJsonType("{\"PropertyTimeOfDay\":true}");
  }

  @Test(expected = DeserializerException.class)
  public void bindOperationWrongJsonTypeForToOne() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":[\"ESTwoPrim(2)\"]"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindOperationWrongJsonTypeForToMany() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":\"ESTwoPrim(2)\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindOperationWrongJsonTypeForToManyNumberInArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[123,456]"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindOperationWrongAnnotationFormat() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"@odata.bind\":\"ESTwoPrim(2)\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NAVIGATION_PROPERTY_NOT_FOUND, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindingOperationNullOnToOne() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":null"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindingOperationNullOnToMany() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":null"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void bindingOperationNullInArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[null]"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonSyntax() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonValueForPrimTypeArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":[]}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonValueForPrimTypeObject() throws Exception {
    String entityString =
        "{\"PropertyInt16\":{}}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonValueForPrimCollectionTypeObject() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyString\":"
        + "{\"Employee1@company.example\":1234}"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonValueForComplexTypeTypeString() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":\"InvalidString\""
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidNullValueForComplexTypeNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":null"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETTwoKeyNav")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidNullValueForPrimBeforeComplexTypeNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\": null, \"PropertyString\": \"321\", "
        + "\"PropertyComp\":{\"PropertyInt16\": null, "
        + "\"PropertyComp\": {\"PropertyString\":\"StringValue\"}}"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETTwoKeyNav")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidNullValueForComplexTypePropertyNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\": 123, "
        + "\"PropertyCompTwoPrim\":{\"PropertyInt16\": null, \"PropertyString\":\"StringValue\"}"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETKeyNav")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidNullValueForPrimCollectionNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyString\":["
        + "null,"
        + "\"StringValue_1\",\"TEST 3\"]}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidNullValueForPrimIntCollectionNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyInt16\":[123,\"null\",4711]"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void provokedPrimitiveTypeException() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767000000000000000000000000000000000000"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTMixEnumDefCollCompInvalidEnumValueNull() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : null,"
        + "\"PropertyCompMixedEnumDef\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(
          new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTMixEnumDefCollCompInvalidEnumValueArray() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : [],"
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTMixEnumDefCollCompInvalidEnumValueObject() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : {},"
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTMixEnumDefCollCompInvalidEnumValue() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"invalid\","
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTMixEnumDefCollCompInvalidEnumValueByPrimitiveTypeException() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"18\","
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixEnumDefCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test
  public void ieee754Compatible() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON_IEEE754Compatible);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"9223372036854775807\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"34\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final Entity entity = deserializer
        .entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();

    assertEquals(9223372036854775807L, entity.getProperty("PropertyInt64").asPrimitive());
    assertEquals(BigDecimal.valueOf(34), entity.getProperty("PropertyDecimal").asPrimitive());
  }

  @Test
  public void ieee754CompatibleNull() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON_IEEE754Compatible);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":null," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":null," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    final Entity entity = deserializer
        .entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();

    assertTrue(entity.getProperty("PropertyInt64").isNull());
    assertTrue(entity.getProperty("PropertyDecimal").isNull());
  }

  @Test(expected = DeserializerException.class)
  public void ieee754CompatibleEmptyString() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON_IEEE754Compatible);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\" \"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();
  }

  @Test(expected = DeserializerException.class)
  public void ieee754CompatibleNullAsString() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON_IEEE754Compatible);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"null\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();
  }

  @Test(expected = DeserializerException.class)
  public void ieee754CompatibleAsNumber() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON_IEEE754Compatible);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":123," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();
  }

  @Test(expected = DeserializerException.class)
  public void ieee754NotCompatibleAsString() throws Exception {
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"123\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";

    final InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"))).getEntity();
  }

  private void checkPropertyJsonType(final String entityString) throws DeserializerException {
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(CONTENT_TYPE_JSON);
    try {
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
      fail("Expected an exception but was not thrown: " + this.getClass().getName());
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
    }
  }
}
