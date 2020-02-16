package net.havania.core.utils;

import java.sql.*;

public class Database {
	
	private String urlBase;
	private String host;
	private String database;
	private String username;
	private String password;
	private Connection connection;
	
	/** DATABASE CONSTRUCTOR **/
	public Database(String urlBase, String host, String database, String username, String password) {
		this.urlBase = urlBase;
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	/** DATABASE CONNECTION **/
	public void connection() {
		if(!isConnected()) {
			try{
				this.connection = DriverManager.getConnection(this.urlBase + this.host + "/" + this.database, this.username, this.password);
				return;
			}catch (SQLException e) {
				System.err.println("Cannot connect to the database");
				return;
			}
		}
	}
	
	/** DATABASE DECCONECTION **/
	public void deconnection() {
		if(isConnected()) {
			try{
				this.connection.close();
				return;
			}catch (SQLException e) {
				System.err.println("Cannot disconnect from the database");
				return;
			}
		}
	}
	
	/* DETECTION DATABASE IS OPEN */
	public boolean isConnected() {
		try{
			if((this.connection == null) || (this.connection.isClosed()) || (this.connection.isValid(5))) {
				return false;
			}
			return true;
		}catch (SQLException e) {
			System.err.println("Cannot see teh state of database !");
		}
		return false;
	}
	
	/** DATABASE GET CONNECTION **/
	public Connection getConnection() {
		return connection;
	}
}

