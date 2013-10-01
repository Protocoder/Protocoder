/* 
*	OSC networking protocol, pretty handy to connect different 
*	devices / softwares together 
*/ 

network.startOSCServer(12345, function(data) { 
    console.log(data);
}); 

ui.addButton("Connect", 0, 0, 500, 100, function() { 
    network.connect("127.0.0.1", 12345);
});


ui.addButton("Send", 0, 100, 500, 100, function() { 
    network.sendOSC("hello", "hello");
});