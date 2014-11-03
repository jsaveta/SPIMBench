package eu.ldbc.semanticpublishing.transformations.lexical;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.PropertyNames;
import eu.ldbc.semanticpublishing.properties.Configuration;
import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

public class ReplaceWordDefinition implements DataValueTransformation{

	private WordNetDatabase database = WordNetDatabase.getFileInstance();
	private String splitter;
	private double severity;
	private String wn;
	private final Configuration configuration = new Configuration();
		
	public ReplaceWordDefinition(String wndict, double severity){
		this.splitter = "[\\s|\n|\t]";
		this.severity = severity;
		this.wn = wndict;
	}

	@SuppressWarnings("finally")
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			System.setProperty(PropertyNames.DATABASE_DIRECTORY, configuration.getString(Configuration.WORDNET_PATH));
			WordNetDatabase database = WordNetDatabase.getFileInstance(); 
			
			String[] tokens = f.split(this.splitter);
			f = "";
			Random coin = new Random();
				for(int i = 0; i < tokens.length; i++){
					String syn = tokens[i];
					Synset[] synset_list = database.getSynsets(syn);
					//System.out.println("\n\n** Process word: " + syn);
					
					if(coin.nextDouble() <= this.severity){
						for (int k = 0; k < synset_list.length; k++)
						{
							//System.out.println("");
//							String[] wordForms = synset_list[k].getWordForms();
//							for (int j = 0; j < wordForms.length; j++)
//							{
//								System.out.print((j > 0 ? ", " : "") +
//										wordForms[j]);
//							}
							syn = synset_list[k].getDefinition();
							System.out.println(": " + synset_list[k].getDefinition());
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
