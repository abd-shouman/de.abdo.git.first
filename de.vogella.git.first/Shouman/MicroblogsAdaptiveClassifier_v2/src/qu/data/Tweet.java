package qu.data;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

//import qu.crawl.TweetsCrawler;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

public class Tweet implements Serializable {

	private static final long serialVersionUID = -431365254008291110L;

	String tweetText, screenName, name, inReplyToScreenName, source;
	//String rewteetedTweetText;
	String hashtags[], urls[], mentionedScreenNames[];

	long epoch, tweetId, userId, inReplyToStatusId, inReplyToUserId, retweetedStatusId;
	long mentionedUserIds[];

	int retweetCount, favoriteCount;

	Date createdAt;
	GeoLocation coordinates;
	Place place;

	public Tweet(Status status) throws ParseException{
		this.tweetText = status.getText();
		this.screenName = status.getUser().getScreenName();
		this.name = status.getUser().getName();
		this.source = status.getSource();
		this.inReplyToScreenName = status.getInReplyToScreenName();

		setHashTags(status.getHashtagEntities());

		/*Resulting URLs array can be empty if url in tweet text is for an image. 
		 * Twitter API can also stream tweets with some empty fields*/
		setURLs(status.getURLEntities());
		setUserMentions(status.getUserMentionEntities());

		this.epoch = status.getCreatedAt().getTime();
		this.tweetId = status.getId();
		this.userId = status.getUser().getId();
		this.inReplyToStatusId = status.getInReplyToStatusId();
		this.inReplyToUserId = status.getInReplyToUserId();

		if(status.getRetweetedStatus()!=null)
			this.retweetedStatusId = status.getRetweetedStatus().getId();

		this.retweetCount = status.getRetweetCount();
		this.favoriteCount = status.getFavoriteCount();

		this.createdAt = status.getCreatedAt();
		this.coordinates = status.getGeoLocation();
		this.place = status.getPlace();
	}

	public void setUserMentions(UserMentionEntity users[]){
		int i = 0;
		mentionedUserIds = new long[users.length];
		mentionedScreenNames = new String[users.length];

		for(UserMentionEntity user : users){
			mentionedUserIds[i] = user.getId();
			mentionedScreenNames[i] = user.getScreenName();
			i++;
		}
	}

	public void setHashTags(HashtagEntity hashtags[]){
		int i = 0;
		this.hashtags = new String[hashtags.length];

		for(HashtagEntity hashtag : hashtags){
			this.hashtags[i] = hashtag.getText();
			i++;
		}
	}

	public void setURLs(URLEntity urls[]){
		int i = 0;
		this.urls = new String[urls.length];
		String expandedUrl, tweetUrl;

		for (URLEntity url : urls) {
			expandedUrl = url.getExpandedURL();
			tweetUrl = url.getURL();

			if(expandedUrl != null)
				this.urls[i] = expandedUrl;
			else
				this.urls[i] = tweetUrl;
			i++;
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTweetText() {
		return tweetText;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getName() {
		return name;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public String getSource() {
		return source;
	}

	public String[] getHashtags() {
		return hashtags;
	}

	public String[] getUrls() {
		return urls;
	}

	public String[] getMentionedScreenNames() {
		return mentionedScreenNames;
	}

	public long getEpoch() {
		return epoch;
	}

	public long getTweetId() {
		return tweetId;
	}

	public long getUserId() {
		return userId;
	}

	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public long getRetweetedStatusId() {
		return retweetedStatusId;
	}

	public long[] getMentionedUserIds() {
		return mentionedUserIds;
	}

	public int getRetweetCount() {
		return retweetCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public GeoLocation getCoordinates() {
		return coordinates;
	}

	public Place getPlace() {
		return place;
	}

	@Override
	public String toString() {
		return "[ID: " + tweetId
				+ "],\n[user: " + screenName
				+ "],\n["+ tweetText
				//+ "],\n[created_At: " + TweetsCrawler.returnUTCDate(createdAt, "EEE MMM dd HH:mm:ss z yyyy")
				+ "],\n[URLs: " + Arrays.toString(urls)
				+ "],\n[#: " + Arrays.toString(hashtags)
				+ "],\n[mentions: " + Arrays.toString(mentionedScreenNames)
				+ "],\n[location: " + coordinates+"]\n";
	}
}