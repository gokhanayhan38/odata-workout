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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumMember;

public class EdmMemberImpl extends AbstractEdmNamed implements EdmMember {

  private final FullQualifiedName enumFQN;
  private final CsdlEnumMember member;

  public EdmMemberImpl(final Edm edm, final FullQualifiedName enumFQN, final CsdlEnumMember member) {
    super(edm, member.getName(), member);
    this.enumFQN = enumFQN;
    this.member = member;
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.Member;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return enumFQN;
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

  @Override
  public String getValue() {
    return member.getValue();
  }
}
