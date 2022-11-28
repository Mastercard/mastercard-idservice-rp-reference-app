package com.mastercard.dis.mids.reference.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SystemExitExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        System.setSecurityManager(new DisallowExitSecurityManager());
    }
}