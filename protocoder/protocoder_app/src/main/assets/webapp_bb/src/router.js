define([
  'underscore',
  'backbone',

  // VIEWS
  'src/views/uiView'
], function(_, Backbone, UiView) {

  "use strict";

  // Settings.
  var defaultPage = "home";
  var validPages = ["home", "files", "etc"];

  /**
   * Setup the router.
   * @type {*|void|Object}
   */
  var Router = Backbone.Router.extend({

    /**
     * Valid routes and their callbacks.
     */
    routes: {
      "": "page",
      ":title(/:params)": "page",
      ":title(/)": "page"
    },

    /**
     * Initialize the router: runs once.
     */
    initialize: function() {
      console.log("Welcome to Protocoder router.");

      // Start main views
      Protocoder.uiView = new UiView();
      Protocoder.uiView.init().render();

    },

    /**
     * Select and run the right page when the route changes.
     * @param title The first parameter after the hash (ex: protocoder.org/#this).
     * @param params The second parameter (ex: protocoder.org/#param1/this).
     */
    page: function(title, params) {
      var page = title ? title : defaultPage;

      console.log("Page: " + page);


    }

  });

  return Router;

});