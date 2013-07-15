app.factory "webSocketService", ["$q", '$rootScope', "$location", ($q, $rootScope, $location) ->
  # We return this object to anything injecting our service
  # Keep all pending requests here until they get responses
  # Create a unique callback ID to map requests to responses
  # Create our websocket object with the address to the websocket
  callbacks = {}
  currentCallbackId = 0
  subscribers = {};
  ws = undefined

  pingWs = () ->
    if (ws == undefined || ws.readyState > 1)
      ws = new WebSocket("ws://" + $location.host() + ":8081" + "/api")
      ws.onopen = ->
        console.log("opened")
        if subscribers['on_connected']
          cb() for cb in subscribers['on_connected']
  
      ws.onclose = ->
        console.log("closed")
        if subscribers['on_disconnected']
          cb() for cb in subscribers['on_disconnected']

      ws.onmessage = (message) ->
        listener message
        
      ws
  
  ## INCREDIBLY SIMPLE RECONNECT
  ws = pingWs()
  setInterval(pingWs, 3000);

  sendRequest = (request, streaming_callback) ->
    defer = $q.defer()
    callbackId = getCallbackId()
    callbacks[callbackId] =
      time: new Date()
      streaming_callback: streaming_callback
      cb: defer

    request.callback_id = callbackId
    sendReq = () ->
      ws.send JSON.stringify(request)
    setTimeout sendReq, 500
    defer.promise

  listener = (data) ->
    messageObj = try JSON.parse data.data catch e then data.data
    type = messageObj['type']
    if subscribers[type]
      cb(messageObj) for cb in subscribers[type]

    # If an object exists with callback_id in our callbacks object, resolve it
    if callbacks.hasOwnProperty(messageObj.callback_id)
      if callbacks[messageObj.callback_id].streaming_callback
        $rootScope.$apply callbacks[messageObj.callback_id].streaming_callback(messageObj)
      else
        $rootScope.$apply callbacks[messageObj.callback_id].cb.resolve(messageObj)
        delete callbacks[messageObj.callback_id]
  
  # This creates a new callback ID for a request
  getCallbackId = ->
    currentCallbackId += 1
    currentCallbackId = 0 if currentCallbackId > 15000
    currentCallbackId

  {
    onConnected: (f) -> this.subscribe('on_connected', f)
    onDisconnected: (f) -> this.subscribe('on_disconnected', f)
    ping: () -> pingWs();
    getNewCode: ()->
      request = type: "get_new_code"
      sendRequest(request)

    createNewProject: (name) ->
      sendRequest(type: 'create_new_project', name: name)

    getCodeFor: (project) ->
      sendRequest({type: 'get_code', name: project.name})

    saveFile: (project) ->
      request = type: 'save_file', name: project.name, code: project.code
      sendRequest(request)

    runProject: (obj) ->
      sendRequest({type: 'run_project', name: obj.name, code: obj.code})

    subscribe: (event, cb) ->
      if !subscribers[event] 
        subscribers[event] = []
      console.log("subscribers", event)
      subscribers[event].push cb

    getProjects: () ->
      sendRequest({type: 'get_projects'})
  }]
