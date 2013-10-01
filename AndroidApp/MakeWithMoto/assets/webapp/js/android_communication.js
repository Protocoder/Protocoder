/*
*	Basic communication with the device based on normal GET querys, 
*	so we can support old devices to have basic functionality, dashboard 
*	interaction is made using wecksockets  (below)
*
*/


//listing apps in the future we might filter the listing
function list_apps(filter) { 
	var obj = {};
	obj.cmd = "list_apps";
	obj.filter = filter;

	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
		setProjectList(filter, JSON.parse(data));
	});
}

//push the code
function push_code(project) { 
	var obj = {};
	obj.cmd = "push_code";

	var o = new Object(); 
	o.name = project.name;
	o.url = project.url;
	o.code = project.code;
	o.type = project.type;

	$.ajax({
		url:remoteIP + "cmd="+JSON.stringify(obj),
		type: 'post',
		data: o,
		success: function(data) {
			
		}
	});
} 



function executeCode(code) { 
	var obj = {};
	obj.cmd = "execute_code";

	var o = new Object();
	o.code = code;
	o.codeToSend = code;  


	$.ajax({
		url:remoteIP + "cmd="+JSON.stringify(obj),
		type: 'post',
		data: o,
		success: function(data) {
			
		}
	});

	//console.log(JSON.stringify(obj));
}

//fetch the code
function fetch_code(pName, pType) { 
	var obj = {};
	obj.cmd = "fetch_code";
	obj.name = pName;
	obj.type = pType;
	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
		var code = JSON.parse(data);
		setCode(unescape(code.code));
		currentProject.name = pName;
		currentProject.type = pType;
		document.title = " protocoder | " + pName;
		var tabs = w2ui['code_editor'].get("main").tabs;
		tabs.get("tab1").caption = pName;
		tabs.refresh();

		list_files_in_project(pName, pType);
	});
}

//fetch the code
function list_files_in_project(pName, pType) { 
	var obj = {};
	obj.cmd = "list_files_in_project";
	obj.name = pName;
	obj.type = pType;
	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
		w2ui['grid'].clear();
		//console.log(data);

		$.each(JSON.parse(data).files, function(k, v) {
			//console.log(v); 
			v.recid = k;
			w2ui['grid'].add( v );
		});

		console.log(currentProject);

		initUpload();

	});
} 

function run_app(project) {
	var obj = {};
	obj.cmd = "run_app";
	obj.name = project.name;
	obj.url = project.url;
	obj.type = project.type;
	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 		//alert('Load was performed. ' + data);
	});
	$("#console").empty();

}


function create_new_project(new_name) {
	var obj = {};
	obj.cmd = "create_new_project";
	obj.name = new_name;
	console.log(obj);
	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 		//alert('Load was performed. ' + data);
 		list_apps("user");
 		fetch_code(obj.name, "list_projects");
	});
}
function remove_app(id) {
	var obj = {};
	obj.cmd = "remove_app";

	obj.remove_app = name;
	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) {
 		alert('Load was performed. ' + data);
	});
}

function get_reference(id) {
	var obj = {};
	obj.cmd = "get_documentation";

	$.get(remoteIP + "cmd="+JSON.stringify(obj), function(data) { 
		var doc = JSON.parse(data);
		parse_help(doc.api);
	});
}


function get_camera() {
	var url = 'http://'+localhost+':8080/takePic';

	$.get(url, function(data) { 
		
		var img = $("<img />").attr('src', 'http://somedomain.com/image.jpg').load(function() {
        if (!this.complete || typeof this.naturalWidth == "undefined" || this.naturalWidth == 0) {
            alert('broken image!');
        } else {
            $("#widgets").append(img);
        }
    });

	});
}


/*
*	Websockets section 
*
*/

var remoteIP = window.location.hostname; //'localhost';
var remotePlot;

var ws; 

function initWebsockets() {
  // Write your code in the same way as for native WebSocket:
  ws = new WebSocket('ws://'+ remoteIP +':8587');

  ws.onopen = function() {
    connected();
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

    if (result.type == "error") { 
      currentError = result.values; 
      console.log("error "  + currentError);
      $("#console").empty();
      $("#console").append("<p> " + currentError + " </p>");
   
      var objDiv = document.getElementById("console");
      objDiv.scrollTop = objDiv.scrollHeight;
     // w2ui['layout'].show('bottom', false);
    }


    if (result.type == "console") { 
      var log = result.values.val; 
      $("#console").append('<p>' + log + '</p>');

      var objDiv = document.getElementById("console");
      objDiv.scrollTop = objDiv.scrollHeight;
    }

    if (result.type == 'ide') { 
      var ready = result.values.ready; 


      if (ready) { 
        $("#tb_toolbar_item_project_run").addClass("app_running");
        $("#tb_toolbar_item_project_run").removeClass("app_connected");

        $("#tb_toolbar_item_project_run").find(".w2ui-tb-caption").text("close");
      } else { 
        $("#tb_toolbar_item_project_run").addClass("app_connected");
        $("#tb_toolbar_item_project_run").removeClass("app_running");
        $("#tb_toolbar_item_project_run").find(".w2ui-tb-caption").text("run");
      }

    }
    

    console.log(result);
    if (result.type == "widget") { 
      
      if (result.action == "showDashboard") { 
        if (result.values.val == true) { 
          showDashboard();
          console.log("show dashboard");
        } else {
          hideDashboard();
        }
      } else if (result.action == "add") { 
        console.log("adding widget");
        remotePlot = addWidget(result.values);
      } else if (result.action == "update") {
        console.log("updating widget");
        console.log(result.values.val);
        remotePlot(result.values.val, "");
      } else if (result.action == "setText") {
        setText(result.values.id, result.values.val);

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
  $("#connection").addClass('card');
  $("#connection").css("border-color","#00ff00");
  $("#tb_toolbar_item_project_run").addClass("app_connected");

}

function disconnected() { 
  console.log('disconnected');
  $("#tb_toolbar_item_project_run").removeClass("app_connected");
  $("#tb_toolbar_item_project_run").removeClass("app_running");

  $("#connection").addClass('card');
  $("#connection").css("border-color","#ff0000");
  $("#tb_toolbar_item_project_run").removeClass("app_running");
  $("#tb_toolbar_item_project_run").removeClass("app_connected");


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
