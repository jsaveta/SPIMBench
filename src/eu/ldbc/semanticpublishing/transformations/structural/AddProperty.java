package eu.ldbc.semanticpublishing.transformations.structural;

import java.util.Random;
import java.lang.Object;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.refdataset.DataManager;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;


public class AddProperty implements Transformation {
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	public AddProperty(){
		
	}
	
	@SuppressWarnings("finally")
	public Model executeStatement(Statement arg) { //statement as arg 
	Statement s = (Statement)arg;
	Model model = new LinkedHashModel();
		
	if(arg instanceof Statement){
		Random ru = new Random(); //
		URI predicate = SesameBuilder.sesameValueFactory.createURI(core + "creator"); 
		Value object;
		if(ru.nextBoolean()){object = SesameBuilder.sesameValueFactory.createURI(DataManager.organisationsIdsList.get(ru.nextInt(DataManager.organisationsIdsList.size())).replace("<", "").replace(">", "").replace("\\", ""));}
		else{object = SesameBuilder.sesameValueFactory.createURI(DataManager.personsIdsList.get(ru.nextInt(DataManager.personsIdsList.size())).replace("<", "").replace(">", "").replace("\\", ""));}
		
		model.add(arg);
		model.add((Resource)s.getSubject(),(URI) predicate ,(Value)object,(Resource)s.getContext());
		
	}
	else{
		try {
			throw new InvalidTransformation();
		} catch (InvalidTransformation e) {
			e.printStackTrace();
		}finally{
			return model;
		}
	}
	return model;
	}
	
	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object execute(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}
}
