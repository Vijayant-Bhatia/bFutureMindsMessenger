package com.barclaycard.hackathon.befuturemind.model;

/**
 * Created by vbhatia on 11/23/2017.
 */
public class ActionResponse {
    private String action;
    private String senderId;
    private String response;
    private String sessionId;
    private boolean serviceCallFlag;

    public boolean isServiceCallFlag() {
        return serviceCallFlag;
    }

    public void setServiceCallFlag(boolean serviceCallFlag) {
        this.serviceCallFlag = serviceCallFlag;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
