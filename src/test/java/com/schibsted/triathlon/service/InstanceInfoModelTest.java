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
import com.schibsted.triathlon.model.InstanceInfoModel;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Created by amanya on 07/08/15
 */
public class InstanceInfoModelTest {

    @Test
    public void testInterestSubscriber() throws Exception {
        List<NetworkAddress> na = Collections.singletonList(new NetworkAddress("public",
                NetworkAddress.ProtocolType.IPv4, "192.168.0.1", "myHost"));
        DataCenterInfo myDataCenter = new BasicDataCenterInfo("TestDataCenter", na);
        InstanceInfo ii = new InstanceInfo.Builder()
                .withId("testDataCenterInfo")
                .withDataCenterInfo(myDataCenter)
                .build();
        ChangeNotification<InstanceInfo> cn = new ChangeNotification<>(ChangeNotification.Kind.Add, ii);
        InstanceInfoModel.interestSubscriber(cn);
        assert(InstanceInfoModel.getInstanceInfo().values().contains(cn.getData()));
    }

}