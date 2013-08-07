
//parse the help comming directly from the android and create a structure for it
function parse_help(docString) {
	console.log(docString);
	ds = docString;
	var doc = JSON.parse(docString).apiClasses;
	d = doc;

	//iterate through classes 
	$.each(doc, function(k, v) {
	    //all
	    //console.log(v);


	    //class
	    var className = v.name;
	   // console.log(className);
	    $("#reference").append('<div id = "'+ className+'" class = "card APIclass"> <h1>' + className + ' </h1></div>');

	    //iterate through api methods 
	    $.each (v.apiMethods, function(m, n) {

	    	//console.log(m, n);

	        var method = n;
	        $("#"+className).append('<div id ='+ method.name +' class = "APImethod"></div>');
	        $("#"+method.name).append('<h2>'+ method.name +'</h2>');
	        
	        if (method.description != undefined) { 
	        	$("#"+method.name).append('<p id = "description"> '+ method.description +' </p>');
	        }
	        if (method.example != undefined){ 
				$("#"+method.name).append('<p id = "example"> '+ method.example +' </p>');
	        	$('<button> '+ method.name +' </button>')
	        		.click(function() {
	        			console.log("qq" + method.name)
	        		})
	        		.appendTo("#"+method.name);

	        }

	       // console.log(method.name, method.description, method.example);
	    });
	});
}