(function() {
  app.directive('component-row', function() {
    return {
      restrict: 'E',
      require: '^ngModel',
      scope: {
        id: '=',
        label: '=',
        placeholder: '=',
        type: '='
      },
      priority: 100,
      templateUrl: '/templates/shared/form_input.html.erb',
      compile: function(element, attrs, transclude) {
        if (attrs.ngModel) {
          return console.log("MDOEL");
        }
      }
    };
  });

}).call(this);
