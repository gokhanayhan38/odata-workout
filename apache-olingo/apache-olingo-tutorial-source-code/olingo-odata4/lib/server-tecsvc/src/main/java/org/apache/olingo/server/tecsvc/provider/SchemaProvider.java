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
package org.apache.olingo.server.tecsvc.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;

public class SchemaProvider {

  private EdmTechProvider prov;

  public static final String NAMESPACE = "olingo.odata.test1";

  public SchemaProvider(final EdmTechProvider prov) {
    this.prov = prov;
  }

  public List<CsdlSchema> getSchemas() throws ODataException {
    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);
    schema.setAlias("Namespace1_Alias");
    schemas.add(schema);

    // EnumTypes
    List<CsdlEnumType> enumTypes = new ArrayList<CsdlEnumType>();
    schema.setEnumTypes(enumTypes);

    // TypeDefinitions
    List<CsdlTypeDefinition> typeDefinitions = new ArrayList<CsdlTypeDefinition>();
    schema.setTypeDefinitions(typeDefinitions);
    typeDefinitions.add(prov.getTypeDefinition(TypeDefinitionProvider.nameTDString));
    enumTypes.add(prov.getEnumType(EnumTypeProvider.nameENString));

    // EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    schema.setEntityTypes(entityTypes);

    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCollAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETMixPrimCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoKeyTwoPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETMixEnumDefCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETBase));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoBase));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllKey));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompCollAllPrim));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETMedia));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETFourKeyAlias));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETServerSidePaging));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAllNullable));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETBaseTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETTwoBaseTwoKeyNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETKeyNavCont));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETCompMixPrimCollComp));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETKeyPrimNav));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAbstract));
    entityTypes.add(prov.getEntityType(EntityTypeProvider.nameETAbstractBase));

    // ComplexTypes
    List<CsdlComplexType> complexType = new ArrayList<CsdlComplexType>();
    schema.setComplexTypes(complexType);
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTAllPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCollAllPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoPrim));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTMixPrimCollComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTMixEnumDef));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTBase));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoBase));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompCollComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTPrimComp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTNavFiveProp));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTBasePrimCompNav));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTTwoBasePrimCompNav));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTCompNav));
    complexType.add(prov.getComplexType(ComplexTypeProvider.nameCTNavCont));

    // Actions
    List<CsdlAction> actions = new ArrayList<CsdlAction>();
    schema.setActions(actions);
    actions.addAll(prov.getActions(ActionProvider.nameBAETTwoKeyNavRTETTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAESAllPrimRTETAllPrim));
    actions.addAll(prov.getActions(ActionProvider.nameBAESTwoKeyNavRTESTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAESTwoKeyNavRTESKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAETBaseTwoKeyNavRTETBaseTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav));
    actions.addAll(prov.getActions(ActionProvider.nameBAETAllPrimRT));
    actions.addAll(prov.getActions(ActionProvider.nameBAESAllPrimRT));
    actions.addAll(prov.getActions(ActionProvider.nameUARTString));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCollStringTwoParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCTTwoPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCollCTTwoPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTETTwoKeyTwoPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCollETKeyNavParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTETAllPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTCollETAllPrimParam));
    actions.addAll(prov.getActions(ActionProvider.nameUART));
    actions.addAll(prov.getActions(ActionProvider.nameUARTParam));
    actions.addAll(prov.getActions(ActionProvider.nameUARTTwoParam));

    // Functions
    List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
    schema.setFunctions(functions);

    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTInt16));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETTwoKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETTwoKeyNavParamCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTStringTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollETTwoKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollStringTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTAllPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTTwoPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollCTTwoPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETMedia));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollETMedia));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTCollETMixPrimCollCompTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTETAllPrimTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollETMixPrimCollCompTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFNRTCollCTNavFiveProp));
    functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTCollETKeyNavContParam));

    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCStringRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTETTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESBaseTwoKeyNavRTESBaseTwoKey));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESAllPrimRTCTAllPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollCTTwoPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollString));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCSINavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETBaseTwoKeyNavRTESBaseTwoKey));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCollStringRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESBaseTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCollCTPrimCompRTESAllPrim));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESKeyNavRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETKeyNavRTETKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFESTwoKeyNavRTESTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTETTwoKeyNav));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCETTwoKeyNavRTCTTwoPrim));
    // functions.addAll(prov.getFunctions(FunctionProvider.nameUFCRTESMixPrimCollCompTwoParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCTNavFiveProp));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTCollCTNavFiveProp));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESTwoKeyNavRTStringParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESKeyNavRTETKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTETTwoKeyNavParam));
    functions.addAll(prov.getFunctions(FunctionProvider.nameBFCESKeyNavRTESTwoKeyNav));

    // functions.addAll(prov.getFunctions(FunctionProvider.nameBFCCTPrimCompRTESTwoKeyNavParam));

    // EntityContainer
    schema.setEntityContainer(prov.getEntityContainer());

    return schemas;
  }

}
