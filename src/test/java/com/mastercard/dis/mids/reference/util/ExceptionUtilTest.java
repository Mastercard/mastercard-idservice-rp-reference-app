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

package com.mastercard.dis.mids.reference.util;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ErrorResponseErrors;
import org.openapitools.client.model.ErrorResponseErrorsErrorInner;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilTest {

    @InjectMocks
    private ApiException apiException;
    @InjectMocks
    private ExceptionUtil exceptionUtil;

    @Test
    void create_WithApiException_Works() {
        String exceptionMessage = "org.openapitools.client.ApiException";
        ApiException apiExceptionMock = mock(ApiException.class);
        ExceptionUtil exceptionUtilMock = mock(ExceptionUtil.class);


        when(exceptionUtilMock.logAndConvertToServiceException(apiExceptionMock))
                .thenReturn(new ServiceException(exceptionMessage));

        ServiceException serviceException = exceptionUtilMock.logAndConvertToServiceException(apiExceptionMock);

        assertNotNull(serviceException);
        assertEquals(exceptionMessage, serviceException.getMessage());
    }

    @Test
    void run_WithApiException_When_cant_deserialize() {
        String exceptionMessage = "org.openapitools.client.ApiException";
        ErrorResponseErrorsErrorInner errorInner = new ErrorResponseErrorsErrorInner().source("mids").reasonCode("").description("").recoverable(false).details("");
        ErrorResponseErrors errorResponseErrors = new ErrorResponseErrors().addErrorItem(errorInner);
        apiException = new ApiException(500, exceptionMessage, new HashMap<>(), errorResponseErrors.toString());

        Exception exception = assertThrows(Exception.class, () ->
                exceptionUtil.logAndConvertToServiceException(apiException)
        );

        assertNotNull(exception);
    }

}