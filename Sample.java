import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manage all samples here.
 */
public class Sample {
    /* inner class */
    class FingerData{
        public int count;
        public ArrayList< ArrayList<Coordinate> > coordinateSeq=new ArrayList<>();// sequence of coordinates frame by frame

        //default constructor
        public FingerData(){}
        //preferred constructor
        public FingerData(ArrayList<Frame> source){
            //count the number of fingers in the first frame
            HandList first=source.get(0).hands();
            count=0;
            if(allHands.type==HandType.BOTH){
                for(Hand hand:first){
                    for (Finger finger : first.get(0).fingers())
                        if (finger.isExtended()) count++;
                }
            }else{
                // doesn't work when hands.get(0) change to hand(0)
                for (Finger finger : first.get(0).fingers())
                    if (finger.isExtended())
                        count++;
            }

            //iterate frame by frame to get the coordinates of the finger(s)
            for (Frame frame : source) {
                ArrayList<Coordinate> fingers_coordinates = new ArrayList<>();
                if (frame.fingers().count() > 0) {
                    for (Finger finger : frame.fingers()) {
                        Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                        fingers_coordinates.add(cor);
                    }
                    coordinateSeq.add(fingers_coordinates);
                }
            }
        }
    }

    class PalmData{
        public ArrayList< ArrayList<Coordinate> > coordinateSeq=new ArrayList<>();// sequence of coordinates frame by frame

        //default constructor
        public PalmData(){}
        //preferred constructor
        public PalmData(ArrayList<Frame> source){
            for (Frame frame : source) {
                ArrayList<Coordinate> palm_coordinates = new ArrayList<>();
                if (frame.hands().count() > 0) {
                    for (Hand hand : frame.hands()) {
                        Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                        palm_coordinates.add(cor);
                    }
                    coordinateSeq.add(palm_coordinates);
                }
            }
        }
    }

    class HandData{
        public HandType type;
        public int count;

        //default constructor
        public HandData(){}
        //preferred constructor
        public HandData(ArrayList<Frame> source){
            //determine the characteristic of hand(s) by the first frame
            HandList first=source.get(0).hands();

            //the code should be safe as Leap Motion controller won't recognise more than 2 hands
            count=first.count();
            if (this.count > 1) {
                type=HandType.BOTH;
            } else if (first.get(0).isLeft()) {
                type=HandType.LEFT;
            } else{
                type=HandType.RIGHT;
            }
        }
    }


    /**
     * field
     */
    static enum HandType{LEFT,RIGHT,BOTH}

    FingerData allFingers;
    PalmData allPalms;
    HandData allHands;



    /**
     * constructor
     */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    public Sample() {
        allFingers = new FingerData();
        allPalms = new PalmData();
        allHands=new HandData();
    }

    //copy constructor
    public Sample(Sample source) {
        this.allFingers=source.allFingers;
        this.allPalms=source.allPalms;
        this.allHands=source.allHands;
    }

    public Sample(Collection<Frame> source) throws Exception {
        //Empty sample is meaningless
        if (source == null || source.isEmpty())
            throw new Exception();

        //become the Array List type
        ArrayList<Frame> frames= new ArrayList<Frame>();frames.addAll(source);

        allHands=new HandData(frames);
        allPalms=new PalmData(frames);
        allFingers=new FingerData(frames);
    }
}


