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
package org.apache.olingo.server.tecsvc.processor.queryoptions.options;

import java.util.Iterator;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmBindingTarget;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.ExpressionVisitorImpl;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.TypedOperand;
import org.apache.olingo.server.tecsvc.processor.queryoptions.expression.operand.VisitorOperand;

public class FilterHandler {

  protected static final OData oData;
  protected static final EdmPrimitiveType primBoolean;

  static {
    oData = OData.newInstance();
    primBoolean = oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Boolean);
  }

  public static void applyFilterSystemQuery(final FilterOption filterOption, final EntityCollection entitySet,
      final EdmBindingTarget edmEntitySet) throws ODataApplicationException {

    if (filterOption == null) {
      return;
    }

    try {
      final Iterator<Entity> iter = entitySet.getEntities().iterator();

      while (iter.hasNext()) {
        final VisitorOperand operand = filterOption.getExpression()
            .accept(new ExpressionVisitorImpl(iter.next(), edmEntitySet));
        final TypedOperand typedOperand = operand.asTypedOperand();
        
        if(typedOperand.is(primBoolean)) {
          if(Boolean.FALSE.equals(typedOperand.getTypedValue(Boolean.class))) {
            iter.remove();
          }
        } else {
          throw new ODataApplicationException("Invalid filter expression. Filter expressions must return a value of " 
                + "type Edm.Boolean", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ROOT);
        }
      }

    } catch (ExpressionVisitException e) {
      throw new ODataApplicationException("Exception in filter evaluation",
          HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }
}
