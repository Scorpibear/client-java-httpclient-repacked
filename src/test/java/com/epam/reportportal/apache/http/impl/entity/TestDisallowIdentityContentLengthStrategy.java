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
import org.mockito.Mockito;

import com.epam.reportportal.apache.http.HttpMessage;
import com.epam.reportportal.apache.http.ProtocolException;
import com.epam.reportportal.apache.http.entity.ContentLengthStrategy;

public class TestDisallowIdentityContentLengthStrategy {

    @Test
    public void testZeroLength() throws Exception {
        final ContentLengthStrategy strat1 = Mockito.mock(ContentLengthStrategy.class);
        final HttpMessage message = new DummyHttpMessage();
        Mockito.when(strat1.determineLength(message)).thenReturn(0L);
        final DisallowIdentityContentLengthStrategy strat2 = new DisallowIdentityContentLengthStrategy(strat1);
        Assert.assertEquals(0L, strat2.determineLength(message));
    }

    @Test(expected=ProtocolException.class)
    public void testIdentity() throws Exception {
        final ContentLengthStrategy strat1 = Mockito.mock(ContentLengthStrategy.class);
        final HttpMessage message = new DummyHttpMessage();
        Mockito.when(strat1.determineLength(message)).thenReturn((long) ContentLengthStrategy.IDENTITY);
        final DisallowIdentityContentLengthStrategy strat2 = new DisallowIdentityContentLengthStrategy(strat1);
        strat2.determineLength(message);
    }

}

