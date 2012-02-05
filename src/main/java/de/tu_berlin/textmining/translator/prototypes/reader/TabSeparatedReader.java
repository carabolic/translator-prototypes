package de.tu_berlin.textmining.translator.prototypes.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import de.tu_berlin.textmining.translator.prototypes.data.DictionaryEntry;
import de.tu_berlin.textmining.translator.prototypes.data.DictionaryEntryBuilder;

public class TabSeparatedReader implements DictionaryFileReader {

	private static final String PARAN_PATTERN = "\\([^\\)]*\\)";
	private static final String SQUARE_BRACKETS_PATTERN = "\\[[^\\]]*\\]";
	private static final String CURLY_BRACES_PATTERN = "\\{[^\\}]*\\}";

	private final String path;
	private final BufferedReader bufReader;

	public TabSeparatedReader(final String path) throws FileNotFoundException {
		this.path = path;
		this.bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	}

	public String getPath() {
		return this.path;
	}

	public DictionaryEntry readEntry() throws IOException {
		DictionaryEntry result = null;
		String line;

		Parsing: if ((line = this.bufReader.readLine()) != null) {
			while (this.isIrrelevant(line)) {
				line = bufReader.readLine();
				if (line == null) {
					break Parsing;
				}
			}
			String[] fields = line.split("\t");
			if (fields.length >= 2) {
				String source = fields[0].replaceAll(PARAN_PATTERN, "").replaceAll(SQUARE_BRACKETS_PATTERN, "")
						.replaceAll(CURLY_BRACES_PATTERN, "").trim().toLowerCase();
				String target = fields[1].replaceAll(PARAN_PATTERN, "").replaceAll(SQUARE_BRACKETS_PATTERN, "")
						.replaceAll(CURLY_BRACES_PATTERN, "").trim().toLowerCase();
				String[] context = new String[0];
				int left = fields[0].indexOf('[');
				int right = fields[0].indexOf(']');
				if (right - left > 0) {
					context = fields[0].substring(left + 1, right).split(",");
				}
				
				String[] attributes = new String[0];
				left = fields[0].indexOf('{');
				right = fields[0].indexOf('}');
				if (right - left > 0) {
					attributes = fields[0].substring(left + 1, right).split(",");
				}

				if (!source.isEmpty()) {
					DictionaryEntryBuilder dictEnBld;
					if (!target.isEmpty()) {
						dictEnBld = new DictionaryEntryBuilder(source, target);
					} else {
						dictEnBld = new DictionaryEntryBuilder(source, source);
					}					
					if (context.length > 0) {
						dictEnBld.addContexts(Arrays.asList(context));
					}
					if (attributes.length > 0) {
						dictEnBld.addAttributes(Arrays.asList(attributes));
					}
					result = dictEnBld.build();
				}
			}
		}

		return result;
	}

	private boolean isIrrelevant(final String line) {
		if (line.startsWith("#")) {
			return true;
		} else if (line.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
