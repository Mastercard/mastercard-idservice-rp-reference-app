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

import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.Configuration;
import org.openapitools.client.Pair;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SasAccessTokenApi {

    private ApiClient localVarApiClient;

    public SasAccessTokenApi() {
        this(Configuration.getDefaultApiClient());
    }

    public SasAccessTokenApi(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }


    @SuppressWarnings("rawtypes")
    public okhttp3.Call createSasAccessTokenCall(SasAccessTokenRequestDTO sasAccessTokenRequest, final ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;

        String localVarPath = "/com-mastercard-saat/saat-auth/api/oauth2/token";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();
        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {
                "application/x-www-form-urlencoded"
        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        localVarFormParams.put("grant_type",sasAccessTokenRequest.getGrantType());
        localVarFormParams.put("redirect_uri",sasAccessTokenRequest.getRedirectUrl());
        localVarFormParams.put("code",sasAccessTokenRequest.getCode());
        localVarFormParams.put("client_assertion_type",sasAccessTokenRequest.getClientAssertionType());
        localVarFormParams.put("client_assertion",sasAccessTokenRequest.getClientAssertion());
        localVarFormParams.put("code_verifier",sasAccessTokenRequest.getCodeVerifier());

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(localVarPath, "POST", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);

    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call createSasAccessTokenBeforeCall(SasAccessTokenRequestDTO sasAccessTokenRequest, final ApiCallback _callback) throws ApiException {
        if (sasAccessTokenRequest == null) {
            throw new ApiException("Missing the required parameter 'sasAccessTokenRequest' when calling createSasAccessToken(SasAccessTokenRequestDTO)");
        }
        okhttp3.Call localVarCall = createSasAccessTokenCall(sasAccessTokenRequest, _callback);
        return localVarCall;
    }

    public SasAccessTokenResponseDTO createSasAccessToken(SasAccessTokenRequestDTO sasAccessTokenRequest) throws ApiException {
        ApiResponse<SasAccessTokenResponseDTO> localVarResp = createSasAccessTokenWithHttpInfo(sasAccessTokenRequest);
        return localVarResp.getData();
    }

    public ApiResponse<SasAccessTokenResponseDTO> createSasAccessTokenWithHttpInfo(SasAccessTokenRequestDTO sasAccessTokenRequest) throws ApiException {
        okhttp3.Call localVarCall = createSasAccessTokenBeforeCall(sasAccessTokenRequest, null);
        Type localVarReturnType = new TypeToken<SasAccessTokenResponseDTO>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }
}