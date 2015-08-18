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

import com.schibsted.triathlon.model.generated.Marathon;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by amanya on 31/07/15
 */
public class TriathlonServiceImplTest {
    @Test
    public void testParseJson() throws Exception {
        TriathlonService service = new TriathlonServiceImpl();
        Observable<ByteBuf> json = Observable.just(
                Unpooled.wrappedBuffer("{\"constraints\": [[\"hostname\", \"UNIQUE\"], [\"datacenter\", \"aws\"]]}"
                                .getBytes()
                )
        );

        Observable<Marathon> marathonObject = service.parseJson(json);
        marathonObject.subscribe(val -> {
            assertEquals(2, val.getConstraints().size());
        });
    }

    @Test
    public void testSerializeJson() throws Exception {
        String content = IOUtils.toString(
                this.getClass().getResourceAsStream("existing_datacenter.json"),
                "UTF-8"
        );

        ByteBuf buffer = Unpooled.copiedBuffer(content.getBytes());

        TriathlonService service = new TriathlonServiceImpl();
        Observable<Marathon> marathonObject = service.parseJson(Observable.just(buffer));

        String serializedJson = service.serializeMarathon(marathonObject.toBlocking().toFuture().get());
        assertThat(serializedJson, not(containsString("constraints")));
    }


}