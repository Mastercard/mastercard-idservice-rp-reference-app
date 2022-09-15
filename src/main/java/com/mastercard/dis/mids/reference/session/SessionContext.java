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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionContext {

    private static SessionContext instance;
    private String jwtToken;

    public static SessionContext create(String jwtToken) {
        if (ObjectUtils.isEmpty(jwtToken)) {
            throw new ServiceException("JWT token cannot be empty");
        }
        instance = new SessionContext();
        instance.jwtToken = jwtToken;
        return instance;
    }

    public static SessionContext get() {
        if (ObjectUtils.isEmpty(instance)) {
            throw new ServiceException("Session not created.");
        }
        return instance;
    }
}