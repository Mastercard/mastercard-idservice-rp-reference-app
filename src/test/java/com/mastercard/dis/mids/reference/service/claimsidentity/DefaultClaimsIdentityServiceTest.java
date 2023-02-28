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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private ClaimsSharingApiAdapter claimsSharingApiAdapter;

    @Test
    void claimsIdentityAttributes_ShouldThrowApiException() throws Exception {
        when(claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(any(String.class), any(String.class))).thenThrow(ApiException.class);
        when(exceptionUtilMock.logAndConvertToServiceException(any(ApiException.class))).thenThrow(new ServiceException("Error while processing request"));
        Exception exception = assertThrows(ServiceException.class, () -> defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN));
        assertNotNull(exception);
    }

}