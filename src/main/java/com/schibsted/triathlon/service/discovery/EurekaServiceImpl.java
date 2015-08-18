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

package com.schibsted.triathlon.service.discovery;

import com.netflix.config.DynamicPropertyFactory;
import com.netflix.eureka2.client.EurekaInterestClient;
import com.netflix.eureka2.client.EurekaRegistrationClient;
import com.netflix.eureka2.client.Eurekas;
import com.netflix.eureka2.client.resolver.ServerResolver;
import com.netflix.eureka2.interests.ChangeNotification;
import com.netflix.eureka2.interests.Interest;
import com.netflix.eureka2.registry.datacenter.BasicDataCenterInfo;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.schibsted.triathlon.service.impl.TriathlonEndpointImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

import static com.netflix.eureka2.client.resolver.ServerResolvers.fromEureka;
import static com.netflix.eureka2.client.resolver.ServerResolvers.fromHostname;
import static com.netflix.eureka2.interests.Interests.forApplications;
import static com.netflix.eureka2.interests.Interests.forVips;

/**
 * Register with Eureka2 and subscribes to interest groups to get notifications about marathon instances.
 *
 * @author Albert Manyà
 */
public class EurekaServiceImpl implements EurekaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriathlonEndpointImpl.class);

    public static final InstanceInfo TRIATHLON = new InstanceInfo.Builder()
            .withId("triathlon")
            .withApp("Triathlon")
            .withAppGroup("Triathlon_1")
            .withStatus(InstanceInfo.Status.UP)
            .withDataCenterInfo(BasicDataCenterInfo.fromSystemData())
            .build();

    private final String writeServerHostname;
    private final int writeServerRegistrationPort;
    private final int writeServerInterestPort;
    private final String readServerVip;

    private Subscription subscription;
    EurekaRegistrationClient registrationClient;
    EurekaInterestClient interestClient;

    /**
     * Register the application with Eureka2
     * TODO: Remove this logic from the constructor
     */
    public EurekaServiceImpl() {
        writeServerHostname = DynamicPropertyFactory.getInstance().getStringProperty("triathlon.writeServerHostname", "localhost").get();
        writeServerRegistrationPort = DynamicPropertyFactory.getInstance().getIntProperty("triathlon.writeServerRegistrationPort", 12102).get();
        writeServerInterestPort = DynamicPropertyFactory.getInstance().getIntProperty("triathlon.writeServerInterestPort", 12103).get();
        readServerVip = DynamicPropertyFactory.getInstance().getStringProperty("triathlon.readServerVip", "eureka-read-cluster").get();

        registrationClient = Eurekas.newRegistrationClientBuilder()
                .withServerResolver(fromHostname(writeServerHostname).withPort(writeServerRegistrationPort))
                .build();

        BehaviorSubject<InstanceInfo> infoSubject = BehaviorSubject.create();
        subscription = registrationClient.register(infoSubject).subscribe();

        LOGGER.info("Registering application");
        infoSubject.onNext(TRIATHLON);

        ServerResolver interestClientResolver =
                fromEureka(
                        fromHostname(writeServerHostname).withPort(writeServerInterestPort)
                ).forInterest(forVips(readServerVip));

        interestClient = Eurekas.newInterestClientBuilder()
                .withServerResolver(interestClientResolver)
                .build();
    }

    /**
     * Perform some cleanup tasks.
     * TODO: Find a way to run on server shutdown
     */
    @Override
    public void shutdown() {
        subscription.unsubscribe();

        // Terminate both clients.
        LOGGER.info("Shutting down clients");
        registrationClient.shutdown();
        interestClient.shutdown();

    }

    /**
     * Subscribe to an Eureka2 interest
     *
     * @param interest a regular expression for the interest (i.e. "marathon.*")
     * @return
     */
    @Override
    public Observable<ChangeNotification<InstanceInfo>> subscribeToInterest(String interest) {
        LOGGER.info("Subscribing to interest: " + interest);
        return interestClient.forInterest(forApplications(Interest.Operator.Like, interest))
                .filter(cn -> cn.getKind().equals(ChangeNotification.Kind.Add));
    }
}
