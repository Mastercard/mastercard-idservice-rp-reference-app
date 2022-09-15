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

package com.mastercard.dis.mids.reference.component;

import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityService;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenService;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import org.openapitools.client.model.ClaimsIdentityAttributes;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IDRPReference {

    private final ClaimsIdentityService claimsIdentityService;
    private final SasAccessTokenService sasAccessTokenService;

    public ClaimsIdentityAttributes callClaimsIdentityAttributes(String arid, String accessToken) {
        return claimsIdentityService.claimsIdentityAttributes(arid, accessToken);
    }

    public SasAccessTokenResponseDTO callSasAccessToken(SasAccessTokenRequestDTO sasAccessTokenRequestDTO) {
        return sasAccessTokenService.sasAccessTokenResponse(sasAccessTokenRequestDTO);
    }
}