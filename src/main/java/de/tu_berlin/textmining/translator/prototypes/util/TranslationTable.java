package de.tu_berlin.textmining.translator.prototypes.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.collect.TreeBasedTable;

public class TranslationTable {

	private static final double PROBABILITY_THRESHOLD = 0.01;

	private final Table<String, String, Integer> pairCounts;
	private final Table<String, String, Double> translationProbs;

	public TranslationTable() {
		this.pairCounts = TreeBasedTable.create();
		this.translationProbs = TreeBasedTable.create();
	}

	public double getProb(final String source, final String target) {
		Double prob = this.translationProbs.get(source, target);
		return (prob != null) ? prob : 0.0;
	}

	public void setProb(final String source, final String target, final double prob) {
		this.translationProbs.put(source, target, prob);
	}

	public int getCount(final String source, final String target) {
		Integer count = this.pairCounts.get(source, target);
		return (count != null) ? count : 0;
	}

	public void setCount(final String source, final String target, final int count) {
		this.pairCounts.put(source, target, count);
	}

	public int getCount(final String source) {
		int total = 0;
		Collection<Integer> cooccurrenceCounts = this.pairCounts.row(source).values();
		for (int c : cooccurrenceCounts) {
			total += c;
		}
		return total;
	}

	public void initUniformProb() {
		Set<String> rowKeys = this.pairCounts.rowKeySet();
		for (String key : rowKeys) {
			int total = this.getCount(key);
			Map<String, Integer> rowColMap = this.pairCounts.row(key);
			for (String colKey : rowColMap.keySet()) {
				this.translationProbs.put(key, colKey, 1.0 / total);
			}
		}
	}

	public SortedSet<TranslationCandidate> getTranslations(final String word) {
		SortedSet<TranslationCandidate> sortedTranslations = new TreeSet<TranslationCandidate>();
		Map<String, Double> candidates = this.translationProbs.row(word);
		for (Map.Entry<String, Double> entry : candidates.entrySet()) {
			sortedTranslations.add(new TranslationCandidate(entry.getKey(), entry.getValue()));
		}

		return sortedTranslations;
	}

	public void reduce() {
		Iterator<Cell<String, String, Double>> cellIter = this.translationProbs.cellSet().iterator();
		Cell<String, String, Double> cell = null;
		while (cellIter.hasNext()) {
			cell = cellIter.next();
			if (cell.getValue() < PROBABILITY_THRESHOLD) {
				cellIter.remove();
			}
		}
	}

	public int size() {
		return this.translationProbs.cellSet().size();
	}

	public static class TranslationCandidate implements Comparable<TranslationCandidate> {
		private final String target;
		private final double prob;

		public TranslationCandidate(final String target, final double prob) {
			this.target = target;
			this.prob = prob;
		}

		public String getTargetWord() {
			return this.target;
		}

		public double getTranslationProb() {
			return this.prob;
		}

		public int compareTo(TranslationCandidate o) {
			return -ComparisonChain.start().compare(prob, o.prob).compare(target, o.target).result();
		}

		@Override
		public String toString() {
			return this.target + "->" + this.prob;
		}
	}
}
