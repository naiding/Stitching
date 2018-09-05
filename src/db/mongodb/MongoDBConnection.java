package db.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import static com.mongodb.client.model.Filters.eq;

public class MongoDBConnection implements DBConnection {

	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public MongoDBConnection() {
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	
	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public boolean existUser(String username) {
		FindIterable<Document> iterable = 
				db.getCollection("users").find(eq("username", username));
		return iterable.first() != null;
	}

	@Override
	public boolean createUser(String username, String password, String email) {
		db.getCollection("users").insertOne(new Document()
				.append("username", username)
				.append("password", password)
				.append("email", email)
				.append("vip", "0"));
		return true;
	}

	@Override
	public boolean verifyLogin(String username, String password) {
		FindIterable<Document> iterable = 
				db.getCollection("users").find(eq("username", username));
		if (iterable.first() != null) {
			Document doc = iterable.first();
			return doc.getString("password").equals(password);
		}
		return false;
	}

	@Override
	public String getUserVip(String username) {
		FindIterable<Document> iterable = 
				db.getCollection("users").find(eq("username", username));
		if (iterable.first() != null) {
			Document doc = iterable.first();
			return doc.getString("vip");
		}
		return null;
	}
}
