var require = {
  paths: {
    'app': 'src/app',
    'jquery': '//ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min',

    // EXTERNAL LIBRARIES
    'underscore': 'src/vendors/underscore/underscore',
    'backbone': 'src/vendors/backbone/backbone-min',

    // require.js plugins
    "text": "src/vendors/require/text",
    "domReady": "src/vendors/require/domReady",

    // jQuery plugins
    "jquery.mousewheel": "src/vendors/jquery-plugins/jquery.mousewheel",
    "jquery.filedrop": "src/vendors/jquery-plugins/jquery.filedrop",
    "w2ui": "src/vendors/w2ui-1.2/w2ui-1.2.min",

    // D3
    "d3": "src/vendors/d3.v3/d3.v3.min",

    // CONTROLLERS
    "CommunicationController": "src/controllers/communicationController",

    // VIEWS
    "UiView": "src/views/uiView"

  },
  shim: {
    underscore: {
      exports: '_'
    },
    backbone: {
      deps: [
        'underscore',
        'jquery'
      ],
      exports: 'Backbone'
    }
  }
};