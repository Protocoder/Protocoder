function OnSerialRead(data){
    android.toast(data, 2000);
}
ui.button("START", 500,800,500,100, function(){makr.start("");});
//makr.start("");
ui.button("LEDON", 500,400,500,100, function(){makr.writeSerial("LEDON");});
ui.button("LEDOFF", 500,800,500,100, function(){makr.writeSerial("LEDOFF");});
ui.button("STOP", 500,1200,500,100, function(){makr.stop();});
