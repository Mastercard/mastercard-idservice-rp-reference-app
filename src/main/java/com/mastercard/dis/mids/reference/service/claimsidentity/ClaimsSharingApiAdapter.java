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

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated code from ClaimsSharingApi.
 * This adapter only contains one change to fix an issue with the existing library.
 */
@Slf4j
public class ClaimsSharingApiAdapter {
    private final OkHttpClient client = new OkHttpClient();

    private ApiClient localVarApiClient;

    public ClaimsSharingApiAdapter(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return localVarApiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.localVarApiClient = apiClient;
    }

    /**
     * Build call for retrieveClaimsIdentityAttributes
     * @param arid UUID representing the ARID (required)
     * @param apiCallback Callback for upload/download progress
     * @return Call to execute
     * @throws ApiException If fail to serialize the request body object
     * @http.response.details
    <table summary="Response Details" border="1">
    <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
    <tr><td> 200 </td><td> Success </td><td>  * X-Transaction-ID - A random 128-bit UUID representing the transaction <br>  </td></tr>
    <tr><td> 400 </td><td> Something was wrong with the request. </td><td>  -  </td></tr>
    <tr><td> 404 </td><td> Request didn&#39;t match an existing resource. </td><td>  -  </td></tr>
    </table>
     */
    public Response retrieveClaimsIdentityAttributesCall(String arid, String accessToken, final ApiCallback<?> apiCallback) throws ApiException, IOException {
        Object localVarPostBody = null;

        /*
            This is the only line that was changed from the generated code.
            The issue from the generated code is that it does not parse the path with path parameter as expected.
         */
        // create path and map variables
        String localVarPath = String.format("/%s/%s/%s/%s",
                "idservice-rp",
                "claims",
                arid,
                "identity-attributes");

        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();
        Map<String, String> localVarHeaderParams = new HashMap<>();
        Map<String, String> localVarCookieParams = new HashMap<>();
        Map<String, Object> localVarFormParams = new HashMap<>();
        final String[] localVarAccepts = {
                "application/json"
        };
        final String localVarAccept = localVarApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = localVarApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);
        String[] localVarAuthNames = new String[]{};
        localVarHeaderParams.put("Authorization", "Bearer " +accessToken);
        if (accessToken.equals("jwt")) {
            okhttp3.Call localVarCall = localVarApiClient.buildCall(null, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, apiCallback);
            return localVarCall.execute();
        } else {
            localVarApiClient.buildCall(null, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, apiCallback);
            Request request = localVarApiClient.buildRequest(null, localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, apiCallback);
            log.info("<<--- Claims Identity Attributes Request --->>\n" + request);
            log.info("<<--- Claims Identity Attributes Path --->>" + localVarPath);
            Call call = client.newCall(request);
            return call.execute();
        }
    }

    @SuppressWarnings("rawtypes")
    private Response retrieveClaimsIdentityAttributesValidateBeforeCall(String arid, String accessToken, final ApiCallback apiCallback) throws ApiException, IOException {
        // verify the required parameter 'arid' is set
        if (arid == null) {
            throw new ApiException("Missing the required parameter 'arid' when calling retrieveClaimsIdentityAttributes(Async)");
        }
        return retrieveClaimsIdentityAttributesCall(arid, accessToken,  apiCallback);
    }

    /**
     * Retrieve Identity Attributes
     * Retrieve the Identity Attributes from a given ARID.
     * @param arid UUID representing the ARID (required)
     * @return ClaimsIdentityAttributes
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
    <table summary="Response Details" border="1">
    <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
    <tr><td> 200 </td><td> Success </td><td>  * X-Transaction-ID - A random 128-bit UUID representing the transaction <br>  </td></tr>
    <tr><td> 400 </td><td> Something was wrong with the request. </td><td>  -  </td></tr>
    <tr><td> 404 </td><td> Request didn&#39;t match an existing resource. </td><td>  -  </td></tr>
    </table>
     */
    public Response retrieveClaimsIdentityAttributes(String arid, String accessToken) throws ApiException, IOException {
        return retrieveClaimsIdentityAttributesValidateBeforeCall(arid, accessToken, null);
    }
}