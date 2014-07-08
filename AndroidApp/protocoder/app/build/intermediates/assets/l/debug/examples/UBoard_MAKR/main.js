/* this example runs only with makr boards, you should upload 
*  to the board the example located in the makewithmoto website 
*/

var dataLabel = ui.addLabel("data : ",10, 20,500,100);

//start the makr board
ui.addButton("START", 50,150,500,100, function() { 
	//when the makr board sends data back we will 
	//show it in the dataLabel
    makr.start(function(data) {
		dataLabel.setText("Data : "+ data);
	});
});


//write to the serial led on
ui.addButton("LEDON", 50,250,500,100, function(){ 
	makr.writeSerial("ledon"); 
});

ui.addButton("LEDOFF", 50,350,500,100, function(){
	makr.writeSerial("ledoff");
});


ui.addButton("STOP", 50,450,500,100, function(){
	makr.stop(); 
});
