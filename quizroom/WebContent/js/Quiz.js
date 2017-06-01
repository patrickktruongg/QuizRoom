
var buttonSelected;
var width;
var leaderboard;
$(document).ready(function(){
	
	var sound = new Howl({
		src: ['audio/quizroom.mp3'],
		autoplay: true,
		loop: true,
		volume: 0.5
	});
	sound.fade(0,0.5,10000);
	
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
				Scope : "Quiz",
				LobbyName : $('#lobbyName2').val()
		};

		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(msg));
		$("#chatmsg").val("");
		}
	});
	$(document).on('click', '.selectionText', function(){
		$('.selectionText').prop("disabled",true);
		buttonSelected = $(this);
	   $(this).css('background-color', 'yellow');
		var msg = {
				Type: "Guess",
				Username : $("#username").val(),
				Guess: $(this).html(),
				Time: width
		};

		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(msg));
	});
	
	$(document).on('click', '#leaveGame', function(){
		window.location = "Lobby_Browser.jsp";
	});
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
				Page: "Quiz",
				Lobby: $('#lobbyName2').val(),
				Username: $("#username").val(),
				Creator : "False"
				
		};
	    
		// Send the msg object as a JSON-formatted string.
		socket.send(JSON.stringify(msg));
		 if($("#username").val()==$("#lobbyHost").val()){
	    	  var msg = {
	  				Type: "Start Quiz",
	  				Lobby: $('#lobbyName2').val()

	  				
	  		};
    	    setTimeout(function(){
//				$('#preloader').fadeOut('slow',function(){
//					$(this).remove();
//				});
				socket.send(JSON.stringify(msg));
			}, 2000);
	      }
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
		}else if(obj.Type=="Question"){
			$('#question').html(obj.Question);
			$('#choicea').html(obj.Choice1);
			$('#choiceb').html(obj.Choice2);
			$('#choicec').html(obj.Choice3);
			$('#choiced').html(obj.Choice4);
			$('.selectionText').prop("disabled",false);
			move();
		}else if(obj.Type=="Leaderboard"){
			 
			leaderboard = obj.Leaderboard;
			 $('#leaderboardtable').html(''); 
			for(var i = 0 ; i<leaderboard.length;i++){
				
	         $('#leaderboardtable').append('<tr >'+
              '  <td id="userElement">'+
                 ' <font>' + leaderboard[i] + '</font>'+
                '</td>'+
              '</tr>');
			}
		
		}
		else if(obj.Type=="Correct Answer"){
			$('.selectionText').prop("disabled",true);
			if(buttonSelected){
				if(buttonSelected.html()!=obj.CorrectAnswer){
					buttonSelected.css("background-color", "red");
				}
			}
			if($("#choicea").html()==obj.CorrectAnswer){
				$('#choicea').css("background-color", "green");
			}
			if($("#choiceb").html()==obj.CorrectAnswer){
				$('#choiceb').css("background-color", "green");				
			}
			if($("#choicec").html()==obj.CorrectAnswer){
				$('#choicec').css("background-color", "green");				
			}
			if($("#choiced").html()==obj.CorrectAnswer){
				$('#choiced').css("background-color", "green");				
			}
			setTimeout(
					function()
					{
						$('.selectionText').css('background-color', 'white');
						buttonSelected = null; 
						if($("#username").val()==$("#lobbyHost").val()){
							var msg = {
									Type : "Request Quiz",
									LobbyName : $('#lobbyName2').val()
							}
							socket.send(JSON.stringify(msg));
						}
						//do something special
					}, 2000);
	

		
		}else if(obj.Type=="Game Over"){
		
			var firstPlace = leaderboard[0];
			var firstPlaceArray = firstPlace.split(" ");
			var firstPlaceUsername = '';
			for(var i = 1; i < (firstPlaceArray.length-1); ++i)
				firstPlaceUsername += firstPlaceArray[i];
			var secondPlace;
			var thirdPlace;
			if(leaderboard[1]){
				secondPlace = leaderboard[1];
				var secondPlaceArray = secondPlace.split(" ");
				var secondPlaceUsername = '';
				for(var i = 1; i < (secondPlaceArray.length-1); ++i)
					secondPlaceUsername += secondPlaceArray[i];
			}
			if(leaderboard[2]){
				thirdPlace = leaderboard[2];
				var thirdPlaceArray = thirdPlace.split(" ");
				var thirdPlaceUsername = '';
				for(var i = 1; i < (thirdPlaceArray.length-1); ++i)
					thirdPlaceUsername += thirdPlaceArray[i];
			}
			if(leaderboard[2]){
				$('#qip_mainDiv').html('<div id="placesImages"><img id="secondPlace" class="placeImages" title="' + secondPlaceUsername + '" src=ImageServlet?username=' + secondPlaceUsername + ' />' +
						'<img id="firstPlace" class="placeImages" title="' + firstPlaceUsername + '"  src=ImageServlet?username=' + firstPlaceUsername + ' />' +
						'<img id="thirdPlace" class="placeImages" title="' + thirdPlaceUsername + '"  src=ImageServlet?username=' + thirdPlaceUsername + ' /></div>' +
						'<div id="placesButton">'+
						'<button type="button" id="leaveGame">Leave Game</button>'+
						'</div>'
				);
			}
			else if(leaderboard[1]){
				$('#qip_mainDiv').html('<div id="placesImages"><img id="secondPlace" class="placeImages" title="' + secondPlaceUsername + '"  src=ImageServlet?username=' + secondPlaceUsername + ' />' +
						'<img id="firstPlace" class="placeImages" title="' + firstPlaceUsername + '"  src=ImageServlet?username=' + firstPlaceUsername +  ' /></div>' +
						'<div id="placesButton">'+
						'<button type="button" id="leaveGame">Leave Game</button>'+
						'</div>'
				);
			}
			else{
				$('#qip_mainDiv').html('<div id="placesImages"><img id="firstPlace" class="placeImages" title="' + firstPlaceUsername + '"  src=ImageServlet?username=' + firstPlaceUsername + ' /></div>' +
						'<div id="placesButton">'+
						'<button type="button" id="leaveGame">Leave Game</button>'+
						'</div>'
				);
			}
			//TODO add rate quiz
		}

	}
	socket.onclose = function(event) {
		$('.chatMessages-table').append('<td id="chatMessage">Closing...</td>');
	}
}
var temp = 100; 
function move() {
	  var elem = document.getElementById("myBar");   
	  width = 0.0;
	  var id = setInterval(frame, 15);
	
	  function frame() {
	    if (width >= 100) {
	      clearInterval(id);
	      if(temp==100){
		      if($("#username").val()==$("#lobbyHost").val()){
		      var msg = {
						Type: "Round Over",
						LobbyName : $('#lobbyName2').val()
				};
		      socket.send(JSON.stringify(msg));
		      }
	      }
	      temp = 101; 
	    } else {
	    	temp = 100; 
	      width += 0.1; 
	      elem.style.width = width + '%'; 
	    }
	  }
	}