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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilsTest {

    @Mock
    File file;

    @Mock
    KeyStore keyStore;

    @Test
    void testJweEncryptThrowsServiceException() {
        ClassPathResource encryptionCertificateFile = new ClassPathResource("/certificate_not_found.pem");
        String encryptionCertificateFingerPrint = "336b870e55e33c6d278a5661006a6017ef88430bc8d00cc5fd7989a4c077bfe2";

        assertThatThrownBy(() -> EncryptionUtils.jweEncrypt(null, encryptionCertificateFile, encryptionCertificateFingerPrint))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    void whenTryToEncryptWithInvalidCertificateAndFingerPrint_thenThrowsServiceException() {
        Mockito.doReturn("pathToFile").when(file).getPath();
        Resource createdResource = new FileSystemResource(file);

        assertThatThrownBy(() -> EncryptionUtils.jweEncrypt("{\"dataKey\":\"dataValue\"}", createdResource, "encryptionCertificateFingerPrint"))
                .isInstanceOf(ServiceException.class);
    }

    @Test
    void whenTryToDecryptWithInvalidKey_thenThrowsException() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyStoreSpi keyStoreSpiMock = mock(KeyStoreSpi.class);
        this.keyStore = new KeyStore(keyStoreSpiMock, null, "test"){};
        keyStore.load(null);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyStore.getKey(anyString(), any(char[].class));

        assertThatThrownBy(() -> EncryptionUtils.jweDecrypt("Invalid Encrypted Object", rsaPrivateKey))
                .hasMessage("You must include at least an encryption certificate or a decryption key");
    }
}