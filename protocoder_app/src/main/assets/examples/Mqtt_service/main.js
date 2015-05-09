/*
*	
*	Description ........ 
*	by ........ 
*
*/

//if (false) {
    var client = network.createMqttClient();
    console.log(client);
    console.log("lala");
    client.connect(function(status) {
        console.log(status);
        
        client.subscribe("planets/earth", function(topic, data) {
            console.log(topic, data);
        });
    });
    
    client.onNewData(function(topic, data) {
       console.log(topic, data); 
    });
    
//}
var btn = ui.addButton("publish", 0, 0, 225, 225).onClick(function(){ 
    client.publish("planets/earth", "hola");
});
