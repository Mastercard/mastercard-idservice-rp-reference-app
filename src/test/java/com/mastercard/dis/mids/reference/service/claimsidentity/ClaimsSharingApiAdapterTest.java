package com.mastercard.dis.mids.reference.service.claimsidentity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;

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
