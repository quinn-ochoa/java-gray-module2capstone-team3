package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    public Transfer transfer(Transfer transfer);

    public Transfer createTransfer(Transfer transfer);
    public Transfer getTransferById(int id);
    public List<Transfer> getTransfersByAccountId(int id);
    public Transfer updateTransfer(Transfer transfer);
}
