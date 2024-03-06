package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int accountFromId;
    private int accountToId;
    private BigDecimal amount;
    private int transferType;
    private int transferStatus;

    public Transfer() {}

    public Transfer(int accountFromId, int accountToId, BigDecimal amount, int transferType, int transferStatus) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getTransferType() {
        return transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }
    //TODO override String()
}
