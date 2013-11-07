/* 
* 	UI 
*/

var Ui = function() { 
	var dropboxEnabled = false;
	this.initUI();
}


Ui.prototype.initUI = function() { 

	var pstyle = 'border: 0px solid #dfdfdf; padding: 0px;';


	$('#layout').w2layout({
	    name: 'layout',
	    panels: [
	        { type: 'main', style: pstyle, content: 'main' },
	        { type: 'right', size: 300, style: pstyle, content: 'right', resizable: true, hidden: false}

	    ]
	});

	//w2ui['layout'].hide('right', false);


	$().w2layout({
	    name: 'code_editor',
	    panels: [
	       { type: 'main', style: pstyle, content: 'main', tabs: {
	            active: 'tab1',
	            name: 'tabs',
	            tabs: [
	                { id: 'tab1', caption: 'Code' },
	            ],
	            onClick: function (target, data) {
	            
	                //this.owner.content('main', target);
	            }
	        } },
	        { type: 'bottom', size: "100px", resizable: true, hidden: false, style: pstyle, content: '<div id = "console_wrapper"> <div id = "console"></div> </div>' }
	    ]
	});

	$().w2layout({
	    name: 'right_bar',
	    panels: [
	        { type: 'main', size: '70%', resizable: true, style: pstyle, content: '<div id = "reference_container" style="background:#dfdfdf"> <div id = "reference"><h1 style="color:#777777; font-size:1.25em; margin-bottom: 24px; font-style:normal; font-weight:500; text-shadow: 2px 2px #eeeeee; vertical-align:middle;"> QUICK REFERENCE</h1> <div id ="content"> </div></div> </div>' },
	        { type: 'bottom', size: '30%', resizable: true, hidden: false, style: pstyle, content: 'bottom', 
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

	var gridRendered = false; 
	$('#grid').w2grid({ 
	    name: 'grid', 
	    url: '',
	    onRender: function() { 
	        gridRendered = true;
	    },
	    columns: [              
	        { field: 'file_name', caption: 'File Name', size: '70%' },
	        { field: 'file_size', caption: 'File Size', size: '30%' },
	    ]
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
	        console.log(e.target);

	        if (dropboxEnabled == false) {
	            initUpload();
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
	        console.log(e.target); 
	        dropboxEnabled = false;
	      	$("#dropbox").remove();
	        //initUpload();
	        e.preventDefault();
	        e.stopPropagation();
	    });
 	}, 2000);

}

Ui.prototype.showProjectsStatus = false; 

Ui.prototype.showProjects = function() {
	if (this.showProjectsStatus == false) { 
		$("#list_projects").fadeIn(300);
	} else { 
		$("#list_projects").fadeOut(300);
	} 

	this.showProjectsStatus ^= true;
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


Ui.prototype.initUpload = function() {
	dropboxEnabled = true;

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
				$('#dropbox').css("background-color", "rgba(0, 0, 0, 0.1);");

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
							'<span class="imageHolder">'+
								'<img />'+
								'<span class="uploaded"></span>'+
							'</span>'+
							'<div class="progressHolder">'+
								'<div class="progress"></div>'+
							'</div>'+
						'</div>'; 
		
		
		function createImage(file){

			var preview = $(template), 
				image = $('img', preview);
				
			var reader = new FileReader();
			
			image.width = 100;
			image.height = 100;
			
			reader.onload = function(e){
				
				// e.target.result holds the DataURL which
				// can be used as a source of the image:
				
				image.attr('src',e.target.result);
			};
			
			// Reading the file as a DataURL. When finished,
			// this will trigger the onload function above:
			reader.readAsDataURL(file);
			
			message.hide();
			preview.appendTo(dropbox);
			
			// Associating a preview container
			// with the file, using jQuery's $.data():
			
			$.data(file,preview);
		}

		function showMessage(msg){
			message.html(msg);
		}
	}
};

