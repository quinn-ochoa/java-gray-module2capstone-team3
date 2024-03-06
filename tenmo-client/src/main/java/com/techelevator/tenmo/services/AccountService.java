package com.techelevator.tenmo.services;

import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    private final String BASE_URL;
    private final RestTemplate restTemplate;
    private String token; // token set in handleLogin() in App.java (should be set when user logs in)
    public AccountService(String BASE_URL){
        this.BASE_URL = BASE_URL + "/accounts/";
        this.restTemplate = new RestTemplate();
    }

    public BigDecimal getBalanceByAccountId(int accountId){
        BigDecimal balance = null;
        HttpEntity<Void> entity = makeAuthEntity();
        try{
            ResponseEntity<BigDecimal> response = restTemplate.exchange(BASE_URL + accountId, HttpMethod.GET,
                    entity, BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
    public void setToken(String token){
        this.token = token;
    }
}
