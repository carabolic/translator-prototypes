package de.tu_berlin.textmining.translator.prototypes.reader;

import java.io.IOException;

import de.tu_berlin.textmining.translator.prototypes.data.lexicon.DictionaryEntry;

public interface DictionaryFileReader {
	String getPath();
	DictionaryEntry readEntry() throws IOException;
}
