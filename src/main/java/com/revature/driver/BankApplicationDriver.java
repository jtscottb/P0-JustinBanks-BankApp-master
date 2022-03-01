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
		System.out.println("Sign in or Register?");
		User u = null;
		String action = input.next().toUpperCase();
		
		if(action.contains("REG")) {
			BeginRegistration();
		} else if(action.contains("SIGN")) {
			u = SignIn();
		} else {
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
		User u = us.login(username, password);
		
//		STORE WHO'S LOGGED ON
		SessionCache.setCurrentUser(u);
		return u;
	}
	
	public static void Selection(User user) {
		Scanner input = new Scanner(System.in);
		int choice = 0;
		User customer = new User();
		customer.setUserType(UserType.CUSTOMER);
		User employee = new User();
		employee.setUserType(UserType.EMPLOYEE);
		
		if(user.getUserType().equals(customer.getUserType())) {
			//As a customer, I can apply for a new bank account with a starting balance		DONE - 3
			//As a customer, I can view the balance of a specific account					DONE - 1 
			//As a customer, I can make a deposit to a specific account
			//As a customer, I can make a withdrawal from a specific account
			//As a customer, I can post a money transfer to another account.
			//4
			System.out.println("What would you like to do?: "
					+ "\n(1.) Apply for new account (Type: 1)"
					+ "\n(2.) View balance of my account (Type: 2)"
					+ "\n(3.) Deposit to an account (Type: 3)"
					+ "\n(4.) Make a withdrawal (Type: 4)"
					+ "\n(5.) Transfer money to an account (Type: 5)");
			
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
				accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
				System.out.println("Enter account number: ");
				num = input.nextInt();
				a = adf.getAccount(num);
				System.out.println(a.getBalance());
				break;
				
			case 3:
				accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
				System.out.println("Enter account number: ");
				num = input.nextInt();
				a = adf.getAccount(num);
				System.out.println("Enter amount to deposit: ");
				amount = input.nextDouble();
				as.deposit(a, amount);
				System.out.println(a);
				break;
				
			case 4:
				accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
				System.out.println("Enter account number: ");
				num = input.nextInt();
				a = adf.getAccount(num);
				System.out.println("Enter amount to withdraw: ");
				amount = input.nextDouble();
				as.withdraw(a, amount);
				System.out.println(a);
				break;
				
			case 5:
				accounts.forEach((account) -> System.out.println("Account ID: " + account.getId()));
				System.out.println("Enter account number to withdraw from: ");
				num = input.nextInt();
				a = adf.getAccount(num);
				System.out.println("Enter account number to deposit into: ");
				int num2 = input.nextInt();
				
				AccountDaoFile adf2 = new AccountDaoFile();
				a2 = adf2.getAccount(num2);
				System.out.println("Enter amount to transfer: ");
				amount = input.nextDouble();
				
				as.transfer(a, a2, amount);
				System.out.println(user.getAccounts());
				break;
			}
		} else if(user.getUserType().equals(employee.getUserType())) {
			//As an employee, I can approve or reject an account.
			//As an employee, I can view a log of all transactions.		DONE - 2
			//2
			System.out.println("What would you like to do?: "
				+ "\n(1.) Approve or reject account (Type: 1)"
				+ "\n(2.) View log of all transactions (Type: 2)");
			
			choice = input.nextInt();
			
			switch(choice) {
			case 1:
				AccountService as = new AccountService(null);
				AccountDaoFile adf = new AccountDaoFile();
				int accountID = 0;
				Account a = new Account();
				boolean approval = false;
				System.out.println("Enter account number: ");
				accountID = input.nextInt();
				a = adf.getAccount(accountID);
				System.out.println("Enter \n(1.) Approved \n(2.) Unapproved");
				approval = (input.nextInt() == 1) ? true : false;
				as.approveOrRejectAccount(a, approval);
				break;
				
			case 2:
				TransactionDaoFile tdf = new TransactionDaoFile();
				List<Transaction> allTransactions = new ArrayList<Transaction>();
				allTransactions = tdf.getAllTransactions();
				allTransactions.forEach((a) -> System.out.println(a));
				break;
			}
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
		Selection(user);
	}
	
	//End of bank driver class
}