package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BigDecimal getBalanceById(int id) {
        BigDecimal balance = null;
        Account account;
        String sql = "SELECT * FROM account WHERE user_id = ?;";
        try{
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if(results.next()){
                account = mapRowToAccount(results);
                balance = account.getBalance();
            }
            //balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);

        } catch(CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        }
        return balance;
    }

    @Override
    public BigDecimal updateBalanceById(int id) {


        return null;
    }

    public BigDecimal transfer(Account fromUser, Account toUser, BigDecimal amount, int transferType) {
        if (transferType == 2) {
            fromUser.setBalance(fromUser.getBalance().subtract(amount));
            toUser.setBalance(toUser.getBalance().add(amount));
        }
        return null;
    }



    public Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }

}
