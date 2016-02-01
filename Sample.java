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

    class OneFrame{
        FingerData fingerData;
        PalmData palmData;
        HandType handType;

        class FingerData{
            public int count = 0;
            public ArrayList<Coordinate> coordinates = new ArrayList<>();

            public FingerData(){};

            public FingerData(Frame frame){

                HandList first=frame.hands();

                //the code should be safe as Leap Motion controller won't recognise more than 2 hands
                if (first.count() > 1) {
                    handType = HandType.BOTH;
                } else if (first.get(0).isLeft()) {
                    handType = HandType.LEFT;
                } else{
                    handType = HandType.RIGHT;
                }

                count=0;
                if(handType == HandType.BOTH){
                    for(Hand hand:first){
                        for (Finger finger : hand.fingers())
                            if (finger.isExtended()) count++;
                    }
                }else{
                    // doesn't work when hands.get(0) change to hand(0)
                    for (Finger finger : first.get(0).fingers())
                        if (finger.isExtended())
                            count++;
                }

                //iterate finger by finger to get the coordinates of the finger(s)
                // If single hand, put it directly
                // otherwise, put finger in left hand first( position 0-4) and then right hand (position 5-9)
                if (frame.fingers().count() > 0) {
                    if(frame.fingers().count() == 5){
                        for (Finger finger : frame.fingers()) {
                            Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                            coordinates.add(cor);
                        }
                    }else if(frame.fingers().count() == 10){
                        int label = 0;
                        for(Hand hand : frame.hands()){
                            if(hand.isLeft()){
                                label = 0;
                            }else {
                                label = 5;
                            }
                            for(Finger finger : hand.fingers()){
                                Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                                coordinates.set(label,cor);
                            }
                        }
                    }
                }
            }
        }

        class PalmData{
            public int count = 0;
            public ArrayList<Coordinate> coordinates = new ArrayList<>();

            public PalmData(Frame frame) {
                this.count = frame.hands().count();
                if (this.count > 0) {
                    if(this.count == 1){
                        for (Hand hand : frame.hands()) {
                            Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                            coordinates.add(cor);
                        }
                    }else if(this.count == 2){
                        for(Hand hand : frame.hands()){
                            Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                            if(hand.isLeft()){
                                coordinates.set(0, cor);
                            }else{
                                coordinates.set(1, cor);
                            }
                        }
                    }

                }

            }
        }

        public OneFrame(){}

        public OneFrame(Frame frame) throws Exception{
            if (frame == null) {
                throw new Exception();
            }

            fingerData = new FingerData(frame);
            palmData = new PalmData(frame);
        }
    }

    /* inner class */
    /*
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

    */

    /**
     * field
     */

    /*
    FingerData allFingers;
    PalmData allPalms;
    HandData allHands;
    */


    static enum HandType{LEFT,RIGHT,BOTH}
    ArrayList<OneFrame> allFrame;
    int fingerCount = 0;
    HandType handType;


    /**
     * constructor
     */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    public Sample() {
        /*
        allFingers = new FingerData();
        allPalms = new PalmData();
        allHands=new HandData();
        */
        allFrame = new ArrayList<>();
    }

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

        /*
        allHands=new HandData(frames);
        allPalms=new PalmData(frames);
        allFingers=new FingerData(frames);
        */

        for(Frame frame : frames){
            if(frame == null){
                throw new Exception();
            }
            OneFrame oneFrame = new OneFrame(frame);
            this.allFrame.add(oneFrame);
        }

        this.fingerCount = this.allFrame.get(0).fingerData.count;
        this.handType = this.allFrame.get(0).handType;
    }
}


