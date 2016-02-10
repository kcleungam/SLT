import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

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
     * inner class
     */

    class OneFrame{
        @JsonProperty("fingerData")
        public FingerData fingerData;
        @JsonProperty("palmData")
        public PalmData palmData;
        @JsonProperty("handType")
        public HandType handType;

        /* for Jongo, Jackson */
        @JsonCreator
        protected OneFrame(){}

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
    }



    /**
     * field
     */

    @JsonProperty("allFrames") ArrayList<OneFrame> allFrames=new ArrayList<>();
    @JsonProperty("initialFingerCount") int initialFingerCount = 0;
    @JsonProperty("initialPalmCount") int initialPalmCount=0;
    @JsonProperty("initialHandType") HandType initialHandType;


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
}


