/*
*	Editor
*
*/

var Editor = function(p) { 
	this.protoEvent = p.event;
	
	//events
	this.liveExecRec = {
		liveExecHistory : [],
		firstTime : null 
	};
	
	this.init();
}

Editor.prototype.initEvents = function() {
	var that = this;
	
	that.protoEvent.listen("editor_resize", function(e) {
		that.editor.resize();
	});	
	
	that.protoEvent.listen("editor_setTypeAndCode", function(e) {
		//console.log(e);
		that.setTypeAndCode(e.detail.type, e.detail.code)
	});	
		
	that.protoEvent.listen("editor_saveCode", function(e) {
		that.saveCode();
	});	
		
	that.protoEvent.listen("editor_setCode", function(e) {
		that.setCode(e.detail.code);
	});	
	
	that.protoEvent.listen("editor_insert", function(e) {
		that.editor.insert(e.detail);
	});		
	
	that.protoEvent.listen("editor_runApp", function(e) {
		currentProject.code = that.session.getValue();
        that.protoEvent.send("communication_pushCode", {"currentProject":currentProject, "fileName":""});
        that.protoEvent.send("dashboard_removeWidgets");
        that.protoEvent.send("runApp", currentProject);    
	});
}

Editor.prototype.init = function() { 
	//init
	var that = this;
	var editor = ace.edit("editor");
	this.editor = editor;
	var session = editor.getSession();
	this.session = session;
	ace.require("ace/lib/fixoldbrowsers");
	session.setMode("ace/mode/javascript");
	var config = ace.require("ace/config");
	ace.require("ace/edit_session");
	ace.require("ace/undomanager");
   	ace.require("ace/marker");
   	ace.require("ace/range");
   	//ace.require("ace/ext/emmet");  

	//var dom = ace.require("ace/lib/dom");
	//var net = ace.require("ace/lib/net");
	//var lang = ace.require("ace/lib/lang");
	//var useragent = ace.require("ace/lib/useragent");
	this.Range = ace.require('ace/range').Range;
	var event = ace.require("ace/lib/event");
	var theme = ace.require("ace/theme/textmate");
	ace.require("ace/ext/language_tools");

	//editor.setTheme("ace/theme/chrome");

	var renderer = editor.renderer;

	session.setWrapLimitRange(null, null);

	//session.setUseWrapMode(true);
	//session.setWrapLimitRange();

    renderer.setShowPrintMargin = null; 

	editor.setPrintMarginColumn(false);
	editor.setFontSize(14);
	renderer.setPadding(8);

	editor.setOptions({
		enableBasicAutocompletion: true
	});

	//editor.setOption("enableEmmet", true);


	//evento cuando cambia el codigo	
	session.on('change', function() {
		//var cursorPosition = editor.getCursorPosition();
		
		if (that.isSaved != false ) {
			that.protoEvent.send("ui_setTabFeedback", true);
			that.isSaved = true;
		}
	});


	//------------------------------------------------- 
	//keybinding 

	//save
	editor.commands.addCommand({
	    name: 'save_command',
	    bindKey: {
	        win: 'Ctrl-S',
	        mac: 'Command-S',
	        sender: 'editor|cli'
	    },
	    exec: function(env, args, request) {
	    	that.saveCode();
	    }
	});

	//save
	editor.commands.addCommand({
	    name: 'run_command',
	    bindKey: {
	        win: 'Ctrl-R',
	        mac: 'Command-R',
	        sender: 'editor|cli'
	    },
	    exec: function(env, args, request) {
	    	currentProject.code = session.getValue();
	    	
	    	//that.protoEvent.send("saveProject", currentProject);
	    	that.protoEvent.send("runApp", currentProject);
	    }
	});

	//dashboard 
	editor.commands.addCommand({
	    name: 'show_hide_dashboard',
	    bindKey: {
	        win: 'Ctrl-Shift-D',
	        mac: 'Command-Shift-D',
	        sender: 'editor'
	    },
	    exec: function(env, args, request) {
	    	that.protoEvent.send("dashboard_toggle");
	    }
	});

	editor.commands.addCommand({
    name: "showKeyboardShortcuts",
    bindKey: {win: "Ctrl-Alt-h", mac: "Command-Alt-h"},
    exec: function(editor) {
        config.loadModule("ace/ext/keybinding_menu", function(module) {
            module.init(editor);
            editor.showKeyboardShortcuts()
        })
    }
	});

 
	editor.commands.addCommand({
    name: "snippet",
    bindKey: {win: "Alt-C", mac: "Command-Alt-C"},
    exec: function(editor, needle) {
        if (typeof needle == "object") {
            editor.cmdLine.setValue("snippet ", 1);
            editor.cmdLine.focus();
            return;
        }
        var s = snippetManager.getSnippetByName(needle, editor);
        if (s)
            snippetManager.insertSnippet(editor, s.content);
    },
    readOnly: true
	});

	 
	editor.commands.addCommand({
    name: "increaseFontSize",
    bindKey: "Ctrl-+",
    exec: function(editor) {
        var size = parseInt(editor.getFontSize(), 10) || 12;
        editor.setFontSize(size + 1);
    }
	});

	 
	editor.commands.addCommand({
    name: "decreaseFontSize",
    bindKey: "Ctrl+-",
    exec: function(editor) {
        var size = parseInt(editor.getFontSize(), 10) || 12;
        editor.setFontSize(Math.max(size - 1 || 1));
    }
	});


	//save
	editor.commands.addCommand({
	    name: 'liveExecution',
	    bindKey: {
	        win: 'Ctrl-Shift-X',
	        mac: 'Command-Shift-X',
	        sender: 'mmeditor'
	    },
	    exec: function(env, args, request) {

			var range = editor.getSelection().getRange(); 
	    	var selectedText = session.getTextRange(range); 
	    	var liveExec = {};
	    	var now = new Date().getTime();
	    	liveExec.time = now;

	    	if (that.liveExecRec.firstTime == null) {
	    		that.liveExecRec.firstTime = now;
	    	}

 	    	liveExec.selectedText = selectedText; 
	    	
	    	//get the code selected or the whole row 
	    	if (selectedText.length > 0) { 
	    		liveExec.range = range;
	    	} else { 
	    		var cursorPosition = editor.getCursorPosition();
    			var numLine = cursorPosition['row'];
    	
	    		liveExec.numLine = numLine;
	    	}
	    	that.runLiveExec(liveExec);
	    	that.liveExecRec.liveExecHistory.push(liveExec);

	    }
	});

	//play live execution history 
	editor.commands.addCommand({
	    name: 'playLiveExec',
	    bindKey: {
	        win: 'Ctrl-Shift-1',
	        mac: 'Command-Shift-1',
	        sender: 'mmeditor'
	    },

	    exec: function() { that.playLiveExec(false) },
	});

	//play live execution history 
	editor.commands.addCommand({
	    name: 'playLiveExec',
	    bindKey: {
	        win: 'Ctrl-Shift-2',
	        mac: 'Command-Shift-2',
	        sender: 'mmeditor'
	    },

	    exec: function() { that.playLiveExec(true) },
	});

	

	//play live execution history 
	editor.commands.addCommand({
	    name: 'playLiveExec',
	    bindKey: {
	        win: 'Ctrl-Shift-0',
	        mac: 'Command-Shift-0',
	        sender: 'mmeditor'
	    },

	    exec: that.clearLiveExecRec,
	});

	
	this.initEvents();
}


Editor.prototype.runLiveExec = function(liveExec) {
	//console.log(liveExec);

   	//get the code selected or the whole row 
	if (liveExec.selectedText.length > 0) { 
		this.protoEvent.send("liveExecute", liveExec.selectedText);
		this.highlight(liveExec.range);
	} else { 
		var currentLine = this.session.getDocument().$lines[liveExec.numLine]; 
		//console.log(liveExec.numLine + " " + currentLine + " " + currentLine.length);
		var range_line = new this.Range(liveExec.numLine, 0, liveExec.numLine, currentLine.length);


		if (currentLine.length > 0) { 
			this.protoEvent.send("liveExecute", currentLine);
			this.highlight(range_line);
		}

	}
}

Editor.prototype.clearLiveExecRec = function() { 
	this.liveExecRec.firstTime = null;
	this.liveExecRec.liveExecHistory = [];
}

Editor.prototype.playLiveExec = function(b) { 
	//console.log(this);
	var history = this.liveExecRec.liveExecHistory;
	var firstTime = this.liveExecRec.firstTime;

	for (var i = 0; i < history.length; i++) {
	
		(function(i) { 
			var delay = Math.abs(firstTime - history[i].time);
			console.log(delay);
			setTimeout(function() {
				that.protoEvent.send("liveExecute", history[i]);
			}, delay);
		})(i);

	};

}

Editor.prototype.highlight = function(range) { 
	var that = this;
	var marker = this.session.addMarker( range, "run_code", "fullLine" );

	setTimeout(function() { 
		that.session.removeMarker(marker);
	}, 500); 
}


Editor.prototype.showErrors = function () { 
	this.session.setAnnotations([{row:1 ,column: 0, text: "message",type:"error"}]); 
}

//set Code and Type
Editor.prototype.setTypeAndCode = function (type, code) { 
	this.setCode(code); 
	this.setType(type); 
}


//set Code 
Editor.prototype.setCode = function (code) { 
	this.session.setValue(code); 
}



//set Code 
Editor.prototype.saveCode = function () { 
	currentProject.code = this.session.getValue();
	var tab = protocoder.ui.getActiveTab();
	console.log(tab.name);
	//no tab
	if (!tab) {

	} else {
		this.protoEvent.send("saveProject", {"currentProject":currentProject, "tabName":tab.name});
		this.isSaved = true;
	}
}

//set Code 
Editor.prototype.setType = function (fileName) { 
	var fileExtension = fileName.split('.').pop();

	switch(fileExtension) { 
		case 'js': 
			this.session.setMode("ace/mode/javascript");
			break;
		case 'html': 
			this.session.setMode("ace/mode/html");
			break;
		case 'htm': 
			this.session.setMode("ace/mode/html");
			break;
			
	}
}

