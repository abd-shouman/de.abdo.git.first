package qu.tweetprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.ParseException;
import java.util.Iterator;

import qu.data.CollectionReader;

import com.badrit.adaptiveclassifier.booleanclassifier.BooleanClassifier;
import com.badrit.adaptiveclassifier.crawler.ClassifierConfiguration;
import com.badrit.adaptiveclassifier.main.Program;
import com.badrit.adaptiveclassifier.models.Tweet;
import com.badrit.adaptiveclassifier.models.TweetEntry;
import com.badrit.adaptiveclassifier.svmclassifier.SVMClassifierFactory;
import com.badrit.adaptiveclassifier.svmclassifier.SVMModelBuilder;

public class ProcessTweet {
	
	String[] topicQueries;
	String[] randomQueries;
	BooleanClassifier booleanClassifier;
	SVMClassifierFactory svmClassifier;
	static int waitingGap; 
	static boolean firstModelCreated;
	
	public ProcessTweet() throws IOException, ParseException{
		booleanClassifier = BooleanClassifier.getInstance();
		svmClassifier = SVMClassifierFactory.getInstance();
		ClassifierConfiguration classifierConfig = ClassifierConfiguration
				.getInstance();
		
		waitingGap = Program.initialWaitingTime;
		firstModelCreated = false;
	}
	
	public void applyClassiferSpecialCase(long startTime) throws IOException{
		/*Read tweets in the previous window of 20 hours and apply the SVM classifier*/
		BufferedReader br;
		String entry;
		Tweet tempTweet = new Tweet();
		int i =0;
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
		
	}
	
	private void updateFileToWrite(long startTime) {
		try {
			Program.fileToRead ++;
			booleanClassifier.setOutputFile();
			Program.fileStartTime = startTime;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Error: Could not update the reading file");
			e.printStackTrace();
		}
	}
	
	public boolean filter(Tweet tweet) throws IOException, ParseException{
		long timeSinceBegingingOfSimulationTrack = (tweet.epoch - CollectionReader.trackSimulationStartTime)/(3600*1000);
		long timeSinceBegingingWriteOfFile = (tweet.epoch - Program.fileStartTime)/(3600*1000);
		
		if((int)timeSinceBegingingOfSimulationTrack >= waitingGap){
			
			System.out.println("Total Time for 20H: " +  ((System.currentTimeMillis()/60000) - Program.startTime/60000 ));
			
			updateFileToWrite(tweet.epoch);

			//BuildModel
			new SVMModelBuilder(CollectionReader.trackSimulationStartTime);
			
			System.out.println("Total Time for Building Model: " +  ((System.currentTimeMillis()/60000) - Program.startTime/60000 ));
			
			svmClassifier = null;
			svmClassifier = SVMClassifierFactory.getInstance();
			
//			if(waitingGap == Program.initialWaitingTime) {
//				long startTime = System.currentTimeMillis();
//				applyClassiferSpecialCase(CollectionReader.trackSimulationStartTime);
//				System.out.println("Total time for appling special case: \n"
//						+ (System.currentTimeMillis() - startTime) + " ms\n"
//						+ (System.currentTimeMillis()/1000 - startTime/1000) + " s\n"
//						+ (System.currentTimeMillis()/60000 - startTime/60000) + " m\n"
//						+ (System.currentTimeMillis()/3600000 - startTime/3600000) + " h"
//						);
//				
//			}

			//Update waiting gap for building another model
			waitingGap = Program.frequentGapTime;
			firstModelCreated = true;
			
			CollectionReader.trackSimulationStartTime = tweet.epoch;
			Program.modelVersion ++;
			Program.startTime = System.currentTimeMillis();
			
			return false;
		}
		else if((int)timeSinceBegingingWriteOfFile >= Program.frequentGapTime){
			updateFileToWrite(tweet.epoch);
		}
		
		if(svmClassifier.model != null){
			svmClassifier.classifyTweet(tweet, tweet.epoch);
		}else {
			booleanClassifier.classifyTweet(tweet);
		}
		return true;
	}
}