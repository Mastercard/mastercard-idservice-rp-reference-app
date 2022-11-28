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
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiException;
import org.openapitools.client.JSON;
import org.openapitools.client.model.ErrorResponseErrors;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionUtil {

    public ServiceException logAndConvertToServiceException(ApiException e) {
        log.error("Error while processing request {} {} ", e.getMessage(), e.getResponseBody());
        return new ServiceException(e, deserializeErrors(e.getResponseBody()));
    }

    private ErrorResponseErrors deserializeErrors(String body) {
        return JSON.deserialize(body, ErrorResponseErrors.class);
    }
}