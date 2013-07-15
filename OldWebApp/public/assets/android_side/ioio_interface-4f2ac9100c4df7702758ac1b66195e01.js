(function() {
  var _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  (typeof exports !== "undefined" && exports !== null ? exports : this).IOIOInterface = (function(_super) {
    __extends(IOIOInterface, _super);

    function IOIOInterface() {
      _ref = IOIOInterface.__super__.constructor.apply(this, arguments);
      return _ref;
    }

    IOIOInterface.prototype.createDigitalOutput = function(pin, name, callback) {
      var cb, givenCallback, self;

      self = this;
      cb = function(publicName, privateName) {
        self.global[publicName] = {
          write: function(state) {
            return self.proxy.writeDigitalOutput(privateName, state);
          }
        };
        if (callback) {
          return self.global[callback].apply(self, this);
        }
      };
      givenCallback = this._generateCallback(cb);
      return this.proxy.createDigitalOutput(pin, name, givenCallback);
    };

    IOIOInterface.prototype.createDigitalInput = function(pin, name, callback) {
      var cb, self;

      self = this;
      cb = function(publicName, privateName) {
        self.global[publicName] = {
          read: function(into) {
            return self.proxy.readDigitalInput(privateName, self._generateCallback(into));
          },
          when: function(v, into) {
            return self.proxy.blockReadDigitalInput(privateName, v, self._generateCallback(into));
          },
          whenever: function(v, into) {
            return self.proxy.loopBlockReadDigitalInput(privateName, self._generateCallback(into));
          }
        };
        if (callback) {
          return self.global[callback].apply(self, this);
        }
      };
      return this.proxy.createDigitalInput(pin, name, this._generateCallback(cb));
    };

    IOIOInterface.prototype.createAnalogInput = function(pin, name, callback) {
      var cb, self;

      self = this;
      cb = function(publicName, privateName) {
        self.global[publicName] = {
          read: function(into) {
            return self.proxy.readAnalogInput(privateName, self._generateCallback(into));
          }
        };
        if (callback) {
          return self.global[callback].apply(self, this);
        }
      };
      return this.proxy.createAnalogInput(pin, name, this._generateCallback(cb));
    };

    IOIOInterface.prototype.createPulseInput = function(pin, name, callback) {
      var cb, self;

      self = this;
      cb = function(publicName, privateName) {
        self.global[publicName] = {
          getDuration: function(into) {
            return self.proxy.getPulseInputDuration(privateName, self._generateCallback(into));
          },
          getFrequency: function(into) {
            return self.proxy.getPulseFrequency(privateName, self._generateCallback(into));
          }
        };
        if (callback) {
          return self.global[callback].apply(self, this);
        }
      };
      return this.proxy.createPwmOutput(pin, name, this._generateCallback(cb));
    };

    IOIOInterface.prototype.createPwmOutput = function(pin, freq, name, callback) {
      var cb, self;

      self = this;
      cb = function(publicName, privateName) {
        self.global[publicName] = {
          setDutyCycle: function(num) {
            return self.proxy.setDutyCycle(privateName, num);
          },
          setPulseWidth: function(num) {
            return self.proxy.setPulseWidth(privateName, num);
          }
        };
        if (callback) {
          return self.global[callback].apply(self, this);
        }
      };
      return this.proxy.createPwmOutput(pin, freq, name, this._generateCallback(cb));
    };

    return IOIOInterface;

  })(BaseInterface);

  IOIOInterface.instance(window, "ioio", "_ioio", true);

}).call(this);
