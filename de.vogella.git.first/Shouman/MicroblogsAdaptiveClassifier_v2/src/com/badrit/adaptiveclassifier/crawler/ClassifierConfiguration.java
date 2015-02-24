package com.badrit.adaptiveclassifier.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.badrit.adaptiveclassifier.main.Program;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ClassifierConfiguration {

	public int windowsSize;
	public String streamLang;
	public Configuration twitterStreamConfiguration;
	private String twitterConsumerKey;
	private String twitterConsumerSecret;
	private String twitterAccessToken;
	private String twitterAccessTokenSecret;

	public static final String ADAPTIVE_CLASSIFIER_TOPIC_QUERIES_FILE = "resources//topic_queries.txt";
	public static final String ADAPTIVE_CLASSIFIER_RANDOM_QUERIES_FILE = "resources//random_queries.txt";
	public static final String ADAPTIVE_CLASSIFIER_EXTENDED_QUERIES_FILE = "resources//extended_queries.txt";
	
	/*
	public static final String BOOLEAN_CLASSIFIER_POSITIVE_OUTPUT_FILE = "boolean_positive_tweets.txt";
	public static final String BOOLEAN_CLASSIFIER_NEGATIVE_OUTPUT_FILE = "boolean_negative_tweets.txt";
	*/
	
	public static final String SVM_CLASSIFIER_POSITIVE_OUTPUT_FILE = "svm_positive_tweets.txt";
	public static final String SVM_CLASSIFIER_NEGATIVE_OUTPUT_FILE = "svm_negative_tweets.txt";
	
	private static final String ADAPTIVE_CLASSIFIER_CONFIG_FILE = "resources//classifier.config";
	
	private final String CLASSIFIER_WINDOW = "classifier.window";
	private final String TWITTER_STREAM_LANG = "query.lang";
	
	
	public ArrayList<Configuration> crawlersConfigurationList;
	
	public String[] topicQueries;
	public String[] randomQueries;


	private static ClassifierConfiguration configurationInstance;

	private ClassifierConfiguration() throws IOException {
		configure();
	}

	public static ClassifierConfiguration getInstance() throws IOException {
		if (configurationInstance == null)
			configurationInstance = new ClassifierConfiguration();
		return configurationInstance;
	}

	private void configure() throws IOException {

		// Read the properties
		Properties configurationProperties = new Properties();
		FileInputStream fileInputStream = new FileInputStream(ADAPTIVE_CLASSIFIER_CONFIG_FILE);
		configurationProperties.load(fileInputStream);

		// Extract classifier parameters
		this.windowsSize = Integer.parseInt(configurationProperties.getProperty(CLASSIFIER_WINDOW, "24"));
		this.streamLang = configurationProperties.getProperty(TWITTER_STREAM_LANG, null);


		// Read predefined topic queries
		topicQueries = readTopicQueries();
		randomQueries = readRandomQueries();
	}

	private String[] readTopicQueries() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
				ADAPTIVE_CLASSIFIER_TOPIC_QUERIES_FILE), "UTF8"));
		String q;
		ArrayList<String> queries = new ArrayList<String>();
		while ((q = br.readLine()) != null)
			queries.add(q);
		br.close();
		
		String[] queriesArr = new String[queries.size()];
		for (int i = 0; i < queriesArr.length; i++) {
			queriesArr[i] = queries.get(i);
		}
		return queriesArr;
	}

	private String[] readRandomQueries() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
				ADAPTIVE_CLASSIFIER_RANDOM_QUERIES_FILE), "UTF8"));
		String q;
		ArrayList<String> queries = new ArrayList<String>();
		while ((q = br.readLine()) != null)
			queries.add(q);
		br.close();
		
		String[] queriesArr = new String[queries.size()];
		for (int i = 0; i < queriesArr.length; i++) {
			queriesArr[i] = queries.get(i);
		}
		return queriesArr;
	}
	public void printConfig() {
		System.out.println("windowsSize: " + windowsSize);
		System.out.println("streamLang: " + streamLang);
		System.out.println("twitterConsumerKey: " + twitterConsumerKey);
		System.out.println("twitterConsumerSecret: " + twitterConsumerSecret);
		System.out.println("twitterAccessToken: " + twitterAccessToken);
		System.out.println("twitterAccessTokenSecret: " + twitterAccessTokenSecret);
		System.out.println("Queries: ");
		for (String q : topicQueries)
			System.out.println("\t" + q);
	}
	
	/**
	 * Split input string on one or more spaces
	 * 
	 * @param strDataItem
	 *            input string
	 * @return List of token in input string
	 */
	private List<String> splitStringOnSpace(String strDataItem) {
		return Arrays.asList(strDataItem.trim().split("\\s+"));
	}
	
	/**
	 * Load resource file in array of string, each new line in a separate string
	 * 
	 * @param strResourceFileName
	 *            Resource file name
	 * @return list of strings, containing the data in the input file, each new
	 *         line in a separate string
	 * @throws IOException
	 */
	private ArrayList<String> loadResourceFile(String strResourceFileName) throws IOException {
		ArrayList<String> lstResourceFileData = new ArrayList<String>();
		BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(strResourceFileName)));

		String strLine = null;
		while ((strLine = objBufferedReader.readLine()) != null) {
			if (strLine.trim().length() > 0 && !strLine.startsWith("#")) {
				lstResourceFileData.add(strLine);
			}
		}

		objBufferedReader.close();
		return lstResourceFileData;
	}
	
	public static String get_BOOLEAN_CLASSIFIER_POSITIVE_Output(){
		return "boolean_positive_tweets_v"+ Program.fileToRead + ".txt";
	}
	
	public static String get_BOOLEAN_CLASSIFIER_NEGATIVE_Output(){
		return "boolean_negative_tweets_v"+ Program.fileToRead + ".txt";
	}
}
