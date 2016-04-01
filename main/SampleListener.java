package main; /**
 * Created by Krauser on 7/11/2015.
 */

/**
 *  We have to use the listener to track the data. Unfortunately, it provide several function only which are being trigger automatically.
 *  Those function provided will only be  triggered when some condition fulfilled.
 *
 *  The purpose of building this class is to return ""one sample""   for every time we new a SampleListener.
 *  Therefore, it will only make one sample and then it will not work anymore.
 *  If you want to grab another sample, remove the current listener and make a new one
 *
 *  The usage of this class is like this:
 *
 *         sampleListener.reset();
            sampleListener.onFocusGained(controller);

        while(true){
                if (sampleListener.checkFinish() == true){
                        if(sampleListener.checkValid() == true) {
                            oneSample = sampleListener.returnOneSample()
                            // DO  SOMETHING, Most Likely store sign
                            break;
                         } else {
                                System.out.println("The recording is invalid");
                                break;
                        }
                }
                Thread.currentThread().sleep(10);   // The current thread is too fast, will fail to trace Listener if missing this code
         }
        sampleListener.onFocusLost(controller);
 *
 */

import com.leapmotion.leap.*;

import java.util.ArrayList;

public class SampleListener extends Listener {
	static float minRecVelocity = 45;
	static float maxRecVelocity = 300;
	static int minPoseFrames = 30;
	static ArrayList<Frame> oneSample;
	static boolean recording;
	static boolean finishRec;
	static boolean validSample;
	static boolean focus;
	static int pauseCount;
	static int minPauseCount = 10;


	public SampleListener() {
		oneSample = new ArrayList<Frame>();
		recording = false;
		finishRec = false;
		validSample = false;
		focus = false;
		pauseCount = 0;
	}

	public void reset() {
		oneSample = new ArrayList<Frame>();
		recording = false;
		finishRec = false;
		validSample = false;
		focus = false;
		pauseCount = 0;
	}

	/**
	 * Called when the Controller object connects to the Leap Motion software
	 * and the Leap Motion hardware device is plugged in, or when this Listener
	 * object is added to a Controller that is already connected.
	 * 
	 * @param controller
	 */
	public void onConnect(Controller controller) {
		System.out.println("Connected");
	}

	public void onDisconnect(Controller controller) {
		// Note: not dispatched when running in a debugger.
		System.out.println("Disconnected");
	}

	public void onDeviceChange(Controller controller) {
		System.out.println("Device changed");
	}

	public void onExit(Controller controller) {
		System.out.println("Exited");
		System.out.println("Cation!!!! Please check the connection between your computer and Leap Motion Controller!!!");
		System.out.println("Please check if the LMC device connect properly and re-execute this program again!");
	}

	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}

	/**
	 * Called when the Leap Motion daemon/service connects to your application
	 * Controller.
	 * 
	 * @param controller
	 */
	public void onServiceConnect(Controller controller) {
		System.out.println("Service connected");
	}

	/**
	 * Called when the Leap Motion daemon/service disconnects to your
	 * application Controller.
	 * 
	 * @param controller
	 */
	public void onServiceDisconnect(Controller controller) {
		System.out.println("Service connection lost");
	}

	/**
	 * **** This function is very important!!!!!*** Called when this application
	 * becomes the foreground application. Only the foreground application
	 * receives tracking data from the Leap Motion Controller. This function is
	 * only called when the controller object is in a connected state. *****
	 * That means, call this function will enable to grab data
	 * 
	 * @param controller
	 */
	public void onFocusGained(Controller controller) {
		System.out.println("Focus gained");
	}

	public void gainFocus(){
		this.focus = true;
		System.out.println("gained");
	}

	/**
	 * **** Another important function, it disable the listener. You can enable
	 * it by can onFocusGained ****
	 * 
	 * @param controller
	 */

	public void onFocusLost(Controller controller) {
		System.out.println("Focus lost");
	}

	public void lostFocus(){
		System.out.println("lost");
		this.focus = false;
	}
	/**
	 * Called when a ***new frame of hand and finger tracking data is
	 * available****. Access the new frame data using the Controller::frame()
	 * function. !!!!! Be Careful, this function will call repeatedly, Just like
	 * while loop
	 * 
	 * @param controller
	 */
	public void onFrame(Controller controller) {
		Frame frame = controller.frame();

		// This part of function will check whether the frame is recordable. It
		// start to record when recordable frame = true;
		// When the user finish, it count 10 more frame
		// which are not able to record ( stop / too fast)
		// That means users stop for a while.
		// The benefit of doing this is to enable short pausing during the
		// change of velocity
		// e.g. from + 100 to -100 will have 0 in between, previous method can't
		// record this

		if (focus == true && finishRec == false) {
			if (recordableFrame(frame, minRecVelocity, maxRecVelocity) == true) {
				pauseCount = 0;
				recording = true;
				recordFrame(oneSample, frame);
			} else if (recording == true) {
				pauseCount++;
				if (pauseCount > minPauseCount) {
					System.out.println("Recording Finish");
					System.out.println("Sample Size" + oneSample.size());
					if (oneSample.size() >= minPoseFrames) {
						finishRec = true;
						validSample = true;
						System.out.println("Yes it finished");

						// Important!!!
						// This function will do nothing after finish recording
						// It's time to return oneSample

					} else if (oneSample.size() < minPoseFrames) {
						finishRec = true;
						validSample = false;
						System.out.println("Fail to Record, " + "Time recorded is Too Short!!!");

					}
				}
			}
		}
	}

	/**
	 * Return if the hand velocity is within the range or not
	 */
	public static boolean recordableFrame(Frame frame, float min, float max) {
		HandList hands = frame.hands();
		boolean recordable = false;
		/*
		 * First check the palmVelocity of each hand to see if it is moving Then
		 * check each finger of each hand to see if it is moving
		 */
		for (int i = 0; i < hands.count(); i++) {
			Hand hand = hands.get(i);
			FingerList fingerList = hand.fingers();
			// Check the min < palmVelocity < max ?
			float PalmVelocity = pythCal(hand.palmVelocity().getX(), hand.palmVelocity().getY(),
					hand.palmVelocity().getZ());

			if (min <= PalmVelocity && PalmVelocity <= max) {
				recordable = true;
				break;
			}

			// Check the min < tipVelocity < max ?
			for (int j = 0; j < fingerList.count(); j++) {
				com.leapmotion.leap.Vector tipV = fingerList.get(j).tipVelocity();
				float TipV = pythCal(tipV.getX(), tipV.getY(), tipV.getZ());

				if (min <= TipV && TipV <= max) {
					recordable = true;
					break;
				}
			}
		}

		return recordable;
	}

	/**
	 * Applies the Pyth. Theorem on the 3D dimention.
	 */
	public static float pythCal(float X, float Y, float Z) {
		return (float) Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2) + Math.pow(Z, 2));
	}

	public static void recordFrame(ArrayList<Frame> oneSample, Frame frame) {
		oneSample.add(frame);
		return;
	}

	public static boolean checkFinish() {
		return finishRec;
	}

	public static boolean checkValid() {
		return validSample;
	}

	public static ArrayList<Frame> returnOneSample() {
		return oneSample;
	}
}
