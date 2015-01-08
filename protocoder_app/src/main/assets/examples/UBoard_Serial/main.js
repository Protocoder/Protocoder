/* Arduino example 
* Works with Android devices with OTG support and Arduinos 
* such as Uno, load the Arduino.ino in your Arduino to the 
* the example working 
*/


var dataLabel = ui.addText("data : ",10, 20, 500, 100);

var arduino; 

//start connexion with arduino
ui.addButton("START", 50, 150, 500,100, function() { 
	//show arduino incoming data
    arduino = boards.startSerial(9600, function(data) {
		dataLabel.setText("Data : "+ data);
		console.log(data);
		media.textToSpeech("tick"); 
	});
});


//write to the serial led on
ui.addButton("LEDON", 50, 250, 500,100, function(){ 
	arduino.writeSerial("ledon"); 
});

ui.addButton("LEDOFF", 50, 350, 500,100, function(){
	arduino.writeSerial("ledoff");
});


ui.addButton("STOP", 50, 450, 500,100, function(){
	arduino.stop(); 
});
