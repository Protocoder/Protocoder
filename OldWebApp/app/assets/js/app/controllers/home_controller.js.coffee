app.controller "HomeController", ['$scope', '$http', 'webSocketService', 'parserService', ($scope, $http, webSocketService, parserService) ->

  $scope.application = {}
  $scope.projects = []
  $scope.selected_project = null
  $scope.creatingNewProject = false
  $scope.newProjectName
  $scope.name = ""
  $scope.code = "//Loading code..."
  $scope.errors = []
  $scope.saveEnabled = false
  $scope.running = false
  $scope.isConnected = false;

  $scope.currentOutput = {
    info: []
    debug: []
  }
      
  webSocketService.onConnected -> 
    $scope.$apply -> $scope.isConnected = true
  webSocketService.onDisconnected ->
    $scope.$apply -> $scope.isConnected = false

  webSocketService.getProjects().then (msg) ->
    $scope.projects = msg.projects
    $scope.selected_project = $scope.projects[0]
  webSocketService.subscribe 'log_event', (msg) ->
    $scope.$apply ->
      $scope.addLogLine(msg.tag, msg.msg)
  webSocketService.subscribe 'save_event', (msg) ->
    $scope.$apply ->
      project = null
      for proj in $scope.projects
        if proj.name == msg.name
          project = proj
      if project
        # FORCE UPDATE
        $scope.selected_project = null
        $scope.selected_project = project

  $scope.$watch 'selected_project', (name) ->
    if name
      getCodeFor(name)
      
  getCodeFor = (name) ->
    webSocketService.getCodeFor(name).then (msg) ->
      $scope.code = msg.code
      $scope.name = msg.name
      $scope.creatingNewProject = false
      $scope.saveEnabled = true
      
  $scope.addLogLine = (tag, msg) ->
    if ($scope.currentOutput[tag] == undefined)
      console.log("addLogLine", tag)
      $scope.currentOutput[tag] = []
    $scope.currentOutput[tag].unshift(msg)
    if $scope.currentOutput[tag].length > 1000
      $scope.currentOutput[tag].shift()

  $scope.newProject = () ->
    webSocketService.getNewCode().then (msg) ->
      $scope.code = msg.code

  ## Creating new project
  $scope.setCreatingNewProject = () ->
    $scope.creatingNewProject = true
  $scope.cancelCreatingNewProject = () ->
    $scope.creatingNewProject = false
  $scope.saveCreatingNewProject = () ->
    $scope.creatingNewProject = false
    webSocketService.createNewProject($scope.newProjectName).then (msg) ->
      console.log "createNewProject", msg
      $scope.projects.push msg.project
      $scope.selected_project = msg.project

  $scope.runProject = () ->
    $scope.saveEnabled = false
    $scope.running = true
    obj =
      name: $scope.selected_project.name
      code: $scope.code
    webSocketService.runProject(obj).then (msg) ->

  $scope.saveFile = () ->
    $scope.saveEnabled = false
    obj =
      name: $scope.selected_project.name
      code: $scope.code
    console.log(obj)
    webSocketService.saveFile(obj).then (msg) ->
      console.log "MSG", msg

  $scope.editorChanged = (evt, editor) ->
    # Implement autocomplete
    session = editor.getSession()
    aceDoc= session.getDocument()
    numberOfLines = aceDoc.getLength()
    # Clear the errors
    session.removeGutterDecoration(i, "editorError") for i in [0..numberOfLines ]
    tree = parserService.parse editor.getValue()
    # If we have errors, show them in the gutter
    if tree.errors
      for e in tree.errors
        session.addGutterDecoration(e.lineNumber - 1, "editorError")
        range = session.documentToScreenPosition(e.lineNumber, e.column)

  $scope.contentSuggest = (editor) ->
    cursor = editor.selection.getRange()
    text = editor.session.getLine(cursor.start.row)
    token = editor.session.getTokenAt(cursor.start.row, cursor.start.column)
    tree = parserService.contentAssist(token)
    # console.log tree
]
