/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2020 Huawei Technologies Co., Ltd. All rights reserved.
 * ================================================================================
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
package org.onap.so.bpmn.infrastructure.scripts

import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.onap.aaiclient.client.aai.AAIObjectType
import org.onap.aaiclient.client.aai.entities.uri.AAIResourceUri
import org.onap.aaiclient.client.aai.entities.uri.AAIUriFactory
import org.onap.aaiclient.client.generated.fluentbuilders.AAIFluentTypeBuilder
import org.onap.aaiclient.client.generated.fluentbuilders.AAIFluentTypeBuilder.Types
import org.onap.so.beans.nsmf.oof.TemplateInfo
import org.onap.so.bpmn.common.scripts.MsoGroovyTest
import org.onap.so.bpmn.common.scripts.OofUtils

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

class DoAllocateTnNssiTest extends MsoGroovyTest {
    @Before
    void init() throws IOException {
        super.init("DoAllocateTnNssiTest")
    }

    @Captor
    static ArgumentCaptor<ExecutionEntity> captor = ArgumentCaptor.forClass(ExecutionEntity.class)

    @Test
    void testPreProcessRequest() {
        when(mockExecution.getVariable("msoRequestId")).thenReturn("4c614769-f58a-4556-8ad9-dcd903077c82")
        when(mockExecution.getVariable("mso.workflow.sdncadapter.callback")).thenReturn("http://localhost:8090/SDNCAdapterCallback")
        when(mockExecution.getVariable("modelInvariantUuid")).thenReturn("f85cbcc0-ad74-45d7-a5a1-17c8744fdb71")
        when(mockExecution.getVariable("modelUuid")).thenReturn("36a3a8ea-49a6-4ac8-b06c-89a54544b9b6")
        //when(mockExecution.getVariable("serviceInstanceID")).thenReturn("eb0863e9-a69b-4b17-8a56-f05ad110bef7")
        //when(mockExecution.getVariable("operationId")).thenReturn("998c2081-5a71-4a39-9ae6-d6b7c5bb50c0")
        when(mockExecution.getVariable("operationType")).thenReturn("opTypeTest")
        when(mockExecution.getVariable("globalSubscriberId")).thenReturn("5GCustomer")
        when(mockExecution.getVariable("servicename")).thenReturn("5G-test")
        when(mockExecution.getVariable("networkType")).thenReturn("5G-network")
        when(mockExecution.getVariable("subscriptionServiceType")).thenReturn("5G-service")
        when(mockExecution.getVariable("nsiId")).thenReturn("88f65519-9a38-4c4b-8445-9eb4a5a5af56")
        when(mockExecution.getVariable("jobId")).thenReturn("f70e927b-6087-4974-9ef8-c5e4d5847ca4")
        when(mockExecution.getVariable("sliceParams")).thenReturn(mockSliceParams())

        TnAllocateNssi obj = new TnAllocateNssi()
        obj.preProcessRequest(mockExecution)
        Mockito.verify(mockExecution, times(1)).setVariable(eq("sliceServiceInstanceId"), captor.capture())
        String sliceServiceInstanceId = captor.getValue()
        assertNotNull(sliceServiceInstanceId)
    }

    @Test
    void testCreateServiceInstance() {
        when(mockExecution.getVariable("sliceServiceInstanceId")).thenReturn("5ad89cf9-0569-4a93-9306-d8324321e2be")
        when(mockExecution.getVariable("sliceServiceInstanceName")).thenReturn("5G-service")
        when(mockExecution.getVariable("globalSubscriberId")).thenReturn("5GCustomer")
        when(mockExecution.getVariable("subscriptionServiceType")).thenReturn("5G")
        when(mockExecution.getVariable("modelInvariantUuid")).thenReturn("f85cbcc0-ad74-45d7-a5a1-17c8744fdb71")
        when(mockExecution.getVariable("modelUuid")).thenReturn("36a3a8ea-49a6-4ac8-b06c-89a54544b9b6")
        when(mockExecution.getVariable("sliceProfile")).thenReturn(mockSliceProfile())

//        JsonUtils jsonUtil = new JsonUtils()
//        String sliceProfile = jsonUtil.getJsonValue(mockSliceParams(), "sliceProfile")
//        when(mockExecution.getVariable("sliceProfile")).thenReturn(sliceProfile)

        AAIResourceUri serviceInstanceUri = AAIUriFactory.createResourceUri(AAIFluentTypeBuilder.business().customer("5GCustomer").serviceSubscription("5G").serviceInstance("5ad89cf9-0569-4a93-9306-d8324321e2be"))
        DoCreateTnNssiInstance obj = spy(DoCreateTnNssiInstance.class)
        when(obj.getAAIClient()).thenReturn(client)

        obj.createServiceInstance(mockExecution)
    }


    private String mockSliceParams() {
        String expect = """{
    "sliceProfile": {
      "snssaiList": [
        "001-100001"
      ],
      "sliceProfileId": "ab9af40f13f721b5f13539d87484098",
      "plmnIdList": [
        "460-00",
        "460-01"
      ],
      "perfReq": {
      },
      "coverageAreaTAList": [
      ],
      "latency": 2,
      "maxBandwidth": 100,
      "resourceSharingLevel": "non-shared"
    },
    "transportSliceNetworks": [
            {
                "connectionLinks": [
                    {
                        "transportEndpointA": "tranportEp_ID_XXX",
                        "transportEndpointB": "tranportEp_ID_YYY"
                    },
                    {
                        "transportEndpointA": "tranportEp_ID_AAA",
                        "transportEndpointB": "tranportEp_ID_BBB"
                    }
                ]
            },
            {
                "connectionLinks": [
                    {
                        "transportEndpointA": "tranportEp_ID_CCC",
                        "transportEndpointB": "tranportEp_ID_DDD"
                    },
                    {
                        "transportEndpointA": "tranportEp_ID_EEE",
                        "transportEndpointB": "tranportEp_ID_FFF"
                    }
                ]
            }
    ],
    "nsiInfo": {
      "nsiId": "NSI-M-001-HDBNJ-NSMF-01-A-ZX",
      "nsiName": "eMBB-001"
    },
    "scriptName": "AN1"
        }"""
        return expect.replaceAll("\\\\s+", "")
    }

    private String mockSliceProfile() {
        String expect = """{
      "snssaiList": [
        "001-100001"
      ],
      "sliceProfileId": "ab9af40f13f721b5f13539d87484098",
      "plmnIdList": [
        "460-00",
        "460-01"
      ],
      "perfReq": {
      },
      "coverageAreaTAList": [
      ],
      "latency": 2,
      "maxBandwidth": 100,
      "resourceSharingLevel": "non-shared"
     }"""
        return expect.replaceAll("\\\\s+", "")
    }

    @Test
    void testPrepareOofSelection() {
        when(mockExecution.getVariable("msoRequestId")).thenReturn("10dad82d-4bd9-467a-b113-5f8ea7eaae3c")
        when(mockExecution.getVariable("modelUuid")).thenReturn("e2eb2fe3-92a7-447b-8582-077db5cd0862")
        when(mockExecution.getVariable("modelInvariantUuid")).thenReturn("22e6ce80-a55f-4171-a457-a7ecb1865669")
        when(mockExecution.getVariable("tnModelName")).thenReturn("Tn_ONAP_internal_BH")
        when(mockExecution.getVariable("mso.adapters.oof.timeout")).thenReturn("")
        TnAllocateNssi obj = spy(TnAllocateNssi.class)
        OofUtils oofUtils = spy(OofUtils.class)
        String requestId = "10dad82d-4bd9-467a-b113-5f8ea7eaae3c"
        String nsstInfo = "\"{\"UUID\":\"e2eb2fe3-92a7-447b-8582-077db5cd0862\",\"invariantUUID\":\"22e6ce80-a55f-4171-a457-a7ecb1865669\",\"name\":\"Tn_ONAP_internal_BH\"}"
        String messageType = "NSSISelectionResponse"
        String sliceProfile = "{\"maxBandwidth\":3000,\"sliceProfileId\":\"ab9af40f13f721b5f13539d87484098\",\"latency\":10,\"snssaiList\":[\"001-100001\"],\"pLMNIdList\":[\"460-00\",\"460-01\"],\"perfReq\":{},\"coverageAreaTAList\":[],\"resourceSharingLevel\":\"shared\"}}"
        Integer timeout = 600

        when(oofUtils.buildSelectNSSIRequest(requestId, nsstInfo as TemplateInfo, messageType, sliceProfile, timeout)).thenReturn("""
        {
    "apiPath": "/api/oof/selection/nssi/v1",
    "requestDetails": "{\\"requestInfo\\":{\\"transactionId\\":\\"10dad82d-4bd9-467a-b113-5f8ea7eaae3c\\",\\"requestId\\":\\"10dad82d-4bd9-467a-b113-5f8ea7eaae3c\\",\\"callbackUrl\\":\\"http://so-oof-adapter.onap:8090/so/adapters/oof/callback/v1/NSSISelectionResponse/10dad82d-4bd9-467a-b113-5f8ea7eaae3c\\",\\"sourceId\\":\\"SO\\",\\"timeout\\":600,\\"numSolutions\\":1},\\"NSSTInfo\\":{\\"UUID\\":\\"e2eb2fe3-92a7-447b-8582-077db5cd0862\\",\\"invariantUUID\\":\\"22e6ce80-a55f-4171-a457-a7ecb1865669\\",\\"name\\":\\"Tn_ONAP_internal_BH\\"},\\"sliceProfile\\":{\\"maxBandwidth\\":3000,\\"sliceProfileId\\":\\"ab9af40f13f721b5f13539d87484098\\",\\"latency\\":10,\\"snssaiList\\":[\\"001-100001\\"],\\"pLMNIdList\\":[\\"460-00\\",\\"460-01\\"],\\"perfReq\\":{},\\"coverageAreaTAList\\":[],\\"resourceSharingLevel\\":\\"shared\\"}}"
    }""".replaceAll("\\\\s+", ""))
        obj.prepareOofSelection(mockExecution)
        verify(mockExecution, times(1)).setVariable(eq("nssiSelection_oofRequest"), captor.capture())
        String nssiSelection_oofRequest = captor.getValue()
        assertNotNull(nssiSelection_oofRequest)
    }

    @Test
    void testprocessOofSelection() {
        when(mockExecution.getVariable("nssiSelection_oofRequest")).thenReturn("""
        {
    "requestId": "a727643a-bf89-4fd9-b33c-b7bdda18c2b7",
    "transactionId": "a727643a-bf89-4fd9-b33c-b7bdda18c2b7",
    "requestStatus": "completed",
    "statusMessage": "",
    "solutions": [
        {
            "UUID": "e2eb2fe3-92a7-447b-8582-077db5cd0862",
            "invariantUUID": "22e6ce80-a55f-4171-a457-a7ecb1865669",
            "NSSIName": "nssi_tnservice12",
            "NSSIId": "fe26a924-3845-4001-b84e-a439a27a88c0"
        }
    ]
    }""".replaceAll("\\\\s+", ""))
        TnAllocateNssi obj = spy(TnAllocateNssi.class)
        ArrayList<String> solution = new ArrayList<>()
        assertEquals(1, solution.size())
        verify(mockExecution, times(1)).setVariable(eq("isOofTnNssiSelected"), captor.capture())
        String isOofTnNssiSelected = captor.getValue()
        assertEquals("true", isOofTnNssiSelected)
    }
}
