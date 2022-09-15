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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SasAccessTokenRequestDTO {

    @JsonProperty("grantType")
    private String grantType;

    @JsonProperty("redirectUrl")
    private String redirectUrl;

    @JsonProperty("code")
    private String code;

    @JsonProperty("clientAssertionType")
    private String clientAssertionType;

    @JsonProperty("clientAssertion")
    private String clientAssertion;

    @JsonProperty("codeVerifier")
    private String codeVerifier;

}