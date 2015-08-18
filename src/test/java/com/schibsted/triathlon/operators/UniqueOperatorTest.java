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

package com.schibsted.triathlon.operators;

import com.netflix.eureka2.interests.ChangeNotification;
import com.netflix.eureka2.registry.datacenter.BasicDataCenterInfo;
import com.netflix.eureka2.registry.datacenter.DataCenterInfo;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.registry.instance.NetworkAddress;
import com.netflix.eureka2.registry.instance.ServicePort;
import com.schibsted.triathlon.model.ConstraintModel;
import com.schibsted.triathlon.model.InstanceInfoModel;
import com.schibsted.triathlon.model.generated.Marathon;
import com.schibsted.triathlon.service.TriathlonService;
import com.schibsted.triathlon.service.TriathlonServiceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Albert Manyà
 */
public class UniqueOperatorTest {

    @Before
    public void setUp() throws Exception {
    }

    private void initializeInstanceInfos(int count) {
        for (int n = 0; n < count; n++) {
            List<NetworkAddress> na = Collections.singletonList(new NetworkAddress("public",
                    NetworkAddress.ProtocolType.IPv4, "192.168.0." + Integer.toString(n + 1),
                    "myHost-" + Integer.toString(n)));
            DataCenterInfo dc = new BasicDataCenterInfo("datacenter-" + Integer.toString(n), na);
            InstanceInfo ii = new InstanceInfo.Builder()
                    .withId("instance-info-id-" + Integer.toString(n))
                    .withDataCenterInfo(dc)
                    .build();

            ChangeNotification<InstanceInfo> cn = new ChangeNotification<>(ChangeNotification.Kind.Add, ii);
            InstanceInfoModel.interestSubscriber(cn);
        }
    }

    @After
    public void tearDown() throws Exception {
        InstanceInfoModel.clear();
    }

    @Test
    public void testDeployFourInstancestoFourDataCenters() throws Exception {
        initializeInstanceInfos(4);
        List<String> constraint = new ArrayList<String>() {{
            add("datacenter");
            add("UNIQUE");
        }};
        TriathlonService triathlonService = new TriathlonServiceImpl();

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("unique_operator_1.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());

        Marathon appDefinition = triathlonService.parseJson(Observable.just(buffer)).toBlocking().toFuture().get();
        appDefinition.setInstances(5);

        ConstraintModel constraintModel = ConstraintModel.createConstraintModel(constraint);

        Operator cluster = new UniqueOperator(triathlonService, constraintModel);

        Operator spyCluster = Mockito.spy(cluster);
        doReturn(Observable.just(Unpooled.wrappedBuffer("TEST".getBytes()))).when(spyCluster).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());

        when(spyCluster.getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Observable<ByteBuf> val = Observable.just(Unpooled.wrappedBuffer(((String) args[1]).getBytes()));
            Marathon appDef = triathlonService.parseJson(val).toBlocking().toFuture().get();
            assertEquals(Integer.valueOf(1), appDef.getInstances());
            return val;
        });

        spyCluster.apply(appDefinition).subscribe();
        verify(spyCluster, times(4)).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());
    }

    @Test
    public void testDeployFourInstancestoTwoDataCenters() throws Exception {
        initializeInstanceInfos(2);

        List<String> constraint = new ArrayList<String>() {{
            add("datacenter");
            add("UNIQUE");
        }};
        TriathlonService triathlonService = new TriathlonServiceImpl();

        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("unique_operator_1.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());

        Marathon appDefinition = triathlonService.parseJson(Observable.just(buffer)).toBlocking().toFuture().get();
        appDefinition.setInstances(5);

        ConstraintModel constraintModel = ConstraintModel.createConstraintModel(constraint);

        Operator cluster = new UniqueOperator(triathlonService, constraintModel);

        Operator spyCluster = Mockito.spy(cluster);
        doReturn(Observable.just(Unpooled.wrappedBuffer("TEST".getBytes()))).when(spyCluster).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());

        when(spyCluster.getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString())).thenAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            Observable<ByteBuf> val = Observable.just(Unpooled.wrappedBuffer(((String) args[1]).getBytes()));
            Marathon appDef = triathlonService.parseJson(val).toBlocking().toFuture().get();
            assertEquals(Integer.valueOf(1), appDef.getInstances());
            return val;
        });

        spyCluster.apply(appDefinition).subscribe();
        verify(spyCluster, times(2)).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());

    }
}