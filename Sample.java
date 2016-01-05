import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manage all samples here.
 */
public class Sample {
    /**
     * field
     */

    ArrayList<ArrayList<Coordinate>> allFinger;
    ArrayList<ArrayList<Coordinate>> allPalm;

    private int handCount;
    private String handType;

    //finger
    private int fingerCount;


    /**
     * constructor
     */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    public Sample() {
        allFinger = new ArrayList<ArrayList<Coordinate>>();
        allPalm = new ArrayList<ArrayList<Coordinate>>();

    }

    //copy constructor
    public Sample(Sample source) {
        this.allPalm = source.allPalm;
        this.allFinger = source.allFinger;
        this.fingerCount = source.fingerCount;
        this.handCount = source.handCount;
        this.handType = source.handType;
    }

    public Sample(Collection<Frame> source) throws Exception {
        //Empty sample is meaningless
        if (source == null || source.isEmpty())
            throw new Exception();
        //become the Array List type
        this.allFinger = new ArrayList<ArrayList<Coordinate>>();
        this.allPalm = new ArrayList<ArrayList<Coordinate>>();
        for (Frame frame : source) {
            ArrayList<Coordinate> frameFinger = new ArrayList<Coordinate>();
            if (frame.fingers().count() > 0) {
                for (Finger finger : frame.fingers()) {
                    Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                    frameFinger.add(cor);
                }
                allFinger.add(frameFinger);
            }
        }
        for (Frame frame : source) {
            ArrayList<Coordinate> framePalm = new ArrayList<Coordinate>();
            if (frame.hands().count() > 0) {
                for (Hand hand : frame.hands()) {
                    Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                    framePalm.add(cor);
                }
                allPalm.add(framePalm);
            }
        }


        int fCount = 0;
        ArrayList<Frame> frameList = new ArrayList<>();
        frameList.addAll(source);
        if (frameList.get(0).hands().count() > 1) {
            handType = "Both Hand";
            for (Hand hand : frameList.get(0).hands()) {
                for (Finger finger : hand.fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            }
        } else if (frameList.get(0).hands().count() == 1) {
            if (frameList.get(0).hands().get(0).isLeft()) {
                handType = "Left";
                // doesn't work when hands.get(0) change to hand(0)
                for (Finger finger : frameList.get(0).hands().get(0).fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            } else if (frameList.get(0).hands().get(0).isRight()) {
                handType = "Right";
                // doesn't work when hands.get(0) change to hand(0)
                for (Finger finger : frameList.get(0).hands().get(0).fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            }
        }

        fingerCount = fCount;
        handCount = frameList.get(0).hands().count();


    }

    public boolean setSample(Collection<Frame> source) {
        //Empty sample is meaningless
        if (source == null || source.isEmpty())
            return false;
        //become the Array List type
        this.allFinger = new ArrayList<ArrayList<Coordinate>>();
        this.allPalm = new ArrayList<ArrayList<Coordinate>>();
        for (Frame frame : source) {
            ArrayList<Coordinate> frameFinger = new ArrayList<Coordinate>();
            if (frame.fingers().count() > 0) {
                for (Finger finger : frame.fingers()) {
                    Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                    frameFinger.add(cor);
                }
                allFinger.add(frameFinger);
            }
        }
        for (Frame frame : source) {
            ArrayList<Coordinate> framePalm = new ArrayList<Coordinate>();
            if (frame.hands().count() > 0) {
                for (Hand hand : frame.hands()) {
                    Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                    framePalm.add(cor);
                }
                allPalm.add(framePalm);
            }
        }

        int fCount = 0;
        ArrayList<Frame> frameList = new ArrayList<>();
        frameList.addAll(source);
        if (frameList.get(0).hands().count() > 1) {
            this.setHandType( "Both Hand" );
            for (Hand hand : frameList.get(0).hands()) {
                for (Finger finger : hand.fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            }
        } else if (frameList.get(0).hands().count() == 1) {
            if (frameList.get(0).hands().get(0).isLeft()) {
                this.setHandType( "Left" );
                // doesn't work when hands.get(0) change to hand(0)
                for (Finger finger : frameList.get(0).hands().get(0).fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            } else if (frameList.get(0).hands().get(0).isRight()) {
                this.setHandType( "Right" );
                // doesn't work when hands.get(0) change to hand(0)
                for (Finger finger : frameList.get(0).hands().get(0).fingers()) {
                    if (finger.isExtended()) {
                        fCount++;
                    }
                }
            }
        }

        this.setFingerCount(fCount);
        this.setHandCount( frameList.get(0).hands().count() );

        return true;
    }

    public void setAllFinger(ArrayList<ArrayList<Coordinate>> allFinger) {
        this.allFinger = allFinger;
    }

    public void setAllPalm(ArrayList<ArrayList<Coordinate>> allPalm) {
        this.allPalm = allPalm;
    }

    public ArrayList<ArrayList<Coordinate>> getAllFinger() {
        return allFinger;
    }

    public ArrayList<ArrayList<Coordinate>> getAllPalm() {
        return allPalm;
    }

    public void setHandCount(int handCount) {
        this.handCount = handCount;
    }

    public void setFingerCount(int fingerCount) {
        this.fingerCount = fingerCount;
    }

    public void setHandType(String handType) {
        this.handType = handType;
    }

    public int getFingerCount() {
        return fingerCount;
    }

    public int getHandCount() {
        return handCount;
    }

    public String getHandType() {
        return handType;
    }
}


