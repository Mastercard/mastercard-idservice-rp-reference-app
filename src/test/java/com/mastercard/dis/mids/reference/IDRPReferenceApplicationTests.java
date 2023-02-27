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

import com.mastercard.dis.mids.reference.component.IDRPReference;
import com.mastercard.dis.mids.reference.extension.DisableSystemExit;
import com.mastercard.dis.mids.reference.extension.SystemExitException;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IDRPReferenceApplicationTests {

	private static final Map<String, String> MENU_MAP_TEST = new HashMap<>();

	@InjectMocks
	IDRPReferenceApplication idrpReferenceApplication;

	@Mock
	IDRPReference idRpReference;

	Scanner scanner;

	@BeforeEach
	void setup() {
		MENU_MAP_TEST.put("1",  "1)   Claims Identity Attributes");
		MENU_MAP_TEST.put("2",  "2)   Exit");
	}

	@Test
	void consoleMenu_runAndCheckValues_works() {
		for (Map.Entry<String, String> entry : MENU_MAP_TEST.entrySet()) {
			String valueMenu = MENU_MAP.get(entry.getKey());
			assertEquals(valueMenu, entry.getValue());
		}
	}

	@Test
	void console_showMenu_works() {
		IDRPReferenceApplication spyMIDSReferenceApplication = spy(new IDRPReferenceApplication(null));

		spyMIDSReferenceApplication.showMenu();

		verify(spyMIDSReferenceApplication, times(1)).showMenu();
	}

	@Test
	void console_handleOption_works() {

		IDRPReferenceApplication spyMIDSReferenceApplication = mock(IDRPReferenceApplication.class);

		spyMIDSReferenceApplication.handleOption("1");
		spyMIDSReferenceApplication.handleOption("2");

		verify(spyMIDSReferenceApplication, times(2)).handleOption(anyString());
	}

	@Test
	void perform_performClaimsIdentityAttributes_works_with_failed_verifyJWSProof() {
		SasAccessTokenRequestDTO tokenRequestDTO = new SasAccessTokenRequestDTO("authorization_code", "", "", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer", "", "");
		SasAccessTokenResponseDTO tokenResponseDTO = new SasAccessTokenResponseDTO("access",1,"id","lorem","jws", "token", "300");
		Request mockRequest = new Request.Builder()
				.url("https://mock_test_url.com")
				.build();
		Response response = new Response.Builder()
				.request(mockRequest)
				.protocol(Protocol.HTTP_2)
				.code(200)
				.message("mock response")
				.body(ResponseBody.create(
						"{'body':'success'}",
						MediaType.get("application/json; charset=utf-8")
				))
				.build();

		when(idRpReference.callSasAccessToken(tokenRequestDTO)).thenReturn(tokenResponseDTO);
		when(idRpReference.callClaimsIdentityAttributes("", tokenResponseDTO.getAccess_token())).thenReturn(response);

		idrpReferenceApplication.performClaimsIdentityAttributes("", "","", "","");

		verify(idRpReference, times(1)).callSasAccessToken(tokenRequestDTO);
	}

	@Test
	void perform_performClaimsIdentityAttributes_works_with_failed_verifyJWSProof_with_decryption_enabled() {
			SasAccessTokenRequestDTO tokenRequestDTO = new SasAccessTokenRequestDTO("authorization_code", "", "", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer", "", "");
			SasAccessTokenResponseDTO tokenResponseDTO = new SasAccessTokenResponseDTO("access",1,"id","lorem","jws", "token", "300");
		Request mockRequest = new Request.Builder()
				.url("https://mock_test_url.com")
				.build();
		Response response = new Response.Builder()
				.request(mockRequest)
				.protocol(Protocol.HTTP_2)
				.code(200)
				.message("mock response")
				.body(ResponseBody.create(
						"{'encryptedData':'eyJraWQiOiJmMWU2MjczZTVkOWNiZTdkNzBiY2I3OGVkMDdiZWQ4MmU2ZjdiZGM1NTIyYTFjYTc1ZjEzMGFjNzIxNzc3ZDU4IiwiZW5jIjoiQTEyOEdDTSIsImFsZyI6IlJTQS1PQUVQLTI1NiJ9.eyJlbmNyeXB0ZWRPYmplY3QiOiJUZXN0IE9iamVjdCJ9.xRl2g9V-9owAdRR_.ROrXH1T0NsF9AGNZPwY9CgQ-KLepVQsjnlZkDzYbVNnQrtt_3YMGnVGOyVnuhm17XrKfp04h1_8AW-rT8wHZCbjC4Bskg9ab4OF6PVH2vQv1b409C0rr4XjRAJ6W4rXdj9ZeYI6_JmNyp04JVu3gFVDe0FKPydY.nGAcTSeH2ry-XQlpuAMnKw'}",
						MediaType.get("application/json; charset=utf-8")
				))
				.build();

		when(idRpReference.isDecryptionEnabled()).thenReturn(true);
		when(idRpReference.decryptClaimsIdentityAttributesBody(anyString())).thenReturn("{\"example\":\"Lorem Ipsum\"}");
		when(idRpReference.callSasAccessToken(tokenRequestDTO)).thenReturn(tokenResponseDTO);
		when(idRpReference.callClaimsIdentityAttributes("", tokenResponseDTO.getAccess_token())).thenReturn(response);

		idrpReferenceApplication.performClaimsIdentityAttributes("", "","", "","");

		verify(idRpReference, times(1)).callSasAccessToken(tokenRequestDTO);
	}

	@Test
	void Should_return_false_when_verifies_signature_with_exception(){
		String identityAttributeResponseBodyErrorMock = "{\"transactionId\":\"95bcd8a4-fcb4-4dce-8626-d7a98666df45\",\"fraudDetectionMeta\":null,\"verifiableCredential\":{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://www.w3.org/2018/credentials/id/v1\"],\"type\":[\"VerifiableCredential\",\"ID\"],\"issuer\":\"https://idservice.com/\",\"issuanceDate\":\"2022-12-08T12:50:43Z\",\"credentialSubject\":{\"id\":\"737a3d24a05ceb98d196f82c3e9fa4e3b605c0ce52b978a176f7d16087cb068f\",\"claimsAttributes\":{\"address\":{\"assuranceLevel\":1,\"lastVerifiedDate\":\"2022-12-08T12:48:28.586Z\",\"claims\":{\"address\":\"2200 MASTERCARD BLVD, O FALLON, MO, 63368, USA\"},\"dataMatch\":\"FULL\"}}},\"proof\":{\"jws\":\"eyJ4NXQjUzI1NiI6Ilg1OWF0QTRIMHlxNWx6VnlMUnl2Ym9oNmJrZHRMZHRQT002ZlR0OEFZMUkiLCJ4NWMiOlsiTUlJRVJ6Q0NBeStnQXdJQkFnSUlEek1MV200c09jQXdEUVlKS29aSWh2Y05BUUVMQlFBd2dZVXhDekFKQmdOVkJBWVRBa0pGTVJ3d0dnWURWUVFLRXhOTllYTjBaWEpEWVhKa0lGZHZjbXgzYVdSbE1TUXdJZ1lEVlFRTEV4dEhiRzlpWVd3Z1NXNW1iM0p0WVhScGIyNGdVMlZqZFhKcGRIa3hNakF3QmdOVkJBTVRLVTFoYzNSbGNrTmhjbVFnU1ZSR0lFMWxjM05oWjJWeklGTnBaMjVwYm1jZ1UzVmlJRU5CSUVjeU1CNFhEVEl4TURVd05qSXlNVGMxTmxvWERUSTFNREl4TURFMk1qazBPVm93Z2FReEN6QUpCZ05WQkFZVEFrSkZNUkV3RHdZRFZRUUhFd2hYWVhSbGNteHZiekV1TUN3R0ExVUVDaE1sVFdGemRHVnlRMkZ5WkNCSmJuUmxjbTVoZEdsdmJtRnNJRWx1WTI5eWNHOXlZWFJsWkRFWk1CY0dBMVVFQ3hNUVJHbG5hWFJoYkNCSlpHVnVkR2wwZVRFM01EVUdBMVVFQXhNdVpHbHpMVzFwWkhNdGMyVnlkbWxqWlhNdGFuZHpMWE5wWjI0dGRHVnpkQzV0WVhOMFpYSmpZWEprTG1OdmJUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUx0RmZ0cFhHT1NYT1U2bXpEXC81YXJBTzhUNnphVENwQ0NtSkd2ZEZDYVhFNUhpczd5bmZ0N1dnNDlsR1dnTXdqN3pFdytLQmdCc2grOENxZ1VyUGhNa0hVaWhCcG9YbE9cL005emRUQ09XZEt1cjFOZFlGeFdzV3pHZVprcCt2Q2VtXC9lalJraUNza2kyeExCbHJsY1F3UnhPVDNIRUlsdnZtTlMwMmJ5SU5sbjJReWJaSHBwMkxpZDhkYlFVREVZU1hoeEk5cFdLcEFDZExBU3FUajhIK2VpK0xuMXBITTZtbVZNdCtueVFoMTR1K2FcLzdVa3ZlVzN3Zis4c0hFZmp6ZFVMcEFkaENMbGNnQWc5bVZuaDFPQ2lyYlN3WHVxXC9uWUs1TnA3UXR0dVV0bUhnSU1iV0llNFRyMVkybmlcL3dQRkQzek9cLzZJZjh0aXg2RDY4emNcLzM4Q0F3RUFBYU9CbVRDQmxqQTVCZ05WSFJFRU1qQXdnaTVrYVhNdGJXbGtjeTF6WlhKMmFXTmxjeTFxZDNNdGMybG5iaTEwWlhOMExtMWhjM1JsY21OaGNtUXVZMjl0TUE0R0ExVWREd0VCXC93UUVBd0lBZ0RBSkJnTlZIUk1FQWpBQU1CMEdBMVVkRGdRV0JCUjllS0xOVXYzSUVJenBydWFrTmp5M1RvdmVJREFmQmdOVkhTTUVHREFXZ0JSYlBFQlNEKzVQZ091YmF0U0cyWXRnM0g5WVZUQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFVZXkzZmNJN05HUTlMVlNhUHY4UGsyTldsWnV0eDdNRHJGbVROK0ZZSUpZVGRRMWlLS2RyM3BUSjdocUR1NFBkcmsrT3VDUnVEODhZNDZCMDNRNUt6TUo4MDRVbU1sUG1yVll3RzUyZ29WTURWc2RIenR3cFwvUURaWW1GSE51b3d2TU5VcHozQjk4dFp6VVwva1lYWXF6a2hkS1ZzbGE0anc4RnBySXlTY01cL2l6aEVsWEp3dDI5SXd3YnBMT0NKaGNmZVFmOXZCdlh0VTcrQ3orZFBHUFZTMXVPdUNkWkI2NWdxV2dFb1FxczdwUDdtXC9VYTFqejFhTFdISmpXYStwV3duWWVUM1ZlR1pSM3JNOFUxMldiaXc5Uk53b1R2UzlUd2wybllybzI4YUpsZnRWb0RGMnM0T3djWEplSlBTMDN5aDhzd2pta0RqOUhoUmdwakI3aUJBPT0iXSwiYWxnIjoiUlMyNTYifQ.eyJjbGFpbXNBdHRyaWJ1dGVzIjp7ImFkZHJlc3MiOnsibGFzdFZlcmlmaWVkRGF0ZSI6IjIwMjItMTItMDhUMTI6NDg6MjguNTg2WiIsImNsYWltcyI6eyJhZGRyZXNzIjoiMjIwMCBNQVNURVJDQVJEIEJMVkQsIE8gRkFMTE9OLCBNTywgNjMzNjgsIFVTQSJ9LCJhc3N1cmFuY2VMZXZlbCI6MSwiZGF0YU1hdGNoIjoiRlVMTCJ9fSwiaWQiOiI3MzdhM2QyNGEwNWNlYjk4ZDE5NmY4MmMzZTlmYTRlM2I2MDVjMGNlNTJiOTc4YTE3NmY3ZDE2MDg3Y2IwNjhmIn0.gMXXDbBZKlw7PyeZZIEliTQx-TB8sHnnqXXnb9fp9DZINGMjMHoXZLbQNsD-8YPYfUJX8xqtZHtDdwRxleYpRoSTJhwzUxju6gMwWhMMsTv5Vh7Gr4IH-CpYu83CYUstcKe4PJHVXDxT0BEtSuq2c_32nN_yXbPlPjJcCVzxYiFeqEpN7k65L9uKc-KEj_XphOE5YC16KnOm2YkqJd3jCOq3P96XoQkXGAsuIt67a2IvjgD3b3mbr1dGNSyssaPA0j-vzwAeJIEhnay7hr1-LHld-7BwRrGeoAva4myZvEa_xTw6X1hPC00KjZdg6iguLh57vT3iRO9fDNtP4ZIzO\"}}}";
		boolean matches = idrpReferenceApplication.verifyJWSProof(identityAttributeResponseBodyErrorMock);
		assertFalse(matches);
	}

	@Test
	void Should_return_true_when_verifies_valid_signature(){
		String identityAttributeResponseBodyMock = "{\"transactionId\":\"95bcd8a4-fcb4-4dce-8626-d7a98666df45\",\"fraudDetectionMeta\":null,\"verifiableCredential\":{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://www.w3.org/2018/credentials/id/v1\"],\"type\":[\"VerifiableCredential\",\"ID\"],\"issuer\":\"https://idservice.com/\",\"issuanceDate\":\"2022-12-08T12:50:43Z\",\"credentialSubject\":{\"id\":\"737a3d24a05ceb98d196f82c3e9fa4e3b605c0ce52b978a176f7d16087cb068f\",\"claimsAttributes\":{\"address\":{\"assuranceLevel\":1,\"lastVerifiedDate\":\"2022-12-08T12:48:28.586Z\",\"claims\":{\"address\":\"2200 MASTERCARD BLVD, O FALLON, MO, 63368, USA\"},\"dataMatch\":\"FULL\"}}},\"proof\":{\"jws\":\"eyJ4NXQjUzI1NiI6Ilg1OWF0QTRIMHlxNWx6VnlMUnl2Ym9oNmJrZHRMZHRQT002ZlR0OEFZMUkiLCJ4NWMiOlsiTUlJRVJ6Q0NBeStnQXdJQkFnSUlEek1MV200c09jQXdEUVlKS29aSWh2Y05BUUVMQlFBd2dZVXhDekFKQmdOVkJBWVRBa0pGTVJ3d0dnWURWUVFLRXhOTllYTjBaWEpEWVhKa0lGZHZjbXgzYVdSbE1TUXdJZ1lEVlFRTEV4dEhiRzlpWVd3Z1NXNW1iM0p0WVhScGIyNGdVMlZqZFhKcGRIa3hNakF3QmdOVkJBTVRLVTFoYzNSbGNrTmhjbVFnU1ZSR0lFMWxjM05oWjJWeklGTnBaMjVwYm1jZ1UzVmlJRU5CSUVjeU1CNFhEVEl4TURVd05qSXlNVGMxTmxvWERUSTFNREl4TURFMk1qazBPVm93Z2FReEN6QUpCZ05WQkFZVEFrSkZNUkV3RHdZRFZRUUhFd2hYWVhSbGNteHZiekV1TUN3R0ExVUVDaE1sVFdGemRHVnlRMkZ5WkNCSmJuUmxjbTVoZEdsdmJtRnNJRWx1WTI5eWNHOXlZWFJsWkRFWk1CY0dBMVVFQ3hNUVJHbG5hWFJoYkNCSlpHVnVkR2wwZVRFM01EVUdBMVVFQXhNdVpHbHpMVzFwWkhNdGMyVnlkbWxqWlhNdGFuZHpMWE5wWjI0dGRHVnpkQzV0WVhOMFpYSmpZWEprTG1OdmJUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUx0RmZ0cFhHT1NYT1U2bXpEXC81YXJBTzhUNnphVENwQ0NtSkd2ZEZDYVhFNUhpczd5bmZ0N1dnNDlsR1dnTXdqN3pFdytLQmdCc2grOENxZ1VyUGhNa0hVaWhCcG9YbE9cL005emRUQ09XZEt1cjFOZFlGeFdzV3pHZVprcCt2Q2VtXC9lalJraUNza2kyeExCbHJsY1F3UnhPVDNIRUlsdnZtTlMwMmJ5SU5sbjJReWJaSHBwMkxpZDhkYlFVREVZU1hoeEk5cFdLcEFDZExBU3FUajhIK2VpK0xuMXBITTZtbVZNdCtueVFoMTR1K2FcLzdVa3ZlVzN3Zis4c0hFZmp6ZFVMcEFkaENMbGNnQWc5bVZuaDFPQ2lyYlN3WHVxXC9uWUs1TnA3UXR0dVV0bUhnSU1iV0llNFRyMVkybmlcL3dQRkQzek9cLzZJZjh0aXg2RDY4emNcLzM4Q0F3RUFBYU9CbVRDQmxqQTVCZ05WSFJFRU1qQXdnaTVrYVhNdGJXbGtjeTF6WlhKMmFXTmxjeTFxZDNNdGMybG5iaTEwWlhOMExtMWhjM1JsY21OaGNtUXVZMjl0TUE0R0ExVWREd0VCXC93UUVBd0lBZ0RBSkJnTlZIUk1FQWpBQU1CMEdBMVVkRGdRV0JCUjllS0xOVXYzSUVJenBydWFrTmp5M1RvdmVJREFmQmdOVkhTTUVHREFXZ0JSYlBFQlNEKzVQZ091YmF0U0cyWXRnM0g5WVZUQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFVZXkzZmNJN05HUTlMVlNhUHY4UGsyTldsWnV0eDdNRHJGbVROK0ZZSUpZVGRRMWlLS2RyM3BUSjdocUR1NFBkcmsrT3VDUnVEODhZNDZCMDNRNUt6TUo4MDRVbU1sUG1yVll3RzUyZ29WTURWc2RIenR3cFwvUURaWW1GSE51b3d2TU5VcHozQjk4dFp6VVwva1lYWXF6a2hkS1ZzbGE0anc4RnBySXlTY01cL2l6aEVsWEp3dDI5SXd3YnBMT0NKaGNmZVFmOXZCdlh0VTcrQ3orZFBHUFZTMXVPdUNkWkI2NWdxV2dFb1FxczdwUDdtXC9VYTFqejFhTFdISmpXYStwV3duWWVUM1ZlR1pSM3JNOFUxMldiaXc5Uk53b1R2UzlUd2wybllybzI4YUpsZnRWb0RGMnM0T3djWEplSlBTMDN5aDhzd2pta0RqOUhoUmdwakI3aUJBPT0iXSwiYWxnIjoiUlMyNTYifQ.eyJjbGFpbXNBdHRyaWJ1dGVzIjp7ImFkZHJlc3MiOnsibGFzdFZlcmlmaWVkRGF0ZSI6IjIwMjItMTItMDhUMTI6NDg6MjguNTg2WiIsImNsYWltcyI6eyJhZGRyZXNzIjoiMjIwMCBNQVNURVJDQVJEIEJMVkQsIE8gRkFMTE9OLCBNTywgNjMzNjgsIFVTQSJ9LCJhc3N1cmFuY2VMZXZlbCI6MSwiZGF0YU1hdGNoIjoiRlVMTCJ9fSwiaWQiOiI3MzdhM2QyNGEwNWNlYjk4ZDE5NmY4MmMzZTlmYTRlM2I2MDVjMGNlNTJiOTc4YTE3NmY3ZDE2MDg3Y2IwNjhmIn0.gMXXDbBZKlw7PyeZZIEliTQx-TB8sHnnqXXnb9fp9DZINGMjMHoXZLbQNsD-8YPYfUJX8xqtZHtDdwRxleYpRoSTJhwzUxju6gMwWhMMsTv5Vh7Gr4IH-CpYu83CYUstcKe4PJHVXDxT0BEtSuq2c_32nN_yXbPlPjJcCVzxYiFeqEpN7k65L9uKc-KEj_XphOE5YC16KnOm2YkqJd3jCOq3P96XoQkXGAsuIt67a2IvjgD3b3mbr1dGNSyssaPA0j-vzwAeJIEhnay7hr1-LHld-7BwRrGeoAva4myZvEa_xTw6X1hPC00KjZdg6iguLh57vT3iRO9fDNtP4ZIzOQ\"}}}";
		boolean matches = idrpReferenceApplication.verifyJWSProof(identityAttributeResponseBodyMock);
		assertTrue(matches);
	}

	@Test
	void Should_return_false_when_verifies_signature_but_does_not_match(){
		String identityAttributeResponseBodyMock = "{\"transactionId\":\"95bcd8a4-fcb4-4dce-8626-d7a98666df45\",\"fraudDetectionMeta\":null,\"verifiableCredential\":{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://www.w3.org/2018/credentials/id/v1\"],\"type\":[\"VerifiableCredential\",\"ID\"],\"issuer\":\"https://idservice.com/\",\"issuanceDate\":\"2022-12-08T12:50:43Z\",\"credentialSubject\":{\"id\":\"737a3d24a05ceb98d196f82c3e9fa4e3b605c0ce52b978a176f7d16087cb068f\",\"claimsAttributes\":{\"address\":{\"assuranceLevel\":1,\"lastVerifiedDate\":\"2022-12-08T12:48:28.586Z\",\"claims\":{\"address\":\"2200 MASTERCARD BLVD, O FALLON, MO, 63368, USA\"},\"dataMatch\":\"FULL\"}}},\"proof\":{\"jws\":\"eyJ4NXQjUzI1NiI6Ilg1OWF0QTRIMHlxNWx6VnlMUnl2Ym9oNmJrZHRMZHRQT002ZlR0OEFZMUkiLCJ4NWMiOlsiTUlJRVJ6Q0NBeStnQXdJQkFnSUlEek1MV200c09jQXdEUVlKS29aSWh2Y05BUUVMQlFBd2dZVXhDekFKQmdOVkJBWVRBa0pGTVJ3d0dnWURWUVFLRXhOTllYTjBaWEpEWVhKa0lGZHZjbXgzYVdSbE1TUXdJZ1lEVlFRTEV4dEhiRzlpWVd3Z1NXNW1iM0p0WVhScGIyNGdVMlZqZFhKcGRIa3hNakF3QmdOVkJBTVRLVTFoYzNSbGNrTmhjbVFnU1ZSR0lFMWxjM05oWjJWeklGTnBaMjVwYm1jZ1UzVmlJRU5CSUVjeU1CNFhEVEl4TURVd05qSXlNVGMxTmxvWERUSTFNREl4TURFMk1qazBPVm93Z2FReEN6QUpCZ05WQkFZVEFrSkZNUkV3RHdZRFZRUUhFd2hYWVhSbGNteHZiekV1TUN3R0ExVUVDaE1sVFdGemRHVnlRMkZ5WkNCSmJuUmxjbTVoZEdsdmJtRnNJRWx1WTI5eWNHOXlZWFJsWkRFWk1CY0dBMVVFQ3hNUVJHbG5hWFJoYkNCSlpHVnVkR2wwZVRFM01EVUdBMVVFQXhNdVpHbHpMVzFwWkhNdGMyVnlkbWxqWlhNdGFuZHpMWE5wWjI0dGRHVnpkQzV0WVhOMFpYSmpZWEprTG1OdmJUQ0NBU0l3RFFZSktvWklodmNOQVFFQkJRQURnZ0VQQURDQ0FRb0NnZ0VCQUx0RmZ0cFhHT1NYT1U2bXpEXC81YXJBTzhUNnphVENwQ0NtSkd2ZEZDYVhFNUhpczd5bmZ0N1dnNDlsR1dnTXdqN3pFdytLQmdCc2grOENxZ1VyUGhNa0hVaWhCcG9YbE9cL005emRUQ09XZEt1cjFOZFlGeFdzV3pHZVprcCt2Q2VtXC9lalJraUNza2kyeExCbHJsY1F3UnhPVDNIRUlsdnZtTlMwMmJ5SU5sbjJReWJaSHBwMkxpZDhkYlFVREVZU1hoeEk5cFdLcEFDZExBU3FUajhIK2VpK0xuMXBITTZtbVZNdCtueVFoMTR1K2FcLzdVa3ZlVzN3Zis4c0hFZmp6ZFVMcEFkaENMbGNnQWc5bVZuaDFPQ2lyYlN3WHVxXC9uWUs1TnA3UXR0dVV0bUhnSU1iV0llNFRyMVkybmlcL3dQRkQzek9cLzZJZjh0aXg2RDY4emNcLzM4Q0F3RUFBYU9CbVRDQmxqQTVCZ05WSFJFRU1qQXdnaTVrYVhNdGJXbGtjeTF6WlhKMmFXTmxjeTFxZDNNdGMybG5iaTEwWlhOMExtMWhjM1JsY21OaGNtUXVZMjl0TUE0R0ExVWREd0VCXC93UUVBd0lBZ0RBSkJnTlZIUk1FQWpBQU1CMEdBMVVkRGdRV0JCUjllS0xOVXYzSUVJenBydWFrTmp5M1RvdmVJREFmQmdOVkhTTUVHREFXZ0JSYlBFQlNEKzVQZ091YmF0U0cyWXRnM0g5WVZUQU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFVZXkzZmNJN05HUTlMVlNhUHY4UGsyTldsWnV0eDdNRHJGbVROK0ZZSUpZVGRRMWlLS2RyM3BUSjdocUR1NFBkcmsrT3VDUnVEODhZNDZCMDNRNUt6TUo4MDRVbU1sUG1yVll3RzUyZ29WTURWc2RIenR3cFwvUURaWW1GSE51b3d2TU5VcHozQjk4dFp6VVwva1lYWXF6a2hkS1ZzbGE0anc4RnBySXlTY01cL2l6aEVsWEp3dDI5SXd3YnBMT0NKaGNmZVFmOXZCdlh0VTcrQ3orZFBHUFZTMXVPdUNkWkI2NWdxV2dFb1FxczdwUDdtXC9VYTFqejFhTFdISmpXYStwV3duWWVUM1ZlR1pSM3JNOFUxMldiaXc5Uk53b1R2UzlUd2wybllybzI4YUpsZnRWb0RGMnM0T3djWEplSlBTMDN5aDhzd2pta0RqOUhoUmdwakI3aUJBPT0iXSwiYWxnIjoiUlMyNTYifQ.eyJjbGFpbXNBdHRyaWJ1dGVzIjp7ImFkZHJlc3MiOnsibGFzdFZlcmlmaWVkRGF0ZSI6IjIwMjItMTItMDhUMTI6NDg6MjguNTg2WiIsImNsYWltcyI6eyJhZGRyZXNzIjoiMjIwMCBNQVNURVJDQVJEIEJMVkQsIE8gRkFMTE9OLCBNTywgNjMzNjgsIFVTQSJ9LCJhc3N1cmFuY2VMZXZlbCI6MSwiZGF0YU1hdGNoIjoiRlVMTCJ9fSwiaWQiOiI3MzdhM2QyNGEwNWNlYjk4ZDE5NmY4MmMzZTlmYTRlM2I2MDVjMGNlNTJiOTc4YTE3NmY3ZDE2MDg3Y2IwNjhmIn0.gMXXDbBZKlw7PyeZZIEliTQx-TB8sHnnqXXnb9fp9DZINGMjMHoXZLbQNsD-8YPYfUJX8xqtZHtDdwRxleYpRoSTJhwzUxju6gMwWhMMsTv5Vh7Gr4IH-CpYu83CYUstcKe4PJHVXDxT0BEtSuq2c_32nN_yXbPlPjJcCVzxYiFeqEpN7k65L9uKc-KEj_XphOE5YC16KnOm2YkqJd3jCOq3P96XoQkXGAsuIt67a2IvjgD3b3mbr1dGNSyssaPA0j-vzwAeJIEhnay7hr1-LHld-7BwRrGeoAva4myZvEa_xTw6X1hPC00KjZdg6iguLh57vT3iRO9fDNtP4ZIzOA\"}}}";
		boolean matches = idrpReferenceApplication.verifyJWSProof(identityAttributeResponseBodyMock);
		assertFalse(matches);
	}

	@Test
	void Should_go_through_all_application_flow_when_selecting_one(){

		String data = String.format("%s\n%s\n%s\n%s\n%s", getAridMock(), getClientAssertionMock(), getCodeMock(), getCodeVerifierMock(), getRedirectUriMock());
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		idrpReferenceApplication.handleOption("1");

		assertFalse(scanner.hasNext());
	}

	@Test
	void Should_go_through_all_application_flow_when_selecting_one_with_wrong_input(){

		String data = String.format("%s\n%s\n%s\n%s\n%s\n%s%s\n%s\n%s\n%s", "", getAridMock(), "", getClientAssertionMock(), "", getCodeMock(), "", getCodeVerifierMock(), "", getRedirectUriMock());
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		idrpReferenceApplication.handleOption("1");

		assertFalse(scanner.hasNext());
	}

	@Test
	void Should_exit_application_flow_when_selecting_two(){

		String data = String.format("%s\n%s\n%s\n%s\n%s", getAridMock(), getClientAssertionMock(), getCodeMock(), getCodeVerifierMock(), getRedirectUriMock());
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		idrpReferenceApplication.handleOption("2");

		assertTrue(scanner.hasNext());
	}

	@Test
	@DisableSystemExit
	void Should_cover_menu_flow(){
		String data = String.format("%s\n%s\n%s\n%s", "0", "\t", "2", "\t");
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		SystemExitException exitException = assertThrows(SystemExitException.class, () ->
				idrpReferenceApplication.run()
		);
		assertEquals(0, exitException.getStatusCode());
	}

	private String getAridMock(){
		return "714ebdd5-531a-4992-8cb6-c226c2faa19b";
	}

	private String getClientAssertionMock(){
		return "eyJraWQiOiJQeG9pbS1oQlVWZFI5UHI1LV9rNi03MllrT1NjSkV0bVNRNXlqRXNQMzZlNjVmYmMhZjE3MDFhNjllZWNiNGU3Mzk5NjQ1NmYxNDg4OWQwNDYwMDAwMDAwMDAwMDAwMDAwIiwiY3R5IjoiSldTIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJub3RfYXZhaWxhYmxlIiwibmJmIjoxNjcwNDcxNDgyLCJleHAiOjE2NzA0NzIzODIsImlhdCI6MTY3MDQ3MTQ4MiwianRpIjoiNzgwZGU5N2ItM2Y0MS00ODIyLWI2MGMtMTc2YmVkNGRiYjY1In0.N6c1uQRsVs_jiBfSO_IknMFlJOt5U1jVPMe3wqjl3LzPjnfOzwPz_0GE8dklVd3K6R0r8xh_L_drCZiZLDEz3l5NRv8JQzcmqzXfJ4YJ7uNKnWX1TFnzeFUdY34DxpDoCDTtVwmw1a8or7EvnpQwVRc3PWbmFRhnUFQ9bUMUi6HtkJ4nTSNWnyKDwwKrtUvGglCTRYoGzgodLvZGEj2TfWh6Qwd9MMKn560r0ZxlOvstXHOQT_9oc1eHGsQybGzP4qjh-ZQF-_BXR5R30mW4Pxw12wh_55LW53BMeumxDXXdoNnIv_t85hHzQZwQcuuCsoJ4BeuXtMKLGgzqidOyyQ";
	}

	private String getCodeMock(){
		return "eyJ4NXQjUzI1NiI6Ildvank5ekFZQzFCWlMxMTJDeTIzMjhnWkwyMm1nUzZUMEZuV1dvUFJTTkUiLCJraWQiOiIyMDIwMDYxMTEyMzYzNi1TQUFULVRva2VuLUVuZ2luZSIsImN0eSI6IkpXUyIsInR5cCI6IkpXVCIsImFsZyI6IlJTMjU2In0.eyJ1c2VyX2NvbnNlbnQiOiJ0cnVlIiwic3ViIjoiNTYwMWYzMmMtYjM2Yy00YmQ4LTllMzQtNTBiMTJiODlkZmY2Iiwic2NwIjoiY29tLm1hc3RlcmNhcmQuZGlzLm1pZHM6ZGlzLW1pZHMtc2VydmljZXM6aWQtc2VydmljZTphZGRyZXNzOjA6MCIsImdycCI6WyJZMDJoV0VPWTZZRmNHYjNLc1M2YjZZeWNvTzFYSVZJdXkzcHZybHYwYjRiMThjNWEiXSwiaXNzIjoiTUFTVEVSQ0FSRF9TQVMiLCJjb2RlX2NoYWxsZW5nZV9tZXRob2QiOiJTMjU2Iiwibm9uY2UiOiJyYW5kb20iLCJ0aWQiOiJNSURTIiwiYXVkIjoibWFzdGVyY2FyZC1ycC1zZXJ2aWNlIiwibmJmIjoxNjcwNDQwMzExLCJncmFudF90eXBlIjoiQVVUSE9SSVpBVElPTl9DT0RFIiwicmVkaXJlY3RfdXJpIjoiaHR0cHM6XC9cL3JwbG9nby51cmwuY29tIiwiZXhwIjoxNjcwNDQwNjEyLCJpYXQiOjE2NzA0NDAzMTIsImp0aSI6IjFkNmUxNjlkYTBjYWNlMTUzMDA4ZjA5NDVkNzQ5YTYyIiwiY29kZV9jaGFsbGVuZ2UiOiJ6eTFEanBtZExuRU5lWWJBVlVkbmo0bUczbWRpSWs0ZS1RZEo1ZzNRQjRvIiwiY2lkIjoiUHhvaW0taEJVVmRSOVByNS1fazYtNzJZa09TY0pFdG1TUTV5akVzUDM2ZTY1ZmJjIn0.HX2jv8qijDsIkk-WqvSqC6d9Tmo35WLYEZ7WCZzQzL94SfChFsTs_j6suSEi9UzaBSdGfShfLxuB17p-BRDuLR_kGg41CQAIzBPuYuM_GOYDUCJaJbpmENaFbzAbuW0f3S1YADH-xtJ2_ogLLF1gWF1lyRqRT569sirj18mTVnNlMlFlrUbKMRtO0v5S2WkxgTIFI2yhEY4g9OS2uW0QaEu86UZd4kMmrh_zJIEra9gACfy3SFXf1eVa2iUB_g_eWws187DOv_jGXxz6EeucPK8fzj8zUvcC56CwQplV4SdRZOH6aFcaEDU9-LsHhociHkuiXU6-3QJh5C2Ya6_goQ";
	}

	private String getCodeVerifierMock(){
		return "my-code-verifier-string";
	}

	private String getRedirectUriMock(){
		return "https://rplogo.url.com";
	}

}