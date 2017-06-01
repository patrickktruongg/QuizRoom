$(document).ready(function(){
		function connectToServer() {
			socket = new WebSocket(localStorage.getItem("socketPath"));
			socket.onopen = function (event) {
		
				var temp = {
						Type: "VerifyUsername",
						Username: $("#username").val()
				};
				// Send the msg object as a JSON-formatted string.
				socket.send(JSON.stringify(temp));	
			}
			socket.onmessage = function (event) {
				
			}
			socket.onclose = function(event) {
			}
			socket.onerror = function(){
			}
		}
	var quizName = null;
	$(document).on('click', '#lm_backToBrowser', function () {
		window.location="Lobby_Browser.jsp";
	});
	
	$(document).on('click', '.quizChoose', function () {
		$('.quizchoose').css("background-color", "white");
		$(this).css("background-color", "#45B7E5");
		quizName = $(this).attr('id');
		$.get("QuizSelectionServlet?quizName=" + quizName, function(responseText) {
	    	$('#quizSubject').html(responseText.Subject);
	    	$('#quizStructure').html(responseText.Structure);
	    	$('#quizAuthor').html(responseText.Creator);
	    	$('#quizDifficulty').html(responseText.Difficulty);
	    });
	});
	
	$(document).on('click', '#lm_makeLobby', function () {
		var maxNum = $("#lm_maxInput").val();
		if(!($('#lm_nameInput').val())){
			$("#error").html("Please input a lobby name");
		}
		else if(!maxNum){
			$("#error").html("Please input a lobby limit");
		}else if (maxNum<1){
			$("#error").html("The minimum number of players is 1");
		}
		else if(!quizName){
			$("#error").html("Please select a quiz");
		}
		else{
		  	 $.post("GameCreatorServlet",
	 		        {
	 		          LobbyName: $('#lm_nameInput').val(), 	
	 		          NumPlayers : maxNum,
	 		          QuizName : quizName
	 		        },
	 		        function(responseText){
	 		        	if(responseText == "Exists"){
	 		        		$("#error").html("Lobby name is taken");
	 		        	}
	 		        	else if(responseText == "No Quiz"){
	 		        		$("#error").html("Please choose a quiz");
	 		        	}
	 		        	else {
	 		        		window.location = 'Quiz_Lobby.jsp';
	 		        	}
	 		        });
		}
	});
	
	$(document).on("keydown","#searchbar",function(e){
		if(e.keyCode == 13){
			searchQuiz();
		}
	});

	$(document).on("click","#submit",function(){
		searchQuiz();
	});

});

function searchQuiz(){
 	 $.post("QuizSearchServlet",
		        {
		          Search: $('#searchbar').val(), 		          
		        },
		        function(responseText){
		        	var arr =JSON.parse(responseText);
		        	$('#quizSearchList').html("");
		        	for (var i = 0; i < arr.length; ++i){
		        		$('#quizSearchList').append('<tr><td class="tableElement"><font>' + arr[i] + '</font></td><td><button type="button" class="quizChoose" id="' + arr[i] + '">Choose Quiz!</button></td></tr>');
		        	}
		        });
}