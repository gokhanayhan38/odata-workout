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
package org.apache.olingo.server.tecsvc.processor.queryoptions.expression;

import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceProperty;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitor;
import org.apache.olingo.server.api.uri.queryoption.expression.Literal;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.TypedOperand;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.UntypedOperand;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.VisitorOperand;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operation.BinaryOperator;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operation.MethodCallOperator;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operation.UnaryOperator;

public class ExpressionVisitorImpl implements ExpressionVisitor<VisitorOperand> {

  final private Entity entity;

  public ExpressionVisitorImpl(final Entity entity, final EdmBindingTarget bindingTarget) {
    this.entity = entity;
  }

  @Override
  public VisitorOperand visitBinaryOperator(final BinaryOperatorKind operator, final VisitorOperand left,
      final VisitorOperand right)
      throws ExpressionVisitException, ODataApplicationException {

    final BinaryOperator binaryOperator = new BinaryOperator(left, right);

    switch (operator) {
    case AND:
      return binaryOperator.andOperator();
    case OR:
      return binaryOperator.orOperator();
    case EQ:
      return binaryOperator.equalsOperator();
    case NE:
      return binaryOperator.notEqualsOperator();
    case GE:
      return binaryOperator.greaterEqualsOperator();
    case GT:
      return binaryOperator.greaterThanOperator();
    case LE:
      return binaryOperator.lessEqualsOperator();
    case LT:
      return binaryOperator.lessThanOperator();
    case ADD:
    case SUB:
    case MUL:
    case DIV:
    case MOD:
      return binaryOperator.arithmeticOperator(operator);
    default:
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitUnaryOperator(final UnaryOperatorKind operator, final VisitorOperand operand)
      throws ExpressionVisitException, ODataApplicationException {

    final UnaryOperator unaryOperator = new UnaryOperator(operand);

    switch (operator) {
    case MINUS:
      return unaryOperator.minusOperation();
    case NOT:
      return unaryOperator.notOperation();
    default:
      // Can't happen.
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitMethodCall(final MethodKind methodCall, final List<VisitorOperand> parameters)
      throws ExpressionVisitException, ODataApplicationException {

    final MethodCallOperator methodCallOperation = new MethodCallOperator(parameters);

    switch (methodCall) {
    case ENDSWITH:
      return methodCallOperation.endsWith();
    case INDEXOF:
      return methodCallOperation.indexOf();
    case STARTSWITH:
      return methodCallOperation.startsWith();
    case TOLOWER:
      return methodCallOperation.toLower();
    case TOUPPER:
      return methodCallOperation.toUpper();
    case TRIM:
      return methodCallOperation.trim();
    case SUBSTRING:
      return methodCallOperation.substring();
    case CONTAINS:
      return methodCallOperation.contains();
    case CONCAT:
      return methodCallOperation.concat();
    case LENGTH:
      return methodCallOperation.length();
    case YEAR:
      return methodCallOperation.year();
    case MONTH:
      return methodCallOperation.month();
    case DAY:
      return methodCallOperation.day();
    case HOUR:
      return methodCallOperation.hour();
    case MINUTE:
      return methodCallOperation.minute();
    case SECOND:
      return methodCallOperation.second();
    case FRACTIONALSECONDS:
      return methodCallOperation.fractionalseconds();
    case ROUND:
      return methodCallOperation.round();
    case FLOOR:
      return methodCallOperation.floor();
    case CEILING:
      return methodCallOperation.ceiling();

    default:
      return throwNotImplemented();
    }
  }

  @Override
  public VisitorOperand visitLambdaExpression(final String lambdaFunction, final String lambdaVariable,
      final Expression expression)
      throws ExpressionVisitException, ODataApplicationException {

    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitLiteral(final Literal literal) throws ExpressionVisitException, ODataApplicationException {
    return new UntypedOperand(literal.getText());
  }

  @Override
  public VisitorOperand visitMember(final UriInfoResource member) throws ExpressionVisitException,
      ODataApplicationException {

    final List<UriResource> uriResourceParts = member.getUriResourceParts();

    // UriResourceParts contains at least one UriResource
    if (!(uriResourceParts.get(0) instanceof UriResourceProperty)) {
      return throwNotImplemented();
    }

    EdmProperty currentEdmProperty = ((UriResourceProperty) uriResourceParts.get(0)).getProperty();
    Property currentProperty = entity.getProperty(currentEdmProperty.getName());

    for (int i = 1; i < uriResourceParts.size(); i++) {
      if (currentProperty.isComplex()) {
        currentEdmProperty = ((UriResourceProperty) uriResourceParts.get(i)).getProperty();
        final List<Property> complex = currentProperty.asComplex().getValue();
        for (final Property innerProperty : complex) {
          if (innerProperty.getName().equals(currentEdmProperty.getName())) {
            currentProperty = innerProperty;
            break;
          }
        }
      }
    }

    return new TypedOperand(currentProperty.getValue(), currentEdmProperty.getType(), currentEdmProperty);
  }

  @Override
  public VisitorOperand visitAlias(final String aliasName) throws ExpressionVisitException, ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitTypeLiteral(final EdmType type) throws ExpressionVisitException, 
      ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitLambdaReference(final String variableName) throws ExpressionVisitException,
      ODataApplicationException {
    return throwNotImplemented();
  }

  @Override
  public VisitorOperand visitEnum(final EdmEnumType type, final List<String> enumValues)
      throws ExpressionVisitException,
      ODataApplicationException {
    return throwNotImplemented();
  }

  private VisitorOperand throwNotImplemented() throws ODataApplicationException {
    throw new ODataApplicationException("Not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
        Locale.ROOT);
  }
}
