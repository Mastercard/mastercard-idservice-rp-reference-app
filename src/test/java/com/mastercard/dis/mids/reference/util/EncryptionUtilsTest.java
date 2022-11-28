package com.mastercard.dis.mids.reference.util;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilsTest {

    @Mock
    File file;

    @Test
    void testJweEncryptThrowsServiceException() {
        ClassPathResource encryptionCertificateFile = new ClassPathResource("/certificate_not_found.pem");
        String encryptionCertificateFingerPrint = "336b870e55e33c6d278a5661006a6017ef88430bc8d00cc5fd7989a4c077bfe2";

        assertThatThrownBy(() -> EncryptionUtils.jweEncrypt(null, encryptionCertificateFile, encryptionCertificateFingerPrint))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    void whenTryToDecryptWithInvalidCertificateAndFingerPrint_thenThrowsServiceException() {
        Mockito.doReturn("pathToFile").when(file).getPath();
        Resource createdResource = new FileSystemResource(file);

        assertThatThrownBy(() -> EncryptionUtils.jweEncrypt("{\"dataKey\":\"dataValue\"}", createdResource, "encryptionCertificateFingerPrint"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void Should_return_service_exception_when_cert_file_not_found() {
        String filePath = "cert_file_name_not_found_mock.cert";
        Resource createdResource = new FileSystemResource(filePath);

        Exception exception = assertThrows(ServiceException.class, () -> {
            EncryptionUtils.jweEncrypt("{\"dataKey\":\"dataValue\"}", createdResource, "encryptionCertificateFingerPrint");
        });

        assertNotNull(exception);
        assertEquals(filePath, exception.getMessage());
    }

    @Test
    void Should_throw_parse_exception() throws IOException {
        RSAPrivateKey privateKey = mock(RSAPrivateKey.class);
        Exception exception = assertThrows(ServiceException.class, () -> {
            EncryptionUtils.jweDecrypt("{\"dataKey\":\"dataValue\"}", privateKey);
        });
        assertNotNull(exception);
    }
}