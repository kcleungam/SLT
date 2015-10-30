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
    com.leapmotion.leap.Vector sped = finger.tipVelocity();

}
