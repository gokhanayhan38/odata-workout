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
package org.apache.olingo.server.core.uri.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;

public class ExpandValidator implements TestValidator {
  private Edm edm;
  private TestValidator invokedByValidator;

  private int expandItemIndex;
  private ExpandOptionImpl expandOption;
  private ExpandItem expandItem;

  // --- Setup ---

  public ExpandValidator setUpValidator(final TestValidator validator) {
    invokedByValidator = validator;
    return this;
  }

  public ExpandValidator setExpand(final ExpandOptionImpl expand) {
    expandOption = expand;
    first();
    return this;
  }

  public ExpandValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // --- Navigation ---

  public ExpandValidator goUpToExpandValidator() {
    return (ExpandValidator) invokedByValidator;
  }

  public ResourceValidator goUpToUriResourceValidator() {
    return (ResourceValidator) invokedByValidator;
  }

  public ResourceValidator goPath() {
    UriInfoImpl uriInfo = (UriInfoImpl) expandItem.getResourcePath();

    if (uriInfo.getKind() != UriInfoKind.resource) {
      fail("goPath() can only be used on UriInfoKind.resource");
    }

    return new ResourceValidator()
    .setUpValidator(this)
    .setEdm(edm)
    .setUriInfoImplPath(uriInfo);

  }

  public FilterValidator goOrder(final int index) {
    OrderByOptionImpl orderBy = (OrderByOptionImpl) expandItem.getOrderByOption();

    return new FilterValidator()
    .setValidator(this)
    .setEdm(edm)
    .setExpression(orderBy.getOrders().get(index).getExpression());
  }

  public ResourceValidator goSelectItem(final int index) {
    SelectOptionImpl select = (SelectOptionImpl) expandItem.getSelectOption();

    SelectItem item = select.getSelectItems().get(index);
    UriInfoImpl uriInfo = (UriInfoImpl) item.getResourcePath();

    return new ResourceValidator()
    .setUpValidator(this)
    .setEdm(edm)
    .setUriInfoImplPath(uriInfo);

  }

  public ExpandValidator goExpand() {
    return new ExpandValidator()
    .setExpand((ExpandOptionImpl) expandItem.getExpandOption())
    .setUpValidator(this);
  }

  public ExpandValidator first() {
    expandItemIndex = 0;
    expandItem = expandOption.getExpandItems().get(expandItemIndex);
    return this;
  }

  public ExpandValidator next() {
    expandItemIndex++;

    try {
      expandItem = expandOption.getExpandItems().get(expandItemIndex);
    } catch (IndexOutOfBoundsException ex) {
      fail("not enough segments");
    }
    return this;

  }

  public ExpandValidator isSegmentStar() {
    assertTrue(expandItem.isStar());
    return this;
  }

  public ExpandValidator isSegmentRef() {
    assertTrue(expandItem.isRef());
    return this;
  }

  public ExpandValidator isLevelText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getLevelsOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSkipText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSkipOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isTopText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getTopOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isInlineCountText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getCountOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectText(final String text) {
    QueryOptionImpl option = (QueryOptionImpl) expandItem.getSelectOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectItemStar(final int index) {
    SelectOption select = expandItem.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertTrue(item.isStar());
    return this;
  }

  public ExpandValidator isSelectItemAllOperations(final int index, final FullQualifiedName fqn) {
    SelectOption select = expandItem.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }

  public ExpandValidator isFilterOptionText(final String text) {
    QueryOption option = expandItem.getFilterOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isFilterSerialized(final String serialized) {
    FilterOption filter = expandItem.getFilterOption();

    try {
      String tmp = FilterTreeToText.Serialize(filter);
      assertEquals(serialized, tmp);
    } catch (ExpressionVisitException e) {
      fail("Exception occurred while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    } catch (ODataApplicationException e) {
      fail("Exception occurred while converting the filterTree into text" + "\n"
          + " Exception: " + e.getMessage());
    }

    return this;
  }

  public ExpandValidator isSortOrder(final int index, final boolean descending) {
    OrderByOptionImpl orderBy = (OrderByOptionImpl) expandItem.getOrderByOption();
    assertEquals(descending, orderBy.getOrders().get(index).isDescending());
    return this;
  }

  public ExpandValidator isExpandStartType(final FullQualifiedName fullName) {
    EdmType actualType = expandItem.getStartTypeFilter();

    FullQualifiedName actualName = new FullQualifiedName(actualType.getNamespace(), actualType.getName());
    assertEquals(fullName, actualName);
    return this;

  }

}
