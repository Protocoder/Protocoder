/*
* Show camera view and capture and image when button
* is clicked
* if you are in the editor an image will be shown 
* in the console 
*/ 

var camera;

//add camera 
camera = ui.addCameraView(0, 50, 50, 200, 200);

//take a picture and save it 
ui.addButton("Take pic", 0, 300, 500, 100, function() { 
    camera.takePicture("picture.png", function() {
        console.log('<img src="' + app.getProjectURL() + 'picture.png"/>');
    });
});