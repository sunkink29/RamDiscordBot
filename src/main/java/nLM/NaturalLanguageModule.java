package nLM;

import java.util.ArrayList;
import java.util.Calendar;

public class NaturalLanguageModule {
	
	Word[] knowWords = new Word[]{new Word("is", "equality"), new Word("what", "question definition"), 
			new Noun("time", "A system of sequential relations"), new Word("it", "pronoun general"), new Word("the", "definite article")};
	
	public String getResponse(String input) {
		
		input = input.toLowerCase();
		String[] words = input.split("\\s+");
		ArrayList<Word> words2 = new ArrayList<Word>();
		String sType = "";
		
		for (int i = 0; i < words.length; i++) {
			for (int j = 0; j < knowWords.length; j++) {
				if (words[i].equals(knowWords[j].word)) {
					words2.add(knowWords[j]);
					if (knowWords[j].type.contains("question")) {
						sType += "question ";
					}
					if (knowWords[j].type.contains("definition")) {
						sType += "definition ";
					}
				}
			}
		}
		
		Sentance sentance = new Sentance(sType, words2);
		Noun noun = new Noun("unknown", "no Noun");
		String tense = "general";
		
		for (int i = 0; i < sentance.words.size(); i++) {
			if (sentance.words.get(i).type.contains("noun")) {
				noun = (Noun)sentance.words.get(i);
			}
			if (sentance.words.get(i).type.contains("definite article")) {
				tense = "current";
			}
		}
		
		if (sentance.sType.contains("question")) {
			if (sentance.sType.contains("definition")) {
				if (tense.contains("current")) {
					if (noun.word.equals("time")) {
						return "The time is "+ Calendar.getInstance().getTime().toString();
					}
				}
			}
		}
		
		return words2.toString();
	}
}
