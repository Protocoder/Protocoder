window['moto'] =
  
  setup: ->
    if typeof (window['setup']) is "function"
      window.setup()
      _moto.ok("")

  loop: ->
    if typeof (window.loop) is "function"
      resp = window.loop()
      if resp is `undefined` or resp is true
        _moto.ok("")
      else
        _moto.finish("")
    else
      _moto.finish("")
