app.directive 'gFlickable', ->
  (scope, element, attrs) ->
    el = $(element[0]) # get a Zepto element

    # initial state - the first is selected
    scope.selectedIndex = 0
    selectedEl = -> $(el.children()[scope.selectedIndex])

    tapping = false
    el.flickable
      segmentPx: 245

      # Once again, we detect a tap event and show/hide the buttons
      onStart: -> tapping = true
      onMove: -> tapping = false
      onEnd: -> selectedEl().toggleClass('showbuttons') if tapping

      # If we're moving to a new element, update everything
      onScroll: (eventData, newSelectedIndex) ->
        # Hide the buttons on the old one
        selectedEl().removeClass('selected showbuttons')

        # Show the buttons on the new one
        scope.selectedIndex = newSelectedIndex
        selectedEl().addClass('selected showbuttons')

        # Tell the rest of our app
        #scope.didFlick()
        scope.$apply()
