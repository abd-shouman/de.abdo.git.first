package com.badrit.adaptiveclassifier.main;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import qu.data.CollectionReader;
import twitter4j.TwitterException;
import twitter4j.conf.Configuration;

import com.badrit.adaptiveclassifier.crawler.ClassifierConfiguration;
import com.badrit.adaptiveclassifier.models.Tweet;
import com.badrit.adaptiveclassifier.models.TweetEntry;
import com.badrit.adaptiveclassifier.svmclassifier.SVMClassifierFactory;
import com.badrit.adaptiveclassifier.svmclassifier.SVMModelBuilder;
import com.sun.tools.internal.ws.processor.model.Model;

public class Program {

	/**
	 * Keep track of model version
	 */
	public static int modelVersion;
	public static int initialWaitingTime;
	public static int frequentGapTime;
	public static int fileToRead;
	public static long fileStartTime;
	public static int hoursToProcceed;
	
	public static long startTime;
	
	/**
	 * Start Reading the collection
	 * CollectionReader will then process each twet
	 * 
	 * 
	 * @throws TwitterException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) {
		modelVersion = 0;
		initialWaitingTime = 2;
		frequentGapTime = 1;
		fileToRead = 1;
		hoursToProcceed = 2;
		
		// start reading process
		try {
			String startingFileName = "mh-twt_20140703_10-000";
			
			Calendar startingDate = new GregorianCalendar();
			startingDate.set(Integer.parseInt(startingFileName.substring(7, 11)), Integer.parseInt(startingFileName.substring(11, 13)), Integer.parseInt(startingFileName.substring(13, 15)));
			startingDate.set(Calendar.HOUR,Integer.parseInt(startingFileName.substring(16, 18)));
			
			startTime = System.currentTimeMillis();
			
			CollectionReader.read("sample_tweets" , startingDate);
			//simulateBuildingModel();
			//applyModel();
			System.out.println("Total time: "
					+ (System.currentTimeMillis() - startTime) + " ms\n"
					+ (System.currentTimeMillis()/1000 - startTime/1000) + " s\n"
					+ (System.currentTimeMillis()/60000 - startTime/60000) + " m\n"
					+ (System.currentTimeMillis()/3600000 - startTime/3600000) + " h"
					);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Reading collection could not be completed successfully");
			e.printStackTrace();
		}
	}
	
	/**
	 * Apply model is a function that apply an existing model 
	 * on an existing set of pre-processed tweets for simulation purposes 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void applyModel() throws NumberFormatException, IOException{
		//Set files to read
		Program.fileToRead = 3;
		//Set starting time
		long start = 1404381600000L;
		//Set classifier
		SVMClassifierFactory svmClassifier;
		try {
			svmClassifier = SVMClassifierFactory.getInstance();
			BufferedReader br;
			String entry;
			int i;
			Tweet tempTweet = new Tweet();
			
			i=0;
			
			long classifyingTime;
			long preProcessting;
			/*Tweets that were classified Negative*/
			for(int file=(Program.fileToRead-2); file < Program.fileToRead; file++){
				String booleanClassifierNegativeOutputFile = "boolean_negative_tweets_v" + file + ".txt";
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(booleanClassifierNegativeOutputFile), "UTF8"));
				
				while ((entry = br.readLine()) != null) {
					i++;
					if(i%10 == 0) 
						System.out.println(i + " negative tweets processed so far. Current file is: " + (file));
					if((Long.parseLong(entry.split("\t")[2].replace("\ufeff", ""),10) - startTime)/(3600*1000) > Program.initialWaitingTime)
						continue;
					else{
						preProcessting = System.nanoTime();
						
						tempTweet.setTweet(Long.parseLong( entry.split("\t")[0].replace("\ufeff", "") , 10),
								entry.split("\t")[1], Long.parseLong(entry.split("\t")[2].replace("\ufeff", ""),10));
						
						System.out.println("Pre-processing Time: " + (System.nanoTime() - preProcessting) + " ns " 
								+ (System.currentTimeMillis()/1000 - preProcessting/1000) + " s");
						
						classifyingTime = System.currentTimeMillis();
						svmClassifier.classifyTweet(
								tempTweet, 
								Long.parseLong(entry.split("\t")[2].replace("\ufeff", ""),10));
						
						System.out.println("Classifying Time: " + (System.nanoTime() - classifyingTime) + " ms " 
								+ (System.nanoTime()/1000 - classifyingTime/1000) + " s");
					}
				}
				br.close();
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Error Applying Model");
			e.printStackTrace();
		}
	}
	
	/**
	 * Start to build the model on an existing pre-processed
	 * tweets for simulation purposes
	 */
	private static void simulateBuildingModel(){
		//Set files to read
		Program.fileToRead = 6;
		//Set beginign time
		CollectionReader.trackSimulationStartTime = 1404378698000L;
		//BuildModel
		new SVMModelBuilder(CollectionReader.trackSimulationStartTime);
	}
}