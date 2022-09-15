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

package com.mastercard.dis.mids.reference.exception;

import org.junit.jupiter.api.Test;
import org.openapitools.client.model.ErrorResponseErrors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceExceptionTest {

    private static final String ERROR_MESSAGE = "message1";

    @Test
    void create_withStringAndException_works() {
        Exception exception = new Exception();

        ServiceException serviceException = new ServiceException (ERROR_MESSAGE, exception);

        assertEquals( ERROR_MESSAGE, serviceException.getMessage());
    }

    @Test
    void create_withStringAndErrorResponseErrors_works() {
        ErrorResponseErrors errorResponseErrors = new ErrorResponseErrors();

        ServiceException serviceException = new ServiceException(ERROR_MESSAGE, errorResponseErrors);

        assertEquals(serviceException.getServiceErrors().getErrors().getError(), errorResponseErrors.getError());
        assertEquals(ERROR_MESSAGE, serviceException.getMessage());
    }

    @Test
    void create_withExceptionAndErrorResponseErrors_works() {
        ErrorResponseErrors errorResponseErrors = new ErrorResponseErrors();
        Exception exception = new Exception(ERROR_MESSAGE);

        ServiceException serviceException = new ServiceException(exception, errorResponseErrors);

        assertEquals(serviceException.getCause(), exception);
    }
}