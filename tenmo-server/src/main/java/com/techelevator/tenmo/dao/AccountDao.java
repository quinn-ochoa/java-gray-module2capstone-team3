package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDao {
    public BigDecimal getBalanceById(int id);
    public BigDecimal updateBalanceById(int id);
}
