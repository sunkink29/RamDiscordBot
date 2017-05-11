package nLM;

import java.util.ArrayList;

public class Noun {
	
	final String word;
	ArrayList<String> attributes = new ArrayList<String>();
	String type;
	final String defineR;
	final String defineCurrentR;
	
	public Noun(String word, ArrayList<String> attributes, String type) {
		this.word = word;
		this.attributes = attributes;
		this.type = type;
		defineR = null;
		defineCurrentR = null;
	}

	public Noun(String word, String defineR) {
		this.word = word;
		this.defineR = defineR;
		defineCurrentR = null;
	}
	
	public Noun(String word, String defineR, String defineCurrentR) {
		this.word = word;
		this.defineR = defineR;
		this.defineCurrentR = defineCurrentR;
		
	}
}