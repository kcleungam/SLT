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

        public OneFrame(){}

        public OneFrame(Frame frame) throws Exception{
            if (frame == null) {
                throw new Exception();
            }

            fingerData = new FingerData(frame);
            palmData = new PalmData(frame);
            this.handType=fingerData.handType;
        }
    }



    /**
     * field
     */

    public ArrayList<OneFrame> allFrame=new ArrayList<>();
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
        this.allFrame = source.allFrame;
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
            this.allFrame.add(new OneFrame(frame));
        }

        initialFingerCount = this.allFrame.get(0).fingerData.count;
        initialPalmCount=this.allFrame.get(0).palmData.count;
        initialHandType = this.allFrame.get(0).handType;
    }
}


