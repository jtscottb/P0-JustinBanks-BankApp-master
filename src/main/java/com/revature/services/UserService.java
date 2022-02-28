package com.revature.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.User;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction.TransactionType;
import com.revature.beans.User.UserType;
import com.revature.dao.AccountDao;
import com.revature.dao.AccountDaoFile;
import com.revature.dao.UserDao;
import com.revature.dao.UserDaoFile;
import com.revature.driver.BankApplicationDriver;
import com.revature.exceptions.InvalidCredentialsException;

/**
 * This class should contain the business logic for performing operations on users
 */
public class UserService {
	
	UserDao userDao;
	AccountDao accountDao;
	
	public UserService(UserDao udao, AccountDao adao) {
		this.userDao = udao;
		this.accountDao = adao;
	}
	
	/**
	 * Validates the username and password, and return the User object for that user
	 * @throws InvalidCredentialsException if either username is not found or password does not match
	 * @return the User who is now logged in
	 */
	public User login(String username, String password) {
		UserDaoFile udf = new UserDaoFile();
		User user = (User) udf.getUser(username, password);
		if(user == null) {
			System.out.println("Incorrect username or password");
//			BankApplicationDriver.SignIn();
			throw new InvalidCredentialsException();
		}
		return user;
	}
	
	/**
	 * Creates the specified User in the persistence layer
	 * @param newUser the User to register
	 * @throws UsernameAlreadyExistsException if the given User's username is taken
	 */
	public void register(User newUser) {
		Scanner input = new Scanner(System.in);
		
		File[] files = new File("Users").listFiles();
		String doc = null;
		int nextID = 0;
		if(files.length == 0) {
			nextID = 1;
		} else {
			for(File f : files) {
				doc = f.getName();
			}
			nextID = Integer.parseInt(doc.split("\\.", 2)[0]) + 1;
		}
		newUser.setId(nextID);

		System.out.println("Enter username: ");
		String username = input.next();
		newUser.setUsername(username);

		System.out.println("Enter password: ");
		String password = input.next();
		newUser.setPassword(password);

		System.out.println("Enter First Name: ");
		String fname = input.next().toUpperCase();
		newUser.setFirstName(fname);

		System.out.println("Enter Last Name: ");
		String lname = input.next().toUpperCase();
		newUser.setLastName(lname);

		System.out.println("Enter Customer or Employee: ");
		String userType = input.next().toUpperCase();
		switch (userType) {
		case "CUSTOMER":
			newUser.setUserType(UserType.CUSTOMER);
			break;
		case "EMPLOYEE":
			newUser.setUserType(UserType.EMPLOYEE);
			break;
		default:
			System.out.println("Spelling error!");
			input.close();
			break;
		}
		
		List<Account> myAccounts = new ArrayList<>();
		myAccounts = null;
		newUser.setAccounts(myAccounts);
		
//		Write New User to a File
		UserDaoFile userDaoFile = new UserDaoFile();
		userDaoFile.addUser(newUser);
	}
}
