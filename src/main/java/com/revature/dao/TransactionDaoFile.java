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
import java.util.Comparator;
import java.util.List;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.User;

public class TransactionDaoFile implements TransactionDao {
	
	public static String fileLocation = ".\\Transactions";

	public List<Transaction> getAllTransactions() {
		// TODO Auto-generated method stub
		List<Transaction> transactions = new ArrayList<Transaction>();
		File[] files = new File(fileLocation).listFiles();
		Transaction t = new Transaction();
		
		for(File f : files) {
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "/" + f.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				t = (Transaction) ois.readObject();
				transactions.add(t);
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
		
		transactions.sort((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()));
		return transactions;
	}
	
	public Transaction addTransaction(Transaction t) {
		File[] files = new File(fileLocation).listFiles();
		List<Integer> docNums = new ArrayList<Integer>();
		int num = 0;
		
//		FIND NEXT DOC NUMBER
		for(File f : files) {
			int doc = Integer.parseInt(f.getName().split("\\.", 2)[0]);
			docNums.add(doc);
		}
		if(docNums.isEmpty()) {
			num = 1;
		} else {
			num = Collections.max(docNums) + 1;
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(fileLocation + "\\" + num + ".txt");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(t);
			oos.reset();
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return t;
	}
	
	public List<Transaction> getTransactionsByAccount(Account a) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		for(Transaction trans : getAllTransactions()) {
			if(trans.getSender().getId().equals(a.getId())) {
				transactions.add(trans);
			}
		}
		return transactions;
	}
	
	public boolean removeTransaction(Transaction t) {
		Transaction transaction = new Transaction();
		boolean removed = false;
		File[] files = new File(fileLocation).listFiles();
		
		for(File f : files) {
			try {
				FileInputStream fis = new FileInputStream(fileLocation + "/" + f.getName());
				ObjectInputStream ois = new ObjectInputStream(fis);
				transaction = (Transaction) ois.readObject();
				
				if(t.getTimestamp().equals(transaction.getTimestamp())) {
					removed = f.delete();
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
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return removed;
	}

}
