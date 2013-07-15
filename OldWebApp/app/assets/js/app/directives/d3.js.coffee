app.directive 'd3', () ->
  restrict: 'A'
  transclude: true
  require: '?ngModel'
  template: '<div class="translucded" ng-transclude></div>'
  link: (scope, element, attrs, ngModel) ->
    height = attrs.height || 300
    width = attrs.width || 300
    
    if angular.isDefined(ngModel)
      scope.$watch ngModel, (v) ->
        console.log "value", v

        viz = d3.select(element[0])
          .append('svg')
          .attr('height', height)
          .attr('width', width)
        
        # Demo code
        viz.append('svg:line')
          .attr('x1', 30)
          .attr('y1', 30)
          .attr('x2', 90)
          .attr('y2', 120)
          .style('stroke', 'rgb(6,120,90)')
