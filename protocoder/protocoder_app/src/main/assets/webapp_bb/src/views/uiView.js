define([
  'underscore',
  'backbone',
  'text!src/tpl/ui.html'
], function (_, Backbone, uiHtml) {

  'use strict';

  var Ui = Backbone.View.extend({

    events: {
      "click .ui": "clicked"
    },

    init: function () {

      console.log("Welcome to the Ui view!");

      return this;
    },
    render: function () {

      var tpl = _.template(uiHtml);

      console.log(tpl({a: 1000}));

      return this;
    },

    /**
     * Example callback function for view events.
     */
    clicked: function() {
      console.log("You clicked on the Ui element.");
    }
  });

  return Ui;

});