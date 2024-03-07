package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/transfers/")
@RestController
public class TransferController {

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Transfer transfer(){

    }
}
