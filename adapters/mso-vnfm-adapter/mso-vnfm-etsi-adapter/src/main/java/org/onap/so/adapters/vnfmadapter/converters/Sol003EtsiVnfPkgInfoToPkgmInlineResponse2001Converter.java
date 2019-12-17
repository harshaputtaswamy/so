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

package org.onap.so.adapters.vnfmadapter.converters;

import org.onap.so.adapters.vnfmadapter.extclients.etsicatalog.model.*;
import org.onap.so.adapters.vnfmadapter.extclients.vnfm.packagemanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Converter to convert from an Etsi Catalog Model {@link VnfPkgInfo} Object to a PackageManagement Model
 * {@link InlineResponse2001} Object
 *
 * @author andrew.a.lamb@est.tech
 */
@Service
public class Sol003EtsiVnfPkgInfoToPkgmInlineResponse2001Converter
        implements Converter<VnfPkgInfo, InlineResponse2001> {
    private static final Logger logger =
            LoggerFactory.getLogger(Sol003EtsiVnfPkgInfoToPkgmInlineResponse2001Converter.class);

    /**
     * Convert a {@link VnfPkgInfo} Object to an {@link InlineResponse2001} Object
     * 
     * @param vnfPkgInfo The VnfPkgInfo Object to Convert
     * @return The Converted InlineResponse2001 Object
     */
    @Override
    public InlineResponse2001 convert(final VnfPkgInfo vnfPkgInfo) {
        if (vnfPkgInfo == null) {
            logger.info("No VnfPkgInfo Object Provided for Conversion. (Null object received, returning Null)");
            return null;
        }
        final InlineResponse2001 response = new InlineResponse2001();
        response.setId(vnfPkgInfo.getId());
        response.setVnfdId(vnfPkgInfo.getVnfdId());
        response.setVnfProvider(vnfPkgInfo.getVnfProvider());
        response.setVnfProductName(vnfPkgInfo.getVnfProductName());
        response.setVnfSoftwareVersion(vnfPkgInfo.getVnfSoftwareVersion());
        response.setVnfdVersion(vnfPkgInfo.getVnfdVersion());
        response.setChecksum(convertChecksumToVnfPackagesChecksum(vnfPkgInfo.getChecksum()));
        response.setSoftwareImages(
                convertVnfPackageSoftwareImageInfoListToVnfPackagesSoftwareImagesList(vnfPkgInfo.getSoftwareImages()));
        response.setAdditionalArtifacts(convertVnfPackageArtifactInfoListToVnfPackagesAdditionalArtifactsList(
                vnfPkgInfo.getAdditionalArtifacts()));

        if (vnfPkgInfo.getOnboardingState() != null) {
            response.setOnboardingState(
                    InlineResponse2001.OnboardingStateEnum.fromValue(vnfPkgInfo.getOnboardingState().getValue()));
        }

        if (vnfPkgInfo.getOperationalState() != null) {
            response.setOperationalState(
                    InlineResponse2001.OperationalStateEnum.fromValue(vnfPkgInfo.getOperationalState().getValue()));
        }

        response.setUserDefinedData((vnfPkgInfo.getUserDefinedData()));

        if (vnfPkgInfo.getLinks() != null) {
            response.setLinks(convertVNFPKGMLinkSerializerToVnfPackagesLinks(vnfPkgInfo.getLinks()));
        }

        return response;
    }

    private VnfPackagesChecksum convertChecksumToVnfPackagesChecksum(final Checksum checksum) {
        final VnfPackagesChecksum vnfPackagesChecksum = new VnfPackagesChecksum();
        if (checksum != null) {
            vnfPackagesChecksum.setAlgorithm(checksum.getAlgorithm());
            vnfPackagesChecksum.setHash(checksum.getHash());
        }
        return vnfPackagesChecksum;
    }

    private List<VnfPackagesSoftwareImages> convertVnfPackageSoftwareImageInfoListToVnfPackagesSoftwareImagesList(
            final List<VnfPackageSoftwareImageInfo> vnfPackageSoftwareImageInfoList) {
        final List<VnfPackagesSoftwareImages> vnfPackagesSoftwareImages = new ArrayList<>();
        if (vnfPackageSoftwareImageInfoList != null) {
            for (final VnfPackageSoftwareImageInfo vnfPackageSoftwareImageInfo : vnfPackageSoftwareImageInfoList) {
                final VnfPackagesSoftwareImages softwareImage =
                        convertVnfPackageSoftwareImageInfoToVnfPackagesSoftwareImages(vnfPackageSoftwareImageInfo);
                vnfPackagesSoftwareImages.add(softwareImage);
            }
        }
        return vnfPackagesSoftwareImages;
    }

    private VnfPackagesSoftwareImages convertVnfPackageSoftwareImageInfoToVnfPackagesSoftwareImages(
            final VnfPackageSoftwareImageInfo vnfPackageSoftwareImageInfo) {
        final VnfPackagesSoftwareImages vnfPackagesSoftwareImages = new VnfPackagesSoftwareImages();
        vnfPackagesSoftwareImages.setId(vnfPackageSoftwareImageInfo.getId());
        vnfPackagesSoftwareImages.setName(vnfPackageSoftwareImageInfo.getName());
        vnfPackagesSoftwareImages.setProvider(vnfPackageSoftwareImageInfo.getProvider());
        vnfPackagesSoftwareImages.setVersion(vnfPackageSoftwareImageInfo.getVersion());
        vnfPackagesSoftwareImages
                .setChecksum(convertChecksumToVnfPackagesChecksum(vnfPackageSoftwareImageInfo.getChecksum()));
        if (vnfPackageSoftwareImageInfo.getContainerFormat() != null) {
            vnfPackagesSoftwareImages.setContainerFormat(VnfPackagesSoftwareImages.ContainerFormatEnum
                    .fromValue(vnfPackageSoftwareImageInfo.getContainerFormat().getValue()));
        }

        if (vnfPackageSoftwareImageInfo.getDiskFormat() != null) {
            vnfPackagesSoftwareImages.setDiskFormat(VnfPackagesSoftwareImages.DiskFormatEnum
                    .fromValue(vnfPackageSoftwareImageInfo.getDiskFormat().getValue()));
        }

        vnfPackagesSoftwareImages.setCreatedAt(vnfPackageSoftwareImageInfo.getCreatedAt());
        vnfPackagesSoftwareImages.setMinDisk(vnfPackageSoftwareImageInfo.getMinDisk());
        vnfPackagesSoftwareImages.setMinRam(vnfPackageSoftwareImageInfo.getMinRam());
        vnfPackagesSoftwareImages.setSize(vnfPackageSoftwareImageInfo.getSize());
        vnfPackagesSoftwareImages.setUserMetadata(vnfPackageSoftwareImageInfo.getUserMetadata());
        vnfPackagesSoftwareImages.setImagePath(vnfPackageSoftwareImageInfo.getImagePath());
        return vnfPackagesSoftwareImages;
    }

    private List<VnfPackagesAdditionalArtifacts> convertVnfPackageArtifactInfoListToVnfPackagesAdditionalArtifactsList(
            final List<VnfPackageArtifactInfo> vnfPackageArtifactInfoList) {
        if (vnfPackageArtifactInfoList != null) {
            final List<VnfPackagesAdditionalArtifacts> additionalArtifacts = new ArrayList<>();
            for (final VnfPackageArtifactInfo artifactInfo : vnfPackageArtifactInfoList) {
                final VnfPackagesAdditionalArtifacts artifact =
                        convertVnfPackageArtifactInfoToVnfPackagesAdditionalArtifacts(artifactInfo);
                additionalArtifacts.add(artifact);
            }
            return additionalArtifacts;
        }
        return null;
    }

    private VnfPackagesAdditionalArtifacts convertVnfPackageArtifactInfoToVnfPackagesAdditionalArtifacts(
            final VnfPackageArtifactInfo vnfPackageArtifactInfo) {
        final VnfPackagesAdditionalArtifacts vnfPackagesAdditionalArtifacts = new VnfPackagesAdditionalArtifacts();
        vnfPackagesAdditionalArtifacts.setArtifactPath(vnfPackageArtifactInfo.getArtifactPath());
        vnfPackagesAdditionalArtifacts
                .setChecksum(convertChecksumToVnfPackagesChecksum(vnfPackageArtifactInfo.getChecksum()));
        vnfPackagesAdditionalArtifacts.setMetadata(vnfPackageArtifactInfo.getMetadata());
        return vnfPackagesAdditionalArtifacts;
    }

    private VnfPackagesLinks convertVNFPKGMLinkSerializerToVnfPackagesLinks(
            final VNFPKGMLinkSerializer vnfpkgmLinkSerializer) {
        final VnfPackagesLinks vnfPackagesLinks = new VnfPackagesLinks();
        vnfPackagesLinks.setSelf(convertUriLinkToVnfPackagesLinksSelf(vnfpkgmLinkSerializer.getSelf()));
        vnfPackagesLinks.setVnfd(convertUriLinkToVnfPackagesLinksSelf(vnfpkgmLinkSerializer.getVnfd()));
        vnfPackagesLinks
                .setPackageContent(convertUriLinkToVnfPackagesLinksSelf(vnfpkgmLinkSerializer.getPackageContent()));
        return vnfPackagesLinks;
    }

    private VnfPackagesLinksSelf convertUriLinkToVnfPackagesLinksSelf(final UriLink uriLink) {
        final VnfPackagesLinksSelf vnfPackagesLinksSelf = new VnfPackagesLinksSelf();
        if (uriLink != null) {
            vnfPackagesLinksSelf.setHref(uriLink.getHref());
        }
        return vnfPackagesLinksSelf;
    }

}
