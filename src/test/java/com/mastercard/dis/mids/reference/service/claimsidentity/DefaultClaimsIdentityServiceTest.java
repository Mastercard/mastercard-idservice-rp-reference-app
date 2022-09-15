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

package com.mastercard.dis.mids.reference.service.claimsidentity;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.session.SessionContext;
import com.mastercard.dis.mids.reference.util.ExceptionUtil;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import okhttp3.Call;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.ApiResponse;
import org.openapitools.client.model.ClaimsIdentityAttributes;
import org.openapitools.client.model.FraudDetectionResponseMeta;
import org.openapitools.client.model.FraudDetectionScore;
import org.openapitools.client.model.VerifiableCredential;
import org.openapitools.client.model.VerifiableCredentialJwsToken;
import org.openapitools.client.model.VerifiableCredentialSubject;
import org.openapitools.client.model.VerifiableCredentialSubjectClaimValue;

import java.lang.reflect.Type;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mastercard.dis.mids.reference.constants.AppConstants.X_MIDS_USERAUTH_SESSIONID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultClaimsIdentityServiceTest {

    private static final String FIRST_NAME = "John";
    private static final String TRANSACTION_ID = "6385eb6c-68d6-4646-be45-811a40449f56";
    private static final String ISSUER = "https://idservice.com/";
    private static final String FRAUD_SIGNAL = "anonymous_ip";
    private static final String FRAUD_BAND = "GREEN";
    private static final String JWS = "jws";
    private static final String FIRST_NAME_KEY ="firstName";
    private static final String CLAIM_ATTRIBUTES_KEY = "claimsAttributes";
    private static final String ARID = "df52649e-4096-456a-bca0-751ee470009f";
    private static final String ACCESS_TOKEN = "jwt";

    private static final List<String> AT_CONTEXT = Arrays.asList("https://www.w3.org/2018/credentials/v1");
    private static final List<String> TYPE = Arrays.asList("VerifiableCredential");

    private static final int FRAUD_VALUE = 0;
    private static final int ASSURANCE_LEVEL = 1;

    private static final OffsetDateTime LOCAL_DATE_TIME = OffsetDateTime.now();

    private static final String PUBLIC_KEY = "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";
    private static final String MESSAGE_PAYLOAD = "{\"content\":\"ABC\"}";
    private static final String SIGNATURE_FROM_CAAS_BASE64_ENCODED = "GX+OntURpY9LxqkZ8ongbt2OG+qiX9i5lb1CTauhjmADdgeJ917YOBR7PTExp6dNOCddQG2ME6OVGOOiELVtlvzqi5ic0N3nYbSGlmpKvySp5q9sbo69nT4RrBMK7V7JfQ1EM8Y/lGocIogl5B0LIu4GH3SRhfY90yrOCJIYVHOHy9EWNRvh4SAVfqgu0JdBc7j/U+Oo1YrSyFZqV/AMsQ5/IHthddDuuU4iIagNI15OsFEpK/fOOxzNYHqJujWvh5Fj5kUYOqw01ju/VGQzo5fO5YHIxFOVumJR7UYOpw4/v4iIrroGLYHZ7UOEWCSI76BDPCwE5ycRG8+0wiZY5A==";

    @InjectMocks
    private DefaultClaimsIdentityService defaultClaimsIdentityService;

    @Mock
    private ApiClient apiClientMock;

    @Mock
    private ExceptionUtil exceptionUtilMock;

    Map<String, List<String>> headers;

    List<String> headersList;

    @BeforeEach
    void setUp() throws Exception {
        headers = new HashMap<>();
        headersList = new ArrayList<>();
        headersList.add(X_MIDS_USERAUTH_SESSIONID);
        headers.put(X_MIDS_USERAUTH_SESSIONID, headersList);
        when(apiClientMock.buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any())).thenReturn(mock(Call.class));
        SessionContext.create(X_MIDS_USERAUTH_SESSIONID);
    }

    @Test
    void claimsIdentityAttributes_SuccessfulApiCall_ShouldReturnResponse() throws ApiException {
        when(apiClientMock.execute(any(Call.class), any(Type.class)))
                .thenReturn(new ApiResponse<>(200, headers, getClaimsIdentityAttributes()));

        ClaimsIdentityAttributes result = defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN);

        verify(apiClientMock, atLeastOnce()).buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
        verify(apiClientMock, times(1)).execute(any(Call.class), any(Type.class));
        assertAll(() -> assertNotNull(result),
                () -> assertEquals(TRANSACTION_ID, result.getTransactionId()),
                () -> assertEquals(AT_CONTEXT, result.getVerifiableCredential().getAtContext()),
                () -> assertEquals(TYPE, result.getVerifiableCredential().getType()),
                () -> assertEquals(ISSUER, result.getVerifiableCredential().getIssuer()),
                () -> assertEquals(LOCAL_DATE_TIME, result.getVerifiableCredential().getIssuanceDate()),
                () -> assertEquals(TRANSACTION_ID, result.getVerifiableCredential().getCredentialSubject().getId()),
                () -> assertEquals(ASSURANCE_LEVEL, result.getVerifiableCredential().getCredentialSubject().getClaimsAttributes().get(CLAIM_ATTRIBUTES_KEY).getAssuranceLevel()),
                () -> assertEquals(LOCAL_DATE_TIME, result.getVerifiableCredential().getCredentialSubject().getClaimsAttributes().get(CLAIM_ATTRIBUTES_KEY).getLastVerifiedDate()),
                () -> assertTrue(result.getVerifiableCredential().getCredentialSubject().getClaimsAttributes().get(CLAIM_ATTRIBUTES_KEY).getValues().toString().contains(FIRST_NAME)),
                () -> assertEquals(LOCAL_DATE_TIME.toString(), result.getVerifiableCredential().getCredentialSubject().getClaimsAttributes().get(CLAIM_ATTRIBUTES_KEY).getDataMatch()),
                () -> assertEquals(JWS, result.getVerifiableCredential().getProof().getJws()),
                () -> assertEquals(FRAUD_VALUE, result.getFraudDetectionMeta().getScore().getValue()),
                () -> assertEquals(FRAUD_BAND, result.getFraudDetectionMeta().getScore().getBand()),
                () -> assertEquals(FRAUD_SIGNAL, result.getFraudDetectionMeta().getScore().getSignals().get(0))
        );
    }

    @Test
    void claimsIdentityAttributes_FailureApiCall_ShouldThrowApiException() throws ApiException {
        when(apiClientMock.execute(any(Call.class), any(Type.class))).thenThrow(new ApiException());
        when(exceptionUtilMock.logAndConvertToServiceException(any(ApiException.class))).thenThrow(new ServiceException("Error while processing request"));

        assertThrows(ServiceException.class, () -> defaultClaimsIdentityService.claimsIdentityAttributes(ARID, ACCESS_TOKEN));

        verify(apiClientMock, atMostOnce()).buildCall(anyString(), anyString(), anyList(), anyList(), any(), anyMap(), anyMap(), anyMap(), any(), any());
        verify(apiClientMock, atMostOnce()).execute(any(Call.class), any(Type.class));
    }

    @Test
    public void claimsIdentityAttributes_VerifyJWSGeneratedUsingCaasSignerStub() throws Exception {
        when(apiClientMock.execute(any(Call.class), any(Type.class)))
                .thenReturn(new ApiResponse<>(200, headers, getClaimsIdentityAttributes()));

        ClaimsIdentityAttributes result = defaultClaimsIdentityService.claimsIdentityAttributes(ARID,ACCESS_TOKEN);

        //BUILD JWS Object
        JWSHeader jwsHeader = generateFullJwsHeader();
        Payload jwsPayload = new Payload(MESSAGE_PAYLOAD);
        JWSObject jwsTokenObject = new JWSObject(jwsHeader, jwsPayload);

        //Checks if signingInput only contains header and payload portions
        String jwsTokenSigningInput = new String(jwsTokenObject.getSigningInput());
        assertTrue(jwsTokenSigningInput.matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+"));

        //Signs the JWS Object
        JWSSigner jwsSigner = new CaasSignerStub(SIGNATURE_FROM_CAAS_BASE64_ENCODED);
        jwsTokenObject.sign(jwsSigner);
        //Checks if signed jwsObject contains 3 parts (header, payload, signature)
        String jwsTokenCaasString = jwsTokenObject.serialize();
        assertTrue(jwsTokenCaasString.matches("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]+"));

        //Verify jwsString using public certificate
        JWSObject jwsObject = JWSObject.parse(jwsTokenCaasString);
        JWSHeader jwsHeaderValue = jwsObject.getHeader();
        List<com.nimbusds.jose.util.Base64> x509CertificateChainBase64 = jwsHeaderValue.getX509CertChain();
        com.nimbusds.jose.util.Base64 x509CertificateBase64 = x509CertificateChainBase64.get(0);
        byte[] x509CertificateBytes = x509CertificateBase64.decode();
        X509Certificate x509Certificate = X509CertUtils.parseWithException(x509CertificateBytes);
        JWSVerifier jwsVerifier = new RSASSAVerifier((RSAPublicKey) x509Certificate.getPublicKey());
        assertTrue(jwsObject.verify(jwsVerifier));
        assertEquals(MESSAGE_PAYLOAD, jwsObject.getPayload().toString());
        boolean verified = jwsTokenObject.verify(jwsVerifier);
        assertTrue(verified);
    }

    private JWSHeader generateFullJwsHeader() {
        List<com.nimbusds.jose.util.Base64> x5c = new ArrayList<>();
        com.nimbusds.jose.util.Base64 base64PublicKey = com.nimbusds.jose.util.Base64.from(PUBLIC_KEY);
        x5c.add(base64PublicKey);
        X509Certificate x509PublicKeyCertificate = X509CertUtils.parse(base64PublicKey.decode());
        Base64URL sha256Thumbprint = X509CertUtils.computeSHA256Thumbprint(x509PublicKeyCertificate);

        return new JWSHeader.Builder(JWSAlgorithm.RS256)
                .x509CertSHA256Thumbprint(sha256Thumbprint)
                .x509CertChain(x5c)
                .build();
    }

    private ClaimsIdentityAttributes getClaimsIdentityAttributes() {
        FraudDetectionScore fraudDetectionScore = new FraudDetectionScore();
        fraudDetectionScore.setValue(FRAUD_VALUE);
        fraudDetectionScore.setBand(FRAUD_BAND);
        fraudDetectionScore.setSignals(Arrays.asList(FRAUD_SIGNAL));

        FraudDetectionResponseMeta fraudDetectionResponseMeta = new FraudDetectionResponseMeta();
        fraudDetectionResponseMeta.setScore(fraudDetectionScore);

        Map<String, String> claimsValues = new HashMap<>();
        claimsValues.put(FIRST_NAME_KEY, FIRST_NAME);

        VerifiableCredentialSubjectClaimValue verifiableCredentialSubjectClaimValue = new VerifiableCredentialSubjectClaimValue();
        verifiableCredentialSubjectClaimValue.setAssuranceLevel(ASSURANCE_LEVEL);
        verifiableCredentialSubjectClaimValue.setLastVerifiedDate(LOCAL_DATE_TIME);
        verifiableCredentialSubjectClaimValue.setValues(claimsValues);
        verifiableCredentialSubjectClaimValue.setDataMatch(LOCAL_DATE_TIME.toString());

        Map<String, VerifiableCredentialSubjectClaimValue> claimAttributes = new HashMap<>();
        claimAttributes.put(CLAIM_ATTRIBUTES_KEY, verifiableCredentialSubjectClaimValue);

        VerifiableCredentialSubject verifiableCredentialSubject = new VerifiableCredentialSubject();
        verifiableCredentialSubject.setId(TRANSACTION_ID);
        verifiableCredentialSubject.setClaimsAttributes(claimAttributes);

        VerifiableCredentialJwsToken verifiableCredentialJwsToken = new VerifiableCredentialJwsToken();
        verifiableCredentialJwsToken.setJws(JWS);

        VerifiableCredential verifiableCredential = new VerifiableCredential();
        verifiableCredential.setAtContext(AT_CONTEXT);
        verifiableCredential.setType(TYPE);
        verifiableCredential.setIssuer(ISSUER);
        verifiableCredential.setIssuanceDate(LOCAL_DATE_TIME);
        verifiableCredential.setCredentialSubject(verifiableCredentialSubject);
        verifiableCredential.setProof(verifiableCredentialJwsToken);

        ClaimsIdentityAttributes claimsIdentityAttributes = new ClaimsIdentityAttributes();
        claimsIdentityAttributes.setTransactionId(TRANSACTION_ID);
        claimsIdentityAttributes.setVerifiableCredential(verifiableCredential);
        claimsIdentityAttributes.setFraudDetectionMeta(fraudDetectionResponseMeta);

        return claimsIdentityAttributes;
    }
}