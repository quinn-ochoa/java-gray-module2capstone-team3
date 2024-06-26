package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final TransferService transferService = new TransferService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private int currentUserId;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            currentUserId = currentUser.getUser().getId();
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            accountService.setToken(currentUser.getToken());
            transferService.setToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                System.exit(1);
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		BigDecimal response = accountService.getAccountById(currentUserId).getBalance();
        System.out.println(response);
	}

    private void viewTransferHistory() {

        User[] users = accountService.getUsers();
        String toUsername;
        String fromUsername;
        int accountId = accountService.getAccountById(currentUserId).getAccountId();
        Transfer[] transfers = accountService.getTransfersByAccountId(accountId);
        System.out.println("\nTransfers: ");
        for(Transfer transfer : transfers){
            System.out.print("ID: " + transfer.getTransferId());
            boolean isCurrentUserFromId = accountId == transfer.getAccountFromId();
            System.out.print((isCurrentUserFromId) ? (" To: ") : (" From: "));
            toUsername = accountService.getUserByAccountId(transfer.getAccountToId()).getUsername();
            fromUsername = accountService.getUserByAccountId(transfer.getAccountFromId()).getUsername();
            System.out.print((isCurrentUserFromId) ? toUsername : fromUsername);
            System.out.println(" Amount: $" + transfer.getAmount());
        }
        int input = consoleService.promptForInt("Please enter transfer id (0 to cancel): ");

        if (input == 0){
            mainMenu();
        } else {
            System.out.println("\nTransfer Details: ");
            for(Transfer transfer : transfers){
                if(input == transfer.getTransferId()) {
                    toUsername = accountService.getUserByAccountId(transfer.getAccountToId()).getUsername();
                    fromUsername = accountService.getUserByAccountId(transfer.getAccountFromId()).getUsername();
                    System.out.println("ID: " + transfer.getTransferId());
                    System.out.println("From: " + fromUsername);
                    System.out.println("To: " + toUsername);
                    System.out.println("Type: " + resolveTransferType(transfer.getTransferType()));
                    System.out.println("Status: " + resolveTransferStatus(transfer.getTransferStatus()));
                    System.out.println("Amount: $" + transfer.getAmount());
                }
            }
            mainMenu();
        }
    }

    private void viewPendingRequests() {

        final int APPROVED_TRANSFER_STATUS = 2;
        final int REJECTED_TRANSFER_STATUS = 3;

        int accountId = accountService.getAccountById(currentUserId).getAccountId();
        Transfer[] transfers = accountService.getTransfersByAccountId(accountId);
        System.out.println("\nPending Transfers: ");
        for(Transfer transfer : transfers){
            if (transfer.getTransferStatus() == 1 && transfer.getAccountFromId() == accountId) {
                System.out.println("ID: " + transfer.getTransferId());
                System.out.println("From: " + accountService.getUserByAccountId(transfer.getAccountToId()).getUsername());
                System.out.println("Amount: $" + transfer.getAmount());
            }
        }
        int input = consoleService.promptForInt("\nPlease enter transfer ID to approve/reject (0 to cancel): ");
        Transfer userChoice = null;

        if(input == 0) {
            mainMenu();
        } else {
            for(Transfer transfer : transfers){
                if(input == transfer.getTransferId()) {
                    userChoice = transfer;
                }
            }
            if(userChoice != null) {
                System.out.println("1: Approve ");
                System.out.println("2: Reject ");
                System.out.println("0: Don't approve or reject ");
                System.out.println("----------------------");
                input = consoleService.promptForInt("\nPlease choose an option: ");
                if (input == 0) {
                    System.out.println("Okay, returning to main menu.");
                    mainMenu();
                }
                else if(input == 1) {
                    userChoice.setTransferStatus(APPROVED_TRANSFER_STATUS);
                    transferService.approveOrRejectTransfer(userChoice);
                }
                else if (input == 2) {
                    userChoice.setTransferStatus(REJECTED_TRANSFER_STATUS);
                    transferService.approveOrRejectTransfer(userChoice);
                }
                else {
                    System.out.println("Invalid entry. Returning to main menu.");
                    mainMenu();
                }
            }


        }
    }

	private void sendBucks() {
        // initialize vars
		User[] users = accountService.getUsers();
        String fromUsername = currentUser.getUser().getUsername();
        String toUsername;

        // user chooses who to send bucks to
        consoleService.displayOtherUsers(users, currentUser.getUser());
        int userId;
        do {
            userId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        }
        while (userId == currentUser.getUser().getId());
        toUsername = accountService.getUserById(userId).getUsername();

        // user chooses amount to send
        BigDecimal amount = null;
        do {
           amount = consoleService.promptForBigDecimal("Enter amount to transfer: ");
        }while(amount.compareTo(BigDecimal.ZERO) <= 0 &&
                amount.compareTo(accountService.getAccountById(currentUserId).getBalance()) > 0);
        if(!overdraftProtection(amount)){
            System.out.println("overdraft");
            return;
        }

        // program creates new transfer for database
        int currentUserAccountId = accountService.getAccountById(currentUserId).getAccountId();
        int toUserAccountId = accountService.getAccountById(userId).getAccountId();
        Transfer newTransfer = new Transfer(currentUserAccountId, toUserAccountId, amount, 2, 2);
        Transfer receivedTransfer = transferService.transfer(newTransfer);
        System.out.println("You transferred $" + receivedTransfer.getAmount() + " to this user: " + toUsername);

	}

    private void requestBucks() {
        User[] users = accountService.getUsers();
        String fromUsername;
        String toUsername =  currentUser.getUser().getUsername();

        consoleService.displayOtherUsers(users, currentUser.getUser());
        int userId;
        do {
            userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        }
        while (userId == currentUser.getUser().getId());
        fromUsername = accountService.getUserById(userId).getUsername();

        BigDecimal amount = null;
        do {
            amount = consoleService.promptForBigDecimal("Enter amount to request: ");
        }while(overdraftProtection(amount) && amount.compareTo(BigDecimal.ZERO) <= 0);
        if(!overdraftProtection(amount)){
            System.out.println("overdraft");
            return;
        }

        int fromUserAccountId = accountService.getAccountById(userId).getAccountId();
        int currentUserAccountId = accountService.getAccountById(currentUserId).getAccountId();
        Transfer newTransfer = new Transfer(fromUserAccountId, currentUserAccountId, amount, 1, 1);
        Transfer receivedTransfer = transferService.transfer(newTransfer);
        System.out.println("You requested $" + receivedTransfer.getAmount() + " from this account: " + fromUsername);
    }

    public boolean overdraftProtection(BigDecimal amount){
        if(amount == null){
            return false;
        }
        return amount.compareTo(accountService.getAccountById(currentUserId).getBalance()) <= 0;
    }

    public String resolveTransferType(int transType) {
        if (transType == 1) {
            return "Request";
        }
        else if (transType == 2) {
            return "Send";
        }
        return null;
    }

    public String resolveTransferStatus(int transStatus) {
        if (transStatus == 1) {
            return "Pending";
        } else if (transStatus == 2) {
            return "Approved";
        } else if (transStatus == 3) {
            return "Rejected";
        }
        return null;
    }
}
