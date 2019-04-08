/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
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

package org.onap.so.adapters.vnfmadapter.lifecycle;

import com.google.common.base.Optional;
import org.onap.aai.domain.yang.EsrVnfm;
import org.onap.aai.domain.yang.GenericVnf;
import org.onap.so.adapters.vnfmadapter.extclients.aai.AaiHelper;
import org.onap.so.adapters.vnfmadapter.extclients.aai.AaiServiceProvider;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.VnfmHelper;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.VnfmServiceProvider;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InlineResponse201;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.InstantiateVnfRequest;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.LccnSubscriptionRequest;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.TerminateVnfRequest;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.model.TerminateVnfRequest.TerminationTypeEnum;
import org.onap.so.adapters.vnfmadapter.jobmanagement.JobManager;
import org.onap.so.adapters.vnfmadapter.rest.exceptions.VnfNotFoundException;
import org.onap.so.adapters.vnfmadapter.rest.exceptions.VnfmNotFoundException;
import org.onap.vnfmadapter.v1.model.CreateVnfRequest;
import org.onap.vnfmadapter.v1.model.CreateVnfResponse;
import org.onap.vnfmadapter.v1.model.DeleteVnfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages lifecycle operations towards the VNFMs.
 */
@Component
public class LifecycleManager {
    private static final Logger logger = LoggerFactory.getLogger(LifecycleManager.class);
    private final AaiServiceProvider aaiServiceProvider;
    private final VnfmServiceProvider vnfmServiceProvider;
    private final AaiHelper aaiHelper;
    private final VnfmHelper vnfmHelper;
    private final JobManager jobManager;

    @Autowired
    LifecycleManager(final AaiServiceProvider aaiServiceProvider, final AaiHelper aaiHelper,
            final VnfmHelper vnfmHelper, final VnfmServiceProvider vnfmServiceProvider, final JobManager jobManager) {
        this.aaiServiceProvider = aaiServiceProvider;
        this.vnfmServiceProvider = vnfmServiceProvider;
        this.aaiHelper = aaiHelper;
        this.vnfmHelper = vnfmHelper;
        this.jobManager = jobManager;
    }

    /**
     * Create a VNF on a VNFM.
     *
     * @param vnfIdInAai the ID of the VNF in AAI
     * @param request the create request
     * @return the response to the request
     */
    public CreateVnfResponse createVnf(final String vnfIdInAai, final CreateVnfRequest request) {
        final GenericVnf genericVnf = getGenericVnfFromAai(vnfIdInAai);
        checkIfVnfAlreadyExistsInVnfm(genericVnf);

        EsrVnfm vnfm = aaiHelper.getAssignedVnfm(genericVnf);
        if (vnfm == null) {
            vnfm = aaiHelper.selectVnfm(genericVnf);
            aaiHelper.addRelationshipFromGenericVnfToVnfm(genericVnf, vnfm.getVnfmId());
        }

        final String vnfIdInVnfm = sendCreateRequestToVnfm(genericVnf);
        createNotificationSubscription(vnfm.getVnfmId(), vnfIdInVnfm);
        final String operationId = sendInstantiateRequestToVnfm(vnfm, genericVnf, request, vnfIdInAai, vnfIdInVnfm);

        final String jobId = jobManager.createJob(vnfm.getVnfmId(), operationId, false);
        final CreateVnfResponse response = new CreateVnfResponse();
        response.setJobId(jobId);
        return response;
    }

    private void checkIfVnfAlreadyExistsInVnfm(final GenericVnf genericVnf) {
        if (genericVnf.getSelflink() != null && !genericVnf.getSelflink().isEmpty()) {
            Optional<InlineResponse201> response = Optional.absent();
            try {
                response = vnfmServiceProvider.getVnf(genericVnf.getSelflink());
            } catch (final Exception exception) {
                logger.debug("Ignoring invalid self link in generic vnf", exception);
            }
            if (response.isPresent()) {
                throw new IllegalArgumentException("VNF " + genericVnf.getVnfId()
                        + " is already defined on the VNFM, self link: " + genericVnf.getSelflink());
            }
        }
    }

    private String sendCreateRequestToVnfm(final GenericVnf genericVnf) {
        // TODO call create request
        genericVnf.setSelflink("http://dummy.value/until/create/implememted/vnfId");
        return "vnfId";
    }

    private void createNotificationSubscription(final String vnfmId, final String vnfId) {
        try {
            final LccnSubscriptionRequest subscriptionRequest = vnfmHelper.createNotificationSubscriptionRequest(vnfId);
            vnfmServiceProvider.subscribeForNotifications(vnfmId, subscriptionRequest);
        } catch (final Exception exception) {
            logger.warn("Subscription for notifications to VNFM: " + vnfmId + " for VNF " + vnfId
                    + " failed. AAI will not be updated unless the VNFM is configured by other means to send notifications relating to this VNF",
                    exception);
        }
    }

    private String sendInstantiateRequestToVnfm(final EsrVnfm vnfm, final GenericVnf genericVnf,
            final CreateVnfRequest createVnfRequest, final String vnfIdInAai, final String vnfIdInVnfm) {

        final InstantiateVnfRequest instantiateVnfRequest =
                vnfmHelper.createInstantiateRequest(createVnfRequest.getTenant(), createVnfRequest);
        final String jobId = vnfmServiceProvider.instantiateVnf(genericVnf.getSelflink(), instantiateVnfRequest);

        logger.info("Instantiate VNF request successfully sent to " + genericVnf.getSelflink());
        return jobId;
    }

    /**
     * Delete a VNF on a VNFM.
     *
     * @param vnfIdInAai the ID of the VNF in AAI
     * @return the response to the request
     */
    public DeleteVnfResponse deleteVnf(final String vnfIdInAai) {
        final GenericVnf genericVnf = getGenericVnfFromAai(vnfIdInAai);
        final String vnfmId = getIdOfAssignedVnfm(genericVnf);

        final String operationId = sendTerminateRequestToVnfm(genericVnf);
        final String jobId = jobManager.createJob(vnfmId, operationId, true);

        return new DeleteVnfResponse().jobId(jobId);
    }

    private String sendTerminateRequestToVnfm(final GenericVnf genericVnf) {
        final TerminateVnfRequest terminateVnfRequest = new TerminateVnfRequest();
        terminateVnfRequest.setTerminationType(TerminationTypeEnum.FORCEFUL);
        return vnfmServiceProvider.terminateVnf(genericVnf.getSelflink(), terminateVnfRequest);
    }

    private GenericVnf getGenericVnfFromAai(final String vnfIdInAai) {
        final GenericVnf genericVnf = aaiServiceProvider.invokeGetGenericVnf(vnfIdInAai);
        if (genericVnf == null) {
            throw new VnfNotFoundException("VNF not found in AAI: " + vnfIdInAai);
        }
        logger.debug("Retrieved generic VNF from AAI: " + genericVnf);
        return genericVnf;
    }

    private String getIdOfAssignedVnfm(final GenericVnf genericVnf) {
        final String vnfmId = aaiHelper.getIdOfAssignedVnfm(genericVnf);
        if (vnfmId == null) {
            throw new VnfmNotFoundException("No VNFM found in AAI for VNF " + genericVnf.getVnfId());
        }
        return vnfmId;
    }
}
