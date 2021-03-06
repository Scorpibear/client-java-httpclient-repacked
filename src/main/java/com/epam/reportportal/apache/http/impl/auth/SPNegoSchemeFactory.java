/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.epam.reportportal.apache.http.impl.auth;

import com.epam.reportportal.apache.http.annotation.Immutable;
import com.epam.reportportal.apache.http.auth.AuthScheme;
import com.epam.reportportal.apache.http.auth.AuthSchemeFactory;
import com.epam.reportportal.apache.http.auth.AuthSchemeProvider;
import com.epam.reportportal.apache.http.params.HttpParams;
import com.epam.reportportal.apache.http.protocol.HttpContext;

/**
 * {@link AuthSchemeProvider} implementation that creates and initializes
 * {@link SPNegoScheme} instances.
 *
 * @since 4.2
 */
@Immutable
@SuppressWarnings("deprecation")
public class SPNegoSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider {

    private final boolean stripPort;

    public SPNegoSchemeFactory(final boolean stripPort) {
        super();
        this.stripPort = stripPort;
    }

    public SPNegoSchemeFactory() {
        this(false);
    }

    public boolean isStripPort() {
        return stripPort;
    }

    public AuthScheme newInstance(final HttpParams params) {
        return new SPNegoScheme(this.stripPort);
    }

    public AuthScheme create(final HttpContext context) {
        return new SPNegoScheme(this.stripPort);
    }

}
