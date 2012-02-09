package de.tu_berlin.textmining.translator.prototypes.data;

public enum Language {
	GERMAN("de"),
	ENGLISH("en");
	
	private final String language;
	
	private Language(final String language) {
		this.language = language;
	}
	
	@Override
	public String toString() {
		return this.language;
	}
}
