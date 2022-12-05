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

package com.mastercard.dis.mids.reference.util;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionUtilTest {

    @Mock
    private ApiException apiExceptionMock;
    @Mock
    private ExceptionUtil exceptionUtilMock;


    @Test
    void create_WithApiException_Works() {
        String exceptionMessage = "org.openapitools.client.ApiException";

        when(exceptionUtilMock.logAndConvertToServiceException(apiExceptionMock))
                .thenReturn(new ServiceException(exceptionMessage));

        ServiceException serviceException = exceptionUtilMock.logAndConvertToServiceException(apiExceptionMock);

        assertNotNull(serviceException);
        assertEquals(exceptionMessage, serviceException.getMessage());
    }
}