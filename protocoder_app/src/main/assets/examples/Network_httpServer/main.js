/*
*	
*	Description ........ 
*	by ........ 
*
*/

var server = network.startSimpleHttpServer(1111, function(url, method) {
    console.log(url, method);
    if (url == "/qq") {
        console.log("qq2");
        return server.respond("hola qq");
    }
    
});