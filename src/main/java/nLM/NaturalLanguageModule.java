package nLM;

import java.util.ArrayList;
import java.util.Arrays;

public class NaturalLanguageModule {
	
	NaturalLanguageParser nLParser;
	Noun[] nounsArray = {
			new Noun("it",new ArrayList<String>(Arrays.asList("time")), "")
	};
	ArrayList<Noun> nouns = new ArrayList<Noun>(Arrays.asList());
	
	public NaturalLanguageModule() {
		nLParser = new NaturalLanguageParser();
	}

	public String getResponse(String input) {
		ArrayList<String> output = nLParser.getResponse(input);
		ArrayList<ArrayList<String>> wordsList = new ArrayList<ArrayList<String>>();
		output = nLParser.getResponse(input);
		for (int i = 0; i < output.size(); i++) {
			char lastChar = '.';
			String currentWord = "";
			wordsList.add(new ArrayList<String>());
			for (int j = 0;j < output.get(i).length(); j++) {
				char currentChar = output.get(i).charAt(j);
				if (currentChar == '	' && currentChar != lastChar){
					wordsList.get(i).add(currentWord);
					currentWord = "";
				} else {
					currentWord += currentChar;
				}
				lastChar = currentChar;
			}
		}
		
		int isQuestion = -1;
		for(int i = 0; i < wordsList.size(); i++) {
			if (wordsList.get(i).get(1).equalsIgnoreCase("what")) {
				isQuestion = i;
				break;
			}
		}
		
		if (isQuestion > -1) {
			if (Integer.parseInt(wordsList.get(isQuestion).get(6)) != 0) {
				
			}
		}
		return wordsList.toString();
	}

}
