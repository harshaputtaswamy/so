/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 - 2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 Samsung
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
 * ============LICENSE_END=========================================================
 */

package org.onap.so.adapters.vnfmadapter;

import org.onap.so.security.SoBasicWebSecurityConfigurerAdapter;
import org.onap.so.security.SoUserCredentialConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * @author Waqas Ikram (waqas.ikram@est.tech)
 * @author Gareth Roper (gareth.roper@est.tech)
 *
 */
@EnableWebSecurity
@Configuration
public class VnfmBasicWebSecurityConfigurerAdapter extends SoBasicWebSecurityConfigurerAdapter {

    @Value("${server.ssl.client-auth:none}")
    private String clientAuth;
    SoUserCredentialConfiguration soUserCredentialConfiguration;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        if (("need").equalsIgnoreCase(clientAuth)) {
            http.csrf().disable().authorizeRequests().anyRequest().permitAll();
        } else {
            super.configure(http);
            http.authorizeRequests().antMatchers(HttpMethod.GET, "/etsi/subscription/notification").permitAll();
        }
    }
}

