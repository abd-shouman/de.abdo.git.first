package qu.data;

import java.io.Serializable;
import java.util.Arrays;

import twitter4j.Status;
import twitter4j.URLEntity;

public class User implements Serializable{

	private static final long serialVersionUID = 3807887712152040481L;

	String screenName, name, location, description, urls[];
	int followersCount, friendsCount, favoriteCount, statusCount, listsCount;
	long userId, tweetEpoch;
	boolean verified;

	public User()
	{

	}

	public User(Status status) {
		twitter4j.User user = status.getUser();

		this.screenName = user.getScreenName();
		this.name = user.getName();
		this.location = user.getLocation();
		this.description = user.getDescription();

		setURLs(user.getDescriptionURLEntities(), user.getURLEntity());

		this.followersCount = user.getFollowersCount();
		this.friendsCount = user.getFriendsCount();
		this.favoriteCount = user.getFavouritesCount();
		this.statusCount = user.getStatusesCount();
		this.listsCount = user.getListedCount();

		this.userId = user.getId();
		this.tweetEpoch = status.getCreatedAt().getTime();
		this.verified = user.isVerified();
	}

	public void setURLs(URLEntity descriptionUrls[], URLEntity userUrl){
		int i = 0;
		this.urls = new String[descriptionUrls.length+1];

		for (URLEntity url : descriptionUrls) {
			this.urls[i] = url.getExpandedURL();
			i++;
		}
		this.urls[descriptionUrls.length] = userUrl.getExpandedURL();
	}

	@Override
	public String toString() {
		return "{[ID: " + userId + 
				"], [ScreenName: " + screenName + 
				"], [description: " + description +
				"], [location: " + location +
				"], [followers_Count: " + followersCount + 
				"], [URL: " + Arrays.toString(urls)+ "]}";
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public int getStatusCount() {
		return statusCount;
	}

	public void setStatusCount(int statusCount) {
		this.statusCount = statusCount;
	}

	public int getListsCount() {
		return listsCount;
	}

	public void setListsCount(int listsCount) {
		this.listsCount = listsCount;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTweetEpoch() {
		return tweetEpoch;
	}

	public void setTweetEpoch(long tweetEpoch) {
		this.tweetEpoch = tweetEpoch;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}