package com.barclaycard.hackathon.befuturemind.remote;

import com.test.gen.GraphResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

/**
 * Created by vbhatia on 11/22/2017.
 */
@Service
public class FacebookGraphService {

    @Autowired
    @Qualifier("facebookGraphRestTemplate")
    RestTemplate facebookGraphRestTemplate;

    public String getFbId(String senderId) {
        GraphResponse response = facebookGraphRestTemplate.getForObject("https://graph.facebook.com/v2.11/"+senderId+"/ids_for_apps" +
                "?page=232811100589221" +
                "&access_token=EAAbT2pUV3WYBAGuD7vjgl0yN09sHCZCCOSVSQrybcnvp4YsroeAAhUByZAmukBimMSnraX8mJW7ZBkMZAkFFZBCupOHpzxQhbDivurRavZA0iZAqPcOzTvfrFFMQwgzPGUga2YHpq8HNZAg8SG9LZBVS8wRi0z5k6QwDTEJi9XhHL5QZDZD&appsecret_proof=ffd7a9eaef86c31a81a69f1f73887707c7ecea01616caae3dbd04e56b26430a2", GraphResponse.class);
        if(response.getData().isEmpty()){
            return "NOT_LINKED";
        }

        String id = response.getData().get(0).getId();
        System.out.println(id);
        return id;
    }
}
