package eu.ldbc.semanticpublishing.transformations.lexical;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

public class ChangeAntonym implements DataValueTransformation{

	private WordNetDatabase database = WordNetDatabase.getFileInstance();
	private String splitter;
	private double severity;
	private final Configuration configuration = new Configuration();
		
	public ChangeAntonym(String wndict, double severity){
		this.splitter = "[\\s|\n|\t]";
		this.severity = severity;
	}

	@SuppressWarnings("finally")
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			System.setProperty("wordnet.database.dir", configuration.getString(Configuration.WORDNET_PATH));
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			
			String[] tokens = f.split(this.splitter);
			f = "";
			Random coin = new Random();
				for(int i = 0; i < tokens.length; i++){
					String syn = tokens[i];
					Synset[] synset_list = database.getSynsets(syn);
					if(coin.nextDouble() <= this.severity){
					for (Synset synset : synset_list) {
						for (String wordForm : synset.getWordForms()) {
							for (WordSense antonym : synset.getAntonyms(wordForm)) {
								for (String opposite : antonym.getSynset().getWordForms()) {
									syn = opposite;
								break;
								}
							}
						}
					}
				}
				f += syn + " ";
			}
			f = f.trim();
		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return arg;
			}
		}
		return f;
	}

	
	public List<Synset> getSynsets(String word) {
		return Arrays.asList(database.getSynsets(word));
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}  

}
