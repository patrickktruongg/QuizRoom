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

@WebServlet("/AddFriendServlet")
public class AddFriendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		
		String action = request.getParameter("action");
		
		UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
		String username = currUser.getUsername();
		
		
		String otherUser = request.getParameter("otherUser");
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		
		if(action.equals("Add Friend"))
			msd.addFriend(username, otherUser);
		else
			msd.removeFriend(username, otherUser);
			
		msd.stop();
	}

}
