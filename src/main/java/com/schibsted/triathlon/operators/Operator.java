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

import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.schibsted.triathlon.model.generated.Marathon;
import com.schibsted.triathlon.service.commands.MarathonCommand;
import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.util.Collection;

/**
 * @author Albert Manyà
 */
public interface Operator {
    public Observable<ByteBuf> apply(Marathon appDefinition);
    public Observable<ByteBuf> getMarathonCommand(InstanceInfo instanceInfo, String appDefinitionJson);
}
