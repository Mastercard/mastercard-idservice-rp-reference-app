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

import static com.mastercard.dis.mids.reference.constants.AppConstants.X_MIDS_USERAUTH_SESSIONID;
import com.mastercard.dis.mids.reference.util.ExceptionUtil;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.session.SessionContext;
import com.mastercard.dis.mids.reference.constants.AppConstants;

import okhttp3.Call;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultSasAccessTokenServiceTest {

    private static final String ACCESS_TOKEN = "jwt";
    private static final String ID_TOKEN = "df52649e-4096-456a-bca0-751ee470009f";
    private static final String SCOPE = "read write";
    private static final String TOKEN_TYPE = "Bearer";
    private static final String AUTH_CODE = "7189112-8987654";

    private static final int EXPIRES_IN = 3600;

    @InjectMocks
    private DefaultSasAccessTokenService sasAccessTokenService;

    @Mock
    private ApiClient apiClientMock;

    @Mock
    private ExceptionUtil exceptionUtilMock;

    Map<String, List<String>> headers;

    List<String> headersList;

    @BeforeEach
    void setUp() throws Exception {
        headers = new HashMap<>();
        headersList = new ArrayList<>();
        headersList.add(AppConstants.X_MIDS_USERAUTH_SESSIONID);
        headers.put(AppConstants.X_MIDS_USERAUTH_SESSIONID, headersList);
        when(apiClientMock.buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any())).thenReturn(mock(Call.class));
        SessionContext.create(X_MIDS_USERAUTH_SESSIONID);
    }

    @Test
    void sasAccessTokenResponse_SuccessfulApiCall_ShouldReturnResponse() throws ServiceException, ApiException {
        when(apiClientMock.execute(any(Call.class), any(Type.class)))
                .thenReturn(
                        new ApiResponse<>(200, headers, getSasAccessTokenResponse())
                );

        SasAccessTokenResponseDTO result = sasAccessTokenService.sasAccessTokenResponse(SasAccessTokenRequestExample.sasAccessTokenRequestExample(AUTH_CODE));

        verify(apiClientMock, atLeastOnce()).buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
        verify(apiClientMock, times(1)).execute(any(Call.class), any(Type.class));
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(ACCESS_TOKEN, result.getAccess_token()),
                () -> assertEquals(EXPIRES_IN, result.getExpires_in()),
                () -> assertEquals(ID_TOKEN, result.getId_token()),
                () -> assertEquals(SCOPE, result.getScope()),
                () -> assertEquals(TOKEN_TYPE, result.getToken_type())
        );
    }

    @Test
    void sasAccessTokenResponse_FailureApiCall_ShouldThrowApiException() throws ApiException {
        when(apiClientMock.execute(any(Call.class), any(Type.class))).thenThrow(new ApiException());
        when(exceptionUtilMock.logAndConvertToServiceException(any(ApiException.class))).thenThrow(new ServiceException(""));

        SasAccessTokenRequestDTO sasAccessTokenRequestDTO = new SasAccessTokenRequestDTO();

        assertThrows(ServiceException.class, () -> sasAccessTokenService.sasAccessTokenResponse(sasAccessTokenRequestDTO));
        verify(apiClientMock, atLeastOnce()).buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
        verify(apiClientMock, atLeastOnce()).execute(any(Call.class), any(Type.class));
    }

    private SasAccessTokenResponseDTO getSasAccessTokenResponse() {
        SasAccessTokenResponseDTO sasAccessTokenResponseDTO = new SasAccessTokenResponseDTO();
        sasAccessTokenResponseDTO.setAccess_token(ACCESS_TOKEN);
        sasAccessTokenResponseDTO.setExpires_in(EXPIRES_IN);
        sasAccessTokenResponseDTO.setId_token(ID_TOKEN);
        sasAccessTokenResponseDTO.setScope(SCOPE);
        sasAccessTokenResponseDTO.setToken_type(TOKEN_TYPE);
        return sasAccessTokenResponseDTO;
    }
}