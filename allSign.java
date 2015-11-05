import java.util.Map;

/**
 * Created by Krauser on 5/11/2015.
 */
public class allSign {
    private Map<String,Sign> allSign;

    public void setAllSign( Map<String, Sign> allSign) {
        this.allSign = allSign;
    }

    public void insertOneSign(Sign sign){
        this.allSign.put(sign.getSignName(), sign);
        System.out.println(sign.getSignName() + "inserted");
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
}
