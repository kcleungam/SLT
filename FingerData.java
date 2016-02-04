import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;

import java.util.Collection;

/**
 * Created by alex on 1/2/2016.
 */
public class FingerData extends Data {
    /* constructor */

    /** exclusive use for Jongo */
    public FingerData() {}

    public FingerData(Frame frame) {
        HandList hands = frame.hands();
        count = (frame.fingers().count() > 0) ? frame.fingers().count() : 0;

        //the code should be safe as Leap Motion controller won't recognise more than 2 hands
        if (hands.count() > 1) {
            handType = HandType.BOTH;
        } else if (hands.get(0).isLeft()) {
            handType = HandType.LEFT;
        } else if (hands.get(0).isRight()) {
            handType = HandType.RIGHT;
        } else {
            System.err.println("Cannot determine the hand type.");
        }

        //iterate finger by finger to get the coordinates of the finger(s)
        // If single hand, put it directly
        // otherwise, put finger in left hand first( position 0-4) and then right hand (position 5-9)
        if (count == 5) {
            for (Finger finger : frame.fingers()) {
                Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                coordinates.add(cor);
            }
        } else if (count == 10) {
            int label = 0;
            for(int i = 0; i < 10; i++){
                Coordinate cor = new Coordinate(0,0,0);
                coordinates.add(cor);     // init the coordinates arraylist and ready to set below
            }

            for (Hand hand : frame.hands()) {
                label=(hand.isLeft())?0:5;
                for (Finger finger : hand.fingers()) {
                    Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                    coordinates.set(label++, cor);
                }
            }
        }

    }
}
