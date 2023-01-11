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

package com.mastercard.dis.mids.reference.component;

import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityService;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class IDRPReferenceTest {

    private static final String ARID = "d22a5b3e-dbb5-4f77-ac74-30040fef4561";
    private static final String ACCESS_TOKEN = "jwt";

    @InjectMocks
    IDRPReference idRpReference;

    @Mock
    ClaimsIdentityService claimsIdentityServiceMock;

    @Mock
    SasAccessTokenService sasAccessTokenServiceMock;

    @Test
    @DisplayName("Call Claims Identity Attributes API - Valid - Successful")
    void callClaimsIdentityAttributes_Valid_SuccessfulCall() {
        idRpReference.callClaimsIdentityAttributes(ARID, ACCESS_TOKEN);

        verify(claimsIdentityServiceMock, times(1)).claimsIdentityAttributes(eq(ARID), eq(ACCESS_TOKEN));
        verifyNoMoreInteractions(claimsIdentityServiceMock);
    }

    @Test
    @DisplayName("Call SAS Access Token API - Valid - Successful")
    void callSasAccessToken_Valid_SuccessfulCall() {
        SasAccessTokenRequestDTO sasAccessTokenRequestDTO = new SasAccessTokenRequestDTO();
        sasAccessTokenRequestDTO.setClientAssertionType("urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        sasAccessTokenRequestDTO.setClientAssertion("eyJraWQiOiJjdkF4d0FqaENJRnY5MFZnQ3M0b2o1Wmp2SmoyUlRXSC1sWHh3X2diMWRmMWV");
        sasAccessTokenRequestDTO.setGrantType("authorization_code");
        sasAccessTokenRequestDTO.setCodeVerifier("my-code-verifier-string");
        sasAccessTokenRequestDTO.setCode("eyJ4NXQjUzI1NiI6Ild...");
        sasAccessTokenRequestDTO.setRedirectUrl("https://venomsoft.ie");
        idRpReference.callSasAccessToken(sasAccessTokenRequestDTO);

        verify(sasAccessTokenServiceMock, times(1)).sasAccessTokenResponse(eq(sasAccessTokenRequestDTO));
        verifyNoMoreInteractions(sasAccessTokenServiceMock);
    }
}