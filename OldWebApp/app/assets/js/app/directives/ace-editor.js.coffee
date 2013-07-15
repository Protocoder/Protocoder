app.directive 'ace', ['$timeout', ($timeout) ->
  restrict: 'A'
  transclude: true
  require: '?ngModel'
  template: '<div class="translucded" ng-transclude></div><div class="ace-editor"></div>'

  link:(scope, element, attributes, ngModel) ->
    loadAce = (element, opts) ->
      config = ace.config
      editor = ace.edit element[0]
      session = editor.getSession()

      session.setUseWorker(false)
      session.setMode('ace/mode/' + opts.mode) if angular.isDefined(opts.mode)
      mode = session.getMode()
      editor.setTheme('ace/theme/' + opts.theme) if angular.isDefined(opts.theme)

      session.setUseSoftTabs        opts.softTabs || true
      session.setTabSize            opts.tabSize || 2
      editor.setShowPrintMargin     opts.showPrintMargin || false
      editor.renderer.setShowGutter opts.showGutter || true
      # editor.setValue("", 1)
      ## TODO: Abstract this
      if opts.keyCommands
        for key of opts.keyCommands
          action = opts.keyCommands[key]
          editor.commands.addCommand
              name: 'contentAssist'
              bindKey: {win: key,  mac: key},
              exec: action
            
      editor

    ## Hide the text area first
    ta = $(element).find('textarea')
    ta.hide()

    opts = scope.$eval(attributes.ace)
    editor = loadAce(element, opts)
    session = editor.getSession()
    scope.ace = editor

    if angular.isDefined(ngModel)
      ngModel.$formatters.push (val) ->
        if angular.isUndefined(val) or val is null
          ""
        val

      ngModel.$render = ->
        session.setValue ngModel.$viewValue

    onChange = (cb) ->
      (e) ->
        newVal = session.getValue()
        if newVal isnt scope.$eval(attributes.value) and not scope.$$phase
          if angular.isDefined(ngModel)
            ngModel.$setViewValue newVal
        
        if angular.isDefined(cb)
          if angular.isFunction(cb)
            cb e, editor
          else
            throw new Error('Not a function')
    
    session.on 'change', onChange(opts.onChange)

    if angular.isDefined(ngModel)
      ngModel.$formatters.push (value) ->
        if angular.isUndefined(value) or value is null
          return ""
        else throw new Error("ui-ace cannot use an object or an array as a model")  if angular.isObject(value) or angular.isArray(value)
        value

      ngModel.$render = ->
        session.setValue ngModel.$viewValue
  ]
