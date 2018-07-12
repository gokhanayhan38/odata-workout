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
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;

// CHECKSTYLE:ON (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;

public interface PaymentInstrumentComposableInvoker
    extends
    org.apache.olingo.ext.proxy.api.StructuredComposableInvoker<PaymentInstrument, PaymentInstrument.Operations>
{

  @Override
  PaymentInstrumentComposableInvoker select(String... select);

  @Override
  PaymentInstrumentComposableInvoker expand(String... expand);

  @Key
  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "PaymentInstrumentID",
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
  java.lang.Integer getPaymentInstrumentID();

  void setPaymentInstrumentID(java.lang.Integer _paymentInstrumentID);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "FriendlyName",
      type = "Edm.String",
      nullable = false,
      defaultValue = "",
      maxLenght = Integer.MAX_VALUE,
      fixedLenght = false,
      precision = 0,
      scale = 0,
      unicode = true,
      collation = "",
      srid = "")
  java.lang.String getFriendlyName();

  void setFriendlyName(java.lang.String _friendlyName);

  @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CreatedDate",
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
  java.sql.Timestamp getCreatedDate();

  void setCreatedDate(java.sql.Timestamp _createdDate);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "TheStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getTheStoredPI();

      void
      setTheStoredPI(
          StoredPI _theStoredPI);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "BackupStoredPI",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = false)
  StoredPI
      getBackupStoredPI();

      void
      setBackupStoredPI(
          StoredPI _backupStoredPI);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "BillingStatements",
      type = "Microsoft.Test.OData.Services.ODataWCFService.StoredPI",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "StoredPIs",
      containsTarget = true)
  PaymentInstrument.BillingStatements
      getBillingStatements();

      void
      setBillingStatements(
          PaymentInstrument.BillingStatements _billingStatements);

  @org.apache.olingo.ext.proxy.api.annotations.EntitySet(name = "BillingStatements", contained = true)
  interface BillingStatements
      extends
      org.apache.olingo.ext.proxy.api.EntitySet<Statement, StatementCollection>,
      org.apache.olingo.ext.proxy.api.StructuredCollectionQuery<BillingStatements>,
  AbstractEntitySet<Statement, java.lang.Integer, StatementCollection> {
    // No additional methods needed for now.
  }

}
