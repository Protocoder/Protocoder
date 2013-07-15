app.directive 'component-row', () ->
  restrict: 'E'
  require: '^ngModel'
  scope:{
    id: '='
    label: '='
    placeholder: '='
    type: '='
  }
  priority: 100
  templateUrl: '/templates/shared/form_input.html.erb'
  compile: (element, attrs, transclude) ->
    if attrs.ngModel
      console.log "MDOEL"

