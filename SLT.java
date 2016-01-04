import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;

import java.util.ArrayList;
import java.util.Scanner;



public class SLT {
	private static Database db = new Database("Signs","HK_Signs");

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
					System.out.println("Please enter your choice\n " + "1. Record new sign\n "
							+ "2. Train your translator\n " + "3. Print all sign\n" + "4. DTW\n" + "5. Remove all Sign(Debug)\n");
					int i = sc.nextInt();
					boolean inputValid = true;
					//Sign sign = new Sign();
					Sign sign;
					switch (i) {
					case 1:
						// recordingMode = true;
						/*
						 * keep grabbing the frame from controller, check
						 * whether it is recordable, add to oneSample if it is.
						 * """store as new Sign"""
						 */
						System.out.println("Please enter the gesture name: ");
						String signName = sc.next();
						if (allSigns.getAllSigns().containsKey(signName)) {
							System.out.println("The name existed in the database, exiting to menu... ");
						} else {
							ready();
							sampleListener.reset();
							sampleListener.gainFocus();

							while (true) {
								if (sampleListener.checkFinish()) {
									if (sampleListener.checkValid()) {
										if (savePrompt()) {
											//recordSign(sampleListener.returnOneSample(), signName, sign, allsign);
											sign=new Sign(signName,new Sample(sampleListener.returnOneSample()));
											//ToDo: handle the boolean return value aftter adding the given sign
											allSigns.addSign(signName,sign);
											db.addSign(sign);
										}
									} else {
										System.out.println("The recording is invalid. ");
									}
									break;
								}
								// The current thread is too fast, will fail to
								// trace Listener if missing this code
								Thread.currentThread().sleep(10);
							}
							sampleListener.lostFocus();
						}
						/*
						 * while (true) { Frame frame = controller.frame();
						 * System.out.println("The frame is valid?" +
						 * frame.hand(0).palmVelocity()); if (recordingMode ==
						 * true) { //recordSign(frame, oneSample,signName,
						 * sign,allsign); //recordingMode = false at the end of
						 * recordSign } else { break; } }
						 */

						break;

					case 2:
						// Train the Listener, add more samples
						printTraining();
						System.out.println("Please enter the name of the sign you want to train: ");
						String trainName = sc.next();
						if (allSigns.getAllSigns().containsKey(trainName)) {
							System.out.println("Sign found, ready to start training");
							// recordingMode = true;
							ready();

							// SampleListener trainListener = new
							// SampleListener(); //new a listener everytime
							// controller.addListener(trainListener); // add
							// listener, grab data
							sampleListener.reset();
							sampleListener.gainFocus();
							/*
							 * keep grabbing the frame from controller, check
							 * whether it is recordable, add to the oneSample if
							 * it is. """"put it as one sample of specific
							 * sign"""
							 */
							while (true) {
								if (sampleListener.checkFinish()) {
									if (sampleListener.checkValid()) {
										if (savePrompt()) {
											//trainOneSign(sampleListener.returnOneSample(), trainName, allsign);
											//ToDo: handle the boolean return type after adding sample to the given sign
											sign=new Sign(trainName,new Sample(sampleListener.returnOneSample()));
											allSigns.addSign(trainName,sign);
											db.addSign(sign);
										}
									} else {
										System.out.println("The recording is invalid");
									}
									break;
								}
								// It is necessary, without it, bug occur
								Thread.currentThread().sleep(10);
							}
							// controller.removeListener(trainListener);
							sampleListener.lostFocus();
							/*
							 * while (true) { Frame frame = controller.frame();
							 * if (recordingMode == true) {
							 * trainOneSign(frame,oneSample,trainName,allsign);
							 * //recordingMode = false at the end of
							 * trainOneSign } else { break; } }
							 */
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

					case 4:
						System.out.println("--------------------DTW----------------------");
						ready();
						sampleListener.reset();
						sampleListener.gainFocus();
						boolean getSample = false;
						Sample rSample = new Sample();
						while (true) {
							if (sampleListener.checkFinish()) {
								if (sampleListener.checkValid()) {

										rSample.set(sampleListener.returnOneSample());
										getSample = true;

								} else {
									System.out.println("The recording is invalid. ");
								}
								break;
							}
							// The current thread is too fast, will fail to
							// trace Listener if missing this code
							Thread.currentThread().sleep(10);
						}
						sampleListener.lostFocus();

						if(getSample == false){
							break;
						}
						DTW dtw = new DTW(rSample);
						/*
						for(Sign storedSign : allSigns.getAllSigns().values()){
							dtw.setStoredSign(storedSign);
							dtw.calDTW();
						}
						dtw.printResult();
						*/

					case 5:
						db.removeAllSign();
						allSigns.removeAllSign();
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

	/**
	 * Records the attributes of the new Sign and the Sample frame
	 */
	/*
	public static void recordSign(ArrayList<Frame> oneSample, String signName, Sign sign, SignBank allsign) throws Exception {
		sign.addSample(new Sample(oneSample));
		sign.setName(signName);
		sign.setHandCount(oneSample.get(0).hands().count());
		int fingerCount = 0;
		if (oneSample.get(0).hands().count() > 1) {
			sign.setHandType(bothHand);
			for (Hand hand : oneSample.get(0).hands()) {
				for (Finger finger : hand.fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			}
		} else if (oneSample.get(0).hands().count() == 1) {
			if (oneSample.get(0).hands().get(0).isLeft()) {
				sign.setHandType(leftHand);
				// doesn't work when hands.get(0) change to hand(0)
				for (Finger finger : oneSample.get(0).hands().get(0).fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			} else if (oneSample.get(0).hands().get(0).isRight()) {
				sign.setHandType(rightHand);
				// doesn't work when hands.get(0) change to hand(0)
				for (Finger finger : oneSample.get(0).hands().get(0).fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			}

			 // I find that hands.get(0) != hand(0). That is because hand(0) will
			 // get the hand with ID = 0, which is hard to track; But
			 //hands.get(0) will get the first hand in the current list

		}

		sign.setFingerCount(fingerCount);
		allsign.addSign(sign.getName(),sign);

	}*/

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
			System.out.println("Finger Count = " + allSigns.getAllSigns().get(key).getFingerCount() + "\n");

			System.out.println("!!!!!--------For debug----------!!!!!");
			for(Sign a : allSigns.getAllSigns().values()){
				System.out.println("AAA");
				for(Sample s : a.getAllSamples()){
					System.out.println("SSSSS");
					for(Frame f : s.getAllFrames()){
						System.out.println("FFFFFFF");
						System.out.println(f.id());
					}
				}
			}

		}

		System.out.println("All sign are printed");
	}

	/**
	 * Provide a 3-second countdown.
	 */
	public static void ready() {
		for (int count = 3; count >= 0; count--) {
			try {
				System.out.println(count);
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Give a save prompt
	 */
	public static boolean savePrompt() {
		System.out.println("Save this Sample? (Y/N)");
		Scanner sc = new Scanner(System.in);//added
		String signName=new String();//added
		while(true){//while (signName = sc.next()) {
			signName=sc.next();//added
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
