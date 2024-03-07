package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/transfers/")
@RestController
public class TransferController {

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    public TransferController(UserDao userDao, AccountDao accountDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    public Transfer transfer(@RequestBody Transfer transfer){ //TODO figure out validation for primitive types (ints) in the transfer object
        return transferDao.transfer(transfer);
    }

}
