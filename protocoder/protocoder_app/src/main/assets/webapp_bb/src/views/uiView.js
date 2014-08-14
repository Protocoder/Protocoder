define([
  'underscore',
  'backbone',
  'text!src/tpl/ui.html'
], function (_, Backbone, uiHtml) {

  'use strict';

  var UiView = Backbone.View.extend({

    events: {
      "click .ui": "clicked"
    },

    /**
     * Init.
     * @returns {Ui}
     */
    initialize: function () {

      console.log("Welcome to the Ui view!");

      return this;
    },

    /**
     * Render the view (add dom elements or html to the page).
     * @returns {Ui}
     */
    render: function () {

      // Prepare html
      var tpl = _.template(uiHtml);
      var tplHtml = tpl({a: 1000});

      // Inject html (at once for performance)
      $('#example_for_victor').html(tplHtml);

      return this;
    },

    /**
     * Example callback function for view events.
     */
    clicked: function() {
      console.log("You clicked on the Ui element.");
    }
  });

  return UiView;

});