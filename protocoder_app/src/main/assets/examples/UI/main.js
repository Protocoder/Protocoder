/* 
*   And example with widgets that can be used 
*
*/ 

//ui.setToolbarTitle("UI examples");
ui.setToolbarBgColor(0, 255, 0, 255);
//ui.showToolbar(true);
ui.allowScroll(true);

//ui.showVirtualKeys(true);

//Set up the canvas with padding all around and a white background
//ui.setPadding(16, 16, 16, 16);
ui.setBackgroundColor(255, 255, 255);
ui.setBackgroundImageTile("patata2.png");

//Add a generic button
var btn = ui.addButton("Button", 0, 0, 500, 100, function(){
    device.vibrate(500);
    ui.jump(btn);
});


ui.move(btn, 0, 0);
ui.rotate(btn, 0, 0, 0);

//Add a seekbar
var slider = ui.addSlider(0, 150, 500, 100, 100, 50, function(val) { 
    console.log(val);
});

//Add a label with text
ui.addText("I love ice cream", 0, 300, 500, 100);

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
console.log("hoalalalala");

//Add an image
ui.addImage("patata2.png", 0, 1050, 300, 300);

//Add an image button with a background
ui.addImageButton(400, 1050, 300, 300,"patata2.png", false, function(val){ 
    console.log(val); 
});

//Add an image loaded from the web
ui.addImage("http://www.protocoder.org/images/patata.png", 0, 1400, 500, 500);

//Add an image loaded from the web
ui.addSwitch(0, 2000, 500, 100, true, function(val){ 
    console.log(val);
});

var htmlText = ui.addText("lala", 0, 2100, 500, 100);
htmlText.html("This is a <strong> HTML </strong> text");

var font = util.loadFont("visitor2.ttf");
var label = ui.addText("hola fonts", 0, 2200, 300, 200); 
label.setTextSize(50);
label.font(font);
 
//apparently some devices cannot load svg files 
var img = ui.addImage( "awesome_tiger.svg", 0, 2300, 500, 500);