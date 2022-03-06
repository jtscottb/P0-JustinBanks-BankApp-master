package com.revature.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import com.revature.beans.Transaction;

/**
 * Implementation of AccountDAO which reads/writes to files
 */
public class AccountDaoFile implements AccountDao {
	// use this file location to persist the data to
	public static String fileLocation = ".\\Accounts";

	public Account addAccount(Account a) {
		// TODO Auto-generated method stub
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation + "\\" + a.getId() + ".txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(a);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	public Account getAccount(Integer actId) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		Account account = new Account();
		accounts = getAccounts();
		for(Account a : accounts) {
			if(a.getId().equals(actId)) {
				account = a;
				break;
			}
		}
		return account;
	}

	public List<Account> getAccounts() {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		Account account = new Account();
		File[] files = new File(fileLocation).listFiles();
		if(files.length > 0) {
			for(File file : files) {
				try {
					FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
					ObjectInputStream ois = new ObjectInputStream(fis);
					account = (Account) ois.readObject();
					accounts.add(account);
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
		}
		return accounts;
	}

	public List<Account> getAccountsByUser(User u) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		List<Account> myAccounts = new ArrayList<Account>();
		accounts = getAccounts();
		if(!accounts.isEmpty()) {
			for(Account a : accounts) {
				if(u.getId().equals(a.getOwnerId())) {
					myAccounts.add(a);
				}
			}
		}
		return myAccounts;
	}

	public Account updateAccount(Account a) {
		// TODO Auto-generated method stub
		int docID = 0;
		File[] files = new File(fileLocation).listFiles();
		
		if(files.length > 0) {
			for(File file : files) {
				docID = Integer.parseInt(file.getName().split("\\.", 2)[0]);
				if(a.getId().equals(docID)) {
					try {
						FileOutputStream fos = new FileOutputStream(fileLocation + "\\" + file.getName());
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(a);
						oos.close();
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return a;
	}

	public boolean removeAccount(Account a) {
		// TODO Auto-generated method stub
		boolean removed = false;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			int docID = Integer.parseInt(file.getName().split("\\.", 2)[0]);
			
			if(docID == a.getId()) {
				removed = file.delete();
				break;
			}
		}
		return removed;
	}

}
