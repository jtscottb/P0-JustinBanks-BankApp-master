package com.revature.driver;

import java.io.*;
import java.util.*;

import com.revature.beans.Account;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction;
import com.revature.beans.Transaction.TransactionType;
import com.revature.beans.User;
import com.revature.beans.User.UserType;
import com.revature.dao.AccountDao;
import com.revature.dao.AccountDaoDB;
import com.revature.dao.AccountDaoFile;
import com.revature.dao.TransactionDaoDB;
import com.revature.dao.TransactionDaoFile;
import com.revature.dao.UserDao;
import com.revature.dao.UserDaoDB;
import com.revature.dao.UserDaoFile;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.exceptions.OverdraftException;
import com.revature.exceptions.UsernameAlreadyExistsException;
import com.revature.services.AccountService;
import com.revature.services.UserService;
import com.revature.utils.SessionCache;

/**
 * This is the entry point to the application
 */
public class BankApplicationDriver {
	
	public static String spacer = new String(new char[170]).replace("\0", "-");

	public static void main(String[] args) {
		// your code here...
		User user;
		user = Startup();
		if(user == null) {
			user = SignIn();
		}
		Selection(user);
	}
	
	public static User Startup() {
		Scanner input = new Scanner(System.in);
		System.out.println("Welcome \n(1.) Register \n(2.) Sign in");
		User u = null;
		int action = input.nextInt();
		
		switch(action) {
		case 1:
			BeginRegistration();
			break;
		case 2:
			u = SignIn();
			break;
		default:
			System.out.println("Enter 1 or 2");
			System.out.println(spacer + "\n" + spacer);
			u = Startup();
			break;
		}
		return u;
	}
	
	public static void BeginRegistration() {
		Scanner input = new Scanner(System.in);
		User newUser = new User();
		System.out.println(spacer + "\n" + spacer);
		
//		CHOOSE USER TYPE
		System.out.println("Select user type: \n(1.) Customer \n(2.) Employee");
		int userType = input.nextInt();
		switch (userType) {
		case 1:
			newUser.setUserType(UserType.CUSTOMER);
			break;
		case 2:
			newUser.setUserType(UserType.EMPLOYEE);
			break;
		default:
			System.out.println("Press [1] or [2] only");
			BeginRegistration();
			break;
		}
		
//		SELECT USERNAME
		System.out.println("Enter username: ");
		String username = input.next().toUpperCase();
		newUser.setUsername(username);
		
//		CHOOSE PASSWORD
		System.out.println("Enter password: ");
		String password = input.next();
		newUser.setPassword(password);
		
//		ENTER FIRST NAME
		System.out.println("Enter First Name: ");
		String fname = input.next().toUpperCase();
		newUser.setFirstName(fname);
		
//		ENTER LAST NAME
		System.out.println("Enter Last Name: ");
		String lname = input.next().toUpperCase();
		newUser.setLastName(lname);
		
//		SET EMPTY ACCOUNTS LIST
		newUser.setAccounts(new ArrayList<Account>());
		
//		CHECK REGISTRATION
		UserService us = new UserService(null, null);
		try {
			us.register(newUser);
		} catch(UsernameAlreadyExistsException ex) {
			System.out.println("Username already exists. Choose a different username");
			BeginRegistration();
		}
	}
	
	public static User SignIn() {
		Scanner input = new Scanner(System.in);
		UserService us = new UserService(null, null);
		User u = new User();
		System.out.println(spacer + "\n" + spacer);
		
//		OBTAIN LOGIN INFO
		System.out.println("Enter username: ");
		String username = input.next().toUpperCase();
		System.out.println("Enter password: ");
		String password = input.next();
		
//		CHECK CREDENTIALS
		try {
			u = us.login(username, password);
		} catch(InvalidCredentialsException ex) {
			System.out.println("Incorrect username or password");
			u = SignIn();
		}
		
//		RETURN LOGGED IN USER
		return u;
	}
	
	public static void Selection(User user) {
		User customer = new User();
		customer.setUserType(UserType.CUSTOMER);
		User employee = new User();
		employee.setUserType(UserType.EMPLOYEE);
		System.out.println(spacer + "\n" + spacer);
		
		System.out.println("\nWelcome " + user.getFirstName() + " " + user.getLastName());
		
		if(user.getUserType().equals(customer.getUserType())) {
			/*	As a customer, I can apply for a new bank account with a starting balance	DONE - 3
				As a customer, I can view the balance of a specific account					DONE - 1 
				As a customer, I can make a deposit to a specific account					DONE - 2
				As a customer, I can make a withdrawal from a specific account				DONE - 2
				As a customer, I can post a money transfer to another account.				DONE - 3
																							TOTAL - 11
			*/
			Customer(user);
		} else if(user.getUserType().equals(employee.getUserType())) {
			/*	As an employee, I can approve or reject an account.		DONE - 2
				As an employee, I can view a log of all transactions.	DONE - 2
																		TOTAL - 4
			*/
			Employee(user);
		}
		
		/*	USER
				As a user, I can register with a username and password 		DONE - 1
				As a user, I can login with a username and password	 		DONE - 1
			SYSTEM
				As the system, I reject registration attempts for usernames that already exist	DONE - 1
				As the system, I reject login attempts with invalid credentials					DONE - 1
				As the system, I reject and prevent overdrafts									DONE - 1
				As the system, I reject deposits or withdrawals of negative money				DONE - 2
				As the system, I reject any transactions of unapproved accounts					DONE - 1
				As the system, I reject invalid transfers (negative amounts or overdrafts)		DONE - 1
																								TOTAL - 9
		 */
	}
	
	public static void Customer(User user) {
		Scanner input = new Scanner(System.in);
		int choice = 0;
		System.out.println(spacer + "\n" + spacer);

		System.out.println("What would you like to do?: "
				+ "\n(1.) Apply for new account"
				+ "\n(2.) View balance of my account"
				+ "\n(3.) Deposit to an account"
				+ "\n(4.) Make a withdrawal"
				+ "\n(5.) Transfer money to an account"
				+ "\n(6.) Exit");
		
//		CHOICE SETUP
		choice = input.nextInt();
		
		switch(choice) {
		case 1:	//Apply for new account
			ApplyForNewAccount(user);
			break;
			
		case 2:	//View balance of my account
			ViewBalance(user);
			break;
			
		case 3:	//Deposit to an account
			MakeDeposit(user);
			break;
			
		case 4:	//Make a withdrawal
			MakeWithdrawal(user);
			break;
			
		case 5:	//Transfer money to an account
			MakeTransfer(user);
			break;
		case 6:
			input.close();
			break;
		}
		
		if(choice > 5) {
			System.out.println("Goodbye");
		} else {
			Customer(user);
		}
	}
	
	public static void ApplyForNewAccount(User user) {
		Scanner input = new Scanner(System.in);
		AccountService as = new AccountService(null);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		TransactionDaoFile tdf = new TransactionDaoFile();
		TransactionDaoDB tdb = new TransactionDaoDB();
		Account a = new Account();
		System.out.println(spacer);
		
		a = as.createNewAccount(user);
		a = adf.getAccount(a.getId());
//		a = adb.getAccount(a.getId());
		
//		CHOOSE ACCOUNT TYPE
		System.out.println("Choose account type: \n(1.) Checking \n(2.) Savings");
		int accountType = input.nextInt();
		switch(accountType) {
		case 1:
			a.setType(AccountType.CHECKING);
			break;
		case 2:
			a.setType(AccountType.SAVINGS);
			break;
		default:
			System.out.println("Enter [1] or [2]");
			adf.removeAccount(a);
			adb.removeAccount(a);
			tdf.removeTransactionsByAccount(a);
			tdb.removeTransactionsByAccount(a);
			ApplyForNewAccount(user);
			break;
		}
		
		a = adf.updateAccount(a);
		a = adb.updateAccount(a);
		System.out.println("Account added \n" + a);
	}
	
	public static void ViewBalance(User user) {
		Scanner input = new Scanner(System.in);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		List<Account> accounts = adf.getAccountsByUser(user);
//		List<Account> accounts = adb.getAccountsByUser(user);
		Account a = new Account();
		System.out.println(spacer);
		
//		PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
		accounts.forEach((account) -> System.out.println("Account ID: " + account.getId() + "\t" + account.getType()));
		
//		SELECT ACCOUNT TO VIEW BALANCE
		System.out.println("\nEnter account number: ");
		int num = input.nextInt();
		if(accounts.isEmpty()) {
			System.out.println("You don't have any accounts yet");
		} else if(!adf.getAccount(num).getOwnerId().equals(user.getId())) {
			System.out.println("This is not one of your accounts. Enter your account number");
			ViewBalance(user);
		} else {
//			COPY ACCOUNT AND PRINT BALANCE
			a = adf.getAccount(num);
//			a = adb.getAccount(num);
			System.out.println("$" + a.getBalance());
		}
	}
	
	public static void MakeDeposit(User user) {
		Scanner input = new Scanner(System.in);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		AccountService as = new AccountService(null);
		List<Account> accounts = adf.getAccountsByUser(user);
//		List<Account> accounts = adb.getAccountsByUser(user);
		Account a = new Account();
		System.out.println(spacer);
		
//		PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
		accounts.forEach((account) -> System.out.println("Account ID: " + account.getId() + "\t" + account.getType()));
		
//		CHOOSE ACCOUNT TO DEPOSIT AND COPY ACCOUNT
		System.out.println("\nEnter account number: ");
		int num = input.nextInt();
		if(accounts.isEmpty()) {
			System.out.println("You have no accounts yet!");
		} else if(!adf.getAccount(num).getOwnerId().equals(user.getId())) {
			System.out.println("This is not one of your accounts. Enter your account number");
			MakeDeposit(user);
		} else {
			a = adf.getAccount(num);
//			a = adb.getAccount(num);
		
//			ENTER AMOUNT TO DEPOSIT
			System.out.println("\nEnter amount to deposit: ");
			double amount = input.nextDouble();
			
//			DEPOSIT AMOUNT AND PRINT UPDATED ACCOUNT
			try {
				as.deposit(a, amount);
				System.out.println(adf.getAccount(num));
			} catch(UnsupportedOperationException ex) {
				System.out.println("An error has occurred");
				ex.getLocalizedMessage();
				MakeDeposit(user);
			}
		}
	}
	
	public static void MakeWithdrawal(User user) {
		Scanner input = new Scanner(System.in);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		AccountService as = new AccountService(null);
		List<Account> accounts = adf.getAccountsByUser(user);
//		List<Account> accounts = adb.getAccountsByUser(user);
		Account a = new Account();
		System.out.println(spacer);
		
//		PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
		accounts.forEach((account) -> System.out.println("Account ID: " + account.getId() + "\t" + account.getType()));
		
//		CHOOSE ACCOUNT TO WITHDRAW AND COPY ACCOUNT
		System.out.println("\nEnter account number: ");
		int num = input.nextInt();
		if(accounts.isEmpty()) {
			System.out.println("You have no accounts yet!");
		} else if(!adf.getAccount(num).getOwnerId().equals(user.getId())) {
			System.out.println("This is not one of your accounts. Enter your account number");
			MakeWithdrawal(user);
		} else {
			a = adf.getAccount(num);
	//		a = adb.getAccount(num);
			
//			ENTER AMOUNT TO WITHDRAW
			System.out.println("\nEnter amount to withdraw: ");
			double amount = input.nextDouble();
			
//			WITHDRAW AMOUNT AND PRINT UPDATED ACCOUNT
			try {
				as.withdraw(a, amount);
				System.out.println(adf.getAccount(num));
			} catch(UnsupportedOperationException ex) {
				System.out.println("An error has occurred");
				ex.getLocalizedMessage();
				MakeWithdrawal(user);
			} catch(OverdraftException ex) {
				System.out.println("The amount entered is more than is in the account");
				MakeWithdrawal(user);
			}
		}
	}
	
	public static void MakeTransfer(User user) {
		Scanner input = new Scanner(System.in);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		AccountService as = new AccountService(null);
		List<Account> accounts = adf.getAccountsByUser(user);
//		List<Account> accounts = adb.getAccountsByUser(user);
		Account a = new Account();
		Account a2 = new Account();
		System.out.println(spacer);
		
//		PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
		accounts.forEach((account) -> System.out.println("Account ID: " + account.getId() + "\t" + account.getType()));
		
//		CHOOSE WHICH ACCOUNT TO WITHDRAW FROM
		System.out.println("\nEnter account number to withdraw from: ");
		int num = input.nextInt();
		
//		COPY WITHDRAW ACCOUNT
		if(accounts.isEmpty()) {
			System.out.println("You have no accounts yet");
		} else if(!adf.getAccount(num).getOwnerId().equals(user.getId())) {
			System.out.println("You're not authorized to withdraw from this account");
			MakeTransfer(user);
		} else {
			a = adf.getAccount(num);
//			a = adb.getAccount(num);
			
//			CHOOSE WHICH ACCOUNT TO DEPOSIT INTO
			System.out.println("\nEnter account number to deposit into: ");
			int num2 = input.nextInt();
			
//			COPY DEPOSIT ACCOUNT
			AccountDaoFile adf2 = new AccountDaoFile();
			a2 = adf2.getAccount(num2);
			
//			SET TRANSFER AMOUNT
			System.out.println("\nEnter amount to transfer: ");
			double amount = input.nextDouble();
			
//			TRANSFER AMOUNT AND PRINT BOTH UPDATED ACCOUNTS
			try {
				as.transfer(a, a2, amount);
				System.out.println(adf.getAccount(a.getId()) + "\n" + adf.getAccount(a2.getId()));
			} catch(UnsupportedOperationException ex) {
				System.out.println("An error has occurred");
				ex.getLocalizedMessage();
				MakeTransfer(user);
			}
		}
	}

	public static void Employee(User user) {
		Scanner input = new Scanner(System.in);
		int choice = 0;
		System.out.println(spacer + "\n" + spacer);
		
		System.out.println("What would you like to do?: "
				+ "\n(1.) Approve or reject an account"
				+ "\n(2.) View log of all transactions"
				+ "\n(3.) Delete an account"
				+ "\n(4.) Exit");
			
		choice = input.nextInt();
		
		switch(choice) {
		case 1:
			UpdateApproval();
			break;
			
		case 2:
			ListAllTransactions();
			break;
			
		case 3:
			DeleteAccount();
			break;
			
		case 4:
			input.close();
			break;
		}
		if(choice > 3) {
			System.out.println("Goodbye");
		} else {
			Employee(user);
		}
	}
	
	public static void UpdateApproval() {
		Scanner input = new Scanner(System.in);
//		SETUP
		List<Account> accounts = new ArrayList<Account>();
		AccountService as = new AccountService(null);
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		Account a = new Account();
		System.out.println(spacer);
		
//		GET AND PRINT LIST OF ALL ACCOUNTS
		accounts = adf.getAccounts();
//		accounts = adb.getAccounts();
		accounts.forEach(account -> System.out.println(account));
		
//		CHOOSE AND COPY SELECTED ACCOUNT
		System.out.println("\nEnter account number: ");
		int accountID = input.nextInt();
		if(accounts.isEmpty()) {
			System.out.println("There are no accounts to delete");
		} else if(!accounts.contains(adf.getAccount(accountID))) {
			System.out.println("The chosen account does not exist");
			UpdateApproval();
		} else {
			a = adf.getAccount(accountID);
//			a = adb.getAccount(accountID);
			
//			CHANGE APPROVAL STATUS
			System.out.println("\nEnter \n(1.) Approved \n(2.) Unapproved");
			boolean approval = (input.nextInt() == 1) ? true : false;
			as.approveOrRejectAccount(a, approval);
		}
	 }
	
	public static void ListAllTransactions() {
//		SETUP
		TransactionDaoFile tdf = new TransactionDaoFile();
		TransactionDaoDB tdb = new TransactionDaoDB();
		List<Transaction> allTransactions = new ArrayList<Transaction>();
		System.out.println(spacer);
		
//		GET LIST OF ALL TRANSACTIONS BY ALL USERS
		allTransactions = tdf.getAllTransactions();
//		allTransactions = tdb.getAllTransactions();
		
//		PRINT ALL TRANSACTIONS
		allTransactions.forEach((trans) -> System.out.println(trans));
	}
	
	public static void DeleteAccount() {
		Scanner input = new Scanner(System.in);
		List<Account> accounts = new ArrayList<Account>();
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		TransactionDaoFile tdf = new TransactionDaoFile();
		TransactionDaoDB tdb = new TransactionDaoDB();
		System.out.println(spacer);
		
//		GET AND PRINT LIST OF ALL ACCOUNTS
		accounts = adf.getAccounts();
//		accounts = adb.getAccounts();
		accounts.forEach(account -> System.out.println(account));
		
//		DELETE SELECTED ACCOUNT AND TRANSACTIONS
		System.out.println("\nEnter account number: ");
		int accountID = input.nextInt();
		if(accounts.isEmpty()) {
			System.out.println("There are no accounts to delete");
		} else if(!accounts.contains(adf.getAccount(accountID))) {
			System.out.println("The selected account does not exist");
			DeleteAccount();
		} else {
			Account a = adf.getAccount(accountID);
//			Account a = adb.getAccount(accountID);
			adf.removeAccount(a);
			adb.removeAccount(a);
			tdf.removeTransactionsByAccount(a);
			tdb.removeTransactionsByAccount(a);
			System.out.println("Account deleted");
		}
	}

}