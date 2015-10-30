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
    
    //DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
    
    //example of data inserting and the database schema
    db.getCollection("testing_gesture").insertOne(
            new Document("sign_ID", "001")
                    .append("meaning", "Hello")
                    .append("part_of_speech", "")
                    .append("frames", asList(
//                            new Document()
//                                    .append("date", format.parse("2014-10-01T00:00:00Z"))
//                                    .append("grade", "A")
//                                    .append("score", 11),
//                            new Document()
//                                    .append("date", format.parse("2014-01-16T00:00:00Z"))
//                                    .append("grade", "B")
//                                    .append("score", 17)))
                    .append("country", "Hong Kong")	//maybe optional
                    .append("type", "single_handed"));
}
