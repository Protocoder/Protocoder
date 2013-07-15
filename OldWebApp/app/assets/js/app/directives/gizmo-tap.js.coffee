app.directive 'gTap', ->
  (scope, element, attrs) ->
    element.bind 'touchstart', -> tapping = true
    element.bind 'touchmovie', -> tapping = false
    element.bind 'touchend', -> scope.$apply(attrs['gTap']) if tapping
    ## Support desktop
    element.bind 'click', -> scope.$apply(attrs['gTap'])
