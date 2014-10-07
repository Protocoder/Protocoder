/*
* Dashboard 
*
*/

var Dashboard = function() { 
  this.widgets = new Array();
  this.status = false;

}


Dashboard.prototype.hide = function () { 
  $("#overlay > #container").removeClass("on");
  this.status = false;
  $("#overlay #toggle").removeClass("on");
  $("body > #container").removeClass("off");
  $("body > #toolbar").removeClass("off");
} 

Dashboard.prototype.show = function () { 
  $("#overlay > #container").addClass("on");
  $("body > #container").addClass("off");
  $("body > #toolbar").addClass("off");
  $("#overlay #toggle").addClass("on");
  this.status = true;
}

Dashboard.prototype.toggle = function() {
  if (this.status == false) { 
    this.show();
  } else {
    this.hide();
  }
}


Dashboard.prototype.addWidget = function(widget) { 
  this.widgets.push(widget.id);

  if (widget.type == "plot") { 
    return this.addPlot(widget.id, widget.name, widget.x, widget.y, widget.w, widget.h, widget.minLimit, widget.maxLimit);
  } else if (widget.type == "button") {
    return this.addButton(widget.id, widget.name, widget.x, widget.y, widget.w, widget.h);
  } else if (widget.type == "slider") {
    return this.addSlider(widget.id, widget.name, widget.x, widget.y, widget.w, widget.h, widget.min, widget.max);
  } else if (widget.type == "label") { 
    return this.addLabel(widget.id, widget.name, widget.x, widget.y, widget.w, widget.h, widget.size, widget.color);
  } else if (widget.type == "input") { 
    return this.addInput(widget.id, widget.name, widget.x, widget.y, widget.w, widget.h);
  } else if (widget.type == "image") { 
    return this.addImage(widget.id, widget.url, widget.x, widget.y, widget.w, widget.h);
  } else if (widget.type == "html") { 
    return this.addHTML(widget.id, widget.html, widget.x, widget.y); 
  } else if (widget.type == "background") { 
    return this.setBackgroundColor(widget.r, widget.g, widget.b, widget.a); 
  } 

}


Dashboard.prototype.removeWidgets = function() { 
  $.each(this.widgets, function(k,v) {
    $("#overlay #container " + "#"+v).remove();
    protocoder.dashboard.widgets.pop(v); 
  });

  //remove everything
  $("#overlay #container").empty();
}

Dashboard.prototype.addHTML = function(element, html, posx, posy) { 
   $('<div class ="widget chtml" id = "html_' + element +'">'+ html +' </div>')
          .appendTo("#overlay #container")
          .css({"top":posy+"px","left":posx+"px"});

          //.css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"})
          //.draggable();
}


Dashboard.prototype.addLabel = function(element, name, posx, posy, w, h, size, color) { 
   $('<div class ="widget label" id = "label_' + element +'">'+ name +' </div>')
          .appendTo("#overlay #container")
          .css({"font-size": size+"px", "color":color, "top":posy+"px","left":posx+"px", "width":w+"px", "height":h+"px" })
          .draggable();
}


Dashboard.prototype.setLabelText = function(element, text) { 
   $("#overlay #container #label_"+ element).text(text)
}


Dashboard.prototype.changeImage = function(element, url) { 
  var img = $("#overlay #container #image_"+ element);
  img.attr("src", url);
  console.log(url);
  console.log("------");
  console.log(img);
}


Dashboard.prototype.addImage = function(element, url, posx, posy, w, h) { 
  if (url.indexOf("http") == -1) { 
    url = window.location.origin + "/apps/" + currentProject.type + "/" + currentProject.name + "/" + url;
  } 

   $('<img src = "'+url+'" class ="widget image" id = "image_' + element +'"/>')
          .appendTo("#overlay #container")
          .css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"})
          .draggable();
}

Dashboard.prototype.addSlider = function(element, name, posx, posy, w, h, min, max) {
  $('<div class = "widget slider" id = "slider_' + element +'"> <span>'+ name + '</span> </div>')
          .appendTo("#overlay #container")
        //  .css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"})
          .css({"top":posy+"px","left":posx+"px"});
  $("#overlay #container #slider_"+element + " input").css({"width":w+"px", "height":h+"px"});
      
  $('<input type="range" min="'+ min +'" max="'+ max +'" class ="widget"> </input>').appendTo("#slider_" + element)
          .change(function() {
            ws.send('{type:slider, id:'+ element +', val:'+this.value+'}');
          });
} 


Dashboard.prototype.addInput = function(element, name, posx, posy, w, h) {
  $('<div class = "widget text_input" id = "text_input_' + element +'"> <span>'+ name + '</span> </div>')
          .appendTo("#overlay #container")
        //  .css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"})
          .css({"top":posy+"px","left":posx+"px"});
  $("#overlay #container #text_input_"+element + " input").css({"width":w+"px", "height":h+"px"});
  
  $("#container #text_input_"+element + " input").on('keypress', function(e) {
    if ((e.keyCode || e.which) == 13) {
      sendAndClear();
    }
  });    

  $('<input class ="widget"> </input><button><i class = "fa fa-circle-o"></i></button>').appendTo("#text_input_" + element);

  $("#container #text_input_"+element + " button").click(function() {
    sendAndClear();
  });
  function sendAndClear() {
    var value = $("#container #text_input_"+element + " input").val();
    console.log(value);
    var obj = {type:"text_input", id:element, val:value}
    ws.send(JSON.stringify(obj));
    $("#container #text_input_"+element + " input").val("");
  }
} 


Dashboard.prototype.addButton = function(element, name, posx, posy, w, h) {
  $('<button class ="widget" id = "button_' + element +'">'+ name +' </button>')
          .appendTo("#overlay #container")
          .css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"})
          .click(function() {
            ws.send('{type:button, id:'+ element +'}');
          });
} 


Dashboard.prototype.addCameraPreview = function(element, posx, posy, w, h) {
  $('<canvas class ="widget" id = "camera_' + element +'"> </canvas>')
          .appendTo("#overlay #container")
          .css({"width": w+"px", "height":h+"px","top":posy+"px","left":posx+"px"});
} 

Dashboard.prototype.updateCamera = function() {
  var drawingCanvas = document.getElementById('camera_canvas');

    if(drawingCanvas.getContext) {
      var context = drawingCanvas.getContext('2d');
      var cam = new Image();
      cam.onload = function() {
         context.drawImage(cam, 0, 0, cam.width, cam.height);
      }
      cam.src = 'cam.jpg?pwd=' + pwd;
      setTimeout("update()", 500);
    } 
}

Dashboard.prototype.setBackgroundColor = function(r, g, b, a) { 
  $("#overlay #container").css("background", "rgba("+r+","+g+","+b+","+a+")");
}

Dashboard.prototype.addPlot = function(element, name, posx, posy, w, h, minLimit, maxLimit) {
  var _delay = 10;
  var _n = 500;
  var minVal; 
  var maxVal;
  var newPlot = true;
  var _minLimit = minLimit;
  var _maxLimit = maxLimit;

  var n = _n || 40,
      delay = _delay || 500,
      data = d3.range(n).map(function() { return 0; });
 
  var margin = {top: 10, right: 10, bottom: 20, left: 10},
      width = (w || 350) - margin.left - margin.right,
      height = (h || 250) - margin.top - margin.bottom;
 
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
  var line = d3.svg.area()
	  //.interpolate("step-before")
	  .interpolate("cardinal")
	  //.interpolate("monotone")
	      .y0(height)

      .x(function(d, i) { return x(i); })
      .y(function(d, i) { return y(d); });
 

      $("#overlay #container").append('<div class ="plot_container widget" id = "' + element +'"><h1> '+ name +' </h1><div id = "plot"> </div></div>');
      $("#"+element).draggable();

      // Setup the svg element
  var svg = d3.select("#"+element).append("svg")
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
      .attr("transform", "translate(0," + height + ")");
  //    .call(d3.svg.axis().scale(x).orient("bottom"));
 
      // Set up the y axis so it's on the left side
  svg.append("g")
      .attr("class", "y axis");
     // .call(d3.svg.axis().scale(y).orient("left"));
 
      // Finally, setup the path
  var path = svg.append("g")
  	.attr("clip-path", "url(#clip)")
    .append("path")
    .data([data])
    .attr("class", "line")
    .attr("d", line);

  //move the plot 
  //$("#plot_container h1").text(plot_name);

  console.log("#plot_container#" + element + " " + posx + " " + posy);
  $(".plot_container#" + element).css({left:posx, top:posy}); 

   
  return function(val, cb) {
 
    // push a new data point onto the back
    if (val) {

      if (newPlot) {
        minVal = val; maxVal = val;
        newPlot = false;
      }

      data.push(val);
      if (val < minVal) minVal = val;
      else maxVal = val;
  
      x.domain([0, n - 1]);
      // x.domain(d3.extent(data, function(d) { return x(d); }));
     // y.domain([d3.min(data, function(d) { return d; }), d3.max(data, function(d) { return d; })]);
      y.domain([
                _minLimit,
                _maxLimit
                ]);
  
      //y.domain([minVal, maxVal]);

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




