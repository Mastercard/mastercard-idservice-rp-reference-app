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

import com.mastercard.dis.mids.reference.component.IDRPReference;
import com.mastercard.dis.mids.reference.exception.ServiceException;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestDTO;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import com.mastercard.dis.mids.reference.session.SessionContext;
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
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
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

	@Test
	void Should_go_through_all_application_flow_when_selecting_one(){

		String data = String.format("%s\n%s\n%s\n%s\n%s", getAridMock(), getClienteAssertionMock(), getCodeMock(), getCodeVerifierMock(), getRedirectUriMock());
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		idrpReferenceApplication.handleOption("1");

		assertFalse(scanner.hasNext());
	}

	@Test
	void Should_exit_application_flow_when_selecting_two(){

		String data = String.format("%s\n%s\n%s\n%s\n%s", getAridMock(), getClienteAssertionMock(), getCodeMock(), getCodeVerifierMock(), getRedirectUriMock());
		InputStream stream = new ByteArrayInputStream(data.getBytes());
		Scanner streamScanner = new Scanner(new BufferedInputStream(stream), "UTF-8");

		ReflectionTestUtils.setField(idrpReferenceApplication, "scanner",  streamScanner);
		scanner = (Scanner) ReflectionTestUtils.getField(idrpReferenceApplication, "scanner");

		idrpReferenceApplication.handleOption("2");

		assertTrue(scanner.hasNext());
	}

	private String getAridMock(){
		return "714ebdd5-531a-4992-8cb6-c226c2faa19b";
	}

	private String getClienteAssertionMock(){
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