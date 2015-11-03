/**
 * Created by alex on 30/10/2015.
 */
/*//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\*/
/* Leap Motion */
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.jongo.Jongo;
import org.jongo.MongoCursor;
import org.jongo.MongoCollection.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static java.util.Arrays.asList;


public class database {
    /* field */
    private String name="";
    private MongoClient client;
    private MongoCollection M;
    private Jongo Jdb;
    private org.jongo.MongoCollection J;

    /* constructor */
    public database(String database_name){
        // record the database name
        name=database_name;
        // connect to the database
        client=new MongoClient();
        // create Jongo
        Jdb=new Jongo(client.getDB(name));
        J=Jdb.getCollection("gestures");
        // create Mongo
        M=client.getDatabase(name).getCollection("gestures", SLT.class);
    }

    /* methods */
    //save a gesture
    public boolean saveGesture(SLT slt,String gesture_name,String asian_sign_bank_id){
        if(slt==null||gesture_name==null||isNameValid(gesture_name)||isNameValid(asian_sign_bank_id))
            return false;
        //TODO: check whether the "save" API adds elements without repetition
        J.save(slt);
        return true;
    }
    public SLT findGesture(String gesture_name) throws Exception{
        if(gesture_name==null||isNameValid(gesture_name))
            throw new Exception();
        /* find the given gesture in the database */
        SLT output=J.findOne("{name:"+gesture_name+"}").as(SLT.class);

        return output;
    }

    /* helper function */
    private boolean isNameValid(String name){
        //ToDo: may use regular expression to forbid certain kinds of gesture names
        return !(name.equals(""));
    }





}
