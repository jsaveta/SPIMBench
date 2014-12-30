package eu.ldbc.semanticpublishing.transformations.lexical;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;


import org.openrdf.model.vocabulary.XMLSchema;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class ChangeBooleanValue implements Transformation {
	
	public ChangeBooleanValue(){}
	
	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name;
	}

	/* (non-Javadoc)
	 * @see it.unimi.dico.islab.iimb.transfom.Transformation#execute(java.lang.Object)
	 */
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String f = (String)arg;
		if(arg instanceof String){
			if(f.equals("false")){
				f = "true";
			}else if(f.equals("true")){
				f = "false";
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
		return SesameBuilder.sesameValueFactory.createLiteral(f,XMLSchema.BOOLEAN);
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}
