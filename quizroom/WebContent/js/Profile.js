$.urlParam = function(name){
	var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
	return results[1] || 0;
}
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
	var username = $.urlParam('username');
	var u =  $.urlParam('user');
	if(username ===  u){
		$(".friendButton").hide();
		$("#friend-button-row").hide();
	}
	$('.friendButton').click(function(e){
		e.preventDefault();

		var action = $('.friendButton').val();

		if(action === 'Remove Friend'){
			$('.friendButton').val('Add Friend');			
		} else {
			$('.friendButton').val('Remove Friend');
		}

		$.get("AddFriendServlet?action=" + action + "&otherUser=" + username);

		if(action == "Add Friend"){
			$('#friends-List').append('<tr id="friend-' + u +'"><td class="tableElement"><a href="ProfileServlet?username=' + u + '&user=' + username + '">' + u + '</a></td></tr>');
		}
		else
		{
			$("#friend-" + u).remove();
		}

	});
});