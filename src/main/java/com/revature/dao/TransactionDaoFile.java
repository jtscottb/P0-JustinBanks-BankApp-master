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
		List<Transaction> tList = new ArrayList<Transaction>();
		List<Account> aList = new ArrayList<Account>();
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				User user = (User) ois.readObject();
				aList.addAll(user.getAccounts());
				
				for(Account i : aList) {
					tList.addAll(i.getTransactions());
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
		tList.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
		return tList;
	}

}
