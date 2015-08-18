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

package com.schibsted.triathlon.model;

import com.netflix.eureka2.interests.ChangeNotification;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Store the subscription information from the Eureka2 interest.
 * TODO: Find a better way than a static class
 *
 * @author Albert Manyà
 */
public class InstanceInfoModel {
    private static HashMap<String, InstanceInfo> instanceInfo = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceInfoModel.class);

    public static void clear() {
        // Added for cleaning the class for tests
        // p.s. god forgive me
        instanceInfo = new HashMap<>();
    }

    /**
     * Stores {@link ChangeNotification} from Eureka2.
     *
     * @param cn
     */
    public synchronized static void interestSubscriber(ChangeNotification<InstanceInfo> cn) {
        String id = cn.getData().getId();

        instanceInfo.put(id, cn.getData());
    }

    /**
     * Return the stored information.
     *
     * @return
     */
    public synchronized static HashMap<String, InstanceInfo> getInstanceInfo() {
        return instanceInfo;
    }

}
