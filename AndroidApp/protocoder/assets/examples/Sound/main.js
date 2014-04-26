/* 
*	Play a sound, supports wav, mp3, and ogg files 
*	although ogg is highly recommended 
*/ 

media.setVolume(100);
ui.enableVolumeKeys(true);

ui.addButton("Meow", 0, 0, 500, 100, function() { 
	media.playSound("meow.ogg");
});

ui.addButton("Record", 0, 200, 500, 100, function() {
	media.recordAudio("recording.mp4", true);
})