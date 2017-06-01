package Server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.UserProfile;
import network.MySQLDriver;

@WebServlet("/LobbyCreatorServlet")
public class LobbyCreatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
		String username = currUser.getUsername();
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		
		session.setAttribute("quizList", msd.getQuizzes(username));
		msd.stop();
		response.sendRedirect("Lobby_Creator.jsp");
	}

}
