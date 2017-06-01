package network;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;

import data.Leaderboard;
import data.LeaderboardPlayer;
import data.Lobby;
import data.Quiz;
import data.QuizQuestion; 
@ServerEndpoint(value = "/ws")
public class WebServer {
	private static HashSet<Session> sessionVector = new HashSet<Session>();
	private static Vector<Lobby> lobbies = new Vector<Lobby>();
	private static HashSet<Session> notInLobby = new HashSet<Session>();
	private static HashSet<Lobby> fullLobby = new HashSet<Lobby>();
	private static Queue <Lobby> nonFullLobby = new LinkedList<Lobby>();
	private static Vector<Lobby> inGameLobbies = new Vector<Lobby>();
	private static Lock lock = new ReentrantLock();
	public static HashMap<Session,String>allUser = new HashMap<Session,String>(); 
	
	@OnOpen
	public void open(Session session) {

		lock.lock();
		sessionVector.add(session);
		notInLobby.add(session);
		lock.unlock();
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		lock.lock();
		try{
		JSONObject obj = new JSONObject(message);
		String type = obj.getString("Type");
		switch(type){
			case "Message":
				String scope = obj.getString("Scope");
				switch(scope){
					case "Global":
						sendMessageToAll(message);
						break;
					case "Lobby":
						sendMessageToLobby(obj.getString("LobbyName"),message);
						break;
					default:
						sendMessageToQuiz(obj.getString("LobbyName"),message);
						break;
				}
				break;
			case "Create Quiz":
				Quiz createdQuiz = createQuiz(obj);
				MySQLDriver driver = new MySQLDriver();
				driver.connect();
				if(driver.quizNameExists(createdQuiz.getName())){
					String jsonMessage = "{\"Type\":\"Error\",\"Error\":\"Failed: Quiz Name Taken\"}";
					sendMessage(jsonMessage, session);
				} else{
					driver.insertQuiz(createdQuiz);
					String jsonMessage = "{\"Type\":\"Success\",\"Success\":\"Success: Quiz Added\"}";
					sendMessage(jsonMessage, session);
				}
				driver.stop();
				break;
			case "Start Game":
				String lobbyName = obj.getString("LobbyName");
				Lobby game = stringToLobby(lobbyName);
				moveLobbyToGame(game);
				String startMessage = "{\"Type\":\"Start Game\"}";
				sendMessageToLobby(lobbyName, startMessage);
				break;
			case "Create Lobby":
				String username = obj.getString("Username");
				String quizName = obj.getString("QuizName");
				lobbyName = obj.getString("LobbyName");
				int numPlayers = obj.getInt("numPlayers");
				driver = new MySQLDriver();
				driver.connect();
				Vector<Quiz> quizzes = driver.getQuizzes(username);
				Quiz desired = null;
				for(Quiz quiz : quizzes){
					if(quiz.getName().equals(quizName)){
						desired = quiz;
					}
				}
				addLobby(numPlayers, desired, username, lobbyName);
				driver.stop();
				break;
			case "Lobby Created":
				notInLobby.remove(session);
				break;
			case "Join Lobby":
				String matching = obj.getString("Matching");
				username = obj.getString("Username");
				switch(matching){
					case "Quick Match":
						quickMatch(username, session);
						break;
					case "Regular Match":
						String lobby = obj.getString("LobbyName");
						regularMatch(username, lobby, session);
						break;
				}
				break;
			case "Leave Lobby":
				username = obj.getString("Username");
				Lobby lobby = playerToLobby(username);
				if(lobby.isFull()){
					JSONObject js2 = new JSONObject();
					js2.put("Type", "Remove Player Not Full");
					js2.put("Host", lobby.getHost());
					js2.put("LobbyName", lobby.getLobbyName());
					js2.put("Structure", lobby.getStructure());
					js2.put("PlayerCount", lobby.getJoinedPlayerCount()-1);
					js2.put("MaxPlayers", lobby.getPlayerCount());
					sendMessageToAll(js2.toString());
					fullLobby.remove(lobby);
					nonFullLobby.add(lobby);
				}
				else{
					JSONObject js = new JSONObject();
					js.put("Type", "Remove Player");
					js.put("Username", username);
					js.put("LobbyName", lobby.getLobbyName());
					js.put("PlayerCount", lobby.getJoinedPlayerCount());
					js.put("MaxPlayers", lobby.getPlayerCount());
					sendMessageToAll(js.toString());
				}
				lobby.removePlayer(username);
				JSONObject jsObj = new JSONObject();
				jsObj.put("Type", "Remove Player");
				jsObj.put("Username", username);
				sendMessageToLobby(lobby.getLobbyName(), jsObj.toString());
				
				break;
			case "Guess":
				
				username = obj.getString("Username");
				String guess = obj.getString("Guess");
				double time = (obj.getDouble("Time"))/10.0;
				int score = 0;
				lobby = playerToLobby(username);
				boolean right = lobby.getQuiz().getQuestions().get(lobby.getQuestionPosition()).guessAnswer(guess);
				boolean isComp = lobby.getQuiz().getStructure().equals("Competative");
				if(right && isComp){
					score = (int) Math.round(10000 * (1/time));
				}
				lobbyName = playerToLobby(username).getLobbyName();
				for(int i = 0; i < inGameLobbies.size(); i++){
					if(inGameLobbies.get(i).getLobbyName().equals(lobbyName)){
						inGameLobbies.get(i).getLeaderboard().addScore(username, score);
						break;
					}
				}
				break;
			case "Round Over":
				lobbyName = obj.getString("LobbyName");
				sendMessagesAfterRound(lobbyName);
				stringToLobby(lobbyName).incrementQuestionPosition();
				sendLeaderboardToQuiz(lobbyName);
				for(int i = 0; i < inGameLobbies.size(); i++){
					if(inGameLobbies.get(i).getLobbyName().equals(lobbyName)){
						inGameLobbies.get(i).getLeaderboard().resetPlayerScores();
						break;
					}
				}
				break;
			case "Request Quiz":
				sendQuizStatus(obj.getString("LobbyName"));
				break;
			case "Webpage":
				if(!obj.getString("Page").equals("Mainpage")){
					notInLobby.remove(session);
				}
				if(obj.getString("Page").equals("Lobby")){
					updateLobbyPlayers(obj.getString("Lobby"),obj.getString("Username"),obj.getString("Creator"),session);
				}else if(obj.getString("Page").equals("Quiz")){
					updateQuizPlayers(obj.getString("Lobby"), obj.getString("Username"), obj.getString("Creator"), session);
					sendLeaderboardToQuiz(obj.getString("Lobby"));
					
				}break;
				case "VerifyUsername":
					allUser.put(session,obj.getString("Username").toLowerCase());
					break;
				case "CloseLobby":
					removeLobby(obj.getString("Lobby"));
					JSONObject removeMessage = new JSONObject();
					removeMessage.put("Type", "Remove Lobby");
					removeMessage.put("LobbyName", obj.get("Lobby"));
					sendMessageToAll(removeMessage.toString());
					break;
				case "Start Quiz" : 
					sendQuizStatus(obj.getString("Lobby"));
					break;
		}
		}catch(Exception e){
			System.out.println("Exception in onMessage(): " + e.getMessage());
			e.printStackTrace();
		}finally{
		lock.unlock();	
		}
	}
		/*if(type.equals("Message")){
			String scope = obj.getString("Scope");
			if(scope.equals("Global")){
				sendMessageToAll(message,session);
			}else if(scope.equals("Lobby")){
				sendMessageToLobby(obj.getString("LobbyName"),message, session);
			}else{
				sendMessageToQuiz(obj.getString("QuizName"),message,session);

			}
		}*/
	
	private void removeLobby(String lobbyName) {
		JSONObject js = new JSONObject();
		js.put("Type", "Removed");
		
		sendMessageToLobby(lobbyName, js.toString());
		for(Lobby lobby: nonFullLobby){
			if(lobby.getLobbyName().equals(lobbyName)){
				nonFullLobby.remove(lobby);
				break;
			}
		}
		for(Lobby lobby: fullLobby){
			if(lobby.getLobbyName().equals(lobbyName)){
				fullLobby.remove(lobby);
				break;
			}
		}
		for(Lobby lobby: inGameLobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				inGameLobbies.remove(lobby);
				break;
			}
		}
		for(Lobby lobby : lobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				lobbies.remove(lobby);
				break;
			}
		}
	}

	private void sendQuizStatus(String lobbyName){
		
		Lobby currLobby = stringToLobby(lobbyName);
		Quiz quiz = currLobby.getQuiz();

		try{
		QuizQuestion firstQuestion = quiz.getQuestions().get(currLobby.getQuestionPosition());
		JSONObject js = new JSONObject();
		js.put("Type", "Question");
		js.put("Question", firstQuestion.getQuestion());
		Vector<String> shuffle = firstQuestion.getChoices();
		Collections.shuffle(shuffle);
		js.put("Choice1", shuffle.get(0));
		js.put("Choice2", shuffle.get(1));
		js.put("Choice3", shuffle.get(2));
		js.put("Choice4", shuffle.get(3));
		sendMessageToQuiz(lobbyName, js.toString());					
		
		for(Lobby temp : inGameLobbies){
			if(temp.getLobbyName().equals(currLobby.getLobbyName())){
				temp = currLobby;
			}
		}
		}catch(Exception e){
			JSONObject message = new JSONObject();
			message.put("Type", "Game Over");
			sendMessageToQuiz(lobbyName, message.toString());
		}
		
	}
	
	//FIX THIS DEPENDING ON WHAT THE GUYS SAY TODO
	private void updateLobbyPlayers(String currLobby, String username, String Creator,Session session) {

		lock.lock();
		for(Lobby lobby : nonFullLobby ){
		
			if(lobby.getLobbyName().equals(currLobby)){
			
				lobby.addPlayer(username, session);
				JSONObject js = new JSONObject();
				js.put("Type", "Player Added");
				js.put("Username", username);
				
				sendMessageToLobby(currLobby, js.toString());
				
				js.put("PlayerCount", lobby.getJoinedPlayerCount());
				js.put("MaxPlayers", lobby.getPlayerCount());
				js.put("Host",lobby.getHost());
				js.put("Lobby",lobby.getLobbyName());
				js.put("Structure",lobby.getStructure());
				if(lobby.getJoinedPlayerCount() == lobby.getPlayerCount()){
					JSONObject js1 = new JSONObject();
					js1.put("Type", "Remove Lobby");
					js1.put("LobbyName", lobby.getLobbyName());
					sendMessageToAll(js1.toString());
				} else{
					sendMessageToAll(js.toString());
				}
				
				
				if(lobby.getPlayerCount() == lobby.getJoinedPlayerCount()){
					nonFullLobby.remove(lobby);
					fullLobby.add(lobby);
				}
			}
		}
		lock.unlock();
	}
	
	private void updateQuizPlayers(String currLobby, String username, String Creator, Session session){
		lock.lock();
		for(Lobby lobby : inGameLobbies ){
		
			if(lobby.getLobbyName().equals(currLobby)){
			
				lobby.addPlayer(username, session);
				JSONObject js = new JSONObject();
				js.put("Type", "Player Added");
				js.put("Username", username);
				
				//sendMessageToQuiz(currLobby, js.toString());
			}
		}
		lock.unlock();
	}

	public Quiz createQuiz(JSONObject obj){
		Quiz quiz = null;

		JSONArray jsArray = obj.getJSONArray("Array");
		Vector<QuizQuestion> quizQuestionList= new Vector<QuizQuestion>(); 

		for(int i = 0; i<jsArray.length();i++){

			JSONObject question = jsArray.getJSONObject(i);
			String quizQuestion = (String) question.get("Question"); 
			String choiceB =(String) question.get("choiceB"); 
			String choiceC =(String) question.get("choiceC"); 
			String choiceD =(String) question.get("choiceD"); 
			String solution =(String) question.get("solution");
			Vector<String > choices = new Vector<String>();
			choices.add(choiceB);
			choices.add(choiceC);
			choices.add(choiceD);
			choices.add(solution);
			QuizQuestion qq = new QuizQuestion(quizQuestion, choices,solution);
			quizQuestionList.add(qq);
		}
		quiz = new Quiz((String)obj.get("Creator"), (String)obj.get("Difficulty"),(String)obj.get("Subject"), (String)obj.get("Structure"), (String)obj.get("Name"), quizQuestionList, 1, 15);

		return quiz;
	}

	private void sendMessageToLobby(String lobbyName,String message) {
		Lobby lobby = stringToLobby(lobbyName);
		for(Session s : lobby.getSessions()){
			sendMessage(message, s);
		}
	}

	private void sendMessageToAll(String message) {
		for( Session s : notInLobby ){
			sendMessage(message, s);
		}
		
	}
	private void sendMessageToQuiz(String lobbyName,String message) {
		for(Lobby lobby : inGameLobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				for(Session s : lobby.getSessions()){
					sendMessage(message, s);
				}
				break;
			}
		}
	}
	private void sendLeaderboardToQuiz(String lobbyName){
		for(Lobby lobby : inGameLobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				Vector<String> leaderboard = lobby.getLeaderboard().printResults();
				JSONArray rankings = new JSONArray();
				JSONObject temp = new JSONObject();
				for(String rank : leaderboard){
					rankings.put(rank);
				}
				temp.put("Type", "Leaderboard");
				temp.put("Leaderboard", rankings);
				for(int i = 0; i < lobby.getSessions().size(); i++){
					//sendObject(temp, lobby.getSessions().get(i));
					sendMessage(temp.toString(),lobby.getSessions().get(i));
				}
				break;
			}
		}
	}
	private void sendMessagesAfterRound(String lobbyName){
		for(Lobby lobby : inGameLobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				for(int i = 0; i < lobby.getSessions().size(); i++){
					JSONObject js = new JSONObject();
					js.put("Type", "Correct Answer");
					js.put("Score", lobby.getLeaderboard().getScore(lobby.getPlayers().get(i)));
					js.put("CorrectAnswer", lobby.getQuiz().getQuestions().get(lobby.getQuestionPosition()).getCorrectChoice());
					sendMessage(js.toString(), lobby.getSessions().get(i));
				}
				lobby.getLeaderboard().resetPlayerScores();
				break;
			}
		}
	}
	private void sendMessage(String message, Session session){
		
		try{
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			System.out.println("ioe: " + e.getMessage());
			close(session);
		}
	}
//	private void sendObject(Object object, Session session){
//		try{
//			session.getBasicRemote().sendObject(object);
//		} catch (EncodeException e) {
//			System.out.println("eee: " + e.getMessage());
//		} catch (IOException e){
//			System.out.println("ioe: " + e.getMessage());
//		} finally {
//			//close(session);
//		}
//	}

	@OnClose
	public void close(Session session) {
		lock.lock();
		sessionVector.remove(session);
		notInLobby.remove(session);
		allUser.remove(session);
		for(Lobby lobby: nonFullLobby){
			if(lobby.sessionExists(session)){
				if(lobby.isEmpty()){
					nonFullLobby.remove(lobby);
				}
				String name = lobby.removeSession(session);
				JSONObject js = new JSONObject();
				js.put("Type", "Remove Player");
				js.put("Username", name);
				sendMessageToLobby(lobby.getLobbyName(), js.toString());
				/*js.put("LobbyName", lobby.getLobbyName());
				js.put("PlayerCount", lobby.getJoinedPlayerCount());
				js.put("MaxPlayers", lobby.getPlayerCount());
				sendMessageToAll(js.toString());*/
				break;
			}
		}
		for(Lobby lobby : fullLobby){
			if(lobby.sessionExists(session)){
//				if(lobby.isEmpty()){
//					fullLobby.remove(lobby);
//				}
					
				String name = lobby.removeSession(session);
				JSONObject js = new JSONObject();
				js.put("Type", "Remove Player");
				js.put("Username", name);
				sendMessageToLobby(lobby.getLobbyName(), js.toString());
				
				
				
				break;
			}
		}
		for(Lobby lobby: inGameLobbies){
			if(lobby.sessionExists(session)){
				if(lobby.isEmpty()){
					inGameLobbies.remove(lobby);
				}
				String name = lobby.removeSession(session);
				JSONObject js = new JSONObject();
				js.put("Type", "Remove Player");
				js.put("Username", name);

				//sendMessageToLobby(lobby.getLobbyName(), js.toString());
				break;
			}
		}
		lock.unlock();
	}

	@OnError
	public void onError(Throwable error) {
		
	}
	
	public void verify(){
	}
	
	//HELPER
	public Lobby playerToLobby(String user){
		for(int i = 0; i < lobbies.size(); i++){
			Lobby temp = lobbies.get(i);
			for(int j = 0; j < temp.getPlayers().size(); j++){
				if(temp.getPlayers().get(j).equals(user)){
					return temp;
				}
			}
		}
		return null;
	}
	public Lobby stringToLobby(String lobbyName){
		for(int i = 0; i < lobbies.size(); i++){
			if(lobbies.get(i).getLobbyName().equals(lobbyName)){
				return lobbies.get(i);
			}
		}
		return lobbies.get(0);
	}
	
	//LOBBY ADDING
	
	public Lobby addLobby(int playerCount, Quiz quiz, String host, String lobbyName){
		Lobby lobby = new Lobby(playerCount, quiz, host, lobbyName);
		if(lobby.getPlayerCount() > 1)
			nonFullLobby.add(lobby);
		else
			fullLobby.add(lobby);
		lobbies.add(lobby);
		JSONObject js = new JSONObject(); 
		js.put("Type", "Lobby Created");
		js.put("Host",host);
		js.put("PlayerCount",1 );
		js.put("MaxPlayers",playerCount);
		js.put("Lobby",lobbyName);
		js.put("Structure",lobby.getStructure());
		sendMessageToAll(js.toString());
		return lobby;
	}
		
	//LOBBY MATCHING
	/**
	 * Updates the lobby queue
	 * @param lobby
	 */
	public void updateLobbyQueue(Lobby lobby){
		nonFullLobby.add(lobby);
	}
	/**
	 * Assigns a user to a non full lobby, and then updates the queue if necessary (when the non full lobby becomes full and needs to be removed)
	 * @param user
	 * @return whether the lobby joined is full or not
	 */
	public boolean quickMatch(String user, Session session){
		if(!nonFullLobby.isEmpty()){
			Lobby lobby = nonFullLobby.peek();
			/*lobby.addPlayer(user, session);
			for(Lobby temp : lobbies){
				if(temp == lobby){
					temp = lobby;
					break;
				}
				
			}*/
			JSONObject js = new JSONObject(); 
			js.put("Type", "Join Lobby");
			js.put("Lobby", lobby.getLobbyName());
			sendMessage(js.toString(), session);
			notInLobby.remove(session);
			if(lobby.isFull()){
				nonFullLobby.remove(); 
				fullLobby.add(lobby);
				return true;
			}
		}
		else{
			String jsonMessage = "{\"Type\":\"Failure\",\"Message\":\"Failed: No Available Lobbies\"}";
			sendMessage(jsonMessage, session);
		}
		return false;
	}
	/**
	 * @param user, lobby
	 * @return whether the lobby joined is full or not
	 */
	public boolean regularMatch(String user,String lobbyName, Session session){
		Lobby lobby = stringToLobby(lobbyName);
		if(lobby.isFull()){
			String jsonMessage = "{\"Type\":\"Message\",\"Message\":\"Failed: Lobby Full\"}";
			sendMessage(jsonMessage, session);
		}
		else{
			lobby.addPlayer(user, session);
			notInLobby.remove(session);
			if(lobby.isFull()){
				nonFullLobby.remove(lobby);
			}
		}	
		return lobby.isFull();
	}
	
	//IN GAME ACTIONS
	
	//removes lobbies from nonFull lobby queue or fullLobby queue and moves it to running game lobbies
	public void moveLobbyToGame(Lobby lobby){
		if(fullLobby.contains(lobby)){
			fullLobby.remove(lobby);
		}
		nonFullLobby.remove(lobby);
		setUpLeaderboard(lobby);
		inGameLobbies.add(lobby);
	}
	//sets up leaderboard for a game
	public void setUpLeaderboard(Lobby lobby){
		Leaderboard leaderboard = new Leaderboard();
		for(int i = 0; i < lobby.getJoinedPlayerCount(); i++){
			LeaderboardPlayer player = new LeaderboardPlayer(lobby.getPlayers().get(i), 1, 0);
			leaderboard.addPlayer(player);
		}
		lobby.setLeaderboard(leaderboard);
	}
	
//	public void cancelTimer(){
//		timer.cancel();
//	}
	public boolean lobbyExists(String lobbyName){
		for(Lobby lobby : nonFullLobby){
			if(lobby.getLobbyName().equals(lobbyName)){
				return true; 
			}
		}for(Lobby lobby : inGameLobbies){
			if(lobby.getLobbyName().equals(lobbyName)){
				return true; 
			}
		}for(Lobby lobby : fullLobby){
			if(lobby.getLobbyName().equals(lobbyName)){
				return true; 
			}
		}
		return false; 
		}
	public Queue<Lobby> getLobby(){
		return nonFullLobby; 
		
	}

	public Lobby getLobby(String lobbyname) {
	
		for(Lobby lobby : nonFullLobby){
		
			if (lobby.getLobbyName().equals(lobbyname))
			{
		
				return lobby;
			}
		}return null; 
		// TODO Auto-generated method stub
		
	}
	public boolean verifyGuest(String name){
		return allUser.containsValue(name.toLowerCase());
	}
}