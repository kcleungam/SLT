import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;

import java.util.ArrayList;
import java.util.Scanner;

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
	private static Database db = new Database("Signs", "HK_Signs");

	// TODO: What's the use of recordingMode? - by Jacky
	static boolean recordingMode = false;
	static boolean recording = false;

	// static int recordedFrames = 0;

	static SignBank allSigns;

	public static void main(String args[]) throws Exception {
		allSigns = new SignBank(db);

		// New Controller for the Leap
		Controller controller = new Controller();
		Scanner sc = new Scanner(System.in);
		// New a Listener everytime
		SampleListener sampleListener = new SampleListener();
		sampleListener.lostFocus();
		// Add listener, grab data
		controller.addListener(sampleListener);
		while (true) {
			if (controller.isConnected() == true) {
				while (true) {
					sampleListener.lostFocus();
					System.out.println("Please enter your choice/n " + "1. Record new sign/n "
							+ "2. Train your translator/n " + "3. Print all sign");
					int i = sc.nextInt();
					boolean inputValid = true;
					// Sign sign = new Sign();
					Sign sign;
					switch (i) {
					case 1:
						// construct new sign and add the sample
						System.out.println("Please enter the sign name: ");
						String signName = sc.next();
						if (allSigns.getAllSigns().containsKey(signName)) {
							System.out.println("The name existed in the database, exiting to menu... ");
						} else {
							if (addSample(sampleListener, sign, signName)) {
								System.out.println("Sample added, exiting to menu... ");
							} else {
								System.out.println("Add sample failed, exiting to menu... ");
							}
						}

						break;

					case 2:
						// Train the Listener, add more samples
						printTraining();
						System.out.println("Please enter the name of the sign you want to train: ");
						String trainName = sc.next();
						if (allSigns.getAllSigns().containsKey(trainName)) {
							System.out.println("Sign found, ready to start training");
							if (addSample(sampleListener, sign, trainName)) {
								System.out.println("Sample added, exiting to menu... ");
							} else {
								System.out.println("Add sample failed, exiting to menu... ");
							}
						} else { // sign not found
							System.out.println("Sign not found!");
							inputValid = false;
							// recordingMode = false;
							break;
						}

						break;
					case 3:
						printAllDetails();
						sampleListener.lostFocus();
						break;
					default:
						System.out.println("Please enter a valid option");
						sampleListener.lostFocus();
						inputValid = false;
						break;
					}
					if (inputValid == false) {
						// go back and ask again if the input is not valid
						sign = null;
						continue;
					}
				}
			}
		}
	}

	/**
	 * Record the sample and store it into the database according the signName
	 */
	public static boolean addSample(SampleListener listener, Sign sign, String signName) {
		ready();

		sampleListener.reset();
		sampleListener.gainFocus();

		// keep grabbing the frame from controller
		while (true) {
			// check recordable of frame
			if (sampleListener.checkFinish()) {
				if (sampleListener.checkValid()) {
					if (savePrompt()) {
						// the following function handles the addSample and
						// newSample cases itself
						// ToDo: handle the boolean return type after adding
						// sample to the given sign
						// what you want to do with the boolean return? - Jacky
						sign = new Sign(signName, new Sample(sampleListener.returnOneSample()));
						allSigns.addSign(trainName, sign);
						db.addSign(sign);
					}
				} else {
					System.out.println("The recording is invalid");
					return false;
				}
				break;
			}
			// It is necessary, without it, bug occur
			Thread.currentThread().sleep(10);
		}
		// controller.removeListener(trainListener);
		sampleListener.lostFocus();
		return true;
		/*
		 * while (true) { Frame frame = controller.frame(); if (recordingMode ==
		 * true) { trainOneSign(frame,oneSample,trainName,allsign);
		 * //recordingMode = false at the end of trainOneSign } else { break; }
		 * }
		 */
	}

	// This function should be not used now? - Jacky
	public static void trainOneSign(ArrayList<Frame> oneSample, String signName, SignBank allSigns) throws Exception {
		allSigns.getSign(signName).addSample(new Sample(oneSample));
		/*
		 * if (recordableFrame(frame, minRecVelocity, maxRecVelocity) == true) {
		 * recording = true; recordFrame(oneSample, frame); } else if
		 * (recordableFrame(frame, minRecVelocity, maxRecVelocity) == false &&
		 * recording == true) { ///////////////recording finish if
		 * (oneSample.size() >= minPoseFrames) {
		 * allsign.getSign(signName).addSample(oneSample); } recordingMode =
		 * false; //finish recording }
		 */
	}

	// function here originally moved to Sign.java,
	// Sign(String SignName, Sample sample) constructor.

	public static void printTraining() {
		System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

		for (String key : allSigns.getAllSigns().keySet()) {
			System.out.println("Sign Name:  " + key + "   , Consist of "
					+ allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
		}

		System.out.println("");
	}

	/**
	 * Print out the basic info of the Sign stored in the trainer.
	 */
	public static void printAllDetails() {
		System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

		for (String key : allSigns.getAllSigns().keySet()) {
			System.out.println("Sign Name   : " + key + " ,   Consist of "
					+ allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
			System.out.println("Hand Count  : " + allSigns.getAllSigns().get(key).getHandCount());
			System.out.println("Hand Type   :" + allSigns.getAllSigns().get(key).getHandType());
			System.out.println("Finger Count = " + allSigns.getAllSigns().get(key).getFingerCount());
		}

		System.out.println(" \n All sign printed");
	}

	/**
	 * Provide a 3-second countdown.
	 */
	public static void ready() {
		for (int count = 3; count > 0; count--) {
			try {
				System.out.println(count);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Start!");
	}

	/**
	 * Give a save prompt
	 */
	public static boolean savePrompt() {
		System.out.println("Save this Sample? (Y/N)");
		Scanner sc = new Scanner(System.in);
		String signName = new String();
		while (true) {
			signName = sc.next();
			if (signName.equals("Y")) {
				System.out.println("Done!!!");
				return true;
			} else if (signName.equals("N")) {
				System.out.println("The Sample is not added.");
				return false;
			} else {
				System.out.println("Please input Y or N.");
				System.out.println("Save this Sample? (Y/N)");
			}
		}
	}
}

/*
 *
 * Listener listener = new Listener();//Create New Listener For The Leap
 * Controller controller = new Controller();//Create New Controller For The Leap
 * Frame frame = controller.frame();
 * 
 * //Use of hand Object HandList hands = frame.hands(); // be careful about the
 * s, it means hand list Hand h1 = hands.get(0); Hand h2 = frame.hand(1);
 * //another way to get specific hand com.leapmotion.leap.Vector palmV =
 * frame.hand(1).palmVelocity(); com.leapmotion.leap.Vector palmN =
 * frame.hand(1).palmNormal();
 * 
 * //Use of finger Object Finger finger = frame.fingers().get(arg0); FingerList
 * allFingers = frame.fingers(); //must use com.leapmotion.leap.Vector as it has
 * conflict with Vector in java com.leapmotion.leap.Vector tipV =
 * finger.tipVelocity(); com.leapmotion.leap.Vector tipP = finger.tipPosition();
 * //this finger is an object to store tracked finger com.leapmotion.leap.Vector
 * tipV1 = frame.finger(1).tipVelocity(); //specific one finger in a frame
 * 
 * //arms Arm arm = hand.arm(); com.leapmotion.leap.Vector wrist =
 * arm.wristPosition(); com.leapmotion.leap.Vector direction = arm.direction();
 * 
 * //time and ID float framePeriod = frame.timestamp() -
 * controller.frame(1).timestamp(); //timestamp of frames long currentID =
 * controller.frame().id(); //calls the ID of the frame
 * 
 * //vector operation com.leapmotion.leap.Vector normalizedVector =
 * otherVector.normalized(); com.leapmotion.leap.Vector sum =
 * thisVector.plus(thatVector); com.leapmotion.leap.Vector difference =
 * thisVector.minus(thatVector); float length = thisVector.magnitude();
 * //magnitude of vector float lengthSquared = thisVector.magnitudeSquared();
 * float x = thisVector.get(0); // 1 = y-coor; 2 = z-coor
 * com.leapmotion.leap.Vector crossProduct = thisVector.cross(thatVector); float
 * angleInRadians = Vector.xAxis().angleTo(Vector.yAxis()); // angleInRadians =
 * PI/2 (90 degrees)
 * 
 * //Matrix //
 * https://developer.leapmotion.com/documentation/java/api/Leap.Matrix.html#
 * javaclasscom_1_1leapmotion_1_1leap_1_1_matrix Matrix M = new Matrix();
 * com.leapmotion.leap.Vector xBasis = new com.leapmotion.leap.Vector(23, 0, 0);
 * com.leapmotion.leap.Vector yBasis = new com.leapmotion.leap.Vector(0, 12, 0);
 * com.leapmotion.leap. Vector zBasis = new com.leapmotion.leap.Vector(0, 0,
 * 45); Matrix transformMatrix = new Matrix(xBasis, yBasis, zBasis);
 * com.leapmotion.leap.Vector thisTranslation = transformMatrix.getOrigin();
 *
 */
