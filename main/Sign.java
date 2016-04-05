package main;
/**
 * Created by Krauser on 4/11/2015.
 */
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import data.HandType;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Collection;
import java.util.HashSet;

public class Sign {
	/** field */

	/* fundamental information */
	@MongoObjectId
	private String _id;
	@JsonProperty("name")
	private String name;//unique field
	@JsonProperty("samples")
	private HashSet<Sample> samples=new HashSet<Sample>();//samples should not repeat

	/* extra information */
	//note: the below 3 fields are based on the first frame of the first sample
	@JsonProperty("initialPalmCount")
	private int initialPalmCount;
	@JsonProperty("initialHandType")
	private HandType initialHandType;
	@JsonProperty("initialFingerCount")
	private int initialFingerCount;



	/** constructor */
	/* this constructor is used for converting JSON data back to object(s)
	To make the code safe, please don't use this constructor.
	Use the constructor forcing you to give initialised parameter instead.
	 */

	//basic constructor with 1 sample only
	public Sign(String SignName,Sample sample) throws Exception {
		//Valid sign name is required
		if(SignName==null||isNameInvalid(SignName)||sample==null)
			throw new Exception();

		this.name=SignName;
		this.initialFingerCount= sample.getInitialFingerCount();
		this.initialPalmCount= sample.getInitialPalmCount();
		this.initialHandType= sample.getInitialHandType();
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

	//advanced constructor for Jackson mapping
	@JsonCreator
	public Sign(@JsonProperty("name") String SignName, @JsonProperty("samples") Collection<Sample> samples,@JsonProperty("initialPalmCount") int PalmCount,@JsonProperty("initialHandType") HandType HandType,@JsonProperty("initialFingerCount") int FingerCount) throws Exception {
		//Valid sign name is required & non-empty samples
		if(SignName==null||isNameInvalid(SignName)||samples==null||samples.isEmpty())
			throw new Exception();

		this.name =SignName;
		this.samples.addAll(samples);
		setInitialPalmCount(PalmCount);
		setInitialHandType(HandType);
		setInitialFingerCount(FingerCount);
	}



	/** method   */

	//replace all the samples
	@JsonSetter("samples")
	public boolean setAllSamples(Collection<Sample> source){
		if(source==null||source.isEmpty())
			return false;

		samples.clear();
		this.samples.addAll(samples);
		Sample first=samples.iterator().next();
		this.initialPalmCount= first.getInitialPalmCount();
		this.initialHandType= first.getInitialHandType();
		this.initialFingerCount= first.getInitialFingerCount();
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
	@JsonSetter("name")
	public boolean setName(String SignName) {
		if(SignName==null||isNameInvalid(SignName))
			return false;
		this.name=SignName;
		return true;
	}
	@JsonSetter("initialPalmCount")
	public boolean setInitialPalmCount(int PalmCount) {
		if(PalmCount<1||PalmCount>2)
			return false;
		this.initialPalmCount=PalmCount;
		return true;
	}
	@JsonSetter("initialHandType")
	public boolean setInitialHandType(HandType HandType) {
		if(HandType==null)
			return false;
		this.initialHandType=HandType;
		return true;
	}
	@JsonSetter("initialFingerCount")
	public boolean setInitialFingerCount(int FingerCount) {
		if(FingerCount<0||FingerCount>10)
			return false;
		this.initialFingerCount=FingerCount;
		return true;
	}

	@JsonGetter("name")
	public String getName() {
		return name;
	}
	@JsonGetter("initialFingerCount")
	public int getInitialFingerCount() {
		return initialFingerCount;
	}
	@JsonGetter("initialHandType")
	public HandType getInitialHandType() {
		return initialHandType;
	}
	@JsonGetter("initialPalmCount")
	public int getInitialPalmCount() {
		return initialPalmCount;
	}
	@JsonGetter("samples")
	public HashSet<Sample> getAllSamples() {
		return this.samples;
	}
	public Sample getFirstSamples(){
		return samples.iterator().next();
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
