var uploadedFile = null;
var span = document.getElementsByClassName("close")[0];
var modal = document.getElementById('myModal');
var message;

$(document).ready(function(){
	$('.container').load("html/ButtonChoices.html");
	
//	var IP = window.prompt("Please input the host's IP:","localhost");
//	
//	while(IP == null || IP == ""){
//		IP = window.prompt("Please input the host's IP:","localhost");
//	}
	
	var socketPath = "ws://" + location.hostname + ":8080/quiz-room/ws";
	
	localStorage.setItem("socketPath", socketPath);
	
	//buttons to navigate menu
	$(document).on('click', '#Login-Button-Choice', function () {
		$('.container').load("html/Login.html", function(e){
			$("input").attr("required", true);
			$('input').keyup(function(e){
				if(e.keyCode == 13){
					loadNextPageAsUser(e);
				}
			});
		});
	});

	$(document).on('click', '#SignUp-Button-Choice', function () {
		$('.container').load("html/SignUp.html", function(e){
			$("input").attr("required", true);
			$('input').keyup(function(e){
				if(e.keyCode == 13){
					loadNextPageAsNewUser(e, uploadedFile);
				}
			});
			
			window.URL = window.URL || window.webkitURL;
			var elBrowse = document.getElementById("file-input"),
			    elPreview = document.getElementById("preview"),
			    useBlob = false && window.URL;

			function readImage (file) {
				var reader = new FileReader();
				reader.addEventListener("load", function () {
					var image  = new Image();
					image.addEventListener("load", function () {
						var imageInfo = file.name +' '+
						image.width +'×'+
						image.height +' '+
						file.type +' '+
						Math.round(file.size/1024) +'KB';
						if(file.size/1024 > 512){
							alert("File size too large: " + Math.round(file.size/1024) + " KB (limit 512 KB)");
							return;
						}
						else{
							elPreview.appendChild(this);
							elPreview.insertAdjacentHTML("beforeend", imageInfo +'<br/>');
							if (useBlob) {
								window.URL.revokeObjectURL(image.src);
							}
							uploadedFile = file;
						}
					});
					image.src = useBlob ? window.URL.createObjectURL(file) : reader.result;
				});
				reader.readAsDataURL(file);  
			}
			elBrowse.addEventListener("change", function() {
				var files = this.files, errors = "";
				if (!files) {
					errors += "File upload not supported by your browser.";
				}
				if (files && files[0]) {
					for(var i=0; i<files.length; i++) {
						var file = files[i];
						if ( (/\.(png|jpeg|jpg|gif)$/i).test(file.name) ) {
							readImage(file); 
						} 
						else {
							errors += file.name +" Unsupported Image extension\n";  
						}
					}
				}
				// Handle errors
				if (errors) {
					alert(errors); 
				}
			});
		});
	});
	$(document).on('click', '#Guest-Button-Choice', function () {
		$('.container').load("html/GuestLogin.html", function(e){
			$("input").attr("required", true);
			$('input').keyup(function(e){
				if(e.keyCode == 13){
					loadNextPageAsGuest(e);
				}
			});
		});
	});
	$(document).on('click', '.back-button', function () {
		$('.container').load("html/ButtonChoices.html"); 
	});
	
	
	$(document).on('click', '#Credit-Button-Choice', function () {
		modal = document.getElementById('myModal');
		$('#modal-message').html("Credits " +
				'<div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>' +
				"<p> Login insired by https://codepen.io/caporta</br>/pen/XbWgGP </p>" +
				"<p>Music by Vaso Katanic</p>" +
				"<p>Special Thanks to Sondhayni Murmu</p>" +
				"" +
				"" +
				"");
		modal.style.display = "block";
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

//buttons to send to servlet
$(document).on('click', '.login-button-command', function (e) {
	loadNextPageAsUser(e);
});

$(document).on('click', '.signup-button-command', function (e) {
	loadNextPageAsNewUser(e, uploadedFile);
});

$(document).on('click', '.guest-button-command', function (e) {
	loadNextPageAsGuest(e);
});

function loadNextPageAsUser(e){
	$("#error").html("");
	e.preventDefault();
    $.get("WelcomeServlet?username=" + $("#username_input").val() + "&password=" +$("#password_input").val() + "&action=login", function(responseText) {
    	if(responseText == "valid"){
    		window.location = "Lobby_Browser.jsp";
    	}
    	else{
       		$("#error").html(responseText);
    	}
    });
}

function loadNextPageAsNewUser(e, uploadedFile){
	if(uploadedFile == null){
		$("#error").html("");
		e.preventDefault();
	    $.get("WelcomeServlet?username=" + $("#username_input").val() + "&password=" +$("#password_input").val() + "&vpassword=" + $("#verify_password_input").val() + "&name=" + $("#name_username_input").val() + "&action=signup"+ "&avatarRadio=" + $("input[name='avatarRadio']:checked").val() + "&useDefault=true", function(responseText) {
	    	if(responseText == "valid"){
	    		window.location = "Lobby_Browser.jsp";
	    	}
	    	else{
	       		$("#error").html(responseText);
	    	}
	    });
	}
	else{
		$("#error").html("");
		e.preventDefault();
	    $.get("WelcomeServlet?username=" + $("#username_input").val() + "&password=" +$("#password_input").val() + "&vpassword=" + $("#verify_password_input").val() + "&name=" + $("#name_username_input").val() + "&action=signup"+ "&avatarRadio=" + $("input[name='avatarRadio']:checked").val() + "&useDefault=false", function(responseText) {
	    	if(responseText == "valid"){
	    		var formData = new FormData();
	    	    formData.append("file", uploadedFile);
	
	    	    var xhr = new XMLHttpRequest();
	    	    xhr.open("POST", "uploadServlet", false);
	    	    xhr.send(formData);
	    		window.location = "Lobby_Browser.jsp";
	    	}
	    	else{
	       		$("#error").html(responseText);
	    	}
	    });
	}
}

function loadNextPageAsGuest(e){
	$("#error").html("");
	e.preventDefault();
    $.get("WelcomeServlet?username=" + $("#username_input").val() + "&action=guest", function(responseText) {
    	if(responseText == "valid"){
    		window.location = "Lobby_Browser.jsp";
    	}
    	else{
       		$("#error").html(responseText);
    	}
    });
}

$(document).on('click', '#file-input', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar1', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar2', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar3', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar4', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar5', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar6', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar7', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar8', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
});

$(document).on('click', '#avatar9', function () {
	$("#avatar1-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar2-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar3-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar4-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar5-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar6-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar7-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar8-image").css({"-webkit-filter":"opacity(100%)","filter":"opacity(100%)"});
	$("#avatar9-image").css({"-webkit-filter":"opacity(50%)","filter":"opacity(50%)"});
});