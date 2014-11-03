package eu.ldbc.semanticpublishing.refdataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.refdataset.model.Entity;
import eu.ldbc.semanticpublishing.util.StringUtil;

/**
 * A class for storing important to the benchmark data e.g.
 *   - next available ID for a CreativeWork (greatest number)
 *   - list of tagged popular and regular entities
 */
public class DataManager {
		
	//a list for popular entities 
	public static final ArrayList<Entity> popularEntitiesList = new ArrayList<Entity>();
	
	//a list for regular entities
	public static final ArrayList<Entity> regularEntitiesList = new ArrayList<Entity>();
	
	//a list for all entities that have been tagged in a Creative Work
	public static final ArrayList<String> taggedEntityUrisList = new ArrayList<String>();
	
	//a list of all geonames ids taken from reference dataset
	public static final ArrayList<String> geonamesIdsList = new ArrayList<String>();	
//a list of all events ids taken from reference dataset
	public static final ArrayList<String> eventsIdsList = new ArrayList<String>();
	
	//a list of all events labels taken from reference dataset
	public static final ArrayList<String> eventsLabelsList = new ArrayList<String>();
	
	//a list of all events comments taken from reference dataset
	public static final ArrayList<String> eventsCommentsList = new ArrayList<String>();
	
	//a list of all events countries taken from reference dataset
	public static final ArrayList<String> eventsCountriesList = new ArrayList<String>();
	
	//a list of all events labels taken from reference dataset
	public static final ArrayList<String> eventsSubjectsList = new ArrayList<String>();
	
	//a list of all sports ids taken from reference dataset
	public static final ArrayList<String> sportsIdsList = new ArrayList<String>();
	
	//a list of all sports comments taken from reference dataset
	public static final ArrayList<String> sportsCommentsList = new ArrayList<String>();
	
	//a list of all sports captions taken from reference dataset
	public static final ArrayList<String> sportsCaptionsList = new ArrayList<String>();
	
	//a list of all sports equipments taken from reference dataset
	public static final ArrayList<String> sportsEquipmentsList = new ArrayList<String>();
	
	//a list of all sports olympics taken from reference dataset
	public static final ArrayList<String> sportsOlympicsList = new ArrayList<String>();
	
	//a list of all sports teams taken from reference dataset
	public static final ArrayList<String> sportsTeamsList = new ArrayList<String>();
	
	//a list of all places ids taken from reference dataset
	public static final ArrayList<String> placesIdsList = new ArrayList<String>();
	
	//a list of all places names taken from reference dataset
	public static final ArrayList<String> placesNamesList = new ArrayList<String>();
	
	//a list of all places comments taken from reference dataset
	public static final ArrayList<String> placesCommentsList = new ArrayList<String>();
	
	//a list of all places countries taken from reference dataset
	public static final ArrayList<String> placesCountriesList = new ArrayList<String>();
	
	//a list of all places geos taken from reference dataset
	public static final ArrayList<String> placesGeosList = new ArrayList<String>();
	
	//a list of all organisations ids taken from reference dataset
	public static final ArrayList<String> organisationsIdsList = new ArrayList<String>();
	
	//a list of all organisations labels taken from reference dataset
	public static final ArrayList<String> organisationsLabelsList = new ArrayList<String>();
	
	//a list of all organisations comments taken from reference dataset
	public static final ArrayList<String> organisationsCommentsList = new ArrayList<String>();
	
	//a list of all organisations managers taken from reference dataset
	public static final ArrayList<String> organisationsManagersList = new ArrayList<String>();
	
	//a list of all organisations names taken from reference dataset
	public static final ArrayList<String> organisationsNamesList = new ArrayList<String>();
	
	//a list of all organisations nicknames taken from reference dataset
	public static final ArrayList<String> organisationsNicknamesList = new ArrayList<String>();
	
	//a list of all organisations websites taken from reference dataset
	public static final ArrayList<String> organisationsWebsitesList = new ArrayList<String>();
	

	//a list of all musicalartists ids taken from reference dataset
	public static final ArrayList<String> musicalartistsIdsList = new ArrayList<String>();
	
	//a list of all musicalartists comments taken from reference dataset
	public static final ArrayList<String> musicalartistsCommentsList = new ArrayList<String>();
	
	//a list of all musicalartists names taken from reference dataset
	public static final ArrayList<String> musicalartistsNamesList = new ArrayList<String>();
	
	//a list of all musicalartists labels taken from reference dataset
	public static final ArrayList<String> musicalartistsLabelsList = new ArrayList<String>();
	
	//a list of all musicalartists birthplace taken from reference dataset
	public static final ArrayList<String> musicalartistsBirthplacesList = new ArrayList<String>();
	
	//a list of all musicalartists genre taken from reference dataset
	public static final ArrayList<String> musicalartistsGenresList = new ArrayList<String>();
	
	//a list of all musicalartists hometown taken from reference dataset
	public static final ArrayList<String> musicalartistsHometownsList = new ArrayList<String>();
	
	//a list of all musicalartists birthdate taken from reference dataset
	public static final ArrayList<String> musicalartistsBirthdatesList = new ArrayList<String>();
	

	//a list of all persons ids taken from reference dataset
	public static final ArrayList<String> personsIdsList = new ArrayList<String>();
	
	//a list of all persons names taken from reference dataset
	public static final ArrayList<String> personsNamesList = new ArrayList<String>();
	
	//a list of all persons surnames taken from reference dataset
	public static final ArrayList<String> personsSurnamesList = new ArrayList<String>();
	
	//a list of all persons givennames taken from reference dataset
	public static final ArrayList<String> personsGivennamesList = new ArrayList<String>();
	
	//a list of all persons description taken from reference dataset
	public static final ArrayList<String> personsDescriptionsList = new ArrayList<String>();
	
	//a list of all persons birthdate taken from reference dataset
	public static final ArrayList<String> personsBirthdatesList = new ArrayList<String>();
	
	//a list of all persons birthplace taken from reference dataset
	public static final ArrayList<String> personsBirthplacesList = new ArrayList<String>();
	
	//a list of all persons deathdate taken from reference dataset
	public static final ArrayList<String> personsDeathdatesList = new ArrayList<String>();
	
	//a list of all persons deathplace taken from reference dataset
	public static final ArrayList<String> personsDeathplacesList = new ArrayList<String>();
	
	//a list of all travels ids taken from reference dataset
	public static final ArrayList<String> travelsIdsList = new ArrayList<String>();
	
	//a list of all travels types taken from reference dataset
	public static final ArrayList<String> travelsTypesList = new ArrayList<String>();
	
	//a list of all travels surrounded bys taken from reference dataset
	public static final ArrayList<String> travelsSurroundedBysList = new ArrayList<String>();
	
	//a list of all travels occupied bys taken from reference dataset
	public static final ArrayList<String> travelsIsOccupiedBysList = new ArrayList<String>();
	
	//a list of all travels hasheadofstates bys taken from reference dataset
	public static final ArrayList<String> travelsHasHeadOfStatesList = new ArrayList<String>();
	

	//a list of all travels Occupies taken from reference dataset
	public static final ArrayList<String> travelsOccupiesList = new ArrayList<String>();
	
	//a list of all travels HasBoundary  taken from reference dataset
	public static final ArrayList<String> travelsHasBoundaryList = new ArrayList<String>();
	
	//a list of all travels HasRecognitionStatuses taken from reference dataset
	public static final ArrayList<String> travelsHasRecognitionStatusList = new ArrayList<String>();
	
	//a list of all travels has physical properties bys taken from reference dataset
	public static final ArrayList<String> travelsHasPhysicalPropertiesList = new ArrayList<String>();
	
	//a list of all travels has capitals bys taken from reference dataset
	public static final ArrayList<String> travelsHasCapitalSList = new ArrayList<String>();
	
	//a list of all travels has direct parts taken from reference dataset
	public static final ArrayList<String> travelsDirectPartOfsList = new ArrayList<String>();
	
	//a list of all travels has areas parts taken from reference dataset
	public static final ArrayList<String> travelsHasAreasList = new ArrayList<String>();
	
	//a list of all travels has population parts taken from reference dataset
	public static final ArrayList<String> travelsHasPopulationsList = new ArrayList<String>();
	
	//a list of all travels has statistics parts taken from reference dataset
	//public static final ArrayList<String> travelsHasStatisticsList = new ArrayList<String>();
	
	//a list of entity URIs generated for Exponential Decay modeling of data (Major entities)
	public static final ArrayList<String> exponentialDecayEntitiesMajorList = new ArrayList<String>();
	
	//a list of entity URIs generated for Exponential Decay modeling of data (Minor entities)
	public static final ArrayList<String> exponentialDecayEntitiesMinorList = new ArrayList<String>();
	
	//a list of entity URIs generated for Correlations modeling of data
	public static final ArrayList<String> correlatedEntitiesList = new ArrayList<String>();
	
	//stores the ID of a Creative Work which is has the greatest value, used for further CRUD operations
	public static AtomicLong creativeWorksNextId = new AtomicLong(0);
	
	//Dataset info constants for persisting
	private static final String CREATIVE_WORK_NEXT_ID_TEXT = "[CreativeWorkNextId]";
	private static final String EXP_DECAY_ENTITIES_MAJOR_TEXT = "[ExponentialDecayEntitiesMajor]";
	private static final String EXP_DECAY_ENTITIES_MINOR_TEXT = "[ExponentialDecayEntitiesMinor]";
	private static final String CORRELATED_ENTITIES_TEXT = "[CorrelatedEntities]";
	
	private enum actionsEnum {NONE, CREATIVE_WORK_NEXT_ID, EXP_DECAY_MAJOR_ENTITIES, EXP_DECAY_MINOR_ENTITIES, CORRELATED_ENTITIES};
	
	/**
	 * The method will serialize entity URIs into a file which will help with identifying the important entities during the benchmark phase.
	 * Location of the file will be in creativeWorksPath and will be part of the generated data
	 * FileName will be stored in the property file :   
	 * @param correlatedEntitiesList
	 * @param exponentialDecayingMajorEntitiesList
	 * @param exponentialDecayingMinorEntitiesList
	 */
	public static void persistDatasetInfo(String destinationFullPath, List<Entity> correlatedEntitiesList, List<Entity> exponentialDecayingMajorEntitiesList, List<Entity> exponentialDecayingMinorEntitiesList) throws IOException {
		Writer writer = null;
		
		//do not serialize if no file name is specified
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFullPath)));
			
			//serialize next ID of creative Work
			writer.write(String.format("%s\n", CREATIVE_WORK_NEXT_ID_TEXT));
			writer.write(String.format("%d\n", DataManager.creativeWorksNextId.get()));
			writer.write("\n");
			
			if (exponentialDecayingMajorEntitiesList != null) {
				writer.write(String.format("%s\n", EXP_DECAY_ENTITIES_MAJOR_TEXT));
				
				for (int i = 0; i < exponentialDecayingMajorEntitiesList.size(); i++) {
					writer.write(String.format("%s\n", exponentialDecayingMajorEntitiesList.get(i).getURI()));
				}					
				writer.write("\n");
			}
			
			if (exponentialDecayingMinorEntitiesList != null) {
				writer.write(String.format("%s\n", EXP_DECAY_ENTITIES_MINOR_TEXT));
				
				for (int i = 0; i < exponentialDecayingMinorEntitiesList.size(); i++) {
					writer.write(String.format("%s\n", exponentialDecayingMinorEntitiesList.get(i).getURI()));
				}										
				writer.write("\n");					
			}
			
			if (correlatedEntitiesList != null) {
				writer.write(String.format("%s\n", CORRELATED_ENTITIES_TEXT));
				
				//write entities that participate in correlation first (entityA and entityB)
				for (int i = 0; i < correlatedEntitiesList.size(); i++) {
					writer.write(String.format("%s\n", correlatedEntitiesList.get(i).getURI()));
				}											
				writer.write("\n");							
			}
			
		} finally {
			try {
				writer.close(); 
			} catch (Exception e) {
			}
		}
	}
	
	public static void initDatasetInfo(String filePath, boolean suppressWarnings) {
		BufferedReader br = null;
		boolean canRead = false;
		actionsEnum action = actionsEnum.NONE;

		try {
			br = new BufferedReader(new FileReader(filePath));
			
			String line = br.readLine();
			while (line != null) {
				if (line.trim().isEmpty() || line.startsWith("#")) {
					canRead = false;
				}
				
				if (canRead) {
					switch (action) {
					case CREATIVE_WORK_NEXT_ID :
						//skip for now
//						DataManager.creativeWorksNexId.set(Long.parseLong(line));
						break;
					case EXP_DECAY_MAJOR_ENTITIES:
						DataManager.exponentialDecayEntitiesMajorList.add(line);
						break;
					case EXP_DECAY_MINOR_ENTITIES:
						DataManager.exponentialDecayEntitiesMinorList.add(line);
						break;
					case CORRELATED_ENTITIES:
						DataManager.correlatedEntitiesList.add(line);
						break;
					default:
						break;
					}
				}
				
				if (line.contains("[CreativeWorkNextId]")) {
					action = actionsEnum.CREATIVE_WORK_NEXT_ID;
					canRead = true;
				}

				if (line.contains("[ExponentialDecayEntitiesMajor]")) {
					action = actionsEnum.EXP_DECAY_MAJOR_ENTITIES;
					canRead = true;
				}
				
				if (line.contains("[ExponentialDecayEntitiesMinor]")) {
					action = actionsEnum.EXP_DECAY_MINOR_ENTITIES;
					canRead = true;
				}
				
				if (line.contains("[CorrelatedEntities]")) {
					action = actionsEnum.CORRELATED_ENTITIES;
					canRead = true;
				}			
				
				line = br.readLine();
			}
		} catch (IOException e) {
			//sink the exception if file doesn't exist
			if (!suppressWarnings) {
				System.out.println("\nWarning : Details about generated dataset were not found at location : " + filePath + " - generate data to fix that, continuing with default settings.");
			}
		} finally {
			try { br.close(); } catch(Exception e) {}
		}
	}
	
	public static String buildDataInfoFilePath(Configuration configuration) {
		if (!configuration.getString(Configuration.CREATIVE_WORKS_INFO).isEmpty()) {
			return String.format("%s%s%s", StringUtil.normalizePath(configuration.getString(Configuration.CREATIVE_WORKS_PATH)), File.separator, configuration.getString(Configuration.CREATIVE_WORKS_INFO));  
		}		
		return "";
	}
}
