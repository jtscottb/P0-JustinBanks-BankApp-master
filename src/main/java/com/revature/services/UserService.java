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
//		Checking if username already exists
		List<String> usernameList = new ArrayList<String>();
		for(File file : files) {
			try {
				FileInputStream fileIn = new FileInputStream("Users\\" + file.getName());
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				User user = (User) objectIn.readObject();
				usernameList.add(user.getUsername());
				objectIn.close();
				fileIn.close();
			} catch(FileNotFoundException e) {
				System.out.println("User is not found");
				e.printStackTrace();
			} catch(IOException e) {
				System.out.println("Could not return user");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("User not found. You must register first");
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
//		Throw exception if username exists
		if(usernameList.contains(username)) {
			System.out.println("Username already taken");
			throw new UsernameAlreadyExistsException();
		} else {
//			Continue program for new username
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
	
			System.out.println("(1.) Customer or \n(2.) Employee");
			int userType = input.nextInt();
			switch (userType) {
			case 1:
				newUser.setUserType(UserType.CUSTOMER);
				break;
			case 2:
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
}
