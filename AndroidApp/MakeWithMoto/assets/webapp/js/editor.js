var editor; 
var session;

function initEditor() { 

	/* 
	*	Editor 
	*/ 
	editor = ace.edit("editor");
	session = editor.getSession();

	//editor.setTheme("ace/theme/monokai");
	session.setMode("ace/mode/javascript");
	//var EditSession = require("ace/edit_session").EditSession;
	//var UndoManager = require("ace/undomanager").UndoManager;

	var renderer = editor.renderer;
    session.setWrapLimitRange(40, 40);
    renderer.setPrintMarginColumn(-12);

    editor.renderer.setShowPrintMargin = null; 
	editor.setTheme("ace/theme/chrome");
	editor.setFontSize(15);
	//------------------------------------------------- 
	//keybinding 

	//save
	editor.commands.addCommand({
	    name: 'save_command',
	    bindKey: {
	        win: 'Ctrl-S',
	        mac: 'Ctrl-S',
	        sender: 'mmeditor'
	    },
	    exec: function(env, args, request) {
	    	currentProject.code = session.getValue();
	    	push_code(currentProject);
	    }
	});

	//save
	editor.commands.addCommand({
	    name: 'run_command',
	    bindKey: {
	        win: 'Ctrl-R',
	        mac: 'Ctrl-R',
	        sender: 'mmeditor'
	    },
	    exec: function(env, args, request) {
	    	currentProject.code = session.getValue();
	    	push_code(currentProject);
	    	removeWidgets();
	    	run_app(currentProject);

	    }
	});


	/* 
	* Binding UI 
	*/
	var overlayShow = false;
	$("#overlay #toggle").click(function() { 
		if (overlayShow) {
			hideDashboard();
		} else {
			showDashboard();
		}

		overlayShow ^= true;
	});
	$("#overlay #container #project_list").draggable();
	$("#overlay #container #example_list").draggable();

	$("#overlay #container #device_status").draggable();
	$("#overlay #container #help").draggable();
	$("#overlay #container #connection").draggable();
	$("#error_from_android").draggable();
	$("#remote_log").draggable();

	if (location.hash.indexOf("#dashboard") != -1) {
		showDashboard();
	}

}

function hideDashboard() { 
	$("#overlay #container").fadeOut(200);
} 

function showDashboard() { 
	$("#overlay #container").fadeIn(300);

}


//set Code 
function setCode(code) { 
	session.setValue(code); 
}

