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

package com.schibsted.triathlon.service;

import com.netflix.eureka2.interests.ChangeNotification;
import com.netflix.eureka2.registry.datacenter.BasicDataCenterInfo;
import com.netflix.eureka2.registry.datacenter.DataCenterInfo;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.registry.instance.NetworkAddress;
import com.netflix.eureka2.registry.instance.ServicePort;
import com.schibsted.triathlon.model.InstanceInfoModel;
import com.schibsted.triathlon.service.impl.TriathlonEndpointImpl;
import com.schibsted.triathlon.service.commands.MarathonCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;
import rx.schedulers.TestScheduler;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by amanya on 07/08/15
 */
public class TriathlonEndpointImplTest {

    /*
    private InstanceInfo ii;

    @Before
    public void setUp() throws Exception {
        List<NetworkAddress> na = Collections.singletonList(new NetworkAddress("public",
                NetworkAddress.ProtocolType.IPv4, "192.168.0.1", "myHost"));
        ServicePort sp = new ServicePort("port", 8080, false, null);
        DataCenterInfo myDataCenter = new BasicDataCenterInfo("TestDataCenter", na);
        InstanceInfo ii = new InstanceInfo.Builder()
                .withId("testDataCenterInfo")
                .withPorts(sp)
                .withDataCenterInfo(myDataCenter)
                .build();
        this.ii = ii;
        ChangeNotification<InstanceInfo> cn = new ChangeNotification<>(ChangeNotification.Kind.Add, ii);
        InstanceInfoModel.interestSubscriber(cn);
    }

    @Test
    public void testConstraintsWithAnExistingDataCenter() throws Exception {
        TestScheduler testScheduler = new TestScheduler();

        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        HttpServerResponse response = Mockito.mock(HttpServerResponse.class);

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("existing_datacenter.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());
        when(request.getContent()).thenReturn(Observable.just(buffer));

        MarathonCommand marathonCommand = Mockito.mock(MarathonCommand.class);

        TriathlonEndpointImpl endpoint = new TriathlonEndpointImpl();
        TriathlonEndpointImpl spyEndpoint = Mockito.spy(endpoint);
        doReturn(marathonCommand).when(spyEndpoint).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());
        when(response.close()).thenReturn(Observable.<Void>empty());
        spyEndpoint.postApps(request, response).observeOn(testScheduler).subscribe();

        verify(spyEndpoint, times(1)).getMarathonCommand(eq(ii), Mockito.anyString());
    }

    @Test
    public void testConstraintsWithoutAnExistingDataCenter() throws Exception {
        TestScheduler testScheduler = new TestScheduler();

        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        HttpServerResponse response = Mockito.mock(HttpServerResponse.class);

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("non_existing_datacenter.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());
        when(request.getContent()).thenReturn(Observable.just(buffer));

        MarathonCommand marathonCommand = Mockito.mock(MarathonCommand.class);

        TriathlonEndpointImpl endpoint = new TriathlonEndpointImpl();
        TriathlonEndpointImpl spyEndpoint = Mockito.spy(endpoint);
        doReturn(marathonCommand).when(spyEndpoint).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());
        when(response.close()).thenReturn(Observable.<Void>empty());
        spyEndpoint.postApps(request, response).observeOn(testScheduler).subscribe();

        verify(spyEndpoint, times(0)).getMarathonCommand(Mockito.anyObject(), Mockito.anyString());
    }

    @Test
    public void testNoConstraints() throws Exception {
        TestScheduler testScheduler = new TestScheduler();

        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        HttpServerResponse response = Mockito.mock(HttpServerResponse.class);

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("no_constraints.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());
        when(request.getContent()).thenReturn(Observable.just(buffer));

        MarathonCommand marathonCommand = Mockito.mock(MarathonCommand.class);

        TriathlonEndpointImpl endpoint = new TriathlonEndpointImpl();
        TriathlonEndpointImpl spyEndpoint = Mockito.spy(endpoint);
        doReturn(marathonCommand).when(spyEndpoint).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());
        when(response.close()).thenReturn(Observable.<Void>empty());
        spyEndpoint.postApps(request, response).observeOn(testScheduler).subscribe();

        verify(spyEndpoint, times(0)).getMarathonCommand(Mockito.anyObject(), Mockito.anyString());
    }

    @Test(expected=Exception.class)
    public void testInvalidConstraints() throws Exception {
        TestScheduler testScheduler = new TestScheduler();

        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        HttpServerResponse response = Mockito.mock(HttpServerResponse.class);

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("invalid_constraints.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());
        when(request.getContent()).thenReturn(Observable.just(buffer));

        MarathonCommand marathonCommand = Mockito.mock(MarathonCommand.class);

        TriathlonEndpointImpl endpoint = new TriathlonEndpointImpl();
        TriathlonEndpointImpl spyEndpoint = Mockito.spy(endpoint);
        doReturn(marathonCommand).when(spyEndpoint).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());
        when(response.close()).thenReturn(Observable.<Void>empty());
        spyEndpoint.postApps(request, response).observeOn(testScheduler).subscribe();

        verify(spyEndpoint, times(0)).getMarathonCommand(Mockito.anyObject(), Mockito.anyString());

    }

    */

}