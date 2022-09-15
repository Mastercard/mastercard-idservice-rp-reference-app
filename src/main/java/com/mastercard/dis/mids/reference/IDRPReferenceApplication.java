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
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenRequestExample;
import com.mastercard.dis.mids.reference.service.sas.SasAccessTokenResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.mastercard.dis.mids.reference.constants.Menu.MENU_MAP;

import java.util.Map;
import java.util.Scanner;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class IDRPReferenceApplication implements CommandLineRunner {

	private static final String UNDER_DEVELOPMENT = "Under Development...";
	private static final String ERROR = "Error : ";

	@Value("${arid}")
	private String arid;

	@Value("${authCode}")
	private String authCode;

	private final IDRPReference idRpReference;

	private boolean exit = false;
	private Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(IDRPReferenceApplication.class);
		System.exit(0);
	}

	@Override
	public void run(String... args) {
		scanner = new Scanner(System.in, "UTF-8");

		while (!exit) {
			showMenu();
			handleOption(scanner.nextLine());
			pressAnyKey();
		}
		System.exit(0);
	}

	void showMenu() {
		log.info(" <--- Welcome to ID RP Reference Application --->");
		for (Map.Entry<String, String> entry : MENU_MAP.entrySet()) {
			log.info(entry.getValue());
		}
		log.info(" ---> Type your option and press ENTER: ");
	}

	void handleOption(String option) {
		log.info("Your option : " + option);
		switch (option) {
			case "1":
				performClaimsIdentityAttributes(arid);
				break;
			case "2":
				log.info(UNDER_DEVELOPMENT);
				break;
			case "3":
				this.exit = true;
				break;
			default:
				log.info("Invalid option!");
		}
	}

	void performClaimsIdentityAttributes(String arid) {
		try {
			log.info("<<--- Claims Identity Attributes Started --->>");
			SasAccessTokenResponseDTO sasAccessTokenResponseDTO = idRpReference.callSasAccessToken(SasAccessTokenRequestExample.sasAccessTokenRequestExample(authCode));
			log.info("<<--- Access Token --->>"+sasAccessTokenResponseDTO.getAccess_token());
			idRpReference.callClaimsIdentityAttributes(arid, sasAccessTokenResponseDTO.getAccess_token());
			log.info("<<--- Claims Identity Attributes Successfully Ended --->>");
		}
		catch (Exception e) {
			log.info(ERROR + e.getMessage());
			log.info("<<--- Claims Identity Attributes Failed Ended --->>");
		}
	}

	void pressAnyKey() {
		if (!exit) {
			log.info("Press ENTER to continue...");
			scanner.nextLine();
		}
	}
}