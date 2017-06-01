package data;

import java.util.HashSet;
import java.util.Vector;

public class UserProfile {
	HashSet<UserProfile> friendList; 
	Vector<Quiz> quizHistory; 
	boolean guessed;
	int userID; 
	String name;
	String username; 
	String password; 
	String imageURL;
	byte[] salt;
	/**
	 * Constructor for the user profile
	 * @param name,username,password, imageURL
	 */
	public UserProfile(String name, String username, String password,String imageURL, byte[] salt){
		this.name = name;
		this.username = username; 
		this.password = password; 
		this.imageURL = imageURL ; 
		this.quizHistory = new Vector<Quiz>();
		this.friendList = new HashSet<UserProfile>();
		this.salt = salt;
		guessed = false;
	}
	/**
	 * Constructor for a guest user
	 * @param username, imageURL
	 */
	public UserProfile(String username, String imageURL){
		this.username = username;
		this.imageURL = imageURL;
		guessed = false;
	}
	
	//GETTERS
	/**
	 * @return the friendList
	 */
	public HashSet<UserProfile> getFriendList() {
		return friendList;
	}
	/**
	 * @return the quizHistory
	 */
	public Vector<Quiz> getQuizHistory() {
		return quizHistory;
	}
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the salt
	 */
	public byte[] getSalt() {
		return salt;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @return the imageURL
	 */
	public String getImageURL() {
		//FIX THIS ASAP UTSAV!!!
		return imageURL;
		//return imageURL;
	}
	/**
	 * Adds user to the friend list. 
	 * @param useerProfile Friend
	 */
	
	//HELPERS
	public void addFriend (UserProfile friend) {
		friendList.add(friend);
//		friend.addFriend(this);
	}
	public void addQuiz(Quiz quiz){
		quizHistory.add(quiz);
	}
	/**
	 * Removes user from the friend list. 
	 * @param useerProfile Friend
	 */
	public void removeFriend(UserProfile friend) {
		friendList.remove(friend);
		friend.removeFriend(this);
	}
	/**
	 * Checks if two users are friends. 
	 * @param useerProfile Friend
	 */
	public boolean isFriend(UserProfile friend) {
		return(friendList.contains(friend));
	}
	
	//SETTERS
	public void toggleGuessed(){
		if(!guessed){
			guessed = true;
		}
		guessed = false;
	}
}
