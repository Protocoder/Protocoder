/* 
*   And example with widgets that can be used 
*
*/ 

//ui.setTheme("BLUE");
ui.setTitle("UI examples");
ui.setTitleBgColor(0, 255, 0);
ui.showTitleBar(true);

//Set up the canvas with padding all around and a white background
ui.setPadding(16, 16, 16, 16);
ui.backgroundColor(255, 255, 255);
ui.backgroundImage("makewithmotologo.png");

//Add a generic button
ui.addButton("Button", 0, 0,500,100, function(){
    android.vibrate(500);
});


//Add a seekbar
ui.addSlider(0, 150, 500, 100, 100, 50, function(val) { 
    console.log(val);
});

//Add a label with text size of 16
ui.addLabel("I love ice cream", 0, 300, 500, 100, 16);

//Add an edit text
ui.addInput("Type something here", 0, 450, 500, 100, function(val){ 
    console.log(val);
});

//Add a toggle button
ui.addToggle("I'm toggleable", 0, 600, 500, 100, true, function(val) { 
    console.log(val);
});

//Add a checkbox
ui.addCheckbox("Check me out bro", 0, 750, 500, 100, true, function(val) { 
    console.log(val);
});

//Add a radio
ui.addRadioButton("Why am I called a radio button", 0, 900, 500, 100, true, function(val){ 
    console.log(val);
});

//Add an image
ui.addImage(0, 1050, 300, 300, "makewithmotologo.png");

//Add an image button with a background
ui.addImageButton(400, 1050, 300, 300,"makewithmotologo.png", false, function(val){ 
    console.log(val); 
});

//Add an image loaded from the web
ui.addImage(0, 1400, 500, 500, "http://images.latinospost.com/data/images/full/22151/pokemon.jpg");

//Add an image loaded from the web
ui.addSwitch(0, 2000, 500, 100, true, function(val){ 
    console.log(val);
});

ui.addImage(0, 2000, 500,500, "https://maps.googleapis.com/maps/api/staticmap?center=-15.800513,-47.91378&zoom=11&size=200x200&sensor=false");

//Add a circular seekbar
ui.addKnob(0, 2500, 350, 350, function(val) {
    console.log(val);
});