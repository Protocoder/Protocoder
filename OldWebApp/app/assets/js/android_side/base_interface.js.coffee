class (exports ? this).BaseInterface
  
  constructor: (@global, @globalName, interfaceName, @usePromises) ->
    @proxy = @global[interfaceName]
    @currIdCount = 1
    @callbacks = {}
    @registeredMethods = []
    @promises = []
    @readyState = !@usePromises
  
  ## Register a method
  _registerMethod: (name, callback) ->
    return if name == "constructor" || name[0] == "_"
    self = @
    @registeredMethods.push name  
    @global[@globalName][name] = ->
      args = Array::slice.call(arguments) || ["_____undefinedCallback"]
      givenCallback = undefined
      if args.length > 0 and typeof (args[args.length - 1]) is "function"
        givenCallback = self._generateCallback(args.pop())
        args.push givenCallback
      f = ->
        if callback
          callback.apply self, args
      if self.usePromises || self.readyState
        f.apply this, args
      else
        deferred = Q.defer()
        self.promises.push deferred.promise

  ## Register all methods in a hash
  # of the form
  # { name: function() {}, other_name: function() {} }
  _registerMethods: (obj) ->
    for m of obj
      @_registerMethod m, obj[m]
  
  ## Generate a callback with a unique id
  _generateCallback: (fn) ->
    id = @_generatedUniqueId()
    self = @
    @global[id] = ->
      fn.apply self, arguments
    id
  
  ## Generate a unique id
  _generatedUniqueId: ->
    @currIdCount = 1  if @currIdCount >= 2048
    @globalName + "-callback-" + (@currIdCount++) + "-" + Date.now()
  
  # ## Create a proxy to wrap methods in
  # _proxy: (context) ->
  #   args = Array::slice.call(arguments)
  #   fun = args.shift()
  #   ->
  #     fun.apply context, args
      
  ## Clean properties, accounting for different types
  _clean: (opts) ->
    for prop of opts
      opts[prop] = @_generateCallback(opts[prop]) if typeof (opts[prop]) is "function"
    JSON.stringify opts
    
  ## Set ready state
  _setReady: () -> 
    @readyState = true
    if @usePromises
      console.log("READY TO GO!!!!!!")
      for promise of @promises
        promise.resolve(@)
    
  ## singleton instance
  @instances = {}
  @instance: (global, name, interfaceName, usePromises) ->
    if @instances[name] == undefined
      inst = new @(global, name, interfaceName, usePromises)
      @instances[name] = global[name] = inst
      ## Register all instance methods on the object
      (inst._registerMethod(k, v) for k, v of inst when typeof v is "function")
    
    global[name]