var dataLabel = ui.label("data : ",10, 20,500,100);


ui.button("START", 50,150,500,100, function(){ 
    makr.start(function(data) {
		ui.labelSetText(dataLabel, "Data : "+ data);
	});
});


ui.button("LEDON", 50,250,500,100, function(){ 
	makr.writeSerial("LEDON"); 
});

ui.button("LEDOFF", 50,350,500,100, function(){
	makr.writeSerial("LEDOFF");
});


ui.button("STOP", 50,450,500,100, function(){
	makr.stop(); 
});
