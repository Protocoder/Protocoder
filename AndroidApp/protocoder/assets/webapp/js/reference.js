/*
*	Reference
*/ 

var Reference = function() { 

}

//parse the help comming directly from the android and create a structure for it
Reference.prototype.parseHelp = function (docString) {
	//console.log(docString);
	var ds = docString;
	var doc = JSON.parse(docString).apiClasses;

	var countReturns = 0;

	//iterate through classes 
	$.each(doc, function(k, v) {
	    //all
	    //console.log(v);

	    //class
	    var className = v.name.substr(1, v.name.length).toLowerCase();
	   // console.log(className);
	    $("#reference").append('<div id = "class_'+ className+'" class = "card APIclass"> <h2>' + className + '</h2>  <div class = "methods"> </div>');

	    //iterate through api methods 
	    $.each (v.apiMethods, function(m, n) {

	        var method = n;

	        //className 
	        var m = $('<div id ="method_'+ method.name +'" class = "APImethod"></div>');
	        $("#reference #class_"+className + " .methods").append(m);
	      
			//method [return] methodName [parameters]      
			var parameters = "";
			if (typeof method.parametersName !== "undefined") {
				parameters = method.parametersName.join(", ");
			}

			//if return type is void dont show it 
			if (method.returnType == "void") method.returnType = "";

	        $("#reference #class_"+className + " #method_"+method.name).append('<h3><span id = "returnType">'+  method.returnType + " </span><strong>" + className + "." + method.name + '</strong><i>(<span id = "params">' +  parameters + '</span>)</i></h3>');
	        
	        //add description if exist 
	        if (method.description != undefined) { 
	        	$("#method_"+method.name).append('<p id = "description"> '+ "here goes description" /*method.description*/ +' </p>');
	        }

	        //add example if exist 
	        if (method.example != undefined){ 
				$("#method_"+method.name).append('<p id = "example"> '+ "here goes example" /* method.example */ +' </p>');
	        	
	        	/* 
	        	$('<button> '+ method.name +' </button>')
	        		.click(function() {
	        			console.log("qq" + method.name)
	        		})
	        		.appendTo("#"+method.name);
	        	*/
	        	
	        }

	        $("#class_" + className).find(".methods").not(':animated').slideToggle()

 			//when click on reference insert text 
	        m.click(function() { 
	        	var returnType = "";
	        	if (method.returnType != "") {
	        		returnType = "var var" + countReturns++ + " = ";
	        	}

	        	// add curly braces when insert a callback
	        	var p = parameters.split(",");
	        	for (var q in p) { 
	        		console.log(p[q].indexOf("function")); 

	        		if (p[q].indexOf("function") == 1) { 
	        			console.log("hola");
	        			p[q] = p[q] + " { " + '\n' + '\n' + "}"
	        		}
	        	} 
	        	p.join(",");

	        	protocoder.editor.editor.insert(returnType + "" + className + "." + method.name + "(" + p + ");"+'\n\n')
	        });
	       // console.log(method.name, method.description, method.example);
	    });
	});

	//foldable reference 
	$('.card h2').click(function(e){
		e.preventDefault();
		$(this).closest('.card').find('.methods').not(':animated').slideToggle();
	});
}