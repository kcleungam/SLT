import com.leapmotion.leap.Frame;

import java.util.ArrayList;

/**
 * Created by Krauser on 21/12/2015.
 */
public class DTW {
    public double bestMatch = Double.POSITIVE_INFINITY;
    public Sample rSample;       // Sample for recognize
    public Sample storedSample; // Sample from database
    public int maxSlope = 3;    // the maximum number of slope
    public int minFrame = 30;
    public double globalThreshold; // The maximum distance between sample and stored sample which can be recognize
                                    // If the bestMatch > globalThreshold, then unknown gesture

    public double localThreshold;   // The distance between sample and one of the stored sample


    public DTW(Sample rSample, Sample storedSample){
        this.rSample = rSample;
        this.storedSample = storedSample;

    }

    /**
     *
     *   When you try to understand this  function, you have to study DTW first
     *   Then, You have to draw three table-----   tab, slopeI, slopeJ
     *   Follow the code and draw it step by step, then you can understand it
     *
     */
    public void calDTW(){
        ArrayList<Frame> rAllFrame = rSample.getAllFrames();
        ArrayList<Frame> storedAllFrame = storedSample.getAllFrames();
        int rSize = rAllFrame.size();
        int storedSize = storedAllFrame.size();
        // +1 is for the last frame as you need extra 1 more space to compare the last
        double[][] tab = new double[rSize +1][storedSize +1];
        int[][] slopeI = new int[rSize +1][storedSize +1];
        int[][] slopeJ = new int[rSize +1][storedSize +1];

        for(int i = 0; i < rSize + 1; i++){
            for(int j = 0; j < storedSize +1; j++){
                tab[i][j] = Double.POSITIVE_INFINITY;
                slopeI[i][j] = 0;
                slopeJ[i][j] = 0;
            }
        }

        tab[0][0] = 0;  // We don't compare the first frame of rSample and storedSample

        // I will try to do things at once
        for(int i = 1; i < rSize +1; i++){
            for(int j = 1; j < storedSize +1; j++){
                if(tab[i][ j-1 ] < tab[ i-1 ][ j-1 ] && tab[i][ j-1 ] < tab[ i-1 ][j] && slopeI[i][j-1] < maxSlope){
                    tab[i][j] = dist(rAllFrame.get( i-1 ),storedAllFrame.get( j-1 )) + tab[i][ j -1 ];
                    //slope is the things that limit the repeated step in DTW
                    slopeI[i][j] = slopeI[i][ j-1 ] +1;
                    slopeJ[i][j] = 0;
                }
                else if(tab[ i-1 ][j] < tab[ i-1 ][ j-1 ] && tab[ i-1 ][j] < tab[i][ j-1 ] && slopeJ[ i-1 ][j] < maxSlope){
                    slopeI[i][j] = 0;
                    slopeJ[i][j] = slopeJ[ i-1 ][j] +1;
                }
                else{
                    tab[i][j] = dist(rAllFrame.get( i -1 ), storedAllFrame.get( j -1));
                    slopeI[i][j] = 0;
                    slopeJ[i][j] = 0;
                }
            }
        }

        // this function dynamically find the best ending of the sample for recognition
        // imagine that it just like storedSample unchanged but cutting the frame of rSample
        for(int i = rSize +1; i > minFrame; i--){
            if(tab[i][storedSize +1] < bestMatch){
                bestMatch = tab[i][storedSize +1];
            }
        }
    }

    public double getCost(){
        return bestMatch;
    }

    public double dist(Frame rFrame, Frame storedFrame){
        // TODO calculate the distance between two frames
        return 1.0;
    }
}
