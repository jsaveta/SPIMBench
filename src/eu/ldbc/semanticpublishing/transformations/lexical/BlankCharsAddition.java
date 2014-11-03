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
 * https://code.google.com/p/swing-generator/downloads/list
 * iimb.0.1.src.tgz
 */
public class BlankCharsAddition implements DataValueTransformation {
	
	public BlankCharsAddition(double severity){
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
		if(arg instanceof String){
			//Do the job
			Random coin = new Random();
			String buffer = "";
			for(char c: f.toCharArray()){
				if(coin.nextDouble() <= this.severity){
					buffer += " ";
					buffer += c;
				}else{
					buffer += c;
				}
			}
			f = buffer;
			
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
