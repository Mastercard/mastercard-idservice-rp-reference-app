package com.mastercard.dis.mids.reference.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    KeyStore keyStore;

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