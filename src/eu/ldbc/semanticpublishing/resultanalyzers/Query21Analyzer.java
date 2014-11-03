package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXQuery21TemplateTransformer;

/**
 * A class used to extract title, year, month, day from a query21.txt's result.
 */
public class Query21Analyzer {
	
	public ArrayList<String> collectDatesList(String result) throws UnsupportedEncodingException {
		SAXQuery21TemplateTransformer transformer = new SAXQuery21TemplateTransformer();
		transformer.transform(new ByteArrayInputStream(result.getBytes("UTF-8")));
		return transformer.getDatesList();
	}
}