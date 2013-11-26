/*
*	Shows which id and message are written in the NFC tag 
*/

var nfcinfo = ui.addLabel("", 20, 20, 500, 100, 16);

sensors.onNFC(function (id, data) { 
    console.log("the nfc id is: " + id, data); 
    nfcinfo.setText(id + " " + data)
});


//when we click 
//the next touched nfc will be written with the data 
ui.addButton("Write to NFC", 0, 0,500,100, function(){
	sensors.writeNFC("this is a test", function() {
		nfcinfo.setText("data written");
	});
});


