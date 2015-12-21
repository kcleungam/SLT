import com.leapmotion.leap.Frame;

import java.util.ArrayList;

/**
 * Created by Krauser on 21/12/2015.
 */
public class DTW {
    public double bestMatch = Double.POSITIVE_INFINITY;
    public Sample rSample;       // Sample for recognize
    public Sample storedSample; // Sample from database

    public double globalThreshold; // The maximum distance between sample and stored sample which can be recognize
                                    // If the bestMatch > globalThreshold, then unknown gesture

    public double localThreshold;   // The distance between sample and one of the stored sample


    public DTW(Sample rSample, Sample storedSample){
        ArrayList<Frame> rAllFrame = rSample.getAllFrames();
        ArrayList<Frame> storedAllFrame = storedSample.getAllFrames();
        int rSize = rAllFrame.size();
        int storedSize = storedAllFrame.size();

        double cost[][] = new double[rSize][storedSize];    //Create a table which store the cost of distance between each frame

        cost[0][0] = dist(rAllFrame.get(0), storedAllFrame.get(0));

        // calculate the first row
        for(int i = 1; i < rSize; i++){
            cost[ i ][ 0 ] = cost[ i - 1 ][0] + dist(rAllFrame.get(i),storedAllFrame.get(0));
        }

        //calculate the first column
        for( int j = 1; j < storedSize; j++){
            cost[0][j] = cost[0][ j - 1] + dist(rAllFrame.get(0),storedAllFrame.get(j));
        }

        // fill the entire matrix
        for(int i = 1; i < rSize; i++){
            for(int j = 1; j < storedSize; j++){
                cost[i][j] = Math.min( cost[ i-1][ j ], cost[ i-1][ j-1] ) + dist(rAllFrame.get(i),storedAllFrame.get(j));
            }
        }

        // The cost of the entire comparison should be the cost[rSize][storedSize], it can show the distance between two sample
        // However we have to implement some method to improve it
        // I have read some source code and have some idea, I know it will work

    }

    public double dist(Frame rFrame, Frame storedFrame){
        // TODO calculate the distance between two frames
        return 1.0;
    }
}
