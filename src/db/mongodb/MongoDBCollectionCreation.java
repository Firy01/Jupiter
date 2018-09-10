package db.mongodb;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

public class MongoDBCollectionCreation {
	// Run as Java application to create MongoDB collections with index.
	private static final String USERSDB = "users";
	private static final String ITEMSDB = "items";
	public static void main(String[] args) throws ParseException {
		  
			// Step 1, connetion to MongoDB
			MongoClient mongoClient = new MongoClient();
			MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

			// Step 2, remove old collections.
			db.getCollection(USERSDB).drop();
			db.getCollection(ITEMSDB).drop();
			

			// Step 3, create new collections
			IndexOptions options = new IndexOptions().unique(true);
			db.getCollection(USERSDB).createIndex(new Document("user_id", 1), options);
			db.getCollection(ITEMSDB).createIndex(new Document("item_id", 1), options);

			// Step 4, insert fake user data and create index.
			db.getCollection(USERSDB).insertOne(
					new Document().append("user_id", "1111").append("password", "3229c1097c00d497a0fd282d586be050")
							.append("first_name", "John").append("last_name", "Smith"));

			mongoClient.close();
			System.out.println("Import is done successfully.");

	  }

}
