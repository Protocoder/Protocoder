app.directive 'circuitViz', () ->
  restrict: 'E'
  replace: true
  scope:
    circuit: '='
  link: (scope, element, attrs) ->
    console.log "CIRCUIT"
    height = attrs.height || 300
    width = attrs.width || 300

    viz = d3.select(element[0])
      .append('svg')
      .attr('height', height)
      .attr('width', width)

    viz.append('svg:line')
      .attr('x1', 30)
      .attr('y1', 30)
      .attr('x2', 90)
      .attr('y2', 120)
      .style('stroke', 'rgb(6,120,90)')
