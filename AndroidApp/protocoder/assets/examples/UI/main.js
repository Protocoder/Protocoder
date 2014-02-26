/* 
*   And example with widgets that can be used 
*
*/ 
ui.setTitle("UI examples");
ui.setTitleBgColor(0, 255, 0);
ui.showTitleBar(true);

//Set up the canvas with padding all around and a white background
ui.setPadding(16, 16, 16, 16);
ui.backgroundColor(255, 255, 255);
ui.backgroundImage("patata2.png");

//Add a generic button
var btn = ui.addButton("Button", 0, 0, 500, 100, function(){
    device.vibrate(500);
    ui.jump(btn);
});

//Add a seekbar
var slider = ui.addSlider(0, 150, 500, 100, 100, 50, function(val) { 
    console.log(val);
});

//Add a label with text
ui.addLabel("I love ice cream", 0, 300, 500, 100);

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
ui.addRadioButton("radio button", 0, 900, 500, 100, true, function(val) { 
    console.log(val);
});

//Add an image
ui.addImage(0, 1050, 300, 300, "patata2.png");

//Add an image button with a background
ui.addImageButton(400, 1050, 300, 300,"patata2.png", false, function(val){ 
    console.log(val); 
});

//Add an image loaded from the web
ui.addImage(0, 1400, 500, 500, "http://www.protocoder.org/images/patata.png");

//Add an image loaded from the web
ui.addSwitch(0, 2000, 500, 100, true, function(val){ 
    console.log(val);
});