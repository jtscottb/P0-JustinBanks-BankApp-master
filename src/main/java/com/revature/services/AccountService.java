package com.revature.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.User;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction.TransactionType;
import com.revature.dao.AccountDao;
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
			throw new UnsupportedOperationException();
		} else {
			Transaction action = new Transaction();
			action.setType(TransactionType.WITHDRAWAL);
			action.setSender(null);
			action.setRecipient(a);
			action.setAmount(amount);
			action.setTimestamp();
			a.setBalance(a.getBalance() - amount);
			a.getTransactions().add(action);
		}
	}
	
	/**
	 * Deposit funds to an account
	 * @throws UnsupportedOperationException if amount is negative
	 */
	public void deposit(Account a, Double amount) {
		
		if (!a.isApproved()) {
			throw new UnsupportedOperationException();
		} else {
			Transaction action = new Transaction();
			action.setType(TransactionType.DEPOSIT);
			action.setSender(null);
			action.setRecipient(a);
			action.setAmount(amount);
			action.setTimestamp();
			a.setBalance(a.getBalance() + amount);
			a.getTransactions().add(action);
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
			Transaction sender = new Transaction();
			sender.setType(TransactionType.TRANSFER);
			sender.setSender(fromAct);
			sender.setRecipient(toAct);
			sender.setAmount(-amount);
			sender.setTimestamp();
			fromAct.setBalance(fromAct.getBalance() - amount);
			fromAct.getTransactions().add(sender);
			
			Transaction receiver = new Transaction();
			receiver.setType(TransactionType.TRANSFER);
			receiver.setSender(fromAct);
			receiver.setRecipient(toAct);
			receiver.setAmount(amount);
			receiver.setTimestamp();
			toAct.setBalance(toAct.getBalance() + amount);
			toAct.getTransactions().add(receiver);
		}
	}
	
	/**
	 * Creates a new account for a given User
	 * @return the Account object that was created
	 */
	public Account createNewAccount(User u) {
		Account account = new Account();
		Scanner input = new Scanner(System.in);
		
		System.out.println("Enter Checking or Savings");
		String accountType = input.next().toUpperCase();
		switch(accountType) {
		case "CHECKING":
			account.setType(AccountType.CHECKING);
			break;
		case "SAVINGS":
			account.setType(AccountType.SAVINGS);
			break;
		default:
			System.out.println("Spelling Error!");
			break;
		}
		
		int accountID = 1;
		account.setId(accountID);
		
		account.setOwnerId(u.getId());
		account.setBalance(STARTING_BALANCE);
		
		System.out.println("Account approved (Yes/No): ");
		String approve = input.next();
		account.setApproved(approveOrRejectAccount(account, approve.matches(approve)));
		
		List<Transaction> myTransactions = new ArrayList<>();
		Transaction t = new Transaction();
		t.setAmount(STARTING_BALANCE);
		t.setTimestamp();
		t.setType(TransactionType.DEPOSIT);
		myTransactions.add(t);
		account.setTransactions(myTransactions);
		
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
