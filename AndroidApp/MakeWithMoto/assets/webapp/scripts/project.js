var currentProject = {};
currentProject.id = "ioio_1"; 

//set projects in project list 
function setProjects(data) { 
	console.log(data.projects);

  $(data.projects).each(function(k, project) {
    console.log("-->", k, project); 
   
    console.log("binding -> " + "#project_list " + "#"+project.name);
    $('<p id ="'+project.name+'">' + project.name + '</p>').click(function () {
      fetch_code(project.name);
      currentProject.id = project.name;
    }).appendTo("#project_list");
  
  });


};