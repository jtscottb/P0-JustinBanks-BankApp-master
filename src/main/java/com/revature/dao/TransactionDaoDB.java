package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.revature.beans.Account;
import com.revature.beans.Transaction;
import com.revature.beans.Transaction.TransactionType;
import com.revature.beans.User;
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
		Transaction action = new Transaction();
		List<Transaction> transactions = new ArrayList<Transaction>();
		String query = "select * from transactions";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				action.setSender((Account) rs.getObject("accountid"));
				action.setRecipient((Account) rs.getObject("recipientid"));
				action.setAmount(rs.getDouble("amount"));
				action.setType((TransactionType) rs.getObject("type"));
				action.setTimestamp((LocalDateTime) rs.getObject("timestamp"));
				transactions.add(action);
			}
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return transactions;
	}

}
