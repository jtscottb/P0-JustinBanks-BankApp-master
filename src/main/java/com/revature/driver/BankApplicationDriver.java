package com.revature.driver;

import java.util.Scanner;

import com.revature.beans.User;
import com.revature.beans.User.UserType;

/**
 * This is the entry point to the application
 */
public class BankApplicationDriver {

	public static void main(String[] args) {
		// your code here...
		Scanner input = new Scanner(System.in);
		System.out.println("Would you like to register?: ");
		String registration = input.next().toUpperCase();
		
		if(registration == "YES") {Register();}
		
		input.close();
	}
	
	public static void Register() {
		Scanner info = new Scanner(System.in);
		System.out.println("Enter Customer or Employee: ");
		String userType = info.next().toUpperCase();
		
		if(userType == "CUSTOMER") {
			User customer = new User();
			customer.setId(001);
			System.out.println("Enter username: ");
			String username = info.next();
			customer.setUsername(username);	
			
			System.out.println("Enter password: ");
			String password = info.next();
			customer.setPassword(password);
			
			System.out.println("Enter First Name: ");
			String fname = info.next();
			customer.setFirstName(fname);
			
			System.out.println("Enter Last Name: ");
			String lname = info.next();
			customer.setLastName(lname);
			customer.setUserType(UserType.CUSTOMER);
			
			customer.setAccounts(null);
			
			System.out.println(customer);
		}
		info.close();
	}

}
