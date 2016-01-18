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

    public static double globalThreshold = Double.POSITIVE_INFINITY; // The maximum distance between rSample and stored sample which can be recognize
                                    // If the bestMatch > globalThreshold, then unknown gesture

    public double localThreshold = Double.POSITIVE_INFINITY;   // The distance between sample and one of the stored sample

    public double adjust = 100;
    public double palmTolerance = 100;      // The maximum distance of palm distance between rSample and storedSample can be accepted

    public String result = "GestureNotExist";


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

            int rSize = rSample.allFingers.coordinateSeq.size();
            int storedSize = storedSample.allFingers.coordinateSeq.size();
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
                        costTab[i][j] = calDist(rSample, i - 1, storedSample, j - 1);
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
     * @param rPosition         = the  frame number in r sample
     * @param storedPosition = the frame number in storedSample
     *
     *                       This function focus on   """   one  """"     frame, calculate the distance between the finger of storedSample and  rSample
     *
     *
     * @return
     */
    public double calDist(Sample rSample, int rPosition, Sample storedSample, int storedPosition){

        double distance = Double.POSITIVE_INFINITY;
        if (rSample.allPalms.coordinateSeq.get(rPosition).size() != storedSample.allPalms.coordinateSeq.get(storedPosition).size()){
            distance = Double.POSITIVE_INFINITY;
            return distance;

        }else if(rSample.allFingers.coordinateSeq.get(rPosition).size() != storedSample.allFingers.coordinateSeq.get(storedPosition).size()){
            distance = Double.POSITIVE_INFINITY;
            return distance;

        }   else{

            ArrayList<Coordinate> rFingerList = rSample.allFingers.coordinateSeq.get(rPosition);
            ArrayList<Coordinate> storedFingerList = storedSample.allFingers.coordinateSeq.get(storedPosition);
            ArrayList<Coordinate> rPalmList  = rSample.allPalms.coordinateSeq.get(rPosition);
            ArrayList<Coordinate> storedPalmList = storedSample.allPalms.coordinateSeq.get(storedPosition);
            double fingerDistance = 0;
            double palmDistance = 0;

            for(int i = 0; i < rSample.allFingers.coordinateSeq.get(rPosition).size(); i++){

                int handNumber = i/5;   // from 0 to 4 will give 0, from 5 to 9 give 1
                // Hand number indicate the finger is left hand finger or right hand finger
                // Then we can get the normalize coordinate by FingerCoor - PalmCoor
                fingerDistance = fingerDistance + fingerDist(rFingerList.get(i), rPalmList.get(handNumber), storedFingerList.get(i),storedPalmList.get(handNumber));
                //Calculate Finger by Finger, remember to put the correct handNumber
            }

            for(int j = 0; j < rPalmList.size(); j++){
                Coordinate rPalmOrigin = rSample.allPalms.coordinateSeq.get(0).get(j);  // Get the "Frame 0" Palm
                Coordinate storedPalmOrigin = storedSample.allPalms.coordinateSeq.get(0).get(j);
                palmDistance = palmDistance + palmDist(rPalmList.get(j), rPalmOrigin,storedPalmList.get(j), storedPalmOrigin);
            }

            if(palmDistance > palmTolerance*rPalmList.size()){
                palmDistance = Double.POSITIVE_INFINITY;
                // If the difference of palm between rSample and StoredSample > palmTolerance, we don't consider it
            }

            distance = fingerDistance   +   (Math.pow(palmDistance, 2)/Math.pow(adjust, 2))* palmDistance;
            System.out.println("Finger part = " + fingerDistance);
            System.out.println("Palm part = " + (Math.pow(palmDistance,2)/Math.pow(adjust,2))*palmDistance);
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
        System.out.println("------------Result of DTW-----------");
        System.out.println("The most similar gesture is -- " + result);
        System.out.println("The minimum cost of DTW is " + bestMatch);
    }

    public void reset(){
        bestMatch = Double.POSITIVE_INFINITY;
        result = "GestureNotExist";
    }

    public void run(){}

}

