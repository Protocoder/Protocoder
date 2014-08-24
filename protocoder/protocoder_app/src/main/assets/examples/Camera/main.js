/*
* Show camera view and capture and image when button
* is clicked
* if you are in the editor an image will be shown 
* in the console 
*/ 

var camera;

//add camera 
camera = ui.addCameraView(0, 0, 0, 500, 500);

//take a picture and save it 
ui.addButton("Take pic", 0, 500, 500, 100, function() { 
    camera.takePicture("picture.png", function() {
        console.log('<img src="' + app.getProjectUrl() + 'picture.png"/>');
    });
});

//toggle flash on and off
ui.addToggle("Flash", 0, 700, 500, 100, false, function(state) { 
    camera.turnOnFlash(state);
});