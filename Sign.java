/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.Frame;

import java.util.ArrayList;

public class Sign {
    private int id;
    private String signName;
    private ArrayList< ArrayList <Frame> > allSample;

    public Sign(){
        this.id = -1;
        this.signName = "";
        this.allSample = null;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }
    public void insertSample(ArrayList<ArrayList<Frame>> allSample, ArrayList<Frame> oneSample){
        allSample.add(oneSample);
    }


    public int getId() {
        return id;
    }

    public String getSignName() {
        return this.signName;
    }

    public void setAllSample(ArrayList<ArrayList<Frame>> allSample) {
        this.allSample = allSample;
    }

    public ArrayList<ArrayList<Frame>> getAllSample() {
        return allSample;
    }

}
