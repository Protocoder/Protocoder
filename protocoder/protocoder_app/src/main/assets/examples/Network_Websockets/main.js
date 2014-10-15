/* 
*	Websockets 
*
*
*/ 

var port= 2525; 

var server = network.startWebsocketServer(port, function(status, socket, data) {
	console.log("server:", status, socket.getLocalSocketAddress(), data);
	if (status == "onMessage") {
		socket.send("pong");
	}
});

var url = "ws://127.0.0.1:2525";
var client = network.connectWebsocket(url, function(status, data) {
	console.log("client:", status, data);
});
console.log(client);

ui.addButton("send data", 10, 10, 200, 200, function() {
	client.send("ping!");
});

//other useful methods 
//server.stop();
//client.close();

