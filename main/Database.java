package main;
/**
 * Jongo supports MongoDB-java-driver with version 3.x
 * MongoDB-java-driver with version 3.x have different syntax with the previous versions.
 * Some APIs are depreciated in version 3.x, however, Jongo sticks on the previous versions.
 * Further updates of Jongo with Mongo-java-driver 3.0 has not been announced.
 *
 * We use Jongo here because of its simplicity =]
 */
import com.mongodb.MongoClient;
import data.HandType;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
	/** field */

	// fundamental information
	private String database_name = "Signs";
	private String collection_name = "HK_Signs";

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
		Jcoll = Jdb.getCollection(collection_name);
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

		// else, duplication, database gets a serious problem
		throw new Exception("Duplicated Signs:\t" + sign.getName());
	}

	//remove a sign
	public boolean removeSign(Sign sign) throws Exception{
		if (sign == null || isNameInvalid(sign.getName())) {
			System.err.println("Method 'SaveGesture' has received an improper parameter");
			return false;
		}

		// check whether the given sign name exist
		long existence = Jcoll.count("{name:#}", sign.getName());
		if (existence == 0) // not exist
			return false;
		else if (existence == 1) {// add sample(s) in it
			Jcoll.remove("{name:#}",sign.getName());
			return true;
		}

		// else, duplication, database get a serious problem
		throw new Exception("Duplicated Signs:\t" + sign.getName());
	}

	//get all signs
	public HashMap<String,Sign> getAllSigns() throws IOException {
		HashMap<String,Sign> result=new HashMap<String,Sign>();
		Sign temp;

		MongoCursor<Sign> all = Jcoll.find().as(Sign.class);
		while(all.hasNext()) {
			temp=all.next();
			result.put(temp.getName(),temp);
		}

		all.close();

		return result;
	}

    //remove all signs
    public void removeAllSign() throws Exception {
        Jcoll.remove("{}");
    }

	//ToDo: maybe use Enum to simplify the following query operations

	//search by the number of finger(s)
	public HashMap<String,Sign> getSignsByInitialFingerCount(int fingers) throws IOException {
		HashMap<String,Sign> result=new HashMap<String,Sign>();
		Sign temp;

		MongoCursor<Sign> all = Jcoll.find("{initialFingerCount:#}",fingers).as(Sign.class);
		while(all.hasNext()) {
			temp=all.next();
			result.put(temp.getName(),temp);
		}

		all.close();

		return result;
	}

	// Search by the handType
	public HashMap<String,Sign> getSignsByInitialHandType(HandType hand_type) throws IOException {
		HashMap<String,Sign> result=new HashMap<String,Sign>();
		Sign temp;

		MongoCursor<Sign> all = Jcoll.find("{initialHandType:#}",hand_type).as(Sign.class);
		while(all.hasNext()) {
			temp=all.next();
			result.put(temp.getName(),temp);
		}

		all.close();

		return result;
	}

	// Search by both handType and the number of finger(s)
	public HashMap<String, Sign> getSignsByBoth(int fingers, HandType hand_type, int tolerance) throws Exception {
		HashMap<String, Sign> result = new HashMap<String, Sign>();
		Sign temp;

		if(tolerance<0) throw new Exception("tolerance MUST be non-negative.");

		for(int i=-tolerance;i<=tolerance;i++){
			//remark: fingers+i<0 should give no result
			MongoCursor<Sign> all = Jcoll.find("{initialHandType:#, initialFingerCount:#}", hand_type, fingers+i).as(Sign.class);
			while (all.hasNext()) {
				temp = all.next();
				result.put(temp.getName(), temp);
			}

			all.close();
		}

		return result;
	}

	// Search by name
	public Sign getSignsByName(String name) throws IOException {
		Sign result = Jcoll.findOne("{name:#}", name).as(Sign.class);

		return result;
	}

	//Get the first sample of a given gesture
	public Sample getFirstSample(String name) throws Exception{
		Sign result=Jcoll.findOne("{name:#}",name).projection("{samples:{$slice:1}}").as(Sign.class);
        /*if(result==null) throw new NullPointerException("Wrong query: null");
        if(result.getAllSamples().size()>1) throw new Exception("Wrong query: more than 1 gesture");
        if(result.getAllSamples().size()==0) throw new NullPointerException("No sample");*/
        ArrayList<Sample> arrayList=new ArrayList<>();
        arrayList.addAll(result.getAllSamples());
        Sample firstSample=arrayList.get(0);
        return firstSample;
	}

	//Check whether the given sign name exist
	public Boolean isNameExist(String name){
		long existence = Jcoll.count("{name:#}", name);

		if (existence == 0) // not exist
			return false;
		else
			return true;
	}


	/** helper function */
	private boolean isNameInvalid(String name) {
		// TODO: may use regular expression to forbid certain kinds of gesture
		if(name==null||name.isEmpty())
			return true;
		return false;
	}

}
