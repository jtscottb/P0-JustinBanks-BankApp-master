package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.Transaction.TransactionType;
import com.revature.beans.User;
import com.revature.beans.Account.AccountType;
import com.revature.beans.User.UserType;
import com.revature.utils.ConnectionUtil;

public class TransactionDaoDB implements TransactionDao {
	
	private static Connection conn;
	private static Statement stmt;
	private static PreparedStatement pstmt;
	private static ResultSet rs;
	
	public TransactionDaoDB() {
		conn = ConnectionUtil.getConnection();
	}

	public List<Transaction> getAllTransactions() {
		// TODO Auto-generated method stub
		AccountDaoDB adb = new AccountDaoDB();
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		String query = "select * from transactions";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				Transaction action = new Transaction();
				action.setSender(adb.getAccount(rs.getInt("accountid")));
				action.setRecipient( Objects.isNull(adb.getAccount(rs.getInt("recipientid"))) ? null : adb.getAccount(rs.getInt("recipientid")) );
				action.setAmount(rs.getDouble("amount"));
				String type = rs.getString("type");
				switch(type) {
				case "DEPOSIT":
					action.setType(TransactionType.DEPOSIT);
				case "WITHDRAWAL":
					action.setType(TransactionType.WITHDRAWAL);
				case "TRANSFER":
					action.setType(TransactionType.TRANSFER);
				}
				action.setTimestamp((LocalDateTime) rs.getTimestamp("timestamp").toLocalDateTime());
				transactions.add(action);
			}
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transactions;
	}
	
	public Transaction addTransaction(Transaction t) {
		// TODO Auto-generated method stub
		String query = "insert into transactions (accountid, recipientid, amount, type, timestamp) values (?, ?, ?, ?, ?)";
		Transaction transaction1 = new Transaction();
		Transaction transaction2 = new Transaction();
		Transaction transaction3 = new Transaction();
		transaction1.setType(TransactionType.DEPOSIT);
		transaction2.setType(TransactionType.WITHDRAWAL);
		transaction3.setType(TransactionType.TRANSFER);
		
		String type = null;
		if(t.getType().equals(transaction1.getType())) {
			type = "DEPOSIT";
		} else if(t.getType().equals(transaction2.getType())) {
			type = "WITHDRAWAL";
		} else if(t.getType().equals(transaction3.getType())) {
			type = "TRANSFER";
		}
		
		int senderID = t.getSender().getId();
		int recipientID = Objects.isNull(t.getRecipient()) ? 0 : t.getRecipient().getId();
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, senderID);
			pstmt.setInt(2, recipientID);
			pstmt.setDouble(3, t.getAmount().doubleValue());
			pstmt.setString(4, type);
			pstmt.setObject(5, t.getTimestamp());
			pstmt.executeUpdate();
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return t;
	}
	
	public List<Transaction> getTransactionsByAccount(Account a) {
		List<Transaction> transactions= new ArrayList<Transaction>();
		List<Transaction> tList = getAllTransactions();
		for(Transaction t : tList) {
			if(t.getSender().getId().equals(a.getId())) {
				transactions.add(t);
			}
		}
		
		return transactions;
	}
	
	public boolean removeTransactionsByAccount(Account a) {
		String query = "delete from transactions where accountid=" + a.getId();
		boolean status = false;
		try {
			stmt = conn.createStatement();
			status = stmt.execute(query);
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return status;
	}
}
