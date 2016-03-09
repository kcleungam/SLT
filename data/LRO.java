package data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by Krauser on 7/3/2016.
 */
public class LRO {
    @JsonProperty("leftPalmOrigin") public Coordinate leftPalmOrigin;
    @JsonProperty("rightPalmOrigin") public Coordinate rightPalmOrigin;

    public LRO(){
        leftPalmOrigin = null;
        rightPalmOrigin = null;
    }

    @JsonCreator
    private LRO(@JsonProperty("leftPalmOrigin") Coordinate left,@JsonProperty("rightPalmOrigin") Coordinate right){
        //if(left==null||right==null) throw new NullPointerException();
        leftPalmOrigin=left;
        rightPalmOrigin=right;
    }

    @JsonSetter("leftPalmOrigin")
    public void setLO(Coordinate left){
        leftPalmOrigin = left;
    }
    @JsonSetter("rightPalmOrigin")
    public void setRO(Coordinate right){
        rightPalmOrigin = right;
    }

    public void setLRO(Coordinate left, Coordinate right){
        leftPalmOrigin = left;
        rightPalmOrigin = right;
    }

    public void resetLRO(){
        leftPalmOrigin = null;
        rightPalmOrigin = null;
    }

    @JsonGetter("leftPalmOrigin")
    public Coordinate getLeftPalmOrigin(){return leftPalmOrigin;}
    @JsonGetter("rightPalmOrigin")
    public Coordinate getRightPalmOrigin(){return rightPalmOrigin;}
}
