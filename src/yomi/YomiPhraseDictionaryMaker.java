package yomi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class YomiPhraseDictionaryMaker {
	
	public static String numberDigits = "０１２３４５６７８９0123456789";
	public static String alphabetChars = "ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんゔゕゖ゙゚゛゜ゝゞゟァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶヷヸヹヺ・ーヽヾ･ｦｧｨｩｪｫｬｭｮｯｰｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝﾞﾟㇰㇱㇲㇳㇴㇵㇶㇷㇸㇹㇺㇻㇼㇽㇾㇿ";
	
	public boolean countersOnlyMode = false;
	
	public HashMap<String,Yomi> PopulateFromFile(HashMap<String, Yomi> phraseDict, String pFileName){
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(pFileName);
			InputStreamReader isr = new InputStreamReader(fis, "EUC-JP");
			BufferedReader br = new BufferedReader(isr);
					
			
			String sLine;
			String sCurrentPhrase = "";
			
	        for (;;){
	        	sLine = br.readLine();
	        	if(sLine == null){
	        		break;
	        	}
	        	int openBIndex = sLine.indexOf('[');
	        	int closeBIndex = sLine.indexOf(']');
	        	if (openBIndex < 0 || closeBIndex < 0){
	        		continue;
	        	}
	        	String linePhrase = sLine.substring(0, openBIndex-1).trim();
	        	if (linePhrase == ""){
	        		continue;
	        	}
	        	String lineYomi = sLine.substring(openBIndex+1, closeBIndex).trim();
	        	if (lineYomi == ""){
	        		continue;
	        	}
	        	//here we omit all numeric stuff.
	        	if (isAsianNumeric(linePhrase)){
	        		continue;
	        	}
	        	
	        	if (countersOnlyMode){
	        		if (!sLine.toLowerCase().contains("(ctr)")){
	        			continue;
	        		}
	        	}
	        	
	        	//if it is a different phrase, then we reset the values
	        	if (!linePhrase.equals(sCurrentPhrase))
	        	{
	        		sCurrentPhrase = linePhrase;
	        	}
	        	int lineYomiWorth = 2;
	        	if (sLine.contains("(P)")){
	        		lineYomiWorth = 3;
	        	}
	        	if (sLine.toLowerCase().contains("(ok)") || sLine.toLowerCase().contains("(uk)") || sLine.toLowerCase().contains("(arch)")){
	        		lineYomiWorth = 1;
	        	}
	        	
	        	Integer currentYomiWorth = 0;
	        	if (phraseDict.containsKey(linePhrase)){
	        		currentYomiWorth = phraseDict.get(linePhrase).worth;
	        	}
	        	
	        	if (lineYomiWorth > currentYomiWorth){
	        		phraseDict.put(linePhrase, new Yomi(lineYomi, lineYomiWorth));
	        	}
	    		
	        }
	        
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//collect all extra entries at once, and put them in the map later
		HashMap<String, Yomi> extraEntries = new HashMap<String, Yomi>();
		//next step, we go through and for phrase/yomi that end in same hiragana, 
		//we can trim them away for added detection.
		for (HashMap.Entry<String,Yomi> entry : phraseDict.entrySet()){
        	String sPhrase = entry.getKey();
        	Yomi oYomi = entry.getValue();
        	String sYomi = oYomi.yomigata;
        	while( sPhrase.length() > 0 && sYomi.length() > 0 && 
        			(sPhrase.charAt(sPhrase.length()-1) == sYomi.charAt(sYomi.length()-1))
        			){
        		sPhrase = sPhrase.substring(0, sPhrase.length() - 1);
        		sYomi = sYomi.substring(0, sYomi.length() - 1);
        		if (!phraseDict.containsKey(sPhrase) && !extraEntries.containsKey(sPhrase)){
        			if (isAsianNumeric(sPhrase)){
        				break;
        			}
        			extraEntries.put(sPhrase, new Yomi(sYomi, oYomi.worth));
        		}
        	}
		}
		
		//next we can try to trim away stuff like "ken" for city names
		for (HashMap.Entry<String,Yomi> entry : phraseDict.entrySet()){
        	String sPhrase = entry.getKey();
        	Yomi oYomi = entry.getValue();
        	String sYomi = oYomi.yomigata;
        	if (sPhrase.endsWith("県") && sYomi.endsWith("けん")){
        		sPhrase = sPhrase.substring(0, sPhrase.length() - 1);
        		sYomi = sYomi.substring(0, sYomi.length() - 2);
        		if (!phraseDict.containsKey(sPhrase) && !extraEntries.containsKey(sPhrase)){
        			extraEntries.put(sPhrase, new Yomi(sYomi, oYomi.worth));
        		}
        	}
		}
		
		//add all of the extra entries
		for (HashMap.Entry<String,Yomi> entry : extraEntries.entrySet()){
			phraseDict.put(entry.getKey(), entry.getValue());
		}
		
		return phraseDict;
	}
	
	public static boolean isAsianNumeric(String sInput)
	{
		for (char c : sInput.toCharArray()){
			if (numberDigits.indexOf(c) < 0){
				return false;
			}
		}
		return true;
	}
}
