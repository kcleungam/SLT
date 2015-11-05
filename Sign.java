/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.*;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.Console;
import java.util.Scanner;
import java.util.Vector;
import java.lang.Math;
import java.util.*;

public class Sign {
    private int id;
    private String gestureName;
    private Vector< Vector <Frame> > allSample;

    public Sign(){
        this.id = -1;
        this.gestureName = "";
        this.allSample = null;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }
    public void insertSample(Vector<Vector<Frame>> allSample, Vector<Frame> oneSample){
        allSample.add(oneSample);
    }

    public void setSample(Vector<Vector<Frame>> allSample) {
        this.allSample = allSample;
    }

    public int getId() {
        return id;
    }

    public String getGestureName() {
        return gestureName;
    }

    public Vector<Vector<Frame>> getAllSample() {
        return allSample;
    }

    public Sign(String gestureName, int id, Vector<Frame> data){
        this.id = id;
        this.gestureName = gestureName;
        this.allSample = allSample;
    }
}
