

var http = require('http');
var io = require('socket.io');
var fs = require('fs');
var path = require('path');
var express = require('express');
//var osc = require('node-osc'); 
//var form = require('connect-form');

window.require = undefined; 

var app = express();
var server = http.createServer(app);
server.listen(8080);
//var oscServer = new osc.Server(3333, '0.0.0.0');


var currentPath = process.execPath.substring(0, process.execPath.length - 92)


app.configure(function() {
    var route = path.join(currentPath, '/web/server/public'); 
    console.log(route);
    app.use("/", express.static(route));
	//form({ keepExtensions: true })
}); 
    

var websocket_server = io.listen(server, { log: false }); //.set('log level', 1); // reduce logging


function handler (req, res) {
  fs.readFile(currentPath + '/index.html',
  function (err, data) {
    if (err) {
      res.writeHead(500);
      return res.end('Error loading index.html');
    }

    res.writeHead(200);
    res.end(data);
  });
}
var i = 0;
console.log("sfgagddsg");


websocket_server.sockets.on('connection', function (socket) {
  i++;
  socket.emit('news', { hello: i });
  //socket.emit('news', {sockets.clients(socket)}); 
  console.log("Join");
  //socket.emit('news', { hello: 'world21515' });
  //socket.on('my other event', function (data) {
  // console.log(data);
  //});
  //setInterval(function(){socket.emit('news', { hello: i }); i++;},1000);
    
  console.log(i);
  socket.emit('registerController', {number: 500});

  socket.on('returnNumber', function () {
    console.log("data:");
    socket.emit('number', {number: 500});
  });

  socket.on('disconnect', function () {
    //socket.emit('news', { hello: i });
    //socket.emit('news', { hello: 'world21515' });
    //socket.on('my other event', function (data) {
    // console.log(data);
    //});
    //setInterval(function(){socket.emit('news', { hello: i }); i++;},1000);
    i--;
    console.log("leave");
    console.log(i);

  });


    
  socket.on('log', function() {
    console.log("sadgads");
    var Numbers = new Array();
    var min = 1000;
    var max = 10000;
    // and the formula is:
    var random = Math.floor(Math.random() * (max - min + 1)) + min;
    for (var i = 0; i < 3; i++) {
      Numbers[i] = random;
      random = Math.floor(Math.random() * (max - min + 1)) + min;
    };
    
    for (i = 0; i < Numbers.length; i ++)
    {
      console.log(Numbers[i]);
    }
    socket.emit('updateScores', {array: Numbers});
  
    var Positions =  new Array();
    min = 0;
    max = 100;
    for(i = 0; i < 3 ; i ++)
    {
      Positions[i] = new Object();
      Positions[i].x = Math.floor(Math.random() * (max - min + 1)) + min;
      Positions[i].y = Math.floor(Math.random() * (max - min + 1)) + min;
    }
    

    socket.emit('updatePositions', {positions: Positions});
    
  }); 
    
});


/*
oscServer.on("message", function (msg, rinfo) {
    
    console.log(msg);
    console.log(rinfo);
    websocket_server.sockets.emit("remoteController", msg); 
    
      var blobsJson=[]
      //blobsJson.push( {'time':diffTime }) //timestamp
      for (var i=2; i<msg.length; i+=1){
        var blob=msg[i];  
        if(blob[2]!=undefined){
          if(blob[1]=='set'){
            blobsJson.push({'x':Math.round(blob[3]*width) , 'y' :Math.round(blob[4]*height),'id':blob[2]} )                
          }
        }              
      }            
      //msgsArray.push(blobsJson)
      //console.log( blobsJson )
      
    // socket.broadcast.emit('updateClient', msg); 
});
*/
