package com.barclaycard.hackathon.befuturemind.remote;

import com.barclaycard.hackathon.befuturemind.model.Account;
import com.barclaycard.hackathon.befuturemind.model.SenderSession;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.gen.CustomerLinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vbhatia on 11/23/2017.
 */
@Service
public class BeFutureMindService {

    @Autowired
    RestTemplate servicesRestTemplate;

    public List<Account> getAccounts(String senderId) {
        try {
        String jsonResponse = (String)servicesRestTemplate.getForObject("http://localhost:8091/account/"+senderId, String.class);

            return fetchList(jsonResponse, Account.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public SenderSession getSenderSession(String senderId) {
        SenderSession senderSession = servicesRestTemplate.getForObject("http://localhost:8091/sendersession/" + senderId, SenderSession.class);
        return senderSession;
    }

    public SenderSession saveSenderSession(String senderId) {
        SenderSession senderSessionRequest = new SenderSession();
        senderSessionRequest.setAuthenticatedFlag(true);
        senderSessionRequest.setLastLoginTS(new Timestamp(new Date().getTime()));
        SenderSession senderSession = servicesRestTemplate.postForObject("http://localhost:8091/sendersession", senderSessionRequest, SenderSession.class);
        return senderSession;
    }

    public SenderSession updateSenderSession(SenderSession senderSessionRequest) {
        senderSessionRequest.setSessionStarted(false);
        senderSessionRequest.setAuthenticatedFlag(true);
        senderSessionRequest.setUpdateTS(new Timestamp(new Date().getTime()));
        SenderSession senderSession = servicesRestTemplate.postForObject("http://localhost:8091/sendersession", senderSessionRequest, SenderSession.class);
        return senderSession;
    }

    public SenderSession updateSenderSessionForPin(SenderSession senderSessionRequest) {
        senderSessionRequest.setSessionStarted(true);
        senderSessionRequest.setAuthenticatedFlag(true);
        senderSessionRequest.setUpdateTS(new Timestamp(new Date().getTime()));
        SenderSession senderSession = servicesRestTemplate.postForObject("http://localhost:8091/sendersession", senderSessionRequest, SenderSession.class);
        return senderSession;
    }

    public SenderSession logoutSenderSession(SenderSession senderSessionRequest) {
        senderSessionRequest = getSenderSession(senderSessionRequest.getSenderID());
        senderSessionRequest.setSessionStarted(false);
        senderSessionRequest.setAuthenticatedFlag(false);
        senderSessionRequest.setUpdateTS(new Timestamp(new Date().getTime()));
        SenderSession senderSession = servicesRestTemplate.postForObject("http://localhost:8091/sendersession", senderSessionRequest, SenderSession.class);
        return senderSession;
    }

    public <T> List<T> fetchList(String jsonString, Class targetType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, targetType);
        return mapper.readValue(jsonString, type);
    }

    public void saveSenderSession(CustomerLinkResponse customerLinkResponse) {
        SenderSession senderSession = new SenderSession();
        senderSession.setSecurePin(customerLinkResponse.getSecurePin());
        senderSession.setSenderID(customerLinkResponse.getFbId());
        senderSession.setAuthenticatedFlag(false);
        senderSession.setSessionStarted(false);
        senderSession.setLastLoginTS(new Timestamp(new Date().getTime()));
        senderSession.setUpdateTS(new Timestamp(new Date().getTime()));
        servicesRestTemplate.postForObject("http://localhost:8091/sendersession", senderSession, SenderSession.class);
    }


}
