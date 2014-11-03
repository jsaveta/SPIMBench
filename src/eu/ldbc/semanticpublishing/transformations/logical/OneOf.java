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

public class OneOf implements Transformation{
	private static final String travel = "http://www.co-ode.org/roberts/travel.owl#";
	private static final String ldbc = "http://www.ldbc.eu/";

	private Map<String, String> OneOfMap;
	public OneOf(AbstractAsynchronousWorker worker){
		OneOfMap = new HashMap<String, String>();
		OneOfMap.put(travel + "Island", ldbc + "Archipelago");
		OneOfMap.put(travel + "City", ldbc + "State");
		OneOfMap.put(travel + "AdministrativeDivision", ldbc + "ManagerialPartition");
		OneOfMap.put(travel + "TierOneAdministrativeDivision", ldbc + "TierOneManagerialPartition");
		OneOfMap.put(travel + "Coastline", ldbc + "Inshore");
		OneOfMap.put(travel + "Continent", ldbc + "Mainland");
		OneOfMap.put(travel + "Country",ldbc + "Land");
		OneOfMap.put(travel + "GeographicalFeature",ldbc + "GeographicCharacteristic");
		OneOfMap.put(travel + "River",ldbc + "Streamlet");
		OneOfMap.put(travel + "Ocean",ldbc + "Bay");
		OneOfMap.put(travel + "bodyOfLand",ldbc + "PieceOfGround");
		OneOfMap.put(travel + "person_agent",ldbc + "Representative");

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
				if(OneOfMap.containsKey(s.getObject().toString())){
	    			 model.add(s.getSubject(), s.getPredicate(), SesameBuilder.sesameValueFactory.createURI(OneOfMap.get(s.getObject().toString())),s.getContext());
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
