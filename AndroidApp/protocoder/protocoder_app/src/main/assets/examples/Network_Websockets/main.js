/* 
*	Websockets 
*
*
*/ 

var port= 2525; 

var server = network.startWebsocketServer(port, function(status, socket, data) {
	console.log("server: ", status, socket.getLocalSocketAddress(), data);
	if (status == "message") {
		client.send("pong");
	}
});

var url = "ws://127.0.0.1:2525";
var client = network.connectWebsocket(uri, function(status, data) {
	console.log(status, data);
});

ui.addButton("send data", 10, 10, 200, 100, function() {
	client.send("client ", "ping!");
});

//other useful methods 
//server.stop();
//client.close();