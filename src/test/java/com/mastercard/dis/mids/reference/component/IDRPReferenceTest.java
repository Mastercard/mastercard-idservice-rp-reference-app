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

import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityService;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class IDRPReferenceTest {

    private static final String ARID = "d22a5b3e-dbb5-4f77-ac74-30040fef4561";
    private static final String ACCESS_TOKEN = "jwt";
    private static final String ENCRYPTED_BODY = "{\"encryptedData\":\"eyJraWQiOiJmMWU2MjczZTVkOWNiZTdkNzBiY2I3OGVkMDdiZWQ4MmU2ZjdiZGM1NTIyYTFjYTc1ZjEzMGFjNzIxNzc3ZDU4IiwiZW5jIjoiQTEyOEdDTSIsImFsZyI6IlJTQS1PQUVQLTI1NiJ9.eIFxpoTzFv8Jqs108tMJ_tF7FbGLrbNu6pSc6jRkNBpgD58zGay2rBXMQ2o9aDgNn68M8i9ruWMlF6no6u7Pi8hrecGhPhkH3VXw0u3QncbMTTd69WBZP2Qt9wzVHdETXpOdxnC0-uNrwjGpfylr_XzXBAzMNHaQwYQDH9Uel_Bom410hzXfK4GNh0vOe6El_ri53gbG2P_UzoPwolZMH3_IlsKywzp9phMhkPnpXATIdvadW-AWMUWlfRYioqLHZaiTs04snIZfJB4eAySdm4lPOP75Gg6NP8j-9uVy9rub1nLDAVW0A6RPKiMu4inJVzKbJp1FP7aIBJzeGqsXjw.8empi1QNEcgxrtOe.H7_If5Nlt5dUw6rdn2ul8xEsCDxzSkVIWKxOAEGnENXse1D-lFEhSPonoGELuSWvtV0q8EX_DM_E2wW0rzaRBBlU0YgwQ8O3ik_x21mfyQYuLuIGT-VRvBO-lAkTPzb28YU0pM3tRpThTF8SP8-ySOHj8B0-Oe_ZVvRPBb3ib-TVO-tgSzwIcwueOZeukFAMj3kNCz1UamUDSElYy1N1_-9MXvYQ_hOHZn2HxUS0vXJVyZDI7JZhM2Yl68vObaPDxNOYiN7RWNh8mT3P7QbCNg6cw9ye199CjOZUu_zJtjcmIWjOpgcPfZFxTuK88HXvrAhLR97so5-AUuupDGqeVGNqxQux96V3pR0sjz7mP1MiLz0PECiiR_bmptx7RhFNYK2Evqf4c9qOJEkc5KeKCXYFy3-wADs_efF-f2HrzyfuaaSvTZi4GTjdqABjIJunTSk7gBRysLKVQkqadgL_7C4EWxiePmPY2TfoyrgL3Us-jFG37hyjoEMLWi2X9tRJOlZsAQlU-OmvQ-7Aks9_ZKhyoDF8cuW7Z5wj0qgglGc-Zx-TZxKUWqKegCLCtV0nTtW6O63iQQ040UolqcbFtwE5JdeYCRUIr_ca8jfHc4aNAfl_FY_9O_KpYUluigvqnpC6qHHX4CGEH5jVljDC0zoPVFXjJ99bNxcmdylfZHEgFTgW2S_HbLsp1GQEXO86DgfZ1m3mWV9qgTe36LmZX9_FLwO4RKQ9KIWN3Z6O9wev2IW5iQTtyBtQkumMsLgnm9QraQi-SEEjbuZb8U9eNYnj-eBcavW73lTnGzJTB7V22-VK5bm2XWthesnVlcH2aq-P-rl9eOdZZgHdXmCDB8kKbYeHVF8OKh8AG2m8mH1dIY-xPDeGY2MQOgHltmn0GcmoG0CGLsVTsH8kdw9Tj7JZU2H06de4TheC_HgFJzesWtlzX8MkUZuTGVgFCsUJ3jPXjGtocYNi_p0vwsUMokC3_AlHVOJ0rF01__NGTHWD4XZLIkmIPMgOokDxtX8ejOO3i4Ucjmd17XGLCZDR_D7qhqUEAdhYm0SBSqf2cyXoK-yBGFMEc5ZgZqfewSIA_NlM8cKPG0LqQw-R4R6oPVMDaMKnY8GkjIZRmquV2zGQbxPd1Vq2DdYwAcbDsK254GkqRO4axr93I-kVr2-fjueKxsNJQusy2tR88Ae7UDrbY7S7jnK-2ws08ZMpgCk4Kpwm6end3zetgyzf2jw7bSEiPkV11bWhZXQTkILXpk-OgI4qzESgMgUQ_7jhE6REg73LZaHRkC2p2aomGF4Yh6zq62-So95quC7XHExdIr4z-p60ueCsKQhZB48KVWKfmdHu2rtweJ-7hY1jPZ7T6zSLOe-_FVDsmA1C3-WSKeQA9lLSCqN8x1JZxuMK_LJ-H1f2wZ9hIla0_bVTgKKY5XUok7XY6qXDz6dr--QALnm-90ooIV3yqt3_lkFQnykZv56lcX10vD2x03pzyTuL12ejKZYR2W46vIT4YRImRX6Am8V-yriyB2YW4nzsfxOA_X0pHJ6qEmAXr4aLYLIOQt_7bpD9RkmA2zeAtfwkwv9cwTyOXWvZJeK1cQwRtAB-aC0-7v-pgirURUvzNLNcfoL7aMYwUlSyuv-U_oJU-MDqSNzsthhmxKV-tIko3PblT4zDeSGn6VPDpgPBoOun7ovWB4IF1ZY7vKLJMIiaHGrNoLJuBRbOrDpjcrTKnxcvynh5lDj3628ehLtzOqmXhYbSon5HaIKnTdBO-x3T-zPPgIxBlJ7kw42TLTPY6_9t9jpqTq9cnB3CYT4Pnweibr0-VbmTgGMTSRxP9aqM-SkXcnXVcgYCiOMkVLYdLf147ERkO59So98oBbx5HdTMd_En--HFaJE0BIfE3iKcGIlENAUF5abbucZw3dzsww3603mzZ4h2jk2curnk8jl-IH1SVLc2pjqBJIJihhK6EXxkrdvuDLWu9OsCDnmEykx4q1VYGZ287_m5VSiQXKqmy0ZQ39xS2bUFS3h2RVa4HH08tltbfRRqj0DpcKuGGfSYZu5R_rgPmQBAUlKN1wgZwe66RIUTZFR0hmErdczj-bE7G5b7bVapy1cpFBLFCbUgke2JCcvNHWtK0ssSMNv2e3wpqx1G_Hei15OCSeiMFJ6EUXwAITUmT5PcV2mofjdKuKQCIVMyoQ_o254fheq6vkvDAKtwsFr98V5_9jqM0OMt_W_F0qRNl_8ah7QriwdP6mCW1V8AoFAwSoHy_1drYWS1wC9hSoffPHC1bCuE1rrwCCvZ9QhAP_cSfAwLaUWZ7HhKnxj83Lm0cJh-NKq0Lz-z3w20on11DPtXZwyeP4favDg-ocvgk8nAPVO0gRejx6S3pfKa31G85zCAl4RflwO0hy1N1czRtOv1iIZwJSoF4e9_sHpL7-IsxDcszhlGal3n-BziHinnBzhfKz2b_2_rFg0AUm1q6nqc-iOvhUEEMpdbKG5WBAEOHJZk-eSSVhjG6C2StBom7VVwR-0WqgVN_Xu7s05yMQbayUmuq3g-cWehuTod6AMDkCIfJ1ncBpAqfKpQpa2ISTFZHIrAMy8CK0se7AnV3Th4Qvh0rdP1bDjnTXigVhk34gjifLtL7RFE3k2xZ-ETUWUPuHr6Nf9p65pNBJCYLDUSIw3msW7lqbKsIItLmj0IBhnH5S3neTvirKTYCFXC1G8V1w26fqG2-x7pefZ9xLcTsVVb8l0JWO38QLeG2LPrVt3j27kjcnTzQQH1vblujOKahBT2PfWMKr42-PwtElVQ6F4sBliScM3BDvDL_nDQSb_LCfGT6mTG12Kq43eBqi0KatgkyDwvZTO8YR7DduuGo5NhjZeS2uLyfZM1QD8UqnbnSo4bv25HF_Six1fY3FTR7wcu5CjmJARB3RJlklSlIWGDWeiY9KtdeECL6HST7oGbIuoFK__rPcHr2o3WjwESnWu7s_xFoKiT9ajKf1mYuc9RDF30UhfGYU3o9KBxcGeouoLVOj3jI08Pav7uwaZ8IBrGW1EhDTt5WLsasN2qFujbSkjbGUZ7iHsL7wj-DuvU94Ztw7CGaDFtcsX1JN6QbQC0y58iT3o0_SIbbTL6NtqqHtseXHG9sTcxrbCbIOjLzcXB2qUa59_7DQw4XUCWUdtxy2ONQzD1p9H8mNV1C0KDBt_VW9_zPXNOcjd-12rMIFp2bPf0WbS-rntNt5cIbLwYir0BiyS9YG_SvGpp915BoTZObvCT4onhT5cc2adRlh56Tjb6YxV0qpzzfv6nDEzPAmF4qMdzeh46fjC0KBlKYr-KSOu3K3nENTOeuiK9KaZNIBAnFJGLT6mCzPjsLpL2IUnqPZ1iYTr6cVcejbQ2QlUewfl_HWun2kaiqhR5X9cY6UcP4yaOK_1cC-WmOKxQBGHEFWmNzanMWv0zf_KDVNtiJztpeCzBqkI9L7g1E1gndMfGzF49bws_IXLEb2pLSpZ208C-i79XJYqLCIEC9YYRuk4o_S9EB6cnfdwS1AnuleqBJ2vKRJP3aAcEBE1km9m9iJv1sUyS-pCsTnVghqfOU8wHfOD2ewbJFsD4O4dMJ8NrsB77z3IzF6_Js-QThN2K3ZLor4N1fdL3dVC-hjEsyIpT-bt6BzDyJXXws82o6IHF_s7XukaqWxBLl-pv9kx1iqyz5rCSH_nuOmmJURXmzaNfS2--9gmbN3a6X4czsNNspi2FHLwKZP_4udBFnuZ5AS242vqZK9tukt5Q2qoXraM11dRWaPk70Uytnu0nKpk81fX4U9m3LIAyYosAibsgigAaB8qz1yE6mjOkVioBaZAl2E2m0ugfROU3k1ew-HekKMJpEdIGpKcQKmBI0eLEhtJVI2tMhXA6Ce2Bb5xi5kU_llmHuNg0mtlrdxMt9BM58GpRYlEKa1Vypo96JDCbSUFXZudAxfaeMDLcdh_l6B7QzUTLb2xeSbdvht9qPTs5QoRZyD83GBHwg-kRWOtclBUUhYL1SnKyQA8knKSI3VYwflxA7ca-x4j6FKmTCVPoZrb1bU9I5RMS7NNuaCxRNFRS_a3-sCP9kfoezvcVHQ55RMjQeDSKcah4TAOs1p_qjQg9dC6y7AM-4rful8MLLl1qhN4nk3dw1Z55fn-o7935_3RcTwJTBy7mXO17kUgQvJgbFXptiaAaFtZeJMgiKFCDU5mohLNcp4qZd9v3tfhix7w5s5bA07byxzqKVTUPsIVIIYoeSjc5yl_fZAo.kmed_NVuHDQD0JcFUan82Q\"}";
    @InjectMocks
    IDRPReference idRpReference;

    @Mock
    ClaimsIdentityService claimsIdentityServiceMock;

    @Mock
    SasAccessTokenService sasAccessTokenServiceMock;

    @Test
    @DisplayName("Call Claims Identity Attributes API - Valid - Successful")
    void callClaimsIdentityAttributes_Valid_SuccessfulCall() {
        idRpReference.callClaimsIdentityAttributes(ARID, ACCESS_TOKEN);

        verify(claimsIdentityServiceMock, times(1)).claimsIdentityAttributes(eq(ARID), eq(ACCESS_TOKEN));
        verifyNoMoreInteractions(claimsIdentityServiceMock);
    }

    @Test
    @DisplayName("Call SAS Access Token API - Valid - Successful")
    void callSasAccessToken_Valid_SuccessfulCall() {
        SasAccessTokenRequestDTO sasAccessTokenRequestDTO = new SasAccessTokenRequestDTO();
        sasAccessTokenRequestDTO.setClientAssertionType("urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        sasAccessTokenRequestDTO.setClientAssertion("eyJraWQiOiJjdkF4d0FqaENJRnY5MFZnQ3M0b2o1Wmp2SmoyUlRXSC1sWHh3X2diMWRmMWV");
        sasAccessTokenRequestDTO.setGrantType("authorization_code");
        sasAccessTokenRequestDTO.setCodeVerifier("my-code-verifier-string");
        sasAccessTokenRequestDTO.setCode("eyJ4NXQjUzI1NiI6Ild...");
        sasAccessTokenRequestDTO.setRedirectUrl("https://venomsoft.ie");
        idRpReference.callSasAccessToken(sasAccessTokenRequestDTO);

        verify(sasAccessTokenServiceMock, times(1)).sasAccessTokenResponse(eq(sasAccessTokenRequestDTO));
        verifyNoMoreInteractions(sasAccessTokenServiceMock);
    }

    @Test
    @DisplayName("Call Decrypt Claims Identity Attributes - Null Key Store Value - Service Exception")
    void CallDecryptClaimsIdentityAttributes_NullKeyStoreValue_ServiceException() {
        ReflectionTestUtils.setField(idRpReference, "decryptionKeystore", null);
        Exception exception = assertThrows(ServiceException.class, () ->
                idRpReference.decryptClaimsIdentityAttributesBody(ENCRYPTED_BODY)
        );
        assertNotNull(exception);
        assertEquals(ServiceException.class, exception.getClass());
        assertTrue(exception.getMessage().contains("NullPointerException"));
    }

    @Test
    @DisplayName("Call Decrypt Claims Identity Attributes - Not Valid Key Store Value - Service Exception")
    void CallDecryptClaimsIdentityAttributes_NotValidKeyStoreValue_ServiceException() {
        ClassPathResource decryptPath = new ClassPathResource("/encryption-mc-RP.p12");
        ReflectionTestUtils.setField(idRpReference, "decryptionKeystore", decryptPath);
        ReflectionTestUtils.setField(idRpReference, "decryptionKeystoreAlias", "Alias");
        ReflectionTestUtils.setField(idRpReference, "decryptionKeystorePassword", "KeyAlias");
        Exception exception = assertThrows(ServiceException.class, () ->
                idRpReference.decryptClaimsIdentityAttributesBody(ENCRYPTED_BODY)
        );

        assertNotNull(exception);
        assertEquals(ServiceException.class, exception.getClass());
        assertTrue(exception.getMessage().contains(".p12 Key not found"));
    }

    @Test
    @DisplayName("Call Decrypt Claims Identity Attributes - Invalid Encrypted Body - Service Exception")
    void CallDecryptClaimsIdentityAttributes_InvalidEncryptedBody_ServiceException(){
        Exception exception = assertThrows(ServiceException.class, () ->
                idRpReference.decryptClaimsIdentityAttributesBody("Invalid Encrypted Body")
        );

        assertNotNull(exception);
        assertEquals(ServiceException.class, exception.getClass());
        assertTrue(exception.getMessage().contains("Unable to decrypt response from server"));
    }

    @Test
    @DisplayName("Call Decrypt Claims Identity Attributes - Valid - Decryption Enabled")
    void CallDecryptClaimsIdentityAttributes_Valid_DecryptionEnabled(){
        ReflectionTestUtils.setField(idRpReference, "decryptionEnabled", true);

        assertTrue(idRpReference.isDecryptionEnabled());
    }
}