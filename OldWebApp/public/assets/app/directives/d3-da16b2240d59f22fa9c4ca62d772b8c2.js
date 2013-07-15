(function() {
  app.directive('d3', function() {
    return {
      restrict: 'A',
      transclude: true,
      require: '?ngModel',
      template: '<div class="translucded" ng-transclude></div>',
      link: function(scope, element, attrs, ngModel) {
        var height, width;

        height = attrs.height || 300;
        width = attrs.width || 300;
        if (angular.isDefined(ngModel)) {
          return scope.$watch(ngModel, function(v) {
            var viz;

            console.log("value", v);
            viz = d3.select(element[0]).append('svg').attr('height', height).attr('width', width);
            return viz.append('svg:line').attr('x1', 30).attr('y1', 30).attr('x2', 90).attr('y2', 120).style('stroke', 'rgb(6,120,90)');
          });
        }
      }
    };
  });

}).call(this);
