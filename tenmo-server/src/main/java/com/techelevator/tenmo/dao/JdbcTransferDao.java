package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        //say we have a transfer with status = approved, type = request
        //in the DB, we want to update both accounts related to the transfer and then update the transfer

        boolean isPending = transfer.getTransferStatus() == 1;  //false
        boolean isSending = (transfer.getTransferType() == 2);  //false
        boolean isApproved = transfer.getTransferStatus() == 2; //true
        boolean isRequest = transfer.getTransferType() == 1;    //true

        if (isSending || (isRequest && isApproved)) {

            Account fromUser = accountDao.getAccountByAccountId(transfer.getAccountFromId()); //TODO fix userId vs accountId

            Account toUser = accountDao.getAccountByAccountId(transfer.getAccountToId());   //TODO fix userId vs accountId

            fromUser.setBalance(fromUser.getBalance().subtract(transfer.getAmount()));
            toUser.setBalance(toUser.getBalance().add(transfer.getAmount()));
            accountDao.updateAccount(fromUser);
            accountDao.updateAccount(toUser);  //TODO: consider making updateAccount take two accounts and use a TRANSACTION in SQL
        }

        //Transfer status: 1 - pending 2 - approved 3 - rejected
        //Transfer type  : 1 - request 2 - sending

        if (isPending || isSending) {
            return createTransfer(transfer);
        }
        /*
        type     status
        1           1   pending/request   happens ^^^
        1           2   pending/sending   CANT HAPPEN
        2           1   approved/request  doesnt get caught, want to update
        2           2   approved/sending  happens ^^^
        3           1   rejected/request  doesnt get caught, want to update
        3           2   rejected/sending  CANT HAPPEN
         */

        else {
            return updateTransfer(transfer);
        }
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {

        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";

        try {
            int numberOfRows = jdbcTemplate.update(sql,
                    transfer.getTransferStatus(), transfer.getTransferId());
            if (numberOfRows == 0) {
                throw new DaoException("Zero rows affected, expected at least one");
            }
            else {
                updatedTransfer = getTransferById(transfer.getTransferId());
            }
        }
        catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }

        return updatedTransfer;
    }


    @Override
    public Transfer createTransfer(Transfer transfer) {
        Transfer createdTransfer = null;
        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) RETURNING transfer_id";
        try{
            //TODO Change User ID to Account ID
            int createdTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferType(), transfer.getTransferStatus(),
                    transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
            createdTransfer = getTransferById(createdTransferId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
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

    @Override
    public List<Transfer> getTransfersByAccountId(int id) {
        List <Transfer> transfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer FULL JOIN account ON transfer.account_from " +
                    "= account.account_id WHERE account_from = ? OR account_to = ?; ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
            while (results.next()) {
                transfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return transfers;
    }


    public Transfer mapRowToTransfer(SqlRowSet rowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rowSet.getInt("transfer_id"));
        transfer.setAccountFromId(rowSet.getInt("account_from"));
        transfer.setAccountToId(rowSet.getInt("account_to"));
        transfer.setAmount(rowSet.getBigDecimal("amount"));
        transfer.setTransferStatus(rowSet.getInt("transfer_status_id"));
        transfer.setTransferType(rowSet.getInt("transfer_type_id"));
        return transfer;
    }

//    public Transfer mapRowToTransfer(SqlRowSet rowSet, String toUsername, String fromUsername) {
//        Transfer transfer = new Transfer();
////        transfer.setId(rowSet.getInt("transfer_id"));
//        transfer.setAccountFromId(rowSet.getInt("account_from"));
//        transfer.setAccountToId(rowSet.getInt("account_to"));
//        transfer.setAmount(rowSet.getBigDecimal("amount"));
//        transfer.setTransferStatus(rowSet.getInt("transfer_status_id"));
//        transfer.setTransferType(rowSet.getInt("transfer_type_id"));
//        transfer.setAccountToUsername(toUsername);
//        transfer.setAccountFromUsername(fromUsername);
//        return transfer;
//    }
}
