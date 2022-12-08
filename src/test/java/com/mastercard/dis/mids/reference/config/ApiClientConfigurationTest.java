package com.mastercard.dis.mids.reference.config;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ApiClientConfigurationTest {

    @InjectMocks
    ApiClientConfiguration apiClientConfiguration;

    @Test
    void Should_throw_service_exception_when_keyFile_and_consumerKey_are_empty(){
        String message = ".p12 file or consumerKey does not exist, please add details in application.properties";
        Exception exception = assertThrows(ServiceException.class, () -> {
            apiClientConfiguration.initialize();
        });
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void Should_return_service_exception_when_any_error_occurs() {
        String message = "Error occurred while configuring ApiClient";

        Exception exception = assertThrows(ServiceException.class, () -> {
            apiClientConfiguration.apiClient();
        });
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

}


