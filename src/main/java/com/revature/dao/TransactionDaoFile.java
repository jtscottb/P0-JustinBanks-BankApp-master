package com.revature.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.User;

public class TransactionDaoFile implements TransactionDao {
	
	public static String fileLocation = ".\\Users";

	public List<Transaction> getAllTransactions() {
		// TODO Auto-generated method stub
		List<Transaction> transactions = new ArrayList<Transaction>();
		List<Account> accounts = new ArrayList<Account>();
		AccountDaoFile adf = new AccountDaoFile();
		accounts = adf.getAccounts();
		for(Account a : accounts) {
			if(a.getTransactions().equals(null)) {
				accounts.remove(a);
			}
		}
		accounts.forEach((a) -> transactions.addAll(a.getTransactions()));
		
		transactions.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
		return transactions;
	}

}
