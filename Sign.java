/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.Frame;

import java.util.ArrayList;

public class Sign {
    private int id;
    private String SignName;
    private ArrayList< ArrayList <Frame> > allSample;

    public Sign(){
        this.id = -1;
        this.SignName = "";
        this.allSample = null;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setSignName(String signName) {
        this.SignName = signName;
    }
    public void insertSample(ArrayList<ArrayList<Frame>> allSample, ArrayList<Frame> oneSample){
        allSample.add(oneSample);
    }


    public int getId() {
        return id;
    }

    public String getSignName() {
        return this.SignName;
    }

    public void setAllSample(ArrayList<ArrayList<Frame>> allSample) {
        this.allSample = allSample;
    }

    public ArrayList<ArrayList<Frame>> getAllSample() {
        return allSample;
    }

}
