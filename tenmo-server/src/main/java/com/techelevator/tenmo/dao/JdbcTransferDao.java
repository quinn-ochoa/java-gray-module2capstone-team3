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
public class JdbcTransferDao implements TransferDao{
    private final JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = new JdbcAccountDao(jdbcTemplate);
    }
    @Override
    public Transfer transfer(Transfer transfer) {
        Account fromUser = accountDao.makeAccountObjectByUserId(transfer.getAccountFromId());
        Account toUser = accountDao.makeAccountObjectByUserId(transfer.getAccountToId());
        fromUser.setBalance(fromUser.getBalance().subtract(transfer.getAmount()));
        toUser.setBalance(toUser.getBalance().add(transfer.getAmount()));
        accountDao.updateAccount(fromUser);
        accountDao.updateAccount(toUser);
        // TODO update to accept dynamic transferStatus
        return createTransfer(transfer);
    }
    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer createdTransfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        try{
            int createdTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferType(), transfer.getTransferStatus(),
                    transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
            createdTransfer = getTransferById(createdTransferId);
        } catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e){
            throw new DaoException("Data integrity violation", e);
        }

        return createdTransfer;
    }

    @Override
    public Transfer getTransferById(int id) {
        Transfer receivedTransfer = null;
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if(results.next()){
            receivedTransfer = mapRowToTransfer(results);
        }
        return receivedTransfer;
    }

    public Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setAccountFromId(rowSet.getInt("account_from"));
        transfer.setAccountToId(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        transfer.setTransferStatus(rowSet.getInt("transfer_status_id"));
        transfer.setTransferType(rowSet.getInt("transfer_type_id"));
        return transfer;
    }
}
