
package eu.ldbc.semanticpublishing.resultanalyzers.saxparsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



/**
 * SAX Parser for transforming an RDF-XML result stream into a strings list
 */
public class SAXPlacesTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	
	
	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String PLACESID_BINDING_ELEMENT_ATTIRBUTE = "placesId";
	private static final String PLACESNAME_BINDING_ELEMENT_ATTIRBUTE = "placesName";
	private static final String PLACESCOMMENT_BINDING_ELEMENT_ATTIRBUTE = "placesComment";
	private static final String PLACESCOUNTRY_BINDING_ELEMENT_ATTIRBUTE = "placesCountry";
	private static final String PLACESGEO_BINDING_ELEMENT_ATTIRBUTE = "placesGeo";
	
	
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	
		
	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder nameSb;
	private StringBuilder commentSb;
	private StringBuilder countrySb;
	private StringBuilder geoSb;

	private ArrayList<String> placesIdsList; 
	private ArrayList<String> placesNamesList;
	private ArrayList<String> placesCommentsList;
	private ArrayList<String> placesCountriesList;
	private ArrayList<String> placesGeosList;
	
	@Override
	public void startDocument() throws SAXException {
		placesIdsList = new ArrayList<String>();
		placesNamesList = new ArrayList<String>();
		placesCommentsList = new ArrayList<String>();
		placesCountriesList = new ArrayList<String>();
		placesGeosList = new ArrayList<String>();
		
	}
	
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		updateFlagsState(localName, true);
	
		if (resultElementBeginFlag) {
			String attributeName = atts.getValue(NAME);
			if (attributeName != null && localName.equals(BINDING)) {
				currentBindingName = attributeName;
			}
			
			if (entityConstructionBegin == false) {
				uriSb = new StringBuilder();
				nameSb = new StringBuilder();
				commentSb = new StringBuilder();
				countrySb = new StringBuilder();
				geoSb = new StringBuilder();
				
				currentBindingName = "";
				entityConstructionBegin = true;
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		boolean wasInResultElement = false;
		
		if (resultElementBeginFlag && localName.equals(RESULT_XML_ELEMENT)) {
			wasInResultElement = true;
		}
			
		updateFlagsState(localName, false);

		
		if(wasInResultElement) {
			entityConstructionBegin = false;
			currentBindingName = "";
			
			uriSb.insert(0, "<");
			uriSb.append(">");
			
			nameSb.insert(0, "<");
			nameSb.append(">");
				
			commentSb.insert(0, "<");
			commentSb.append(">");
			
			countrySb.insert(0, "<");
			countrySb.append(">");
			
			geoSb.insert(0, "<");
			geoSb.append(">");
						
				
			placesIdsList.add(uriSb.toString());
			placesNamesList.add(nameSb.toString());
			placesCommentsList.add(commentSb.toString());
			placesCountriesList.add(countrySb.toString());
			placesGeosList.add(geoSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(PLACESID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(PLACESCOUNTRY_BINDING_ELEMENT_ATTIRBUTE)) {
					countrySb.append(str);
				}
			}
				
			if (literalElementBeginFlag) {
				if (currentBindingName.equals(PLACESNAME_BINDING_ELEMENT_ATTIRBUTE)) {
					nameSb.append(str);
				}
				else if (currentBindingName.equals(PLACESCOMMENT_BINDING_ELEMENT_ATTIRBUTE)) {	
					commentSb.append(str);	
				}
				else if (currentBindingName.equals(PLACESGEO_BINDING_ELEMENT_ATTIRBUTE)) {
					geoSb.append(str);
				}
				
			}
		}
	}	
	
	/**
	 * Updates boolean flags about which element is currently visited.
	 */
	private void updateFlagsState(String element, boolean state) {			
		if (element.equals(RESULT_XML_ELEMENT)) {
			resultElementBeginFlag = state;
			
		} else if (element.equals(URI_XML_ELEMENT)) {
			uriElementBeginFlag = state;
		}
		else if (element.equals(LITERAL_XML_ELEMENT)) {
			literalElementBeginFlag = state;
		}
	}
	
	public ArrayList<String> getPlacesIds() {
		return placesIdsList;
	}
	public ArrayList<String> getPlacesNames() {
		return placesNamesList;
	}
	public ArrayList<String> getPlacesComments() {
		return placesCommentsList;
	}
	
	public ArrayList<String> getPlacesCountries() {
		return placesCountriesList;
	}
	public ArrayList<String> getPlacesGeos() {
		return placesGeosList;
	}

	
	@Override
	public void transform(InputStream is) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setValidating(false);
			SAXParser saxParser;
			saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			xmlReader.parse(new InputSource(is));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
