package com.mastercard.dis.mids.reference.service.claimsidentity;

import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ClaimsSharingApiAdapterTest {

    @InjectMocks
    private ClaimsSharingApiAdapter claimsSharingApiAdapter;

    @Test
    void Should_return_not_found_response_for_given_arid_with_jwt() throws IOException, ApiException {
        String aridMock = "aaaa0000-bbbb-0000-cccc-xx11zz22yy22";
        String accessTokenMock = "jwt";

        ReflectionTestUtils.setField(claimsSharingApiAdapter, "localVarApiClient",  new ApiClient());

        Response response = claimsSharingApiAdapter.retrieveClaimsIdentityAttributesCall(aridMock, accessTokenMock, null);

        assertNotNull(response);
        assertEquals(404, response.code());
    }

    @Test
    void Should_return_not_found_response_for_given_arid() throws IOException, ApiException {
        String aridMock = "aaaa0000-bbbb-0000-cccc-xx11zz22yy22";
        String accessTokenMock = "";

        ReflectionTestUtils.setField(claimsSharingApiAdapter, "localVarApiClient", new ApiClient());

        Response response = claimsSharingApiAdapter.retrieveClaimsIdentityAttributesCall(aridMock, accessTokenMock, null);

        assertNotNull(response);
        assertEquals(404, response.code());
    }

    @Test
    void Should_return_null_when_arid_null() {
        String aridMock = null;
        String accessTokenMock = "";

        Exception exception = assertThrows(ApiException.class, () -> {
            claimsSharingApiAdapter.retrieveClaimsIdentityAttributes(aridMock, accessTokenMock);
        });

        assertNotNull(exception);
        assertTrue(ApiException.class.equals(exception.getClass()));
    }

    @Test
    void Should_return_api_client(){
        ApiClient apiClientMock = new ApiClient();
        ApiClient apiClientMockSet = new ApiClient();

        ClaimsSharingApiAdapter claimsSharingApiAdapterMock = new ClaimsSharingApiAdapter(apiClientMock);
        claimsSharingApiAdapterMock.setApiClient(apiClientMockSet);

        ApiClient client = claimsSharingApiAdapterMock.getApiClient();

        assertNotEquals(client, apiClientMock);
        assertEquals(client, apiClientMockSet);
    }
}
