package de.tu_berlin.textmining.translator.prototypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DictPair
{
	private final String german;
	private final Set<String> attributes;
	private final String english;
	
	public DictPair(String ger, String en, Set<String> att)
	{
		this.german = ger;
		this.attributes = att;
		this.english = en;
	}

	public String getGerman()
	{
		return this.german;
	}

	public Set<String> getAttribute()
	{
		return this.attributes;
	}

	public String getEnglish()
	{
		return this.english;
	}
}