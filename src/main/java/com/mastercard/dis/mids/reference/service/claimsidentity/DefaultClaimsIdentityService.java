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

import com.mastercard.dis.mids.reference.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.openapitools.client.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DefaultClaimsIdentityService implements ClaimsIdentityService {

    private final ClaimsSharingApiAdapter claimsSharingApiAdapter;
    private final ExceptionUtil exceptionUtil;

    @Autowired
    public DefaultClaimsIdentityService(ClaimsSharingApiAdapter claimSharingApiAdapter, ExceptionUtil exceptionUtil) {
        claimsSharingApiAdapter = claimSharingApiAdapter;
        this.exceptionUtil = exceptionUtil;
    }

    @Override
    public Response claimsIdentityAttributes(String arid, String accessToken, boolean isDecryptionEnabled) {
        try {
            return claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(arid, accessToken, isDecryptionEnabled);
        } catch (ApiException | IOException apiException) {
            throw exceptionUtil.logAndConvertToServiceException((ApiException) apiException);
        }
    }

}