var pd = media.initPDPatch("notes.pd", function(type, data) { 
    console.log(type, data);
});

android.loop(10, function() { 
   // pd.sendFloat("midinote", 50 + Math.round(50 * Math.random()));
});


sensors.startAccelerometer(function(x, y, z) {
   //console.log("accelerometer " + x + ", " + y + ", " + z);
    pd.sendFloat("midinote", 82 + Math.round(x));
});