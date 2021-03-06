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

package com.epam.reportportal.apache.http.impl.cookie;

import java.util.Iterator;
import java.util.List;

import com.epam.reportportal.apache.http.impl.cookie.AbstractCookieSpec;
import org.junit.Assert;
import org.junit.Test;

import com.epam.reportportal.apache.http.Header;
import com.epam.reportportal.apache.http.cookie.Cookie;
import com.epam.reportportal.apache.http.cookie.CookieAttributeHandler;
import com.epam.reportportal.apache.http.cookie.CookieOrigin;
import com.epam.reportportal.apache.http.cookie.MalformedCookieException;
import com.epam.reportportal.apache.http.cookie.SetCookie;

public class TestAbstractCookieSpec {

    private static class DummyCookieSpec extends AbstractCookieSpec {

        public List<Header> formatCookies(final List<Cookie> cookies) {
            return null;
        }

        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }

        public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
            return null;
        }

        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }

        public int getVersion() {
            return 0;
        }

        public Header getVersionHeader() {
            return null;
        }

    }

    private static class DummyCookieAttribHandler implements CookieAttributeHandler {

        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }

        public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        }

        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }

    }

    @Test
    public void testSimpleRegisterAndGet() {
        final CookieAttributeHandler h1 = new DummyCookieAttribHandler();
        final CookieAttributeHandler h2 = new DummyCookieAttribHandler();

        final AbstractCookieSpec cookiespec = new DummyCookieSpec();
        cookiespec.registerAttribHandler("this", h1);
        cookiespec.registerAttribHandler("that", h2);
        cookiespec.registerAttribHandler("thistoo", h1);
        cookiespec.registerAttribHandler("thattoo", h2);

        Assert.assertTrue(h1 == cookiespec.getAttribHandler("this"));
        Assert.assertTrue(h2 == cookiespec.getAttribHandler("that"));
        Assert.assertTrue(h1 == cookiespec.getAttribHandler("thistoo"));
        Assert.assertTrue(h2 == cookiespec.getAttribHandler("thattoo"));

        final Iterator<CookieAttributeHandler> it = cookiespec.getAttribHandlers().iterator();
        Assert.assertNotNull(it.next());
        Assert.assertNotNull(it.next());
        Assert.assertNotNull(it.next());
        Assert.assertNotNull(it.next());
        Assert.assertFalse(it.hasNext());
    }

    @Test(expected=IllegalStateException.class)
    public void testInvalidHandler() {
        final CookieAttributeHandler h1 = new DummyCookieAttribHandler();
        final CookieAttributeHandler h2 = new DummyCookieAttribHandler();

        final AbstractCookieSpec cookiespec = new DummyCookieSpec();
        cookiespec.registerAttribHandler("this", h1);
        cookiespec.registerAttribHandler("that", h2);

        Assert.assertNull(cookiespec.findAttribHandler("whatever"));
        cookiespec.getAttribHandler("whatever");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBasicPathInvalidInput1() throws Exception {
        final AbstractCookieSpec cookiespec = new DummyCookieSpec();
        cookiespec.registerAttribHandler(null, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBasicPathInvalidInput2() throws Exception {
        final AbstractCookieSpec cookiespec = new DummyCookieSpec();
        cookiespec.registerAttribHandler("whatever", null);
    }

}
