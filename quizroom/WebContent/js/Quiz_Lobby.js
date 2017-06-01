var span = document.getElementsByClassName("close")[0];
var modal = document.getElementById('myModal');
var message;

$(document).ready(function(){
	
	var sound = new Howl({
		src: ['audio/quizroom.mp3'],
		autoplay: true,
		loop: true,
		volume: 0.5
	});
	sound.fade(0,0.5,10000);
	
	$('.chatMessages-table').append('<td id="chatMessage">Connecting...</td>');
	if($("#username").val()!=$("#lobbyHost").html()){
		$("#ql_button11").hide();
	}
	else{
		$("#ql_button12").html("Close Lobby");
	}
	$("#submit").click(function() {
		
		//chat scroll
		var height = 1000;
		$('.chatMessages').animate({scrollTop: height});
		
		if($("#chatmsg").val()!=""){
			
		var msg = {
				Type: "Message",
				Message : $("#chatmsg").val(),
				Username : $("#username").val(),
				Scope : "Lobby",
				LobbyName : $('#lobbyName').html()
		};
	

		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(msg));

		$("#chatmsg").val("");
		}
	});
	
	$(document).on('click', '#ql_button11', function() {
		if($("#username").val()==$("#lobbyHost").html()){
			var msg = {
					Type: "Start Game",
					LobbyName : $('#lobbyName').html()
			};
			socket.send(JSON.stringify(msg));
		}
	});
	$(document).on('click', '#ql_button12', function() {
		if($("#username").val()==$("#lobbyHost").html()){
			var msg = {
					Type: "CloseLobby",
					Lobby : $('#lobbyName').html()
			};
			socket.send(JSON.stringify(msg));
		}
		else{
			var msg = {
					Type: "Leave Lobby",
					Username : $('#username').val()
			};
			socket.send(JSON.stringify(msg));
		}
		
		window.location = "Lobby_Browser.jsp";
	});
	
	span = document.getElementsByClassName("close")[0];
	span.onclick = function() {
		modal.style.display = "none";
	}
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}
});

var socket;

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
				Page: "Lobby",
				Lobby: $('#lobbyName').html(),
				Username: $("#username").val(),
				Creator : "False"
				
		};
		if(msg.Username==$("#lobbyHost").val()){
			msg.Creator = "True";
		}
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
		}
		else if(obj.Type=="Player Added"){
			$('.chatMessages-table').append('<td id="chatMessage">== ' + obj.Username + ' has joined the game ==</td>');
			$("#"+obj.Username).remove();
			
			var temp;
			if(obj.Username==$("#lobbyHost").html()){
				temp = obj.Username + ' (host)';
			}
			else{
				temp = obj.Username;
			}
			$('#usertabletable').append(
					'<tr id ='+obj.Username+'> '+
				   '	 <td id="ql_picElement">'+
	                   '<img id = "userlistimage" src="ImageServlet?username='+obj.Username+'">'+
	                 ' </td>'+
	                 ' <td id="ql_userElement">'+
	                    '<font>'+temp+'</font>'+
	              '    </td>'+
	                '  </tr>'
			
			)
			
		}
		else if(obj.Type=="Remove Player"){
			$('.chatMessages-table').append('<td id="chatMessage">== ' + obj.Username + ' has left the game ==</td>');
			$("#"+obj.Username).remove();
		}
		else if(obj.Type=="Start Game"){
			window.location = "Quiz.jsp";
		}	
		else if(obj.Type=="Removed"){
			modal = document.getElementById('myModal');
			$('#modal-message').html("The host has closed the lobby, you will be redirected to the lobby browser in 5 seconds.");
			modal.style.display = "block";
			setTimeout(
					function()
					{
						window.location = "Lobby_Browser.jsp";						
					}, 5000);
		}
	}
	socket.onclose = function(event) {
	
		$('.chatMessages-table').append('<td id="chatMessage">Closing...</td>');
	}
	socket.onerror = function(){
		
	}
	
	
}