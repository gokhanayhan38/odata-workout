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
package org.apache.olingo.server.core;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Collections;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.processor.ActionComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionComplexProcessor;
import org.apache.olingo.server.api.processor.ActionEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionEntityProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.ActionPrimitiveProcessor;
import org.apache.olingo.server.api.processor.ActionVoidProcessor;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.processor.ComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.ComplexProcessor;
import org.apache.olingo.server.api.processor.CountComplexCollectionProcessor;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.processor.CountPrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.ErrorProcessor;
import org.apache.olingo.server.api.processor.MediaEntityProcessor;
import org.apache.olingo.server.api.processor.MetadataProcessor;
import org.apache.olingo.server.api.processor.PrimitiveCollectionProcessor;
import org.apache.olingo.server.api.processor.PrimitiveProcessor;
import org.apache.olingo.server.api.processor.PrimitiveValueProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.processor.ReferenceCollectionProcessor;
import org.apache.olingo.server.api.processor.ReferenceProcessor;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.debug.ServerCoreDebugger;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class ODataHandlerTest {

  private static final String BASE_URI = "http://localhost/odata";

  @Test
  public void serviceDocumentNonDefault() throws Exception {
    final ServiceDocumentProcessor processor = mock(ServiceDocumentProcessor.class);
    final ODataResponse response = dispatch(HttpMethod.GET, "/", processor);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());

    verify(processor).readServiceDocument(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.POST, "/", processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, "/", processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, "/", processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, "/", processor);
  }

  @Test
  public void serviceDocumentDefault() throws Exception {
    final ODataResponse response = dispatch(HttpMethod.GET, "/", null);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    String ct = response.getHeader(HttpHeader.CONTENT_TYPE);
    assertThat(ct, containsString("application/json"));
    assertThat(ct, containsString("odata.metadata=minimal"));

    assertNotNull(response.getContent());
    String doc = IOUtils.toString(response.getContent());

    assertThat(doc, containsString("\"@odata.context\":\"$metadata\""));
    assertThat(doc, containsString("\"value\":"));
  }

  @Test
  public void serviceDocumentRedirect() throws Exception {
    final ODataResponse response = dispatch(HttpMethod.GET, "", null);
    assertEquals(HttpStatusCode.TEMPORARY_REDIRECT.getStatusCode(), response.getStatusCode());
    assertEquals(BASE_URI + "/", response.getHeader(HttpHeader.LOCATION));
  }

  @Test
  public void metadataNonDefault() throws Exception {
    final MetadataProcessor processor = mock(MetadataProcessor.class);
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", processor);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());

    verify(processor).readMetadata(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.POST, "$metadata", processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, "$metadata", processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, "$metadata", processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, "$metadata", processor);
  }

  @Test
  public void metadataDefault() throws Exception {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", null);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.APPLICATION_XML.toContentTypeString(), response.getHeader(HttpHeader.CONTENT_TYPE));

    assertNotNull(response.getContent());
    assertThat(IOUtils.toString(response.getContent()),
        containsString("<edmx:Edmx Version=\"4.0\""));
  }

  @Test
  public void maxVersionNone() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", null);
    assertEquals(ODataServiceVersion.V40.toString(), response.getHeader(HttpHeader.ODATA_VERSION));
  }

  @Test
  public void maxVersionSupported() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", null,
        HttpHeader.ODATA_MAX_VERSION, ODataServiceVersion.V40.toString(), null);
    assertEquals(ODataServiceVersion.V40.toString(), response.getHeader(HttpHeader.ODATA_VERSION));
  }

  @Test
  public void maxVersionNotSupported() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", null,
        HttpHeader.ODATA_MAX_VERSION, ODataServiceVersion.V30.toString(), null);

    assertEquals(ODataServiceVersion.V40.toString(), response.getHeader(HttpHeader.ODATA_VERSION));
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void contentNegotiationSupported() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", "$format=xml", null, null, null);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void contentNegotiationNotSupported() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", "$format=not/Supported", null, null, null);
    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void contentNegotiationNotSupported2() {
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", "$format=notSupported", null, null, null);
    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void unregisteredProcessor() {
    final ODataResponse response = dispatch(HttpMethod.GET, "ESAllPrim", null);
    assertEquals(HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void uriParserExceptionResultsInRightResponseNotFound() throws Exception {
    final ODataResponse response = dispatch(HttpMethod.GET, "NotFound", null);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void uriParserExceptionResultsInRightResponseBadRequest() throws Exception {
    final ODataResponse response = dispatch(HttpMethod.GET, "ESAllPrim('122')", null);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void applicationExceptionInProcessor() throws Exception {
    MetadataProcessor processor = mock(MetadataProcessor.class);
    doThrow(new ODataApplicationException("msg", 425, Locale.ENGLISH)).when(processor).readMetadata(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));
    final ODataResponse response = dispatch(HttpMethod.GET, "$metadata", processor);
    assertEquals(425, response.getStatusCode());
  }

  @Test
  public void uriParserExceptionResultsInRightResponseEdmCause() throws Exception {
    final OData odata = OData.newInstance();
    final ServiceMetadata serviceMetadata = odata.createServiceMetadata(
        new CsdlAbstractEdmProvider() {
          @Override
          public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
              throws ODataException {
            throw new ODataException("msg");
          }
        },
        Collections.<EdmxReference> emptyList());

    ODataRequest request = new ODataRequest();
    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("EdmException");

    final ODataResponse response =
        new ODataHandler(odata, serviceMetadata, new ServerCoreDebugger(odata)).process(request);
    assertNotNull(response);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void dispatchBatch() throws Exception {
    final String uri = "$batch";
    final BatchProcessor processor = mock(BatchProcessor.class);

    dispatch(HttpMethod.POST, uri, null, HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED.toContentTypeString(),
        processor);
    verify(processor).processBatch(any(BatchFacade.class), any(ODataRequest.class), any(ODataResponse.class));

    dispatchMethodNotAllowed(HttpMethod.GET, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, uri, processor);
  }

  @Test
  public void dispatchEntitySet() throws Exception {
    final String uri = "ESAllPrim";
    final EntityCollectionProcessor processor = mock(EntityCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, uri, processor);
  }

  @Test
  public void dispatchEntitySetCount() throws Exception {
    final String uri = "ESAllPrim/$count";
    final CountEntityCollectionProcessor processor = mock(CountEntityCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).countEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, uri, processor);
  }

  @Test
  public void dispatchCountWithNavigation() throws Exception {
    final CountEntityCollectionProcessor processor = mock(CountEntityCollectionProcessor.class);
    dispatch(HttpMethod.GET, "ESAllPrim(0)/NavPropertyETTwoPrimMany/$count", processor);

    verify(processor).countEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
  }

  @Test
  public void dispatchFunction() throws Exception {
    EntityProcessor entityProcessor = mock(EntityProcessor.class);
    dispatch(HttpMethod.GET, "FICRTETKeyNav()", entityProcessor);
    verify(entityProcessor).readEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    EntityCollectionProcessor entityCollectionProcessor = mock(EntityCollectionProcessor.class);
    dispatch(HttpMethod.GET, "FICRTCollESTwoKeyNavParam(ParameterInt16=123)", entityCollectionProcessor);
    verify(entityCollectionProcessor).readEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    final String entityCountUri = "FICRTCollESTwoKeyNavParam(ParameterInt16=123)/$count";
    final CountEntityCollectionProcessor entityCountProcessor = mock(CountEntityCollectionProcessor.class);
    dispatch(HttpMethod.GET, entityCountUri, entityCountProcessor);
    verify(entityCountProcessor).countEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
    dispatchMethodNotAllowed(HttpMethod.POST, entityCountUri, entityCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, entityCountUri, entityCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, entityCountUri, entityCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, entityCountUri, entityCountProcessor);

    PrimitiveProcessor primitiveProcessor = mock(PrimitiveProcessor.class);
    dispatch(HttpMethod.GET, "FICRTString()", primitiveProcessor);
    verify(primitiveProcessor).readPrimitive(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    final String valueUri = "FINRTInt16()/$value";
    final PrimitiveValueProcessor primitiveValueProcessor = mock(PrimitiveValueProcessor.class);
    dispatch(HttpMethod.GET, valueUri, primitiveValueProcessor);
    verify(primitiveValueProcessor).readPrimitiveValue(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));
    dispatchMethodNotAllowed(HttpMethod.POST, valueUri, primitiveValueProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, valueUri, primitiveValueProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, valueUri, primitiveValueProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, valueUri, primitiveValueProcessor);

    final String primitiveCollectionUri = "FICRTCollString()";
    PrimitiveCollectionProcessor primitiveCollectionProcessor = mock(PrimitiveCollectionProcessor.class);
    dispatch(HttpMethod.GET, primitiveCollectionUri, primitiveCollectionProcessor);
    verify(primitiveCollectionProcessor).readPrimitiveCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));
    dispatchMethodNotAllowed(HttpMethod.POST, primitiveCollectionUri, primitiveCollectionProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, primitiveCollectionUri, primitiveCollectionProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, primitiveCollectionUri, primitiveCollectionProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, primitiveCollectionUri, primitiveCollectionProcessor);

    final String primitiveCountUri = "FICRTCollString()/$count";
    final CountPrimitiveCollectionProcessor primitiveCountProcessor = mock(CountPrimitiveCollectionProcessor.class);
    dispatch(HttpMethod.GET, primitiveCountUri, primitiveCountProcessor);
    verify(primitiveCountProcessor).countPrimitiveCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
    dispatchMethodNotAllowed(HttpMethod.POST, primitiveCountUri, primitiveCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, primitiveCountUri, primitiveCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, primitiveCountUri, primitiveCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, primitiveCountUri, primitiveCountProcessor);

    ComplexProcessor complexProcessor = mock(ComplexProcessor.class);
    dispatch(HttpMethod.GET, "FICRTCTTwoPrim()", complexProcessor);
    verify(complexProcessor).readComplex(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    ComplexCollectionProcessor complexCollectionProcessor = mock(ComplexCollectionProcessor.class);
    dispatch(HttpMethod.GET, "FICRTCollCTTwoPrim()", complexCollectionProcessor);
    verify(complexCollectionProcessor).readComplexCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    final String complexCountUri = "FICRTCollCTTwoPrim()/$count";
    final CountComplexCollectionProcessor complexCountProcessor = mock(CountComplexCollectionProcessor.class);
    dispatch(HttpMethod.GET, complexCountUri, complexCountProcessor);
    verify(complexCountProcessor).countComplexCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
    dispatchMethodNotAllowed(HttpMethod.POST, complexCountUri, complexCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, complexCountUri, complexCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, complexCountUri, complexCountProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, complexCountUri, complexCountProcessor);

    final String mediaUri = "FICRTESMedia(ParameterInt16=1)/$value";
    final MediaEntityProcessor mediaProcessor = mock(MediaEntityProcessor.class);
    dispatch(HttpMethod.GET, mediaUri, mediaProcessor);
    verify(mediaProcessor).readMediaEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));
    dispatchMethodNotAllowed(HttpMethod.POST, mediaUri, mediaProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, mediaUri, mediaProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, mediaUri, mediaProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, mediaUri, mediaProcessor);
  }

  @Test
  public void dispatchAction() throws Exception {
    final ActionPrimitiveProcessor primitiveProcessor = mock(ActionPrimitiveProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT_STRING, primitiveProcessor);
    verify(primitiveProcessor).processActionPrimitive(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));
    dispatchMethodNotAllowed(HttpMethod.GET, ContainerProvider.AIRT_STRING, primitiveProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, ContainerProvider.AIRT_STRING, primitiveProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, ContainerProvider.AIRT_STRING, primitiveProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, ContainerProvider.AIRT_STRING, primitiveProcessor);

    ActionPrimitiveCollectionProcessor primitiveCollectionProcessor = mock(ActionPrimitiveCollectionProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT_COLL_STRING_TWO_PARAM, primitiveCollectionProcessor);
    verify(primitiveCollectionProcessor).processActionPrimitiveCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionComplexProcessor complexProcessor = mock(ActionComplexProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRTCT_TWO_PRIM_PARAM, complexProcessor);
    verify(complexProcessor).processActionComplex(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionComplexCollectionProcessor complexCollectionProcessor = mock(ActionComplexCollectionProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT_COLL_CT_TWO_PRIM_PARAM, complexCollectionProcessor);
    verify(complexCollectionProcessor).processActionComplexCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionEntityProcessor entityProcessor = mock(ActionEntityProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRTET_TWO_KEY_TWO_PRIM_PARAM, entityProcessor);
    verify(entityProcessor).processActionEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionEntityCollectionProcessor entityCollectionProcessor = mock(ActionEntityCollectionProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT_COLL_ET_KEY_NAV_PARAM, entityCollectionProcessor);
    verify(entityCollectionProcessor).processActionEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionEntityProcessor entityProcessorEs = mock(ActionEntityProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRTES_ALL_PRIM_PARAM, entityProcessorEs);
    verify(entityProcessorEs).processActionEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    ActionEntityCollectionProcessor entityCollectionProcessorEs = mock(ActionEntityCollectionProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT_COLL_ES_ALL_PRIM_PARAM, entityCollectionProcessorEs);
    verify(entityCollectionProcessorEs).processActionEntityCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    final ActionVoidProcessor voidProcessor = mock(ActionVoidProcessor.class);
    dispatch(HttpMethod.POST, ContainerProvider.AIRT, voidProcessor);
    verify(voidProcessor).processActionVoid(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));
    dispatchMethodNotAllowed(HttpMethod.GET, ContainerProvider.AIRT, voidProcessor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, ContainerProvider.AIRT, voidProcessor);
    dispatchMethodNotAllowed(HttpMethod.PUT, ContainerProvider.AIRT, voidProcessor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, ContainerProvider.AIRT, voidProcessor);
  }

  @Test
  public void dispatchEntity() throws Exception {
    final String uri = "ESAllPrim(0)";
    final EntityProcessor processor = mock(EntityProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.PATCH, uri, processor);
    verify(processor).updateEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor, times(2)).updateEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deleteEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatch(HttpMethod.POST, "ESAllPrim", processor);
    verify(processor).createEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
  }

  @Test
  public void dispatchMedia() throws Exception {
    final String uri = "ESMedia(1)/$value";
    final MediaEntityProcessor processor = mock(MediaEntityProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readMediaEntity(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.POST, "ESMedia", processor);
    verify(processor).createMediaEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor).updateMediaEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class), any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deleteMediaEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
  }

  @Test
  public void dispatchMediaDeleteIndirect() throws Exception {
    final MediaEntityProcessor processor = mock(MediaEntityProcessor.class);
    dispatch(HttpMethod.DELETE, "ESMedia(1)", processor);

    verify(processor).deleteEntity(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
  }

  @Test
  public void dispatchPrimitiveProperty() throws Exception {
    final String uri = "ESAllPrim(0)/PropertyString";
    final PrimitiveProcessor processor = mock(PrimitiveProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readPrimitive(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.PATCH, uri, processor);
    verify(processor).updatePrimitive(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor, times(2)).updatePrimitive(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deletePrimitive(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
  }

  @Test
  public void dispatchPrimitivePropertyValue() throws Exception {
    final String uri = "ESAllPrim(0)/PropertyString/$value";
    final PrimitiveValueProcessor processor = mock(PrimitiveValueProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readPrimitiveValue(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, null, HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString(),
        processor);
    verify(processor).updatePrimitiveValue(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deletePrimitiveValue(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
  }

  @Test
  public void dispatchPrimitiveCollectionProperty() throws Exception {
    final String uri = "ESMixPrimCollComp(7)/CollPropertyString";
    final PrimitiveCollectionProcessor processor = mock(PrimitiveCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readPrimitiveCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor).updatePrimitiveCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deletePrimitiveCollection(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
  }

  @Test
  public void dispatchPrimitiveCollectionPropertyCount() throws Exception {
    final String uri = "ESMixPrimCollComp(7)/CollPropertyString/$count";
    final CountPrimitiveCollectionProcessor processor = mock(CountPrimitiveCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).countPrimitiveCollection(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, uri, processor);
  }

  @Test
  public void dispatchComplexProperty() throws Exception {
    final String uri = "ESMixPrimCollComp(7)/PropertyComp";
    final ComplexProcessor processor = mock(ComplexProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readComplex(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.PATCH, uri, processor);
    verify(processor).updateComplex(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor, times(2)).updateComplex(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deleteComplex(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
  }

  @Test
  public void dispatchComplexCollectionProperty() throws Exception {
    final String uri = "ESMixPrimCollComp(7)/CollPropertyComp";
    final ComplexCollectionProcessor processor = mock(ComplexCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readComplexCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor).updateComplexCollection(
        any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class), any(ContentType.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uri, processor);
    verify(processor).deleteComplexCollection(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
  }

  @Test
  public void dispatchComplexCollectionPropertyCount() throws Exception {
    final String uri = "ESMixPrimCollComp(7)/CollPropertyComp/$count";
    final CountComplexCollectionProcessor processor = mock(CountComplexCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).countComplexCollection(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.DELETE, uri, processor);
  }

  @Test
  public void dispatchReference() throws Exception {
    final String uri = "ESAllPrim(0)/NavPropertyETTwoPrimOne/$ref";
    final String uriMany = "ESAllPrim(0)/NavPropertyETTwoPrimMany/$ref";
    final ReferenceProcessor processor = mock(ReferenceProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readReference(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatch(HttpMethod.PATCH, uri, processor);
    verify(processor).updateReference(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatch(HttpMethod.PUT, uri, processor);
    verify(processor, times(2)).updateReference(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.POST, uri, processor);

    dispatch(HttpMethod.POST, uriMany, processor);
    verify(processor).createReference(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatch(HttpMethod.DELETE, uriMany, "$id=ESTwoPrim(1)", null, null, processor);
    verify(processor).deleteReference(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class));
  }

  @Test
  public void dispatchReferenceCollection() throws Exception {
    final String uri = "ESAllPrim(0)/NavPropertyETTwoPrimMany/$ref";
    final ReferenceCollectionProcessor processor = mock(ReferenceCollectionProcessor.class);

    dispatch(HttpMethod.GET, uri, processor);
    verify(processor).readReferenceCollection(any(ODataRequest.class), any(ODataResponse.class), any(UriInfo.class),
        any(ContentType.class));

    dispatchMethodNotAllowed(HttpMethod.PATCH, uri, processor);
    dispatchMethodNotAllowed(HttpMethod.PUT, uri, processor);
  }

  @Test
  public void noRequestContentType() throws Exception {
    EntityProcessor processor = mock(EntityProcessor.class);
    final ODataResponse response = dispatch(HttpMethod.POST, "ESAllPrim", null,
        HttpHeader.CONTENT_TYPE, null, processor);
    verifyZeroInteractions(processor);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void illegalRequestContentType() throws Exception {
    EntityProcessor processor = mock(EntityProcessor.class);
    final ODataResponse response = dispatch(HttpMethod.POST, "ESAllPrim", null,
        HttpHeader.CONTENT_TYPE, "*/*", processor);
    verifyZeroInteractions(processor);
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void unsupportedRequestContentType() throws Exception {
    EntityProcessor processor = mock(EntityProcessor.class);
    ErrorProcessor errorProcessor = mock(ErrorProcessor.class);
    dispatch(HttpMethod.POST, "ESAllPrim", null, HttpHeader.CONTENT_TYPE, "some/unsupported", errorProcessor);
    verifyZeroInteractions(processor);
    verify(errorProcessor).processError(any(ODataRequest.class), any(ODataResponse.class),
        any(ODataServerError.class),
        any(ContentType.class));
  }

  private ODataResponse dispatch(final HttpMethod method, final String path, final String query,
      final String headerName, final String headerValue, final Processor processor) {
    ODataRequest request = new ODataRequest();
    request.setMethod(method);
    request.setRawBaseUri(BASE_URI);
    if (path.isEmpty()) {
      request.setRawRequestUri(BASE_URI);
    }
    request.setRawODataPath(path);
    request.setRawQueryPath(query);

    if (headerName != null) {
      request.addHeader(headerName, Collections.singletonList(headerValue));
    }

    if (headerName != HttpHeader.CONTENT_TYPE) {
      request.addHeader(HttpHeader.CONTENT_TYPE, Collections.singletonList(
          ContentType.JSON.toContentTypeString()));
    }

    final OData odata = OData.newInstance();
    final ServiceMetadata metadata = odata.createServiceMetadata(
        new EdmTechProvider(), Collections.<EdmxReference> emptyList());

    ODataHandler handler = new ODataHandler(odata, metadata, new ServerCoreDebugger(odata));

    if (processor != null) {
      handler.register(processor);
    }

    final ODataResponse response = handler.process(request);
    assertNotNull(response);
    return response;
  }

  private ODataResponse dispatch(final HttpMethod method, final String path, final Processor processor) {
    return dispatch(method, path, null, null, null, processor);
  }

  private void dispatchMethodNotAllowed(final HttpMethod method, final String path, final Processor processor) {
    final ODataResponse response = dispatch(method, path, processor);
    assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getContent());
  }
}
