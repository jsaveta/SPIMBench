
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
public class SAXSportsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	
	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String SPORTSID_BINDING_ELEMENT_ATTIRBUTE = "sportsId";
	private static final String SPORTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE = "sportsComment";
	private static final String SPORTSCAPTION_BINDING_ELEMENT_ATTIRBUTE = "sportsCaption";
	private static final String SPORTSEQUIPMENT_BINDING_ELEMENT_ATTIRBUTE = "sportsEquipment";
	private static final String SPORTSOLYMPIC_BINDING_ELEMENT_ATTIRBUTE = "sportsOlympic";
	private static final String SPORTSTEAM_BINDING_ELEMENT_ATTIRBUTE = "sportsTeam";
	
	
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	
	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder commentSb;
	private StringBuilder captionSb;
	private StringBuilder equipmentSb;
	private StringBuilder olympicSb;
	private StringBuilder teamSb;

	private ArrayList<String> sportsIdsList; 
	private ArrayList<String> sportsCommentsList;
	private ArrayList<String> sportsCaptionsList;
	private ArrayList<String> sportsEquipmentsList;
	private ArrayList<String> sportsOlympicsList;
	private ArrayList<String> sportsTeamsList;
	
	@Override
	public void startDocument() throws SAXException {
		sportsIdsList = new ArrayList<String>();
		sportsCommentsList = new ArrayList<String>();
		sportsCaptionsList = new ArrayList<String>();
		sportsEquipmentsList = new ArrayList<String>();
		sportsOlympicsList = new ArrayList<String>();
		sportsTeamsList = new ArrayList<String>();
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
				commentSb = new StringBuilder();
				captionSb = new StringBuilder();
				equipmentSb = new StringBuilder();
				olympicSb = new StringBuilder();
				teamSb = new StringBuilder();
				
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
			
			commentSb.insert(0, "<");
			commentSb.append(">");
			
			captionSb.insert(0, "<");
			captionSb.append(">");
			
			equipmentSb.insert(0, "<");
			equipmentSb.append(">");
			
			olympicSb.insert(0, "<");
			olympicSb.append(">");
			
			teamSb.insert(0, "<");
			teamSb.append(">");
						
			
			sportsIdsList.add(uriSb.toString());
			sportsCommentsList.add(commentSb.toString());
			sportsCaptionsList.add(captionSb.toString());
			sportsEquipmentsList.add(equipmentSb.toString());
			sportsOlympicsList.add(olympicSb.toString());
			sportsTeamsList.add(teamSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(SPORTSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(SPORTSEQUIPMENT_BINDING_ELEMENT_ATTIRBUTE)) {
					equipmentSb.append(str);
				}
			}
				
			if (literalElementBeginFlag) {
				if (currentBindingName.equals(SPORTSEQUIPMENT_BINDING_ELEMENT_ATTIRBUTE)) {
					equipmentSb.append(str);
				}
				else if (currentBindingName.equals(SPORTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE)) {	
					commentSb.append(str);	
				}
				else if (currentBindingName.equals(SPORTSCAPTION_BINDING_ELEMENT_ATTIRBUTE)) {
					captionSb.append(str);
				}
				else if (currentBindingName.equals(SPORTSOLYMPIC_BINDING_ELEMENT_ATTIRBUTE)) {
					olympicSb.append(str);
				}
				else if (currentBindingName.equals(SPORTSTEAM_BINDING_ELEMENT_ATTIRBUTE)) {
					teamSb.append(str);
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
	
	public ArrayList<String> getSportsIds() {
		return sportsIdsList;
	}
	public ArrayList<String> getSportsComments() {
		return sportsCommentsList;
	}
	public ArrayList<String> getSportsCaptions() {
		return sportsCaptionsList;
	}
	
	public ArrayList<String> getSportsEquipments() {
		return sportsEquipmentsList;
	}
	public ArrayList<String> getSportsOlympics() {
		return sportsOlympicsList;
	}
	public ArrayList<String> getSportsTeams() {
		return sportsTeamsList;
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
