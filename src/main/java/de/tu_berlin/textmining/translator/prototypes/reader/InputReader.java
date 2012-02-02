package de.tu_berlin.textmining.translator.prototypes.reader;

import java.io.IOException;

import de.tu_berlin.textmining.translator.prototypes.DictPair;

public interface InputReader {
	String getPath();
	DictPair readWord() throws IOException;
}
