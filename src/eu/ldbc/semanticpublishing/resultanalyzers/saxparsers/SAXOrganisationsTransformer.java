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
public class SAXOrganisationsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	
	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String ORGANISATIONSID_BINDING_ELEMENT_ATTIRBUTE = "organisationsId";
	private static final String ORGANISATIONSLABEL_BINDING_ELEMENT_ATTIRBUTE = "organisationsLabel";
	private static final String ORGANISATIONSCOMMENT_BINDING_ELEMENT_ATTIRBUTE = "organisationsComment";
	private static final String ORGANISATIONSMANAGER_BINDING_ELEMENT_ATTIRBUTE = "organisationsManager";	
	private static final String ORGANISATIONSNAME_BINDING_ELEMENT_ATTIRBUTE = "organisationsName";
	private static final String ORGANISATIONSNICKNAME_BINDING_ELEMENT_ATTIRBUTE = "organisationsNickname";
	private static final String ORGANISATIONSWEBSITE_BINDING_ELEMENT_ATTIRBUTE = "organisationsWebsite";
		
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	
		
	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder labelSb;
	private StringBuilder commentSb;
	private StringBuilder managerSb;
	private StringBuilder nameSb;
	private StringBuilder nicknameSb;
	private StringBuilder websiteSb;
	

	private ArrayList<String> organisationsIdsList; 
	private ArrayList<String> organisationsLabelsList;
	private ArrayList<String> organisationsCommentsList;
	private ArrayList<String> organisationsManagersList;
	private ArrayList<String> organisationsNamesList;
	private ArrayList<String> organisationsNicknamesList;
	private ArrayList<String> organisationsWebsitesList;
	
	
	@Override
	public void startDocument() throws SAXException {
		organisationsIdsList = new ArrayList<String>();
		organisationsLabelsList = new ArrayList<String>();
		organisationsCommentsList = new ArrayList<String>();
		organisationsManagersList = new ArrayList<String>();
		organisationsNamesList = new ArrayList<String>();
		organisationsNicknamesList = new ArrayList<String>();
		organisationsWebsitesList = new ArrayList<String>();
		
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
				managerSb = new StringBuilder();
				nameSb = new StringBuilder();
				nicknameSb = new StringBuilder();
				websiteSb = new StringBuilder();
				
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
			
			managerSb.insert(0, "<");
			managerSb.append(">");
			
			nameSb.insert(0, "<");
			nameSb.append(">");
			
			nicknameSb.insert(0, "<");
			nicknameSb.append(">");
			
			websiteSb.insert(0, "<");
			websiteSb.append(">");
		
			organisationsIdsList.add(uriSb.toString());
			organisationsLabelsList.add(labelSb.toString());
			organisationsCommentsList.add(commentSb.toString());
			organisationsManagersList.add(managerSb.toString());
			organisationsNamesList.add(nameSb.toString());
			organisationsNicknamesList.add(nicknameSb.toString());
			organisationsWebsitesList.add(websiteSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(ORGANISATIONSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(ORGANISATIONSWEBSITE_BINDING_ELEMENT_ATTIRBUTE)) {
					websiteSb.append(str);
				}
//				else if (currentBindingName.equals(ORGANISATIONSNAME_BINDING_ELEMENT_ATTIRBUTE)) {
//					nameSb.append(str);
//				}
//				else if (currentBindingName.equals(ORGANISATIONSNICKNAME_BINDING_ELEMENT_ATTIRBUTE)) {
//					nicknameSb.append(str);
//				}
//				else if (currentBindingName.equals(ORGANISATIONSMANAGER_BINDING_ELEMENT_ATTIRBUTE)) {
//					managerSb.append(str);
//				}
			}
				
			if (literalElementBeginFlag) {
				if (currentBindingName.equals(ORGANISATIONSCOMMENT_BINDING_ELEMENT_ATTIRBUTE)) {	
					commentSb.append(str);	
				}
				else if (currentBindingName.equals(ORGANISATIONSLABEL_BINDING_ELEMENT_ATTIRBUTE)) {	
					labelSb.append(str);	
				}
				else if (currentBindingName.equals(ORGANISATIONSNAME_BINDING_ELEMENT_ATTIRBUTE)) {
					nameSb.append(str);
				}
				else if (currentBindingName.equals(ORGANISATIONSNICKNAME_BINDING_ELEMENT_ATTIRBUTE)) {
					nicknameSb.append(str);
				}
				else if (currentBindingName.equals(ORGANISATIONSMANAGER_BINDING_ELEMENT_ATTIRBUTE)) {
					managerSb.append(str);
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
	
	public ArrayList<String> getOrganisationsIds() {
		return organisationsIdsList;
	}
	public ArrayList<String> getOrganisationsLabels() {
		return organisationsLabelsList;
	}
	public ArrayList<String> getOrganisationsComments() {
		return organisationsCommentsList;
	}
	
	public ArrayList<String> getOrganisationsManagers() {
		return organisationsManagersList;
	}
	public ArrayList<String> getOrganisationsNames() {
		return organisationsNamesList;
	}
	public ArrayList<String> getOrganisationsNicknames() {
		return organisationsNicknamesList;
	}
	public ArrayList<String> getOrganisationsWebsites() {
		return organisationsWebsitesList;
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
