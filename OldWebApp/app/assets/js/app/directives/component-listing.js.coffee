app.directive 'componentListing', ->
  restrict: 'A'
  replace: true
  terminal: true
  transclude: true
  scope:
    obj: '='
  templateUrl: "templates/components/_listing.html"
  compile: (scope, element, attrs) ->
    console.log "hi", getTemplate
