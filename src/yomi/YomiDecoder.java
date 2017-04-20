package yomi;

import java.util.HashMap;
import java.util.ArrayList;

public class YomiDecoder {
	
	public static HashMap<String, Yomi> phraseDict = null;
	public static HashMap<String, Yomi> counterDict = null;
	public static Object dictLock = new Object();
	
	public static int YOMI_MAX_CHAR_LENGTH = 300;
	
	public boolean isColored = false;
	public static String[] colorList = { "#339900" , "#0044cc"};
	
	public void MakeDictionary(){
		if (phraseDict == null){
			synchronized(dictLock){
				if (phraseDict == null){
					phraseDict = new HashMap<String, Yomi>();
					counterDict = new HashMap<String, Yomi>();
					
					YomiPhraseDictionaryMaker YomiMaker = new YomiPhraseDictionaryMaker();
					YomiMaker.PopulateFromFile(phraseDict, "c:\\YOMI\\edict.gz");
					YomiMaker.PopulateFromFile(phraseDict, "c:\\YOMI\\enamdict.gz");
					
					YomiMaker.countersOnlyMode = true;
					YomiMaker.PopulateFromFile(counterDict, "c:\\YOMI\\edict.gz");
				}
			}
		}
	}

	public String GetYomi(String sInput)
	{
		//oh well ensure it is created..
		MakeDictionary();
		
		if (sInput.length() > YOMI_MAX_CHAR_LENGTH)
		{
			sInput = sInput.substring(0, YOMI_MAX_CHAR_LENGTH);
		}

		return GetAdvancedYomi(sInput);
	}
	
	public static int KanjiCount(String sInput){
		int result = 0;
		
		for (char c : sInput.toCharArray()){
			if (YomiPhraseDictionaryMaker.alphabetChars.indexOf(c) < 0){
				result += 1;
			}
		}
		
		return result;
	}
	
	public String GetAdvancedYomi(String sInput) {
		ArrayList<String> indexYomi = new ArrayList<String>();
		ArrayList<Double> indexWorth = new ArrayList<Double>();
		ArrayList<Integer> indexColorIndex = new ArrayList<Integer>();
		
		indexYomi.add("");
		indexWorth.add(0.0);
		indexColorIndex.add(0);
		
		for (int n = 0; n < sInput.length(); n++){
			indexYomi.add("");
			indexWorth.add(-1000.0);
			indexColorIndex.add(0);
		}
		
		String remainingInput = sInput;
		for (int n = 1; n < indexYomi.size(); n++){

			String previousYomi = indexYomi.get(n-1);
			Double previousWorth = indexWorth.get(n-1);
			Integer previousColorIndex = indexColorIndex.get(n-1);
			
			String remainingCumulative = "";
			//iterate each substring of sInput to see if it was a valid thing.
			for (int m = 0; m < remainingInput.length(); m++){
				char c = remainingInput.charAt(m);
				remainingCumulative += c;
				
				String nextYomi = previousYomi;
				Double nextWorth = previousWorth;
				Integer nextColorIndex = previousColorIndex;
				boolean endsWithNumber = false;
				if (previousYomi.length() >= 1 && YomiPhraseDictionaryMaker.isAsianNumeric(previousYomi.substring(previousYomi.length()-1,previousYomi.length()))){
					endsWithNumber = true;
				}
				
				
				Double value = 0.0;
				String yomiOut = "";
				boolean hasMatch = false;
				String matchYomi = "";
				double pointMultiplier = 1.0;
				//match with counters first if previous was a number
				if (endsWithNumber){
					if (!hasMatch && counterDict.containsKey(remainingCumulative)){
						hasMatch = true;
						matchYomi = counterDict.get(remainingCumulative).yomigata;
						pointMultiplier = 2.0;
					}
				}
				//match the phrase dictionary
				if (!hasMatch && phraseDict.containsKey(remainingCumulative)){
					hasMatch = true;
					matchYomi = phraseDict.get(remainingCumulative).yomigata;
				}
				
				if(hasMatch){
					value = Math.pow( KanjiCount(remainingCumulative), 2.5) * pointMultiplier + Math.pow(remainingCumulative.length(), 1.5);
					if (isColored){
						yomiOut	= "<span style='font-weight:bold; color:" + colorList[nextColorIndex] + "'>" + matchYomi + "</span>";
					}else{
						yomiOut	= matchYomi;
					}
					nextColorIndex = (nextColorIndex + 1) % colorList.length;
				}else{
					value = -(1.0 + KanjiCount(remainingCumulative)*2);
					yomiOut = remainingCumulative;
				}
				
				nextWorth += value;
				nextYomi += yomiOut;
				
				int nextIndex = n + m;
				if (indexWorth.get(nextIndex) < nextWorth){
					indexYomi.set(nextIndex, nextYomi);
					indexWorth.set(nextIndex, nextWorth);
					indexColorIndex.set(nextIndex, nextColorIndex);
				}
			}
				
			remainingInput = remainingInput.substring(1);

		}
		
		return indexYomi.get(indexYomi.size()-1);
	}
	
}
