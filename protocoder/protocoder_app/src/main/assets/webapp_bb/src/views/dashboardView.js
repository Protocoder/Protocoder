define([
  'underscore',
  'backbone',
  'text!src/tpl/dashboard.html'
], function (_, Backbone, dashboardHtml) {

  'use strict';

  var DashboardView = Backbone.View.extend({

    /**
     * Init the view.
     */
    initialize: function () {
      this.widgets = new Array();
      this.status = false;

      // Cache selectors
      this.$container = $("body > .container");
      this.$overlayContainer = $("#overlay > .container");
      this.$toolbar = $("body > #toolbar");
      this.$overlayToggle = $("#overlay #toggle");
    },

    /**
     * Render the view (add dom elements or html to the page).
     */
    render: function () {
      // prepare html or DOM elements + inject them to the page here
    },

    /**
     * Hide.
     */
    hide: function () {
      this.$container.removeClass("off");
      this.$overlayContainer.removeClass("on");
      this.$toolbar.removeClass("off");
      this.$overlayToggle.removeClass("on");
      this.status = false;
    },

    /**
     * Show.
     */
    show: function () {
      this.$overlayContainer.addClass("on");
      this.$container.addClass("off");
      this.$toolbar.addClass("off");
      this.$overlayToggle.addClass("on");
      this.status = true;
    },

    /**
     * Toggle.
     */
    toggle: function() {
    if (this.status == false) {
      this.show();
    } else {
      this.hide();
    }
  }

  });

  return DashboardView;

});