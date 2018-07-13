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
package org.apache.olingo.server.api.processor;

/**
 * Processor interface for handling a single instance of an Delta Response.
 */
public interface DeltaProcessor extends Processor {

  //  NOT YET AVAILABLE
  //  /**
  //   * Reads delta information from persistence and put it as serialized content and
  //   * with according status into the response.
  //   * @param request  OData request object containing raw HTTP information
  //   * @param response OData response object for collecting response data
  //   * @param uriInfo  information of a parsed OData URI
  //   * @param responseFormat   requested content type after content negotiation
  //   * @throws ODataApplicationException if the service implementation encounters a failure
  //   * @throws ODataLibraryException
  //   */
  //  void readDelta(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat)
  //      throws ODataApplicationException, ODataLibraryException;
}