import java.util.ArrayList;

/**
 * Created by Krauser on 21/12/2015.
 */
public class DTW{
    public static double bestMatch = Double.POSITIVE_INFINITY;
    public static Sample rSample;       // Sample for recognize
    public static Sign storedSign; // Sample from database
    public static int maxSlope = 3;    // the maximum number of slope
    public static int minFrame = 30;
    public static int cutFrame = 10;   // The number of frame can be cut off on order to find the best ending of Sample

    public static double globalThreshold = 450; // The maximum distance between rSample and stored sample which can be recognize
                                    // If the bestMatch > globalThreshold, then unknown gesture

    public double localThreshold = Double.POSITIVE_INFINITY;   // The distance between sample and one of the stored sample

    public double adjust = 60;     // The adjustment according to the palm, 60 = 6 cm
    public double punishment = 200; // The number which add to distance if the number of hands are different

    public String result = "Unknown Gesture !";


    public DTW(){
        this.rSample = null;
        this.storedSign = null;
    }

    public DTW(Sample rSample) throws Exception{
        if(rSample==null)
            throw new Exception();
        this.rSample = rSample;
    }

    public DTW(Sample rSample, Sign storedSign) throws Exception{
        if(rSample==null||storedSign==null) throw new Exception();
        this.rSample = rSample;
        this.storedSign = storedSign;
    }

    public void setRSample(Sample rSample){ this.rSample = rSample; }
    public void setStoredSign(Sign storedSign){
        this.storedSign = storedSign;
    }

    /**
     *
     *      costTab : a table store the distance between each frame
     *      accuTab : a table store the accumulate distance in DTW
     *      stepCount : a table store the step taken of every entry of accuTab
     *
     *   When you try to understand this  function, you have to study DTW first
     *   Then, You have to draw three table-----   tab, slopeI, slopeJ
     *   Follow the code and draw it step by step, then you can understand it
     *
     */
    public void calDTW(){

        for(Sample storedSample: storedSign.getAllSamples()) {

            int rSize = rSample.allFrame.size();
            int storedSize = storedSample.allFrame.size();
            // +1 is for the last frame as you need extra 1 more space to compare the last
            double[][] costTab = new double[rSize + 1][storedSize + 1];
            double[][] accuTab = new double[rSize + 1][storedSize + 1];
            double[][] stepCount = new double[rSize + 1][storedSize +1];
            int[][] slopeI = new int[rSize + 1][storedSize + 1];
            int[][] slopeJ = new int[rSize + 1][storedSize + 1];


            for (int i = 0; i < rSize + 1; i++) {
                for (int j = 0; j < storedSize + 1; j++) {
                    accuTab[i][j] = Double.POSITIVE_INFINITY;
                    stepCount[i][j] = Double.POSITIVE_INFINITY;
                    slopeI[i][j] = 0;
                    slopeJ[i][j] = 0;

                }
            }

            for(int i = 1; i < rSize + 1; i++){
                for(int j = 1; j < storedSize + 1; j++) {
                        costTab[i][j] = calDist(rSample.allFrame.get( i - 1 ), storedSample.allFrame.get( j - 1 ), rSample, storedSample );
                }
            }

            costTab[0][0] = 0;
            accuTab[0][0] = 0;  // We don't compare the first frame of rSample and storedSample
            stepCount[0][0] = 0;

            // I will try to do things at once
            for (int i = 1; i < rSize + 1; i++) {
                for (int j = 1; j < storedSize + 1; j++) {
                    if (accuTab[i][j - 1] < accuTab[i - 1][j - 1] && accuTab[i][j - 1] < accuTab[i - 1][j] && slopeI[i][j - 1] < maxSlope) {

                        accuTab[i][j] =  accuTab[i][ j - 1 ] + costTab[i][j];
                        stepCount[i][j] = stepCount[i][j-1] + 1;
                        slopeI[i][j] = slopeI[i][j - 1] + 1;
                        slopeJ[i][j] = 0;
                        //slope is the things that limit the repeated step in DTW

                    } else if (accuTab[i - 1][j] < accuTab[i - 1][j - 1] && accuTab[i - 1][j] < accuTab[i][j - 1] && slopeJ[i - 1][j] < maxSlope) {

                        accuTab[i][j] = accuTab[i-1][j] + costTab[i][j] ;
                        stepCount[i][j] = stepCount[i - 1][j] + 1;
                        slopeI[i][j] = 0;
                        slopeJ[i][j] = slopeJ[i - 1][j] + 1;

                    }else {

                        accuTab[i][j] = accuTab[i-1][j-1] + costTab[i][j];
                        stepCount[i][j] = stepCount[i - 1][j - 1] + 1;
                        slopeI[i][j] = 0;
                        slopeJ[i][j] = 0;

                    }
                }
            }

            // this function dynamically find the best ending of the sample for recognition
            // imagine that it just like storedSample unchanged but cutting the frame of rSample

            localThreshold = Double.POSITIVE_INFINITY;
            for (int i = 0; i < cutFrame; i++) {
                for(int j = 0; j < cutFrame ; j++){
                    if(stepCount[rSize - i][storedSize - j] == Double.POSITIVE_INFINITY){
                        System.out.println("Should not happen");
                        continue;           // it will occur if DTW can not reach the end, that means the difference of number of frames are too large
                    }
                    if (accuTab[rSize - i][storedSize - j] / stepCount[rSize - i][storedSize - j] < localThreshold) {
                        localThreshold = accuTab[rSize - i][storedSize - j] / stepCount[rSize - i][storedSize - j];
                    }
                }
            }
            if(localThreshold < bestMatch){
                bestMatch = localThreshold;
                result = storedSign.getName();
            }

        }

        if(bestMatch > globalThreshold){
            result = "Unknown Gesture !";
        }

    }

    public double getCost(){
        return bestMatch;
    }

    /**
     *
     *          Distance = ( rFinger - storedFinger)^2  + (rPalm - storedPalm)
     *          The reason why finger use square but palm not is to make distance between finger become dominant
     *
     *
     * @param rFrame         = the  frame in r sample
     * @param storedFrame = the frame in storedSample
     *
     *                       This function focus on   """   one  """"     frame, calculate the distance between the finger of storedSample and  rSample
     *
     *
     * @return
     */
    public double calDist(Sample.OneFrame rFrame, Sample.OneFrame storedFrame, Sample rSample, Sample storedSample){

        ArrayList<Coordinate> rFingerList = rFrame.fingerData.coordinates;
        ArrayList<Coordinate> storedFingerList = storedFrame.fingerData.coordinates;
        ArrayList<Coordinate> rPalmList  = rFrame.palmData.coordinates;
        ArrayList<Coordinate> storedPalmList = storedFrame.palmData.coordinates;
        double fingerDistance = 0;
        double palmDistance = 0;

        double distance = Double.POSITIVE_INFINITY;


        if (rFrame.palmData.count == storedFrame.palmData.count){

            for(int i = 0; i < rFingerList.size(); i++){

                int handNumber = i/5;   // from 0 to 4 will give 0, from 5 to 9 give 1
                // Hand number indicate the finger is left hand finger or right hand finger
                // Then we can get the normalize coordinate by FingerCoor - PalmCoor
                fingerDistance = fingerDistance + fingerDist(rFingerList.get(i), rPalmList.get(handNumber), storedFingerList.get(i),storedPalmList.get(handNumber));
                //Calculate Finger by Finger, remember to put the correct handNumber
            }

            for(int j = 0; j < rPalmList.size(); j++){
                Coordinate rPalmOrigin = rSample.allFrame.get(0).palmData.coordinates.get(j);  // Get the "Frame 0" Palm
                Coordinate storedPalmOrigin = storedSample.allFrame.get(0).palmData.coordinates.get(j);
                palmDistance = palmDistance + palmDist(rPalmList.get(j), rPalmOrigin,storedPalmList.get(j), storedPalmOrigin);
            }


            distance = fingerDistance   +   (Math.pow(palmDistance, 2)/Math.pow(adjust, 2))* palmDistance;
            System.out.println("Finger part = " + fingerDistance);
            System.out.println("Palm part = " + (Math.pow(palmDistance,2)/Math.pow(adjust,2))*palmDistance);


        }else{          //TODO   Different in hand number

            int rHandNumber = 0;
            int storedHandNumber = 0;
            int rFingerNumber = 0;
            int storedFingerNumber = 0;

            if(rFrame.palmData.count == 1 && storedFrame.palmData.count ==2){

                for(int i = 0; i < rFrame.fingerData.count; i++){        // suppose to be 5
                    if(rFrame.handType ==HandType.LEFT){
                        rHandNumber = 0;
                        storedHandNumber = 0;
                        rFingerNumber = 0;
                        storedFingerNumber = 0;
                    }else{                          // Right hand case, then take right hand data of storedFrame
                        rHandNumber = 0;
                        storedHandNumber = 1;       // right hand
                        rFingerNumber = 0;
                        storedFingerNumber = 5;     // finger in right hand
                    }
                    fingerDistance = fingerDistance + fingerDist(rFingerList.get(i + rFingerNumber), rPalmList.get(rHandNumber),
                            storedFingerList.get(i + storedFingerNumber),storedPalmList.get(storedHandNumber));
                }

                for(int j = 0; j < rPalmList.size(); j++){      // rPalmList.size() suppose to be 1
                    Coordinate rPalmOrigin = rSample.allFrame.get(0).palmData.coordinates.get(j);  // Get the "Frame 0" Palm
                    Coordinate storedPalmOrigin = storedSample.allFrame.get(0).palmData.coordinates.get(j);
                    palmDistance = palmDistance + palmDist(rPalmList.get(j), rPalmOrigin,storedPalmList.get(j), storedPalmOrigin);
                }


                distance = fingerDistance   +   (Math.pow(palmDistance, 2)/Math.pow(adjust, 2))* palmDistance;
                // TODO    Punishment is the value added to distance due to different number of hand
                distance = distance + punishment;

                System.out.println("Finger part = " + fingerDistance);
                System.out.println("Palm part = " + (Math.pow(palmDistance,2)/Math.pow(adjust,2))*palmDistance);
                System.out.println("With punishment " + punishment);

            } else if(rFrame.palmData.count == 2 && storedFrame.palmData.count == 1) {

                for (int i = 0; i < storedFrame.fingerData.count; i++) {        // suppose to be 5

                    if (storedFrame.handType == HandType.LEFT) {   // Left hand case
                        rHandNumber = 0;
                        storedHandNumber = 0;
                        rFingerNumber = 0;
                        storedFingerNumber = 0;
                    } else {                          // Right hand case, then take right hand data of storedFrame
                        rHandNumber = 1;            // right hand
                        storedHandNumber = 0;
                        rFingerNumber = 5;          // finger in right hand
                        storedFingerNumber = 0;
                    }

                    fingerDistance = fingerDistance + fingerDist(rFingerList.get( i + rFingerNumber ), rPalmList.get(rHandNumber),
                            storedFingerList.get( i + storedFingerNumber ), storedPalmList.get(storedHandNumber));
                }

                for (int j = 0; j < storedPalmList.size(); j++) {      // rPalmList.size() suppose to be 1
                    Coordinate rPalmOrigin = rSample.allFrame.get(0).palmData.coordinates.get( j + rHandNumber);  // Get the "Frame 0" Palm
                    Coordinate storedPalmOrigin = storedSample.allFrame.get(0).palmData.coordinates.get(j + storedHandNumber);

                    palmDistance = palmDistance + palmDist(rPalmList.get(j + rHandNumber), rPalmOrigin
                            , storedPalmList.get( j + storedHandNumber), storedPalmOrigin);
                }


                distance = fingerDistance + (Math.pow(palmDistance, 2) / Math.pow(adjust, 2)) * palmDistance;
                // TODO    Punishment is the value added to distance due to different number of hand
                distance = distance + punishment;

                System.out.println("Finger part = " + fingerDistance);
                System.out.println("Palm part = " + (Math.pow(palmDistance, 2) / Math.pow(adjust, 2)) * palmDistance);
                System.out.println("With punishment " + punishment);
            }

            return distance;

        }

        return distance;
    }

    /**
     *
     * @param rFinger
     * @param rPalm
     * @param storedFinger
     * @param storedPalm
     *
     *          We first calculate the normalize coordinate of finger, we use relative coordinate between finger and palm
     *          ie.    Finger coordinate  -  Palm Coordinate =  normalize coordinate
     *          This can preserve the Finger movement but losing the Palm movement ,  and that's what I want
     *
     *          Then calculate the distance between normalized rFinger and normalized storedFinger
     *
     * @return
     */

    public double fingerDist(Coordinate rFinger, Coordinate rPalm, Coordinate storedFinger, Coordinate storedPalm){

        double distance = 0.0;
        // R = recognizing, X = x coordinate, N = normalize
        // Actually, it is the relative coordinate between finger and palm
        double RXN = rFinger.getX()- rPalm.getX();
        double RYN = rFinger.getY() - rPalm.getY();
        double RZN = rFinger.getZ() - rPalm.getZ();

        // S = stored
        double SXN = storedFinger.getX() - storedPalm.getX();
        double SYN = storedFinger.getY() - storedPalm.getY();
        double SZN = storedFinger.getZ() - storedPalm.getZ();


        distance =Math.sqrt(
                Math.pow(RXN - SXN, 2) +
                Math.pow(RYN - SYN, 2)+
                Math.pow(RZN - SZN, 2)
        );

        return distance;
    }

    /**
     *
     * @param rPalm
     * @param rPalmOrigin
     * @param storedPalm
     * @param storedPalmOrigin
     *
     *              We first calculate the normalized coordinate of Palm, we use relative coordinate between the Palm in " Current frame " and Palm in " Frame 0 "
     *              ie.   Current Palm coordinate - Frame 0 Palm coordinate
     *              This can preserve the movement  of Palm
     *
     *              Then calculate the distance between the normalized rPalm and normalized storedPalm
     *
     * @return
     */

    public double palmDist(Coordinate rPalm, Coordinate rPalmOrigin, Coordinate storedPalm, Coordinate storedPalmOrigin){

        double distance = 0.0;

        // R = recognizing, X = x coordinate, N = normalize
        // It is the relative coordinate between palm in current frame and frame0
        double RXN = rPalm.getX() - rPalmOrigin.getX();
        double RYN = rPalm.getY() - rPalmOrigin.getY();
        double RZN = rPalm.getZ() - rPalmOrigin.getZ();

        double SXN = storedPalm.getX() - storedPalmOrigin.getX();
        double SYN = storedPalm.getY() - storedPalmOrigin.getY();
        double SZN = storedPalm.getZ() - storedPalmOrigin.getZ();

        distance = Math.sqrt(
                Math.pow(RXN - SXN, 2) +
                        Math.pow(RYN - SYN, 2) +
                        Math.pow(RZN - SZN, 2)
        );

        return distance;
    }

    public void printResult(){
        if(result.equals("Unknown Gesture !")){
            System.out.println("Unknown Gesture !");
        }else {
            System.out.println("------------Result of DTW-----------");
            System.out.println("The most similar gesture is -- " + result);
            System.out.println("The minimum cost of DTW is " + bestMatch);
        }
    }

    public void reset(){
        bestMatch = Double.POSITIVE_INFINITY;
        result = "Unknown Gesture !";
    }

    public void run(){}

}

