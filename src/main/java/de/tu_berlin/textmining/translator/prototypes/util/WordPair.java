package de.tu_berlin.textmining.translator.prototypes.util;

import com.google.common.collect.ComparisonChain;

public class WordPair implements Comparable<WordPair> {
	private final String first;
	private final String second;

	public WordPair(final String first, final String second) {
		this.first = first;
		this.second = second;
	}

	public String getFirst() {
		return this.first;
	}

	public String getSecond() {
		return this.second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordPair other = (WordPair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.first + "|" + this.second;
	}

	public int compareTo(WordPair o) {
		return ComparisonChain.start().compare(first, o.first).compare(second, o.second).result();
	}
}
