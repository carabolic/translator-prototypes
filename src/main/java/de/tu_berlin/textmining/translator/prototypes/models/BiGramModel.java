package de.tu_berlin.textmining.translator.prototypes.models;

import com.google.common.collect.Lists;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BiGramModel implements LanguageModel {
	
	private HashMap<String,Integer> words = new HashMap<String,Integer>();
	private double totalSum = 0;
	
	public BiGramModel() {}
	
	public BiGramModel(BufferedReader reader) throws IOException {
		this();
		this.train(reader);
	}
	
	public void addUniGram(String w) {
		if(words.get(w)!=null) {
			words.put(w,words.get(w).intValue()+1);
		} else {
			words.put(w, 1);
		}
	}
	
	public void addBiGram(String w1, String w2) {
		if(words.get(w1+"_"+w2)!=null) {
			words.put(w1+"_"+w2,words.get(w1+"_"+w2).intValue()+1);
		} else {
			words.put(w1+"_"+w2,new Integer(1));
		}
	}
	
	public double getCount(String w1, String w2) {
		if(words.get(w1+"_"+w2)!=null) {
			return words.get(w1+"_"+w2);
		} else {
			return 0;
		}
	}
	
	public double getCount(String w) {
		if(words.get(w)!=null) {
			return words.get(w);
		} else {
			return 0;
		}
	}
	
	public void train(Collection<List<String>> corpus) {
		for (List<String> sentence : corpus) {
			List<String> fullSentence = Lists.newArrayList();
	        fullSentence.add("<start>");
	        fullSentence.addAll(sentence);
	        fullSentence.add("<stop>");
	        for (String word : fullSentence) {
	        	if(!word.equals("<stop>"))addBiGram(word,fullSentence.get(fullSentence.indexOf(word)+1));
	        	addUniGram(word);
	        	totalSum++;
	        }
	    }
	}
	
	public void train(BufferedReader bufReader) throws IOException {
		String line;
		while ((line = bufReader.readLine()) != null) {
			String[] words = line.split(" ");
			for (int i = 0; i < words.length; i++) {
				String second = words[i].replaceAll("[^a-zA-Z0-9äöüßÄÖÜ\\-]", "").trim().toLowerCase();
				String first;
				if (i == 0) {
					first = "<start>";
				}
				else {
					first = words[i - 1];
				}
				this.addUniGram(first);
				this.addBiGram(first, second);
				this.totalSum++;
			}
		}
	}
	
	public double getBiGramProbability(String w1, String w2) {
		double probability = getCount(w1, w2);
		
		if (probability == 0.0) {
			probability = 1.0;
		}
		
		double wordProbability = probability / (getCount(w1) + 1.0);
		assert(wordProbability > 0);
		return wordProbability;
	}
	
	public double getUniGramProbability(String word) {
		assert(totalSum > 0);
		return getCount(word) / this.totalSum;
	}

	public double getWordProbability(List<String> sentence, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double sentenceLogProbability(List<String> sentence) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Iterable<String> generateSentence() {
		// TODO Auto-generated method stub
		return null;
	}
}
