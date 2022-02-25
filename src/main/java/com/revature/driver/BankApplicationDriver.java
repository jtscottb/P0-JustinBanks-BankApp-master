package com.revature.driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.revature.beans.Account;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction;
import com.revature.beans.Transaction.TransactionType;
import com.revature.beans.User;
import com.revature.beans.User.UserType;

/**
 * This is the entry point to the application
 */
public class BankApplicationDriver {

	public static void main(String[] args) {
		// your code here...
		Scanner input = new Scanner(System.in);
		System.out.println("Would you like to register? (Yes/No): ");
		String registration = input.next().toUpperCase();
		
		if(registration == "YES") {Register();}
		
		Register();
		input.close();
	}
	
	public static void Register() {
		Scanner info = new Scanner(System.in);
		
		User customer = new User();
		
		int id = 001;
		customer.setId(id);
		
		System.out.println("Enter username: ");
		String username = info.next();
		customer.setUsername(username);	
		
		System.out.println("Enter password: ");
		String password = info.next();
		customer.setPassword(password);
		
		System.out.println("Enter First Name: ");
		String fname = info.next().toUpperCase();
		customer.setFirstName(fname);
		
		System.out.println("Enter Last Name: ");
		String lname = info.next().toUpperCase();
		customer.setLastName(lname);
		
		System.out.println("Enter Customer or Employee: ");
		String userType = info.next().toUpperCase();
		if(userType == "CUSTOMER") {
			customer.setUserType(UserType.CUSTOMER);
		} else {
			customer.setUserType(UserType.EMPLOYEE);
		}
		
		List<Account> myAccounts = new ArrayList<Account>();
		myAccounts.add(NewAccount());
		customer.setAccounts(myAccounts);
		
		System.out.println(customer);
		
		info.close();
	}
	
	public static Account NewAccount() {
		Scanner in = new Scanner(System.in);
		Account account = new Account();
		
		System.out.println("Enter Checking or Savings");
		String type = in.next().toUpperCase();
		if(type == "CHECKING") {
			account.setType(AccountType.CHECKING);
		} else {
			account.setType(AccountType.SAVINGS);
		}
		
		int accountID = 005;
		account.setId(accountID);
		
		System.out.println("Enter Owner ID: ");
		int ownerID = in.nextInt();
		account.setOwnerId(ownerID);
		
		System.out.println("Balance amount: ");
		double amount = in.nextDouble();
		account.setBalance(amount);
		
		System.out.println("Approved (Yes/No): ");
		String approval = in.next().toUpperCase();
		if(approval == "YES") {
			account.setApproved(true);
		} else {
			account.setApproved(false);
		}
		
//		List<Transaction> myTransactions = new ArrayList<Transaction>();
//		myTransactions.add(Transactions());
		account.setTransactions(null);
		
		in.close();
		return account;
	}
	
	public static Transaction NewTransaction() {
		Scanner input = new Scanner(System.in);
		Transaction action = new Transaction();
		
		System.out.println("Deposit, Withdrawal, or Transfer?");
		String type = input.next().toUpperCase();
		if(type == "DEPOSIT") {
			action.setType(TransactionType.DEPOSIT);
		} else if(type == "WITHDRAWAL") {
			action.setType(TransactionType.WITHDRAWAL);
		} else {
			action.setType(TransactionType.TRANSFER);
		}
		
		System.out.println("Enter account number: ");
		Account from = input.nextInt();
		action.setSender(from);
		
		if(action.getType() == TransactionType.TRANSFER) {
			System.out.println("Account number to transfer to: ");
			Account to = input.nextInt();
			action.setRecipient(to);
		} else {
			action.setRecipient(null);
		}
		
		System.out.println("Amount");
		double amount = input.nextDouble();
		action.setAmount(amount);
		
		action.setTimestamp();
		
		input.close();
		return action;
	}

}
