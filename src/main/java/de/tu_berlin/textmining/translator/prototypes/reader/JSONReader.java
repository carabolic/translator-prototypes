package de.tu_berlin.textmining.translator.prototypes.reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import de.tu_berlin.textmining.translator.prototypes.DictPair;

public class JSONReader implements InputReader {
	
	private final String path;
	private final BufferedReader bufReader;
	
	public JSONReader(String path) throws FileNotFoundException {
		this.path = path;
		this.bufReader = new BufferedReader(new FileReader(path));
	}

	public String getPath() {
		return this.path;
	}

	public DictPair readWord() throws IOException {
		String line;
		if ((line = bufReader.readLine()) != null) {
			
		}
		// TODO Auto-generated method stub
		return null;
	}

}
