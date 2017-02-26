package nLM;

public class Noun extends Word {
	
	final String defineR;
	final String defineCurrentR;

	public Noun(String word, String defineR) {
		super(word, "noun");
		this.defineR = defineR;
		defineCurrentR = null;
	}
	
	public Noun(String word, String defineR, String defineCurrentR) {
		super(word,"noun");
		this.defineR = defineR;
		this.defineCurrentR = defineCurrentR;
		
	}
}