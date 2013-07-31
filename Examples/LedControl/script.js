function OnSerialRead(data){
    android.toast(data, 2000);
}
ui.button("START", 50,50,500,100, function(){makr.start();});
ui.button("LEDON", 50,150,500,100, function(){makr.writeSerial("LEDON");});
ui.button("LEDOFF", 50,250,500,100, function(){makr.writeSerial("LEDOFF");});
ui.button("STOP", 50,350,500,100, function(){makr.stop();});
