package eu.ldbc.semanticpublishing.transformations.structural;

import java.util.ArrayList;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LinkedHashModel;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;
import eu.ldbc.semanticpublishing.transformations.Transformation;

public class ExtractProperty  implements Transformation{
		int N;
	    ArrayList <String> extractProp; 
		private static final String foaf = "http://xmlns.com/foaf/0.1/";
		private static final String dbpediaowl = "http://dbpedia.org/ontology/";
		private static final String geo = "http://www.w3.org/2003/01/geo/wgs84_pos#";
		private static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		private static final String dbpprop = "http://dbpedia.org/property/";
		private static final String dcterms = "http://purl.org/dc/terms/";
		
	    
		public ExtractProperty(int N){
			extractProp = new ArrayList<String>();
			extractProp.add(foaf + "name");
			extractProp.add(foaf + "surname");
			extractProp.add(foaf + "giveName");
			extractProp.add(rdfs + "comment");
			extractProp.add(dbpediaowl + "country");
			extractProp.add(dcterms + "subject");
			extractProp.add(geo + "geometry");
			extractProp.add(rdfs + "label");
			extractProp.add(dbpprop + "country");
			extractProp.add(dcterms + "subject");
			extractProp.add(dbpprop + "manager");
			extractProp.add(dbpprop + "name");
			extractProp.add(dbpprop + "nickname");
			extractProp.add(dbpprop + "website");
			
			this.N = N;
			
		}
		public ExtractProperty(){
			this.N = 2;
			extractProp = new ArrayList<String>();
			extractProp.add(foaf + "name");
			extractProp.add(foaf + "surname");
			extractProp.add(foaf + "giveName");
			extractProp.add(rdfs + "comment");
			extractProp.add(dbpediaowl + "country");
			extractProp.add(dcterms + "subject");
			extractProp.add(geo + "geometry");
			extractProp.add(rdfs + "label");
			extractProp.add(dbpprop + "country");
			extractProp.add(dcterms + "subject");
			extractProp.add(dbpprop + "manager");
			extractProp.add(dbpprop + "name");
			extractProp.add(dbpprop + "nickname");
			extractProp.add(dbpprop + "website");
		}
		
		@SuppressWarnings("finally")
		public Model executeStatement(Statement arg) { //statement as arg 
			
			Statement st = (Statement)arg;
			Model model = new LinkedHashModel();
			
		    if(arg instanceof Statement){
				
		    	String s = st.getObject().stringValue();
		    	if(!st.getPredicate().stringValue().contains("date") && !st.getPredicate().stringValue().contains("about") && !extractProp.contains(st.getPredicate().stringValue()) ){
					final int base = s.length() / N;
				    final int remainder = s.length() % N;
		
				    String[] parts = new String[N];
				    for (int i = 0; i < N; i++) {
				        int length = base + (i < remainder ? 1 : 0);
				        parts[i] = s.substring(0, length);
				        s = s.substring(length);
				        
				        URI predicate = SesameBuilder.sesameValueFactory.createURI((st.getPredicate().stringValue() + Integer.toString(i)));
				        if(st.getObject() instanceof Literal){
							Value object = SesameBuilder.sesameValueFactory.createLiteral(parts[i]);
							model.add((Resource)st.getSubject(), (URI)predicate ,(Value)object,(Resource)st.getContext());
						}
				        else{
				        	model.add((Resource)st.getSubject(), (URI)predicate ,(Value)st.getObject(),(Resource)st.getContext());	
							
				        }
				    }
		    	}
		    	else{ model.add(st);}
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


