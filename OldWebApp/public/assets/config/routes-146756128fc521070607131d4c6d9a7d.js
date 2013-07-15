(function() {
  app.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/");
    return $stateProvider.state("default", {
      abstract: true,
      views: {
        "": {
          controller: "FrameController",
          templateUrl: "/assets/layouts/default-8cd205f83c4107031c23a7e0d03fb95b.html"
        }
      }
    }).state('home', {
      parent: "default",
      url: "/",
      views: {
        '': {
          templateUrl: "/assets/home-7486e9d5889604ff74f1333f66849bc4.html",
          controller: "HomeController"
        }
      }
    }).state('about', {
      parent: "default",
      url: "/help",
      views: {
        '': {
          templateUrl: "/assets/help-876a1067fc453fba77b526bfff084665.html",
          controller: "HelpController"
        }
      }
    }).state('instruments', {
      url: '/instruments',
      views: {
        '': {
          templateUrl: "/assets/instruments-23d2cc6088a0756aedc7964e9e4ab81c.html",
          controller: "InstrumentsController"
        }
      }
    });
  });

}).call(this);
