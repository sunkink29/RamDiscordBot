package nLM;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NaturalLanguageParser {

	public static void main(String[] args) {
		String[] cmd = {
				"/bin/sh",
				"-c",
				"echo \"the fat cat sat on the rat\" | syntaxnet/demo.sh"
				};
	    try {
	      runProcess(cmd, "/Users/rps/Documents/workspace/RamBot/syntaxnet");
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	
	public static ArrayList<String> getResponse(String input) {
		String[] cmd = {
				"/bin/sh",
				"-c",
				"echo \""+input+"\" | syntaxnet/demo.sh"
				};
	    try {
	      return runProcess(cmd, "/Users/rps/Documents/workspace/RamBot/syntaxnet");
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return new ArrayList<String>();
	}
	
	 private static ArrayList<String> printLines(String name, InputStream ins) throws Exception {
		    String line = null;
		    ArrayList<String> output = new ArrayList<String>();
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(ins));
		    while ((line = in.readLine()) != null) {
		    	if (!(line.charAt(0) == 'W') && !(line.charAt(0) == 'I') && !(line.charAt(0) == 'i')){
		    		output.add(name + " " + line);
		    	} else {
		    		System.out.println(line);
		    	}
		    }
	    	return output;
		  }

	  private static ArrayList<String> runProcess(String[] command, String dir) throws Exception {
		ArrayList<String> output = new ArrayList<String>();
		File fileDir = new File(dir);
	    Process pro = Runtime.getRuntime().exec(command,null,fileDir);
	    output = printLines(" ", pro.getInputStream());
	    output.addAll(printLines(command + " stderr:", pro.getErrorStream()));
	    pro.waitFor();
	    System.out.println(command + " exitValue() " + pro.exitValue());
	    return output;
	  }

}
