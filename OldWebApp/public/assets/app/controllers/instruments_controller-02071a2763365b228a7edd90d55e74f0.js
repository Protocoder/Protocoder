var setup = function(element, obj) {
  var width   = 250, 
      height  = 90,
      keep    = 50;
      
	var graph = d3.select(element)
                .append("svg:svg")
                .attr("width", "100%")
                .attr("height", "100%")
              .append("g")
                .attr('transform', 'translate('+20+','+30+')');
  
  var data = [];
	var x = d3.scale.linear().domain([0, keep]).range([-5, width]);
	var y = d3.scale.linear().domain([-1, 1]).range([height, 0]);
  // Line object
  var line  = d3.svg.line()
                .x(function(d, i) {return x(i); })
                .y(function(d) {return y(d);})
                .interpolate('basis');
                
  graph.append('svg:path').attr('d', line(data));
  
  return function(msg) {
    data.push(y(msg.x));
    
    x.domain(d3.extent(data, function(d) { return x(d); }));
    y.domain([0, d3.max(data, function(d) { return d; })]);
    
    graph.selectAll('path')
          .data([data])
          .attr('transform', 'translate('+x(1)+')')
          .attr('d', line)
          .transition()
          .ease('linear')
          .duration(1000)
          .attr('transform', 'translate('+x(0)+')');
  
    if (data.length > keep){
      data.shift();
    }
  };
}

app.controller('InstrumentsController', ['$scope', 'webSocketService', function($scope, webSocketService) {
  $scope.accelerometerX = {path: []};
  
  webSocketService.subscribe("sensor", function(msg) {
    if (msg.name == "accelerometer") {
      updateX(msg);
    }
  });
  
  var updateX = setup($("#accelerometerx")[0], $scope.accelerometerX);
  webSocketService.runProject({});
}]);
