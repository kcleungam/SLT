/**
 * Created by Krauser on 7/3/2016.
 */
public class LRO {
    Coordinate leftPalmOrigin;
    Coordinate rightPalmOrigin;

    LRO(){
        leftPalmOrigin = null;
        rightPalmOrigin = null;
    }

    void setLO(Coordinate left){
        leftPalmOrigin = left;
    }

    void setRO(Coordinate right){
        rightPalmOrigin = right;
    }

    void setLRO(Coordinate left, Coordinate right){
        leftPalmOrigin = left;
        rightPalmOrigin = right;
    }

    void resetLRO(){
        leftPalmOrigin = null;
        rightPalmOrigin = null;
    }

}
