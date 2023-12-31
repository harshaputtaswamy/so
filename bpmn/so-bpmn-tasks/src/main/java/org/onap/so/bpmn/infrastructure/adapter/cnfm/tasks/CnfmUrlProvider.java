/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2023 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.so.bpmn.infrastructure.adapter.cnfm.tasks;

import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Sagar Shetty (sagar.shetty@est.tech)
 * @author Waqas Ikram (waqas.ikram@est.tech)
 * @author Raviteja Karumuri (raviteja.karumuri@est.tech)
 *
 */
@Service
public class CnfmUrlProvider {

    private final URI baseUri;

    @Autowired
    public CnfmUrlProvider(@Value("${cnfm.endpoint:http://so-cnfm-lcm.onap:9888}") final String baseUrl) {
        this.baseUri = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/so/so-cnfm/v1/api/aslcm/v1").build().toUri();
    }

    public String getCreateAsRequestUrl() {
        return UriComponentsBuilder.fromUri(baseUri).pathSegment("as_instances").build().toString();
    }

    public String getInstantiateAsRequestUrl(String asInstanceId) {
        return UriComponentsBuilder.fromUri(baseUri).pathSegment("as_instances").pathSegment(asInstanceId)
                .pathSegment("instantiate").build().toString();
    }

    public String getDeleteAsRequestUrl(String asInstanceId) {
        return UriComponentsBuilder.fromUri(baseUri).pathSegment("as_instances").pathSegment(asInstanceId).build()
                .toString();
    }

    public String getTerminateAsRequestUrl(String asInstanceId) {
        return UriComponentsBuilder.fromUri(baseUri).pathSegment("as_instances").pathSegment(asInstanceId)
                .pathSegment("terminate").build().toString();
    }
}
