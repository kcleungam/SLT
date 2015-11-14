
/**
 * Created by Krauser on 4/11/2015.
 */
import com.leapmotion.leap.Frame;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Sign {
	/** field */

	/* fundamental information */
	private String name;//unique field
	private HashSet<Sample> samples=new HashSet<Sample>();//samples should not repeat

	/* extra information */
	//hand
	private int handCount;
	private String handType;

	//finger
	private int fingerCount;



	/** constructor */

	//basic constructor
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
