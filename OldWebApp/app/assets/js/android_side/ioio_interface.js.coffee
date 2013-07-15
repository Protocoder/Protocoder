class (exports ? this).IOIOInterface extends BaseInterface
    
  ## Digital output        
  createDigitalOutput: (pin, name, callback) -> 
    self = @
    cb = (publicName, privateName) ->
      self.global[publicName] = {
        write: (state) -> self.proxy.writeDigitalOutput(privateName, state)
      }
      self.global[callback].apply(self, @) if callback
    
    givenCallback = @_generateCallback(cb)
    @proxy.createDigitalOutput(pin, name, givenCallback)
  
  ## Digital input
  createDigitalInput: (pin, name, callback) ->
    self = @
    cb = (publicName, privateName) ->
      self.global[publicName] = {
        read: (into) -> self.proxy.readDigitalInput(privateName, self._generateCallback(into)),
        when: (v, into) -> self.proxy.blockReadDigitalInput(privateName, v, self._generateCallback(into)),
        whenever: (v, into) -> self.proxy.loopBlockReadDigitalInput(privateName, self._generateCallback(into))
      }
      self.global[callback].apply(self, @) if callback
      
    @proxy.createDigitalInput(pin, name, @_generateCallback(cb))
  
  ## Analog input
  createAnalogInput: (pin, name, callback) ->
    self = @
    cb = (publicName, privateName) ->
      self.global[publicName] = {
        read: (into) -> self.proxy.readAnalogInput(privateName, self._generateCallback(into))
      }
      self.global[callback].apply(self, @) if callback
    @proxy.createAnalogInput(pin, name, @_generateCallback(cb))
    
  ## Pulse input
  createPulseInput: (pin, name, callback) ->
    self = @
    cb = (publicName, privateName) ->
      self.global[publicName] = {
        getDuration: (into) -> self.proxy.getPulseInputDuration(privateName, self._generateCallback(into)),
        getFrequency: (into) -> self.proxy.getPulseFrequency(privateName, self._generateCallback(into))
      }
      self.global[callback].apply(self, @) if callback
    @proxy.createPwmOutput(pin, name, @_generateCallback(cb))
  
  ## Pulse output
  createPwmOutput: (pin, freq, name, callback) ->
    self = @
    cb = (publicName, privateName) ->
      self.global[publicName] = {
        setDutyCycle: (num) -> self.proxy.setDutyCycle(privateName, num),
        setPulseWidth: (num) -> self.proxy.setPulseWidth(privateName, num)
      }
      self.global[callback].apply(self, @) if callback
    @proxy.createPwmOutput(pin, freq, name, @_generateCallback(cb))
    

IOIOInterface.instance(window, "ioio", "_ioio", true)