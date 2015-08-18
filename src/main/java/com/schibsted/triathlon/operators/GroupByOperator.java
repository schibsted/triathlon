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
import com.schibsted.triathlon.model.ConstraintModel;
import com.schibsted.triathlon.model.InstanceInfoModel;
import com.schibsted.triathlon.model.generated.Marathon;
import com.schibsted.triathlon.service.TriathlonService;
import com.schibsted.triathlon.service.commands.MarathonCommand;
import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Albert Manyà
 */
public class GroupByOperator implements Operator {

    private final TriathlonService triathlonService;
    private final ConstraintModel constraint;

    public GroupByOperator(TriathlonService triathlonService, ConstraintModel constraint) {
        this.triathlonService = triathlonService;
        this.constraint = constraint;
    }

    @Override
    public Observable<ByteBuf> apply(Marathon appDefinition) {
        try {
            int desiredInstances = appDefinition.getInstances();

            List<Integer> instancesToDeploy  = new ArrayList<>();
            Map<String, InstanceInfo> dataCenters = Observable.from(InstanceInfoModel.getInstanceInfo().values())
                    .take(desiredInstances)
                    .toMap(ii -> {
                        return ii.getDataCenterInfo().getName();
                    })
                    .toBlocking()
                    .toFuture()
                    .get();

            int numDataCenters = dataCenters.keySet().size();

            int remainder = desiredInstances % numDataCenters;

            instancesToDeploy.addAll(dataCenters.keySet()
                    .stream()
                    .map(dataCenter -> desiredInstances / numDataCenters)
                    .collect(Collectors.toList()));

            for (int n = 0; n < remainder; n++) {
                instancesToDeploy.set(n, instancesToDeploy.get(n) + 1);
            }

            List<List<String>> commands = new ArrayList<>();

            int n = 0;
            for (String dataCenter : dataCenters.keySet()) {
                appDefinition.setInstances(instancesToDeploy.get(n));
                String json = triathlonService.serializeMarathon(appDefinition);
                List<String> item = new ArrayList<>();
                item.add(dataCenter);
                item.add(json);
                commands.add(item);
                n++;
            }

            return Observable.from(commands)
                    .flatMap(cmd -> {
                        InstanceInfo ii = dataCenters.get(cmd.get(0));
                        return getMarathonCommand(ii, cmd.get(1));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Observable<ByteBuf> getMarathonCommand(InstanceInfo instanceInfo, String appDefinitionJson) {
        return new MarathonCommand(instanceInfo, appDefinitionJson).observe();
    }
}
