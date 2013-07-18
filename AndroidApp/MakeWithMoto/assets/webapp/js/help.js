

function parse_help(docString) {
	console.log(docString);
	var doc = JSON.parse(docString).apiClasses;

	//iterate through classes 
	for (var j in doc) {
	    //all
	    console.log(doc[j]);

	    //class
	    var className = doc[j].name;
	    console.log(className);
	    $("#help").append('<div id = "'+ className+'" class = "APIclass"> <h1>' + className + ' </h1></div>');

	    //iterate through api methods 
	    for (var k in doc[j].apiMethods) {
	        
	        var method = doc[j].apiMethods[k];
	        $("#"+className).append('<div id ='+ method.name +' class = "APImethod"></div>');
	        $("#"+method.name).append('<h2>'+ method.name +'</h2>');
	        
	        if (method.description != undefined) $("#"+method.name).append('<p> '+ method.description +' </p>');
	        if (method.example != undefined) $("#"+method.name).append('<p> '+ method.example +' </p>');


	        console.log(method.name, method.description, method.example);
	    }
	}
}