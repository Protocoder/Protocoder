(function() {
  app.directive("formidableInput", function() {
    return {
      restrict: "E",
      require: "ngModel",
      link: function(scope, element, attr, ngModel) {
        if (attr.type !== "path") {
          return;
        }
        element.off("input");
        return element.on("input", function() {
          var path;

          path = this.value.replace(/\\/g, "/");
          return scope.$apply(function() {
            return ngModel.$setViewValue(path);
          });
        });
      }
    };
  });

}).call(this);
