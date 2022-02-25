package com.revature.driver;

import java.io.*;
import java.util.*;

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
		
		if(registration.matches("YES")) {
			Register();
		}
		input.close();
	}
	
	public static void CreateNewLoginFile(String uname, String pword) {
		try {
			File newFile = new File(uname+".txt");
			if(newFile.createNewFile()) {
				FileWriter newWriter = new FileWriter(newFile);
				newWriter.write(pword);
				newWriter.close();
			} else {
				System.out.println("User already exists!");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static User Register() {
		Scanner info = new Scanner(System.in);
		
		User customer = new User();
		
		//Increasing id of user as a user is registered.
		SortedSet<Integer> idList = new TreeSet<Integer>();
		idList.add(0);
		int max = Collections.max(idList);
		idList.add(max+1);
		customer.setId(max+1);
		
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
		switch(userType) {
			case "CUSTOMER":
				customer.setUserType(UserType.CUSTOMER);
				break;
			case "EMPLOYEE":
				customer.setUserType(UserType.EMPLOYEE);
				break;
			default:
				System.out.println("Spelling error! Enter Customer or Employee");
				Register();
				break;
		}
		
		List<Account> myAccounts = new ArrayList<>();
		myAccounts.add(NewAccount());
		customer.setAccounts(myAccounts);
		
		CreateNewLoginFile(username, password);
		System.out.println(customer);
		
		info.close();
		return customer;
	}
	
	public static Account NewAccount() {
		Scanner in = new Scanner(System.in);
		Account account = new Account();
		
		System.out.println("Enter Checking or Savings");
		String accountType = in.next().toUpperCase();
		switch(accountType) {
			case "CHECKING":
				account.setType(AccountType.CHECKING);
				break;
			case "SAVINGS":
				account.setType(AccountType.SAVINGS);
				break;
			default:
				System.out.println("Spelling Error!");
				NewAccount();
				break;
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
		if(approval.matches("YES")) {
			account.setApproved(true);
		} else {
			account.setApproved(false);
		}
		
//		List<Transaction> myTransactions = new ArrayList<>();
//		myTransactions.add(Transactions());
		account.setTransactions(null);
		
		in.close();
		return account;
	}
	
	public static Transaction NewTransaction() {
		Scanner input = new Scanner(System.in);
		Transaction action = new Transaction();
		
		System.out.println("Deposit, Withdrawal, or Transfer?");
		String transactionType = input.next().toUpperCase();
		switch(transactionType) {
			case "DEPOSIT":
				action.setType(TransactionType.DEPOSIT);
				break;
			case "WITHDRAWAL":
				action.setType(TransactionType.WITHDRAWAL);
				break;
			case "TRANSFER":
				action.setType(TransactionType.TRANSFER);
				break;
			default:
				System.out.println("Spelling Error!");
				NewTransaction();
				break;
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
