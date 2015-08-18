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

package com.schibsted.triathlon.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Albert Manyà
 */
public class ConstraintModel {
    static List<String> VALID_OPERATORS = new ArrayList<String>() {{
        add("UNIQUE");
        add("CLUSTER");
        add("GROUP_BY");
        add("LIKE");
        add("UNLIKE");
    }};

    public String getField() {
        return field;
    }

    public String getOperator() {
        return operator;
    }

    public String getParameter() {
        return parameter;
    }

    private final String field;
    private final String operator;
    private final String parameter;

    private ConstraintModel(List<String> constraint) {
        String param = null;
        this.field = constraint.get(0);
        this.operator = constraint.get(1);
        if (constraint.size() == 3) {
            param = constraint.get(2);
        }
        this.parameter = param;
    }

    public static ConstraintModel createConstraintModel(List<String> constraint) throws Exception {
        if (!validateConstraint(constraint)) {
            throw new Exception("Invalid constraint");
        }
        return new ConstraintModel(constraint);
    }

    private static boolean validateConstraint(List<String> constraint) {
        return !(constraint.size() < 2 || constraint.size() > 3) && VALID_OPERATORS.contains(constraint.get(1));
    }
}
