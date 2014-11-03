package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;


public class UnionOf implements Transformation{
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	private static final String ldbc = "http://www.ldbc.eu/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	
	private Map<String,ArrayList<String>> ClassUnionOfMap; 
	public UnionOf(AbstractAsynchronousWorker worker){
		ClassUnionOfMap = new HashMap<String,ArrayList<String>>();
		ArrayList<String> temp = new ArrayList<String>();
		temp = new ArrayList<String>();
		temp.add(dbpediaowl + "Place");
		temp.add(core + "Theme");
		temp.add(dbpediaowl + "Event");
		temp.add(dbpediaowl + "Organisation");
		temp.add(foaf + "Person");
		ClassUnionOfMap.put(ldbc+"Thing",temp);
		
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
			String key = "";
			for (Entry<String, ArrayList<String>> entry : ClassUnionOfMap.entrySet()) {
				 if(entry.getValue().contains(s.getObject().toString())){
					 key = entry.getKey();
					 String[] new_sub = s.getSubject().toString().replace("rdf-schema#", "").split("/");
					 String lastOne_ = new_sub[new_sub.length-1];
						
					 model.add(SesameBuilder.sesameValueFactory.createURI(ldbc+lastOne_),s.getPredicate() ,SesameBuilder.sesameValueFactory.createURI(key),s.getContext());
				 }
				 else{
					 model.add(s);
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
