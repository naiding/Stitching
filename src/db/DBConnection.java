package db;

public interface DBConnection {
	
	/**
     * Close the connection.
     */
    public void close();
    
    /**
     * Return whether the user is existed
     * 
     * @param username
     */
    public boolean existUser(String username);
    
    /**
     * Return the result of creating a user
     * 
     * @param username
     * @param email
     * @param password
     * @return
     */
    public boolean createUser(String username, String email, String password);

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
