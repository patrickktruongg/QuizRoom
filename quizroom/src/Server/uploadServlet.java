package Server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import data.UserProfile;
import network.MySQLDriver;

@WebServlet("/uploadServlet")
@MultipartConfig
public class uploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession(true);
	    final Part filePart = request.getPart("file");
	    
	    InputStream filecontent = filePart.getInputStream();
	    
	    MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		msd.addImage(((UserProfile)session.getAttribute("currUser")).getUsername(), filecontent);
		msd.stop();
	}
}
