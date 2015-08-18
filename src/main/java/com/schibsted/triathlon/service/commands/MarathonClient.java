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

import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.registry.instance.NetworkAddress;
import io.netty.buffer.ByteBuf;
import io.netty.handler.logging.LogLevel;
import io.reactivex.netty.RxNetty;
import io.reactivex.netty.channel.StringTransformer;
import io.reactivex.netty.pipeline.PipelineConfigurator;
import io.reactivex.netty.pipeline.PipelineConfigurators;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import rx.Observable;

/**
 * This is an implementation of an RxNetty Http client intended to be used with {@link MarathonCommand}.
 *
 * @author Albert Manyà
 */
public class MarathonClient {
    private int port;
    private NetworkAddress networkAddress;

    /**
     * Create the client using the {@link InstanceInfo} data.
     *
     * @param instanceInfo data about the instance which is running marathon such as ip and port
     */
    public MarathonClient(InstanceInfo instanceInfo) {
        this.networkAddress = Observable.from(instanceInfo.getDataCenterInfo().getAddresses())
                .filter(address -> address.getProtocolType().equals(NetworkAddress.ProtocolType.IPv4))
                .filter(address -> address.getLabel().equals("private"))
                .first()
                .map(item -> item)
                .toBlocking()
                .single();


        this.port = Observable.from(instanceInfo.getPorts())
                .first()
                .toBlocking()
                .single()
                .getPort();
    }

    public Observable<HttpClientResponse<ByteBuf>> postMessage(String message) {
        PipelineConfigurator<HttpClientResponse<ByteBuf>, HttpClientRequest<ByteBuf>> pipelineConfigurator
                = PipelineConfigurators.httpClientConfigurator();

        HttpClient<ByteBuf, ByteBuf> client = RxNetty.<ByteBuf, ByteBuf>newHttpClientBuilder(networkAddress.getIpAddress(), port)
                .pipelineConfigurator(pipelineConfigurator)
                .enableWireLogging(LogLevel.ERROR).build();

        HttpClientRequest<ByteBuf> request = HttpClientRequest.createPost("/v2/apps");
        request.withRawContentSource(Observable.just(message), StringTransformer.DEFAULT_INSTANCE);
        request.withHeader("Content-Type", "application/json");
        return client.submit(request);
    }
}
