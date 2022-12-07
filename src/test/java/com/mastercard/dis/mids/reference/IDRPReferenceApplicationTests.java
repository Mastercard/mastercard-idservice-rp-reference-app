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

package com.mastercard.dis.mids.reference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mastercard.dis.mids.reference.component.IDRPReference;
import com.mastercard.dis.mids.reference.service.claimsidentity.ClaimsIdentityAttributesResponseDTO;
import com.mastercard.dis.mids.reference.service.claimsidentity.signingvalidator.SigningValidator;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IDRPReferenceApplicationTests {

	private static final Map<String, String> MENU_MAP_TEST = new HashMap<>();

	@InjectMocks
	IDRPReferenceApplication idrpReferenceApplication;

	@Mock
	IDRPReference idRpReference;

	@BeforeEach
	void setup() {
		MENU_MAP_TEST.put("1",  "1)   Claims Identity Attributes");
		MENU_MAP_TEST.put("2",  "2)   Exit");
	}

	@Test
	void consoleMenu_runAndcheckingValues_works() {
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
	void perform_performClaimsIdentityAttributes_works() {
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
		when(idRpReference.callClaimsIdentityAttributes("", tokenRequestDTO.toString())).thenReturn(response);

		idrpReferenceApplication.performClaimsIdentityAttributes("", "","", "","");

		verify(idRpReference, times(1)).callSasAccessToken(tokenRequestDTO);
	}
}