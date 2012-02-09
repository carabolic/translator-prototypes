package de.tu_berlin.textmining.translator.prototypes;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import de.tu_berlin.textmining.translator.prototypes.data.Dictionary;
import de.tu_berlin.textmining.translator.prototypes.data.HashDictionary;
import de.tu_berlin.textmining.translator.prototypes.reader.ElcombriReader;

/**
 * Hello world!
 * 
 */
public class App {

	static String trainingSentencesFile = "prototypes/corpus.txt";
	
	static Collection<List<String>> getSentences(String corpus){
	    Collection<List<String>> sentences = new ArrayList<List<String>>();
	    for (String rawSentence : corpus.split("\n")) {
	        List<String> sentence = Lists.newArrayList();
	        for (String word : rawSentence.split("\\s+")) {
	            sentence.add(word.toLowerCase());
	        }
	        sentences.add(sentence);
	    }
	    return sentences;
	}
	
	static void train(LanguageModel languageModel) throws IOException {
	    String trainingCorpus = Resources.toString(Resources.getResource(trainingSentencesFile), Charsets.UTF_8);
	    languageModel.train(getSentences(trainingCorpus));
	}
	
	private static final String[] GERMAN_SENTENCES = {
			"Über den Wolken, schwingen die Vögel ihre Flügel.",
			"Zehn zahme Ziegen zogen zehn Zentner Zucker im Zwickauer Zoo",
			"Franz jagt im komplett verwahrlosten Taxi quer durch Bayern.",
			"Hamburg - Der Druck auf die Bundesregierung wächst, den künftigen europäischen Rettungsschirm ESM aufzustocken.",
			"Nach Informationen des SPIEGEL wirbt Italiens Ministerpräsident Mario Monti dafür, das Finanzierungsvolumen des Rettungsschirms von 500 Milliarden Euro auf eine Billion Euro zu erhöhen.",
			"Eine solche Maßnahme schaffe Vertrauen in die Währungsunion, was die Zinsen für Staatsanleihen sinken lasse, argumentiert Monti. In dieser Einschätzung wird er von Spanien und Portugal unterstützt.",
			"Auch Frankreich plädiert dafür, dass Deutschland seine wirtschaftliche und finanzielle Stärke zugunsten des Euro in die Waagschale wirft.",
			"Die Bundesregierung hat Monti von seinen Wünschen schon in Kenntnis gesetzt.",
			"Rückendeckung bekommt der italienische Ministerpräsident auch von seinem Landsmann Mario Draghi, dem Präsidenten der Europäischen Zentralbank (EZB).",
			"Dieser hat einen Kompromissvorschlag zur Diskussion gestellt.",
			"Demnach sollen die unverbrauchten Mittel des vorläufigen Rettungsschirms EFSF nicht auf den ESM angerechnet werden.",
			"Stattdessen soll der " + "dauerhafte Rettungsschirm die Restmittel zusätzlich zur Verfügung gestellt bekommen.",
			"So ließe sich dessen Volu" + "men immerhin auf rund 750 Milliarden Euro aufstocken.",
			"Die Bundesregierung verweist bisher dagegen eisern darauf, dass erst im März geprüft werde, ob der ESM mit ausreichendem Kapital ausgestattet ist.",
			"So war es beim letzten EU-Gipfel vereinbart worden." };

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: App [path to dict] [path to JSON output]");
			return;
		}

		String pathToDict = args[0];
		//String pathToJSON = args[1];
		/** Load the Dictionary */
		Dictionary dict = new HashDictionary();
		// test parse dict.txt file into hashmap and create json file
		System.out.print("Reading dictionary file... ");
		//dict.parseDictFile(pathToDict);
		try {
			// dict.load(new TabSeparatedReader(pathToDict));
			dict.load(new ElcombriReader(pathToDict));
		} catch (FileNotFoundException e) {
			System.err.println("Dictionary not found at given path: " + pathToDict);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error while opening file " + pathToDict);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.print("DONE\n");

		for (String sentence : GERMAN_SENTENCES) {
			String englishString = dict.translateSentence(sentence);
			System.out.println("GERMAN:\t" + sentence);
			System.out.println("ENGLISH:\t" + englishString);
			System.out.println();
		}
		
		/** Train the Language Model */
		BiGramModel languageModel = new BiGramModel();
	    try {
			train(languageModel);
			System.out.println(languageModel.getBiGramProbability("Madam","President"));
		} catch (IOException e) {
			System.err.println("Error while opening file " + trainingSentencesFile);
			e.printStackTrace();
			System.exit(1);
		}
	}
}
