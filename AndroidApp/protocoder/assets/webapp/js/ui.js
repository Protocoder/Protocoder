/* 
* 	UI 
*/

var Ui = function() { 
	this.dropboxEnabled = true; 
	this.gridRendered = false;
	this.init();
	this.tabs = {}; //[name] -> id , code 
	this.tabId = 0;
}


Ui.prototype.init = function() { 
	var that = this;




	$('#layout').w2layout({
	    name: 'layout',
	    panels: [
	        { type: 'main', content: 'main' },
	        { type: 'right', size: 300, content: 'right', resizable: true, hidden: false}

	    ],
	    onRender: function() { }
	});

	//w2ui['layout'].hide('right', false);


	$().w2layout({
	    name: 'code_editor',
	    panels: [
	       { type: 'main', content: 'main', tabs: {
	            active: 'tab1',
	            name: 'tabs',
	            tabs: [
	                { id: 'tab1', caption: 'Main' },
	            ],
	            onClick: function (target, data) {
	            	console.log(target);
	            	console.log(data);
	            	that.setTab(target);
	            }
	        } },
	        { type: 'bottom', size: "100px", resizable: true, hidden: false, content: '<div id = "console_wrapper"> <div id = "console"></div><div id = "console_input"><span>></span> <input type="text" name="fname" style=""> </div> </div>' }
	    ]
	});

	$().w2layout({
	    name: 'right_bar',
	    panels: [
	        { type: 'main', size: '70%', resizable: true, content: '<div id = "reference_container" style="background:#dfdfdf"> <div id = "reference"><h1> QUICK REFERENCE</h1> <div id ="content"> </div></div> </div>' },
	        { type: 'bottom', size: '30%', resizable: true, hidden: false, content: 'bottom', 
	      	/* TODO add toolbar */ 
	        /*
	        	toolbar: {
				items: [
					{ type: 'button',  id: 'upload',  caption: 'Upload', img: 'icon-save', hint: 'Hint for item 5' },
					{ type: 'button',  id: 'rename',  caption: 'Rename', img: 'icon-save', hint: 'Hint for item 5' },
					{ type: 'button',  id: 'delete',  caption: 'Delete', img: 'icon-save', hint: 'Hint for item 5' }

				],
				onClick: function (target, data) {
					this.owner.content('main', target);
				}
			}
			*/
		}
	    ]
	});
	$('#grid').w2grid({ 
	    name: 'grid', 
	    url: '',
	    onRender: function() { 
	        if (that.gridRendered == false) {
	        	//this.gridRendered = true;
		        //add tabs button 
		        //$("#layout_code_editor_panel_main").append('<div id="tabMenu"><div id="showHideMenu">+</div> <div id="menu"><ul> <li id = "addTab">New tab</li><li id = "renameTab">Rename selected tab</li><li id = "deleteTab"> Delete selected tab</li></ul></div></div>');
		        $("#layout_code_editor_panel_main").append('<div id="tabMenu"><div id="addTab">+</div></div>');
	

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

		    	//$("#tabMenu #addTab").click(function() { 
		    		//protocoder.ui.addTab("qq");
		    	//}); 

		    }
	    	/* 
	    	$(document).mouseup(function (e) {
   				var container = $("#tabMenu #menu");

			    if (!container.is(e.target) // if the target of the click isn't the container...
			        && container.has(e.target).length === 0) // ... nor a descendant of the container
			    {
     				container.hide();
  				}
			}); 
			*/

	    },
	    columns: [              
	        { field: 'file_name', caption: 'File Name', size: '70%' },
	        { field: 'file_size', caption: 'File Size', size: '30%' },
	    ], 

	    onClick: function(t, e) {
	    	console.log(t);
	    	console.log(e);

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
		//console.log(target); 
		//console.log(eventData); 
		eventData.onComplete = function() { 
			protocoder.editor.editor.resize();

			setTimeout(function() {
				$("#toolbar").addClass("show"); 
			}, 500);	

			setTimeout(function() {
				$("#overlay #toggle").addClass("show");
			}, 1000);

			setTimeout(function() {
				$("#container").addClass("on");
			//	//$("#start_curtain h1").removeClass("fade");
			//	$("#start_curtain").fadeOut();
			}, 700);
		}
	});


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
	                protocoder.communication.createNewProject( $("input#project_name").val() );
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
	* Binding UI 
	*/
	var overlayShow = false;
	$("#toolbar #newProjectBtn").click(function() { 
		openPopup();
	});

	//save file
	$("#toolbar #saveBtn").click(function() { 
		if (currentProject.length != 'undefined') { 
			currentProject.code = session.getValue();
			protocoder.communication.pushCode(currentProject);
		} else { 
			openPopup();
		}
	});

	//run app
	$("#toolbar #runBtn").click(function() { 
       if (currentProject.name != 'undefined') { 
            currentProject.code = protocoder.editor.session.getValue();
            protocoder.communication.pushCode(currentProject);
            protocoder.dashboard.removeWidgets();
            protocoder.communication.runApp(currentProject);
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
			protocoder.dashboard.hide();
		} else {
			protocoder.dashboard.show();
		}

		overlayShow ^= true;
	});


	$("#toolbar #projectsBtn").click(function() { 
		protocoder.ui.showProjects();
	});

	if (location.hash.indexOf("#dashboard") != -1) {
		protocoder.dashboard.show();
	}
	
	setTimeout(function() { 
	    $("#grid_grid_records").on('dragenter', function(e) {
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
	    	console.log("lalalalalal");
	   

	   });


		$("#console_wrapper input").keydown(function(e){ 
   			var code = e.which; // recommended to use e.which, it's normalized across browsers
    		if(code==13) {
				console.log("enter");
				var cmd = $("#console_wrapper input").val();
				consoleInputHistory.push(cmd);
				protocoder.communication.executeCode(cmd);
				$("#console_wrapper input").val("");
				currentHistoryEntry = consoleInputHistory.length;
				e.preventDefault();
			}

    	    if (code==38) {
		    	console.log("up");
		    	if (currentHistoryEntry > 0) {
		    		currentHistoryEntry--;
		    	} 
		    	$("#console_wrapper input").val(consoleInputHistory[currentHistoryEntry]);
		    } 
		    if (code == 40) {
		    	console.log("down");
		    	if (currentHistoryEntry < consoleInputHistory.length) {
		    		currentHistoryEntry++;
		    	}
		    	$("#console_wrapper input").val(consoleInputHistory[currentHistoryEntry]);
		    }
		});
 	}, 2000);

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

Ui.prototype.loadHTMLRightBar = function(filePath) { 
	//w2ui['layout'].load('right', filePath);
	//w2ui['layout'].load('right', filePath, 'slide-left');
	$("#reference_container #content").load(filePath).fadeIn('500');
}


//http://w2ui.com/web/docs/w2tabs.add 
Ui.prototype.addTabAndCode = function(name, code) {
	console.log("called");
	var q = w2ui['code_editor'];
	var q2 = q.get("main");
	q2.tabs.add([{id : this.tabId, caption : name, closable: true }]);
	//q2.tabs.active = this.tabId;
	//q2.tabs.refresh();
	protocoder.editor.setTypeAndCode(name, code);

	this.tabs[name] = {};
	this.tabs[name].id = this.tabId;
	this.tabId++;
	this.tabs[name].code = code;
}

Ui.prototype.getTabNameById = function(id) {
	for (i in protocoder.ui.tabs) { 
	  if (protocoder.ui.tabs[i].id == id) { 
	    return i; 
	  } 
	}; 
}

Ui.prototype.textChanged = function() {


}

Ui.prototype.removeTab = function() {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	q2.tabs.remove("tab7");

}

Ui.prototype.setTab = function(id) {
	var q = w2ui['code_editor'];
	var q2 = q.get("main");

	//q2.tabs.active = id;
	//q2.tabs.refresh();  
	var name = this.getTabNameById(id);
	console.log(id + " " + name);
	protocoder.editor.setTypeAndCode(name, this.tabs[name].code);

};


Ui.prototype.clearFileElements = function() {
  	w2ui['grid'].clear();
}

Ui.prototype.addFileElement = function(f) {
  	w2ui['grid'].add( f );
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
			
			maxfiles: 10,
	    	maxfilesize: 5,
			url: './?' + 'name=' + currentProject.name + '&fileType=' + currentProject.type,
			
			uploadFinished:function(i,file,response){
				$.data(file).addClass('done');
				protocoder.communication.listFilesInProject(currentProject.name, currentProject.type);
				// response is the JSON object that post_file.php returns
				//$('#dropbox').css("background-color", "rgba(0, 0, 0, 0.1);");
				$("#dropbox").remove();
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

