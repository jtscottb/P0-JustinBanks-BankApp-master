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
import com.revature.dao.AccountDaoDB;
import com.revature.dao.AccountDaoFile;
import com.revature.driver.BankApplicationDriver;
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
//			SETUP
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction action = new Transaction();
			Account account = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			AccountDaoDB adb = new AccountDaoDB();
			
//			COPY ACCOUNT FROM USER FILE
			account = adf.getAccount(a.getId());
//			account = adb.getAccount(a.getId());
			
//			CREATE TRANSACTION DETAILS
			action.setType(TransactionType.WITHDRAWAL);
			action.setSender(account);
			action.setRecipient(null);
			action.setAmount(amount);
			action.setTimestamp();
			
//			ADJUST ACCOUNT BALANCE
			a.setBalance(a.getBalance() - amount);
			
//			COPY EXISTING TRANSACTIONS
			transactions = a.getTransactions();
			
//			ADD NEW TRANSACTION AND SET NEW TRANSACTION LIST TO ACCOUNT
			transactions.add(action);
			a.setTransactions(transactions);
			
//			UPDATE USER FILE WITH UPDATED ACCOUNT
			adf.updateAccount(a);
			adb.updateAccount(a);
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
//			SETUP
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction action = new Transaction();
			Account account = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			AccountDaoDB adb = new AccountDaoDB();
			
//			COPY ACCOUNT
			account = adf.getAccount(a.getId());
//			account = adb.getAccount(a.getId());
			
//			CREATE TRANSACTION DETAILS
			action.setType(TransactionType.DEPOSIT);
			action.setSender(account);
			action.setRecipient(null);
			action.setAmount(amount);
			action.setTimestamp();
			
//			UPDATE ACCOUNT BALANCE
			a.setBalance(a.getBalance() + amount);
			
//			COPY EXISTING TRANSACTIONS AND ADD NEW TRANSACTION TO LIST
			transactions = a.getTransactions();
			transactions.add(action);
			
//			SET NEW TRANSACTION TO TRANSACTION LIST IN ACCOUNT
			a.setTransactions(transactions);
			
//			UPDATE USER FILE WITH UPDATED ACCOUNT
			adf.updateAccount(a);
			adb.updateAccount(a);
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
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction t = new Transaction();
			Account a = new Account();
			AccountDaoFile adf = new AccountDaoFile();
			AccountDaoDB adb = new AccountDaoDB();
			
//			SENDER TRANSACTION
//			COPY ACCOUNT
			a = adf.getAccount(fromAct.getId());
//			a = adb.getAccount(fromAct.getId());
			
//			SETUP NEW TRANSACTION DETAILS
			t.setType(TransactionType.TRANSFER);
			t.setSender(a);
			t.setRecipient(toAct);
			t.setAmount(amount);
			t.setTimestamp();
			
//			UPDATE ACCOUNT BALANCE
			fromAct.setBalance(fromAct.getBalance() - amount);
			
//			COPY TRANSACTIONS FROM ACCOUNT AND ADD NEW TRANSACTION
			transactions = fromAct.getTransactions();
			
//			UPDATE ACCOUNT WITH NEW TRANSACTIONS LIST AND UPDATE USER FILE
			fromAct.setTransactions(transactions);
			adf.updateAccount(fromAct);
			adb.updateAccount(fromAct);
			
//			RECEIVER TRANSACTION
//			COPY ACCOUNT
			a = adf.getAccount(fromAct.getId());
//			a = adb.getAccount(toAct.getId());
			
//			SETUP NEW TRANSACTION DETAILS
			t.setType(TransactionType.TRANSFER);
			t.setSender(fromAct);
			t.setRecipient(a);
			t.setAmount(amount);
			t.setTimestamp();
//			UPDATE ACCOUNT BALANCE
			toAct.setBalance(toAct.getBalance() + amount);
			
//			COPY TRANSACTIONS FROM ACCOUNT AND ADD NEW TRANSACTION
			transactions = toAct.getTransactions();
			
//			UPDATE ACCOUNT WITH NEW TRANSACTIONS LIST AND UPDATE USER FILE
			toAct.setTransactions(transactions);
			adf.updateAccount(toAct);
			adb.updateAccount(toAct);
		}
	}
	
	/**
	 * Creates a new account for a given User
	 * @return the Account object that was created
	 */
	public Account createNewAccount(User u) {
		List<Transaction> myTransactions = new ArrayList<>();
		List<Account> accounts = new ArrayList<Account>();
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		Transaction t = new Transaction();
		Account account = new Account();
		
		account.setBalance(STARTING_BALANCE);
		account.setApproved(false);
		account.setOwnerId(u.getId());
		
//		FIND NEXT AVAILABLE ACCOUNT NUMBER
		int accountID = 0;
		accounts = adf.getAccounts();
		for(Account a : accounts) {
			if(accounts.isEmpty()) {
				accountID = 1;
			} else {
				accountID = (a.getId() > accountID) ? a.getId() : accountID;
			}
		}
		
//		SET NEXT ACCOUNT NUMBER TO ID
		account.setId(accountID + 1);
		
//		GENERATE TRANSACTION DETAIL FOR STARTING BALANCE
		t.setAmount(STARTING_BALANCE);
		t.setTimestamp();
		t.setType(TransactionType.DEPOSIT);
		myTransactions.add(t);
		
//		SET TRANSACTION TO ACCOUNT
		account.setTransactions(new ArrayList<Transaction>());
		adf.addAccount(account);
		adb.addAccount(account);
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
		AccountDaoFile adf = new AccountDaoFile();
		AccountDaoDB adb = new AccountDaoDB();
		a.setApproved(approval);
		adf.updateAccount(a);
		adb.updateAccount(a);
		System.out.println( approval ? "Account approved" : "Account rejected");
		return approval;
	}
}
