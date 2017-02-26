package nLM;

import java.util.ArrayList;

public class Sentance {
	final String sType;
	final ArrayList<Word> words;
	
	public Sentance(String sType, ArrayList<Word> words) {
		this.sType = sType;
		this.words = words;
	}

}
