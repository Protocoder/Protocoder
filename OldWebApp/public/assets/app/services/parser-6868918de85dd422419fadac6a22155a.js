(function() {
  app.factory("parserService", [
    "$q", '$rootScope', "$location", function($q, $rootScope, $location) {
      var contentAssist, doParse, _parser;

      _parser = esprima;
      doParse = function(code) {
        var error;

        try {
          return _parser.parse(code, {
            tolerant: true,
            loc: true,
            range: true
          });
        } catch (_error) {
          error = _error;
          console.error(error);
          return {
            errors: [error]
          };
        }
      };
      contentAssist = function(token) {
        if (token) {
          switch (token.type) {
            case 'punctuation.operator':
              return true;
            case 'identifier' || 'text':
              return true;
            case 'storage.type':
              return true;
          }
        }
      };
      return {
        parse: doParse,
        contentAssist: contentAssist
      };
    }
  ]);

}).call(this);
