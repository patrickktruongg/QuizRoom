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
		<link rel="stylesheet" type="text/css" href="css/Profile.css" />
		<script src="js/jquery-3.2.1.js" type="text/javascript"></script>
		<script src="js/Profile.js" type="text/javascript"></script>
		<%@ page import="java.util.ArrayList"%>
		<%@ page import="data.UserProfile"%>
		<%@ page import="java.util.HashSet"%>
		<%@ page import="java.util.Vector"%>
		<%@ page import="data.Quiz"%>
		<title>Quiz Room - Profile</title>
	</head>
	<body>
	
	 <input type='hidden' id="username" value=<%=username%> />
	
	

		<div id="vp_row">
      
      
			<div id="vp_friendsContainer">
				<table id="vp_friendTable">
					<tr>
						<td class="tableElement">
							<h1>Friends List</h1>
						</td>
					</tr>
					<tr>
						<td>
							<div id="vp_friendScroll">
								<table id = "friends-List">
									<%
										HashSet<UserProfile> friendsList = (HashSet<UserProfile>)request.getAttribute("profileFriendsList");
										String theID;
										String theHref;
										if (friendsList != null && friendsList.size() > 0) {
											for (UserProfile friend : friendsList) {
												theID = "friend-" + friend.getUsername();
												theHref = "ProfileServlet?username=" + friend.getUsername() + "&user=" + username;
									%>
									<tr id=<%=theID%>>
										<td class="tableElement"><a href=<%=theHref %>><%=friend.getUsername()%></a></td>
									</tr>
									<%}}%>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
        
        
        
    <div id="vp_ProfileContainer">
    <table id="vp_ProfileTable">
      <tr>
        <td>
				    <img id="vp_ProfileImage" src=<%="ImageServlet?username=" + (String)request.getAttribute("profileUsername") %>>
        </td>
      </tr>
      
      <tr>
        <td>
				    <h1><%=(String)request.getAttribute("profileUsername") %></h1>
        </td>
      </tr>
      
      <tr>
        <td>
				    <h1><%=(String)request.getAttribute("profileName") %></h1>
        </td>
      </tr>
      
      <tr>
        <td>
				    <a href="Lobby_Browser.jsp">Back To Browser</a>
        </td>
      </tr>
      
      <%
		String buttonName = "Add Friend";
		
      	if(!isGuest && !((String)request.getAttribute("profileUsername")).equals(username)){
			if (friendsList != null && friendsList.size() > 0){
				for (UserProfile friend : friendsList){
					if(friend.getUsername().equals(username)){
						buttonName = "Remove Friend";
					}
				}
			}
	   %>
	      <tr id="friend-button-row">
	        <td>
				<input type="submit" value="<%=buttonName%>" id="profile-follow-button" class="friendButton"/>
	        </td>
	      </tr>
      <%}%>
      
    </table>
        </div>
			
	
        
        
        
			<div id="vp_quizContainer">
				<table id="vp_quizTable">
					<tr>
						<td class="tableElement">
							<h1>Quiz List</h1>
						</td>
					</tr>
					<tr>
						<td>
							<div id="vp_quizScroll">
								<table>
									<%
										Vector<Quiz> quizList = (Vector<Quiz>)request.getAttribute("profileQuizList");
										if (quizList != null && quizList.size() > 0)
											for (Quiz quiz : quizList){
									%>
									<tr>
                    					<td class="tableElement"><font><%=quiz.getName()%></font></td>
									</tr>
									<%} %>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</body>
</html>