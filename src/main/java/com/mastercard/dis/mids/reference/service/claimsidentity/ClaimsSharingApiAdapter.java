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

import com.google.gson.reflect.TypeToken;
import org.openapitools.client.ApiCallback;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.Configuration;
import org.openapitools.client.Pair;
import org.openapitools.client.model.ClaimsIdentityAttributes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generated code from ClaimsSharingApi.
 * This adapter only contains one change to fix an issue with the existing library.
 */
public class ClaimsSharingApiAdapter {

    private ApiClient localVarApiClient;

    public ClaimsSharingApiAdapter() {
        this(Configuration.getDefaultApiClient());
    }

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
     * @param _callback Callback for upload/download progress
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
    public okhttp3.Call retrieveClaimsIdentityAttributesCall(String arid, String accessToken,  final ApiCallback _callback) throws ApiException {
        Object localVarPostBody = null;

        /*
            This is the only line that was changed from the generated code.
            The issue from the generated code is that it does not parse the path with path parameter as expected.
         */
        // create path and map variables
        String localVarPath = new StringBuilder()
                .append("/idservice-rp")
                .append("/claims/")
                .append(arid)
                .append("/identity-attributes")
                .toString();

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        Map<String, String> localVarHeaderParams = new HashMap<String, String>();
        Map<String, String> localVarCookieParams = new HashMap<String, String>();
        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        localVarHeaderParams.put("Authorization", "Bearer " +accessToken);

        String[] localVarAuthNames = new String[] {  };
        return localVarApiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAuthNames, _callback);
    }

    @SuppressWarnings("rawtypes")
    private okhttp3.Call retrieveClaimsIdentityAttributesValidateBeforeCall(String arid, String accessToken, final ApiCallback _callback) throws ApiException {

        // verify the required parameter 'arid' is set
        if (arid == null) {
            throw new ApiException("Missing the required parameter 'arid' when calling retrieveClaimsIdentityAttributes(Async)");
        }


        okhttp3.Call localVarCall = retrieveClaimsIdentityAttributesCall(arid, accessToken,  _callback);
        return localVarCall;

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
    public ClaimsIdentityAttributes retrieveClaimsIdentityAttributes(String arid, String accessToken) throws ApiException {
        ApiResponse<ClaimsIdentityAttributes> localVarResp = retrieveClaimsIdentityAttributesWithHttpInfo(arid, accessToken);
        return localVarResp.getData();
    }

    /**
     * Retrieve Identity Attributes
     * Retrieve the Identity Attributes from a given ARID.
     * @param arid UUID representing the ARID (required)
     * @return ApiResponse&lt;ClaimsIdentityAttributes&gt;
     * @throws ApiException If fail to call the API, e.g. server error or cannot deserialize the response body
     * @http.response.details
    <table summary="Response Details" border="1">
    <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
    <tr><td> 200 </td><td> Success </td><td>  * X-Transaction-ID - A random 128-bit UUID representing the transaction <br>  </td></tr>
    <tr><td> 400 </td><td> Something was wrong with the request. </td><td>  -  </td></tr>
    <tr><td> 404 </td><td> Request didn&#39;t match an existing resource. </td><td>  -  </td></tr>
    </table>
     */
    public ApiResponse<ClaimsIdentityAttributes> retrieveClaimsIdentityAttributesWithHttpInfo(String arid, String accessToken) throws ApiException {
        okhttp3.Call localVarCall = retrieveClaimsIdentityAttributesValidateBeforeCall(arid, accessToken, null);
        Type localVarReturnType = new TypeToken<ClaimsIdentityAttributes>(){}.getType();
        return localVarApiClient.execute(localVarCall, localVarReturnType);
    }

    /**
     * Retrieve Identity Attributes (asynchronously)
     * Retrieve the Identity Attributes from a given ARID.
     * @param arid UUID representing the ARID (required)
     * @param _callback The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException If fail to process the API call, e.g. serializing the request body object
     * @http.response.details
    <table summary="Response Details" border="1">
    <tr><td> Status Code </td><td> Description </td><td> Response Headers </td></tr>
    <tr><td> 200 </td><td> Success </td><td>  * X-Transaction-ID - A random 128-bit UUID representing the transaction <br>  </td></tr>
    <tr><td> 400 </td><td> Something was wrong with the request. </td><td>  -  </td></tr>
    <tr><td> 404 </td><td> Request didn&#39;t match an existing resource. </td><td>  -  </td></tr>
    </table>
     */
    public okhttp3.Call retrieveClaimsIdentityAttributesAsync(String arid, String accessToken, final ApiCallback<ClaimsIdentityAttributes> _callback) throws ApiException {

        okhttp3.Call localVarCall = retrieveClaimsIdentityAttributesValidateBeforeCall(arid, accessToken, _callback);
        Type localVarReturnType = new TypeToken<ClaimsIdentityAttributes>(){}.getType();
        localVarApiClient.executeAsync(localVarCall, localVarReturnType, _callback);
        return localVarCall;
    }
}