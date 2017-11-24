package com.barclaycard.hackathon.befuturemind.service;

import com.barclaycard.hackathon.befuturemind.api.AIConfiguration;
import com.barclaycard.hackathon.befuturemind.api.AIDataService;
import com.barclaycard.hackathon.befuturemind.api.model.AIRequest;
import com.barclaycard.hackathon.befuturemind.api.model.AIResponse;
import com.barclaycard.hackathon.befuturemind.model.Account;
import com.barclaycard.hackathon.befuturemind.model.ActionResponse;
import com.barclaycard.hackathon.befuturemind.model.SenderSession;
import com.barclaycard.hackathon.befuturemind.remote.BeFutureMindService;
import com.barclaycard.hackathon.befuturemind.remote.FacebookGraphService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.gen.CustomerLinkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by vbhatia on 11/21/2017.
 */
@Service
public class NPLService {

    @Autowired
    FacebookGraphService facebookGraphService;

    @Autowired
    BeFutureMindService beFutureMindService;

    @Autowired
    RestTemplate appServiceRestTemplate;

    private static final Logger logger = LoggerFactory.getLogger(NPLService.class);


    /*
    1.  Check for session using the service call to BFMService  (Table contains senderID, lastLoginTS, authenticatedFlag, securePin, updateTS)
    2.  If not Authenticate ask for secure pin and check
    3.  If authenticated then show message and call services according to the action
    4.  for account service of multiple accounts send the acct. id along with the request to check the balance
    */

    public ActionResponse getText(String senderId, String messageText) {
        //Fetch ASID using PSID
        String socialId = fetchId(senderId);
        ActionResponse actionResponse = new ActionResponse();
        if(!isCustomerLinkedWithBot(socialId, actionResponse)) return actionResponse;
        //Check authentication, if not do the authentication
        if (!authenticate(socialId, actionResponse)) return actionResponse;

        SenderSession senderSession = beFutureMindService.getSenderSession(socialId);
        if("authenticationRequired".equals(actionResponse.getAction())){
            if(!messageText.equals(senderSession.getSecurePin())) {
                actionResponse.setResponse("Invalid secure pin please enter again");
                return actionResponse;
            }else {
                messageText = "Hi";
            }
        }

        AIConfiguration configuration = new AIConfiguration("21ba9b6d043e42449c0651b5590e808b");
        AIDataService dataService = new AIDataService(configuration);

        String line;
        try {
            AIRequest request = new AIRequest(messageText);
            AIResponse response = dataService.request(request);
            if (response.getStatus().getCode() == 200) {
                System.out.println(response.getResult().getFulfillment().getSpeech());
                line = response.getResult().getFulfillment().getSpeech();
                actionResponse.setAction(response.getResult().getAction());
                System.out.println(">>>>>>>>>>>>>" + actionResponse.getAction());
                actionResponse.setResponse(line);
                actionResponse.setSenderId(socialId);
                if("input.unknown".equals(actionResponse.getResponse())){
                    actionResponse.setServiceCallFlag(false);
                }else {
                    actionResponse.setServiceCallFlag(true);
                }
            } else {
                System.err.println(response.getStatus().getErrorDetails());
                line = response.getStatus().getErrorDetails();
                actionResponse.setAction(response.getResult().getAction());
                actionResponse.setResponse(line);
                actionResponse.setSenderId(socialId);
            }
            beFutureMindService.updateSenderSession(senderSession);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return actionResponse;
    }

    private boolean isCustomerLinkedWithBot(String socialId, ActionResponse actionResponse) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.TEXT_HTML);
        HttpEntity<String> entity = new HttpEntity("parameters", headers);
        String url = "http://bank.harshnv.me/getCustomer.php?asid=" + socialId;
        ResponseEntity<String> exchange = appServiceRestTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        try {
            CustomerLinkResponse customerLinkResponse = (CustomerLinkResponse)fetchObject(exchange.getBody(), CustomerLinkResponse.class);
            if(null == customerLinkResponse || customerLinkResponse.getError() ) {
                actionResponse.setAction("customerNotLinked");
                actionResponse.setResponse("Please link your facebook account with Barclays account, using bank's site");
                actionResponse.setSenderId(socialId);
                actionResponse.setServiceCallFlag(false);
                return false;
            }else {
                SenderSession senderSession = beFutureMindService.getSenderSession(customerLinkResponse.getFbId());
                if(null == senderSession)
                    beFutureMindService.saveSenderSession(customerLinkResponse);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            actionResponse.setResponse("Something went wrong, our customer care representative will call you");
            return false;
        }
    }

    private Object fetchObject(String jsonString, Class targetType) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, targetType);
    }

    private boolean authenticate(String socialId, ActionResponse actionResponse) {
        SenderSession senderSession = beFutureMindService.getSenderSession(socialId);
        if(null == senderSession){
            beFutureMindService.saveSenderSession(socialId);
            return false;
        }
        if(senderSession.isSessionStarted()){
            actionResponse.setAction("authenticationRequired");
            return true;
        }
        long i = new Date().getTime() - senderSession.getUpdateTS().getTime();
        if(senderSession.isAuthenticatedFlag() && i<60000) {
            return true;
        }else {
            actionResponse.setAction("authenticationRequired");
            actionResponse.setResponse("Please enter secure pin for authentication");
            actionResponse.setSenderId(socialId);
            actionResponse.setServiceCallFlag(false);
            beFutureMindService.updateSenderSessionForPin(senderSession);
            return false;
        }
    }

    public String fetchId(String senderId) {
        return facebookGraphService.getFbId(senderId);
    }

    public String hitServices(ActionResponse actionResponse) {

        switch(actionResponse.getAction()) {
            case "input.welcome":
                break;
            case "getAccountDetails":
                List<Account> accounts = (List<Account>)beFutureMindService.getAccounts(actionResponse.getSenderId());
                if(!accounts.isEmpty()) {
                    return "Your Account Type is :[" + accounts.get(0).getAccountType() + "]";
                }
               break;
            case "userDetails":
                break;
            case "getLastFiveTxn":
                break;
            case "getBalance":
                return "Aapka balance kam h.";
            case "goodbye.action":
                System.out.println("Good bye Acction");
                SenderSession senderSessionRequest = new SenderSession();
                senderSessionRequest.setSenderID(actionResponse.getSenderId());
                beFutureMindService.logoutSenderSession(senderSessionRequest);
                return "";
            default:

        }

        return "";
    }

}
