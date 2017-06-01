<html>
<%
	UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
	String username = currUser.getUsername();
	
	%>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="icon" type="image/ico" href="img/favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/main.css" />
  <link rel="stylesheet" href="css/Lobby_Creator.css" type="text/css">
  <script src="js/jquery-3.2.1.js" type="text/javascript"></script>
  <script src="js/Lobby_Creator.js" type="text/javascript"></script>
		<%@ page import="java.util.ArrayList"%>
		<%@ page import="data.UserProfile"%>
		<%@ page import="java.util.HashSet"%>
		<%@ page import="java.util.Vector"%>
		<%@ page import="data.Quiz"%>
  <title>Quiz Room - Lobby Creator</title>
</head>

<body>
  	<input type='hidden' id="username" value=<%=username%> />

  <!-- First Parent Div : Top Row -->
  <div id="lm_row1">
    <!-- Second Div - table -->
    <div id="lm_Div2">
    

        <div id="lm_quizName">
          <h1>Lobby Name:</h1	>
          <input type="text" name="Quiz Name" placeholder="  Enter Lobby Name" id="lm_nameInput" class="rnd">
        </div>

        <fieldset id="group1_subject">
          <font> Subject  </font> <br>
          <font id="quizSubject"></font>
        </fieldset>


        <fieldset id="group2_numberPlayers">
        
          <font>Player Limit </font> <br>
          <input type="number" name="Max Input" placeholder="Enter Player Limit" id="lm_maxInput">

        </fieldset>


        <fieldset id="group3_structure">
          <font>Game Structure </font> <br>
          <font id="quizStructure"></font>

        </fieldset>

        <fieldset id="group4_type">
          <font>Author </font> <br>
          <font id="quizAuthor"></font>

        </fieldset>

        <fieldset id="group5_difficulty">
          <font>Difficulty  </font><br>
          <font id="quizDifficulty"></font>

        </fieldset>
		<p id="error"></p>
        <input type="submit" value="Make Lobby" id="lm_makeLobby" class ="btn">
		<button type="button" id="lm_backToBrowser" class="btn">Back to Lobby Browser</button>

    </div>
    
      
  </div>

  <!-- Second Parent Div : bottom Row -->
  <div id="lm_row2">


    <div id="lm_savedContainer"> 
      <table id="lm_savedTable">
        <tr id="lm_saveTable-title">
          <td>
            <h2>Saved Quizzes</h2>
          </td>
        </tr>
        <tr>
          <td>
            <h2>Available Quizzes</h2>
          </td>
        </tr>
        
        <tr>
          <td>
            <div class="scrollableDiv">
              <table>
                <%
					Vector<Quiz> quizList = (Vector<Quiz>)session.getAttribute("quizList");
					if (quizList != null && quizList.size() > 0) {
						for (Quiz quiz : quizList) {
				%>
				<tr>
		            <td class="tableElement"><font><%=quiz.getName()%></font></td><td><button type="button" class="quizChoose" id="<%=quiz.getName()%>">Choose Quiz!</button></td>
				</tr>
				<%}}%>
              </table>
            </div>
          </td>
        </tr>
        
      </table>
      
      
    
    </div>
    
    
    
    
    
    
    <div id="lm_defaultContainer"> 
   		 <br/>
         <h1 id="searchTitle">Search Quiz:</h1>
         <input id = "searchbar" type="text" name="search" placeholder="Search..." class="rnd"></input>
         <input type="button" name="search" value = "Search" id = "submit" class="btn"></input>
		 <br/>
		 <br/>
      <table id="lm_defaultTable">
        <tr>
          <td>
            <h2>Available Quizzes</h2>
          </td>
        </tr>
        
        
         <tr>
          <td>
            <div class="scrollableDiv">
              <table id="quizSearchList">
                  
              </table>
            </div>
          </td>
        </tr>
        
      </table>
    
    </div>
    
    
    
  </div>

</body>

</html>