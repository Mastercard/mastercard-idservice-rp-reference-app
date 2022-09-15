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

package com.mastercard.dis.mids.reference.interceptor;

import com.mastercard.developer.utils.AuthenticationUtils;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class EncryptionDecryptionInterceptor implements Interceptor {

    @Value("${mastercard.api.encryption.certificateFile}")
    private Resource encryptionCertificateFile;

    @Value("${mastercard.api.encryption.fingerPrint}")
    private String encryptionCertificateFingerPrint;

    @Value("${mastercard.api.decryption.keystore}")
    private Resource decryptionKeystore;

    @Value("${mastercard.api.decryption.alias}")
    private String decryptionKeystoreAlias;

    @Value("${mastercard.api.decryption.keystore.password}")
    private String decryptionKeystorePassword;

    @Value("${mastercard.client.encryption.enable}")
    private boolean isEncryptionEnable;

    @Value("${mastercard.client.decryption.enable}")
    private boolean isDecryptionEnable;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = handleRequest(chain.request());
        Response response = chain.proceed(request);
        return handleResponse(request, response);
    }

    private Request handleRequest(Request request) {
        log.info("request.url(): {}", request.url().uri().getPath());
        if (isEncryptionEnable && isEncryptionRequired(request)) {
            try {
                String body = bodyToString(request);
                JSONObject requestJson = (JSONObject) JSONValue.parse(body);
                String encryptedData = EncryptionUtils.jweEncrypt(requestJson.toJSONString(), encryptionCertificateFile, encryptionCertificateFingerPrint);
                JSONObject encryptedRequestJson = new JSONObject();
                encryptedRequestJson.put("encryptedData", encryptedData);
                log.info("Encrypted Payload sending to server: {}", encryptedRequestJson);
                Request.Builder post = request
                        .newBuilder()
                        .headers(request.headers())
                        .post(RequestBody.create(MediaType.parse("application/json"), encryptedRequestJson.toJSONString()));
                return post.build();
            } catch (Exception e) {
                log.error("Unable to encrypt request data to server", e);
                throw new ServiceException("Unable to encrypt request data to server", e);
            }
        }
        return request;
    }

    private Response handleResponse(Request request, Response encryptedResponse) {
        if (isDecryptionEnable && isDecryptionRequired(request)) {
            try {
                if (encryptedResponse.code() != 200) {
                    return encryptedResponse; // We will receive encrypted payload only for 200 response
                }
                ResponseBody responseBody = encryptedResponse.body();
                String encryptedResponseStr = responseBody.string();
                log.info("Encrypted Payload received from server: {}", encryptedResponseStr);
                JSONObject encryptedResponseJson = (JSONObject) JSONValue.parse(encryptedResponseStr);
                PrivateKey signingKey = AuthenticationUtils.loadSigningKey(decryptionKeystore.getFile().getAbsolutePath(), decryptionKeystoreAlias, decryptionKeystorePassword);
                String decryptedPayload = EncryptionUtils.jweDecrypt(encryptedResponseJson.getAsString("encryptedData"), (RSAPrivateKey) signingKey);

                JSONObject responseJson = (JSONObject) JSONValue.parse(decryptedPayload);

                Response.Builder responseBuilder = encryptedResponse.newBuilder();
                ResponseBody decryptedBody = ResponseBody.create(responseBody.contentType(), responseJson.toJSONString());

                return responseBuilder
                        .body(decryptedBody)
                        .header("Content-Length", String.valueOf(decryptedBody.contentLength()))
                        .build();
            } catch (Exception e) {
                log.error("Unable to decrypt response from server", e);
                throw new ServiceException("Unable to process response from server", e);
            }
        }

        return encryptedResponse;
    }

    private static String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    private boolean isEncryptionRequired(Request request) {
        List<String> list = Arrays.asList(
                "tba enc api");
        return list.stream().anyMatch(entry -> request.url().uri().getPath().contains(entry));
    }

    private boolean isDecryptionRequired(Request request) {
        List<String> list = Arrays.asList(
                "tba dec api");
        return list.stream().anyMatch(entry -> request.url().uri().getPath().contains(entry));
    }
}