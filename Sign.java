
/**
 * Created by Krauser on 4/11/2015.
 */
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Sign {
	/** field */

	/* fundamental information */
	@MongoObjectId
	private String _id;
	String name;//unique field
	private HashSet<Sample> samples=new HashSet<Sample>();//samples should not repeat

	/* extra information */
	//note: the below 3 fields are based on the first frame of the first sample
	private int handCount;
	private Sample.HandType handType;
	private int fingerCount;



	/** constructor */
	/* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */
	// for Jongo exclusively
	public Sign(){}

	//basic constructor with 1 sample only
	public Sign(String SignName,Sample sample) throws Exception {
		//Valid sign name is required
		if(SignName==null||isNameInvalid(SignName)||sample==null)
			throw new Exception();

		this.name=SignName;
		setHandCount(sample.allHands.count);
		setHandType(sample.allHands.type);
		setFingerCount(sample.allFingers.count);
		this.samples.add(sample);
	}

	//basic constructor with a sequence of samples
	public Sign(String SignName, Collection<Sample> samples) throws Exception {
		//Valid sign name is required & non-empty samples
		if(SignName==null||isNameInvalid(SignName)||samples==null||samples.isEmpty())
			throw new Exception();

		this.name=SignName;
		setAllSamples(samples);
	}

	//advanced constructor
	public Sign(String SignName, Collection<Sample> samples,int HandCount,Sample.HandType HandType,int FingerCount) throws Exception {
		//Valid sign name is required & non-empty samples
		if(SignName==null||isNameInvalid(SignName)||samples==null||samples.isEmpty())
			throw new Exception();

		this.name =SignName;
		this.samples.addAll(samples);
		setHandCount(HandCount);
		setHandType(HandType);
		setFingerCount(FingerCount);
	}



	/** method   */

	//replace all the samples
	public boolean setAllSamples(Collection<Sample> source){
		if(source==null||source.isEmpty())
			return false;

		samples.clear();
		this.samples.addAll(samples);
		Sample first=samples.iterator().next();
		this.handCount=first.allHands.count;
		this.handType=first.allHands.type;
		this.fingerCount=first.allFingers.count;
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

	public boolean setHandType(Sample.HandType HandType) {
		//ToDo: restrict the values
		if(HandType==null)
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
		return name;
	}

	public int getFingerCount() {
		return fingerCount;
	}

	public Sample.HandType getHandType() {
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
