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
import com.revature.dao.AccountDaoFile;
import com.revature.dao.TransactionDaoFile;
import com.revature.dao.UserDao;
import com.revature.dao.UserDaoFile;
import com.revature.services.AccountService;
import com.revature.services.UserService;
import com.revature.utils.SessionCache;

/**
 * This is the entry point to the application
 */
public class BankApplicationDriver {

	public static void main(String[] args) {
		// your code here...
		User user;
		user = Startup();
		if(SessionCache.getCurrentUser() == null) {
			user = SignIn();
		}
		System.out.println(user);
		Selection(user);
	}
	
	public static User Startup() {
		Scanner input = new Scanner(System.in);
		System.out.println("(1.) Sign in \n(2.) Register?");
		User u = null;
		int action = input.nextInt();
		
		switch(action) {
		case 1:
			BeginRegistration();
		case 2:
			u = SignIn();
		default:
			System.out.println("Enter 1 or 2");
			u = Startup();
		}
		return u;
	}
	
	public static void BeginRegistration() {
		UserService us = new UserService(null, null);
		User newUser = new User();
		us.register(newUser);
	}
	
	public static User SignIn() {
		Scanner input = new Scanner(System.in);
		UserService us = new UserService(null, null);
//		OBTAIN LOGIN INFO
		System.out.println("Enter username: ");
		String username = input.next();
		System.out.println("Enter password: ");
		String password = input.next();
//		CHECK CREDENTIALS
		User u = us.login(username, password);
		
//		STORE WHO'S LOGGED ON
		SessionCache.setCurrentUser(u);
//		RETURN LOGGED IN USER
		return u;
	}
	
	public static void Selection(User user) {
		User customer = new User();
		customer.setUserType(UserType.CUSTOMER);
		User employee = new User();
		employee.setUserType(UserType.EMPLOYEE);
		
		if(user.getUserType().equals(customer.getUserType())) {
			//As a customer, I can apply for a new bank account with a starting balance		DONE - 3
			//As a customer, I can view the balance of a specific account					DONE - 1 
			//As a customer, I can make a deposit to a specific account						DONE - 2
			//As a customer, I can make a withdrawal from a specific account				DONE - 2
			//As a customer, I can post a money transfer to another account.
			//8
			Customer(user);
		} else if(user.getUserType().equals(employee.getUserType())) {
			//As an employee, I can approve or reject an account.
			//As an employee, I can view a log of all transactions.		DONE - 2
			//2
			Employee(user);
		}
		
		//USER
			//As a user, I can register with a username and password 	DONE - 1
			//As a user, I can login with a username and password	 	DONE - 1
		//SYSTEM
			//As the system, I reject registration attempts for usernames that already exist	DONE - 1
			//As the system, I reject login attempts with invalid credentials					DONE - 1
			//As the system, I reject and prevent overdrafts									DONE - 1
			//As the system, I reject deposits or withdrawals of negative money					DONE - 2
			//As the system, I reject any transactions of unapproved accounts					DONE - 1
			//As the system, I reject invalid transfers (negative amounts or overdrafts)		DONE - 1
		//9
	}
	
	public static void Customer(User user) {
		Scanner input = new Scanner(System.in);
		int choice = 0;

		System.out.println("What would you like to do?: "
				+ "\n(1.) Apply for new account"
				+ "\n(2.) View balance of my account"
				+ "\n(3.) Deposit to an account"
				+ "\n(4.) Make a withdrawal"
				+ "\n(5.) Transfer money to an account");
		
//		CHOICE SETUP
		choice = input.nextInt();
		AccountService as = new AccountService(null);
		AccountDaoFile adf = new AccountDaoFile();
		List<Account> accounts = adf.getAccountsByUser(user);
		Account a = new Account();
		Account a2 = new Account();
		int num = 0;
		double amount = 0;
		
		switch(choice) {
		case 1:
			a = as.createNewAccount(user);
			a2 = adf.addAccount(a);
			System.out.println(a2);
			break;
			
		case 2:
//			PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
			accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
//			SELECT ACCOUNT TO VIEW BALANCE
			System.out.println("Enter account number: ");
			num = input.nextInt();
//			COPY ACCOUNT AND PRINT BALANCE
			a = adf.getAccount(num);
			System.out.println(a.getBalance());
			break;
			
		case 3:
//			PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
			accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
//			CHOOSE ACCOUNT TO DEPOSIT AND COPY ACCOUNT
			System.out.println("Enter account number: ");
			num = input.nextInt();
			a = adf.getAccount(num);
//			ENTER AMOUNT TO DEPOSIT
			System.out.println("Enter amount to deposit: ");
			amount = input.nextDouble();
//			DEPOSIT AMOUNT AND PRINT UPDATED ACCOUNT
			as.deposit(a, amount);
			System.out.println(adf.getAccount(num));
			break;
			
		case 4:
//			PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
			accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
//			CHOOSE ACCOUNT TO WITHDRAW AND COPY ACCOUNT
			System.out.println("Enter account number: ");
			num = input.nextInt();
			a = adf.getAccount(num);
//			ENTER AMOUNT TO WITHDRAW
			System.out.println("Enter amount to withdraw: ");
			amount = input.nextDouble();
//			WITHDRAW AMOUNT AND PRINT UPDATED ACCOUNT
			as.withdraw(a, amount);
			System.out.println(adf.getAccount(num));
			break;
			
		case 5:
//			PRINT LIST OF ACCOUNTS FOR THE CURRENT USER
			accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
//			CHOOSE WHICH ACCOUNT TO WITHDRAW FROM
			System.out.println("Enter account number to withdraw from: ");
			num = input.nextInt();
//			COPY WITHDRAW ACCOUNT
			a = adf.getAccount(num);
//			CHOOSE WHICH ACCOUNT TO DEPOSIT INTO
			System.out.println("Enter account number to deposit into: ");
			int num2 = input.nextInt();
//			COPY DEPOSIT ACCOUNT
			AccountDaoFile adf2 = new AccountDaoFile();
			a2 = adf2.getAccount(num2);
//			SET TRANSFER AMOUNT
			System.out.println("Enter amount to transfer: ");
			amount = input.nextDouble();
//			TRANSFER AMOUNT AND PRINT BOTH UPDATED ACCOUNTS
			as.transfer(a, a2, amount);
			System.out.println(adf.getAccount(a.getId()) + "\n" + adf.getAccount(a2.getId()));
			break;
			
		}
		Customer(user);
	}
	
	public static void Employee(User user) {
		Scanner input = new Scanner(System.in);
		int choice = 0;
		
		System.out.println("What would you like to do?: "
				+ "\n(1.) Approve or reject an account"
				+ "\n(2.) View log of all transactions");
			
		choice = input.nextInt();
		
		switch(choice) {
		case 1:
//			SETUP
			List<Account> accounts = new ArrayList<Account>();
			AccountService as = new AccountService(null);
			AccountDaoFile adf = new AccountDaoFile();
			int accountID = 0;
			Account a = new Account();
			boolean approval = false;
//			GET AND PRINT LIST OF ALL ACCOUNTS
			accounts = adf.getAccounts();
			accounts.forEach(account -> System.out.println(account));
//			CHOOSE AND COPY SELECTED ACCOUNT
			System.out.println("Enter account number: ");
			accountID = input.nextInt();
			a = adf.getAccount(accountID);
//			CHANGE APPROVAL STATUS
			System.out.println("Enter \n(1.) Approved \n(2.) Unapproved");
			approval = (input.nextInt() == 1) ? true : false;
			as.approveOrRejectAccount(a, approval);
			break;
			
		case 2:
//			SETUP
			TransactionDaoFile tdf = new TransactionDaoFile();
			List<Transaction> allTransactions = new ArrayList<Transaction>();
//			GET LIST OF ALL TRANSACTIONS BY ALL USERS
			allTransactions = tdf.getAllTransactions();
//			PRINT ALL TRANSACTIONS
			allTransactions.forEach((account) -> System.out.println(account));
			break;
		}
		Employee(user);
	}
	
	//End of bank driver class
}