<% 
	UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
	String username = currUser.getUsername();

%>





<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="icon" type="image/ico" href="img/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="css/main.css" />
		<link rel="stylesheet" href="css/Quiz_Creator.css" type="text/css">
		<script src="js/jquery-3.2.1.js" type="text/javascript"></script>
		<script src="js/Quiz_Creator.js" type="text/javascript"></script>
		<%@ page import="data.UserProfile"%>
		<title>Quiz Room - Quiz Creator</title>
	</head>
	<body onload="connectToServer();" >
	
	
		    <!-- Second Div - table -->
		    <div id="qm_row1">
          
          
	        	<div id="qm_quizName">
	          		<h1>Quiz Name:</h1>
	          		<input type="text" name="Quiz Name" placeholder="  Enter Quiz Name" id="qm_nameInput" class="rnd">
	        	</div>
          	          		<input type="hidden" value = <%=username%> id="username">
          
          
            <div id="group1_container">
              <div id="subjectContainer">
              <font>Subject:</font>
		          <select id="group1_subject">
		          	<option value="Computer Science">Computer Science</option>
		          	<option value="History">History</option>
		         	<option value="Pop Culture">Pop Culture</option>
		         	<option value="Math">Math</option>
		         	<option value="Chemistry">Chemistry</option>
		         	<option value="Literature">Literature</option>
		         	<option value="Science">Science</option>
		         	<option value="Music">Music</option>
		         	<option value="Movies">Movies</option>
		         	<option value="Video Games">Video Games</option>
		         	<option value="Art">Art</option>
		         	<option value="Geography">Geography</option>
		         	<option value="History">History</option>
		         	<option value="Language">Language</option>
		         	<option value="Biology">Biology</option>
		         	<option value="Technology">Technology</option>
		         	<option value="Food">Food</option>
		         	<option value="Physics">Physics</option>
		         	<option value="Religion">Religion</option>
		         	<option value="Books">Books</option>
		         	<option value="Comics">Comics</option>
		         	<option value="Sports">Sports</option>
		         	<option value="Trivia">Trivia</option>
		         	<option value="Other">Other</option>
		          </select>
              </div>
            </div>
            
          
            <div id="group2_container">
		        <fieldset id="group2_type">
              <font>Game Structure</font> <br>
              <input type="radio" value="Competative" name="type"><font>Competative</font><br>
              <input type="radio" value="Casual" name="type"><font>Casual</font>
		        </fieldset>
            </div>
          
            <div id="group3_container">
		        <fieldset id="group3_difficulty">
              <font>Difficulty</font> <br>
              <input type="radio" value="Easy" name="difficulty"><font>Easy</font><br>
              <input type="radio" value="Normal" name="difficulty"><font>Normal</font><br>
		          	<input type="radio" value="Hard" name="difficulty"><font>Hard</font><br>
              <input type="radio" value="Very Hard" name="difficulty"><font>Very Hard</font>
		        </fieldset>
            </div>
          
            
          
          
		    </div>
      
   
	  	<!-- new questions appear here! -->
    
    <div id="qm_row2">
			
      
      
    </div>
	  	
		
    
		<div>
			<p id="error"></p>
		</div>
    
    <div id="new_container">
			   <button type="button" id="qm_addQuestion" class="btn">Add Another Question</button>
		</div>
    
    <div id="leave_container">
			   <button type="button" id="qm_backToBrowser" class="btn">Back to Lobby Browser</button>
		</div>
    
		<div>
			<button type="button" id="qm_saveQuiz" class="btn">Save Quiz</button>
		</div>
    
    
    
    
	</body>
</html>