import java.util.HashMap;
import java.util.Map;

/**
 * Created by Krauser on 5/11/2015.
 */
public class SignBank {
    /** field */

    //fundamental information
    private HashMap<String,Sign> signs = new HashMap<String,Sign>();
    //private String DBName;
    //private String CollectionName;



    /** constructor */

    //load from database
    public SignBank(Database db)throws Exception{
        if(db == null)
            throw new Exception();
        this.signs = db.getAllSigns();
    }

    //with initialisation
    public SignBank(Map<String,Sign> source) throws Exception {
        if(source == null||source.isEmpty())
            throw new Exception();
        this.signs.putAll(source);
    }

    //copy constructor
    public SignBank(SignBank other) throws Exception {
        if(other == null)
            throw new Exception();
        this.signs = other.signs;
    }



    /** method  */

    //add one sign
    public boolean addSign(String SignName,Sign sign){
        if(SignName == null||SignName.isEmpty() || sign == null)
            return false;
        if(signs.containsKey(SignName)) {
            return signs.get(SignName).addSamples(sign.getAllSamples());
        }else{
            signs.put(SignName,sign);
            return true;
        }
    }

    //remove one sign by SignName
    public boolean removeSign(String SignName){
        if(SignName == null || SignName.isEmpty())
            return false;
        if(signs.containsKey(SignName)){
            signs.remove(SignName);
            return true;
        }
        return false;
    }

    //remove all signs
    public boolean removeAllSign(){
            signs.clear();
            return true;
    }



    /** setter & getter */

    public Sign getSign(String SignName){
        return signs.get(SignName);
    }

    //replace all signs
    public boolean setAllSigns(Map<String,Sign> source) {
        if(source == null)
            return false;
        signs.clear();
        signs.putAll(source);
        return true;
    }

    public HashMap<String,Sign> getAllSigns() {
        return signs;
    }
}
