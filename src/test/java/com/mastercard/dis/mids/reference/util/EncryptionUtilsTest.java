package com.mastercard.dis.mids.reference.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.KeyStore;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EncryptionUtilsTest {

    @Mock
    KeyStore keyStore;

    @Test
    void whenTryToDecryptWithInvalidKey_thenThrowsException() {

        assertThatThrownBy(() -> EncryptionUtils.jweDecrypt("eyJraWQiOiJmMWU2MjczZTVkOWNiZTdkNzBiY2I3OGVkMDdiZWQ4MmU2ZjdiZGM1NTIyYTFjYTc1ZjEzMGFjNzIxNzc3ZDU4IiwiZW5jIjoiQTEyOEdDTSIsImFsZyI6IlJTQS1PQUVQLTI1NiJ9.eyJlbmNyeXB0ZWRPYmplY3QiOiJUZXN0IE9iamVjdCJ9.xRl2g9V-9owAdRR_.ROrXH1T0NsF9AGNZPwY9CgQ-KLepVQsjnlZkDzYbVNnQrtt_3YMGnVGOyVnuhm17XrKfp04h1_8AW-rT8wHZCbjC4Bskg9ab4OF6PVH2vQv1b409C0rr4XjRAJ6W4rXdj9ZeYI6_JmNyp04JVu3gFVDe0FKPydY.nGAcTSeH2ry-XQlpuAMnKw", null))
                .hasMessage("You must include at least an encryption certificate or a decryption key");
    }
}