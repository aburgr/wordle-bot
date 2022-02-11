package at.burgr.wordlebot;

import java.util.ArrayList;
import java.util.Set;

public class GameProgress {

	private int attempts;
	private final int maxAttempts;
	private final String word;
	private final ArrayList<String> history = new ArrayList<>();
	private boolean success = false;
	
	public GameProgress(String word, int maxAttempts)	{
		this.word = word; 
		this.maxAttempts = maxAttempts;
	}
	
	public String match(Set<String> dictionary, String word, String attempt)	{
		attempt = attempt.toUpperCase();
		
		if(!attempt.matches("^[a-zA-Z]*$"))	{
			return "Ich mag nur Buchstaben.";
		}
		if(word.length() != attempt.length())	{
			return "Das Wort ist zu lang oder zu kurz.";
		}
		if(!dictionary.contains(attempt))	{
			return "Ich kenne dieses Wort nicht.";
		}
		
		attempts++;
		
		int matches = 0;		
		String result = "";
		
		for(int i = 0; i < attempt.length(); i++)	{
			char charToCheck = attempt.charAt(i);
			if(word.charAt(i) == charToCheck)	{
				result = result + "🟩";
				matches++;
			} else	{
				if(word.contains(String.valueOf(charToCheck)))	{
					result = result + "🟨";
				} else	{
					result = result + "⬛";
				}
			}
		}

		history.add(result);
		if(matches == word.length())	{
			success = true;
		}
		
		return String.format("%s %d/%d", result, attempts, maxAttempts);
	}
	
	public int getAttempts()	{
		return attempts;
	}
	
	public String getWord()	{
		return word;
	}

	public boolean isSuccess()	{
		return success;
	}
	
	public String getHistory() {
		StringBuilder result = new StringBuilder();
		for(String h : history)	{
			result.append(h).append("\n");
		}
		return result.toString();
	}

	public Object getMaxAttempts() {
		return maxAttempts;
	}
}
