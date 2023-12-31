/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 # Copyright (c) 2020, CMCC Technologies Co., Ltd.
 #
 # Licensed under the Apache License, Version 2.0 (the "License")
 # you may not use this file except in compliance with the License.
 # You may obtain a copy of the License at
 #
 #       http://www.apache.org/licenses/LICENSE-2.0
 #
 # Unless required by applicable law or agreed to in writing, software
 # distributed under the License is distributed on an "AS IS" BASIS,
 # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 # See the License for the specific language governing permissions and
 # limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.so.beans.nsmf;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo implements Serializable {

    private static final long serialVersionUID = 7895110339097615695L;

    private String serviceInvariantUuid;

    private String serviceUuid;

    private String globalSubscriberId;

    private String subscriptionServiceType;

    private String serviceType;

    private String nsiId;

    private String nssiId;

    private String sST;

    private String nssiName;

    private String pLMNIdList;

    private String actionType;

    private String resourceSharingLevel;

}
