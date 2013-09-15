/* 
*	Plays a video located in the same folder 
*   video of cityfireflies by uncoded.es 
*   Sergio Galan and Victor Diaz 
*/ 

var video = ui.addVideoView("cityfireflies.m4v", 50, 0, 600, 600);

ui.button("Play", 0, 500, ui.screenWidth, 100, function() { 
    video.play();
});

ui.button("Pause", 0, 600, ui.screenWidth, 100, function() { 
    video.pause();
});

video.onUpdate(function(ms, totalDuration) { 
    console.log(ms + " " + totalDuration);
});

ui.seekbar(0, 750, ui.screenWidth, 100, 100, 0, function(val) {
    var pos = val * video.getDuration() / 100;
    console.log(pos);
    video.seekTo(pos); 
});