package com.mastercard.dis.mids.reference.service.claimsidentity;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.impl.BaseJWSProvider;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;

import java.util.Collections;
import java.util.Set;

public class CaasSignerStub extends BaseJWSProvider implements JWSSigner{

    private static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS = Collections.singleton(JWSAlgorithm.RS256);
    private String SIGNATURE_FROM_CAAS_BASE64_ENCODED;

    public CaasSignerStub(final String mockSignature) {
        super(SUPPORTED_ALGORITHMS);
        SIGNATURE_FROM_CAAS_BASE64_ENCODED = mockSignature;
    }

    @Override
    public Base64URL sign(JWSHeader jwsHeader, byte[] bytes) throws JOSEException {
        byte[] signatureBase64 = Base64.from(SIGNATURE_FROM_CAAS_BASE64_ENCODED).decode();
        return Base64URL.encode(signatureBase64);
    }
}
