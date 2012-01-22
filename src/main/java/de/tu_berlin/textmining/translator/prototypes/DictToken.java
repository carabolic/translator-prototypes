package de.tu_berlin.textmining.translator.prototypes;

import java.util.ArrayList;

public class DictToken
{
	private String german;
	private ArrayList<String> attribute;
	private ArrayList<String> english;

	public DictToken()
	{
		this.german = "";
		this.attribute = new ArrayList<String>();
		this.english = new ArrayList<String>();
	}
	
	public DictToken(String ger, ArrayList<String> att, ArrayList<String> en)
	{
		this.german = ger;
		this.attribute = att;
		this.english = en;
	}

	public String getGerman()
	{
		return this.german;
	}

	public ArrayList<String> getAttribute()
	{
		return this.attribute;
	}

	public ArrayList<String> getEnglish()
	{
		return this.english;
	}
	public void setGerman(String ger)
	{
		this.german = ger;
	}

	public void setAttribute(ArrayList<String> att)
	{
		this.attribute = att;
	}

	public void setEnglish(ArrayList<String> en)
	{
		this.english = en;
	}
}