var remoteIP = 'localhost';

var ws; 

function testWebsockets() {
  // Write your code in the same way as for native WebSocket:
  ws = new WebSocket('ws://'+ remoteIP +':8081');

  ws.onopen = function() {
    connected();
    //ws.send('Hello')  // Sends a message.

    //OK get projects
    //ws.send('{type:get_projects}');

    //get new code template 
    //ws.send('{type:get_new_code}');

    //OK get project 
    //ws.send('{type:get_code, name:ioio_1}');

    //OK new project 
    //ws.send('{type:create_new_project, name:qlalalala}'); 

    //OK run project 
    //ws.send('{type:run_project, name:hello}');


  }
  ws.onmessage = function(e) {
    // Receives a message.
    console.log('message', e.data)

    //run_project
    if (e.data == "Connected") { 
      return;
    }

    var result = JSON.parse(e.data);

    //show remote logs 
    if (result.type == 'log_event') {
      console.log("REMOTE_LOG::" + result.tag + " --> " + result.msg);
    }  

    //get_projects
    if (result.projects != null) { 
      console.log("got projects"); 

      var projects = result.projects;
      setProjects(projects);
    }

    //get_code 
    if (result.code != null) { 
      console.log(result.code);
      editor.setValue(result.code);
    }

    //get_new_code or save_file 
    if (result.project != null) {
      console.log(result.project);
    }

    if (result.acc_x != null) {
      //plot1(result.acc_x, ""); 

    } 

    console.log(result);
    if (result.type == "widget") { 
          console.log("widget");

      if (result.action == "add") { 
                  console.log("add");

        addWidget(result.values);
      }

    }

  }
  ws.onclose = function() {
    disconnected();
  }

}

var reconnectionInterval;

function connected() { 
  console.log('connected');
  clearInterval(reconnectionInterval); //removes the reconnection 
  $("#connection").css("background-color","#00ff00");
}

function disconnected() { 
  console.log('disconnected');
  $("#connection").css("background-color","#ff0000");

  //try to reconnect 
  reconnectionInterval = setTimeout(function() {
    console.log("trying to reconnect");
    testWebsockets();
  }, 1000);
}

var currentProject = [];

function getCodeFor(projectName) {
   //get project 
   console.log("get project --> " + projectName);
   ws.send('{type:get_code, name:"'+projectName+'"}');
}

 //save project 
function saveCode(name) { 
  
  var json = {}; 
  json["type"] = "save_file";
  json["name"] = currentProject.name;
  json["code"] = unescape(editor.getValue());

  console.log(json);

  ws.send(JSON.stringify(json));

}