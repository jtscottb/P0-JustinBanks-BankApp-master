package com.revature.dao;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import com.revature.beans.Account;
import com.revature.beans.User;
import com.revature.beans.User.UserType;

/**
 * Implementation of UserDAO that reads and writes to a file
 */
public class UserDaoFile implements UserDao {
	
	public static String fileLocation = "Users";

	public User addUser(User user) {
		// TODO Auto-generated method stub
		
		try {
			FileOutputStream fileOut = new FileOutputStream(fileLocation + "\\" + user.getId() + ".txt", true);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(user);
			objectOut.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("File could not be saved.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("File could not be saved");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
		return null;
	}

	public User getUser(Integer userId) {
		// TODO Auto-generated method stub
		try {
			File[] files = new File(fileLocation).listFiles();
			for(File file : files) {
				String doc = file.getName();
				if(doc == userId.toString()) {
					FileInputStream fileIn = new FileInputStream(new File(fileLocation + "\\" + doc));
					ObjectInputStream objectIn = new ObjectInputStream(fileIn);
					User user = (User) objectIn.readObject();
					
					System.out.println(user);
					objectIn.close();
					fileIn.close();
				} else {
					throw new FileNotFoundException("Could not find file");
				}
			}
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("User could not be located");
		} catch (ClassNotFoundException e) {
			System.out.println("User is not defined");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public User getUser(String username, String pass) {
		// TODO Auto-generated method stub
		try {
			File[] files = new File(fileLocation).listFiles();
			for(File file : files) {
				
				String doc = file.getName();
				FileInputStream fileIn = new FileInputStream(new File(fileLocation + "\\" + doc));
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				User user = (User) objectIn.readObject();
				
				if(username.matches(user.getUsername())) {
					if(pass.matches(user.getPassword())) {
						objectIn.close();
						fileIn.close();
						System.out.println("Log in successful");
						return user;
					} else {
						System.out.println("Incorrect password");
					}
				}
			}
		} catch(EOFException e) {
			System.out.println("End of file");
		} catch(FileNotFoundException e) {
			System.out.println("User is not found");
			e.printStackTrace();
		} catch(IOException e) {
			System.out.println("Could not return user");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("User not found. You must register first");
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		//open file in input stream
		//create list for users
		//while file has more content
		//	add user to list
		//return list
		return null;
	}

	public User updateUser(User u) {
		// TODO Auto-generated method stub
		//open file in input stream
		//while file has more content
		//	read object into user
		//	see if user == u
		//	if true write new user, if not writ old
		//return updated user
		return null;
	}

	public boolean removeUser(User u) {
		// TODO Auto-generated method stub
		//open file in input stream
		//while file has more content
		//	read object into user
		//	see if userid/username == u.userid
		//		if not write user to file
		//		if matches -- dont write
		//return true if found, false if not

		
		return false;
	}

}
