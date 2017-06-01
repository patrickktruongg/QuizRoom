package Server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Quiz;
import network.MySQLDriver;
import org.json.JSONObject;

@WebServlet("/QuizSelectionServlet")
public class QuizSelectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String quizName = request.getParameter("quizName");
		
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		
		Quiz quiz = msd.getQuiz(quizName);
		
		String quizSubject = quiz.getTopic();
		String quizStructure = quiz.getStructure();
		String quizCreator = quiz.getHost();
		String quizDifficulty = quiz.getDifficulty();

		JSONObject jsObject = new JSONObject(); 
		jsObject.put("Subject", quizSubject);
		jsObject.put("Structure", quizStructure);
		jsObject.put("Creator", quizCreator);
		jsObject.put("Difficulty", quizDifficulty);
		msd.stop();
		response.setContentType("application/json");
		// Get the printwriter object from response to write the required json object to the output stream      
		PrintWriter out = response.getWriter();
		// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
		out.print(jsObject);
		out.flush(); 
	    // Write response body.

	}
}
