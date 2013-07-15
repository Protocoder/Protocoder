(function() {
  var _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  (typeof exports !== "undefined" && exports !== null ? exports : this).AndroidInterface = (function(_super) {
    __extends(AndroidInterface, _super);

    function AndroidInterface() {
      _ref = AndroidInterface.__super__.constructor.apply(this, arguments);
      return _ref;
    }

    AndroidInterface.prototype.vibrate = function(duration) {
      return this.proxy.vibrate(duration);
    };

    return AndroidInterface;

  })(BaseInterface);

  AndroidInterface.instance(window, "android", "_android", false);

}).call(this);
