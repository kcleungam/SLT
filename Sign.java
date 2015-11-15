
/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.Hand;

import de.undercouch.bson4jackson.types.ObjectId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Collection;
import java.util.HashSet;

public class Sign {
	/** field */

	/* fundamental information */
	@MongoObjectId
	private String _id;
	String name;//unique field
	private HashSet<Sample> samples=new HashSet<Sample>();//samples should not repeat

	/* extra information */
	//hand
	private int handCount;
	private String handType;

	//finger
	private int fingerCount;



	/** constructor */
	/* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor force you to give initialised parameter in stead.
	 */
	// for Jongo exclusively
	public Sign(){}

	//basic constructor with 1 sample only
	public Sign(String SignName,Sample sample) throws Exception {
		if(SignName==null||isNameInvalid(SignName)||sample==null)
			throw new Exception();
		this.name=SignName;
		setHandCount(sample.getAllFrames().get(0).hands().count());
		int fingerCount = 0;
		if (sample.getAllFrames().get(0).hands().count() > 1) {
			setHandType("Both Hand");
			for (Hand hand : sample.getAllFrames().get(0).hands()) {
				for (Finger finger : hand.fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			}
		} else if (sample.getAllFrames().get(0).hands().count() == 1) {
			if (sample.getAllFrames().get(0).hands().get(0).isLeft()) {
				setHandType("Left");
				// doesn't work when hands.get(0) change to hand(0)
				for (Finger finger : sample.getAllFrames().get(0).hands().get(0).fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			} else if (sample.getAllFrames().get(0).hands().get(0).isRight()) {
				setHandType("Right");
				// doesn't work when hands.get(0) change to hand(0)
				for (Finger finger : sample.getAllFrames().get(0).hands().get(0).fingers()) {
					if (finger.isExtended()) {
						fingerCount++;
					}
				}
			}
		}
		setFingerCount(fingerCount);
		this.samples.add(sample);
	}

	//basic constructor with a sequence of samples
	public Sign(String SignName, Collection<Sample> samples) throws Exception {
		if(SignName==null||isNameInvalid(SignName)||samples==null||samples.isEmpty())
			throw new Exception();
		this.name=SignName;
		this.samples.addAll(samples);
	}

	//advanced constructor
	public Sign(String SignName, Collection<Sample> samples,int HandCount,String HandType,int FingerCount) throws Exception {
		if(SignName==null||isNameInvalid(SignName)||samples==null||samples.isEmpty())
			throw new Exception();
		this.name =SignName;
		this.samples.addAll(samples);

		if(HandCount<1||HandCount>2) throw new Exception();
		this.handCount = HandCount;

		//ToDo: restrict the values
		if(HandType==null||HandType.isEmpty()) throw new Exception();
		this.handType=HandType;

		if(FingerCount<0||FingerCount>10) throw new Exception();
		this.fingerCount=FingerCount;
	}



	/** method   */

	//replace all the samples
	public boolean setAllSamples(Collection<Sample> source){
		if(source==null||source.isEmpty())
			return false;
		//make it to be HashSet
		samples.clear();
		this.samples.addAll(source);
		return true;
	}

	//add one sample
	public boolean addSample(Sample sample){
		if(sample==null)
			return false;
		return this.samples.add(sample);
	}

	//remove one sample
	public boolean removeSample(Sample sample){
		if(sample==null)
			return false;
		return this.samples.remove(sample);
	}

	//add more than one sample
	public boolean addSamples(Collection<Sample> samples){
		if(samples==null||samples.isEmpty())
			return false;
		boolean hasNewSample=false;
		for(Sample s:samples){
			hasNewSample=hasNewSample||addSample(s);
		}
		return hasNewSample;
	}



	/** setter & getter */

	public boolean setName(String SignName) {
		if(SignName==null||isNameInvalid(SignName))
			return false;
		this.name=SignName;
		return true;
	}

	public boolean setHandCount(int HandCount) {
		if(HandCount<1||HandCount>2)
			return false;
		this.handCount=HandCount;
		return true;
	}

	public boolean setHandType(String HandType) {
		//ToDo: restrict the values
		if(HandType==null||HandType.isEmpty())
			return false;
		this.handType=HandType;
		return true;
	}

	public boolean setFingerCount(int FingerCount) {
		if(FingerCount<0||FingerCount>10)
			return false;
		this.fingerCount=FingerCount;
		return true;
	}

	public String getName() {
		return this.name;
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

	public HashSet<Sample> getAllSamples() {
		return this.samples;
	}



	/** helper function */

	//name validation
	private boolean isNameInvalid(String name){
		//ToDO: may use regular expression to do this
		if(name.isEmpty())
			return true;
		return false;
	}
}
