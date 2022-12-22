# RP for Identity Providers Reference Implementation

[![](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](./LICENSE)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)
[![](https://sonarcloud.io/api/project_badges/measure?project=Mastercard_mastercard-idservice-rp-reference-app&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Mastercard_mastercard-idservice-rp-reference-app)

## Table of Contents
- [Overview](#overview)
  * [References](#references)
- [Usage](#usage)
  * [Prerequisites](#prerequisites)
  * [Configuration](#configuration)
  * [Integrating with OpenAPI Generator](#integrating-with-openapi-generator)
  * [OpenAPI Generator Plugin Configuration](#openAPI_generator_plugin_configuration)
  * [Generating The API Client Sources](#generating_the_API_client_sources)
  * [Test Case Execution](#test-case-execute)
  * [Use Cases](#use-cases)
- [API Reference](#api-reference)
  * [Authorization](#authorization)
  * [Request Examples](#request-examples)
  * [Recommendation](#recommendation)
- [Support](#support)

## Overview <a name="overview"></a>
ID is a digital identity service from MastercardⓇ that helps you apply for, enroll in, log in to, and access services more simply, securely and privately. Rather than manually providing your information when you are trying to complete tasks online or in apps, ID enables you to share your verified information automatically, more securely, and with your consent and control. ID also enables you to do away with passwords and protects your personal information. Please see here for more details on the API: [Mastercard Developers](https://developer.mastercard.com/mastercard-id-service/documentation/).

For more information regarding the program refer to [Id Service](https://idservice.com/)

### References <a name="references"></a>
* [Mastercard’s OAuth Signer library](https://github.com/Mastercard/oauth1-signer-java)
* [Using OAuth 1.0a to Access Mastercard APIs](https://developer.mastercard.com/platform/documentation/using-oauth-1a-to-access-mastercard-apis/)

## Usage <a name="usage"></a>
### Prerequisites <a name="prerequisites"></a>
* [Mastercard Developers Account](https://developer.mastercard.com/dashboard) with access to RP for Identity Providers API
* A text editor or IDE
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* [Spring Boot 2.2+](https://spring.io/projects/spring-boot)
* [Apache Maven 3.3+](https://maven.apache.org/download.cgi)
* Set up the `JAVA_HOME` environment variable to match the location of your Java installation.

### Configuration <a name="configuration"></a>
* Create an account at [Mastercard Developers](https://developer.mastercard.com/account/sign-up).  
* Create a new project and add `ID for Relying Parties` API to your project.   
* Configure project and download all the keys. It will download multiple files.  
* Select all `.p12` files, `.pem` file and copy it to `src/main/resources` in the project folder.
* Open `${project.basedir}/src/main/resources/application.properties` and configure below parameters.
    
    >**mastercard.api.base.path=corresponding MC ID Service Url, example : https://sandbox.api.mastercard.com/mcidservice**, its a static field, will be used as a host to make API calls.
    
    **The properties below will be required for authentication of API calls.**
    
    >**mastercard.api.key.file=**, this refers to .p12 file found in the signing key. Please place .p12 file at src\main\resources in the project folder and add classpath for .p12 file.
    
    >**mastercard.api.consumer.key=**, this refers to your consumer key. Copy it from "Keys" section on your project page in [Mastercard Developers](https://developer.mastercard.com/dashboard)
      
    >**mastercard.api.keystore.alias=keyalias**, this is the default value of key alias. If it is modified, use the updated one from keys section in [Mastercard Developers](https://developer.mastercard.com/dashboard).
    
    >**mastercard.api.keystore.password=keystorepassword**, this is the default value of key alias. If it is modified, use the updated one from keys section in [Mastercard Developers](https://developer.mastercard.com/dashboard).

### Integrating with OpenAPI Generator <a name="integrating-with-openapi-generator"></a>
[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) generates API client libraries from [OpenAPI Specs](https://github.com/OAI/OpenAPI-Specification). 
It provides generators and library templates for supporting multiple languages and frameworks.

See also:
* [OpenAPI Generator (maven Plugin)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin)
* [OpenAPI Generator (executable)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-cli)
* [CONFIG OPTIONS for java](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/java.md)

#### OpenAPI Generator Plugin Configuration <a name="openAPI_generator_plugin_configuration"></a>
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

#### Generating The API Client Sources <a name="generating_the_API_client_sources"></a>
Now that you have all the dependencies you need, you can generate the sources. To do this, use one of the following two methods:

`Using IDE`
* **Method 1**<br/>
  In IntelliJ IDEA, open the Maven window **(View > Tool Windows > Maven)**. Click the icons `Reimport All Maven Projects` and `Generate Sources and Update Folders for All Projects`

* **Method 2**<br/>

  In the same menu, navigate to the commands **({Project name} > Lifecycle)**, select `clean` and `compile` then click the icon `Run Maven Build`. 

`Using Terminal`
* Navigate to the root directory of the project within a terminal window and execute `mvn clean compile` command.

### Test Case Execution <a name="test-case-execute"></a>
Navigate to the test package and right click to  `Run All Tests`

### Use cases <a name="use-cases"></a>
Main use cases in RP for Identity Providers Reference APIs are Claims Sharing and Oauth2 Access Token.                                                      

Below are the different APIs available in RP for Identity Providers Reference application:

A] [Claims Sharing - Documentation](https://developer.mastercard.com/mastercard-id-for-rp/documentation/api-reference/#apis).
    
- Please refer to `callClaimsIdentityAttributes` in [IDRPReference.java](./src\main\java\com\mastercard\dis\mids\reference\component\IDRPReference.java) for details from attached reference application.  

    URL      : `/idservice-rp/claims/{arid}/identity-attributes`

    Path Variable  : arid

    Response : [ClaimsIdentityAttributes](./docs/ClaimsIdentityAttributes.md)
    
    Once this endpoint returns a proof object containing a JWS. 
    You may validate this JWS using the below implementation.
    
    - Here in method `verifyJWSProof` on [IDRPReferenceApplication.java](./src\main\java\com\mastercard\dis\mids\reference\IDRPReferenceApplication.java) we may see a use case to verify the signature
    
    - Also refer to `verify` in [SigningValidator.java](./src\main\java\com\mastercard\dis\mids\reference\service\claimsidentity\signingvalidator\SigningValidator.java) for more information.
    
        Example: [JWS Token](./docs/JWSToken.md)

B] [Oauth 2.0 Access Token - Documentation](https://developer.mastercard.com/mastercard-id-for-rp/documentation/api-reference/#apis).
  
- Please refer to `callSasAccessToken` in [IDRPReference.java](./src\main\java\com\mastercard\dis\mids\reference\component\IDRPReference.java) for details from attached reference application.

  URL      : `/saat-auth/oauth2/token`

  Request  : [SasAccessTokenRequestDTO](./docs/SasAccessTokenRequestDTO.md)

  Response : [SasAccessTokenResponseDTO](./docs/SasAccessTokenResponseDTO.md)
  
Details on the inputs needed to run the reference app flow can be found [here](https://developer.mastercard.com/mastercard-id-for-rp/tutorial/end-to-end-testing-guide/step3/).

Guides and tutorials can be found [here](https://developer.mastercard.com/mastercard-id-for-rp/documentation/tutorials-and-guides/).

## API Reference <a name="api-reference"></a>

- To develop a client application that consumes a RESTful ID Service API with Spring Boot, refer to the documentation below.
- [RP for Identity Providers Reference](https://developer.mastercard.com/mastercard-id-service/documentation/api-reference/).

### Authorization <a name="authorization"></a>
The `com.mastercard.dis.mids.reference.config` package will provide you API client. These class will take care of adding the correct `Authorization` header before sending the request.

### Request Examples <a name="request-examples"></a>
You can change the default input passed to APIs, modify values in following file:
* `com.mastercard.dis.mids.reference.constants.AppConstants`

### Recommendation <a name="recommendation"></a>
It is recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Support <a name="support"></a>
If you would like further information, please send an email to ` MC_ID@mastercard.com`
- For information regarding licensing, refer to the [License file](LICENSE.md).
- For copyright information, refer to the [COPYRIGHT.md](COPYRIGHT.md).
- For instructions on how to contribute to this project, refer to the [Contributing file](CONTRIBUTING.md).
- For changelog information, refer to the [CHANGELOG.md](CHANGELOG.md).