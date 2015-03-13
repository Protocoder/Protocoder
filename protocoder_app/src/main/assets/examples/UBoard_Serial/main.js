/* Arduino example 
* Works with Android devices with OTG support and Arduinos 
* such as Uno, load the Arduino.ino in your Arduino to the 
* the example working 
*/


var dataLabel = ui.addText("data : ",10, 20, 500, 100);

var arduino; 

//start connexion with arduino
ui.addButton("START", 50, 150, 500,100).onClick(function() { 
	
	//show arduino incoming data
    arduino = boards.startSerial(9600, function(connected) {
    	console.log("connected " + connected);
	});

	arduino.onNewData(function() {
		dataLabel.setText("Data : "+ data);
		console.log(data);
		media.textToSpeech("tick"); 
	});

});


//write to the serial led on
ui.addButton("LEDON", 50, 250, 500,100).onClick(function(){ 
	arduino.write("ledon"); 
});

ui.addButton("LEDOFF", 50, 350, 500,100).onClick(function(){
	arduino.write("ledoff");
});


ui.addButton("STOP", 50, 450, 500,100).onClick(function(){
	arduino.stop(); 
});
