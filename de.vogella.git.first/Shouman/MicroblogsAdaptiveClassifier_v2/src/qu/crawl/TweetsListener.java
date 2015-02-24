//package qu.crawl;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import qu.data.ObjectWriter;
//import qu.data.User;
//
//import qu.data.Tweet;
//import twitter4j.StallWarning;
//import twitter4j.Status;
//import twitter4j.StatusDeletionNotice;
//import twitter4j.StatusListener;
//
//public class TweetsListener implements StatusListener {
//	//Initialize counts of different types of tweets
//	long epoch, pastEpoch, arCount = 0, totalCount = 0, spamCount = 0;
//	long tweetId = 0, lastIdToFile = 0, lastIdInOrder = 0;
//
//	int hour, pastHour, index = 0;
//	int fileCount = 0;
//	int maxTweetsPerFile = 10000;
//
//	String tweetsFile, usersFile, crawlerId, date, format, tweetsOutputPath, 
//	usersOutputPath, tweetText;
//
//	String fileCountFormatted = String.format("%03d", fileCount);
//
//	boolean track;
//
//	Tweet tweet = null, tempTweet = null;
//	User user = null;
//
//	ObjectWriter tweetWriter = null, userWriter = null;
//	List<Object> tweetsToFile = new ArrayList<Object>();
//	List<Object> usersToFile = new ArrayList<Object>();
//	//List<String> spamFilteringList = new ArrayList<String>();
//
//	public TweetsListener(int maxTweetsPerFile, boolean track, String tweetsOutputPath, String usersOutputPath, String format, String crawlerId, List<String> spamFilteringList) {
//		this.track = track;
//		this.format = format;
//		//this.spamFilteringList = spamFilteringList;
//		this.crawlerId = crawlerId;
//		this.tweetsOutputPath = tweetsOutputPath;
//		this.usersOutputPath = usersOutputPath;
//		this.maxTweetsPerFile = maxTweetsPerFile;
//
//		pastEpoch = System.currentTimeMillis();
//		pastHour = Integer.parseInt(TweetsCrawler.returnUTCDate(new Date(pastEpoch), "HH"));
//
//		date = TweetsCrawler.returnUTCDate(new Date(pastEpoch), format);
//		tweetsFile = tweetsOutputPath+crawlerId+"-twt_"+date+"-"+fileCountFormatted+".ser";
//		usersFile = usersOutputPath+crawlerId+"-usr_"+date+"-"+fileCountFormatted+".ser";
//
//		tweetWriter = new ObjectWriter(tweetsFile);
//		userWriter = new ObjectWriter(usersFile);
//	}
//
//	@Override
//	public void onStatus(Status status) {
//		totalCount++;
//
//		if(track)
//			writeTweetAndUser(status);
//
//		//To sample Arabic tweets only
//		else if (status.getIsoLanguageCode().equals("ar"))
//			writeTweetAndUser(status);
//	}
//
//	public void writeTweetAndUser(Status status){
//		arCount++;
//		try {
//			tweet = new Tweet(status);
//			user = new User(status);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		tweetText = tweet.getTweetText().replaceAll("\\r\\n|\\r|\\n", " ");
//		tweetId = tweet.getTweetId();
//
//		/*//Check if a tweet is spam or not
//		for (String term : spamFilteringList) 
//			if (tweetText.contains(term)) {
//				spamCount++;
//				return;
//			}*/
//		
//		epoch = tweet.getEpoch();
//		hour = Integer.parseInt(TweetsCrawler.returnUTCDate(new Date(epoch), "HH"));
//
//		/*To store tweets in a fixed time duration (hour). Within each hour Split tweets 
//		 * into files each with maxTweetsPerFile tweets*/
//		if(hour-pastHour >= 1 || tweetsToFile.size() == maxTweetsPerFile) {
//			try {
//				//Update the id of last tweet written to latest file
//				if(tweetsToFile.size()!=0)
//					lastIdToFile = ((Tweet)tweetsToFile.get(tweetsToFile.size() - 1)).getTweetId();
//
//				//Write list of tweets and users to their corresponding files
//				tweetWriter.writeList(tweetsToFile);
//				tweetWriter.close();
//
//				userWriter.writeList(usersToFile);
//				userWriter.close();
//			} 
//			catch (IOException e1) {
//				try {
//					tweetWriter.close();
//					userWriter.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				e1.printStackTrace();
//			}
//
//			//Reset counter each hour
//			if(hour-pastHour >= 1)
//				fileCount = 0;
//			else
//				fileCount++;
//
//			//Print statistics on current counts
//			System.out.println("Total tweets received so far: " + totalCount);
//			System.out.println("Total Arabic tweets received so far: " + arCount);
//			//System.out.println("Spam tweets so far: " + spamCount);
//
//			//Reached maxNoTweetsPerFile. Write to a new file.
//			fileCountFormatted = String.format("%03d", fileCount);
//
//			date = TweetsCrawler.returnUTCDate(new Date(epoch), format);
//			tweetsFile = tweetsOutputPath+crawlerId+"-twt_"+date+"-"+fileCountFormatted+".ser";
//			usersFile = usersOutputPath+crawlerId+"-usr_"+date+"-"+fileCountFormatted+".ser";
//
//			tweetWriter = new ObjectWriter(tweetsFile);
//			userWriter = new ObjectWriter (usersFile);
//
//			//Clear tweets and users lists
//			tweetsToFile.clear();
//			usersToFile.clear();
//
//			//Update time reference point
//			pastEpoch = epoch;
//			pastHour = hour;
//		}
//		
//		tweetsToFile.add(tweet);
//		usersToFile.add(user);
//		
//		/*Store temporally ordered, non duplicate tweets only. Don't write tweets 
//		 * to file if they are older than last tweet written to the latest file*/
//		/*if (tweetId <= lastIdToFile)
//			return;
//
//		//If first tweet in list or tweet is in-order. Add it to list. 
//		if(tweetsToFile.size()== 0 || tweetId > lastIdInOrder) {
//			lastIdInOrder = tweetId;
//
//			tweetsToFile.add(tweet);
//			usersToFile.add(user);
//			return;
//		}
//
//		//Found out-of-order tweet. Check this to ensure elimination of duplicates
//		if(tweetId < lastIdInOrder){
//
//			//Find insertion point to add tweet to
//			for(int j=tweetsToFile.size()-1; j>-1 ; j--){
//				tempTweet = (Tweet)tweetsToFile.get(j);
//
//				if(tweetId > tempTweet.getTweetId()){
//					index  = tweetsToFile.indexOf(tempTweet)+1;
//					break;
//				}
//				else if (j==0)
//					index = 0;
//			}
//			tweetsToFile.add(index, tweet);
//			usersToFile.add(index, user);
//		}*/
//	}
//
//	@Override
//	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//		//Good to know deleted tweets. Overwhelming to keep printing this message
//		// System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
//	}
//
//	@Override
//	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//		//System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
//	}
//
//	@Override
//	public void onScrubGeo(long userId, long upToStatusId) {
//		System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
//	}
//
//	@Override
//	public void onStallWarning(StallWarning warning) {
//		System.out.println("Got stall warning:" + warning);
//	}
//
//	@Override
//	public void onException(Exception ex) {
//		ex.printStackTrace();
//	}
//}