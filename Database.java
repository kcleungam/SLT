
/**
 * Jongo supports MongoDB-java-driver with version 3.x
 * MongoDB-java-driver with version 3.x have different syntax with the previous versions.
 * Some APIs are depreciated in version 3.x, however, Jongo sticks on the previous versions.
 * Further updates of Jongo with Mongo-java-driver 3.0 has not been announced.
 *
 * We use Jongo here because of its simplicity =]
 */
import com.leapmotion.leap.Frame;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

import java.util.ArrayList;

public class Database {
	/** field */

	// fundamental information
	private String database_name;
	private String collection_name;

	// Mongo
	private MongoClient client;

	// Jongo
	private Jongo Jdb;
	private org.jongo.MongoCollection Jcoll;



	/** constructor */

	public Database(String database_name, String collection_name) {
		// record the database name
		this.database_name = database_name;
		this.collection_name = collection_name;

		// connect to the database
		client = new MongoClient();

		// create Jongo
		// if it doesn't exist, Mongo will create it for you
		Jdb = new Jongo(client.getDB(database_name));
		// if it doesn't exist, Mongo will create it for you
		Jcoll = Jdb.getCollection("gestures");
	}

	/** methods */

	//add a sign
	public boolean addSign(Sign sign) throws Exception {
		if (sign == null || isNameInvalid(sign.getName())) {
			System.err.println("Method 'SaveGesture' has received an improper parameter");
			return false;
		}

		// check whether the given sign name exist
		long existence = Jcoll.count("{name:#}", sign.getName());
		if (existence == 0) {// insert
			Jcoll.insert(sign);
			//System.out.println("Added 1 new gesture.");
			return true;
		} else if (existence == 1) {// add sample(s) in it
			for(Sample s:sign.getAllSamples()){
				Jcoll.update("{name:#}", sign.getName()).with("{$addToSet:{samples:#}}",s);
				return true;
			}
		}

		// else, duplication, database get a serious problem
		throw new Exception("Duplicated Signs:\t" + sign.getName());
	}

	//remove a sign
	public boolean remove(Sign sign) throws Exception{
		if (sign == null || isNameInvalid(sign.getName())) {
			System.err.println("Method 'SaveGesture' has received an improper parameter");
			return false;
		}

		// check whether the given sign name exist
		long existence = Jcoll.count("{name:#}", sign.getName());
		if (existence == 0) // not exist
			return false;
		else if (existence == 1) {// add sample(s) in it
			for(Sample s:sign.getAllSamples()){
				Jcoll.update("{name:#}", sign.getName()).with("{$addToSet:{samples:#}}",s);
				return true;
			}
		}

		// else, duplication, database get a serious problem
		throw new Exception("Duplicated Signs:\t" + sign.getName());
	}

	/* helper function */
	private boolean isNameInvalid(String name) {
		// TODO: may use regular expression to forbid certain kinds of gesture
		if(name==null||name.isEmpty())
			return true;
		return false;
	}

}
