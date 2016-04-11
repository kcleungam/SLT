package gui;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alex on 10/4/2016.
 */
public class LoggingTemplate {
    public enum UPDATE{ADD,DELETE};

    public static ArrayList<Text> getUpdateMessage(String name, int sample_size, UPDATE update){
        if(name==null||name.isEmpty()) throw new IllegalArgumentException();

        ArrayList<Text> result=new ArrayList<>();
        Text date=new Text((new Date()).toString()+"\t");
        Text gesture_name=new Text(name);gesture_name.setFill(Color.BLUE);
        Text sentence=new Text();

        switch (update){
            case ADD:
                if(sample_size<=0) throw new IllegalArgumentException();
                sentence.setText(" is added with "+sample_size+" samples.\n");
                break;
            case DELETE:
                sentence.setText(" is deleted.\n");
                break;
        }

        result.add(date);result.add(gesture_name);result.add(sentence);
        return result;
    }

    public static ArrayList<Text> getModeMessage(Mode mode){
        ArrayList<Text> result=new ArrayList<>();
        Text date=new Text((new Date()).toString()+"\t");
        Text mode_name=new Text(String.valueOf(mode));mode_name.setFill(Color.LIMEGREEN);
        Text sentence=new Text(" is selected.\n");

        result.add(date);result.add(mode_name);result.add(sentence);
        return result;
    }

    public static ArrayList<Text> getRecogniseMessage(String name,double cost){
        if(name==null||name.isEmpty()||cost<0) throw new IllegalArgumentException();

        ArrayList<Text> result=new ArrayList<>();
        Text date=new Text((new Date()).toString()+"\t");
        Text recognised_gesture=new Text(name);recognised_gesture.setFill(Color.BLUE);
        Text sentence=new Text(" is recognised with cost "+cost+"\n");

        result.add(date);result.add(recognised_gesture);result.add(sentence);
        return result;
    }

    public static ArrayList<Text> getSystemMessage(String message){
        if(message==null||message.isEmpty()) throw new IllegalArgumentException();
        ArrayList<Text> result=new ArrayList<>();
        result.add(new Text((new Date()).toString()+"\t"));
        Text system_message=new Text(message+"\n");system_message.setFill(Color.PINK);
        result.add(system_message);
        return result;
    }

    public static Text getErrorMessage(String message){
        if(message==null||message.isEmpty()) throw new IllegalArgumentException();
        Text error=new Text((new Date()).toString()+"\t"+message+"\n");
        error.setFill(Color.RED);
        return error;
    }
}
