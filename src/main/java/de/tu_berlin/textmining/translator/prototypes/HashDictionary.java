package de.tu_berlin.textmining.translator.prototypes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import de.tu_berlin.textmining.translator.prototypes.reader.ElcombriReader;
import de.tu_berlin.textmining.translator.prototypes.reader.InputReader;

public class HashDictionary implements Dictionary {
	/** holds the dictionary german->english: HashMap<German, English> */
	private Map<String, Set<String>> translationData = new HashMap<String, Set<String>>();
	/** holds the additional information for german: HashMap<German, Attribute> */
	private Map<String, Set<String>> attributeData = new HashMap<String, Set<String>>();

	public HashDictionary() {

	}

	public HashDictionary(String path) throws IOException, FileNotFoundException {
		this.load(path);
	}

	public void load(String path) throws IOException, FileNotFoundException {
		InputReader reader = new ElcombriReader(path);
		this.load(reader);
	}

	public void load(InputReader reader) throws IOException {
		DictPair pair = null;
		while ((pair = reader.readWord()) != null) {
			String ger = pair.getGerman();
			String eng = pair.getEnglish();
			Set<String> attrs = pair.getAttribute();
			this.addWord(ger, eng, attrs);
		}
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
	}

	/**
	 * Parses a dictionary file from dict.cc into HashMap NOTE: Ignores any
	 * Phrases in (..) [..]; Stores Phrases in {..} as Attribute
	 * 
	 * @param path
	 *          file path
	 */
	/*
	 * public void parseDictFile(String path) { BufferedReader br = null; try { br
	 * = new BufferedReader(new InputStreamReader(new FileInputStream(path),
	 * "Cp1252")); String aLine; String[] aTempLine; String pattern1 =
	 * "\\([^\\)]*\\)"; // replace (..) with empty string String pattern2 =
	 * "\\[[^\\]]*\\]"; // replace [..)]with empty string String pattern3 =
	 * "\\{[^\\}]*\\}"; // replace {..} with empt string String pattern4 =
	 * "[^\\{^\\}]*\\{"; // replace ..{ with empty string String pattern5 =
	 * "\\}[^\\{]*"; // replace }.. with empty string String eng = ""; String att
	 * = ""; String ger = ""; ArrayList<String> tempEngList; ArrayList<String>
	 * tempAttList; String[] attList; int engIndex = 0; while (br.ready()) {
	 * engIndex = 0; att = ""; aLine = br.readLine(); // split a line from dict.cc
	 * dictionary into german and english part aTempLine = aLine.split("::", 2);
	 * if (aTempLine.length == 2) { ger = aTempLine[0].replaceAll(pattern1, "");
	 * // remove all (..) ger = ger.replaceAll(pattern2, ""); // remove all [..]
	 * ger = ger.replaceAll(pattern3, ""); // remove all {..} ger = ger.trim(); //
	 * remove leading and tailing whitespaces ger = ger.toLowerCase(); eng =
	 * aTempLine[1].replaceAll(pattern1, ""); // remove all (..) eng =
	 * eng.replaceAll(pattern2, ""); // remove all [..] eng =
	 * eng.replaceAll(pattern3, ""); // remove all {..} eng = eng.trim(); //
	 * remove leading and tailing whitespaces if ((ger.compareToIgnoreCase("") !=
	 * 0) && (eng.compareToIgnoreCase("") != 0)) { if ((aTempLine[0].contains("{")
	 * == true) &&
	 * (aTempLine[0].substring(aTempLine[0].indexOf('{')).contains("}") == true))
	 * { att = aTempLine[0].replaceAll(pattern4, "{"); // replace "...{" // with
	 * "{" att = att.replaceAll(pattern5, ""); // replace "}.." with "" attList =
	 * att.substring(1).split("\\{"); for (int i = 0; i < attList.length; i++) {
	 * att = attList[i].trim(); // remove leading and tailing // whitespaces if
	 * (translationData.containsKey(ger) == true) { tempEngList =
	 * translationData.get(ger); tempAttList = attributeData.get(ger); if
	 * (tempEngList.contains(eng) == true) { engIndex = tempEngList.indexOf(eng);
	 * if (tempAttList.get(engIndex).compareTo(att) != 0) { tempEngList.add(eng);
	 * tempAttList.add(att); translationData.put(ger, tempEngList);
	 * attributeData.put(ger, tempAttList); } } else { tempEngList.add(eng);
	 * tempAttList.add(att); translationData.put(ger, tempEngList);
	 * attributeData.put(ger, tempAttList); } } else { tempEngList = new
	 * ArrayList<String>(); tempAttList = new ArrayList<String>();
	 * tempEngList.add(eng); tempAttList.add(att); translationData.put(ger,
	 * tempEngList); attributeData.put(ger, tempAttList); } } } else { if
	 * (translationData.containsKey(ger) == true) { tempEngList =
	 * translationData.get(ger); tempAttList = attributeData.get(ger); if
	 * (tempEngList.contains(eng) == true) { engIndex = tempEngList.indexOf(eng);
	 * if (tempAttList.get(engIndex).compareTo(att) != 0) { tempEngList.add(eng);
	 * tempAttList.add(att); translationData.put(ger, tempEngList);
	 * attributeData.put(ger, tempAttList); } } else { tempEngList.add(eng);
	 * tempAttList.add(att); translationData.put(ger, tempEngList);
	 * attributeData.put(ger, tempAttList); } } else { tempEngList = new
	 * ArrayList<String>(); tempAttList = new ArrayList<String>();
	 * tempEngList.add(eng); tempAttList.add(att); translationData.put(ger,
	 * tempEngList); attributeData.put(ger, tempAttList); } } } } } } catch
	 * (Exception ex) { throw new RuntimeException(ex); } finally { try {
	 * br.close(); } catch (IOException e) { e.printStackTrace(); } } }
	 */

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

	public boolean train() {
		// TODO Auto-generated method stub
		return false;
	}
}
