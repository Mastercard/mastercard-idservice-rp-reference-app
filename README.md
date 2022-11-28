# Mastercard ID RP Service Reference Implementation

[![](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](./LICENSE)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)

## Table of Contents
- [Overview](#overview)
  * [Compatibility](#compatibility)
  * [References](#references)
- [Usage](#usage)
  * [Prerequisites](#prerequisites)
  * [Configuration](#configuration)
  * [Integrating with OpenAPI Generator](#integrating-with-openapi-generator)
  * [Build and Execute](#build-and-execute)
- [Use Cases](#use-cases)
- [API Reference](#api-reference)
  * [Authorization](#authorization)
  * [Encryption and Decryption](#encryption-decryption)
  * [Request Examples](#request-examples)
  * [Recommendation](#recommendation)
- [Support](#support)
- [License](#license)

## Overview <a name="overview"></a>
ID is a digital identity service from MastercardⓇ that helps you apply for, enroll in, log in to, and access services more simply, securely and privately. Rather than manually providing your information when you are trying to complete tasks online or in apps, ID enables you to share your verified information automatically, more securely, and with your consent and control. ID also enables you to do away with passwords and protects your personal information. Please see here for more details on the API: [Mastercard Developers](https://developer.mastercard.com/mastercard-id-service/documentation/).

For more information regarding the program refer to [Id Service](https://idservice.com/)

### Compatibility <a name="compatibility"></a>
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later

### References <a name="references"></a>
* [Mastercard’s OAuth Signer library](https://github.com/Mastercard/oauth1-signer-java)
* [Using OAuth 1.0a to Access Mastercard APIs](https://developer.mastercard.com/platform/documentation/using-oauth-1a-to-access-mastercard-apis/)

## Usage <a name="usage"></a>
### Prerequisites <a name="prerequisites"></a>
* [Mastercard Developers Account](https://developer.mastercard.com/dashboard) with access to Mastercard ID Service API
* A text editor or IDE
* [Spring Boot 2.2+](https://spring.io/projects/spring-boot)
* [Apache Maven 3.3+](https://maven.apache.org/download.cgi)
* Set up the `JAVA_HOME` environment variable to match the location of your Java installation.

### Configuration <a name="configuration"></a>
* Create an account at [Mastercard Developers](https://developer.mastercard.com/account/sign-up).  
* Create a new project and add `Mastercard ID Service` API to your project.   
* Configure project and download all the keys. It will download multiple files.  
* Select all `.p12` files, `.pem` file and copy it to `src/main/resources` in the project folder.
* Open `${project.basedir}/src/main/resources/application.properties` and configure below parameters.
    
    >**mastercard.api.base.path=corresponding MC ID Service Url, example : https://sandbox.api.mastercard.com/mcidservice**, its a static field, will be used as a host to make API calls.
    
    **The properties below will be required for authentication of API calls.**
    
    >**mastercard.api.key.file=**, this refers to .p12 file found in the signing key. Please place .p12 file at src\main\resources in the project folder and add classpath for .p12 file.
    
    >**mastercard.api.consumer.key=**, this refers to your consumer key. Copy it from "Keys" section on your project page in [Mastercard Developers](https://developer.mastercard.com/dashboard)
      
    >**mastercard.api.keystore.alias=keyalias**, this is the default value of key alias. If it is modified, use the updated one from keys section in [Mastercard Developers](https://developer.mastercard.com/dashboard).
    
    >**mastercard.api.keystore.password=keystorepassword**, this is the default value of key alias. If it is modified, use the updated one from keys section in [Mastercard Developers](https://developer.mastercard.com/dashboard).

    # Encryption
    >**mastercard.api.encryption.certificateFile=**, this is the path to certificate (.pem) file. Add classpath for .crt file, after placing it at src\main\resources in the project folder.mastercard.api.encryption.certificateFile=classpath:
    
    >**mastercard.api.encryption.fingerPrint=**, this is the encryption key, required to encrypt a request.
    
    # Decryption
    >**mastercard.api.decryption.keystore=**, Copy .p12 file in src/main/resources and set value as classpath:keyalias-encryption-mc.p12
    
    >**mastercard.api.decryption.alias=**, Key alias, this is the user provide keyalias that is used while creating the API project in https://developer.mastercard.com
    
    >**mastercard.api.decryption.keystore.password=**, Keystore password, this is the password provided while creating the API project in https://developer.mastercard.com
    
    

### Integrating with OpenAPI Generator <a name="integrating-with-openapi-generator"></a>
[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) generates API client libraries from [OpenAPI Specs](https://github.com/OAI/OpenAPI-Specification). 
It provides generators and library templates for supporting multiple languages and frameworks.

See also:
* [OpenAPI Generator (maven Plugin)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin)
* [OpenAPI Generator (executable)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-cli)
* [CONFIG OPTIONS for java](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/java.md)

#### OpenAPI Generator Plugin Configuration
```xml
<!-- https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin -->
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/mids-reference-app-spec.yaml</inputSpec>
                <generatorName>java</generatorName>
                <library>okhttp-gson</library>
                <generateApiTests>false</generateApiTests>
                <generateModelTests>false</generateModelTests>
                <configOptions>
                    <sourceFolder>src/gen/main/java</sourceFolder>
                    <dateLibrary>java8</dateLibrary>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Generating The API Client Sources
Now that you have all the dependencies you need, you can generate the sources. To do this, use one of the following two methods:

`Using IDE`
* **Method 1**<br/>
  In IntelliJ IDEA, open the Maven window **(View > Tool Windows > Maven)**. Click the icons `Reimport All Maven Projects` and `Generate Sources and Update Folders for All Projects`

* **Method 2**<br/>

  In the same menu, navigate to the commands **({Project name} > Lifecycle)**, select `clean` and `compile` then click the icon `Run Maven Build`. 

`Using Terminal`
* Navigate to the root directory of the project within a terminal window and execute `mvn clean compile` command.

### Test Case Execution <a name="build-and-execute"></a>
Navigate to the test package and right click to  `Run All Tests`

### Use cases
Main use cases in Mastercard ID RP Service Reference APIs are TBA                                                              

Below are the different APIs available in Mastercard ID RP Service Reference application:

A] [Claims Identity Attributes API Documentation](/documentation/parameters/#claims-identity-attributes).
    
- Please refer to `callClaimsIdentityAttributes` in [IDRPReference.java](./src\main\java\com\mastercard\dis\mids\reference\component\IDRPReference.java) for details from attached reference application.  

    URL      : /claims/{arid}/identity-attributes

    Path Variable  : arid (UUID representing the ARID for example: df52649e-4096-456a-bca0-751ee470009f)

    Response : [ClaimsIdentityAttributes](./docs/ClaimsIdentityAttributes.md)

B] [SAS Access Token API Documentation](/documentation/parameters/#sas-access-tokens).

- Please refer to `callSasAccessToken` in [IDRPReference.java](./src\main\java\com\mastercard\dis\mids\reference\component\IDRPReference.java) for details from attached reference application.

  URL      : /api/oauth2/token

  Request  : [SasAccessTokenRequestDTO](./docs/SasAccessTokenRequestDTO.md)

  Response : [SasAccessTokenResponseDTO](./docs/SasAccessTokenResponseDTO.md)

## API Reference <a name="api-reference"></a>

- To develop a client application that consumes a RESTful ID Service API with Spring Boot, refer to the documentation below.
- [Mastercard ID Service Reference](https://developer.mastercard.com/mastercard-id-service/documentation/api-reference/).
- This will be used for the following:
    - TBA

### Authorization <a name="authorization"></a>
The `com.mastercard.dis.mids.reference.config` package will provide you API client. These class will take care of adding the correct `Authorization` header before sending the request.

### Encryption and Decryption <a name="encryption-decryption"></a>
The `com.mastercard.dis.mids.reference.interceptor` package will provide you with interceptor class which used in API client. This class will encrypt the request body and add the encrypted text as value to 'encryptedData' JSON property in the request body before sending the request and decrypt the 'encryptedData' JSON property value from the response body.

### Request Examples <a name="request-examples"></a>
You can change the default input passed to APIs, modify values in following file:
* `com.mastercard.dis.mids.reference.constants.Constants`

### Recommendation <a name="recommendation"></a>
It is recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Support <a name="support"></a>
If you would like further information, please send an email to `IDservicepilothelp@mastercard.com`
- For information regarding licensing, refer to the [License file](LICENSE.md).
- For copyright information, refer to the [COPYRIGHT.md](COPYRIGHT.md).
- For instructions on how to contribute to this project, refer to the [Contributing file](CONTRIBUTING.md).
- For changelog information, refer to the [CHANGELOG.md](CHANGELOG.md).

## License <a name="license"></a>
Copyright 2022 Mastercard
 
Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 
       http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.