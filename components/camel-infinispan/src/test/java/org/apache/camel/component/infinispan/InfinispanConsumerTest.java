/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.infinispan;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class InfinispanConsumerTest extends InfinispanTestSupport {

    @EndpointInject(uri = "mock:result")
    private MockEndpoint mockResult;

    @Test
    public void consumerReceivedPreAndPostEntryCreatedEventNotifications() throws Exception {
        mockResult.expectedMessageCount(2);

        mockResult.message(0).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_CREATED");
        mockResult.message(0).outHeader(InfinispanConstants.IS_PRE).isEqualTo(true);
        mockResult.message(0).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockResult.message(0).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        mockResult.message(1).outHeader(InfinispanConstants.EVENT_TYPE).isEqualTo("CACHE_ENTRY_CREATED");
        mockResult.message(1).outHeader(InfinispanConstants.IS_PRE).isEqualTo(false);
        mockResult.message(1).outHeader(InfinispanConstants.CACHE_NAME).isNotNull();
        mockResult.message(1).outHeader(InfinispanConstants.KEY).isEqualTo(KEY_ONE);

        currentCache().put(KEY_ONE, VALUE_ONE);
        mockResult.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("infinispan://localhost?cacheContainer=#cacheContainer&sync=false&eventTypes=CACHE_ENTRY_CREATED")
                        .to("mock:result");
            }
        };
    }
}

