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
