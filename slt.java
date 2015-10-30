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
    Finger finger = frame.fingers().get(arg0);
    ScreenList screenList = controller.calibratedScreens();
    Screen screen = screenList.get(0);

    com.leapmotion.leap.Vector inter = screen.intersect(finger, true);
    com.leapmotion.leap.Vector tipV = finger.tipVelocity();
    com.leapmotion.leap.Vector tipP = finger.tipPosition();             //this finger is an object to store tracked finger
    com.leapmotion.leap.Vector tipV1 = frame.finger(1).tipVelocity();   //specific one finger in a frame
    com.leapmotion.leap.Vector palmV = frame.hand(1).palmVelocity();
    com.leapmotion.leap.Vector palmN = frame.hand(1).palmNormal();
    int sx = screen.widthPixels();
    int sy = screen.heightPixels();

}
