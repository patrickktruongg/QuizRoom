package data;

import java.io.Serializable;
import java.util.Vector;

public class Leaderboard implements Serializable {
	
	public static final long serialVersionUID = 2L;
	private int numGuesses;
	
	private Vector<LeaderboardPlayer> leaderboard;
	private Vector<Integer> playerScores;
	
	public Leaderboard(){
		leaderboard = new Vector<LeaderboardPlayer>();
		playerScores = new Vector<Integer>();
		numGuesses = 0;
	}
	
	public void addPlayer(LeaderboardPlayer player){
		leaderboard.add(player);
		playerScores.add(0);
	}
	
	public void addScore(String username, int score){
		int counter = 0;
		for(LeaderboardPlayer player : leaderboard){
			if(player.getUsername().equals(username)){
				player.addScore(score);
				playerScores.set(counter, score);
				numGuesses++;
				break;
			}
			counter++;
		}
	}
	
	public int getScore(String username){
		int counter = 0;
		for(LeaderboardPlayer player : leaderboard){
			if(player.getUsername().equals(username)){
				return playerScores.get(counter);
			}
			counter++;
		}
		return playerScores.get(0);
	}
	
	public boolean allPlayersGuessed(){
		return (numGuesses == leaderboard.size());
	}
	public void resetNumGuesses(){
		numGuesses = 0;
	}
	public void resetPlayerScores(){
		for(int i = 0; i < playerScores.size(); i++){
			playerScores.set(i, 0);
		}
	}
	
	public void updateLeaderboard(){
		for(int i = 0; i < leaderboard.size(); i++){
			int position = findCorrectPosition(i, leaderboard);
			LeaderboardPlayer temp = leaderboard.get(i);
			leaderboard.set(i, leaderboard.get(position));
			leaderboard.set(position, temp);
		}
		for(int i = 0; i < leaderboard.size(); i++){
			leaderboard.get(i).setRank(i+1);
			if(i > 0){
				if(leaderboard.get(i).getScore() == leaderboard.get(i-1).getScore()){
					leaderboard.get(i).setRank(leaderboard.get(i-1).getRank());
				}
			}
		}
	}
	
	public Vector<String> printResults(){
		
		updateLeaderboard();
		Vector<String> print = new Vector<String>();
		
		for(int i = 0; i < leaderboard.size(); i++){
			String message = "";
			LeaderboardPlayer player = leaderboard.get(i);
			message += player.getRank() + " " + player.getUsername() + " " + player.getScore();
			print.add(message);
		}
		
		return print;
	}
	
	public int findCorrectPosition(int desiredPos, Vector<LeaderboardPlayer> players){
		
		int currPos = desiredPos;
		int highestScore = players.get(desiredPos).getScore();
		
		for(int i = desiredPos; i < players.size(); i++){
			if(players.get(i).getScore() > highestScore){
				currPos = i;
			}
		}
		
		return currPos;
		
	}
	
	public Vector<Integer> getPlayerScores(){
		return playerScores;
	}
	
}
