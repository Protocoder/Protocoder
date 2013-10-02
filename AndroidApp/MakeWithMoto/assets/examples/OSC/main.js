/* 
*	OSC networking protocol, pretty handy to connect different 
*	devices / softwares together 
*/ 

network.startOSCServer(12345, function(name, data) { 
    console.log(name + " " + data);
}); 

var client;
ui.addButton("Connect", 0, 0, 500, 100, function() { 
    client = network.connectOSC("192.168.43.17", 12002);
});


ui.addButton("Send", 0, 100, 500, 100, function() { 
    var o = new Array();
    o.push("hola");
    o.push(2);
    client.send("hello", o);
});