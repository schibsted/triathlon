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
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import io.netty.buffer.ByteBuf;
import rx.Observable;

/**
 * This is an implementation of a {@link HystrixObservableCommand} to forward the request to the selected marathon
 * server.
 *
 * @author Albert Manyà
 */
public class MarathonCommand extends HystrixObservableCommand<ByteBuf> {

    private final InstanceInfo marathonInstance;
    private final String message;

    /**
     * Create the {@link HystrixObservableCommand} with info about the marathon instance we need to connect.
     *
     * @param marathonInstance data about the instance which is running marathon such as ip and port
     * @param message          the json message to send to marathon
     */
    public MarathonCommand(InstanceInfo marathonInstance, String message) {
        super(HystrixCommandGroupKey.Factory.asKey("MarathonCommand"));
        this.marathonInstance = marathonInstance;
        this.message = message;
    }

    @Override
    protected Observable<ByteBuf> construct() {
        return new MarathonClient(marathonInstance)
                .postMessage(message)
                .flatMap(response -> response.getContent());
    }


}
