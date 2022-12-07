package com.mastercard.dis.mids.reference.service.claimsidentity.signingvalidator;

import com.google.common.hash.Hashing;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.ParseException;

@Slf4j
@Service
public class SigningValidator {

    public boolean verify(String jws) {
        try {
            JWSObject jwsObject = getJwsObjectWithHashedPayload(jws);
            return verifySignature(jwsObject);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | CertificateException |
                 ParseException exception) {
            throw new ServiceException("[Couldn't verify signature] " + exception.getMessage(), exception);
        }
    }

    /**
     * Verify JWS signature
     *
     * @param jwsObject
     * @return
     * @throws NoSuchAlgorithmException
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws CertificateException
     */
    private boolean verifySignature(JWSObject jwsObject) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateException {
        // Extracting certificate from certificate chain included in the JWS header
        byte[] certificate = jwsObject.getHeader().getX509CertChain().get(0).decode();

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(getPublicKey(certificate));
        sig.update(jwsObject.getSigningInput());
        byte[] decodedSignature = jwsObject.getSignature().decode();
        return sig.verify(decodedSignature);
    }

    /**
     * Build JWS Object from identity-attributes response
     *
     * @param jws
     * @throws ParseException
     */
    private JWSObject getJwsObjectWithHashedPayload(String jws) throws ParseException {
        JWSObject jwsObject = JWSObject.parse(jws);
        String payloadAsString = jwsObject.getPayload().toString();
        jwsObject = new JWSObject(
                jwsObject.getHeader().getParsedBase64URL(),
                getHashedPayload(payloadAsString),
                jwsObject.getSignature()

        );
        return jwsObject;
    }

    /**
     * To validade the identity-attributes JWS signature, the payload needs to be hashed
     *
     * @param payloadAsString
     */
    private Payload getHashedPayload(String payloadAsString) {
        return new Payload(Hashing.sha256()
                .hashString(payloadAsString, StandardCharsets.UTF_8)
                .asBytes());
    }

    /**
     * Extract public key from certificate
     *
     * @param certificate
     * @throws CertificateException
     */
    private PublicKey getPublicKey(byte[] certificate) throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        X509Certificate cer = (X509Certificate) fact.generateCertificate(new ByteArrayInputStream(certificate));
        return cer.getPublicKey();
    }
}
