package Server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.Lobby;
import data.Quiz;
import data.UserProfile;
import network.MySQLDriver;
import network.WebServer;


@WebServlet("/GameCreatorServlet")
public class GameCreatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			WebServer ws = new WebServer();
			
			HttpSession session = request.getSession(true);
			String lobbyName = request.getParameter("LobbyName");
			int playerCount = Integer.parseInt(request.getParameter("NumPlayers"));
			String quizName = request.getParameter("QuizName");
			String host = (((UserProfile) session.getAttribute("currUser")).getUsername());
			PrintWriter out = response.getWriter();
			if(quizName == null){
				out.print("No Quiz");
				out.flush();
			}
			else if(ws.lobbyExists(lobbyName)){
				out.print("Exists");
				out.flush();
				
			}else{
				MySQLDriver msd = new MySQLDriver(); 
				msd.connect();
				
				Quiz quiz = msd.getQuiz(quizName);
				Lobby lobby = ws.addLobby(playerCount, quiz, host, lobbyName);
			
				
				session.setAttribute("lobby",lobby);
				session.setAttribute("lobbyName",lobbyName);
				session.setAttribute("lobbyPlayerCount",playerCount);
				session.setAttribute("lobbyQuizName",quizName);
				session.setAttribute("lobbyHost",host);
				session.setAttribute("lobbySubject", quiz.getTopic());
				session.setAttribute("lobbyStructure", quiz.getStructure());
				session.setAttribute("lobbyDifficulty", quiz.getDifficulty());
				out.print("Valid");
				out.flush();
			}
	}
}
