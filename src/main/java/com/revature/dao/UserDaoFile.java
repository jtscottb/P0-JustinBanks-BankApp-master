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
import com.revature.exceptions.InvalidCredentialsException;

/**
 * Implementation of UserDAO that reads and writes to a file
 */
public class UserDaoFile implements UserDao {
	
	public static String fileLocation = "Users";

	public User addUser(User user) {
		// TODO Auto-generated method stub
		
		try {
			FileOutputStream fileOut = new FileOutputStream(fileLocation + "\\" + user.getId() + ".txt");
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
		return user;
	}

	public User getUser(Integer userId) {
		// TODO Auto-generated method stub
		User user = null;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			String doc = file.getName().split("\\.", 2)[0];
			if(doc.matches(userId.toString())) {
				try {
					FileInputStream fileIn = new FileInputStream(fileLocation + "\\" + doc);
					ObjectInputStream objectIn = new ObjectInputStream(fileIn);
					user = (User) objectIn.readObject();
					objectIn.close();
					fileIn.close();
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
				break;
			}
		}
		return user;
	}

	public User getUser(String username, String pass) {
		// TODO Auto-generated method stub
		String doc;
		User u = null;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			doc = file.getName();
			
			try {
				FileInputStream fileIn = new FileInputStream(fileLocation + "\\" + doc);
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				User user = (User) objectIn.readObject();
				objectIn.close();
				fileIn.close();
				
				if(username.matches(user.getUsername())) {
					
					if(pass.matches(user.getPassword())) {
						u = user;
						System.out.println("Log in successful");
						break;	
					}
					
				}
	
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
		}
		return u;
	}

	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		List<User> users = null;
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			try {
				FileInputStream fileIn = new FileInputStream(fileLocation + "\\" + file.getName());
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				User user = (User) objectIn.readObject();
				users.add(user);
				objectIn.close();
				fileIn.close();
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
		}
		return users;
	}

	public User updateUser(User u) {
		// TODO Auto-generated method stub
		File[] files = new File(fileLocation).listFiles();
		
		for(File file : files) {
			String doc = file.getName();
			
			if(u.getId() == Integer.parseInt(doc.split("\\.", 2)[0])) {
				
				try {
					FileOutputStream fileOut = new FileOutputStream(fileLocation + "\\" + doc, false);
					ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
					objectOut.writeObject(u);
					objectOut.close();
					fileOut.close();
					System.out.println("User updated");
				} catch(FileNotFoundException e) {
					System.out.println("User is not found");
					e.printStackTrace();
				} catch(IOException e) {
					System.out.println("Could not return user");
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			break;
		}
		return u;
	}

	public boolean removeUser(User u) {
		// TODO Auto-generated method stub
		File[] files = new File(fileLocation).listFiles();
		for(File file : files) {
			String doc = file.getName();
			if( u.getId() == Integer.parseInt(doc.split(".", 2)[0]) ) {
				return file.delete();
			}
		}
		return false;
	}

}
