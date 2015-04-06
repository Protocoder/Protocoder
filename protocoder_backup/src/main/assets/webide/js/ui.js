/* 
* 	UI 
*/

var Ui = function(p) { 
	this.protoEvent = p.event;
	this.dropboxEnabled = true; 
	this.gridRendered = false;
	this.tabs = {}; //[name] -> id , code 
	this.activeTab = null;
	this.tabId = 0;
	this.init();
}

Ui.prototype.initEvents = function() {
	var that = this;
	
	this.protoEvent.listen("ui_setMainTab", function(e) {
		that.setMainTab(e.detail.pName, e.detail.code);
	});	
	
	this.protoEvent.listen("ui_setTabFeedback", function(e) {
		that.setTabFeedback(e.detail);
	})
	
	this.protoEvent.listen("ui_clearFileElements", function(e) {
		that.clearFileElements();
	});	
		
	this.protoEvent.listen("ui_addFileElement", function(e) {
		that.addFileElement(e.detail);
	});	
		
	this.protoEvent.listen("consoleLog", function(e) {
		that.consoleLog(e.detail.log);
	}); 
	
	this.protoEvent.listen("ui_appConnected", function(e) {
		that.appConnected(e.detail);
	});
	
	this.protoEvent.listen("ui_appRunning", function(e) {
		that.appRunning(e.detail);
	});	

	this.protoEvent.listen("saveProject", function(e) {
		that.toolbarFeedback("save");
	});	

	this.protoEvent.listen("runProject", function(e) {
		that.toolbarFeedback("run");
	}); 
	
	this.protoEvent.listen("projects_received", function(e) {
		that.setProjectList(e.detail.filter, e.detail.data);
	});

	//this.protoEvent.listen("ui_appRunning", function(e) {
	//	ui.appRunning(e.detail);
	//});	

	
		
}

Ui.prototype.init = function() { 
	var that = this;

	$('#layout').w2layout({
	    name: 'layout',
	    panels: [
	        { type: 'main', content: 'main' },
	        { type: 'right', size: 300, content: 'right', resizable: true, hidden: false}

	    ],
	    onRender: function() { 
  			if (that.gridRendered == false) {
	        	that.gridRendered = true;
		        //add tabs button 
		        //$("#layout_code_editor_panel_main").append('<div id="tabMenu"><div id="showHideMenu">+</div> <div id="menu"><ul> <li id = "addTab">New tab</li><li id = "renameTab">Rename selected tab</li><li id = "deleteTab"> Delete selected tab</li></ul></div></div>');
		        $("#layout_code_editor_panel_main").append('<div id="tabMenu"><i id="addTab" class="fa fa-plus"></i></div>');
	

		        jQuery.fn.btnToggle = function(cb1, cb2) {
	    			var o = $(this[0]) // It's your element

	    			function handle1 () { 
	    				cb1();
	    				o.one('click', handle2);
	    			} 

	    			function handle2 () {
	    				cb2(); 
	    				o.one('click', handle1);
	    			}
	    			o.one('click', handle1);

				};

		    	//bind ui
		    	$("#tabMenu #addTab").btnToggle(function() {
		    	//	$("#tabMenu #menu").fadeIn();
		    	}, function() {
		    	//	$("#tabMenu #menu").fadeOut();
		    	}); 

		    }

	    }
	});

	$().w2layout({
	    name: 'code_editor',
	    panels: [
	       { type: 'main', content: 'main', tabs: {
	            active: 'tab1',
	            name: 'tabs',
	            tabs: [
	                { id: 'tab0', caption: 'Main' },
	            ],
	            onClick: function (target, data) {
	            	//console.log(target);
	            	//console.log(data);
	            	that.setActiveTab(target);
	            }
	        } },
	        { type: 'bottom', size: "100px", resizable: true, hidden: false, content: '<div id = "console_wrapper"> <div id = "console"></div><div id = "console_input"><span>></span> <input type="text" name="fname" style=""> </div> </div>' }
	    ]
	});

	$().w2layout({
	    name: 'right_bar',
	    panels: [
	        { type: 'main', size: '70%', resizable: true, content: '<div id = "sidebar_container"><li id = "opts> </li></div>' },
	        { type: 'bottom', size: '30%', resizable: true, hidden: false, content: 'bottom', 
	 
		}
	    ]
	});
	$('#grid').w2grid({ 
	    name: 'grid', 
	    url: '',
	    onRender: function() { 
	    },
	    columns: [              
	        { field: 'file_name', caption: 'File Name', size: '70%' },
	        { field: 'file_size', caption: 'File Size', size: '30%' },
	    ], 

	    onClick: function(t, e) {
	    	//console.log(t);
	    	//console.log(e);

	    	var prefix = currentProject.url;
	    	var fileName = w2ui['grid']['records'][e.recid].file_name;
	    	var url = prefix + fileName;
	
			$.get(url, function(data) {
				that.addTabAndCode(fileName, data);
			}, "text");
	    },

	});


	w2ui['right_bar'].content('bottom', w2ui['grid']);
	w2ui['layout'].content('main', w2ui['code_editor']);
	w2ui['layout'].content('right', w2ui['right_bar']);
	w2ui['code_editor'].content('main', '<pre id="editor"></pre>');
	w2ui['code_editor'].on('resize', function(target, eventData) { 
		//var that = this;
		console.log("onresize " + target); 

		//console.log(eventData); 
		eventData.onComplete = function() { 
			that.protoEvent.send("editor_resize")
			//that.initUpload();

			setTimeout(function() {
				that.dropboxEnabled = true;
				that.bindUpload();
			}, 500);

			setTimeout(function() {
				$("#toolbar").addClass("show"); 
			}, 500);	

			setTimeout(function() {
				$("#overlay #toggle").addClass("show");
			}, 1000);

			setTimeout(function() {
				$("#container").addClass("on");
			}, 700);
		}
	});


	//popup create project 
	function openPopup () {
	    $().w2form({
	        name: 'foo',
	        style: 'border: 0px; background-color: transparent;',
	        formHTML: 
	            '<div class="w2ui-page page-0">'+
	            '   <div class="w2ui-label"> Name:</div>'+
	            '   <div class="w2ui-field">'+
	            '       <input name="project_name" type="text" size="35"/>'+
	            '   </div>'+
	            '</div>'+
	            '<div class="w2ui-buttons">'+
	            '   <input type="button" value="OK" name="save">'+
	            '   <input type="button" value="Cancel" name="cancel">'+
	            '</div>',
	        fields: [
	            { name: 'project_name', type: 'text', required: true },
	        ],
	        record: { 
	            first_name  : ':)',
	        },
	        actions: {
	            "save": function () { 
	                that.protoEvent.send("createNewProject",  $("input#project_name").val() );
	                $().w2popup('close'); 
	            },
	            "cancel": function () { 
	                this.clear();  
	                $().w2popup('close'); 
	            },
	        }
	    });
	    $().w2popup('open', {
	        title   : 'Create a new project',
	        body    : '<div id="form" style="width: 100%; height: 100%;"></div>',
	        style   : 'padding: 15px 0px 0px 0px',
	        width   : 400, 
	        height  : 200, 
	        onOpen  : function () {
	            $('#w2ui-popup #form').w2render('foo');
	        }
	    });
	}  

		
	// Prevent the backspace key from navigating back.
	$(document).unbind('keydown').bind('keydown', function (event) {
	    var doPrevent = false;
	    if (event.keyCode === 8) {
	        var d = event.srcElement || event.target;
	        if ((d.tagName.toUpperCase() === 'INPUT' && (d.type.toUpperCase() === 'TEXT' || d.type.toUpperCase() === 'PASSWORD' || d.type.toUpperCase() === 'FILE')) 
	             || d.tagName.toUpperCase() === 'TEXTAREA') {
	            doPrevent = d.readOnly || d.disabled;
	        }
	        else {
	            doPrevent = true;
	        }
	    }

	    if (doPrevent) {
	        event.preventDefault();
	    }
	});

	//prevents swipes on macs 
	//https://github.com/micho/jQuery.preventMacBackScroll
	(function ($) {

	  // This code is only valid for Mac
	  if (!navigator.userAgent.match(/Macintosh/)) {
	    return;
	  }

	  // Handle scroll events in Chrome
	  if (navigator.userAgent.match(/Chrome/)) {

	    // TODO: This only prevents scroll when reaching the topmost or leftmost
	    // positions of a container. It doesn't handle rightmost or bottom,
	    // and Lion scroll can be triggered by scrolling right (or bottom) and then
	    // scrolling left without raising your fingers from the scroll position.
	    $(window).mousewheel(function (e, d, x, y) {

	    	if (typeof _ != 'undefined') { 
		      var prevent_left, prevent_up;

		      // If none of the parents can be scrolled left when we try to scroll left
		      prevent_left = x < 0 && !_($(e.target).parents()).detect(function (el) {
		        return $(el).scrollLeft() > 0;
		      });

		      // If none of the parents can be scrolled up when we try to scroll up
		      prevent_up = y > 0 && !_($(e.target).parents()).detect(function  (el) {
		        return $(el).scrollTop() > 0;
		      });

		      // Prevent futile scroll, which would trigger the Back/Next page event
		      if (prevent_left || prevent_up) {
		        e.preventDefault();
		      }
	      }
	    });

	  }

	}(jQuery));

	/*
	*  
	* Binding UI 
	*
	*/
	var overlayShow = false;
	$("#toolbar #newProjectBtn").click(function() { 
		openPopup();
	});

	//save file
	$("#toolbar #saveBtn").click(function() { 
		if (!$.isEmptyObject(currentProject)) { 
			that.protoEvent.send("editor_saveCode");
		} else { 
			openPopup();
		}
	});

	//run app
	$("#toolbar #runBtn").click(function() { 
        if (!$.isEmptyObject(currentProject)) { 
            that.protoEvent.send("runApp", currentProject);    
        } else { 
            openPopup();
        } 
	});
	
	//show hide bar
	$("#toolbar #sideBarBtn").click(function() { 
		w2ui['layout'].toggle('right', false);
		//this.text("hola");
	});
	
	$("#overlay #toggle").click(function() { 
		if (overlayShow) {
			that.protoEvent.send("dashboard_visible", false);
		} else {
			that.protoEvent.send("dashboard_visible", true);
		}

		overlayShow ^= true;
	});

	$("#toolbar #projectsBtn").click(function() { 
		that.showProjects();
	});

	//shortcut dashboard 
	if (location.hash.indexOf("#dashboard") != -1) {
		console.log("lala");
		setTimeout(function() {
			that.protoEvent.send("dashboard_visible", true);
		}, 1000);
	}

	//shorcut project 
	if (location.hash.indexOf("#project") != -1) {

	}

   //console input 
	$("#console_wrapper input").keydown(function(e){ 
			var code = e.which; // recommended to use e.which, it's normalized across browsers
		if(code==13) {
			console.log("enter");
			var cmd = $("#console_wrapper input").val();
			consoleInputHistory.push(cmd);
			that.protoEvent.send("liveExecute", cmd);
			$("#console_wrapper input").val("");
			currentHistoryEntry = consoleInputHistory.length;
			e.preventDefault();
		}

		//console-like history
		//up
	    if (code==38) {
	    	if (currentHistoryEntry > 0) {
	    		currentHistoryEntry--;
	    	} 
	    	$("#console_wrapper input").val(consoleInputHistory[currentHistoryEntry]);
	    } 
	    //down
	    if (code == 40) {
	    	if (currentHistoryEntry < consoleInputHistory.length) {
	    		currentHistoryEntry++;
	    	}
	    	$("#console_wrapper input").val(consoleInputHistory[currentHistoryEntry]);
	    }
	});

	// Prevent the backspace key from navigating back.
	$(document).unbind('keydown').bind('keydown', function (event) {
	    var doPrevent = false;
	    if (event.keyCode === 8) {
	        var d = event.srcElement || event.target;
	        if ((d.tagName.toUpperCase() === 'INPUT' && (d.type.toUpperCase() === 'TEXT' || d.type.toUpperCase() === 'PASSWORD' || d.type.toUpperCase() === 'FILE')) 
	             || d.tagName.toUpperCase() === 'TEXTAREA') {
	            doPrevent = d.readOnly || d.disabled;
	        }
	        else {
	            doPrevent = true;
	        }
	    }

	    if (doPrevent) {
	        event.preventDefault();
	    }
	});
	
	//init events
	this.initEvents();
}

//upload 
Ui.prototype.bindUpload = function() {
	var that = this;
	
    $("#layout_right_bar_panel_bottom #grid_grid_records").on('dragenter', function(e) {
    	console.log("lalallaala")
        console.log(e.target);

        if (that.dropboxEnabled == true) {
            that.initUpload();
            e.preventDefault();
            e.stopPropagation();
        }
    });

    $("#grid_grid_records").on('dragover', function(e) {
        e.preventDefault();
        e.stopPropagation();
    });

    $("#grid_grid_records").on('drop', function(e) {
        e.preventDefault();
        e.stopPropagation();
    });
    

    $("#grid_grid_records").on('dragend', function(e) {
    	//console.log("lalalalalal");
    });
}

var consoleInputHistory = new Array();
var currentHistoryEntry;

Ui.prototype.showProjectsStatus = false; 

Ui.prototype.showProjects = function() {
	if (this.showProjectsStatus == false) { 
		$("#list_projects").addClass("show");
		$("#toolbar #projectsBtn").addClass("on");
	} else { 
		$("#list_projects").removeClass("show");
		$("#toolbar #projectsBtn").removeClass("on");
	} 

	this.showProjectsStatus ^= true;
} 

Ui.prototype.toolbarFeedback = function(action) {
	var div;
	if (action === "save") {
		div = "#saveBtn";
	} else if (action === "run") {
		div = "#runBtn";
	}
	$("#toolbar " + div).addClass("on")
	setTimeout(function() {
		$("#toolbar " + div).removeClass("on")
	}, 500);
}


Ui.prototype.appRunningStatus = false; 
Ui.prototype.appRunning = function(b) {
	if (b) { 
   		$("#runBtn").addClass("app_running")
   					.removeClass("app_connected")
        			.find("span").text("close");

    } else { 
   		$("#runBtn").addClass("app_connected")
   					.removeClass("app_running")
   					.find("span").text("run");
	} 
	this.appRunningStatus = b;
} 

Ui.prototype.appConnectedStatus = false; 
Ui.prototype.appConnected = function(b) {
	if (b) { 	 
		$("#runBtn").addClass("app_connected");
    } else { 
		$("#runBtn").removeClass("app_connected")
					.removeClass("app_running");
	} 
	this.appConnectedStatus = b;
} 


//set projects in project list 
Ui.prototype.setProjectList = function(filter, data) { 
	var that = this;
	var div = filter;
	if (filter == "examples") { 
   		listExamples = data.projects;
	} else if (filter == "projects") {
    	listProjects = data.projects;
	} 
	
	$("#list_projects #"+div+" ul").empty(); 

	var obj = new Object();
	obj.items = new Array();
	
	$(data.projects).each(function(k, project) {
	
	$('<li id ="'+project.name+'"><span>' + project.name + '</span><div id ="actions"> <div id ="cont"> <i id = "rename" class="fa fa-pencil"></i> <i id = "delete" class="fa fa-trash-o"> </i>   </div> </div> </li>')
	  .click(function () {
	    currentProject = project;
	    currentProject.type = filter;
	    that.protoEvent.send("fetchCode", {"name":project.name, "type":project.type});
	    that.showProjects(false);
	
	    $("#list_projects #" + project.name + " #rename").click(function(e) { 
	      that.protoEvent.send("renameProject", project);
	      e.stopPropagation();
	    });
	
	    $("#list_projects #" + project.name + " #delete").click(function(e) { 
	      that.protoEvent.send("removeApp", project);
	      e.stopPropagation();
	    });
	  })
	  .hover(function() {
	    //console.log("hover " + filter + " " + project.name)
	    that.protoEvent.send("deviceProjectHighlight", {"filter":filter, "projectName":project.name});
	  }).appendTo('#list_projects #'+div+" ul");
	
	  obj.items.push(project.name);
	});

  currentObject = obj;

};


Ui.prototype.loadHTMLRightBar = function(filePath) { 
	//w2ui['layout'].load('right', filePath);
	//w2ui['layout'].load('right', filePath, 'slide-left');
	$("#reference_container #content").load(filePath).fadeIn('500');
}

Ui.prototype.resetTabs = function() {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	q2.tabs.add([{id : "tab0", caption : 'Main', closable: false }]);
	this.tabs = {};
	this.tabId = 0;
}

//http://w2ui.com/web/docs/w2tabs.add 
Ui.prototype.addTabAndCode = function(name, code) {

	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	q2.tabs.add([{
		id : "tab"+this.tabId, 
		caption : name, closable: true, 
		onDblClick: function(event) {
        	console.log(event);
    	}
   	}]);
	
	q2.tabs.active = "tab" + this.tabId;
	q2.tabs.refresh();
	
	protocoder.event.send("editor_setTypeAndCode", {"type":name, "code":code});

	this.tabs[name] = {};
	this.tabs[name].id = "tab"+this.tabId;
	this.tabId++;
	this.tabs[name].code = code;
}

Ui.prototype.getTabNameById = function(id) {
	for (i in this.tabs) { 
	  if (this.tabs[i].id == id) { 
	    return i; 
	  } 
	}; 
}

Ui.prototype.textChanged = function() {


}

Ui.prototype.removeTab = function(id) {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");
	q2.tabs.remove(id);
}

Ui.prototype.removeAllTabs = function() {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	for (i in this.tabs) { 
		var id = this.tabs[i].id;

		if (id != 'tab0') { 
			q2.tabs.remove(id); 
		}
	};

}

Ui.prototype.setMainTab = function(name, code) {
	this.removeAllTabs();

	var tabs = w2ui['code_editor'].get("main").tabs;
	tabs.get("tab0").caption = name;
	tabs.active = "tab0";

	tabs.refresh();
	this.tabs = {};

	this.tabId = 0;
	this.tabs[name] = {};

	protocoder.event.send("editor_setTypeAndCode", {"type":"main.js", "code":code});
	this.tabs[name].id = "tab"+this.tabId;
	this.tabId++;
	this.tabs[name].code = code;
	this.tabs[name].name = "main.js";
	this.activeTab = this.tabs[name];
}

Ui.prototype.setActiveTab = function(id) {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	q2.tabs.active = id;
	q2.tabs.refresh();  

	var name = this.getTabNameById(id);
	var code;

	//console.log(id + " " + name);

	if ( !(this.tabs[name]) )  {
		code = "";
	} else {
		this.activeTab = this.tabs[name];
		code = this.tabs[name].code;
		this.tabs[name].name = name;

		if (id === "tab0") {
			//console.log(this.tabs);
			this.tabs[name].name = "main.js";
		}
	}


	this.protoEvent.send("editor_setTypeAndCode", {"name":name, "code":code});
}; 

Ui.prototype.getActiveTab = function() {
	return this.activeTab;
}

Ui.prototype.getOpenTabs = function() {
	return this.tabs;
}

Ui.prototype.clearFileElements = function() {
	var that = this;
  	w2ui['grid'].clear();
  	setTimeout(function() {
  	  	that.bindUpload();

  	}, 500);
}

Ui.prototype.addFileElement = function(f) {
  	w2ui['grid'].add( f );
}

Ui.prototype.setTabFeedback = function(f) {

} 

Ui.prototype.openNewWindow = function(f) {
	window.open("http://192.168.1.100:8585", "_blank", "toolbar=no, location=no, status=no, resizable=yes, width=500, height=500, left=10, top=10");
}

Ui.prototype.consoleLog = function(text) {
    //limit to 1000 the num of log entries that can be displayed 
   if (self.countLogs > 1000) { 
		$("#console_wrapper #console p").slice(0, 100).remove(); 
		self.countLogs -= 100;
	}

   $("#console_wrapper #console").append(text);        
   var c = $("#console_wrapper #console")[0];
   c.scrollTop = c.scrollHeight;

   self.countLogs++;
}

Ui.prototype.initUpload = function() {
	that = this;
	//console.log("initUpload");
	this.dropboxEnabled = false;

	$("#dropbox").remove();
	if (currentProject.name != "undefined") { 
		$("#grid_grid_records").append('<div id = "dropbox"> </div>');

		var dropbox = $('#dropbox'),
			message = $('.message', dropbox);
		

		dropbox.filedrop({
			// The name of the $_FILES entry:
			paramname:'pic',
			
			maxfiles: 100,
	    	maxfilesize: 25,
			url: './?' + 'name=' + currentProject.name + '&fileType=' + currentProject.type,
			
			uploadFinished:function(i,file,response){
				$.data(file).addClass('done');
				that.protoEvent.send("listFilesInProject", currentProject);
				// response is the JSON object that post_file.php returns
				//$('#dropbox').css("background-color", "rgba(0, 0, 0, 0.1);");
				$("#grid_grid_records #dropbox").remove();
				that.dropboxEnabled = true;

			},
			
			dragEnter: function() { 
				//$('#dropbox').css("background-color", "rgba(255, 0, 0, 0.5);");
			},

			dragOver: function() { 
				$('#dropbox').css("background-color", "rgba(255, 0, 0, 0.5);");
			}, 

			dragLeave: function() { 
				$('#dropbox').css("background-color", "rgba(0, 0, 0, 0.1);");
				$("#grid_grid_records #dropbox").remove();
				that.dropboxEnabled = true;

			},
	    	error: function(err, file) {
				switch(err) {
					case 'BrowserNotSupported':
						showMessage('Your browser does not support HTML5 file uploads!');
						break;
					case 'TooManyFiles':
						alert('Too many files! Please select 10 file at most!');
						break;
					case 'FileTooLarge':
						alert(file.name+' is too large! Please upload files up to 5mb.');
						break;
					default:
						break;
				}
			},
			
			// Called before each upload is started
			beforeEach: function(file){
				/*
				if(!file.type.match(/^image\//)){
					alert('Only images are allowed!');
					
					// Returning false will cause the
					// file to be rejected
					return false;
				}
				*/
			},
			
			uploadStarted:function(i, file, len){
				createImage(file);
			},
			
			progressUpdated: function(i, file, progress) {
				$.data(file).find('.progress').width(progress);
			}
	    	 
		});
		
		var template = '<div class="preview">'+
							'<div class="progressHolder">'+
								'<div class="progress"></div>'+
							'</div>'+
						'</div>'; 
		
		
		function createImage(file){

			var preview = $(template);
				//image = $('img', preview);
				
			var reader = new FileReader();
			
			//image.width = 100;
			//image.height = 100;
			
			//reader.onload = function(e){
				// e.target.result holds the DataURL which
				// can be used as a source of the image:
				// image.attr('src',e.target.result);
			//};
			
			// Reading the file as a DataURL. When finished,
			// this will trigger the onload function above:
			reader.readAsDataURL(file);
			
			message.hide();
			preview.appendTo(dropbox);
			
			// Associating a preview container
			// with the file, using jQuery's $.data():
			$.data(file, preview);
		}

		function showMessage(msg){
			message.html(msg);
		}
	}
};

