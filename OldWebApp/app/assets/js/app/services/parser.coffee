app.factory "parserService", ["$q", '$rootScope', "$location", ($q, $rootScope, $location) ->
  
  _parser = esprima
  
  doParse = (code) ->
    try
      _parser.parse(code, {tolerant: true, loc: true, range: true})
    catch error
      console.error error
      {errors: [error]}
      
  contentAssist = (token) ->
    if token
      switch token.type
        when 'punctuation.operator' # [something].
          true
        when 'identifier' or 'text' # any word
          true
        when 'storage.type' # var
          true
        else
          # Nothing
      
  {
    parse: doParse,
    contentAssist: contentAssist
  }
  ]