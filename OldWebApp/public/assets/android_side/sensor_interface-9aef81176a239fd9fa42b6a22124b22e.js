(function() {
  var _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  (typeof exports !== "undefined" && exports !== null ? exports : this).SensorInterface = (function(_super) {
    __extends(SensorInterface, _super);

    function SensorInterface() {
      _ref = SensorInterface.__super__.constructor.apply(this, arguments);
      return _ref;
    }

    SensorInterface.prototype.startAccelerometer = function(cb) {
      return this.proxy.startAccelerometer(cb);
    };

    return SensorInterface;

  })(BaseInterface);

  SensorInterface.instance(window, "sensors", "_sensors", false);

}).call(this);
