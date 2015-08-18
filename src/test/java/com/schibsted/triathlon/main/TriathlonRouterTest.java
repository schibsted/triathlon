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

import com.google.common.collect.Lists;
import com.schibsted.triathlon.service.impl.TriathlonEndpointImpl;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.reactivex.netty.protocol.http.server.HttpRequestHeaders;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import netflix.karyon.transport.http.health.HealthCheckEndpoint;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class TriathlonRouterTest {
    private HttpServerRequest request;
    private HttpServerResponse response;
    private TriathlonEndpointImpl triathlonEndpoint;
    private HealthCheckEndpoint healthCheck;
    private TriathlonRouter triathlonRouter;

    @Before
    public void setUp(){

        this.triathlonEndpoint = Mockito.mock(TriathlonEndpointImpl.class);
        this.healthCheck       = Mockito.mock(HealthCheckEndpoint.class);
        this.triathlonRouter   = new TriathlonRouter(healthCheck, triathlonEndpoint);
    }
    @Test
    public void testRouteTriathlon() throws Exception {
        Observable<Void> observable = Mockito.mock(Observable.class);
        when(this.triathlonEndpoint.postApps(any(), any())).thenReturn(observable);
        HttpRequestHeaders headers = Mockito.mock(HttpRequestHeaders.class);

        List<String> uris = Lists.newArrayList(
                "/v2/apps"
        );
        while(uris.size()>0){
            String uri = uris.remove(0);
            request  = getRequest(uri, headers, HttpMethod.POST);
            response = getResponse();
            this.triathlonRouter.handle(request, response);
            verify(this.triathlonEndpoint, times(1)).postApps(request, response);
        }
        request  = getRequest("/someOtherUri", headers, HttpMethod.POST);
        response = getResponse();
        this.triathlonRouter.handle(request, response);
        verify(this.triathlonEndpoint,times(0)).postApps(request, response);
        verify(response, times(1)).setStatus(argThat(matchResponseStatus(HttpResponseStatus.NOT_FOUND)));
    }

    private HttpServerRequest getRequest(String path, HttpRequestHeaders headers, HttpMethod method){
        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getHttpMethod()).thenReturn(method);
        when(request.getHttpVersion()).thenReturn(HttpVersion.HTTP_1_1);
        when(request.getPath()).thenReturn(path);
        when(request.getQueryString()).thenReturn("");
        when(request.getUri()).thenReturn(path);
        when(request.getQueryParameters()).thenReturn(new HashMap<String, List<String>>());
        when(request.getCookies()).thenReturn(new HashMap<String, Set<String>>());
        when(request.getNettyChannel()).thenReturn(new LocalChannel());
        return request;
    }
    private HttpServerResponse getResponse(){
        HttpServerResponse response = Mockito.mock(HttpServerResponse.class);
        when(response.getChannel()).thenReturn(new LocalChannel());

        when(response.close()).thenReturn(Observable.empty());

        return response;
    }
    Matcher<HttpResponseStatus> matchResponseStatus(final HttpResponseStatus status) {
        return new TypeSafeMatcher<HttpResponseStatus>() {
            public boolean matchesSafely(HttpResponseStatus item) {
                return status.equals(item);
            }
            public void describeTo(Description description) {description.appendText("a response status " + status);}
        };
    }


}