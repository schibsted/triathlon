/*
 * Copyright (c) 2015 Schibsted Products and Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.schibsted.triathlon.service.commands;

import com.netflix.eureka2.registry.datacenter.BasicDataCenterInfo;
import com.netflix.eureka2.registry.datacenter.DataCenterInfo;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.registry.instance.NetworkAddress;
import com.netflix.eureka2.registry.instance.ServicePort;
import com.schibsted.triathlon.service.RxTestServer;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.protocol.http.AbstractHttpContentHolder;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import io.reactivex.netty.server.RxServer;
import netflix.karyon.transport.http.SimpleUriRouter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static rx.Observable.using;

/**
 * @author Albert Manyà
 */
public class MarathonCommandTest {

    private InstanceInfo ii;
    private static RxTestServer testServer = new RxTestServer(8088);

    @After
    public void tearDown() throws Exception {
        testServer.shutdown();
    }

    @Before
    public void setUp() throws Exception {
        List<NetworkAddress> na = Collections.singletonList(new NetworkAddress("private",
                NetworkAddress.ProtocolType.IPv4, "localhost", "myHost"));
        ServicePort sp = new ServicePort("port", 8088, false, null);
        DataCenterInfo myDataCenter = new BasicDataCenterInfo("TestDataCenter", na);

        InstanceInfo ii = new InstanceInfo.Builder()
                .withId("testDataCenterInfo")
                .withPorts(sp)
                .withDataCenterInfo(myDataCenter)
                .build();
        this.ii = ii;

        testServer.start();
    }

    @Test
    public void testConstruct() throws Exception {
        MarathonCommand cmd = new MarathonCommand(ii, "asd");
        String val = cmd.observe()
                .map(content -> content.toString(Charset.defaultCharset()))
                .toBlocking().single();
        System.out.println("VAL " + val);

        /*
        HttpClientRequest<ByteBuf> req = HttpClientRequest.createPost("/v2/apps");

        Observable<HttpClientResponse<ByteBuf>> client = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder("localhost", 8088)
                .build()
                .submit(req);
        client.flatMap(AbstractHttpContentHolder::getContent)
                .toBlocking().toFuture().get();
        */
    }

}