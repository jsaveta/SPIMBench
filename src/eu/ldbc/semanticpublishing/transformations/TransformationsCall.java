package eu.ldbc.semanticpublishing.transformations;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.properties.Definitions;
/**
 * We map every transformation with a unique number for reasons of space on the GoldStandard file.
 *
 */
public class TransformationsCall {
	public static Map <String, String> transformationsMap;
	private Map <String, Transformation> predicatesObjectsMap;
	private Map<String, List<Transformation>> predicatesObjectsComplexMap;
	private ArrayList <String> lexicalArrayList;
	private ArrayList <String> structuralArrayList;
	private ArrayList <String> logicalArrayList;
	private ArrayList <String> complexArrayList;
	private List <Transformation> transformationsArrayList;
	
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	/*jsaveta start*/
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String dbpprop = "http://dbpedia.org/property/";
	private static final String dcterms = "http://purl.org/dc/terms/";
	private static final String travel = "http://www.co-ode.org/roberts/travel.owl#";
	private static final String upper = "http://www.co-ode.org/roberts/upper.owl#";
	private static final String bbcNamespace = "http://www.bbc.co.uk/ontologies/bbc/";
	private static final String ldbc = "http://www.ldbc.eu/";
	
	private Tranformation transfPerc = Tranformation.LEXICAL;
	private Lexical lexPerc = Lexical.BLANKCHARSADDITION;
	private Structural structPerc = Structural.ADDPROPERTY;
	private Logical logPerc = Logical.SAMEAS;
	private SimpleCombination simplePerc = SimpleCombination.LEXICAL;
	private ComplexCombination complexPerc = ComplexCombination.LEXICAL_STRUCTURAL;
	private FunctionalInverseFunctional functPerc = FunctionalInverseFunctional.FUNCTIONALPROPERTY;
	
	private AbstractAsynchronousWorker worker;
	private final Configuration configuration = new Configuration();
	
	public TransformationsCall(AbstractAsynchronousWorker worker_){
		this.worker = worker_;
		transformationsMap = new HashMap<String, String>();
		transformationsMap.put("0", "Not_Transformed");
		// 1 - 19 are lexical transformations
		transformationsMap.put("1", "BlankCharsAddition");
		transformationsMap.put("2", "BlankCharsDeletion");
		transformationsMap.put("3", "RandomCharsAddition");
		transformationsMap.put("4", "RandomCharsDeletion");
		transformationsMap.put("5", "RandomCharsModifier");
		transformationsMap.put("6", "TokenAddition");
		transformationsMap.put("7", "TokenDeletion");
		transformationsMap.put("8", "TokenShuffle");
		transformationsMap.put("9", "NameStyleAbbreviation");
		transformationsMap.put("10", "ChangeSynonym");
		transformationsMap.put("11", "ChangeNumberFormat"); 
		transformationsMap.put("12", "ChangeDateFormat");
		transformationsMap.put("13", "ChangeLanguage");
		transformationsMap.put("14", "ChangeBooleanValue"); 
		transformationsMap.put("15", "ChangeGenderFormat");
		transformationsMap.put("16", "ChangeAntonym"); 
		transformationsMap.put("17", "KeepRoot"); 
		transformationsMap.put("18", "ChangeSyntheticCompound");// not impemented
		transformationsMap.put("19", "ChangeWordPermutation");

		// 20 - 22 are structural transformations
		transformationsMap.put("20", "AddProperty"); 
		transformationsMap.put("21", "DeleteProperty");
		transformationsMap.put("22", "ExtractProperty");
		transformationsMap.put("23", "AggregateProperties");
		
		//23 - n are logical transformations 
		transformationsMap.put("24", "CWSameAs");
		transformationsMap.put("25", "SameAsOnExistingCW");
		transformationsMap.put("26", "CWDifferentFrom");
		transformationsMap.put("27", "EquivalentClass");
		transformationsMap.put("28", "EquivalentProperty");
		transformationsMap.put("29", "SubClassOf");
		transformationsMap.put("30", "DisjointWith"); 
		transformationsMap.put("31", "SubPropertyOf"); 
		transformationsMap.put("32", "DisjointProperty"); //alldisjointproperties 
		transformationsMap.put("33", "FunctionalProperty"); 
		transformationsMap.put("34", "InverseFunctionalProperty");
		transformationsMap.put("35", "ComplementOf");
		transformationsMap.put("36", "IntersectionOf");
		transformationsMap.put("37", "UnionOf");
		transformationsMap.put("38", "OneOf"); //.....

		setLexicalList();
		setStructuralList();
		setComplexTransformationsList();
		predicatesObjectsMap = new HashMap<String, Transformation>(); //if you remove it you will have a null pointer error
		predicatesObjectsComplexMap = new HashMap<String, List<Transformation>>();
		
	}
	private static enum Tranformation { 
		LEXICAL , STRUCTURAL ,LOGICAL , SIMPLECOMBINATION ,COMPLEXCOMBINATION, NOTRANSFORMATION 
	}
	//for every enum below also insert a no transformation value?
	private static enum Lexical {  
		BLANKCHARSADDITION , BLANKCHARSDELETION ,RANDOMCHARSADDITION , RANDOMCHARSDELETION ,RANDOMCHARSMODIFIER, TOKENADDITION, 
		TOKENDELETION, TOKENSHUFFLE, NAMESTYLEABBREVIATION, CHANGESYNONYM, CHANGENUMBERFORMAT, CHANGEDATEFORMAT, CHANGELANGUAGE, 
		CHANGEBOOLEAN, CHANGEGENDERFORMAT, CHANGEANTONYM, KEEPROOT,CHANGESYNTHETICCOMPOUND, CHANGEWORDPERMUTATION, NOTRANSFORMATION
	}
	
	private static enum Structural { 
		ADDPROPERTY, DELETEPROPERTY, EXTRACTPROPERTY, AGGREGATEPROPERTY, NOTRANSFORMATION
	}
	
	private static enum Logical { 
		SAMEAS, SAMEASONEXISTINGCW, DIFFERENTFROM, EQUIVALENTCLASS, EQUIVALENTPROPERTY, SUBCLASSOF, DISJOINTWITH, SUBPROPERTYOF,
		 DISJOINTPROPERTY, FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY, INTERSECTIONOF, UNIONOF, ONEOF ,NOTRANSFORMATION 

	}
	private static enum SimpleCombination {
		LEXICAL , STRUCTURAL ,LOGICAL , NOTRANSFORMATION 
	}
	private static enum ComplexCombination {
		LEXICAL_STRUCTURAL, LEXICAL_LOGICAL, STRUCTURAL_LOGICAL, LEXICAL_STRUCTURAL_LOGICAL, NOTRANSFORMATION 
	}
	private  static enum FunctionalInverseFunctional {
		FUNCTIONALPROPERTY, INVERSEFUNCTIONALPROPERTY
	}
	
	private void initializeTransformationsEntity() {
		try {			
			
			switch (Definitions.transformationAllocation.getAllocation()) {
				case 0 :
					this.transfPerc = Tranformation.LEXICAL;
					break;
				case 1 :
					this.transfPerc = Tranformation.STRUCTURAL;
					break;
				case 2 :
					this.transfPerc = Tranformation.LOGICAL;
					break;	
				case 3 :
					this.transfPerc = Tranformation.SIMPLECOMBINATION;
					break;
				case 4 :
					this.transfPerc = Tranformation.COMPLEXCOMBINATION;
					break;
				case 5 :
					this.transfPerc = Tranformation.NOTRANSFORMATION;
					break;
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check transformation percentages");
		}
	}
	
	private void initializeLexicalEntity() {
		try {			

			switch (Definitions.lexicalAllocation.getAllocation()) {
				case 0 :
					this.lexPerc = Lexical.BLANKCHARSADDITION;
					break;
				case 1 :
					this.lexPerc = Lexical.BLANKCHARSDELETION;
					break;
				case 2 :
					this.lexPerc = Lexical.RANDOMCHARSADDITION;
					break;	
				case 3 :
					this.lexPerc = Lexical.RANDOMCHARSDELETION;
					break;
				case 4 :
					this.lexPerc = Lexical.RANDOMCHARSMODIFIER;
					break;
				case 5 :
					this.lexPerc = Lexical.TOKENADDITION;
					break;
				case 6 :
					this.lexPerc = Lexical.TOKENDELETION;
					break;
				case 7 :
					this.lexPerc = Lexical.TOKENSHUFFLE;
					break;
				case 8 :
					this.lexPerc = Lexical.NAMESTYLEABBREVIATION;
					break;
				case 9 :
					this.lexPerc = Lexical.CHANGESYNONYM;
					break;
				case 10 :
					this.lexPerc = Lexical.CHANGENUMBERFORMAT;
					break;
				case 11 :
					this.lexPerc = Lexical.CHANGEDATEFORMAT;
					break;
				case 12 :
					this.lexPerc = Lexical.CHANGELANGUAGE;
					break;
				case 13 :
					this.lexPerc = Lexical.CHANGEBOOLEAN;
					break;
				case 14 :
					this.lexPerc = Lexical.CHANGEGENDERFORMAT;
					break;
				case 15 :
					this.lexPerc = Lexical.CHANGEANTONYM;
					break;
				case 16 :
					this.lexPerc = Lexical.KEEPROOT;
					break;
				case 17 :
					this.lexPerc = Lexical.CHANGESYNTHETICCOMPOUND;
					break;
				case 18 :
					this.lexPerc = Lexical.CHANGEWORDPERMUTATION;
					break;	
				case 19 :
					this.lexPerc = Lexical.NOTRANSFORMATION;
					break;	
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check lexical transformation percentages");
		}
	}
	private void initializeStructuralEntity() {
		try {			
			switch (Definitions.structuralAllocation.getAllocation()) {
				case 0 :
					this.structPerc = Structural.ADDPROPERTY;
					break;
				case 1 :
					this.structPerc = Structural.DELETEPROPERTY;
					break;
				case 2 :
					this.structPerc = Structural.EXTRACTPROPERTY;
					break;	
				case 3 :
					this.structPerc = Structural.AGGREGATEPROPERTY;
					break;
				case 4 :
					this.structPerc = Structural.NOTRANSFORMATION;
					break;
				
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check structural transformation percentages");
		}
	}
	private void initializeLogicalEntity() {
		try {	
			switch (Definitions.logicalAllocation.getAllocation()) {
				case 0 :
					this.logPerc = Logical.SAMEAS;
					break;
				case 1 :
					this.logPerc = Logical.SAMEASONEXISTINGCW;
					break;
				case 2 :
					this.logPerc = Logical.DIFFERENTFROM;
					break;	
				case 3 :
					this.logPerc = Logical.EQUIVALENTCLASS;
					break;
				case 4 :
					this.logPerc = Logical.EQUIVALENTPROPERTY;
					break;
				case 5 :
					this.logPerc = Logical.SUBCLASSOF;
					break;
				case 6 :
					this.logPerc = Logical.DISJOINTWITH;
					break;
				case 7 :
					this.logPerc = Logical.SUBPROPERTYOF;
					break;
				case 8 :
					this.logPerc = Logical.DISJOINTPROPERTY;
					break;
				case 9 :
					this.logPerc = Logical.FUNCTIONALPROPERTY;
					break;
				case 10 :
					this.logPerc = Logical.INVERSEFUNCTIONALPROPERTY;
					break;
				case 11 :
					this.logPerc = Logical.INTERSECTIONOF;
					break;
				case 12 :
					this.logPerc = Logical.UNIONOF;
					break;
				case 13 :
					this.logPerc = Logical.ONEOF;
					break;
				case 14 :
					this.logPerc = Logical.NOTRANSFORMATION;
					break;
					//add here every time you implement + on configuration file + cases below 
					
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check logical transformation percentages");
		}
	}
	
	private void initializeSimpleCombinationEntity() {
		try {			
			
			switch (Definitions.simpleCombinationAllocation.getAllocation()) {
				case 0 :
					this.simplePerc = SimpleCombination.LEXICAL;
					break;
				case 1 :
					this.simplePerc = SimpleCombination.STRUCTURAL;
					break;
				case 2 :
					this.simplePerc = SimpleCombination.LOGICAL;
					break;	
				case 3 :
					this.simplePerc = SimpleCombination.NOTRANSFORMATION;
					break;
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check simple combination percentages");
		}
	}
	
	private void initializeComplexCombinationEntity() {
		try {			
			
			switch (Definitions.complexCombinationAllocation.getAllocation()) {
				case 0 :
					this.complexPerc = ComplexCombination.LEXICAL_STRUCTURAL;
					break;
				case 1 :
					this.complexPerc = ComplexCombination.LEXICAL_LOGICAL;
					break;
				case 2 :
					this.complexPerc = ComplexCombination.STRUCTURAL_LOGICAL;
					break;
				case 3 :
					this.complexPerc = ComplexCombination.LEXICAL_STRUCTURAL_LOGICAL;
					break;
				case 4 :
					this.complexPerc = ComplexCombination.NOTRANSFORMATION;
					break;
			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check simple combination percentages");
		}
	}
	private void initializeFunctionalPropertyEntity() {
		try {	
			switch (Definitions.functionalInverseFunctionalAllocation.getAllocation()) {

				case 0 :
					this.functPerc = FunctionalInverseFunctional.FUNCTIONALPROPERTY;
					break;
				case 1 :
					this.functPerc = FunctionalInverseFunctional.INVERSEFUNCTIONALPROPERTY;
					break;

			}

			
		} catch (IllegalArgumentException iae) {
				System.err.println("Check functionalInverseFunctionalAllocation transformation percentages");
		}
	}		
	//put it in random util
	public static String getKey(Map<String,String> table,String value_){
		String key = null;
        String value = value_;
        Iterator<Entry<String, String>> it = table.entrySet().iterator();
        while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
            if(value.equals(entry.getValue())){
                key = entry.getKey().toString();
                break; //breaking because its one to one map
            }
        }
        return key;
	}
	
	public void setTransformationList(){
		
		transformationsArrayList = new ArrayList<Transformation>();

		//lexical
		transformationsArrayList.add(TransformationConfiguration.addRANDOMBLANKS(0.2));
		transformationsArrayList.add(TransformationConfiguration.deleteRANDOMBLANKS(0.2));
		transformationsArrayList.add(TransformationConfiguration.addRANDOMCHARS(0.2));
		transformationsArrayList.add(TransformationConfiguration.deleteRANDOMCHARS(0.2));
		transformationsArrayList.add(TransformationConfiguration.substituteRANDOMCHARS(0.2));
		transformationsArrayList.add(TransformationConfiguration.addTOKENS("a", 0.2));
		transformationsArrayList.add(TransformationConfiguration.deleteTOKENS("b", 0.2));
		transformationsArrayList.add(TransformationConfiguration.shuffleTOKENS("e", 0.2));
		transformationsArrayList.add(TransformationConfiguration.shuffleTOKENS(" ", 0.2));
		transformationsArrayList.add(TransformationConfiguration.KEEPROOT(1.0));
		transformationsArrayList.add(TransformationConfiguration.abbreviateNAME(/*NameStyleAbbreviation.SCOMMANDOT,*/1)); //NDOTS/ALLDOTS
		//transformationsArrayList.add(TransformationConfiguration.abbreviateNAME(NameStyleAbbreviation.NDOTS,1)); //NDOTS/ALLDOTS
		//transformationsArrayList.add(TransformationConfiguration.abbreviateNAME(NameStyleAbbreviation.ALLDOTS,1)); //NDOTS/ALLDOTS
		transformationsArrayList.add(TransformationConfiguration.changeSYNONYMS(configuration.getString(Configuration.WORDNET_PATH),1.0));	
		//call here change antonym
		transformationsArrayList.add(TransformationConfiguration.changeLANGUAGE());
		transformationsArrayList.add(TransformationConfiguration.dateFORMAT("yyyy-MM-dd HH:mm:ss", DateFormat.SHORT)); //SHORT/MEDIUM/LONG
		transformationsArrayList.add(TransformationConfiguration.dateFORMAT("yyyy-MM-dd HH:mm:ss", DateFormat.MEDIUM)); //SHORT/MEDIUM/LONG
		transformationsArrayList.add(TransformationConfiguration.dateFORMAT("yyyy-MM-dd HH:mm:ss", DateFormat.LONG)); //SHORT/MEDIUM/LONG
		transformationsArrayList.add(TransformationConfiguration.dateFORMAT("yyyy-MM-dd HH:mm:ss", DateFormat.FULL)); //SHORT/MEDIUM/LONG
		transformationsArrayList.add(TransformationConfiguration.dateFORMAT("yyyy-MM-dd", DateFormat.LONG)); //SHORT/MEDIUM/LONG
		transformationsArrayList.add(TransformationConfiguration.numberFORMAT(2, 0.5));
		transformationsArrayList.add(TransformationConfiguration.changeBOOLEAN());
		
		//start structural
		transformationsArrayList.add(TransformationConfiguration.addPROPERTY());
		transformationsArrayList.add(TransformationConfiguration.deletePROPERTY());
		transformationsArrayList.add(TransformationConfiguration.extractPROPERTY());
		transformationsArrayList.add(TransformationConfiguration.extractPROPERTY(3));
		transformationsArrayList.add(TransformationConfiguration.aggregatePROPERTIES(worker));
		//end structural


		//start logical
		transformationsArrayList.add(TransformationConfiguration.CWSAMEAS(worker)); //25
		transformationsArrayList.add(TransformationConfiguration.CWSAMEASEXISTING(worker));
		transformationsArrayList.add(TransformationConfiguration.CWDIFFERENTFROM(worker));
		transformationsArrayList.add(TransformationConfiguration.EQUIVALENTCLASS(worker));
		transformationsArrayList.add(TransformationConfiguration.EQUIVALENTPROPERTY(worker));
		transformationsArrayList.add(TransformationConfiguration.SUBCLASSOF(worker,1));
		transformationsArrayList.add(TransformationConfiguration.SUBCLASSOF(worker,2));
		transformationsArrayList.add(TransformationConfiguration.DISJOINTWITH(worker));
		transformationsArrayList.add(TransformationConfiguration.SUBPROPERTY(worker));

		 transformationsArrayList.add(TransformationConfiguration.DISJOINTPROPERTY(worker));
		 transformationsArrayList.add(TransformationConfiguration.FUNCTIONALPROPERTY(worker));
		 transformationsArrayList.add(TransformationConfiguration.INVERSEFUNCTIONALPROPERTY(worker));
		 transformationsArrayList.add(TransformationConfiguration.INTERSECTIONOF(worker));
		 transformationsArrayList.add(TransformationConfiguration.UNIONOF(worker));
		 transformationsArrayList.add(TransformationConfiguration.ONEOF(worker));
		 

		//end logical
	}
	public List<Transformation> getTransformationList(){
		return this.transformationsArrayList;
	}
	
	public void setLexicalList(){

		lexicalArrayList = new ArrayList<String>();
		lexicalArrayList.add(cworkNamespace + "title");
		lexicalArrayList.add(cworkNamespace + "shortTitle");
		//lexicalArrayList.add(cworkNamespace + "category");
		lexicalArrayList.add(cworkNamespace + "description");
		lexicalArrayList.add(cworkNamespace + "dateCreated"); //mono date
		lexicalArrayList.add(cworkNamespace + "dateModified"); //mono date
		//lexicalArrayList.add(cworkNamespace + "altText");
		lexicalArrayList.add(core + "shortLabel");
		lexicalArrayList.add(core + "slug");
		lexicalArrayList.add(core + "prefferedLabel");
		lexicalArrayList.add(core + "disambiguationHint");
		lexicalArrayList.add(core + "twitter");
		lexicalArrayList.add(core + "facebook");
		lexicalArrayList.add(core + "officialHomepage");
		lexicalArrayList.add(travel + "hasArea"); //mono number
		lexicalArrayList.add(cworkNamespace + "liveCoverage"); 
		lexicalArrayList.add(dbpediaowl + "country");
	}
	public ArrayList<String> getLexicalList(){
		return this.lexicalArrayList;
	}
	
	public void setStructuralList(){
		structuralArrayList = new ArrayList<String>();
		structuralArrayList.add(cworkNamespace + "title"); 
		structuralArrayList.add(cworkNamespace + "shortTitle");
		//structuralArrayList.add(cworkNamespace + "category");
		structuralArrayList.add(cworkNamespace + "description");
		structuralArrayList.add(cworkNamespace + "altText");
		structuralArrayList.add(core + "shortLabel");
		structuralArrayList.add(core + "slug");
		structuralArrayList.add(core + "prefferedLabel");
		structuralArrayList.add(core + "disambiguationHint");
		structuralArrayList.add(core + "twitter");
		structuralArrayList.add(core + "facebook");
		structuralArrayList.add(core + "officialHomepage");
		structuralArrayList.add(travel + "hasArea");

	}
	public ArrayList<String> getStructuralList(){
		return this.structuralArrayList; 
	}
	
	public void setLogicalList(){
		
		logicalArrayList = new ArrayList<String>();
		//TODO  ta sameas k different prepei na min ginontai kan add sto arraylist an to prob einai 0 gt meta krasarei
		//'H estw na uparxei pososto sto notransf.. auto mporei na sumvainei k se alles periptwseis! check
		
		//Random rand = new Random();
		
		logicalArrayList.add("logicalSameAs"); //sameas
		logicalArrayList.add("logicalSameAsOnExistingCW"); //same as existing
		logicalArrayList.add("logicalDifferentFrom"); //differentfrom
		logicalArrayList.add(cworkNamespace + "NewsItem"); 
		logicalArrayList.add(cworkNamespace + "Programme");
		logicalArrayList.add(cworkNamespace + "BlogPost");
		//logicalArrayList.add(dbpediaowl+"Sport"); //equiv class //not ok for disjoint //TODO 
		logicalArrayList.add(foaf+"Person"); //equiv class , unionof
		logicalArrayList.add(dbpediaowl+"Place"); //equiv class , unionof
		logicalArrayList.add(dbpediaowl+"Event"); //equiv class , unionof
		logicalArrayList.add(dbpediaowl+"Organisation"); //equiv class , unionof
		logicalArrayList.add(core+"Theme"); //unionof
		logicalArrayList.add(travel+"AdministrativeDivision");
		logicalArrayList.add(travel+"City");
		logicalArrayList.add(travel+"Continent");
		logicalArrayList.add(travel+"Coastline");
		logicalArrayList.add(travel+"Country");
		logicalArrayList.add(travel+"GeographicalFeature");
		logicalArrayList.add(travel+"Island");
		logicalArrayList.add(travel+"Ocean");
		logicalArrayList.add(travel+"River");
		logicalArrayList.add(travel+"TierOneAdministrativeDivision");
		logicalArrayList.add(travel+"bodyOfLand");
		logicalArrayList.add(upper+"Person_agent");
		logicalArrayList.add(core + "twitter"); //subpropertyof , disjointproperty
		logicalArrayList.add(core + "facebook"); //subpropertyof, disjointproperty
		logicalArrayList.add(core + "officialHomepage"); //subpropertyof , disjointproperty
		logicalArrayList.add(cworkNamespace + "about"); //subpropertyof
		logicalArrayList.add(cworkNamespace + "mentions"); //subpropertyof
		logicalArrayList.add(travel +"hasArea"); //subpropertyof
		logicalArrayList.add(travel +"hasPopulation"); //subpropertyof
		logicalArrayList.add(ldbc + "Person_Organisation"); //intersectionof
		logicalArrayList.add(ldbc + "Event_Place_Theme"); //intersectionof
		
		initializeFunctionalPropertyEntity();
		switch (functPerc) {
		case FUNCTIONALPROPERTY :
			logicalArrayList.add(core + "primaryTopic"); //functional prop
			break;
			
		case INVERSEFUNCTIONALPROPERTY :
			logicalArrayList.add(bbcNamespace + "primaryContentOf");//inverse functional prop
			break;
		}
	}
	
	public ArrayList<String> getLogicalList(){
		return this.logicalArrayList; 
	}
	public void setComplexTransformationsList(){
		
		//fix this!
		complexArrayList = new ArrayList<String>();
		complexArrayList.add(core + "shortLabel");
		complexArrayList.add(core + "slug");
		complexArrayList.add(core + "prefferedLabel");
		complexArrayList.add(core + "disambiguationHint");
		complexArrayList.add(core + "twitter");
		complexArrayList.add(core + "facebook");
		complexArrayList.add(core + "officialHomepage");
		complexArrayList.add(travel + "hasArea"); //apo lexical mono number
		complexArrayList.add(cworkNamespace + "title");
		complexArrayList.add(cworkNamespace + "shortTitle");
		complexArrayList.add(cworkNamespace + "description");
		complexArrayList.add(cworkNamespace + "dateCreated"); //apo lexical mono date
		complexArrayList.add(cworkNamespace + "dateModified"); //apo lexical mono date
		
		complexArrayList.add(foaf + "name");
		complexArrayList.add(rdfs + "comment");
		complexArrayList.add(dbpediaowl + "country");
		complexArrayList.add(dcterms + "subject");
		complexArrayList.add(rdfs + "label");
		complexArrayList.add(dbpprop + "country");
		complexArrayList.add(dcterms + "subject");
		
		complexArrayList.add(cworkNamespace + "about"); //subpropertyof + structural 
		complexArrayList.add(cworkNamespace + "mentions"); //subpropertyof + structural 
		complexArrayList.add(cworkNamespace + "category");
		complexArrayList.add(dbpprop + "manager");
		complexArrayList.add(dbpprop + "name");
		complexArrayList.add(dbpprop + "nickname");
		complexArrayList.add(dbpprop + "website");
		
		complexArrayList.add(geo + "geometry");
		


		
	}
	public ArrayList<String> getComplexList(){
		return this.complexArrayList; 
	}
	
	
	public Map <String, Transformation> lexicalCases(ArrayList<String> lexicalArrayList){
		this.lexicalArrayList = lexicalArrayList;
		Random rand = new Random();
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < getLexicalList().size(); i++){
			initializeLexicalEntity();
//			if(lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated")){
//				this.lexPerc = Lexical.CHANGEDATEFORMAT;
//			}
//			else if(lexicalArrayList.get(i).equals(cworkNamespace + "dateModified")){
//				this.lexPerc = Lexical.CHANGEDATEFORMAT;
//			}
//			else if(lexicalArrayList.get(i).equals(travel + "hasArea")){
//				this.lexPerc = Lexical.CHANGENUMBERFORMAT;
//			}
			
		switch (lexPerc) {
		
			case BLANKCHARSADDITION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(0));
				}
				else{
					i--;
				}
				break;
			case BLANKCHARSDELETION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
				predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(1));
				}
				else{
					i--;
				}
				break;
			case RANDOMCHARSADDITION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					//System.out.println("RANDOMCHARSADDITION " + lexicalArrayList.get(i));
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(2));
				}
				else{
					i--;
				}
				break;
			case RANDOMCHARSDELETION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					//System.out.println("RANDOMCHARSDELETION " + lexicalArrayList.get(i));
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(3));
				}
				else{
					i--;
				}
				break;
			case RANDOMCHARSMODIFIER :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(4));
				}
				else{
					i--;
				}
				break;
			case TOKENADDITION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(5));
				}
				else{
					i--;
				}
				break;
			case TOKENDELETION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(6));
				}
				else{
					i--;
				}
				break;
			case TOKENSHUFFLE :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(7));
				}
				else{
					i--;
				}
				break;
			case NAMESTYLEABBREVIATION :
				if(lexicalArrayList.get(i).equals(dbpediaowl + "country")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(10));
				}
				else{
					i--;
				}
				break;
			case CHANGESYNONYM :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(11));
				}
				else{
					i--;
				}
				break;
			case CHANGENUMBERFORMAT :
				if(lexicalArrayList.get(i).equals(travel + "hasArea")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(18));
				}
				else{
					i--;
				}
				break;
			case CHANGEDATEFORMAT :
				//System.out.println("i prin to meiwsw " + i);
				if(lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") || lexicalArrayList.get(i).equals(cworkNamespace + "dateModified")){
					//predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(14));
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(rand.nextInt((17 - 13) + 1) + 13));
				}
				else{
					i--;
				}
				//14-16 (rand.nextInt((15 - 13) + 1) + 15));
				//einai kai to 17 alla oxi gia ton format pou exw edw
				break;
			case CHANGELANGUAGE : 
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(12));
				}
				else{
					i--;
				}
				break;
			case CHANGEBOOLEAN :
				if(lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(19));
				}
				else{
				 i--;
				}
				break;
			case CHANGEGENDERFORMAT :
				break;
			case CHANGEANTONYM :
				break;
			case KEEPROOT :
				//dden douleuei to keep root
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(8));
				}
				else{
					i--;
				}
				break;
			case CHANGESYNTHETICCOMPOUND :
				break;
			case CHANGEWORDPERMUTATION :
				if(!lexicalArrayList.get(i).equals(cworkNamespace + "dateCreated") && !lexicalArrayList.get(i).equals(cworkNamespace + "dateModified") && !lexicalArrayList.get(i).equals(travel + "hasArea") && !lexicalArrayList.get(i).equals(cworkNamespace + "liveCoverage")){
					predicatesObjectsMap.put(lexicalArrayList.get(i), transformationsArrayList.get(8));
				}
				else{
					i--;
				}
				break;
			case NOTRANSFORMATION: 
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	public Map <String, Transformation> structuralCases(ArrayList<String> structuralArrayList){
		this.structuralArrayList = structuralArrayList;
		Random rand = new Random();
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < getStructuralList().size(); i++){
			initializeStructuralEntity();
//			if(structuralArrayList.get(i).equals(cworkNamespace + "title")){
//				this.structPerc = Structural.AGGREGATEPROPERTY;
//			}
			switch(structPerc){
			case ADDPROPERTY:
				if(!structuralArrayList.get(i).equals(complexArrayList.get(i))){
					predicatesObjectsMap.put(structuralArrayList.get(i), transformationsArrayList.get(20));
				}
				break;
			case DELETEPROPERTY:
				if(!structuralArrayList.get(i).equals(complexArrayList.get(i))){
					predicatesObjectsMap.put(structuralArrayList.get(i), transformationsArrayList.get(21));
				}
				break;
			case EXTRACTPROPERTY:
				predicatesObjectsMap.put(structuralArrayList.get(i), transformationsArrayList.get(rand.nextInt((22 - 21) + 1) + 21));
				break;
			case AGGREGATEPROPERTY:
				if(structuralArrayList.get(i).equals(cworkNamespace + "title")){
					predicatesObjectsMap.put(structuralArrayList.get(i), transformationsArrayList.get(23));
				}
				else{
				 i--;
				}
				break;
			case NOTRANSFORMATION:
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	public Map <String, Transformation> logicalCases(ArrayList <String>logicalArray){
		this.logicalArrayList = logicalArray;
		Random rand = new Random();
		Map <String, Transformation> predicatesObjectsMap = new HashMap<String, Transformation>();
		for(int i = 0; i < logicalArray.size(); i++){
			initializeLogicalEntity();
			//na elegxo kai ta upoloipa string otan to arraylist einai to complex
		//	System.out.println("logPerc " + logPerc +"        " + logicalArrayList.get(i) +"   "+ i);
		
			switch(logPerc){
			//	!logicalArrayList.add(core + "primaryTopic") && !logicalArrayList.add(bbcNamespace + "primaryContentOf");//inverse functional prop
			case SAMEAS:
				if( logicalArrayList.get(i).equals("logicalSameAs")){predicatesObjectsMap.put("logicalSameAs", transformationsArrayList.get(25));}
				else{ i--; }
				break;
			case SAMEASONEXISTINGCW:
				if(logicalArrayList.get(i).equals("logicalSameAsOnExistingCW")){predicatesObjectsMap.put("logicalSameAsOnExistingCW", transformationsArrayList.get(26));}
				else{ i--; }
				break;
			case DIFFERENTFROM:
				if(  logicalArrayList.get(i).equals("logicalDifferentFrom")){predicatesObjectsMap.put("logicalDifferentFrom", transformationsArrayList.get(27));}
				else{ i--; }
				break;
			case EQUIVALENTCLASS:
				if( !logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(bbcNamespace + "primaryContentOf")
						&& !logicalArrayList.get(i).equals(ldbc + "Person_Organisation") && !logicalArrayList.get(i).equals(ldbc + "Event_Place_Theme") 
						&&!logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(travel +"hasPopulation") 
						&& !logicalArrayList.get(i).equals(travel +"hasArea") && !logicalArrayList.get(i).equals(complexArrayList.get(i)) 
						&&  !logicalArrayList.get(i).equals(core + "twitter") && !logicalArrayList.get(i).equals(core + "facebook")
						&& !logicalArrayList.get(i).equals(core + "officialHomepage") && !logicalArrayList.get(i).equals(cworkNamespace + "about") 
						&& !logicalArrayList.get(i).equals(cworkNamespace + "mentions") && !logicalArrayList.get(i).equals("logicalSameAs") &&
						!logicalArrayList.get(i).equals("logicalSameAsOnExistingCW") && !logicalArrayList.get(i).equals("logicalDifferentFrom")){
					
						predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(28));
				}
				else{
				 i--;
				}
				break;
			case EQUIVALENTPROPERTY:
				//check this
				if( !logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(bbcNamespace + "primaryContentOf")
						&& !logicalArrayList.get(i).equals(ldbc + "Person_Organisation") 
						&& !logicalArrayList.get(i).equals(ldbc + "Event_Place_Theme") 
						&&!logicalArrayList.get(i).equals(core + "primaryTopic") 
						&& !logicalArrayList.get(i).equals("logicalSameAs") 
						&& !logicalArrayList.get(i).equals("logicalSameAsOnExistingCW") 
						&& !logicalArrayList.get(i).equals("logicalDifferentFrom")){
					
						predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(29));
				}
				else{
				 i--;
				}
				break;
			case SUBCLASSOF:
				if( !logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(bbcNamespace + "primaryContentOf")
						&& !logicalArrayList.get(i).equals(ldbc + "Person_Organisation") && !logicalArrayList.get(i).equals(ldbc + "Event_Place_Theme") &&!logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(travel +"hasPopulation") && !logicalArrayList.get(i).equals(travel +"hasArea") &&!logicalArrayList.get(i).equals(complexArrayList.get(i)) &&  !logicalArrayList.get(i).equals(core + "twitter") && !logicalArrayList.get(i).equals(core + "facebook")
						&& !logicalArrayList.get(i).equals(core + "officialHomepage") && !logicalArrayList.get(i).equals(cworkNamespace + "about") 
						&& !logicalArrayList.get(i).equals(cworkNamespace + "mentions") && !logicalArrayList.get(i).equals("logicalSameAs") &&
						!logicalArrayList.get(i).equals("logicalSameAsOnExistingCW") && !logicalArrayList.get(i).equals("logicalDifferentFrom")){
					
						predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(rand.nextInt((30 - 29) + 1) + 30));
				}
				else{
				 i--;
				}
				break;
			case DISJOINTWITH:
				if( !logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(bbcNamespace + "primaryContentOf")
						&& !logicalArrayList.get(i).equals(ldbc + "Person_Organisation") && !logicalArrayList.get(i).equals(ldbc + "Event_Place_Theme") && !logicalArrayList.get(i).equals(core + "primaryTopic") && !logicalArrayList.get(i).equals(travel +"hasPopulation") && !logicalArrayList.get(i).equals(travel +"hasArea") && !logicalArrayList.get(i).equals(complexArrayList.get(i)) && !logicalArrayList.get(i).equals(core + "facebook")
						&& !logicalArrayList.get(i).equals(core + "officialHomepage") && !logicalArrayList.get(i).equals(cworkNamespace + "about") 
						&& !logicalArrayList.get(i).equals(cworkNamespace + "mentions") && !logicalArrayList.get(i).equals("logicalSameAs") &&
						!logicalArrayList.get(i).equals("logicalSameAsOnExistingCW") && !logicalArrayList.get(i).equals("logicalDifferentFrom")){
					
						predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(32));
				}
				else{
				 i--;
				}
				break;
			case SUBPROPERTYOF:
				
				if(logicalArrayList.get(i).equals(travel +"hasPopulation") ||logicalArrayList.get(i).equals(travel +"hasArea") || logicalArrayList.get(i).equals(core + "twitter") || logicalArrayList.get(i).equals(core + "facebook")
						|| logicalArrayList.get(i).equals(core + "officialHomepage") || logicalArrayList.get(i).equals(cworkNamespace + "about") 
						|| logicalArrayList.get(i).equals(cworkNamespace + "mentions")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(33));
				}
				else{
				 i--;
				}
				
				break; 
			case DISJOINTPROPERTY:
				if(logicalArrayList.get(i).equals(core + "facebook") || 
					logicalArrayList.get(i).equals(core + "twitter") ||
					logicalArrayList.get(i).equals(core + "officialHomepage")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(34));
				}
				else{
					i--;
				}
				break; 
			case FUNCTIONALPROPERTY:
				if(logicalArrayList.get(i).equals(core + "primaryTopic")){
				 predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(35));
				 //System.out.println("FUNCTIONALPROPERTY");
				}
				else{
					i--;
				}
				 break;
			case INVERSEFUNCTIONALPROPERTY:
				if(logicalArrayList.get(i).equals(bbcNamespace + "primaryContentOf")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(36));
					//System.out.println("INVERSEFUNCTIONALPROPERTY");
				}
				else{
					i--;
				}
				 break;
			case INTERSECTIONOF:
				if(logicalArrayList.get(i).equals(ldbc + "Person_Organisation") || logicalArrayList.get(i).equals(ldbc + "Event_Place_Theme")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(37));
					//System.out.println("INTERSECTIONOF case  " + logicalArrayList.get(i) +"   "+  transformationsArrayList.get(37));
				}
				else{
					i--;
				}
				break;
				
			case UNIONOF:
				if(logicalArrayList.get(i).equals(foaf + "Person") 
						|| logicalArrayList.get(i).equals(dbpediaowl + "Event")
						|| logicalArrayList.get(i).equals(dbpediaowl + "Organisation")
						|| logicalArrayList.get(i).equals(dbpediaowl + "Place")
						|| logicalArrayList.get(i).equals(core + "Theme")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(38));
					//System.out.println("union of case  " + logicalArrayList.get(i));
				}
				else{
					i--;
				}
				 break;
				 
			case ONEOF:
				if(logicalArrayList.get(i).equals(travel+"AdministrativeDivision") 
						|| logicalArrayList.get(i).equals(travel+"City")
						|| logicalArrayList.get(i).equals(travel+"Continent")
						|| logicalArrayList.get(i).equals(travel+"Country")
						|| logicalArrayList.get(i).equals(travel+"GeographicalFeature")
						|| logicalArrayList.get(i).equals(travel+"Island")
						|| logicalArrayList.get(i).equals(travel+"Ocean")
						|| logicalArrayList.get(i).equals(travel+"River")
						|| logicalArrayList.get(i).equals(travel+"TierOneAdministrativeDivision")
						|| logicalArrayList.get(i).equals(travel+"bodyOfLand")
						|| logicalArrayList.get(i).equals(travel+"Person_agent")){
					predicatesObjectsMap.put(logicalArrayList.get(i), transformationsArrayList.get(39));
					//System.out.println("one of case  " + logicalArrayList.get(i));
				}
				else{
					i--;
				}
				 break;
			case NOTRANSFORMATION:
				break;
			}
		}
		return predicatesObjectsMap;
	}
	
	public void setTransformationConf(){		
		setTransformationList();
		lexicalArrayList = getLexicalList();
		structuralArrayList = getStructuralList();
		setLogicalList();
		logicalArrayList = getLogicalList();
		
		// 	na valw auta pou einai sto string sto map se ena array
		//diatrexw to array kai kalw tin antistoixi initialize gia kathe case
		//se kathe case lew : 
		// predicatesObjectsMap.put(array[i],transformationsArrayList.get(num of transformation selected));
		//kai elegxw kai sugkekrimenes periptoseis
		
		initializeTransformationsEntity();
		switch (transfPerc) { 
		//prosoxi otan allazoun tanoumera sto transformationsArrayList
		case LEXICAL :
			predicatesObjectsMap = lexicalCases(lexicalArrayList);
			break;
		
		case STRUCTURAL :
			predicatesObjectsMap = structuralCases(structuralArrayList);
			break;
			
		case LOGICAL:
			predicatesObjectsMap = logicalCases(logicalArrayList);
			break;
			
		case SIMPLECOMBINATION :
			initializeSimpleCombinationEntity();
			switch(simplePerc){
			case LEXICAL :
				predicatesObjectsMap = lexicalCases(lexicalArrayList);
				break;
			
			case STRUCTURAL :
				predicatesObjectsMap = structuralCases(structuralArrayList);
				break;
				
			case LOGICAL:
				predicatesObjectsMap = logicalCases(logicalArrayList);
				break;
				
			case NOTRANSFORMATION:
				break;
			}
			
			break;
		//N transformations per triple N >= 2 && N <= 3 	
		case COMPLEXCOMBINATION :
	initializeComplexCombinationEntity();
			
			List <Transformation> transformation;
			Transformation lexvalue;
			Transformation structvalue;
			Transformation logvalue;
			Map <String, Transformation> predicatesObjectsMapTemp;
			int pick=0;
			Random rand = new Random();
			predicatesObjectsComplexMap = new HashMap<String, List<Transformation>>();
			//System.out.println(complexPerc);
			for(int i = 0; i < getComplexList().size(); i++){
				//initializeComplexCombinationEntity();
				switch(complexPerc){
				
				case LEXICAL_STRUCTURAL:
//					System.out.println("LEXICAL_STRUCTURAL");
					transformation = new ArrayList<Transformation>();
					
					if(getComplexList().get(i).equals(foaf + "name") || getComplexList().get(i).equals(cworkNamespace + "title")){ 
						pick = new Random().nextInt(Lexical.values().length);
						transformation.add(transformationsArrayList.get(pick)); //lexical
						transformation.add(transformationsArrayList.get(23)); //aggr
						
						predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					}
					else if(i<10){ //10 first  elements
						pick = new Random().nextInt(Lexical.values().length);

						transformation.add(transformationsArrayList.get(pick)); //lexical
						int pick_str = rand.nextInt((19 - 18) + 1) + 19;
						
						transformation.add(transformationsArrayList.get(pick_str)); //extract
						predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					}
				

					
//					transformation = new ArrayList<Transformation>();			
//					predicatesObjectsMapTemp = lexicalCases(complexArrayList); 
//					lexvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(lexvalue);
//					predicatesObjectsMapTemp = structuralCases(complexArrayList); 
//					structvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(structvalue);
//					predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					

					break;
					
				case LEXICAL_LOGICAL:
//					System.out.println("LEXICAL_LOGICAL");
					transformation = new ArrayList<Transformation>();
					int pick_ [] = {29,33,34};
					if(i>=10){ //after 10 first  elements
						pick = new Random().nextInt(Lexical.values().length);

						transformation.add(transformationsArrayList.get(pick)); //lexical
						transformation.add(transformationsArrayList.get(29)); //equiv prop

						predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					}
					else if(getComplexList().get(i).equals(core + "officialHomepage") || getComplexList().get(i).equals(core + "facebook") || getComplexList().get(i).equals(core + "twitter")){ //10 first  elements
						pick = new Random().nextInt(Lexical.values().length);
						
						transformation.add(transformationsArrayList.get(pick)); //lexical
						int pick_log = pick_[rand.nextInt(pick_.length)];

						transformation.add(transformationsArrayList.get(pick_log)); // equiv pr or sub pro or disj prop 
						predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					}

//					transformation = new ArrayList<Transformation>();
//					predicatesObjectsMapTemp = lexicalCases(complexArrayList); 
//					lexvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(lexvalue); 
//					predicatesObjectsMapTemp = logicalCases(complexArrayList); 
//					logvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(logvalue);
//					predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);

					break;
					
				case STRUCTURAL_LOGICAL:
//					//System.out.println("STRUCTURAL_LOGICAL");
//					transformation = new ArrayList<Transformation>();
//					
//					predicatesObjectsMapTemp = logicalCases(complexArrayList); 
//					logvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(logvalue);
//					
//					predicatesObjectsMapTemp = structuralCases(complexArrayList); 
//					structvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(structvalue);
//					
//					predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
					

					break;
					
				case LEXICAL_STRUCTURAL_LOGICAL:
//					//System.out.println("LEXICAL_STRUCTURAL_LOGICAL");
//					transformation = new ArrayList<Transformation>();
//					
//					predicatesObjectsMapTemp = lexicalCases(complexArrayList); 
//					lexvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(lexvalue);
//					
//					predicatesObjectsMapTemp = logicalCases(complexArrayList); 
//					logvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(logvalue);
//					
//					predicatesObjectsMapTemp = structuralCases(complexArrayList); 
//					structvalue = (Transformation) predicatesObjectsMapTemp.values().toArray()[0];
//					transformation.add(structvalue);
//					
//					predicatesObjectsComplexMap.put(getComplexList().get(i), transformation);
//					

					break;
					
				case NOTRANSFORMATION:
					//System.out.println("NOTRANSFORMATION");
					break;
					
				}
			}
			break;
			
		case NOTRANSFORMATION :
			break;
		
		}
	

		
			
	}
	//return a map that as key contains every predicate and as value the type of transformation
	//that will be done o n its object. The key might also denote that the object will not change
	public HashMap<String, Transformation> getTransformationConf(){return (HashMap<String, Transformation>) predicatesObjectsMap;}
	public HashMap<String, List<Transformation>> getTransformationConfComplex(){return (HashMap<String, List<Transformation>>) predicatesObjectsComplexMap;}	
	
}
