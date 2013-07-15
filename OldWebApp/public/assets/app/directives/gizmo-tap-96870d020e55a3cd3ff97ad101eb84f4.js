(function() {
  app.directive('gTap', function() {
    return function(scope, element, attrs) {
      element.bind('touchstart', function() {
        var tapping;

        return tapping = true;
      });
      element.bind('touchmovie', function() {
        var tapping;

        return tapping = false;
      });
      element.bind('touchend', function() {
        if (tapping) {
          return scope.$apply(attrs['gTap']);
        }
      });
      return element.bind('click', function() {
        return scope.$apply(attrs['gTap']);
      });
    };
  });

}).call(this);
