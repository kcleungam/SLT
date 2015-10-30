/**
 * Created by alex on 30/10/2015.
 */
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;

public class database {
    MongoClient mongoClient = new MongoClient();
    MongoDatabase db = mongoClient.getDatabase("test");
    
    //example of data inserting and the database schema
    db.getCollection("testing_gesture").insertOne(
            new Document("sign_ID", "001")
                    .append("meaning", "Hello")
                    .append("part_of_speech", "")
                    .append("frames", asList(
                    		/*Frames*/
                    		)
                    .append("country", "Hong Kong")	//maybe optional
                    .append("type", "single_handed"));
}
