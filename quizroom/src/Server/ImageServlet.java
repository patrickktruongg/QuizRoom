package Server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import network.MySQLDriver;

@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		
		String username = request.getParameter("username");
		String avatar = null;
		byte[] imageStream = null;
		
		imageStream = msd.getImage(username);
		
		if(imageStream == null){
			avatar = msd.getAvatar(username);
			if(avatar == null){
				avatar = "defaultAvatar.png";
			}
			URL url = getClass().getResource(avatar);
			FileInputStream is = new FileInputStream(new File(url.getPath()));
			
			byte[] buffer = new byte[8192];
		    int bytesRead;
		    ByteArrayOutputStream output = new ByteArrayOutputStream();
		    while ((bytesRead = is.read(buffer)) != -1)
		    {
		        output.write(buffer, 0, bytesRead);
		    }
		    is.close();
		    imageStream = output.toByteArray();
		}
		msd.stop();
		response.setContentType("image/png");
		response.setContentLength(imageStream.length);
		response.getOutputStream().write(imageStream);
	}

}
