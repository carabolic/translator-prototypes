package de.tu_berlin.textmining.translator.prototypes.util;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class CooccurrenceCounter {
	
	private static final int DEFAULT_INCREMENT = 1;
	
	private final Table<String, String, Integer> table;
	
	public CooccurrenceCounter() {
		this.table = TreeBasedTable.create();
	}
	
	public void increment(final String word1, final String word2) {
		this.increment(word1, word2, DEFAULT_INCREMENT);
	}
	
	public void increment(final String word1, final String word2, final int increment) {
		Integer count = this.table.get(word1, word2);
		if (count != null) {
			this.table.put(word1, word2, count + increment);
		} else {
			this.table.put(word1, word2, increment);
		}
	}
	
	public int getAbsoluteCount(final String word1, final String word2) {
		Integer count = this.table.get(word1, word2);
		return (count != null) ? count : 0;
	}
	
	public double getRelativeCount(final String word1, final String word2) {
		long total = 0l;
		int count = this.getAbsoluteCount(word1, word2);
		Collection<Integer> counts = this.table.row(word1).values();
		for (int c : counts) {
			total += c;
		}
		return (total > 0) ? (double) count / total : 0;
	}
	
	public Map<String, Integer> getCooccurences(final String word) {
		return this.table.row(word);
	}
}
