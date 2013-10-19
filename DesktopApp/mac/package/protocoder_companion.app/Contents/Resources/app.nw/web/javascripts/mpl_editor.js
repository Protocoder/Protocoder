

$(document).ready(function() { 

    /* 
    * EDITOR
    * 
    *
    */
    /*
    var require = {
    	baseUrl: window.location.protocol + "//" + window.location.host + window.location.pathname.split("/").slice(0, -1).join("/"),
    	paths: {
    		ace: "lib/ace"
    	}
    };
    */

	editor = ace.edit("editor"); 
	editor.setTheme("ace/theme/twilight"); 
	var textarea = $('textarea[name="editor"]').hide();
	var JavaScriptMode = require("ace/mode/javascript").Mode;
	var event = require("ace/lib/event");  
	var EditSession = require("ace/edit_session").EditSession;
	var UndoManager = require("ace/undomanager").UndoManager;

    session = editor.getSession();
	session.setMode(new JavaScriptMode()); 
	var renderer = editor.renderer;
    session.setWrapLimitRange(40, 40);
    renderer.setPrintMarginColumn(-12);
    
    helpOpen = false;
    editor.on("mousedown", function(ev) { 
        window.ev = ev;
        pos = ev.getDocumentPosition()
        var token = ev.editor.session.getTokenAt(pos.row, pos.column)
        //window.alert(token.value);
        
        if(token.value != 'undefined') {
    	    console.log(token.value);
	
    	    if(helpOpen == true) { 
    	        helpOpen = false;
    	        showHelp(false);
    	    } else {
    	        helpOpen = true;
    	        loadReference(token.value);
    	    }
        }	
    });
    
    
	
	function updateCodeLive(cursorPosition) { 
		//var editor = ace.edit("editor"); 

		numLine = cursorPosition['row']; 
		previousLine = session.getDocument().$lines[numLine - 1]; 
		currentLine = session.getDocument().$lines[numLine]; 
		nextLine = session.getDocument().$lines[numLine + 1]; 

        //set live coding 
		$('#linum0').text(numLine); 
		$('#linum1').text(numLine + 1);  
		$('#linum2').text(numLine + 2); 

		$('#li0').text(previousLine); 
		$('#li1').text(currentLine);  
		$('#li2').text(nextLine); 

		//sendCodeLine(cursorPosition); 
		
	} 
	
	var liveCoding = true; 
//evento cuando cambia el codigo	
	session.on('change', function () { 
		var cursorPosition = editor.getCursorPosition(); 	
		updateCodeLive(cursorPosition); 		
		if (liveCoding == true) {     //Guarda código si liveCoding == true
			 saveAndReload(session); 
		} 
		saveLocal(session) //TODO esto quizás es un poco excesivo grabar tan a menudo		
	});

	//TODO esto es parare de la interface… no debería ira aquí:
	
	 session.on('change', function () { 
  	     $('#saveButton').removeClass('greeny') ;

   }); 
	
//Cuando cambia la posición del cursor	
	editor.getSession().selection.on('changeCursor', function () { 
		var cursorPosition = editor.getCursorPosition(); 
		updateCodeLive(cursorPosition); 
	});
	
	
	editor.renderer.setShowPrintMargin = null; 

	//------------------------------------------------- 
	//keybinding 
	editor.commands.addCommands([{
	    name: 'saveCommand',
	    bindKey: {
	        win: 'Ctrl-M',
	        mac: 'Command-M',
	        sender: 'editor'
	    },
	    exec: function(env, args, request) {
			saveAndReload(session); 
	    }
	},{
	    name: 'saveRemote',
	    bindKey: {
	        win: 'Ctrl-S',
	        mac: 'Command-S',
	        sender: 'editor|cli'
	    },
	    exec: function(env, args, request) {
  			saveDecider(); 
  			return false
	    }, readonly:true
	},{
	    name: 'run',
	    bindKey: {
	        win: 'Ctrl-R',
	        mac: 'Command-R',
	        sender: 'editor|cli'
	    },
	    exec: function(env, args, request) {
	    console.log("enter")
  			$("#playButton").trigger('click')
	    }
	}]

	);


  loadCodeBeggining()	
	
}); // fin document ready

//------------------------------------------------- 
//load Code 
function loadCodeBeggining() { 
	//------------------------------------------------- 
	//if content was saved in the datastore then show it, 
	//if not go to original 



  		  jQuery.get('./data/fachadaMedialabPradoPequenya.pde', function(data) {
  	    	session.setValue(data);
    		}); 
  		  
  
  
  
}


//------------------------------------------------- 
//set Code 
function setCode(code) { 
	var editor = ace.edit("editor"); 
	var session = editor.getSession(); 
	session.setValue(unescape(code)); 
	saveAndReload(session);  
}


//------------------------------------------------- 
//save and Reload 
function saveAndReload(session) { 
	source = session.getValue(); 
//	console.log(source);	
	reloadCanvas(source); 
	localStorage.setItem('code', source); 
	
	//if (fachada == false) sendCode(source); 
} 


//------------------------------------------------- 
//save onlyLocal
function saveLocal(session) { 
  source = session.getValue(); 	
	localStorage.setItem('code', source); 
} 


//------------------------------------------------- 
//add Help 
function addHelp() {
	
	
}

//------------------------------------------------- 
//show javascript object methods and fields 
function listProperties(obj) {
   var propList = "";
   for(var propName in obj) {
      if(typeof(obj[propName]) != "undefined") {
         propList += (propName + ", ");
      }
   }
   //window.alert(propList);
}

	
//------------------------------------------------- 
// refresh canvas
var procInit = false;
var frameCount = 0;
function reloadCanvas(source) {

	var canvasHTML = "<iframe id = 'iframeProcessing' src='./html/canvas.html'> </iframe>";	
		
    //remember previous frameCount
    if (procInit) {
        try {
            frameCount = $("#iframeProcessing")[0].contentWindow.getProcessing().frameCount;
        } catch(e) {
            console.log("okdsñko");
        }
        console.log("lalala");
    } else {
        console.log("lololo");
    }
    
	// reload canvas 
	$("#iframeProcessing").remove();
    $("#processing").append(canvasHTML); 
	
	$("#iframeProcessing").load(function() {
	    console.log("qq2"+frameCount);
	    $("#iframeProcessing")[0].contentWindow.changeSketch(source, frameCount);
        
    });

	$("#display").fadeIn("normal");
}



/* 
* UI 
*
* 
*/ 


$(document).ready(function() { 

	var elem = document.querySelector('body');
    //enterFullscreen();



	function inputChange(e) {
	  if (e.results) { // e.type == 'webkitspeechchange'
	    for (var i = 0, result; result = e.results[i]; ++i) {
	      console.log(result.utterance, result.confidence);
	    }
	    console.log('Best result: ' + this.value);
	  }
	}


	document.addEventListener('webkitvisibilitychange', function(e) {
	  console.log('hidden:' + document.webkitHidden,
	              'state:' + document.webkitVisibilityState)
	}, false); 

	//mira si hay conectividad a internet o no 
	window.addEventListener('online', function(e) {
	  // Re-sync data with server.
	}, false);

	window.addEventListener('offline', function(e) {
	  // Queue up events for server.
	}, false); 


	var liveCoding = false; 
	var fachada = false; 


	function saveImage() { 
	  q = document.getElementsByTagName("canvas")[0]
	  image = q.toDataURL(); 
	  code = "";

	  $.post('../../savePNG/', {image:image, code:escape(code)}, function(data) { 
		console.log(data)
	  }); 

	}

	function sendCodeLine(codeLine) { 
		//console.log(code); 
		$.post('../../setConfig/', {config:codeLine}, function(data) {
			console.log(data)
		}); 		
	} 
	
	/*
	$("#slides").slides({
	    preload: true,
	    generateNextPrev:true
	});
	*/ 
	
    

	/*	
	$('#liveCoding').click (function () { 		
		$(this).is (':checked') ? liveCoding = true : liveCoding = false; 

	}); 
	*/
	/*
	$('#fachada').click (function () { 
		if ($(this).is (':checked')) {  
		   fachada = true; 
		  getCodeListener(); 
		} else {
		  fachada = false; 
		  source = null;
		} 
	}); 
	

	$('#muestracodigo').click (function () { 		
		$('#muestracodigo').is (':checked') ? showLiveCode(true) : showLiveCode(false); 

	});

	function showLiveCode(visibility) { 
		if (visibility == true) { 
			$('#codeoncanvas').show(); 
		} else { 
			$('#codeoncanvas').hide(); 
			
		}
	}
    */
	
}); 