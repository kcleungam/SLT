import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alex on 1/2/2016.
 */
public abstract class Data {
    /**
     * field
     */
    @JsonProperty("count") int count=0;
    @JsonProperty("handType") HandType handType;
    @JsonProperty("coordinates") ArrayList<Coordinate> coordinates = new ArrayList<>();

    /**
     *  setter for Jackson, Jongo
     */
    @JsonSetter("count")
    public void setCount(int count){this.count=count;}

    @JsonSetter("handType")
    public void setHandType(HandType handType){this.handType=handType;}

    @JsonSetter("coordinates")
    public boolean setCoordinates(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.coordinates.clear();
        this.coordinates.addAll(source);
        return true;
    }
}
