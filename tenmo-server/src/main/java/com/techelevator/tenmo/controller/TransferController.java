package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@PreAuthorize("isAuthenticated()")
@RequestMapping(path = "transfers/")
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

    @RequestMapping(path = "request/{transferId}", method = RequestMethod.PUT)
    public Transfer updateTransferStatus(@RequestBody Transfer transfer , @PathVariable int transferId) {
        Transfer returnTransfer = null;
        transfer.setTransferId(transferId);
        try {
            returnTransfer = transferDao.transfer(transfer);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to update transfer log or accounts, if needed." + e.getMessage());
        }
        return returnTransfer;
    }

}
