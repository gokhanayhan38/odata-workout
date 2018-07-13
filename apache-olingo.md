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


**Implement the required methods**

For example: The entries that are displayed in the Service Document are provided by the method getEntityContainerInfo() The structure of EntityTypes is declared in the method getEntityType()

In our simple example, we implement the minimum amount of methods, required to run a meaningful OData service. These are:

	getEntityType() Here we declare the EntityType “Product” and a few of its properties
	getEntitySet() Here we state that the list of products can be called via the EntitySet “Products”
	getEntityContainer() Here we provide a Container element that is necessary to host the EntitySet.
	getSchemas() The Schema is the root element to carry the elements.
	getEntityContainerInfo() Information about the EntityContainer to be displayed in the Service Document


Create CsdlEntityType then CsdlEntitySet then CsdlEntityContainer, then CsdlSchema create all of them ordered.

```java
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



/*
In our example service, we want to provide a list of products to users who call the OData service. The user of our service, for example an app-developer, may ask: 
What does such a "product" entry look like? 
How is it structured? 
Which information about a product is provided? 
For example, the name of it and which data types can be expected from these properties? Such information is provided by a CsdlEdmProvider (and for convenience we extend the CsdlAbstractEdmProvider).

In our example service, for modelling the CsdlEntityType, we have to provide the following metadata:

The name of the EntityType: “Product” The properties: name and type and additional info, e.g. “ID” of type “edm.int32” Which of the properties is the “key” property: a reference to the “ID” property.
*/

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

/*
The procedure for declaring the Entity Sets is similar. An EntitySet is a crucial resource, when an OData service is used to request data. In our example, we will invoke the following URL, which we expect to provide us a list of products:

http://localhost:8080/DemoService/DemoServlet.svc/Products

When declaring an EntitySet, we need to define the type of entries which are contained in the list, such as an CsdlEntityType. In our example, we set our previously created CsdlEntityType, which is referred by a FullQualifiedName.
*/
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

/*
In order to provide data, our OData service needs an EntityContainer that carries the EntitySets. In our example, we have only one EntitySet, so we create one EntityContainer and set our EntitySet.

How multi entitySets works?
*/
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

/*
Up to this point, we have declared the type of our data (CsdlEntityType) and our list (CsdlEntitySet), and we have put it into a container (CsdlEntityContainer). Now we are required to put all these elements into a CsdlSchema. While the model of an OData service can have several schemas, in most cases there will probably be only one schema. So, in our example, we create a list of schemas, where we add one new CsdlSchema object. The schema is configured with a Namespace, which serves to uniquely identify all elements. Then our elements are added to the Schema.
*/
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
	}
```
**Summary:** We have created a class that declares the metadata of our OData service. We have declared the main elements of an OData service: EntityType, EntitySet, EntityContainer and Schema (with the corresponding Olingo classes CsdlEntityType, CsdlEntitySet, CsdlEntityContainer and CsdlSchema).

At runtime of an OData service, such metadata can be viewed by invoking the Metadata Document.

In our example invokation of the URL: 
	
	http://localhost:8080/DemoService/DemoService.svc/$metadata

Give us the result below:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<edmx:Edmx Version="4.0" xmlns:edmx="http://docs.oasis-open.org/odata/ns/edmx">
  <edmx:DataServices>
    <Schema xmlns="http://docs.oasis-open.org/odata/ns/edm" Namespace="OData.Demo">
      <EntityType Name="Product">
        <Key>
          <PropertyRef Name="ID"/>
        </Key>
        <Property Name="ID" Type="Edm.Int32"/>
        <Property Name="Name" Type="Edm.String"/>
        <Property Name="Description" Type="Edm.String"/>
      </EntityType>
      <EntityContainer Name="Container">
        <EntitySet Name="Products" EntityType="OData.Demo.Product"/>
      </EntityContainer>
    </Schema>
  </edmx:DataServices>
</edmx:Edmx>
```
The Service Document can be invoked to view the Entity Sets, like in our example at the URL: 

	http://localhost:8080/DemoService/DemoService.svc/

Which give us the Service Document as result:

```json
{
  "@odata.context" : "$metadata",
  "value" : [
  {
    "name" : "Products",
    "url" : "Products"
  } ]
}
```

Note: After implementing the EdmProvider, we can, as an intermediate step, build/deploy the service and invoke the 2 static pages: Service Document and Metadata Document. If desired, you can proceed with implementing the required steps for web application as described in 4.3. and run the application as described in chapter 5.

**Provide the data**

After implementing the EdmProvider, the next step is the main task of an OData service: provide data. 

In our example, we imagine that our OData service is invoked by a user who wants to see which products are offered by a web shop. He invokes a URL and gets a list of products. Providing the list of products is the task that we are going to implement in this chapter.

The work that we have to do in this chapter can be divided into 4 tasks:

- Check the URI We need to identify the requested resource and have to consider Query Options (if available)
- Provide the data Based on the URI info, we have to obtain the data from our data store (can be e.g. a database)
- Serialize the data The data has to be transformed into the required format
- Configure the response Since we are implementing a “processor”, the last step is to provide the response object

These 4 steps will be considered in the implementation of the readEntityCollection() method.

**Background**

In our example, we have stated in our Metadata Document that we will provide a list of “products” whenever the EntitySet with name “Products” is invoked. This means that the user of our OData service will append the EntitySet name to the root URL of the service and then invoke the full URL. 

This is 

	http://localhost:8080/DemoService/DemoServlet1.svc/Products 

So, whenever this URL is fired, Olingo will invoke the EntityCollectionProcessor implementation of our OData service. Then our EntityCollectionProcessor implementation is expected to provide a list of products.

**As we have already mentioned, the Metadata Document is the contract for providing data. This means that when it comes to provide the actual data, we have to do it according to the specified metadata. For example, the property names have to match, also the types of the properties, and, if specified, the length of the strings, etc**

**Implement the required methods**

After creation of the Java class, we can see that there are 2 methods to be implemented:

**init()** This method is invoked by the Olingo library, allowing us to store the context object
**readEntityCollection()** Here we have to fetch the required data and pass it back to the Olingo library

```java
package myservice.mynamespace.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

/**
 * This class is invoked by the Olingo framework when the the OData service is invoked order to display a list/collection of data (entities).
 * This is the case if an EntitySet is requested by the user.
 * Such an example URL would be:
 * http://localhost:8080/ExampleService1/ExampleService1.svc/Products
 */
public class DemoEntityCollectionProcessor implements EntityCollectionProcessor {

  //Don’t forget to declare this the member variables
  private OData odata;
  private ServiceMetadata serviceMetadata;

/*
This method (init()) is common to all processor interfaces. The Olingo framework initializes the processor with an instance of the OData object. According to the Javadoc, this object is the “Root object for serving factory tasks…” We will need it later, so we store it as member variable.
*/
  // our processor is initialized with the OData context object
  public void init(OData odata, ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

/*
http://olingo.apache.org/doc/odata4/tutorials/read/tutorial_read.html#423-implement-the-required-methods

readEntityCollection(...)-method is invoked, when the OData service is called with an HTTP GET operation for an entity collection.

The readEntityCollection(...) method is used to “read” the data in the backend (this can be e.g. a database) and to deliver it to the user who calls the OData service.


*/
  // the only method that is declared in the EntityCollectionProcessor interface
  // this method is called, when the user fires a request to an EntitySet
  // in our example, the URL would be:
  // http://localhost:8080/ExampleService1/ExampleServlet1.svc/Products

  // The “request” parameter contains raw HTTP information. It is typically used for creation scenario, where a request body is sent along with the request.

  // With the second parameter, the “response” object is passed to our method in order to carry the response data. So here we have to set the response body, along with status code and content-type header.

  // The third parameter, the “uriInfo”, contains information about the relevant part of the URL. This means, the segments starting after the service name.

  public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, SerializerException {

    // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
    List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
    UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
    EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

    // 2nd: fetch the data from backend for this requested EntitySetName // it has to be delivered as EntitySet object
    EntityCollection entitySet = getData(edmEntitySet);

    // 3rd: create a serializer based on the requested format (json)
    ODataSerializer serializer = odata.createSerializer(responseFormat);
    
    // 4th: Now serialize the content: transform from the EntitySet object to InputStream
    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

    final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
    EntityCollectionSerializerOptions opts =
        EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
    SerializerResult serializedContent = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);

    // Finally: configure the response object: set the body, headers and status code
    response.setContent(serializedContent.getContent());
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
  }

/*
We have not elaborated on fetching the actual data. In our tutorial, to keep the code as simple as possible, we use a little helper method that delivers some hardcoded entries. Since we are supposed to deliver the data inside an EntityCollection instance, we create the instance, ask it for the (initially empty) list of entities and add some new entities to it. We create the entities and their properties according to what we declared in our DemoEdmProvider class. So we have to take care to provide the correct names to the new property objects. If a client requests the response in ATOM format, each entity have to provide it`s own entity id. The method createId allows us to create an id in a convenient way.
*/
  /**
   * Helper method for providing some sample data
   * @param edmEntitySet for which the data is requested
   * @return data of requested entity set
   */
  private EntityCollection getData(EdmEntitySet edmEntitySet){

    EntityCollection productsCollection = new EntityCollection();
    // check for which EdmEntitySet the data is requested
    if(DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
      List<Entity> productList = productsCollection.getEntities();

      // add some sample product entities
      final Entity e1 = new Entity()
          .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1))
          .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Basic 15"))
          .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
              "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB"));
      e1.setId(createId("Products", 1));
      productList.add(e1);

      final Entity e2 = new Entity()
          .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2))
          .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "1UMTS PDA"))
          .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
              "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network"));
      e2.setId(createId("Products", 1));
      productList.add(e2);

      final Entity e3 = new Entity()
          .addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 3))
          .addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Ergo Screen"))
          .addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
              "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"));
      e3.setId(createId("Products", 1));
      productList.add(e3);
    }

    return productsCollection;
  }
  
  private URI createId(String entitySetName, Object id) {
    try {
      return new URI(entitySetName + "(" + String.valueOf(id) + ")");
    } catch (URISyntaxException e) {
      throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
    }
  }
}
```


**Example:** If the user calls the following URL: 

	http://localhost:8080/DemoService/DemoService.svc/Products 

The readEntityCollection(...) method is invoked and the uriInfo object contains one segment: “Products”

If the user calls the following URL: 

	http://localhost:8080/DemoService/DemoService.svc/Products?$filter=ID eq 1 

Then the readEntityCollection(...) method is invoked and the uriInfo contains the information about the entity set and furthermore the system query option $filter and its value.

The last parameter, the “responseFormat”, contains information about the content type that is requested by the user. This means that the user has the choice to receive the data either in XML or in JSON.

**Example:**
If the user calls the following URL: 

	http://localhost:8080/DemoService/DemoService.svc/Products?$format=application/json;odata.metadata=minimal

then the content type is: application/json;odata.metadata=minimal which means that the payload is formatted in JSON (like it is shown in the introduction section of this tutorial)

Note: The content type can as well be specified via the following request header Accept: application/json;odata.metadata=minimal In this case as well, our readEntityCollection() method will be called with the parameter responseFormat containing the content type > information.

Note: If the user doesn’t specify any content type, then the default is JSON.

Why is this parameter needed? Because the readEntityCollection(...) method is supposed to deliver the data in the format that is requested by the user. We will use this parameter when creating a serializer based on it.

The steps for implementating the method readEntityCollection(...) are:

- Which data is requested? Usually, an OData service provides different EntitySets, so first it is required to identify which EntitySet has been requested. This information can be retrieved from the uriInfo object.

- Fetch the data As a developer of the OData service, you have to know how and where the data is stored. In many cases, this would be a database. At this point, you would connect to your database and fetch the requested data with an appropriate SQL statement. The data that is fetched from the data storage has to be put into an EntityCollection object. The package org.apache.olingo.commons.api.data provides interfaces that describe the actual data, not the metadata.

- Transform the data Olingo expects us to provide the data as low-level InputStream object. However, Olingo supports us in doing so, by providing us with a proper "serializer". So what we have to do is create the serializer based on the requested content type, configure it and call it.

- Configure the response The response object has been passed to us in the method signature. We use it to set the serialized data (the InputStream object). Furthermore, we have to set the HTTP status code, which means that we have the opportunity to do proper error handling. And finally we have to set the content type.


