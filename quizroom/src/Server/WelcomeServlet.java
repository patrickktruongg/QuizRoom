package Server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import data.UserProfile;
import network.MySQLDriver;
import network.WebServer;

@WebServlet("/WelcomeServlet")
public class WelcomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Random RANDOM = new SecureRandom();

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(true);
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		
		String username = request.getParameter("username");
		if(username.contains(" ")){
			response.getWriter().write("No spaces allowed in the username.");
		}
		else{
			if(request.getParameter("action").equals("login")){
				
				String password = request.getParameter("password");
				if(password.equals("")||password.equals("")){
					response.getWriter().write("Please input a username and a password ");
				}else{
				if(!msd.usernameExists(username)){
					response.getWriter().write("Username does not exist. ");
				}else{
					byte[] salt = msd.getSalt(username);
					String hashedPassword = get_SHA_512_SecurePassword(password, salt);
					if(msd.passwordCorrect(username, hashedPassword)){
						session.setAttribute("currUser", msd.getUser(username));
						response.getWriter().write("valid");
						session.setAttribute("isGuest",false);
	
					}else{
						response.getWriter().write("Username and password do not match. ");
	
					}
				}
				}
				
			}else if(request.getParameter("action").equals("signup")){
				String password = request.getParameter("password");
				String verifyPassword = request.getParameter("vpassword");
				String fullName = request.getParameter("name");
				String image = request.getParameter("avatarRadio");
				
	
				byte[] salt = getNextSalt();
				String hashedPassword = get_SHA_512_SecurePassword(password, salt);
				
				if(username==""||password.equals("")||verifyPassword.equals("")||password.equals("")||(image.equals("undefined") && request.getParameter("useDefault").equals("true"))){
					response.getWriter().write("All the fields are required. Please fill the entire form.");
				}else{
				if(!password.equals(verifyPassword)){
					 response.getWriter().write("Password and verify password don't match!");
				}else{
				 if(msd.usernameExists(username)){
					 response.getWriter().write("Username is taken!");
				 }else{
					 if(request.getParameter("useDefault").equals("false")){
						 //this means the "image" value is not correct
						 image = null;
					 }
						UserProfile currUser = new UserProfile(fullName, username, hashedPassword, image, salt);
						msd.insertUser(currUser);
						session.setAttribute("currUser", currUser);
						session.setAttribute("isGuest",false);
						response.getWriter().write("valid");
				}
				}
				}
			}else if(request.getParameter("action").equals("guest")){
				String image = request.getParameter("image");
				WebServer ws = new WebServer(); 
				 if(username.equals("")){
					 response.getWriter().write("Please type a username.");
				 }else{
				 if(msd.usernameExists(username)){
					 response.getWriter().write("Username is taken by a registered user!");
				 }else if(ws.verifyGuest(username)){
					 response.getWriter().write("Username is taken by a guest!");
				 }
				 else{
						UserProfile currUser = new UserProfile(username,  image);
						session.setAttribute("currUser", currUser);
						session.setAttribute("isGuest",true);
	
						response.getWriter().write("valid");
						
				}
				 }
			
			}
		}
		msd.stop();
	}

	public String get_SHA_512_SecurePassword(String passwordToHash, byte[] salt){
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(salt);
			byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			generatedPassword = sb.toString();
		} 
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
			e.printStackTrace();
		}
		return generatedPassword;
	}
	
	public static byte[] getNextSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return salt;
	}
}
