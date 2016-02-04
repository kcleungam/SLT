import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alex on 1/2/2016.
 */
public abstract class Data {
    public int count=0;
    public HandType handType;
    public ArrayList<Coordinate> coordinates = new ArrayList<>();

    /* setter for Jackson, Jongo */
    public void setCount(int count){this.count=count;}
    public void setHandType(HandType handType){this.handType=handType;}
    public boolean setCoordinates(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.coordinates.clear();
        this.coordinates.addAll(source);
        return true;
    }
}
