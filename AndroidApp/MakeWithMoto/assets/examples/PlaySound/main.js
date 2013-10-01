/* 
*	Play a sound, supports wav, mp3, and ogg files 
*	although ogg is highly recommended 
*/ 

ui.addButton("Meow", 0, 0, 500, 100, function() { 
	media.playSound("meow.ogg");
});

