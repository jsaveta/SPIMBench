package eu.ldbc.semanticpublishing.resultanalyzers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.resultanalyzers.saxparsers.SAXQuery17TemplateTransformer;

/**
 * A class used to extract cwork uris, geonamesids, lat and long properties from a query17.txt result.
 */
public class Query17Analyzer {
	public ArrayList<Entity> collectEntitiesList(String result) throws UnsupportedEncodingException {
		SAXQuery17TemplateTransformer transformer = new SAXQuery17TemplateTransformer();
		transformer.transform(new ByteArrayInputStream(result.getBytes("UTF-8")));
		return transformer.getEntitiesList();
	}
}
