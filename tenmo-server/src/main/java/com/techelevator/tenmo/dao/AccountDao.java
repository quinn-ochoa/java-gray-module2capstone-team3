package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    public BigDecimal getBalanceById(int id);
    public BigDecimal updateBalanceById(int id);
    public Account updateAccount(Account account);
}
