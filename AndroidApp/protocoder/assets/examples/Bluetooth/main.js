/*
* Bluetooth serial example 
* This example is beta but included to experiment with it
* The methods are commented out, feel free to experiment and 
* dont forget to give feedback in github! :D
*
* If you want to connect a bluetooth module to Arduino this is a 
* good start 
* https://learn.sparkfun.com/tutorials/using-the-bluesmirf/all
*/

network.startBluetooth();

//network.connectBluetoothByUI("");

//OK
// network.scanBTNetworks(function(n, m, s) { 
//     console.log("hola", n, m, s);
// });

//OK 
network.connectBluetoothByMac("00:06:66:64:45:B1", function(data) {
    console.log(data);
});

//network.connectBluetoothByName("azulico", "");
//network.sendBluetoothSerial("a");

//OK
//network.disconnectBluetooth();
//OK
//network.disableBluetooth();
//OK
//network.enableBluetooth();