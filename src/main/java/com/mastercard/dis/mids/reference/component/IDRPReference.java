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

package com.mastercard.dis.mids.reference.component;

import com.mastercard.developer.utils.AuthenticationUtils;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityService;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenService;
import com.mastercard.dis.mids.reference.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.interfaces.RSAPrivateKey;

@Component
@RequiredArgsConstructor
public class IDRPReference {

    private final ClaimsIdentityService claimsIdentityService;
    private final SasAccessTokenService sasAccessTokenService;

    @Value("${mastercard.api.decryption.keystore}")
    private Resource decryptionKeystore;

    @Value("${mastercard.api.decryption.alias}")
    private String decryptionKeystoreAlias;

    @Value("${mastercard.api.decryption.keystore.password}")
    private String decryptionKeystorePassword;

    @Value("${mastercard.client.decryption.enable:false}")
    private boolean decryptionEnabled;

    public Response callClaimsIdentityAttributes(String arid, String accessToken) {
        return claimsIdentityService.claimsIdentityAttributes(arid, accessToken);
    }

    public SasAccessTokenResponseDTO callSasAccessToken(SasAccessTokenRequestDTO sasAccessTokenRequestDTO) {
        return sasAccessTokenService.sasAccessTokenResponse(sasAccessTokenRequestDTO);
    }

    public String decryptClaimsIdentityAttributesBody(String encryptedBody) {
        JSONObject parse;
        RSAPrivateKey signingKey;
        try{
            parse = (JSONObject) JSONValue.parse(encryptedBody);
            signingKey = (RSAPrivateKey) AuthenticationUtils.loadSigningKey(new FileInputStream(decryptionKeystore.getFile()), decryptionKeystoreAlias, decryptionKeystorePassword);

        }catch (FileNotFoundException e) {
            throw new ServiceException(".p12 Key not found", e);
        }catch (NullPointerException e){
            throw new ServiceException("NullPointerException", e);
        }catch (Exception e){// Service exception já resolve o da main
            throw new ServiceException("Unable to decrypt response from server", e);
        }
        return EncryptionUtils.jweDecrypt(parse.getAsString("encryptedData"), signingKey);
    }

    public boolean isDecryptionEnabled() {
        return decryptionEnabled;
    }
}