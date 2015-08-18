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
import com.schibsted.triathlon.service.commands.MarathonCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Albert Manyà
 */
public class ClusterOperatorTest {
    private InstanceInfo ii;

    @After
    public void tearDown() throws Exception {
        InstanceInfoModel.clear();
    }

    @Before
    public void setUp() throws Exception {
        List<NetworkAddress> na = Collections.singletonList(new NetworkAddress("public",
                NetworkAddress.ProtocolType.IPv4, "192.168.0.1", "myHost"));
        ServicePort sp = new ServicePort("port", 8080, false, null);
        DataCenterInfo myDataCenter = new BasicDataCenterInfo("pluto-dc", na);
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
    public void testDeployExistingDataCenter() throws Exception {
        List<String> constraint = new ArrayList<String>() {{
            add("datacenter");
            add("CLUSTER");
            add("pluto-dc");
        }};
        TriathlonService triathlonService = Mockito.mock(TriathlonService.class);
        Marathon appDefinition = Mockito.mock(Marathon.class);
        ConstraintModel constraintModel = ConstraintModel.createConstraintModel(constraint);

        String appDefinitionJson = "";
        when(triathlonService.serializeMarathon(Mockito.anyObject())).thenReturn(appDefinitionJson);

        Operator cluster = new ClusterOperator(triathlonService, constraintModel);

        Operator spyCluster = Mockito.spy(cluster);
        doReturn(Observable.just(Unpooled.wrappedBuffer("TEST".getBytes()))).when(spyCluster).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());

        assertEquals("TEST", spyCluster.apply(appDefinition).toBlocking().toFuture().get().toString(Charset.defaultCharset()));
        verify(spyCluster, times(1)).getMarathonCommand(eq(ii), Mockito.anyString());
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeployNonExistingDataCenter() throws Exception {
        List<String> constraint = new ArrayList<String>() {{
            add("datacenter");
            add("CLUSTER");
            add("mars-dc");
        }};
        TriathlonService triathlonService = Mockito.mock(TriathlonService.class);
        Marathon appDefinition = Mockito.mock(Marathon.class);
        ConstraintModel constraintModel = ConstraintModel.createConstraintModel(constraint);

        String appDefinitionJson = "";
        when(triathlonService.serializeMarathon(Mockito.anyObject())).thenReturn(appDefinitionJson);

        Operator cluster = new ClusterOperator(triathlonService, constraintModel);

        Operator spyCluster = Mockito.spy(cluster);
        doReturn(Observable.just(Unpooled.wrappedBuffer("TEST".getBytes()))).when(spyCluster).getMarathonCommand(Mockito.any(InstanceInfo.class), Mockito.anyString());

        spyCluster.apply(appDefinition);
        verify(spyCluster, times(0)).getMarathonCommand(eq(ii), Mockito.anyString());
    }
}