package eu.ldbc.semanticpublishing.transformations.lexical;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import com.gtranslate.Language;
import com.gtranslate.Translator;

import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

//https://code.google.com/p/java-google-translate-text-to-speech/

public class ChangeLanguage implements DataValueTransformation{
	public ChangeLanguage(){}
	
	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg){
		String input = (String)arg;
		String translated;
		if(arg instanceof String){
		Translator translate = Translator.getInstance();
		translated = translate.translate(input, Language.ENGLISH, Language.GREEK);
		//System.out.println(translated); 
		}else{
			try {
				throw new InvalidTransformation();
			} catch (InvalidTransformation e) {
				e.printStackTrace();
			}finally{
				return input;
			}
		}
		return translated.replace("\\", "");
		
	}
//		public static void main(String[] args) throws Exception {
//			Translator translate = Translator.getInstance();
//			String text = translate.translate("I am programmer", Language.ENGLISH, Language.GREEK);
//			System.out.println(text); 
//
//		}

	@Override
	public String print() {
		//String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		//return name ;
		return null;
	}

	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}
	
	}

