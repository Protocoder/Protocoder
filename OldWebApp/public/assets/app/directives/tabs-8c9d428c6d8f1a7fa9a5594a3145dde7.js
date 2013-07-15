(function() {
  app.directive('tabs', function() {
    return {
      restrict: "E",
      transclude: true,
      controller: [
        '$scope', '$element', function($scope, $element) {
          var panes;

          panes = $scope.panes = [];
          $scope.select = function(pane) {
            angular.forEach(panes, function(pane) {
              return pane.active = false;
            });
            return pane.active = true;
          };
          return this.addPane = function(pane) {
            if (panes.length === 0) {
              $scope.select(pane);
            }
            return panes.push(pane);
          };
        }
      ],
      template: "<div class=\"section-container tabs\" data-section ng-transclude></div>",
      replace: true
    };
  }).directive("pane", function() {
    return {
      require: "^tabs",
      restrict: "E",
      transclude: true,
      scope: {
        title: '='
      },
      link: function(scope, element, attrs, TabsCtl) {
        scope.title = attrs.title;
        return TabsCtl.addPane(scope);
      },
      template: "<section class='tab-content' ng-class=\"{active:active}\">" + "<p class=\"title\" data-section-title>" + "<a>{{title}}</a></p>" + "<div class='content' data-section-content ng-transclude></div>" + "</section>",
      replace: true
    };
  });

}).call(this);
