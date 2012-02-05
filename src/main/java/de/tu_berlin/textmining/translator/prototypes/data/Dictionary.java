package de.tu_berlin.textmining.translator.prototypes.data;

import java.io.IOException;
import java.util.Set;

import de.tu_berlin.textmining.translator.prototypes.reader.DictionaryFileReader;

public interface Dictionary {
	
	/**
	 * Loads the dictionary from the specified InputReader
	 * @param reader
	 * 	Specific reader
	 * @throws IOException
	 */
	void load(DictionaryFileReader reader) throws IOException;
	
	void addWord(DictionaryEntry dictEntry);
	
	/**
	 * Adds a word-to-word translation pair to the dictionary
	 * @param src
	 * 	Word in source language
	 * @param target
	 * 	Translated word in target language
	 * @param attrs
	 * 	Set of attributes for the word in the source language
	 */
	void addWord(String src, String target, Set<String> attrs);
	
	/**
	 * 
	 * @param src
	 * @param target
	 * @param attrs
	 */
	void addWord(String src, Set<String> target, Set<String> attrs);
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	String[] translate(String[] sentences);
	
	/**
	 * 
	 * @param sentence
	 * @return
	 */
	String translateSentence(String sentence);
	
	/**
	 * 
	 * @param word
	 * @return
	 */
	String[] translateWord(String word);
}
