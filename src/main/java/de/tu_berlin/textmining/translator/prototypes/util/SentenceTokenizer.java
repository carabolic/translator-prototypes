package de.tu_berlin.textmining.translator.prototypes.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

@SuppressWarnings("deprecation")
public class SentenceTokenizer implements Tokenizer {

	private final String input;

	public SentenceTokenizer(final String input) {
		this.input = input;
	}
	
	public String getInput() {
		return this.input;
	}

	public Iterator<String> iterator() {
		return new Iterator<String>() {
			private boolean first = true;
			private boolean hasNext = true;
			private final Reader inputReader = new StringReader(input);
			private final StandardTokenizer stdTok = new StandardTokenizer(Version.LUCENE_35, inputReader);

			public boolean hasNext() {
				if (this.first) {
					this.first = false;
					this.hasNext = this.increment();
				}
				return this.hasNext;
			}

			public String next() {
				if (this.first) {
					this.first = false;
				}
				if (this.hasNext) {
					String term = this.stdTok.getAttribute(TermAttribute.class).toString();
					this.hasNext = this.increment();
					return term;
				} else {
					throw new NoSuchElementException();
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			private boolean increment() {
				try {
					return this.stdTok.incrementToken();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
}
