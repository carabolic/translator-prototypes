package de.tu_berlin.textmining.translator.prototypes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import de.tu_berlin.textmining.translator.prototypes.reader.InputReader;

public interface Dictionary {

	void load(String path) throws IOException, FileNotFoundException;
	void load(InputReader reader) throws IOException;
	void addWord(String src, String dest, Set<String> attrs);
	void addWord(String src, Set<String> dest, Set<String> attrs);
	String translateSentence(String sentence);
	String[] translateWord(String word);
}
