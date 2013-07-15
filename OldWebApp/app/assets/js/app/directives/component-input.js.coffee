app.directive "formidableInput", ->
  restrict: "E"
  require: "ngModel"
  link: (scope, element, attr, ngModel) ->
    return  if attr.type isnt "path"

    # Override the input event and add custom 'path' logic
    element.off "input"
    element.on "input", ->
      path = @value.replace(/\\/g, "/")
      scope.$apply ->
        ngModel.$setViewValue path



