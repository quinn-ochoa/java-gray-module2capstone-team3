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
                continue;
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
        // TODO Auto-generated method stub
        User[] users = accountService.getUsers();
//        User toUser;
//        User fromUser;
        int accountId = accountService.getAccountById(currentUserId).getAccountId();
        Transfer[] transfers = accountService.getTransfersByAccountId(accountId);
        System.out.println("\nTransfers: ");
        for(Transfer transfer : transfers){
            //TODO need access to transfer ID
            System.out.print("ID: " + transfer.getTransferId());
            boolean isCurrentUserFromId = accountId == transfer.getAccountFromId();
            System.out.print((isCurrentUserFromId) ? (" To: ") : (" From: "));
            System.out.print((isCurrentUserFromId) ? (transfer.getAccountToId()) : (transfer.getAccountFromId()));
            //TODO replace account number in prev line w/ username
            System.out.println(" Amount: " + transfer.getAmount());
        }
        int input = consoleService.promptForInt("Please enter transfer id (0 to cancel): ");

        if (input == 0){
            mainMenu();
        } else {
            System.out.println("\nTransfer Details: ");
            for(Transfer transfer : transfers){
                if(input == transfer.getTransferId()) {
                    System.out.println("ID: " + transfer.getTransferId());
//                    System.out.println("From: " + transfer.getAccountFromUsername()); //should be account name
//                    System.out.println("To: " + transfer.getAccountToUsername()); //should be account name
                    System.out.println("Type: " + transfer.getTransferType());
                    System.out.println("Status: " + transfer.getTransferStatus());
                    System.out.println("Amount: $" + transfer.getAmount());
                }
            }
            mainMenu();
        }
    }

//	private void viewTransferHistory() {
//		// TODO Auto-generated method stub
//        int accountId = accountService.getAccountById(currentUserId).getAccountId();
//        Transfer[] transfers = accountService.getTransfersByAccountId(accountId);
//        for(Transfer transfer : transfers){
//            //TODO need access to transfer ID
//            System.out.println("From: " + transfer.getAccountFromId());
//            System.out.println("To: " + transfer.getAccountToId());
//            System.out.println("Amount: " + transfer.getAmount());
//            consoleService.promptForInt("Please enter transfer id: (0 to cancel)");
//        }
//
//	}

    private void viewPendingRequests() {
        // TODO Auto-generated method stub
        int accountId = accountService.getAccountById(currentUserId).getAccountId();
        Transfer[] transfers = accountService.getTransfersByAccountId(accountId);
        System.out.println("\nPending Transfers: ");
        for(Transfer transfer : transfers){
            if (transfer.getTransferStatus() == 1) {
                System.out.println("ID: " + transfer.getTransferId());
                System.out.println("To: " + transfer.getAccountToUsername()); //should be account name
                System.out.println("Amount: " + transfer.getAmount());
            }
        }
        int input = consoleService.promptForInt("\nPlease enter transfer ID to approve/reject (0 to cancel): ");
        int userChoice = 0;

        if(input == 0) {
            mainMenu();
        } else {
            for(Transfer transfer : transfers){
                if(input == transfer.getTransferId()) {
                    userChoice = transfer.getTransferId();
                }
            }
            if(userChoice != 0) {
                System.out.println("1: Approve ");
                System.out.println("2: Reject ");
                System.out.println("0: Don't approve or reject ");
                System.out.println("----------------------");
                input = consoleService.promptForInt("\nPlease choose an option: ");

            }
        }
    }

	private void sendBucks() {
		User[] users = accountService.getUsers();

        consoleService.displayOtherUsers(users, currentUser.getUser());
        int userId;
        do {
            userId = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        }
        while (userId == currentUser.getUser().getId());
        BigDecimal amount = null;
        do {
           amount = consoleService.promptForBigDecimal("Enter amount to transfer: ");
        }while(amount.compareTo(BigDecimal.ZERO) <= 0 &&
                amount.compareTo(accountService.getAccountById(currentUserId).getBalance()) > 0);

        Transfer newTransfer = new Transfer(currentUserId, userId, amount, 2, 2);
        Transfer receivedTransfer = transferService.transfer(newTransfer);
        System.out.println("You transferred $" + receivedTransfer.getAmount() + " to this account: " + receivedTransfer.getAccountToId());

	}

    private void requestBucks() {
        // TODO Auto-generated method stub
        User[] users = accountService.getUsers();
        consoleService.displayOtherUsers(users, currentUser.getUser());
        int userId;
        do {
            userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        }
        while (userId == currentUser.getUser().getId());
        BigDecimal amount = null;
        do {
            amount = consoleService.promptForBigDecimal("Enter amount to request: ");
        }while(amount.compareTo(BigDecimal.ZERO) <= 0);

        Transfer newTransfer = new Transfer(userId, currentUserId, amount, 1, 1);
        Transfer receivedTransfer = transferService.transfer(newTransfer);
        System.out.println("You requested $" + receivedTransfer.getAmount() + " from this account: " + receivedTransfer.getAccountToId());
    }
}
