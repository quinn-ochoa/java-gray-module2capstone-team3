package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {
    public Account getAccountById(int id);
    public Account updateAccount(Account account);
    public Account getAccountByAccountId(int id);

}
