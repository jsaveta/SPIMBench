package eu.ldbc.semanticpublishing.generators.data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;

import eu.ldbc.semanticpublishing.generators.data.sesamemodelbuilders.SesameBuilder;
import eu.ldbc.semanticpublishing.transformations.Transformation;
import eu.ldbc.semanticpublishing.transformations.TransformationsCall;
import eu.ldbc.semanticpublishing.transformations.WriteIntermediateGS;
import eu.ldbc.semanticpublishing.util.RandomUtil;

/**
 * Abstract class for extending worker_s for the DataGenerator.
 */
public abstract class AbstractAsynchronousWorker extends Thread {
	private static final String cworkNamespace = "http://www.bbc.co.uk/ontologies/creativework/";
	
	protected static final String FILENAME_FORMAT = "%s%sgeneratedCreativeWorks-%04d.";
	protected static final String FILENAME_FORMAT_D2 = "%s%sgeneratedCreativeWorksD2-%04d.";
	protected static final String FILENAME_FORMAT_GS = "%s%sgeneratedCreativeWorksGS-%04d.";
	//protected static final String FILENAME_FORMAT_SIMPLEGS = "%s%sgeneratedCreativeWorksSIMPLEGS-%04d.";
	
	

	
	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			System.out.println("Exception caught by : " + Thread.currentThread().getName() + " : " + e.getMessage());
		}
	}	
	
	
	/**
	 * This method will be called for execution of a concrete task
	 */
	public abstract void execute() throws Exception;
	
	public abstract String getFileName1();
	public abstract Model getSesameModel();
	public abstract Model getSesameModel_2();
	public abstract RandomUtil getru();
	public abstract ArrayList<Model> getsesameModel2ArrayList();
	public abstract ArrayList<Model> getsesameModelArrayList();
	public abstract HashMap<String, String> getURIMapping();
	public abstract Map <String, Transformation> getTransformationConf();
	
	
	private static final String bbcid = "http://www.bbc.co.uk/things/";
	private static final String bbcNamespace = "http://www.bbc.co.uk/ontologies/bbc/";
	private static final String core = "http://www.bbc.co.uk/ontologies/coreconcepts/";
	private static final String transformationPath =  "eu.ldbc.semanticpublishing.transformations.";
	private static final String ldbc = "http://www.ldbc.eu/";
	
	private static Map <String,ArrayList<Double>> Ftransformations = new HashMap <String,ArrayList<Double>>();
	private Map <String, Transformation> TransformationConf;
	private Map <String, List<Transformation>> TransformationConfComplex;
	private AbstractAsynchronousWorker worker_;
	public abstract Model getSesameModel_GS();
	static int same_lexical = 0;
	static int diff_lexical = 0;
	static int same_struct = 0;
	static int diff_struct = 0;
	static int different_log = 0;
	static int diff_log= 0;
	static int same_comp = 0;
	static int diff_comp= 0;
	
	public Model TransformateSesameModel(AbstractAsynchronousWorker worker,Model sesameModel_GS_,FileOutputStream fos_3) throws RDFHandlerException, IOException{
		Model sesameModel_GS = sesameModel_GS_;
		this.worker_ = worker;
		WriteIntermediateGS writegs = new WriteIntermediateGS(worker_);
		TransformationsCall Transformationscall = new TransformationsCall(worker_);
		Transformationscall.setTransformationConf();
		TransformationConf = Transformationscall.getTransformationConf();
		TransformationConfComplex = Transformationscall.getTransformationConfComplex();
		Statement statement;
		Model temp_sesameModel_d2 = new LinkedHashModel();
		//loop for every triple in sesamemodel in order to transform and create D2
		ValueFactory factory = ValueFactoryImpl.getInstance();
		
		Iterator<Statement> it = worker_.getSesameModel().iterator();
		while(it.hasNext())
		{
			statement = it.next();
			if(!worker_.getURIMapping().containsKey(statement.getSubject().stringValue())){
					if(TransformationConf.containsKey((core + "primaryTopic")) && statement.getSubject().stringValue().startsWith(bbcid)){
						worker_.getURIMapping().put(statement.getSubject().stringValue(), statement.getSubject().stringValue());
						writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,"0","CW_id",sesameModel_GS,fos_3); //not transformed
					}
					else if (statement.getSubject().stringValue().startsWith(bbcid)){
						worker_.getURIMapping().put(statement.getSubject().stringValue(), getru().GenerateUniqueID(statement.getSubject().stringValue()));
						writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,"0","CW_id",sesameModel_GS,fos_3); //transformed
					}
					else {
						worker_.getURIMapping().put(statement.getSubject().stringValue(), getru().GenerateUniqueID(statement.getSubject().stringValue()));				
					}
					//start of subclass change for URIs that are subclasses of CWs
				Value subClassOf = statement.getObject();
				if(TransformationConf.containsKey(statement.getObject().stringValue())){
					Model tempModel = TransformationConf.get(statement.getObject().stringValue()).executeStatement(statement);
					if(!tempModel.isEmpty()){
						
						writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.75,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
						for (Statement st : tempModel){
							subClassOf = st.getObject();
						}
					}	
				}
				//end of subclass change 
				Resource subjectFromMap = SesameBuilder.sesameValueFactory.createURI( worker_.getURIMapping().get(statement.getSubject().stringValue()));
				
				temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),(Value)subClassOf, (Resource)statement.getContext());		
			}
			else{ 
				Resource subjectFromMap = SesameBuilder.sesameValueFactory.createURI( worker_.getURIMapping().get(statement.getSubject().stringValue()));				
				if( statement.getPredicate().stringValue().equals(bbcNamespace + "primaryContentOf")){	
					Statement nameStatement=factory.createStatement(statement.getSubject(), statement.getPredicate(), statement.getObject(), statement.getContext());;
					if(TransformationConf.containsKey(core + "primaryTopic")){
						String uri = getru().GenerateUniqueID(statement.getObject().stringValue());
						worker_.getURIMapping().put(statement.getObject().stringValue(),uri);
						nameStatement = factory.createStatement(statement.getSubject(), statement.getPredicate(), SesameBuilder.sesameValueFactory.createURI(uri), statement.getContext());
						
					}
					else{
						worker_.getURIMapping().put(statement.getObject().stringValue(), statement.getObject().stringValue());
					}
					
					if( TransformationConf.containsKey(statement.getPredicate().stringValue()) && TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "logical.InverseFunctionalProperty"))){
						Model tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(nameStatement);
						for (Statement st : tempModel){
							temp_sesameModel_d2.add(st.getSubject(),st.getPredicate(),SesameBuilder.sesameValueFactory.createURI(worker_.getURIMapping().get(st.getObject().stringValue())),st.getContext());
							writegs.WriteGSAsTriples(statement.getObject().stringValue(), worker_.getURIMapping().get(statement.getObject().stringValue()),1.0, TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
							
						}
						
					}
					else{
						Value objectFromMap = SesameBuilder.sesameValueFactory.createURI( worker_.getURIMapping().get(statement.getObject().stringValue()));
						temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),objectFromMap, (Resource)statement.getContext());
					}
					if(!statement.getObject().stringValue().equals(worker_.getURIMapping().get(statement.getObject().stringValue()))){
						writegs.WriteGSAsTriples(statement.getObject().stringValue(), worker_.getURIMapping().get(statement.getObject().stringValue()),1.0,"0",/*statement.getPredicate().stringValue()*/"primary_id",sesameModel_GS,fos_3);
					}
					continue;
				}
				else if(statement.getPredicate().stringValue().equals(core + "primaryTopic")){
					Iterator<Statement> it_2 = it;
					Statement temp_statement = factory.createStatement(statement.getSubject(),statement.getPredicate(),statement.getObject(),statement.getContext());
					if(it_2.hasNext()){temp_statement = it_2.next();}
					if(TransformationConf.containsKey(statement.getPredicate().stringValue())){
						if( TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "logical.FunctionalProperty"))){
						Model tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(statement);
						if(TransformationConf.containsKey(temp_statement.getObject().stringValue()) && TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.EquivalentClass"))){
								for (Statement st : tempModel){
									Statement nameStatement = factory.createStatement(st.getSubject(), st.getPredicate(), SesameBuilder.sesameValueFactory.createURI(getru().generateEquivalentClassInstance(st).toString()), st.getContext());
									tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(nameStatement);
								}
								
						}
						else if(TransformationConf.containsKey(temp_statement.getObject().stringValue()) && (TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.UnionOf")) ||
								/*TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.OneOf")) ||*/
								TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.IntersectionOf")))){	
							for (Statement st : tempModel){
								Statement nameStatement = factory.createStatement(st.getSubject(), st.getPredicate(), SesameBuilder.sesameValueFactory.createURI(getru().generateUnionOneIntersectionInstance(st).toString()), st.getContext());
								tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(nameStatement);
							}
							
						}
						
						for (Statement st : tempModel){
								worker_.getURIMapping().put(statement.getObject().stringValue(), st.getObject().stringValue());
								if(!st.getObject().stringValue().equals(statement.getObject().stringValue())){
									writegs.WriteGSAsTriples(statement.getObject().stringValue(), worker_.getURIMapping().get(statement.getObject().stringValue()),1.0, TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								}
							}
						}
					}
					else if(TransformationConf.containsKey(temp_statement.getObject().stringValue()) && TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.EquivalentClass"))){
						worker_.getURIMapping().put(statement.getObject().stringValue(), getru().generateEquivalentClassInstance(statement).toString());
						
					}
					else if(TransformationConf.containsKey(temp_statement.getObject().stringValue()) &&(TransformationConf.containsKey(temp_statement.getObject().stringValue()) && TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.UnionOf")) ||
							TransformationConf.get(temp_statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.IntersectionOf")))){
						worker_.getURIMapping().put(statement.getObject().stringValue(), getru().generateUnionOneIntersectionInstance(statement).toString());
						
					}
					else{
						worker_.getURIMapping().put(statement.getObject().stringValue(), statement.getObject().stringValue());
					}
					
					
					Value objectFromMap = SesameBuilder.sesameValueFactory.createURI( worker_.getURIMapping().get(statement.getObject().stringValue()));
					temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),objectFromMap, (Resource)statement.getContext());
					
					if(!statement.getObject().stringValue().equals(worker_.getURIMapping().get(statement.getObject().stringValue()))){
						writegs.WriteGSAsTriples(statement.getObject().stringValue(), worker_.getURIMapping().get(statement.getObject().stringValue()),1.0,"0",/*statement.getPredicate().stringValue()*/"Thing_sub",sesameModel_GS,fos_3);
					}
					statement = temp_statement;
					subjectFromMap = SesameBuilder.sesameValueFactory.createURI( worker_.getURIMapping().get(statement.getSubject().stringValue()));
				}
							
				if (TransformationConf.containsKey(statement.getObject().stringValue())){

					if(TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.EquivalentClass"))){
						Model tempModel = TransformationConf.get(statement.getObject().stringValue()).executeStatement(statement);
						for (Statement st : tempModel){
							temp_sesameModel_d2.add(subjectFromMap, (URI)st.getPredicate(),(Value)st.getObject(), (Resource)st.getContext());
							worker_.getURIMapping().put(statement.getObject().stringValue(), st.getObject().stringValue());
							if(!st.getObject().stringValue().equals(statement.getObject().stringValue())){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								different_log++;
								
							}
							else{diff_log++;}
						}	
					}
					else if(TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.UnionOf")) ||
							TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.OneOf")) ||
							TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.IntersectionOf"))){
						Model tempModel = TransformationConf.get(statement.getObject().stringValue()).executeStatement(statement);
						for (Statement st : tempModel){
							temp_sesameModel_d2.add(subjectFromMap, (URI)st.getPredicate(),(Value)st.getObject(), (Resource)st.getContext());
							worker_.getURIMapping().put(statement.getObject().stringValue(), st.getObject().stringValue());
							if(!st.getObject().stringValue().equals(ldbc + "Individual_Corporation")){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.85,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								different_log++;
							}
							else if(!st.getObject().stringValue().equals(statement.getObject().stringValue())){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.75,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								different_log++;
								
							}
							else{diff_log++;}
						}
					}
					else if(TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.SubClassOf"))){
						Model tempModel = TransformationConf.get(statement.getObject().stringValue()).executeStatement(statement);
						for (Statement st : tempModel){
							if(!st.getObject().stringValue().equals(statement.getObject().stringValue())){
								//writegs.WriteSimpleGS(statement.getSubject().stringValue(),  worker_.getURIMapping().get(statement.getSubject().stringValue()), 0.75, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]);
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.75,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								different_log++;
								
							}
							else{diff_log++;}
							worker_.getURIMapping().put(statement.getObject().stringValue(), st.getObject().stringValue());
							
						}
					}
					else if(TransformationConf.get(statement.getObject().stringValue()).toString().startsWith((transformationPath + "logical.DisjointWith"))){
						Model tempModel = TransformationConf.get(statement.getObject().stringValue()).executeStatement(statement);
						for (Statement st : tempModel){
							temp_sesameModel_d2.add(subjectFromMap, (URI)st.getPredicate(),(Value)st.getObject(), (Resource)st.getContext());
							worker_.getURIMapping().put(statement.getObject().stringValue(), st.getObject().stringValue());
							if(!st.getObject().stringValue().equals(statement.getObject().stringValue())){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getObject().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getObject().stringValue(),sesameModel_GS,fos_3);
								different_log++;
								
							}
							else{diff_log++;}
						}
					}
				}
				else if(TransformationConf.containsKey(statement.getPredicate().stringValue())){
					URI predicate = statement.getPredicate();
					if(	TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "logical.EquivalentProperty")) || 
						TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "logical.DisjointProperty"))){ 
					Model tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(statement);
					if(!tempModel.isEmpty()){	
						for (Statement st : tempModel){							
							predicate = st.getPredicate();
							temp_sesameModel_d2.add(subjectFromMap, (URI)predicate,(Value)statement.getObject(), (Resource)statement.getContext());
								if(!predicate.stringValue().equals(statement.getPredicate().stringValue())){
									writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.9, TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
									different_log++;
									
								}
								else{diff_log++;}
							}
						}	
					}
					else if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "logical.SubPropertyOf"))){ //+ "logical.SubPropertyOf"
						Model tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(statement);
						if(!tempModel.isEmpty()){	
							for (Statement st : tempModel){							
								predicate = st.getPredicate();
								temp_sesameModel_d2.add(subjectFromMap, (URI)predicate,(Value)statement.getObject(), (Resource)statement.getContext());
									if(!predicate.toString().equals(statement.getPredicate().stringValue())){
										writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.0, TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "logical.", "").split("@")[0]), statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
										different_log++;
										
									}
									else{diff_log++;}
								}
							}	
						}
					
				    //structural transformations
					else if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "structural."))){
						Model tempModel = TransformationConf.get(statement.getPredicate().stringValue()).executeStatement(statement);
						boolean didStructTrans = false;
						if(!tempModel.isEmpty()){ 
							for (Statement tempStatement : tempModel){
								temp_sesameModel_d2.add((Resource)subjectFromMap,(URI)tempStatement.getPredicate(),(Value)tempStatement.getObject(),(Resource)statement.getContext());											
								if(tempStatement.getObject().stringValue().equals(statement.getObject().stringValue())){
									same_struct++;
								}
								else{
									diff_struct++;
								}
							}	
							if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "structural.AddProperty"))){
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.9,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "structural.", "").split("@")[0]),statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
								didStructTrans = false;
							}
							else {
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "structural.", "").split("@")[0]),statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
								didStructTrans = false;
							}
							if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "structural.AggregateProperties"))){
								if(it.hasNext())statement = it.next();
							}
						}
						else if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "structural.DeleteProperty"))){
							writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),0.9,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "structural.", "").split("@")[0]),statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
							didStructTrans = false;
						}
						if(didStructTrans == true){
							same_struct++;
						}
						else{
							diff_struct++;
						}
					}				
					else if(TransformationConf.get(statement.getPredicate().stringValue()).toString().startsWith((transformationPath + "lexical."))){
						//lexical transformations
						Object temp = TransformationConf.get(statement.getPredicate().stringValue()).execute(statement.getObject().stringValue());
						
						//for experiments
						if(temp.toString().equals(statement.getObject().stringValue())){
							same_lexical++;
						}
						else{
							diff_lexical++;
						}
						 
						if(temp instanceof String){
							if(!(temp.toString().equals("")) && (temp!=null) && !(temp.toString().equals(statement.getObject().stringValue()))){
								if(((String) temp).replace("\"", "").startsWith("http://www.")){
									temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),SesameBuilder.sesameValueFactory.createURI(((String) temp).replace("\"", "")), (Resource)statement.getContext());	
								}
								else{
									temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),(Value)SesameBuilder.sesameValueFactory.createLiteral(((String) temp).replace("\"", "")), (Resource)statement.getContext());
								}
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "lexical.", "").split("@")[0]),statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
							
							}
							else{
								temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),(Value)statement.getObject(), (Resource)statement.getContext());
							}
						}
						else if (temp instanceof Value){
							temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),(Value)temp, (Resource)statement.getContext());
							writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get(statement.getPredicate().stringValue()).toString().replace(transformationPath + "lexical.", "").split("@")[0]),statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
						}
					}
					
				}
				else if (TransformationConfComplex.containsKey(statement.getPredicate().stringValue())){
					Model complexModel = new LinkedHashModel();
					List<Transformation> value = TransformationConfComplex.get(statement.getPredicate().stringValue());//entry.getValue();
					complexModel = new LinkedHashModel();
					for (int e = 0; e < value.size(); e++) {
						Transformation tr = (Transformation) value.get(e);
						if(tr.toString().startsWith((transformationPath + "lexical."))){
							Object temp;
							if(!(statement.getObject() instanceof Literal)){
								 temp = statement.getObject().stringValue();
							}
							else{
								 temp = tr.execute(statement.getObject().stringValue());
							}
							if (temp instanceof Literal){
								complexModel.add(subjectFromMap, (URI)statement.getPredicate(),(Value)SesameBuilder.sesameValueFactory.createLiteral(((Value) temp).stringValue()), (Resource)statement.getContext());
								writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(
										 statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, 
												 tr.toString().replace(transformationPath + "lexical.", "").split("@")[0]),
												 statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
								 if(temp.toString().equals(statement.getObject().stringValue())){
									 same_comp++;
									
								}
								else{diff_comp++;}
							 }
							 else{
								 if( temp.toString().startsWith("http://")){
									 complexModel.add(subjectFromMap, (URI)statement.getPredicate(),(Value)SesameBuilder.sesameValueFactory.createURI(temp.toString().replace("\"", "")), (Resource)statement.getContext());
									  
								 }
								 else{
									 complexModel.add(subjectFromMap, (URI)statement.getPredicate(),(Value)SesameBuilder.sesameValueFactory.createLiteral(temp.toString().replace("\"", "")), (Resource)statement.getContext());
									 
								 }
								 if(temp.toString().equals(statement.getObject().stringValue())){
									 same_comp++;
									
								}
								else{diff_comp++;}
								 writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(
										 statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, 
												 tr.toString().replace(transformationPath + "lexical.", "").split("@")[0]),
												 statement.getPredicate().stringValue(),sesameModel_GS,fos_3);
							 }
						}
						else if(tr.toString().startsWith((transformationPath + "logical."))){
							Model temp = new LinkedHashModel();
							
							if(!complexModel.isEmpty()){
								for (Statement st : complexModel){
									temp = tr.executeStatement(st);
									if(!temp.isEmpty()){
										for (Statement st1 : temp){
											if(!st1.toString().equals(st.toString())){
												writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(
														 statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, 
																 tr.toString().replace(transformationPath + "logical.", "").split("@")[0]),
																 statement.getPredicate().stringValue(),sesameModel_GS,fos_3);	
												diff_comp++;
												
											}
											else{same_comp++;}
											complexModel = new LinkedHashModel();
											complexModel.add(subjectFromMap, (URI)st1.getPredicate(),(Value)st1.getObject(), (Resource)st1.getContext());
										} 
									}
								} 
							}else{
								temp = tr.executeStatement(statement);
								if(!temp.isEmpty()){
									complexModel = new LinkedHashModel();
									for (Statement st1 : temp){
										complexModel.add(subjectFromMap, (URI)st1.getPredicate(),(Value)st1.getObject(), (Resource)st1.getContext());
									} 
								}
							} 
						}
						else if(tr.toString().startsWith((transformationPath + "structural."))){
						Model temp = new LinkedHashModel();	
						if(!complexModel.isEmpty()){
							for (Statement st : complexModel){
								temp = tr.executeStatement(st);
								complexModel = new LinkedHashModel();
								if(!temp.isEmpty()){
									for (Statement st1 : temp){
										if(!st1.toString().equals(st.toString())){
											writegs.WriteGSAsTriples(statement.getSubject().stringValue(), worker_.getURIMapping().get(
													 statement.getSubject().stringValue()),1.0,TransformationsCall.getKey(TransformationsCall.transformationsMap, 
															 tr.toString().replace(transformationPath + "structural.", "").split("@")[0]),
															 statement.getPredicate().stringValue(),sesameModel_GS,fos_3);	
											diff_comp++;
											
										}
										else{same_comp++;}
										
										complexModel.add(subjectFromMap, (URI)st1.getPredicate(),(Value)st1.getObject(), (Resource)st1.getContext());
									} 
								}
							}
						}
						else{
							if(!temp.isEmpty()){
								complexModel = new LinkedHashModel();
								for (Statement st1 : temp){
									complexModel.add(subjectFromMap, (URI)st1.getPredicate(),(Value)st1.getObject(), (Resource)st1.getContext());
								} 
							}
						} 
					}
				}
				for (Statement st : complexModel){
					temp_sesameModel_d2.add(st);
				}
				}
				else{
					if(statement.getPredicate().stringValue().equals(cworkNamespace+"altText")){
						String new_altText = "thumbnail atlText for CW http://www.bbc.co.uk/context/" + subjectFromMap.toString().replace("http://www.bbc.co.uk/things/", "");
						temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),SesameBuilder.sesameValueFactory.createLiteral(new_altText));	
					}
					else{
						temp_sesameModel_d2.add(subjectFromMap, (URI)statement.getPredicate(),(Value) statement.getObject(), (Resource)statement.getContext());	
					}
				}
				
			}
			sesameModel_GS = new LinkedHashModel();
			
		}
//System.out.println("same_lexical " + same_lexical);
//System.out.println("diff_lexical " + diff_lexical);
//System.out.println("same_struct " + same_struct);
//System.out.println("diff_struct " + diff_struct);
//System.out.println("different_log " + different_log);
//System.out.println("diff_log " + diff_log);
//System.out.println("same_comp " + same_comp);
//System.out.println("diff_comp " + diff_comp);

		getsesameModelArrayList().add(worker_.getSesameModel()); //add every CW from the sesame model in an arraylist as models	
		getsesameModel2ArrayList().add(temp_sesameModel_d2); //add every CW from the sesame model 2 in an arraylist as models
		return 	temp_sesameModel_d2;	
	}
	
	
	public void InsertSameAsOrDifferentFromCW(AbstractAsynchronousWorker worker,Model sesameModel_GS_,FileOutputStream fos_3) throws RDFHandlerException, IOException{
		Model sesameModel_GS = sesameModel_GS_;
		this.worker_ = worker;
		WriteIntermediateGS writegs = new WriteIntermediateGS(worker_);
		TransformationsCall TrCall = new TransformationsCall(worker_);
		TrCall.setTransformationConf();
		TransformationConf = TrCall.getTransformationConf();	
		if(TransformationConf.containsKey("logicalSameAsOnExistingCW")){
			Model model_e =TransformationConf.get("logicalSameAsOnExistingCW").executeStatement(null);
			if(!model_e.isEmpty()){	
				for (Statement statement : model_e){
					writegs.WriteGSAsTriples( TrCall.getKey(worker_.getURIMapping(),statement.getSubject().stringValue()),statement.getObject().stringValue(),1.0,TrCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get("logicalSameAsOnExistingCW").toString().replace(transformationPath + "logical.", "").split("@")[0]),"CW",sesameModel_GS,fos_3);
			    	writegs.WriteGSAsTriples( TrCall.getKey(worker_.getURIMapping(),statement.getObject().stringValue()),statement.getSubject().stringValue(),1.0,TrCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get("logicalSameAsOnExistingCW").toString().replace(transformationPath + "logical.", "").split("@")[0]),"CW",sesameModel_GS,fos_3);
			
				}
			}
		}
		if(TransformationConf.containsKey("logicalSameAs")){
			Model model_s = TransformationConf.get("logicalSameAs").executeStatement(null);
			if(!model_s.isEmpty()){
				for (Statement statement : model_s){
					writegs.WriteGSAsTriples( TrCall.getKey(worker_.getURIMapping(),statement.getSubject().stringValue()),statement.getObject().stringValue(),1.0,TrCall.getKey(TransformationsCall.transformationsMap, TransformationConf.get("logicalSameAs").toString().replace(transformationPath + "logical.", "").split("@")[0]),"CW",sesameModel_GS,fos_3);
				}
			}
		}
		if(TransformationConf.containsKey("logicalDifferentFrom")){
			TransformationConf.get("logicalDifferentFrom").executeStatement(null);
		}
		
	}


	public static Map <String,ArrayList<Double>> getFtransformations() {
		return Ftransformations;
	}


	public static void setFtransformations(Map <String,ArrayList<Double>> ftransformations) {
		Ftransformations = ftransformations;
	}
	

}
