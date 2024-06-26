package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferId;
    private int accountFromId;
//    private String accountFromUsername;
    private int accountToId;
//    private String accountToUsername;
    private BigDecimal amount;

    /*
    1 - Request
    2 - Send
     */
    private int transferType;

    /*
    1 - Pending
    2 - Approved
    3 - Rejected
     */
    private int transferStatus;

    public Transfer() {}

    public Transfer(int accountFromId, int accountToId, BigDecimal amount, int transferType, int transferStatus) {
//        this.transferId = transferId;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
    }

//    public Transfer(int accountFromId, String accountFromUsername, int accountToId, String accountToUsername, BigDecimal amount, int transferType, int transferStatus) {
//        this.accountFromUsername = accountFromUsername;
//        this.accountFromId = accountFromId;
//        this.accountToId = accountToId;
//        this.accountToUsername = accountToUsername;
//        this.amount = amount;
//        this.transferType = transferType;
//        this.transferStatus = transferStatus;
//    }
//    public String getAccountFromUsername() {
//        return accountFromUsername;
//    }
//
//    public void setAccountFromUsername(String accountFromUsername) {
//        this.accountFromUsername = accountFromUsername;
//    }
//
//    public String getAccountToUsername() {
//        return accountToUsername;
//    }
//
//    public void setAccountToUsername(String accountToUsername) {
//        this.accountToUsername = accountToUsername;
//    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
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
