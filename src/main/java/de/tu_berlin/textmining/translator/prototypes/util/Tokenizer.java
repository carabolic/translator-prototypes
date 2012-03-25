package de.tu_berlin.textmining.translator.prototypes.util;

import java.util.Iterator;

public interface Tokenizer extends Iterable<String> {
	
	public Iterator<String> iterator();
}
