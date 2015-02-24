//package qu.crawl;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.TimeZone;
//
//import twitter4j.FilterQuery;
//import twitter4j.StatusListener;
//import twitter4j.TwitterStream;
//import twitter4j.TwitterStreamFactory;
//import twitter4j.conf.ConfigurationBuilder;
//
//public class TweetsCrawler {
//
//	//Can be of any size
//	public static String spamFilteringTermsFile = "";
//	//Supposed to be 400 lines only
//	public static String topTermsFile = "";
//
//	//Paths to store tweets and users collections
//	public static String tweetsOutputPath = "";
//	public static String usersOutputPath = "";
//
//	//Maximum number of tweets a tweets file can have
//	public static int maxTweetsPerFile;
//
//	//To distinguish between files crawled by different machines. 
//	public static String crawlerId = "";
//
//	//Use sample() by default. Track top terms if set to true
//	public static boolean track = false;
//
//	//To start crawling tweets
//	public static void crawlTweets() throws IOException {
//		String format = "yyyyMMdd_HH";
//		List<String> filteringList = new ArrayList<String>();
//		List<String> trackedTermsList = new ArrayList<String>();
//
//		try {
//			//filteringList = readFile(spamFilteringTermsFile);
//			trackedTermsList = readFile(topTermsFile);
//		}catch (Exception e) {
//			System.out.println(e);
//			System.exit(0);
//		}
//
//		//Initialize the listener
//		StatusListener listener = new TweetsListener(maxTweetsPerFile, track, tweetsOutputPath, usersOutputPath, format, crawlerId, filteringList);
//		ConfigurationBuilder cb = new ConfigurationBuilder();
//
//		//Initialize the stream given the listener
//		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
//		twitterStream.addListener(listener);
//
//		if(track){
//			//Track Arabic tweets with at least one of the top terms
//			FilterQuery q =new FilterQuery();
//			String[] trackedTerms = new String[trackedTermsList.size()];
//			trackedTermsList.toArray(trackedTerms);
//			q.language(new String [] {"ar"});
//			q.track(trackedTerms);
//			twitterStream.filter(q);
//		}
//		else
//			//Sample tweets
//			twitterStream.sample();
//	}
//
//	public static List<String> readFile(String fileName) throws IOException{
//		String line = null;
//		List<String> termsList = new ArrayList<String>();
//		BufferedReader br = new BufferedReader(new FileReader(fileName));
//
//		while ((line = br.readLine()) != null) 
//			termsList.add(line);
//
//		br.close();
//
//		return termsList;
//	}
//
//	public static String returnUTCDate(java.util.Date date, String format)
//	{
//		String utcDate;
//		//UTC time zone  
//		DateFormat utcFormat = new SimpleDateFormat(format);
//		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//		//Time in UTC
//		utcDate = utcFormat.format(date);
//		return utcDate;
//	}
//
//	public static void main(String[] args) throws IOException {
//		//String helpMessage = "usage: TweetsCrawler.java id {-t|-s} maxTweetsPerFile tweetsOutputPath usersOutputPath spamTermsFile topTerms\n" +
//		String helpMessage = "usage: TweetsCrawler.java id {-t|-s} maxTweetsPerFile tweetsOutputPath usersOutputPath topTerms\n" +
//				"   id: 2-character long identifier\n"+
//				"   {-t|-s}: (crawling method) -t track tweets with top terms OR -s sample\n"+
//				"   maxTweetsPerFile: maximum no. of tweets to be stored per file\n"+
//				"   tweetsOutputPath: path of directory to store craweled tweets\n"+
//				"   usersOutputPath: path of directory to store tweeters\n"+
//				//"   spamTermsFile: file path of spam filtering terms\n"+
//				"   topTermsFile: file path of top frequent terms (400 terms or less)\n"+
//				"   Example arguments input: mh -t collection/ data/top_terms.txt";  
//		if(args.length != 6)
//			System.out.println(helpMessage);
//		else{
//			if(args[0].length()!=2)
//				System.out.println("Too short identifier!");	
//			if(!(args[1].equals("-t")||args[1].equals("-s")))
//				System.out.println("Invalid crawling option.\n-t track tweets with top terms OR -s sample");
//			else{  
//				crawlerId = args[0];
//				if(args[1].equals("-t"))
//					track = true;
//				maxTweetsPerFile = Integer.parseInt(args[2]);
//				tweetsOutputPath = args[3];
//				usersOutputPath = args[4];
//				//spamFilteringTermsFile = args[5];
//				topTermsFile = args[5];
//				crawlTweets();
//			}
//		}
//	}
//}