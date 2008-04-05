package com.polenta.e.schiz.orario.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class OrarioPartitionScanner extends RuleBasedPartitionScanner
{
	public String ELEMENT = "__element";

	public OrarioPartitionScanner()
	{
		super();
		IToken element= new Token(ELEMENT);

		List<IPredicateRule> rules= new ArrayList<IPredicateRule>();

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\')); 

		// Add rules for multi-line comments and javadoc.
		rules.add(new MultiLineRule("<", ">", element, (char) 0, true)); 

		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
