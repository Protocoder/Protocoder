(function() {
  app.controller("HomeController", [
    '$scope', '$http', '$state', '$stateParams', 'webSocketService', 'parserService', function($scope, $http, $state, $stateParams, webSocketService, parserService) {
      var getCodeFor;

      $scope.application = {};
      $scope.projects = [];
      $scope.selected_project = null;
      $scope.creatingNewProject = false;
      $scope.newProjectName;
      $scope.name = "";
      $scope.code = "//Loading code...";
      $scope.errors = [];
      $scope.saveEnabled = false;
      $scope.running = false;
      $scope.isConnected = false;
      $scope.currentOutput = {
        info: [],
        debug: []
      };
      webSocketService.onConnected(function() {
        return $scope.$apply(function() {
          return $scope.isConnected = true;
        });
      });
      webSocketService.onDisconnected(function() {
        return $scope.$apply(function() {
          return $scope.isConnected = false;
        });
      });
      if ($state.current.name === "home") {
        webSocketService.getProjects().then(function(msg) {
          $scope.projects = msg.projects;
          return $scope.selected_project = $scope.projects[0];
        });
        webSocketService.subscribe('log_event', function(msg) {
          return $scope.$apply(function() {
            return $scope.addLogLine(msg.tag, msg.msg);
          });
        });
        webSocketService.subscribe('save_event', function(msg) {
          return $scope.$apply(function() {
            var proj, project, _i, _len, _ref;

            project = null;
            _ref = $scope.projects;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              proj = _ref[_i];
              if (proj.name === msg.name) {
                project = proj;
              }
            }
            if (project) {
              $scope.selected_project = null;
              return $scope.selected_project = project;
            }
          });
        });
      }
      $scope.$watch('selected_project', function(name) {
        if (name) {
          return getCodeFor(name);
        }
      });
      getCodeFor = function(name) {
        return webSocketService.getCodeFor(name).then(function(msg) {
          $scope.code = msg.code;
          $scope.name = msg.name;
          $scope.creatingNewProject = false;
          return $scope.saveEnabled = true;
        });
      };
      $scope.addLogLine = function(tag, msg) {
        if ($scope.currentOutput[tag] === void 0) {
          console.log("addLogLine", tag);
          $scope.currentOutput[tag] = [];
        }
        $scope.currentOutput[tag].unshift(msg);
        if ($scope.currentOutput[tag].length > 1000) {
          return $scope.currentOutput[tag].shift();
        }
      };
      $scope.newProject = function() {
        return webSocketService.getNewCode().then(function(msg) {
          return $scope.code = msg.code;
        });
      };
      $scope.setCreatingNewProject = function() {
        return $scope.creatingNewProject = true;
      };
      $scope.cancelCreatingNewProject = function() {
        return $scope.creatingNewProject = false;
      };
      $scope.saveCreatingNewProject = function() {
        $scope.creatingNewProject = false;
        return webSocketService.createNewProject($scope.newProjectName).then(function(msg) {
          console.log("createNewProject", msg);
          $scope.projects.push(msg.project);
          return $scope.selected_project = msg.project;
        });
      };
      $scope.runProject = function() {
        var obj;

        $scope.saveEnabled = false;
        $scope.running = true;
        obj = {
          name: $scope.selected_project.name,
          code: $scope.code
        };
        return webSocketService.runProject(obj).then(function(msg) {});
      };
      $scope.saveFile = function() {
        var obj;

        $scope.saveEnabled = false;
        obj = {
          name: $scope.selected_project.name,
          code: $scope.code
        };
        console.log(obj);
        return webSocketService.saveFile(obj).then(function(msg) {
          return console.log("MSG", msg);
        });
      };
      $scope.isSaveEnabled = function() {
        return $scope.isConnected && $scope.selected_project && !$scope.creatingNewProject;
      };
      $scope.editorChanged = function(evt, editor) {
        var aceDoc, e, i, numberOfLines, range, session, tree, _i, _j, _len, _ref, _results;

        session = editor.getSession();
        aceDoc = session.getDocument();
        numberOfLines = aceDoc.getLength();
        for (i = _i = 0; 0 <= numberOfLines ? _i <= numberOfLines : _i >= numberOfLines; i = 0 <= numberOfLines ? ++_i : --_i) {
          session.removeGutterDecoration(i, "editorError");
        }
        tree = parserService.parse(editor.getValue());
        if (tree.errors) {
          _ref = tree.errors;
          _results = [];
          for (_j = 0, _len = _ref.length; _j < _len; _j++) {
            e = _ref[_j];
            session.addGutterDecoration(e.lineNumber - 1, "editorError");
            _results.push(range = session.documentToScreenPosition(e.lineNumber, e.column));
          }
          return _results;
        }
      };
      return $scope.contentSuggest = function(editor) {
        var cursor, text, token, tree;

        cursor = editor.selection.getRange();
        text = editor.session.getLine(cursor.start.row);
        token = editor.session.getTokenAt(cursor.start.row, cursor.start.column);
        return tree = parserService.contentAssist(token);
      };
    }
  ]);

}).call(this);
