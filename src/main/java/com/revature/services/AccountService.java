package com.revature.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.User;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction.TransactionType;
import com.revature.dao.AccountDao;
import com.revature.dao.AccountDaoFile;
import com.revature.exceptions.OverdraftException;
import com.revature.utils.SessionCache;

/**
 * This class should contain the business logic for performing operations on Accounts
 */
public class AccountService {
	
	public AccountDao actDao;
	public static final double STARTING_BALANCE = 25d;
	
	public AccountService(AccountDao dao) {
		this.actDao = dao;
	}
	
	/**
	 * Withdraws funds from the specified account
	 * @throws OverdraftException if amount is greater than the account balance
	 * @throws UnsupportedOperationException if amount is negative
	 */
	public void withdraw(Account a, Double amount) {
		
		if(a.getBalance() < amount) {
			throw new OverdraftException();
		} else if(amount < 0) {
			throw new UnsupportedOperationException("Cannot withdraw a negative amount");
		} else if(!a.isApproved()) {
			throw new UnsupportedOperationException("Account not approved");
		} else {
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction action = new Transaction();
			Account account = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			account = adf.getAccount(a.getId());
			action.setType(TransactionType.WITHDRAWAL);
			action.setSender(account);
			action.setRecipient(null);
			action.setAmount(amount);
			action.setTimestamp();
			a.setBalance(a.getBalance() - amount);
			transactions = a.getTransactions();
			transactions.add(action);
			a.setTransactions(transactions);
			adf.updateAccount(a);
		}
	}
	
	/**
	 * Deposit funds to an account
	 * @throws UnsupportedOperationException if amount is negative
	 */
	public void deposit(Account a, Double amount) {
		if (amount < 0) {
			throw new UnsupportedOperationException();
		} else if(!a.isApproved()) {
			throw new UnsupportedOperationException("Account not approved");
		} else {
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction action = new Transaction();
			Account account = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			account = adf.getAccount(a.getId());
			action.setType(TransactionType.DEPOSIT);
			action.setSender(account);
			action.setRecipient(null);
			action.setAmount(amount);
			action.setTimestamp();
			a.setBalance(a.getBalance() + amount);
			transactions = a.getTransactions();
			transactions.add(action);
			a.setTransactions(transactions);
			adf.updateAccount(a);
		}
	}
	
	/**
	 * Transfers funds between accounts
	 * @throws UnsupportedOperationException if amount is negative or 
	 * the transaction would result in a negative balance for either account
	 * or if either account is not approved
	 * @param fromAct the account to withdraw from
	 * @param toAct the account to deposit to
	 * @param amount the monetary value to transfer
	 */
	public void transfer(Account fromAct, Account toAct, double amount) {
		if(amount < 0) {
			throw new UnsupportedOperationException("Cannot transfer negative funds");
		} else if(amount > fromAct.getBalance()) {
			throw new UnsupportedOperationException("Insufficient funds to transfer");
		} else if(!fromAct.isApproved()) {
			throw new UnsupportedOperationException("Sending account is not approved");
		} else if(!toAct.isApproved()) {
			throw new UnsupportedOperationException("Recipient account is not approved");
		} else {
//			SENDER TRANSACTION
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction t = new Transaction();
			Account a = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			a = adf.getAccount(fromAct.getId());
			t.setType(TransactionType.TRANSFER);
			t.setSender(a);
			t.setRecipient(toAct);
			t.setAmount(amount);
			t.setTimestamp();
			fromAct.setBalance(fromAct.getBalance() - amount);
			transactions = fromAct.getTransactions();
			transactions.add(t);
			fromAct.setTransactions(transactions);
			adf.updateAccount(a);
			
//			RECEIVER TRANSACTION.
			a = adf.getAccount(toAct.getId());
			t.setType(TransactionType.TRANSFER);
			t.setSender(fromAct);
			t.setRecipient(a);
			t.setAmount(amount);
			t.setTimestamp();
			toAct.setBalance(toAct.getBalance() + amount);
			transactions = toAct.getTransactions();
			transactions.add(t);
			toAct.setTransactions(transactions);
			adf.updateAccount(fromAct);
		}
	}
	
	/**
	 * Creates a new account for a given User
	 * @return the Account object that was created
	 */
	public Account createNewAccount(User u) {
		Account account = new Account();
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter \n(1.) Checking or \n(2.) Savings");
		int accountType = input.nextInt();
		switch(accountType) {
		case 1:
			account.setType(AccountType.CHECKING);
			break;
		case 2:
			account.setType(AccountType.SAVINGS);
			break;
		default:
			System.out.println("Enter 1 for Checking or 2 for Savings");
			input.close();
		}
		
		//code to find the next available account number
		int accountID = 0;
		User user = new User();
		File[] files = new File("Users").listFiles();
		List<Account> act = new ArrayList<Account>();
		
		for(File file : files) {
			try {
				FileInputStream fileIn = new FileInputStream(new File("Users\\" + file.getName()));
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				user = (User) objectIn.readObject();
				
				if(user.getAccounts() != null) {
					act.addAll(user.getAccounts());
				}
				for(Account a : act) {
					if(accountID < a.getId()) {
						accountID = a.getId();
					}
				}
				objectIn.close();
				fileIn.close();
			} catch (FileNotFoundException e) {
				// TODO: handle exception
				System.out.println("File not found");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("User could not be located");
			} catch (ClassNotFoundException e) {
				System.out.println("User is not defined");
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		account.setId(accountID + 1);
		//end account number code
		
		account.setOwnerId(u.getId());
		account.setBalance(STARTING_BALANCE);
		
		System.out.println("Account approved? \n(1.) Yes \n(2.) No)");
		int approve = input.nextInt();
		boolean b = (approve == 1) ? true : false;
		account.setApproved(b);
		
		List<Transaction> myTransactions = new ArrayList<>();
		Transaction t = new Transaction();
		t.setAmount(STARTING_BALANCE);
		t.setTimestamp();
		t.setType(TransactionType.DEPOSIT);
		myTransactions.add(t);
		
		account.setTransactions(myTransactions);
		System.out.println("Account created");
		
		return account;
	}
	
	/**
	 * Approve or reject an account.
	 * @param a
	 * @param approval
	 * @throws UnauthorizedException if logged in user is not an Employee
	 * @return true if account is approved, or false if unapproved
	 */
	public boolean approveOrRejectAccount(Account a, boolean approval) {
		
		return false;
	}
}
