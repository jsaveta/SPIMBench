package eu.ldbc.semanticpublishing.transformations;

import eu.ldbc.semanticpublishing.generators.data.AbstractAsynchronousWorker;
import eu.ldbc.semanticpublishing.transformations.lexical.BlankCharsAddition;
import eu.ldbc.semanticpublishing.transformations.lexical.BlankCharsDeletion;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeBooleanValue;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeDateFormat;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeGenderFormat;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeLanguage;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeNumberFormat;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeWordPermutation;
import eu.ldbc.semanticpublishing.transformations.lexical.StemWord;
import eu.ldbc.semanticpublishing.transformations.lexical.NameStyleAbbreviation;
import eu.ldbc.semanticpublishing.transformations.lexical.RandomCharsAddition;
import eu.ldbc.semanticpublishing.transformations.lexical.RandomCharsDeletion;
import eu.ldbc.semanticpublishing.transformations.lexical.RandomCharsModifier;
import eu.ldbc.semanticpublishing.transformations.lexical.ChangeSynonym;
import eu.ldbc.semanticpublishing.transformations.lexical.TokenAddition;
import eu.ldbc.semanticpublishing.transformations.lexical.TokenDeletion;
import eu.ldbc.semanticpublishing.transformations.lexical.TokenShuffle;
import eu.ldbc.semanticpublishing.transformations.logical.CWDifferentFrom;
import eu.ldbc.semanticpublishing.transformations.logical.CWSameAs;
import eu.ldbc.semanticpublishing.transformations.logical.DisjointProperty;
import eu.ldbc.semanticpublishing.transformations.logical.DisjointWith;
import eu.ldbc.semanticpublishing.transformations.logical.EquivalentClass;
import eu.ldbc.semanticpublishing.transformations.logical.EquivalentProperty;
import eu.ldbc.semanticpublishing.transformations.logical.FunctionalProperty;
import eu.ldbc.semanticpublishing.transformations.logical.IntersectionOf;
import eu.ldbc.semanticpublishing.transformations.logical.InverseFunctionalProperty;
import eu.ldbc.semanticpublishing.transformations.logical.OneOf;
import eu.ldbc.semanticpublishing.transformations.logical.SameAsOnExistingCW;
import eu.ldbc.semanticpublishing.transformations.logical.SubClassOf;
import eu.ldbc.semanticpublishing.transformations.logical.SubPropertyOf;
import eu.ldbc.semanticpublishing.transformations.logical.UnionOf;
import eu.ldbc.semanticpublishing.transformations.structural.AddProperty;
import eu.ldbc.semanticpublishing.transformations.structural.AggregateProperties;
import eu.ldbc.semanticpublishing.transformations.structural.DeleteProperty;
import eu.ldbc.semanticpublishing.transformations.structural.ExtractProperty;


public class TransformationConfiguration {
	
	//lexical transformations
	public static RandomCharsModifier substituteRANDOMCHARS(double severity){
		return new RandomCharsModifier(severity);
	}
	
	public static BlankCharsDeletion deleteRANDOMBLANKS(double severity){
		return new BlankCharsDeletion(severity);
	}

	public static BlankCharsAddition addRANDOMBLANKS(double severity){
		return new BlankCharsAddition(severity);
	}

	public static RandomCharsDeletion deleteRANDOMCHARS(double severity){
		return new RandomCharsDeletion(severity);
	}

	public static RandomCharsAddition addRANDOMCHARS(double severity){
		return new RandomCharsAddition(severity);
	}

	public static TokenShuffle shuffleTOKENS(String splitter, double severity){
		return new TokenShuffle(splitter,severity);
	}

	public static TokenDeletion deleteTOKENS(String splitter, double severity){
		return new TokenDeletion(splitter,severity);
	}
	
	public static TokenAddition addTOKENS(String splitter, double severity){
		return new TokenAddition(splitter,severity);
	}
	
	public static ChangeSynonym changeSYNONYMS(String wndict, double severity){
		return new ChangeSynonym(wndict, severity);
	}

	public static ChangeDateFormat dateFORMAT(String sourceFormat, int format){
		return new ChangeDateFormat(sourceFormat, format);
	}
	
	public static ChangeGenderFormat genderFORMAT(){
		return new ChangeGenderFormat();
	}
	
	public static ChangeNumberFormat numberFORMAT(int add, double severity){
		return new ChangeNumberFormat(add, severity);
	}
	
	public static NameStyleAbbreviation abbreviateNAME(/*int format,*/ int surnames){
		return new NameStyleAbbreviation(/*format,*/ surnames);
	}
	public static ChangeLanguage changeLANGUAGE(){
		return new ChangeLanguage();
	}
	
	public static ChangeBooleanValue changeBOOLEAN(){
		return new ChangeBooleanValue();
	}
	
	public static ChangeWordPermutation changeWORDPERMUTATION(){
		return new ChangeWordPermutation();
	}
	
	public static StemWord STEMWORD(){
		return new StemWord();
	}
	//structural transformations
	public static DeleteProperty deletePROPERTY(){
		return new DeleteProperty();
	}
	public static AddProperty addPROPERTY(){
		return new AddProperty();
	}
	public static ExtractProperty extractPROPERTY(){
		return new ExtractProperty();
	}
	public static ExtractProperty extractPROPERTY(int split){
		return new ExtractProperty(split);
	}
	public static AggregateProperties aggregatePROPERTIES(AbstractAsynchronousWorker worker){
		return new AggregateProperties(worker);
	}

	//logical transformations
	public static CWSameAs CWSAMEAS(AbstractAsynchronousWorker worker){
		return new CWSameAs(worker);
	}
	public static CWDifferentFrom CWDIFFERENTFROM(AbstractAsynchronousWorker worker){
		return new CWDifferentFrom(worker);
	}
	public static SameAsOnExistingCW CWSAMEASEXISTING(AbstractAsynchronousWorker worker){
		return new SameAsOnExistingCW(worker);
	}
	public static SubClassOf SUBCLASSOF(AbstractAsynchronousWorker worker,int depth){
		return new SubClassOf(worker,depth);
	}
	public static EquivalentClass EQUIVALENTCLASS(AbstractAsynchronousWorker worker){
		return new EquivalentClass(worker);
	}
	public static EquivalentProperty EQUIVALENTPROPERTY(AbstractAsynchronousWorker worker){
		return new EquivalentProperty(worker);
	}	
	public static DisjointWith DISJOINTWITH(AbstractAsynchronousWorker worker){
		return new DisjointWith(worker);
	}
	public static SubPropertyOf SUBPROPERTY(AbstractAsynchronousWorker worker){
		return new SubPropertyOf(worker);
	}

	public static DisjointProperty DISJOINTPROPERTY(AbstractAsynchronousWorker worker){
		return new DisjointProperty(worker);
	}
	
	public static FunctionalProperty FUNCTIONALPROPERTY(AbstractAsynchronousWorker worker){
		return new FunctionalProperty(worker);
	}
	
	public static InverseFunctionalProperty INVERSEFUNCTIONALPROPERTY(AbstractAsynchronousWorker worker){
		return new InverseFunctionalProperty(worker);
	}

	public static IntersectionOf INTERSECTIONOF(AbstractAsynchronousWorker worker){
		return new IntersectionOf(worker);
	}
	public static UnionOf UNIONOF(AbstractAsynchronousWorker worker){
		return new UnionOf(worker);
	}
	public static OneOf ONEOF(AbstractAsynchronousWorker worker){
		return new OneOf(worker);
	}

	
	
	
	
	
}
