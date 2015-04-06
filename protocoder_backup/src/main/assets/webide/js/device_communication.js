/*
*	Basic communication with the device based on normal GET querys, 
*	so we can support old devices to have basic functionality, dashboard 
*	interaction is made using wecksockets  (below)
*
*/

var Communication = function(p, useWebsockets) { 
	this.protoEvent = p.event;
	this.remoteIP = window.location.hostname;  
	//this.remoteIP = 'localhost';
	//this.remoteIP = "192.168.10.14"
	this.remoteWSPORT = '8587';
	this.self = this; 
	this.countLogs = 0; 
	
	if (useWebsockets) { 
		this.initWebsockets();
	}
	
	this.init();
}

Communication.prototype.initEvents = function() {
	var that = this;
	
	this.protoEvent.listen("runApp", function(e) {
		that.runApp(e.detail)
	});	
		
	this.protoEvent.listen("saveProject", function(e) {
		that.pushCode(e.detail.currentProject, e.detail.fileName);
	});	

	this.protoEvent.listen("liveExecute", function(e) {
		that.executeCode(e.detail);
	});	
	
	this.protoEvent.listen("createNewProject", function(e) {
		that.createNewProject(e.detail);
	});
	
	this.protoEvent.listen("listFilesInProject", function(e) {
		that.listFilesInProject(e.detail);
	});
	
	this.protoEvent.listen("renameProject", function(e) {
		that.renameApp(e.detail);
	});
		
	this.protoEvent.listen("removeApp", function(e) {
		that.removeApp(e.detail);
	});
		
	this.protoEvent.listen("deviceProjectHighlight", function(e) {
		that.highlight(e.filter, e.detail.name);
	}); 
	
	this.protoEvent.listen("fetchCode", function(e) {
		that.fetchCode(e.detail.name, e.detail.type);
	});
}

Communication.prototype.init = function() {
	this.initEvents();
}



//listing apps in the future we might filter the listing
Communication.prototype.listApps = function (filter) { 
	var that = this;
	var obj = {};
	obj.cmd = "list_apps";
	obj.filter = filter;

	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) { 
		that.protoEvent.send("projects_received", {"filter":filter, "data":JSON.parse(data) });
	});
}

//push the code
Communication.prototype.pushCode = function (project, fileName) { 
	var obj = {};
	obj.cmd = "push_code";

	var o = new Object(); 
	o.name = project.name;
	o.url = project.url;
	o.code = project.code;
	o.type = project.type;

	if (!fileName) {
		o.fileName = "main.js";
	} else { 
		o.fileName = fileName;
	}
	
	$.ajax({
		url:this.remoteIP + "cmd="+JSON.stringify(obj),
		type: 'post',
		data: o,
		success: function(data) {
			
		}
	});
} 



Communication.prototype.executeCode = function (code) { 
	var obj = {};
	obj.cmd = "execute_code";

	var o = new Object();
	o.code = code;
	o.codeToSend = code;  


	$.ajax({
		url:this.remoteIP + "cmd="+JSON.stringify(obj),
		type: 'post',
		data: o,
		success: function(data) {
			
		}
	});

	//console.log(JSON.stringify(obj));
}

//fetch the code
Communication.prototype.fetchCode = function(pName, pType) { 
	var that = this;
	var obj = {};
	obj.cmd = "fetch_code";
	obj.name = pName;
	obj.type = pType;
	try {
	  	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {
	  		var code = JSON.parse(data);
	  		currentProject.name = pName;
	  		currentProject.type = pType;
	  		currentProject.url = location.href + "apps/" + pType + "/" + pName + "/";
	  		document.title = " protocoder | " + pName;

	  		var code = unescape(code.code);
	  		
	  		that.protoEvent.send("ui_setMainTab", {"pName":pName, "code":code});

	  		that.listFilesInProject(currentProject);
	  	});
	} catch (e) {} 
}

//list files in project 
Communication.prototype.listFilesInProject = function (currentProject) { 
	var that = this;
	var obj = {};
	obj.cmd = "list_files_in_project";
	obj.name = currentProject.name;
	obj.type = currentProject.type;

	try {
  		$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {	
	  		that.protoEvent.send("ui_clearFileElements")
	  		
	  		//console.log(data);
	
	  		$.each(JSON.parse(data).files, function(k, v) {
	  			//console.log(v); 
	  			v.recid = k;
	  			that.protoEvent.send("ui_addFileElement", v)
	  		});
  		});
  	} catch (e) {};
} 

Communication.prototype.runApp = function (project) {
	var obj = {};
	obj.cmd = "run_app";
	obj.name = project.name;
	obj.url = project.url;
    obj.remoteIP = this.remoteIP;
	obj.type = project.type;
  	try {
		$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 			//alert('Load was performed. ' + data);
		});
	} catch (e) {};

	$("#console_wrapper #console").empty();
	this.countLogs = 0;

}


Communication.prototype.createNewProject = function (new_name) {
	var obj = {};
	obj.cmd = "create_new_project";
	obj.name = new_name;
	console.log(obj);
 	var self = this;

 	try {

	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 		//alert('Load was performed. ' + data);
 	  	self.listApps("projects");
 		self.fetchCode(obj.name, "projects");
	});

	} catch (e) {};

}

Communication.prototype.removeApp = function (project) {
	var that = this;
	var obj = {};
	obj.cmd = "remove_app";
	obj.name = project.name;
	obj.type = project.type;
	
	try {

	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 	    that.listApps("projects");
 		alert('app removed ' + data);
	});

	} catch (e) {};

}


Communication.prototype.renameApp = function (project) {
	var obj = {};
	obj.cmd = "rename_app";
	obj.name = project.name;
	obj.type = project.type;
 
 	try {

	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 		alert('rename' + data);
	});
	} catch (e) {};

}


Communication.prototype.getReference = function (id) {
	var that = this;
	var obj = {};
	obj.cmd = "get_documentation";

	$.get(this.remoteIP + "cmd="+JSON.stringify(obj), function(data) { 
		var doc = JSON.parse(data);
		that.protoEvent.send("reference_parseHelp", {"docApi":doc.api});
	});
}

/*
*	Websockets section 
*
*/

var ws; 

Communication.prototype.initWebsockets = function () {
  var that = this;
  // Write your code in the same way as for native WebSocket:
  ws = new WebSocket('ws://'+ this.remoteIP +':' + this.remoteWSPORT);
  var self = this;
  ws.onopen = function() {
    self.connected();
  }
  ws.onmessage = function(e) {
    // Receives a message.
    //console.log('message', e.data)

    //run_project
    if (e.data == "Connected") { 
      return;
    }

    var result = JSON.parse(e.data);

    //show remote logs 
    if (result.type == 'log_event') {
      //console.log("REMOTE_LOG::" + result.tag + " --> " + result.msg);
    }  

    //get_projects
    if (result.projects != null) { 
      console.log("got projects"); 

      var projects = result.projects;
      setProjects(projects);
    }

    //get_code 
    if (result.code != null) { 
      //console.log(result.code);
      this.protoEvent.send("editor_setCode", {"code":result.code});
    }

    //get_new_code or save_file 
    if (result.project != null) {
      //console.log(result.project);
    }

    if (result.type == "error") { 
      currentError = result.values; 
      //console.log("error "  + currentError);

      //$("#console_wrapper #console").empty();
      $("#console_wrapper #console").append("<p class ='error'> " + currentError + " </p>");
   
      var c = $("#console_wrapper #console")[0];
      c.scrollTop = c.scrollHeight;
     // w2ui['layout'].show('bottom', false);
    }


    if (result.type == "console") { 
      if (result.action == "log") { 
	  	
	  	var val = '<p>' + result.values.val + '</p>';
	  	that.protoEvent.send("consoleLog", {"log":val});
	
	   } else if (result.action == "logC") {
		 var val = '<p style ="color:'+ result.values.color + '">' + result.values.val + '</p>';
	  	 that.protoEvent.send("consoleLog", {"log":val});
       } else if (result.action == "clear") { 
       	 $("#console_wrapper #console").empty();
       	 self.countLogs = 0;
       } else if (result.action == "backgroundColor") {
       	 $("#console_wrapper").css("background-color", result.values.color)
       } else if (result.action == "textColor") {
       		$('<style>#console p { color: ' + result.values.textColor + '; }</style>').appendTo('head');
       } else if (result.action == "textSize") {
       		$('<style>#console p { font-size: ' + result.values.textSize + '; }</style>').appendTo('head');
       } else if (result.action == "show") {
	       	if (result.values.val == true) {
	       		$("#console_wrapper").show();
	       	} else {
	       		$("#console_wrapper").hide();
	       	}
       }
    }

    if (result.type == 'ide') { 
      if (result.action == "ready") {
        if (result.values.ready) { 
          that.protoEvent.send("ui_appRunning", true);
        } else {
          that.protoEvent.send("ui_appRunning", false);
        }
      } else if (result.action == "new_files_in_project") { 
        self.listFilesInProject(currentProject.name, currentProject.type);
      } else if (result.action == "customjs") {
      	eval(result.values.val)
      }

    }
    

    //console.log(result);
    if (result.type == "widget") { 
		that.protoEvent.send("dashboard", result);
    }

  }
  ws.onclose = function() {
    self.disconnected();
  }

}
var reconnectionInterval;
//text-decoration: underline;

Communication.prototype.connected = function () { 
  //console.log('connected');
  clearInterval(reconnectionInterval); //_s the reconnection 
  this.protoEvent.send("ui_appConnected", true);
}

Communication.prototype.disconnected = function () { 
  var that = this;
  this.protoEvent.send("ui_appConnected", false);

  //console.log('disconnected');

  //try to reconnect 
  reconnectionInterval = setTimeout(function() {
    //console.log("trying to reconnect");
    that.initWebsockets();
  }, 1000);
}

var currentProject = [];

Communication.prototype.getCodeFor = function (projectName) {
   //get project 
   //console.log("get project --> " + projectName);
   ws.send('{type:get_code, name:"'+projectName+'"}');
}

Communication.prototype.highlight = function(folder, name) {
   ws.send('{id: "protocoderApp", type:"project_highlight", folder:"'+folder+'", name:"'+name+'"}');
}

//save project 
Communication.prototype.saveCode = function (name) { 
  
  var json = {}; 
  json["type"] = "save_file";
  json["name"] = currentProject.name;
  json["code"] = unescape(editor.getValue());

  //console.log(json);

  ws.send(JSON.stringify(json));

}
