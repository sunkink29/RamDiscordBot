package nLM;

public class Word {
	final String word;
	final String type;

	public Word(String word, String type) {
		this.word = word;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return word;
	}
}
