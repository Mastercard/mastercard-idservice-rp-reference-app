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

package com.mastercard.dis.mids.reference.service.sas;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SasAccessTokenRequestExample {

    private static final String GRANT_TYPE = "authorization_code";
    private static final String REDIRECT_URL = "https://rp-domain.com";
    private static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private static final String CLIENT_ASSERTION = "eyJhbGciOiJSUzI1NiIsImN0eSI6IkpXUyIsInR5cCI6IkpXVCIsImtpZCI6ImFhdUZUVXlQWFNvZ1VGc1U0VUZOeEpMLTB0LTBWelJfWDd1UUUtdFc5MjUxOWYyMyFlZmJhZTdjZGMyMTA0NWU0ODExNDI2ZWJhMzAyNmJlMzAwMDAwMDAwMDAwMDAwMDAifQ.eyJuYmYiOjE2MjYyNzc1MzMsInN1YiI6Im5vdF9hdmFpbGFibGUiLCJqdGkiOiI2QzVFNzYyOC1EMDg1LTRGNjktQTE3RC02MkYzMEVENUExRUYiLCJpYXQiOjE2MjYyNzc1MzMsImV4cCI6MTY1NzgxMzUzM30.Fnz0L75zqQGTrhpp3uIzI2ypuf2bvUZwNb9GvMrVeiTAsNf52TNErmLLr6qD_4egY3t7oAZ0A7Lg9Egj1TAsWECAMx3SdcC7vnMYanov2rJntYUWl809tQ3j5b0tZl778Roej-MlrbNGe6vQtiBEXS--7RS8YOqJm08IwafilfwDA1FEvnfuyHyWlxQK5TY0j0a8XzNQ0HkLlzrP-UaRL1M9crTOag1Nq7R2Elj9Q1z1dxesTPIMKdfVAoJ9rWc9fmnfOwNgUR1nLPDGN3CndS6t-yez6fHB9lsXKRO7sCp1c7K_OtGid9IuEH3e92JXNHsN4UJpRUthivvTC49Yxg";
    private static final String CODE_VERIFIER = "my-code-verifier-string";

    /**
     * Create a sample request for SAS Access Token API.
     *
     * @return a constructed DTO for SAS Access Token.
     */
    public static SasAccessTokenRequestDTO sasAccessTokenRequestExample(String authCode) {
        return SasAccessTokenRequestDTO.builder()
                .grantType(GRANT_TYPE)
                .redirectUrl(REDIRECT_URL)
                .code(authCode)
                .clientAssertionType(CLIENT_ASSERTION_TYPE)
                .clientAssertion(CLIENT_ASSERTION)
                .codeVerifier(CODE_VERIFIER)
                .build();
    }
}