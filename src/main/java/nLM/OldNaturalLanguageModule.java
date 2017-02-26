package nLM;
import java.util.Arrays;
import java.util.Calendar;

public class OldNaturalLanguageModule {
	
	String[] greetings = new String[]{"hi","hello","hey"};
	Noun[] nouns = new Noun[]{new Noun("time", "Time is the way things move", "action time"), new Noun("earth", "Earth is the planet we live on", null)};
	
	public String getResponse(String input) {
		
		questionTypes qTypes = null;
		int nounIndex = -1;
		
		String[] words = input.split("\\s+");
		Arrays.sort(greetings);
		//Arrays.sort(words);
		for (int i = 0; i < words.length; i++) {
			if (Arrays.binarySearch(greetings, words[0]) >= 0) {
				qTypes = questionTypes.greetings;
			}else if (input.contains("what")) {
				qTypes = questionTypes.define;
			}else if (input.contains("why")) {
				qTypes = questionTypes.explan;
			}else if (input.contains("where")) {
				qTypes = questionTypes.location;
			}else if (input.contains("how")) {
				qTypes = questionTypes.process;
			}
		}
		
		if (qTypes == questionTypes.greetings){
			return "hello";
		}
		
		for (int i = 0; i < nouns.length; i++) {
			if(input.contains(nouns[i].word)) {
				nounIndex = i;
				break;
			}
			if (nounIndex != -1) {
				break;
			}
		}
		
		if (qTypes == questionTypes.define && nounIndex != -1) {
			if (input.contains("the")&& nouns[nounIndex].defineCurrentR != null) {
				if (nouns[nounIndex].defineCurrentR.contains("action")){
					if (nouns[nounIndex].defineCurrentR.contains("time")){
						return "The current time is " + Calendar.getInstance().getTime().toString();
					}
				}
			}
			return nouns[nounIndex].defineR;
		}
		
		if (qTypes != null) {
			return "I do not know";
		}
	
		
		return "I do not understand";
	}
}

enum questionTypes {greetings,define,explan,location,process}