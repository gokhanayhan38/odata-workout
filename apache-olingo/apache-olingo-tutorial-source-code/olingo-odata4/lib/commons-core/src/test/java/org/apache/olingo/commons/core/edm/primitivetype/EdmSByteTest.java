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
package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmSByteTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte);

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(Uint7.getInstance()));
  }

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("127", instance.toUriLiteral("127"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("127", instance.fromUriLiteral("127"));
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("0", instance.valueToString(0, null, null, null, null, null));
    assertEquals("8", instance.valueToString((byte) 8, null, null, null, null, null));
    assertEquals("16", instance.valueToString((short) 16, null, null, null, null, null));
    assertEquals("32", instance.valueToString(Integer.valueOf(32), null, null, null, null, null));
    assertEquals("64", instance.valueToString(64L, null, null, null, null, null));
    assertEquals("-128", instance.valueToString(BigInteger.valueOf(Byte.MIN_VALUE), null, null, null, null, null));

    expectContentErrorInValueToString(instance, -129);
    expectContentErrorInValueToString(instance, 128);
    expectContentErrorInValueToString(instance, BigInteger.valueOf(128));

    expectTypeErrorInValueToString(instance, 'A');
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(Byte.valueOf((byte) 1), instance.valueOfString("1", null, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) -2), instance.valueOfString("-2", null, null, null, null, null, Short.class));
    assertEquals(Byte.valueOf((byte) 127), instance.valueOfString("127", null, null, null, null, null, Byte.class));
    assertEquals(Byte.valueOf((byte) -128), instance.valueOfString("-128", null, null, null, null, null, Byte.class));
    assertEquals(Integer.valueOf(0), instance.valueOfString("0", null, null, null, null, null, Integer.class));
    assertEquals(Long.valueOf(0), instance.valueOfString("0", null, null, null, null, null, Long.class));
    assertEquals(BigInteger.TEN, instance.valueOfString("10", null, null, null, null, null, BigInteger.class));

    expectContentErrorInValueOfString(instance, "128");
    expectContentErrorInValueOfString(instance, "-129");
    expectContentErrorInValueOfString(instance, "1.0");

    expectTypeErrorInValueOfString(instance, "1");
  }
}
