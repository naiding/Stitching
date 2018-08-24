package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBConnection;

public class MySQLConnection implements DBConnection {
	
	private Connection conn;

	public MySQLConnection() {
		try {
            System.out.println("Connecting to " + MySQLDBUtil.URL);
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            this.conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean existUser(String username) {
		if (conn == null) {
			return false;
		} 
		
		try {
			String sql = "SELECT username FROM users WHERE username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public boolean createUser(String username, String password, String email) {
		if (conn == null) {
			return false;
		}
		
		try {			
			String sql = "INSERT INTO users "
	            			+ "(username, password, email, vip) "
	            			+ "VALUES "
	            			+ "(?, ?, ?, '0')";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, email);
            stmt.executeUpdate();
            	
            return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		return false;
	}

	@Override
	public boolean verifyLogin(String username, String password) {
		if (conn == null) {
			return false;
		}
		
		try {
			String sql = "SELECT username FROM users WHERE username = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	@Override
	public String getUserVip(String username) {
		if (conn == null) {
			return null;
		}
		try {
			String sql = "SELECT vip FROM users WHERE username = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				return rs.getString("vip");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
