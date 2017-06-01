<% 
	UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
	String username = ((UserProfile)session.getAttribute("currUser")).getUsername();
	Lobby lobby = ((Lobby)session.getAttribute("lobby"));
	String host = lobby.getHost();
%>

<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="icon" type="image/ico" href="img/favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/main.css" />
  <link rel="stylesheet" href="css/Quiz_Lobby.css" type="text/css">
  <link rel="stylesheet" href="css/Chat.css" type="text/css">
  <link rel="stylesheet" href="css/Popup.css" type="text/css">
  <script src="js/jquery-3.2.1.js" type="text/javascript"></script>  
  <script src="js/howler.js" type="text/javascript"></script>
  <script src="js/Quiz_Lobby.js" type="text/javascript"></script>
  <%@ page import="data.Lobby"%>  
  <%@ page import="data.UserProfile"%>
  <%@include file="html/Popup.html"%>
  <title>Quiz Room - Quiz Lobby</title>
</head>

<body onload="connectToServer();">


  <!-- First Parent Div : Top Row -->
  <div id="ql_row1">

    <!-- Second Div - table -->
    <div id="ql_Div2">
        <div id="ql_quizName">
          <font size="+4">Lobby:</font>
          <font id = "lobbyName" size="+4"><%=(String)session.getAttribute("lobbyName")%></font>
        </div>
        <div id="group1_subject">
          <font>Subject</font> <br>
          <font><%=(String)session.getAttribute("lobbySubject")%></font>
        </div>
        <div id="group2_numberPlayers">
          <font>Lobby Limit</font> <br>
          <font><%=session.getAttribute("lobbyPlayerCount")%></font>
        </div>
        <div id="group3_structure">
          <font>Game Structure</font> <br>
          <font><%=(String)session.getAttribute("lobbyStructure")%></font>
        </div>
        <div id="group4_type">
          <font>Author</font> <br>
          <font id = "lobbyHost"><%=(String)session.getAttribute("lobbyHost")%></font>
        </div>

        <div id="group5_difficulty">
          <font>Difficulty</font> <br>
          <font><%=(String)session.getAttribute("lobbyDifficulty")%></font>
        </div>
    </div>
  </div>


  <!-- Second Parent Div : bottom Row -->
  <div id="ql_row2">
	<input type='hidden' id="username" value=<%=username%> /> 
	<%@include file="html/Chat.jsp"%>
    
    <div id= "ql_buttons">
      
    <div id="ql_button1">
    	<button id="ql_button11" type="button" class="btn" >Start Game</button>
    </div>
    
    <div id="ql_button2">
    	<button id="ql_button12" type="button" class="btn" >Leave Lobby</button>
    </div>
    </div>
    
    <div id="ql_userListContainer"> 
      <table id="ql_userTable">
        <tr>
          <td id="ql_userTableHeader">
              <h3>Users in Lobby</h3>
          </td>
        </tr>
        
        <tr>
          <td>
            <div id="ql_userTableScroll">
              <table id = "usertabletable">
                <% 
	        	for (String user : lobby.getPlayers()) {%>
            			<tr id =<%=user%>> 
				   	 <td id="ql_picElement">
	                   <img id = "userlistimage" src="ImageServlet?username=<%=user%>">
	                 </td>
	                 <td id="ql_userElement">
	                 <%
	                 	if(user.equals(host)){
	                 %>
	                    <font><%=user+" (host)" %></font>
	                 <%
                		} else {
	                 %>
	                 	<font><%=user %></font>
	                 <%} %>
	              </td>
	               </tr>
             <%   } %> 
               
                
                
                
              </table>
            </div>
          </td>
        </tr>
        
      </table>
    </div>
    
    
  </div>

</body>

</html>