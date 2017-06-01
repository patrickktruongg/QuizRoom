<% 
	UserProfile currUser =  (UserProfile)session.getAttribute("currUser");  
	String username = currUser.getUsername();
	String name = currUser.getName();
	boolean isGuest = (boolean)session.getAttribute("isGuest");
%>



<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="icon" type="image/ico" href="img/favicon.ico" />
		<link rel="stylesheet" type="text/css" href="css/main.css" />
		<link rel="stylesheet" href="css/Lobby_Browser.css" type="text/css">
		<link rel="stylesheet" href="css/Chat.css" type="text/css">
		<link rel="stylesheet" href="css/Popup.css" type="text/css">
		<script src="js/jquery-3.2.1.js" type="text/javascript"></script>
		<script src="js/Lobby_Browser.js" type="text/javascript"></script>
		<%@ page import="data.UserProfile"%>		
		<%@ page import="data.Lobby"%>
		<%@ page import="network.WebServer"%>
		<%@page import ="java.util.LinkedList"%>
        <%@page import ="java.util.Queue"%>
		<title>Quiz Room - Browser</title>
	</head>
	<body onload="connectToServer();">
	  <!-- First Parent Div : Top Row -->
	  <div id="lb_row1">
	    <!-- First Div - Profile Picture -->
	    <table id="lb_table">
	    	<tr>
	    		<td>
				    <div id="lb_Div0">
				    	<h1 class = "lb_Div1_Info"><%=username %></h1>
				    	<%
				    		if(name != null){
				    	%>
				    	<h1 class = "lb_Div1_Info"><%=name %></h1>
				    	<%}
		            	if(!isGuest){
			            %>
				    	<a href = <%="ProfileServlet?username=" +  username + "&user=" + username%>>
				    		<font class="lb_Div1_Info">View Profile</font>
				    	</a>
			            <%
			           	}
			            %>
				    </div>
	    		</td>
	    		<td>
				    <div id="lb_Div1">
				    <%
	            	if(!isGuest){
	                %>
					    <a href = <%="ProfileServlet?username=" +  username + "&user=" + username%>>
					    	<img id="lb_Div1_ProfileImage" src=<%="ImageServlet?username=" + username %>>
					    </a>
	                <%
	            	}
	            	else{
	                %>
	                	<img id="lb_Div1_ProfileImage" src=<%="ImageServlet?username=" + username %>>
	                <%
	            	}
	                %>
				    </div>
	    		</td>
	    	</tr>
	    </table>
	
	    <!-- Second Div - Return Button + Friend Search +main menu -->
	    <div id="lb_Div2">
	      <table style="text-align:center; width: 100%;">
	        <tr>
	          <td>
	            <a href="Logout">
	              <div id="lb_Div2_1">
	                <h1>Logout</h1>
	              </div>
	            </a>
	          </td>
	        </tr>
	
	        <tr>
	          <td>
	            <div id="lb_Div2_2">
	              <input id="vp_Div2_Search" type="text" name="lbSearchFriend" placeholder="  User Search" class="rnd"/> <input id="username-search" type="submit" value="Search" class="btn" />
	            	<p id="error"></p>
	            </div>
	          </td>
	        </tr>
	      </table>
	    </div>
	
	    <!-- Third Div - Custom Quiz Button + Create Lobby -->
	    <div id="lb_Div3">
	      <table style="text-align:center; width: 100%;">
	        <tr>
	          <td>
	            <%
	            	if(!isGuest){
	            %>
	              <a href="LobbyCreatorServlet">
	                <div id="lb_Div3_1">
	                  <h1>Host Lobby</h1>
	                </div>
	              </a>
	            <%
	            	}
	            	else{
	            %>
	            	<div id="lb_Div3_1">
	                  <h2>Hosting a Lobby is not available to guests</h2>
	                </div>
	            <%
	            	}
	            %>
	          </td>
	        </tr>
	
	        <tr>
	          <td>
	              <%
	            	if(!isGuest){
	              %>
	              <a href="Quiz_Creator.jsp">
	                <div id="lb_Div3_2">
	                  <h1>Create A Quiz</h1>
	                </div>
	              </a>
	              <%
	            	}
	            	else{
	              %>
	                <div id="lb_Div3_2">
	                  <h2>Creating a Quiz is not available to guests</h2>
	                </div>
	              <%
	            	}
	              %>
	          </td>
	        </tr>
	
	      </table>
	    </div>
	
	    <!-- Fourth Div - Quickplay Button -->
	    <div id="lb_Div4">
	      <div id="lb_Div4_1">
	        <h1>Quick Play!</h1>
	      </div>
	    </div>
	  </div>
	
	
	  <div id="lb_row2">
	    <input type='hidden' id="username" value=<%=username%> /> 
		<%@include file="html/Chat.jsp"%>
		
		<%@include file="html/Popup.html"%>
	
	    <div id="lb_lobbyContainer">
	      <table id="lb_lobbyTable">
	
	        <tr id="lb_lobbyTableHeader">
	          <td id="lobby2">Lobby Name</td>
	          <td id="host2">Host</td>
	          <td id="type2">Game Structure</td>
	          <td id="players2">Players</td>
	          <td id="join2">Join Game</td>
	        </tr>
	        <tr>
	          <td colspan="5">
	            <div id="lb_ScrollableTableContainer">
	              <table id="lb_ScrollableTable">
	                <% WebServer ws = new WebServer(); 
	                   Queue<Lobby> lobby = ws.getLobby(); 
	                   for(Lobby temp : lobby){

	                	   if(!temp.isEmpty()||!temp.isFull()){

	                	   %><tr id="lb_lobbyRow">
	                       <td id="lobby"><%=temp.getLobbyName() %></td>
	                       <td id="host"><%=temp.getHost() %></td>
	                       <td id="type"><%=temp.getStructure()%></td>
	                       <td id="players"><%=temp.getJoinedPlayerCount()+"/"+temp.getPlayerCount() %></td>
	                       <td id="<%=temp.getLobbyName()%>" class = "join"><input type = "button" value ="Join" class="btn"/></td>
	                     </tr><%
	                   }}
	                %>
	              </table>
	            </div>
	          </td>
	        </tr>
	      </table>
	    </div>
	
	
	  </div>
	</body>

</html>