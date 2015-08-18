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
package com.schibsted.triathlon.common.health;

import com.google.inject.Singleton;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import netflix.karyon.health.HealthCheckHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Singleton
public class HealthCheck implements HealthCheckHandler {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheck.class);

    @PostConstruct
    public void init() {
        logger.info("Health check initialized.");
    }

    @Override
    public int getStatus() {
        logger.info("Health check invoked.");

        return new ServiceCheck().isUp() ? 200 : 418;
    }

    public static class ServiceCheck {
        public static final int DEFAULT_CHECK_TIMEOUT = 5000;
        public static final int DEFAULT_CHECK_INTERVAL = 30000;
        public static final int DEFAULT_APP_PORT = 8080;

        private Logger logger = LoggerFactory.getLogger(getClass().getName());

        protected final String appHost;
        protected final int appPort;
        protected final String appHealthcheckPath;
        protected final boolean appPortSecure;
        protected final Integer checkTimeout;

        public ServiceCheck() {
            DynamicStringProperty appHost = DynamicPropertyFactory.getInstance().getStringProperty("registration.appHost", "localhost");
            DynamicIntProperty appPort = DynamicPropertyFactory.getInstance().getIntProperty("registration.appPort", DEFAULT_APP_PORT);
            DynamicStringProperty appHealthcheckPath = DynamicPropertyFactory.getInstance().getStringProperty("registration.appHealthcheckPatht", "/");
            DynamicBooleanProperty appPortSecure = DynamicPropertyFactory.getInstance().getBooleanProperty("registration.appPortSecure", false);
            DynamicIntProperty checkTimeout = DynamicPropertyFactory.getInstance().getIntProperty("registration.checkTimeout", DEFAULT_CHECK_TIMEOUT);

            this.appHost = appHost.get();
            this.appPort = appPort.get();
            this.appHealthcheckPath = appHealthcheckPath.get();
            this.appPortSecure = appPortSecure.get();
            this.checkTimeout = checkTimeout.get();
        }

        public boolean isUp() {
            logger.info("Checking service availability");
            String protocol = appPortSecure ? "https://" : "http://";
            HttpClientResponse<ByteBuf> resp;
            try {
                resp = getResponse(String.format("%s%s:%d%s", protocol, appHost, appPort, appHealthcheckPath));
            } catch (Exception e) {
                logger.error(e.toString());
                return false;
            }

            return resp.getStatus().code() == HttpResponseStatus.OK.code();
        }

        private HttpClientResponse<ByteBuf> getResponse(String serviceUrl) throws MalformedURLException, InterruptedException, ExecutionException, TimeoutException {
            String host, path;
            int port;

            URL url = new URL(serviceUrl);
            host = url.getHost();
            port = url.getPort();
            path = url.getPath();
            System.out.println(url);

            HttpClient<ByteBuf, ByteBuf> httpClient = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder(host, port)
                    .pipelineConfigurator(PipelineConfigurators.<ByteBuf, ByteBuf>httpClientConfigurator())
                    .build();
            return httpClient.submit(HttpClientRequest.createGet(path)).toBlocking().toFuture().get(checkTimeout, TimeUnit.MILLISECONDS);
        }
    }
}
