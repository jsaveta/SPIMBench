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
 * 
 * http://swatproject.org/travelOntology.asp
 */
/*
 * 
 * KAI AYTA EINAI MONO TA "APLA" PROPERTIES
<http://www.co-ode.org/roberts/travel.owl#coastlineOf>
<http://www.co-ode.org/roberts/travel.owl#coincidentWith>
<http://www.co-ode.org/roberts/travel.owl#contiguousWith>
<http://www.co-ode.org/roberts/travel.owl#dependencyOf>
<http://www.co-ode.org/roberts/travel.owl#directlyFlowsInto>
<http://www.co-ode.org/roberts/travel.owl#directPartOf>
<http://www.co-ode.org/roberts/travel.owl#flowsInto>
<http://www.co-ode.org/roberts/travel.owl#hasArea>
<http://www.co-ode.org/roberts/travel.owl#hasBoundary>
<http://www.co-ode.org/roberts/travel.owl#hasCapital>
<http://www.co-ode.org/roberts/travel.owl#hasCoastline>
<http://www.co-ode.org/roberts/travel.owl#hasDirectPart>
<http://www.co-ode.org/roberts/travel.owl#hasHeight>
<http://www.co-ode.org/roberts/travel.owl#hasLandBoundary>
<http://www.co-ode.org/roberts/travel.owl#hasLength>
<http://www.co-ode.org/roberts/travel.owl#hasMaximumHeight>
<http://www.co-ode.org/roberts/travel.owl#hasMemberIsland>
<http://www.co-ode.org/roberts/travel.owl#hasPhysicalProperty>
<http://www.co-ode.org/roberts/travel.owl#hasPhysicalProperty>web
<http://www.co-ode.org/roberts/travel.owl#hasPopulation>
<http://www.co-ode.org/roberts/travel.owl#hasSource>
<http://www.co-ode.org/roberts/travel.owl#hasStatistic>
<http://www.co-ode.org/roberts/travel.owl#hasWaterBoundary>
<http://www.co-ode.org/roberts/travel.owl#hasWidth>
<http://www.co-ode.org/roberts/travel.owl#isMemberIslandOf>
<http://www.co-ode.org/roberts/travel.owl#nextTo>
<http://www.co-ode.org/roberts/travel.owl#surroundedBy>
<http://www.co-ode.org/roberts/travel.owl#surrounds>
<http://www.co-ode.org/roberts/upper.owl#has_member>
<http://www.co-ode.org/roberts/upper.owl#has_part>
<http://www.co-ode.org/roberts/upper.owl#is_occupied_by>
<http://www.co-ode.org/roberts/upper.owl#is_part_of>
<http://www.co-ode.org/roberts/upper.owl#occupies>
*/

public class SAXTravelsTransformer extends DefaultHandler implements SAXResultTransformer {
	private boolean entityConstructionBegin = false;
	private boolean resultElementBeginFlag = false;
	private boolean uriElementBeginFlag = false;
	private boolean literalElementBeginFlag = false;
	
	private static final String RESULT_XML_ELEMENT = "result";	
	private static final String URI_XML_ELEMENT = "uri";
	private static final String LITERAL_XML_ELEMENT = "literal";
	private static final String TRAVELSID_BINDING_ELEMENT_ATTIRBUTE = "travelsId";
	private static final String TRAVELSTYPE_BINDING_ELEMENT_ATTIRBUTE = "travelsType";

	private static final String TRAVELSHASRECOGNITIONSTATUS_BINDING_ELEMENT_ATTIRBUTE = "hasRecognitionStatus";
	private static final String TRAVELSHASBOUNDARY_BINDING_ELEMENT_ATTIRBUTE = "hasBoundary";
	private static final String TRAVELSHASHEADOFSTATE_BINDING_ELEMENT_ATTIRBUTE = "hasHeadOfState";
	private static final String TRAVELSOCCUPIES_BINDING_ELEMENT_ATTIRBUTE = "occupies";
	private static final String TRAVELSSURROUNDEDBY_BINDING_ELEMENT_ATTIRBUTE = "surroundedBy";
	private static final String TRAVELSISOCCUPIEDBY_BINDING_ELEMENT_ATTIRBUTE = "is_occupied_by";
	private static final String TRAVELSHASPHYSICALPROPERTY_BINDING_ELEMENT_ATTIRBUTE = "hasPhysicalProperty";
	private static final String TRAVELSHASCAPITAL_BINDING_ELEMENT_ATTIRBUTE = "hasCapital";
	private static final String TRAVELSDIRECTPARTOF_BINDING_ELEMENT_ATTIRBUTE = "directPartOf";
	private static final String TRAVELSHASAREA_BINDING_ELEMENT_ATTIRBUTE = "hasArea";
	private static final String TRAVELSHASPOPULATION_BINDING_ELEMENT_ATTIRBUTE = "hasPopulation";
	//private static final String TRAVELSHASSTATISTIC_BINDING_ELEMENT_ATTIRBUTE = "hasStatistic";
	
	
	
	
	private static final String BINDING = "binding";
	private static final String NAME = "name";	
	
	private String currentBindingName;
	private StringBuilder uriSb;
	private StringBuilder typeSb;
	private StringBuilder hasRecognitionStatusSb;
	private StringBuilder hasBoundarySb;
	private StringBuilder hasHeadOfStateSb;
	private StringBuilder occupiesSb;
	private StringBuilder surroundedBySb;
	private StringBuilder is_occupied_bySb;
	private StringBuilder hasPhysicalPropertySb;
	private StringBuilder hasCapitalSb;
	private StringBuilder directPartOfSb;
	private StringBuilder hasAreaSb;
	private StringBuilder hasPopulationSb;
	//private StringBuilder hasStatisticSb;
	

	private ArrayList<String> travelsIdsList;
	private ArrayList<String> travelsTypesList; 
	private ArrayList<String> travelsHasRecognitionStatusList; 
	private ArrayList<String> travelsHasBoundaryList; 
	private ArrayList<String> travelsHasHeadOfStatesList; 
	private ArrayList<String> travelsOccupiesList; 
	private ArrayList<String> travelsSurroundedBysList;  
	private ArrayList<String> travelsIsOccupiedBysList;  
	private ArrayList<String> travelsHasPhysicalPropertiesList; 
	private ArrayList<String> travelsHasCapitalsList; 
	private ArrayList<String> travelsDirectPartOfsList; 
	private ArrayList<String> travelsHasAreasList; 
	private ArrayList<String> travelsHasPopulationsList; 
	//private ArrayList<String> travelsHasStatisticsList; 


	
	@Override
	public void startDocument() throws SAXException {
		travelsIdsList = new ArrayList<String>();
		travelsTypesList = new ArrayList<String>();
		travelsHasRecognitionStatusList = new ArrayList<String>();
		travelsHasBoundaryList = new ArrayList<String>();
		travelsHasHeadOfStatesList = new ArrayList<String>();
		travelsOccupiesList = new ArrayList<String>();
		travelsSurroundedBysList = new ArrayList<String>(); 
		travelsIsOccupiedBysList = new ArrayList<String>();
		travelsHasPhysicalPropertiesList = new ArrayList<String>();
		travelsHasCapitalsList = new ArrayList<String>(); 
		travelsDirectPartOfsList = new ArrayList<String>();
		travelsHasAreasList = new ArrayList<String>();
		travelsHasPopulationsList = new ArrayList<String>(); 
		//travelsHasStatisticsList = new ArrayList<String>();
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
				typeSb = new StringBuilder();
				hasRecognitionStatusSb = new StringBuilder();
				hasBoundarySb = new StringBuilder();
				hasHeadOfStateSb = new StringBuilder();
				occupiesSb = new StringBuilder();
				surroundedBySb = new StringBuilder();
				is_occupied_bySb = new StringBuilder();
				hasPhysicalPropertySb = new StringBuilder();
				hasCapitalSb = new StringBuilder();
				directPartOfSb = new StringBuilder();
				hasAreaSb = new StringBuilder();
				hasPopulationSb = new StringBuilder();
				//hasStatisticSb = new StringBuilder();
				
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
			
			typeSb.insert(0, "<");
			typeSb.append(">");

			
			hasRecognitionStatusSb.insert(0, "<");
			hasRecognitionStatusSb.append(">");
			
			hasBoundarySb.insert(0, "<");
			hasBoundarySb.append(">");
			
			hasHeadOfStateSb.insert(0, "<");
			hasHeadOfStateSb.append(">");
			
			occupiesSb.insert(0, "<");
			occupiesSb.append(">");
			
			surroundedBySb.insert(0, "<");
			surroundedBySb.append(">");
			
			is_occupied_bySb.insert(0, "<");
			is_occupied_bySb.append(">");


			
			hasPhysicalPropertySb.insert(0, "<");
			hasPhysicalPropertySb.append(">");
			
			hasCapitalSb.insert(0, "<");
			hasCapitalSb.append(">");
			
			directPartOfSb.insert(0, "<");
			directPartOfSb.append(">");
			
			hasAreaSb.insert(0, "<");
			hasAreaSb.append(">");

			hasPopulationSb.insert(0, "<");
			hasPopulationSb.append(">");
			
//			hasStatisticSb.insert(0, "<");
//			hasStatisticSb.append(">");


						
			
			travelsIdsList.add(uriSb.toString());
			travelsTypesList.add(typeSb.toString());
			travelsHasRecognitionStatusList.add(hasRecognitionStatusSb.toString());
			travelsHasBoundaryList.add(hasBoundarySb.toString());
			travelsHasHeadOfStatesList.add(hasHeadOfStateSb.toString());
			travelsOccupiesList.add(occupiesSb.toString());
			travelsSurroundedBysList.add(surroundedBySb.toString()); 
			travelsIsOccupiedBysList.add(is_occupied_bySb.toString()); 
			travelsHasPhysicalPropertiesList.add(hasPhysicalPropertySb.toString()); 
			travelsHasCapitalsList.add(hasCapitalSb.toString()); 
			travelsDirectPartOfsList.add(directPartOfSb.toString());
			travelsHasAreasList.add(hasAreaSb.toString());
			travelsHasPopulationsList.add(hasPopulationSb.toString()); 
			//travelsHasStatisticsList.add(hasStatisticSb.toString());
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {		
		if (length > 0 && entityConstructionBegin) {
			String str = new String(ch, start, length);

			if (uriElementBeginFlag) {
				if (currentBindingName.equals(TRAVELSID_BINDING_ELEMENT_ATTIRBUTE)) {
					uriSb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSTYPE_BINDING_ELEMENT_ATTIRBUTE)) {
					typeSb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSSURROUNDEDBY_BINDING_ELEMENT_ATTIRBUTE)) {
					surroundedBySb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSISOCCUPIEDBY_BINDING_ELEMENT_ATTIRBUTE)) {
					is_occupied_bySb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSDIRECTPARTOF_BINDING_ELEMENT_ATTIRBUTE)) {
					directPartOfSb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSHASCAPITAL_BINDING_ELEMENT_ATTIRBUTE)) {
					hasCapitalSb.append(str);
				}
				
				else if (currentBindingName.equals(TRAVELSHASBOUNDARY_BINDING_ELEMENT_ATTIRBUTE)) {
					hasBoundarySb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSHASRECOGNITIONSTATUS_BINDING_ELEMENT_ATTIRBUTE)) {
					hasRecognitionStatusSb.append(str);
				}
			}
				
			if (literalElementBeginFlag) {
				
				if (currentBindingName.equals(TRAVELSHASPHYSICALPROPERTY_BINDING_ELEMENT_ATTIRBUTE)) {
					hasPhysicalPropertySb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSHASAREA_BINDING_ELEMENT_ATTIRBUTE)) {
					hasAreaSb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSHASPOPULATION_BINDING_ELEMENT_ATTIRBUTE)) {
					hasPopulationSb.append(str);
				}
				else if (currentBindingName.equals(TRAVELSHASHEADOFSTATE_BINDING_ELEMENT_ATTIRBUTE)) {	
					hasHeadOfStateSb.append(str);	
				}
				else if (currentBindingName.equals(TRAVELSOCCUPIES_BINDING_ELEMENT_ATTIRBUTE)) {
					occupiesSb.append(str);
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

	public ArrayList<String> getTravelsIds() {
		return travelsIdsList;
	}

	public ArrayList<String> getTravelsSurroundedBy() {
		return travelsSurroundedBysList;
	}

	public ArrayList<String> getTravelsHasPhysicalProperty() {
		return travelsHasPhysicalPropertiesList;
	}

	public ArrayList<String> getTravelsDirectPartOf() {
		return travelsDirectPartOfsList;
	}

	public ArrayList<String> getTravelsHasPopulation() {
		return travelsHasPopulationsList;
	}

//	public ArrayList<String> getTravelsHasStatistic() {
//		return travelsHasStatisticsList;
//	}

	public ArrayList<String> getTravelsTypes() {
		return travelsTypesList;
	}

	public ArrayList<String> getTravelsIsOccupiedBy() {
		return travelsIsOccupiedBysList;
	}


	public ArrayList<String> getTravelsHasCapital() {
		return travelsHasCapitalsList;
	}

	public ArrayList<String> getTravelsHasArea() {
		return travelsHasAreasList;
	}
	
	public ArrayList<String> getTravelsHasHeadOfState() {
		return travelsHasHeadOfStatesList;
	}
	public ArrayList<String> getTravelsHasBoundary() {
		return travelsHasBoundaryList;
	}


	public ArrayList<String> getTravelsOccupies() {
		return travelsOccupiesList;
	}

	public ArrayList<String> getTravelsHasRecognitionStatus() {
		return travelsHasRecognitionStatusList;
	}
}
