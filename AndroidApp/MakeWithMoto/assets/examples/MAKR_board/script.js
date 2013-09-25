/* this example runs only with makr boards, you should upload 
*  to the board the example located in the makewithmoto website 
*/

var dataLabel = ui.label("data : ",10, 20,500,100);

//start the makr board
ui.button("START", 50,150,500,100, function() { 
	//when the makr board sends data back we will 
	//show it in the dataLabel
    makr.start(function(data) {
		ui.labelSetText(dataLabel, "Data : "+ data);
	});
});


//write to the serial led on
ui.button("LEDON", 50,250,500,100, function(){ 
	makr.writeSerial("ledon"); 
});

ui.button("LEDOFF", 50,350,500,100, function(){
	makr.writeSerial("ledoff");
});


ui.button("STOP", 50,450,500,100, function(){
	makr.stop(); 
});
