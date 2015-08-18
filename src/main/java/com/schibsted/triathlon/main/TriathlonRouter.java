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

import com.schibsted.triathlon.service.impl.TriathlonEndpointImpl;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.RequestHandler;
import netflix.karyon.transport.http.SimpleUriRouter;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import rx.Observable;

/**
 * A {@link RequestHandler} implementation for Triathlon.
 */
public class TriathlonRouter implements RequestHandler<ByteBuf, ByteBuf> {
    private final SimpleUriRouter<ByteBuf, ByteBuf> delegate;

    public TriathlonRouter(HealthCheckEndpoint healthCheckEndpoint, TriathlonEndpointImpl triathlonEndpoint) {

        delegate = new SimpleUriRouter<>();
        delegate.addUri("/healthcheck", healthCheckEndpoint)
                .addUri("/v2/apps",
                        (request, response) -> {
                            Observable<Void> result = Observable.defer(response::close);
                            if (request.getHttpMethod() == HttpMethod.POST) {
                                result = triathlonEndpoint.postApps(request, response);
                            }
                            return result;
                        });
    }

    @Override
    public Observable<Void> handle(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return delegate.handle(request, response).doOnCompleted(response::close);
    }
}
