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

package com.schibsted.triathlon.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.schibsted.triathlon.model.ConstraintModel;
import com.schibsted.triathlon.model.InstanceInfoModel;
import com.schibsted.triathlon.model.generated.Marathon;
import com.schibsted.triathlon.operators.Operator;
import com.schibsted.triathlon.operators.OperatorFactory;
import com.schibsted.triathlon.service.TriathlonModule;
import com.schibsted.triathlon.service.TriathlonService;
import com.schibsted.triathlon.service.api.TriathlonEndpoint;
import com.schibsted.triathlon.service.commands.MarathonCommand;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

/**
 * Endpoint class for the Triathlon server.
 *
 * @author Albert Manyà
 */
public class TriathlonEndpointImpl implements TriathlonEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriathlonEndpointImpl.class);
    private static TriathlonService triathlonService;

    public TriathlonEndpointImpl() {
        Injector injector = Guice.createInjector(new TriathlonModule());
        triathlonService = injector.getInstance(TriathlonService.class);
    }

    /**
     * This endpoint will forward the post data to the selected marathon server.
     *
     * TODO: Move logic from here
     *
     * @param request
     * @param response
     * @return response to be send to the caller
     */
    @Override
    public Observable<Void> postApps(HttpServerRequest<ByteBuf> request, HttpServerResponse<ByteBuf> response) {
        return triathlonService.parseJson(request.getContent())
                .flatMap(this::matchDataCenter)
                .flatMap(content -> {
                    response.write(content);
                    return response.close();
                })
                .onErrorResumeNext(throwable -> {
                    LOGGER.info("Service ERROR");
                    response.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                    return response.close();
                })
                .doOnCompleted(() -> response.close(true));
    }

    /**
     * Try to find the datacenter specified in the field `datacenter` of the constraints on the info obtained
     * from the marathons subscribed on Eureka2.
     * If a match is found, build and return {@link MarathonCommand} to
     * @param appDefinition object used to serialize the json document
     * @return
     */
    private Observable<ByteBuf> matchDataCenter(Marathon appDefinition) {
        try {
            return Observable.from(appDefinition.getConstraints())
                    .map(constraint -> {
                        ConstraintModel cm = null;
                        try {
                            cm = ConstraintModel.createConstraintModel(constraint);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return cm;
                    })
                    .filter(constraint -> constraint.getField().equals("datacenter"))
                    .flatMap(constraint -> {
                        return this.runConstraint(constraint, appDefinition);
                    });
        } catch (Exception e) {
            LOGGER.error("matchDataCenter error: ", e);
            return Observable.error(e);
        }
    }

    private Observable<ByteBuf> runConstraint(ConstraintModel constraint, Marathon appDefinition) {
        return OperatorFactory.build(constraint).apply(appDefinition);
    }
}
