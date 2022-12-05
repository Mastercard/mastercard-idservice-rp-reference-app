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

package com.mastercard.dis.mids.reference.service.claimsidentity;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.session.SessionContext;
import com.mastercard.dis.mids.reference.util.ExceptionUtil;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mastercard.dis.mids.reference.constants.AppConstants.X_MIDS_USERAUTH_SESSIONID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultClaimsIdentityServiceTest {

    private static final String ARID = "df52649e-4096-456a-bca0-751ee470009f";
    private static final String ACCESS_TOKEN = "jwt";

    Map<String, List<String>> headers;
    List<String> headersList;
    @InjectMocks
    private DefaultClaimsIdentityService defaultClaimsIdentityService;
    @Mock
    private ApiClient apiClientMock;
    @Mock
    private ExceptionUtil exceptionUtilMock;
    private OkHttpClient httpClient;

    @BeforeEach
    void setUp() throws Exception {
        headers = new HashMap<>();
        headersList = new ArrayList<>();
        headersList.add(X_MIDS_USERAUTH_SESSIONID);
        headers.put(X_MIDS_USERAUTH_SESSIONID, headersList);
        when(apiClientMock.buildCall(any(), anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any())).thenReturn(mock(Call.class));
        SessionContext.create(X_MIDS_USERAUTH_SESSIONID);
    }

    @Test
    void claimsIdentityAttributes_SuccessfulApiCall_ShouldReturnResponse() throws ApiException {
        defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN);
        verify(apiClientMock, atLeastOnce()).buildCall(any(), anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
    }

    @Test
    void claimsIdentityAttributes_FailureApiCall_ShouldThrowApiException() throws ApiException {
        when(apiClientMock.buildCall(any(), anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any())).
                thenThrow(new ApiException());
        when(exceptionUtilMock.logAndConvertToServiceException(any(ApiException.class))).thenThrow(new ServiceException("Error while processing request"));
        assertThrows(ServiceException.class, () -> defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN));
        verify(apiClientMock, atMostOnce()).buildCall(any(), anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
    }

    @Test
    public void claimsIdentityAttributes_VerifyJWSGeneratedUsingCaasSignerStub() throws Exception {

        Response result = defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN);
        verify(apiClientMock, atMostOnce()).buildCall(any(), anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
    }
}