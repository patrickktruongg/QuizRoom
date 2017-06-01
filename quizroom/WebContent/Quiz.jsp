<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="icon" type="image/ico" href="img/favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/main.css" />
  <link rel="stylesheet" href="css/Chat.css" type="text/css">
  <link rel="stylesheet" href="css/Quiz.css" type="text/css">
  <script src="js/jquery-3.2.1.js" type="text/javascript"></script>
  <script src="js/howler.js" type="text/javascript"></script>
  <script src="js/Quiz.js" type="text/javascript"></script>
  	<%@ page import="data.Quiz" %>
	<%@ page import="data.QuizQuestion" %>
	<%@ page import="data.UserProfile" %>
	<%@ page import="data.Lobby" %>
	<%@ page import="network.MySQLDriver" %>
  <title>Quiz Room - Quiz</title>
</head>

<body onload="connectToServer();">

	<%
	UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
	String username = currUser.getUsername();
	Quiz quiz = (Quiz)session.getAttribute("currQuiz");
	QuizQuestion currQuestion = (QuizQuestion)session.getAttribute("currQuestion");
	Lobby lobby = (Lobby)session.getAttribute("lobby");

	%>
  <!-- 1st Parent Div : bottom Row -->
  <div id="qip_row1">

	<input type='hidden' id="username" value=<%=username%> /> 

	<input type='hidden' id="lobbyHost" value=<%=lobby.getHost()%> /> 
	

	<input type='hidden' id="lobbyName2" value=<%=lobby.getLobbyName()%> /> 

    <%@include file="html/Chat.jsp"%>

    <div id="qip_mainDiv">
      <table id="qip_mainDivTable">
        <col width="350">
        <col width="350">
        <tr>
          <td colspan="2">
            <font id = "lobbyName" size="+4"><%=lobby.getLobbyName() %></font>
          </td>
        </tr>

        <!-- Timer TODO -->
       

        <!-- Question TODO -->
        <tr>
          <td colspan="2">
            <div id="qip_questionHolder">
	            <div id="qip_questionHolder2">
	            	<font id ="question" size="+3"></font>
	            </div>
            </div>
              
         </td>
        </tr>
         <tr>
          <td colspan="2">
            <div id="myProgress">
			  <div id="myBar"></div>
			</div>
        </tr>
        <tr>
          <td id="selectionTD">
            <div id="submitChoice">
            	<button type="button" id = "choicea" class="selectionText"></button>
            </div>
            <div id="score" style="display:none;">
            </div>
          </td>
          <td id="selectionTD">
            <div id="submitChoice">
            	<button type="button"  id = "choiceb" class="selectionText"></button>
            </div>
          </td>
        </tr>
        <tr>
          <td id="selectionTD">
            <div id="submitChoice">
            	<button type="button"  id = "choicec" class="selectionText"></button>
            </div>
          </td>
          <td id="selectionTD">
            <div id="submitChoice">
            	<button type="button"  id = "choiced" class="selectionText"></button>
            </div>
          </td>
        </tr>


      </table>
    </div>


    <div id="qip_userTableContainer">
      <table id="qip_userTable">
        <tr>
          <td id="userHeader">
            <h3>Leaderboard</h3>
          </td>
        </tr>
        
        <!-- JSP LOOP TODO -->
        <tr>
          <td>
            <div id="qip_userScrollable">
              <table id = "leaderboardtable">
              </table>
            </div>
          </td>
        </tr>
        
      </table>
    </div>
  </div>

</body>

</html>