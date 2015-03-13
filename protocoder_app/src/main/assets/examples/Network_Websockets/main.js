/* 
*	Websockets 
*
*
*/ 

//------------ server 
var port= 2525; 

var server = network.startWebsocketServer(port, function(status, socket, data) {
	console.log("server:", status, socket.getLocalSocketAddress(), data);
	
	if (status == "onMessage") {
		socket.send("pong");
	}
});

//------------ client 
var url = "ws://127.0.0.1:2525";
var client = network.connectWebsocket(url, function(status) {
	console.log(status);
});

client.onNewData(function(status, data) {
	console.log("client:", status, data);
});
console.log(client);

ui.addButton("send data", 10, 10, 200, 200).onClick(function() {
	client.send("ping!");
});

//other useful methods 
//server.stop();
//client.close();

