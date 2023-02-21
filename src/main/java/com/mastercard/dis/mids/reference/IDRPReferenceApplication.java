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

package com.mastercard.dis.mids.reference;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mastercard.dis.mids.reference.component.IDRPReference;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityAttributesResponseDTO;
import com.mastercard.dis.mids.reference.service.claimsidentity.signingvalidator.SigningValidator;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static com.mastercard.dis.mids.reference.constants.Constants.JWT_REGEX;
import static com.mastercard.dis.mids.reference.constants.Constants.REDIRECT_URI_PATTERN;
import static com.mastercard.dis.mids.reference.constants.Constants.UUID_REGEX;
import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;

@Slf4j
@SpringBootApplication
public class IDRPReferenceApplication implements CommandLineRunner {

    private static final String ERROR = "Error : ";
    private final IDRPReference idRpReference;

    private boolean exit = false;
    private final Scanner scanner;

    private String arid;
    private String code;
    private String clientAssertion;
    private String codeVerifier;

    public IDRPReferenceApplication(IDRPReference idRpReference) {
        this.idRpReference = idRpReference;
        scanner = new Scanner(System.in, "UTF-8");
    }

    public static void main(String[] args) {
        SpringApplication.run(IDRPReferenceApplication.class);
        System.exit(0);
    }

    @Override
    public void run(String... args) {
        while (!exit) {
            showMenu();
            handleOption(scanner.nextLine());
            pressAnyKey();
        }
        scanner.close();
        System.exit(0);
    }

    void showMenu() {
        log.info(" <--- Welcome to ID RP Reference Application --->");
        for (Map.Entry<String, String> entry : MENU_MAP.entrySet()) {
            log.info(entry.getValue());
        }
    }

    void aridLogScanner() {
        log.info("<<--- To Start Claims Identity Attribute, Please enter arid and press Enter--->>");
        takeArid(scanner.nextLine());
    }

    private void takeArid(String aridScanned) {
        if (aridScanned != null && !aridScanned.isEmpty() && UUID_REGEX.matcher(aridScanned).matches()) {
            log.info("Arid entered : " + aridScanned);
            arid = aridScanned;
            clientAssertionLogScanner();
        } else {
            log.info("Invalid arid entered, please enter a valid arid");
            aridLogScanner();
        }
    }

    private void clientAssertionLogScanner() {
        log.info("Please enter client assertion and press Enter");
        takeClientAssertion(scanner.nextLine());
    }

    private void takeClientAssertion(String clientAssertionScanned) {
        if (clientAssertionScanned != null && !clientAssertionScanned.isEmpty() && JWT_REGEX.matcher(clientAssertionScanned).matches()) {
            log.info("Entered client assertion :  " + clientAssertionScanned);
            clientAssertion = clientAssertionScanned;
            codeLogScanner();
        } else {
            log.info("Invalid client assertion, enter a valid client assertion and press Enter");
            clientAssertionLogScanner();
        }
    }

    private void codeLogScanner() {
        log.info("Please enter code and Press Enter");
        takeCode(scanner.nextLine());
    }

    void takeCode(String codeScanned) {
        log.info("Code entered : " + codeScanned);
        if (codeScanned != null && !codeScanned.isEmpty() && JWT_REGEX.matcher(codeScanned).matches()) {
            code = codeScanned;
            codeVerifierLogScanner();
            redirectUriLogScanner();
        } else {
            log.info("Invalid code entered, enter a valid code and press Enter");
            codeLogScanner();
        }
    }

    private void codeVerifierLogScanner() {
        log.info("Please enter code verifier and press Enter");
        takeCodeVerifier(scanner.nextLine());
    }

    private void takeCodeVerifier(String codeVerifierScanned) {
        log.info("Code verifier entered : " + codeVerifierScanned);
        if (codeVerifierScanned != null && !codeVerifierScanned.isEmpty()) {
            codeVerifier = codeVerifierScanned;
        } else {
            log.info("Invalid code verifier entered, enter a valid code and press Enter");
            codeVerifierLogScanner();
        }
    }

    void redirectUriLogScanner() {
        log.info("Please enter redirect uri and press ENTER:");
        takeRedirectUri(scanner.nextLine());
    }

    private void takeRedirectUri(String redirectUriScanned) {
        log.info("Redirect Uri entered : " + redirectUriScanned);
        if (redirectUriScanned != null && !redirectUriScanned.isEmpty() && REDIRECT_URI_PATTERN.matcher(redirectUriScanned).matches()) {
            performClaimsIdentityAttributes(arid, clientAssertion, code, codeVerifier, redirectUriScanned);
        } else {
            log.info("Invalid redirect uri entered, please enter a valid redirect uri and press Enter");
            redirectUriLogScanner();
        }
    }


    void handleOption(String option) {
        log.info("Your option : " + option);
        switch (option) {
            case "1":
                aridLogScanner();
                break;
            case "2":
                this.exit = true;
                break;
            default:
                log.info("Invalid option!");
        }
    }

    void performClaimsIdentityAttributes(String arid, String clientAssertion, String code, String codeVerifier, String redirectUri) {
        try {
            SasAccessTokenRequestDTO sasAccessTokenRequestDTO = new SasAccessTokenRequestDTO();
            sasAccessTokenRequestDTO.setClientAssertion(clientAssertion);
            sasAccessTokenRequestDTO.setCode(code);
            sasAccessTokenRequestDTO.setRedirectUrl(redirectUri);
            sasAccessTokenRequestDTO.setCodeVerifier(codeVerifier);
            sasAccessTokenRequestDTO.setGrantType("authorization_code");
            sasAccessTokenRequestDTO.setClientAssertionType("urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
            SasAccessTokenResponseDTO sasAccessTokenResponseDTO = idRpReference.callSasAccessToken(sasAccessTokenRequestDTO);

            Response response = idRpReference.callClaimsIdentityAttributes(arid, sasAccessTokenResponseDTO.getAccess_token());
            String responseBody = Objects.requireNonNull(response.body()).string();

            log.info("<<--- Claims Identity Attributes Response --->>\n" + responseBody);
            if (responseBody.contains("encryptedData\":\"")) {
                log.info("<<--- Decrypted Response --->>\n");
                responseBody = idRpReference.decryptClaimsIdentityAttributesBody(responseBody);
                log.info(responseBody);
            }

            // Optional: to verify the proof (JWS) see the function below
            verifyJWSProof(responseBody);
        } catch (ServiceException serviceException) {
            log.info(ERROR + serviceException.getMessage());
            log.info("<<--- NOTE: A \"Bad Request\" Error is expected in this scenario when using the ID for Relying Parties Reference Implementation, as the Auth Code submitted in the request being a point in time value which has since timed out on the Authorization Server --->>");
            log.info("<<--- Claims Identity Attributes Failed --->>");
        } catch (Exception e) {
            log.info(ERROR + e.getMessage());
            log.info("<<--- Claims Identity Attributes Failed --->>");
        }
    }

    boolean verifyJWSProof(String identityAttrResponseBody) {
        boolean matches = false;
        try {
            ClaimsIdentityAttributesResponseDTO responseDTO = new ObjectMapper()
                    .registerModule(new JavaTimeModule()) // deserialize dates
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // ignore unnecessary properties
                    .readValue(identityAttrResponseBody, ClaimsIdentityAttributesResponseDTO.class);

            matches = new SigningValidator().verify(Objects.requireNonNull(responseDTO.getVerifiableCredential().getProof()).getJws());
            if (matches) {
                log.info("<<--- Signature Verification Successful --->>");
            } else {
                log.info("<<--- Signature Verification Failed - Please check your JWS token --->>");
            }
        } catch (Exception e) {
            log.info("<<--- Signature Verification Failed --->>");
            log.info(ERROR + e.getMessage());
        }
        return matches;
    }

    void pressAnyKey() {
        if (!exit) {
            log.info("Press ENTER to continue...");
            scanner.nextLine();
        }
    }
}