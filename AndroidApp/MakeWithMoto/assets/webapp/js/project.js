var currentProject = {};
currentProject.name = "";
currentProject.url = ""; 
currentProject.type = -1;
var currentObject; 

//set projects in project list 
function setProjectList(filter, data) { 
	console.log(data.projects);

	var div = "";
	if (filter == "example") { 
		div = "list_examples";
	} else if (filter == "user") {
		div = "list_projects";
	} 

  var obj = new Object();
  obj.items = new Array();

  $(data.projects).each(function(k, project) {
   // console.log("-->", k, project); 
   
   // console.log("binding -> " + div + " #"+project.name);
    $('<p id ="'+project.name+'">' + project.name + '</p>').click(function () {
      fetch_code(project.name, project.url);
      currentProject = project;
      currentProject.type = filter;
    }).appendTo('#'+div);
    obj.items.push(project.name);
  });

  currentObject = obj;

  //setting the toolbar
  console.log("setting ", obj, "in", div);
  w2ui['toolbar'].set(div, obj);

};