/**
 * 
 */
package eu.ldbc.semanticpublishing.transformations.lexical;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import eu.ldbc.semanticpublishing.transformations.DataValueTransformation;
import eu.ldbc.semanticpublishing.transformations.InvalidTransformation;

public class NameStyleAbbreviation implements DataValueTransformation {
	
//	public static int NDOTS = 0;
//	public static int SCOMMANDOT = 1;
//	public static int ALLDOTS = 2;
	
//	private int format;
	
	private int surnames;
	private Map<String, String> countryAbbreviation;
	/*
	 * surnames is the number of surnames that is foreseen
	 */
	public NameStyleAbbreviation(/*int format,*/ int surnames){
	//	this.format = format;
		this.surnames = surnames;
		countryAbbreviation = new HashMap<String, String>();
		 countryAbbreviation.put("Ascension Island","ac");
		 countryAbbreviation.put("Andorra","ad");
		 countryAbbreviation.put("United Arab Emirates","ae");
		 countryAbbreviation.put("Afghanistan","af");
		 countryAbbreviation.put("Antigua And Barbuda","ag");
		 countryAbbreviation.put("Anguilla","ai");
		 countryAbbreviation.put("Albania","al");
		 countryAbbreviation.put("Armenia","am");
		 countryAbbreviation.put("Netherlands Antilles","an");
		 countryAbbreviation.put("Angola","ao");
		 countryAbbreviation.put("Antarctica","aq");
		 countryAbbreviation.put("Argentina","ar");
		 countryAbbreviation.put("American Samoa","as");
		 countryAbbreviation.put("Austria","at");
		 countryAbbreviation.put("Australia","au");
		 countryAbbreviation.put("Aruba","aw");
		 countryAbbreviation.put("Aland","ax");
		 countryAbbreviation.put("Azerbaijan","az");
		 countryAbbreviation.put("Bosnia And Herzegovina","ba");
		 countryAbbreviation.put("Barbados","bb");
		 countryAbbreviation.put("Belgium","be");
		 countryAbbreviation.put("Bangladesh","bd");
		 countryAbbreviation.put("Burkina Faso","bf");
		 countryAbbreviation.put("Bulgaria","bg");
		 countryAbbreviation.put("Bahrain","bh");
		 countryAbbreviation.put("Burundi","bi");
		 countryAbbreviation.put("Benin","bj");
		 countryAbbreviation.put("Bermuda","bm");
		 countryAbbreviation.put("Brunei Darussalam","bn");
		 countryAbbreviation.put("Bolivia","bo");
		 countryAbbreviation.put("Brazil","br");
		 countryAbbreviation.put("Bahamas","bs");
		 countryAbbreviation.put("Bhutan","bt");
		 countryAbbreviation.put("Bouvet Island","bv");
		 countryAbbreviation.put("Botswana","bw");
		 countryAbbreviation.put("Belarus","by");
		 countryAbbreviation.put("Belize","bz");
		 countryAbbreviation.put("Canada","ca");
		 countryAbbreviation.put("Cocos (Keeling) Islands","cc");
		 countryAbbreviation.put("Congo (Democratic Republic)","cd");
		 countryAbbreviation.put("Central African Republic","cf");
		 countryAbbreviation.put("Congo (Republic)","cg");
		 countryAbbreviation.put("Switzerland","ch");
		 countryAbbreviation.put("Cook Islands","ck");
		 countryAbbreviation.put("Chile","cl");
		 countryAbbreviation.put("Cameroon","cm");
		 countryAbbreviation.put("Colombia","co");
		 countryAbbreviation.put("Costa Rica","cr");
		 countryAbbreviation.put("Cuba","cu");
		countryAbbreviation.put("Cape Verde","cv");
		countryAbbreviation.put("Christmas Island","cx");
		countryAbbreviation.put("Cyprus","cy");
		countryAbbreviation.put("Czech Republic","cz");
		countryAbbreviation.put("Germany","de");
		countryAbbreviation.put("Djibouti","dj");
		countryAbbreviation.put("Denmark","dk");
		countryAbbreviation.put("Dominica","dm");
		countryAbbreviation.put("Dominican Republic","do");
		countryAbbreviation.put("Algeria","dz");
		countryAbbreviation.put("Ecuador","ec");
		countryAbbreviation.put("Estonia","ee");
		countryAbbreviation.put("Egypt","eg");
		countryAbbreviation.put("Eritrea","er");
		countryAbbreviation.put("Spain","es");
		countryAbbreviation.put("Ethiopia","et");
		countryAbbreviation.put("European Union","eU");
		countryAbbreviation.put("Finland","fi");
		countryAbbreviation.put("Fiji","fj");
		countryAbbreviation.put("Falkland Islands (Malvinas)","fk");
		countryAbbreviation.put("Micronesia, Federated States Of","fm");
		countryAbbreviation.put("Faroe Islands","fo");
		countryAbbreviation.put("France","fr");
		countryAbbreviation.put("Gabon","ga");
		countryAbbreviation.put("United Kingdom","gb");
		countryAbbreviation.put("Grenada","gd");
		countryAbbreviation.put("Georgia","ge");
		countryAbbreviation.put("French Guiana","gf");
		countryAbbreviation.put("Guernsey","gg");
		countryAbbreviation.put("Ghana","gh");
		countryAbbreviation.put("Gibraltar","gi");
		countryAbbreviation.put("Greenland","gl");
		countryAbbreviation.put("Gambia","gm");
		countryAbbreviation.put("Guinea","gn");
		countryAbbreviation.put("Guadeloupe","gp");
		countryAbbreviation.put("Equatorial Guinea","gq");
		countryAbbreviation.put("Greece","gr");
		countryAbbreviation.put("South Georgia And The South Sandwich Islands","gs");
		countryAbbreviation.put("Guatemala","gt");
		countryAbbreviation.put("Guam","gu");
		countryAbbreviation.put("Guinea-Bissau","gw");
		countryAbbreviation.put("Guyana","gy");
		countryAbbreviation.put("Hong Kong","hk");
		countryAbbreviation.put("Heard And Mc Donald Islands","hm");
		countryAbbreviation.put("Honduras","hn");
		countryAbbreviation.put("Croatia (local name: Hrvatska)","hr");
		countryAbbreviation.put("Haiti","ht");
		countryAbbreviation.put("Hungary","hu");
		countryAbbreviation.put("Indonesia","id");
		countryAbbreviation.put("Ireland","ie");
		countryAbbreviation.put("Israel","il");
		countryAbbreviation.put("Isle of Man","im");
		countryAbbreviation.put("India","in");
		countryAbbreviation.put("British Indian Ocean Territory","io");
		countryAbbreviation.put("Iraq","iq");
		countryAbbreviation.put("Iran (Islamic Republic Of)","ir");
		countryAbbreviation.put("Iceland","is");
		countryAbbreviation.put("Italy","it");
		countryAbbreviation.put("Jersey","je");
		countryAbbreviation.put("Jamaica","jm");
		countryAbbreviation.put("Jordan","jo");
		countryAbbreviation.put("Japan","jp");
		countryAbbreviation.put("Kenya","ke");
		countryAbbreviation.put("Kyrgyzstan","kg");
		countryAbbreviation.put("Cambodia","kh");
		countryAbbreviation.put("Kiribati","ki");
		countryAbbreviation.put("Comoros","km");
		countryAbbreviation.put("Saint Kitts And Nevis","kn");
		countryAbbreviation.put("Korea, Republic Of","kr");
		countryAbbreviation.put("Kuwait","kw");
		countryAbbreviation.put("Cayman Islands","ky");
		countryAbbreviation.put("Kazakhstan","kz");
		countryAbbreviation.put("Lebanon","lb");
		countryAbbreviation.put("Saint Lucia","lc");
		countryAbbreviation.put("Liechtenstein","li");
		countryAbbreviation.put("Sri Lanka","lk");
		countryAbbreviation.put("Liberia","lr");
		countryAbbreviation.put("Lesotho","ls");
		countryAbbreviation.put("Lithuania","lt");
		countryAbbreviation.put("Luxembourg","lu");
		countryAbbreviation.put("Latvia","lv");
		countryAbbreviation.put("Libyan Arab Jamahiriya","ly");
		countryAbbreviation.put("Morocco","ma");
		countryAbbreviation.put("Monaco","mc");
		countryAbbreviation.put("Moldova, Republic Of","md");
		countryAbbreviation.put("Montenegro","me");
		countryAbbreviation.put("Madagascar","mg");
		countryAbbreviation.put("Marshall Islands","mh");
		countryAbbreviation.put("Macedonia, The Former Yugoslav Republic Of","mk");
		countryAbbreviation.put("Mali","ml");
		countryAbbreviation.put("Myanmar","mm");
		countryAbbreviation.put("Mongolia","mn");
		countryAbbreviation.put("Macau","mo");
		countryAbbreviation.put("Northern Mariana Islands","mp");
		countryAbbreviation.put("Martinique","mq");
		countryAbbreviation.put("Mauritania","mr");
		countryAbbreviation.put("Montserrat","ms");
		countryAbbreviation.put("Malta","mt");
		countryAbbreviation.put("Mauritius","mu");
		countryAbbreviation.put("Maldives","mv");
		countryAbbreviation.put("Malawi","mw");
		countryAbbreviation.put("Mexico","mx");
		countryAbbreviation.put("Malaysia","my");
		countryAbbreviation.put("Mozambique","mz");
		countryAbbreviation.put("Namibia","na");
		countryAbbreviation.put("New Caledonia","nc");
		countryAbbreviation.put("Niger","ne");
		countryAbbreviation.put("Norfolk Island","nf");
		countryAbbreviation.put("Nigeria","ng");
		countryAbbreviation.put("Nicaragua","ni");
		countryAbbreviation.put("Netherlands","nl");
		countryAbbreviation.put("Norway","no");
		countryAbbreviation.put("Nepal","np");
		countryAbbreviation.put("Nauru","nr");
		countryAbbreviation.put("Niue","nu");
		countryAbbreviation.put("New Zealand","nz");
		countryAbbreviation.put("Oman","om");
		countryAbbreviation.put("Panama","pa");
		countryAbbreviation.put("Peru","pe");
		countryAbbreviation.put("French Polynesia","pf");
		countryAbbreviation.put("Papua New Guinea","pg");
		countryAbbreviation.put("Philippines, Republic of the","ph");
		countryAbbreviation.put("Pakistan","pk");
		countryAbbreviation.put("Poland","pl");
		countryAbbreviation.put("St. Pierre And Miquelon","pm");
		countryAbbreviation.put("Pitcairn","pn");
		countryAbbreviation.put("Puerto Rico","pr");
		countryAbbreviation.put("Palestine","ps");
		countryAbbreviation.put("Portugal","pt");
		countryAbbreviation.put("Palau","pw");
		countryAbbreviation.put("Paraguay","py");
		countryAbbreviation.put("Qatar","qa");
		countryAbbreviation.put("Reunion","re");
		countryAbbreviation.put("Romania","ro");
		countryAbbreviation.put("Serbia","rs");
		countryAbbreviation.put("Russian Federation","ru");
		countryAbbreviation.put("Rwanda","rw");
		countryAbbreviation.put("Saudi Arabia","sa");
		countryAbbreviation.put("Scotland","uk");
		countryAbbreviation.put("Solomon Islands","sb");
		countryAbbreviation.put("Seychelles","sc");
		countryAbbreviation.put("Sudan","sd");
		countryAbbreviation.put("Sweden","se");
		countryAbbreviation.put("Singapore","sg");
		countryAbbreviation.put("St. Helena","sh");
		countryAbbreviation.put("Slovenia","si");
		countryAbbreviation.put("Svalbard And Jan Mayen Islands","sj");
		countryAbbreviation.put("Slovakia (Slovak Republic)","sk");
		countryAbbreviation.put("Sierra Leone","sl");
		countryAbbreviation.put("San Marino","sm");
		countryAbbreviation.put("Senegal","sn");
		countryAbbreviation.put("Somalia","so");
		countryAbbreviation.put("Suriname","sr");
		countryAbbreviation.put("Sao Tome And Principe","st");
		countryAbbreviation.put("Soviet Union","su");
		countryAbbreviation.put("El Salvador","sv");
		countryAbbreviation.put("Syrian Arab Republic","sy");
		countryAbbreviation.put("Swaziland","sz");
		countryAbbreviation.put("Turks And Caicos Islands","tc");
		countryAbbreviation.put("Chad","td");
		countryAbbreviation.put("French Southern Territories","tf");
		countryAbbreviation.put("Togo","tg");
		countryAbbreviation.put("Thailand","th");
		countryAbbreviation.put("Tajikistan","tj");
		countryAbbreviation.put("Tokelau","tk");
		countryAbbreviation.put("East Timor","ti");
		countryAbbreviation.put("Turkmenistan","tm");
		countryAbbreviation.put("Tunisia","tn");
		countryAbbreviation.put("Tonga","to");
		countryAbbreviation.put("East Timor","tp");
		countryAbbreviation.put("Turkey","tr");
		countryAbbreviation.put("Trinidad And Tobago","tt");
		countryAbbreviation.put("Tuvalu","tv");
		countryAbbreviation.put("Taiwan","tw");
		countryAbbreviation.put("Tanzania, United Republic Of","tz");
		countryAbbreviation.put("Ukraine","ua");
		countryAbbreviation.put("Uganda","ug");
		countryAbbreviation.put("United Kingdom","uk");
		countryAbbreviation.put("United States Minor Outlying Islands","um");
		countryAbbreviation.put("United States","us");
		countryAbbreviation.put("Uruguay","uy");
		countryAbbreviation.put("Uzbekistan","uz");
		countryAbbreviation.put("Vatican City State (Holy See)","va");
		countryAbbreviation.put("Saint Vincent And The Grenadines","vc");
		countryAbbreviation.put("Venezuela","ve");
		countryAbbreviation.put("Virgin Islands (British)","vg");
		countryAbbreviation.put("Virgin Islands (U.S.)","vi");
		countryAbbreviation.put("Viet Nam","vn");
		countryAbbreviation.put("Vanuatu","vu");
		countryAbbreviation.put("Wallis And Futuna Islands","wf");
		countryAbbreviation.put("Samoa","ws");
		countryAbbreviation.put("Yemen","ye");
		countryAbbreviation.put("Mayotte","yt");
		countryAbbreviation.put("South Africa","za");
		countryAbbreviation.put("Zambia","zm");
	}

	public String print(){
		String name = this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1);
		return name + "\t" + this.surnames /*+ "\t" + this.format*/;
	}

	@SuppressWarnings("finally")
	@Override
	public Object execute(Object arg) {
		String newS = "";
		String s = (String)arg;
		if(arg instanceof String){
			String[] tokens = s.split(" ");
			for(int i = 0; i < tokens.length; i++){
				if(countryAbbreviation.containsKey(tokens[i].toLowerCase())){
					newS += countryAbbreviation.get(tokens[i].toLowerCase());
					newS += " ";
		    	}
				else{
					newS += tokens[i];
					newS += " ";
				}
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
		return newS;
	}
	
	@Override
	public Model executeStatement(Statement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}
