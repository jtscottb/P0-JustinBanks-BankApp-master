package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.revature.beans.Account;
import com.revature.beans.Account.AccountType;
import com.revature.beans.Transaction;
import com.revature.beans.User;
import com.revature.beans.Transaction.TransactionType;
import com.revature.utils.ConnectionUtil;

/**
 * Implementation of AccountDAO which reads/writes to a database
 */
public class AccountDaoDB implements AccountDao {
	
	private static Connection conn;
	private static Statement stmt;
	private static PreparedStatement pstmt;
	private static ResultSet rs;
	
	public AccountDaoDB() {
		conn = ConnectionUtil.getConnection();
	}

	public Account addAccount(Account a) {
		// TODO Auto-generated method stub
		String query = "insert into accounts (accountid, userid, balance, type, approved) values (?, ?, ?, ?, ?)";
		Account account1 = new Account();
		Account account2 = new Account();
		account1.setType(AccountType.CHECKING);
		account2.setType(AccountType.SAVINGS);
		String type = null;
		
		if(a.getType() != null) {
			if(a.getType().equals(account1.getType())) {
				type = "CHECKING";
			} else if(a.getType().equals(account2.getType())) {
				type = "SAVINGS";
			}
		}
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, a.getId().intValue());
			pstmt.setInt(2, a.getOwnerId().intValue());
			pstmt.setDouble(3, a.getBalance().doubleValue());
			pstmt.setString(4, type);
			pstmt.setBoolean(5, a.isApproved());
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return a;
	}

	public Account getAccount(Integer actId) {
		// TODO Auto-generated method stub
		Account account = new Account();
		List<Account> accounts = getAccounts();
		for(Account a : accounts) {
			if(a.getId().equals(actId)) {
				account = a;
				break;
			}
		}
		return account;
	}

	public List<Account> getAccounts() {
		// TODO Auto-generated method stub
		List<Account> accounts = new ArrayList<Account>();
		String query = "select * from accounts";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				Account account = new Account();
				account.setId(rs.getInt("accountid"));
				account.setOwnerId(rs.getInt("userid"));
				account.setBalance(rs.getDouble("balance"));
				
				if(rs.getString("type").equals("CHECKING")) {
					account.setType(AccountType.CHECKING);
				} else if(rs.getString("type").equals("SAVINGS")) {
					account.setType(AccountType.SAVINGS);
				} else {
					account.setType(null);
				}
				
				account.setApproved((rs.getObject("approved").equals(1) ? true : false));
				accounts.add(account);
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
		return accounts;
	}

	public List<Account> getAccountsByUser(User u) {
		// TODO Auto-generated method stub
		List<Account> accounts = getAccounts();
		List<Account> myAccounts = new ArrayList<Account>();
		for(Account a : accounts) {
			if(a.getOwnerId().equals(u.getId())) {
				myAccounts.add(a);
			}
		}
		return myAccounts;
	}

	public Account updateAccount(Account a) {
		// TODO Auto-generated method stub
		String query = "update accounts set userid=?, balance=?, type=?, approved=? where accountid=" + a.getId().intValue();
		Account account1 = new Account();
		Account account2 = new Account();
		account1.setType(AccountType.CHECKING);
		account2.setType(AccountType.SAVINGS);
		String type = null;
		
		if(a.getType().equals(account1.getType())) {
			type = "CHECKING";
		} else if(a.getType().equals(account2.getType())) {
			type = "SAVINGS";
		}
		
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, a.getOwnerId().intValue());
			pstmt.setDouble(2, a.getBalance().doubleValue());
			pstmt.setString(3, type);
			pstmt.setBoolean(4, a.isApproved());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return a;
	}

	public boolean removeAccount(Account a) {
		// TODO Auto-generated method stub
		boolean status = false;
		String query = "delete from accounts where accountid =" + a.getId().intValue();
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
		}
		System.out.println((status) ? "Account removed" : "Account removal failed");
		return status;
	}

}
