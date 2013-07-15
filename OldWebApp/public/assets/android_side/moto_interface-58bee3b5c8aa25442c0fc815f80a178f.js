(function() {
  window['moto'] = {
    setup: function() {
      if (typeof window['setup'] === "function") {
        return window.setup();
      }
    },
    loop: function() {
      var resp;

      if (typeof window.loop === "function") {
        return resp = window.loop();
      }
    }
  };

}).call(this);
