/**
 * 
 */
package eu.ldbc.semanticpublishing.transformations.lexical;

import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

/**
 * @author Alfio Ferrara, Universita` degli Studi di Milano
 * @date 18/mag/2010
 * Changes a random number of chars in a string (num of chars modified depends on severity)
 */

public class RandomCharsAddition implements DataValueTransformation {
	//private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	private static final String bbcNamespace = "http://www.bbc.co.uk/ontologies/bbc/";
	//private static final String rdfTypeNamespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	//private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	//private static final String person = "http://www.bbc.co.uk/ontologies/person/";
	
	public RandomCharsAddition(double severity){
		this.severity = severity;
	}
	
	private double severity = 0.0;

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.severity;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		String f_temp = f;
		boolean flag_c = false;
		boolean flag_s = false;
		boolean flag_e = false;
		//den pianoun katholou autoi oi elegxoi !!!!
		
		if(f.startsWith(bbcNamespace) /* || f.contains(rdfTypeNamespace) || f.contains(core) || f.contains(person)*/){
			f = f_temp.replace(bbcNamespace, "");
			f_temp = f;
			flag_c = true;
		}
		if(f.startsWith("http://www.")){
			f = f_temp.replace("http://www.", "");
			f_temp = f;
			//System.out.println(f);
			flag_s = true;
		}
		if(f.endsWith("#id")){
			f = f_temp.replace("#id", "");
			//System.out.println(f);
			flag_e = true;
		}
		
		if(arg instanceof String){
			//Do the job
			Random coin = new Random();
			String buffer = "";
			for(char c: f.toCharArray()){
				if(coin.nextDouble() <= this.severity){
					buffer += Utils.pickChar();
					buffer += c;
				}else{
					buffer += c;
				}
			}
			
			if(flag_c == true){
				f =  bbcNamespace + buffer;
				//System.out.println("-------------");
			}
			else if(flag_s == true && flag_e == true){
				f = "http://www." + buffer+"#id";
				//System.out.println("-------------");
			}
			else if(flag_s == true && flag_e == false){
				f = "http://www." + buffer;
			}
			else if(flag_s == false && flag_e == true){
				f = buffer+"#id";
			}
			else if(flag_s == false && flag_e == false){
				f = buffer;
			}
			
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
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}
