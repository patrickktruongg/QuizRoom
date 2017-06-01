
var span = document.getElementsByClassName("close")[0];
var modal = document.getElementById('myModal');
var message;

$(document).ready(function(){
	$('.chatMessages-table').append('<td id="chatMessage">Connecting...</td>');
	$("#submit").click(function() {

		//chat scroll
		var height = 1000;
		$('.chatMessages').animate({scrollTop: height});

		if($("#chatmsg").val()!=""){

			var msg = {
					Type: "Message",
					Message : $("#chatmsg").val(),
					Username : $("#username").val(),
					Scope : "Global"
			};


			// Send the msg object as a JSON-formatted string.
			socket.send(JSON.stringify(msg));

			$("#chatmsg").val("");
		}
	});

	$(document).on("click",".join",function() {
		
		$.post("JoinLobbyServlet?LobbyName=" + $('.join').attr('id'),
				function(responseText){
					if(responseText =="Full"){
						modal = document.getElementById('myModal');
						$('#modal-message').html("The lobby is either full or does not exist");
						modal.style.display = "block";

					}else{
						window.location = 'Quiz_Lobby.jsp';
					}
				});
	});
	$("#lb_Div4_1").click(function() {
		var msg = {
				Type: "Join Lobby",
				Matching : "Quick Match",
				Username : $("#username").val(),

		}
		socket.send(JSON.stringify(msg));
	});
	// When the user clicks on <span> (x), close the modal
	span = document.getElementsByClassName("close")[0];
	span.onclick = function() {
		modal.style.display = "none";
	}
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}

	$(document).on("keydown","#vp_Div2_Search",function(e){
		if(e.keyCode == 13){
			searchUser();
		}
	});

	$(document).on("click","#username-search",function(){
		searchUser();
	});

});

function searchUser(){
	$("#error").html("");
	$.get("ProfileSearchServlet?username=" + $("#vp_Div2_Search").val(), function(responseText) {
		if(responseText === "valid"){
			window.location = "ProfileServlet?username=" + $("#vp_Div2_Search").val() + "&user=" + $("#username").val();
		}
		else{
			$("#error").html(responseText);
		}
	});
}

var socket;
var timerID=0;
function sendMessage() {
	socket.send("<%=name%>"+ document.myform.message.value);
	return false;
}

function connectToServer() {
	socket = new WebSocket(localStorage.getItem("socketPath"));
	socket.onopen = function (event) {
		$('.chatMessages-table').append('<td id="chatMessage">Connected</td>');		
		var msg = {
				Type: "Webpage",
				Page: "Mainpage"
		};
		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(msg));	
		var temp = {
				Type: "VerifyUsername",
				Username: $("#username").val()
		};
		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(temp));	
	}
	socket.onmessage = function (event) {
		var obj = JSON.parse(event.data);
		if(obj.Type=="Message"){
			var height = 1000;
			$('.chatMessages').animate({scrollTop: height});
			$('.chatMessages-table').append('<td id="chatMessage">[' + obj.Username + ']: ' + obj.Message + '</td>');
		}else if(obj.Type=="Lobby Created"){
			$("#lb_ScrollableTable").append('<tr id="lb_lobbyRow">'+
					'<td id="lobby">'+obj.Lobby+'</td>'+
					'<td id="host">'+obj.Host+'</td>'+
					'<td id="type">'+obj.Structure+'</td>'+
					'<td id="players">'+obj.PlayerCount+'/'+obj.MaxPlayers+'</td>'+
					'<td id = "'+obj.Lobby+'"class="join"><button type = "button" value ="Join" class="btn">Join</button></td>'+
			'</tr>')
		}else if(obj.Type=="Failure"){
			modal = document.getElementById('myModal');
			$('#modal-message').html(obj.Message);
			modal.style.display = "block";
		}else if(obj.Type=="Join Lobby"){
			var lobbyName = obj.Lobby;
			$.post("JoinLobbyServlet?LobbyName="+lobbyName,
					function(responseText){
						if(responseText =="Full"){
							modal = document.getElementById('myModal');
							$('#modal-message').html("The lobby is either full or does not exist");
							modal.style.display = "block";
						}else{
							window.location = 'Quiz_Lobby.jsp';
						}
					});
		}else if(obj.Type=="Remove Player Not Full"){
			var table = document.getElementById("lb_ScrollableTable");
			$("#lb_ScrollableTable").append('<tr id="lb_lobbyRow">'+
					'<td id="lobby">'+obj.LobbyName+'</td>'+
					'<td id="host">'+obj.Host+'</td>'+
					'<td id="type">'+obj.Structure+'</td>'+
					'<td id="players">'+obj.PlayerCount+'/'+obj.MaxPlayers+'</td>'+
					'<td id = "'+obj.LobbyName+'"class="join"><button type = "button" value ="Join" class="btn">Join</button></td>'+
			'</tr>');
		}else if(obj.Type=="Remove Player"){
			var table = document.getElementById("lb_ScrollableTable");
			for (var i = 0, row; row = table.rows[i]; i++) {
			   //iterate through rows
			    for (var j = 0, col; col = row.cells[j]; j++) {
			     //iterate through columns
				   if(table.rows[i].cells[j].innerHTML == obj.LobbyName){
					   table.rows[i].cells[j+3].innerHTML = '<td id="players">'+obj.PlayerCount+'/'+obj.MaxPlayers+'</td>';
					   break;
				   }
			   }  
			}
		}else if(obj.Type=="Player Added"){
			var table = document.getElementById("lb_ScrollableTable");
			for (var i = 0, row; row = table.rows[i]; i++) {
			   //iterate through rows
				
			    for (var j = 0, col; col = row.cells[j]; j++) {
			     //iterate through columns
				   if(table.rows[i].cells[j].innerHTML == obj.Lobby){
					   table.rows[i].cells[j+3].innerHTML = '<td id="players">'+obj.PlayerCount+'/'+obj.MaxPlayers+'</td>';
					   change = true;
				   }
			   }  
			}
		}
		if(obj.Type=="Remove Lobby"){
			var table = document.getElementById("lb_ScrollableTable");
			for (var i = 0, row; row = table.rows[i]; i++) {
				//iterate through rows
				if(table.rows[i].cells[0].innerHTML == obj.LobbyName){
					table.deleteRow(i);
					break;
					
				}	
			}
		}
	}
	socket.onclose = function(event) {
		$('.chatMessages-table').append('<td id="chatMessage">Closing...</td>');
	}
	socket.onerror = function(){
	}
}