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


}


