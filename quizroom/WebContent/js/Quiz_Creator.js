var socket;
$(document).ready(function(){
	var questionSize = 1;
	appendQuizField(questionSize);
	$(document).on('click', '#qm_saveQuiz', function (e) {
		var valid= true;
		//TODO FIX THIS 
		if(($("#qm_nameInput").val()===	"")||!($("input[name='difficulty']:checked").val())||!($("input[name='type']:checked").val())){
			$("#error").html("Please fill out all the required forms!");
			valid = false; 
		}
		if(($('#qm_questionNameInput1').val() === "")||($('#qm_answer1').val() === "")||( $('#qm_choiceB1').val() === "") || ($('#qm_choiceC1').val() === "") || $('#qm_choiceC1').val() === ""){
			$("#error").html("Please input at least one question.");
			valid = false; 
		}
		e.preventDefault();
		//Make quiz question objects here with a for loop and questionSize
		if(valid){
			$("#error").html("");
			var myArray = new Array();
			var question;
			var solution;
			var choiceB;
			var choiceC;
			var choiceD;
			for(var i = 0; i < questionSize; ++i){
				question = $('#qm_questionNameInput' + (i+1)).val();
				solution = $('#qm_answer' + (i+1)).val();
				choiceB = $('#qm_choiceB' + (i+1)).val();
				choiceC = $('#qm_choiceC' + (i+1)).val();
				choiceD = $('#qm_choiceD' + (i+1)).val();
				var msg = {
						Question:question,
						solution:solution,
						choiceB:choiceB,
						choiceC:choiceC,
						choiceD:choiceD
				};
				if(question && solution && choiceB && choiceC && choiceD)
					myArray.push(msg);
			}
			var type = {
					Type : "Create Quiz",		
					Name : $("#qm_nameInput").val(),
					Subject :$( "#group1_subject" ).val(),
					Difficulty :  $("input[name='difficulty']:checked").val(),			   
					Structure : $("input[name='type']:checked").val(),
					Creator : $("#username").val(),
					Timer : 15,
					Array : myArray
			};
			var json = JSON.stringify(type);
			socket.send(json);
		}
	});
	$(document).on('click', '#qm_addQuestion', function () {
		++questionSize;
		appendQuizField(questionSize);
		var height = questionSize*300;
		$('#qm_row2').animate({scrollTop: height}); 
	});
	$(document).on('click', '#qm_backToBrowser', function () {
		window.location="Lobby_Browser.jsp";
	});
});

function appendQuizField(qNum) {
	$('#qm_row2').append(
			'<div id="qm_form2Holder">' +
			'<div id="qm_form2_top">' + 
			'<h2 id="qm_questionTitle">Question ' + qNum + ':</h2>' + 
			'<input type="text" name="questionName" class="rnd" placeholder="  Enter Question" id="qm_questionNameInput' + qNum + '">' + 
			'</div>' + 
			'<div id="qm_form2_middle">' + 
			'<input type="text" name="questionName" class="rnd" placeholder="  Enter Solution" id="qm_answer' + qNum + '">' + 
			'<input type="text" name="questionName" class="rnd" placeholder="  Enter Choice 2" id="qm_choiceB' + qNum + '">' + 
			'</div>' + 
			'<div id="qm_form2_bottom">' + 
			'<input type="text" name="questionName" class="rnd" placeholder="  Enter Choice 3" id="qm_choiceC' + qNum + '">' + 
			'<input type="text" name="questionName" class="rnd" placeholder="  Enter Choice 4" id="qm_choiceD' + qNum + '">' + 
			'</div>' + 
			'</div>'
	);
}

function connectToServer() {	
	socket = new WebSocket(localStorage.getItem("socketPath"));
	
	socket.onmessage = function (event) {
		var obj = JSON.parse(event.data);
		if(obj.Type == "Error"){
			$("#error").html("Quiz name is taken!");
		}
		else if(obj.Type == "Success"){
			window.location.replace("Lobby_Browser.jsp");
		}
	}
}