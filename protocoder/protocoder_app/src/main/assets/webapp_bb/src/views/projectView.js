define([
  'underscore',
  'backbone',
  'text!src/tpl/project.html'
], function (_, Backbone, projectHtml) {

  'use strict';

  var ProjectView = Backbone.View.extend({

    /**
     * Init.
     */
    initialize: function () {

      this.currentProject = new Object();
      this.currentProject.name = "";
      this.currentProject.url = "";
      this.currentProject.type = -1;
      this.currentObject;
      this.listExamples;
      this.listProjects;

      return this;
    },

    /**
     * Render html.
     */
    render: function() {

      return this;
    },

    /**
     * set projects in project list
     */
    setProjectList: function (filter, data) {
      //console.log(data.projects);
      var self = this;

      var div = filter;
      if (filter == "examples") {
        this.listExamples = data.projects;
      } else if (filter == "projects") {
        this.listProjects = data.projects;
      }

      // @TODO convert this code into a template ?

      $("#list_projects #" + div + " ul").empty();

      var obj = new Object();
      obj.items = new Array();

      $(data.projects).each(function (k, project) {
        //console.log("-->", k, project);
        //console.log('#list_projects #'+div+" #ul");
        // console.log("binding -> " + div + " #"+project.name);
        $('<li id ="' + project.name + '"><span>' + project.name + '</span><div id ="actions"> <div id ="cont"> <i id = "rename" class="fa fa-pencil"></i> <i id = "delete" class="fa fa-trash-o"> </i>   </div> </div> </li>').click(function () {
          self.currentProject = project;
          self.currentProject.type = filter;
          protocoder.communication.fetchCode(project.name, project.type);
          protocoder.ui.showProjects(false);

          $("#list_projects #" + project.name + " #rename").click(function (e) {
            console.log("rename");
            protocoder.communication.renameProject(project);
            e.stopPropagation();

          });

          $("#list_projects #" + project.name + " #delete").click(function (e) {
            console.log("delete");
            protocoder.communication.removeApp(project);
            e.stopPropagation();
          });


        }).appendTo('#list_projects #' + div + " ul");
        obj.items.push(project.name);
      });

      currentObject = obj;


    },

    getProject: function (list, name) {
      $.each(list, function (k, v) {
        if (v.name == name) {
          console.log(k, v);
          return v;
        }
      });
    }

  });

  return ProjectView;

});