package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;
import java.util.Random;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class DisjointWith implements Transformation{
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	
	
	private ArrayList<ArrayList<String>> ClassDisjointness; 
	public DisjointWith(AbstractAsynchronousWorker worker){
		
		//this ArrayList contains the ontology disjointness.
		ClassDisjointness = new ArrayList<ArrayList<String>>();
		ArrayList<String> temp = new ArrayList<String>();
		
		temp.add(cworkNamespace + "NewsItem");
		temp.add(cworkNamespace + "BlogPost");
		temp.add(cworkNamespace + "Programme");
		ClassDisjointness.add(temp);
			
	}
	@Override
	public Object execute(Object arg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("finally")
	@Override
	public Model executeStatement(Statement arg) {
	   Statement s = (Statement)arg;
		Model model = new LinkedHashModel(); 
		 if(arg instanceof Statement){
	    	for(int i = 0; i < ClassDisjointness.size(); i++){
	    		ArrayList<String> list = ClassDisjointness.get(i);
	    		for(int j = 0; j < list.size(); j++){
	    			model = new LinkedHashModel();
	    			if(list.contains(s.getObject().stringValue())){
	    				Random rand = new Random();
	    				int disj = 0;
	    				while(list.get(disj).equals(s.getObject().stringValue())){disj = rand.nextInt(list.size());}
	    				model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(list.get(disj)),s.getContext());
	    			}
	    			else{
	    				model.add(s);
	    			}
	    		}
	    	}
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

}
