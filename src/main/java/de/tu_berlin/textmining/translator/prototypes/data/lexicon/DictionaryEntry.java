package de.tu_berlin.textmining.translator.prototypes.data.lexicon;

import java.util.Set;

public class DictionaryEntry {
	private final String source;
	private final String target;
	private final Set<String> attributes;
	private final Set<String> contexts;

	public DictionaryEntry(String source, String target, Set<String> attributes, Set<String> contexts) {
		this.source = source;
		this.target = target;
		this.attributes = attributes;
		this.contexts = contexts;
	}

	public String getSource() {
		return this.source;
	}

	public String getTarget() {
		return this.target;
	}

	public Set<String> getAttributes() {
		return this.attributes;
	}

	public Set<String> getContexts() {
		return this.contexts;
	}
}