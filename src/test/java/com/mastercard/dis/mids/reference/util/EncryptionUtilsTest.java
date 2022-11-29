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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
}