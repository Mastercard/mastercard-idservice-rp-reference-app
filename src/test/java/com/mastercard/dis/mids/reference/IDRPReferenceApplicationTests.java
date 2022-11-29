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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IDRPReferenceApplicationTests {

	private static final Map<String, String> MENU_MAP_TEST = new HashMap<>();

	@BeforeAll
	static void setup() {
		MENU_MAP_TEST.put("1",  "1)   Claims Identity Attributes");
		MENU_MAP_TEST.put("2",  "2)   Upcoming Flow");
		MENU_MAP_TEST.put("3",  "3)   Exit");
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
		IDRPReferenceApplication spyMIDSReferenceApplication = spy(new IDRPReferenceApplication(null));

		spyMIDSReferenceApplication.handleOption("1");
		spyMIDSReferenceApplication.handleOption("2");

		verify(spyMIDSReferenceApplication, times(2)).handleOption(anyString());
	}
}