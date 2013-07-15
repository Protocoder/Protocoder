(function() {
  app.directive('componentListing', function() {
    return {
      restrict: 'A',
      replace: true,
      terminal: true,
      transclude: true,
      scope: {
        obj: '='
      },
      templateUrl: "templates/components/_listing.html",
      compile: function(scope, element, attrs) {
        return console.log("hi", getTemplate);
      }
    };
  });

}).call(this);
