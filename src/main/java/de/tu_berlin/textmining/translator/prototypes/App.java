package de.tu_berlin.textmining.translator.prototypes;

/**
 * Hello world!
 * 
 */
public class App {
	
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: App [path to dict] [path to JSON output]");
			return;
		}
		String pathToDict = args[0];
		String pathToJSON = args[1];
		/** Load the Dictionary */
		Dictionary dict = new Dictionary();
		// test parse dict.txt file into hashmap and create json file
		dict.parseDictFile(pathToDict);
		dict.createJSONFile(pathToJSON);
		// test parse json file into hashmap
		// dict.parseJSONFile("/home/textmining/Desktop/dict.json");
		System.out.println("done");
		
		String germanString = "Zehn zahme Ziegen zogen zehn Zentner Zucker";
		String englishString = dict.translateSentence(germanString);
		System.out.println("GERMAN:\t" + germanString);
		System.out.println("ENGLISH:\t" + englishString);
	}
}
