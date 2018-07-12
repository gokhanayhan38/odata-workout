Tutorial: http://olingo.apache.org/doc/odata4/

Tutorial Part 1: Create a read service

Source code : \samples\tutorials\p1_read

According to the OData specification, an OData service has to declare its structure in the so-called Metadata Document. This document defines the contract, such that the user of the service knows which requests can be executed, the structure of the result and how the service can be navigated.

The Metadata Document can be invoked via the following URI:

	<servicerooturl>/$metadata
Örnek:

	http://services.odata.org/v4/(S(40hvswlavy2blrqn4bnxohgt))/TripPinServiceRW/$metadata

Furthermore, OData specifies the usage of the so-called Service Document Here, the user can see which Entity Collections are offered by an OData service.

The service document can be invoked via the following URI:

	<servicerooturl>/

Örnek:

	http://services.odata.org/v4/(S(40hvswlavy2blrqn4bnxohgt))/TripPinServiceRW/

The information that is given by these 2 URIs, has to be implemented in the service code. Olingo provides an API for it and we will use it in the implementation of our CsdlEdmProvider.

Create class:

edm is the abbreviation for Entity Data Model. Accordingly, we understand that the CsdlEdmProvider is supposed to provide static descriptive information.

The Entity Model of the service can be defined in the EDM Provider. The EDM model basically defines the available EntityTypes and the relation between the entities. An EntityType consists of primitive, complex or navigation properties. The model can be invoked with the Metadata Document request.

For example: The entries that are displayed in the Service Document are provided by the method getEntityContainerInfo() The structure of EntityTypes is declared in the method getEntityType()

In our simple example, we implement the minimum amount of methods, required to run a meaningful OData service. These are:

	getEntityType() Here we declare the EntityType “Product” and a few of its properties
	getEntitySet() Here we state that the list of products can be called via the EntitySet “Products”
	getEntityContainer() Here we provide a Container element that is necessary to host the EntitySet.
	getSchemas() The Schema is the root element to carry the elements.
	getEntityContainerInfo() Information about the EntityContainer to be displayed in the Service Document

```java
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
	package myservice.mynamespace.service;

	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collections;
	import java.util.List;

	import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
	import org.apache.olingo.commons.api.edm.FullQualifiedName;
	import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
	import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
	import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
	import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
	import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
	import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
	import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
	import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

	/**
	 * this class is supposed to declare the metadata of the OData service
	 * it is invoked by the Olingo framework e.g. when the metadata document of the service is invoked
	 * e.g. http://localhost:8080/ExampleService1/ExampleService1.svc/$metadata
	 */
	public class DemoEdmProvider extends CsdlAbstractEdmProvider {

	  // Service Namespace
	  public static final String NAMESPACE = "OData.Demo";

	  // EDM Container
	  public static final String CONTAINER_NAME = "Container";
	  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	  // Entity Types Names
	  public static final String ET_PRODUCT_NAME = "Product";
	  public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

	  // Entity Set Names
	  public static final String ES_PRODUCTS_NAME = "Products";


	  @Override
	  public List<CsdlSchema> getSchemas() {

	    // create Schema
	    CsdlSchema schema = new CsdlSchema();
	    schema.setNamespace(NAMESPACE);

	    // add EntityTypes
	    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
	    entityTypes.add(getEntityType(ET_PRODUCT_FQN));
	    schema.setEntityTypes(entityTypes);

	    // add EntityContainer
	    schema.setEntityContainer(getEntityContainer());

	    // finally
	    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
	    schemas.add(schema);

	    return schemas;
	  }


	  @Override
	  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

	    // this method is called for one of the EntityTypes that are configured in the Schema
	    if(entityTypeName.equals(ET_PRODUCT_FQN)){

	      //create EntityType properties
	      CsdlProperty id = new CsdlProperty().setName("ID").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
	      CsdlProperty name = new CsdlProperty().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
	      CsdlProperty  description = new CsdlProperty().setName("Description").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

	      // create CsdlPropertyRef for Key element
	      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
	      propertyRef.setName("ID");

	      // configure EntityType
	      CsdlEntityType entityType = new CsdlEntityType();
	      entityType.setName(ET_PRODUCT_NAME);
	      entityType.setProperties(Arrays.asList(id, name , description));
	      entityType.setKey(Collections.singletonList(propertyRef));

	      return entityType;
	    }

	    return null;
	  }

	  @Override
	  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

	    if(entityContainer.equals(CONTAINER)){
	      if(entitySetName.equals(ES_PRODUCTS_NAME)){
	        CsdlEntitySet entitySet = new CsdlEntitySet();
	        entitySet.setName(ES_PRODUCTS_NAME);
	        entitySet.setType(ET_PRODUCT_FQN);

	        return entitySet;
	      }
	    }

	    return null;
	  }

	  @Override
	  public CsdlEntityContainer getEntityContainer() {

	    // create EntitySets
	    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
	    entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));

	    // create EntityContainer
	    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
	    entityContainer.setName(CONTAINER_NAME);
	    entityContainer.setEntitySets(entitySets);

	    return entityContainer;
	  }

	  @Override
	  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

	    // This method is invoked when displaying the service document at e.g. http://localhost:8080/DemoService/DemoService.svc
	    if(entityContainerName == null || entityContainerName.equals(CONTAINER)){
	      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
	      entityContainerInfo.setContainerName(CONTAINER);
	      return entityContainerInfo;
	    }

	    return null;
	  }
	}
```

