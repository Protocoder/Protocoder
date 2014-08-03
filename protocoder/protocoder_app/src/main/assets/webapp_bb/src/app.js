define([
  'underscore',
  'backbone',
  // ROUTER
  'src/router'
], function(_, Backbone, Router) {

  "use strict";

  Protocoder.router = new Router();
  Backbone.history.start();

  console.log('start protocoderrr');

});