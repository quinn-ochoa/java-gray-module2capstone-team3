package com.techelevator.dao;

import com.techelevator.dao.BaseDaoTests;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcAccountDaoTests extends BaseDaoTests {

    protected static final Account ACCOUNT_1 = new Account(2001,1001, BigDecimal.valueOf(1000.11));
    protected static final Account ACCOUNT_2 = new Account(2002, 1002, BigDecimal.valueOf(1000.11));
    protected static final Account ACCOUNT_3 = new Account(2003, 1003, BigDecimal.valueOf(1000.11));

    private  Account testAccount1;

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void getAccountById_given_invalid_id_returns_null() {
        Account account = sut.getAccountById(0);
        Assert.assertNull(account);
    }

    @Test
    public void getAccountById_given_valid_id_returns_account() {
        Account account = sut.getAccountById(1001);
        assertAccountsMatch(ACCOUNT_1, account);

//        account = sut.getAccountById(1002);
//        assertAccountsMatch(ACCOUNT_2, account);
    }

    @Test
    public void updateAccount_given_valid_id_returns_updated_account() {
        Account accountToUpdate = sut.getAccountById(1003);

        accountToUpdate.setUserId(1003); //I'm not sure here
        accountToUpdate.setBalance(BigDecimal.valueOf(200.0));

        Account updatedAccount = sut.updateAccount(accountToUpdate);
        Assert.assertNotNull(updatedAccount);

        Account retrievedAccount = sut.getAccountById(1003);
        assertAccountsMatch(updatedAccount, retrievedAccount);
    }

    @Test
    public void makeAccountObjectByUserId_given_invalid_id_returns_null() {
        Account account = sut.getAccountByAccountId(-100);
        Assert.assertNull(account);
    }

    @Test
    public void makeAccountObjectByUserId_given_valid_id_returns_account() {
        Account account = sut.getAccountByAccountId(2001);
        assertAccountsMatch(ACCOUNT_1, account);

        account = sut.getAccountByAccountId(2002);
        assertAccountsMatch(ACCOUNT_2, account);
    }

    private void assertAccountsMatch(Account expected, Account actual){
        Assert.assertEquals("Account id does not match", expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals("User id does not match", expected.getUserId(), actual.getUserId());
        Assert.assertEquals("Balance does not match", expected.getBalance(), actual.getBalance());
    }
}