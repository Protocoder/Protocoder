(function() {
  app.directive('gFlickable', function() {
    return function(scope, element, attrs) {
      var el, selectedEl, tapping;

      el = $(element[0]);
      scope.selectedIndex = 0;
      selectedEl = function() {
        return $(el.children()[scope.selectedIndex]);
      };
      tapping = false;
      return el.flickable({
        segmentPx: 245,
        onStart: function() {
          return tapping = true;
        },
        onMove: function() {
          return tapping = false;
        },
        onEnd: function() {
          if (tapping) {
            return selectedEl().toggleClass('showbuttons');
          }
        },
        onScroll: function(eventData, newSelectedIndex) {
          selectedEl().removeClass('selected showbuttons');
          scope.selectedIndex = newSelectedIndex;
          selectedEl().addClass('selected showbuttons');
          return scope.$apply();
        }
      });
    };
  });

}).call(this);
