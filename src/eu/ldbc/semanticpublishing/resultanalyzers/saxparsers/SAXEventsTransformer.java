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
public class SAXEventsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	
	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String EVENTSID_BINDING_ELEMENT_ATTIRBUTE = "eventsId";
	private static final String EVENTSLABEL_BINDING_ELEMENT_ATTIRBUTE = "eventsLabel";
	private static final String EVENTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE = "eventsComment";
	private static final String EVENTSCOUNTRY_BINDING_ELEMENT_ATTIRBUTE = "eventsCountry";
	private static final String EVENTSSUBJECT_BINDING_ELEMENT_ATTIRBUTE = "eventsSubject";
	
	
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	
	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder labelSb;
	private StringBuilder commentSb;
	private StringBuilder countrySb;
	private StringBuilder subjectSb;

	private ArrayList<String> eventsIdsList; 
	private ArrayList<String> eventsLabelsList;
	private ArrayList<String> eventsCommentsList;
	private ArrayList<String> eventsCountriesList;
	private ArrayList<String> eventsSubjectsList;
	
	@Override
	public void startDocument() throws SAXException {
		eventsIdsList = new ArrayList<String>();
		eventsLabelsList = new ArrayList<String>();
		eventsCommentsList = new ArrayList<String>();
		eventsCountriesList = new ArrayList<String>();
		eventsSubjectsList = new ArrayList<String>();
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
				labelSb = new StringBuilder();
				commentSb = new StringBuilder();
				countrySb = new StringBuilder();
				subjectSb = new StringBuilder();
				
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
			
			labelSb.insert(0, "<");
			labelSb.append(">");
			
			commentSb.insert(0, "<");
			commentSb.append(">");
			
			countrySb.insert(0, "<");
			countrySb.append(">");
			
			subjectSb.insert(0, "<");
			subjectSb.append(">");
						
			
			eventsIdsList.add(uriSb.toString());
			eventsLabelsList.add(labelSb.toString());
			eventsCommentsList.add(commentSb.toString());
			eventsCountriesList.add(countrySb.toString());
			eventsSubjectsList.add(subjectSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(EVENTSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(EVENTSSUBJECT_BINDING_ELEMENT_ATTIRBUTE)) {
					subjectSb.append(str);
				}
			}
				
			if (literalElementBeginFlag) {
				if (currentBindingName.equals(EVENTSLABEL_BINDING_ELEMENT_ATTIRBUTE)) {	
					labelSb.append(str);	
				}
				else if (currentBindingName.equals(EVENTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE)) {
					commentSb.append(str);
				}
				else if (currentBindingName.equals(EVENTSCOUNTRY_BINDING_ELEMENT_ATTIRBUTE)) {
					countrySb.append(str);
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
	
	public ArrayList<String> getEventsIds() {
		//System.out.println("eventsIdsList.size() " + eventsIdsList.size());
		return eventsIdsList;
	}
	public ArrayList<String> getEventLabels() {
		//System.out.println("eventsLabelsList.size() " + eventsLabelsList.size());
		return eventsLabelsList;
	}
	public ArrayList<String> getEventComments() {
		//System.out.println("eventsCommentsList.size() " + eventsCommentsList.size());
		return eventsCommentsList;
	}
	public ArrayList<String> getEventsCountries() {
		//System.out.println("eventsCountriesList.size() " + eventsCountriesList.size());
		return eventsCountriesList;
	}
	public ArrayList<String> getEventsSubjects() {
		//System.out.println("eventsSubjectsList.size() " + eventsSubjectsList.size());
		return eventsSubjectsList;
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
