var Connection = function () { 
  console.log("Websockets init");
  var connected = false;
  var ws;
  var wsAddress = "ws://localhost:8081";

  Connection.prototype.connect = function() {
    // Write your code in the same way as for native WebSocket:
    this.ws = new WebSocket(this.wsAddress);
    this.ws.onopen = function() {
      this.connected = true;
    };

    this.ws.onmessage = function(e) {
      // Receives a message.
      console.log('message', e.data)
    }
    
    this.ws.onclose = function() {
      console.log('close');
      this.connected = false;
      //this.reconnect();
    }

  }

  Connection.prototype.reconnect = function() {
    setInterval(function() {
      this.connect();
    }, 2000);
  }


  Connection.prototype.getProjects = function () { 
    //OK get projects
    ws.send('{type:get_projects}');

  }


}

var con = new Connection();
con.connect();