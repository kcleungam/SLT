import com.leapmotion.leap.Frame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manage all samples here.
 */
public class Sample {
    /** field */

    ArrayList<Frame> frames=new ArrayList<Frame>();//temporarily store all information from the Leap Motion controller



    /** constructor */

    /* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
    // for Jongo exclusively
    public Sample(){}

    public Sample(Collection<Frame> source) throws Exception {
        //Empty sample is meaningless
        if(source==null||source.isEmpty())
            throw new Exception();
        //become the Array List type
        this.frames.addAll(source);
    }

    //copy constructor
    public Sample(Sample source){
        this.frames=source.frames;
    }



    /** method*/
    /* Expect replacement only. In general, we don't modify the sampling frames. We prefer to record samples again */

    public boolean set(Collection<Frame> other){
        if(other==null||other.isEmpty())
            return false;
        //become the Array List type
        this.frames.clear();
        this.frames.addAll(other);
        return true;
    }

    public ArrayList<Frame> getAllFrames(){
        return frames;
    }
}
