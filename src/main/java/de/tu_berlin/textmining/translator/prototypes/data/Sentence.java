package de.tu_berlin.textmining.translator.prototypes.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.base.Preconditions;

public final class Sentence {

	private final List<String> words;
	private final char punctuation;
	private final int length;
	
	public Sentence(String sentence) {
		StringTokenizer strTok = new StringTokenizer(sentence);
		
		String token;
		List<String> words = new ArrayList<String>();
		while (strTok.hasMoreTokens()) {
			
		}
		
		this.words = words;
		this.length = words.size();
		this.punctuation = 'c';
	}
	
	public List<String> getWords() {
		return new ArrayList<String>(this.words);
	}

	public String getWordAt(int index)	{
		Preconditions.checkArgument(index >= 0 && index < length);
		return this.words.get(index);
	}
	
	public char getPunctuation() {
		return this.punctuation;
	}
	
	public int getLength() {
		return this.length;
	}
}
