package data;

import java.io.Serializable;
import java.util.Vector;

import javax.websocket.Session;

public class Lobby implements Serializable{
	private static final long serialVersionUID = 1L;
	private Vector<String> players;
	private Vector<Session> sessions;
	private Quiz quiz;
	private String lobbyName;
	private String host;
	private int playerCount;
	private int joinedPlayerCount;
	private Leaderboard leaderboard;
	private boolean full;
	private int questionPos;
	public Lobby(int playerCount, Quiz quiz, String host, String lobbyName){
		this.playerCount = playerCount;
		this.quiz = quiz;
		this.host = host;
		this.lobbyName = lobbyName;
		players = new Vector<String>();
		sessions = new Vector<Session>();
		full = false;
		joinedPlayerCount = 0;
		leaderboard = new Leaderboard();
		questionPos = 0;
	}
	
	//ADD AND REMOVE PLAYERS
	public void addPlayer(String player, Session session){
	
		players.add(player);
		sessions.add(session);
		if(players.size() == playerCount){
			full = true;
		}
		joinedPlayerCount++;
	}
	public void removePlayer(String player){
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).equals(player)){
				players.remove(i);
				sessions.remove(i);
				break;
			}
		}
		full = false;
		joinedPlayerCount--;
	}
	
	public void setLeaderboard(Leaderboard leaderboard){
		this.leaderboard = leaderboard;
	}
	
	//GETTERS
	public Leaderboard getLeaderboard(){
		return leaderboard;
	}
	public int getPlayerCount(){
		return playerCount;
	}
	public String getLobbyName(){
		return lobbyName;
	}
	public Vector<String> getPlayers(){
		return players;
	}
	public String getHost(){
		return host;
	}
	public Vector<Session> getSessions(){
		return sessions;
	}
	public Quiz getQuiz(){
		return quiz;
	}
	public int getJoinedPlayerCount(){
		return joinedPlayerCount;
	}
	public int getQuestionPosition(){
		return questionPos;
	}
	
	//QUESTIONS ABOUT LOBBY
	public boolean isFull(){
		return full;
	}
	public boolean playerExists(String check){
		for(int i = 0; i < players.size(); i++){
			if(players.get(i).equals(check)){
				return true;
			}
		}
		return false;
	}
	public boolean sessionExists(Session session){
		for(int i = 0; i < sessions.size(); i++){
			if(sessions.get(i)==(session)){
				return true;
			}
		}
		return false;
	}
	public String removeSession(Session session){
		String user = null; 
		for(int i = 0; i < sessions.size(); i++){
			if(sessions.get(i)==(session)){
				user = players.get(i); 
				players.remove(i);
				sessions.remove(i);
				break;
			}
		}
		full = false;
		joinedPlayerCount--;
		return user; 
	}
	public String getStructure(){
		return quiz.getStructure();
	}

	public String getQuizName() {
		// TODO Auto-generated method stub
		return quiz.getName();
	}

	public String getTopic() {
		// TODO Auto-generated method stub
		return quiz.getTopic();
	}

	public String getDifficulty() {
		// TODO Auto-generated method stub
		return quiz.getDifficulty(); 
	}
	
	public void incrementQuestionPosition(){
		questionPos++;
	}
	public boolean isEmpty(){
		if(sessions.size()==0) return true; 
		return false; 
	}
}
