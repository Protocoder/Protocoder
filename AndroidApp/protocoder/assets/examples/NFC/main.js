/*
*	Shows which id and message are written in the NFC tag 
*/

var nfcdata = ui.addLabel("", 20, 20, 500, 100, 16);

sensors.onNFC(function (id, data) { 
    console.log("the nfc id is: " + id, data); 
    nfcdata.setText(id + " " + data)
});