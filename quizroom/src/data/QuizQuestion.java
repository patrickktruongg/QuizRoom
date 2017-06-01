package data;

import java.io.Serializable;
import java.util.Vector;

public class QuizQuestion implements Serializable{
	private static final long serialVersionUID = 2L;
	private String question;
	private Vector<String> choices;
	private String correctChoice;
	int qqIndex; 
	/**
	 * 
	 * @param question, difficulty, topic, choices, correctChoice
	 */
	public QuizQuestion(String question, Vector<String> choices, String correctChoice){
		this.question = question;
		this.choices = choices;
		this.correctChoice = correctChoice;
		qqIndex = 0; 
	}
	
	//GETTERS

	/**
	 * @return question
	 */
	public String getQuestion(){
		return question;
	}
	/**
	 * @return choices
	 */
	public Vector<String> getChoices(){
		return choices;
	}
	/**
	 * @return correct choice
	 */
	public String getCorrectChoice(){
		return correctChoice;
	}
	/**
	 * @return number of choices of a question
	 */
	public int getNumChoices(){
		return choices.size();
	}
	//HELPERS
	/**
	 * @param guess
	 * @return whether answer is right or wrong
	 */
	public boolean guessAnswer(String guess){
		if(guess.equals(correctChoice)){
			return true;
		}
		else{
			return false;
		}
	}

}
