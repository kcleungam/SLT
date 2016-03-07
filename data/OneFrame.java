package data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.leapmotion.leap.Frame;

/**
 * Created by Krauser on 21/2/2016.
 */
public class OneFrame {
    @JsonProperty("fingerData")
    public FingerData fingerData;
    @JsonProperty("palmData")
    public PalmData palmData;
    @JsonProperty("handType")
    public HandType handType;

    /* for Jongo, Jackson */
    @JsonCreator
    private OneFrame(@JsonProperty("fingerData") FingerData fingerData,@JsonProperty("palmData") PalmData palmData,@JsonProperty("handType") HandType handType) throws NullPointerException{
        if(fingerData==null||palmData==null||handType==null) throw new NullPointerException();
        this.fingerData=fingerData;
        this.palmData=palmData;
        this.handType=handType;
    }

    /* preferred constructor */
    public OneFrame(Frame frame) throws Exception{
        if (frame == null) {
            throw new Exception();
        }

        fingerData = new FingerData(frame);
        palmData = new PalmData(frame);
        this.handType=fingerData.handType;
    }

    /* setter for Jackson, Jongo */
    @JsonSetter("fingerData")
    public boolean setFingerData(FingerData fingerData){
        if(fingerData==null||this.fingerData.equals(fingerData))
            return false;
        this.fingerData=fingerData;
        return true;
    }
    @JsonSetter("palmData")
    public boolean setPalmData(PalmData palmData){
        if(palmData==null||this.palmData.equals(palmData))
            return false;
        this.palmData=palmData;
        return true;
    }
    @JsonSetter("handType")
    public boolean setHandType(HandType handType){
        if(handType==null||this.handType.equals(handType))
            return false;
        this.handType=handType;
        return true;
    }
    @JsonGetter("fingerData")
    public FingerData getFingerData(){return this.fingerData;}
    @JsonGetter("palmData")
    public PalmData getPalmData(){return this.palmData;}
    @JsonGetter("handType")
    public HandType getHandType(){return this.handType;}
}
