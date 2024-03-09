package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.model.Transfer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private static final Transfer TRANSFER_1 = new Transfer(3001, 2001, 2002, BigDecimal.valueOf(150.99), 1, 1);
    private static final Transfer TRANSFER_2 = new Transfer( 3002, 2002, 2003, BigDecimal.valueOf(60.99), 2, 2);
    private static final Transfer TRANSFER_3 = new Transfer( 3003, 2003, 2001, BigDecimal.valueOf(600.99), 1, 3);
    private static final Transfer TRANSFER_4 = new Transfer( 3004, 2003, 2002, BigDecimal.valueOf(30.99), 1, 3);

    private Transfer testTransfer;
    private JdbcTransferDao sut;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransferDao(jdbcTemplate);
        testTransfer = new Transfer(2002, 2001, BigDecimal.valueOf(45.99), 2, 2);
    }

    @Test
    public void updateTransfer_returns_updated_transfer() {
        Transfer transferToUpdate = sut.getTransferById(3001);

        transferToUpdate.setTransferType(1);
        transferToUpdate.setTransferStatus(2);
        transferToUpdate.setAccountFromId(2001);
        transferToUpdate.setAccountToId(2003);
        transferToUpdate.setAmount(BigDecimal.valueOf(80.99));

        Transfer updatedTransfer = sut.updateTransfer(transferToUpdate);
        Assert.assertNotNull(updatedTransfer);

        Transfer retrievedTransfer = sut.getTransferById(3001);
        assertTransfersMatch(updatedTransfer, retrievedTransfer);
    }

    @Test
    public void createTransfer_creates_transfer() {
        Transfer createdTransfer = sut.createTransfer(testTransfer);
        Assert.assertNotNull(createdTransfer);

        int newId = createdTransfer.getTransferId();
        Assert.assertTrue(newId > 0);

        Transfer retrievedTransfer = sut.getTransferById(newId);
        assertTransfersMatch(createdTransfer, retrievedTransfer);
    }

    @Test
    public void getTransferById_given_invalid_transferId_returns_null() {
        Transfer transfer = sut.getTransferById(0);
        Assert.assertNull(transfer);
    }

    @Test
    public void getTransferById_given_valid_transferId_returns_transfer() {
        Transfer transfer = sut.getTransferById(3001);
        assertTransfersMatch(TRANSFER_1, transfer);

        transfer = sut.getTransferById(3002);
        assertTransfersMatch(TRANSFER_2, transfer);
    }

    @Test
    public void getTransfersByAccountId_given_invalid_transferId_returns_null() {
        List<Transfer> transfers = sut.getTransfersByAccountId(0);
        Assert.assertEquals(0, transfers.size());
    }

    @Test
    public void getTransfersByAccountId_given_valid_transferId_returns_transfer() {
        List<Transfer> transfers = sut.getTransfersByAccountId(2003);
        Assert.assertEquals("Different number of accounts found than expected", 3, transfers.size());
        assertTransfersMatch(TRANSFER_2, transfers.get(0));
        assertTransfersMatch(TRANSFER_3, transfers.get(1));
        assertTransfersMatch(TRANSFER_4, transfers.get(2));

        transfers = sut.getTransfersByAccountId(2001);
        Assert.assertEquals("Different number of accounts found than expected", 2, transfers.size());
        assertTransfersMatch(TRANSFER_1, transfers.get(0));
        assertTransfersMatch(TRANSFER_3, transfers.get(1));
    }

    private void assertTransfersMatch (Transfer expected, Transfer actual) {
        Assert.assertEquals("Transfer id does not match", expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals("Transfer type does not match", expected.getTransferType(), actual.getTransferType());
        Assert.assertEquals("Transfer status does not match", expected.getTransferStatus(), actual.getTransferStatus());
        Assert.assertEquals("From account does not match", expected.getAccountFromId(), actual.getAccountFromId());
        Assert.assertEquals("To account does not match", expected.getAccountToId(), actual.getAccountToId());
        Assert.assertEquals("Amount does not match", expected.getAmount(), actual.getAmount());
    }
}