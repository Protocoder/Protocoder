ui.setTheme("BLUE");
ui.fullscreen();
ui.addCameraView(50, 50, 200, 200);
ui.addVideoView(500, 100, 500, 500);
//Set up the canvas with padding all around and a white background
ui.setPadding(16, 16, 16, 16);
ui.backgroundColor(255, 255, 255);
ui.backgroundImage("makewithmotologo.png");

//Add a generic button
ui.button("Button", 0, 0,500,100, function(){android.vibrate(500);});
//Add a seekbar
ui.seekbar(100, 50, 0, 150, 500, 100, function(){android.vibrate(500);});
//Add a label with text size of 16
ui.label("I love ice cream", 0, 300, 500, 100, 16);
//Add an edit text
ui.input("Type something here", 0, 450, 500, 100, function(){android.vibrate(500);});
//Add a toggle button
ui.toggleButton("I'm toggleable", 0, 600, 500, 100, true, function(){android.vibrate(500);});
//Add a checkbox
ui.checkbox("Check me out bro", 0, 750, 500, 100, true, function(){android.vibrate(500);});
//Add a radio
ui.radiobutton("Why am I called a radio button", 0, 900, 500, 100, true, function(){android.vibrate(500);});
//Add an image
ui.image(0, 1050, 300, 300, "makewithmotologo.png");
//Add an image button with a background
ui.imagebutton(400, 1050, 300, 300,"makewithmotologo.png", false, function(){android.vibrate(500);});
//Add an image loaded from the web
ui.webimage(0, 1400, 500, 500, "http://images.latinospost.com/data/images/full/22151/pokemon.jpg");
//Add an image loaded from the web
ui.toggleswitch(0, 2000, 500, 100, true, function(){android.vibrate(500);});

ui.webimage(0, 2000, 500,500, "https://maps.googleapis.com/maps/api/staticmap?center=-15.800513,-47.91378&zoom=11&size=200x200&sensor=false");