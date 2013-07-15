var setup = function(element, obj) {
  var n = obj.n || 40,
      delay = obj.delay || 500,
      data = d3.range(n).map(function() { return 0; });
 
  var margin = {top: 10, right: 10, bottom: 20, left: 40},
      width = (obj.width || 350) - margin.left - margin.right,
      height = (obj.height || 250) - margin.top - margin.bottom;
 
      // Set the scale from 0 to n-1
  var x = d3.scale.linear()
      .domain([0, n - 1])
      .range([0, width]);
 
      // Set the y scale so we go from the top to the bottom
  var y = d3.scale.linear()
      .domain([-1, 1])
      .range([height, 0]);
 
      // Setup the line to stay within the scales
      // we specified
  var line = d3.svg.line()
      .x(function(d, i) { return x(i); })
      .y(function(d, i) { return y(d); });
 
      // Setup the svg element
  var svg = d3.select(element).append("svg")
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
    .append("g")
      // And move it over to sit within the bounds we specified above
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
 
      // Use a clipPath so we don't run off the end on 
      // either side
  svg.append("defs").append("clipPath")
      .attr("id", "clip")
    .append("rect")
      .attr("width", width)
      .attr("height", height);
 
      // Setup the x axis so that it's on the bottom of the chart
  svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.svg.axis().scale(x).orient("bottom"));
 
      // Set up the y axis so it's on the left side
  svg.append("g")
      .attr("class", "y axis")
      .call(d3.svg.axis().scale(y).orient("left"));
 
      // Finally, setup the path
  var path = svg.append("g")
      .attr("clip-path", "url(#clip)")
    .append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", line);
   
  return function(val, cb) {
 
    // push a new data point onto the back
    if (val) {
      data.push(val);
  
      x.domain([0, n - 1]);
      // x.domain(d3.extent(data, function(d) { return x(d); }));
      y.domain([d3.min(data, function(d) { return d; }), d3.max(data, function(d) { return d; })]);
 
      // redraw the line, and slide it to the left
      path
          .attr("d", line)
          .attr("transform", null)
        .transition()
          .duration(delay)
          .ease("linear")
          .attr("transform", "translate(" + x(-1) + ")")
          .attr('end', cb);
 
      // pop the old data point off the front
      data.shift();
    }
  }
}


app.controller('InstrumentsController', ['$scope', 'webSocketService', function($scope, webSocketService) {  
  var xPoints = [],
      yPoints = [],
      zPoints = [],
      n       = 50;
      updateAxis = function(arr, val) {
        if (val) arr.push(val);
        if (arr.length > n) { arr.pop(); }
      }
  // subscribe to the `sensor` event
  webSocketService.subscribe("sensor", function(msg) {
    if (msg.name == "accelerometer") {
      updateAxis(xPoints, msg.x);
      updateAxis(yPoints, msg.y);
      updateAxis(zPoints, msg.z);
    }
  });
  
  var graphOpts = {
    width: 250,
    height: 100,
    delay: 10,
    n: n
  }
  var updateX = setup($("#accelerometerx")[0], graphOpts);
  var updateY = setup($("#accelerometery")[0], graphOpts);
  var updateZ = setup($("#accelerometerz")[0], graphOpts);
  setInterval(function() {
    updateX(xPoints.pop());
    updateY(yPoints.pop());
    updateZ(zPoints.pop());
  }, 100);
  webSocketService.runProject({name: 'instruments'});
}]);
