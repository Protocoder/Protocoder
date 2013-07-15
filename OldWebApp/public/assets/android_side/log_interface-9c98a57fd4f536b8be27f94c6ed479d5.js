(function() {
  var _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  (typeof exports !== "undefined" && exports !== null ? exports : this).LogInterface = (function(_super) {
    __extends(LogInterface, _super);

    function LogInterface() {
      _ref = LogInterface.__super__.constructor.apply(this, arguments);
      return _ref;
    }

    LogInterface.prototype.log = function(msg) {
      return this.proxy.log(msg);
    };

    return LogInterface;

  })(BaseInterface);

  LogInterface.instance(window, "console", "_console", false);

}).call(this);
