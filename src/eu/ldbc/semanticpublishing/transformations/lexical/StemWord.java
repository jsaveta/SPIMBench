
	/*
	 * http://stackoverflow.com/questions/4397107/is-there-a-java-implementation-of-porter2-stemmer
	 * http://chianti.ucsd.edu/svn/csplugins/trunk/soc/layla/WordCloudPlugin/trunk/WordCloud/src/cytoscape/csplugins/wordcloud/Stemmer.java
	 * http://alvinalexander.com/java/jwarehouse/lucene-1.3-final/src/java/org/apache/lucene/analysis/PorterStemmer.java.shtml
	 * http://ir.dcs.gla.ac.uk/resources/linguistic_utils/porter.java
	 * http://tipsandtricks.runicsoft.com/Other/JavaStemmer.html
	 * 
	 * PORTER ALGORITHM
	 * 
	 * http://tipsandtricks.runicsoft.com/Other/JavaStemmer.html
	 * */
	
package eu.ldbc.semanticpublishing.transformations.lexical;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.tartarus.snowball.ext.EnglishStemmer;
import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

public class StemWord implements DataValueTransformation{

	private String splitter;
		
	public StemWord(){
		this.splitter = "[\\s|\n|\t]";
	}
	/*
	 *  1.Danish-DanishStemmer
		2.Dutch-DutchStemmer
		3.English-EnglishStemmer
		4.Finnish-FinnishStemmer
		5.French-FrenchStemmer
		6.German-GermanStemmer
		7.Hungarian-HungarianStemmer
		8.Italian-ItalianStemmer
		9.Norwegian-NorwegianStemmer
		10.Portuguese-PortugueseStemmer
		11.Romanian-RomanianStemmer
		12.Russian-RussianStemmer
		13.Spanish-SpanishStemmer
		14.Swedish-SwedishStemmer
		15.Turkish-TurkishStemmer*/
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		
		if(arg instanceof String){
			String[] tokens = f.split(this.splitter);
			f = "";
			for(int i = 0; i < tokens.length; i++){
				String syn = tokens[i];
					//System.out.println("syn :" + syn);
					//PorterStemmer stemmer = new PorterStemmer();
					EnglishStemmer stemmer = new EnglishStemmer();
					stemmer.setCurrent(syn);
					if(stemmer.stem())
					{
						 syn = stemmer.getCurrent();
						 //System.out.println("syn transf: " + syn);
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

