package com.mastercard.dis.mids.reference.service.claimsidentity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimsSharingApiAdapterTest {

    @InjectMocks
    private ClaimsSharingApiAdapter claimsSharingApiAdapter;

    @Mock
    private ApiClient mockApiClient;

    @Test
    @DisplayName("Should return API exception when ARID is null")
    void Should_return_exception_when_arid_null() {
        String aridMock = null;
        String accessTokenMock = "";

        Exception exception = assertThrows(ApiException.class, () ->
                claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(aridMock, accessTokenMock, false)
        );

        assertNotNull(exception);
        assertEquals(ApiException.class, exception.getClass());
    }

    @Test
    @DisplayName("Should return a valid Api Client")
    void Should_return_api_client() {
        ApiClient apiClientMock = new ApiClient();
        ApiClient apiClientMockSet = new ApiClient();

        ClaimsSharingApiAdapter claimsSharingApiAdapterMock = new ClaimsSharingApiAdapter(apiClientMock);
        claimsSharingApiAdapterMock.setApiClient(apiClientMockSet);

        ApiClient client = claimsSharingApiAdapterMock.getApiClient();

        assertNotEquals(client, apiClientMock);
        assertEquals(client, apiClientMockSet);
    }

    @ParameterizedTest
    @ValueSource(strings = {"jwt", "jws"})
    @DisplayName("Should return API exception when error occurs in Api Client's buildCall method")
    void retrieveClaimsIdentityAttributesCall_returns_ApiException(String accessToken) throws IOException, ApiException {
        String arid = "df52649e-4096-456a-bca0-751ee470009f";
        String[] localVarAccepts = { "application/json" };
        String headerString = "{\"Authorization\" : \"Bearer " + accessToken + "\", " + "\"X-Encrypted-Payload\" : \"false\", \"Accept\" : \"application/json\", \"Content-Type\" : null}";
        Map header = new ObjectMapper().readValue(headerString, HashMap.class);

        when(mockApiClient.selectHeaderAccept(localVarAccepts)).thenReturn(localVarAccepts[0]);
        when(mockApiClient.buildCall(
                null,
                "/idservice-rp/claims/" + arid + "/identity-attributes",
                "GET",
                new ArrayList<>(),
                new ArrayList<>(),
                null,
                header,
                new HashMap<>(),
                new HashMap<>(),
                new String[]{},
                null
        )).thenThrow(ApiException.class);

        ApiException exception = assertThrows(ApiException.class, () ->
                claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(arid, accessToken, false));

        assertNotNull(exception);
        assertEquals(ApiException.class, exception.getClass());
    }

    @ParameterizedTest
    @ValueSource(strings = {"jwt", "jws"})
    @DisplayName("Should return Null exception so cant the flow")
    void retrieveClaimsIdentityAttributesCall_returns_NullException(String accessToken) {
        String arid = "df52649e-4096-456a-bca0-751ee470009f";
        String[] localVarAccepts = { "application/json" };

        when(mockApiClient.selectHeaderAccept(localVarAccepts)).thenReturn(localVarAccepts[0]);

        Exception exception = assertThrows(Exception.class, () ->
                claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(arid, accessToken, false));

        assertNotNull(exception);
        assertEquals(NullPointerException.class, exception.getClass());
    }

}