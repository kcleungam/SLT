import com.leapmotion.leap.*;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.Console;
import java.util.Scanner;
import java.util.Vector;
import java.lang.Math;
import java.util.*;


public class SLT {
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
    //must use com.leapmotion.leap.Vector as it has conflict with Vector in java
    com.leapmotion.leap.Vector inter = screen.intersect(finger, true);
    com.leapmotion.leap.Vector tipV = finger.tipVelocity();
    com.leapmotion.leap.Vector tipP = finger.tipPosition();             //this finger is an object to store tracked finger
    com.leapmotion.leap.Vector tipV1 = frame.finger(1).tipVelocity();   //specific one finger in a frame

    //arms
    Arm arm = hand.arm();
    com.leapmotion.leap.Vector wrist = arm.wristPosition();
    com.leapmotion.leap.Vector direction = arm.direction();

    //Matrix
    // https://developer.leapmotion.com/documentation/java/api/Leap.Matrix.html#javaclasscom_1_1leapmotion_1_1leap_1_1_matrix
    Matrix M = new Matrix();
    com.leapmotion.leap.Vector xBasis = new com.leapmotion.leap.Vector(23, 0, 0);
    com.leapmotion.leap.Vector yBasis = new com.leapmotion.leap.Vector(0, 12, 0);
    com.leapmotion.leap. Vector zBasis = new com.leapmotion.leap.Vector(0, 0, 45);
    Matrix transformMatrix = new Matrix(xBasis, yBasis, zBasis);
    com.leapmotion.leap.Vector thisTranslation = transformMatrix.getOrigin();

    // Still finding how to use the following, probably it talking about the view in LMC
    ScreenList screenList = controller.calibratedScreens();
    Screen screen = screenList.get(0);
    int sx = screen.widthPixels();
    int sy = screen.heightPixels();


}
