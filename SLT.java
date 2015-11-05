import com.leapmotion.leap.*;

import java.util.ArrayList;


//Object List
    /*
        HandList hands = frame.hands(); // be careful about the s, it means hand list
        Listener listener = new Listener();//Create New Listener For The Leap
        Controller controller = new Controller();//Create New Controller For The Leap
        Hand h1 = hands.get(0);
        Hand h2 = frame.hand(1);    //another way to get specific hand
        Finger finger = frame.fingers().get(arg0);
        FingerList allFingers = frame.fingers();
        //must use com.leapmotion.leap.Vector as it has conflict with Vector in java
        com.leapmotion.leap.Vector tipV = finger.tipVelocity();
        float angleInRadians = Vector.xAxis().angleTo(Vector.yAxis()); // angleInRadians = PI/2 (90 degrees)
        Arm arm = hand.arm();
        com.leapmotion.leap.Vector wrist = arm.wristPosition();
        com.leapmotion.leap.Vector direction = arm.direction();
        float framePeriod = frame.timestamp() - controller.frame(1).timestamp(); //timestamp of frames
        long currentID = controller.frame().id();	//calls the ID of the frame
        Matrix M = new Matrix();
        com.leapmotion.leap.Vector xBasis = new com.leapmotion.leap.Vector(23, 0, 0);
    */

public class SLT {

    static boolean recording = false;
    static float minRecVelocity = 100;
    static float maxRecVelocity = 300;
    static int recordedFrames   = 0;
    static int minPoseFrames    = 75;
    public static void main(String args[]) throws InterruptedException {
        Listener listener = new Listener();//Create New Listener For The Leap
        Controller controller = new Controller(listener);//Create New Controller For The Leap
        while (true) {
            if (controller.isConnected() == true) {
                listener.onConnect(controller);     //it will print "connected"
                /**
                                    * keep grabbing the frame from controller, check whether it is recordable, add to the collection if it is. store it at last
                                */
                ArrayList<Frame> oneSample = new ArrayList<Frame>();
                while (true) {
                    Frame frame = controller.frame();
                    if(recording == true ){
                        if( recordableFrame(frame, minRecVelocity, maxRecVelocity) == true){
                            oneSample = recordFrame(oneSample, frame);
                        }else{
                            continue;
                        }
                    }else if(recording == false){
                        /**
                                             * This can be gesture recognition session
                                             */
                    }
                }



            }
            if (controller.isConnected() == false) {
                listener.onDisconnect(controller);  // it will print "disconnected"
                while(controller.isConnected() == false){
                    Thread.currentThread().wait(1000);
                }
            }
        }
    }

    public static boolean recordableFrame(Frame frame, float min, float max){
        HandList hands = frame.hands();
        boolean recordable = false;
        /*  First check the palmVelocity of each hand to see if it is moving
        *   Then check each finger of each hand to see if it is moving*/
        for(int i = 0; i < hands.count(); i++ ){
            if( recordable == true){
                break;
            }
            Hand hand = hands.get(i);
            FingerList fingerList = hand.fingers();
            // find the palmVelocity and see if it is between min and max
            // Maybe we can use pythagoras theorem to get more accurate one
            float maxPalmVelocity  = Math.max( Math.abs(hand.palmVelocity().getX()),
                                            Math.abs(hand.palmVelocity().getY())
                                            );
            maxPalmVelocity        = Math.max(Math.abs(maxPalmVelocity),
                                            Math.abs(hand.palmVelocity().getZ())
                                            );
            if( maxPalmVelocity >= min){
                if( maxPalmVelocity <=max){
                    recordable = true;
                    break;
                }
            }
            for(int j = 0; j < fingerList.count(); j++){
                com.leapmotion.leap.Vector tipV =   fingerList.get(j).tipVelocity();
                float maxTipV    =   Math.max(  Math.abs(tipV.getX()),
                                                Math.abs(tipV.getY())
                                                );
                maxTipV = Math.max( maxTipV,
                                    Math.abs(tipV.getZ())
                                    );
                if(maxTipV >= min){
                    if(maxTipV <= max){
                        recordable = true;
                        break;
                    }
                }
            }
        }

        if(recordable){
            recordedFrames++;
            if(recordedFrames >= minPoseFrames){
                recording = true;
            }
        } else {
            recordedFrames = 0;
        }

        return recordable;  //return if the hand is moving or not
    }

    public static ArrayList<Frame> recordFrame(ArrayList<Frame> oneSample, Frame frame ){
        oneSample.add(frame);
        return oneSample;
    }



    public void startTraining(int countDown, Sign sign, Frame frame){ // also the gesture set

    }


}

/**
 *
Listener listener = new Listener();//Create New Listener For The Leap
Controller controller = new Controller();//Create New Controller For The Leap
Frame frame = controller.frame();

//Use of hand Object
HandList hands = frame.hands(); // be careful about the s, it means hand list
Hand h1 = hands.get(0);
Hand h2 = frame.hand(1);    //another way to get specific hand
com.leapmotion.leap.Vector palmV = frame.hand(1).palmVelocity();
com.leapmotion.leap.Vector palmN = frame.hand(1).palmNormal();

//Use of finger Object
Finger finger = frame.fingers().get(arg0);
FingerList allFingers = frame.fingers();
//must use com.leapmotion.leap.Vector as it has conflict with Vector in java
com.leapmotion.leap.Vector tipV = finger.tipVelocity();
com.leapmotion.leap.Vector tipP = finger.tipPosition();             //this finger is an object to store tracked finger
com.leapmotion.leap.Vector tipV1 = frame.finger(1).tipVelocity();   //specific one finger in a frame

//arms
Arm arm = hand.arm();
com.leapmotion.leap.Vector wrist = arm.wristPosition();
com.leapmotion.leap.Vector direction = arm.direction();

//time and ID
float framePeriod = frame.timestamp() - controller.frame(1).timestamp(); //timestamp of frames
long currentID = controller.frame().id();	//calls the ID of the frame

//vector operation
com.leapmotion.leap.Vector normalizedVector = otherVector.normalized();
com.leapmotion.leap.Vector sum = thisVector.plus(thatVector);
com.leapmotion.leap.Vector difference = thisVector.minus(thatVector);
float length = thisVector.magnitude();	//magnitude of vector
float lengthSquared = thisVector.magnitudeSquared();
float x = thisVector.get(0);	// 1 = y-coor; 2 = z-coor
com.leapmotion.leap.Vector crossProduct = thisVector.cross(thatVector);
float angleInRadians = Vector.xAxis().angleTo(Vector.yAxis()); // angleInRadians = PI/2 (90 degrees)

//Matrix
// https://developer.leapmotion.com/documentation/java/api/Leap.Matrix.html#javaclasscom_1_1leapmotion_1_1leap_1_1_matrix
Matrix M = new Matrix();
com.leapmotion.leap.Vector xBasis = new com.leapmotion.leap.Vector(23, 0, 0);
com.leapmotion.leap.Vector yBasis = new com.leapmotion.leap.Vector(0, 12, 0);
com.leapmotion.leap. Vector zBasis = new com.leapmotion.leap.Vector(0, 0, 45);
Matrix transformMatrix = new Matrix(xBasis, yBasis, zBasis);
com.leapmotion.leap.Vector thisTranslation = transformMatrix.getOrigin();

 */
