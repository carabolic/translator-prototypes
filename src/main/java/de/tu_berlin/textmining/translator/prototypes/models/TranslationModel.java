package de.tu_berlin.textmining.translator.prototypes.models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import de.tu_berlin.textmining.translator.prototypes.data.Language;
import de.tu_berlin.textmining.translator.prototypes.util.SentenceTokenizer;
import de.tu_berlin.textmining.translator.prototypes.util.TranslationTable;
import de.tu_berlin.textmining.translator.prototypes.util.TranslationTable.TranslationCandidate;
import de.tu_berlin.textmining.translator.prototypes.util.WordPair;

public class TranslationModel {

	private static final Logger LOGGER = Logger.getLogger(TranslationModel.class);
	private static final Level DEFAULT_LOG_LEVEL = Level.DEBUG;
	private static final boolean DEFAULT_REDUCE = false;
	private static final int MAX_ITERATION = 4;

	private final NGramLanguageModel languageModel;
	private final Language source;
	private final Language target;
	private final String sourceFile;
	private final String targetFile;
	private final Set<String> sourceVocab;
	private final Set<String> targetVocab;
	private final TranslationTable translationTable;

	public TranslationModel(final int maxDegree, final Language sourceLanguage, final Language targetLanguage,
			final String sourceFile, final String targetFile) {
		this(maxDegree, sourceLanguage, targetLanguage, sourceFile, targetFile, DEFAULT_LOG_LEVEL);
	}

	public TranslationModel(final int maxDegree, final Language sourceLanguage, final Language targetLanguage,
			final String sourceFile, final String targetFile, final Level logLevel) {
		BasicConfigurator.configure();
		LOGGER.setLevel(logLevel);
		this.languageModel = new NGramLanguageModel(maxDegree);
		this.source = sourceLanguage;
		this.target = targetLanguage;
		this.sourceFile = sourceFile;
		this.targetFile = targetFile;
		this.translationTable = new TranslationTable();
		this.sourceVocab = new HashSet<String>();
		this.targetVocab = new HashSet<String>();
	}

	public void init() throws IOException {
		LOGGER.debug("Initializing...");
		BufferedReader sourceCorpus = new BufferedReader(new FileReader(this.sourceFile));
		BufferedReader targetCorpus = new BufferedReader(new FileReader(this.targetFile));
		String sLine, tLine = null;

		// Build vocabularies and count co-occurrences
		while ((sLine = sourceCorpus.readLine()) != null && (tLine = targetCorpus.readLine()) != null) {
			this.languageModel.train(tLine);
			SentenceTokenizer srcTok = new SentenceTokenizer(sLine);
			SentenceTokenizer tgtTok = new SentenceTokenizer(tLine);

			for (String s : srcTok) {
				this.sourceVocab.add(s);
				for (String t : tgtTok) {
					this.targetVocab.add(t);
					this.translationTable.setCount(s, t, this.translationTable.getCount(s, t) + 1);
				}
			}
		}
		// TODO how to calculate the uniform distribution?
		// just 1 / (size of the target vocabulary)
		// or all co-occurring words are equal likely
		this.translationTable.initUniformProb();
	}

	public void train() throws IOException {
		this.train(DEFAULT_REDUCE);
	}

	public void train(boolean reduce) throws IOException {
		BufferedReader sourceCorpus;
		BufferedReader targetCorpus;
		this.init();
		// double oldPP = 0;
		// double curPP = this.calculateLogPerplexity();
		int i = 0;
		while (i < MAX_ITERATION) {
			LOGGER.debug("Expactation Maximation iteration " + (i + 1));
			long stepStart = System.currentTimeMillis();
			sourceCorpus = new BufferedReader(new FileReader(this.sourceFile));
			targetCorpus = new BufferedReader(new FileReader(this.targetFile));
			String sLine, tLine = null;

			// Reset translation count
			Map<WordPair, Double> translationCount = new HashMap<WordPair, Double>();

			// Foreach sentence pair (s, t)
			LOGGER.debug("Collecting fractional counts...");
			long fcStart = System.currentTimeMillis();
			while ((sLine = sourceCorpus.readLine()) != null && (tLine = targetCorpus.readLine()) != null) {
				SentenceTokenizer srcTok = new SentenceTokenizer(sLine);
				SentenceTokenizer tgtTok = new SentenceTokenizer(tLine);

				// Collect fractional counts
				for (String t : tgtTok) {
					double total = 0;
					for (String s : srcTok) {
						total += this.translationTable.getProb(s, t);
					}

					for (String s : srcTok) {
						WordPair pair = new WordPair(s, t);
						Double tc = translationCount.get(pair);
						if (tc == null) {
							tc = 0.0;
						}
						translationCount.put(pair, tc + (this.translationTable.getProb(s, t) / total));
					}
				}
			}
			long fcEnd = System.currentTimeMillis();
			LOGGER.debug("Duration: " + DurationFormatUtils.formatDurationWords((fcEnd - fcStart), true, false));

			// Normalize counts
			LOGGER.debug("Normalizing counts...");
			long ncStart = System.currentTimeMillis();
			Map<String, Double> counts = new HashMap<String, Double>();
			for (Entry<WordPair, Double> entry : translationCount.entrySet()) {
				String s = entry.getKey().getFirst();
				Double c = counts.get(s);
				if (c != null) {
					counts.put(s, c + entry.getValue());
				} else {
					counts.put(s, entry.getValue());
				}
			}
			long ncEnd = System.currentTimeMillis();
			LOGGER.debug("Duration: " + DurationFormatUtils.formatDurationWords((ncEnd - ncStart), true, false));

			LOGGER.debug("Updating translation probabilites...");
			long tpStart = System.currentTimeMillis();
			for (Entry<WordPair, Double> entry : translationCount.entrySet()) {
				String s = entry.getKey().getFirst();
				String t = entry.getKey().getSecond();
				this.translationTable.setProb(s, t, translationCount.get(entry.getKey()) / counts.get(s));
				LOGGER.trace("T(" + t + "|" + s + ") = " + this.translationTable.getProb(s, t));
			}
			long tpEnd = System.currentTimeMillis();
			LOGGER.debug("Duration: " + DurationFormatUtils.formatDurationWords((tpEnd - tpStart), true, false));

			// oldPP = curPP;
			// curPP = this.calculateLogPerplexity();
			// LOGGER.trace("Perplexity: " +
			// this.log2(this.calculateLogPerplexity()));
			long stepEnd = System.currentTimeMillis();
			LOGGER.debug("Iteration time:" + DurationFormatUtils.formatDurationWords((stepEnd - stepStart), true, false));
			i++;
		}

		LOGGER.debug("Statistics");
		LOGGER.debug("Number of " + this.source.name() + " words:\t" + this.sourceVocab.size());
		LOGGER.debug("Number of " + this.target.name() + " words:\t" + this.targetVocab.size());
		LOGGER.debug("Number of translation probabilities:\t" + this.translationTable.size());
		if (reduce) {
			this.translationTable.reduce();
			LOGGER.debug("Number of translation probabilities (after reduction):\t" + this.translationTable.size());
		}
	}

	public String translate(final String input) {
		LOGGER.debug("Translating " + input);
		List<String> output = Lists.newArrayList();
		int pos = 0;
		SentenceTokenizer tokenizer = new SentenceTokenizer(input);

		// Build first translation with most likely translations
		for (String word : tokenizer) {
			SortedSet<TranslationCandidate> candidates = this.getTranslations(word);
			LOGGER.debug("Possibilities for " + word + " " + candidates);
			if (!candidates.isEmpty()) {
				output.add(candidates.first().getTargetWord());
			}
		}
		LOGGER.debug("Initial translation " + output);

		// Find translation candidates that maximize P(e)P(f|e)
		double maxProb = Double.NEGATIVE_INFINITY;
		for (String s : tokenizer) {
			SortedSet<TranslationCandidate> candidates = this.getTranslations(s);
			for (TranslationCandidate c : candidates) {
				if (!output.get(pos).equals(c.getTargetWord())) {
					List<String> hypothesis = Lists.newArrayList(output);
					hypothesis.set(pos, c.getTargetWord());
					double prob = this.translationProbability(tokenizer, hypothesis)
							* this.languageModel.getSentenceLogProbability(hypothesis);
					if (prob > maxProb) {
						output = hypothesis;
					}
				}
			}
			pos++;
		}

		StringBuilder strBld = new StringBuilder();
		for (String word : output) {
			strBld.append(word);
			strBld.append(" ");
		}

		return strBld.toString();
	}

	public String[] reorderSentence(String[] sentence) {
		if (sentence.length > 1) {
			// double maxProb = 0.0;
		}
		return sentence;
	}

	public boolean isConverged(final double oldPerplexity, final double currentPerplexity) {
		return (oldPerplexity == currentPerplexity) ? true : false;
	}

	public double translationProbability(final String sourceSentence, final String targetSentence) {
		SentenceTokenizer srcTok = new SentenceTokenizer(sourceSentence);
		SentenceTokenizer tgtTok = new SentenceTokenizer(targetSentence);
		return this.translationProbability(srcTok, tgtTok);
	}

	public double translationProbability(final Iterable<String> sourceSentence, final Iterable<String> targetSentence) {
		boolean first = true;
		int sLen = 0;
		int tLen = 0;

		double outerSum = 0.0;
		for (String t : targetSentence) {
			tLen++;
			double innerSum = 0.0;
			for (String s : sourceSentence) {
				if (first) {
					sLen++;
				}
				innerSum += this.translationTable.getProb(s, t);
			}
			outerSum += innerSum;

			first = (first) ? false : true;
		}

		return outerSum / Math.pow(sLen, tLen);
	}

	public double calculateLogPerplexity() throws IOException {
		double sum = 0.0;
		BufferedReader sourceCorpus = new BufferedReader(new FileReader(this.sourceFile));
		BufferedReader targetCorpus = new BufferedReader(new FileReader(this.targetFile));
		String sLine, tLine = null;

		while ((sLine = sourceCorpus.readLine()) != null && (tLine = targetCorpus.readLine()) != null) {
			sum += log2(this.translationProbability(sLine, tLine));
		}

		return -sum;
	}

	private static double log2(final double value) {
		return Math.log(value) / Math.log(2.0);
	}

	public Language getSourceLanguage() {
		return this.source;
	}

	public Language getTargetLanguage() {
		return this.target;
	}

	public SortedSet<TranslationTable.TranslationCandidate> getTranslations(final String word) {
		return this.translationTable.getTranslations(word);
	}

	public static void main(String... args) throws IOException {
		if (args.length == 2) {
			String source = args[0];
			String target = args[1];

			TranslationModel tm = new TranslationModel(3, Language.GERMAN, Language.ENGLISH, source, target);
			tm.train(true);
			System.out.println("das: " + tm.getTranslations("das"));
			System.out.println("buch: " + tm.getTranslations("buch"));
			System.out.println(tm.translationTable.getProb("das", "the"));
			System.out.println(tm.languageModel.getSentenceLogProbability(Arrays.asList("das", "buch")));
			System.out.println("wenn->" + tm.getTranslations("wenn"));
			System.out.println("man->" + tm.getTranslations("man"));
			System.out.println("darauf->" + tm.getTranslations("darauf"));
			System.out.println("achtet->" + tm.getTranslations("achtet"));
			System.out.println(tm.translate("wenn man darauf achtet"));
			System.out.println(tm.translate("ich bin zufrieden"));
		} else {
			System.out.println("Usage:\nTranslationModel sourceCorpus targetCorpus");
		}
	}
}
