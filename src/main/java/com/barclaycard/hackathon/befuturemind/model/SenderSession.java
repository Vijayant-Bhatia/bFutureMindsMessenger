package com.barclaycard.hackathon.befuturemind.model;

import java.sql.Timestamp;

public class SenderSession {

    private String senderID;

    private Timestamp lastLoginTS;

    private boolean authenticatedFlag;

    private String securePin;

    private Timestamp updateTS;

    private boolean sessionStarted;

    public boolean isSessionStarted() {
        return sessionStarted;
    }

    public void setSessionStarted(boolean sessionStarted) {
        this.sessionStarted = sessionStarted;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Timestamp getLastLoginTS() {
        return lastLoginTS;
    }

    public void setLastLoginTS(Timestamp lastLoginTS) {
        this.lastLoginTS = lastLoginTS;
    }

    public boolean isAuthenticatedFlag() {
        return authenticatedFlag;
    }

    public void setAuthenticatedFlag(boolean authenticatedFlag) {
        this.authenticatedFlag = authenticatedFlag;
    }

    public String getSecurePin() {
        return securePin;
    }

    public void setSecurePin(String securePin) {
        this.securePin = securePin;
    }

    public Timestamp getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(Timestamp updateTS) {
        this.updateTS = updateTS;
    }
}
