package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.revature.beans.User;
import com.revature.beans.User.UserType;
import com.revature.utils.ConnectionUtil;

/**
 * Implementation of UserDAO that reads/writes to a relational database
 */
public class UserDaoDB implements UserDao {
	
	private static Connection conn;
	private static Statement stmt;
	private static PreparedStatement pstmt;
	private static ResultSet rs;
	
	public UserDaoDB() {
		conn = ConnectionUtil.getConnection();
	}
	
	public User addUser(User user) {
		// TODO Auto-generated method stub
		String query = "insert into users (userid, username, password, firstname, lastname, usertype) values (?, ?, ?, ?, ?, ?)";
		User u = new User();
		u.setUserType(UserType.CUSTOMER);
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, user.getId().intValue());
			pstmt.setString(2, user.getUsername());
			pstmt.setString(3, user.getPassword());
			pstmt.setString(4, user.getFirstName());
			pstmt.setString(5, user.getLastName());
			pstmt.setString(6, (user.getUserType().equals(u.getUserType())) ? "CUSTOMER" : "EMPLOYEE");
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
		return user;
	}

	public User getUser(Integer userId) {
		// TODO Auto-generated method stub
		User user = new User();
		List<User> users = getAllUsers();
		for(User u : users) {
			if(u.getId().equals(userId)) {
				user = u;
				break;
			}
		}
		return user;
	}

	public User getUser(String username, String pass) {
		// TODO Auto-generated method stub
		User user = new User();
		List<User> users = getAllUsers();
		for(User u : users) {
			if(u.getUsername().equals(username)) {
				if(u.getPassword().equals(pass)) {
					user = u;
					break;
				}
			}
		}
		return user;
	}

	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		List<User> users = new ArrayList<User>();
		String query = "select * from users";
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				User user = new User();
				user.setId(rs.getInt("userid"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setFirstName(rs.getString("firstname"));
				user.setLastName(rs.getString("lastname"));
				user.setUserType(rs.getString("usertype").equals("CUSTOMER") ? UserType.CUSTOMER : UserType.EMPLOYEE);
				users.add(user);
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
		return users;
	}

	public User updateUser(User u) {
		// TODO Auto-generated method stub
		String query = "update users set username=?, password=?, firstname=?, lastname=?, usertype=? where userid =" + u.getId().intValue();
		User user = new User();
		user.setUserType(UserType.CUSTOMER);
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, u.getUsername());
			pstmt.setString(2, u.getPassword());
			pstmt.setString(3, u.getFirstName());
			pstmt.setString(4, u.getLastName());
			pstmt.setString(5, (u.getUserType().equals(user.getUserType()) ? "CUSTOMER" : "EMPLOYEE"));
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
		System.out.println("User updated");
		return u;
	}

	public boolean removeUser(User u) {
		// TODO Auto-generated method stub
		boolean status = false;
		String query = "delete from users where userid = " + u.getId().intValue();
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
		System.out.println((status) ? "User removed" : "User removal failed");
		return status;
	}

}
