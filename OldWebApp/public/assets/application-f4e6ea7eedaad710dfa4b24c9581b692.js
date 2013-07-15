(function() {
  this.app = angular.module('makeWithMotoApp', ['ui.compat', 'ngCookies']);

  this.app.value('Authentication', {});

  this.app.directive('csrfToken', function(Authentication) {
    return function(scope, element, attrs) {
      return Authentication.csrf_token = attrs.csrfToken;
    };
  });

}).call(this);
(function() {
  app.config(function($locationProvider) {
    return $locationProvider.html5Mode(false);
  });

}).call(this);
(function() {
  app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/");
    return $stateProvider.state("default", {
      abstract: true,
      views: {
        "": {
          controller: "FrameController",
          templateUrl: "/assets/layouts/default-8cd205f83c4107031c23a7e0d03fb95b.html"
        }
      }
    }).state('home', {
      parent: "default",
      url: "/",
      views: {
        '': {
          templateUrl: "/assets/home-7486e9d5889604ff74f1333f66849bc4.html",
          controller: "HomeController"
        }
      }
    }).state('about', {
      parent: "default",
      url: "/help",
      views: {
        '': {
          templateUrl: "/assets/help-876a1067fc453fba77b526bfff084665.html",
          controller: "HelpController"
        }
      }
    }).state('instruments', {
      url: '/instruments',
      views: {
        '': {
          templateUrl: "/assets/instruments-23d2cc6088a0756aedc7964e9e4ab81c.html",
          controller: "InstrumentsController"
        }
      }
    });
  });

}).call(this);
(function() {
  window.Types = {
    Object: [],
    Global: ['function', 'var'],
    'moto': ['createDigitalOutput']
  };

}).call(this);
(function() {
  app.config(function($httpProvider) {
    return $httpProvider.defaults.transformRequest.push(function(data, headersGetter) {
      var d, utf8_data;

      utf8_data = data;
      if (!angular.isUndefined(data)) {
        d = angular.fromJson(data);
        d["_utf8"] = "&#9731;";
        utf8_data = angular.toJson(d);
      }
      return utf8_data;
    });
  });

}).call(this);
(function() {
  app.controller("AboutController", function($scope) {});

}).call(this);
(function() {
  app.controller("DocsController", [
    '$scope', '$http', '$state', '$stateParams', '$timeout', function($scope, $http, $state, $stateParams, $timeout) {
      var cache, converter, searchTimeout, updateSearch;

      $scope.entries = [];
      searchTimeout = void 0;
      cache = [];
      converter = new Showdown.converter();
      $http.get('/docs.json').then(function(res) {
        var entry, name, _ref, _results;

        _ref = res.data.entries;
        _results = [];
        for (name in _ref) {
          entry = _ref[name];
          if (!cache[name]) {
            cache[name] = {
              name: name,
              desc: converter.makeHtml(entry.desc),
              usage: converter.makeHtml(entry.usage)
            };
          }
          _results.push(cache[name] || (cache[name] = entry));
        }
        return _results;
      });
      updateSearch = function() {
        var matches, rank, search;

        search = $scope.search;
        rank = function(term, entries) {
          var bestMatch, entry, keyword, keywords, name, ranking, rankings, _i, _j, _len, _len1;

          rankings = [];
          term = term.toLowerCase();
          for (name in entries) {
            entry = entries[name];
            keywords = entry.usage.split(" ");
            ranking = {
              rank: 1,
              entry: entry
            };
            if (name.toLowerCase().indexOf(term) >= 0) {
              ranking.rank += 60;
            }
            for (_i = 0, _len = keywords.length; _i < _len; _i++) {
              keyword = keywords[_i];
              if (keyword.indexOf(term) >= 0) {
                ranking.rank += 1;
              }
              rankings.push(ranking);
            }
          }
          bestMatch = {
            rank: 0
          };
          for (_j = 0, _len1 = rankings.length; _j < _len1; _j++) {
            ranking = rankings[_j];
            if (ranking.rank > bestMatch.rank) {
              bestMatch = ranking;
            }
          }
          return bestMatch.entry;
        };
        if (search) {
          matches = rank(search, cache);
          return searchTimeout = $timeout(function() {
            return $scope.bestMatch = matches;
          });
        } else {
          return $scope.bestMatch = null;
        }
      };
      return $scope.$watch('search', updateSearch);
    }
  ]);

}).call(this);
(function() {
  app.controller('FrameController', function($scope) {});

}).call(this);
(function() {
  app.controller("HelpController", function($scope) {
    return $("pre code").each(function(i, e) {
      return hljs.highlightBlock(e);
    });
  });

}).call(this);
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
(function() {
  app.directive('ace', [
    '$timeout', function($timeout) {
      return {
        restrict: 'A',
        transclude: true,
        require: '?ngModel',
        template: '<div class="translucded" ng-transclude></div><div class="ace-editor"></div>',
        link: function(scope, element, attributes, ngModel) {
          var editor, loadAce, onChange, opts, session, ta;

          loadAce = function(element, opts) {
            var action, config, editor, key, mode, session;

            config = ace.config;
            editor = ace.edit(element[0]);
            session = editor.getSession();
            session.setUseWorker(false);
            if (angular.isDefined(opts.mode)) {
              session.setMode('ace/mode/' + opts.mode);
            }
            mode = session.getMode();
            if (angular.isDefined(opts.theme)) {
              editor.setTheme('ace/theme/' + opts.theme);
            }
            session.setUseSoftTabs(opts.softTabs || true);
            session.setTabSize(opts.tabSize || 2);
            editor.setShowPrintMargin(opts.showPrintMargin || false);
            editor.renderer.setShowGutter(opts.showGutter || true);
            if (opts.keyCommands) {
              for (key in opts.keyCommands) {
                action = opts.keyCommands[key];
                editor.commands.addCommand({
                  name: 'contentAssist',
                  bindKey: {
                    win: key,
                    mac: key
                  },
                  exec: action
                });
              }
            }
            return editor;
          };
          ta = $(element).find('textarea');
          ta.hide();
          opts = scope.$eval(attributes.ace);
          editor = loadAce(element, opts);
          session = editor.getSession();
          scope.ace = editor;
          if (angular.isDefined(ngModel)) {
            ngModel.$formatters.push(function(val) {
              if (angular.isUndefined(val) || val === null) {
                "";
              }
              return val;
            });
            ngModel.$render = function() {
              return session.setValue(ngModel.$viewValue);
            };
          }
          onChange = function(cb) {
            return function(e) {
              var newVal;

              newVal = session.getValue();
              if (newVal !== scope.$eval(attributes.value) && !scope.$$phase) {
                if (angular.isDefined(ngModel)) {
                  ngModel.$setViewValue(newVal);
                }
              }
              if (angular.isDefined(cb)) {
                if (angular.isFunction(cb)) {
                  return cb(e, editor);
                } else {
                  throw new Error('Not a function');
                }
              }
            };
          };
          session.on('change', onChange(opts.onChange));
          if (angular.isDefined(ngModel)) {
            ngModel.$formatters.push(function(value) {
              if (angular.isUndefined(value) || value === null) {
                return "";
              } else {
                if (angular.isObject(value) || angular.isArray(value)) {
                  throw new Error("ui-ace cannot use an object or an array as a model");
                }
              }
              return value;
            });
            return ngModel.$render = function() {
              return session.setValue(ngModel.$viewValue);
            };
          }
        }
      };
    }
  ]);

}).call(this);
(function() {
  app.directive('circuitViz', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        circuit: '='
      },
      link: function(scope, element, attrs) {
        var height, viz, width;

        console.log("CIRCUIT");
        height = attrs.height || 300;
        width = attrs.width || 300;
        viz = d3.select(element[0]).append('svg').attr('height', height).attr('width', width);
        return viz.append('svg:line').attr('x1', 30).attr('y1', 30).attr('x2', 90).attr('y2', 120).style('stroke', 'rgb(6,120,90)');
      }
    };
  });

}).call(this);
(function() {
  app.directive("formidableInput", function() {
    return {
      restrict: "E",
      require: "ngModel",
      link: function(scope, element, attr, ngModel) {
        if (attr.type !== "path") {
          return;
        }
        element.off("input");
        return element.on("input", function() {
          var path;

          path = this.value.replace(/\\/g, "/");
          return scope.$apply(function() {
            return ngModel.$setViewValue(path);
          });
        });
      }
    };
  });

}).call(this);
(function() {
  app.directive('componentListing', function() {
    return {
      restrict: 'A',
      replace: true,
      terminal: true,
      transclude: true,
      scope: {
        obj: '='
      },
      templateUrl: "templates/components/_listing.html",
      compile: function(scope, element, attrs) {
        return console.log("hi", getTemplate);
      }
    };
  });

}).call(this);
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
(function() {
  app.directive('gFlickable', function() {
    return function(scope, element, attrs) {
      var el, selectedEl, tapping;

      el = $(element[0]);
      scope.selectedIndex = 0;
      selectedEl = function() {
        return $(el.children()[scope.selectedIndex]);
      };
      tapping = false;
      return el.flickable({
        segmentPx: 245,
        onStart: function() {
          return tapping = true;
        },
        onMove: function() {
          return tapping = false;
        },
        onEnd: function() {
          if (tapping) {
            return selectedEl().toggleClass('showbuttons');
          }
        },
        onScroll: function(eventData, newSelectedIndex) {
          selectedEl().removeClass('selected showbuttons');
          scope.selectedIndex = newSelectedIndex;
          selectedEl().addClass('selected showbuttons');
          return scope.$apply();
        }
      });
    };
  });

}).call(this);
(function() {
  app.directive('gTap', function() {
    return function(scope, element, attrs) {
      element.bind('touchstart', function() {
        var tapping;

        return tapping = true;
      });
      element.bind('touchmovie', function() {
        var tapping;

        return tapping = false;
      });
      element.bind('touchend', function() {
        if (tapping) {
          return scope.$apply(attrs['gTap']);
        }
      });
      return element.bind('click', function() {
        return scope.$apply(attrs['gTap']);
      });
    };
  });

}).call(this);
(function() {
  app.directive('component-row', function() {
    return {
      restrict: 'E',
      require: '^ngModel',
      scope: {
        id: '=',
        label: '=',
        placeholder: '=',
        type: '='
      },
      priority: 100,
      templateUrl: '/templates/shared/form_input.html.erb',
      compile: function(element, attrs, transclude) {
        if (attrs.ngModel) {
          return console.log("MDOEL");
        }
      }
    };
  });

}).call(this);
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
(function() {
  app.filter("newlines", function() {
    return function(text) {
      return text.replace(/\n/g, "<br/>");
    };
  }).filter("noHTML", function() {
    return function(text) {
      return text.replace(/&/g, "&amp;").replace(/>/g, "&gt;").replace(/</g, "&lt;");
    };
  });

}).call(this);
(function() {
  app.factory("parserService", [
    "$q", '$rootScope', "$location", function($q, $rootScope, $location) {
      var contentAssist, doParse, _parser;

      _parser = esprima;
      doParse = function(code) {
        var error;

        try {
          return _parser.parse(code, {
            tolerant: true,
            loc: true,
            range: true
          });
        } catch (_error) {
          error = _error;
          console.error(error);
          return {
            errors: [error]
          };
        }
      };
      contentAssist = function(token) {
        if (token) {
          switch (token.type) {
            case 'punctuation.operator':
              return true;
            case 'identifier' || 'text':
              return true;
            case 'storage.type':
              return true;
          }
        }
      };
      return {
        parse: doParse,
        contentAssist: contentAssist
      };
    }
  ]);

}).call(this);
(function() {
  app.factory("webSocketService", [
    "$q", '$rootScope', "$location", function($q, $rootScope, $location) {
      var callbacks, currentCallbackId, getCallbackId, listener, pingWs, sendRequest, subscribers, ws;

      callbacks = {};
      currentCallbackId = 0;
      subscribers = {};
      ws = void 0;
      pingWs = function() {
        if (ws === void 0 || ws.readyState > 1) {
          ws = new WebSocket("ws://" + $location.host() + ":8081" + "/api");
          ws.onopen = function() {
            var cb, _i, _len, _ref, _results;

            console.log("opened");
            if (subscribers['on_connected']) {
              _ref = subscribers['on_connected'];
              _results = [];
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                cb = _ref[_i];
                _results.push(cb());
              }
              return _results;
            }
          };
          ws.onclose = function() {
            var cb, _i, _len, _ref, _results;

            console.log("closed");
            if (subscribers['on_disconnected']) {
              _ref = subscribers['on_disconnected'];
              _results = [];
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                cb = _ref[_i];
                _results.push(cb());
              }
              return _results;
            }
          };
          ws.onmessage = function(message) {
            return listener(message);
          };
          return ws;
        }
      };
      ws = pingWs();
      setInterval(pingWs, 3000);
      sendRequest = function(request, streaming_callback) {
        var callbackId, defer, sendReq;

        defer = $q.defer();
        callbackId = getCallbackId();
        callbacks[callbackId] = {
          time: new Date(),
          streaming_callback: streaming_callback,
          cb: defer
        };
        request.callback_id = callbackId;
        sendReq = function() {
          return ws.send(JSON.stringify(request));
        };
        setTimeout(sendReq, 500);
        return defer.promise;
      };
      listener = function(data) {
        var cb, e, messageObj, type, _i, _len, _ref;

        messageObj = (function() {
          try {
            return JSON.parse(data.data);
          } catch (_error) {
            e = _error;
            return data.data;
          }
        })();
        type = messageObj['type'];
        if (subscribers[type]) {
          _ref = subscribers[type];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            cb = _ref[_i];
            cb(messageObj);
          }
        }
        if (callbacks.hasOwnProperty(messageObj.callback_id)) {
          if (callbacks[messageObj.callback_id].streaming_callback) {
            return $rootScope.$apply(callbacks[messageObj.callback_id].streaming_callback(messageObj));
          } else {
            $rootScope.$apply(callbacks[messageObj.callback_id].cb.resolve(messageObj));
            return delete callbacks[messageObj.callback_id];
          }
        }
      };
      getCallbackId = function() {
        currentCallbackId += 1;
        if (currentCallbackId > 15000) {
          currentCallbackId = 0;
        }
        return currentCallbackId;
      };
      return {
        onConnected: function(f) {
          return this.subscribe('on_connected', f);
        },
        onDisconnected: function(f) {
          return this.subscribe('on_disconnected', f);
        },
        ping: function() {
          return pingWs();
        },
        getNewCode: function() {
          var request;

          request = {
            type: "get_new_code"
          };
          return sendRequest(request);
        },
        createNewProject: function(name) {
          return sendRequest({
            type: 'create_new_project',
            name: name
          });
        },
        getCodeFor: function(project) {
          return sendRequest({
            type: 'get_code',
            name: project.name
          });
        },
        saveFile: function(project) {
          var request;

          request = {
            type: 'save_file',
            name: project.name,
            code: project.code
          };
          return sendRequest(request);
        },
        runProject: function(obj) {
          return sendRequest({
            type: 'run_project',
            name: obj.name,
            code: obj.code
          });
        },
        subscribe: function(event, cb) {
          if (!subscribers[event]) {
            subscribers[event] = [];
          }
          console.log("subscribers", event);
          return subscribers[event].push(cb);
        },
        getProjects: function() {
          return sendRequest({
            type: 'get_projects'
          });
        }
      };
    }
  ]);

}).call(this);
(function() {
  Zepto(function($) {
    hljs.tabReplace = '  ';
    hljs.initHighlightingOnLoad();
    return $(document).foundation();
  });

}).call(this);
