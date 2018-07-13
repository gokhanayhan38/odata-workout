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
package org.apache.olingo.server.core.serializer.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.EdmComplexTypeImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLBuilderTest {

  @Test
  public void buildServiceDocument() {
    final ContextURL contextURL = ContextURL.with()
        .serviceRoot(URI.create("http://host/service/")).build();
    assertEquals("http://host/service/$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildRelative() {
    final ContextURL contextURL = ContextURL.with().build();
    assertEquals("$metadata", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .build();
    assertEquals("http://host/service/$metadata#Customers", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildDerivedEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildEntitySetWithEntitySuffix() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals("http://host/service/$metadata#Customers/$entity", ContextURLBuilder.create(contextURL)
        .toASCIIString());
  }

  @Test
  public void buildEntity() {
    EdmEntityType entityType = mock(EdmEntityType.class);
    when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("namespace.entityType"));
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(entityType)
        .build();
    assertEquals("http://host/service/$metadata#namespace.entityType", ContextURLBuilder.create(contextURL)
        .toASCIIString());

    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(entityType)
        .asCollection()
        .build();
    assertEquals("http://host/service/$metadata#Collection(namespace.entityType)", ContextURLBuilder.create(contextURL)
        .toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildDerivedEntitySetWithoutEntitySet() {
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    ContextURLBuilder.create(ContextURL.with().derived(derivedType).build());
  }

  @Test
  public void buildDerivedEntity() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(new FullQualifiedName("Model", "VipCustomer"));
    final ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .derived(derivedType)
        .suffix(Suffix.ENTITY)
        .build();
    assertEquals("http://host/service/$metadata#Customers/Model.VipCustomer/$entity",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildProperty() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .keyPath("1")
        .navOrPropertyPath("Name")
        .build();
    assertEquals("http://host/service/$metadata#Customers(1)/Name",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .entitySet(entitySet)
        .keyPath("one=1,two='two'")
        .navOrPropertyPath("ComplexName")
        .selectList("Part1")
        .build();
    assertEquals("http://host/service/$metadata#Customers(one=1,two='two')/ComplexName(Part1)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildPrimitiveType() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(EdmString.getInstance())
        .build();
    assertEquals("http://host/service/$metadata#Edm.String",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(EdmString.getInstance()).asCollection()
        .build();
    assertEquals("http://host/service/$metadata#Collection(Edm.String)",
        ContextURLBuilder.create(contextURL).toString());
  }

  @Test
  public void buildComplexType() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    FullQualifiedName baseName = new FullQualifiedName("namespace", "BaseTypeName");
    CsdlComplexType baseComplexType = new CsdlComplexType();
    List<CsdlProperty> baseProperties = new ArrayList<CsdlProperty>();
    baseProperties.add(new CsdlProperty().setName("prop1").setType(
        EdmPrimitiveTypeKind.String.getFullQualifiedName()));
    List<CsdlNavigationProperty> baseNavigationProperties = new ArrayList<CsdlNavigationProperty>();
    baseNavigationProperties.add(new CsdlNavigationProperty().setName("nav1"));
    baseComplexType.setName("BaseTypeName").setAbstract(false).setOpenType(false).setProperties(baseProperties)
        .setNavigationProperties(baseNavigationProperties);
    when(provider.getComplexType(baseName)).thenReturn(baseComplexType);

    EdmComplexType baseType = new EdmComplexTypeImpl(edm, baseName, baseComplexType);

    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURL contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(baseType)
        .build();
    assertEquals("http://host/service/$metadata#namespace.BaseTypeName",
        ContextURLBuilder.create(contextURL).toASCIIString());

    contextURL = ContextURL.with().serviceRoot(URI.create("http://host/service/"))
        .type(baseType)
        .asCollection()
        .build();
    assertEquals("http://host/service/$metadata#Collection(namespace.BaseTypeName)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildSuffixWithoutEntitySet() {
    ContextURLBuilder.create(ContextURL.with().suffix(Suffix.ENTITY).build());
  }

  @Test
  public void buildReference() {
    final ContextURL contextURL = ContextURL.with().suffix(Suffix.REFERENCE).build();
    assertEquals("$metadata#$ref", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void buildReferenceWithEntitySet() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Customers");
    ContextURLBuilder.create(ContextURL.with().entitySet(entitySet).suffix(Suffix.REFERENCE).build());
  }

  @Test
  public void buildWithCharactersToBeEscaped() {
    EdmEntitySet entitySet = Mockito.mock(EdmEntitySet.class);
    Mockito.when(entitySet.getName()).thenReturn("Entitäten");
    EdmEntityType derivedType = Mockito.mock(EdmEntityType.class);
    Mockito.when(derivedType.getFullQualifiedName()).thenReturn(
        new FullQualifiedName("Namensräumchen", "UnüblicherName"));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet).derived(derivedType).build();
    assertEquals("$metadata#Entit%C3%A4ten/Namensr%C3%A4umchen.Un%C3%BCblicherName",
        ContextURLBuilder.create(contextURL).toString());
  }
}
