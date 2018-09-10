package db.mongodb;

import static com.mongodb.client.model.Filters.eq;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MongoDBConnection implements DBConnection{
	
	private MongoClient mongoClient;
	private MongoDatabase db;
	private static final String USERSDB = "users";
	private static final String ITEMSDB = "items";
	private static final String USER_ID = "user_id";
	private static final String FAVORITE = "favorite";
	private static final String CATEGORIES = "categories";
	private static final String RATING = "rating";
	private static final String IMAGE_URL = "image_url";
	private static final String URL = "url";
	private static final String ADDRESS = "address";
	private static final String NAME = "name";
	private static final String DISTANCE = "distance";
	private static final String ITEM_ID = "item_id";
	public MongoDBConnection() {
		this.mongoClient = new MongoClient();
		this.db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		if(mongoClient != null) {
			mongoClient.close();
		}
	}

	/*
	 * db.users.updateOne{ 
	 *              {user_id: 1111}, 
	 *              { $push
	 *                  { favorite:
	 *                      { $each:["abcd"," efgh"] }
                        }
                    }
	 * 
	 * }
	 */
	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		/*db.getCollection("users").updateOne(new Document("user_id", userId),
				new Document("$push", new Document("favorite", new Document("$each", itemIds))));*/
		db.getCollection(USERSDB).updateOne(new Document(USER_ID, userId),
				new Document("$push", new Document(FAVORITE, new Document("$each", itemIds))));
	}

	/*
	 * db.users.updateOne{
	 * 	{user_id: 1111},
	 * 	{$pullAll
	 * 		{favorite:["abcd"],["efgh"]}
	 *  }
	 * }
	 */

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		db.getCollection(USERSDB).updateOne(new Document(USER_ID, userId), 
				new Document("$pullAll", new Document(FAVORITE, itemIds)));
	}


	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		Set<String> favoriteItems = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection(USERSDB).find(eq(USER_ID, userId));
		if(iterable != null && iterable.first().containsKey(FAVORITE)) {
			List<String> items = (List<String>) iterable.first().get(FAVORITE);
			favoriteItems.addAll(items);
		}
		return favoriteItems;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		Set<Item> items = new HashSet<>();
		Set<String> favoriteItems = getFavoriteItemIds(userId);
		for(String itemId : favoriteItems) {
			FindIterable<Document> iterable = db.getCollection(ITEMSDB).find(eq(ITEM_ID, itemId));
			if(iterable.first() != null) {
				Document doc = iterable.first();
				
				ItemBuilder builder = new ItemBuilder();
				//String itemId = doc.getString("item_id");
				builder.setItemId(doc.getString(ITEM_ID));
				builder.setDistance(doc.getDouble(DISTANCE));
				builder.setName(doc.getString(NAME));
				builder.setAddress(doc.getString(ADDRESS));
				builder.setUrl(doc.getString(URL));
				builder.setImageUrl(doc.getString(IMAGE_URL));
				builder.setRating(doc.getDouble(RATING));
				builder.setCategories(getCategories(itemId));
				items.add(builder.build());
			}
		}
		return items;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		Set<String> categories = new HashSet<>();
		FindIterable<Document> iterable = db.getCollection(ITEMSDB).find(eq(ITEM_ID, itemId));
		if(iterable != null && iterable.first().containsKey(CATEGORIES)) {
			List<String> cate = (List<String>) iterable.first().get(CATEGORIES);
			categories.addAll(cate);
		}
		return categories;
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		List<Item> items = tmApi.search(lat, lon, term);
		for(Item item : items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		FindIterable<Document> iterable = db.getCollection(ITEMSDB).find(eq(ITEM_ID, item.getItemId()));

		if (iterable.first() == null) {
			db.getCollection(ITEMSDB)
					.insertOne(new Document().append(ITEM_ID, item.getItemId()).append(DISTANCE, item.getDistance())
							.append(NAME, item.getName()).append(ADDRESS, item.getAddress())
							.append(URL, item.getUrl()).append(IMAGE_URL, item.getImageUrl())
							.append(RATING, item.getRating()).append(CATEGORIES, item.getCategories()));
		}
	}

	
	@Override
	public String getFullname(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
