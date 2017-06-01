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
import network.WebServer;

@WebServlet("/JoinLobbyServlet")
public class JoinLobbyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WebServer ws = new WebServer(); 
		HttpSession session = request.getSession(true);
		Lobby lobby = ws.getLobby(request.getParameter("LobbyName")); 
		
		PrintWriter out = response.getWriter();
		if(lobby==null){
			out.print("Full");
		}else{
			session.setAttribute("lobby",lobby);
			session.setAttribute("lobbyName",lobby.getLobbyName());
			session.setAttribute("lobbyPlayerCount",lobby.getPlayerCount());
			session.setAttribute("lobbyQuizName",lobby.getQuizName());
			session.setAttribute("lobbyHost",lobby.getHost());
			session.setAttribute("lobbySubject", lobby.getTopic());
			session.setAttribute("lobbyStructure", lobby.getStructure());
			session.setAttribute("lobbyDifficulty", lobby.getDifficulty());
			out.print("Valid");
			out.flush();
		}
	}
}
