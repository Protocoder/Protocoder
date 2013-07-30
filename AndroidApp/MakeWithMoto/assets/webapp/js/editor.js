var editor; 
var session;

function initEditor() { 

	/* 
	*	Editor 
	*/ 
	editor = ace.edit("mmeditor");
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
	    	push_code(currentProject.url, session.getValue());
	    	run_app(currentProject.name, currentProject.url);
	    }
	});


	/* 
	* Binding UI 
	*/
	var overlayShow = false;

	$("#overlay #toggle").click(function() { 
		if (overlayShow) {
			$("#overlay #container").fadeOut(500);
		} else {
			$("#overlay #container").fadeIn(500);
		}

		overlayShow ^= true;
	});
	$("#overlay #container #project_list").draggable();
	$("#overlay #container #example_list").draggable();

	$("#overlay #container #device_status").draggable();
	$("#overlay #container #help").draggable();
	$("#overlay #container #connection").draggable();


}


//set Code 
function setCode(code) { 
	var editor = ace.edit("mmeditor"); 
	var session = editor.getSession(); 
	session.setValue(code); 
}

