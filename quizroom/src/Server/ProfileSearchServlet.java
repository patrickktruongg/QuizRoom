package Server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import network.MySQLDriver;

@WebServlet("/ProfileSearchServlet")
public class ProfileSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		if(!msd.usernameExists(username)){
			msd.stop();
			response.getWriter().print("User does not exist.");
		}
		else{
			msd.stop();
			response.getWriter().print("valid");
		}
	}

}
