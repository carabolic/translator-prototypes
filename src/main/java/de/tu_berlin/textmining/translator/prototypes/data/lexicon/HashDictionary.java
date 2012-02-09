package de.tu_berlin.textmining.translator.prototypes.data.lexicon;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import de.tu_berlin.textmining.translator.prototypes.reader.DictionaryFileReader;

/**
 * Implementation of the Dictionary interface using a HashMap for storing words and phrases.
 * @author Christoph Brücke (christoph.bruecke@camput.tu-berlin.de)
 *
 */
public class HashDictionary implements Dictionary {

	private Map<String, Set<String>> translationData = new HashMap<String, Set<String>>();	
	private Map<String, Set<String>> attributeData = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> contextData = new HashMap<String, Set<String>>();

	public HashDictionary() {}

	public HashDictionary(DictionaryFileReader reader) throws IOException {
		this.load(reader);
	}

	public void load(DictionaryFileReader reader) throws IOException {
		DictionaryEntry dictEntry = null;
		while ((dictEntry = reader.readEntry()) != null) {
			String source = dictEntry.getSource();
			String target = dictEntry.getTarget();
			Set<String> attrs = dictEntry.getAttributes();
			this.addWord(source, target, attrs);
		}
	}

	public void addWord(final DictionaryEntry dictEntry) {
		String source = dictEntry.getSource().toLowerCase();
		String target = dictEntry.getTarget().toLowerCase();
		Set<String> attributes = dictEntry.getAttributes();
		Set<String> contexts = dictEntry.getContexts();
		
		Set<String> targets;
		if (translationData.containsKey(source)) {
			targets = this.translationData.get(source);
		} else {
			targets = new HashSet<String>();
		}
		targets.add(target);
		
		this.translationData.put(source, targets);
		this.attributeData.put(source, attributes);
		this.contextData.put(source, contexts);
	}
	
	public void addWord(final String src, final String dest, final Set<String> attrs) {
		final String srcLower = src.toLowerCase();
		final String destLower = dest.toLowerCase();

		// Add translation data (e.g. sonne -> sun)
		Set<String> destSet;
		if (this.translationData.containsKey(srcLower)) {
			destSet = this.translationData.get(srcLower);
		} else {
			destSet = new HashSet<String>();
		}
		destSet.add(destLower);
		this.translationData.put(srcLower, destSet);

		// Add attribute data
		// TODO attribute to lowercase
		Set<String> attrsSet;
		if (this.attributeData.containsKey(srcLower)) {
			attrsSet = this.attributeData.get(srcLower);
		} else {
			attrsSet = new HashSet<String>();
		}
		attrsSet.addAll(attrs);
		this.attributeData.put(srcLower, attrsSet);
	}

	public void addWord(String src, Set<String> dest, Set<String> attrs) {
		// TODO implement me
		throw new UnsupportedOperationException();
	}

	/**
	 * reads the Dictionary from a JSON file into HashMap
	 * 
	 * @param path
	 *          : total file path
	 */
	/*
	 * public void parseJSONFile(String path) { BufferedReader br = null; try {
	 * Gson gsonParser = new Gson(); br = new BufferedReader(new
	 * FileReader(path)); String aLine = br.readLine(); while (aLine != null) {
	 * DictPair aToken = gsonParser.fromJson(aLine, DictPair.class);
	 * translationData.put(aToken.getGerman(), aToken.getEnglish());
	 * attributeData.put(aToken.getGerman(), aToken.getAttribute()); aLine =
	 * br.readLine(); } br.close(); } catch (IOException ioe) { throw new
	 * RuntimeException(ioe); } finally { if (br != null) { try { br.close(); }
	 * catch (IOException ioe) { ioe.printStackTrace(); } } } }
	 */

	/**
	 * Creates a JSON file of the dictionary
	 * 
	 * @param path
	 *          : full file path
	 */
	/*
	 * public void createJSONFile(String path) { try { String ger;
	 * ArrayList<String> att; ArrayList<String> eng; DictPair aToken; Gson
	 * gsonParser = new Gson(); FileWriter writer = new FileWriter(path); for
	 * (Map.Entry<String, ArrayList<String>> anEntry : translationData.entrySet())
	 * { ger = anEntry.getKey(); att = attributeData.get(ger); eng =
	 * anEntry.getValue(); aToken = new DictPair(ger, att, eng);
	 * writer.write(gsonParser.toJson(aToken) +
	 * System.getProperty("line.separator")); } writer.close(); } catch (Exception
	 * ex) { throw new RuntimeException(ex); } }
	 */
	
	public String[] translate(String[] sentences) {
		throw new UnsupportedOperationException();
	}

	public String translateSentence(String sentence) {
		StringTokenizer strTok = new StringTokenizer(sentence);
		StringBuilder strBld = new StringBuilder();
		while (strTok.hasMoreTokens()) {
			String word = strTok.nextToken().toLowerCase();
			String[] transWords = this.translateWord(word);
			if (transWords != null) {
				strBld.append(HashDictionary.wordArrayToString(transWords));
			} else {
				strBld.append('[');
				strBld.append(word);
				strBld.append(']');
			}
			strBld.append(" ");
		}

		return strBld.toString();
	}

	public String[] translateWord(String word) {
		if (this.translationData.containsKey(word)) {
			return this.translationData.get(word).toArray(new String[0]);
		}
		return null;
	}

	public void translateWord(String[] sentence, int index) {

	}

	private static String wordArrayToString(String[] words) {
		StringBuilder strBld = new StringBuilder();
		if (words.length < 2) {
			strBld.append(words[0]);
		} else {
			strBld.append('(');
			for (int i = 0; i < words.length; i++) {
				strBld.append(words[i]);
				if (i < words.length - 1) {
					strBld.append('/');
				}
			}
			strBld.append(')');
		}

		return strBld.toString();
	}
}
