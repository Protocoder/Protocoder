(function() {
  this.app = angular.module('makeWithMotoApp', ['ui.compat', 'ngCookies']);

  this.app.value('Authentication', {});

  this.app.directive('csrfToken', function(Authentication) {
    return function(scope, element, attrs) {
      return Authentication.csrf_token = attrs.csrfToken;
    };
  });

}).call(this);
