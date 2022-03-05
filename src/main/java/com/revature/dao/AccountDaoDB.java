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
		Account act = new Account();
		act.setType(AccountType.CHECKING);
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, a.getId().intValue());
			pstmt.setInt(2, a.getOwnerId().intValue());
			pstmt.setDouble(3, a.getBalance().doubleValue());
			pstmt.setObject(4, (a.getType().equals(act.getType())) ? 1 : 2);
			pstmt.setBoolean(5, a.isApproved());
			pstmt.executeUpdate();
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
		System.out.println("Account added");
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
		Account account = new Account();
		List<Account> accounts = new ArrayList<Account>();
		String query = "select * from accounts";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				account.setId(rs.getInt("accountid"));
				account.setOwnerId(rs.getInt("userid"));
				account.setBalance(rs.getDouble("balance"));
				account.setType((AccountType) rs.getObject("type"));
				account.setApproved((boolean) rs.getObject("approved"));
				accounts.add(account);
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
		return accounts;
	}

	public List<Account> getAccountsByUser(User u) {
		// TODO Auto-generated method stub
		List<Account> accounts = getAccounts();
		for(Account a : accounts) {
			if(a.getOwnerId().equals(u.getId())) {
				accounts.add(a);
			}
		}
		return accounts;
	}

	public Account updateAccount(Account a) {
		// TODO Auto-generated method stub
		String query = "update accounts set accountid=?, userid=?, balance=?, type=?, approved=?";
		Account account = new Account();
		account.setType(AccountType.CHECKING);
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, a.getId().intValue());
			pstmt.setInt(2, a.getOwnerId().intValue());
			pstmt.setDouble(3, a.getBalance().doubleValue());
			pstmt.setObject(4, (a.getType().equals(account.getType())) ? 1 : 2);
			pstmt.setBoolean(5, a.isApproved());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Account updated");
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
		if (conn != null)
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println((status) ? "Account removed" : "Account removal failed");
		return status;
	}

}
