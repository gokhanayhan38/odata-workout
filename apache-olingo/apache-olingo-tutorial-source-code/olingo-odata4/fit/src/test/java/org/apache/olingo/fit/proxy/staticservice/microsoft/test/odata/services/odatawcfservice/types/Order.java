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
package org.apache.olingo.fit.proxy.staticservice.microsoft.test.odata.services.odatawcfservice.types;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;

// CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "Order",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface Order
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<Order>, org.apache.olingo.ext.proxy.api.StructuredQuery<Order> {

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OrderID",
      type = "Edm.Int32",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.Integer getOrderID();

  void setOrderID(java.lang.Integer _orderID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OrderDate",
      type = "Edm.DateTimeOffset",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.sql.Timestamp getOrderDate();

  void setOrderDate(java.sql.Timestamp _orderDate);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ShelfLife",
      type = "Edm.Duration",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.math.BigDecimal getShelfLife();

  void setShelfLife(java.math.BigDecimal _shelfLife);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OrderShelfLifes",
      type = "Edm.Duration",
      nullable = true,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.math.BigDecimal> getOrderShelfLifes();

  void setOrderShelfLifes(org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.math.BigDecimal> _orderShelfLifes);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "LoggedInEmployee",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Employee",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Employees",
      containsTarget = false)
  Employee
      getLoggedInEmployee();

      void
      setLoggedInEmployee(
          Employee _loggedInEmployee);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "CustomerForOrder",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Customer",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Customers",
      containsTarget = false)
  Customer
      getCustomerForOrder();

      void
      setCustomerForOrder(
          Customer _customerForOrder);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "OrderDetails",
      type = "Microsoft.Test.OData.Services.ODataWCFService.OrderDetail",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "OrderDetails",
      containsTarget = false)
  OrderDetailCollection
      getOrderDetails();

      void
      setOrderDetails(
          OrderDetailCollection _orderDetails);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderDate",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderDateAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ShelfLife",
        type = "Edm.Duration")
    org.apache.olingo.ext.proxy.api.Annotatable getShelfLifeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderShelfLifes",
        type = "Edm.Duration")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderShelfLifesAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "LoggedInEmployee",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Employee")
    org.apache.olingo.ext.proxy.api.Annotatable getLoggedInEmployeeAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "CustomerForOrder",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Customer")
    org.apache.olingo.ext.proxy.api.Annotatable getCustomerForOrderAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "OrderDetails",
        type = "Microsoft.Test.OData.Services.ODataWCFService.OrderDetail")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderDetailsAnnotations();
  }

}
