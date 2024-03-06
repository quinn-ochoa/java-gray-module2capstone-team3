package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Component;

@Component
public class JdbcTransferDao implements TransferDao{
    @Override
    public Transfer createTransfer(Transfer transfer) {
        return null;
    }
}
