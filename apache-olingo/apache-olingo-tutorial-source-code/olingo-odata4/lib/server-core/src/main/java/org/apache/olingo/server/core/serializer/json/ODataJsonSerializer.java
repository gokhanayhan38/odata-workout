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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.AbstractODataSerializer;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.apache.olingo.server.core.uri.UriHelperImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataJsonSerializer extends AbstractODataSerializer {

  private final boolean isIEEE754Compatible;
  private final boolean isODataMetadataNone;

  public ODataJsonSerializer(final ContentType contentType) {
    this.isIEEE754Compatible = isODataIEEE754Compatible(contentType);
    this.isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
  }

  @Override
  public SerializerResult serviceDocument(final ServiceMetadata metadata, final String serviceRoot)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ServiceDocumentJsonSerializer(metadata, serviceRoot, isODataMetadataNone).writeServiceDocument(json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    throw new SerializerException("Metadata in JSON format not supported!",
        SerializerException.MessageKeys.JSON_METADATA);
  }

  @Override
  public SerializerResult error(final ODataServerError error) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ODataErrorSerializer().writeErrorDocument(json, error);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final EntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      writeContextURL(contextURL, json);

      writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeCount(entitySet, json);
      }
      json.writeFieldName(Constants.VALUE);
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, false, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), options.getSelect(), options.getWriteOnlyReferences(), json);
      }
      writeNextLink(entitySet, json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          options == null ? null : options.getSelect(),
          options == null ? false : options.getWriteOnlyReferences(),
          json);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  private ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (isODataMetadataNone) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final EntityCollection entitySet, final ExpandOption expand, final SelectOption select,
      final boolean onlyReference, final JsonGenerator json) throws IOException,
      SerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet.getEntities()) {
      if (onlyReference) {
        json.writeStartObject();
        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
        json.writeEndObject();
      } else {
        writeEntity(metadata, entityType, entity, null, expand, select, false, json);
      }
    }
    json.writeEndArray();
  }

  protected void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ContextURL contextURL, final ExpandOption expand,
      final SelectOption select, final boolean onlyReference, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    if (!isODataMetadataNone) {
      if (contextURL != null) { // top-level entity
        writeContextURL(contextURL, json);
        writeMetadataETag(metadata, json);
      }
      if (entity.getETag() != null) {
        json.writeStringField(Constants.JSON_ETAG, entity.getETag());
      }
      if (entityType.hasStream()) {
        if (entity.getMediaETag() != null) {
          json.writeStringField(Constants.JSON_MEDIA_ETAG, entity.getMediaETag());
        }
        if (entity.getMediaContentType() != null) {
          json.writeStringField(Constants.JSON_MEDIA_CONTENT_TYPE, entity.getMediaContentType());
        }
        if (entity.getMediaContentSource() != null) {
          json.writeStringField(Constants.JSON_MEDIA_READ_LINK, entity.getMediaContentSource().toString());
        }
        if (entity.getMediaEditLinks() != null && !entity.getMediaEditLinks().isEmpty()) {
          json.writeStringField(Constants.JSON_MEDIA_EDIT_LINK, entity.getMediaEditLinks().get(0).getHref());
        }
      }
    }
    if (onlyReference) {
      json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
    } else {
      final EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
      if (!isODataMetadataNone && !resolvedType.equals(entityType)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + entity.getType());
      }
      writeProperties(resolvedType, entity.getProperties(), select, json);
      writeNavigationProperties(metadata, resolvedType, entity, expand, json);
      json.writeEndObject();
    }
  }

  protected EdmEntityType resolveEntityType(final ServiceMetadata metadata, final EdmEntityType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmEntityType derivedType = metadata.getEdm().getEntityType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("EntityType not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmEntityType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected EdmComplexType resolveComplexType(final ServiceMetadata metadata, final EdmComplexType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmComplexType derivedType = metadata.getEdm().getComplexType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("Complex Type not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmComplexType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected void writeProperties(final EdmStructuredType type, final List<Property> properties,
      final SelectOption select, final JsonGenerator json)
      throws IOException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? new HashSet<String>() :
        ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(edmProperty, property, selectedPaths, json);
      }
    }
  }

  protected void writeNavigationProperties(final ServiceMetadata metadata,
      final EdmStructuredType type, final Linked linked, final ExpandOption expand,
      final JsonGenerator json) throws SerializerException, IOException {
    if (ExpandSelectHelper.hasExpand(expand)) {
      final boolean expandAll = ExpandSelectHelper.isExpandAll(expand);
      final Set<String> expanded = expandAll ? new HashSet<String>() :
          ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
      for (final String propertyName : type.getNavigationPropertyNames()) {
        if (expandAll || expanded.contains(propertyName)) {
          final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
          final Link navigationLink = linked.getNavigationLink(property.getName());
          final ExpandItem innerOptions = expandAll ? null :
              ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
          if (innerOptions != null && innerOptions.getLevelsOption() != null) {
            throw new SerializerException("Expand option $levels is not supported.",
                SerializerException.MessageKeys.NOT_IMPLEMENTED);
          }
          writeExpandedNavigationProperty(metadata, property, navigationLink,
              innerOptions == null ? null : innerOptions.getExpandOption(),
              innerOptions == null ? null : innerOptions.getSelectOption(), json);
        }
      }
    }
  }

  protected void writeExpandedNavigationProperty(final ServiceMetadata metadata, final EdmNavigationProperty property,
      final Link navigationLink, final ExpandOption innerExpand, final SelectOption innerSelect,
      final JsonGenerator json) throws IOException, SerializerException {
    json.writeFieldName(property.getName());
    if (property.isCollection()) {
      if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
        json.writeStartArray();
        json.writeEndArray();
      } else {
        writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), innerExpand,
            innerSelect, false, json);
      }
    } else {
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), null,
            innerExpand, innerSelect, false, json);
      }
    }
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        json.writeNull();
      }
    } else {
      writePropertyValue(edmProperty, property, selectedPaths, json);
    }
  }

  private void writePropertyValue(final EdmProperty edmProperty,
      final Property property, final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    try {
      if (edmProperty.isPrimitive()) {
        if (edmProperty.isCollection()) {
          writePrimitiveCollection((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
        } else {
          writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
        }
      } else if (edmProperty.isCollection()) {
        writeComplexCollection((EdmComplexType) edmProperty.getType(), property, selectedPaths, json);
      } else if (property.isComplex()) {
        writeComplexValue((EdmComplexType) edmProperty.getType(), property.asComplex().getValue(), selectedPaths, json);
      } else if (property.isEnum()) {
        writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
            edmProperty.isNullable(), edmProperty.getMaxLength(),
            edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
      } else {
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      }
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          edmProperty.getName(), property.getValue().toString());
    }
  }

  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
        try {
          writePrimitiveValue(type, value, isNullable, maxLength, precision, scale, isUnicode, json);
        } catch (EdmPrimitiveTypeException e) {
          throw new SerializerException("Wrong value for property!", e,
              SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
              property.getName(), property.getValue().toString());
        }
        break;
      case COLLECTION_GEOSPATIAL:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      case COLLECTION_ENUM:
        json.writeString(value.toString());
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writeComplexCollection(final EdmComplexType type, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        writeComplexValue(type, ((ComplexValue) value).getValue(), selectedPaths, json);
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writePrimitive(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws EdmPrimitiveTypeException, IOException, SerializerException {
    if (property.isPrimitive()) {
      writePrimitiveValue(type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else if (property.isGeospatial()) {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
    } else if (property.isEnum()) {
      writePrimitiveValue(type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json) throws EdmPrimitiveTypeException, IOException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (value == null) {
      json.writeNull();
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
      json.writeBoolean(Boolean.parseBoolean(value));
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)
        || (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64))
        && !isIEEE754Compatible) {
      json.writeNumber(value);
    } else {
      json.writeString(value);
    }
  }

  protected void writeComplexValue(final EdmComplexType type, final List<Property> properties,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty((EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, propertyName),
            json);
      }
    }
    json.writeEndObject();
  }

  private Property findProperty(final String propertyName, final List<Property> properties) {
    for (final Property property : properties) {
      if (propertyName.equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  @Override
  public SerializerResult primitive(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (property.isNull()) {
        throw new SerializerException("Property value can not be null.", SerializerException.MessageKeys.NULL_INPUT);
      } else {
        json.writeFieldName(Constants.VALUE);
        writePrimitive(type, property,
            options == null ? null : options.isNullable(),
            options == null ? null : options.getMaxLength(),
            options == null ? null : options.getPrecision(),
            options == null ? null : options.getScale(),
            options == null ? null : options.isUnicode(), json);
      }
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (final EdmPrimitiveTypeException e) {
      cachedException = new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      final EdmComplexType resolvedType = resolveComplexType(metadata, type, property.getType());
      if (!isODataMetadataNone && !resolvedType.equals(type)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + property.getType());
      }
      final List<Property> values =
          property.isNull() ? Collections.<Property> emptyList() : property.asComplex().getValue();
      writeProperties(type, values, options == null ? null : options.getSelect(), json);
      if (!property.isNull() && property.isComplex()) {
        writeNavigationProperties(metadata, type, property.asComplex(),
            options == null ? null : options.getExpand(), json);
      }
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult primitiveCollection(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      json.writeFieldName(Constants.VALUE);
      writePrimitiveCollection(type, property,
          options == null ? null : options.isNullable(),
          options == null ? null : options.getMaxLength(),
          options == null ? null : options.getPrecision(),
          options == null ? null : options.getScale(),
          options == null ? null : options.isUnicode(), json);
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      json.writeFieldName(Constants.VALUE);
      writeComplexCollection(type, property, null, json);
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult reference(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final Entity entity, final ReferenceSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);

      json.writeStartObject();
      writeContextURL(contextURL, json);
      json.writeStringField(Constants.JSON_ID, uriHelper.buildCanonicalURL(edmEntitySet, entity));
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult referenceCollection(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final EntityCollection entityCollection, final ReferenceCollectionSerializerOptions options)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      writeContextURL(contextURL, json);
      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeCount(entityCollection, json);
      }

      json.writeArrayFieldStart(Constants.VALUE);
      for (final Entity entity : entityCollection.getEntities()) {
        json.writeStartObject();
        json.writeStringField(Constants.JSON_ID, uriHelper.buildCanonicalURL(edmEntitySet, entity));
        json.writeEndObject();
      }
      json.writeEndArray();

      writeNextLink(entityCollection, json);

      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }

  }

  private void writeContextURL(final ContextURL contextURL, JsonGenerator json) throws IOException {
    if (!isODataMetadataNone && contextURL != null) {
      json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
    }
  }

  private void writeMetadataETag(final ServiceMetadata metadata, JsonGenerator json) throws IOException {
    if (!isODataMetadataNone
        && metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      json.writeStringField(Constants.JSON_METADATA_ETAG,
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }
  }

  private void writeCount(final EntityCollection entityCollection, JsonGenerator json) throws IOException {
    if (entityCollection.getCount() != null) {
      if (isIEEE754Compatible) {
        json.writeStringField(Constants.JSON_COUNT, entityCollection.getCount().toString());
      } else {
        json.writeNumberField(Constants.JSON_COUNT, entityCollection.getCount());
      }
    }
  }

  private void writeNextLink(final EntityCollection entitySet, JsonGenerator json) throws IOException {
    if (entitySet.getNext() != null) {
      json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
    }
  }

  private boolean isODataIEEE754Compatible(final ContentType contentType) {
    return contentType.getParameters().containsKey(ContentType.PARAMETER_IEEE754_COMPATIBLE)
        && Boolean.TRUE.toString().equalsIgnoreCase(
            contentType.getParameter(ContentType.PARAMETER_IEEE754_COMPATIBLE).toLowerCase());
  }
}
