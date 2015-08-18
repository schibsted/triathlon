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

package com.schibsted.triathlon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.schibsted.triathlon.model.generated.Marathon;
import io.netty.buffer.ByteBuf;
import rx.Observable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Methods for working with the message in Json.
 *
 * @author Albert Manyà
 */
public class TriathlonServiceImpl implements TriathlonService {
    public static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    /**
     * Parses the raw json from the Http request and converts it to {@link Marathon} POJOs.
     *
     * @param byteBufs an observable list of {@link ByteBuf} with the Json to be parsed
     * @return an observable {@link Marathon}
     */
    @Override
    public Observable<Marathon> parseJson(Observable<ByteBuf> byteBufs) {
        final Observable<String> chunks = byteBufs.map(content -> content.toString(StandardCharsets.UTF_8));
        return chunks.map(this::parseMarathonJson);
    }

    private Marathon parseMarathonJson(String jsonStr) {
        try {
            return objectMapper.readValue(jsonStr, Marathon.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Convert a {@link Marathon} object to Json.
     *
     * @param marathon
     * @return
     * @throws IOException
     */
    @Override
    public String serializeMarathon(Marathon marathon) throws IOException {
        FilterProvider filters = new SimpleFilterProvider().addFilter("filterConstraints",
                SimpleBeanPropertyFilter.serializeAllExcept("constraints"));

        String json = objectMapper.setFilterProvider(filters).writeValueAsString(marathon);
        return json;
    }
}
