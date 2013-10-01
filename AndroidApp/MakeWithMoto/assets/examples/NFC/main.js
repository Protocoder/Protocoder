/*
*	Shows a popup when the nfc tag id matches 
*	the detectedNFCId 
*/

//replace your nfc tag id 
var detectedNFCId = "040E0E020A0B00";

sensors.onNFC(function (data) { 
    console.log("the nfc id is: " + data); 
    
    if (data == detectedNFCId) { 
        android.toast(data + " does match", 2000);
    } else { 
        android.toast(data + " doesn't match", 2000);  
    } 
    
});