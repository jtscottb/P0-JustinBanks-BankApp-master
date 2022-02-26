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
import com.revature.dao.UserDao;
import com.revature.dao.UserDaoFile;
import com.revature.services.UserService;
import com.revature.utils.SessionCache;

/**
 * This is the entry point to the application
 */
public class BankApplicationDriver {

	public static void main(String[] args) {
		// your code here...
		Startup();
		if(SessionCache.getCurrentUser() == null) {
			SignIn();
		}
		Selection();
		
	}
	
	public static void Startup() {
		Scanner input = new Scanner(System.in);
		System.out.println("Sign in or Register?");
		String action = input.next().toUpperCase();
		
		if(action.contains("REG")) {
			BeginRegistration();
		} else {
			SignIn();
		}
	}
	
	public static void BeginRegistration() {
		UserService us = new UserService(null, null);
		User newUser = new User();
		us.register(newUser);
	}
	
	public static void SignIn() {
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
	}
	
	public static void Selection() {
		Scanner input = new Scanner(System.in);
		System.out.println("What would you like to do?: "
				+ "\nApply for new account \nView balance of my account "
				+ "\nDeposit to an account \nMake a withdrawal "
				+ "\nTransfer money to an account \nView log of all transactions");
		String choice = input.nextLine();
		System.out.println(choice);
	}
}