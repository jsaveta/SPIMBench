package eu.ldbc.semanticpublishing.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;

/**
 * A utility class, for producing random values, strings, sentences, uris, etc. 
 */
public class RandomUtil {
	private static final String baseURI = "http://www.bbc.co.uk/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String ldbc = "http://www.ldbc.eu/";
	private static final char[] symbols = new char[62];
	
	private Random randomGenerator;
	private List<String> wordsList = new ArrayList<String>();
	private String wordsFilePath;
	private long seed = 0;
	private int seedYear = 2000;
	private int dataGenerationPeriodYears = 1;

	static {
		// numbers 0..9
		for (int index = 0; index < 10; ++index) {
			symbols[index] = (char) ('0' + index);
		}
		// letters a..z
		for (int index = 10; index < 36; ++index) {
			symbols[index] = (char) ('a' + index - 10);
		}
		// letters A..Z
		for (int index = 36; index < 62; ++index) {
			symbols[index] = (char) ('A' + index - 10 - 26);
		}
	}
	
	public RandomUtil(String wordsFilePath, long seed, int seedYear, int dataGenerationPeriodYears) {
		this.randomGenerator = new Random(seed);
		this.wordsFilePath = wordsFilePath;
		this.seed = seed;
		this.seedYear = seedYear;
		this.dataGenerationPeriodYears = dataGenerationPeriodYears;
		buildWordsArray(this.wordsFilePath);	
	}
	
	//Using this constructor in the factory method. WordsList has already been initialized, no need to parse the words file again.
	private RandomUtil(long seed, int seedYear, int dataGenerationPeriodYears) {
		this.randomGenerator = new Random(seed);
		this.seed = seed;
		this.randomGenerator.setSeed(seed);
		this.seedYear = seedYear;
		this.dataGenerationPeriodYears = dataGenerationPeriodYears;
	}
	
	public RandomUtil randomUtilFactory(long newSeed) {
		RandomUtil newRandomUtil = new RandomUtil(newSeed, seedYear, dataGenerationPeriodYears);
		//re-using already initialized words list
		newRandomUtil.setWordsList(this.wordsList);
		return newRandomUtil;
	}

	public String getWordsFilePath() {
		return this.wordsFilePath;
	}
	
	public List<String> getWordsList() {
		return wordsList;
	}
	
	public void setWordsList(List<String> wordsList) {
		this.wordsList = wordsList;
	}
	
	public void setRandomSeed(long seed) {
		randomGenerator.setSeed(seed);
	}
	
	public int getSeedYear() {
		return this.seedYear;
	}
	
	public long getSeed() {
		return this.seed;
	}
	
	public void setSeedYear(int seedYear) {
		this.seedYear = seedYear;
	}
	
	public int getDataGenerationPeriodYears() {
		return this.dataGenerationPeriodYears;
	}
	
	public Random getRandom() {
		return randomGenerator;
	}
	
	public int nextInt(int min, int max) {
		if (min >= max) {
			System.out.println("Warning : RandomUtil : wrong parameter value of : min (" + min + ") >= value of max (" + max + ")! Inconsistent behaviour expected!");
		}
		return randomGenerator.nextInt(max - min) + min;
	}

	public int nextInt(int max) {
		if (max <= 0) {
			System.out.println("Warning : RandomUtil : wrong int parameter value (" + max + ")! Inconsistent behaviour expected!");			
		}
		return randomGenerator.nextInt(max);
	}

	public long nextLong(long max) {
		if (max <= 0) {
			System.out.println("Warning : RandomUtil : wrong long parameter value (" + max + ")! Inconsistent behaviour expected!");			
		}
		long value = randomGenerator.nextLong();
		if( value < 0 ) {
			value = -value;
		}
		return value % max;
	}

	public boolean nextBoolean() {
		return randomGenerator.nextBoolean();
	}

	public double nextDouble(double min, double max) {
		return min + randomGenerator.nextDouble() * (max - min);
	}
	
	/**
	 * Produces a xsd:dateTime literal for current time and date
	 */
	public String currentDateTimeString() {
		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);

		return formatDateTime(year, month, day, hour, minute, second);
	}

	/**
	 * Produces a random xsd:dateTime literal with a fixed year set in seedYear
	 *   e.g. "2011-10-21T20:55:58.379+03:00"^^xsd:dateTime
	 */
	public String randomDateTimeString() {
		int year = seedYear;
		if ((dataGenerationPeriodYears - 1) > 0) {
			year += nextInt(dataGenerationPeriodYears - 1);
		}

		int month = nextInt(0, 11);

		int day;
		if (month == 2) {
			day = nextInt(1, 28);
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = nextInt(1, 30);
		} else { // if (month == 1 || month == 3 || month == 5 || month == 7 ||
					// month == 8 || month == 10|| month == 12) {
			day = nextInt(1, 31);
		}

		int hour = nextInt(0, 23);
		int minute = nextInt(0, 59);
		int second = nextInt(0, 59);

		return formatDateTime(year, month + 1, day, hour, minute, second);
	}
	
	/**
	 * Produces a xsd:dateTime literal from given parameter Date
	 *   e.g. "2011-10-21T20:55:58.379+03:00"^^xsd:dateTime
	 */	
	public String dateTimeString(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		
		return formatDateTime(year, month, day, hour, minute, second);
	}
	
	/**
	 * Produces a random Date object with a fixed offset of YEARS_OFFSET years from now
	 */
	public Date randomDateTime() {
		Calendar calendar = Calendar.getInstance();
		
		int year = seedYear;
		if ((dataGenerationPeriodYears - 1) > 0) {
			year += nextInt(dataGenerationPeriodYears - 1);
		}

		int month = nextInt(0, 11);

		int day;
		if (month == 2) {
			day = nextInt(1, 28);
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = nextInt(1, 30);
		} else { // if (month == 1 || month == 3 || month == 5 || month == 7 ||
					// month == 8 || month == 10|| month == 12) {
			day = nextInt(1, 31);
		}

		int hour = nextInt(0, 23);
		int minute = nextInt(0, 59);
		int second = nextInt(0, 59);
		int millisecond = nextInt(0, 999);

		calendar.set(year, month, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
		
		return calendar.getTime();
	}
	
	/**
	 * Produces a random Date object starting with 1.1.seedYear and a random offset of maxDaysAfterSeedYear
	 */
	public Date randomDateTime(int maxDaysAfter) {
		Calendar calendar = Calendar.getInstance();
		
		int year = seedYear;
		if ((dataGenerationPeriodYears - 1) > 0) {
			year += nextInt(dataGenerationPeriodYears - 1);
		}
		
		calendar.set(year, 0, 1, nextInt(23), nextInt(59), nextInt(59));
		
		int offset = nextInt(maxDaysAfter);
		
		calendar.add(Calendar.DAY_OF_YEAR, offset);
		
		return calendar.getTime();
	}

	/**
	 * Produces a random Date object with max limit on the year on month random selection. If input values are less
	 * than zero, will use the random behavior from randomDateTime() method 
	 */
	public Date randomDateTime(int maxYear, int maxMonth) {
		Calendar calendar = Calendar.getInstance();
		
		int year = maxYear;
		
		if (maxYear < 0) {
			year = seedYear;
		}
		
		if  ((dataGenerationPeriodYears - 1) > 0) {
			year += (dataGenerationPeriodYears - 1);
		}
		
		int month = 0;
		
		if (maxMonth < 0) {
			month = nextInt(0, 11);
		} else {
			month = nextInt(0, maxMonth);
		}

		int day;
		if (month == 1) {
			day = nextInt(1, 28);
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = nextInt(1, 30);
		} else { // if (month == 1 || month == 3 || month == 5 || month == 7 ||
					// month == 8 || month == 10|| month == 12) {
			day = nextInt(1, 31);
		}

		int hour = nextInt(0, 23);
		int minute = nextInt(0, 59);
		int second = nextInt(0, 59);

		calendar.set(year, month, day, hour, minute, second);
		
		return calendar.getTime();
	}	
	
	/**
	 * Produce a data time with format "2013-06-05T16:43:30Z"^^xsd:dateTime
	 */
	private String formatDateTime(int year, int month, int day, int hour, int minute, int second) {
		return String.format("\"%1$04d-%2$02d-%3$02dT%4$02d:%5$02d:%6$02dZ\"^^<http://www.w3.org/2001/XMLSchema#dateTime>", year, month, day, hour,	minute, second);
	}

	public String createBoolean(boolean value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		if (value == true) {
			sb.append("true");
		} else {
			sb.append("false");
		}
		sb.append("\"");
		sb.append("^^<http://www.w3.org/2001/XMLSchema#boolean>");

		return sb.toString();
	}

	public String randomString(int minLength, int maxLength, boolean surroundWithQuotes, boolean appendDataType) {
		StringBuilder sb = new StringBuilder();
		int randomLen = minLength;

		randomLen = nextInt(minLength, maxLength);

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		for (int i = 0; i < randomLen; i++) {
			// add a random character
			sb.append(symbols[randomGenerator.nextInt(RandomUtil.symbols.length)]);
		}

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		if (appendDataType) {
			sb.append("^^<http://www.w3.org/2001/XMLSchema#string>");
		}

		return sb.toString();
	}

	public String randomWordFromDictionary(boolean surroundWithQuotes, boolean appendDataType) {
		StringBuilder sb = new StringBuilder();

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		sb.append(wordsList.get(randomGenerator.nextInt(wordsList.size())));

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		if (appendDataType) {
			sb.append("^^<http://www.w3.org/2001/XMLSchema#string>");
		}

		return sb.toString();
	}

	public String sentenceFromRandomStrings(int numberOfWords, boolean surroundWithQuotes, boolean appendDataType) {
		int minWordLength = 2;
		int maxWordLength = 15;
		StringBuilder sb = new StringBuilder();

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		for (int i = 0; i < numberOfWords; i++) {
			sb.append(randomString(minWordLength, randomGenerator.nextInt(maxWordLength), false, false));
			if (i != (numberOfWords - 1)) {
				sb.append(" ");
			} else {
				sb.append(".");
			}
		}

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		if (appendDataType) {
			sb.append("^^<http://www.w3.org/2001/XMLSchema#string>");
		}

		return sb.toString();
	}

	public String sentenceFromDictionaryWords(String firstString, int numberOfWords, boolean surroundWithQuotes,
			boolean appendDataType) {
		StringBuilder sb = new StringBuilder();

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		sb.append(firstString);
		sb.append(" ");

		for (int i = 0; i < numberOfWords; i++) {
			sb.append(randomWordFromDictionary(false, false));
			if (i != (numberOfWords - 1)) {
				sb.append(" ");
			} else {
				sb.append(".");
			}
		}

		if (surroundWithQuotes) {
			sb.append("\"");
		}

		if (appendDataType) {
			sb.append("^^<http://www.w3.org/2001/XMLSchema#string>");
		}

		return sb.toString();
	}

	/**
	 * Generates a random URI, using nextInt() method.
	 * 
	 * @param domain
	 *            - adds the domain name to the URI, e.g. domain=sports, then
	 *            uri is : http://www.bbc.co.uk/sports/
	 * @param appendBrackets - appends < > 
	 * @param appendSuffixId - appends the string "#id" at the end, useful for Creative Works URI format             
	 * @return the generated URI
	 */
	public String randomURI(String domain, boolean appendBrackets, boolean appendSuffixId) {
		String nextIntStr = Integer.toString(Math.abs(randomGenerator.nextInt()));
		StringBuilder sb = new StringBuilder();

		if (appendBrackets) {
			sb.append("<");
		}

		sb.append(RandomUtil.baseURI);
		sb.append(domain);
		sb.append("/");
		sb.append(nextIntStr);

		if (appendSuffixId) {
			sb.append("#id");
		}

		if (appendBrackets) {
			sb.append(">");
		}

		return sb.toString();
	}

	/**
	 * Generates a URI using an incrementally increasing Id, starting from 0
	 * @param domain - adds the domain name to the URI, e.g. domain=sports, then 
	 * @param sequentialNumberId - next id
	 * @param appendBrackets - appends < > 
	 * @param appendSuffixId - appends the string "#id" at the end, useful for Creative Works URI format             
	 * @return the URI
	 */
	public String numberURI(String domain, long sequentialNumberId, boolean appendBrackets, boolean appendSuffixId) {
		StringBuilder sb = new StringBuilder();
		
		if (appendBrackets) {
			sb.append("<");
		}
		
		sb.append(RandomUtil.baseURI);
		sb.append(domain);
		sb.append("/");
		sb.append(sequentialNumberId);
		
		if (appendSuffixId) {
			sb.append("#id");
		}
		
		if (appendBrackets) {
			sb.append(">");
		}
		
		return sb.toString();		
	}
	
	/**
	 * Generates a URI using a string, passed as input parameter
	 * @param domain - sets the domain name to the URI, e.g. domain=sports
	 * @param core - the string token used as an constant
	 */
	public String stringURI(String domain, String token, boolean appendBrackets, boolean appendSuffixId) {
		StringBuilder sb = new StringBuilder();

		if (appendBrackets) {
			sb.append("<");
		}

		sb.append(RandomUtil.baseURI);
		sb.append(domain);
		sb.append("/");
		sb.append(token);

		if (appendSuffixId) {
			sb.append("#id");
		}

		if (appendBrackets) {
			sb.append(">");
		}

		return sb.toString();
	}
	
	public String stringURI(String baseURI,String domain, String token, boolean appendBrackets, boolean appendSuffixId) {
		StringBuilder sb = new StringBuilder();

		if (appendBrackets) {
			sb.append("<");
		}

		sb.append(baseURI);
		sb.append(domain);
		sb.append("/");
		sb.append(token);

		if (appendSuffixId) {
			sb.append("#id");
		}

		if (appendBrackets) {
			sb.append(">");
		}

		return sb.toString();
	}
	
	public String randomURL(boolean surroundWithBrackets, boolean appendRandomSuffix) {
		StringBuilder sb = new StringBuilder();
		String[] domainsArray = { "com", "net", "org", "gov", "biz", "eu" };

		if (surroundWithBrackets) {
			sb.append("<");
		}

		sb.append("http://www.");
		sb.append(randomString(4, 12, false, false));
		sb.append(".");

		sb.append(domainsArray[nextInt(domainsArray.length)]);

		if (appendRandomSuffix) {
			sb.append("/");
			sb.append(randomString(6, 12, false, false));
		}

		if (surroundWithBrackets) {
			sb.append(">");
		}

		return sb.toString().toLowerCase();
	}

	/**
	 * Reads a text file and extracts all unique words in it
	 * 
	 * @param fullPath
	 */
	protected void buildWordsArray(String textFilePath) {
		BufferedReader br = null;
		HashSet<String> wordsSet = new HashSet<String>();

		try {
			//reading the WordsDictionary.txt file as a resource from jar
//			InputStream is = getClass().getResourceAsStream(textFilePath);
//			br = new BufferedReader(new InputStreamReader(is));
			br = new BufferedReader(new FileReader(textFilePath));

			String line = br.readLine();

			while (line != null) {
				if (line.trim().isEmpty()) {
					line = br.readLine();
					continue;
				}

				String[] words = line.split(" ");
				for (int i = 0; i < words.length; i++) {
					String aWord = new String(words[i]).trim();

					aWord = aWord.replaceAll("[\".;,{}\\[\\]()]", "");

					if (!wordsSet.contains(aWord.trim())) {
						wordsSet.add(aWord.toLowerCase());
					}
				}
				line = br.readLine();
			}
			br.close();

			// Add all unique words from text file to the wordsArray
			for (String word : wordsSet) {
				if (!word.isEmpty()) {
					wordsList.add(word);
				}
			}
			
//          Save unique words to a new file
//			BufferedWriter bw = new BufferedWriter(new FileWriter("data" + File.separator + "UniqueWordsDictionary.txt"));
//			for (String word : wordsList) {
//				System.out.println("word : " + word);
//				bw.write(word);
//				bw.write("\n");
//			}
//			bw.flush();
//			bw.close();
		}
		catch (IOException e) {
			System.out.println("IOException from buildWordsDictionary : " + e.getMessage());
			e.printStackTrace();
		}
	}
		/*jsaveta start*/
	/**
	 * Generates a random URI, using nextInt() method.
	 * 
	 * @param domain is the baseURI parameter
	 * @param appendBrackets - appends < > 
	 * @param appendSuffixId - appends the string "#id" at the end, useful for Creative Works URI format             
	 * @return the generated URI
	 */
	public String randomURI(String baseURI,String domain, boolean appendBrackets, boolean appendSuffixId) {
		String nextIntStr = Integer.toString(Math.abs(randomGenerator.nextInt()));
		StringBuilder sb = new StringBuilder();

		if (appendBrackets) {
			sb.append("<");
		}

		sb.append(baseURI);
		sb.append(domain);
		sb.append("/");
		sb.append(nextIntStr);

		if (appendSuffixId) {
			sb.append("#id");
		}

		if (appendBrackets) {
			sb.append(">");
		}

		return sb.toString();
	}
	public String GenerateUniqueID(String URI_){
		if(URI_.endsWith("#id")){
			String URI = URI_;
			URI = URI.replaceAll("[0-9]+/*\\.*[0-9]*",generateUniqueId());
			return URI;
		}
		return URI_;
	}

	public static String generateUniqueId() {      
	    UUID idOne = UUID.randomUUID();
	    String str=""+idOne;        
	    int uid=str.hashCode();
	    String filterStr=""+uid;
	    str=filterStr.replaceAll("-", "");
	    return str;
	}
	
	//for primary topic object
	public Resource generateEquivalentClassInstance(Statement s){
		String[] new_sub = s.getObject().toString().replace("rdf-schema#", "").split("/");
		String lastOne_ = new_sub[new_sub.length-1];
		Resource object_t = SesameBuilder.sesameValueFactory.createURI(core+lastOne_);
		return object_t;
	}
	
	public Resource generateUnionOneIntersectionInstance(Statement s){
		String[] new_sub = s.getObject().toString().replace("rdf-schema#", "").split("/");
		String lastOne_ = new_sub[new_sub.length-1];
		Resource object_t = SesameBuilder.sesameValueFactory.createURI(ldbc+lastOne_);
		return object_t;
	}
	
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
	/*jsaveta end*/
}
