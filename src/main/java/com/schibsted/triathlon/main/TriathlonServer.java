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

package com.schibsted.triathlon.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.governator.annotations.Modules;
import com.schibsted.triathlon.common.health.HealthCheck;
import com.schibsted.triathlon.model.InstanceInfoModel;
import com.schibsted.triathlon.service.TriathlonModule;
import com.schibsted.triathlon.service.impl.TriathlonEndpointImpl;
import com.schibsted.triathlon.service.discovery.EurekaService;
import io.netty.buffer.ByteBuf;
import netflix.adminresources.resources.KaryonWebAdminModule;
import netflix.karyon.KaryonBootstrap;
import netflix.karyon.ShutdownModule;
import netflix.karyon.archaius.ArchaiusBootstrap;
import netflix.karyon.transport.http.KaryonHttpModule;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RxNetty server initialization
 */
@ArchaiusBootstrap
@KaryonBootstrap(name = "Triathlon", healthcheck = HealthCheck.class)
@Singleton
@Modules(include = {
        ShutdownModule.class,
        KaryonWebAdminModule.class,
        TriathlonServer.KaryonRxRouterModuleImpl.class
})
public interface TriathlonServer {
    class KaryonRxRouterModuleImpl extends KaryonHttpModule<ByteBuf, ByteBuf> {
        private static EurekaService eurekaService;

        private static final Logger logger = LoggerFactory.getLogger(HealthCheck.class);
        public static final int DEFAULT_PORT = 9090;

        public KaryonRxRouterModuleImpl() {
            super("httpServerTriathlon", ByteBuf.class, ByteBuf.class);

            Injector injector = Guice.createInjector(new TriathlonModule());
            eurekaService = injector.getInstance(EurekaService.class);
        }

        @Override
        protected void configureServer() {
            DynamicIntProperty httpPort = DynamicPropertyFactory.getInstance().getIntProperty("registration.httpPort", DEFAULT_PORT);

            TriathlonEndpointImpl triathlonEndpoint = new TriathlonEndpointImpl();
            HealthCheckEndpoint healthCheckEndpoint = new HealthCheckEndpoint(new HealthCheck());

            bindRouter().toInstance(new TriathlonRouter(healthCheckEndpoint, triathlonEndpoint));
            eurekaService.subscribeToInterest("marathon.*").subscribe(InstanceInfoModel::interestSubscriber);
            server().port(httpPort.get());
        }
    }
}