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

package com.epam.reportportal.apache.http.impl.execchain;

import java.io.IOException;

import com.epam.reportportal.apache.http.Header;
import com.epam.reportportal.apache.http.HttpException;
import com.epam.reportportal.apache.http.client.HttpRequestRetryHandler;
import com.epam.reportportal.apache.http.conn.routing.HttpRoute;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.epam.reportportal.apache.http.annotation.Immutable;
import com.epam.reportportal.apache.http.client.NonRepeatableRequestException;
import com.epam.reportportal.apache.http.client.methods.CloseableHttpResponse;
import com.epam.reportportal.apache.http.client.methods.HttpExecutionAware;
import com.epam.reportportal.apache.http.client.methods.HttpRequestWrapper;
import com.epam.reportportal.apache.http.client.protocol.HttpClientContext;
import com.epam.reportportal.apache.http.util.Args;

/**
 * Request executor in the request execution chain that is responsible
 * for making a decision whether a request failed due to an I/O error
 * should be re-executed.
 * <p/>
 * Further responsibilities such as communication with the opposite
 * endpoint is delegated to the next executor in the request execution
 * chain.
 *
 * @since 4.3
 */
@Immutable
public class RetryExec implements ClientExecChain {

    private final Log log = LogFactory.getLog(getClass());

    private final ClientExecChain requestExecutor;
    private final HttpRequestRetryHandler retryHandler;

    public RetryExec(
            final ClientExecChain requestExecutor,
            final HttpRequestRetryHandler retryHandler) {
        Args.notNull(requestExecutor, "HTTP request executor");
        Args.notNull(retryHandler, "HTTP request retry handler");
        this.requestExecutor = requestExecutor;
        this.retryHandler = retryHandler;
    }

    public CloseableHttpResponse execute(
            final HttpRoute route,
            final HttpRequestWrapper request,
            final HttpClientContext context,
            final HttpExecutionAware execAware) throws IOException, HttpException {
        Args.notNull(route, "HTTP route");
        Args.notNull(request, "HTTP request");
        Args.notNull(context, "HTTP context");
        final Header[] origheaders = request.getAllHeaders();
        for (int execCount = 1;; execCount++) {
            try {
                return this.requestExecutor.execute(route, request, context, execAware);
            } catch (final IOException ex) {
                if (execAware != null && execAware.isAborted()) {
                    this.log.debug("Request has been aborted");
                    throw ex;
                }
                if (retryHandler.retryRequest(ex, execCount, context)) {
                    if (this.log.isInfoEnabled()) {
                        this.log.info("I/O exception ("+ ex.getClass().getName() +
                                ") caught when processing request: "
                                + ex.getMessage());
                    }
                    if (this.log.isDebugEnabled()) {
                        this.log.debug(ex.getMessage(), ex);
                    }
                    if (!Proxies.isRepeatable(request)) {
                        this.log.debug("Cannot retry non-repeatable request");
                        throw new NonRepeatableRequestException("Cannot retry request " +
                                "with a non-repeatable request entity", ex);
                    }
                    request.setHeaders(origheaders);
                    this.log.info("Retrying request");
                } else {
                    throw ex;
                }
            }
        }
    }

}
