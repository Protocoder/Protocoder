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
	    	currentProject.code = escape(session.getValue());
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
	    	console.log(currentProject);
	    	console.log(currentProject.name, currentProject.type);
	    	currentProject.code = escape(session.getValue());
	    	push_code(currentProject);
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


}

function hideDashboard() { 
	$("#overlay #container").fadeOut(200);
} 

function showDashboard() { 
	$("#overlay #container").fadeIn(300);

}


//set Code 
function setCode(code) { 
	var editor = ace.edit("editor"); 
	var session = editor.getSession(); 
	session.setValue(code); 
}

