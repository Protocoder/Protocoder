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

	//iterate through classes 
	$.each(doc, function(k, v) {
	    //all
	    //console.log(v);


	    //class
	    var className = v.name.substr(1, v.name.length).toLowerCase();
	   // console.log(className);
	    $("#reference").append('<div id = "class_'+ className+'" class = "card APIclass"> <h1>' + className + '</h1>  <div class = "methods"> </div>');

	    //iterate through api methods 
	    $.each (v.apiMethods, function(m, n) {

	        var method = n;
	        $("#class_"+className + " .methods").append('<div id ="method_'+ method.name +'" class = "APImethod"></div>');
	        $("#method_"+method.name).append('<h2><i>'+  method.returnType + " </i><strong>" + className + "." + method.name + "</strong><i>(" + method.parameters + ' )</i></h2>');
	        
	        if (method.description != undefined) { 
	        	$("#method_"+method.name).append('<p id = "description"> '+ "here goes description" /*method.description*/ +' </p>');
	        }
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

	       // console.log(method.name, method.description, method.example);
	    });
	});

	//foldable reference 
	$('.card h1').click(function(e){
		e.preventDefault();
		$(this).closest('.card').find('.methods').not(':animated').slideToggle();
	});
}