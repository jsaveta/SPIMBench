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
public class SAXMusicalartistsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	

	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String MUSICALARTISTSID_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsId";
	private static final String MUSICALARTISTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsComment";
	private static final String MUSICALARTISTSLABEL_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsLabel";
	private static final String MUSICALARTISTSNAME_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsName";
	private static final String MUSICALARTISTSBIRTHDATE_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsBirthdate";	
	private static final String MUSICALARTISTSBIRTHPLACE_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsBirthplace";
	private static final String MUSICALARTISTSGENRE_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsGenre";
	private static final String MUSICALARTISTSHOMETOWN_BINDING_ELEMENT_ATTIRBUTE = "musicalartistsHometown";
		
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	

	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder commentSb;
	private StringBuilder labelSb;
	private StringBuilder nameSb;
	private StringBuilder birthdateSb;
	private StringBuilder birthplaceSb;
	private StringBuilder genreSb;
	private StringBuilder hometownSb;
	

	private ArrayList<String> musicalartistsIdsList; 
	private ArrayList<String> musicalartistsCommentsList;
	private ArrayList<String> musicalartistsLabelsList;
	private ArrayList<String> musicalartistsNamesList;
	private ArrayList<String> musicalartistsBirthdatesList;
	private ArrayList<String> musicalartistsBirthplacesList;
	private ArrayList<String> musicalartistsGenresList;
	private ArrayList<String> musicalartistsHometownsList;
	
	
	@Override
	public void startDocument() throws SAXException {
		musicalartistsIdsList = new ArrayList<String>();
		musicalartistsCommentsList = new ArrayList<String>();
		musicalartistsLabelsList = new ArrayList<String>();
		musicalartistsNamesList = new ArrayList<String>();
		musicalartistsBirthdatesList = new ArrayList<String>();
		musicalartistsBirthplacesList = new ArrayList<String>();
		musicalartistsGenresList = new ArrayList<String>();
		musicalartistsHometownsList = new ArrayList<String>();
		
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
				labelSb = new StringBuilder();
				nameSb = new StringBuilder();
				birthdateSb = new StringBuilder();
				birthplaceSb = new StringBuilder();
				genreSb = new StringBuilder();
				hometownSb = new StringBuilder();
				
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
		
			labelSb.insert(0, "<");
			labelSb.append(">");

			nameSb.insert(0, "<");
			nameSb.append(">");
			
			birthdateSb.insert(0, "<");
			birthdateSb.append(">");
			
			birthplaceSb.insert(0, "<");
			birthplaceSb.append(">");
			
			genreSb.insert(0, "<");
			genreSb.append(">");
			
			hometownSb.insert(0, "<");
			hometownSb.append(">");
		
				
			musicalartistsIdsList.add(uriSb.toString());
			musicalartistsCommentsList.add(commentSb.toString());
			musicalartistsLabelsList.add(labelSb.toString());
			musicalartistsNamesList.add(nameSb.toString());
			musicalartistsBirthdatesList.add(birthdateSb.toString());
			musicalartistsBirthplacesList.add(birthplaceSb.toString());
			musicalartistsGenresList.add(genreSb.toString());
			musicalartistsHometownsList.add(hometownSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);
			
			if (uriElementBeginFlag) {
				if (currentBindingName.equals(MUSICALARTISTSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(MUSICALARTISTSBIRTHPLACE_BINDING_ELEMENT_ATTIRBUTE)) {
					birthplaceSb.append(str);
				}
//				else if (currentBindingName.equals(MUSICALARTISTSNAME_BINDING_ELEMENT_ATTIRBUTE)) {
//					nameSb.append(str);
//				}
				else if (currentBindingName.equals(MUSICALARTISTSGENRE_BINDING_ELEMENT_ATTIRBUTE)) {
					genreSb.append(str);
				}
				else if (currentBindingName.equals(MUSICALARTISTSHOMETOWN_BINDING_ELEMENT_ATTIRBUTE)) {
					hometownSb.append(str);
				}
			}
				
			if (literalElementBeginFlag) {
				if (currentBindingName.equals(MUSICALARTISTSLABEL_BINDING_ELEMENT_ATTIRBUTE)) {	
					labelSb.append(str);	
				}
				else if (currentBindingName.equals(MUSICALARTISTSCOMMENT_BINDING_ELEMENT_ATTIRBUTE)) {	
					commentSb.append(str);	
				}
				
				else if (currentBindingName.equals(MUSICALARTISTSNAME_BINDING_ELEMENT_ATTIRBUTE)) {
					nameSb.append(str);
				}
				else if (currentBindingName.equals(MUSICALARTISTSBIRTHDATE_BINDING_ELEMENT_ATTIRBUTE)) {
					birthdateSb.append(str);
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
	
	public ArrayList<String> getMusicalartistsIds() {
		return musicalartistsIdsList;
	}
	public ArrayList<String> getMusicalartistsComments() {
		return musicalartistsCommentsList;
	}
	
	public ArrayList<String> getMusicalartistsLabels() {
		return musicalartistsLabelsList;
	}
	public ArrayList<String> getMusicalartistsNames() {
		return musicalartistsNamesList;
	}
	
	public ArrayList<String> getMusicalartistsBirthdates() {
		return musicalartistsBirthdatesList;
	}
	
	public ArrayList<String> getMusicalartistsBirthplaces() {
		return musicalartistsBirthplacesList;
	}
	public ArrayList<String> getMusicalartistsGenres() {
		return musicalartistsGenresList;
	}
	public ArrayList<String> getMusicalartistsHometowns() {
		return musicalartistsHometownsList;
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
