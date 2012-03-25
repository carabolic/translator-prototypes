package de.tu_berlin.textmining.translator.prototypes.models;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.tu_berlin.textmining.translator.prototypes.data.tree.Trie;

@SuppressWarnings("deprecation")
public class NGramLanguageModel {
	
	private final static String SENTENCE_START = "<s>";
	private final static String SENTENCE_END = "<\\s>";
	private final static long DEFAULT_COUNT = 1l;
	
	private final int maxDegree;
	private final Trie trie;
	
	public NGramLanguageModel(int maxDegree)	 {
		this.trie = new Trie();
		this.maxDegree = maxDegree;
	}
	
	public void train(final String sentence) throws IOException {
		String stoppedSentence = this.annotateSentence(sentence);
		StandardTokenizer stdTokenizer = new StandardTokenizer(Version.LUCENE_35, new StringReader(stoppedSentence));
		ShingleFilter filter = new ShingleFilter(stdTokenizer, this.maxDegree, this.maxDegree);
		filter.setOutputUnigrams(false);
		
		while (filter.incrementToken()) {
			TermAttribute term = filter.getAttribute(TermAttribute.class);
			List<String> ngram = Arrays.asList(term.toString().split(" "));
			this.trie.insert(ngram);
		}
	}
	
	public void train(final List<String> sentence) {
		List<String> stoppedSentence = this.annotateSentence(sentence);
	}
	
	public double getWordLogProbability(List<String> sentence, int index) {
		Preconditions.checkArgument(sentence.size() > index, "The specified index is too large: " + index);
		// the length of the history specified for this instance
		// e.g.: the length of the history for trigram language models would be 2
		int historyLen = this.maxDegree - 1;
		List<String> ngram;
		List<String> history;
		
		if (index == 0) {
			ngram = sentence.subList(0, 1);
			history = Lists.newArrayList();
		}	else if (index - historyLen < 0) {
			ngram = sentence.subList(0, index + 1);
			history = sentence.subList(0, index);
		} else {
			ngram = sentence.subList(index - historyLen, index + 1);
			history = sentence.subList(index - historyLen, index);
		}
		long ngramCount = this.trie.retrieve(ngram);
		if (ngramCount == 0l) {
			ngramCount = NGramLanguageModel.DEFAULT_COUNT;
		}
		long histCount =  this.trie.retrieve(history) + 1;
		
		double prob = (ngramCount + 0.0) / histCount;
		return Math.log(prob);
	}
	
	public double getSentenceLogProbability(final List<String> sentence) {
		double sum = 0.0;
		
		for (int i = 0; i < sentence.size(); i++) {
			sum += this.getWordLogProbability(sentence, i);
		}
		
		return sum;
	}
	
	public long getCount(final List<String> ngram) {
		return this.trie.retrieve(ngram);
	}
	
	private String annotateSentence(final String sentence) {
		StringBuilder strBld = new StringBuilder(sentence.length() + this.maxDegree);
		for (int i = 0; i < this.maxDegree - 1; i++) {
			strBld.append(NGramLanguageModel.SENTENCE_START);
		}
		strBld.append(sentence);
		strBld.append(NGramLanguageModel.SENTENCE_END);
		
		return strBld.toString();
	}
	
	private List<String> annotateSentence(final List<String> sentence) {
		List<String> stoppedSentence = Lists.newArrayList();
		for (int i = 0; i < this.maxDegree - 1; i++) {
			stoppedSentence.add(SENTENCE_START);
		}
		stoppedSentence.addAll(sentence);
		stoppedSentence.add(SENTENCE_END);
		
		return stoppedSentence;
	}
	
	public static void main(String... args) throws IOException {
		NGramLanguageModel lm = new NGramLanguageModel(3);
		String s1 = "Hallo du, wie geht es dir?";
		String s2 = "Hallo du, wie geht's?";
		String s3 = "Hallo Welt!";
		String s4 = "Franz jagt im komplett verwarlosten Taxi quer durch Bayern.";
		lm.train(s1);
		lm.train(s2);
		lm.train(s3);
		lm.train(s4);
		
		StandardTokenizer stdTok = new StandardTokenizer(Version.LUCENE_35, new StringReader(s3));
		List<String> sentence = Lists.newArrayList();
		
		while (stdTok.incrementToken()) {
			TermAttribute term = stdTok.getAttribute(TermAttribute.class);
			sentence.add(term.toString());
		}
		
		System.out.println(lm.getSentenceLogProbability(sentence));
		System.out.println(lm.getSentenceLogProbability(Arrays.asList(new String[] {"Welt", "Hallo"})));
		System.out.println(lm.getSentenceLogProbability(Arrays.asList(new String[] {"Hallo", "du"})));
		System.out.println(lm.getSentenceLogProbability(Arrays.asList(new String[] {"Hallo", "Welt"})));
	}
}
