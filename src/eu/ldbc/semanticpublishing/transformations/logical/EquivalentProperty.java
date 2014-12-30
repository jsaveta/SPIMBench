package eu.ldbc.semanticpublishing.transformations.logical;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class EquivalentProperty implements Transformation{
	private static final String foaf = "http://xmlns.com/foaf/0.1/";
	private static final String dbpediaowl = "http://dbpedia.org/ontology/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	private static final String dbpprop = "http://dbpedia.org/property/";
	private static final String dcterms = "http://purl.org/dc/terms/";
	private static final String dc =  "http://purl.org/dc/elements/1.1/" ;
	
	
	private Map<String, String> PropertyEquivalence;
	
	public EquivalentProperty(AbstractAsynchronousWorker worker){
		//this map contains the ontology hierarchy. As key is the subclass and as value the superclass
		PropertyEquivalence = new HashMap<String, String>();
		PropertyEquivalence.put(foaf + "name", core + "name");
		PropertyEquivalence.put(foaf + "surname", core + "surname");
		PropertyEquivalence.put(foaf + "givenName", core + "givenName");
		PropertyEquivalence.put(dbpediaowl + "country", core + "country");
		PropertyEquivalence.put(dbpprop + "population", core + "population");
		PropertyEquivalence.put(dcterms + "subject", core + "subject");
		PropertyEquivalence.put(geo + "geometry",core + "geometry");
		
		PropertyEquivalence.put(dbpprop + "country", core + "country");
		PropertyEquivalence.put(dcterms + "subject", core + "subject");
		PropertyEquivalence.put(dbpprop + "manager", core + "manager");
		PropertyEquivalence.put(dbpprop + "name", core + "name");
		PropertyEquivalence.put(dbpprop + "nickname",core + "nickname");
		PropertyEquivalence.put(dbpprop + "website",core + "website");
		
		PropertyEquivalence.put(dbpprop + "caption",core + "caption");
		PropertyEquivalence.put(dbpprop + "equipment",core + "equipment");
		PropertyEquivalence.put(dbpprop + "olympic",core + "olympic");
		PropertyEquivalence.put(dbpprop + "team",core + "team");

		PropertyEquivalence.put(dc + "description",core + "description");
		PropertyEquivalence.put(dbpediaowl + "birthPlace",core + "birthPlace");
		PropertyEquivalence.put(dbpediaowl + "birthDate",core + "birthDate");
		PropertyEquivalence.put(dbpediaowl + "deathPlace",core + "deathPlace");
		PropertyEquivalence.put(dbpediaowl + "deathDate",core + "deathDate");
		
		
		//na psaksw ola osa einai equivalent metaaksu tous kai na ta prosthesw!
		
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
		    	if(PropertyEquivalence.containsKey(s.getPredicate().stringValue())){
		    		model.add(s.getSubject(), SesameBuilder.sesameValueFactory.createURI(PropertyEquivalence.get(s.getPredicate().stringValue())),s.getObject(),s.getContext());
		    	}
		    	else{
		    		model.add(s);
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
