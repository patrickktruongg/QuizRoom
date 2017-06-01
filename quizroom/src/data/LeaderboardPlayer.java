package data;

import java.io.Serializable;

public class LeaderboardPlayer implements Serializable {
	
	public static final long serialVersionUID = 1L;
	
	private String username;
	private int rank;
	private int score;
	
	public LeaderboardPlayer(String username, int rank, int score){
		this.username = username;
		this.rank = rank;
		this.score = score;
	}
	
	//GETTERS
	public String getUsername(){
		return username;
	}
	public int getRank(){
		return rank;
	}
	public int getScore(){
		return score;
	}
	
	//SETTERS
	public void setRank(int rank){
		this.rank = rank;
	}
	public void addScore(int score){
		this.score += score;
	}
	
}
