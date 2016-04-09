package main;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import data.Coordinate;
import data.FingerData;
import data.OneFrame;
import data.PalmData;

import java.util.ArrayList;
import java.util.HashMap;
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
		sampleListener.setReady(false);
		// Add listener, grab data
		controller.addListener(sampleListener);
		DTW dtw = new DTW();

		while (true) {
			if (controller.isConnected() == true) {
				while (true) {
					sampleListener.lostFocus();
					System.out.println("Please enter your choice\n " + "1. Record new sign\n "
							+ "2. Train your translator\n " + "3. Print all sign\n " + "4. DTW\n " + "5. Remove all Sign(Debug)\n " +
							"6. Delete one sign\n");
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
							sampleListener.setReady(true);

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
						sampleListener.setReady(true);
						boolean getSample = false;
						Sample rSample=null;

						while (true) {
							if (sampleListener.checkFinish()) {
								if (sampleListener.checkValid()) {

										rSample = new Sample(sampleListener.returnOneSample());
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
						sampleListener.setReady(false);

						if(getSample == false){
							break;
						}

						dtw.setRSample(rSample);//TODO: if rSample is created by default constructor, error may occurs

						final int tolerance=1;

						HashMap<String, Sign> signByBoth = db.getSignsByBoth(rSample.getInitialFingerCount(), rSample.getInitialHandType(),tolerance);
						/*
						for (Sign storedSign : signByBoth.values()) {
							dtw.setStoredSign(storedSign);
							dtw.calDTW();
						}
						*/

						for(Sign storedSign : allSigns.getAllSigns().values()){
							//System.out.println("Checking : " + storedSign.getName());
							dtw.setStoredSign(storedSign);
							dtw.calDTW();
						}

						dtw.printResult();
						dtw.reset();
						break;

					case 5:
						/*
						db.removeAllSign();
						allSigns.removeAllSign();
						*/
						System.out.println("This function has been disabled to protect data. Enable it yourself if necessary!");
						break;

						case 6:
							System.out.println("Please enter the name of the sign you want to Delete: ");
							String delName = sc.next();
							if(db.getSignsByName(delName) != null){
								if(db.removeSign(db.getSignsByName(delName))){
									System.out.println("Remove " + delName + " successfully!");
								}
							}

							break;
						case 7:
							System.out.println("Please enter the gesture name you want to change:");
							String checkName = sc.next();
							if(db.getSignsByName(checkName) != null){
								Sign tempSign = db.getSignsByName(checkName);
								System.out.println("Please enter the new name:");
								String newName = sc.next();
								tempSign.setName(newName);

								db.removeSign(db.getSignsByName(checkName));
								allSigns.getSign(checkName).setName(newName);
								db.addSign(tempSign);
								System.out.println("Change Sign name successfully!");
							}
							break;
						case 8://database testing area
							Sample sample=db.getFirstSample("two");
							ArrayList<OneFrame> replayGesture = sample.getAllFrames();
							for (OneFrame t:sample.getAllFrames()) {
								PalmData palms = t.getPalmData();
								FingerData finger = t.getFingerData();
								for (Coordinate c: palms.getCoordinates()) {
									System.out.println("palm:");
									System.out.println("X:" +c.getX() +"Y:" +c.getY()+"Z:" +c.getZ());
								}
								for (Coordinate c: finger.getDistal()) {
									System.out.println("distal:");
									System.out.println("X:" +c.getX() +"Y:" +c.getY()+"Z:" +c.getZ());
								}
								for (Coordinate c: finger.getCoordinates()) {
									System.out.println("finger:");
									System.out.println("X:" +c.getX() +"Y:" +c.getY()+"Z:" +c.getZ());
								}
								System.out.println(sample.getInitialHandType());
								Thread.sleep(100);
							}
							System.out.println(sample.getInitialHandType());
							break;


					default:
						System.out.println("Please enter a valid option");
						sampleListener.lostFocus();
						sampleListener.setReady(false);
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

	}
	

	public static void printTraining() {
		System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");

		for (String key : allSigns.getAllSigns().keySet()) {
			System.out.println("Sign Name:  " + key + "   , Consist of "
					+ allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
		}

		System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");
	}

	/**
	 * Print out the basic info of the Sign stored in the trainer.
	 */
	public static void printAllDetails() {

		for (String key : allSigns.getAllSigns().keySet()) {
			System.out.println("Sign Name   : " + key + " ,   Consist of "
					+ allSigns.getAllSigns().get(key).getAllSamples().size() + " Sample");
			System.out.println("Initial Palm Count  : " + allSigns.getAllSigns().get(key).getInitialPalmCount());
			System.out.println("Initial Hand Type   :" + allSigns.getAllSigns().get(key).getInitialHandType());
			System.out.println("Initial Finger Count = " + allSigns.getAllSigns().get(key).getInitialFingerCount() + "\n");
		}
		System.out.println("There are " + allSigns.getAllSigns().size() + " sign in database:\n");
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

