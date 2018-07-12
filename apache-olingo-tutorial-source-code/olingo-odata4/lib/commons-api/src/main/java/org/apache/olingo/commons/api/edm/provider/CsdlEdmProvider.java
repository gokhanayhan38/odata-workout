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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * The interface Csdl edm provider.
 */
public interface CsdlEdmProvider {

  /**
   * This method should return an {@link CsdlEnumType} or <b>null</b> if nothing is found
   *
   * @param enumTypeName full qualified name of enum type
   * @return for given name
   * @throws ODataException the o data exception
   */
  CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException;

  /**
   * This method should return an {@link CsdlTypeDefinition} or <b>null</b> if nothing is found
   *
   * @param typeDefinitionName full qualified name of type definition
   * @return for given name
   * @throws ODataException the o data exception
   */
  CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException;

  /**
   * This method should return an {@link CsdlEntityType} or <b>null</b> if nothing is found
   *
   * @param entityTypeName full qualified name of entity type
   * @return for the given name
   * @throws ODataException the o data exception
   */
  CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException;

  /**
   * This method should return a {@link CsdlComplexType} or <b>null</b> if nothing is found.
   *
   * @param complexTypeName full qualified name of complex type
   * @return for the given name
   * @throws ODataException the o data exception
   */
  CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException;

  /**
   * This method should return a list of all {@link CsdlAction} for the FullQualifiedname
   * or <b>null</b> if nothing is found
   *
   * @param actionName full qualified name of action
   * @return List of
   * or null
   * @throws ODataException the o data exception
   */
  List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException;

  /**
   * This method should return a list of all {@link CsdlFunction} for the FullQualifiedname or <b>null</b> if nothing is
   * found
   *
   * @param functionName full qualified name of function
   * @return List of
   * or null
   * @throws ODataException the o data exception
   */
  List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException;

  /**
   * This method should return a {@link CsdlTerm} for the FullQualifiedName or <b>null</b> if nothing is found.
   * @param termName the name of the Term
   * @return or null
   * @throws ODataException the o data exception
   */
  CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException;

  /**
   * This method should return an {@link CsdlEntitySet} or <b>null</b> if nothing is found
   *
   * @param entityContainer this EntitySet is contained in
   * @param entitySetName name of entity set
   * @return for the given container and entityset name
   * @throws ODataException the o data exception
   */
  CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlSingleton} or <b>null</b> if nothing is found
   *
   * @param entityContainer this Singleton is contained in
   * @param singletonName name of singleton
   * @return for given container and singleton name
   * @throws ODataException the o data exception
   */
  CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlActionImport} or <b>null</b> if nothing is found
   *
   * @param entityContainer this ActionImport is contained in
   * @param actionImportName name of action import
   * @return for the given container and ActionImport name
   * @throws ODataException the o data exception
   */
  CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException;

  /**
   * This method should return a {@link CsdlFunctionImport} or <b>null</b> if nothing is found
   *
   * @param entityContainer this FunctionImport is contained in
   * @param functionImportName name of function import
   * @return for the given container name and function import name
   * @throws ODataException the o data exception
   */
  CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlEntityContainerInfo} or <b>null</b> if nothing is found
   *
   * @param entityContainerName (null for default container)
   * @return for the given name
   * @throws ODataException the o data exception
   */
  CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
      throws ODataException;

  /**
   * This method should return a list of all namespaces which have an alias
   *
   * @return List of alias info
   * @throws ODataException the o data exception
   */
  List<CsdlAliasInfo> getAliasInfos() throws ODataException;

  /**
   * This method should return a collection of all {@link CsdlSchema}
   *
   * @return List of
   * @throws ODataException the o data exception
   */
  List<CsdlSchema> getSchemas() throws ODataException;

  /**
   * Returns the entity container of this edm
   * @return of this edm
   * @throws ODataException the o data exception
   */
  CsdlEntityContainer getEntityContainer() throws ODataException;

  /**
   * Gets annotations group.
   *
   * @param targetName full qualified name of target
   * @return group for the given Target
   * @throws ODataException the o data exception
   */
  CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName) throws ODataException;

  /**
   * Gets annotatable.
   *
   * @param annotatedName full qualified name of annotatable
   * @return Annotatable element by target name
   * @throws ODataException the o data exception
   */
  CsdlAnnotatable getAnnotatable(FullQualifiedName annotatedName) throws ODataException;
}
