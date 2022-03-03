package com.revature.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import com.revature.exceptions.UsernameAlreadyExistsException;

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
		UserDaoFile udf = new UserDaoFile();
//		CHECK TO SEE IF ANOTHER USER HAS USERNAME
		List<User> users = new ArrayList<User>();
		users = udf.getAllUsers();
		List<String> usernameList = new ArrayList<String>();
		for(User u : users) {
			usernameList.add(u.getUsername());
		}
//		THROW EXCEPTION IF USERNAME EXISTS
		if(usernameList.contains(newUser.getUsername())) {
			System.out.println("Username already taken");
			throw new UsernameAlreadyExistsException();
		} else {
	//		CREATE NEW USER FILE
			udf.addUser(newUser);
		}
	}
}
