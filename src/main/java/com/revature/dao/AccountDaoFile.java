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
	public static String fileLocation = ".\\Users";

	public Account addAccount(Account a) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		User user = new User();
		File[] files = new File(fileLocation).listFiles();
		for(File file : files) {
			
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				user = (User) ois.readObject();
				ois.close();
				fis.close();

				if(a.getOwnerId().equals(user.getId())) {
					if(user.getAccounts() == null) {
						accounts.add(a);
						user.setAccounts(accounts);
					} else {
						accounts = user.getAccounts();
						accounts.add(a);
						user.setAccounts(accounts);
					}
					FileOutputStream fos = new FileOutputStream(fileLocation + "\\" + file.getName());
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(user);
					oos.close();
					fos.close();
					System.out.println("Account added");
					break;
				}
	
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
		return a;
	}

	public Account getAccount(Integer actId) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		User user = new User();
		Account account = new Account();
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				user = (User) ois.readObject();
				
				accounts = user.getAccounts();
				for(Account a : accounts) {
					if(a.getId().equals(actId)) {
						account = a;
						break;
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
		return account;
	}

	public List<Account> getAccounts() {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		List<User> users = new ArrayList<User>();
		UserDaoFile udf = new UserDaoFile();
		users = udf.getAllUsers();
		for(User u : users) {
			if(u.getAccounts() == null) {
				users.remove(u);
			}
		}
		users.forEach((u) -> accounts.addAll(u.getAccounts()));
		return accounts;
	}

	public List<Account> getAccountsByUser(User u) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			
			int docID = Integer.parseInt(file.getName().split("\\.", 2)[0]);
			if(u.getId() == docID) {
				
				try {
					FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
					ObjectInputStream ois = new ObjectInputStream(fis);
					User user = (User) ois.readObject();
					accounts = user.getAccounts();
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
				
				break;
			}
			
		}
		return accounts;
	}

	public Account updateAccount(Account a) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		User user = new User();
		int pos = 0;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				user = (User) ois.readObject();
				
				if(a.getOwnerId().equals(user.getId())) {
					accounts.addAll(user.getAccounts());
					for(Account i : accounts) {
						
						if(i.getId().equals(a.getId())) {
							pos = accounts.indexOf(i);
							accounts.remove(pos);
							System.out.println(accounts.add(a));
							System.out.println(accounts);
							
							user.setAccounts(accounts);
							System.out.println(user);
							
							UserDaoFile udf = new UserDaoFile();
							udf.updateUser(user);
							System.out.println("Account updated");
							break;
						}
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
		return a;
	}

	public boolean removeAccount(Account a) {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		User user = new User();
		boolean removed = false;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				user = (User) ois.readObject();
				
				if(a.getOwnerId() == user.getId()) {
					accounts = user.getAccounts();
					removed = accounts.remove(a);
					user.setAccounts(accounts);
					UserDaoFile udf = new UserDaoFile();
					udf.updateUser(user);
					System.out.println("Account removed");
					break;
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
		return removed;
	}

}
