package db.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema.
    public static void main(String[] args) {
        try {
        	
            // Step 1 Connect to MySQL.
            System.out.println("Connecting to " + MySQLDBUtil.URL);
            // register self to the driver's list
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            // find the proper driver and construct connection
            Connection conn = DriverManager.getConnection(MySQLDBUtil.URL);
            if (conn == null) {
                return;
            }
                   
            // Step 2 Drop tables in case they exist.
            Statement stmt = conn.createStatement();
            String sql = "DROP TABLE IF EXISTS users";
            stmt.executeUpdate(sql);
            
            // Step 3 Create new table 
            sql = "CREATE TABLE users ("
            		+ "username VARCHAR(255) NOT NULL,"
            		+ "email VARCHAR(255) NOT NULL,"
            		+ "password VARCHAR(255) NOT NULL,"
            		+ "vip_level VARCHAR(255) NOT NULL," 
            		+ "PRIMARY KEY (username)"
            		+ ")";
            stmt.executeUpdate(sql);
            
            // Step 4: insert fake user 1111/3229c1097c00d497a0fd282d586be050
            sql = "INSERT INTO users "
            		+ "(username, email, password, vip_level) "
            		+ "VALUES "
            		+ "('root', 'zhounaiding@gmail.com', '4f5fba03a86607a215fe91bd47735689', '1')";
            stmt.executeUpdate(sql);

            System.out.println("Import done successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
