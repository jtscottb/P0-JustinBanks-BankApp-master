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
//			SETUP
			List<Transaction> transactions = new ArrayList<Transaction>();
			Transaction action = new Transaction();
			Account account = new Account();
			AccountDaoFile adf = new AccountDaoFile();
//			COPY ACCOUNT FROM USER FILE
			account = adf.getAccount(a.getId());
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
//			COPY ACCOUNT
			account = adf.getAccount(a.getId());
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
//			SET NEW TRANSACTION TO TRANSACTION LIST IN ACCOUNT AND UPDATE USER FILE
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
//			COPY ACCOUNT
			a = adf.getAccount(fromAct.getId());
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
			transactions.add(t);
//			UPDATE ACCOUNT WITH NEW TRANSACTIONS LIST AND UPDATE USER FILE
			fromAct.setTransactions(transactions);
			adf.updateAccount(a);
			
//			RECEIVER TRANSACTION.
			List<Transaction> receiverTransactions = new ArrayList<Transaction>();
			Transaction tr = new Transaction();
			Account ar = new Account();
			AccountDaoFile adfr = new AccountDaoFile();
//			COPY ACCOUNT
			ar = adfr.getAccount(toAct.getId());
//			SETUP NEW TRANSACTION DETAILS
			tr.setType(TransactionType.TRANSFER);
			tr.setSender(fromAct);
			tr.setRecipient(ar);
			tr.setAmount(amount);
			tr.setTimestamp();
//			UPDATE ACCOUNT BALANCE
			toAct.setBalance(toAct.getBalance() + amount);
//			COPY TRANSACTIONS FROM ACCOUNT AND ADD NEW TRANSACTION
			receiverTransactions = toAct.getTransactions();
			receiverTransactions.add(tr);
//			UPDATE ACCOUNT WITH NEW TRANSACTIONS LIST AND UPDATE USER FILE
			toAct.setTransactions(receiverTransactions);
			adfr.updateAccount(fromAct);
		}
	}
	
	/**
	 * Creates a new account for a given User
	 * @return the Account object that was created
	 */
	public Account createNewAccount(User u) {
		Account account = new Account();
		Scanner input = new Scanner(System.in);
//		CHOOSE ACCOUNT TYPE
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
//		FIND NEXT AVAILABLE ACCOUNT NUMBER
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
//		SET NEXT ACCOUNT NUMBER TO ID
		account.setId(accountID + 1);
//		LINK ACCOUNT WITH USER ID
		account.setOwnerId(u.getId());
		account.setBalance(STARTING_BALANCE);
//		SET APPROVAL
		System.out.println("Account approved? \n(1.) Yes \n(2.) No)");
		int approve = input.nextInt();
		boolean b = (approve == 1) ? true : false;
		account.setApproved(b);
//		GENERATE TRANSACTION DETAIL FOR STARTING BALANCE
		List<Transaction> myTransactions = new ArrayList<>();
		Transaction t = new Transaction();
		t.setAmount(STARTING_BALANCE);
		t.setTimestamp();
		t.setType(TransactionType.DEPOSIT);
		myTransactions.add(t);
//		SET TRANSACTION TO ACCOUNT
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
		AccountDaoFile adf = new AccountDaoFile();
		a.setApproved(approval);
		adf.updateAccount(a);
		System.out.println( approval ? "Account approved" : "Account rejected");
		return approval;
	}
}
