package com.mastercard.dis.mids.reference.extension;

import java.security.Permission;

public class DisallowExitSecurityManager extends SecurityManager{

    private boolean disabled = false;


    @Override
    public void checkExit(int status) throws SystemExitException {
        if (!disabled) {
            disabled = true;
            throw new SystemExitException(status);
        }
    }

    @Override
    public void checkPermission(final Permission perm) {}
}