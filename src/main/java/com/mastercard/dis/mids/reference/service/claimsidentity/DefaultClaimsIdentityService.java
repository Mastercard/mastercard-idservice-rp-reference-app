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

import com.mastercard.dis.mids.reference.session.SessionContext;
import com.mastercard.dis.mids.reference.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.model.ClaimsIdentityAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.mastercard.dis.mids.reference.constants.AppConstants.X_MIDS_USERAUTH_SESSIONID;

@Slf4j
@Service
public class DefaultClaimsIdentityService implements ClaimsIdentityService {

    private final ClaimsSharingApiAdapter claimsSharingApiAdapter;
    private final ExceptionUtil exceptionUtil;

    @Autowired
    public DefaultClaimsIdentityService(ApiClient apiClient, ExceptionUtil exceptionUtil) {
        claimsSharingApiAdapter = new ClaimsSharingApiAdapter(apiClient);
        this.exceptionUtil = exceptionUtil;
    }

    @Override
    public ClaimsIdentityAttributes claimsIdentityAttributes(String arid, String accessToken) {
        try {
            return claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(arid, accessToken);
        } catch (ApiException apiException) {
            throw exceptionUtil.logAndConvertToServiceException(apiException);
        }
    }


}