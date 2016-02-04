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
        FingerData fingerData;
        PalmData palmData;
        HandType handType;

        /* constructor */

        public OneFrame(){}

        public OneFrame(Frame frame) throws Exception{
            if (frame == null) {
                throw new Exception();
            }

            fingerData = new FingerData(frame);
            palmData = new PalmData(frame);
            this.handType=fingerData.handType;
        }

        /* setter for Jackson, Jongo */
        public boolean setFingerData(FingerData fingerData){
            if(fingerData==null||this.fingerData.equals(fingerData))
                return false;
            this.fingerData=fingerData;
            return true;
        }
        public boolean setPalmData(PalmData palmData){
            if(palmData==null||this.palmData.equals(palmData))
                return false;
            this.palmData=palmData;
            return true;
        }
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

    public ArrayList<OneFrame> allFrames=new ArrayList<>();
    public int initialFingerCount = 0;
    public int initialPalmCount=0;
    public HandType initialHandType;


    /**
     * constructor
     */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    public Sample() {}

    //copy constructor
    public Sample(Sample source) {
        this.allFrames = source.allFrames;
        this.initialFingerCount=source.initialFingerCount;
        this.initialPalmCount=source.initialPalmCount;
        this.initialHandType=source.initialHandType;
    }

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
    public boolean setInitialFingerCount(int initialFingerCount){
        if(initialFingerCount>=0&&initialFingerCount<=10) {
            this.initialFingerCount = initialFingerCount;
            return true;
        }
        return  false;
    }
    public boolean setInitialPalmCount(int initialPalmCount){
        if(initialPalmCount>=1&&initialPalmCount<=2) {
            this.initialPalmCount = initialPalmCount;
            return true;
        }
        return  false;
    }
    public void setInitialHandType(HandType initialHandType){this.initialHandType=initialHandType;}
    public boolean setAllFrames(Collection<OneFrame> source){
        if(source==null||source.isEmpty()||source.equals(this.allFrames))
            return false;
        this.allFrames.clear();
        this.allFrames.addAll(source);
        return true;
    }
}


