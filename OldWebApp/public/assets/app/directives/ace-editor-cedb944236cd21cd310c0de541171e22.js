(function() {
  app.directive('ace', [
    '$timeout', function($timeout) {
      return {
        restrict: 'A',
        transclude: true,
        require: '?ngModel',
        template: '<div class="translucded" ng-transclude></div><div class="ace-editor"></div>',
        link: function(scope, element, attributes, ngModel) {
          var editor, loadAce, onChange, opts, session, ta;

          loadAce = function(element, opts) {
            var action, config, editor, key, mode, session;

            config = ace.config;
            editor = ace.edit(element[0]);
            session = editor.getSession();
            session.setUseWorker(false);
            if (angular.isDefined(opts.mode)) {
              session.setMode('ace/mode/' + opts.mode);
            }
            mode = session.getMode();
            if (angular.isDefined(opts.theme)) {
              editor.setTheme('ace/theme/' + opts.theme);
            }
            session.setUseSoftTabs(opts.softTabs || true);
            session.setTabSize(opts.tabSize || 2);
            editor.setShowPrintMargin(opts.showPrintMargin || false);
            editor.renderer.setShowGutter(opts.showGutter || true);
            if (opts.keyCommands) {
              for (key in opts.keyCommands) {
                action = opts.keyCommands[key];
                editor.commands.addCommand({
                  name: 'contentAssist',
                  bindKey: {
                    win: key,
                    mac: key
                  },
                  exec: action
                });
              }
            }
            return editor;
          };
          ta = $(element).find('textarea');
          ta.hide();
          opts = scope.$eval(attributes.ace);
          editor = loadAce(element, opts);
          session = editor.getSession();
          scope.ace = editor;
          if (angular.isDefined(ngModel)) {
            ngModel.$formatters.push(function(val) {
              if (angular.isUndefined(val) || val === null) {
                "";
              }
              return val;
            });
            ngModel.$render = function() {
              return session.setValue(ngModel.$viewValue);
            };
          }
          onChange = function(cb) {
            return function(e) {
              var newVal;

              newVal = session.getValue();
              if (newVal !== scope.$eval(attributes.value) && !scope.$$phase) {
                if (angular.isDefined(ngModel)) {
                  ngModel.$setViewValue(newVal);
                }
              }
              if (angular.isDefined(cb)) {
                if (angular.isFunction(cb)) {
                  return cb(e, editor);
                } else {
                  throw new Error('Not a function');
                }
              }
            };
          };
          session.on('change', onChange(opts.onChange));
          if (angular.isDefined(ngModel)) {
            ngModel.$formatters.push(function(value) {
              if (angular.isUndefined(value) || value === null) {
                return "";
              } else {
                if (angular.isObject(value) || angular.isArray(value)) {
                  throw new Error("ui-ace cannot use an object or an array as a model");
                }
              }
              return value;
            });
            return ngModel.$render = function() {
              return session.setValue(ngModel.$viewValue);
            };
          }
        }
      };
    }
  ]);

}).call(this);
