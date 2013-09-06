network.startOSCServer(12345, function(data) { 
    console.log(data);
    
}); 

ui.button("Connect", 0, 0, 500, 100, function() { 
    
    network.connect("127.0.0.1", 12345);
});


ui.button("Send", 0, 100, 500, 100, function() { 
    
    network.sendOSC("hello", "hello");
});