
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
public class SAXPersonsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;


	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String PERSONSID_BINDING_ELEMENT_ATTIRBUTE = "personsId";
	private static final String PERSONSNAME_BINDING_ELEMENT_ATTIRBUTE = "personsName";
	private static final String PERSONSSURNAME_BINDING_ELEMENT_ATTIRBUTE = "personsSurname";
	private static final String PERSONSGIVENNAME_BINDING_ELEMENT_ATTIRBUTE = "personsGivenname";
	private static final String PERSONSDESCRIPTION_BINDING_ELEMENT_ATTIRBUTE = "personsDescription";
	private static final String PERSONSBIRTHDATE_BINDING_ELEMENT_ATTIRBUTE = "personsBirthdate";	
	private static final String PERSONSBIRTHPLACE_BINDING_ELEMENT_ATTIRBUTE = "personsBirthplace";
	private static final String PERSONSDEATHDATE_BINDING_ELEMENT_ATTIRBUTE = "personsDeathdate";
	private static final String PERSONSDEATHPLACE_BINDING_ELEMENT_ATTIRBUTE = "personsDeathplace";
		
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	

	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder nameSb;
	private StringBuilder surnameSb;
	private StringBuilder givennameSb;
	private StringBuilder descriptionSb;
	private StringBuilder birthdateSb;
	private StringBuilder birthplaceSb;
	private StringBuilder deathdateSb;
	private StringBuilder deathplaceSb;
	

	private ArrayList<String> personsIdsList; 
	private ArrayList<String> personsNamesList;
	private ArrayList<String> personsSurnamesList;
	private ArrayList<String> personsGivennamesList;	
	private ArrayList<String> personsDescriptionsList;
	private ArrayList<String> personsBirthdatesList;
	private ArrayList<String> personsBirthplacesList;
	private ArrayList<String> personsDeathdatesList;
	private ArrayList<String> personsDeathplacesList;
	
	
	@Override
	public void startDocument() throws SAXException {
		
		personsIdsList = new ArrayList<String>();
		personsNamesList = new ArrayList<String>();
		personsSurnamesList = new ArrayList<String>();
		personsGivennamesList = new ArrayList<String>();
		personsDescriptionsList = new ArrayList<String>();
		personsBirthdatesList = new ArrayList<String>();
		personsBirthplacesList = new ArrayList<String>();
		personsDeathdatesList = new ArrayList<String>();
		personsDeathplacesList = new ArrayList<String>();
		
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
				surnameSb = new StringBuilder();
				givennameSb = new StringBuilder();
				descriptionSb = new StringBuilder();
				birthdateSb = new StringBuilder();
				birthplaceSb = new StringBuilder();
				deathdateSb = new StringBuilder();
				deathplaceSb = new StringBuilder();
				
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
			
			surnameSb.insert(0, "<");
			surnameSb.append(">");
		
			givennameSb.insert(0, "<");
			givennameSb.append(">");

			descriptionSb.insert(0, "<");
			descriptionSb.append(">");
			
			birthdateSb.insert(0, "<");
			birthdateSb.append(">");
			
			birthplaceSb.insert(0, "<");
			birthplaceSb.append(">");
			
			deathdateSb.insert(0, "<");
			deathdateSb.append(">");
			
			deathplaceSb.insert(0, "<");
			deathplaceSb.append(">");
		
			personsIdsList.add(uriSb.toString());
			personsNamesList.add(nameSb.toString());
			personsSurnamesList.add(surnameSb.toString());
			personsGivennamesList.add(givennameSb.toString());
			personsDescriptionsList.add(descriptionSb.toString());
			personsBirthdatesList.add(birthdateSb.toString());
			personsBirthplacesList.add(birthplaceSb.toString());
			personsDeathdatesList.add(deathdateSb.toString());
			personsDeathplacesList.add(deathplaceSb.toString());

		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(PERSONSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(PERSONSBIRTHPLACE_BINDING_ELEMENT_ATTIRBUTE)) {
					birthplaceSb.append(str);
				}
				else if (currentBindingName.equals(PERSONSDEATHPLACE_BINDING_ELEMENT_ATTIRBUTE)) {
					deathplaceSb.append(str);
				}

			}
				
			if (literalElementBeginFlag) {
				
					
				if (currentBindingName.equals(PERSONSNAME_BINDING_ELEMENT_ATTIRBUTE)) {	
					nameSb.append(str);	
				}
				else if (currentBindingName.equals(PERSONSSURNAME_BINDING_ELEMENT_ATTIRBUTE)) {	
					surnameSb.append(str);	
				}
				else if (currentBindingName.equals(PERSONSGIVENNAME_BINDING_ELEMENT_ATTIRBUTE)) {	
					givennameSb.append(str);	
				}
				
				else if (currentBindingName.equals(PERSONSDESCRIPTION_BINDING_ELEMENT_ATTIRBUTE)) {
					descriptionSb.append(str);
				}
				else if (currentBindingName.equals(PERSONSBIRTHDATE_BINDING_ELEMENT_ATTIRBUTE)) {
					birthdateSb.append(str);
				}
				else if (currentBindingName.equals(PERSONSDEATHDATE_BINDING_ELEMENT_ATTIRBUTE)) {
					deathdateSb.append(str);
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

	
	public ArrayList<String> getPersonsIds() {
		return personsIdsList;
	}
	public ArrayList<String> getPersonsNames() {
		return personsNamesList;
	}
	public ArrayList<String> getPersonsSurnames() {
		return personsSurnamesList;
	}
	
	public ArrayList<String> getPersonsGivennames() {
		return personsGivennamesList;
	}
	public ArrayList<String> getPersonsDescriptions() {
		return personsDescriptionsList;
	}
	
	public ArrayList<String> getPersonsBirthdates() {
		return personsBirthdatesList;
	}
	
	public ArrayList<String> getPersonsBirthplaces() {
		return personsBirthplacesList;
	}
	public ArrayList<String> getPersonsDeathdates() {
		return personsDeathdatesList;
	}
	public ArrayList<String> getPersonsDeathplaces() {
		return personsDeathplacesList;
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
