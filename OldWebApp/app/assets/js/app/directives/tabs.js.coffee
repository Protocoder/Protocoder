app.directive('tabs', ->
  restrict: "E"
  transclude: true
  controller: ['$scope', '$element', ($scope, $element) ->
    panes = $scope.panes = []
    $scope.select = (pane) ->
      angular.forEach panes, (pane) ->
        pane.active = false
      pane.active = true

    @addPane = (pane) ->
      $scope.select pane if panes.length is 0
      panes.push pane
  ]

  template: "<div class=\"section-container tabs\" data-section ng-transclude></div>"
  replace: true

).directive "pane", ->
  require: "^tabs"
  restrict: "E"
  transclude: true
  scope:
    title: '='
    
  link: (scope, element, attrs, TabsCtl) ->
    scope.title = attrs.title
    TabsCtl.addPane(scope)
  
  template: "<section class='tab-content' ng-class=\"{active:active}\">" +
            "<p class=\"title\" data-section-title>" +
            "<a>{{title}}</a></p>"+
            "<div class='content' data-section-content ng-transclude></div>" +
            "</section>"
  replace: true
