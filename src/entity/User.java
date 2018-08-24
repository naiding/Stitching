package entity;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	private String username;
	private String password;
	private String email;
	private String vip;
	
	private User(UserBuilder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.email = builder.email;
        this.vip = builder.vip;
    }
	
	/*
	 * A series of getter and setter
	 */
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getVip() {
		return vip;
	}
	
	public void setVip(String vip) {
		this.vip = vip;
	}
	
	/* 
	 * To JSON Object
	 */
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("username", username);
			obj.put("password", password);
            obj.put("email", email);
            obj.put("vip", vip);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/*
	 * User builder
	 */
	public static class UserBuilder {
        private String username;
        private String password;
        private String email;
        private String vip;
        
        public void setUsername(String username) {
			this.username = username;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		public void setVip(String vip) {
			this.vip = vip;
		}

		public User build() {
        		return new User(this);
        }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
