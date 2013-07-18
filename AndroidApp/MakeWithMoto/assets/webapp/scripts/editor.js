var editor; 
var session;

function initEditor() { 
	editor = ace.edit("editor");
	session = editor.getSession();

	editor.setTheme("ace/theme/monokai");
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
	        sender: 'editor'
	    },
	    exec: function(env, args, request) {
	    	push_code(currentProject.id, session.getValue());
	    }
	});

	//save
	editor.commands.addCommand({
	    name: 'save_command',
	    bindKey: {
	        win: 'Ctrl-R',
	        mac: 'Ctrl-R',
	        sender: 'editor'
	    },
	    exec: function(env, args, request) {
	    	run_app(currentProject.id);
	    }
	});


}


//set Code 
function setCode(code) { 
	var editor = ace.edit("editor"); 
	var session = editor.getSession(); 
	session.setValue(code); 
}

