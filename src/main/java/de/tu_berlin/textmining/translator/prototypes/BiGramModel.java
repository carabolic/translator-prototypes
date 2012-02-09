package de.tu_berlin.textmining.translator.prototypes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.stanford.nlp.stats.ClassicCounter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BiGramModel implements LanguageModel {
	
	public HashMap<String,Integer> words = new HashMap<String,Integer>();
	public double totalSum = 0;
	
	public void addUniGram(String w) {
		if(words.get(w)!=null) {
			words.put(w,words.get(w).intValue()+1);
		} else {
			words.put(w,new Integer(1));
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
	
	public double getBiGramProbability(String w1, String w2) {
		double probability = getCount(w1, w2);
		
		if (probability == 0.0) {
			probability = 1.0;
		}
		
		double wordProbability = probability / (getCount(w1) + 1.0);
		Preconditions.checkArgument(wordProbability > 0);
		return wordProbability;
	}
}
