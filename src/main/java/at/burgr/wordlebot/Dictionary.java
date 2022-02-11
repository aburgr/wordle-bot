package at.burgr.wordlebot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
public class Dictionary {

	@Autowired
	private Environment environment;

	@Autowired
	private ResourceLoader resourceLoader;
	
	private Set<String> words = null;
	private Random random = new Random();

	private void init() throws IOException	{
		int wordLength = Integer.parseInt(environment.getProperty("word_length"));
		words = new HashSet<>();
		
		final Resource fileResource = resourceLoader.getResource("classpath:" + environment.getProperty("words_file"));
		try (BufferedReader br = new BufferedReader(new FileReader(fileResource.getFile()))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if(line.length() == wordLength)	{
		    	   words.add(line.toUpperCase());
		       }
		    }
		}
		
	}

	public String getNewWord() throws IOException	{
		if(words == null)	{
			init();
		}
		return new ArrayList<>(words).get(random.nextInt(words.size()));
	}

	public Set<String> getAllWords() throws IOException {
		if(words == null)	{
			init();
		}
		return words;
	}
}
