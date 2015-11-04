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
    private Collection<Frame> data;

    public Sign(){
        this.id = -1;
        this.gestureName = "";
        this.data = null;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setGestureName(String gestureName) {
        this.gestureName = gestureName;
    }
    public void insertFrame(Collection<Frame> data, Frame frame){
        data.add(frame);
    }

    public void setData(Collection<Frame> data) {
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getGestureName() {
        return gestureName;
    }

    public Collection<Frame> getData() {
        return data;
    }


    public Sign(String gestureName, int id, Collection<Frame> data){
        this.id = id;
        this.gestureName = gestureName;
        this.data = data;
    }
}
