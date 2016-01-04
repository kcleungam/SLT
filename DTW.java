import com.leapmotion.leap.*;

import java.util.ArrayList;

/**
 * Created by Krauser on 21/12/2015.
 */
public class DTW {
    public static double bestMatch = Double.POSITIVE_INFINITY;
    public static Sample rSample;       // Sample for recognize
    public static Sign storedSign; // Sample from database
    public static int maxSlope = 3;    // the maximum number of slope
    public static int minFrame = 30;
    public static double globalThreshold; // The maximum distance between sample and stored sample which can be recognize
                                    // If the bestMatch > globalThreshold, then unknown gesture

    public double localThreshold;   // The distance between sample and one of the stored sample

    public String result = "";


    public DTW(Sample rSample){
        this.rSample = rSample;
    }

    public DTW(Sample rSample, Sign storedSign){
        this.rSample = rSample;
        this.storedSign = storedSign;
    }

    public void setStoredSign(Sign storedSign){
        this.storedSign = storedSign;
    }

    /**
     *
     *   When you try to understand this  function, you have to study DTW first
     *   Then, You have to draw three table-----   tab, slopeI, slopeJ
     *   Follow the code and draw it step by step, then you can understand it
     *
     */
    public void calDTW(){
        System.out.println("--------In---------");
        ArrayList<Frame> rAllFrame = rSample.getAllFrames();
        Frame rOrigin = rAllFrame.get(0);
        for(Sample storedSample: storedSign.getAllSamples()) {
            System.out.println("Looping");
            ArrayList<Frame> storedAllFrame = storedSample.getAllFrames();
            Frame storedOrigin = storedAllFrame.get(0);

            int rSize = rAllFrame.size();
            int storedSize = storedAllFrame.size();
            // +1 is for the last frame as you need extra 1 more space to compare the last
            double[][] tab = new double[rSize + 1][storedSize + 1];
            int[][] slopeI = new int[rSize + 1][storedSize + 1];
            int[][] slopeJ = new int[rSize + 1][storedSize + 1];
            for (int i = 0; i < rSize + 1; i++) {
                for (int j = 0; j < storedSize + 1; j++) {
                    tab[i][j] = Double.POSITIVE_INFINITY;
                    slopeI[i][j] = 0;
                    slopeJ[i][j] = 0;
                }
            }
            System.out.println("Check point 1 ");
            tab[0][0] = 0;  // We don't compare the first frame of rSample and storedSample

            // I will try to do things at once
            for (int i = 1; i < rSize + 1; i++) {
                for (int j = 1; j < storedSize + 1; j++) {
                    System.out.println("Check point 2 ");
                    if (tab[i][j - 1] < tab[i - 1][j - 1] && tab[i][j - 1] < tab[i - 1][j] && slopeI[i][j - 1] < maxSlope) {
                        tab[i][j] = calDist(rAllFrame.get(i - 1), storedAllFrame.get(j - 1), rOrigin, storedOrigin) + tab[i][j - 1];
                        //slope is the things that limit the repeated step in DTW
                        slopeI[i][j] = slopeI[i][j - 1] + 1;
                        slopeJ[i][j] = 0;
                    } else if (tab[i - 1][j] < tab[i - 1][j - 1] && tab[i - 1][j] < tab[i][j - 1] && slopeJ[i - 1][j] < maxSlope) {
                        tab[i][j] = calDist(rAllFrame.get(i - 1), storedAllFrame.get(j - 1), rOrigin, storedOrigin) + tab[i-1][j];
                        slopeI[i][j] = 0;
                        slopeJ[i][j] = slopeJ[i - 1][j] + 1;
                    }else {
                        tab[i][j] = calDist(rAllFrame.get(i - 1), storedAllFrame.get(j - 1), rOrigin, storedOrigin) + tab[i-1][j-1];
                        slopeI[i][j] = 0;
                        slopeJ[i][j] = 0;
                    }
                    System.out.println("11111111   " + rAllFrame.get(i-1).fingers().count());
                    System.out.println("22222222   " + storedAllFrame.get(j - 1));
                }
            }

            // this function dynamically find the best ending of the sample for recognition
            // imagine that it just like storedSample unchanged but cutting the frame of rSample
            for (int i = rSize; i > minFrame; i--) {
                if (tab[i][storedSize] < localThreshold) {
                    localThreshold = tab[i][storedSize];
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
     * @param rFrame
     * @param storedFrame
     * @return
     */
    public double calDist(Frame rFrame, Frame storedFrame, Frame rOrigin, Frame storedOrigin){

        double distance = 0.0;
        if (rFrame.hands().count() != storedFrame.hands().count()){
            distance = Double.POSITIVE_INFINITY;
            return distance;

        }else if(rFrame.fingers().count() != storedFrame.fingers().count()){
            distance = Double.POSITIVE_INFINITY;
            return distance;

        }   else{
            FingerList rFingerList = rFrame.fingers();
            FingerList storedFingerList = storedFrame.fingers();
            HandList rHandList  = rFrame.hands();
            HandList storedHandList = storedFrame.hands();
            for(int i = 0; i < rFingerList.count(); i++){
                int handNumber = i/2;   // from 0 to 4 will give 0, from 5 to 9 give 1
                distance = distance + fingerDist(rFingerList.get(i),storedFingerList.get(i),
                        rHandList.get(handNumber), storedHandList.get(handNumber));

            }
            for(int j = 0; j < rHandList.count(); j++){
                distance = distance + palmDist(rHandList.get(j), storedHandList.get(j), rOrigin.hands().get(j), storedOrigin.hands().get(j));
            }
        }

        return distance;
    }

    public double fingerDist(Finger rFinger, Finger storedFinger, Hand rHand, Hand storedHand){

        double distance = 0.0;
        // R = recognizing, X = x coordinate, N = normalize
        // Actually, it is the relative coordinate between finger and palm
        double RXN = rFinger.tipPosition().getX() - rHand.palmPosition().getX();
        double RYN = rFinger.tipPosition().getY() - rHand.palmPosition().getY();
        double RZN = rFinger.tipPosition().getZ() - rHand.palmPosition().getZ();

        // S = stored
        double SXN = storedFinger.tipPosition().getX() - storedHand.palmPosition().getX();
        double SYN = storedFinger.tipPosition().getY() - storedHand.palmPosition().getY();
        double SZN = storedFinger.tipPosition().getZ() - storedHand.palmPosition().getZ();


        distance = Math.pow(RXN - SXN, 2) +
                Math.pow(RYN - SYN, 2)+
                Math.pow(RZN - SZN, 2);

        return distance;
    }

    public double palmDist(Hand rHand, Hand rHandOrigin, Hand storedHand, Hand storedHandOrigin){

        double distance = 0.0;

        // R = recognizing, X = x coordinate, N = normalize
        // It is the relative coordinate between palm in current frame and frame0
        double RXN = rHand.palmPosition().getX() - rHandOrigin.palmPosition().getX();
        double RYN = rHand.palmPosition().getY() - rHandOrigin.palmPosition().getY();
        double RZN = rHand.palmPosition().getZ() - rHandOrigin.palmPosition().getZ();

        double SXN = storedHand.palmPosition().getX() - storedHandOrigin.palmPosition().getX();
        double SYN = storedHand.palmPosition().getY() - storedHandOrigin.palmPosition().getY();
        double SZN = storedHand.palmPosition().getZ() - storedHandOrigin.palmPosition().getZ();

        distance = Math.sqrt(
                Math.pow(RXN - SXN, 2) +
                        Math.pow(RYN - SYN, 2) +
                        Math.pow(RZN - SZN, 2)
        );

        return distance;
    }

    public void printResult(){
        System.out.println("The most similar gesture is" + result);
        System.out.println("The minimum cost of DTW is " + bestMatch);
    }

    public void printInformation(){
        System.out.println(this.rSample.getAllFrames().get(0).fingers().count());
    }
}

