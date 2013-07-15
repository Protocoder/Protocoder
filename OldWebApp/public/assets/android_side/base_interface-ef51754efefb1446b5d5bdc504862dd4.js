(function() {
  (typeof exports !== "undefined" && exports !== null ? exports : this).BaseInterface = (function() {
    function BaseInterface(global, globalName, interfaceName, usePromises) {
      this.global = global;
      this.globalName = globalName;
      this.usePromises = usePromises;
      this.proxy = this.global[interfaceName];
      this.currIdCount = 1;
      this.callbacks = {};
      this.registeredMethods = [];
      this.promises = [];
      this.readyState = !this.usePromises;
    }

    BaseInterface.prototype._registerMethod = function(name, callback) {
      var self;

      if (name === "constructor" || name[0] === "_") {
        return;
      }
      self = this;
      this.registeredMethods.push(name);
      return this.global[this.globalName][name] = function() {
        var args, deferred, f, givenCallback;

        args = Array.prototype.slice.call(arguments);
        givenCallback = void 0;
        if (typeof args[args.length - 1] === "function") {
          givenCallback = self._generateCallback(args.pop());
          args.push(givenCallback);
        }
        f = function() {
          if (callback) {
            return callback.apply(self, args);
          }
        };
        if (self.usePromises || self.readyState) {
          return f.apply(this, args);
        } else {
          deferred = Q.defer();
          return self.promises.push(deferred.promise);
        }
      };
    };

    BaseInterface.prototype._registerMethods = function(obj) {
      var m, _results;

      _results = [];
      for (m in obj) {
        _results.push(this._registerMethod(m, obj[m]));
      }
      return _results;
    };

    BaseInterface.prototype._generateCallback = function(fn) {
      var id, self;

      id = this._generatedUniqueId();
      self = this;
      this.global[id] = function() {
        return fn.apply(self, arguments);
      };
      return id;
    };

    BaseInterface.prototype._generatedUniqueId = function() {
      if (this.currIdCount >= 2048) {
        this.currIdCount = 1;
      }
      return this.globalName + "-callback-" + (this.currIdCount++) + "-" + Date.now();
    };

    BaseInterface.prototype._clean = function(opts) {
      var prop;

      for (prop in opts) {
        if (typeof opts[prop] === "function") {
          opts[prop] = this._generateCallback(opts[prop]);
        }
      }
      return JSON.stringify(opts);
    };

    BaseInterface.prototype._setReady = function() {
      var promise, _results;

      this.readyState = true;
      if (this.usePromises) {
        console.log("READY TO GO!!!!!!");
        _results = [];
        for (promise in this.promises) {
          _results.push(promise.resolve(this));
        }
        return _results;
      }
    };

    BaseInterface.instances = {};

    BaseInterface.instance = function(global, name, interfaceName, usePromises) {
      var inst, k, v;

      if (this.instances[name] === void 0) {
        inst = new this(global, name, interfaceName, usePromises);
        this.instances[name] = global[name] = inst;
        for (k in inst) {
          v = inst[k];
          if (typeof v === "function") {
            inst._registerMethod(k, v);
          }
        }
      }
      return global[name];
    };

    return BaseInterface;

  })();

}).call(this);
