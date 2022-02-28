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
			Startup();
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
		System.out.println(u);
		SessionCache.setCurrentUser(u);
		return u;
	}
	
	public static void Selection(User user) {
		Scanner input = new Scanner(System.in);
		
		if(user.getUserType().equals(UserType.CUSTOMER)) {
			//As a customer, I can apply for a new bank account with a starting balance
			//As a customer, I can view the balance of a specific account
			//As a customer, I can make a deposit to a specific account
			//As a customer, I can make a withdrawal from a specific account
			//As a customer, I can post a money transfer to another account.
		} else if(user.getUserType().equals(UserType.EMPLOYEE)) {
			//As an employee, I can approve or reject an account.
			//As an employee, I can view a log of all transactions.
		}
		//USER
			//As a user, I can register with a username and password - 	DONE
			//As a user, I can login with a username and password - 	DONE
		//SYSTEM
			//As the system, I reject registration attempts for usernames that already exist -	DONE
			//As the system, I reject login attempts with invalid credentials -					DONE
			//As the system, I reject and prevent overdrafts
			//As the system, I reject deposits or withdrawals of negative money
			//As the system, I reject any transactions of unapproved accounts
			//As the system, I reject invalid transfers (negative amounts or overdrafts)
		
		System.out.println("What would you like to do?: "
				+ "\nApply for new account (Type new account)"
				+ "\nView balance of my account (Type view balance)"
				+ "\nDeposit to an account (Type deposit)"
				+ "\nMake a withdrawal (Type withdrawal)"
				+ "\nTransfer money to an account (Type transfer)"
				+ "\nView log of all transactions (Type transactions)");
		String choice = input.nextLine().toUpperCase();
		switch(choice) {
		case "TRANSACTIONS":
			List<Transaction> allTrans = SeeAllTransactions();
			allTrans.forEach((a) -> System.out.println(a));
			break;
		case "NEW ACCOUNT":
			System.out.println(NewAccount(user));
			break;
		case "MY ACCOUNTS":
			List<Account> myActs = MyAccounts(user);
			myActs.forEach((a) -> System.out.println(a));
			break;
		}
		Selection(user);
	}

	public static List<Transaction> SeeAllTransactions() {
		List<Transaction> trans;
		TransactionDaoFile tdf = new TransactionDaoFile();
		trans = tdf.getAllTransactions();
		return trans;
	}
	
	public static Account NewAccount(User user) {
		Account a;
		AccountService as = new AccountService(null);
		a = as.createNewAccount(user);
		return a;
	}
	
	public static List<Account> MyAccounts(User user) {
		List<Account> myAccounts;
		AccountDaoFile adf = new AccountDaoFile();
		myAccounts = adf.getAccountsByUser(user);
		return myAccounts;
	}
	
	//End of bank driver class
}