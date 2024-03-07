package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    public Account updateAccount(Account account) {
        Account updatedAccount = null;

        String sql = "UPDATE account SET balance = ? WHERE user_id = ?;";

        try {
            int numberOfRows = jdbcTemplate.update(sql,
                    account.getBalance(), account.getUserId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            else {
                updatedAccount = null; //TODO makes getAccountById
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedAccount;
    }

    @Override
    public Account makeAccountObjectByUserId(int id){
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            account = mapRowToAccount(results);
        }
            return account;
    }



    public Account mapRowToAccount(SqlRowSet rowSet) {
        Account account = new Account();
        account.setAccountId(rowSet.getInt("account_id"));
        account.setUserId(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getBigDecimal("balance"));

        return account;
    }

}
