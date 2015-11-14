
/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.Frame;

import java.util.ArrayList;

public class Sign {
	private int id;
	private String signName;
	private int handCount;
	private String handType;
	private int fingerCount;
	private ArrayList<ArrayList<Frame>> allSample;

	public Sign() {
		this.id = -1;
		this.signName = "";
		this.handCount = 0;
		this.fingerCount = 0;
		this.allSample = new ArrayList<ArrayList<Frame>>();
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSignName(String signName) {
		this.signName = signName;
	}

	public void setHandCount(int handCount) {
		this.handCount = handCount;
	}

	public void setHandType(String handType) {
		this.handType = handType;
	}

	public void setFingerCount(int fingerCount) {
		this.fingerCount = fingerCount;
	}

	public void addSample(ArrayList<Frame> oneSample) {
		allSample.add(oneSample);
	}

	public int getId() {
		return id;
	}

	public String getSignName() {
		return this.signName;
	}

	public int getFingerCount() {
		return fingerCount;
	}

	public String getHandType() {
		return handType;
	}

	public int getHandCount() {
		return handCount;
	}

	public void setAllSample(ArrayList<ArrayList<Frame>> allSample) {
		this.allSample = allSample;
	}

	public ArrayList<ArrayList<Frame>> getAllSample() {
		return allSample;
	}

}
