package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import data.Quiz;
import network.MySQLDriver;

@WebServlet("/QuizSearchServlet")
public class QuizSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String search = request.getParameter("Search");
		MySQLDriver msd = new MySQLDriver(); 
		msd.connect();
		Vector<Quiz> quiz = msd.searchQuiz(search);
		JSONArray quizNames = new JSONArray();
		for(Quiz q : quiz){
			quizNames.put(q.getName());
		}
		msd.stop();
		PrintWriter out = response.getWriter();
		out.print(quizNames);
	}
}