package main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.leapmotion.leap.Frame;
import data.HandType;
import data.LRO;
import data.OneFrame;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manage all samples here.
 */

/**
*       In this class, If there is two hand, I put all left hand data at 0 position and right hand data at 1 position
 *       If it is the finger case, 0-4 will be the fingers of left hand, 5-9 will be fingers of right hand
 */


public class Sample {



    /**
     * field
     */

    @JsonProperty("allFrames") private ArrayList<OneFrame> allFrames=new ArrayList<>();
    @JsonProperty("initialFingerCount") private int initialFingerCount = 0;
    @JsonProperty("initialPalmCount") private int initialPalmCount=0;
    @JsonProperty("initialHandType") private HandType initialHandType;
    @JsonProperty("lro") private LRO lro;
    @JsonProperty("averageHandNumber") private double averageHandNumber = 0;


    /**
     * constructor
     */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    @JsonCreator
    private Sample() {}

    //copy constructor
    public Sample(Sample source) {
        this.allFrames = source.allFrames;
        this.initialFingerCount=source.initialFingerCount;
        this.initialPalmCount=source.initialPalmCount;
        this.initialHandType=source.initialHandType;
        this.lro = source.lro;
        this.averageHandNumber = source.averageHandNumber;
    }

    /* preferred constructor */
    public Sample(Collection<Frame> source) throws Exception {
        //Empty sample is meaningless
        if (source == null || source.isEmpty())
            throw new Exception();

        //become the Array List type
        ArrayList<Frame> frames= new ArrayList<Frame>();frames.addAll(source);

        for(Frame frame : frames){
            if(frame == null){
                throw new Exception();
            }
            this.allFrames.add(new OneFrame(frame));
        }

        initialFingerCount = this.allFrames.get(0).fingerData.count;
        initialPalmCount=this.allFrames.get(0).palmData.count;
        initialHandType = this.allFrames.get(0).handType;

        lro = new LRO();
        for(OneFrame frame: allFrames){
            averageHandNumber = averageHandNumber + (double)frame.getPalmData().count;
            if( (lro.leftPalmOrigin != null) && lro.rightPalmOrigin != null){
                continue;
            }
            if(lro.leftPalmOrigin == null){
                if(frame.handType == HandType.LEFT){
                    lro.setLO(frame.getPalmData().getCoordinates().get(0));    // Single hand, so get 0
                }else if(frame.handType == HandType.BOTH){
                    lro.setLO(frame.getPalmData().getCoordinates().get(0));    // left hand, get 0
                }
            }
            if(lro.rightPalmOrigin == null){
                if(frame.handType == HandType.RIGHT){
                    lro.setRO(frame.getPalmData().getCoordinates().get(0));    // Single hand, so get 0
                }else if(frame.handType == HandType.BOTH){
                    lro.setRO(frame.getPalmData().getCoordinates().get(1));    // right hand, get 1
                }
            }
        }
        averageHandNumber = averageHandNumber / allFrames.size();
    }

    /**
     *  setter for Jackson, Jongo
     */
    @JsonSetter("initialFingerCount")
    public boolean setInitialFingerCount(int initialFingerCount){
        if(initialFingerCount>=0&&initialFingerCount<=10) {
            this.initialFingerCount = initialFingerCount;
            return true;
        }
        return  false;
    }
    @JsonSetter("initialPalmCount")
    public boolean setInitialPalmCount(int initialPalmCount){
        if(initialPalmCount>=1&&initialPalmCount<=2) {
            this.initialPalmCount = initialPalmCount;
            return true;
        }
        return  false;
    }
    @JsonSetter("initialHandType")
    public void setInitialHandType(HandType initialHandType){this.initialHandType=initialHandType;}
    @JsonSetter("allFrames")
    public boolean setAllFrames(Collection<OneFrame> source){
        if(source==null||source.isEmpty()||source.equals(this.allFrames))
            return false;
        this.allFrames.clear();
        this.allFrames.addAll(source);
        return true;
    }
    @JsonSetter("lro")
    public void setLRO(LRO lro){
        if(lro==null) throw new NullPointerException();
        this.lro=lro;
    }
    @JsonSetter("averageHandNumber")
    public boolean setAverageHandNumber(double number){
        if(number<0.0||number>2.0) return false;
        averageHandNumber=number;
        return true;
    }

    @JsonGetter("initialFingerCount")
    public int getInitialFingerCount(){return initialFingerCount;}
    @JsonGetter("initialPalmCount")
    public int getInitialPalmCount(){return initialPalmCount;}
    @JsonGetter("initialHandType")
    public HandType getInitialHandType(){return initialHandType;}
    @JsonGetter("allFrames")
    public ArrayList<OneFrame> getAllFrames(){return allFrames;}
    @JsonGetter("lro")
    public LRO getLRO(){return lro;}
    @JsonGetter("averageHandNumber")
    public double getAverageHandNumber(){return averageHandNumber;}
}


