/**
 * Created by alex on 30/10/2015.
 */
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
public class database {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
}
