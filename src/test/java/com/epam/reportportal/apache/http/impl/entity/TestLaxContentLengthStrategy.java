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

package com.epam.reportportal.apache.http.impl.entity;

import org.junit.Assert;
import org.junit.Test;

import com.epam.reportportal.apache.http.HttpMessage;
import com.epam.reportportal.apache.http.entity.ContentLengthStrategy;

public class TestLaxContentLengthStrategy {

    @Test
    public void testEntityWithTransferEncoding() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Transfer-Encoding", "identity, chunked");
        message.addHeader("Content-Length", "plain wrong");
        Assert.assertEquals(ContentLengthStrategy.CHUNKED, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithIdentityTransferEncoding() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Transfer-Encoding", "identity");
        message.addHeader("Content-Length", "plain wrong");
        Assert.assertEquals(ContentLengthStrategy.IDENTITY, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithUnsupportedTransferEncoding() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Transfer-Encoding", "whatever; param=value, chunked");
        message.addHeader("Content-Length", "plain wrong");
        Assert.assertEquals(ContentLengthStrategy.CHUNKED, lenStrategy.determineLength(message));
    }

    @Test
    public void testChunkedTransferEncodingMustBeLast() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Transfer-Encoding", "chunked, identity");
        message.addHeader("Content-Length", "plain wrong");
        Assert.assertEquals(ContentLengthStrategy.IDENTITY, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithContentLength() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Content-Length", "0");
        Assert.assertEquals(0, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithMultipleContentLength() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Content-Length", "0");
        message.addHeader("Content-Length", "0");
        message.addHeader("Content-Length", "1");
        Assert.assertEquals(1, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithMultipleContentLengthSomeWrong() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Content-Length", "1");
        message.addHeader("Content-Length", "yyy");
        message.addHeader("Content-Length", "xxx");
        Assert.assertEquals(1, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithMultipleContentLengthAllWrong() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Content-Length", "yyy");
        message.addHeader("Content-Length", "xxx");
        Assert.assertEquals(ContentLengthStrategy.IDENTITY, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityWithInvalidContentLength() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        message.addHeader("Content-Type", "unknown");
        message.addHeader("Content-Length", "xxx");
        Assert.assertEquals(ContentLengthStrategy.IDENTITY, lenStrategy.determineLength(message));
    }

    @Test
    public void testEntityNeitherContentLengthNorTransferEncoding() throws Exception {
        final ContentLengthStrategy lenStrategy = new LaxContentLengthStrategy();
        final HttpMessage message = new DummyHttpMessage();

        // lenient mode
        Assert.assertEquals(ContentLengthStrategy.IDENTITY, lenStrategy.determineLength(message));
    }

}

