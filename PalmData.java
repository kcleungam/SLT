import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;

/**
 * Created by alex on 2/2/2016.
 */
public class PalmData extends Data{
    /* exclusive use for Jongo */
    public PalmData() {
    }

    public PalmData(Frame frame) {
        this.count = frame.hands().count();
        if(this.count == 1){
            for (Hand hand : frame.hands()) {
                Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                coordinates.add(cor);
            }
        }else if(this.count == 2){
            for(int i = 0; i < 2; i++){
                Coordinate cor = new Coordinate(0,0,0);
                coordinates.add(cor);   // initialize and ready to set the value below
            }

            for(Hand hand : frame.hands()){
                Coordinate cor = new Coordinate(hand.palmPosition().getX(), hand.palmPosition().getY(), hand.palmPosition().getZ());
                if(hand.isLeft()){
                    coordinates.set(0, cor);
                }else{
                    coordinates.set(1, cor);
                }
            }
        }
    }
}