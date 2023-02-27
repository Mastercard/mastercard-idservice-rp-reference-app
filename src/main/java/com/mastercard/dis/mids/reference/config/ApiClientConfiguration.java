/*
 Copyright (c) 2023 Mastercard

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.mastercard.dis.mids.reference.config;

import com.mastercard.developer.interceptors.OkHttpOAuth1Interceptor;
import com.mastercard.developer.utils.AuthenticationUtils;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsSharingApiAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * This is ApiClient configuration, it will read properties from application.properties and create instance of ApiClient.
 */
@Slf4j
@Configuration
public class ApiClientConfiguration {

    @Value("${mastercard.api.base.path}")
    private String basePath;

    @Value("${mastercard.api.consumer.key}")
    private String consumerKey;

    @Value("${mastercard.api.keystore.alias}")
    private String keystoreAlias;

    @Value("${mastercard.api.keystore.password}")
    private String keystorePassword;

    @Value("${mastercard.api.key.file}")
    private Resource keyFile;

    @Value("${mastercard.client.decryption.enable}")
    private boolean isDecryptionEnabled;

    private static PrivateKey signingKey;

    @PostConstruct
    public void initialize() {
        if (null == keyFile || StringUtils.isEmpty(consumerKey)) {
            throw new ServiceException(".p12 file or consumerKey does not exist, please add details in application.properties");
        }
    }

    @Bean
    public ApiClient apiClient() {
        ApiClient client = new ApiClient();
        try {
            client.setBasePath(basePath);
            client.setDebugging(true);
            client.setReadTimeout(40000);

            return client.setHttpClient(client.getHttpClient()
                    .newBuilder()
                    .addInterceptor(new OkHttpOAuth1Interceptor(consumerKey, getPrivateKey()))
                    .build()
            );
        } catch (Exception e) {
            log.error("Error occurred while configuring ApiClient", e);
            throw new ServiceException("Error occurred while configuring ApiClient", e);
        }
    }

    @Bean
    public ClaimsSharingApiAdapter claimsSharingApiAdapter(ApiClient apiClient) {
        return new ClaimsSharingApiAdapter(apiClient, this.isDecryptionEnabled);
    }

    protected synchronized PrivateKey getPrivateKey() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        if (null == signingKey) {
            signingKey = AuthenticationUtils.loadSigningKey(keyFile.getFile().getAbsolutePath(), keystoreAlias, keystorePassword);
        }
        assert null != signingKey;
        return signingKey;
    }
}