package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "/accounts/")
@RestController
public class AccountController {

    private final UserDao userDao;
    private final AccountDao accountDao;
    private final TransferDao transferDao;

    public AccountController(UserDao userDao, AccountDao accountDao, TransferDao transferDao){
        this.userDao = userDao;
        this.accountDao = accountDao;
        this.transferDao = transferDao;
    }
    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id){
        BigDecimal balance = accountDao.getBalanceById(id);
        if(balance == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account number not found");
        }
        return balance;
    }
    @RequestMapping(path = "", method = RequestMethod.GET)
    public User[] getUsers() {
        User[] users = null;
        try {
            users = userDao.getUsers().toArray(new User[0]);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to get list of user accounts." + e.getMessage());
        }
        return users;
    }

}
