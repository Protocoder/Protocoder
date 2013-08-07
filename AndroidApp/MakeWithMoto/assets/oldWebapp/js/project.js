var currentProject = {};
currentProject.name = "";
currentProject.url = ""; 
currentProject.type = -1;

//set projects in project list 
function setProjectList(filter, data) { 
	console.log(data.projects);

	var div = "";
	if (filter == "example") { 
		div = "#example_list";
	} else if (filter == "user") {
		div = "#project_list";
	}

  $(data.projects).each(function(k, project) {
    console.log("-->", k, project); 
   
    console.log("binding -> " + div + " #"+project.name);
    $('<p id ="'+project.name+'">' + project.name + '</p>').click(function () {
      fetch_code(project.name, project.url);
      currentProject = project;
      currentProject.type = filter;
    }).appendTo(div);
  
  });


};