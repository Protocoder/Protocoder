(function() {
  var _ref,
    __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  (typeof exports !== "undefined" && exports !== null ? exports : this).UiInterface = (function(_super) {
    __extends(UiInterface, _super);

    function UiInterface() {
      _ref = UiInterface.__super__.constructor.apply(this, arguments);
      return _ref;
    }

    UiInterface.prototype.addButton = function(text, callback) {
      return this.proxy.addButton(text, callback);
    };

    UiInterface.prototype.addPlot = function(callback) {
      return this.proxy.addPlot(callback);
    };

    UiInterface.prototype.addPlotValue = function(value) {
      return this.proxy.addPlotValue(value);
    };

    return UiInterface;

  })(BaseInterface);

  UiInterface.instance(window, "ui", "_ui", false);

}).call(this);
