package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

public class AccountService {

    private final String BASE_URL;
    private final RestTemplate restTemplate;
    private String token; // token set in handleLogin() in App.java (should be set when user logs in)

    public AccountService(String BASE_URL){
        this.BASE_URL = BASE_URL + "accounts/";
        this.restTemplate = new RestTemplate();
    }

    public Account getAccountById(int userId){
        Account account = null;
        HttpEntity<Void> entity = makeAuthEntity();
        try{
            ResponseEntity<Account> response = restTemplate.exchange(BASE_URL + userId, HttpMethod.GET,
                    entity, Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public User[] getUsers() {
        User[] users = null;
        HttpEntity<Void> entity = makeAuthEntity();
        try {

            ResponseEntity<User[]> response = restTemplate.exchange(BASE_URL, HttpMethod.GET,
                    entity, User[].class);
            users = response.getBody();

        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    public User getUserById(int id){
        User[] users = getUsers();
        for(int i = 0; i < users.length; i++){
            if(users[i].getId() == id){
                return users[i];
            }

        }
        return null;
    }

    public User getUserByAccountId(int accountId){
        User user = null;
        HttpEntity<Void> entity = makeAuthEntity();
        try{
            ResponseEntity<User> response = restTemplate.exchange(BASE_URL + "users/" + accountId, HttpMethod.GET, entity, User.class);
            user = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return user;
    }

    //view transfer history
    public Transfer[] getTransfersByAccountId(int id){
        Transfer[] transfers = null;
        HttpEntity<Void> entity = makeAuthEntity();
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(BASE_URL + id + "/transfers", HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;
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
