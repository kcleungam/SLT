package data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.leapmotion.leap.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by alex on 1/2/2016.
 */
public class FingerData extends Data {
    /* constructor */
    @JsonProperty("distal")ArrayList<Coordinate> distal = new ArrayList<>();
    @JsonProperty("intermediate")ArrayList<Coordinate> intermediate = new ArrayList<>();
    @JsonProperty("proximal")ArrayList<Coordinate> proximal = new ArrayList<>();
    @JsonProperty("metacarpal")ArrayList<Coordinate> metacarpal = new ArrayList<>();
    /** exclusive use for Jongo */
    @JsonCreator
    private FingerData() {}

    /** preferred constructor */
    public FingerData(Frame frame) {
        HandList hands = frame.hands();
        count = (frame.fingers().count() > 0) ? frame.fingers().count() : 0;

        //the code should be safe as Leap Motion controller won't recognise more than 2 hands
        if (hands.count() > 1) {
            handType = HandType.BOTH;
        } else if (hands.get(0).isLeft()) {
            handType = HandType.LEFT;
        } else if (hands.get(0).isRight()) {
            handType = HandType.RIGHT;
        } else {
            System.err.println("Cannot determine the hand type.");
        }

        //iterate finger by finger to get the coordinates of the finger(s)
        // If single hand, put it directly
        // otherwise, put finger in left hand first( position 0-4) and then right hand (position 5-9)
        if (count == 5) {
            for (Finger finger : frame.fingers()) {
                Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                coordinates.add(cor);

                Coordinate dis = new Coordinate(finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getX(),
                        finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getY(),
                        finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getZ());
                this.distal.add(dis);

                Coordinate inter = new Coordinate(finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getX(),
                        finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getY(),
                        finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getZ());
                this.intermediate.add(inter);

                Coordinate prox = new Coordinate(finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getX(),
                        finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getY(),
                        finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getZ());
                this.proximal.add(prox);

                Coordinate meta = new Coordinate(finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getX(),
                        finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getY(),
                        finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getZ());
                this.metacarpal.add(meta);
            }
        } else if (count == 10) {
            int label = 0;
            for(int i = 0; i < 10; i++){
                Coordinate cor = new Coordinate(0,0,0);
                coordinates.add(cor);     // init the coordinates arraylist and ready to set below

                Coordinate dis = new Coordinate(0,0,0);
                this.distal.add(dis);

                Coordinate inter = new Coordinate(0,0,0);
                this.intermediate.add(inter);

                Coordinate prox = new Coordinate(0,0,0);
                this.proximal.add(prox);

                Coordinate meta = new Coordinate(0,0,0);
                this.metacarpal.add(meta);
            }

            for (Hand hand : frame.hands()) {
                label=(hand.isLeft())?0:5;
                for (Finger finger : hand.fingers()) {
                    Coordinate cor = new Coordinate(finger.tipPosition().getX(), finger.tipPosition().getY(), finger.tipPosition().getZ());
                    coordinates.set(label, cor);

                    Coordinate dis = new Coordinate(finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getX(),
                            finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getY(),
                            finger.bone(Bone.Type.TYPE_DISTAL).prevJoint().getZ());
                    this.distal.set(label, dis);

                    Coordinate inter = new Coordinate(finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getX(),
                            finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getY(),
                            finger.bone(Bone.Type.TYPE_INTERMEDIATE).prevJoint().getZ());
                    this.intermediate.set(label, inter);

                    Coordinate prox = new Coordinate(finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getX(),
                            finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getY(),
                            finger.bone(Bone.Type.TYPE_PROXIMAL).prevJoint().getZ());
                    this.proximal.set(label, prox);

                    Coordinate meta = new Coordinate(finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getX(),
                            finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getY(),
                            finger.bone(Bone.Type.TYPE_METACARPAL).prevJoint().getZ());
                    this.metacarpal.set(label, meta);

                    label++;
                }
            }
        }

    }

    @JsonSetter("distal")
    public boolean setDistal(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.distal.clear();
        this.distal.addAll(source);
        return true;
    }

    @JsonSetter("intermediate")
    public boolean setIntermediate(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.intermediate.clear();
        this.intermediate.addAll(source);
        return true;
    }

    @JsonSetter("proximal")
    public boolean setProximal(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.proximal.clear();
        this.proximal.addAll(source);
        return true;
    }

    @JsonSetter("metacarpal")
    public boolean setMetacarpal(Collection<Coordinate> source){
        if(source==null||source.isEmpty())
            return false;
        this.metacarpal.clear();
        this.metacarpal.addAll(source);
        return true;
    }

    @JsonGetter("distal")
    public ArrayList<Coordinate> getDistal(){
        return this.distal;
    }

    @JsonGetter("intermediate")
    public ArrayList<Coordinate> getIntermediate(){
        return this.intermediate;
    }

    @JsonGetter("proximal")
    public ArrayList<Coordinate> getProximal(){
        return this.proximal;
    }

    @JsonGetter("metacarpal")
    public ArrayList<Coordinate> getMetacarpal(){
        return this.metacarpal;
    }
}
