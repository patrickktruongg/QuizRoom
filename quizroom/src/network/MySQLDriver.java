package network;

import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import data.UserProfile;
import data.Quiz;
import data.QuizQuestion;

public class MySQLDriver {

	private Connection conn;
	private Statement st;
	private ResultSet rs;
	private PreparedStatement ps;

	public MySQLDriver(){
		try{
			new com.mysql.jdbc.Driver();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	public void connect() {
		try{
			conn = DriverManager.getConnection("jdbc:mysql://quizroom.cou98ckgi2s4.us-west-1.rds.amazonaws.com:3306/Data?user=quizroom&password=quizroom&useSSL=false");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void stop(){
		try{
			conn.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	//FOR LOGIN AND SIGNUP
	public boolean usernameExists(String username){
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.username FROM User u");
			while(rs.next()){
				String temp = rs.getString("username").toLowerCase();
				if(username.toLowerCase().equals(temp)){
					return true;
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return false;
	}
	
	public byte[] getSalt(String username){
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.salt FROM User u where username =  '" + username + "'");
			while(rs.next()){
				return rs.getBytes("salt");
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//USED TO VALIDATE PASSWORD
	public boolean passwordCorrect(String username, String password){
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.username, u.password FROM User u WHERE u.username='" + username + "'");
			while(rs.next()){
				String resultPassword = rs.getString("password");
				if(resultPassword.equals(password)){
					return true;
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	//QUIZ FUNCTIONALITY

	//CHECKS IF QUIZ NAME IS AVAILABLE
	public boolean quizNameExists(String quizname){
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT q.quizname FROM Quiz q WHERE q.quizname='" + quizname + "'");
			if(rs.next()){
				return true;
			}
		} catch (SQLException sql) {
			System.out.println("SQL Exception in quizNameExists(): " + sql.getMessage());
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	//RETURNS ALL QUIZZES IN DATABASE
	public Vector<Quiz> getAllQuizzes(){
		Vector<Quiz> quizzes = new Vector<Quiz>();
		Statement st1 = null;
		Statement st2 = null;
		ResultSet quizQuestions = null;
		ResultSet choicesRS = null;
		try{
			st = conn.createStatement();
			st1 = conn.createStatement();
			st2 = conn.createStatement();
			rs = st.executeQuery("SELECT * from Quiz");
			while(rs.next()){
				String host = rs.getString("creator");
				String difficulty = rs.getString("difficulty");
				String structure = rs.getString("structure");
				String name = rs.getString("quizname");
				String topic = rs.getString("topic");
				int timer = rs.getInt("timer");
				Vector<QuizQuestion> questions = new Vector<QuizQuestion>();
				quizQuestions = st1.executeQuery("SELECT q.question, q.correctchoice FROM QuizQuestion q WHERE q.quizname='" + name + "'");
				while(quizQuestions.next()){
					String question = quizQuestions.getString("question");
					String correctChoice = quizQuestions.getString("correctchoice");
					Vector<String> choices = new Vector<String>();
					choicesRS = st2.executeQuery("SELECT qc.choice FROM QuizChoice qc WHERE qc.quizname='" + name + "' AND qc.quizquestion='" + question + "'");
					while(choicesRS.next()){
						String choice = choicesRS.getString("choice");
						choices.add(choice);
					}
					QuizQuestion temp = new QuizQuestion(question, choices, correctChoice);
					questions.add(temp);
				}
				Quiz quiz = new Quiz(host, difficulty, topic, structure, name, questions, 0, timer);
				quizzes.add(quiz);
			}
		} catch (SQLException e){
			System.out.println("SQL Exception in getAllQuizzes(): " + e.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(st1 != null)
					st1.close();
				if(st2 != null)
					st2.close();
				if(choicesRS != null)
					choicesRS.close();
				if(quizQuestions != null)
					quizQuestions.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}
		return quizzes;
	}

	//RETURNS QUIZZES FROM A USER
	public Vector<Quiz> getQuizzes(String username){
		Vector<Quiz> quizzes = new Vector<Quiz>();
		Statement st1 = null;
		Statement st2 = null;
		ResultSet quizQuestions = null;
		ResultSet choicesRS = null;
		try{
			st = conn.createStatement();
			st1 = conn.createStatement();
			st2 = conn.createStatement();
			rs = st.executeQuery("SELECT q.quizname, q.creator, q.difficulty, q.topic, q.structure, q.timer from Quiz q where q.creator='" + username + "'");
			while(rs.next()){
				String host = rs.getString("creator");
				String difficulty = rs.getString("difficulty");
				String structure = rs.getString("structure");
				String name = rs.getString("quizname");
				String topic = rs.getString("topic");
				int timer = rs.getInt("timer");
				Vector<QuizQuestion> questions = new Vector<QuizQuestion>();
				quizQuestions = st1.executeQuery("SELECT q.quizQuestionID, q.quizname, q.question, q.correctchoice FROM QuizQuestion q WHERE q.quizname=\"" + name + "\"");
				while(quizQuestions.next()){
					String question = quizQuestions.getString("question");
					String correctChoice = quizQuestions.getString("correctchoice");
					String quizQuestionID = quizQuestions.getString("quizQuestionID");
					Vector<String> choices = new Vector<String>();
					choicesRS = st2.executeQuery("SELECT q.choice FROM QuizChoice q WHERE q.quizQuestionID='" + quizQuestionID + "'");
					while(choicesRS.next()){
						String choice = choicesRS.getString("choice");
						choices.add(choice);
					}
					QuizQuestion temp = new QuizQuestion(question, choices, correctChoice);
					questions.add(temp);
				}
				Quiz quiz = new Quiz(host, difficulty, topic, structure, name, questions, 0, timer);
				quizzes.add(quiz);
			}
		} catch (SQLException e){
			System.out.println("SQL Exception in getQuizzes(): " + e.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(st1 != null)
					st1.close();
				if(st2 != null)
					st2.close();
				if(choicesRS != null)
					choicesRS.close();
				if(quizQuestions != null)
					quizQuestions.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}
		return quizzes;
	}
	//INSERT QUIZZES
	public void insertQuiz(Quiz quiz){

		try {
			String query = "INSERT INTO Quiz (quizname,creator,difficulty,topic,structure, timer) VALUES (?, ?, ?, ?, ?, ?);";
			ps = conn.prepareStatement(query);
			ps.setString(1, quiz.getName());
			ps.setString(2, quiz.getHost());
			ps.setString(3, quiz.getDifficulty());
			ps.setString(4, quiz.getTopic());
			ps.setString(5, quiz.getStructure());
			ps.setInt(6, quiz.getTimer());
			ps.execute();

			st = conn.createStatement();
			rs = st.executeQuery("SELECT last_insert_id(quizID) from Quiz");

			insertQuestions(quiz);

		} catch (SQLException e) {
			System.out.println("SQL Exception in insertQuiz(): " + e.getMessage());
		}finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
	public void insertQuestions(Quiz quiz){
		try{
			for(QuizQuestion question : quiz.getQuestions()){
				String questionString = question.getQuestion();
				String correctChoice = question.getCorrectChoice();
				String query = "INSERT INTO QuizQuestion (quizname, question, correctchoice) VALUES (?, ?, ?)";
				ps = conn.prepareStatement(query);
				ps.setString(1, quiz.getName());
				ps.setString(2, questionString);
				ps.setString(3,correctChoice);
				ps.execute();
				int quizQuestionID = findMax(); 

				for(int i = 0; i < question.getChoices().size(); i++){
					String choice = question.getChoices().get(i);
					query = "INSERT INTO QuizChoice (quizQuestionID, choice) VALUES (?, ?)";
					ps = conn.prepareStatement(query);
					ps.setInt(1, quizQuestionID);	
					ps.setString(2, choice);
					ps.execute();
				}
			}
		}catch(Exception e){
			System.out.println("SQL Exception in insertQuestions(): " + e.getMessage());
		}finally {
			try {
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	//USER FUNCTIONALITY

	private int findMax() {
		PreparedStatement sizePS = null;
		try {
			sizePS = conn.prepareStatement("SELECT MAX( quizQuestionID ) FROM QuizQuestion;");
			ResultSet sizeRS  = sizePS.executeQuery();
			while (sizeRS.next()) {
				return Integer.parseInt(sizeRS.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if(sizePS != null){
					sizePS.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	//USED TO CONSTRUCT A SINGLE USER --> can pass a logged in users name through a session 
	//and then call this to reconstruct the user
	public UserProfile getUser(String username){
		UserProfile user = null;
		ResultSet friendRS = null;
		Statement st2 = null;
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.username, u.password, u.salt, u.name, u.image FROM User u WHERE u.username='" + username + "'");
			rs.next();
			String name = rs.getString("name");
			String password = rs.getString("password");
			String image = rs.getString("image");
			byte[] salt = rs.getBytes("salt");
			user = new UserProfile(name, username, password, image, salt);

			st2 = conn.createStatement();
			friendRS = st2.executeQuery("SELECT u.name, u.username, u.password, u.salt, "
					+ "u.image FROM User u, Friend f WHERE f.user1='" + username + "' AND f.user2=u.username");
			while(friendRS.next()){
				String friendUsername = friendRS.getString("username");
				String friendName = friendRS.getString("name");
				String friendPassword = friendRS.getString("password");
				String friendImage = friendRS.getString("image");
				byte[] friendSalt = friendRS.getBytes("salt");
				UserProfile tempFriend = new UserProfile(friendName, friendUsername, friendPassword, friendImage, friendSalt);
				user.addFriend(tempFriend);
			}
			Vector<Quiz> quizHistory = getQuizzes(username);
			for(Quiz quiz : quizHistory){
				user.addQuiz(quiz);
			}				
		} catch (SQLException e) {
			System.out.println("SQL Exception in getUser(): " + e.getMessage());
		}finally{
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(friendRS != null)
					friendRS.close();
				if(st2 != null)
					st2.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}
		return user;
	}
	//RETURNS ALL USERS
	public Vector<UserProfile> getUsers(){
		Vector<UserProfile> users = new Vector<UserProfile>();
		Statement st1 = null;
		ResultSet friendRS = null;
		try{
			st = conn.createStatement();
			st1 = conn.createStatement();
			rs = st.executeQuery("SELECT * from User");
			while(rs.next()){
				String username = rs.getString("username");
				String name = rs.getString("name");
				String password = rs.getString("password");
				String image = rs.getString("image");
				byte[] salt = rs.getBytes("salt");
				UserProfile temp = new UserProfile(name, username, password, image, salt);

				friendRS = st1.executeQuery("SELECT u.name, u.username, u.password, u.salt, "
						+ "u.image FROM User u, Friend f WHERE f.user1='" + username + "' AND f.user2=u.username");
				while(friendRS.next()){
					username = friendRS.getString("username");
					name = friendRS.getString("name");
					password = friendRS.getString("password");
					image = friendRS.getString("image");
					byte[] friendSalt = friendRS.getBytes("salt");
					UserProfile tempFriend = new UserProfile(name, username, password, image, friendSalt);
					temp.addFriend(tempFriend);
				}
				Vector<Quiz> quizHistory = getQuizzes(username);
				for(Quiz quiz : quizHistory){
					temp.addQuiz(quiz);
				}
				users.add(temp);
			}

		} catch (SQLException e) {
			System.out.println("SQL Exception in getUsers(): " + e.getMessage());
		}finally {
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(st1 != null)
					st1.close();
				if(friendRS != null)
					friendRS.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}
		return users;
	}
	//INSERT USER
	public void insertUser(UserProfile user){
		try {
			String query = "INSERT INTO User (username,password,salt,name,image) VALUES (?, ?, ?, ?, ?)";
			ps = conn.prepareStatement(query);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.setBytes(3, user.getSalt());
			ps.setString(4, user.getName());
			ps.setString(5, user.getImageURL());
			ps.execute();

			for(UserProfile friend : user.getFriendList()){
				query = "INSERT INTO Friend (user1, user2) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setString(1, friend.getUsername());
				ps.setString(2, user.getUsername());
				ps.execute();

				query = "INSERT INTO Friend (user1, user2) VALUES (?, ?)";
				ps = conn.prepareStatement(query);
				ps.setString(1, user.getUsername());
				ps.setString(2, friend.getUsername());
				ps.execute();
			}

		} catch (SQLException e) {
			System.out.println("SQL Exception in insertUser(): " + e.getMessage());
		} finally {
			try {
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	//ADDING A FRIEND
	public void addFriend(String username, String add){
		try {
			String query = "INSERT INTO Friend (user1, user2) VALUES (?, ?)";
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, add);
			ps.execute();

			query = "INSERT INTO Friend (user1, user2) VALUES (?, ?)";
			ps.getConnection().prepareStatement(query);
			ps.setString(1,  add);
			ps.setString(2, username);
			ps.execute();
		} catch (SQLException e) {
			System.out.println("SQL Exception in insertUser(): " + e.getMessage());
		} finally {
			try {
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void removeFriend(String username, String remove){
		try {
			String query = "DELETE FROM Friend WHERE user1=? AND user2=?";
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, remove);
			ps.execute();

			query = "DELETE FROM Friend WHERE user1=? AND user2=?";
			ps.getConnection().prepareStatement(query);
			ps.setString(1,  remove);
			ps.setString(2, username);
			ps.execute();

		} catch (SQLException e) {
			System.out.println("SQL Exception in removeFriend(): " + e.getMessage());
		} finally {
			try {
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public void addImage(String username, InputStream fstream) {
		try{
			st = conn.createStatement();
			String query = "UPDATE User SET imagedata = ? WHERE username = ?;";
			ps = conn.prepareStatement(query);
			ps.setString(2,username);
			ps.setBlob(1,fstream);
			ps.execute();

		} catch (SQLException e) {
			System.out.println("SQL Exception in addImage(): " + e.getMessage());
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(ps != null){
					ps.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public byte[] getImage(String username){
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.imageData FROM User u where username =  '" + username + "'");
			while(rs.next()){
				return rs.getBytes("imageData");
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getAvatar(String username) {
		try{
			st = conn.createStatement();
			rs = st.executeQuery("SELECT u.image FROM User u where username =  '" + username + "'");
			while(rs.next()){
				return rs.getString("image");
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			try {
				if(st != null){
					st.close();
				}
				if(rs != null){
					rs.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public Quiz getQuiz(String quizName){
		Statement st1 = null;
		Statement st2 = null;
		Quiz quiz = null;
		ResultSet quizQuestions = null;
		ResultSet choicesRS = null;
		try{
			st = conn.createStatement();
			st1 = conn.createStatement();
			st2 = conn.createStatement();
			rs = st.executeQuery("SELECT q.quizname, q.creator, q.difficulty, q.topic, q.structure, q.timer from Quiz q where q.quizname=\"" + quizName + "\"");
			while(rs.next()){
				String host = rs.getString("creator");
				String difficulty = rs.getString("difficulty");
				String structure = rs.getString("structure");
				String name = rs.getString("quizname");
				String topic = rs.getString("topic");
				int timer = rs.getInt("timer");
				Vector<QuizQuestion> questions = new Vector<QuizQuestion>();
				quizQuestions = st1.executeQuery("SELECT q.quizQuestionID, q.quizname, q.question, q.correctchoice FROM QuizQuestion q WHERE q.quizname=\"" + quizName + "\"");
				while(quizQuestions.next()){
					String question = quizQuestions.getString("question");
					String correctChoice = quizQuestions.getString("correctchoice");
					String quizQuestionID = quizQuestions.getString("quizQuestionID");
					Vector<String> choices = new Vector<String>();
					choicesRS = st2.executeQuery("SELECT q.choice FROM QuizChoice q WHERE q.quizQuestionID='" + quizQuestionID + "'");
					while(choicesRS.next()){
						String choice = choicesRS.getString("choice");
						choices.add(choice);
					}
					QuizQuestion temp = new QuizQuestion(question, choices, correctChoice);
					questions.add(temp);
				}
				quiz = new Quiz(host, difficulty, topic, structure, name, questions, 0, timer);

			}
		} catch (SQLException e){
			System.out.println("SQL Exception in getQuiz(): " + e.getMessage());
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(st1 != null)
					st1.close();
				if(st2 != null)
					st2.close();
				if(choicesRS != null)
					choicesRS.close();
				if(quizQuestions != null)
					quizQuestions.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}

		return quiz;
	}
	public Vector<Quiz> searchQuiz(String searchName){
		searchName = "%"+searchName+"%";
		Vector<Quiz> quizzes = new Vector<Quiz>();
		Statement st1 = null;
		Statement st2 = null;
		ResultSet quizQuestions = null;
		ResultSet choicesRS = null;
		try{
			st = conn.createStatement();
			st1 = conn.createStatement();
			st2 = conn.createStatement();
			rs = st.executeQuery("SELECT q.quizname, q.creator, q.difficulty, q.topic, q.structure, q.timer from Quiz q where quizname LIKE '"+searchName+"'");
			while(rs.next()){
				String host = rs.getString("creator");
				String difficulty = rs.getString("difficulty");
				String structure = rs.getString("structure");
				String name = rs.getString("quizname");
				String topic = rs.getString("topic");
				int timer = rs.getInt("timer");
				Vector<QuizQuestion> questions = new Vector<QuizQuestion>();
				quizQuestions = st1.executeQuery("SELECT q.quizQuestionID, q.quizname, q.question, q.correctchoice FROM QuizQuestion q WHERE q.quizname=\"" + name + "\"");
				while(quizQuestions.next()){
					String question = quizQuestions.getString("question");
					String correctChoice = quizQuestions.getString("correctchoice");
					String quizQuestionID = quizQuestions.getString("quizQuestionID");
					Vector<String> choices = new Vector<String>();
					choicesRS = st2.executeQuery("SELECT q.choice FROM QuizChoice q WHERE q.quizQuestionID='" + quizQuestionID + "'");
					while(choicesRS.next()){
						String choice = choicesRS.getString("choice");
						choices.add(choice);
					}
					QuizQuestion temp = new QuizQuestion(question, choices, correctChoice);
					questions.add(temp);
				}
				Quiz quiz = new Quiz(host, difficulty, topic, structure, name, questions, 0, timer);
				quizzes.add(quiz);
			}
		} catch (SQLException e){
			System.out.println("SQL Exception in searchQuiz(): " + e.getMessage());
			e.printStackTrace();
		}
		finally{
			try{
				if(rs != null)
					rs.close();
				if(st != null)
					st.close();
				if(st1 != null)
					st1.close();
				if(st2 != null)
					st2.close();
				if(choicesRS != null)
					choicesRS.close();
				if(quizQuestions != null)
					quizQuestions.close();
			} catch (SQLException e){
				e.getMessage();
			}
		}

		return quizzes;
	}

}
