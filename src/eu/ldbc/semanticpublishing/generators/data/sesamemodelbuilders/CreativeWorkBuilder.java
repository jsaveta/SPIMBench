package eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.XMLSchema;

import eu.ldbc.semanticpublishing.properties.Definitions;
import eu.ldbc.semanticpublishing.refdataset.DataManager;
import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.util.RandomUtil;

public class CreativeWorkBuilder implements SesameBuilder {

	private Date presetDate;
	private CWType cwType = CWType.BLOG_POST;
	private String cwTypeString = "cwork:BlogPost";
	private CWThing cwThing = CWThing.PERSON; //core:Thing subclass
	private WDProduct wdProduct = WDProduct.BLOGS;//product type
	private primaryTopicSubProp primaryTopicSubProp_ = primaryTopicSubProp.TWITTER; //subproperty of primarytopicof
	private CWThumbnail cwThumbnail = CWThumbnail.STANDARDTHUMBNAIL;//thumbnail
	private Travel cwTravel = Travel.ISLAND;//travel
	
	private String cwThingString = "core:Person";
	private String dwProductString = "bbc:Person";
	private String cwThumbnailString = "cwork:StandardThumbnail";
	private String primaryTopicSubPropString = "core:twitter";
	private String cwTravelString = "travel:Island";
	/*jsaveta end*/
	private String contextURI = "";
	private String aboutPresetUri = "";
	private String optionalAboutPresetUri = "";
	private String mentionsPresetUri = "";
	private String optionalMentionsPresetUri = "";
	private int aboutsCount = 0;
	private int mentionsCount = 0;	
	private Entity cwEntity;
	private boolean usePresetData = false;
	
	private final RandomUtil ru;
	
	private static final String rdfTypeNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	private static final String bbcNamespace = "http://www.bbc.co.uk/ontologies/bbc/";
	/*jsaveta start*/
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	private static final String dc = "http://purl.org/dc/elements/1.1/";
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String owl = "http://www.w3.org/2002/07/owl#";
	private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	private static final String dbpprop = "http://dbpedia.org/property/";
	private static final String dcterms = "http://purl.org/dc/terms/";
	private static final String travel = "http://www.co-ode.org/roberts/travel.owl#";
	private static final String upper = "http://www.co-ode.org/roberts/upper.owl#";
	private static final String ldbc = "http://www.ldbc.eu/";
	/*jsaveta end*/
	
	private static enum CWType {
		BLOG_POST, NEWS_ITEM, PROGRAMME
	}
	/*jsaveta start*/
	private static enum CWThing { //core:Thing subclasses
		//foaf:Person
		//dbpedia:Place
		//dbpedia:Event
		//dbpedia:Organisation
		//core:Thing
		//http://www.co-ode.org/roberts/travel.owl //den eimai sigouri an to travel prepei na mpei edw
		PERSON, PLACE, EVENT, ORGANISATION, THEME,  PERSON_ORGANISATION, EVENT_PLACE_THEME, TRAVEL
	}
	
	private static enum WDProduct { //webdocument product 
		BLOGS , EDUCATION ,SPORT , NEWS ,MUSIC 
	}
	
	private static enum primaryTopicSubProp {
		TWITTER , FACEBOOK ,OFFICIALHOMEPAGE  
	}
	
	private static enum CWThumbnail {
		STANDARDTHUMBNAIL , CLOSEUPTHUMBNAIL ,FIXEDSIZE66THUMBNAIL, FIXEDSIZE268THUMBNAIL , FIXEDSIZE466THUMBNAIL
	}
	
	private static enum Travel {
		ADMINISTRATIVEDIVISION, CITY, COASTLINE, CONTINENT, COUNTRY, GEOGRAPHICALFEATURE, ISLAND, OCEAN, RIVER,TIERONEADMINISTRATIVEDIVISION,BODYOFLAND,PERSONAGENT
	}
	/*jsaveta end*/
	
	public CreativeWorkBuilder(String contextURI, RandomUtil ru) {
		this.contextURI = contextURI;
		this.ru = ru;
		Definitions.reconfigureAllocations(ru.getRandom());
		this.aboutsCount = Definitions.aboutsAllocations.getAllocation();
		this.mentionsCount = Definitions.mentionsAllocations.getAllocation();
		initializeCreativeWorkEntity(contextURI.replace("/context/", "/things/"));
	}
	
	public CreativeWorkBuilder(long cwID, RandomUtil ru) {
		this.contextURI = ru.numberURI("things", cwID, true, true).replace("/things/", "/context/");
		this.ru = ru;
		Definitions.reconfigureAllocations(ru.getRandom());
		this.aboutsCount = Definitions.aboutsAllocations.getAllocation();
		this.mentionsCount = Definitions.mentionsAllocations.getAllocation();
		initializeCreativeWorkEntity(contextURI.replace("/context/", "/things/"));
	}
	
	/**
	 * Creates an Entity with existing/new Creative Work URI and the label of a random (popular or not) existing entity.
	 * Label will be used in the title of that Creative Work
	 * @param updateCwUri - if empty, a new URI is generated
	 * @return the CreativeWork entity
	 */
	private void initializeCreativeWorkEntity(String updateCwUri) {
		Entity e;
		String cwURInew;
		try {			
			if (!updateCwUri.isEmpty()) {
				cwURInew = updateCwUri;
			} else {
				cwURInew = ru.numberURI("things", DataManager.creativeWorksNextId.incrementAndGet(), true, true);				
			}
			
			this.contextURI = cwURInew.replace("/things/", "/context/");
			
			switch (Definitions.creativeWorkTypesAllocation.getAllocation()) {
				case 0 :
					this.cwType = CWType.BLOG_POST;
					this.cwTypeString = "cwork:BlogPost";
					break;
				case 1 :
					this.cwType = CWType.NEWS_ITEM;
					this.cwTypeString = "cwork:NewsItem";
					break;
				case 2 :
					this.cwType = CWType.PROGRAMME;
					this.cwTypeString = "cwork:Programme";
					break;					
			}
			/*jsaveta start*/
			
			//make sure that I am not missing something
			switch (Definitions.productTypesAllocation.getAllocation()) {
			case 0 :
				this.wdProduct = WDProduct.BLOGS; 
				this.dwProductString = "bbc:Blogs";
				break;
			case 1 :
				this.wdProduct = WDProduct.EDUCATION; 
				this.dwProductString = "bbc:Education";
				break;
			case 2 :
				this.wdProduct = WDProduct.SPORT; 
				this.dwProductString = "bbc:Sport";
				break;
			case 3 :
				this.wdProduct = WDProduct.NEWS; 
				this.dwProductString = "bbc:News";
				break;
			case 4 :
				this.wdProduct = WDProduct.MUSIC; 
				this.dwProductString = "bbc:Music";
				break;
			}
			
			//edw den prepei na einai core!
			switch (Definitions.coreThingSubClassAllocation.getAllocation()) {
			case 0 :
				this.cwThing = CWThing.PERSON; 
				this.cwThingString = "foaf:Person";
				break;
			case 1 :
				this.cwThing = CWThing.PLACE; 
				this.cwThingString = "dbpediaowl:Place";
				break;
			case 2 :
				this.cwThing = CWThing.EVENT; 
				this.cwThingString = "dbpediaowl:Event";
				break;
			case 3 :
				this.cwThing = CWThing.ORGANISATION; 
				this.cwThingString = "dbpediaowl:Organisation";
				break;
			case 4 :
				this.cwThing = CWThing.THEME; 
				this.cwThingString = "dbpediaowl:Theme";
				break;
			case 5 :
				this.cwThing = CWThing.PERSON_ORGANISATION; 
				this.cwThingString = "ldbc:Person_Organisation";
				break;
			case 6 :
				this.cwThing = CWThing.EVENT_PLACE_THEME; 
				this.cwThingString = "ldbc:Event_Place_Theme";
				break;
			case 7 :
				this.cwThing = CWThing.TRAVEL; 
				this.cwThingString = "dbpediaowl:island";
				break;
				
			}
			
			switch (Definitions.corePrimaryTopicAllocation.getAllocation()) {
			case 0 :
				this.primaryTopicSubProp_ = primaryTopicSubProp.TWITTER; 
				this.primaryTopicSubPropString = "core:twitter";
				break;
			case 1 :
				this.primaryTopicSubProp_ = primaryTopicSubProp.FACEBOOK; 
				this.primaryTopicSubPropString = "core:faceboook";
				this.cwThingString = "core:Place";
				break;
			case 2 :
				this.primaryTopicSubProp_ = primaryTopicSubProp.OFFICIALHOMEPAGE; 
				this.primaryTopicSubPropString = "core:officialHomepage";
				break;
			
			}
			switch (Definitions.thumbnailAllocation.getAllocation()) {
			case 0 :
				this.cwThumbnail = CWThumbnail.STANDARDTHUMBNAIL; 
				this.cwThumbnailString = "core:StandardThumbnail";
				break;
			case 1 :
				this.cwThumbnail = CWThumbnail.CLOSEUPTHUMBNAIL; 
				this.cwThumbnailString = "core:CloseUpThumbnail";
				break;
			case 2 :
				this.cwThumbnail = CWThumbnail.FIXEDSIZE66THUMBNAIL; 
				this.cwThumbnailString = "core:FixedSize66Thumbnail";
				break;
			case 3 :
				this.cwThumbnail = CWThumbnail.FIXEDSIZE268THUMBNAIL; 
				this.cwThumbnailString = "core:FixedSize268Thumbnail";
				break;
			case 4 :
				this.cwThumbnail = CWThumbnail.FIXEDSIZE466THUMBNAIL; 
				this.cwThumbnailString = "core:FixedSize466Thumbnail";
				break;
			}
		
			switch (Definitions.travelAllocation.getAllocation()) {
			case 0 :
				this.cwTravel = Travel.ADMINISTRATIVEDIVISION; 
				this.cwTravelString = "travel:AdministrativeDivision";
				break;
			case 1 :
				this.cwTravel = Travel.CITY; 
				this.cwTravelString = "travel:City";
				break;
			case 2 :
				this.cwTravel = Travel.COASTLINE; 
				this.cwTravelString = "travel:Coastline";
				break;
			case 3 :
				this.cwTravel = Travel.CONTINENT; 
				this.cwTravelString = "travel:Continent";
				break;
			case 4 :
				this.cwTravel = Travel.COUNTRY; 
				this.cwTravelString = "travel:Country";
				break;
			case 5 :
				this.cwTravel = Travel.GEOGRAPHICALFEATURE; 
				this.cwTravelString = "travel:GeographicalFeature";
				break;			
			case 6 :
				this.cwTravel = Travel.ISLAND; 
				this.cwTravelString = "travel:Island";
				break;
			case 7 :
				this.cwTravel = Travel.OCEAN; 
				this.cwTravelString = "travel:Ocean";
				break;
			case 8 :
				this.cwTravel = Travel.RIVER; 
				this.cwTravelString = "travel:River";
				break;

			case 9 :
				this.cwTravel = Travel.TIERONEADMINISTRATIVEDIVISION; 
				this.cwTravelString = "travel:TierOneAdministrativeDivision";
				break;
			case 10 :
				this.cwTravel = Travel.BODYOFLAND; 
				this.cwTravelString = "travel:bodyOfLand";
				break;
			case 11 :
				this.cwTravel = Travel.PERSONAGENT; 
				this.cwTravelString = "upper:Person_agent";
				break;


			}
			/*jsaveta end*/
			boolean usePopularEntity = Definitions.usePopularEntities.getAllocation() == 0;
			
			if (usePopularEntity) {
				e = DataManager.popularEntitiesList.get(ru.nextInt(DataManager.popularEntitiesList.size()));
			} else {
				e = DataManager.regularEntitiesList.get(ru.nextInt(DataManager.regularEntitiesList.size()));
			}
			
			this.cwEntity = new Entity(cwURInew, e.getLabel(), e.getURI(), e.getCategory());
		} catch (IllegalArgumentException iae) {
			if (DataManager.popularEntitiesList.size() + DataManager.regularEntitiesList.size() == 0) {
				System.err.println("No reference data found in repository, initialize repository with ontologies and reference data first!");
			}
			throw new IllegalArgumentException(iae);
		}
	}
	
	public void setDateIncrement(Date startDate, int daySteps) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DATE, daySteps);
		calendar.add(Calendar.HOUR, ru.nextInt(24));
		calendar.add(Calendar.MINUTE, ru.nextInt(60));
		calendar.add(Calendar.SECOND, ru.nextInt(60));
		//milliseconds are fixed, some strange problem when re-running the generator - produces different values
		calendar.set(Calendar.MILLISECOND, ru.nextInt(1000));
		
		this.presetDate = calendar.getTime();
	}
	
	public void setUsePresetData(boolean usePresetData) {
		this.usePresetData = true;
	}
	
	public void setAboutPresetUri(String aboutUri) {
		this.aboutPresetUri = aboutUri;
	}
	
	public void setOptionalAboutPresetUri(String optionalAboutUri) {
		this.optionalAboutPresetUri = optionalAboutUri;
	}
	
	public void setMentionsPresetUri(String mentionsUri) {
		this.mentionsPresetUri = mentionsUri;
	}
	
	public void setOptionalMentionsPresetUri(String optionalMentionsUri) {
		this.optionalMentionsPresetUri = optionalMentionsUri;
	}
		
	/**
	 * Builds a Sesame Model of the Insert query template using values from templateParameterValues array.
	 * Which gets initialized with values during construction of the object.
	 */
	@Override
	public synchronized Model buildSesameModel() {
		Model model = new LinkedHashModel();
		String adaptedContextUri = contextURI.replace("<", "").replace(">", "");
		URI context = sesameValueFactory.createURI(adaptedContextUri);
		
		//Set Creative Work Type
		URI subject = sesameValueFactory.createURI(adaptedContextUri.replace("/context/", "/things/"));
		URI predicate = sesameValueFactory.createURI(rdfTypeNamespace);
		String s = cwTypeString;
		s = s.replace("cwork:", cworkNamespace);
		Value object = sesameValueFactory.createURI(s);
		
		model.add(subject, predicate, object, context);
		
		//Set Title
		predicate = sesameValueFactory.createURI(cworkNamespace + "title");
		object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
		
		model.add(subject, predicate, object, context);

		//Set Short Title
		predicate = sesameValueFactory.createURI(cworkNamespace + "shortTitle");
		object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords("", 10, false, false));
		
		model.add(subject, predicate, object, context);

		//Set Category
		predicate = sesameValueFactory.createURI(cworkNamespace + "category");
		if(cwEntity.getCategory().equals("Place") || cwEntity.getCategory().equals("Event") || cwEntity.getCategory().equals("Organisation")){
			object = sesameValueFactory.createURI(ru.stringURI("http://dbpedia.org/ontology","", cwEntity.getCategory(), false, false));
		}
		else if(cwEntity.getCategory().equals("Person")){
			object = sesameValueFactory.createURI(ru.stringURI("http://xmlns.com/foaf/0.1","", cwEntity.getCategory(), false, false));
		}
		else{
			object = sesameValueFactory.createURI(ru.stringURI("category", cwEntity.getCategory(), false, false));
		}

		model.add(subject, predicate, object, context);
		
		//Set Description
		predicate = sesameValueFactory.createURI(cworkNamespace + "description");
		object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords("", ru.nextInt(8, 26), false, false));
		
		model.add(subject, predicate, object, context);
		
		boolean initialAboutUriUsed = false;
		String initialUri = this.cwEntity.getObjectFromTriple(Entity.ENTITY_ABOUT);
		
		if (usePresetData) {
			initialUri = aboutPresetUri;
		}
		
		//Set About(s)
		//using aboutsCount + 1, because Definitions.aboutsAllocations.getAllocation() returning 0 is still a valid allocation
		ArrayList<String> about_array = new ArrayList<String>();
		for (int i = 0; i < aboutsCount + 1; i++) {
			predicate = sesameValueFactory.createURI(cworkNamespace + "about");
			
			if (!initialAboutUriUsed) {
				initialAboutUriUsed = true;
			} else {
				initialUri = DataManager.regularEntitiesList.get(ru.nextInt(DataManager.regularEntitiesList.size())).getURI();
				//if(this.cwEntity.getCategory().equals("Person") || this.cwEntity.getCategory().equals("Place") || this.cwEntity.getCategory().equals("Event") || this.cwEntity.getCategory().equals("Organisation")){
					about_array.add(initialUri);//.replace("<", "").replace(">", "")
					//System.out.println("initialUri "+initialUri);
					//System.out.println("cwEntity.getCategory() "+cwEntity.getCategory());
				//}
				
			}
			
			object = sesameValueFactory.createURI(initialUri.replace("<", "").replace(">", ""));
			
			model.add(subject, predicate, object, context);
		}
		
		//Add optional About URI - in case of modeling correlations - disregard the about distributions
		if (usePresetData && !optionalAboutPresetUri.isEmpty()) {
			predicate = sesameValueFactory.createURI(cworkNamespace + "about");
			object = sesameValueFactory.createURI(optionalAboutPresetUri.replace("<", "").replace(">", ""));
			model.add(subject, predicate, object, context);			
		}
		
		//Set Mention(s)
		//using mentionsCount + 1, because Definitions.mentionsAllocations.getAllocation() returning 0 is still a valid allocation
		boolean geonamesLocationUsedLocal = false;			
		for (int i = 0; i < mentionsCount + 1; i++) {
			predicate = sesameValueFactory.createURI(cworkNamespace + "mentions");
			
			if (!initialAboutUriUsed) {
				initialAboutUriUsed = true;
			} else {
				if (!geonamesLocationUsedLocal) {
					geonamesLocationUsedLocal = true;
					initialUri = DataManager.geonamesIdsList.get(ru.nextInt(DataManager.geonamesIdsList.size()));
				} else {
					initialUri = DataManager.regularEntitiesList.get(ru.nextInt(DataManager.regularEntitiesList.size())).getURI();
				}
			}
			
			object = sesameValueFactory.createURI(initialUri.replace("<", "").replace(">", ""));			
			
			model.add(subject, predicate, object, context);
		}

		//Add Mentions URI - in case of modeling correlations - disregard the mentions distributions
		if (usePresetData && !mentionsPresetUri.isEmpty()) {
			predicate = sesameValueFactory.createURI(cworkNamespace + "mentions");
			object = sesameValueFactory.createURI(mentionsPresetUri.replace("<", "").replace(">", ""));
			model.add(subject, predicate, object, context);			
		}		

		//Add optional Mentions URI - in case of modeling correlations - disregard the mentions distributions
		if (usePresetData && !optionalMentionsPresetUri.isEmpty()) {
			predicate = sesameValueFactory.createURI(cworkNamespace + "mentions");
			object = sesameValueFactory.createURI(optionalMentionsPresetUri.replace("<", "").replace(">", ""));
			model.add(subject, predicate, object, context);			
		}		

		switch (cwType) {
		case BLOG_POST :
			//Set Audience
			predicate = sesameValueFactory.createURI(cworkNamespace + "audience");
			object = sesameValueFactory.createURI(cworkNamespace + "InternationalAudience");
			
			model.add(subject, predicate, object, context);
			
			//Set LiveCoverage
			predicate = sesameValueFactory.createURI(cworkNamespace + "liveCoverage");
			object = sesameValueFactory.createLiteral(false);
			
			model.add(subject, predicate, object, context);
			
			//Set PrimaryFormat
			predicate = sesameValueFactory.createURI(cworkNamespace + "primaryFormat");
			object = sesameValueFactory.createURI(cworkNamespace + "TextualFormat");
			
			model.add(subject, predicate, object, context);
			
			if (ru.nextBoolean()) {
				//Set additional primary format randomly
				predicate = sesameValueFactory.createURI(cworkNamespace + "primaryFormat");
				object = sesameValueFactory.createURI(cworkNamespace + "InteractiveFormat");
				
				model.add(subject, predicate, object, context);
			}
			
			break;
		case NEWS_ITEM :
			//Set Audience
			predicate = sesameValueFactory.createURI(cworkNamespace + "audience");
			object = sesameValueFactory.createURI(cworkNamespace + "NationalAudience");
			
			model.add(subject, predicate, object, context);
			
			//Set LiveCoverage
			predicate = sesameValueFactory.createURI(cworkNamespace + "liveCoverage");
			object = sesameValueFactory.createLiteral(false);
			
			model.add(subject, predicate, object, context);			
			
			//Set PrimaryFormat
			predicate = sesameValueFactory.createURI(cworkNamespace + "primaryFormat");
			object = sesameValueFactory.createURI(cworkNamespace + "TextualFormat");
			
			model.add(subject, predicate, object, context);			
			
			//Set additional primary format
			predicate = sesameValueFactory.createURI(cworkNamespace + "primaryFormat");
			object = sesameValueFactory.createURI(cworkNamespace + "InteractiveFormat");
			
			model.add(subject, predicate, object, context);
			
			break;
		case PROGRAMME : 
			//Set Audience
			predicate = sesameValueFactory.createURI(cworkNamespace + "audience");
			object = sesameValueFactory.createURI(cworkNamespace + "InternationalAudience");
			
			model.add(subject, predicate, object, context);
			
			//Set LiveCoverage
			predicate = sesameValueFactory.createURI(cworkNamespace + "liveCoverage");
			object = sesameValueFactory.createLiteral(true);
			
			model.add(subject, predicate, object, context);			
			
			//Set PrimaryFormat
			predicate = sesameValueFactory.createURI(cworkNamespace + "primaryFormat");
			if (ru.nextBoolean()) {
				object = sesameValueFactory.createURI(cworkNamespace + "VideoFormat");
			} else {
				object = sesameValueFactory.createURI(cworkNamespace + "AudioFormat");
			}
			
			model.add(subject, predicate, object, context);			
						
			break;
		}
		
		//Creation and Modification date
		Calendar calendar = Calendar.getInstance();
		
		if (usePresetData) {
			//Set Creation Date
			predicate = sesameValueFactory.createURI(cworkNamespace + "dateCreated");
			object = sesameValueFactory.createLiteral(presetDate);
			model.add(subject, predicate, object, context);
			
			//Set Modification Date
			calendar.setTime(presetDate);
			calendar.add(Calendar.MONTH, 1 * ru.nextInt(12));
			calendar.add(Calendar.DATE, 1 * ru.nextInt(31));
			calendar.add(Calendar.HOUR, 1 * ru.nextInt(24));
			calendar.add(Calendar.MINUTE, 1 * ru.nextInt(60));
			calendar.add(Calendar.SECOND, 1 * ru.nextInt(60));
			//milliseconds are fixed, some strange problem when re-running the generator - produces different values
			calendar.set(Calendar.MILLISECOND, ru.nextInt(1000));
			
			predicate = sesameValueFactory.createURI(cworkNamespace + "dateModified");
			object = sesameValueFactory.createLiteral(calendar.getTime());
		} else {
			Date creationDate = ru.randomDateTime();
			
			//Set Creation Date
			predicate = sesameValueFactory.createURI(cworkNamespace + "dateCreated");
			object = sesameValueFactory.createLiteral(creationDate);
			
			model.add(subject, predicate, object, context);
			
			//Set Modification Date
			calendar.setTime(creationDate);
			calendar.add(Calendar.MONTH, 1 * ru.nextInt(12));
			calendar.add(Calendar.DATE, 1 * ru.nextInt(31));
			calendar.add(Calendar.HOUR, 1 * ru.nextInt(24));
			calendar.add(Calendar.MINUTE, 1 * ru.nextInt(60));
			calendar.add(Calendar.SECOND, 1 * ru.nextInt(60));
			//milliseconds are fixed, some strange problem when re-running the generator - produces different values
			calendar.set(Calendar.MILLISECOND, ru.nextInt(1000));
			
			predicate = sesameValueFactory.createURI(cworkNamespace + "dateModified");
			object = sesameValueFactory.createLiteral(calendar.getTime());
		}
		
		model.add(subject, predicate, object, context);
		
		//Set Thumbnail
		predicate = sesameValueFactory.createURI(cworkNamespace + "thumbnail");
		object = sesameValueFactory.createURI(ru.randomURI("thumbnail", false, false));
		
		model.add(subject, predicate, object, context);
		
		//Set cwork:altText to thumbnail
		predicate = sesameValueFactory.createURI(cworkNamespace + "altText");
		object = sesameValueFactory.createLiteral("thumbnail atlText for CW " + adaptedContextUri);
		
		model.add(subject, predicate, object, context);
		

		//Set PrimaryContentOf
//		int random = ru.nextInt(1, 4);
		//i for tha mporouse na paralifthei an theloyme na dimiourgeite ena apo to kathe ena!
//		for (int i = 0; i < random; i++) {
		
//esvisa thn for gia to owl:InverseFunctionalProperty			
			boolean personUsedLocal = false;
			boolean placeUsedLocal = false;
			boolean eventUsedLocal = false;
			boolean organisationUsedLocal = false;
			boolean sportUsedLocal = false;
			boolean travelUsedLocal = false;
			String initialId = "";
			int temp = 0;
			
			
			predicate = sesameValueFactory.createURI(bbcNamespace + "primaryContentOf");
			String primaryContentUri = ru.randomURI("things", false, true);
			object = sesameValueFactory.createURI(primaryContentUri);
			
			model.add(subject, predicate, object, context);
			
			URI subjectPrimaryContent = sesameValueFactory.createURI(primaryContentUri);
			predicate = sesameValueFactory.createURI(bbcNamespace + "webDocumentType");
			if (ru.nextBoolean()) {
				object = sesameValueFactory.createURI(bbcNamespace + "HighWeb");
			} else {
				object = sesameValueFactory.createURI(bbcNamespace + "Mobile");
			}
			
			model.add(subjectPrimaryContent, predicate, object, context);
		
			//Set Product
			predicate = sesameValueFactory.createURI(bbcNamespace + "ProductType");
			//String productUri = ru.randomURI("things", false, true); 
			//URI subjectProduct = sesameValueFactory.createURI(productUri);
			switch (wdProduct) {
			case BLOGS :
				object = sesameValueFactory.createURI(bbcNamespace + "Blogs");
				model.add(subjectPrimaryContent, predicate, object, context);
				break;
			case EDUCATION :
				object = sesameValueFactory.createURI(bbcNamespace + "Education");
				model.add(subjectPrimaryContent, predicate, object, context);
				break;
			
			case SPORT : //is this OK?

				temp = ru.nextInt(DataManager.sportsIdsList.size());
				if (!sportUsedLocal) {
					sportUsedLocal = true;
					initialId = DataManager.sportsIdsList.get(temp);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				URI subject_wdProduct = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(dbpediaowl+"Sport");
				model.add(subject_wdProduct, predicate, object, context);
				//set sport name
				if(!DataManager.sportsCommentsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "comment");
					object = sesameValueFactory.createLiteral(DataManager.sportsCommentsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_wdProduct, predicate, object, context);
				}
				//set sport caption
				if(!DataManager.sportsCaptionsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "caption");
					object = sesameValueFactory.createLiteral(DataManager.sportsCaptionsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_wdProduct, predicate, object, context);
				}
				//set sport equipment
				if(!DataManager.sportsEquipmentsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "equipment"); 
					String se = DataManager.sportsEquipmentsList.get(temp).replace("<", "").replace(">", "");
					if(se.startsWith("http")){
						object = sesameValueFactory.createURI(se.replace(" ", ""));
					}
					else{
						object = sesameValueFactory.createLiteral(se);
					}
					model.add(subject_wdProduct, predicate, object, context);
				}
				//set sport oympic
				if(!DataManager.sportsOlympicsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "olympic");
					object = sesameValueFactory.createLiteral(DataManager.sportsOlympicsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_wdProduct, predicate, object, context);
				}
				//set sport oympic
				if(!DataManager.sportsTeamsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "team");
					object = sesameValueFactory.createLiteral(DataManager.sportsTeamsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_wdProduct, predicate, object, context);
				}

//				object = sesameValueFactory.createURI(bbcNamespace + "Sport");
//				model.add(subjectPrimaryContent, predicate, object, context);
				break;
			case NEWS :
				object = sesameValueFactory.createURI(bbcNamespace + "News");
				model.add(subjectPrimaryContent, predicate, object, context);
				break;
			case MUSIC :
				object = sesameValueFactory.createURI(bbcNamespace + "Music");
				model.add(subjectPrimaryContent, predicate, object, context);
				break;
				
			}
			//Thing primaryTopic
			String productUri = ru.randomURI(core,"Thing", false, true); // is this OK?

			URI subjectProduct = sesameValueFactory.createURI(productUri);

			URI subject_cwThing = subjectProduct;
			
//			//this is not taken from the allocation on the definition file any more
			if(!about_array.isEmpty()){
				if(DataManager.personsIdsList.contains(about_array.get(0))){
					this.cwThing = CWThing.PERSON;
					//System.out.println(" index 0  PERSON" + about_array.get(0)+" cwEntity.getCategory() "+cwEntity.getCategory() );
				}
				if(DataManager.placesIdsList.contains(about_array.get(0))){
					this.cwThing = CWThing.PLACE;
					//System.out.println(" index 0  PLACE" + about_array.get(0) + " cwEntity.getCategory() "+cwEntity.getCategory());
				}
				if(DataManager.eventsIdsList.contains(about_array.get(0))){
					this.cwThing = CWThing.EVENT;
					//System.out.println(" index 0  EVENT" +  about_array.get(0)+ " cwEntity.getCategory() "+cwEntity.getCategory());
				}
				if(DataManager.organisationsIdsList.contains(about_array.get(0))){
					this.cwThing = CWThing.ORGANISATION;
					//System.out.println(" index 0  ORGANISATION" +  about_array.get(0)+" cwEntity.getCategory() "+cwEntity.getCategory());
				}
			}
			//System.out.println("about_array size: " +about_array.size() );
			

			Random random = new Random();
			switch (cwThing) { 
			
			case PERSON :
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				if(!about_array.isEmpty() && DataManager.personsIdsList.contains(about_array.get(0))){
					temp = DataManager.personsIdsList.indexOf(about_array.get(0));
					//System.out.println("einai sto case person me uri " + DataManager.personsIdsList.get(temp));
				}
				else{
					temp = ru.nextInt(DataManager.personsIdsList.size());
				}
				if (!personUsedLocal) {
					personUsedLocal = true;
					initialId = DataManager.personsIdsList.get(temp);
					//System.out.println("einai sto case person me uri " + initialId);
					
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(foaf+"Person");
				model.add(subject_cwThing, predicate, object, context);
				//set person name
				if(!DataManager.personsNamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(foaf + "name");
					object = sesameValueFactory.createLiteral(DataManager.personsNamesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person surname
				if(!DataManager.personsSurnamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(foaf + "surname");
					object = sesameValueFactory.createLiteral(DataManager.personsSurnamesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person given name
				if(!DataManager.personsGivennamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(foaf + "givenName"); 
					object = sesameValueFactory.createLiteral(DataManager.personsGivennamesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person description
				if(!DataManager.personsDescriptionsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dc + "description"); 
					object = sesameValueFactory.createLiteral(DataManager.personsDescriptionsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person birthDate
				if(!DataManager.personsBirthdatesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpediaowl + "birthDate"); 
					object = sesameValueFactory.createLiteral(DataManager.personsBirthdatesList.get(temp).replace("<", "").replace(">", ""),XMLSchema.DATE);
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person birthPlace
				if(!DataManager.personsBirthplacesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpediaowl + "birthPlace"); 
					String bp = DataManager.personsBirthplacesList.get(temp).replace("<", "").replace(">", "");
					if(bp.startsWith("http")){
						object = sesameValueFactory.createURI(bp);
					}
					else{
						object = sesameValueFactory.createLiteral(bp);
					}
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person deathDate
				if(!DataManager.personsDeathdatesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpediaowl + "deathDate"); 
					object = sesameValueFactory.createLiteral(DataManager.personsDeathdatesList.get(temp).replace("<", "").replace(">", ""),XMLSchema.DATE);
					model.add(subject_cwThing, predicate, object, context);
				}
				//set person birthPlace
				if(!DataManager.personsDeathplacesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpediaowl + "deathPlace"); 
					
					String dp = DataManager.personsBirthplacesList.get(temp).replace("<", "").replace(">", "");
					if(dp.startsWith("http")){
						object = sesameValueFactory.createURI(dp);
					}
					else{
						object = sesameValueFactory.createLiteral(dp);
					}
					model.add(subject_cwThing, predicate, object, context);
				}
				
				break;

				
				
			case PLACE :
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				if(!about_array.isEmpty() && DataManager.placesIdsList.contains(about_array.get(0))){
					temp = DataManager.placesIdsList.indexOf(about_array.get(0));
					//System.out.println("einai sto case place me uri " + DataManager.placesIdsList.get(temp));
				}
				else{
					temp = ru.nextInt(DataManager.placesIdsList.size());
				}
				if (!placeUsedLocal) {
					placeUsedLocal = true;
					initialId = DataManager.placesIdsList.get(temp);
					//System.out.println("einai sto case place me uri " + initialId);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(dbpediaowl+"Place");
				model.add(subject_cwThing, predicate, object, context);
				//set place name
				if(!DataManager.placesNamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(foaf + "name");
					object = sesameValueFactory.createLiteral(DataManager.placesNamesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//TODO exw svisei to label?!
				//set place comment
				if(!DataManager.placesCommentsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "comment");
					object = sesameValueFactory.createLiteral(DataManager.placesCommentsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set place country
				if(!DataManager.placesCountriesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpediaowl + "country");
					object = sesameValueFactory.createURI(DataManager.placesCountriesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set place geometry
				if(!DataManager.placesGeosList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(geo + "geometry");
					object = sesameValueFactory.createLiteral(DataManager.placesGeosList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				break;
				
			case EVENT : 
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				if(!about_array.isEmpty() && DataManager.eventsIdsList.contains(about_array.get(0))){
					temp = DataManager.eventsIdsList.indexOf(about_array.get(0));
					//System.out.println("einai sto case event me uri " + DataManager.eventsIdsList.get(temp));
				}
				else{
					temp = ru.nextInt(DataManager.eventsIdsList.size());
				}
				
				if (!eventUsedLocal) {
					eventUsedLocal = true;
					initialId = DataManager.eventsIdsList.get(temp);
					//System.out.println("einai sto case event me uri " + initialId);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(dbpediaowl+"Event");
				model.add(subject_cwThing, predicate, object, context);
				//set event label
				if(!DataManager.eventsLabelsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "label");
					object = sesameValueFactory.createLiteral(DataManager.eventsLabelsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set event comment
				if(!DataManager.eventsCommentsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "comment");
					object = sesameValueFactory.createLiteral(DataManager.eventsCommentsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set event country
				if(!DataManager.eventsCountriesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "country");
					object = sesameValueFactory.createLiteral(DataManager.eventsCountriesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set event subject
				if(!DataManager.eventsSubjectsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dcterms + "subject");
					object = sesameValueFactory.createURI(DataManager.eventsSubjectsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				
				break;
				
			case ORGANISATION :	
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				if(!about_array.isEmpty() && DataManager.organisationsIdsList.contains(about_array.get(0))){
					temp = DataManager.organisationsIdsList.indexOf(about_array.get(0));
				}
				else{
					temp = ru.nextInt(DataManager.organisationsIdsList.size());
				}
				if (!organisationUsedLocal) {
					organisationUsedLocal = true;
					initialId = DataManager.organisationsIdsList.get(temp);
					//System.out.println("einai sto case organisation me uri " + initialId);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(dbpediaowl+"Organisation");
				model.add(subject_cwThing, predicate, object, context);
				//set organisation label
				if(!DataManager.organisationsLabelsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "label");
					object = sesameValueFactory.createLiteral(DataManager.organisationsLabelsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set organisation comment
				if(!DataManager.organisationsCommentsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(rdfs + "comment");
					object = sesameValueFactory.createLiteral(DataManager.organisationsCommentsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				//set organisation manager
				if(!DataManager.organisationsManagersList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "manager"); 
					
					String om = DataManager.organisationsManagersList.get(temp).replace("<", "").replace(">", "").replace(">", "");
					if(om.startsWith("http")){
						object = sesameValueFactory.createURI(om);
					}
					else{
						object = sesameValueFactory.createLiteral(om);
					}
					model.add(subject_cwThing, predicate, object, context);
				}
				//set organisation name
				if(!DataManager.organisationsNamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "name"); 
					
					String on = DataManager.organisationsNamesList.get(temp).replace("<", "").replace(">", "");
					if(on.startsWith("http")){
						object = sesameValueFactory.createURI(on);
					}
					else{
						object = sesameValueFactory.createLiteral(on);
					}					
					model.add(subject_cwThing, predicate, object, context);
				}
				//set organisation nickname
				if(!DataManager.organisationsNicknamesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "nickname"); 
					
					String nn = DataManager.organisationsNicknamesList.get(temp).replace("<", "").replace(">", "");
					if(nn.startsWith("http")){
						object = sesameValueFactory.createURI(nn);
					}
					else{
						object = sesameValueFactory.createLiteral(nn);
					}					
					model.add(subject_cwThing, predicate, object, context);
				}
				//set organisation website
				if(!DataManager.organisationsWebsitesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(dbpprop + "website");
					object = sesameValueFactory.createURI(DataManager.organisationsWebsitesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				break;
				
			case THEME :
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				model.add(subjectPrimaryContent, predicate, sesameValueFactory.createURI(subject_cwThing.toString()), context);
				
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(core + "Theme");
				model.add(subject_cwThing, predicate, object, context);
				//Set thing shortLabel
				
				predicate = sesameValueFactory.createURI(core + "shortLabel"); 
				object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
				model.add(subject_cwThing, predicate, object, context);
				
				//Set thing slug
				predicate = sesameValueFactory.createURI(core + "slug"); 
				object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
				model.add(subject_cwThing, predicate, object, context);
				
				//Set thing prefferedLabel
				predicate = sesameValueFactory.createURI(core + "prefferedLabel"); 
				object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
				model.add(subject_cwThing, predicate, object, context);
				
				//Set thing disambiguationHint
				predicate = sesameValueFactory.createURI(core + "disambiguationHint"); 
				object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
				model.add(subject_cwThing, predicate, object, context);
				

				switch (primaryTopicSubProp_) { //ta subproperties tou promarytopic
				case TWITTER :
					predicate = sesameValueFactory.createURI(core + "twitter");
					break;
				case FACEBOOK :
					predicate = sesameValueFactory.createURI(core + "facebook");
					break;
				case OFFICIALHOMEPAGE :
					predicate = sesameValueFactory.createURI(core + "oficcialHomepage");
					break;
				}
				object = sesameValueFactory.createLiteral(ru.sentenceFromDictionaryWords(this.cwEntity.getLabel(), 10, false, false));
				model.add(subject_cwThing, predicate, object, context);
				
				break;

			case PERSON_ORGANISATION :
				String [] n = {"PERSON", "ORGANISATION"};
				//System.out.println();
				if(n[random.nextInt(n.length)].equals("PERSON")){
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					if(!about_array.isEmpty() && DataManager.personsIdsList.contains(about_array.get(0))){
						temp = DataManager.personsIdsList.indexOf(about_array.get(0));
						//System.out.println("einai sto case person me uri " + DataManager.personsIdsList.get(temp));
					}
					else{
						temp = ru.nextInt(DataManager.personsIdsList.size());
					}
					if (!personUsedLocal) {
						personUsedLocal = true;
						initialId = DataManager.personsIdsList.get(temp);
						//System.out.println("einai sto case person me uri " + initialId);
						
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(ldbc + "Person_Organisation");
					model.add(subject_cwThing, predicate, object, context);

				}
				else if(n[random.nextInt(n.length)].equals("ORGANISATION")){
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					if(!about_array.isEmpty() && DataManager.organisationsIdsList.contains(about_array.get(0))){
						temp = DataManager.organisationsIdsList.indexOf(about_array.get(0));
					}
					else{
						temp = ru.nextInt(DataManager.organisationsIdsList.size());
					}
					if (!organisationUsedLocal) {
						organisationUsedLocal = true;
						initialId = DataManager.organisationsIdsList.get(temp);
						//System.out.println("einai sto case organisation me uri " + initialId);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(ldbc + "Person_Organisation");
					model.add(subject_cwThing, predicate, object, context);

				}


				break;
				
			case EVENT_PLACE_THEME :
				String [] m = {"EVENT", "PLACE", "THEME"};
				if(m[random.nextInt(m.length)].equals("EVENT")){
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					if(!about_array.isEmpty() && DataManager.eventsIdsList.contains(about_array.get(0))){
						temp = DataManager.eventsIdsList.indexOf(about_array.get(0));
					//	System.out.println("einai sto case event me uri " + DataManager.eventsIdsList.get(temp));
					}
					else{
						temp = ru.nextInt(DataManager.eventsIdsList.size());
					}
					
					if (!eventUsedLocal) {
						eventUsedLocal = true;
						initialId = DataManager.eventsIdsList.get(temp);
					//	System.out.println("einai sto case event me uri " + initialId);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(ldbc + "Event_Place_Theme");
					model.add(subject_cwThing, predicate, object, context);

				}
				else if(m[random.nextInt(m.length)].equals("PLACE")){
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					if(!about_array.isEmpty() && DataManager.placesIdsList.contains(about_array.get(0))){
						temp = DataManager.placesIdsList.indexOf(about_array.get(0));
					//	System.out.println("einai sto case place me uri " + DataManager.placesIdsList.get(temp));
					}
					else{
						temp = ru.nextInt(DataManager.placesIdsList.size());
					}
					if (!placeUsedLocal) {
						placeUsedLocal = true;
						initialId = DataManager.placesIdsList.get(temp);
					//	System.out.println("einai sto case place me uri " + initialId);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(ldbc + "Event_Place_Theme");
					model.add(subject_cwThing, predicate, object, context);

				}
				else if(m[random.nextInt(m.length)].equals("THEME")){
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					model.add(subjectPrimaryContent, predicate, sesameValueFactory.createURI(subject_cwThing.toString()), context);
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(ldbc + "Event_Place_Theme");
					model.add(subject_cwThing, predicate, object, context);

				}

				break;
			case TRAVEL :
				//na dw an xreiazetai na kanw random kai otan exw binary search
				switch (cwTravel) { 
				
				case ADMINISTRATIVEDIVISION : //372 indexed items
					
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"AdministrativeDivision"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"AdministrativeDivision");
					model.add(subject_cwThing, predicate, object, context);
					
					break;
					
				case CITY : //57
					
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"City"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"City");
					model.add(subject_cwThing, predicate, object, context);
					
					break;
	
				case COASTLINE :  //23
					
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"Coastline"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"Coastline");
					model.add(subject_cwThing, predicate, object, context);
					
					break;
					
				case CONTINENT : //6
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"Continent"+">");	
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"Continent");
					model.add(subject_cwThing, predicate, object, context);
					
					break;
					
				case COUNTRY : //55
					
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"Country"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"Country");
					model.add(subject_cwThing, predicate, object, context);
										
					break;
	
				case GEOGRAPHICALFEATURE : //905
					
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"GeographicalFeature"+">");

					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"GeographicalFeature");
					model.add(subject_cwThing, predicate, object, context);

					
					break;
					
				case ISLAND : //591
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"Island"+">");
					
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"Island");
					model.add(subject_cwThing, predicate, object, context);

					break;
					
				case OCEAN : //5
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"Ocean"+">");	
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"Ocean");
					model.add(subject_cwThing, predicate, object, context);
					
					break;						
				
				case RIVER : //49
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"River"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"River");
					model.add(subject_cwThing, predicate, object, context);

					
					break;
					
			case TIERONEADMINISTRATIVEDIVISION : //21
					predicate = sesameValueFactory.createURI(core + "primaryTopic");
					temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"TierOneAdministrativeDivision"+">");
					if (!travelUsedLocal) {
						travelUsedLocal = true;
						
						initialId = DataManager.travelsIdsList.get(temp);
					}
					
					object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					model.add(subjectPrimaryContent, predicate, object, context);
					
					subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
					
					
					predicate = sesameValueFactory.createURI(rdfTypeNamespace);
					object = sesameValueFactory.createURI(travel+"TierOneAdministrativeDivision");
					model.add(subject_cwThing, predicate, object, context);

					break;
			case BODYOFLAND : //598
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+travel+"bodyOfLand"+">");	
				
				if (!travelUsedLocal) {
					travelUsedLocal = true;
					
					initialId = DataManager.travelsIdsList.get(temp);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				
				
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(travel+"bodyOfLand");
				model.add(subject_cwThing, predicate, object, context);

				break;
			
					
			case PERSONAGENT : //4
				predicate = sesameValueFactory.createURI(core + "primaryTopic");
				temp = Collections.binarySearch(DataManager.travelsTypesList, "<"+upper+"Person_agent"+">");
				if (!travelUsedLocal) {
					travelUsedLocal = true;
					
					initialId = DataManager.travelsIdsList.get(temp);
				}
				
				object = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				model.add(subjectPrimaryContent, predicate, object, context);
				
				subject_cwThing = sesameValueFactory.createURI(initialId.replace("<", "").replace(">", ""));
				
				
				predicate = sesameValueFactory.createURI(rdfTypeNamespace);
				object = sesameValueFactory.createURI(upper+"Person_agent");
				model.add(subject_cwThing, predicate, object, context);

				break;	


			}
				
				//TODO kapoia tha einai bnodes.. check prin tin metartopi
				
//				if(!DataManager.travelsHasHeadOfStatesList.isEmpty() && !DataManager.travelsHasHeadOfStatesList.get(temp).replace("<", "").replace(">", "").equals("")){
//					predicate = sesameValueFactory.createURI(travel + "hasHeadOfState");
//					object = sesameValueFactory.createBNode(DataManager.travelsHasHeadOfStatesList.get(temp).replace("<", "").replace(">", ""));
//					//System.out.println("has head of state");
//					model.add(subject_cwThing, predicate, object, context);
//				}
//				if(!DataManager.travelsOccupiesList.isEmpty() && !DataManager.travelsOccupiesList.get(temp).replace("<", "").replace(">", "").equals("")){
//					predicate = sesameValueFactory.createURI(upper + "occupies");
//					object = sesameValueFactory.createBNode(DataManager.travelsOccupiesList.get(temp).replace("<", "").replace(">", ""));
//					model.add(subject_cwThing, predicate, object, context);
//				}
//				if(!DataManager.travelsHasBoundaryList.isEmpty() && !DataManager.travelsHasBoundaryList.get(temp).replace("<", "").replace(">", "").equals("")){
//					predicate = sesameValueFactory.createURI(travel + "hasBoundary");
//					object = sesameValueFactory.createLiteral(DataManager.travelsHasBoundaryList.get(temp).replace("<", "").replace(">", ""));
//					model.add(subject_cwThing, predicate, object, context);
//				}
				if(!DataManager.travelsHasRecognitionStatusList.isEmpty() && !DataManager.travelsHasRecognitionStatusList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "hasRecognitionStatus");
					object = sesameValueFactory.createLiteral(DataManager.travelsHasRecognitionStatusList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsSurroundedBysList.isEmpty() && !DataManager.travelsSurroundedBysList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "surroundedBy");
					object = sesameValueFactory.createURI(DataManager.travelsSurroundedBysList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsIsOccupiedBysList.isEmpty() && !DataManager.travelsIsOccupiedBysList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "is_occupied_by");
					object = sesameValueFactory.createURI(DataManager.travelsIsOccupiedBysList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}

				if(!DataManager.travelsHasPhysicalPropertiesList.isEmpty() && !DataManager.travelsHasPhysicalPropertiesList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "hasPhysicalProperty");
					object = sesameValueFactory.createLiteral(DataManager.travelsHasPhysicalPropertiesList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsHasCapitalSList.isEmpty() && !DataManager.travelsHasCapitalSList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "hasCapital");
					object = sesameValueFactory.createURI(DataManager.travelsHasCapitalSList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsDirectPartOfsList.isEmpty() && !DataManager.travelsDirectPartOfsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "directPartOf");
					object = sesameValueFactory.createURI(DataManager.travelsDirectPartOfsList.get(temp).replace("<", "").replace(">", ""));
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsHasAreasList.isEmpty() && !DataManager.travelsHasAreasList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "hasArea");
					object = sesameValueFactory.createLiteral(DataManager.travelsHasAreasList.get(temp).replace("<", "").replace(">", ""),XMLSchema.DOUBLE);
					model.add(subject_cwThing, predicate, object, context);
				}
				if(!DataManager.travelsHasPopulationsList.isEmpty() && !DataManager.travelsHasPopulationsList.get(temp).replace("<", "").replace(">", "").equals("")){
					predicate = sesameValueFactory.createURI(travel + "hasPopulation");
					object = sesameValueFactory.createLiteral(DataManager.travelsHasPopulationsList.get(temp).replace("<", "").replace(">", ""),XMLSchema.DOUBLE); //make it XMLSchema.DOUBLE
					model.add(subject_cwThing, predicate, object, context);
				}
			
			
				
			}
			

		return model;
	}
}
