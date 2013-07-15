class (exports ? this).WebSocket
  
  constructor: (manager, url, path, timeout = 300) ->
    @manager = manager
    @url = "http://" + url
    @path = path
  
class (exports ? this).WebSocketManager
  
  constructor: () ->
    @ws = new WebSocket(@, window.location.hostname + ":8080", "/api")
    @ws.onerror = @onError
    @ws.onmessage = @onMessage
    @ws.onclose = @onClose
    @callbacks = []
    
  onError: (err) ->
    console.log("on error")
  onMessage: (msg) ->
    console.log("Got message: ", msg)
    for c in @callbacks
      c(msg)
  onClose: () ->
    console.log("on close")
    
  subscribe: (cb) ->
    if typeof(cb) is "function"
      alert("registering " + cb)
      @callbacks.push(cb)

# window['WebSocket'] = WebSocket
# window['socketManager'] = new WebSocketManager()