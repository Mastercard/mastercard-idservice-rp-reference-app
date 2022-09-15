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

package com.mastercard.dis.mids.reference.session;

import com.mastercard.dis.mids.reference.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionContextTest {

    public static final String JWT_TOKEN = "jwtToken";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(SessionContext.class, "instance", null);
    }

    @Test
    void create_shouldCreateAnInstanceWithGivenJwtToken() {
        SessionContext sessionContext = SessionContext.create(JWT_TOKEN);

        assertNotNull(sessionContext);
        assertEquals(JWT_TOKEN, sessionContext.getJwtToken());
    }

    @Test
    void get_shouldReturnActiveInstance() {
        SessionContext.create(JWT_TOKEN);

        assertNotNull(SessionContext.get());
    }

    @Test
    void get_shouldThrowExceptionWhenInstanceIsNotSet() {
        assertThrows(ServiceException.class, SessionContext::get);
    }

    @Test
    void get_shouldThrowExceptionWhenJwtIsEmpty() {
        assertThrows(ServiceException.class, () -> SessionContext.create(""));
    }
}