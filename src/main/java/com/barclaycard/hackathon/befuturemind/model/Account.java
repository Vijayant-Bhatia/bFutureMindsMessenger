package com.barclaycard.hackathon.befuturemind.model;

import java.io.Serializable;

/**
 * Created by vbhatia on 11/23/2017.
 */
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountId;

    private String accountType;

    private String userId;

    private String socialId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }
}
