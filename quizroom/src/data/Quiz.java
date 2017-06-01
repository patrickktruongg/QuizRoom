package data;

import java.io.Serializable;
import java.util.Vector;

public class Quiz implements Serializable{
	private static final long serialVersionUID = 1L;
	private String difficulty;
	private String topic;
	private String structure;
	private String name;
	private String host;
	private Vector<QuizQuestion> questions;
	int timer; 
	/**
	 * @param difficulty, topic, structure, questions, quizID
	 */
	public Quiz(String host, String difficulty, String topic, String structure, String name, Vector<QuizQuestion> questions, int quizID,int timer){
		this.difficulty = difficulty;
		this.topic = topic;
		this.structure = structure;
		this.name = name;
		this.host = host;
		this.questions = questions;
		this.timer = timer; 
		
	}
	
	//GETTERS
	/**
	 * @return questions
	 */
	public Vector<QuizQuestion> getQuestions(){
		return questions;
	}
	/**
	 * Adds a question
	 * @param question
	 */
	public void addQuestion(QuizQuestion question){
		questions.add(question);
	}
	/**
	 * @return name
	 */
	public String getName(){
		return name;
	}
	public String getHost(){
		return host;
	}
	public String getDifficulty(){
		return difficulty;
	}
	public String getTopic(){
		return topic;
	}
	public String getStructure(){
		return structure;
	}
	public int getTimer(){
		return timer;
	}
	
	//MODIFIERS
	/**
	 * Removes a question
	 * @param QuizQuestion question
	 */
	public void removeQuestion(QuizQuestion question){
		for(int i = 0; i < questions.size(); i++){
			if(questions.get(i) == question){
				questions.remove(i);
			}
		}
	}
	
	//HELPERS
	
	public QuizQuestion stringToQuestion(String question){
		for(int i = 0; i < questions.size(); i++){
			if(questions.get(i).getQuestion().equals(question)){
				return questions.get(i);
			}
		}
		return questions.get(0);
	}

	
}
