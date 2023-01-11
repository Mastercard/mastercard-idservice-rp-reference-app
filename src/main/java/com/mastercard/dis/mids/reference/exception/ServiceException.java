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

package com.mastercard.dis.mids.reference.exception;

import org.openapitools.client.model.ErrorResponse;
import org.openapitools.client.model.ErrorResponseErrors;

public class ServiceException extends RuntimeException {

    private final transient ErrorResponse errors = new ErrorResponse();

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception e) {
        super(message, e);
    }

    public ServiceException(String message, ErrorResponseErrors serviceErrors) {
        super(message);
        errors.setErrors(serviceErrors);
    }

    public ServiceException(Exception e, ErrorResponseErrors serviceErrors) {
        super(e);
        errors.setErrors(serviceErrors);
    }

    public ErrorResponse getServiceErrors() {
        return errors;
    }
}