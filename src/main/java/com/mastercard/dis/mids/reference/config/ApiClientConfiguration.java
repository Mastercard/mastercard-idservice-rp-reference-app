/*
 Copyright (c) 2021 Mastercard

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
import com.mastercard.dis.mids.reference.interceptor.EncryptionDecryptionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;

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

    @PostConstruct
    public void initialize() {
        if (null == keyFile || StringUtils.isEmpty(consumerKey)) {
            throw new ServiceException(".p12 file or consumerKey does not exist, please add details in application.properties");
        }
    }

    @Bean
    public ApiClient apiClient(EncryptionDecryptionInterceptor encryptionDecryptionInterceptor) {
        ApiClient client = new ApiClient();
        try {
            PrivateKey signingKey = AuthenticationUtils.loadSigningKey(keyFile.getFile().getAbsolutePath(), keystoreAlias, keystorePassword);
            client.setBasePath(basePath);
            client.setDebugging(true);
            client.setReadTimeout(40000);

            return client.setHttpClient(client.getHttpClient()
                    .newBuilder()
                    .addInterceptor(encryptionDecryptionInterceptor) // This interceptor will encrypt and decrypt the payload
                    .addInterceptor(new OkHttpOAuth1Interceptor(consumerKey, signingKey))
                    .build()
            );
        } catch (Exception e) {
            log.error("Error occurred while configuring ApiClient", e);
            throw new ServiceException("Error occurred while configuring ApiClient", e);
        }
    }
}