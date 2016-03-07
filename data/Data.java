package data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
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
    @JsonProperty("count") public int count=0;
    @JsonProperty("handType") public HandType handType;
    @JsonProperty("coordinates") public ArrayList<Coordinate> coordinates = new ArrayList<>();

    @JsonCreator
    protected Data(){}
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
    @JsonGetter("count")
    public int getCount(){return this.count;}
    @JsonGetter("handType")
    public HandType getHandType(){return this.handType;}
    @JsonGetter("coordinates")
    public ArrayList<Coordinate> getCoordinates(){return this.coordinates;}
}
