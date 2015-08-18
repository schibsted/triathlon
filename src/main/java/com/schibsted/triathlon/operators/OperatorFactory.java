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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.schibsted.triathlon.model.ConstraintModel;
import com.schibsted.triathlon.service.TriathlonModule;
import com.schibsted.triathlon.service.TriathlonService;

/**
 * @author Albert Manyà
 */
public class OperatorFactory {
    private static TriathlonService triathlonService;

    public static Operator build(ConstraintModel constraint) {
        Injector injector = Guice.createInjector(new TriathlonModule());
        triathlonService = injector.getInstance(TriathlonService.class);

        switch(constraint.getOperator()) {
            case "UNIQUE":
                return new UniqueOperator(triathlonService, constraint);
            case "CLUSTER":
                return new ClusterOperator(triathlonService, constraint);
            case "GROUP_BY":
                return new GroupByOperator(triathlonService, constraint);
            case "LIKE":
                return new LikeOperator(triathlonService, constraint);
            case "UNLIKE":
                return new UnlikeOperator(triathlonService, constraint);
            default:
                return null;
        }
    }
}
