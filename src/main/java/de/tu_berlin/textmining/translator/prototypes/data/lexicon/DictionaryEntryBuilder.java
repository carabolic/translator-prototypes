package de.tu_berlin.textmining.translator.prototypes.data.lexicon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DictionaryEntryBuilder {
	private String source;
	private String target;
	private Set<String> attributes;
	private Set<String> contexts;

	public DictionaryEntryBuilder() {
		this.source = "";
		this.target = "";
		this.attributes = new HashSet<String>();
		this.contexts = new HashSet<String>();
	}

	public DictionaryEntryBuilder(String source, String target) {
		this.source = source;
		this.target = target;
		this.attributes = new HashSet<String>();
		this.contexts = new HashSet<String>();
	}
	
	public DictionaryEntryBuilder setSource(String source) {
		this.source = source;
		return this;
	}
	
	public DictionaryEntryBuilder setTarget(String target) {
		this.target = target;
		return this;
	}

	public DictionaryEntryBuilder addAttribute(String attribute) {
		this.attributes.add(attribute);
		return this;
	}

	public DictionaryEntryBuilder addAttributes(Collection<String> attributes) {
		this.attributes.addAll(attributes);
		return this;
	}

	public DictionaryEntryBuilder addContext(String context) {
		this.contexts.add(context);
		return this;
	}

	public DictionaryEntryBuilder addContexts(Collection<String> contexts) {
		this.contexts.addAll(contexts);
		return this;
	}

	public DictionaryEntry build() {
		return new DictionaryEntry(this.source, this.target, this.attributes, this.contexts);
	}
}
