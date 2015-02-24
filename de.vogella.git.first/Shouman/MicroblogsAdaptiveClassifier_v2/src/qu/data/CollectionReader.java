package qu.data;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import com.badrit.adaptiveclassifier.booleanclassifier.BooleanClassifier;
import com.badrit.adaptiveclassifier.main.Program;
import com.badrit.adaptiveclassifier.svmclassifier.SVMClassifierFactory;
import com.sun.tools.classfile.StackMapTable_attribute.chop_frame;

import qu.data.Tweet;
import qu.tweetprocess.ProcessTweet;

public class CollectionReader {

	private File[] files;
//	private int nextFile = 0;
	private int nextFile;
	private ObjectInputStream input = null;
	
	private Calendar startingDate = new GregorianCalendar();
	private Calendar tempDate = new GregorianCalendar();
	
//	private static BooleanClassifier booleanClassifier;
//	private static SVMClassifierFactory svmClassifier;
	
	public static long trackSimulationStartTime;
	
	public CollectionReader(File collection, Calendar startingDate){
		if(collection.isDirectory()){
			files = collection.listFiles();
			//Just to be sure. It isn't guaranteed to get correct order with listFiles()
			/*Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2){
					return f1.getName().compareTo(f2.getName());
				} 
			});*/
			System.out.println("Reading: "+collection.getName()+
					" with " + files.length+" files.");
			
			this.startingDate = startingDate;
		}
		else{
			System.out.println("Reading: " + collection.getAbsolutePath());
			files = new File[] {collection};
		}
		
		nextFile=0;
		
		int hours = startingDate.getTime().getHours();
		int day = startingDate.getTime().getDay();
		int month = startingDate.getTime().getMonth();
		int year = startingDate.getTime().getYear();
		
		while( (startingDate.getTimeInMillis() - dateOF(files[nextFile].getName()).getTimeInMillis())/(1000*3600)  < 0 ){
			nextFile++;
		}
		System.out.println(nextFile);
	}
	
	public Calendar dateOF(String fileName){
		int year = Integer.parseInt(fileName.substring(7, 11));
		int month = Integer.parseInt(fileName.substring(11, 13));
		int day = Integer.parseInt(fileName.substring(13, 15));
		int hours = Integer.parseInt(fileName.substring(16, 18));

		tempDate.set(Integer.par0seInt(fileName.substring(7, 11)), Integer.parseInt(fileName.substring(11, 13)), Integer.parseInt(fileName.substring(13, 15)));
		tempDate.set(Calendar.HOUR, Integer.parseInt(fileName.substring(16, 18)));		
		
		return tempDate;
	}

	public Object next() throws IOException, ClassNotFoundException {
		if (input == null) {
			input = new ObjectInputStream(new FileInputStream(files[nextFile]));
			nextFile++;
		}

		Object obj = null;
		while (true) {
			try{
				obj = input.readObject();
				if (obj != null) {
					return obj;
				}

			}catch(EOFException e){
				if(nextFile%1000 == 0)
					System.out.println((nextFile) + " files read so far.");
				input.close();

				if (nextFile >= files.length){
					System.out.println("Done reading all files.");
					//We're out of files to read. Must be the end of the corpus.
					return null;
				}

				//Move to next file.
				input = new ObjectInputStream(new FileInputStream(files[nextFile]));

				nextFile++;
			}
		}
	}
	public void close() throws IOException {
		input.close();
	}
	
	public static void read(String path, Calendar startingDate) throws Exception{
		if(path.isEmpty())
			//System.out.println("usage: TweetsCollectionReader.java collectionPath usersPath\n   " +
			System.out.println("usage: TweetsCollectionReader.java collectionPath\n   " +
					"collectionPath: full or relative path of file or collection directory\n " +
					//"usersPath: full or relative path of file or users directory" +
					"to read\n   Example argument input: collection/mh_20140420-000.ser");
		else{
			File collection = new File("K:/" + path);

			CollectionReader tweetSeqFileReader = new CollectionReader(collection,startingDate);
			Tweet tweet;
			
			ProcessTweet processor = new ProcessTweet();
			
			int i = 0;
			
			tweet = (Tweet) tweetSeqFileReader.next();
			trackSimulationStartTime = tweet.epoch;
			Program.fileStartTime = tweet.epoch;
			
			com.badrit.adaptiveclassifier.models.Tweet tempTweet = new com.badrit.adaptiveclassifier.models.Tweet();
			
			do{
				/*Process and classify tweets*/
				tempTweet.setTweet(tweet);
				
				if(!processor.filter( tempTweet ))
					return;
				
				if(i%10000 == 0){
					System.out.println(i+" tweets read so far.  AND  " +  Program.fileToRead + " files read so far. AND  " +
									"Current Model " + Program.modelVersion);
				}
				i++;
			}while((tweet = (Tweet) tweetSeqFileReader.next()) !=null);
			tweetSeqFileReader.close();
			
			System.out.println("Total number of tweets: " + i);

		}
	}
}