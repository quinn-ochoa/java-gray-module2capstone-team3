package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    private final String BASE_URL;
    private final RestTemplate restTemplate;
    private String token;
    public TransferService(String BASE_URL){
        this.BASE_URL = BASE_URL + "/transfers/";
        this.restTemplate = new RestTemplate();

    }

    //send and request transfer
    public Transfer transfer(Transfer transfer){
        Transfer returnedTransfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(BASE_URL, HttpMethod.POST,makeTransferEntity(transfer), Transfer.class);
            returnedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return  returnedTransfer;
    }



    //view transfer history


    public HttpEntity<Transfer> makeTransferEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(transfer, headers);
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
