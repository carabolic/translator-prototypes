package de.tu_berlin.textmining.translator.prototypes.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;

import de.tu_berlin.textmining.translator.prototypes.DictPair;

public class ElcombriReader implements InputReader {

	private final String path;
	private final BufferedReader bufReader;

	private String pattern1 = "\\([^\\)]*\\)"; // replace (..) with empty string
	private String pattern2 = "\\[[^\\]]*\\]"; // replace [..)]with empty string
	private String pattern3 = "\\{[^\\}]*\\}"; // replace {..} with empt string
	private String pattern4 = "[^\\{^\\}]*\\{"; // replace ..{ with empty string
	private String pattern5 = "\\}[^\\{]*"; // replace }.. with empty string

	public ElcombriReader(final String path) throws FileNotFoundException, UnsupportedEncodingException {
		this.path = path;
		this.bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "Cp1252"));
	}

	public String getPath() {
		return this.path;
	}

	public DictPair readWord() throws IOException {
		DictPair result = null;
		String line;
		
		Parsing:
		if ((line = bufReader.readLine()) != null) {
			while (this.isIrrelevant(line)) {
				line = bufReader.readLine();
				if (line == null) {
					break Parsing;
				}
			}
			String[] aTempLine;
			String eng = "";
			String att = "";
			String ger = "";
			String[] attList = new String[0];
			// split a line from dict.cc dictionary into german and english part
			aTempLine = line.split("::", 2);

			if (aTempLine.length == 2) {
				ger = aTempLine[0].replaceAll(this.pattern1, "") // remove all (..)
						.replaceAll(this.pattern2, "") // remove all [..]
						.replaceAll(this.pattern3, "") // remove all {..}
						.trim() // remove leading and tailing whitespaces
						.toLowerCase();

				eng = aTempLine[1].replaceAll(pattern1, "") // remove all (..)
						.replaceAll(this.pattern2, "") // remove all [..]
						.replaceAll(this.pattern3, "") // remove all {..}
						.trim(); // remove leading and tailing whitespaces

				if (!(ger.equals("") && eng.equals(""))) {
					if ((aTempLine[0].contains("{") == true)
							&& (aTempLine[0].substring(aTempLine[0].indexOf('{')).contains("}") == true)) {
						att = aTempLine[0].replaceAll(pattern4, "{") // replace "...{"
																													// with "{"
								.replaceAll(pattern5, ""); // replace "}.." with ""
						attList = att.substring(1).split("\\{");
					}
					
					result = new DictPair(ger, eng, new HashSet<String>(Arrays.asList(attList)));
				}
			}
		}

		return result;
	}
	
	private boolean isIrrelevant(String line) {
		if (line.startsWith("#")) {
			return true;
		}
		else if (line.isEmpty()) {
			return true;
		}
		else {
			return false;
		}
	}
}
