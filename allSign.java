import java.util.HashMap;
import java.util.Map;

/**
 * Created by Krauser on 5/11/2015.
 */
public class allSign {
    private static HashMap<String,Sign> allSign;

    public allSign(){
        allSign = new HashMap<String, Sign>();
    }

    public Sign getSign(String signName){
        return this.getAllSign().get(signName);
    }
    public void setAllSign( HashMap<String, Sign> allSign) {
        this.allSign = allSign;
    }

    public void addOneSign(Sign sign){
        this.allSign.put(sign.getSignName(), sign);
        System.out.println("Sign \"  "+ sign.getSignName() + " \"  inserted");
    }

    public void removeOneSign(String signName){
        if(allSign.containsKey(signName)){
            allSign.remove(signName);
        }else{
            System.out.println("No such Sign.");
        }
    }

    public Map<String, Sign> getAllSign() {
        return allSign;
    }

    public Sign retrieveOneSign(String signName){
        return this.getAllSign().get(signName);
    }
}
