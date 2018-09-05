package db.mongodb;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBTableCreation {

	public static void main(String[] args) {
		
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
		
		db.getCollection("users").drop();
		
		IndexOptions options = new IndexOptions().unique(true);
		db.getCollection("users").createIndex(new Document("username", 1), options);
		
		db.getCollection("users").insertOne(new Document()
				.append("username", "root")
				.append("password", "bd184365f0042e415780a9ea670f9c89")
				.append("email", "me@naidingz.com")
				.append("vip", "1"));
		mongoClient.close();
		System.out.println("Import is done successfully");
	}
}
