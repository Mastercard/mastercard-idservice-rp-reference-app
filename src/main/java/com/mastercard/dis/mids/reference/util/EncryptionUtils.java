/*
 Copyright (c) 2021 Mastercard

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

import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptionUtils {

    private static final JWEAlgorithm ALGORITHM = JWEAlgorithm.RSA_OAEP_256;
    private static final EncryptionMethod ENCRYPTION_METHOD = EncryptionMethod.A256GCM;

    public static String jweEncrypt(String plainData, Resource crtFileName, String keyFingerPrint) throws JOSEException {

        RSAPublicKey rsaPublicKey = (RSAPublicKey) getPublicKeyFromCrt(crtFileName);
        return encryptWithPublicKey(plainData, rsaPublicKey, keyFingerPrint);
    }

    public static String jweDecrypt(String cipher, RSAPrivateKey privateKeyFile) {

        try {
            // Decrypt JWE with CEK directly, with the DirectDecrypter in promiscuous mode
            JWEObject jwe = JWEObject.parse(cipher);
            jwe.decrypt(new RSADecrypter(privateKeyFile));
            return jwe.getPayload().toString();
        } catch (ParseException | JOSEException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private static String encryptWithPublicKey(String plainData, RSAPublicKey rsaPublicKey, String keyFingerPrint) throws JOSEException {
        // Encrypt the JWE with the RSA public key + specified AES CEK
        final JWEHeader.Builder builder = new JWEHeader.Builder(ALGORITHM, ENCRYPTION_METHOD);

        builder.keyID(keyFingerPrint);

        final JWEHeader jweHeader = builder.build();
        JWEObject jwe = new JWEObject(
                jweHeader,
                new Payload(plainData));

        final RSAEncrypter encrypter = new RSAEncrypter(rsaPublicKey);

        jwe.encrypt(encrypter);
        return jwe.serialize();
    }

    private static PublicKey getPublicKeyFromCrt(Resource filePath) {
        try {
            InputStream is = filePath.getInputStream();
            CertificateFactory factory = CertificateFactory.getInstance("X.509", "SUN");
            return factory.generateCertificate(is).getPublicKey();
        } catch (IOException | CertificateException | NoSuchProviderException ke) {
            throw new ServiceException(ke.getMessage());
        }

    }
}