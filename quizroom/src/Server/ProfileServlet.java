package Server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.UserProfile;
import network.MySQLDriver;


@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		UserProfile user = msd.getUser(username);
		request.setAttribute("profileUsername", username);
		request.setAttribute("profileName", user.getName());
		request.setAttribute("profileFriendsList", user.getFriendList());
		request.setAttribute("profileQuizList", user.getQuizHistory());
		msd.stop();
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher("/Profile.jsp");
		dispatch.forward(request,response);
	}
}
