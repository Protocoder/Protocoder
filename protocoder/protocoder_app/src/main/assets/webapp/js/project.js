/*
*
*
*/

var currentProject = new Object();
currentProject.name = "";
currentProject.url = ""; 
currentProject.type = -1;
var currentObject; 
var listExamples;
var listProjects;

//set projects in project list 
function setProjectList(filter, data) { 
	//console.log(data.projects);

	var div = filter;
	if (filter == "examples") { 
    listExamples = data.projects;
	} else if (filter == "projects") {
    listProjects = data.projects;
	} 

  $("#list_projects #"+div+" ul").empty(); 

  var obj = new Object();
  obj.items = new Array();

  $(data.projects).each(function(k, project) {
   //console.log("-->", k, project); 
   //console.log('#list_projects #'+div+" #ul");
   // console.log("binding -> " + div + " #"+project.name);
  $('<li id ="'+project.name+'"><span>' + project.name + '</span><div id ="actions"> <div id ="cont"> <i id = "rename" class="fa fa-pencil"></i> <i id = "delete" class="fa fa-trash-o"> </i>   </div> </div> </li>').click(function () {
      currentProject = project;
      currentProject.type = filter;
      protocoder.communication.fetchCode(project.name, project.type);
      protocoder.ui.showProjects(false);

      $("#list_projects #" + project.name + " #rename").click(function(e) { 
        console.log("rename");
        protocoder.communication.renameProject(project);
        e.stopPropagation();

      });

      $("#list_projects #" + project.name + " #delete").click(function(e) { 
        console.log("delete");
        protocoder.communication.removeApp(project);
        e.stopPropagation();
      });


    }).appendTo('#list_projects #'+div+" ul");
    obj.items.push(project.name);
  });

  currentObject = obj;


};

function getProject(list, name) { 
  $.each(list, function(k, v) {
    if (v.name == name) {
      console.log(k,v); 
      return v;
    }
  });

}