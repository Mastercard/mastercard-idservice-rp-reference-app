/*
 Copyright (c) 2023 Mastercard

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

package com.mastercard.dis.mids.reference.util;

import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.encryption.JweConfig;
import com.mastercard.developer.encryption.JweConfigBuilder;
import com.mastercard.developer.encryption.jwe.JweObject;
import com.mastercard.developer.json.JsonEngine;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionUtils {

    public static String jweDecrypt(String cipher, RSAPrivateKey privateKeyFile) {
        try {
            // Decrypt JWE with CEK directly, with the DirectDecrypter in promiscuous mode
            JweConfig config = JweConfigBuilder.aJweEncryptionConfig()
                    .withDecryptionKey(privateKeyFile)
                    .build();
            JweObject jwe = JweObject.parse(cipher, JsonEngine.getDefault());
            return jwe.decrypt(config);
        } catch (GeneralSecurityException | EncryptionException e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
