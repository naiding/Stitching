package db;

public interface DBConnection {
	
	/**
     * Close the connection.
     */
    public void close();
    
    public boolean existUser(String username);
    public boolean createUser(String username, String email, String password);
    
	/**
     * Get full name of a user. (This is not needed for main course, just for demo
     * and extension).
     * 
     * @param username
     * @return full name of the user
     */
    public String getFullname(String username);

    /**
     * Return whether the credential is correct. (This is not needed for main
     * course, just for demo and extension)
     * 
     * @param username
     * @param password
     * @return boolean
     */
    public boolean verifyLogin(String username, String password);
}
