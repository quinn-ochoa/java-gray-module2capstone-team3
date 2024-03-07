package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {

    public Transfer transfer(Transfer transfer);

    public Transfer createTransfer(Transfer transfer);
    public Transfer getTransferById(int id);
}
