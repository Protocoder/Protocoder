
var ble = network.startBLE();
ble.connect("D0:39:72:C8:D7:A4", function(data) {
    if(data){
        device.vibrate(500);
        console.log("he conectado");

        ble.listenCharacteristic("a495ff10-c5b1-4b44-b512-1370f02d74de","a495ff11-c5b1-4b44-b512-1370f02d74de",function(data){
             console.log(data);
            device.vibrate(500);
            console.log("PITICLI BONICO");
        });
    }
});