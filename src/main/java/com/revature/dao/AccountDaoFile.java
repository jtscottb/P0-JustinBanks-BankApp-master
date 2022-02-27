package com.revature.dao;

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
import com.revature.beans.User;
import com.revature.services.AccountService;
import com.revature.utils.SessionCache;
import com.revature.beans.Account.AccountType;

/**
 * Implementation of AccountDAO which reads/writes to files
 */
public class AccountDaoFile implements AccountDao {
	// use this file location to persist the data to
	public static String fileLocation = ".\\Users";

	public Account addAccount(Account a) {
		// TODO Auto-generated method stub
		List<Account> accounts;

		return null;
	}

	public Account getAccount(Integer actId) {
		// TODO Auto-generated method stub
		List<Account> accounts;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				User user = (User) ois.readObject();
				accounts = user.getAccounts();
				for(Account a : accounts) {
					if(actId == a.getId()) {
						return a;
					}
				}
				ois.close();
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public List<Account> getAccounts() {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				User user = (User) ois.readObject();
				accounts.addAll(user.getAccounts());
				ois.close();
				fis.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return accounts;
	}

	public List<Account> getAccountsByUser(User u) {
		// TODO Auto-generated method stub
		List<Account> accounts;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			int docID = Integer.parseInt(file.getName().split(".", 2)[0]);
			if(u.getId() == docID) {
				try {
					FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
					ObjectInputStream ois = new ObjectInputStream(fis);
					User user = (User) ois.readObject();
					accounts = user.getAccounts();
					ois.close();
					fis.close();
					return accounts;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Account updateAccount(Account a) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean removeAccount(Account a) {
		// TODO Auto-generated method stub
		return false;
	}

}
